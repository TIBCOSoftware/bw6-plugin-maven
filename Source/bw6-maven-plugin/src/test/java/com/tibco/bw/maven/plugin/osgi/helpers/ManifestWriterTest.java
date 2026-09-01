package com.tibco.bw.maven.plugin.osgi.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ManifestWriter (AMBW-55624, and the line-too-long regression it caused).
 *
 * The writer has two modes, and which one a caller gets is the whole point:
 *
 *   writeManifest / toBytes    - fold at 72 bytes. Everything that ends up inside a
 *                                JAR or EAR goes through here. java.util.jar.Manifest
 *                                reads a header into a 512-byte buffer and throws
 *                                "line too long (line N)" past that, which the jar goal
 *                                reports as "Unable to read manifest file".
 *   writeManifestUnfolded      - one physical line per header. Only for the manifest in
 *                                the project directory that BW Studio reads back;
 *                                continuation lines there desynchronise Eclipse PDE's
 *                                rename refactoring and corrupt Bundle-Version
 *                                into "le-Version" (AMBW-55624).
 */
public class ManifestWriterTest {

    @TempDir
    Path tempDir;

    /** Longest permitted manifest line, per the JAR File Specification. */
    private static final int MAX_LINE_BYTES = 72;

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
    // TC-02  JAR-bound output never exceeds 72 bytes on any line
    // ---------------------------------------------------------------------------

    /**
     * The limit the JAR File Specification actually imposes. Exceeding it is what
     * produced "Error assembling JAR: Unable to read manifest file (line too long
     * (line 9))" on a shared module whose Provide-Capability ran to several hundred
     * bytes.
     */
    @Test
    void noLineExceedsSeventyTwoBytesInOutput() throws Exception {
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule.very.long.name\"; " +
                "version:Version=\"1.2.3.qualifier20260701_123456\"";
        Manifest mf = buildManifest("Provide-Capability", longCapability);
        byte[] bytes = ManifestWriter.toBytes(mf);
        for (String line : lines(bytes)) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= MAX_LINE_BYTES,
                    "Line exceeds " + MAX_LINE_BYTES + " bytes: [" + line + "]");
        }
    }

    /**
     * Folding must be the only thing that changed: a continuation line carries exactly
     * one leading space and the reader strips it, so the value is recovered intact.
     */
    @Test
    void continuationLinesCarryExactlyOneLeadingSpace() throws Exception {
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule.very.long.name\"; " +
                "version:Version=\"1.2.3.qualifier20260701_123456\"";
        Manifest mf = buildManifest("Provide-Capability", longCapability);
        String[] lines = lines(ManifestWriter.toBytes(mf));

        boolean sawContinuation = false;
        for (String line : lines) {
            if (line.startsWith(" ")) {
                sawContinuation = true;
                assertFalse(line.startsWith("  "),
                        "Continuation line must carry exactly one leading space: [" + line + "]");
            }
        }
        assertTrue(sawContinuation, "Pre-condition: the long header must have been folded");
    }

    // ---------------------------------------------------------------------------
    // TC-03  Provide-Capability is folded but recovered in full
    // ---------------------------------------------------------------------------

    /**
     * Folding is transparent to a spec-compliant reader: the header may span several
     * physical lines, but re-parsing must return the original value byte for byte.
     */
    @Test
    void longProvideCapabilityFoldedAndRecoveredInFull() throws Exception {
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule\"; " +
                "version:Version=\"1.0.0.qualifier\"";
        assertTrue(("Provide-Capability: " + longCapability)
                .getBytes(StandardCharsets.UTF_8).length > MAX_LINE_BYTES,
                "Pre-condition: attribute line must exceed 72 bytes");
        Manifest mf = buildManifest("Provide-Capability", longCapability);
        byte[] bytes = ManifestWriter.toBytes(mf);

        Manifest parsed = new Manifest(new ByteArrayInputStream(bytes));
        assertEquals(longCapability,
                parsed.getMainAttributes().getValue("Provide-Capability"),
                "Provide-Capability must be recovered in full after folding");
    }

    // ---------------------------------------------------------------------------
    // TC-04  Require-Capability is folded but recovered in full
    // ---------------------------------------------------------------------------

    @Test
    void longRequireCapabilityFoldedAndRecoveredInFull() throws Exception {
        String longRequire =
                "com.tibco.bw.module; filter:=\"(&(name=com.example.depmodule)" +
                "(version=2.0.0.qualifier20260701))\"";
        assertTrue(("Require-Capability: " + longRequire)
                .getBytes(StandardCharsets.UTF_8).length > MAX_LINE_BYTES,
                "Pre-condition: attribute line must exceed 72 bytes");
        Manifest mf = buildManifest("Require-Capability", longRequire);
        byte[] bytes = ManifestWriter.toBytes(mf);

        Manifest parsed = new Manifest(new ByteArrayInputStream(bytes));
        assertEquals(longRequire,
                parsed.getMainAttributes().getValue("Require-Capability"),
                "Require-Capability must be recovered in full after folding");
    }

    // ---------------------------------------------------------------------------
    // TC-04b  The reported failure, reproduced end to end
    // ---------------------------------------------------------------------------

    /**
     * A shared module's Provide-Capability lists one entry per exported schema, so a
     * real one runs to several hundred bytes - past the 512-byte buffer that
     * java.util.jar.Manifest reads a header into. Unfolded, that is the exact
     * "line too long (line N)" the jar goal failed on; folded, it must read back clean.
     */
    @Test
    void realisticSharedModuleManifestIsReadableAfterFolding() throws Exception {
        StringBuilder provide = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            if (i > 0) provide.append(",");
            provide.append("com.tibco.bw.schemas; ns=\"http://www.example.org/schema/Namespace")
                   .append(i).append("\"");
        }
        String value = provide.toString();
        assertTrue(("Provide-Capability: " + value).getBytes(StandardCharsets.UTF_8).length > 512,
                "Pre-condition: header must exceed the 512-byte read buffer");

        Manifest mf = buildManifest("Provide-Capability", value);

        // Unfolded: this is the failure. Assert it still reproduces, so the test below means something.
        File unfolded = tempDir.resolve("UNFOLDED.MF").toFile();
        ManifestWriter.writeManifestUnfolded(unfolded, mf);
        IOException tooLong = assertThrows(IOException.class, () -> {
            try (java.io.InputStream is = Files.newInputStream(unfolded.toPath())) {
                new Manifest(is);
            }
        }, "Regression anchor: an unfolded manifest of this size must still be unreadable");
        assertTrue(tooLong.getMessage().contains("line too long"),
                "Expected the 'line too long' failure, got: " + tooLong.getMessage());

        // Folded: readable, and the value survives.
        File folded = tempDir.resolve("FOLDED.MF").toFile();
        ManifestWriter.writeManifest(folded, mf);
        try (java.io.InputStream is = Files.newInputStream(folded.toPath())) {
            Manifest parsed = new Manifest(is);
            assertEquals(value, parsed.getMainAttributes().getValue("Provide-Capability"),
                    "Folded manifest must be readable and preserve the full value");
        }
    }

    // ---------------------------------------------------------------------------
    // TC-04c  Unfolded mode keeps AMBW-55624 fixed for the workspace manifest
    // ---------------------------------------------------------------------------

    /**
     * The project-directory manifest that BW Studio reads must keep every header on one
     * physical line. Continuation lines there invalidate the text-edit offsets PDE holds
     * during a rename, splicing Bundle-Version into "le-Version" (AMBW-55624).
     */
    @Test
    void unfoldedWriterKeepsLongHeaderOnASingleLine() throws Exception {
        String longCapability =
                "com.tibco.bw.module; name=\"com.example.sharedmodule\"; " +
                "version:Version=\"1.0.0.qualifier20260701_123456\"";
        assertTrue(("Provide-Capability: " + longCapability)
                .getBytes(StandardCharsets.UTF_8).length > MAX_LINE_BYTES,
                "Pre-condition: attribute line must exceed 72 bytes");

        File target = tempDir.resolve("MANIFEST.MF").toFile();
        ManifestWriter.writeManifestUnfolded(target, buildManifest("Provide-Capability", longCapability));

        String content = new String(Files.readAllBytes(target.toPath()), StandardCharsets.UTF_8);
        for (String line : content.split("\r\n", -1)) {
            assertFalse(line.startsWith(" "),
                    "Workspace manifest must have no continuation lines: [" + line + "]");
        }
        assertTrue(content.contains("Provide-Capability: " + longCapability),
                "Provide-Capability must appear verbatim on one line");
        assertTrue(content.contains("Bundle-Version: 1.0.0\r\n"),
                "Bundle-Version must be intact and unspliced");
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
        for (String line : content.split("\r\n", -1)) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= MAX_LINE_BYTES,
                    "Line exceeds " + MAX_LINE_BYTES + " bytes: [" + line + "]");
        }
        try (java.io.InputStream is = Files.newInputStream(target.toPath())) {
            assertEquals(longCapability,
                    new Manifest(is).getMainAttributes().getValue("Provide-Capability"),
                    "File must yield the full Provide-Capability value when re-read");
        }
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
