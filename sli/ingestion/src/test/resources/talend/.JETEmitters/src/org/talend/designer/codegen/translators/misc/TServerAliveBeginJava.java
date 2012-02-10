package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TServerAliveBeginJava
{
  protected static String nl;
  public static synchronized TServerAliveBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TServerAliveBeginJava result = new TServerAliveBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    try {" + NL + "        java.net.InetAddress address_";
  protected final String TEXT_3 = " = java.net.InetAddress.getByName(";
  protected final String TEXT_4 = ");" + NL + "        boolean isAlive_";
  protected final String TEXT_5 = " = address_";
  protected final String TEXT_6 = ".isReachable(";
  protected final String TEXT_7 = " * 1000);" + NL + "        if(isAlive_";
  protected final String TEXT_8 = ") {" + NL + "            globalMap.put(\"";
  protected final String TEXT_9 = "_SERVER_ALIVE_RESULT\",true);" + NL + "        } else {" + NL + "            globalMap.put(\"";
  protected final String TEXT_10 = "_SERVER_ALIVE_RESULT\",false);" + NL + "        }" + NL + "    } catch(Exception e) {" + NL + "        globalMap.put(\"";
  protected final String TEXT_11 = "_SERVER_ALIVE_RESULT\",false);" + NL + "    }";
  protected final String TEXT_12 = NL + "    try {";
  protected final String TEXT_13 = NL + "            java.util.Properties properties_";
  protected final String TEXT_14 = " = System.getProperties();" + NL + "            properties_";
  protected final String TEXT_15 = ".put(\"socksProxyHost\",";
  protected final String TEXT_16 = ");" + NL + "            properties_";
  protected final String TEXT_17 = ".put(\"socksProxyPort\",";
  protected final String TEXT_18 = ");" + NL + "            properties_";
  protected final String TEXT_19 = ".put(\"java.net.socks.username\", ";
  protected final String TEXT_20 = ");" + NL + "            properties_";
  protected final String TEXT_21 = ".put(\"java.net.socks.password\", ";
  protected final String TEXT_22 = ");";
  protected final String TEXT_23 = NL + "        java.net.Socket server_";
  protected final String TEXT_24 = " = new java.net.Socket();" + NL + "        java.net.InetSocketAddress address_";
  protected final String TEXT_25 = " = new java.net.InetSocketAddress(";
  protected final String TEXT_26 = ", ";
  protected final String TEXT_27 = ");" + NL + "        server_";
  protected final String TEXT_28 = ".connect(address_";
  protected final String TEXT_29 = ", ";
  protected final String TEXT_30 = " * 1000);" + NL + "        server_";
  protected final String TEXT_31 = ".close();" + NL + "        globalMap.put(\"";
  protected final String TEXT_32 = "_SERVER_ALIVE_RESULT\",true);" + NL + "    } catch(Exception e) {" + NL + "        globalMap.put(\"";
  protected final String TEXT_33 = "_SERVER_ALIVE_RESULT\",false);" + NL + "    }";
  protected final String TEXT_34 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String protocol = ElementParameterParser.getValue(node, "__PROTOCOL__");
String host = ElementParameterParser.getValue(node, "__HOST__");
String timeoutInterval = ElementParameterParser.getValue(node, "__TIMEOUT_INTERVAL__");
boolean isEnableProxy = ("true").equals(ElementParameterParser.getValue(node, "__ENABLE_PROXY_SERVER__"));
if(("PING").equals(protocol)) {
    
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(timeoutInterval);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    
} else {
    String port = ElementParameterParser.getValue(node, "__PORT__");
    
    stringBuffer.append(TEXT_12);
    
        if(isEnableProxy) {
            String proxyHost = ElementParameterParser.getValue(node, "__PROXY_HOST__");
            String proxyPort = ElementParameterParser.getValue(node, "__PROXY_PORT__");
            String userName = ElementParameterParser.getValue(node, "__PROXY_USER__");
            String password = ElementParameterParser.getValue(node, "__PROXY_PASS__");
            
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(proxyHost);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(proxyPort);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(userName);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_22);
    
        }
        
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(timeoutInterval);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    
}

    stringBuffer.append(TEXT_34);
    return stringBuffer.toString();
  }
}
