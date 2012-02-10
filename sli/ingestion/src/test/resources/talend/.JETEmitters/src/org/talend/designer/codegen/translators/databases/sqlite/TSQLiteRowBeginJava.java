package org.talend.designer.codegen.translators.databases.sqlite;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSQLiteRowBeginJava
{
  protected static String nl;
  public static synchronized TSQLiteRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSQLiteRowBeginJava result = new TSQLiteRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.sql.Connection conn_";
  protected final String TEXT_3 = " = null;";
  protected final String TEXT_4 = NL + "    conn_";
  protected final String TEXT_5 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_6 = "\");";
  protected final String TEXT_7 = NL + "    java.lang.Class.forName(\"";
  protected final String TEXT_8 = "\");" + NL + "    String url_";
  protected final String TEXT_9 = " = \"jdbc:sqlite:\" + \"/\" + ";
  protected final String TEXT_10 = ";" + NL + "    conn_";
  protected final String TEXT_11 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL;
  protected final String TEXT_14 = NL + "        conn_";
  protected final String TEXT_15 = ".setAutoCommit(false);" + NL + "        int commitEvery_";
  protected final String TEXT_16 = " = ";
  protected final String TEXT_17 = ";    " + NL + "        int commitCounter_";
  protected final String TEXT_18 = " = 0;";
  protected final String TEXT_19 = NL;
  protected final String TEXT_20 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_21 = " = conn_";
  protected final String TEXT_22 = ".prepareStatement(";
  protected final String TEXT_23 = ");\t";
  protected final String TEXT_24 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_25 = " = conn_";
  protected final String TEXT_26 = ".createStatement();";
  protected final String TEXT_27 = NL + "String query_";
  protected final String TEXT_28 = " = \"\";" + NL + "boolean whetherReject_";
  protected final String TEXT_29 = " = false;";
  protected final String TEXT_30 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    String javaDbDriver   = "org.sqlite.JDBC";
    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
if(("true").equals(useExistingConn)) {
    String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
    String conn = "conn_" + connection;
    
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_6);
    
} else {
    
    stringBuffer.append(TEXT_7);
    stringBuffer.append(javaDbDriver );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    
if(!("true").equals(useExistingConn)) {
    if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
        
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

    stringBuffer.append(TEXT_19);
    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_23);
    
	} else {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    
	}

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(TEXT_30);
    return stringBuffer.toString();
  }
}
