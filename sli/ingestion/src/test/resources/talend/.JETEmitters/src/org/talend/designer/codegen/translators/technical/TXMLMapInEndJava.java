package org.talend.designer.codegen.translators.technical;

import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TXMLMapInEndJava
{
  protected static String nl;
  public static synchronized TXMLMapInEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TXMLMapInEndJava result = new TXMLMapInEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "}//TD512";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
