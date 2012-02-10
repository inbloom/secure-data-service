package org.talend.designer.codegen.translators.databases.ms_sql_server;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TMSSqlSPBeginJava
{
  protected static String nl;
  public static synchronized TMSSqlSPBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSSqlSPBeginJava result = new TMSSqlSPBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String dbschema_";
  protected final String TEXT_3 = " =\"\";";
  protected final String TEXT_4 = NL;
  protected final String TEXT_5 = NL + "\tdbschema_";
  protected final String TEXT_6 = " = (String)globalMap.get(\"";
  protected final String TEXT_7 = "\");" + NL + "\tjava.sql.Connection conn_";
  protected final String TEXT_8 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_9 = "\");" + NL + "\t";
  protected final String TEXT_10 = NL + "    dbschema_";
  protected final String TEXT_11 = " = ";
  protected final String TEXT_12 = ";" + NL + "    java.lang.Class.forName(\"net.sourceforge.jtds.jdbc.Driver\");" + NL + "    String port_";
  protected final String TEXT_13 = " = ";
  protected final String TEXT_14 = ";" + NL + "    String dbname_";
  protected final String TEXT_15 = " = ";
  protected final String TEXT_16 = " ;" + NL + "    String url_";
  protected final String TEXT_17 = " = \"jdbc:jtds:sqlserver://\" + ";
  protected final String TEXT_18 = " ; " + NL + "    if (!\"\".equals(port_";
  protected final String TEXT_19 = ")) {" + NL + "    \turl_";
  protected final String TEXT_20 = " += \":\" + ";
  protected final String TEXT_21 = ";" + NL + "    }" + NL + "    if (!\"\".equals(dbname_";
  protected final String TEXT_22 = ")) {" + NL + "    \turl_";
  protected final String TEXT_23 = " += \"//\" + ";
  protected final String TEXT_24 = "; " + NL + "    }" + NL + "    url_";
  protected final String TEXT_25 = " += \";appName=\" + projectName + \";\" + ";
  protected final String TEXT_26 = ";" + NL + "    String dbUser_";
  protected final String TEXT_27 = " = ";
  protected final String TEXT_28 = ";" + NL + "    String dbPwd_";
  protected final String TEXT_29 = " = ";
  protected final String TEXT_30 = ";" + NL + "    java.sql.Connection conn_";
  protected final String TEXT_31 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_32 = ",dbUser_";
  protected final String TEXT_33 = ",dbPwd_";
  protected final String TEXT_34 = ");";
  protected final String TEXT_35 = NL + "//java.sql.Statement stmt_";
  protected final String TEXT_36 = " = conn_";
  protected final String TEXT_37 = ".createStatement();" + NL + "" + NL + "//stmt_";
  protected final String TEXT_38 = ".execute(\"SET NOCOUNT ON\");" + NL + "" + NL + "java.sql.CallableStatement statement_";
  protected final String TEXT_39 = " = conn_";
  protected final String TEXT_40 = ".prepareCall(\"{";
  protected final String TEXT_41 = "call \" + ";
  protected final String TEXT_42 = " + \"(";
  protected final String TEXT_43 = ")}\"";
  protected final String TEXT_44 = NL + "\t,java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY";
  protected final String TEXT_45 = NL + ");" + NL + "" + NL + "java.sql.Timestamp tmpDate_";
  protected final String TEXT_46 = ";" + NL + "String tmpString_";
  protected final String TEXT_47 = ";";
  protected final String TEXT_48 = NL;

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
String dbschema = ElementParameterParser.getValue(node, "__DB_SCHEMA__");
String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
String dbuser = ElementParameterParser.getValue(node, "__USER__");
String dbpwd  = ElementParameterParser.getValue(node, "__PASS__");
String spName = ElementParameterParser.getValue(node, "__SP_NAME__");
boolean isFunction = ("true").equals(ElementParameterParser.getValue(node, "__IS_FUNCTION__"));

List<Map<String, String>> spArgs = (List<Map<String,String>>) ElementParameterParser.getObjectValue(node, "__SP_ARGS__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(TEXT_4);
    
boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
if(useExistingConnection) {
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String conn = "conn_" + connection;
	String schema = "dbschema_" + connection;
	
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(schema);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_9);
    
} else {       
    
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(dbschema);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    
}

    
boolean hasOutput = false;
StringBuilder parameters =new StringBuilder();
for (int i = 0; i < spArgs.size(); i++) {
	if(("RECORDSET").equals(spArgs.get(i).get("TYPE")) 
		|| ("INOUT").equals(spArgs.get(i).get("TYPE"))
		|| ("OUT").equals(spArgs.get(i).get("TYPE"))){
		hasOutput=true;
	}
    if(!("RECORDSET").equals(spArgs.get(i).get("TYPE"))){
        if (parameters.length()==0) {
           	parameters.append("?");
        } else {
            parameters.append(",?");
        }
    }
}

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(isFunction ? "? = " : "");
    stringBuffer.append(TEXT_41);
    stringBuffer.append(spName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(parameters.toString());
    stringBuffer.append(TEXT_43);
    
if(hasOutput){

    stringBuffer.append(TEXT_44);
    
}

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(TEXT_48);
    return stringBuffer.toString();
  }
}
