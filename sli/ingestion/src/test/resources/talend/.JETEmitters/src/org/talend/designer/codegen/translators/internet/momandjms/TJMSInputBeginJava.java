package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.Map;

public class TJMSInputBeginJava
{
  protected static String nl;
  public static synchronized TJMSInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJMSInputBeginJava result = new TJMSInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "\tjava.util.Hashtable props_";
  protected final String TEXT_3 = " = new java.util.Hashtable();" + NL + "\tprops_";
  protected final String TEXT_4 = ".put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, ";
  protected final String TEXT_5 = ");" + NL + "\tprops_";
  protected final String TEXT_6 = ".put(javax.naming.Context.PROVIDER_URL, ";
  protected final String TEXT_7 = ");" + NL + "\t";
  protected final String TEXT_8 = NL + "\tprops_";
  protected final String TEXT_9 = ".put(";
  protected final String TEXT_10 = ", ";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL + "\t" + NL + "\tjavax.naming.Context context_";
  protected final String TEXT_13 = " = new javax.naming.InitialContext(props_";
  protected final String TEXT_14 = ");" + NL + "\tjavax.jms.ConnectionFactory factory_";
  protected final String TEXT_15 = " = (javax.jms.ConnectionFactory) context_";
  protected final String TEXT_16 = ".lookup(";
  protected final String TEXT_17 = ");" + NL + "\tjavax.jms.Connection connection_";
  protected final String TEXT_18 = " = factory_";
  protected final String TEXT_19 = ".createConnection(";
  protected final String TEXT_20 = ", ";
  protected final String TEXT_21 = ");" + NL + "\tjavax.jms.Session session_";
  protected final String TEXT_22 = " = connection_";
  protected final String TEXT_23 = ".createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);" + NL + "\tjavax.jms.Destination dest_";
  protected final String TEXT_24 = " = session_";
  protected final String TEXT_25 = ".create";
  protected final String TEXT_26 = "(";
  protected final String TEXT_27 = ");" + NL + "\tjavax.jms.MessageConsumer consumer_";
  protected final String TEXT_28 = "\t= session_";
  protected final String TEXT_29 = ".createConsumer(dest_";
  protected final String TEXT_30 = ", ";
  protected final String TEXT_31 = ");" + NL + "" + NL + "\tconnection_";
  protected final String TEXT_32 = ".start();" + NL + "" + NL + "\tSystem.out.println(\"Ready to receive message\");" + NL + "\tSystem.out.println(\"Waiting...\");" + NL + "" + NL + "\tjavax.jms.Message message_";
  protected final String TEXT_33 = ";" + NL + "" + NL + "\tint nbline_";
  protected final String TEXT_34 = " = 0;" + NL + "" + NL + "\twhile ((message_";
  protected final String TEXT_35 = " = consumer_";
  protected final String TEXT_36 = ".receive(";
  protected final String TEXT_37 = "*1000)) != null) {";
  protected final String TEXT_38 = NL + "\t\t";
  protected final String TEXT_39 = ".message=message_";
  protected final String TEXT_40 = ";\t";
  protected final String TEXT_41 = NL + "\t\t";
  protected final String TEXT_42 = ".messageContent=((javax.jms.TextMessage) message_";
  protected final String TEXT_43 = ").getText();";
  protected final String TEXT_44 = NL + NL + NL + "\t\t";
  protected final String TEXT_45 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    

CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String contextProvider=ElementParameterParser.getValue(node, "__CONTEXT_PROVIDER__");
String connFacName=ElementParameterParser.getValue(node, "__CONN_FACTORY_NAME__");
String url=ElementParameterParser.getValue(node, "__SERVER_URL__");
String userIdentity=ElementParameterParser.getValue(node, "__USER_IDENTITY__");
String user=ElementParameterParser.getValue(node, "__USER__");
String pass=ElementParameterParser.getValue(node, "__PASS__");
String from=ElementParameterParser.getValue(node, "__FROM__");
String timeout=ElementParameterParser.getValue(node, "__TIMEOUT__");
String messageSelector=ElementParameterParser.getValue(node, "__MSG_SELECTOR__");
String processingMode = ElementParameterParser.getValue(node, "__PROCESSING_MODE__");
String msgType = ElementParameterParser.getValue(node, "__MSGTYPE__");

List<Map<String, String>> advProps = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ADVANCED_PROPERTIES__");

IMetadataTable metadata=null;
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	metadata = metadatas.get(0);
}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(contextProvider);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(url);
    stringBuffer.append(TEXT_7);
    
if(advProps.size() > 0){
	for(Map<String, String> item : advProps){

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(item.get("PROPERTY") );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(item.get("VALUE") );
    stringBuffer.append(TEXT_11);
     
	} 
}

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(connFacName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    if("true".equals(userIdentity)){
    stringBuffer.append(user );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(pass );
    }
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(from );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(messageSelector);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append("-1".equals(timeout)?0:timeout);
    stringBuffer.append(TEXT_37);
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	List<IMetadataColumn> columnLists = metadata.getListColumns();
	for(IConnection conn:conns){
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.MAIN)) {
			String firstConnName = conn.getName();
			if("RAW".equals(processingMode)){

    stringBuffer.append(TEXT_38);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    
			}else{

    stringBuffer.append(TEXT_41);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    
			}
		}
	}
	


    stringBuffer.append(TEXT_44);
    stringBuffer.append(TEXT_45);
    return stringBuffer.toString();
  }
}
