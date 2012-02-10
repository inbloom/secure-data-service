package org.talend.designer.codegen.translators.business.sugarcrm;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSugarCRMInputEndJava
{
  protected static String nl;
  public static synchronized TSugarCRMInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSugarCRMInputEndJava result = new TSugarCRMInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "             " + NL + "                                " + NL + "            }" + NL + "            " + NL + "            getEntryListResult_";
  protected final String TEXT_2 = " = sugarManagement_";
  protected final String TEXT_3 = ".getRecordswithQuery(modulename_";
  protected final String TEXT_4 = ", condition_";
  protected final String TEXT_5 = ", filedArr_";
  protected final String TEXT_6 = ", getEntryListResult_";
  protected final String TEXT_7 = ".getNext_offset(), 100); " + NL + "        } " + NL + " \t" + NL + " \tsugarManagement_";
  protected final String TEXT_8 = ".logout(); " + NL + " \t" + NL + " \tglobalMap.put(\"";
  protected final String TEXT_9 = "_NB_LINE\",nb_line_";
  protected final String TEXT_10 = ");                ";
  protected final String TEXT_11 = NL + "            ";

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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
