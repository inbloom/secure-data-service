package org.talend.designer.codegen.translators.databases.exist;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TEXistPutBeginJava
{
  protected static String nl;
  public static synchronized TEXistPutBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TEXistPutBeginJava result = new TEXistPutBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\torg.xmldb.api.base.Collection col_";
  protected final String TEXT_3 = " = (org.xmldb.api.base.Collection)globalMap.get(\"";
  protected final String TEXT_4 = "\");";
  protected final String TEXT_5 = NL + "\t\tClass cl_";
  protected final String TEXT_6 = " = Class.forName(";
  protected final String TEXT_7 = ");" + NL + "\t    org.xmldb.api.base.Database database_";
  protected final String TEXT_8 = " = (org.xmldb.api.base.Database) cl_";
  protected final String TEXT_9 = ".newInstance();" + NL + "\t    database_";
  protected final String TEXT_10 = ".setProperty(\"create-database\", \"true\");" + NL + "\t    org.xmldb.api.DatabaseManager.registerDatabase(database_";
  protected final String TEXT_11 = ");" + NL + "\t    org.xmldb.api.base.Collection col_";
  protected final String TEXT_12 = " = org.xmldb.api.DatabaseManager.getCollection(";
  protected final String TEXT_13 = " + ";
  protected final String TEXT_14 = ",";
  protected final String TEXT_15 = ",";
  protected final String TEXT_16 = ");";
  protected final String TEXT_17 = NL + "\tint nb_file_";
  protected final String TEXT_18 = " = 0;" + NL + "    java.util.List<java.util.Map<String,String>> list_";
  protected final String TEXT_19 = " = new java.util.ArrayList<java.util.Map<String,String>>();";
  protected final String TEXT_20 = NL + "\t\tjava.util.Map<String,String> map_";
  protected final String TEXT_21 = "_";
  protected final String TEXT_22 = " = new java.util.HashMap<String,String>();" + NL + "\t\tmap_";
  protected final String TEXT_23 = "_";
  protected final String TEXT_24 = ".put(";
  protected final String TEXT_25 = ",";
  protected final String TEXT_26 = ");\t\t" + NL + " \t\tlist_";
  protected final String TEXT_27 = ".add(map_";
  protected final String TEXT_28 = "_";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = "\t" + NL + "" + NL + "\t\tString localdir_";
  protected final String TEXT_31 = "  = ";
  protected final String TEXT_32 = ";\t" + NL + "\t\tfor(java.util.Map<String, String> map_";
  protected final String TEXT_33 = " : list_";
  protected final String TEXT_34 = "){";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String uri = ElementParameterParser.getValue(node, "__URI__");
	String driver = ElementParameterParser.getValue(node, "__DRIVER__");
	String collection = ElementParameterParser.getValue(node, "__COLLECTION__");
	String user = ElementParameterParser.getValue(node, "__USERNAME__");
	String pass = ElementParameterParser.getValue(node, "__PASSWORD__");
	String localdir = ElementParameterParser.getValue(node, "__LOCALDIR__");
	String remotedir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
	List<Map<String, String>> files = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILES__");
	String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
	String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
	if(("true").equals(useExistingConn)){
		String col= "col_" + connection;

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(col);
    stringBuffer.append(TEXT_4);
    
	}else{

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(driver);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(uri);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(collection);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_16);
    
	}

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
    for(int i=0; i<files.size(); i++){
		Map<String, String> line = files.get(i);

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(line.get("FILEMASK"));
    stringBuffer.append(TEXT_25);
    stringBuffer.append(line.get("NEWNAME"));
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_29);
    
	}

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(localdir);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    return stringBuffer.toString();
  }
}
