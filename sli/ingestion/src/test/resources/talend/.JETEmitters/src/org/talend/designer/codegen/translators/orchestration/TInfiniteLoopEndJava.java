package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TInfiniteLoopEndJava
{
  protected static String nl;
  public static synchronized TInfiniteLoopEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TInfiniteLoopEndJava result = new TInfiniteLoopEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    Thread.sleep(";
  protected final String TEXT_3 = ");" + NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String wait = ElementParameterParser.getValue(node, "__WAIT_MS__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(wait);
    stringBuffer.append(TEXT_3);
    return stringBuffer.toString();
  }
}
