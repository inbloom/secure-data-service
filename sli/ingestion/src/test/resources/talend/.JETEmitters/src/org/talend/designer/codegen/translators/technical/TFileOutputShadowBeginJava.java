package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileOutputShadowBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputShadowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputShadowBeginJava result = new TFileOutputShadowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "java.io.FileOutputStream fos_";
  protected final String TEXT_3 = " = new java.io.FileOutputStream(";
  protected final String TEXT_4 = ");" + NL + "java.io.BufferedWriter out_";
  protected final String TEXT_5 = " = null;" + NL + "out_";
  protected final String TEXT_6 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(fos_";
  protected final String TEXT_7 = ", ";
  protected final String TEXT_8 = "));" + NL + "out_";
  protected final String TEXT_9 = ".write(\"<?xml version=\\\"1.0\\\" encoding=\\\"\"+";
  protected final String TEXT_10 = "+\"\\\"?>\");" + NL + "out_";
  protected final String TEXT_11 = ".newLine();" + NL + "out_";
  protected final String TEXT_12 = ".write(\"<array>\");" + NL + "out_";
  protected final String TEXT_13 = ".newLine();";
  protected final String TEXT_14 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
        String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
		if (encoding!=null) {
            if (("").equals(encoding)) {
                encoding = "ISO-8859-15";
            }
        }
        String cid = node.getUniqueName();
        String fileName = ElementParameterParser.getValue(node, "__FILENAME__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
