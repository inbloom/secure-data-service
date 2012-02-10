package org.talend.designer.codegen.translators.databases.postgresplus;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TPostgresPlusRowBeginJava
{
  protected static String nl;
  public static synchronized TPostgresPlusRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPostgresPlusRowBeginJava result = new TPostgresPlusRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "java.sql.Connection conn_";
  protected final String TEXT_3 = " = null;";
  protected final String TEXT_4 = NL + "    conn_";
  protected final String TEXT_5 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_6 = "\");";
  protected final String TEXT_7 = NL + "    java.lang.Class.forName(\"org.postgresql.Driver\");    " + NL + "    String url_";
  protected final String TEXT_8 = " = \"jdbc:postgresql://\"+";
  protected final String TEXT_9 = "+\":\"+";
  protected final String TEXT_10 = "+\"/\"+";
  protected final String TEXT_11 = ";    " + NL + "    String dbUser_";
  protected final String TEXT_12 = " = ";
  protected final String TEXT_13 = ";    " + NL + "    String dbPwd_";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ";    " + NL + "    conn_";
  protected final String TEXT_16 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_17 = ",dbUser_";
  protected final String TEXT_18 = ",dbPwd_";
  protected final String TEXT_19 = "); ";
  protected final String TEXT_20 = NL;
  protected final String TEXT_21 = NL + "        conn_";
  protected final String TEXT_22 = ".setAutoCommit(false);        " + NL + "        int commitEvery_";
  protected final String TEXT_23 = " = ";
  protected final String TEXT_24 = ";        " + NL + "        int commitCounter_";
  protected final String TEXT_25 = " = 0;        ";
  protected final String TEXT_26 = NL;
  protected final String TEXT_27 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_28 = " = conn_";
  protected final String TEXT_29 = ".prepareStatement(";
  protected final String TEXT_30 = ");\t";
  protected final String TEXT_31 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_32 = " = conn_";
  protected final String TEXT_33 = ".createStatement();";
  protected final String TEXT_34 = NL + "String query_";
  protected final String TEXT_35 = " = \"\";" + NL + "boolean whetherReject_";
  protected final String TEXT_36 = " = false;";
  protected final String TEXT_37 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    
    String dbhost = ElementParameterParser.getValue(node, "__HOST__");
    String dbport = ElementParameterParser.getValue(node, "__PORT__");
    String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
    String dbuser= ElementParameterParser.getValue(node, "__USER__");
    String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
}

    stringBuffer.append(TEXT_20);
    
if(!("true").equals(useExistingConn)) {
    if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
        
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
    }
}

    stringBuffer.append(TEXT_26);
    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_30);
    
	} else {

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    
	}

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(TEXT_37);
    return stringBuffer.toString();
  }
}
