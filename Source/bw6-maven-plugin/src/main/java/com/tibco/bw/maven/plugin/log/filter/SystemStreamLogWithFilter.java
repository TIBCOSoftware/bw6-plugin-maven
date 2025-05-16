package com.tibco.bw.maven.plugin.log.filter;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.maven.plugin.logging.Log;

/**
 * "standard" apache output and error output stream logger with filter to remove messages from non BW plugins.
 *
 * @author lshivara
 */
public class SystemStreamLogWithFilter  implements Log
{
	
	private static String[] EXCLUSIONS = {"org.apache.maven", "org.eclipse.aether", "org.eclipse.m2e", "org.eclipse.jgit",
			"jcmd ", "excludes []", "includes []", "-application", "-ws", "win32",
			"com.tibco.bw.core.design.project.BwDesignApplication", "/eclipse", "\\eclipse", "\\src\\test\\resources",
			"\\src\\main\\resources", "/src/test/resources", "/src/main/resources" };
	
	private String previousLogLine = null;
	
    /**
     * @see org.apache.maven.plugin.logging.Log#debug(java.lang.CharSequence)
     */
    public void debug(CharSequence content)
    {
        print("debug", content);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#debug(java.lang.CharSequence, java.lang.Throwable)
     */
    public void debug(CharSequence content, Throwable error)
    {
        print("debug", content, error);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#debug(java.lang.Throwable)
     */
    public void debug(Throwable error)
    {
        print("debug", error);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#info(java.lang.CharSequence)
     */
    public void info(CharSequence content)
    {
        print("info", content);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#info(java.lang.CharSequence, java.lang.Throwable)
     */
    public void info(CharSequence content, Throwable error)
    {
        print("info", content, error);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#info(java.lang.Throwable)
     */
    public void info(Throwable error)
    {
        print("info", error);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#warn(java.lang.CharSequence)
     */
    public void warn(CharSequence content)
    {
        print("warn", content);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#warn(java.lang.CharSequence, java.lang.Throwable)
     */
    public void warn(CharSequence content, Throwable error)
    {
        print("warn", content, error);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#warn(java.lang.Throwable)
     */
    public void warn(Throwable error)
    {
        print("warn", error);
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#error(java.lang.CharSequence)
     */
    public void error(CharSequence content)
    {
        System.err.println("[error] " + content.toString());
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#error(java.lang.CharSequence, java.lang.Throwable)
     */
    public void error(CharSequence content, Throwable error)
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);

        error.printStackTrace(pWriter);

        System.err.println("[error] " + content.toString() + "\n\n" + sWriter.toString());
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#error(java.lang.Throwable)
     */
    public void error(Throwable error)
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);

        error.printStackTrace(pWriter);

        System.err.println("----[error] " + sWriter.toString());
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        // TODO Not sure how best to set these for this implementation...
        return false;
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return true;
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isWarnEnabled()
     */
    public boolean isWarnEnabled()
    {
        return true;
    }

    /**
     * @see org.apache.maven.plugin.logging.Log#isErrorEnabled()
     */
    public boolean isErrorEnabled()
    {
        return true;
    }
    
    private void print(String prefix, CharSequence content)
    {
    	boolean nonBWLog = false;
    	String contentString = content.toString();
    	for (String exclude: EXCLUSIONS) {
    		nonBWLog = contentString.contains(exclude) ? true : false;
    		if (nonBWLog) {
    			break;
    		}
    	}
        if (!nonBWLog) {
        	printLog(prefix, contentString);
        }
    }

    private void printLog(String prefix, String content)
    {
    	String logPrintLine =  content.toString();
    	//log formatting - Remove successive blank lines in log
		if (previousLogLine == null || previousLogLine.isEmpty()) {
			if (!logPrintLine.isEmpty()) {
				System.out.println("[" + prefix + "] " + logPrintLine);
			}
		}else {
			System.out.println("[" + prefix + "] " + logPrintLine);
		}
        previousLogLine = logPrintLine;
    }
    

    private void print(String prefix, Throwable error)
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);

        error.printStackTrace(pWriter);
        print(prefix, sWriter.toString());
    }

    private void print(String prefix, CharSequence content, Throwable error)
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);

        error.printStackTrace(pWriter);
        printLog(prefix, content.toString() + "\n" + sWriter.toString());
    }

}