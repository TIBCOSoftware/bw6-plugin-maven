package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import com.tibco.bw.maven.plugin.utils.BWProjectUtils;
import com.tibco.bw.maven.plugin.utils.BWProjectUtils.MODULE;
import com.tibco.bw.maven.plugin.utils.Constants;


public class ManifestWriter {

    /**
     * Maximum number of bytes a manifest line may occupy, per the JAR File Specification.
     * Readers (java.util.jar.Manifest, and plexus-archiver via maven-archiver) reject
     * anything longer, so every header has to be folded onto continuation lines.
     */
    private static final int MAX_LINE_BYTES = 72;

    public static File updateManifest(MavenProject project , Manifest mf) throws IOException {

        File mfile = new File(project.getBuild().getDirectory(), "MANIFEST.MF");
        mfile.getParentFile().mkdirs();
        // This file is handed to maven-archiver as the JAR manifest, so it must fold.
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(mfile))) {
            writeManifestWithSmartWrapping(mf, os, true);
        }
        return mfile;
    }

    public static void writeManifest(File targetFile, Manifest mf) throws IOException {
        targetFile.getParentFile().mkdirs();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            writeManifestWithSmartWrapping(mf, os, true);
        }
    }

    /**
     * Writes the manifest with every header on one physical line, however long.
     *
     * This is only for the copy of META-INF/MANIFEST.MF that lives in the project
     * directory and is read back by BW Studio, never for a manifest that ends up
     * inside a JAR. See AMBW-55624: when the install goal rewrote the workspace
     * manifest with continuation lines, the text-edit offsets Eclipse PDE holds for
     * a "rename shared module" refactoring no longer lined up with the file, and the
     * edit was spliced into the middle of a header - Bundle-Version came back as
     * "le-Version", after which Studio reported "'Bundle-SymbolicName' header is
     * required" with no quick fix available.
     *
     * The resulting file deliberately violates the 72-byte limit of the JAR File
     * Specification, so it must not be handed to an archiver. Use
     * {@link #writeManifest(File, Manifest)} or {@link #toBytes(Manifest)} for that.
     */
    public static void writeManifestUnfolded(File targetFile, Manifest mf) throws IOException {
        targetFile.getParentFile().mkdirs();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            writeManifestWithSmartWrapping(mf, os, false);
        }
    }

    private static void writeManifestWithSmartWrapping(Manifest mf, OutputStream out, boolean fold) throws IOException {
        Attributes mainAttrs = mf.getMainAttributes();
        // JAR spec requires Manifest-Version to be the first attribute; HashMap iteration order is not guaranteed
        String mfVersion = mainAttrs.getValue(Name.MANIFEST_VERSION);
        printHeader(out, "Manifest-Version: " + (mfVersion != null ? mfVersion : "1.0"), fold);
        for (Map.Entry<Object, Object> entry : mainAttrs.entrySet()) {
            if (Name.MANIFEST_VERSION.equals(entry.getKey())) continue;
            String name = entry.getKey().toString();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            printHeader(out, name + ": " + value, fold);
        }
        println(out);

        // Per-entry sections. BW manifests normally have none, but Manifest.write() emits
        // them, and dropping them silently would change the output for any manifest that has them.
        for (Map.Entry<String, Attributes> section : mf.getEntries().entrySet()) {
            printHeader(out, "Name: " + section.getKey(), fold);
            for (Map.Entry<Object, Object> entry : section.getValue().entrySet()) {
                String name = entry.getKey().toString();
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                printHeader(out, name + ": " + value, fold);
            }
            println(out);
        }
        out.flush();
    }

    /**
     * Writes one header, folding it onto continuation lines so that no line exceeds
     * {@value #MAX_LINE_BYTES} bytes. A continuation line starts with a single space,
     * which the reader strips before re-joining, so the folding is transparent.
     *
     * The limit is counted in UTF-8 bytes, not characters. Splitting a multi-byte
     * character across two lines is safe because the reader concatenates the raw bytes
     * and decodes afterwards. This mirrors java.util.jar.Manifest.println72, so the
     * output stays byte-identical to what mf.write(os) produced before 2.11.4.
     *
     * Folding is not cosmetic: java.util.jar.Manifest reads a header into a fixed 512-byte
     * buffer and throws "line too long (line N)" past that, which plexus-archiver surfaces
     * as "Unable to read manifest file". A BW module with a realistic Provide-Capability
     * comfortably exceeds 512 bytes, so an unfolded manifest fails the jar goal outright.
     *
     * @param fold false to emit the header on a single physical line regardless of length;
     *             only correct for the workspace manifest, see {@link #writeManifestUnfolded}.
     */
    private static void printHeader(OutputStream out, String line, boolean fold) throws IOException {
        if (line.length() > 0) {
            byte[] lineBytes = line.getBytes(StandardCharsets.UTF_8);
            int length = lineBytes.length;
            // The first line carries no leading space, so it holds one byte more
            // than the continuation lines that follow it.
            out.write(lineBytes[0]);
            int pos = 1;
            while (fold && length - pos > MAX_LINE_BYTES - 1) {
                out.write(lineBytes, pos, MAX_LINE_BYTES - 1);
                pos += MAX_LINE_BYTES - 1;
                println(out);
                out.write(' ');
            }
            out.write(lineBytes, pos, length - pos);
        }
        println(out);
    }

    private static void println(OutputStream out) throws IOException {
        out.write('\r');
        out.write('\n');
    }

    /**
     * Returns the manifest serialized as UTF-8 bytes using the same safe wrapping
     * as {@link #writeManifest(File, Manifest)}.  Useful when the caller needs a
     * byte array rather than a file (e.g. for embedding in a ZIP entry).
     */
    public static byte[] toBytes(Manifest mf) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeManifestWithSmartWrapping(mf, baos, true);
        return baos.toByteArray();
    }

    public static void updateManifestVersion(MavenProject project , Manifest mf, String qualifierReplacement, MavenSession session)
    {
        Attributes attributes = mf.getMainAttributes();
        
        String projectVersion = project.getVersion();
        if( projectVersion.indexOf("-SNAPSHOT") != -1 )
        {
        	projectVersion = projectVersion.replace("-SNAPSHOT", ".qualifier");
        	projectVersion = getManifestVersion(mf, projectVersion, qualifierReplacement);
        }
        
    	attributes.put(Name.MANIFEST_VERSION, projectVersion);
        attributes.putValue(Constants.BUNDLE_VERSION, projectVersion );

        //Updating provide capability for Shared Modules
        if(BWProjectUtils.getModuleType(mf) == MODULE.SHAREDMODULE){
        	String updatedProvide = ManifestParser.getUpdatedProvideCapabilities(mf, projectVersion);
        	if(updatedProvide!= null && !updatedProvide.isEmpty()) {
        		attributes.putValue(Constants.BUNDLE_PROVIDE_CAPABILITY, updatedProvide);
        	}
        }
        
        if(BWProjectUtils.getModuleType(mf) == MODULE.APPMODULE || BWProjectUtils.getModuleType(mf) == MODULE.SHAREDMODULE) {
        	Set<Artifact> list=project.getDependencyArtifacts();
        	if(list != null && !list.isEmpty()) {
        		String reqCapability = mf.getMainAttributes().getValue(Constants.BUNDLE_REQUIRE_CAPABILITY);
            	if(reqCapability !=  null && !reqCapability.isEmpty()) {
		        	String updatedRequire=ManifestParser.getRequiredCapabilities(reqCapability, list,session);
		        	if(updatedRequire != null  &&  !updatedRequire.isEmpty()) {
		        		attributes.putValue(Constants.BUNDLE_REQUIRE_CAPABILITY, updatedRequire);
		        	}
            	}
        	}
        }
        

    }
    
    private static String getManifestVersion( Manifest manifest , String version, String qualifierReplacement) 
    {    	
    	return VersionParser.getcalculatedOSGiVersion(version, qualifierReplacement);
    }

}
