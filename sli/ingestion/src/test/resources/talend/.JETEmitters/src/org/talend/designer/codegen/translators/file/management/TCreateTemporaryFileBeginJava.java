package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TCreateTemporaryFileBeginJava
{
  protected static String nl;
  public static synchronized TCreateTemporaryFileBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TCreateTemporaryFileBeginJava result = new TCreateTemporaryFileBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.io.File dir_";
  protected final String TEXT_3 = " = new java.io.File(";
  protected final String TEXT_4 = ");";
  protected final String TEXT_5 = NL + "\tjava.io.File dir_";
  protected final String TEXT_6 = " = new java.io.File(java.lang.System.getProperty(\"java.io.tmpdir\"));";
  protected final String TEXT_7 = NL + "dir_";
  protected final String TEXT_8 = ".mkdirs();" + NL + "String name_";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ".replaceAll(\"XXXX\", routines.TalendString.getAsciiRandomString(4).toUpperCase());" + NL + "String suffix_";
  protected final String TEXT_11 = " = (";
  protected final String TEXT_12 = ".replaceAll(\"\\\\.\", \"\").length() == 0) ? \"tmp\" : ";
  protected final String TEXT_13 = ".replaceAll(\"\\\\.\", \"\");" + NL + "java.io.File file_";
  protected final String TEXT_14 = " = new java.io.File(dir_";
  protected final String TEXT_15 = ", name_";
  protected final String TEXT_16 = " + \".\" + suffix_";
  protected final String TEXT_17 = ");" + NL + "if (file_";
  protected final String TEXT_18 = ".createNewFile()){";
  protected final String TEXT_19 = " " + NL + "    file_";
  protected final String TEXT_20 = ".deleteOnExit();";
  protected final String TEXT_21 = NL + "}" + NL + "globalMap.put(\"";
  protected final String TEXT_22 = "_FILEPATH\", file_";
  protected final String TEXT_23 = ".getCanonicalPath());";
  protected final String TEXT_24 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
boolean rmSuccess = ("true").equals(ElementParameterParser.getValue(node, "__REMOVE__"));
boolean useDefault = ("true").equals(ElementParameterParser.getValue(node, "__USE_DEFAULT_DIR__"));
String dir = ElementParameterParser.getValue(node, "__DIRECTORY__");
String filename = ElementParameterParser.getValue(node, "__TEMPLATE__");
String suffix = ElementParameterParser.getValue(node, "__SUFFIX__");
if(!useDefault){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(dir);
    stringBuffer.append(TEXT_4);
    
}else{

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(suffix);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(suffix);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    if(rmSuccess){
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    }
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(TEXT_24);
    return stringBuffer.toString();
  }
}
