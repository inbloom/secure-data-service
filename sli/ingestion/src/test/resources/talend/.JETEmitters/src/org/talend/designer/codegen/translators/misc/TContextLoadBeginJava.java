package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TContextLoadBeginJava
{
  protected static String nl;
  public static synchronized TContextLoadBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TContextLoadBeginJava result = new TContextLoadBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tjava.util.List<String> assignList_";
  protected final String TEXT_2 = " = new java.util.ArrayList<String>();" + NL + "\tjava.util.List<String> newPropertyList_";
  protected final String TEXT_3 = " = new java.util.ArrayList<String>();" + NL + "\tjava.util.List<String> noAssignList_";
  protected final String TEXT_4 = " = new java.util.ArrayList<String>();" + NL + "\tint nb_line_";
  protected final String TEXT_5 = " = 0;";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    return stringBuffer.toString();
  }
}
