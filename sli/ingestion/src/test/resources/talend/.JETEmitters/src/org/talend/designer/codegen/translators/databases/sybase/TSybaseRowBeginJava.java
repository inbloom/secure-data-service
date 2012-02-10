package org.talend.designer.codegen.translators.databases.sybase;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSybaseRowBeginJava
{
  protected static String nl;
  public static synchronized TSybaseRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSybaseRowBeginJava result = new TSybaseRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.sql.Connection conn_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");" + NL + "\t";
  protected final String TEXT_5 = NL + "    java.lang.Class.forName(\"com.sybase.jdbc3.jdbc.SybDriver\");" + NL + "    ";
  protected final String TEXT_6 = NL + "\t\tString url_";
  protected final String TEXT_7 = " = \"jdbc:sybase:Tds:\" + ";
  protected final String TEXT_8 = " + \":\" + ";
  protected final String TEXT_9 = " + \"/\" + ";
  protected final String TEXT_10 = "; ";
  protected final String TEXT_11 = NL + "\t\tString url_";
  protected final String TEXT_12 = " = \"jdbc:sybase:Tds:\" + ";
  protected final String TEXT_13 = " + \":\" + ";
  protected final String TEXT_14 = " + \"/\" + ";
  protected final String TEXT_15 = " + \"?\" + ";
  protected final String TEXT_16 = ";";
  protected final String TEXT_17 = NL + "    String dbUser_";
  protected final String TEXT_18 = " = ";
  protected final String TEXT_19 = ";" + NL + "    String dbPwd_";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ";" + NL + "    java.sql.Connection conn_";
  protected final String TEXT_22 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_23 = ",dbUser_";
  protected final String TEXT_24 = ",dbPwd_";
  protected final String TEXT_25 = ");";
  protected final String TEXT_26 = NL + "String tableName_";
  protected final String TEXT_27 = " = ";
  protected final String TEXT_28 = " ;" + NL + "String dbschema_";
  protected final String TEXT_29 = " = ";
  protected final String TEXT_30 = ";";
  protected final String TEXT_31 = NL + "        conn_";
  protected final String TEXT_32 = ".setAutoCommit(false);        " + NL + "        int commitEvery_";
  protected final String TEXT_33 = " = ";
  protected final String TEXT_34 = ";    " + NL + "        int commitCounter_";
  protected final String TEXT_35 = " = 0;    ";
  protected final String TEXT_36 = NL + NL + "if (dbschema_";
  protected final String TEXT_37 = " != null && dbschema_";
  protected final String TEXT_38 = ".trim().length() != 0) {" + NL + "\ttableName_";
  protected final String TEXT_39 = " = dbschema_";
  protected final String TEXT_40 = " + \".\" + ";
  protected final String TEXT_41 = ";" + NL + "}" + NL;
  protected final String TEXT_42 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_43 = " = conn_";
  protected final String TEXT_44 = ".prepareStatement(";
  protected final String TEXT_45 = ");\t";
  protected final String TEXT_46 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_47 = " = conn_";
  protected final String TEXT_48 = ".createStatement();";
  protected final String TEXT_49 = NL + "    stmt_";
  protected final String TEXT_50 = ".execute(\"SET IDENTITY_INSERT \"+ tableName_";
  protected final String TEXT_51 = " +\" ON\");";
  protected final String TEXT_52 = NL + "String query_";
  protected final String TEXT_53 = " = \"\";" + NL + "boolean whetherReject_";
  protected final String TEXT_54 = " = false;";
  protected final String TEXT_55 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String dbServer = ElementParameterParser.getValue(node, "__SERVER__");
String dbhost = ElementParameterParser.getValue(node, "__HOST__");
String dbport = ElementParameterParser.getValue(node, "__PORT__");
String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
String dbuser= ElementParameterParser.getValue(node, "__USER__");
String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
String tableName = ElementParameterParser.getValue(node,"__TABLE__");
String identityInsert = ElementParameterParser.getValue(node, "__IDENTITY_INSERT__");
String dbschema = ElementParameterParser.getValue(node, "__DB_SCHEMA__");
boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	dbquery = dbquery.replaceAll("\n"," ");
    	dbquery = dbquery.replaceAll("\r"," ");
    
boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
if(useExistingConn) {
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String conn = "conn_" + connection;
	
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_4);
    
} else {
    
    stringBuffer.append(TEXT_5);
    
	if(dbproperties == null || ("\"\"").equals(dbproperties) || ("").equals(dbproperties)) {

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dbServer);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_10);
    
	} else {

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(dbServer);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_16);
    
	}

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
}

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(dbschema);
    stringBuffer.append(TEXT_30);
    
if(!useExistingConn) {
    if(!("").equals(commitEvery) && !("0").equals(commitEvery)){
        
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(commitEvery );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    
    }
}

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_41);
    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_45);
    
	} else {

    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    
	}

    
if(("true").equals(identityInsert)){
    
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    
}

    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(TEXT_55);
    return stringBuffer.toString();
  }
}
