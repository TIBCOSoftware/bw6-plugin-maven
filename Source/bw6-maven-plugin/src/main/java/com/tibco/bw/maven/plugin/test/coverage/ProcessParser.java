package com.tibco.bw.maven.plugin.test.coverage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ProcessParser extends DefaultHandler
{
	
	private ProcessCoverage  coverage = new ProcessCoverage();
	
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException 
	{
		String name = qName.toString();
		
		switch( name )
		{
			case "bpws:process" :
			
				String processName = attr.getValue("name");
				coverage.setProcessName(processName);
				
			break;
			
			case "tibex:ProcessInfo":
				String callable = attr.getValue("callable");
				coverage.setSubProcess( Boolean.parseBoolean(callable));
			break;
			
			case "bpws:link":
				String transition = attr.getValue("name");
				coverage.getTransitions().add(transition);
				
			break;
			
			case "tibex:activityExtension":
			case "tibex:receiveEvent":
			case "tibex:extActivity":	
			case "bpws:invoke":
				String activity = attr.getValue("name");
				coverage.getActivities().add( activity );
			break;
		}
				
	}


	public ProcessCoverage getCoverage() {
		return coverage;
	}
}
