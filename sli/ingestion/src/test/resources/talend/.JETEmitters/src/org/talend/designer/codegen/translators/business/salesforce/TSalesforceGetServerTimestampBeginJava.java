package org.talend.designer.codegen.translators.business.salesforce;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import java.util.List;

public class TSalesforceGetServerTimestampBeginJava
{
  protected static String nl;
  public static synchronized TSalesforceGetServerTimestampBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceGetServerTimestampBeginJava result = new TSalesforceGetServerTimestampBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL;
  protected final String TEXT_4 = NL + "    java.util.Properties props_";
  protected final String TEXT_5 = " = System.getProperties();" + NL + "    props_";
  protected final String TEXT_6 = ".put(\"socksProxyHost\",";
  protected final String TEXT_7 = ");  " + NL + "    props_";
  protected final String TEXT_8 = ".put(\"socksProxyPort\",";
  protected final String TEXT_9 = ");" + NL + "    props_";
  protected final String TEXT_10 = ".put(\"java.net.socks.username\", ";
  protected final String TEXT_11 = ");" + NL + "    props_";
  protected final String TEXT_12 = ".put(\"java.net.socks.password\", ";
  protected final String TEXT_13 = "); " + NL + "    ";
  protected final String TEXT_14 = NL;
  protected final String TEXT_15 = NL + "\t\t\torg.talend.salesforce.SforceManagement sfMgr_";
  protected final String TEXT_16 = " = new org.talend.salesforce.SforceManagementImpl();" + NL + "\t\t\t";
  protected final String TEXT_17 = NL + "\t\t\t\tif(globalMap.get(\"stub_";
  protected final String TEXT_18 = "\")==null || globalMap.get(\"sessionHeader_";
  protected final String TEXT_19 = "\")==null){" + NL + "\t\t\t\t\tthrow new RuntimeException(\"Get null connection from ";
  protected final String TEXT_20 = "\");" + NL + "\t\t\t\t}" + NL + "\t\t\t\tif(globalMap.get(\"callOptions_";
  protected final String TEXT_21 = "\")!=null ){" + NL + "\t\t\t\t\tsfMgr_";
  protected final String TEXT_22 = ".setCallOptions((com.salesforce.soap.partner.CallOptions)globalMap.get(\"callOptions_";
  protected final String TEXT_23 = "\"));" + NL + "\t\t\t\t}" + NL + "\t\t\t\tsfMgr_";
  protected final String TEXT_24 = ".login((com.salesforce.soap.partner.SforceServiceStub)globalMap.get(\"stub_";
  protected final String TEXT_25 = "\"),(com.salesforce.soap.partner.SessionHeader)globalMap.get(\"sessionHeader_";
  protected final String TEXT_26 = "\"));" + NL + "\t\t\t";
  protected final String TEXT_27 = NL + "\t\t\t\t";
  protected final String TEXT_28 = NL + "\t\t\t\t\tsfMgr_";
  protected final String TEXT_29 = ".setClientID(";
  protected final String TEXT_30 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_31 = NL + "\t\t\t\tboolean result_";
  protected final String TEXT_32 = " = sfMgr_";
  protected final String TEXT_33 = ".login(";
  protected final String TEXT_34 = ",";
  protected final String TEXT_35 = ", ";
  protected final String TEXT_36 = ",";
  protected final String TEXT_37 = ",";
  protected final String TEXT_38 = ");" + NL + "\t\t\t\tif(!result_";
  protected final String TEXT_39 = "){" + NL + "\t\t\t\t\tthrow new RuntimeException(\"Login failed! Please check the username,password and endpoint\");" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_40 = NL + "\t\t";
  protected final String TEXT_41 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

	boolean useProxy = ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
	String proxyHost = ElementParameterParser.getValue(node,"__PROXY_HOST__");
   	String proxyPort = ElementParameterParser.getValue(node,"__PROXY_PORT__");
   	String proxyUsername = ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
   	String proxyPassword = ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");
   	boolean needCompression = ("true").equals(ElementParameterParser.getValue(node,"__NEED_COMPRESSION__"));

String cid = node.getUniqueName();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
if(useProxy){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(proxyPort );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(proxyUsername );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_13);
    
}

    stringBuffer.append(TEXT_14);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);
	
	if (metadata != null) {
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		if (outgoingConns != null && outgoingConns.size() > 0){
			String endpoint = ElementParameterParser.getValue(node, "__ENDPOINT__");
			String username = ElementParameterParser.getValue(node, "__USER__");
			String password = ElementParameterParser.getValue(node, "__PASS__");
			String timeout = ElementParameterParser.getValue(node,"__TIMEOUT__");
			boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
			String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
			String clientID = ElementParameterParser.getValue(node,"__CLIENT_ID__");		
			
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    if(useExistingConn){
    stringBuffer.append(TEXT_17);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(connection);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(connection);
    stringBuffer.append(TEXT_26);
    }else{
    stringBuffer.append(TEXT_27);
    if(clientID!=null && !"".equals(clientID) && !"\"\"".equals(clientID)){
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(clientID);
    stringBuffer.append(TEXT_30);
    }
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(timeout);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(needCompression);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    }
    stringBuffer.append(TEXT_40);
    
		}
	}
}

    stringBuffer.append(TEXT_41);
    return stringBuffer.toString();
  }
}
