package org.talend.designer.codegen.translators.databases.informix;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TInformixSPBeginJava
{
  protected static String nl;
  public static synchronized TInformixSPBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TInformixSPBeginJava result = new TInformixSPBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.sql.Connection connection_";
  protected final String TEXT_3 = " = (java.sql.Connection) globalMap.get(\"";
  protected final String TEXT_4 = "\");";
  protected final String TEXT_5 = NL + "\tjava.lang.Class.forName(\"com.informix.jdbc.IfxDriver\");";
  protected final String TEXT_6 = NL + "    \tString url_";
  protected final String TEXT_7 = " = \"jdbc:informix-sqli://\" + ";
  protected final String TEXT_8 = " + \":\" + ";
  protected final String TEXT_9 = " + \"/\" + ";
  protected final String TEXT_10 = " + \":informixserver=\" + ";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = NL + "\t\tString url_";
  protected final String TEXT_13 = " = \"jdbc:informix-sqli://\" + ";
  protected final String TEXT_14 = " + \":\" + ";
  protected final String TEXT_15 = " + \"/\" + ";
  protected final String TEXT_16 = " + \":informixserver=\" + ";
  protected final String TEXT_17 = " + \";\" + ";
  protected final String TEXT_18 = ";";
  protected final String TEXT_19 = NL + "\tjava.sql.Connection connection_";
  protected final String TEXT_20 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_21 = ", ";
  protected final String TEXT_22 = ", ";
  protected final String TEXT_23 = ");" + NL + "\t";
  protected final String TEXT_24 = NL + "\tif(connection_";
  protected final String TEXT_25 = ".getAutoCommit()) {" + NL + "\t    connection_";
  protected final String TEXT_26 = ".setAutoCommit(false);" + NL + "\t}";
  protected final String TEXT_27 = NL + "java.sql.CallableStatement statement_";
  protected final String TEXT_28 = " = connection_";
  protected final String TEXT_29 = ".prepareCall(\"{";
  protected final String TEXT_30 = "call \" + ";
  protected final String TEXT_31 = " + \"(";
  protected final String TEXT_32 = "?";
  protected final String TEXT_33 = ",?";
  protected final String TEXT_34 = ")}\");" + NL + "" + NL + "java.sql.Timestamp tmpDate_";
  protected final String TEXT_35 = ";" + NL + "String tmpString_";
  protected final String TEXT_36 = ";";
  protected final String TEXT_37 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode) codeGenArgument.getArgument();
String cid = node.getUniqueName();
String dbhost = ElementParameterParser.getValue(node, "__HOST__");
String dbport = ElementParameterParser.getValue(node, "__PORT__");
String dbname = ElementParameterParser.getValue(node, "__DBNAME__");
String dbserver = ElementParameterParser.getValue(node, "__DBSERVER__");
String dbuser = ElementParameterParser.getValue(node, "__USER__");
String dbpwd  = ElementParameterParser.getValue(node, "__PASS__");
String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
String spName = ElementParameterParser.getValue(node, "__SP_NAME__");
boolean isFunction = ("true").equals(ElementParameterParser.getValue(node, "__IS_FUNCTION__"));
List<Map<String, String>> spArgs = (List<Map<String,String>>) ElementParameterParser.getObjectValue(node, "__SP_ARGS__");
boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
boolean useTransaction = ("true").equals(ElementParameterParser.getValue(node,"__USE_TRANSACTION__"));
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_23);
    
}

	if (useTransaction) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    
	}

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(isFunction ? "? = " : "");
    stringBuffer.append(TEXT_30);
    stringBuffer.append(spName);
    stringBuffer.append(TEXT_31);
    
boolean isFirstArg = true;
for (int i = 0; i < spArgs.size(); i++) {
	if(!("RECORDSET").equals(spArgs.get(i).get("TYPE"))){
		if(isFirstArg){
			
    stringBuffer.append(TEXT_32);
    
			isFirstArg=false;
		}else{
			
    stringBuffer.append(TEXT_33);
    
		}
	}
}

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(TEXT_37);
    return stringBuffer.toString();
  }
}
