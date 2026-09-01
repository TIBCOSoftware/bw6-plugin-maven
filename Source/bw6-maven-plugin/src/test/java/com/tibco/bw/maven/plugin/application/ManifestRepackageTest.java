package com.tibco.bw.maven.plugin.application;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tibco.bw.maven.plugin.osgi.helpers.ManifestWriter;

/**
 * Tests for the repackageJarWithUpdatedManifest logic in BWEARPackagerMojo.
 *
 * The method is private; these tests reproduce the identical logic so the critical
 * behaviour can be verified independently of the full Mojo wiring: the MANIFEST.MF
 * embedded in the EAR's application-module JAR must obey the JAR File Specification's
 * 72-byte line limit, and the updated Require-Capability must be recoverable in full.
 *
 * Note this is a JAR-bound manifest. The unfolded form kept for the workspace manifest
 * under AMBW-55624 does not apply here; see ManifestWriterTest for that split.
 */
public class ManifestRepackageTest {

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
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.putValue("Bundle-ManifestVersion", "2");
        attrs.putValue("Bundle-SymbolicName", "com.example.appmodule");
        attrs.putValue("Bundle-Version", "1.0.0");
        for (int i = 0; i + 1 < extraKvPairs.length; i += 2) {
            attrs.putValue(extraKvPairs[i], extraKvPairs[i + 1]);
        }
        return mf;
    }

    /**
     * Creates a minimal JAR using the OLD approach (JarOutputStream + Manifest
     * constructor) so the embedded manifest may be line-wrapped.
     */
    private File createSourceJar(Manifest mf, String name) throws Exception {
        File jarFile = tempDir.resolve(name).toFile();
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), mf)) {
            JarEntry entry = new JarEntry("com/example/Dummy.class");
            jos.putNextEntry(entry);
            jos.write("dummy-bytecode".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
            JarEntry entry2 = new JarEntry("META-INF/services/com.example.Service");
            jos.putNextEntry(entry2);
            jos.write("com.example.impl.ServiceImpl".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
        }
        return jarFile;
    }

    /**
     * Mirrors the fixed repackageJarWithUpdatedManifest logic from BWEARPackagerMojo:
     * uses ManifestWriter.toBytes() so the embedded MANIFEST.MF is not line-wrapped.
     */
    private File repackWithManifestWriter(File originalJar, Manifest updatedManifest,
                                          String outputName) throws Exception {
        File tempJar = tempDir.resolve(outputName).toFile();
        byte[] manifestBytes = ManifestWriter.toBytes(updatedManifest);
        byte[] buffer = new byte[8192];
        try (JarFile jarFile = new JarFile(originalJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempJar))) {
            jos.putNextEntry(new JarEntry("META-INF/"));
            jos.closeEntry();
            jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jos.write(manifestBytes);
            jos.closeEntry();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if ("META-INF/".equalsIgnoreCase(entryName) ||
                        "META-INF/MANIFEST.MF".equalsIgnoreCase(entryName)) {
                    continue;
                }
                jos.putNextEntry(new JarEntry(entryName));
                try (InputStream is = jarFile.getInputStream(entry)) {
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        jos.write(buffer, 0, len);
                    }
                }
                jos.closeEntry();
            }
        }
        return tempJar;
    }

    /** Reads the raw bytes of META-INF/MANIFEST.MF from inside a JAR. */
    private byte[] readManifestBytesFromJar(File jarFile) throws Exception {
        try (JarFile jf = new JarFile(jarFile)) {
            JarEntry entry = jf.getJarEntry("META-INF/MANIFEST.MF");
            assertNotNull(entry, "META-INF/MANIFEST.MF must exist in " + jarFile.getName());
            try (InputStream is = jf.getInputStream(entry)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int n;
                while ((n = is.read(buf)) > 0) baos.write(buf, 0, n);
                return baos.toByteArray();
            }
        }
    }

    // ---------------------------------------------------------------------------
    // TC-01  Baseline: JarOutputStream(out, manifest) folds long headers
    // ---------------------------------------------------------------------------

    /**
     * Documents the JDK baseline that ManifestWriter has to match for JAR-bound
     * manifests: JarOutputStream(out, manifest) folds anything over 72 bytes onto
     * continuation lines. ManifestWriter differs from it only in forcing
     * Manifest-Version to be written first, which HashMap iteration order does not
     * guarantee.
     */
    @Test
    void oldJarOutputStreamConstructorProducesContinuationLines() throws Exception {
        String longRequire =
                "com.tibco.bw.module; filter:=\"(&(name=com.example.depmod)" +
                "(version=2.0.0.qualifier20260701_120000))\"";
        assertTrue(("Require-Capability: " + longRequire)
                        .getBytes(StandardCharsets.UTF_8).length > 72,
                "Pre-condition: attribute line must exceed 72 bytes");

        Manifest mf = buildManifest("Require-Capability", longRequire);
        File oldJar = tempDir.resolve("old_style.jar").toFile();
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(oldJar), mf)) {
            jos.putNextEntry(new JarEntry("dummy.txt"));
            jos.write("x".getBytes());
            jos.closeEntry();
        }

        byte[] rawBytes = readManifestBytesFromJar(oldJar);
        String content = new String(rawBytes, StandardCharsets.UTF_8);
        boolean hasContinuation = false;
        for (String line : content.split("\r\n", -1)) {
            if (line.startsWith(" ")) { hasContinuation = true; break; }
        }
        assertTrue(hasContinuation,
                "Old JarOutputStream(out, manifest) must produce continuation lines " +
                "(regression anchor — if this fails, the bug may no longer be reproducible)");
    }

    // ---------------------------------------------------------------------------
    // TC-02  Repacked JAR manifest respects the 72-byte line limit
    // ---------------------------------------------------------------------------

    /**
     * A manifest inside a JAR must obey the JAR File Specification, so a long
     * Require-Capability is expected to fold. What must not happen is a line over
     * 72 bytes, which readers reject outright.
     */
    @Test
    void repackedJarManifestRespectsLineLengthLimit() throws Exception {
        String longRequire =
                "com.tibco.bw.module; filter:=\"(&(name=com.example.depmod)" +
                "(version=2.0.0.qualifier20260701_120000))\"";
        Manifest original = buildManifest("Require-Capability", longRequire);
        File sourceJar = createSourceJar(original, "source.jar");

        Manifest updated = buildManifest("Require-Capability", longRequire);
        File repackedJar = repackWithManifestWriter(sourceJar, updated, "repacked.jar");

        byte[] rawBytes = readManifestBytesFromJar(repackedJar);
        String content = new String(rawBytes, StandardCharsets.UTF_8);
        for (String line : content.split("\r\n", -1)) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= MAX_LINE_BYTES,
                    "Line exceeds " + MAX_LINE_BYTES + " bytes in repacked JAR manifest: [" + line + "]");
        }
    }

    // ---------------------------------------------------------------------------
    // TC-03  Manifest-Version is the first line inside the repacked JAR
    // ---------------------------------------------------------------------------

    @Test
    void repackedJarManifestVersionIsFirstLine() throws Exception {
        File sourceJar = createSourceJar(buildManifest(), "source.jar");
        File repackedJar = repackWithManifestWriter(sourceJar, buildManifest(), "repacked.jar");

        byte[] rawBytes = readManifestBytesFromJar(repackedJar);
        String firstLine = new String(rawBytes, StandardCharsets.UTF_8).split("\r\n")[0];
        assertTrue(firstLine.startsWith("Manifest-Version:"),
                "First line in repacked JAR manifest must be Manifest-Version. Got: " + firstLine);
    }

    // ---------------------------------------------------------------------------
    // TC-04  Non-manifest JAR entries are preserved unchanged
    // ---------------------------------------------------------------------------

    @Test
    void repackedJarPreservesAllNonManifestEntries() throws Exception {
        File sourceJar = createSourceJar(buildManifest(), "source.jar");
        File repackedJar = repackWithManifestWriter(sourceJar, buildManifest(), "repacked.jar");

        try (JarFile jf = new JarFile(repackedJar)) {
            assertNotNull(jf.getJarEntry("com/example/Dummy.class"),
                    "com/example/Dummy.class must be preserved");
            assertNotNull(jf.getJarEntry("META-INF/services/com.example.Service"),
                    "META-INF/services entry must be preserved");
        }
    }

    // ---------------------------------------------------------------------------
    // TC-05  Full attribute value survives JAR embedding and re-parsing
    // ---------------------------------------------------------------------------

    /**
     * Simulates the read path used by ManifestParser.parseManifestFromJAR() and
     * the OSGi runtime: JarInputStream.getManifest().
     * The Require-Capability value must not be truncated.
     */
    @Test
    void requireCapabilitySurvivesJarEmbeddingAndReparsing() throws Exception {
        String longRequire =
                "com.tibco.bw.module; filter:=\"(&(name=com.example.depmod)" +
                "(version=2.0.0.qualifier))\"";
        Manifest updated = buildManifest("Require-Capability", longRequire);
        File sourceJar = createSourceJar(buildManifest(), "source.jar");
        File repackedJar = repackWithManifestWriter(sourceJar, updated, "repacked.jar");

        try (JarInputStream jis = new JarInputStream(new FileInputStream(repackedJar))) {
            Manifest parsed = jis.getManifest();
            assertNotNull(parsed, "Manifest must be readable from repacked JAR");
            assertEquals(longRequire,
                    parsed.getMainAttributes().getValue("Require-Capability"),
                    "Require-Capability full value must not be truncated after JAR round-trip");
        }
    }

    // ---------------------------------------------------------------------------
    // TC-06  Updating Require-Capability in a JAR that originally had a
    //         wrapped manifest produces a clean output
    // ---------------------------------------------------------------------------

    /**
     * End-to-end scenario: source JAR was built the old way (wrapped manifest),
     * repack inserts an updated Require-Capability via ManifestWriter.toBytes().
     * The resulting embedded manifest must contain the full, unbroken value.
     */
    @Test
    void updatedRequireCapabilityIsCleanWhenSourceHadWrappedManifest() throws Exception {
        // Source JAR has a wrapped manifest (built with old JarOutputStream path)
        String originalRequire =
                "com.tibco.bw.module; filter:=\"(&(name=com.example.depmod)(version=1.0.0))\"";
        File sourceJar = createSourceJar(buildManifest("Require-Capability", originalRequire),
                "source_wrapped.jar");

        // Updated manifest carries the new version — longer value to exceed 72 bytes
        String updatedRequire =
                "com.tibco.bw.module; filter:=\"(&(name=com.example.depmod)" +
                "(version=2.5.3.qualifier20260701_090000))\"";
        assertTrue(("Require-Capability: " + updatedRequire)
                        .getBytes(StandardCharsets.UTF_8).length > 72,
                "Pre-condition: updated attribute line must exceed 72 bytes");
        Manifest updated = buildManifest("Require-Capability", updatedRequire);

        File repackedJar = repackWithManifestWriter(sourceJar, updated, "repacked_fixed.jar");

        // Embedded manifest must stay within the spec's line limit
        byte[] rawBytes = readManifestBytesFromJar(repackedJar);
        String content = new String(rawBytes, StandardCharsets.UTF_8);
        for (String line : content.split("\r\n", -1)) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= MAX_LINE_BYTES,
                    "Line exceeds " + MAX_LINE_BYTES + " bytes after fixed repack: [" + line + "]");
        }
        // and the updated value must be recovered in full, not the stale one
        try (JarInputStream jis = new JarInputStream(new FileInputStream(repackedJar))) {
            assertEquals(updatedRequire,
                    jis.getManifest().getMainAttributes().getValue("Require-Capability"),
                    "Updated Require-Capability must be recovered in full from the repacked manifest");
        }
    }
}
