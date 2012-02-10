package org.talend.designer.codegen.translators.custom_code;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TJavaRowEndJava
{
  protected static String nl;
  public static synchronized TJavaRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJavaRowEndJava result = new TJavaRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "globalMap.put(\"";
  protected final String TEXT_3 = "_NB_LINE\",nb_line_";
  protected final String TEXT_4 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
