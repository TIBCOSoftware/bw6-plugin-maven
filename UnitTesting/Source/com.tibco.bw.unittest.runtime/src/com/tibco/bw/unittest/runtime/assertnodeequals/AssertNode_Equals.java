package com.tibco.bw.unittest.runtime.assertnodeequals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import org.genxdm.NodeKind;
import org.genxdm.ProcessingContext;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

import com.tibco.bw.runtime.ActivityFault;
import com.tibco.bw.runtime.ActivityLifecycleFault;
import com.tibco.bw.runtime.AsyncActivity;
import com.tibco.bw.runtime.AsyncActivityCompletionNotifier;
import com.tibco.bw.runtime.AsyncActivityController;
import com.tibco.bw.runtime.ProcessContext;
import com.tibco.bw.runtime.annotation.Property;
import com.tibco.bw.runtime.util.XMLUtils;
import com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals;
import com.tibco.bw.unittest.runtime.faults.ActivityFaults;
import com.tibco.bw.unittest.runtime.faults.PrimitiveFaults;
import com.tibco.bw.unittest.runtime.utils.PrimitiveSelector;

public class AssertNode_Equals<N> extends AsyncActivity<N> {

	@Property
	public AssertNodeEquals assertNodeModel;
	
	protected ComputePrimitivesComparison primitiveComparator;
	private ComputeActivityComparison activityComparator;
	
	@Override
	public void init() throws ActivityLifecycleFault {
		super.init();	
		primitiveComparator = new ComputePrimitivesComparison();
		activityComparator = new ComputeActivityComparison();
	}
	
	@Override
	public void execute(N activityInputData, ProcessContext<N> processCtx, AsyncActivityController activityController) throws ActivityFault {

		AsyncActivityCompletionNotifier notifier = activityController.setPending(0);
		ProcessingContext<N> xmlContext = processCtx.getXMLProcessingContext();
		if (activityLogger.isDebugEnabled()) {
			activityLogger.debug("Input Received:" + XMLUtils.serializeNode(activityInputData, xmlContext));
		}

		try{
			if (activityInputData != null) {
				MutableModel<N> model = xmlContext.getMutableContext().getModel();

				switch(assertNodeModel.getAssertionMode()){
					case PRIMITIVE:
						assertPrimitive(model, activityInputData);		
						break;
					case ACTIVITY:
						assertActivity(model, activityInputData,xmlContext);
						break;
					default:
						break;
				}	
				
			}
		}		
		catch(Throwable e){
			notifier.setReady(e);
		}	
		notifier.setReady(XMLUtils.serializeNode(activityInputData, xmlContext));
	}

	
	@Override
	public N postExecute(Serializable results, ProcessContext<N> processCtx) throws ActivityFault {
				
		if(results instanceof Throwable){
			
			if(results instanceof PrimitiveFaults){
				PrimitiveFaults pFaults = (PrimitiveFaults)results;
				throw pFaults;	
			}
			
			if(results instanceof ActivityFaults){
				ActivityFaults aFaults = (ActivityFaults)results;
				throw aFaults;	
			}
			
			throw new ActivityFault(activityContext,(Throwable)results);
		}
				
		//for now we'll return null but implementation might be needed
		return null;
	}
	
	@Override
	public void cancel(ProcessContext<N> processCtx) {			
	}

	
	private synchronized void assertPrimitive(MutableModel<N> model,N activityInputData) throws PrimitiveFaults{
		N testInput = null,goldInput =null;
		N dataTestElement = null;
		String inputValue = null, goldValue = null;
		Hashtable<String, String> errorReport = new Hashtable<String, String>();
		errorReport.clear();
		
		Iterable<N> dataElementList = model.getChildElementsByName(activityInputData, null, "compare");
		
		if (dataElementList != null) {		
			PrimitiveSelector[] selector = PrimitiveSelector.values();
			PrimitiveSelector psSelected = null;
			
			StringBuilder sb = new StringBuilder();
			for(N element: dataElementList){				
							
				for(int i= 0;i<PrimitiveSelector.values().length;i++){
					psSelected = selector[i];
					dataTestElement = model.getFirstChildElementByName(element, null, psSelected.getValue()); 
					if(dataTestElement != null){
						break;
					}
				}

				if(dataTestElement != null){
					testInput = model.getFirstChildElementByName(dataTestElement, null, "testInput"); 
					goldInput = model.getFirstChildElementByName(dataTestElement, null, "goldInput");				
				}

				if(testInput != null && goldInput != null){
					inputValue = model.getStringValue(testInput);
					goldValue = model.getStringValue(goldInput);
					if(!primitiveComparator.compareValues(psSelected, inputValue, goldValue)){
						String previousValue = "";
						sb.delete(0, sb.length());
						if(errorReport.containsKey("<<" + psSelected.getValue() + "_Primitives")){
							previousValue = 	errorReport.get("<<" + psSelected.getValue() + "_Primitives").split(">>")[0];
						}
						errorReport.put("<<" + psSelected.getValue() + "_Primitives", sb.append(previousValue).append(" Expected: ").append(goldValue).append(" But Got:").append(inputValue).append(">>").toString());					
					}
				}
			}
			
			if(!errorReport.isEmpty()){
				throw new PrimitiveFaults(activityContext,errorReport);
			}
		}
		else{
			throw new PrimitiveFaults(activityContext, new IllegalArgumentException("Source or Target string must not be empty"));
		} 
	}
	
	private synchronized void assertActivity(MutableModel<N> model,N activityInputData,ProcessingContext<N> xmlContext) throws Throwable{
		N testInputStringNode = null;
		N testOutputStringNode = null;
		N goldOutputNode = null;
		N elementReceived = null;
		N elementExpected = null;
		
	 	NodeFactory<N> nodeFactory = xmlContext.getMutableContext().getNodeFactory(); 
	 
    	
		testInputStringNode = model.getFirstChildElementByName(activityInputData, null, "testInputString");
		N deserializedInputNode = XMLUtils.deserializeNode(model.getStringValue(testInputStringNode), xmlContext);
		elementReceived = model.getFirstChildElement(deserializedInputNode);
		
		goldOutputNode = model.getFirstChildElementByName(activityInputData, null, "goldOutput");
		if(assertNodeModel.isGoldOuputFromFile()){
			//created element node
			N nPath = model.getFirstChildElement(goldOutputNode);
			String pathToXml = model.getStringValue(nPath);
			
			testOutputStringNode = nodeFactory.createElement("", "testOutputString", "");
			N testOutputStringNodeValue = nodeFactory.createText(readFromFile(pathToXml));			
			model.appendChild(testOutputStringNode, testOutputStringNodeValue);
			
			N deserializedOutputNode = XMLUtils.deserializeNode(model.getStringValue(testOutputStringNode), xmlContext);
			elementExpected = model.getFirstChildElement(deserializedOutputNode);			
			
		}else{
			elementExpected = model.getFirstChildElement(goldOutputNode);
		}
		
		if (elementReceived != null && elementExpected != null) {	
			if(activityComparator == null){
				activityComparator = new ComputeActivityComparison();
			}
			activityComparator.elementExpected = elementExpected;
			activityComparator.elementReceived = elementReceived;
			activityComparator.processingCtx = xmlContext;
			activityComparator.model = model;
			Hashtable<String, String> theMap = activityComparator.compareValues();
			
			
			if(!theMap.isEmpty()){
				throw new ActivityFaults(activityContext,"inequalities",theMap);
			}
		}
		else{
			throw new ActivityFaults(activityContext,"inequalities", new IllegalArgumentException("testInputString and goldOutput nodes must not be empty!!"));
		}		
	}
	
	private String readFromFile(String pathToXml) throws ActivityFaults{
		StringBuilder xmlString = new StringBuilder(); 
		try (BufferedReader br = new BufferedReader(new FileReader(pathToXml)))
		{
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {				
				xmlString.append(sCurrentLine);
			}
		} 
		catch (IOException e) {
			throw new ActivityFaults(activityContext, "error",e);
		}
				
		return xmlString.toString();
	}
	
	//this method might be used later to build output results, So we're keeping it for the time being
	private N buildOutputRootNode(final ProcessingContext<N> xmlContext) {
		final FragmentBuilder<N> builder = xmlContext.newFragmentBuilder();
		builder.startDocument(null, "xml");

		try {
			builder.startElement(activityContext.getActivityInputType().getTargetNamespace(),activityContext.getActivityInputType().getLocalName(), "demo");
			
			try {
				// Trivial implementation
			} finally {
				builder.endElement();
			}
		} finally {
			builder.endDocument();
		}
		return xmlContext.getModel().getFirstChild(builder.getNode());
	}
	
	class ComputeActivityComparison{
		private boolean isEquals;		
		protected N elementExpected = null;
		protected N elementReceived = null;
		protected MutableModel<N> model = null;
		protected ProcessingContext<N> processingCtx = null;
		private Hashtable<String, String> errorReport = null;
		private StringBuilder sb;
	
		public Hashtable<String, String> compareValues(){
			errorReport = new Hashtable<String, String>();
			errorReport.clear();
			sb = new StringBuilder();
			
			if(elementReceived == null || elementReceived == null){
				return null;
			}
			
			if(primitiveComparator == null){
				primitiveComparator = new ComputePrimitivesComparison();
			}
			
			
			//compare class Name			
			compareActivityName();
			
			//compare nodes
			compareActivityNodes();
			
			
			//compare leafs and their values
			compareActivityNodeValues();
			
			return errorReport;
		}
		
		private void compareActivityName(){
			String expectedName = model.getLocalName(elementExpected);
			String receivedName = model.getLocalName(elementReceived);
			
			isEquals = primitiveComparator.compareValues(PrimitiveSelector.STRING, receivedName, expectedName);
			if(!isEquals){
				buildErrorReport(errorReport, sb ,"activityName", expectedName,receivedName,true);
			}
			
		}
		
		private void compareActivityNodes(){
			Iterable<N> expectedNodes = model.getChildElements(elementExpected);
			Iterable<N> receivedNodes = model.getChildElements(elementReceived);
			compareNodes(expectedNodes,receivedNodes);			
		}
		
		private void compareActivityNodeValues(){
			Iterable<N> expectedNodes = model.getChildElements(elementExpected);
			Iterable<N> receivedNodes = model.getChildElements(elementReceived);
			compareLeafElements(expectedNodes,receivedNodes);
		}
		
		private void compareNodes(Iterable<N> expectedNodes,Iterable<N> receivedNodes){ 
			boolean equals;
			String expectedNodeName;
			String receivedNodeName = null;
			for(N eElement :  expectedNodes){
				expectedNodeName = model.getLocalName(eElement);
				equals = true;
				for(N rElement : receivedNodes){
					receivedNodeName = model.getLocalName(rElement);												
					equals = primitiveComparator.compareValues(PrimitiveSelector.STRING, receivedNodeName, expectedNodeName);
					
					if(equals){
						if(model.getNodeKind(eElement) == NodeKind.ELEMENT && model.getNodeKind(rElement) == NodeKind.ELEMENT){
							Iterable<N> expNodes = model.getChildElements(eElement);
							Iterable<N> recNodes = model.getChildElements(rElement);	
							if(expNodes.iterator().hasNext() && recNodes.iterator().hasNext()){
								compareNodes(expNodes,recNodes);
							}
						}
						break;
					}
				}
				
				if(!equals){					
					buildErrorReport(errorReport, sb ,"ActivityNode_Report", "Node => " + expectedNodeName,"Node => Not found",true);
				}
			}
		
		}
		
		
		private <A> void compareLeafElements(Iterable<N> expectedNodes,Iterable<N> receivedNodes){
            boolean equals = false;
            
            String expectedNodeName;
			
			for(N eElement :  expectedNodes){
				
				expectedNodeName = model.getLocalName(eElement);
		
				equals = compareElement(eElement, receivedNodes);
								
				if(!equals){
					buildErrorReport(errorReport, sb ,"NodeValue_Report", "NodeValueFor => " + expectedNodeName," Value: Node Has No Equal",false);
				}
			}		
		}
		
		
		private boolean compareElement(N eElement, Iterable<N> receivedNodes){
			boolean equals;
            boolean nodeFound = false;
			String expectedNodeName = model.getLocalName(eElement);
			String receivedNodeName = null;
			
			for(N rElement : receivedNodes){
				receivedNodeName = model.getLocalName(rElement);				

				equals = primitiveComparator.compareValues(PrimitiveSelector.STRING, receivedNodeName, expectedNodeName);

				if(equals){					
					if(model.getNodeKind(eElement) == NodeKind.ELEMENT && model.getNodeKind(rElement) == NodeKind.ELEMENT){
						Iterable<N> expNodes = model.getChildElements(eElement);
						Iterable<N> recNodes = model.getChildElements(rElement);

						if(expNodes.iterator().hasNext() && recNodes.iterator().hasNext()){ 
							nodeFound = compareNestedElements(expNodes,recNodes);	
							if(nodeFound){
								break;
							}							
						}
						else{ //we have no child elements so check if they are leafs on the tree

							N firstNe = model.getFirstChild(eElement); N firstNr = model.getFirstChild(rElement);
							N lastNe = model.getLastChild(eElement);   N lastNr = model.getLastChild(rElement);

							if(firstNe != null && lastNe != null && firstNr != null && lastNr != null){									
								if(model.getNodeKind(firstNe).equals(NodeKind.TEXT)  && model.getNodeKind(lastNe).equals(NodeKind.TEXT) &&
										model.getNodeKind(firstNr).equals(NodeKind.TEXT)  && model.getNodeKind(lastNr).equals(NodeKind.TEXT) ){
									boolean leafEquals;
									String valueEx = model.getStringValue(firstNe);
									String valueRe = model.getStringValue(firstNr);

									leafEquals = primitiveComparator.compareValues(PrimitiveSelector.STRING, valueRe, valueEx);

									if(!leafEquals){
										nodeFound = false;
										continue;
									}
									else{
										nodeFound = true;
										break;
									}
								}
							}
						}
					}			
				}

			}//end for
						
			return nodeFound;
		}
		
		private boolean compareNestedElements(Iterable<N> expNodes,Iterable<N> recNodes){

            boolean nodeEquals;
            boolean potentiallyEqual = false;
            
			String expectedNodeName;
			String receivedNodeName = null;
					
			expectedElementList:
				for(N eElement :  expNodes){
					
					expectedNodeName = model.getLocalName(eElement);
					nodeEquals = true; //assume equality
					

					for(N rElement : recNodes){
						receivedNodeName = model.getLocalName(rElement);				

						nodeEquals = primitiveComparator.compareValues(PrimitiveSelector.STRING, receivedNodeName, expectedNodeName);
						if(nodeEquals){ //nodes are equal

							if(model.getNodeKind(eElement) == NodeKind.ELEMENT && model.getNodeKind(rElement) == NodeKind.ELEMENT){
								Iterable<N> expNod = model.getChildElements(eElement);
								Iterable<N> recNod = model.getChildElements(rElement);

								if(expNod.iterator().hasNext() && recNod.iterator().hasNext()){ 
									compareNestedElements(expNod,recNod);							
								}

								N firstNe = model.getFirstChild(eElement); N firstNr = model.getFirstChild(rElement);

								if(firstNe != null  && firstNr != null){									
									if(model.getNodeKind(firstNe).equals(NodeKind.TEXT)  && model.getNodeKind(firstNr).equals(NodeKind.TEXT)){
										String valueEx = model.getStringValue(firstNe);
										String valueRe = model.getStringValue(firstNr);

										boolean leafEquals = primitiveComparator.compareValues(PrimitiveSelector.STRING, valueRe, valueEx);

										if(!leafEquals){	
											potentiallyEqual = false;
											break expectedElementList;								
										}
										else{
											potentiallyEqual = true;
											break;
										}									
									}
								}
							}						
						}
					}//inner for				

				}//outer for
		
			return potentiallyEqual;
		}
		
		
		
		private void buildErrorReport(Hashtable<String, String> errorReport, StringBuilder sb, String key, String expectedValue, String receivedValue,boolean appendFixedMsgs){
			String previousValue = "";
			String expected = " Expected:";
			String got = "  Got:";
			sb.delete(0, sb.length());
			if(errorReport.containsKey("<<" + key)){
				previousValue = 	errorReport.get("<<" + key).split(">>")[0];				
			}
			errorReport.put("<<" + key , sb.append(previousValue).append(appendFixedMsgs?expected:" ").append(expectedValue).append(appendFixedMsgs?got:" ").append(receivedValue).append(" >>").toString());
		}
	}
	
	
	class ComputePrimitivesComparison{
		
		protected  boolean isEquals;
		protected  int ans;
		
		public boolean compareValues(PrimitiveSelector primitiveSelector, String source,String target){
			
			if((source == null || source.isEmpty()) || (target == null || target.isEmpty())){
				try {
					throw new PrimitiveFaults(activityContext, new IllegalArgumentException("Source or Target string must not be empty"));
				} catch (PrimitiveFaults e) {					
					e.printStackTrace();
				}
			}
			
			switch(primitiveSelector){
				case STRING:			
					isEquals = source.trim().equals(target.trim());					
					break;
				case BOOLEAN:
					boolean s = Boolean.valueOf(source);
					boolean t = Boolean.valueOf(target);
					ans = Boolean.compare(s, t);
					if(ans != 0){
						isEquals = false;
					}
					break; 
				case DATETIME:
					DateFormat dtformatter = DateFormat.getDateTimeInstance();
					Date sDate,tDate;
					try {
						sDate = dtformatter.parse(source);
						tDate = dtformatter.parse(target);
						ans = sDate.compareTo(tDate);
					} catch (ParseException e) {						
						e.printStackTrace();
					}	
					if(ans != 0){
						isEquals = false; 
					}
					break;
				case DOUBLE:
					double sDbl = Double.valueOf(source);
					double tDbl = Double.valueOf(target);
					ans = Double.compare(sDbl, tDbl);
					if(ans != 0){
						isEquals = false; 
					}
					break;
				case FLOAT:
					float sFlt = Float.valueOf(source);
					float tFlt = Float.valueOf(target);
					ans = Float.compare(sFlt, tFlt);
					if(ans != 0){
						isEquals = false;
					}
					break;
				case INTEGER:
					int sInt = Integer.valueOf(source);
					int tInt = Integer.valueOf(target);
					ans = Integer.compare(sInt, tInt);
					if(ans != 0){
						isEquals = false; 
					}
					break;		
				default:
					break;
			}
			
			return isEquals;
			
		}
	}	
}
