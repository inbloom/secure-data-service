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

public class TJMSOutputBeginJava
{
  protected static String nl;
  public static synchronized TJMSOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJMSOutputBeginJava result = new TJMSOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.util.Hashtable props_";
  protected final String TEXT_3 = " = new java.util.Hashtable();" + NL + "\tprops_";
  protected final String TEXT_4 = ".put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, ";
  protected final String TEXT_5 = ");" + NL + "\tprops_";
  protected final String TEXT_6 = ".put(javax.naming.Context.PROVIDER_URL, ";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = " " + NL + "\t\t\tprops_";
  protected final String TEXT_9 = ".put(";
  protected final String TEXT_10 = ", ";
  protected final String TEXT_11 = "); ";
  protected final String TEXT_12 = NL + "\t" + NL + "\tjavax.naming.Context context_";
  protected final String TEXT_13 = " = new javax.naming.InitialContext(props_";
  protected final String TEXT_14 = ");" + NL + "\tjavax.jms.ConnectionFactory factory_";
  protected final String TEXT_15 = " = (javax.jms.ConnectionFactory) context_";
  protected final String TEXT_16 = ".lookup(";
  protected final String TEXT_17 = ");" + NL + "\t" + NL + "\tjavax.jms.Connection connection_";
  protected final String TEXT_18 = " = factory_";
  protected final String TEXT_19 = ".createConnection(";
  protected final String TEXT_20 = ", ";
  protected final String TEXT_21 = ");" + NL + "\tconnection_";
  protected final String TEXT_22 = ".start();" + NL + "" + NL + "\tjavax.jms.Session session_";
  protected final String TEXT_23 = " = connection_";
  protected final String TEXT_24 = ".createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);" + NL + "\tjavax.jms.Destination dest_";
  protected final String TEXT_25 = " = session_";
  protected final String TEXT_26 = ".create";
  protected final String TEXT_27 = "(";
  protected final String TEXT_28 = ");" + NL + "" + NL + "\tjavax.jms.MessageProducer producer_";
  protected final String TEXT_29 = " = session_";
  protected final String TEXT_30 = ".createProducer(dest_";
  protected final String TEXT_31 = ");" + NL + "" + NL + "\tproducer_";
  protected final String TEXT_32 = ".setDeliveryMode(javax.jms.DeliveryMode.";
  protected final String TEXT_33 = ");" + NL + "" + NL + "\tint nbline_";
  protected final String TEXT_34 = " = 0;" + NL + "" + NL + "" + NL + "" + NL + "\t\t";
  protected final String TEXT_35 = NL;

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
String to=ElementParameterParser.getValue(node, "__TO__");
String deliverMode = ElementParameterParser.getValue(node, "__DELIVERY_MODE__");
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    if("true".equals(userIdentity)){
    stringBuffer.append(user );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(pass );
    }
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(msgType);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(to );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(deliverMode);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(TEXT_35);
    return stringBuffer.toString();
  }
}
