package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSOAPEndJava
{
  protected static String nl;
  public static synchronized TSOAPEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSOAPEndJava result = new TSOAPEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "soapUtil_";
  protected final String TEXT_2 = ".close();";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
