package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TInfiniteLoopBeginJava
{
  protected static String nl;
  public static synchronized TInfiniteLoopBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TInfiniteLoopBeginJava result = new TInfiniteLoopBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "int counter_";
  protected final String TEXT_3 = " = 0;" + NL + "globalMap.put(\"";
  protected final String TEXT_4 = "_CURRENT_ITERATION\", counter_";
  protected final String TEXT_5 = ");" + NL + "" + NL + "while (counter_";
  protected final String TEXT_6 = " > -1) {" + NL + "    counter_";
  protected final String TEXT_7 = "++;" + NL + "    globalMap.put(\"";
  protected final String TEXT_8 = "_CURRENT_ITERATION\", counter_";
  protected final String TEXT_9 = ");";

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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
