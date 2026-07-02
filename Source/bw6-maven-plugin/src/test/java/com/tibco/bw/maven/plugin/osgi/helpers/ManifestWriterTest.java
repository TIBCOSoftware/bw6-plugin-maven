package com.tibco.bw.maven.plugin.osgi.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ManifestWriter (AMBW-55624).
 *
 * Verifies that the custom writer bypasses Java's 72-byte line wrapping and
 * keeps OSGi headers (Provide-Capability, Require-Capability) on single lines.
 */
public class ManifestWriterTest {

    @TempDir
    Path tempDir;

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private Manifest buildManifest(String... extraKvPairs) {
        Manifest mf = new Manifest();
        Attributes attrs = mf.getMainAttributes();
        attrs.put(Name.MANIFEST_VERSION, "1.0");
        attrs.putValue("Bundle-ManifestVersion", "2");
        attrs.putValue("Bundle-SymbolicName", "com.example.mymodule");
        attrs.putValue("Bundle-Version", "1.0.0");
        for (int i = 0; i + 1 < extraKvPairs.length; i += 2) {
            attrs.putValue(extraKvPairs[i], extraKvPairs[i + 1]);
        }
        return mf;
    }

    private String[] lines(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8).split("\r\n", -1);
    }

    // ---------------------------------------------------------------------------
    // TC-01  Manifest-Version is always the first line
    // ---------------------------------------------------------------------------

    /**
     * The JAR spec mandates Manifest-Version as the first attribute.
     * HashMap iteration order is non-deterministic; the writer must enforce ordering.
     */
    @Test
    void manifestVersionIsFirstLine() throws Exception {
        Manifest mf = buildManifest();
        byte[] bytes = ManifestWriter.toBytes(mf);
        String firstLine = lines(bytes)[0];
        assertTrue(firstLine.startsWith("Manifest-Version:"),
                "First line must be 'Manifest-Version:' but was: " + firstLine);
    }

    /**
     * Deliberately put Manifest-Version last to confirm the writer re-orders it first.
     */
    @Test
    void manifestVersionIsFirstLineEvenWhenAddedLast() throws Exception {
        Manifest mf = new Manifest();
        Attributes attrs = mf.getMainAttributes();
        // Insert other attributes BEFORE Manifest-Version
        attrs.putValue("Bundle-SymbolicName", "com.example.module");
        attrs.putValue("Bundle-Version", "1.0.0");
        attrs.putValue("Provide-Capability",
                "com.tibco.bw.module; name=\"com.example.module\"; version:Version=\"1.0.0\"");
        attrs.put(Name.MANIFEST_VERSION, "1.0"); // intentionally last
        byte[] bytes = ManifestWriter.toBytes(mf);
        String firstLine = lines(bytes)[0];
        assertTrue(firstLine.startsWith("Manifest-Version:"),
                "Manifest-Version must be first even when inserted last. Got: " + firstLine);
    }

    // ---------------------------------------------------------------------------
    // TC-02  No continuation lines (no 72-byte wrapping)
    // ---------------------------------------------------------------------------

    /**
     * No line in the output should start with a space.
     * A leading space is the JAR-spec continuation-line marker, injected by
     * Manifest.write() when a value exceeds 72 bytes.
     */
    @Test
    void noContinuationLinesInOutput() throws Exception {
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule.very.long.name\"; " +
                "version:Version=\"1.2.3.qualifier20260701_123456\"";
        Manifest mf = buildManifest("Provide-Capability", longCapability);
        byte[] bytes = ManifestWriter.toBytes(mf);
        for (String line : lines(bytes)) {
            assertFalse(line.startsWith(" "),
                    "Continuation line found (starts with space): [" + line + "]");
        }
    }

    // ---------------------------------------------------------------------------
    // TC-03  Provide-Capability stays on one line
    // ---------------------------------------------------------------------------

    /**
     * The original bug: Provide-Capability values > 72 bytes were split by
     * Manifest.write(), which Eclipse PDE silently truncated at the first physical
     * line, dropping the version:Version attribute.
     */
    @Test
    void longProvideCapabilityNotSplit() throws Exception {
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule\"; " +
                "version:Version=\"1.0.0.qualifier\"";
        assertTrue(("Provide-Capability: " + longCapability)
                .getBytes(StandardCharsets.UTF_8).length > 72,
                "Pre-condition: attribute line must exceed 72 bytes");
        Manifest mf = buildManifest("Provide-Capability", longCapability);
        byte[] bytes = ManifestWriter.toBytes(mf);
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(content.contains("Provide-Capability: " + longCapability),
                "Provide-Capability must appear on a single unbroken line");
    }

    // ---------------------------------------------------------------------------
    // TC-04  Require-Capability stays on one line
    // ---------------------------------------------------------------------------

    @Test
    void longRequireCapabilityNotSplit() throws Exception {
        String longRequire =
                "com.tibco.bw.module; filter:=\"(&(name=com.example.depmodule)" +
                "(version=2.0.0.qualifier20260701))\"";
        assertTrue(("Require-Capability: " + longRequire)
                .getBytes(StandardCharsets.UTF_8).length > 72,
                "Pre-condition: attribute line must exceed 72 bytes");
        Manifest mf = buildManifest("Require-Capability", longRequire);
        byte[] bytes = ManifestWriter.toBytes(mf);
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(content.contains("Require-Capability: " + longRequire),
                "Require-Capability must appear on a single unbroken line");
    }

    // ---------------------------------------------------------------------------
    // TC-05  Round-trip: write then re-parse preserves all attribute values
    // ---------------------------------------------------------------------------

    /**
     * Java's Manifest(InputStream) is lenient about line length.
     * Verifies that the written manifest can be re-read without value truncation.
     */
    @Test
    void allAttributesPreservedAfterRoundTrip() throws Exception {
        String provideCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule\"; " +
                "version:Version=\"1.0.0.qualifier\"";
        Manifest mf = buildManifest("Provide-Capability", provideCapability);
        byte[] bytes = ManifestWriter.toBytes(mf);

        Manifest parsed = new Manifest(new ByteArrayInputStream(bytes));
        assertEquals("1.0",
                parsed.getMainAttributes().getValue(Name.MANIFEST_VERSION));
        assertEquals("com.example.mymodule",
                parsed.getMainAttributes().getValue("Bundle-SymbolicName"));
        assertEquals("1.0.0",
                parsed.getMainAttributes().getValue("Bundle-Version"));
        assertEquals(provideCapability,
                parsed.getMainAttributes().getValue("Provide-Capability"),
                "Provide-Capability must survive round-trip without truncation");
    }

    // ---------------------------------------------------------------------------
    // TC-06  Null attribute value handled gracefully
    // ---------------------------------------------------------------------------

    @Test
    void nullAttributeValueWrittenAsEmpty() throws Exception {
        Manifest mf = new Manifest();
        mf.getMainAttributes().put(Name.MANIFEST_VERSION, "1.0");
        mf.getMainAttributes().put(new Name("Bundle-Name"), null);

        assertDoesNotThrow(() -> ManifestWriter.toBytes(mf),
                "Null attribute value must not throw");
        byte[] bytes = ManifestWriter.toBytes(mf);
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(content.contains("Bundle-Name: "),
                "Null value must be written as an empty string");
    }

    // ---------------------------------------------------------------------------
    // TC-07  writeManifest(File, Manifest) creates file with correct content
    // ---------------------------------------------------------------------------

    @Test
    void writeManifestCreatesFileWithCorrectContent() throws Exception {
        File target = tempDir.resolve("MANIFEST.MF").toFile();
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.shared\"; version:Version=\"1.0.0\"";
        Manifest mf = buildManifest("Provide-Capability", longCapability);

        ManifestWriter.writeManifest(target, mf);

        assertTrue(target.exists(), "Output file must be created");
        String content = new String(Files.readAllBytes(target.toPath()), StandardCharsets.UTF_8);
        assertTrue(content.startsWith("Manifest-Version:"),
                "File must start with Manifest-Version");
        assertTrue(content.contains("Provide-Capability: " + longCapability),
                "File must contain the full Provide-Capability value");
    }

    // ---------------------------------------------------------------------------
    // TC-08  writeManifest overwrites an existing file
    // ---------------------------------------------------------------------------

    @Test
    void writeManifestOverwritesExistingFile() throws Exception {
        File target = tempDir.resolve("MANIFEST.MF").toFile();
        Files.write(target.toPath(), "stale content that must be gone".getBytes());

        ManifestWriter.writeManifest(target, buildManifest());

        String content = new String(Files.readAllBytes(target.toPath()), StandardCharsets.UTF_8);
        assertFalse(content.contains("stale content"), "Stale content must be overwritten");
        assertTrue(content.startsWith("Manifest-Version:"));
    }

    // ---------------------------------------------------------------------------
    // TC-09  Output uses CRLF line endings and ends with a blank line
    // ---------------------------------------------------------------------------

    @Test
    void outputUsesCrLfAndEndsWithBlankLine() throws Exception {
        byte[] bytes = ManifestWriter.toBytes(buildManifest());

        // Every LF must be preceded by CR
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '\n') {
                assertTrue(i > 0 && bytes[i - 1] == '\r',
                        "Bare LF at byte " + i + "; all line endings must be CRLF");
            }
        }
        // Must end with blank line (CRLF CRLF)
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(content.endsWith("\r\n\r\n"),
                "Manifest must end with a blank line (CRLF CRLF) per JAR spec");
    }

    // ---------------------------------------------------------------------------
    // TC-10  toBytes() and writeManifest() produce identical output
    // ---------------------------------------------------------------------------

    @Test
    void toBytesAndWriteManifestProduceSameContent() throws Exception {
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule\"; " +
                "version:Version=\"1.2.3.qualifier\"";
        Manifest mf = buildManifest("Provide-Capability", longCapability);

        byte[] fromToBytes = ManifestWriter.toBytes(mf);
        File target = tempDir.resolve("MANIFEST.MF").toFile();
        ManifestWriter.writeManifest(target, mf);
        byte[] fromFile = Files.readAllBytes(target.toPath());

        assertArrayEquals(fromToBytes, fromFile,
                "toBytes() and writeManifest() must produce byte-identical output");
    }
}
