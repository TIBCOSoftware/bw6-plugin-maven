package com.tibco.bw.studio.maven.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.osgi.framework.BundleException;

public class ManifestParser 
{
	
	public static Map<String,String> parseManifest( IProject project ) throws Exception
	{
		if(project.exists())
		{
			IFile manifest = PDEProject.getManifest(project);
	
			Map<String,String> headers = new HashMap<String,String>();		
			ManifestElement.parseBundleManifest(new FileInputStream( manifest.getLocation().toFile()), headers);
			
			return headers;
		} else {
			throw new Exception("The project - "+ project.getName() + " does not exists in the workspace. Please import the project.");
		}
	}

	
	public static Manifest parseManifest( File mFile )
	{
		Manifest mf = null;
        InputStream is = null;
        
        
        try {
            is = new FileInputStream(mFile);
            mf = new Manifest(is);
        }
        catch(FileNotFoundException f )
        {
        	
        } catch (IOException e) {
		}
        finally
        {
            try 
            {
				is.close();
			}
            catch (IOException e) 
            {
			}
        }

        return mf;
        
	}

}
