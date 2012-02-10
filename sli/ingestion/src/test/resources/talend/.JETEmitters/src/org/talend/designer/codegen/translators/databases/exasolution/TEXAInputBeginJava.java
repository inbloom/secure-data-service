package org.talend.designer.codegen.translators.databases.exasolution;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TEXAInputBeginJava
{
  protected static String nl;
  public static synchronized TEXAInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TEXAInputBeginJava result = new TEXAInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t    int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\t\t    ";
  protected final String TEXT_4 = NL + "\t\t        java.sql.Connection conn_";
  protected final String TEXT_5 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_6 = "\");" + NL + "\t\t        ";
  protected final String TEXT_7 = NL + "\t\t        java.lang.Class.forName(\"com.exasol.jdbc.EXADriver\");" + NL + "\t\t        ";
  protected final String TEXT_8 = "    " + NL + "\t\t            String url_";
  protected final String TEXT_9 = " = \"jdbc:exa:\" + ";
  protected final String TEXT_10 = " + \":\" + ";
  protected final String TEXT_11 = "+ \":schema=\" + ";
  protected final String TEXT_12 = "; " + NL + "\t\t            ";
  protected final String TEXT_13 = "  " + NL + "\t\t            String url_";
  protected final String TEXT_14 = " = \"jdbc:exa:\" + ";
  protected final String TEXT_15 = " + \":\" + ";
  protected final String TEXT_16 = " + \":schema=\" + ";
  protected final String TEXT_17 = " + \":\" + ";
  protected final String TEXT_18 = ";" + NL + "\t\t            ";
  protected final String TEXT_19 = NL + "\t\t        String dbUser_";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ";" + NL + "\t\t        String dbPwd_";
  protected final String TEXT_22 = " = ";
  protected final String TEXT_23 = ";" + NL + "\t\t        java.sql.Connection conn_";
  protected final String TEXT_24 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_25 = ",dbUser_";
  protected final String TEXT_26 = ",dbPwd_";
  protected final String TEXT_27 = ");" + NL + "\t\t        ";
  protected final String TEXT_28 = NL + "\t\t    java.sql.Statement stmt_";
  protected final String TEXT_29 = " = conn_";
  protected final String TEXT_30 = ".createStatement();" + NL + "\t\t    java.sql.ResultSet rs_";
  protected final String TEXT_31 = " = stmt_";
  protected final String TEXT_32 = ".executeQuery(";
  protected final String TEXT_33 = ");" + NL + "\t\t    java.sql.ResultSetMetaData rsmd_";
  protected final String TEXT_34 = " = rs_";
  protected final String TEXT_35 = ".getMetaData();" + NL + "\t\t    int colQtyInRs_";
  protected final String TEXT_36 = " = rsmd_";
  protected final String TEXT_37 = ".getColumnCount();" + NL + "\t\t    globalMap.put(\"";
  protected final String TEXT_38 = "_QUERY\",";
  protected final String TEXT_39 = ");" + NL + "\t\t    ";
  protected final String TEXT_40 = NL + "\t\t    String tmpContent_";
  protected final String TEXT_41 = " = null;" + NL + "\t\t    while (rs_";
  protected final String TEXT_42 = ".next()) {" + NL + "\t\t        nb_line_";
  protected final String TEXT_43 = "++;" + NL + "\t\t        ";
  protected final String TEXT_44 = " \t" + NL + "\t\t                    if(colQtyInRs_";
  protected final String TEXT_45 = " < ";
  protected final String TEXT_46 = ") { \t\t" + NL + "\t\t                        ";
  protected final String TEXT_47 = ".";
  protected final String TEXT_48 = "=";
  protected final String TEXT_49 = "; \t\t\t" + NL + "\t\t                    } else {" + NL + "\t\t                        ";
  protected final String TEXT_50 = NL + "\t\t                            tmpContent_";
  protected final String TEXT_51 = " = rs_";
  protected final String TEXT_52 = ".getString(";
  protected final String TEXT_53 = ");" + NL + "\t\t                            ";
  protected final String TEXT_54 = NL + "                                        if(tmpContent_";
  protected final String TEXT_55 = " != null) {" + NL + "                                            tmpContent_";
  protected final String TEXT_56 = " = tmpContent_";
  protected final String TEXT_57 = ";" + NL + "                                        }";
  protected final String TEXT_58 = NL + "\t\t                            if(tmpContent_";
  protected final String TEXT_59 = " != null && tmpContent_";
  protected final String TEXT_60 = ".length() > 0) {\t\t\t  \t" + NL + "\t\t                                ";
  protected final String TEXT_61 = ".";
  protected final String TEXT_62 = " = tmpContent_";
  protected final String TEXT_63 = ".charAt(0);\t\t\t  \t\t" + NL + "\t\t                            } else {\t\t\t  \t" + NL + "\t\t                                ";
  protected final String TEXT_64 = "\t\t\t  \t    " + NL + "\t\t                                    if(tmpContent_";
  protected final String TEXT_65 = " == null) {\t\t\t  \t   \t" + NL + "\t\t                                        ";
  protected final String TEXT_66 = ".";
  protected final String TEXT_67 = " = null;\t\t\t  \t\t\t" + NL + "\t\t                                    } else {\t\t\t  \t\t" + NL + "\t\t                                        ";
  protected final String TEXT_68 = ".";
  protected final String TEXT_69 = " = '\\0';\t\t\t  \t\t\t" + NL + "\t\t                                    }" + NL + "\t\t                                    ";
  protected final String TEXT_70 = "\t\t\t  \t\t" + NL + "\t\t                                    if(tmpContent_";
  protected final String TEXT_71 = ".equals(\"\")) {\t\t\t  \t\t" + NL + "\t\t                                        ";
  protected final String TEXT_72 = ".";
  protected final String TEXT_73 = " = '\\0';\t\t\t  \t\t\t" + NL + "\t\t                                    } else {\t\t\t  \t\t" + NL + "                        \t\t\t  \t\t\tthrow new RuntimeException(" + NL + "                        \t\t\t\t\t\t\t\"Value is empty for column : '";
  protected final String TEXT_74 = "' in '";
  protected final String TEXT_75 = "' connection, value is invalid or this column should be nullable or have a default value.\");\t\t\t\t\t\t\t" + NL + "\t\t                                    }\t\t\t  \t\t" + NL + "\t\t                                    ";
  protected final String TEXT_76 = NL + "\t\t                            }\t\t\t" + NL + "\t\t                            ";
  protected final String TEXT_77 = NL + "\t\t                            if(rs_";
  protected final String TEXT_78 = ".getTimestamp(";
  protected final String TEXT_79 = ") != null) {" + NL + "\t\t                                ";
  protected final String TEXT_80 = ".";
  protected final String TEXT_81 = " = new java.util.Date(rs_";
  protected final String TEXT_82 = ".getTimestamp(";
  protected final String TEXT_83 = ").getTime());" + NL + "\t\t                            } else {" + NL + "\t\t                                ";
  protected final String TEXT_84 = ".";
  protected final String TEXT_85 = " =  null;" + NL + "\t\t                            }" + NL + "\t\t                            ";
  protected final String TEXT_86 = NL + "\t\t                            ";
  protected final String TEXT_87 = ".";
  protected final String TEXT_88 = " = (List)rs_";
  protected final String TEXT_89 = ".getObject(";
  protected final String TEXT_90 = ");" + NL + "\t\t                            ";
  protected final String TEXT_91 = NL + "                                    tmpContent_";
  protected final String TEXT_92 = " = rs_";
  protected final String TEXT_93 = ".getString(";
  protected final String TEXT_94 = ");" + NL + "                                    if(tmpContent_";
  protected final String TEXT_95 = " != null) {";
  protected final String TEXT_96 = NL + "                                        ";
  protected final String TEXT_97 = ".";
  protected final String TEXT_98 = " = tmpContent_";
  protected final String TEXT_99 = ";" + NL + "                                    } else {";
  protected final String TEXT_100 = NL + "                                        ";
  protected final String TEXT_101 = ".";
  protected final String TEXT_102 = " = null;" + NL + "                                    }\t\t                            " + NL + "\t\t                            ";
  protected final String TEXT_103 = NL + "\t                                if(rs_";
  protected final String TEXT_104 = ".getObject(";
  protected final String TEXT_105 = ") != null) {" + NL + "\t                                    ";
  protected final String TEXT_106 = ".";
  protected final String TEXT_107 = " = rs_";
  protected final String TEXT_108 = ".get";
  protected final String TEXT_109 = "(";
  protected final String TEXT_110 = ");" + NL + "\t                                } else {" + NL + "\t                                    ";
  protected final String TEXT_111 = NL + "\t                                        ";
  protected final String TEXT_112 = ".";
  protected final String TEXT_113 = " = null;" + NL + "\t                                        ";
  protected final String TEXT_114 = NL + "\t                                        throw new RuntimeException(\"Null value in non-Nullable column\");" + NL + "\t                                        ";
  protected final String TEXT_115 = NL + "\t                                }" + NL + "\t                                ";
  protected final String TEXT_116 = NL + "\t\t                    } \t\t" + NL + "\t\t                    ";
  protected final String TEXT_117 = NL + "\t\t                            ";
  protected final String TEXT_118 = ".";
  protected final String TEXT_119 = "=";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = ";" + NL + "\t\t                            ";
  protected final String TEXT_122 = NL;
  protected final String TEXT_123 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();	
	String dbhost = ElementParameterParser.getValue(node, "__HOST__");
	String dbschema= ElementParameterParser.getValue(node, "__DB_SCHEMA__");
	String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
	String dbuser= ElementParameterParser.getValue(node, "__USER__");
	String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
	String dbport= ElementParameterParser.getValue(node, "__PORT__");
	String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    boolean whetherTrimAllCol = ElementParameterParser.getValue(node, "__TRIM_ALL_COLUMN__").equals("true");
    List<Map<String, String>> trimColumnList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__TRIM_COLUMN__");
    	   
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas != null) && (metadatas.size() > 0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata != null) {
		    
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
		    String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
		    if(useExistingConn.equals("true")) {
		        String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
		        String conn = "conn_" + connection;
		        
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_6);
    
		    } else {
		        
    stringBuffer.append(TEXT_7);
    
		        if(dbproperties == null || dbproperties.equals("\"\"") || dbproperties.equals("")) {        
		            
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(dbschema);
    stringBuffer.append(TEXT_12);
     
		        } else {
		            
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(dbschema);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_18);
    
		        }
		        
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    
		    }
		    
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_39);
    
		    List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
		    List<IMetadataColumn> columnList = metadata.getListColumns();
		    
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    
		        if(conns != null && conns.size() > 0) {
		            IConnection conn = conns.get(0);
		            String firstConnName = conn.getName();
		            int currentColNo = 1;
		            for(IMetadataColumn column : columnList) { 	
                        boolean whetherTrimCol = false;
                        if((trimColumnList != null && trimColumnList.size() > 0) && !whetherTrimAllCol) {
                            for(Map<String, String> trimColumn : trimColumnList) {
                                if(column.getLabel().equals(trimColumn.get("SCHEMA_COLUMN"))) {
                                    if(trimColumn.get("TRIM").equals("true")) {
                                        whetherTrimCol = true;
                                        break;
                                    }
                                }
                            }
                        }
                        String trimMethod = "";
                        if(whetherTrimAllCol || whetherTrimCol) {
                            trimMethod = ".trim()";
                        }                        
		                String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
		                String defVal = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate); 	
		                if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
		                    
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_48);
    stringBuffer.append(defVal);
    stringBuffer.append(TEXT_49);
    
		                        if(typeToGenerate.equals("byte[]")) {
		                            typeToGenerate = "Bytes";
		                        } else if(typeToGenerate.equals("java.util.Date")) {
		                            typeToGenerate = "Timestamp";
		                        } else if(typeToGenerate.equals("Integer")) {
		                            typeToGenerate = "Int";
		                        } else {
		                            typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
		                        }		  
		                        if(typeToGenerate.equals("Char") || typeToGenerate.equals("Character")) {
		                            
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_53);
    
                                    if(whetherTrimAllCol || whetherTrimCol) {
                                        
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(trimMethod);
    stringBuffer.append(TEXT_57);
    
                                    }		                            
		                            
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    
		                                if(typeToGenerate.equals("Character")) {
		                                    
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_67);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_69);
    
		                                } else {
		                                    
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_73);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_74);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_75);
    
		                                }
		                                
    stringBuffer.append(TEXT_76);
    
		                        } else if(typeToGenerate.equals("Timestamp")) {
		                            
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_85);
    
		                        } else if (typeToGenerate.equals("List")) {
		                            
    stringBuffer.append(TEXT_86);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_90);
    
		                        } else if(typeToGenerate.equals("String")) {
		                            
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(trimMethod);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_102);
    
		                        } else {	  
		                            
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_110);
    
	                                    if(column.isNullable()) {
	                                        
    stringBuffer.append(TEXT_111);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_113);
    
	                                    } else {
	                                        
    stringBuffer.append(TEXT_114);
        
	                                    }
	                                    
    stringBuffer.append(TEXT_115);
    
		                        }
		                        
    stringBuffer.append(TEXT_116);
      
		                    currentColNo++;
		                }
		            }
		            if(conns.size() > 1) {
		                for(int connNO = 1 ; connNO < conns.size() ; connNO++) {
		                    IConnection conn2 = conns.get(connNO);
		                    if((conn2.getName().compareTo(firstConnName) != 0) && (conn2.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
		                        for(IMetadataColumn column : columnList) {
		                            
    stringBuffer.append(TEXT_117);
    stringBuffer.append(conn2.getName());
    stringBuffer.append(TEXT_118);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_119);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_121);
     
		                        }
		                    }
		                }
		            }
		        }
		}
	}

    stringBuffer.append(TEXT_122);
    stringBuffer.append(TEXT_123);
    return stringBuffer.toString();
  }
}
