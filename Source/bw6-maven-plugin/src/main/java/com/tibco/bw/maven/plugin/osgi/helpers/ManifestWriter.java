package com.tibco.bw.maven.plugin.osgi.helpers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

    public static File updateManifest(MavenProject project , Manifest mf) throws IOException {

        File mfile = new File(project.getBuild().getDirectory(), "MANIFEST.MF");
        mfile.getParentFile().mkdirs();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(mfile))) {
            writeManifestWithSmartWrapping(mf, os);
        }
        return mfile;
    }

    public static void writeManifest(File targetFile, Manifest mf) throws IOException {
        targetFile.getParentFile().mkdirs();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            writeManifestWithSmartWrapping(mf, os);
        }
    }

    private static void writeManifestWithSmartWrapping(Manifest mf, OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        for (Map.Entry<Object, Object> entry : mf.getMainAttributes().entrySet()) {
            String name = entry.getKey().toString();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            writer.print(name + ": " + value);
            writer.print("\r\n");
        }
        writer.print("\r\n");
        writer.flush();
    }

    /**
     * Returns the manifest serialized as UTF-8 bytes using the same safe wrapping
     * as {@link #writeManifest(File, Manifest)}.  Useful when the caller needs a
     * byte array rather than a file (e.g. for embedding in a ZIP entry).
     */
    public static byte[] toBytes(Manifest mf) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeManifestWithSmartWrapping(mf, baos);
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
