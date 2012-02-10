package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TWaitForSqlDataEndJava
{
  protected static String nl;
  public static synchronized TWaitForSqlDataEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWaitForSqlDataEndJava result = new TWaitForSqlDataEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    if(true){" + NL + "    \tbreak;" + NL + "    }";
  protected final String TEXT_2 = NL + "    if(false){" + NL + "        break;" + NL + "    }" + NL + "    Thread.currentThread().sleep(";
  protected final String TEXT_3 = "*1000);";
  protected final String TEXT_4 = NL + "}";
  protected final String TEXT_5 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

if(("exitloop").equals(ElementParameterParser.getValue(node, "__THEN__"))){

    stringBuffer.append(TEXT_1);
    
} else { 

    stringBuffer.append(TEXT_2);
    stringBuffer.append(ElementParameterParser.getValue(node, "__WAIT__"));
    stringBuffer.append(TEXT_3);
    
}

    stringBuffer.append(TEXT_4);
    stringBuffer.append(TEXT_5);
    return stringBuffer.toString();
  }
}
