package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TMomCommitMainJava
{
  protected static String nl;
  public static synchronized TMomCommitMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMomCommitMainJava result = new TMomCommitMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tjavax.jms.Session session_";
  protected final String TEXT_3 = " = (javax.jms.Session)globalMap.get(\"session_";
  protected final String TEXT_4 = "\");" + NL + "\t\tjavax.jms.Connection connection_";
  protected final String TEXT_5 = " = (javax.jms.Connection)globalMap.get(\"connection_";
  protected final String TEXT_6 = "\");" + NL + "\t\tjavax.jms.MessageProducer producer_";
  protected final String TEXT_7 = " = (javax.jms.MessageProducer)globalMap.get(\"producer_";
  protected final String TEXT_8 = "\");" + NL + "\t\tif(session_";
  protected final String TEXT_9 = " != null && producer_";
  protected final String TEXT_10 = " != null && connection_";
  protected final String TEXT_11 = " != null) {" + NL + "\t\t\tsession_";
  protected final String TEXT_12 = ".commit();";
  protected final String TEXT_13 = NL + "\t\t\tproducer_";
  protected final String TEXT_14 = ".close();" + NL + "\t        session_";
  protected final String TEXT_15 = ".close();" + NL + "\t        connection_";
  protected final String TEXT_16 = ".close();";
  protected final String TEXT_17 = NL + "\t\t}";
  protected final String TEXT_18 = NL + "\t\tcom.ibm.mq.MQQueueManager qMgr_";
  protected final String TEXT_19 = " = (com.ibm.mq.MQQueueManager)globalMap.get(\"qMgr_";
  protected final String TEXT_20 = "\");" + NL + "\t    com.ibm.mq.MQQueue remoteQ_";
  protected final String TEXT_21 = " = (com.ibm.mq.MQQueue)globalMap.get(\"remoteQ_";
  protected final String TEXT_22 = "\");" + NL + "\t\tif(qMgr_";
  protected final String TEXT_23 = " != null && remoteQ_";
  protected final String TEXT_24 = " != null) {" + NL + "\t\t\tqMgr_";
  protected final String TEXT_25 = ".commit();";
  protected final String TEXT_26 = NL + "\t\t\tremoteQ_";
  protected final String TEXT_27 = ".close();" + NL + "\t\t    qMgr_";
  protected final String TEXT_28 = ".disconnect();;";
  protected final String TEXT_29 = NL + "\t\t}";
  protected final String TEXT_30 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();

    String cid = node.getUniqueName();

    String connectionCid = ElementParameterParser.getValue(node,"__CONNECTION__");
    
    boolean close = ("true").equals(ElementParameterParser.getValue(node,"__CLOSE__"));

	String serverType=ElementParameterParser.getValue(node, "__SERVER__");
	
	if (("JBoss").equals(serverType) || ("ActiveMQ").equals(serverType)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(connectionCid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(connectionCid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(connectionCid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
      
		if(close){

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
     
		}

    stringBuffer.append(TEXT_17);
    
	} else {

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(connectionCid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(connectionCid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
      
		if(close){

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
     
		}

    stringBuffer.append(TEXT_29);
    
	}

    stringBuffer.append(TEXT_30);
    return stringBuffer.toString();
  }
}
