package com.tibco.bw.maven.plugin.test.rest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

public class GeneratejunitReport {
	public void genereateReport(String input, String output) {
		 VelocityEngine ve = new VelocityEngine();
		  ClassLoader classLoader = getClass().getClassLoader();
		    ve.setProperty("resource.loaders", "classpath");
		    ve.setProperty("resource.loader.classpath.class",  ClasspathResourceLoader.class.getName());
		    ve.init();
		    Document root = null;
		    try {
		      SAXBuilder builder = new SAXBuilder();
		      String inputFile = input;
		      root = builder.build(inputFile);
		    } catch (Exception ee) {
		      System.out.println("Exception building Document : " + ee);
		      return;
		    }
		    VelocityContext context = new VelocityContext();
		    context.put(Integer.class.getSimpleName(), Integer.class);
		    context.put(String.class.getSimpleName(), String.class);
		    context.put("root", root);
		    getClass().getClassLoader().getResource("");
		    Template template = ve.getTemplate("templates/JUnitReport.vm");
		    Writer writer = null;
		    try {
		      FileWriter fw = new FileWriter(output);
		      writer = new BufferedWriter(fw);
		      template.merge((Context)context, writer);
		      if (writer != null) {
		        writer.flush();
		        writer.close();
		      }
		      fw.close();
		      System.out.println("Junit Report generated to : " + output);
		    } catch (Exception ee) {
		      System.out.println("Exception : " + ee);
		    }
	}
}
