package org.talend.designer.codegen.translators.business.salesforce;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TSalesforceOutputMainJava
{
  protected static String nl;
  public static synchronized TSalesforceOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceOutputMainJava result = new TSalesforceOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    java.util.Properties props_";
  protected final String TEXT_3 = " = System.getProperties();" + NL + "    props_";
  protected final String TEXT_4 = ".put(\"socksProxyHost\",";
  protected final String TEXT_5 = ");  " + NL + "    props_";
  protected final String TEXT_6 = ".put(\"socksProxyPort\",";
  protected final String TEXT_7 = ");" + NL + "    props_";
  protected final String TEXT_8 = ".put(\"java.net.socks.username\", ";
  protected final String TEXT_9 = ");" + NL + "    props_";
  protected final String TEXT_10 = ".put(\"java.net.socks.password\", ";
  protected final String TEXT_11 = "); " + NL + "    ";
  protected final String TEXT_12 = NL;
  protected final String TEXT_13 = NL + NL + "\t";
  protected final String TEXT_14 = " = null;\t\t\t";
  protected final String TEXT_15 = NL + "///////////////////////\t\t" + NL + "" + NL + "" + NL + "Object[] resultMessageObj_";
  protected final String TEXT_16 = " = null;";
  protected final String TEXT_17 = NL + NL + "\t\t\t\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\tjava.util.List<org.apache.axiom.om.OMElement> list_";
  protected final String TEXT_19 = " = new java.util.ArrayList<org.apache.axiom.om.OMElement>();" + NL + "\t\t\t\tjava.util.List<String> nullList_";
  protected final String TEXT_20 = " = new java.util.ArrayList<String>();\t\t\t\t";
  protected final String TEXT_21 = NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = " != null){" + NL + "\t\t\t\t\t\t\tlist_";
  protected final String TEXT_24 = ".add(sforceManagement_";
  protected final String TEXT_25 = ".newOMElement(\"";
  protected final String TEXT_26 = "\",FormatterUtils.format_Date(";
  protected final String TEXT_27 = ".";
  protected final String TEXT_28 = ", ";
  protected final String TEXT_29 = ") ));" + NL + "\t\t\t\t\t\t}";
  protected final String TEXT_30 = NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_31 = ".";
  protected final String TEXT_32 = " != null){" + NL + "\t\t\t\t\t\t\tlist_";
  protected final String TEXT_33 = ".add(sforceManagement_";
  protected final String TEXT_34 = ".newOMElement(\"";
  protected final String TEXT_35 = "\",java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_36 = ".";
  protected final String TEXT_37 = ")).toString() ));" + NL + "\t\t\t\t\t\t}";
  protected final String TEXT_38 = "   \t\t\t\t" + NL + "\t\t    \t\t\t";
  protected final String TEXT_39 = NL + "\t\t    \t\t\tif(";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = " != null && !\"\".equals(String.valueOf(";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = "))) { " + NL + "\t\t    \t\t\t";
  protected final String TEXT_44 = NL + "\t\t    \t\t\tif(!\"\".equals(String.valueOf(";
  protected final String TEXT_45 = ".";
  protected final String TEXT_46 = "))) { " + NL + "\t\t    \t\t\t";
  protected final String TEXT_47 = NL + "\t\t\t\t\t\t\tlist_";
  protected final String TEXT_48 = ".add(sforceManagement_";
  protected final String TEXT_49 = ".newOMElement(\"";
  protected final String TEXT_50 = "\",String.valueOf(";
  protected final String TEXT_51 = ".";
  protected final String TEXT_52 = ") ));" + NL + "\t\t\t\t\t\t}";
  protected final String TEXT_53 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_54 = NL + "\t\t\t\t\t    else{" + NL + "\t\t\t\t\t\t\tnullList_";
  protected final String TEXT_55 = ".add(\"";
  protected final String TEXT_56 = "\");" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_57 = NL + "\t\t\t\t\t\t\t" + NL + " \t\t\t" + NL + "    \t\t\t";
  protected final String TEXT_58 = " " + NL + "\t\t\t";
  protected final String TEXT_59 = NL + NL + "resultMessageObj_";
  protected final String TEXT_60 = " = sforceManagement_";
  protected final String TEXT_61 = ".insert(\"";
  protected final String TEXT_62 = "\", (org.apache.axiom.om.OMElement[])list_";
  protected final String TEXT_63 = ".toArray(new org.apache.axiom.om.OMElement[list_";
  protected final String TEXT_64 = ".size()]));    \t\t\t" + NL;
  protected final String TEXT_65 = NL + NL + "resultMessageObj_";
  protected final String TEXT_66 = " = sforceManagement_";
  protected final String TEXT_67 = ".update(\"";
  protected final String TEXT_68 = "\", ";
  protected final String TEXT_69 = ".Id, (org.apache.axiom.om.OMElement[])list_";
  protected final String TEXT_70 = ".toArray(new org.apache.axiom.om.OMElement[list_";
  protected final String TEXT_71 = ".size()]),(String[])nullList_";
  protected final String TEXT_72 = ".toArray(new String[nullList_";
  protected final String TEXT_73 = ".size()]));    \t\t\t" + NL;
  protected final String TEXT_74 = " " + NL + "" + NL + "resultMessageObj_";
  protected final String TEXT_75 = " = sforceManagement_";
  protected final String TEXT_76 = ".upsert(\"";
  protected final String TEXT_77 = "\", \"";
  protected final String TEXT_78 = "\", (org.apache.axiom.om.OMElement[])list_";
  protected final String TEXT_79 = ".toArray(new org.apache.axiom.om.OMElement[list_";
  protected final String TEXT_80 = ".size()]),(String[])nullList_";
  protected final String TEXT_81 = ".toArray(new String[nullList_";
  protected final String TEXT_82 = ".size()]));    \t\t\t" + NL;
  protected final String TEXT_83 = NL + "    \t\t\t";
  protected final String TEXT_84 = NL + NL + "resultMessageObj_";
  protected final String TEXT_85 = " = sforceManagement_";
  protected final String TEXT_86 = ".delete(";
  protected final String TEXT_87 = ".Id);    \t\t\t" + NL;
  protected final String TEXT_88 = "     \t\t\t" + NL + "    \t\t\t" + NL + "\t\t\t\tnb_line_";
  protected final String TEXT_89 = "++; " + NL;
  protected final String TEXT_90 = NL + "\t\t\t\tjava.util.Map<String,String> resultMessage_";
  protected final String TEXT_91 = " = sforceManagement_";
  protected final String TEXT_92 = ".readResult(resultMessageObj_";
  protected final String TEXT_93 = ");" + NL + "\t\t\t\tif(resultMessage_";
  protected final String TEXT_94 = "!=null){" + NL + "\t\t\t\t\tif(\"true\".equals(resultMessage_";
  protected final String TEXT_95 = ".get(\"success\"))){";
  protected final String TEXT_96 = NL + "\t\t\tnb_success_";
  protected final String TEXT_97 = "++;" + NL + "\t\t\t";
  protected final String TEXT_98 = " = new ";
  protected final String TEXT_99 = "Struct();";
  protected final String TEXT_100 = NL + "\t\t\t";
  protected final String TEXT_101 = ".salesforce_id = resultMessage_";
  protected final String TEXT_102 = ".get(\"id\");";
  protected final String TEXT_103 = "\t\t\t" + NL + "\t\t    ";
  protected final String TEXT_104 = ".";
  protected final String TEXT_105 = " = ";
  protected final String TEXT_106 = ".";
  protected final String TEXT_107 = ";\t\t\t";
  protected final String TEXT_108 = NL + "\t\t\t\t}else{";
  protected final String TEXT_109 = NL + "\t\t\tnb_reject_";
  protected final String TEXT_110 = "++;" + NL + "\t\t\t";
  protected final String TEXT_111 = " = new ";
  protected final String TEXT_112 = "Struct();" + NL + "\t\t\t";
  protected final String TEXT_113 = ".errorCode = resultMessage_";
  protected final String TEXT_114 = ".get(\"StatusCode\");" + NL + "\t\t\t";
  protected final String TEXT_115 = ".errorFields = resultMessage_";
  protected final String TEXT_116 = ".get(\"Fields\");" + NL + "\t\t\t";
  protected final String TEXT_117 = ".errorMessage = resultMessage_";
  protected final String TEXT_118 = ".get(\"Message\");";
  protected final String TEXT_119 = "\t\t\t" + NL + "\t\t    ";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = " = ";
  protected final String TEXT_122 = ".";
  protected final String TEXT_123 = ";\t\t\t";
  protected final String TEXT_124 = NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_125 = "\t";
  protected final String TEXT_126 = NL + "\t\t\tnb_success_";
  protected final String TEXT_127 = "++;" + NL + "\t\t\t";
  protected final String TEXT_128 = " = new ";
  protected final String TEXT_129 = "Struct();";
  protected final String TEXT_130 = "\t\t\t" + NL + "\t\t    ";
  protected final String TEXT_131 = ".";
  protected final String TEXT_132 = " = ";
  protected final String TEXT_133 = ".";
  protected final String TEXT_134 = ";\t\t\t";
  protected final String TEXT_135 = "\t\t \t";
  protected final String TEXT_136 = "\t\t\t    \t\t\t" + NL + "///////////////////////    \t\t\t";
  protected final String TEXT_137 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();    	

	boolean useProxy = ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
	String proxyHost = ElementParameterParser.getValue(node,"__PROXY_HOST__");
   	String proxyPort = ElementParameterParser.getValue(node,"__PROXY_PORT__");
   	String proxyUsername = ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
   	String proxyPassword = ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");

String action = ElementParameterParser.getValue(node, "__ACTION__");
String modulename = ElementParameterParser.getValue(node, "__MODULENAME__");

String customModulename = ElementParameterParser.getValue(node, "__CUSTOM_MODULE_NAME__");

boolean retreiveInsertID = ("true").equals(ElementParameterParser.getValue(node, "__RETREIVE_INSERT_ID__"));
boolean extendedInsert = ("true").equals(ElementParameterParser.getValue(node, "__EXTENDINSERT__"));

    stringBuffer.append(TEXT_1);
    
if(useProxy){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(proxyPort );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(proxyUsername );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_11);
    
}

    stringBuffer.append(TEXT_12);
    
if ("CustomModule".equals(modulename)) {
	//modulename = customModulename; 
	modulename = customModulename.replaceAll("\"","");
}

String upsertkey = ElementParameterParser.getValue(node, "__UPSERT_KEY_COLUMN__");
boolean ceaseForError = ("true").equals(ElementParameterParser.getValue(node, "__CEASE_FOR_ERROR__"));

List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
if(outgoingConns!=null){
	for (int i=0;i<outgoingConns.size();i++) {
    IConnection outgoingConn = outgoingConns.get(i);
    	if (outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_13);
    stringBuffer.append(outgoingConn.getName() );
    stringBuffer.append(TEXT_14);
    
    	}
    }
}
    	
List<? extends IConnection> connsSuccess = node.getOutgoingConnections("MAIN");
List<? extends IConnection> connsReject = node.getOutgoingConnections("REJECT");

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    
 if ("insert".equals(action) || "update".equals(action) || "upsert".equals(action)) {//************

    stringBuffer.append(TEXT_17);
      
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
        			
    			for (int i = 0; i < sizeColumns; i++) {//5  			

  			        
    				IMetadataColumn column = columns.get(i);
    				
    				//make sure to filter the schema "Id", when updating (but not when upserting)
  			        if("Id".equals(column.getLabel()) && !("upsert".equals(action))) continue;
    				
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
    				String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {//Date

    stringBuffer.append(TEXT_21);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_29);
    				
					} else if (javaType == JavaTypesManager.BYTE_ARRAY) {//byte[]

    stringBuffer.append(TEXT_30);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_37);
    				
					} else {//others
						

    stringBuffer.append(TEXT_38);
    if(!isPrimitive) {
    stringBuffer.append(TEXT_39);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_43);
    }else{
    stringBuffer.append(TEXT_44);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_46);
    }
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_52);
    				
					}

    stringBuffer.append(TEXT_53);
    if(!("Id".equals(column.getLabel()) && ("upsert".equals(action)))){
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_56);
    }
    stringBuffer.append(TEXT_57);
    
				}//5	

    stringBuffer.append(TEXT_58);
    
 if ("insert".equals(action)) {//#######

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(modulename );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    
	} else if ("update".equals(action)) {//#######

    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(modulename );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    
    } else if ("upsert".equals(action)) {//#######

    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(modulename );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(upsertkey);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    
  }//#######

    stringBuffer.append(TEXT_83);
    
	} else if ("delete".equals(action)) {//*************	

    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_87);
    
  }//************

    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    
	if(!extendedInsert){ 

    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    
			if (connsSuccess != null && connsSuccess.size() == 1) {
				IConnection connSuccess = connsSuccess.get(0);
				if (connSuccess.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(connSuccess.getName() );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(connSuccess.getName() );
    stringBuffer.append(TEXT_99);
    
					if(retreiveInsertID&&"insert".equals(action)){

    stringBuffer.append(TEXT_100);
    stringBuffer.append(connSuccess.getName() );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    
					}

    
					for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_103);
    stringBuffer.append(connSuccess.getName() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_107);
    					 
					}
				}
			}

    stringBuffer.append(TEXT_108);
    
			if (connsReject != null && connsReject.size() == 1) {
				IConnection connReject = connsReject.get(0);
				if (connReject.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(connReject.getName() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(connReject.getName() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(connReject.getName() );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(connReject.getName() );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(connReject.getName() );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    
					for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_119);
    stringBuffer.append(connReject.getName() );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_123);
    					 
					}
				}
			}

    stringBuffer.append(TEXT_124);
    
	}else{//batch start

    stringBuffer.append(TEXT_125);
    
			if (connsSuccess != null && connsSuccess.size() == 1) {
				IConnection connSuccess = connsSuccess.get(0);
				if (connSuccess.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(connSuccess.getName() );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(connSuccess.getName() );
    stringBuffer.append(TEXT_129);
    
					for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_130);
    stringBuffer.append(connSuccess.getName() );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_134);
    					 
					}
				}
			}

    stringBuffer.append(TEXT_135);
    
	}  //batch end

    stringBuffer.append(TEXT_136);
    
    		}//4
    	}//3
    }//2
}//1


    stringBuffer.append(TEXT_137);
    return stringBuffer.toString();
  }
}
