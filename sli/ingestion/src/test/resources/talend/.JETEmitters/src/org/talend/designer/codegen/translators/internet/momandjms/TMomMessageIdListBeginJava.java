package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.ArrayList;

public class TMomMessageIdListBeginJava
{
  protected static String nl;
  public static synchronized TMomMessageIdListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMomMessageIdListBeginJava result = new TMomMessageIdListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "class HelloInput implements javax.jms.MessageListener {" + NL + "\tjavax.jms.";
  protected final String TEXT_3 = "Connection connection";
  protected final String TEXT_4 = ";" + NL + "\tjavax.jms.";
  protected final String TEXT_5 = "Session session";
  protected final String TEXT_6 = ";" + NL + "\tjavax.jms.";
  protected final String TEXT_7 = " topic";
  protected final String TEXT_8 = ";" + NL + "\tjava.util.Queue<String> msgQueue";
  protected final String TEXT_9 = "=null;\t" + NL + "\tpublic HelloInput(String factoryJNDI, String topicJNDI) throws javax.jms.JMSException, javax.naming.NamingException{" + NL + "\t\tmsgQueue";
  protected final String TEXT_10 = "=new java.util.LinkedList<String>();" + NL + "\t\tjava.util.Hashtable props";
  protected final String TEXT_11 = "=new java.util.Hashtable();" + NL + "\t\tprops";
  protected final String TEXT_12 = ".put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,\"org.jnp.interfaces.NamingContextFactory\");" + NL + "\t\tprops";
  protected final String TEXT_13 = ".put(javax.naming.Context.PROVIDER_URL, ";
  protected final String TEXT_14 = "+\":\"+";
  protected final String TEXT_15 = ");" + NL + "\t\tprops";
  protected final String TEXT_16 = ".put(\"java.naming.rmi.security.manager\", \"yes\");" + NL + "\t\tprops";
  protected final String TEXT_17 = ".put(javax.naming.Context.URL_PKG_PREFIXES, \"org.jboss.naming\");" + NL + "\t\tprops";
  protected final String TEXT_18 = ".put(\"java.naming.factory.url.pkgs\",\"org.jboss.naming:org.jnp.interfaces\");\t" + NL + "\t\tjavax.naming.Context context";
  protected final String TEXT_19 = "=new javax.naming.InitialContext(props";
  protected final String TEXT_20 = ");" + NL + "\t\tjavax.jms.";
  protected final String TEXT_21 = "ConnectionFactory factory";
  protected final String TEXT_22 = "=(javax.jms.";
  protected final String TEXT_23 = "ConnectionFactory)context";
  protected final String TEXT_24 = ".lookup(factoryJNDI);\t" + NL + "\t\tconnection";
  protected final String TEXT_25 = "=factory";
  protected final String TEXT_26 = ".create";
  protected final String TEXT_27 = "Connection();" + NL + "\t\tsession";
  protected final String TEXT_28 = "=connection";
  protected final String TEXT_29 = ".create";
  protected final String TEXT_30 = "Session(false, javax.jms.Session.AUTO_ACKNOWLEDGE);" + NL + "\t\ttopic";
  protected final String TEXT_31 = "=(javax.jms.";
  protected final String TEXT_32 = ")context";
  protected final String TEXT_33 = ".lookup(topicJNDI);" + NL + NL;
  protected final String TEXT_34 = NL + "\t\tjavax.jms.";
  protected final String TEXT_35 = "Receiver receiver";
  protected final String TEXT_36 = "=session";
  protected final String TEXT_37 = ".createReceiver(topic";
  protected final String TEXT_38 = ");" + NL + "\t\treceiver";
  protected final String TEXT_39 = ".setMessageListener(this);";
  protected final String TEXT_40 = NL + "\t\tjavax.jms.";
  protected final String TEXT_41 = "Subscriber subscriber";
  protected final String TEXT_42 = "=session";
  protected final String TEXT_43 = ".createSubscriber(topic";
  protected final String TEXT_44 = ");" + NL + "\t\tsubscriber";
  protected final String TEXT_45 = ".setMessageListener(this);";
  protected final String TEXT_46 = "\t" + NL + "\t\tconnection";
  protected final String TEXT_47 = ".start();" + NL + "\t}" + NL + "\t" + NL + "\tpublic void onMessage(javax.jms.Message m){" + NL + "\ttry {" + NL + "\t\t\tString msg";
  protected final String TEXT_48 = "=((javax.jms.TextMessage)m).getText();" + NL + "\t\t\tmsgQueue";
  protected final String TEXT_49 = ".add(msg";
  protected final String TEXT_50 = ");" + NL + "\t\t} catch (javax.jms.JMSException e) {" + NL + "\t\t\tSystem.err.println(\"Could not get text message:\" + e);" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t}" + NL + "\t}" + NL + "\tpublic void close() throws javax.jms.JMSException{" + NL + "\t\tsession";
  protected final String TEXT_51 = ".close();" + NL + "\t\tconnection";
  protected final String TEXT_52 = ".close();" + NL + "\t}\t" + NL + "}" + NL + "" + NL + "\t" + NL + "\t\t" + NL + "\t\tHelloInput input";
  protected final String TEXT_53 = "=null;" + NL + "\t\ttry {" + NL + "\t\t//begin part" + NL + "\t\t\tinput";
  protected final String TEXT_54 = "=new HelloInput(\"";
  protected final String TEXT_55 = "TopicConnectionFactory";
  protected final String TEXT_56 = "ConnectionFactory";
  protected final String TEXT_57 = "\",";
  protected final String TEXT_58 = ");\t\t\t" + NL + "\t\t} catch (java.lang.Exception e) {" + NL + "\t\t\tSystem.err.println(\"An exception occurred while testing HelloSubscriber:\" + e);" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t}" + NL + "\tSystem.out.println(\"Ready to receive message\");" + NL + "\tSystem.out.println(\"Waiting...\");\t" + NL + "\t\t" + NL + "\tBoolean flag";
  protected final String TEXT_59 = "=true;" + NL + "\twhile(flag";
  protected final String TEXT_60 = "){" + NL + "\t\t\t\t\twhile(!(input";
  protected final String TEXT_61 = ".msgQueue";
  protected final String TEXT_62 = ".size()==0)){" + NL + "\t\t\t\t\t\tString msg";
  protected final String TEXT_63 = "=(String)input";
  protected final String TEXT_64 = ".msgQueue";
  protected final String TEXT_65 = ".poll();" + NL + "\t";
  protected final String TEXT_66 = NL + NL + "\t\t";
  protected final String TEXT_67 = ".from=";
  protected final String TEXT_68 = ";\t" + NL + "\t\t";
  protected final String TEXT_69 = ".message=msg";
  protected final String TEXT_70 = ";\t" + NL + "\t\t\t";
  protected final String TEXT_71 = NL + "\tjava.util.List<String> idList_";
  protected final String TEXT_72 = " = new java.util.ArrayList<String>();" + NL + "" + NL + "\tjava.util.Hashtable properties_";
  protected final String TEXT_73 = "=new java.util.Hashtable();" + NL + "\tproperties_";
  protected final String TEXT_74 = ".put(\"hostname\", ";
  protected final String TEXT_75 = ");" + NL + "\tproperties_";
  protected final String TEXT_76 = ".put(\"port\", Integer.valueOf(";
  protected final String TEXT_77 = "));" + NL + "\tproperties_";
  protected final String TEXT_78 = ".put(\"channel\", ";
  protected final String TEXT_79 = ");" + NL + "\tproperties_";
  protected final String TEXT_80 = ".put(\"CCSID\", new Integer(1208));" + NL + "\tproperties_";
  protected final String TEXT_81 = ".put(\"transport\",\"MQSeries\");" + NL + "\t" + NL + "\tcom.ibm.mq.MQQueueManager qMgr_";
  protected final String TEXT_82 = "=null;" + NL + "\tcom.ibm.mq.MQQueue remoteQ_";
  protected final String TEXT_83 = "=null;" + NL + "\ttry{" + NL + "\t\tint openOptions_";
  protected final String TEXT_84 = "=com.ibm.mq.MQC.MQOO_FAIL_IF_QUIESCING | com.ibm.mq.MQC.MQOO_INPUT_AS_Q_DEF;" + NL + "" + NL + "    \tcom.ibm.mq.MQGetMessageOptions gmo_";
  protected final String TEXT_85 = "=new com.ibm.mq.MQGetMessageOptions();" + NL + "    \tgmo_";
  protected final String TEXT_86 = ".options=gmo_";
  protected final String TEXT_87 = ".options+com.ibm.mq.MQC.MQGMO_SYNCPOINT;" + NL + "    \tgmo_";
  protected final String TEXT_88 = ".options=gmo_";
  protected final String TEXT_89 = ".options+com.ibm.mq.MQC.MQGMO_NO_WAIT;" + NL + "    \tgmo_";
  protected final String TEXT_90 = ".options=gmo_";
  protected final String TEXT_91 = ".options+com.ibm.mq.MQC.MQGMO_FAIL_IF_QUIESCING;" + NL + "    \tgmo_";
  protected final String TEXT_92 = ".waitInterval=com.ibm.mq.MQC.MQWI_UNLIMITED;" + NL + "    \tcom.ibm.mq.MQException.log = null;" + NL + "    \tboolean flag_";
  protected final String TEXT_93 = "=true;" + NL + "    " + NL + "    \tqMgr_";
  protected final String TEXT_94 = "=new com.ibm.mq.MQQueueManager(";
  protected final String TEXT_95 = ",properties_";
  protected final String TEXT_96 = ");" + NL + "    \twhile(flag_";
  protected final String TEXT_97 = "){" + NL + "    \t\tremoteQ_";
  protected final String TEXT_98 = "=qMgr_";
  protected final String TEXT_99 = ".accessQueue(";
  protected final String TEXT_100 = ",openOptions_";
  protected final String TEXT_101 = ");" + NL + "    \t\tcom.ibm.mq.MQMessage inMessage_";
  protected final String TEXT_102 = "=new com.ibm.mq.MQMessage();" + NL + "    \t\tremoteQ_";
  protected final String TEXT_103 = ".get(inMessage_";
  protected final String TEXT_104 = ",gmo_";
  protected final String TEXT_105 = ");" + NL + "    \t\tidList_";
  protected final String TEXT_106 = ".add(new String(inMessage_";
  protected final String TEXT_107 = ".messageId,\"ISO-8859-15\"));" + NL + "    \t\tremoteQ_";
  protected final String TEXT_108 = ".close();" + NL + "\t\t}" + NL + "\t}" + NL + "\tcatch(com.ibm.mq.MQException me_";
  protected final String TEXT_109 = "){" + NL + "\t\tif (me_";
  protected final String TEXT_110 = ".reasonCode == com.ibm.mq.MQException.MQRC_NO_MSG_AVAILABLE) {" + NL + "        \tremoteQ_";
  protected final String TEXT_111 = ".close();" + NL + "        \tqMgr_";
  protected final String TEXT_112 = ".backout();" + NL + "            qMgr_";
  protected final String TEXT_113 = ".disconnect();" + NL + "        }else{" + NL + "        \tthrow me_";
  protected final String TEXT_114 = ";" + NL + "        }" + NL + "\t}" + NL + "" + NL + "\tfor(String tmpId_";
  protected final String TEXT_115 = " : idList_";
  protected final String TEXT_116 = "){" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_117 = "_CURRENT_MESSAGE_ID\", tmpId_";
  protected final String TEXT_118 = ");";
  protected final String TEXT_119 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    

	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String serverType=ElementParameterParser.getValue(node, "__SERVER__");
	String host=ElementParameterParser.getValue(node, "__SERVERADDRESS__");
	String port=ElementParameterParser.getValue(node, "__SERVERPORT__");
	
	IMetadataTable metadata=null;
	List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
		metadata = metadatas.get(0);
		}




if(("JBoss").equals(serverType)){ // server judgement
	String msgType = ElementParameterParser.getValue(node, "__MSGTYPE__");
	String from=ElementParameterParser.getValue(node, "__FROM__");
	
	


    stringBuffer.append(TEXT_2);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    	
	if(("Queue").equals(msgType)){

    stringBuffer.append(TEXT_34);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    
}else{

    stringBuffer.append(TEXT_40);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    
}

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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    if(("Topic").equals(msgType)){
    stringBuffer.append(TEXT_55);
    }else{
    stringBuffer.append(TEXT_56);
    }
    stringBuffer.append(TEXT_57);
    stringBuffer.append(from);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
List<IMetadataColumn> columnList = metadata.getListColumns();
for(IConnection conn:conns){
	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.MAIN)) {
 	String firstConnName = conn.getName();

    stringBuffer.append(TEXT_66);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(from);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    
}
}
}  //server judgement   /***WebSphere MQ*****/
else{
	String channel=ElementParameterParser.getValue(node, "__CHANNEL__");
	String qm=ElementParameterParser.getValue(node, "__QM__");
	String queue = ElementParameterParser.getValue(node, "__QUEUE__");
	String idMask = ElementParameterParser.getValue(node, "__IDMASK__");

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(channel);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(qm);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(queue);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    
}

    stringBuffer.append(TEXT_119);
    return stringBuffer.toString();
  }
}
