/*
 * Copyright (c) 2013-2014 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tibco.bw.maven.utils;

import java.util.HashMap;
import java.util.Map;

public class BWMavenConstants 
{

	public static Map<String,String> capabilities = new HashMap<String,String>();
	
	static
	{
		capabilities.put("com.tibco.bw.model; filter:=\"(name=bw.file)\"", "com.tibco.bw.palette.file.model");
		capabilities.put("com.tibco.bw.binding.model; filter:=\"(name=bw.rest)\"", "com.tibco.bw.binding.rest.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.xml)\"", "com.tibco.bw.palette.xml.design");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.ftp)\"", "com.tibco.bw.palette.ftp.model");
		capabilities.put("com.tibco.bw.model; filter:=\"(name=bw.general)\"", "com.tibco.bw.palette.generalactivities.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.http)\"", "com.tibco.bw.palette.http.model");
		capabilities.put("com.tibco.bw.model; filter:=\"(name=bw.java)\"", "com.tibco.bw.palette.java.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.jms)\"", "com.tibco.bw.palette.jms.model");
		capabilities.put("com.tibco.bw.model; filter:=\"(name=bw.mail)\"", "com.tibco.bw.palette.mail.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.parse)\"", "com.tibco.bw.palette.parse.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.rescue)\"", "com.tibco.bw.palette.rescue.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.tcp)\"", "com.tibco.bw.palette.tcp.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.xml)\"", "com.tibco.bw.palette.xml.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.file)\"", "com.tibco.bw.palette.file.runtime");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.generalactivities)\"", "com.tibco.bw.palette.generalactivities.runtime");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.java)\"", "com.tibco.bw.palette.java.runtime");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.jdbc)\"", "com.tibco.bw.palette.jdbc.runtime");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.mail)\"", "com.tibco.bw.palette.mail.runtime");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.rv)\"", "com.tibco.bw.palette.rv.runtime");
		capabilities.put("com.tibco.bw.model; filter:=\"(name=bwext)\"", "com.tibco.bw.core.model");				
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.dataformat)\"", "com.tibco.bw.sharedresource.dataformat.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.ftp)\"", "com.tibco.bw.sharedresource.ftpconnection.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.httpconnector)\"", "com.tibco.bw.sharedresource.http.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.httpclient)\"", "com.tibco.bw.sharedresource.http.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.httpproxy)\"", "com.tibco.bw.sharedresource.http.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.java)\"", "com.tibco.bw.sharedresource.java.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.jdbc)\"", "com.tibco.bw.sharedresource.jdbc.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.jmsconnection)\"", "com.tibco.bw.sharedresource.jms.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.jndiconfiguration)\"", "com.tibco.bw.sharedresource.jms.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.smtp)\"", "com.tibco.bw.sharedresource.mail.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.notify)\"", "com.tibco.bw.sharedresource.notifyconfiguration.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.rv)\"", "com.tibco.bw.sharedresource.rv.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.sharedvariable)\"", "com.tibco.bw.sharedresource.sharedvariable.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.tcp)\"", "com.tibco.bw.sharedresource.tcpconnection.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.threadpool)\"", "com.tibco.bw.sharedresource.threadpool.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.ldap)\"", "com.tibco.bw.sharedresource.trinity.authn.ldap.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.cred)\"", "com.tibco.bw.sharedresource.trinity.credential.keystore.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.userid)\"", "com.tibco.bw.sharedresource.trinity.identity.subject.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.sslclient)\"", "com.tibco.bw.sharedresource.trinity.ssl.client.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.sslserver)\"", "com.tibco.bw.sharedresource.trinity.ssl.server.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.userid)\"", "com.tibco.bw.sharedresource.trinity.useridentity.model");
		capabilities.put("com.tibco.bw.binding.model; filter:=\"(name=bw.soap)\"", "com.tibco.bw.binding.soap.axis2.model");
		
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.miserver)\"", "com.tibco.bw.sharedresource.mi.model");
		capabilities.put("com.tibco.bw.sharedresource.model; filter:=\"(name=bw.pnscommunicator)\"", "com.tibco.bw.sharedresource.mi.model");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.mi)\"", "com.tibco.bw.palette.mi.runtime");
		capabilities.put("com.tibco.bw.palette; filter:=\"(name=bw.restjson)\"", "com.tibco.bw.palette.restjson.runtime");

		
		
	}

}
