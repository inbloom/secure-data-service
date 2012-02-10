package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TSortOutEndJava
{
  protected static String nl;
  public static synchronized TSortOutEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSortOutEndJava result = new TSortOutEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = "Struct[] array_";
  protected final String TEXT_4 = " = list_";
  protected final String TEXT_5 = ".toArray(new Comparable";
  protected final String TEXT_6 = "Struct[0]);" + NL + "" + NL + "java.util.Arrays.sort(array_";
  protected final String TEXT_7 = ");" + NL + "" + NL + "globalMap.put(\"";
  protected final String TEXT_8 = "\",array_";
  protected final String TEXT_9 = ");" + NL;
  protected final String TEXT_10 = NL + "iterator_";
  protected final String TEXT_11 = ".endPut();" + NL + "" + NL + "globalMap.put(\"";
  protected final String TEXT_12 = "\", iterator_";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();	
String cid = node.getUniqueName();
String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
String rowName="";
if ((node.getIncomingConnections()!=null)&&(node.getIncomingConnections().size()>0)) {
	rowName = node.getIncomingConnections().get(0).getName();
} else {
	rowName="defaultRow";
}

String isExternalSort = ElementParameterParser.getValue(node, "__EXTERNAL__");


    stringBuffer.append(TEXT_1);
    
if(("false").equals(isExternalSort)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
}else{

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
}

    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
