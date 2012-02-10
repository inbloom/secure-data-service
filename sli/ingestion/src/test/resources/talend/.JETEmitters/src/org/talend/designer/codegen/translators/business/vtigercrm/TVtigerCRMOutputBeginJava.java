package org.talend.designer.codegen.translators.business.vtigercrm;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TVtigerCRMOutputBeginJava
{
  protected static String nl;
  public static synchronized TVtigerCRMOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TVtigerCRMOutputBeginJava result = new TVtigerCRMOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    org.talend.vtiger.IVtigerManager vtigerManager_";
  protected final String TEXT_2 = " = new org.talend.vtiger.VtigerManager(";
  protected final String TEXT_3 = ", ";
  protected final String TEXT_4 = ", ";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ", ";
  protected final String TEXT_7 = ", ";
  protected final String TEXT_8 = ");";
  protected final String TEXT_9 = NL + "        java.util.List<org.talend.vtiger.module.outlook.Clndrdetail> clndrDetais_";
  protected final String TEXT_10 = " = new java.util.ArrayList<org.talend.vtiger.module.outlook.Clndrdetail>(); ";
  protected final String TEXT_11 = NL + "        java.util.List<org.talend.vtiger.module.outlook.Contactdetail> contactdetails_";
  protected final String TEXT_12 = " = new java.util.ArrayList<org.talend.vtiger.module.outlook.Contactdetail>();";
  protected final String TEXT_13 = NL + "        java.util.List<org.talend.vtiger.module.outlook.Taskdetail> taskdetails_";
  protected final String TEXT_14 = " = new java.util.ArrayList<org.talend.vtiger.module.outlook.Taskdetail>();";
  protected final String TEXT_15 = NL + "    com.vtiger.vtwsclib.WSClient vtMgr_";
  protected final String TEXT_16 = " = new com.vtiger.vtwsclib.WSClient(";
  protected final String TEXT_17 = ");" + NL + "\tboolean lr_";
  protected final String TEXT_18 = " = vtMgr_";
  protected final String TEXT_19 = ".doLogin(";
  protected final String TEXT_20 = ", ";
  protected final String TEXT_21 = ");" + NL + "\tif(!lr_";
  protected final String TEXT_22 = "){" + NL + "\t \tthrow new RuntimeException(vtMgr_";
  protected final String TEXT_23 = ".lastError().toString());" + NL + "\t}";
  protected final String TEXT_24 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String version_selection = ElementParameterParser.getValue(node, "__VERSION_SELECTION__");
if(version_selection.equals("VTIGER_50")){

    String serverAddr = ElementParameterParser.getValue(node, "__SERVERADDR__");
    String port = ElementParameterParser.getValue(node, "__PORT__");
    String vtigerPath = ElementParameterParser.getValue(node, "__VTIGERPATH__");
    String userName = ElementParameterParser.getValue(node, "__USERNAME__");
    String password = ElementParameterParser.getValue(node, "__PASSWORD__");
    String version = ElementParameterParser.getValue(node, "__VERSION__");
    String method = ElementParameterParser.getValue(node, "__METHODNAME__");
    
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(userName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(version);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(serverAddr);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(vtigerPath);
    stringBuffer.append(TEXT_8);
    
    if(("addClndr").equals(method) || ("updateClndr").equals(method)) {
        
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
    } else if(("addContacts").equals(method) || ("updateContacts").equals(method)) {
        
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
    } else if(("addTasks").equals(method) || ("updateTasks").equals(method)) {
        
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
    }
//*****************************************************version 5.1 start**************************************
}else{
	String endpoint = ElementParameterParser.getValue(node, "__ENDPOINT__");
	String username = ElementParameterParser.getValue(node, "__USERNAME_510__");
    String accessKey = ElementParameterParser.getValue(node, "__ACCESS_KEY__");

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(accessKey);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
}

    stringBuffer.append(TEXT_24);
    return stringBuffer.toString();
  }
}
