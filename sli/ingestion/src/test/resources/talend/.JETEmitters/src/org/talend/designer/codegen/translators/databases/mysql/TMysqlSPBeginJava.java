package org.talend.designer.codegen.translators.databases.mysql;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TMysqlSPBeginJava
{
  protected static String nl;
  public static synchronized TMysqlSPBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMysqlSPBeginJava result = new TMysqlSPBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "    java.sql.Connection connection_";
  protected final String TEXT_4 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_5 = "\");    ";
  protected final String TEXT_6 = NL + "    String dbProperties_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "    String connectionString_";
  protected final String TEXT_9 = " = null;" + NL + "    if(dbProperties_";
  protected final String TEXT_10 = " == null || dbProperties_";
  protected final String TEXT_11 = ".trim().length() == 0) {" + NL + "        connectionString_";
  protected final String TEXT_12 = " = \"jdbc:mysql://\" + ";
  protected final String TEXT_13 = " + \":\" + ";
  protected final String TEXT_14 = " + \"/\" + ";
  protected final String TEXT_15 = ";" + NL + "    } else {" + NL + "        connectionString_";
  protected final String TEXT_16 = " = \"jdbc:mysql://\" + ";
  protected final String TEXT_17 = " + \":\" + ";
  protected final String TEXT_18 = " + \"/\" + ";
  protected final String TEXT_19 = " + \"?\" + dbProperties_";
  protected final String TEXT_20 = ";" + NL + "    }" + NL + "    String userName_";
  protected final String TEXT_21 = " = ";
  protected final String TEXT_22 = ";" + NL + "    String password_";
  protected final String TEXT_23 = " = ";
  protected final String TEXT_24 = ";" + NL + "    java.lang.Class.forName(\"org.gjt.mm.mysql.Driver\");" + NL + "    java.sql.Connection connection_";
  protected final String TEXT_25 = " = java.sql.DriverManager.getConnection(connectionString_";
  protected final String TEXT_26 = ", userName_";
  protected final String TEXT_27 = ", password_";
  protected final String TEXT_28 = ");";
  protected final String TEXT_29 = NL + NL + "java.sql.CallableStatement statement_";
  protected final String TEXT_30 = " = connection_";
  protected final String TEXT_31 = ".prepareCall(\"{";
  protected final String TEXT_32 = "call \" + ";
  protected final String TEXT_33 = " + \"(";
  protected final String TEXT_34 = "?";
  protected final String TEXT_35 = ",?";
  protected final String TEXT_36 = ")}\");" + NL + "" + NL + "java.sql.Date tmpDate_";
  protected final String TEXT_37 = ";" + NL + "String tmpString_";
  protected final String TEXT_38 = ";";
  protected final String TEXT_39 = NL;

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
String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
String dbuser = ElementParameterParser.getValue(node, "__USER__");
String dbpwd  = ElementParameterParser.getValue(node, "__PASS__");
String spName = ElementParameterParser.getValue(node, "__SP_NAME__");
boolean isFunction = ("true").equals(ElementParameterParser.getValue(node, "__IS_FUNCTION__"));
boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
List<Map<String, String>> spArgs = (List<Map<String,String>>) ElementParameterParser.getObjectValue(node, "__SP_ARGS__");

    stringBuffer.append(TEXT_2);
    
if(useExistingConn) {
    String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
    String conn = "conn_" + connection;    
    
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_5);
    
} else {
    
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    
}

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(isFunction ? "? = " : "");
    stringBuffer.append(TEXT_32);
    stringBuffer.append(spName);
    stringBuffer.append(TEXT_33);
    
boolean isFirstArg = true;
for (int i = 0; i < spArgs.size(); i++) {
	if(!("RECORDSET").equals(spArgs.get(i).get("TYPE"))){
		if(isFirstArg){
			
    stringBuffer.append(TEXT_34);
    
			isFirstArg=false;
		}else{
			
    stringBuffer.append(TEXT_35);
    
		}
	}
}

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(TEXT_39);
    return stringBuffer.toString();
  }
}
