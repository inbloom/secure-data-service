package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TMomOutputEndJava
{
  protected static String nl;
  public static synchronized TMomOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMomOutputEndJava result = new TMomOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tglobalMap.put(\"producer_";
  protected final String TEXT_3 = "\",producer_";
  protected final String TEXT_4 = ");" + NL + "\t\tglobalMap.put(\"session_";
  protected final String TEXT_5 = "\",session_";
  protected final String TEXT_6 = ");" + NL + "\t\tglobalMap.put(\"connection_";
  protected final String TEXT_7 = "\",connection_";
  protected final String TEXT_8 = ");";
  protected final String TEXT_9 = NL + "\tSystem.out.println(\"Closing connection\");" + NL + "\tproducer_";
  protected final String TEXT_10 = ".close();" + NL + "    session_";
  protected final String TEXT_11 = ".close();" + NL + "    connection_";
  protected final String TEXT_12 = ".close();" + NL;
  protected final String TEXT_13 = NL + "\t\tglobalMap.put(\"remoteQ_";
  protected final String TEXT_14 = "\",remoteQ_";
  protected final String TEXT_15 = ");" + NL + "\t\tglobalMap.put(\"qMgr_";
  protected final String TEXT_16 = "\",qMgr_";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL + "\tSystem.out.println(\"Closing connection\");" + NL + "\tremoteQ_";
  protected final String TEXT_19 = ".close();" + NL + "    qMgr_";
  protected final String TEXT_20 = ".disconnect();" + NL;
  protected final String TEXT_21 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String serverType=ElementParameterParser.getValue(node, "__SERVER__");
boolean transacted = "true".equals(ElementParameterParser.getValue(node, "__IS_TRANSACTED__"));
if(("JBoss").equals(serverType) || ("ActiveMQ").equals(serverType)){
	String msgType = ElementParameterParser.getValue(node, "__MSGTYPE__");
	if (transacted) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    
	} else {

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
	}
}else{//server judgement   /***WebSphere MQ*****/
	if (transacted) {

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
	} else {

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    
	}
}

    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
