package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TRSSOutputEndJava
{
  protected static String nl;
  public static synchronized TRSSOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRSSOutputEndJava result = new TRSSOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "org.dom4j.io.OutputFormat format";
  protected final String TEXT_2 = " = org.dom4j.io.OutputFormat.createPrettyPrint();" + NL + "format";
  protected final String TEXT_3 = ".setEncoding(";
  protected final String TEXT_4 = ");" + NL + "org.dom4j.io.XMLWriter writer";
  protected final String TEXT_5 = " = new org.dom4j.io.XMLWriter(new java.io.FileWriter(new java.io.File(";
  protected final String TEXT_6 = ")), format";
  protected final String TEXT_7 = ");" + NL + "writer";
  protected final String TEXT_8 = " .write(document";
  protected final String TEXT_9 = ");" + NL + "writer";
  protected final String TEXT_10 = " .close();" + NL + "globalMap.put(\"";
  protected final String TEXT_11 = "_NB_LINE\",nb_line_";
  protected final String TEXT_12 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String filename = ElementParameterParser.getValueWithUIFieldKey(node,"__FILENAME__", "FILENAME");
	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
