package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TMomOutputMainJava
{
  protected static String nl;
  public static synchronized TMomOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMomOutputMainJava result = new TMomOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t\t\t\tString msgID_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ".";
  protected final String TEXT_5 = ";" + NL + "\t\t\t\t\t\tjavax.jms.MapMessage message_";
  protected final String TEXT_6 = " = session_";
  protected final String TEXT_7 = ".createMapMessage();";
  protected final String TEXT_8 = NL + "\t\t\t\t\t\tString msgBody_";
  protected final String TEXT_9 = " = String.valueOf(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL + "\t\t\t\t\t\t\tjavax.jms.TextMessage message_";
  protected final String TEXT_13 = " = session_";
  protected final String TEXT_14 = ".createTextMessage( msgBody_";
  protected final String TEXT_15 = ");";
  protected final String TEXT_16 = NL + "\t\t\t\t\t\t\t message_";
  protected final String TEXT_17 = ".setString(msgID_";
  protected final String TEXT_18 = ",msgBody_";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + "\t\t\t\t\t\tString msgBody_";
  protected final String TEXT_21 = " = String.valueOf(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ");";
  protected final String TEXT_24 = NL + "\t\t\t\t\t\t\tjavax.jms.BytesMessage message_";
  protected final String TEXT_25 = " = session_";
  protected final String TEXT_26 = ".createBytesMessage();" + NL + "\t\t\t\t\t\t\tmessage_";
  protected final String TEXT_27 = ".writeBytes(msgBody_";
  protected final String TEXT_28 = ".getBytes());";
  protected final String TEXT_29 = NL + "\t\t\t\t\t\t\tmessage_";
  protected final String TEXT_30 = ".setBytes(msgID_";
  protected final String TEXT_31 = ",msgBody_";
  protected final String TEXT_32 = ".getBytes());";
  protected final String TEXT_33 = NL + "\t\t\t\t\t\tjavax.jms.MapMessage message_";
  protected final String TEXT_34 = " = session_";
  protected final String TEXT_35 = ".createMapMessage();";
  protected final String TEXT_36 = NL + "\t\t\t\t\t\t\tmessage_";
  protected final String TEXT_37 = ".set";
  protected final String TEXT_38 = "(\"";
  protected final String TEXT_39 = "\",";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = ");\t" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_42 = NL + "\t\t\t\t\t\t\tmessage_";
  protected final String TEXT_43 = ".set";
  protected final String TEXT_44 = "(";
  protected final String TEXT_45 = ");";
  protected final String TEXT_46 = NL + "\t\t\t\t\t\t\tmessage_";
  protected final String TEXT_47 = ".set";
  protected final String TEXT_48 = "Property(";
  protected final String TEXT_49 = ", ";
  protected final String TEXT_50 = ");";
  protected final String TEXT_51 = NL + NL + "\t\t\t\t\t\tproducer_";
  protected final String TEXT_52 = ".send(message_";
  protected final String TEXT_53 = ");";
  protected final String TEXT_54 = NL + "\t\t\t\t\tcom.ibm.mq.MQMessage message_";
  protected final String TEXT_55 = " = new com.ibm.mq.MQMessage();";
  protected final String TEXT_56 = NL + "\t\t\t\t\t\tmessage_";
  protected final String TEXT_57 = ".format = ";
  protected final String TEXT_58 = ";";
  protected final String TEXT_59 = "\t";
  protected final String TEXT_60 = NL + "\t\t\t\t\t\tString msgID_";
  protected final String TEXT_61 = " = ";
  protected final String TEXT_62 = ".";
  protected final String TEXT_63 = ";" + NL + "\t\t\t\t\t\tif (msgID_";
  protected final String TEXT_64 = " != null & !(\"\").equals(msgID_";
  protected final String TEXT_65 = ")) {" + NL + "\t\t\t\t\t\t\tString padding = new String();" + NL + "\t\t\t\t\t       \tint padlen = 24;" + NL + "\t\t\t\t\t " + NL + "\t\t\t\t\t       \tint len = Math.abs(padlen) - msgID_";
  protected final String TEXT_66 = ".toString().length();" + NL + "\t\t\t\t\t       \tif (len > 0) {" + NL + "\t\t\t\t\t        \tfor (int i = 0 ; i < len ; i++) {" + NL + "\t\t\t\t\t           \t\tpadding = padding + \" \";" + NL + "\t\t\t\t\t         \t}" + NL + "\t\t\t\t\t        \tmsgID_";
  protected final String TEXT_67 = " = msgID_";
  protected final String TEXT_68 = " + padding;" + NL + "\t\t\t\t\t        }" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tmessage_";
  protected final String TEXT_69 = ".messageId = msgID_";
  protected final String TEXT_70 = ".getBytes(\"ISO-8859-15\");";
  protected final String TEXT_71 = NL + "\t\t\t\t\t\t\tmessage_";
  protected final String TEXT_72 = ".";
  protected final String TEXT_73 = " = ";
  protected final String TEXT_74 = ";";
  protected final String TEXT_75 = NL + "\t\t\t\t\t\tString msgBody_";
  protected final String TEXT_76 = " = String.valueOf(";
  protected final String TEXT_77 = ".";
  protected final String TEXT_78 = ");" + NL + "\t\t\t\t\t\tmessage_";
  protected final String TEXT_79 = ".writeString(msgBody_";
  protected final String TEXT_80 = ");";
  protected final String TEXT_81 = NL + "\t\t\t\t\t\tString msgBody_";
  protected final String TEXT_82 = " = String.valueOf(";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = ");" + NL + "\t\t\t\t\t\tmessage_";
  protected final String TEXT_85 = ".write(msgBody_";
  protected final String TEXT_86 = ".getBytes());";
  protected final String TEXT_87 = NL + "\t\t\t\t\t\tjava.util.Map msgBody_";
  protected final String TEXT_88 = " = new java.util.HashMap();";
  protected final String TEXT_89 = NL + "\t\t\t\t\t\t\tmsgBody_";
  protected final String TEXT_90 = ".put(\"";
  protected final String TEXT_91 = "\",";
  protected final String TEXT_92 = ".";
  protected final String TEXT_93 = ");";
  protected final String TEXT_94 = NL + "\t\t\t\t    \tmessage_";
  protected final String TEXT_95 = ".writeObject(msgBody_";
  protected final String TEXT_96 = ");" + NL + "\t\t\t\t  ";
  protected final String TEXT_97 = NL + "\t\t\t\t\tremoteQ_";
  protected final String TEXT_98 = ".put(message_";
  protected final String TEXT_99 = ", opM_";
  protected final String TEXT_100 = ");";
  protected final String TEXT_101 = NL + "\t" + NL + "" + NL + "" + NL + "\t\t";
  protected final String TEXT_102 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String serverType=ElementParameterParser.getValue(node, "__SERVER__");
	String useMsgId=ElementParameterParser.getValue(node, "__IS_USE_MESSAGE_ID__");
	boolean useMQFormat = ("true").equals(ElementParameterParser.getValue(node, "__USE_FORMAT__"));
	String wsMQFormat = ElementParameterParser.getValue(node, "__WS_MQ_FORMAT__");
	
	String msgBobyType =  ElementParameterParser.getValue(node, "__MESSAGE_BODY_TYPE__");
	
	boolean setJmsHeader =  ("true").equals(ElementParameterParser.getValue(node, "__SET_JMS_HEADER__"));
	List<Map<String,String>> jmsHeaders = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__JMS_HEADERS__");
	
	boolean setJmsProp =  ("true").equals(ElementParameterParser.getValue(node, "__SET_JMS_PROPERTIES__"));
	List<Map<String,String>> jmsProps = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__JMS_PROPERTIES__");

	boolean setMQMDField =  ("true").equals(ElementParameterParser.getValue(node, "__SET_MQMD_FIELDS__"));
	List<Map<String,String>> mqmdFields = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MQMD_FIELDS__");
		
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		List<IMetadataColumn> columns = metadata.getListColumns();
		List< ? extends IConnection> conns = node.getIncomingConnections();
		if((conns!=null)&&(conns.size()>0)) {
			IConnection conn = conns.get(0);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
			
				if (("JBoss").equals(serverType) || ("ActiveMQ").equals(serverType)) {
				
					/*-------------------1.is use message id.this functions just use map message type-------------------------------------*/
					if(("true").equals(useMsgId)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(((IMetadataColumn)columns.get(1)).getLabel() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
					}
					
					/*--------------------------2.judge message body type----------------------------------------------------------------*/
					if ("Text".equals(msgBobyType)) {

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(((IMetadataColumn)columns.get(0)).getLabel());
    stringBuffer.append(TEXT_11);
    
						if(!("true").equals(useMsgId)){

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    
						} else {

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
						}
					} else if ("Bytes".equals(msgBobyType)) {
						IMetadataColumn column = (IMetadataColumn)columns.get(0);
						String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(((IMetadataColumn)columns.get(0)).getLabel());
    stringBuffer.append(TEXT_23);
    
						if(!("true").equals(useMsgId)){

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    
						} else {

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
						}
					} else if ("Map".equals(msgBobyType)) {

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    
						for(IMetadataColumn column : columns) {
							 String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
							  if(("byte[]").equals(typeToGenerate)) {
					                typeToGenerate = "Bytes";
					            }else if(("Character").equals(typeToGenerate)) {
					            	 typeToGenerate = "Char";
					            }else if(("Integer").equals(typeToGenerate)) {
					            	 typeToGenerate = "Int";
					            } else if(("Java.util.Date").equals(typeToGenerate)||"BigDecimal".equals(typeToGenerate)
					            			||"List".equals(typeToGenerate)) {
					            	 typeToGenerate = "Object";
					            }else {
					                typeToGenerate = typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					            }

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_39);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_41);
    
						}
					} 
					/*---------------------------------------------3.set message headers------------------------------------------------------*/
					
					if (setJmsHeader) {
						for(Map<String,String> header:jmsHeaders) {

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(header.get("JMS_HEADER_NAME"));
    stringBuffer.append(TEXT_44);
    stringBuffer.append(header.get("JMS_HEADER_VALUE"));
    stringBuffer.append(TEXT_45);
    				
						}
					}
					
					/*---------------------------------------------4.set message headers------------------------------------------------------*/
					
					if (setJmsProp) {
						for(Map<String,String> prop:jmsProps) {

    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(prop.get("JMS_PROPERTIES_TYPE"));
    stringBuffer.append(TEXT_48);
    stringBuffer.append(prop.get("JMS_PROPERTIES_NAME"));
    stringBuffer.append(TEXT_49);
    stringBuffer.append(prop.get("JMS_PROPERTIES_VALUE"));
    stringBuffer.append(TEXT_50);
    				
						}
					}
					
					/*---------------------------------------------5.send message to server------------------------------------------------------*/

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    
				} else {//server judgement   /***WebSphere MQ*****/

    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
     
					if(useMQFormat) {

    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(wsMQFormat);
    stringBuffer.append(TEXT_58);
    
					}

    stringBuffer.append(TEXT_59);
    
					if(("true").equals(useMsgId) && !"Map".equals(msgBobyType)){

    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(((IMetadataColumn)columns.get(1)).getLabel() );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    
					}
										
					/*---------------------------------------------set MQMD Fields------------------------------------------------------*/
					
					if (setMQMDField) {
						for(Map<String,String> field:mqmdFields) {

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(field.get("MQMD_FIELD_NAME"));
    stringBuffer.append(TEXT_73);
    stringBuffer.append(field.get("MQMD_FIELD_VALUE"));
    stringBuffer.append(TEXT_74);
    				
						}
					}
					
					if ("Text".equals(msgBobyType)) {

    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(((IMetadataColumn)columns.get(0)).getLabel());
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    
					} else if ("Bytes".equals(msgBobyType)) {

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(((IMetadataColumn)columns.get(0)).getLabel());
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    
					} else if ("Map".equals(msgBobyType)) {

    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    
						for(IMetadataColumn column : columns) {

    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_91);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_93);
    
						}

    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    
					}

    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    
				}
			}
		}
	}  

    stringBuffer.append(TEXT_101);
    stringBuffer.append(TEXT_102);
    return stringBuffer.toString();
  }
}
