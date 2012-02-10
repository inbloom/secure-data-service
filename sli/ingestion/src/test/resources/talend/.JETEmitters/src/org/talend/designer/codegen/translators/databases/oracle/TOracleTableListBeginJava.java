package org.talend.designer.codegen.translators.databases.oracle;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TOracleTableListBeginJava
{
  protected static String nl;
  public static synchronized TOracleTableListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOracleTableListBeginJava result = new TOracleTableListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.sql.Connection conn_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");" + NL + "" + NL + "String whereClause_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "" + NL + "String query_";
  protected final String TEXT_7 = " = \"\";" + NL + "" + NL + "if(whereClause_";
  protected final String TEXT_8 = " != null && whereClause_";
  protected final String TEXT_9 = ".length() > 0){" + NL + "" + NL + "\tquery_";
  protected final String TEXT_10 = " = \"SELECT tname FROM tab WHERE tabtype='TABLE' AND (\" + whereClause_";
  protected final String TEXT_11 = " + \") ORDER BY tname ASC\";" + NL + "" + NL + "}else{" + NL + "" + NL + "\tquery_";
  protected final String TEXT_12 = " = \"SELECT tname FROM tab WHERE tabtype='TABLE' ORDER BY tname ASC\";" + NL + "" + NL + "}" + NL + "" + NL + "int nb_table_";
  protected final String TEXT_13 = " = 0;" + NL + "" + NL + "java.sql.Statement stmt2_";
  protected final String TEXT_14 = " = conn_";
  protected final String TEXT_15 = ".createStatement();" + NL + "" + NL + "java.sql.ResultSet rs_";
  protected final String TEXT_16 = " = stmt2_";
  protected final String TEXT_17 = ".executeQuery(query_";
  protected final String TEXT_18 = ");" + NL + "" + NL + "while (rs_";
  protected final String TEXT_19 = ".next()) {" + NL + "" + NL + "\tString currentTableName_";
  protected final String TEXT_20 = " = rs_";
  protected final String TEXT_21 = ".getString(1); " + NL + "" + NL + "\tnb_table_";
  protected final String TEXT_22 = "++;" + NL + "        " + NL + "\tglobalMap.put(\"";
  protected final String TEXT_23 = "_CURRENT_TABLE\", currentTableName_";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();

    String cid = node.getUniqueName();
    
    String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
    
    String conn = "conn_" + connection;
    
    String schema = "dbschema_" + connection;
    
    String whereClause = ElementParameterParser.getValue(node,"__WHERE_CLAUSE__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(whereClause );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(TEXT_25);
    return stringBuffer.toString();
  }
}
