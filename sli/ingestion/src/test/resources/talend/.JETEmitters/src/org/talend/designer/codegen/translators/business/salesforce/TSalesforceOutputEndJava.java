package org.talend.designer.codegen.translators.business.salesforce;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSalesforceOutputEndJava
{
  protected static String nl;
  public static synchronized TSalesforceOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceOutputEndJava result = new TSalesforceOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " " + NL + " \t" + NL + " \tsforceManagement_";
  protected final String TEXT_2 = ".logout(); " + NL + " \t" + NL + " \tglobalMap.put(\"";
  protected final String TEXT_3 = "_NB_LINE\",nb_line_";
  protected final String TEXT_4 = ");" + NL + " \tglobalMap.put(\"";
  protected final String TEXT_5 = "_NB_SUCCESS\",nb_success_";
  protected final String TEXT_6 = ");" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_7 = "_NB_REJECT\",nb_reject_";
  protected final String TEXT_8 = ");             ";
  protected final String TEXT_9 = NL + "            ";

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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
