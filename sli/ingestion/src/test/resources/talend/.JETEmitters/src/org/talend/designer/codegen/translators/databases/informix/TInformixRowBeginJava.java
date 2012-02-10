package org.talend.designer.codegen.translators.databases.informix;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TInformixRowBeginJava
{
  protected static String nl;
  public static synchronized TInformixRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TInformixRowBeginJava result = new TInformixRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.sql.Connection conn_";
  protected final String TEXT_3 = " = (java.sql.Connection) globalMap.get(\"";
  protected final String TEXT_4 = "\");";
  protected final String TEXT_5 = NL + "\tjava.lang.Class.forName(\"com.informix.jdbc.IfxDriver\");" + NL;
  protected final String TEXT_6 = NL + "    String url_";
  protected final String TEXT_7 = " = \"jdbc:informix-sqli://\" + ";
  protected final String TEXT_8 = " + \":\" + ";
  protected final String TEXT_9 = " + \"/\" + ";
  protected final String TEXT_10 = " + \":informixserver=\" + ";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = NL + "    String url_";
  protected final String TEXT_13 = " = \"jdbc:informix-sqli://\" + ";
  protected final String TEXT_14 = " + \":\" + ";
  protected final String TEXT_15 = " + \"/\" + ";
  protected final String TEXT_16 = " + \":informixserver=\" + ";
  protected final String TEXT_17 = " + \";\" + ";
  protected final String TEXT_18 = ";";
  protected final String TEXT_19 = NL + "\tString dbUser_";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ";" + NL + "\tString dbPwd_";
  protected final String TEXT_22 = " = ";
  protected final String TEXT_23 = ";" + NL + "\tjava.sql.Connection conn_";
  protected final String TEXT_24 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_25 = ",dbUser_";
  protected final String TEXT_26 = ",dbPwd_";
  protected final String TEXT_27 = ");";
  protected final String TEXT_28 = NL + NL + "boolean whetherReject_";
  protected final String TEXT_29 = " = false;";
  protected final String TEXT_30 = NL + NL + "int commitEvery_";
  protected final String TEXT_31 = " = ";
  protected final String TEXT_32 = ";" + NL + "" + NL + "int commitCounter_";
  protected final String TEXT_33 = " = 0;" + NL + "" + NL + "conn_";
  protected final String TEXT_34 = ".setAutoCommit(false);";
  protected final String TEXT_35 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_36 = " = conn_";
  protected final String TEXT_37 = ".prepareStatement(";
  protected final String TEXT_38 = ");\t";
  protected final String TEXT_39 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_40 = " = conn_";
  protected final String TEXT_41 = ".createStatement();";
  protected final String TEXT_42 = NL + "String query_";
  protected final String TEXT_43 = " = \"\";";
  protected final String TEXT_44 = NL;

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
	String dbserver = ElementParameterParser.getValue(node, "__DBSERVER__");
	String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
	String dbuser= ElementParameterParser.getValue(node, "__USER__");
	String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    boolean useTransaction = ("true").equals(ElementParameterParser.getValue(node,"__USE_TRANSACTION__"));
    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
    
    boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
    
	if (useExistingConnection) {
		String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
		String connectionName = "conn_" + connection;

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_4);
    
	} else {

    stringBuffer.append(TEXT_5);
    
		if(dbproperties == null || ("\"\"").equals(dbproperties) || ("").equals(dbproperties)) {

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(dbserver);
    stringBuffer.append(TEXT_11);
    
		} else {

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(dbserver);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    
if(useTransaction && !("").equals(commitEvery)&&!("0").equals(commitEvery)){

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    
}

    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_38);
    
	} else {

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    
	}

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(TEXT_44);
    return stringBuffer.toString();
  }
}
