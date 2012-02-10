package org.talend.designer.codegen.translators.databases.db_jdbc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TJDBCRowBeginJava
{
  protected static String nl;
  public static synchronized TJDBCRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJDBCRowBeginJava result = new TJDBCRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.sql.Connection connection_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");" + NL + "\t";
  protected final String TEXT_5 = NL + "    java.lang.Class.forName(";
  protected final String TEXT_6 = ");" + NL + "    String connectionString_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "    java.sql.Connection connection_";
  protected final String TEXT_9 = " = java.sql.DriverManager.getConnection(connectionString_";
  protected final String TEXT_10 = ", ";
  protected final String TEXT_11 = ", ";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL;
  protected final String TEXT_14 = NL + "        connection_";
  protected final String TEXT_15 = ".setAutoCommit(false);    \t" + NL + "    \tint commitEvery_";
  protected final String TEXT_16 = " = ";
  protected final String TEXT_17 = ";" + NL + "    \tint commitCounter_";
  protected final String TEXT_18 = " = 0;" + NL + "    \t";
  protected final String TEXT_19 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_20 = " = connection_";
  protected final String TEXT_21 = ".prepareStatement(";
  protected final String TEXT_22 = ");\t";
  protected final String TEXT_23 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_24 = " = connection_";
  protected final String TEXT_25 = ".createStatement();";
  protected final String TEXT_26 = NL + "String query_";
  protected final String TEXT_27 = " = \"\";" + NL + "boolean whetherReject_";
  protected final String TEXT_28 = " = false;";
  protected final String TEXT_29 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode) codeGenArgument.getArgument();
String cid = node.getUniqueName();

String driverJar = ElementParameterParser.getValue(node, "__DRIVER_JAR__");
String driverClass = ElementParameterParser.getValue(node, "__DRIVER_CLASS__");
String jdbcUrl = ElementParameterParser.getValue(node, "__URL__");
String dbuser = ElementParameterParser.getValue(node, "__USER__");
String dbpwd = ElementParameterParser.getValue(node, "__PASS__");
String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    
String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
if(("true").equals(useExistingConn)){
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String conn = "conn_" + connection;
	
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_4);
    
} else {       
    
    stringBuffer.append(TEXT_5);
    stringBuffer.append(driverClass );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(jdbcUrl );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(dbuser );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(dbpwd );
    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    
if(!("true").equals(useExistingConn)) {
    if (!("").equals(commitEvery) && !("0").equals(commitEvery)) {
    	
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    
    }
}

    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_22);
    
	} else {

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
	}

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(TEXT_29);
    return stringBuffer.toString();
  }
}
