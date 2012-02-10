package org.talend.designer.codegen.translators.databases.oledb;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TOleDbRowBeginJava
{
  protected static String nl;
  public static synchronized TOleDbRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOleDbRowBeginJava result = new TOleDbRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "org.talend.net.Object conn_";
  protected final String TEXT_3 = " = org.talend.net.Object.createInstance(";
  protected final String TEXT_4 = ",";
  protected final String TEXT_5 = ",new java.lang.Object[] {";
  protected final String TEXT_6 = "});" + NL + "conn_";
  protected final String TEXT_7 = ".invokeGeneric(\"Open\");" + NL + "boolean whetherReject_";
  protected final String TEXT_8 = " = false;" + NL + "" + NL + "org.talend.net.Object cmd_";
  protected final String TEXT_9 = ";" + NL;
  protected final String TEXT_10 = NL + "\t\tcmd_";
  protected final String TEXT_11 = " = org.talend.net.Object.createInstance(";
  protected final String TEXT_12 = ", \"System.Data.OleDb.OleDbCommand\", new java.lang.Object[] { ";
  protected final String TEXT_13 = ",conn_";
  protected final String TEXT_14 = " });" + NL + "\t\tcmd_";
  protected final String TEXT_15 = ".invoke(\"Prepare\");\t";
  protected final String TEXT_16 = NL + "String query_";
  protected final String TEXT_17 = " = \"\";";
  protected final String TEXT_18 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	
	String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
	String assemblyName = ElementParameterParser.getValue(node,"__ASSEMBLY_NAME__");
    String className = ElementParameterParser.getValue(node,"__CLASS_NAME__");
    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(assemblyName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
	if(usePrepareStatement){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(assemblyName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    
	}

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(TEXT_18);
    return stringBuffer.toString();
  }
}
