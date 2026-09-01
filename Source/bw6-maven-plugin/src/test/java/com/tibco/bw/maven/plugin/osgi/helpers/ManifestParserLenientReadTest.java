package com.tibco.bw.maven.plugin.osgi.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ManifestParser's lenient read path.
 *
 * The manifest in the project directory is written with one physical line per header so
 * that a rename refactoring in BW Studio cannot splice its edits into the middle of one
 * (AMBW-55624, ManifestWriter#writeManifestUnfolded). java.util.jar.Attributes.read parses
 * a header into a fixed 512-byte buffer and throws "line too long (line N)" past that, so
 * without a lenient reader the build that writes such a manifest makes the next build fail
 * with "Failed to parse MANIFEST.MF for project". Reading has to tolerate what writing
 * deliberately produces.
 */
public class ManifestParserLenientReadTest {

    @TempDir
    Path tempDir;

    /** Longest permitted manifest line, per the JAR File Specification. */
    private static final int MAX_LINE_BYTES = 72;

    /** A realistic standalone shared module: one Provide-Capability entry per exported schema. */
    private Manifest sharedModuleManifest() {
        StringBuilder provide = new StringBuilder(
                "com.tibco.bw.module; name=\"bw.core_common_kafka.sharedmodule\"; version:Version=\"10.0.0\"");
        for (int i = 0; i < 21; i++) {
            provide.append(",com.tibco.bw.schemas; ns=\"http://www.example.org/schemas/KafkaSchema")
                   .append(i).append("\"");
        }
        Manifest mf = new Manifest();
        Attributes attrs = mf.getMainAttributes();
        attrs.put(Name.MANIFEST_VERSION, "1.0");
        attrs.putValue("Bundle-ManifestVersion", "2");
        attrs.putValue("Bundle-SymbolicName", "bw.core_common_kafka.sharedmodule;singleton:=true");
        attrs.putValue("Bundle-Version", "10.0.0.qualifier");
        attrs.putValue("TIBCO-BW-SharedModule",
                "name=bw.core_common_kafka.sharedmodule;version=1.0.0.qualifier");
        attrs.putValue("Provide-Capability", provide.toString());
        return mf;
    }

    // ---------------------------------------------------------------------------
    // TC-01  The write-then-read cycle across two builds
    // ---------------------------------------------------------------------------

    /**
     * Build 1 writes the workspace manifest unfolded; build 2 must still be able to read it.
     * Before the lenient read path this returned null, and BWModulePackageMojo turned that
     * into "Failed to parse MANIFEST.MF for project".
     */
    @Test
    void unfoldedWorkspaceManifestIsReadableOnTheNextBuild() throws Exception {
        Manifest original = sharedModuleManifest();
        String provide = original.getMainAttributes().getValue("Provide-Capability");
        assertTrue(("Provide-Capability: " + provide).getBytes(StandardCharsets.UTF_8).length > 512,
                "Pre-condition: the header must exceed the 512-byte read buffer");

        File baseDir = tempDir.resolve("sharedmodule").toFile();
        File mfile = new File(baseDir, "META-INF/MANIFEST.MF");
        ManifestWriter.writeManifestUnfolded(mfile, original);

        // Regression anchor: the strict JDK reader must still reject this file, otherwise
        // the lenient path below is not being exercised.
        IOException strict = assertThrows(IOException.class, () -> {
            try (java.io.InputStream is = Files.newInputStream(mfile.toPath())) {
                new Manifest(is);
            }
        });
        assertTrue(strict.getMessage().contains("line too long"),
                "Expected 'line too long', got: " + strict.getMessage());

        Manifest parsed = ManifestParser.parseManifest(baseDir);
        assertNotNull(parsed, "An unfolded workspace manifest must still be parseable");
        assertEquals(provide, parsed.getMainAttributes().getValue("Provide-Capability"),
                "Provide-Capability must be recovered in full from the unfolded manifest");
        assertEquals("10.0.0.qualifier", parsed.getMainAttributes().getValue("Bundle-Version"));
        assertEquals("bw.core_common_kafka.sharedmodule;singleton:=true",
                parsed.getMainAttributes().getValue("Bundle-SymbolicName"));
    }

    // ---------------------------------------------------------------------------
    // TC-02  A well-formed manifest is unaffected
    // ---------------------------------------------------------------------------

    /**
     * The lenient path is a fallback only. A manifest that already respects the 72-byte
     * limit must be parsed by the strict reader and come back identical.
     */
    @Test
    void foldedManifestIsParsedUnchanged() throws Exception {
        Manifest original = sharedModuleManifest();
        String provide = original.getMainAttributes().getValue("Provide-Capability");

        File baseDir = tempDir.resolve("folded").toFile();
        File mfile = new File(baseDir, "META-INF/MANIFEST.MF");
        ManifestWriter.writeManifest(mfile, original);

        for (String line : new String(Files.readAllBytes(mfile.toPath()), StandardCharsets.UTF_8)
                .split("\r\n", -1)) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= MAX_LINE_BYTES,
                    "Pre-condition: the file must already be folded: [" + line + "]");
        }

        Manifest parsed = ManifestParser.parseManifest(baseDir);
        assertNotNull(parsed);
        assertEquals(provide, parsed.getMainAttributes().getValue("Provide-Capability"));
    }

    // ---------------------------------------------------------------------------
    // TC-03  Refolding works on physical lines, so mixed input is handled
    // ---------------------------------------------------------------------------

    /**
     * A manifest edited by hand or by Studio can end up partly folded, partly not, and with
     * mixed line endings. Refolding operates on physical lines, so an already-folded header
     * keeps the leading space it arrived with and is re-joined correctly.
     */
    @Test
    void partlyFoldedManifestWithMixedLineEndingsIsRecovered() throws Exception {
        StringBuilder longValue = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if (i > 0) longValue.append(",");
            longValue.append("com.tibco.bw.schemas; ns=\"http://www.example.org/schemas/S").append(i).append("\"");
        }

        // Bundle-SymbolicName arrives already folded (LF only); Provide-Capability arrives
        // on one long line (CRLF).
        String raw = "Manifest-Version: 1.0\n"
                + "Bundle-SymbolicName: com.example.some.very.long.shared.module.na\n"
                + " me;singleton:=true\n"
                + "Bundle-Version: 10.0.0.qualifier\r\n"
                + "Provide-Capability: " + longValue + "\r\n"
                + "\r\n";
        assertTrue(("Provide-Capability: " + longValue).getBytes(StandardCharsets.UTF_8).length > 512,
                "Pre-condition: the header must exceed the 512-byte read buffer");

        File baseDir = tempDir.resolve("mixed").toFile();
        File mfile = new File(baseDir, "META-INF/MANIFEST.MF");
        mfile.getParentFile().mkdirs();
        Files.write(mfile.toPath(), raw.getBytes(StandardCharsets.UTF_8));

        Manifest parsed = ManifestParser.parseManifest(baseDir);
        assertNotNull(parsed, "A partly folded manifest must be parseable");
        assertEquals("com.example.some.very.long.shared.module.name;singleton:=true",
                parsed.getMainAttributes().getValue("Bundle-SymbolicName"),
                "An already-folded header must be re-joined without losing or gaining a character");
        assertEquals(longValue.toString(),
                parsed.getMainAttributes().getValue("Provide-Capability"));
        assertEquals("10.0.0.qualifier", parsed.getMainAttributes().getValue("Bundle-Version"));
    }

    // ---------------------------------------------------------------------------
    // TC-04  Refolded values match what the strict reader would have produced
    // ---------------------------------------------------------------------------

    /**
     * The lenient path must not be a second, subtly different parser. Folding the same
     * manifest properly and reading it strictly has to give the same attributes.
     */
    @Test
    void lenientReadAgreesWithStrictReadOfTheFoldedForm() throws Exception {
        Manifest original = sharedModuleManifest();

        File unfolded = tempDir.resolve("u/META-INF/MANIFEST.MF").toFile();
        ManifestWriter.writeManifestUnfolded(unfolded, original);
        Manifest lenient = ManifestParser.readManifest(unfolded);

        Manifest strict = new Manifest(new ByteArrayInputStream(ManifestWriter.toBytes(original)));

        assertEquals(strict.getMainAttributes(), lenient.getMainAttributes(),
                "Lenient read of the unfolded file must equal a strict read of the folded file");
    }

    // ---------------------------------------------------------------------------
    // TC-05  A genuinely malformed manifest still fails
    // ---------------------------------------------------------------------------

    /**
     * Leniency is about line length only. A header with no colon is still invalid and must
     * not be silently accepted, or a corrupt manifest would sail through the build.
     *
     * parseManifest logs the failure with e.printStackTrace(), which is the behaviour under
     * test here, so System.err is captured for the duration to keep the build output clean.
     */
    @Test
    void malformedManifestStillFails() throws Exception {
        File baseDir = tempDir.resolve("bad").toFile();
        File mfile = new File(baseDir, "META-INF/MANIFEST.MF");
        mfile.getParentFile().mkdirs();
        Files.write(mfile.toPath(),
                "Manifest-Version: 1.0\r\nthis-line-has-no-colon\r\n\r\n".getBytes(StandardCharsets.UTF_8));

        assertThrows(IOException.class, () -> ManifestParser.readManifest(mfile),
                "A malformed header must still be rejected");

        PrintStream originalErr = System.err;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setErr(new PrintStream(captured, true, StandardCharsets.UTF_8.name()));
        try {
            assertNull(ManifestParser.parseManifest(baseDir),
                    "parseManifest reports a malformed manifest as null, as before");
        } finally {
            System.setErr(originalErr);
        }
        assertTrue(captured.toString(StandardCharsets.UTF_8.name()).contains("invalid header field"),
                "parseManifest must still report why the manifest was rejected");
    }
}
