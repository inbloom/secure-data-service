package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.metadata.IMetadataTable;

public class TFileOutputJSONEndJava
{
  protected static String nl;
  public static synchronized TFileOutputJSONEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputJSONEndJava result = new TFileOutputJSONEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "org.json.simple.JSONObject jsonOut";
  protected final String TEXT_3 = " = new org.json.simple.JSONObject();" + NL + "jsonOut";
  protected final String TEXT_4 = ".put(";
  protected final String TEXT_5 = ",jsonSet";
  protected final String TEXT_6 = ");" + NL + "java.io.File file_";
  protected final String TEXT_7 = " = new java.io.File(";
  protected final String TEXT_8 = ");" + NL + "java.io.File dir_";
  protected final String TEXT_9 = " = file_";
  protected final String TEXT_10 = ".getParentFile();";
  protected final String TEXT_11 = NL + "\tif(dir_";
  protected final String TEXT_12 = "!=null && !dir_";
  protected final String TEXT_13 = ".exists()){" + NL + "\t\tdir_";
  protected final String TEXT_14 = ".mkdirs();" + NL + "\t}";
  protected final String TEXT_15 = NL + "java.io.PrintWriter out";
  protected final String TEXT_16 = " = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(";
  protected final String TEXT_17 = ")));" + NL + "out";
  protected final String TEXT_18 = ".print(jsonOut";
  protected final String TEXT_19 = ");" + NL + "out";
  protected final String TEXT_20 = ".close();" + NL + "globalMap.put(\"";
  protected final String TEXT_21 = "_NB_LINE\",nb_line_";
  protected final String TEXT_22 = ");";
  protected final String TEXT_23 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String datablockname = ElementParameterParser.getValue(node, "__DATABLOCKNAME__");
String filename = ElementParameterParser.getValue(node, "__FILENAME__");
boolean create = "true".equals(ElementParameterParser.getValue(node, "__CREATE__"));
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {

}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(datablockname);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
if(create){

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(TEXT_23);
    return stringBuffer.toString();
  }
}
