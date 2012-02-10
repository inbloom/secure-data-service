package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TWebServiceBeginJava
{
  protected static String nl;
  public static synchronized TWebServiceBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWebServiceBeginJava result = new TWebServiceBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "class Util_";
  protected final String TEXT_2 = "{" + NL + "\tpublic  final String LIST_SIZE_SYMBOL = \".size\";" + NL + "" + NL + "    public  final String LEFT_SQUARE_BRACKET = \"[\";" + NL + "" + NL + "    public  final String RIGHT_SQUARE_BRACKET = \"]\";" + NL + "    " + NL + "    public final String ALL_LIST_SYMBOL = \"[*]\";" + NL + "    " + NL + "    public Object getValue(java.util.Map<String, Object> map, String path) {" + NL + "    \tif (path == null || \"\".equals(path)) {" + NL + "            return null;" + NL + "        }" + NL + "        if (map == null||map.isEmpty()) {" + NL + "            return null;" + NL + "        }" + NL + "        java.util.List<String> paths = new java.util.ArrayList<String>();" + NL + "        resolvePath(map, path, paths);" + NL + "        if (paths.size() > 0) {" + NL + "            if (path.indexOf(ALL_LIST_SYMBOL) == -1) {" + NL + "                return map.get(paths.get(0));" + NL + "            } else {" + NL + "                int size = paths.size();" + NL + "                java.util.List<Object> out = new java.util.ArrayList<Object>(size);" + NL + "                for (int i = 0; i < size; i++) {" + NL + "                    out.add(map.get(paths.get(i)));" + NL + "                }" + NL + "                return out;" + NL + "            }" + NL + "        } else {" + NL + "            return null;" + NL + "        }" + NL + "    }" + NL + "    " + NL + "    public void resolveInputPath(java.util.Map<String, Object> inputMap) {" + NL + "        java.util.Map<String, Object> tempStoreMap = new java.util.HashMap<String, Object>();" + NL + "        java.util.List<String> tempRemovePath = new java.util.ArrayList<String>();" + NL + "        for (String key : inputMap.keySet()) {" + NL + "            if (key.indexOf(ALL_LIST_SYMBOL) != -1) {" + NL + "                String listHeadPath = key.substring(0, key.indexOf(ALL_LIST_SYMBOL));" + NL + "                String listFootPath = key.substring(key.indexOf(ALL_LIST_SYMBOL) + ALL_LIST_SYMBOL.length());" + NL + "                java.util.List listElement = (java.util.List) inputMap.get(key);" + NL + "                //if the list is null, ignore it but remove the original key" + NL + "                if(listElement!=null){" + NL + "                \tfor (int i = 0; i < listElement.size(); i++) {" + NL + "                    \ttempStoreMap.put(listHeadPath + LEFT_SQUARE_BRACKET + i + RIGHT_SQUARE_BRACKET + listFootPath, listElement" + NL + "                        \t    .get(i));" + NL + "                \t}" + NL + "                }" + NL + "                tempRemovePath.add(key);" + NL + "            }" + NL + "        }" + NL + "        inputMap.putAll(tempStoreMap);" + NL + "        for (String removePath : tempRemovePath) {" + NL + "            inputMap.remove(removePath);" + NL + "        }" + NL + "    }" + NL + "    " + NL + "    " + NL + "    public void resolvePath(java.util.Map<String, Object> map, String path, java.util.List<String> paths) {" + NL + "        String listHeadPath = \"\";" + NL + "        String listFootPath = \"\";" + NL + "        int size = 0;" + NL + "        String tempPath = \"\";" + NL + "        if (path.indexOf(ALL_LIST_SYMBOL) != -1) {" + NL + "            listHeadPath = path.substring(0, path.indexOf(ALL_LIST_SYMBOL));" + NL + "            listFootPath = path.substring(path.indexOf(ALL_LIST_SYMBOL) + ALL_LIST_SYMBOL.length());" + NL + "            if (map.get(listHeadPath) == null && map.get(listHeadPath + LIST_SIZE_SYMBOL) != null) {" + NL + "                size = Integer.parseInt(map.get(listHeadPath + LIST_SIZE_SYMBOL).toString());" + NL + "                for (int i = 0; i < size; i++) {" + NL + "                    tempPath = listHeadPath + LEFT_SQUARE_BRACKET + i + RIGHT_SQUARE_BRACKET + listFootPath;" + NL + "                    if (tempPath.indexOf(ALL_LIST_SYMBOL) != -1) {" + NL + "                        resolvePath(map, tempPath, paths);" + NL + "                    } else {" + NL + "                        paths.add(tempPath);" + NL + "                    }" + NL + "                }" + NL + "            }" + NL + "        } else {" + NL + "            paths.add(path);" + NL + "        }" + NL + "    }" + NL + "    " + NL + "    public  java.util.List<Object> normalize(String inputValue, String delimiter) {" + NL + "        if (inputValue == null || \"\".equals(inputValue)) {" + NL + "            return null;" + NL + "        }" + NL + "        Object[] inputValues = inputValue.split(delimiter);" + NL + "        return java.util.Arrays.asList(inputValues);" + NL + "" + NL + "    }" + NL + "" + NL + "    public  String denormalize(java.util.List inputValues, String delimiter) {" + NL + "        if (inputValues == null||inputValues.isEmpty()) {" + NL + "            return null;" + NL + "        }" + NL + "        StringBuffer sb = new StringBuffer();" + NL + "        for (Object o : inputValues) {" + NL + "            sb.append(String.valueOf(o));" + NL + "            sb.append(delimiter);" + NL + "        }" + NL + "        if (sb.length() > 0) {" + NL + "            sb.delete(sb.length() - delimiter.length(),sb.length());" + NL + "        }" + NL + "        return sb.toString();" + NL + "    }" + NL + "}   " + NL + "    System.setProperty(\"org.apache.commons.logging.Log\", \"org.apache.commons.logging.impl.NoOpLog\");" + NL + "\t//shade the log level for DynamicClientFactory.class" + NL + "\tjava.util.logging.Logger LOG_";
  protected final String TEXT_3 = " = org.apache.cxf.common.logging.LogUtils.getL7dLogger(org.apache.cxf.endpoint.dynamic.DynamicClientFactory.class);\t\t\t\t\t" + NL + "\tLOG_";
  protected final String TEXT_4 = ".setLevel(java.util.logging.Level.WARNING);" + NL + "\t" + NL + "\tUtil_";
  protected final String TEXT_5 = " util_";
  protected final String TEXT_6 = " = new Util_";
  protected final String TEXT_7 = "();" + NL + "    " + NL + "    org.talend.webservice.helper.conf.ServiceHelperConfiguration config_";
  protected final String TEXT_8 = " = new org.talend.webservice.helper.conf.ServiceHelperConfiguration();" + NL + "\t" + NL + "\tconfig_";
  protected final String TEXT_9 = ".setConnectionTimeout(Long.valueOf(";
  protected final String TEXT_10 = "));" + NL + "\tconfig_";
  protected final String TEXT_11 = ".setReceiveTimeout(Long.valueOf(";
  protected final String TEXT_12 = "));" + NL + "\t" + NL + "\tconfig_";
  protected final String TEXT_13 = ".setKeyStoreFile(System.getProperty(\"javax.net.ssl.keyStore\"));" + NL + "\tconfig_";
  protected final String TEXT_14 = ".setKeyStoreType(System.getProperty(\"javax.net.ssl.keyStoreType\"));" + NL + "\tconfig_";
  protected final String TEXT_15 = ".setKeyStorePwd(System.getProperty(\"javax.net.ssl.keyStorePassword\"));";
  protected final String TEXT_16 = "   " + NL + "    System.setProperty(\"java.protocol.handler.pkgs\", \"com.sun.net.ssl.internal.www.protocol\");" + NL + "" + NL + "\tSystem.setProperty(\"javax.net.ssl.trustStore\", ";
  protected final String TEXT_17 = ");" + NL + "\tSystem.setProperty(\"javax.net.ssl.trustStorePassword\", ";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = "\t" + NL + "\tconfig_";
  protected final String TEXT_20 = ".setUsername(";
  protected final String TEXT_21 = ");" + NL + "\tconfig_";
  protected final String TEXT_22 = ".setPassword(";
  protected final String TEXT_23 = ");";
  protected final String TEXT_24 = NL + "\tconfig_";
  protected final String TEXT_25 = ".setProxyServer(";
  protected final String TEXT_26 = ");" + NL + "\tconfig_";
  protected final String TEXT_27 = ".setProxyPort(";
  protected final String TEXT_28 = ");" + NL + "    config_";
  protected final String TEXT_29 = ".setProxyUsername(";
  protected final String TEXT_30 = ");" + NL + "    config_";
  protected final String TEXT_31 = ".setProxyPassword( ";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = NL + "\t";
  protected final String TEXT_34 = NL + "\tSystem.setProperty(\"http.auth.ntlm.domain\", ";
  protected final String TEXT_35 = ");" + NL + "\t";
  protected final String TEXT_36 = NL + "\tjava.net.Authenticator.setDefault(new java.net.Authenticator() {" + NL + "        public java.net.PasswordAuthentication getPasswordAuthentication() {" + NL + "            return new java.net.PasswordAuthentication(";
  protected final String TEXT_37 = ", ";
  protected final String TEXT_38 = ".toCharArray());" + NL + "        }" + NL + "    });" + NL + "\t" + NL + "\tconfig_";
  protected final String TEXT_39 = ".setAllowChunking(false);";
  protected final String TEXT_40 = " " + NL + "\torg.talend.webservice.helper.ServiceDiscoveryHelper serviceDiscoveryHelper_";
  protected final String TEXT_41 = " = null ;" + NL + "\torg.talend.webservice.helper.ServiceInvokerHelper serviceInvokerHelper_";
  protected final String TEXT_42 = " = null ;" + NL;
  protected final String TEXT_43 = NL + "\tjava.net.URI uri_";
  protected final String TEXT_44 = " = new java.net.URI(";
  protected final String TEXT_45 = ");" + NL + "    if (\"http\".equals(uri_";
  protected final String TEXT_46 = ".getScheme()) || \"https\".equals(uri_";
  protected final String TEXT_47 = ".getScheme())) {" + NL + "  " + NL + "\t\tserviceInvokerHelper_";
  protected final String TEXT_48 = " = new org.talend.webservice.helper.ServiceInvokerHelper(";
  protected final String TEXT_49 = ",config_";
  protected final String TEXT_50 = ",";
  protected final String TEXT_51 = ");" + NL + "" + NL + "\t} else {";
  protected final String TEXT_52 = NL + "        serviceDiscoveryHelper_";
  protected final String TEXT_53 = " = new org.talend.webservice.helper.ServiceDiscoveryHelper(";
  protected final String TEXT_54 = ",";
  protected final String TEXT_55 = ");" + NL + "    \tserviceInvokerHelper_";
  protected final String TEXT_56 = " = new org.talend.webservice.helper.ServiceInvokerHelper(serviceDiscoveryHelper_";
  protected final String TEXT_57 = ",config_";
  protected final String TEXT_58 = ");";
  protected final String TEXT_59 = NL + "\t}";
  protected final String TEXT_60 = NL + "\t" + NL + "\tjavax.xml.namespace.QName serviceName_";
  protected final String TEXT_61 = " = new javax.xml.namespace.QName(\"";
  protected final String TEXT_62 = "\", \"";
  protected final String TEXT_63 = "\");" + NL + "\tjavax.xml.namespace.QName portName_";
  protected final String TEXT_64 = " = new javax.xml.namespace.QName(\"";
  protected final String TEXT_65 = "\", \"";
  protected final String TEXT_66 = "\");" + NL + "\t" + NL + "\tjava.util.Map<String,Object> inMap_";
  protected final String TEXT_67 = " = null;";
  protected final String TEXT_68 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String endpoint = ElementParameterParser.getValue(node,"__ENDPOINT__");

String serviceNS = ElementParameterParser.getValue(node,"__SERVICE_NS__");
String serviceName = ElementParameterParser.getValue(node,"__SERVICE_NAME__");
String portNS = ElementParameterParser.getValue(node,"__PORT_NS__");
String portName = ElementParameterParser.getValue(node,"__PORT_NAME__");

String soapAction = ElementParameterParser.getValue(node,"__SOAPACTION__");
String methodNS = ElementParameterParser.getValue(node,"__METHOD_NS__");

boolean useNTLM = ("true").equals(ElementParameterParser.getValue(node,"__USE_NTLM__"));
String domain = ElementParameterParser.getValue(node,"__NTLM_DOMAIN__");
String host = ElementParameterParser.getValue(node,"__NTLM_HOST__");

boolean needAuth = ("true").equals(ElementParameterParser.getValue(node,"__NEED_AUTH__"));
String username = ElementParameterParser.getValue(node,"__AUTH_USERNAME__");
String password = ElementParameterParser.getValue(node,"__AUTH_PASSWORD__");
        
boolean useProxy = ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
String proxyHost = ElementParameterParser.getValue(node,"__PROXY_HOST__");
String proxyPort = ElementParameterParser.getValue(node,"__PROXY_PORT__");
String proxyUser = ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
String proxyPassword = ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");

boolean needSSLtoTrustServer = ("true").equals(ElementParameterParser.getValue(node,"__NEED_SSL_TO_TRUSTSERVER__"));
String trustStoreFile = ElementParameterParser.getValue(node,"__SSL_TRUSTSERVER_TRUSTSTORE__");
String trustStorePassword = ElementParameterParser.getValue(node,"__SSL_TRUSTSERVER_PASSWORD__");
        
String connTimeoutStr = ElementParameterParser.getValue(node,"__CONNECTION_TIMEOUT__");
String connTimeoutSec = (connTimeoutStr!=null&&!("").equals(connTimeoutStr))?connTimeoutStr:"20";
long connTimeout = (long)(Double.valueOf(connTimeoutSec) * 1000);

String receiveTimeoutStr = ElementParameterParser.getValue(node,"__RECEIVE_TIMEOUT__");
String receiveTimeoutSec = (receiveTimeoutStr!=null&&!("").equals(receiveTimeoutStr))?receiveTimeoutStr:"20";
long receiveTimeout = (long)(Double.valueOf(receiveTimeoutSec) * 1000);

String tempPath = ElementParameterParser.getValue(node,"__TMPPATH__");
if("".equals(tempPath))tempPath="\"\"";


    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(connTimeout);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(receiveTimeout);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    		
if (needSSLtoTrustServer) {

    stringBuffer.append(TEXT_16);
    stringBuffer.append(trustStoreFile );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(trustStorePassword );
    stringBuffer.append(TEXT_18);
     
}if(needAuth&&!useNTLM){	

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_23);
    
}if(useProxy){

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(proxyPort );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(proxyUser );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_32);
    
}if(useNTLM){

    stringBuffer.append(TEXT_33);
    if(!"\"\"".equals(domain)){
    stringBuffer.append(TEXT_34);
    stringBuffer.append(domain);
    stringBuffer.append(TEXT_35);
    }
    stringBuffer.append(TEXT_36);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
     
} 	

    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    if(!useNTLM){
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(tempPath);
    stringBuffer.append(TEXT_51);
    }
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(tempPath);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    if(!useNTLM){
    stringBuffer.append(TEXT_59);
    }
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(serviceNS);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(serviceName);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(portNS);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(portName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(TEXT_68);
    return stringBuffer.toString();
  }
}
