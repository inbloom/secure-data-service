package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TArrayBeginJava
{
  protected static String nl;
  public static synchronized TArrayBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TArrayBeginJava result = new TArrayBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "java.util.Arrays array";
  protected final String TEXT_3 = ";" + NL + "int nb_fields_";
  protected final String TEXT_4 = " = 0;" + NL + "" + NL + "java.util.List<";
  protected final String TEXT_5 = "Struct> ";
  protected final String TEXT_6 = " = new java.util.ArrayList<";
  protected final String TEXT_7 = "Struct>();" + NL + "globalMap.put(\"";
  protected final String TEXT_8 = "\",";
  protected final String TEXT_9 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
String rowName= "";
if ((node.getIncomingConnections()!=null)&&(node.getIncomingConnections().size()>0)) {
	rowName = node.getIncomingConnections().get(0).getName();
} else {
	rowName="defaultRow";
}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
