package org.talend.designer.codegen.translators.business.salesforce;

import java.util.List;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSalesforceInputBeginJava
{
  protected static String nl;
  public static synchronized TSalesforceInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceInputBeginJava result = new TSalesforceInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "int limit_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "System.setProperty(\"org.apache.commons.logging.Log\", \"org.apache.commons.logging.impl.NoOpLog\");" + NL;
  protected final String TEXT_5 = NL + "    java.util.Properties props_";
  protected final String TEXT_6 = " = System.getProperties();" + NL + "    props_";
  protected final String TEXT_7 = ".put(\"socksProxyHost\",";
  protected final String TEXT_8 = ");  " + NL + "    props_";
  protected final String TEXT_9 = ".put(\"socksProxyPort\",";
  protected final String TEXT_10 = ");" + NL + "    props_";
  protected final String TEXT_11 = ".put(\"java.net.socks.username\", ";
  protected final String TEXT_12 = ");" + NL + "    props_";
  protected final String TEXT_13 = ".put(\"java.net.socks.password\", ";
  protected final String TEXT_14 = "); " + NL + "    ";
  protected final String TEXT_15 = NL;
  protected final String TEXT_16 = NL + "\t\t\torg.talend.salesforce.SforceManagement sfMgr_";
  protected final String TEXT_17 = " = new org.talend.salesforce.SforceManagementImpl();" + NL + "\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\tif(globalMap.get(\"stub_";
  protected final String TEXT_19 = "\")==null || globalMap.get(\"sessionHeader_";
  protected final String TEXT_20 = "\")==null){" + NL + "\t\t\t\t\tthrow new RuntimeException(\"Get null connection from ";
  protected final String TEXT_21 = "\");" + NL + "\t\t\t\t}" + NL + "\t\t\t\tif(globalMap.get(\"callOptions_";
  protected final String TEXT_22 = "\")!=null ){" + NL + "\t\t\t\t\tsfMgr_";
  protected final String TEXT_23 = ".setCallOptions((com.salesforce.soap.partner.CallOptions)globalMap.get(\"callOptions_";
  protected final String TEXT_24 = "\"));" + NL + "\t\t\t\t}" + NL + "\t\t\t\tsfMgr_";
  protected final String TEXT_25 = ".login((com.salesforce.soap.partner.SforceServiceStub)globalMap.get(\"stub_";
  protected final String TEXT_26 = "\"),(com.salesforce.soap.partner.SessionHeader)globalMap.get(\"sessionHeader_";
  protected final String TEXT_27 = "\"));" + NL + "\t\t\t";
  protected final String TEXT_28 = NL + "\t\t\t\t";
  protected final String TEXT_29 = NL + "\t\t\t\t\tsfMgr_";
  protected final String TEXT_30 = ".setClientID(";
  protected final String TEXT_31 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_32 = NL + "\t\t\t\tboolean result_";
  protected final String TEXT_33 = " = sfMgr_";
  protected final String TEXT_34 = ".login(";
  protected final String TEXT_35 = ",";
  protected final String TEXT_36 = ", ";
  protected final String TEXT_37 = ",";
  protected final String TEXT_38 = ",";
  protected final String TEXT_39 = ");" + NL + "\t\t\t\tif(!result_";
  protected final String TEXT_40 = "){" + NL + "\t\t\t\t\tthrow new RuntimeException(\"Login failed! Please check the username,password and endpoint\");" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_41 = NL + "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_42 = "\t\t\t\t\t" + NL + "\t\t\t\t\t" + NL + "\t\t\t\tStringBuilder sql_";
  protected final String TEXT_43 = " = new StringBuilder(";
  protected final String TEXT_44 = ");";
  protected final String TEXT_45 = NL + "\t\t\t\t\tString condition_";
  protected final String TEXT_46 = " = ";
  protected final String TEXT_47 = ";" + NL + "\t\t\t\t\tif (condition_";
  protected final String TEXT_48 = " !=null && !(\"\".equals(condition_";
  protected final String TEXT_49 = "))){" + NL + "\t\t\t\t\t\tsql_";
  protected final String TEXT_50 = ".append(\" where \").append(condition_";
  protected final String TEXT_51 = ");" + NL + "\t\t\t\t\t}";
  protected final String TEXT_52 = NL + "\t\t\t\tcom.salesforce.soap.partner.QueryResult qr_";
  protected final String TEXT_53 = " = null; ";
  protected final String TEXT_54 = NL + "\t\t\t\tqr_";
  protected final String TEXT_55 = " = sfMgr_";
  protected final String TEXT_56 = ".queryAll(sql_";
  protected final String TEXT_57 = ".toString(),new Integer(";
  protected final String TEXT_58 = "));";
  protected final String TEXT_59 = NL + "\t\t\t\tqr_";
  protected final String TEXT_60 = " = sfMgr_";
  protected final String TEXT_61 = ".query(sql_";
  protected final String TEXT_62 = ".toString(),new Integer(";
  protected final String TEXT_63 = "));";
  protected final String TEXT_64 = NL + "\t\t\t\t" + NL + "\t\t\t\torg.talend.salesforce.TopConfig.VALUE_DELIMITER = ";
  protected final String TEXT_65 = ";" + NL + "\t\t\t\torg.talend.salesforce.TopConfig.COLUMNNAME_DELIMTER = ";
  protected final String TEXT_66 = ";" + NL + "\t\t\t\t" + NL + "    \t\t\torg.talend.salesforce.TopQueryResult topqr_";
  protected final String TEXT_67 = " = null;    \t\t\t" + NL + "    \t\t\ttopqr_";
  protected final String TEXT_68 = " = new org.talend.salesforce.TopQueryResult();" + NL + "    \t\t\ttopqr_";
  protected final String TEXT_69 = ".processTopQueryResult(qr_";
  protected final String TEXT_70 = ");\t\t\t\t" + NL + "\t\t\t\t" + NL + "\t\t\t\tboolean bContinue_";
  protected final String TEXT_71 = " = true;" + NL + "\t\t\t\t" + NL + "\t\t\t\twhile (bContinue_";
  protected final String TEXT_72 = " && qr_";
  protected final String TEXT_73 = " != null && qr_";
  protected final String TEXT_74 = ".getRecords() != null) {" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\tjava.util.List<org.talend.salesforce.TopRecord> allTopRecords_";
  protected final String TEXT_75 = " = topqr_";
  protected final String TEXT_76 = ".getAllTopRecords();" + NL + "\t\t\t\t    // process the query results" + NL + "\t\t\t\t    for (int i_";
  protected final String TEXT_77 = " = 0; i_";
  protected final String TEXT_78 = " < allTopRecords_";
  protected final String TEXT_79 = ".size(); i_";
  protected final String TEXT_80 = "++) {\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\tnb_line_";
  protected final String TEXT_81 = " ++;" + NL + "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\torg.talend.salesforce.TopRecord topRecord_";
  protected final String TEXT_82 = " = allTopRecords_";
  protected final String TEXT_83 = ".get(i_";
  protected final String TEXT_84 = ");" + NL + "\t\t\t\t\t\tObject value_";
  protected final String TEXT_85 = " = null;";
  protected final String TEXT_86 = "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t//get the real value according the columnName" + NL + "\t\t\t\t\t\t\tvalue_";
  protected final String TEXT_87 = " = topRecord_";
  protected final String TEXT_88 = ".getValue(\"";
  protected final String TEXT_89 = "\");" + NL + "\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\tif (value_";
  protected final String TEXT_90 = " != null){" + NL + "\t\t\t\t\t\t\t \tString valueStr_";
  protected final String TEXT_91 = " = (String) value_";
  protected final String TEXT_92 = ";";
  protected final String TEXT_93 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_94 = ".";
  protected final String TEXT_95 = " = valueStr_";
  protected final String TEXT_96 = ";";
  protected final String TEXT_97 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_98 = ".";
  protected final String TEXT_99 = " = ParserUtils.parseTo_Date(valueStr_";
  protected final String TEXT_100 = ", ";
  protected final String TEXT_101 = ");";
  protected final String TEXT_102 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_103 = ".";
  protected final String TEXT_104 = " = valueStr_";
  protected final String TEXT_105 = ".getBytes();";
  protected final String TEXT_106 = "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_107 = ".";
  protected final String TEXT_108 = " = ParserUtils.parseTo_";
  protected final String TEXT_109 = "(valueStr_";
  protected final String TEXT_110 = ");";
  protected final String TEXT_111 = "\t\t\t" + NL + "\t\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_112 = ".";
  protected final String TEXT_113 = " = ";
  protected final String TEXT_114 = ";" + NL + "\t\t\t\t\t\t\t}";
  protected final String TEXT_115 = NL + "\t\t\t\t";
  protected final String TEXT_116 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

INode node = (INode)codeGenArgument.getArgument();

	boolean useProxy = ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
	String proxyHost = ElementParameterParser.getValue(node,"__PROXY_HOST__");
   	String proxyPort = ElementParameterParser.getValue(node,"__PROXY_PORT__");
   	String proxyUsername = ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
   	String proxyPassword = ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");
   	String batchSize = ElementParameterParser.getValue(node,"__BATCH_SIZE__");
	boolean needCompression = ("true").equals(ElementParameterParser.getValue(node,"__NEED_COMPRESSION__"));
String cid = node.getUniqueName();

   	String limit = ElementParameterParser.getValue(node,"__LIMIT__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(limit );
    stringBuffer.append(TEXT_4);
    
if(useProxy){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(proxyPort );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(proxyUsername );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_14);
    
}

    stringBuffer.append(TEXT_15);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);
	
	if (metadata != null) {
	
		List<IMetadataColumn> columnList = metadata.getListColumns();
		int nbSchemaColumns = columnList.size();			
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		// if output columns are defined
		if (nbSchemaColumns > 0 && outgoingConns != null && outgoingConns.size() > 0){
			boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
			String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
			
			String endpoint = ElementParameterParser.getValue(node, "__ENDPOINT__");
			String username = ElementParameterParser.getValue(node, "__USER__");
			String password = ElementParameterParser.getValue(node, "__PASS__");
			String timeout = ElementParameterParser.getValue(node,"__TIMEOUT__");
			String normalize_delimiter = ElementParameterParser.getValue(node, "__NORMALIZE_DELIMITER__");
			String columnname_delimiter = ElementParameterParser.getValue(node, "__COLUMNNAME_DELIMITER__");
			boolean queryAll = ("true").equals(ElementParameterParser.getValue(node,"__QUERY_ALL__"));
			String clientID = ElementParameterParser.getValue(node,"__CLIENT_ID__");
			

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    if(useExistingConn){
    stringBuffer.append(TEXT_18);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(connection);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(connection);
    stringBuffer.append(TEXT_27);
    }else{
    stringBuffer.append(TEXT_28);
    if(clientID!=null && !"".equals(clientID) && !"\"\"".equals(clientID)){
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(clientID);
    stringBuffer.append(TEXT_31);
    }
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(timeout);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(needCompression);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    }
    stringBuffer.append(TEXT_41);
    			
			boolean bIsManualQuery = ("true").equals(ElementParameterParser.getValue(node, "__MANUAL_QUERY__"));
			String sql = "";
			String condition = null;
			
			if (bIsManualQuery){

				sql = ElementParameterParser.getValue(node, "__QUERY__");
				sql = sql.replaceAll("\n", " ").replaceAll("\r", " ");

			} else{
				
				String modulename = ElementParameterParser.getValue(node, "__MODULENAME__").trim();
				String customModulename = ElementParameterParser.getValue(node, "__CUSTOM_MODULE__").trim();
				condition = ElementParameterParser.getValue(node, "__CONDITION__").trim();

				StringBuilder sb = new StringBuilder("\"select ");

				for (IMetadataColumn column: columnList){
					sb.append(column.getLabel());
					sb.append(",");
				}
				sb.deleteCharAt(sb.lastIndexOf(","));
				sb.append(" from ").append((("CustomModule").equals(modulename)) ? customModulename.replaceAll("\"","") : modulename);
				sb.append("\"");
				
				sql = sb.toString();
			}

			IConnection outgoingConn = outgoingConns.get(0);
			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { // start 1

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(sql);
    stringBuffer.append(TEXT_44);
    
				if(!("".equals(condition))) {

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(condition );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    
				}

    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    
			if(queryAll){

    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(batchSize);
    stringBuffer.append(TEXT_58);
    			
			}else{

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(batchSize);
    stringBuffer.append(TEXT_63);
    
			}

    stringBuffer.append(TEXT_64);
    stringBuffer.append(normalize_delimiter );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(columnname_delimiter );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
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
    
						for( int i = 0; i < columnList.size(); i++) {
						
							IMetadataColumn column = columnList.get(i);
							
							String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
							
							JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
							
							String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();

    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    
								if (javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) { // String or Object

    stringBuffer.append(TEXT_93);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_94);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    
								} else if(javaType == JavaTypesManager.DATE) { // Date

    stringBuffer.append(TEXT_97);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_98);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_101);
    
								} else if(javaType == JavaTypesManager.BYTE_ARRAY) { // byte[]

    stringBuffer.append(TEXT_102);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_103);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    
								} else  { // other

    stringBuffer.append(TEXT_106);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_107);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_108);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    
								}

    stringBuffer.append(TEXT_111);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_112);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_113);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate));
    stringBuffer.append(TEXT_114);
    
						}
												
					// ending port of for... (move to tSalesforceInput_end.javajet)
					
				}// end 1

    stringBuffer.append(TEXT_115);
    
		}
	}
}

    stringBuffer.append(TEXT_116);
    return stringBuffer.toString();
  }
}
