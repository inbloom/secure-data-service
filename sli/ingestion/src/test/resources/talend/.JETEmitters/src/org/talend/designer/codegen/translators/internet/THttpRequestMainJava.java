package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.List;
import java.util.Map;

public class THttpRequestMainJava
{
  protected static String nl;
  public static synchronized THttpRequestMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    THttpRequestMainJava result = new THttpRequestMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "java.net.URL url_";
  protected final String TEXT_2 = " = new java.net.URL(";
  protected final String TEXT_3 = ");" + NL;
  protected final String TEXT_4 = NL + "  java.net.Authenticator.setDefault(new java.net.Authenticator() {" + NL + "\t  protected java.net.PasswordAuthentication getPasswordAuthentication() {" + NL + "\t  \treturn new java.net.PasswordAuthentication(";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ".toCharArray());" + NL + "  }});";
  protected final String TEXT_7 = NL + "java.net.HttpURLConnection urlConn_";
  protected final String TEXT_8 = " = (java.net.HttpURLConnection) url_";
  protected final String TEXT_9 = ".openConnection();" + NL + "urlConn_";
  protected final String TEXT_10 = ".setRequestMethod(\"";
  protected final String TEXT_11 = "\");" + NL + "urlConn_";
  protected final String TEXT_12 = ".setDoOutput(true);" + NL + "urlConn_";
  protected final String TEXT_13 = ".setDoInput(true);" + NL + "urlConn_";
  protected final String TEXT_14 = ".setUseCaches(false);" + NL;
  protected final String TEXT_15 = NL + "  urlConn_";
  protected final String TEXT_16 = ".setRequestProperty(";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + NL + "urlConn_";
  protected final String TEXT_20 = ".connect();" + NL + "" + NL + "byte[] buffer_";
  protected final String TEXT_21 = " = new byte[1024];" + NL + "int bos_buffer_";
  protected final String TEXT_22 = " = 0;";
  protected final String TEXT_23 = "StringBuilder sb_";
  protected final String TEXT_24 = "=new StringBuilder(); ";
  protected final String TEXT_25 = NL + "    java.io.InputStream bisParam_";
  protected final String TEXT_26 = " = new java.io.BufferedInputStream(new java.io.FileInputStream(";
  protected final String TEXT_27 = "));" + NL + "    java.io.OutputStream bosParam_";
  protected final String TEXT_28 = " = new java.io.BufferedOutputStream(urlConn_";
  protected final String TEXT_29 = ".getOutputStream());" + NL + "    " + NL + "    while ((bos_buffer_";
  protected final String TEXT_30 = " = bisParam_";
  protected final String TEXT_31 = ".read(buffer_";
  protected final String TEXT_32 = ")) != -1) {" + NL + "      bosParam_";
  protected final String TEXT_33 = ".write(buffer_";
  protected final String TEXT_34 = ",0,bos_buffer_";
  protected final String TEXT_35 = ");" + NL + "    }" + NL + "    bosParam_";
  protected final String TEXT_36 = ".flush();" + NL + "    bosParam_";
  protected final String TEXT_37 = ".close();" + NL + "    bisParam_";
  protected final String TEXT_38 = ".close();";
  protected final String TEXT_39 = NL + NL + "if (java.net.HttpURLConnection.HTTP_OK == (urlConn_";
  protected final String TEXT_40 = ".getResponseCode())) {" + NL + "\tjava.io.InputStream bis_";
  protected final String TEXT_41 = " = new java.io.BufferedInputStream(urlConn_";
  protected final String TEXT_42 = ".getInputStream());";
  protected final String TEXT_43 = NL + "\t\tjava.io.OutputStream bosContent_";
  protected final String TEXT_44 = " = new java.io.BufferedOutputStream(new java.io.FileOutputStream(";
  protected final String TEXT_45 = "));" + NL + "\t\twhile ((bos_buffer_";
  protected final String TEXT_46 = " = bis_";
  protected final String TEXT_47 = ".read(buffer_";
  protected final String TEXT_48 = ")) != -1) {" + NL + "\t\t\tbosContent_";
  protected final String TEXT_49 = ".write(buffer_";
  protected final String TEXT_50 = ",0,bos_buffer_";
  protected final String TEXT_51 = ");";
  protected final String TEXT_52 = "sb_";
  protected final String TEXT_53 = ".append(new String(buffer_";
  protected final String TEXT_54 = ",0,bos_buffer_";
  protected final String TEXT_55 = ")); ";
  protected final String TEXT_56 = NL + "\t\t}" + NL + "\t\tbosContent_";
  protected final String TEXT_57 = ".flush();" + NL + "\t\tbosContent_";
  protected final String TEXT_58 = ".close();";
  protected final String TEXT_59 = NL + "\tbis_";
  protected final String TEXT_60 = ".close();" + NL + "} else {" + NL + "  System.err.println(urlConn_";
  protected final String TEXT_61 = ".getResponseCode() + \" \" + urlConn_";
  protected final String TEXT_62 = ".getResponseMessage());" + NL + "}" + NL;
  protected final String TEXT_63 = NL + "        ";
  protected final String TEXT_64 = ".ResponseContent = sb_";
  protected final String TEXT_65 = ".toString();";
  protected final String TEXT_66 = NL + "        ";
  protected final String TEXT_67 = ".";
  protected final String TEXT_68 = " = ";
  protected final String TEXT_69 = ".";
  protected final String TEXT_70 = ";";
  protected final String TEXT_71 = NL + "urlConn_";
  protected final String TEXT_72 = ".disconnect();";
  protected final String TEXT_73 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<? extends IConnection> inConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
List<? extends IConnection> outConns = node.getOutgoingConnections(EConnectionType.FLOW_MAIN);
String inConnName = null, outConnName = null;

boolean hasOutputSchema = false;

if (outConns != null && outConns.size() > 0){
  	outConnName = outConns.get(0).getName();
  	hasOutputSchema = true;
  
  	if (inConns != null && inConns.size() > 0){
    	inConnName = inConns.get(0).getName();
  	}
}

String sURI = ElementParameterParser.getValue(node, "__URI__");
String sMethod = ElementParameterParser.getValue(node, "__METHOD_CHOSED__");
String sParamFile = ElementParameterParser.getValue(node, "__POST_PARAMS_FROM_FILE__");
boolean bOutToFile = "true".equals(ElementParameterParser.getValue(node, "__OUT_TO_FILE__"));
String sOutFilePath = ElementParameterParser.getValue(node, "__OUT_FILE_NAME__");
List<Map<String, String>> headerParams = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__HEADERS__");
boolean bNeedAuthentication = "true".equals(ElementParameterParser.getValue(node, "__NEED_AUTHENTICATION__"));
String sUser = ElementParameterParser.getValue(node, "__USR__");
String sPwd = ElementParameterParser.getValue(node, "__PWD__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(sURI);
    stringBuffer.append(TEXT_3);
    if (bNeedAuthentication){
    stringBuffer.append(TEXT_4);
    stringBuffer.append(sUser);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(sPwd);
    stringBuffer.append(TEXT_6);
    }
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(sMethod);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    for (Map<String, String> header : headerParams){ // set request properties
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(header.get("HEADER_NAME"));
    stringBuffer.append(TEXT_17);
    stringBuffer.append(header.get("HEADER_VALUE"));
    stringBuffer.append(TEXT_18);
    }
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    if(hasOutputSchema){
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    }
    
// add parameters from file
if ("POST".equals(sMethod)){
  if (sParamFile != null && !"".equals(sParamFile)){
  
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(sParamFile);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    
  }
}

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    
  // output response content to file
	if (bOutToFile && sOutFilePath != null){

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(sOutFilePath);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    if(hasOutputSchema){
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    }
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    
	}

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    
// output data
if (outConnName != null){
  List<IMetadataTable> metaTables = node.getMetadataList();
  
  if (metaTables != null && metaTables.size() > 0){
    IMetadataTable metaTable = metaTables.get(0);
    List<IMetadataColumn> columns = metaTable.getListColumns();
    
    for (IMetadataColumn column : columns){
      String colName = column.getLabel();
      
      if ("ResponseContent".equals(colName)){
      
    stringBuffer.append(TEXT_63);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
      } else {
      
    stringBuffer.append(TEXT_66);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_70);
    
      }
    }
  }
}

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(TEXT_73);
    return stringBuffer.toString();
  }
}
