package com.tibco.bw.maven.plugin.module;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
 * Tests covering the two manifest-related concerns in BWModulePackageMojo:
 *
 * 1. repackageJarWithUpdatedManifest — writes MANIFEST.MF using ManifestWriter.toBytes(),
 *    which folds at 72 bytes as the JAR File Specification requires, while forcing
 *    Manifest-Version to be written first.
 *
 * 2. Bundle-ClassPath normalization — trims whitespace and drops blank entries that
 *    appear when a previously line-wrapped manifest value (e.g. ". ,") is re-read.
 *
 * Both methods are private; these tests mirror the identical logic (consistent with
 * the existing ManifestRepackageTest pattern for BWEARPackagerMojo).
 */
public class BWModulePackageMojoManifestTest {

    @TempDir
    Path tempDir;

    /** Longest permitted manifest line, per the JAR File Specification. */
    private static final int MAX_LINE_BYTES = 72;

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private Manifest buildModuleManifest(String... extraKvPairs) {
        Manifest mf = new Manifest();
        Attributes attrs = mf.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.putValue("Bundle-ManifestVersion", "2");
        attrs.putValue("Bundle-SymbolicName", "com.example.module");
        attrs.putValue("Bundle-Version", "1.0.0.202607021200");
        attrs.putValue("Bundle-Vendor", "TIBCO Software Inc.");
        attrs.putValue("TIBCO-BW-Edition", "bwe");
        for (int i = 0; i + 1 < extraKvPairs.length; i += 2) {
            attrs.putValue(extraKvPairs[i], extraKvPairs[i + 1]);
        }
        return mf;
    }

    /** Creates a module JAR the old way (JarOutputStream + Manifest), which wraps at 72 bytes. */
    private File createOldStyleJar(Manifest mf, String name) throws Exception {
        File jarFile = tempDir.resolve(name).toFile();
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), mf)) {
            jos.putNextEntry(new JarEntry("META-INF/module.bwm"));
            jos.write("module-content".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
            jos.putNextEntry(new JarEntry("lib/dependency.jar"));
            jos.write("fake-jar".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
        }
        return jarFile;
    }

    /**
     * Mirrors BWModulePackageMojo.repackageJarWithUpdatedManifest:
     * copies all entries into a new JAR, replacing MANIFEST.MF with ManifestWriter bytes,
     * then replaces the original file atomically.
     */
    private void repackageJarWithUpdatedManifest(File jarFile, Manifest updatedManifest)
            throws Exception {
        File tempJar = File.createTempFile("bwmod_", ".jar");
        byte[] buffer = new byte[8192];
        byte[] manifestBytes = ManifestWriter.toBytes(updatedManifest);
        try (JarFile jf = new JarFile(jarFile);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempJar))) {
            jos.putNextEntry(new JarEntry("META-INF/"));
            jos.closeEntry();
            jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jos.write(manifestBytes);
            jos.closeEntry();
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if ("META-INF/".equalsIgnoreCase(entryName) ||
                        "META-INF/MANIFEST.MF".equalsIgnoreCase(entryName)) {
                    continue;
                }
                jos.putNextEntry(new JarEntry(entryName));
                try (InputStream is = jf.getInputStream(entry)) {
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        jos.write(buffer, 0, len);
                    }
                }
                jos.closeEntry();
            }
        }
        Files.move(tempJar.toPath(), jarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /** Reads raw bytes of META-INF/MANIFEST.MF from a JAR. */
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

    /**
     * Mirrors BWModulePackageMojo.addDependencies() Bundle-ClassPath normalization:
     * trims each comma-separated entry and drops blank ones.
     */
    private static String normalizeBundleClasspath(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return ".";
        }
        StringBuilder normalized = new StringBuilder();
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                if (normalized.length() > 0) normalized.append(",");
                normalized.append(trimmed);
            }
        }
        return normalized.length() > 0 ? normalized.toString() : ".";
    }

    /**
     * Mirrors BWModulePackageMojo.removeExternals(): removes "external" entries and
     * trims/skips blank entries.
     */
    private static String removeExternals(String bundlePath) {
        if (bundlePath == null) return null;
        String[] entries = bundlePath.split(",");
        StringBuilder buffer = new StringBuilder();
        for (String entry : entries) {
            String trimmedEntry = entry.trim();
            if (!trimmedEntry.isEmpty() && !trimmedEntry.contains("external")) {
                if (buffer.length() != 0) buffer.append(",");
                buffer.append(trimmedEntry);
            }
        }
        return buffer.toString();
    }

    // ---------------------------------------------------------------------------
    // Part 1: Module JAR repackaging — no 72-byte wrapping
    // ---------------------------------------------------------------------------

    /**
     * TC-01: Provide-Capability in the module JAR stays within the 72-byte line limit
     * and is still recoverable in full.
     *
     * A module JAR's manifest is read by maven-archiver and by the OSGi runtime, both of
     * which reject an over-long line: that is the "Error assembling JAR: Unable to read
     * manifest file (line too long)" failure. Folding is therefore required, and must be
     * lossless.
     */
    @Test
    void provideCapabilityFoldedWithinLineLimitInModuleJar() throws Exception {
        String provide =
                "com.tibco.bw.module; name=\"com.example.module\"; " +
                "version:Version=\"1.0.0.202607021200\"";
        assertTrue(("Provide-Capability: " + provide).getBytes(StandardCharsets.UTF_8).length > MAX_LINE_BYTES,
                "Pre-condition: attribute line must exceed 72 bytes to trigger folding");

        Manifest mf = buildModuleManifest("Provide-Capability", provide);
        File moduleJar = createOldStyleJar(mf, "module.jar");

        repackageJarWithUpdatedManifest(moduleJar, mf);

        byte[] raw = readManifestBytesFromJar(moduleJar);
        String content = new String(raw, StandardCharsets.UTF_8);
        for (String line : content.split("\r\n", -1)) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= MAX_LINE_BYTES,
                    "Line exceeds " + MAX_LINE_BYTES + " bytes after repack: [" + line + "]");
        }
        try (JarInputStream jis = new JarInputStream(new FileInputStream(moduleJar))) {
            assertEquals(provide, jis.getManifest().getMainAttributes().getValue("Provide-Capability"),
                    "Provide-Capability must be recovered in full from the module JAR");
        }
    }

    /**
     * TC-02: Manifest-Version is the first header in the repacked module JAR.
     */
    @Test
    void manifestVersionIsFirstLineInModuleJar() throws Exception {
        Manifest mf = buildModuleManifest();
        File moduleJar = createOldStyleJar(mf, "module.jar");

        repackageJarWithUpdatedManifest(moduleJar, mf);

        byte[] raw = readManifestBytesFromJar(moduleJar);
        String firstLine = new String(raw, StandardCharsets.UTF_8).split("\r\n")[0];
        assertTrue(firstLine.startsWith("Manifest-Version:"),
                "First line must be Manifest-Version. Got: " + firstLine);
    }

    /**
     * TC-03: Non-manifest JAR entries are preserved intact after repackaging.
     */
    @Test
    void nonManifestEntriesPreservedAfterModuleJarRepack() throws Exception {
        Manifest mf = buildModuleManifest();
        File moduleJar = createOldStyleJar(mf, "module.jar");

        repackageJarWithUpdatedManifest(moduleJar, mf);

        try (JarFile jf = new JarFile(moduleJar)) {
            assertNotNull(jf.getJarEntry("META-INF/module.bwm"),
                    "META-INF/module.bwm must be preserved");
            assertNotNull(jf.getJarEntry("lib/dependency.jar"),
                    "lib/dependency.jar must be preserved");
        }
    }

    /**
     * TC-04: Full attribute values survive a JAR round-trip after repackaging.
     *
     * Simulates ManifestParser.parseManifestFromJAR() reading back the module JAR —
     * the Provide-Capability value must be untruncated.
     */
    @Test
    void provideCapabilitySurvivesModuleJarRoundTrip() throws Exception {
        String provide =
                "com.tibco.bw.module; name=\"com.example.module\"; " +
                "version:Version=\"1.0.0.202607021200\"";
        Manifest mf = buildModuleManifest("Provide-Capability", provide);
        File moduleJar = createOldStyleJar(mf, "module.jar");

        repackageJarWithUpdatedManifest(moduleJar, mf);

        try (JarInputStream jis = new JarInputStream(new FileInputStream(moduleJar))) {
            Manifest parsed = jis.getManifest();
            assertNotNull(parsed, "Manifest must be readable from repacked module JAR");
            assertEquals(provide, parsed.getMainAttributes().getValue("Provide-Capability"),
                    "Provide-Capability must not be truncated after JAR round-trip");
        }
    }

    /**
     * TC-05: Bundle-Version is preserved exactly after repackaging.
     *
     * Regression guard: repackaging must not silently drop or alter Bundle-Version.
     */
    @Test
    void bundleVersionPreservedAfterModuleJarRepack() throws Exception {
        String version = "1.0.0.202607021200";
        Manifest mf = buildModuleManifest();
        File moduleJar = createOldStyleJar(mf, "module.jar");

        repackageJarWithUpdatedManifest(moduleJar, mf);

        try (JarInputStream jis = new JarInputStream(new FileInputStream(moduleJar))) {
            Manifest parsed = jis.getManifest();
            assertEquals(version, parsed.getMainAttributes().getValue("Bundle-Version"),
                    "Bundle-Version must survive repackaging unchanged");
        }
    }

    // ---------------------------------------------------------------------------
    // Part 2: Bundle-ClassPath normalization
    // ---------------------------------------------------------------------------

    /**
     * TC-06: null Bundle-ClassPath → default ".".
     */
    @Test
    void bundleClasspathNullDefaultsToDot() {
        assertEquals(".", normalizeBundleClasspath(null));
    }

    /**
     * TC-07: Empty Bundle-ClassPath → default ".".
     */
    @Test
    void bundleClasspathEmptyDefaultsToDot() {
        assertEquals(".", normalizeBundleClasspath(""));
        assertEquals(".", normalizeBundleClasspath("   "));
    }

    /**
     * TC-08: ". , " (the ". ,<space>" artifact from a previously line-wrapped manifest)
     * normalizes to ".".
     *
     * Root cause: when a previously line-wrapped "Bundle-ClassPath: .\r\n , \r\n"
     * is read by Java's Manifest parser, the value becomes ". , " — a dot followed by
     * a comma and whitespace, with no real lib entry. Normalization must strip this.
     */
    @Test
    void bundleClasspathCorruptedWithBlankEntriesNormalizesToDot() {
        assertEquals(".", normalizeBundleClasspath(". , "));
        assertEquals(".", normalizeBundleClasspath(".,  "));
        assertEquals(".", normalizeBundleClasspath(". ,"));
    }

    /**
     * TC-09: Real lib entries are preserved after normalization.
     */
    @Test
    void bundleClasspathRealEntriesPreserved() {
        assertEquals(".,lib/foo.jar", normalizeBundleClasspath(".,lib/foo.jar"));
        assertEquals(".,lib/foo.jar,lib/bar.jar",
                normalizeBundleClasspath(".,lib/foo.jar,lib/bar.jar"));
    }

    /**
     * TC-10: Lib entries with surrounding whitespace (from previously-wrapped manifest)
     * are trimmed and preserved.
     */
    @Test
    void bundleClasspathEntriesWithWhitespaceTrimmed() {
        assertEquals(".,lib/foo.jar", normalizeBundleClasspath(". , lib/foo.jar"));
        assertEquals(".,lib/foo.jar,lib/bar.jar",
                normalizeBundleClasspath(". , lib/foo.jar , lib/bar.jar"));
    }

    /**
     * TC-11: Trailing comma (degenerate split case) does not produce a blank entry.
     */
    @Test
    void bundleClasspathTrailingCommaProducesNoBlankEntry() {
        assertEquals(".,lib/foo.jar", normalizeBundleClasspath(".,lib/foo.jar,"));
    }

    // ---------------------------------------------------------------------------
    // Part 3: removeExternals normalization
    // ---------------------------------------------------------------------------

    /**
     * TC-12: External entries are removed; non-external entries are kept.
     */
    @Test
    void removeExternalsStripsExternalEntries() {
        assertEquals(".", removeExternals(".,external:plugin/foo.jar"));
        assertEquals(".,lib/bar.jar", removeExternals(".,external:plugin/foo.jar,lib/bar.jar"));
    }

    /**
     * TC-13: Blank/whitespace entries from a previously-wrapped manifest are skipped.
     */
    @Test
    void removeExternalsSkipsBlankEntries() {
        assertEquals(".", removeExternals(". , "));
        assertEquals(".,lib/foo.jar", removeExternals(". , lib/foo.jar"));
    }

    /**
     * TC-14: Value with no external entries passes through unmodified (after trimming).
     */
    @Test
    void removeExternalsLeavesCleanValueUnchanged() {
        assertEquals(".,lib/foo.jar,lib/bar.jar",
                removeExternals(".,lib/foo.jar,lib/bar.jar"));
    }

    /**
     * TC-15: Regression — a Require-Capability long enough to need folding must come back
     * from the repacked module JAR intact, with no line over the 72-byte limit.
     */
    @Test
    void longRequireCapabilityFoldedWithinLineLimitInModuleJar() throws Exception {
        String require =
                "com.tibco.bw.model; filter:=\"(name=bwext)\"," +
                "com.tibco.bw.module; filter:=\"(&(name=com.example.sharedmod)" +
                "(version=2.5.3.qualifier20260702_093000))\"";
        assertTrue(("Require-Capability: " + require).getBytes(StandardCharsets.UTF_8).length > MAX_LINE_BYTES,
                "Pre-condition: Require-Capability line must exceed 72 bytes");

        Manifest mf = buildModuleManifest("Require-Capability", require);
        File moduleJar = createOldStyleJar(mf, "appmod.jar");

        repackageJarWithUpdatedManifest(moduleJar, mf);

        byte[] raw = readManifestBytesFromJar(moduleJar);
        String content = new String(raw, StandardCharsets.UTF_8);
        for (String line : content.split("\r\n", -1)) {
            assertTrue(line.getBytes(StandardCharsets.UTF_8).length <= MAX_LINE_BYTES,
                    "Line exceeds " + MAX_LINE_BYTES + " bytes in repacked module JAR: [" + line + "]");
        }
        try (JarInputStream jis = new JarInputStream(new FileInputStream(moduleJar))) {
            Manifest parsed = jis.getManifest();
            assertEquals(require, parsed.getMainAttributes().getValue("Require-Capability"),
                    "Full Require-Capability value must survive JAR round-trip");
        }
    }
}
