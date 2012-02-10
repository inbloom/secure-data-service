package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TJMSOutputEndJava
{
  protected static String nl;
  public static synchronized TJMSOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJMSOutputEndJava result = new TJMSOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "producer_";
  protected final String TEXT_3 = ".close();" + NL + "session_";
  protected final String TEXT_4 = ".close();" + NL + "connection_";
  protected final String TEXT_5 = ".close();" + NL + "globalMap.put(\"";
  protected final String TEXT_6 = "_NB_LINE\", nbline_";
  protected final String TEXT_7 = ");" + NL + "" + NL + "" + NL + "            ";
  protected final String TEXT_8 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
