package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileOutputEBCDICEndJava
{
  protected static String nl;
  public static synchronized TFileOutputEBCDICEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputEBCDICEndJava result = new TFileOutputEBCDICEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tglobalMap.put(\"";
  protected final String TEXT_2 = "_NB_LINE\", nb_line_";
  protected final String TEXT_3 = ");";

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
    return stringBuffer.toString();
  }
}
