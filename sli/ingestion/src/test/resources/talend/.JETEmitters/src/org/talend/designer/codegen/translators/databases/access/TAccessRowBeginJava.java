package org.talend.designer.codegen.translators.databases.access;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TAccessRowBeginJava
{
  protected static String nl;
  public static synchronized TAccessRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAccessRowBeginJava result = new TAccessRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "boolean whetherReject_";
  protected final String TEXT_3 = " = false;" + NL + "java.sql.Connection conn_";
  protected final String TEXT_4 = " = null;";
  protected final String TEXT_5 = NL + "\tconn_";
  protected final String TEXT_6 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_7 = "\");" + NL + "\t";
  protected final String TEXT_8 = "\t" + NL + "java.lang.Class.forName(\"sun.jdbc.odbc.JdbcOdbcDriver\");" + NL;
  protected final String TEXT_9 = NL + "\tString url_";
  protected final String TEXT_10 = " = \"jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=\" + ";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = NL + "\tString url_";
  protected final String TEXT_13 = " = \"jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=\" + ";
  protected final String TEXT_14 = ";";
  protected final String TEXT_15 = NL + "\tString userName_";
  protected final String TEXT_16 = " = ";
  protected final String TEXT_17 = ";" + NL + "\tString password_";
  protected final String TEXT_18 = " = ";
  protected final String TEXT_19 = ";" + NL;
  protected final String TEXT_20 = NL + "\tconn_";
  protected final String TEXT_21 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_22 = ", userName_";
  protected final String TEXT_23 = ", password_";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = NL + "\tString atnParams_";
  protected final String TEXT_26 = " = ";
  protected final String TEXT_27 = ";" + NL + "\tatnParams_";
  protected final String TEXT_28 = " = atnParams_";
  protected final String TEXT_29 = ".replaceAll(\"&\", \"\\n\");" + NL + "\tjava.util.Properties atnParamsPrope_";
  protected final String TEXT_30 = " = new java.util.Properties();" + NL + "\tatnParamsPrope_";
  protected final String TEXT_31 = ".put(\"user\",userName_";
  protected final String TEXT_32 = ");" + NL + "\tatnParamsPrope_";
  protected final String TEXT_33 = ".put(\"password\",password_";
  protected final String TEXT_34 = ");" + NL + "\tatnParamsPrope_";
  protected final String TEXT_35 = ".load(new java.io.ByteArrayInputStream(atnParams_";
  protected final String TEXT_36 = ".getBytes()));" + NL + "  " + NL + "\tconn_";
  protected final String TEXT_37 = " =  java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_38 = ", atnParamsPrope_";
  protected final String TEXT_39 = ");";
  protected final String TEXT_40 = NL;
  protected final String TEXT_41 = NL + "\t" + NL + "\tint commitEvery_";
  protected final String TEXT_42 = " = ";
  protected final String TEXT_43 = ";" + NL + "\t" + NL + "\tint commitCounter_";
  protected final String TEXT_44 = " = 0;" + NL + "\t" + NL + "\tconn_";
  protected final String TEXT_45 = ".setAutoCommit(false);" + NL + "\t";
  protected final String TEXT_46 = NL;
  protected final String TEXT_47 = NL + "\t\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_48 = " = conn_";
  protected final String TEXT_49 = ".prepareStatement(";
  protected final String TEXT_50 = ");\t";
  protected final String TEXT_51 = NL + "\t\tjava.sql.Statement stmt_";
  protected final String TEXT_52 = " = conn_";
  protected final String TEXT_53 = ".createStatement();";
  protected final String TEXT_54 = NL + "String query_";
  protected final String TEXT_55 = " = \"\";";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
	
	String username = ElementParameterParser.getValue(node, "__USER__");
	
	String password = ElementParameterParser.getValue(node, "__PASS__");
	
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    
    String dbversion = ElementParameterParser.getValue(node, "__DB_VERSION__");

    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
	
	String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
if(useExistingConnection) {
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String conn = "conn_" + connection;
	
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_7);
    
} else {   
    
    stringBuffer.append(TEXT_8);
    if ("Access_2003".equalsIgnoreCase(dbversion)) {

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_11);
    
}else if ("Access_2007".equalsIgnoreCase(dbversion)) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_14);
    
}
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_19);
    
	if(dbproperties == null || ("\"\"").equals(dbproperties) || ("").equals(dbproperties)) {

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    
	} else {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    
	}

    stringBuffer.append(TEXT_40);
    
	}

    
if(!useExistingConnection) {
	if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){
	
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    
	}
}

    stringBuffer.append(TEXT_46);
    
	if(usePrepareStatement){

    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_50);
    
	}else{

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    
	}

    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    return stringBuffer.toString();
  }
}
