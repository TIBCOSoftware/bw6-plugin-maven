///*
// * Copyright(c) 2014 TIBCO Software Inc.
// * All rights reserved.
// *
// * This software is confidential and proprietary information of TIBCO Software Inc.
// *
// */
//
//package test.com.tibco.bw.admin.client.rest;
//
//import java.util.List;
//
//import com.tibco.bw.maven.plugin.admin.client.RemoteDeployer;
//import com.tibco.bw.maven.plugin.admin.client.ClientException;
//import com.tibco.bw.maven.plugin.admin.dto.Agent;
//import com.tibco.bw.maven.plugin.admin.dto.AppNode;
//import com.tibco.bw.maven.plugin.admin.dto.AppSpace;
//import com.tibco.bw.maven.plugin.admin.dto.Application;
//import com.tibco.bw.maven.plugin.admin.dto.Archive;
//import com.tibco.bw.maven.plugin.admin.dto.Domain;
//import com.tibco.bw.maven.plugin.admin.dto.Installation;
//import com.tibco.bw.maven.plugin.admin.dto.Machine;
//import com.tibco.bw.maven.plugin.admin.dto.Property;
//import com.tibco.bw.maven.plugin.admin.dto.AppSpace.AppSpaceRuntimeStatus;
//import com.tibco.bw.maven.plugin.admin.dto.Application.ApplicationRuntimeStates;
//
//import javax.ws.rs.core.Response.*;
///**
// * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
// *
// * @since 1.0.0
// */
//public class AgentClientTester {
//
//	static RemoteDeployer client;
//	private static String domainName = "test2";
//	private static String asName = "as1";
//	private static String nodeName = "nodenew";
//	private static String archiveFilePath = "D:/Installs/BW6/631/bw/6.3/samples/core/admin/ears/samples";
//	private static String archiveFileName = "tibco.bw.sample.palette.file.OrderProcessing.application_1.0.0.ear";
//	private static String appName = "tibco.bw.sample.palette.file.OrderProcessing.application";
//	private static String appVersion = "1.0";
//	
//    /**
//     * @param args
//     */
//    public static void main(final String[] args) throws Exception {
//        String agentName = null;
//        
//        client = new RemoteDeployer("abhide-t430", "8079");
//    
//    	System.out.println("\nInstallation list:");
//        List<Installation> list = client.getInstallations();
//        if (list != null) {
//        	for (Installation listEle : list) {
//        		System.out.println("  " + listEle.getName() +", " + listEle.getLocation());
//        	}
//        } else {
//        	System.out.println("getInstallations returned null");
//        }
//
//    	System.out.println("\nMachine list:");
//        List<Machine> machines = client.getMachines(null);
//        if (machines != null) {
//        	for (Machine machine : machines) {
//        		System.out.println("  " + machine.getName() +", " + machine.getStatus() + ", " + machine.getIpAddress());
//        	}
//        } else {
//        	System.out.println("getInfos returned null");
//        }
//
//    	System.out.println("\nMy agent info:");
//        List<Agent> agents = client.getAgentInfo();
//        if (agents != null) {
//        	for (Agent agent : agents) {
//        		System.out.println("  " + agent.getName() +", " + agent.getState() + ", " + agent.getMachineName());
//        		agentName = agent.getName();
//        	}
//        } else {
//        	System.out.println("getAgentInfo returned null");
//        }
//        
//        
//    	System.out.println("\nAgent list:");
//        agents = client.getInfos(null);
//        if (agents != null) {
//        	for (Agent agent : agents) {
//        		System.out.println("  " + agent.getName() +", " + agent.getState() + ", " + agent.getMachineName());
//        	}
//        } else {
//        	System.out.println("getInfos returned null");
//        }
//        
//    	//System.out.println("\nRegistering agent with tea server");
//        //client.registerTeaAgent(agentName, "http://Salils-MacBook-Pro.local:8777/");
//        //System.out.println("done");
//        
//        listDomains();
//    	try {
//	        System.out.println("\nCreating domain " + domainName);
//	        client.createDomain(domainName, "description", "owner", null, null);
//	        System.out.println("done");
//	        listDomains(); 
//    	} catch (ClientException ce) {
//    		if (ce.getCode() == Status.CONFLICT.getStatusCode()) {
//    			System.out.println("domain already exists");
//    		} else {
//    			System.out.println("got error, exiting: " + ce);
//    			//TODO System.exit(ce.getCode()); // uncomment this after Error handling is fixed
//    		}
//    	}           
//        
//       listArchives(domainName);
//        try {
//	        System.out.println("\nUploading archive " + archiveFilePath+"/"+archiveFileName);
//	        client.uploadArchive(domainName, null, archiveFilePath+"/"+archiveFileName, false);
//	        System.out.println("done");
//	        listArchives(domainName); 
//       	} catch (ClientException ce) {
//    		//if (ce.getCode() == Status.CONFLICT.getStatusCode()) {
//    			System.out.println("archive already exists; replacing it");
//    	        client.uploadArchive(domainName, null, archiveFilePath+"/"+archiveFileName, true);
//    	        System.out.println("done");
//    	        listArchives(domainName); 
//    		//} else {
//    		//	System.out.println("got error, exiting: " + ce);
//    		//	System.exit(ce.getCode());
//    		//}
//       	} finally {
//       		Archive archive = client.getArchive(domainName, archiveFileName);
//       		System.out.println("getArchive returned: " + archive.getName() + ", " + archive.getUploadedTime());
//       		
//       		byte[] archiveContents = client.getArchiveContent(domainName, archiveFileName);
//       		System.out.println("getArchiveContents returned: byte[] size = " + archiveContents.length);
//
//       		// TODO set profileName
//       		//byte[] profleContents = client.exportProfile(domainName, archiveFileName, "profileName");
//       		//System.out.println("exportProfile returned: byte[] size = " + profleContents.length);
//
//       	}
//        
//        AppSpace as = null;
//        listAppSpaces(domainName);
//    	try {
//	        System.out.println("\nCreating appspace: " + asName);
//	        as = client.createAppSpace(domainName, asName, false, 0, null, "description", "owner");
//	        System.out.println("done");
//    	} catch (ClientException ce) {
//    		if (ce.getCode() == Status.CONFLICT.getStatusCode()) {
//    			System.out.println("appspace already exists");
//    		} else {
//    			System.out.println("got error, exiting: " + ce);
//    			System.exit(ce.getCode());
//    		}
//    	} finally {
//    		as = client.getAppSpace(domainName, asName, false, false);
//	        System.out.println("getAppSpace(false,false) returned: " + 
//	        		as.getDomainName() + ", " + as.getName() + ", " + as.getStatus() + ", " + as.getAppNodes()+
//	        		", " + as.getAppNodeRefs());
//    		as = client.getAppSpace(domainName, asName, true, true);
//	        System.out.println("getAppSpace(true,true) returned: " + 
//	        		as.getDomainName() + ", " + as.getName() + ", " + as.getStatus() + ", " + as.getAppNodes()+
//	        		", " + as.getAppNodeRefs());
//	        
//	        // TODO
//	        // client.configAppSpace(domainName, asName, "json format string");
//    	}
//        
//        AppNode node = null;
//        listAppNodes(domainName, asName);
//        try {
//	        System.out.println("\nCreating appnode: " + nodeName);
//	        node = client.createAppNode(domainName, asName, nodeName, agentName, 8060, 0, "description");
//	        System.out.println("done");
//    	} catch (ClientException ce) {
//    		if (ce.getCode() == Status.CONFLICT.getStatusCode()) {
//    			System.out.println("appnode already exists");
//    		} else {
//    			System.out.println("got error, exiting: " + ce);
//    			System.exit(ce.getCode());
//    		}
//    	}  finally {
//    		node = client.getAppNode(domainName, asName, nodeName, false);
//	        System.out.println("getAppNode(false) returned: " + 
//	        		node.getDomainName() + ", " + node.getAppSpaceName() + ", " + node.getName() + ", " + node.getState());
//    		node = client.getAppNode(domainName, asName, nodeName, true);
//	        System.out.println("getAppNode(true) returned: " + 
//	        		node.getDomainName() + ", " + node.getAppSpaceName() + ", " + node.getName() + ", " + node.getState());
//	        
//	        // TODO
//	        // client.configAppNode(domainName, asName, nodeName, "json format string");
//	        // byte[] configProps = client.exportConfig(domainName, asName, nodeName);
//    	}
//                
//        Application app = null;
//        listApplications(domainName, asName);
//        try {
//	        System.out.println("\nDeploying application: ");
//	        app = client.deployApplication(domainName, asName, archiveFileName, null, false, true, null);
//	        System.out.println("done");
//    	} catch (ClientException ce) {
//    		if (ce.getCode() == Status.CONFLICT.getStatusCode()) {
//    			System.out.println("application already exists");
//    		} else {
//    			System.out.println("got error, exiting: " + ce);
//    			System.exit(ce.getCode());
//    		}
//    	} finally {
//            app = client.getApplication(domainName, asName, appName, appVersion);
//            System.out.println("getApplication returned: " + 
//            		app.getName() + ", " + app.getState());
//            
//            System.out.println("app conf properties:");
//            List<Property> conf = 
//            		client.getApplicationConfiguration(domainName, asName, appName, appVersion, nodeName);
//            if (conf != null) {
//            	for (Property p : conf) {
//            		System.out.println(p.getName() + ", " + p.getValue());
//            	}
//            } else {
//            	System.out.println("null");
//            }
//            
//            byte[] configContent = 
//            		client.exportApplicationConfiguration(domainName, asName, appName, appVersion, nodeName);
//            System.out.println("app conf content = " + configContent + ", size = " + 
//            		(configContent != null ? configContent.length : "null"));
//            System.out.println(new String(configContent));
//    	}
//
//		System.out.println("\nStarting appspace: " + getEntityStates());
//        as = client.getAppSpace(domainName, asName, true, true);
//		if (as.getStatus().equals(AppSpaceRuntimeStatus.Stopped)) {
//			client.startAppSpace(domainName, asName);
//	        as = client.getAppSpace(domainName, asName, true, true);
//	        System.out.println("done. " + getEntityStates());
//		} else {
//	        System.out.println("cannot start appspace, status: " + as.getStatus());
//		}
///*		
//        System.out.println("\nStopping appspace: " + getEntityStates());
//        as = client.getAppSpace(domainName, asName, true, true);
//		if (!as.getStatus().equals(AppSpaceRuntimeStatus.Stopped)) {
//			client.stopAppSpace(domainName, asName);
//	        as = client.getAppSpace(domainName, asName, true, true);
//	        System.out.println("done. " + getEntityStates());
//		} else {
//	        System.out.println("cannot stop appspace, status: " + as.getStatus());
//		}
//		
//        System.out.println("\nStarting appnode: " + getEntityStates());
//		node = client.getAppNode(domainName, asName, nodeName, true);
//		if (node.getState().equals(AppNodeRuntimeStates.Stopped)) {
//	        client.startAppNode(domainName, asName, nodeName);
//	        node = client.getAppNode(domainName, asName, nodeName, true);
//	        System.out.println("done. " + getEntityStates());
//		} else {
//	        System.out.println("cannot start appnode, state: " + node.getState());
//		}
//		
//        System.out.println("\nStopping appnode: " + getEntityStates());
//		node = client.getAppNode(domainName, asName, nodeName, true);
//		if (!node.getState().equals(AppNodeRuntimeStates.Stopped)) {
//	        client.stopAppNode(domainName, asName, nodeName);
//	        node = client.getAppNode(domainName, asName, nodeName, true);
//	        System.out.println("done. " + getEntityStates());
//		} else {
//	        System.out.println("cannot stop appnode, state: " + node.getState());
//		}
//*/
//        System.out.println("\nStarting application: " + getEntityStates());
//        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//		if (app.getState().equals(ApplicationRuntimeStates.Stopped)) {
//	        client.startApplication(domainName, asName, app.getName(), app.getVersion(), null);
//	        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//	        System.out.println("done. " + getEntityStates());
//		} else {
//	        System.out.println("cannot start app, state: " + app.getState());
//		}
//
//        System.out.println("\nPausing application");
//        client.pauseApplication(domainName, asName, app.getName(), app.getVersion(), nodeName);
//        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//        System.out.println("done.  " + getEntityStates());
//        System.out.println("Resuming application");
//        client.resumeApplication(domainName, asName, app.getName(), app.getVersion(), nodeName);
//        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//        System.out.println("done.  " + getEntityStates());
//        
//        System.out.println("\nStopping process starter");
//        client.stopProcessStarter(domainName, asName, app.getName(), app.getVersion(), nodeName);
//        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//        System.out.println("done.  " + getEntityStates());
//        System.out.println("\nStarting process starter");
//        client.startProcessStarter(domainName, asName, app.getName(), app.getVersion(), nodeName);
//        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//        System.out.println("done.  " + getEntityStates());
//
//
//        System.out.println("\nStopping application");
//        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//		if (!app.getState().equals(ApplicationRuntimeStates.Stopped)) {
//	        client.stopApplication(domainName, asName, app.getName(), app.getVersion(), null);
//	        app = client.getApplication(domainName, asName, app.getName(), app.getVersion());
//	        System.out.println("done.  " + getEntityStates());
//		} else {
//	        System.out.println("cannot stop app, state: " + app.getState());
//		}
//                
////        // cleanup
////        //client.stopApplication(domainName, asName, app1.getName(), app1.getVersion(), null);
////    	//System.out.println("stopped application: " + app1.getName() + ", " + app1.getState());
////        client.stopAppNode(domainName, asName, nodeName);
////    	System.out.println("stopped appNode: " + nodeName + ", " + node.getState());
////        //client.undeployApplication(domainName, asName, app1.getName(), app1.getVersion());
////    	//System.out.println("undeployed application: " + app1.getName());
////        client.deleteAppNode(domainName, asName, nodeName, true);
////    	System.out.println("deleted AppNode: " + nodeName);
////        listAppNodes(domainName, asName);
////        client.deleteAppSpace(domainName, asName, true);
////    	System.out.println("deleted AppSpace: " + asName);
////        listAppSpaces(domainName);
////        client.deleteArchive(domainName, archiveFileName);
////    	System.out.println("deleted Archive: " + archiveFileName);
////        listArchives(domainName);
////        client.deleteDomain(domainName, true);
////    	System.out.println("deleted Domain: " + domainName);
////        listDomains();
//    }
//    
//    static void listDomains() throws ClientException {
//        List <Domain> list = client.getDomains(null, false, true);
//    	System.out.println("\nDomains:");
//        if (list != null) {
//        	for (Domain listElement : list) {
//        		System.out.println("  " + listElement.getName());
//        	}
//        } else {
//        	System.out.println("getDomains returned null");
//        }
//
//    }
//    static void listAppSpaces(String domainName) throws ClientException {
//        List <AppSpace> list = client.getAppSpaces(domainName, null, false, true);
//    	System.out.println("\nAppSpaces:");
//        if (list != null) {
//        	for (AppSpace listElement : list) {
//        		System.out.println("  " + listElement.getName() + ", " + listElement.getStatus());
//        	}
//        } else {
//        	System.out.println("getAppSpaces returned null");
//        }
//    }
//    static void listAppNodes(String domainName, String appSpaceName) throws ClientException {
//        List <AppNode> list = client.getAppNodes(domainName, appSpaceName, null, true);
//    	System.out.println("\nAppNodes:");
//        if (list != null) {
//        	for (AppNode listElement : list) {
//        		System.out.println("  " + listElement.getName() + ", " + listElement.getState());
//        	}
//        } else {
//        	System.out.println("getAppNodes returned null");
//        }
//    }
//    static void listArchives(String domainName) throws ClientException {
//        List <Archive> list = client.getArchives(domainName, null, null);
//    	System.out.println("\nArchives:");
//        if (list != null) {
//        	for (Archive listElement : list) {
//        		System.out.println("  " + listElement.getName() + ", " + listElement.getUploadedTime());
//        	}
//        } else {
//        	System.out.println("getArchives returned null");
//        }
//    }
//    static void listApplications(String domainName, String asName) throws ClientException {
//        List <Application> list = client.getApplications(domainName, asName, null, true);
//    	System.out.println("\nApplications:");
//        if (list != null) {
//        	for (Application listElement : list) {
//        		System.out.println("  " + listElement.getName() + ", " + listElement.getState());
//        	}
//        } else {
//        	System.out.println("getApplications returned null");
//        }
//    }
//    static String getEntityStates() throws ClientException {
//        AppSpace as = client.getAppSpace(domainName , asName, true, true);
//		AppNode node = client.getAppNode(domainName, asName, nodeName, true);
//        Application app = client.getApplication(domainName, asName, appName, appVersion);
//
//        return "as=" + (as != null ? as.getStatus() : "null") 
//        		+ ", node=" + (node != null ? node.getState() : "null")
//        		+", app=" + (app != null ? app.getState() : "null");
//    }
//
//}