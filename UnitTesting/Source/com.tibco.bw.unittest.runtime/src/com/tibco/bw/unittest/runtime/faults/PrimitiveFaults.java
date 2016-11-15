package com.tibco.bw.unittest.runtime.faults;

import java.util.Hashtable;

import javax.xml.namespace.QName;

import org.genxdm.ProcessingContext;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

import com.tibco.bw.unittest.runtime.RuntimeMessageBundle;
import com.tibco.bw.runtime.ActivityContext;
import com.tibco.bw.runtime.ActivityFault;

@SuppressWarnings("serial")
public class PrimitiveFaults extends ActivityFault {	

	private String strReason;
	private String strDetail;
	private Hashtable<String, String> errors;
	public <N> PrimitiveFaults(ActivityContext<N> activityCtx, Throwable reason) {		
		super(activityCtx, ActivityFault.createLocalizedMessage(RuntimeMessageBundle.ASSERT_NODE_EQUALS_PRIMITIVE_FAILURE_INEQUALITY, activityCtx.getActivityName(),reason.getLocalizedMessage()),reason);
		strReason = reason.getMessage();
		strDetail = reason.getLocalizedMessage();
	}

	public <N> PrimitiveFaults(ActivityContext<N> activityCtx, Hashtable<String, String> errorReport) {		
		
		super(activityCtx, ActivityFault.createLocalizedMessage(RuntimeMessageBundle.ASSERT_NODE_EQUALS_PRIMITIVE_FAILURE_INEQUALITY, activityCtx.getActivityName(), errorReport));
		errors = errorReport;
	}
	
	@Override
	public QName getFaultElementQName() {
		return new QName("http://schemas.tibco.com/bw/plugins/bwunit/unittestExceptions","UnitTestException");		
	}

	@Override
	public <N> void buildFault(ProcessingContext<N> pcx) {
		N faultData = this.createFaultMessageElement(pcx);
		NodeFactory<N> factory = pcx.getMutableContext().getNodeFactory();
		MutableModel<N> model = pcx.getMutableContext().getModel();
		
		if(errors != null && !errors.isEmpty()){
			
			for(int i=0;i<errors.size();i++){
				N sqlStateNode = factory.createElement("", "Comparison_" + i, "");
				N sqlStateValue  = factory.createText(strReason);
				model.appendChild(sqlStateNode, sqlStateValue);
				model.appendChild(faultData, sqlStateValue);
			}
			
		}
		else{
			N sqlStateNode = factory.createElement("", "reason", "");
			N sqlStateValue  = factory.createText(strReason);
			model.appendChild(sqlStateNode, sqlStateValue);
			model.appendChild(faultData, sqlStateValue);

			N detailStrNode = factory.createElement("", "detailStr", "");
			N detailStrValue = factory.createText(strDetail);
			model.appendChild(detailStrNode, detailStrValue);
			model.appendChild(faultData, detailStrNode);
		}
		
		this.setData(faultData);
	}

}
