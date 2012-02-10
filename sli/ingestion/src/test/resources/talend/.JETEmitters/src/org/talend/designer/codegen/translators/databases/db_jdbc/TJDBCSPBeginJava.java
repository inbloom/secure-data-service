package org.talend.designer.codegen.translators.databases.db_jdbc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TJDBCSPBeginJava
{
  protected static String nl;
  public static synchronized TJDBCSPBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJDBCSPBeginJava result = new TJDBCSPBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.sql.Connection connection_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");";
  protected final String TEXT_5 = NL + "java.lang.Class.forName(";
  protected final String TEXT_6 = ");" + NL + "String connectionString_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "java.sql.Connection connection_";
  protected final String TEXT_9 = " = java.sql.DriverManager.getConnection(connectionString_";
  protected final String TEXT_10 = ", ";
  protected final String TEXT_11 = ", ";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL + NL + "java.sql.CallableStatement statement_";
  protected final String TEXT_14 = " = connection_";
  protected final String TEXT_15 = ".prepareCall(\"{";
  protected final String TEXT_16 = "call \" + ";
  protected final String TEXT_17 = " + \"(";
  protected final String TEXT_18 = "?";
  protected final String TEXT_19 = ",?";
  protected final String TEXT_20 = ")}\");" + NL + "" + NL + "java.sql.Date tmpDate_";
  protected final String TEXT_21 = ";" + NL + "String tmpString_";
  protected final String TEXT_22 = ";";
  protected final String TEXT_23 = NL;

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
String dbpwd  = ElementParameterParser.getValue(node, "__PASS__");
String spName = ElementParameterParser.getValue(node, "__SP_NAME__");
boolean isFunction = ("true").equals(ElementParameterParser.getValue(node, "__IS_FUNCTION__"));
List<Map<String, String>> spArgs = (List<Map<String,String>>) ElementParameterParser.getObjectValue(node, "__SP_ARGS__");


    
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
	if(("true").equals(useExistingConn)){
		String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
		String conn = "conn_" + connection;

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_4);
    
	}else{       

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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(isFunction ? "? = " : "");
    stringBuffer.append(TEXT_16);
    stringBuffer.append(spName);
    stringBuffer.append(TEXT_17);
    
boolean isFirstArg = true;
for (int i = 0; i < spArgs.size(); i++) {
	if(!("RECORDSET").equals(spArgs.get(i).get("TYPE"))){
		if(isFirstArg){
			
    stringBuffer.append(TEXT_18);
    
			isFirstArg=false;
		}else{
			
    stringBuffer.append(TEXT_19);
    
		}
	}
}

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(TEXT_23);
    return stringBuffer.toString();
  }
}
