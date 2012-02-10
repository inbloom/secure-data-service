package org.talend.designer.codegen.translators.processing;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TSampleRowBeginJava
{
  protected static String nl;
  public static synchronized TSampleRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSampleRowBeginJava result = new TSampleRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String[] range";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ".split(\",\");";
  protected final String TEXT_5 = NL + "String[] range";
  protected final String TEXT_6 = " = \"";
  protected final String TEXT_7 = "\".split(\",\");";
  protected final String TEXT_8 = NL + "java.util.Set rangeSet";
  protected final String TEXT_9 = " = new java.util.HashSet();" + NL + "" + NL + "Integer nb_line_";
  protected final String TEXT_10 = " = 0;" + NL + "" + NL + "for(int i =0;i<range";
  protected final String TEXT_11 = ".length;i++){" + NL + "" + NL + "\tif(range";
  protected final String TEXT_12 = "[i].matches(\"\\\\d+\")){" + NL + "\t" + NL + "\t\trangeSet";
  protected final String TEXT_13 = " .add(Integer.valueOf(range";
  protected final String TEXT_14 = "[i]));" + NL + "\t\t" + NL + "\t}else if(range";
  protected final String TEXT_15 = "[i].matches(\"\\\\d+\\\\.\\\\.\\\\d+\")){" + NL + "\t\t" + NL + "\t\tString[] edge";
  protected final String TEXT_16 = "= range";
  protected final String TEXT_17 = "[i].split(\"\\\\.\\\\.\");" + NL + "\t\t" + NL + "\t\tfor(int j=Integer.valueOf(edge";
  protected final String TEXT_18 = "[0]).intValue();j<Integer.valueOf(edge";
  protected final String TEXT_19 = "[1]).intValue()+1;j++){\t\t\t" + NL + "\t\t\trangeSet";
  protected final String TEXT_20 = " .add(Integer.valueOf(j));\t\t\t" + NL + "\t\t}\t\t" + NL + "\t}else{" + NL + "\t" + NL + "\t}" + NL + "\t" + NL + "}" + NL + NL;
  protected final String TEXT_21 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String range = ElementParameterParser.getValue(node,"__RANGE__");
boolean isVar = false;
if(range.matches(".*[a-zA-Z]+.*")){
isVar = true;
}else{
range = range.replaceAll("\n",",").replaceAll("\"","");
}

    if(isVar){
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(range);
    stringBuffer.append(TEXT_4);
    }else{
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(range);
    stringBuffer.append(TEXT_7);
    }
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
