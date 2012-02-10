package org.talend.designer.codegen.translators.databases.ms_sql_server;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TMSSqlTableListBeginJava
{
  protected static String nl;
  public static synchronized TMSSqlTableListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSSqlTableListBeginJava result = new TMSSqlTableListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.sql.Connection conn_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");" + NL + "" + NL + "String dbSchema_";
  protected final String TEXT_5 = " = (String)globalMap.get(\"";
  protected final String TEXT_6 = "\");" + NL + "" + NL + "String whereClause_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "" + NL + "String query_";
  protected final String TEXT_9 = " = \"\";" + NL + "" + NL + "if(dbSchema_";
  protected final String TEXT_10 = " != null && dbSchema_";
  protected final String TEXT_11 = ".length() > 0){" + NL + "\t" + NL + "\tquery_";
  protected final String TEXT_12 = " = \"SELECT table_name FROM information_schema.tables WHERE table_type='BASE TABLE' AND table_schema='\" + dbSchema_";
  protected final String TEXT_13 = " + \"'\";" + NL + "\t" + NL + "}else{" + NL + "\t" + NL + "\tquery_";
  protected final String TEXT_14 = " = \"SELECT table_name FROM information_schema.tables WHERE table_type='BASE TABLE'\";" + NL + "\t" + NL + "}" + NL + "if(whereClause_";
  protected final String TEXT_15 = " != null && whereClause_";
  protected final String TEXT_16 = ".length() > 0){" + NL + "" + NL + "\tquery_";
  protected final String TEXT_17 = " = query_";
  protected final String TEXT_18 = " + \" AND (\" + whereClause_";
  protected final String TEXT_19 = " + \") ORDER BY table_name ASC\";" + NL + "" + NL + "}else{" + NL + "" + NL + "\tquery_";
  protected final String TEXT_20 = " = query_";
  protected final String TEXT_21 = " + \" ORDER BY table_name ASC\";" + NL + "" + NL + "}" + NL + "" + NL + "int nb_table_";
  protected final String TEXT_22 = " = 0;" + NL + "" + NL + "java.sql.Statement stmt2_";
  protected final String TEXT_23 = " = conn_";
  protected final String TEXT_24 = ".createStatement();" + NL + "" + NL + "java.sql.ResultSet rs_";
  protected final String TEXT_25 = " = stmt2_";
  protected final String TEXT_26 = ".executeQuery(query_";
  protected final String TEXT_27 = ");" + NL + "" + NL + "while (rs_";
  protected final String TEXT_28 = ".next()) {" + NL + "" + NL + "\tString currentTableName_";
  protected final String TEXT_29 = " = rs_";
  protected final String TEXT_30 = ".getString(1); " + NL + "" + NL + "\tnb_table_";
  protected final String TEXT_31 = "++;" + NL + "        " + NL + "\tglobalMap.put(\"";
  protected final String TEXT_32 = "_CURRENT_TABLE\", currentTableName_";
  protected final String TEXT_33 = ");";
  protected final String TEXT_34 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();

    String cid = node.getUniqueName();
    
    String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
    
    String conn = "conn_" + connection;
    
    String dbSchema = "dbschema_" + connection;
    
    String whereClause = ElementParameterParser.getValue(node,"__WHERE_CLAUSE__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(dbSchema );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(whereClause );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(TEXT_34);
    return stringBuffer.toString();
  }
}
