package org.talend.designer.codegen.translators.business.microsoft_ax;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TMSAXOutputEndJava
{
  protected static String nl;
  public static synchronized TMSAXOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSAXOutputEndJava result = new TMSAXOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\taxapta3_";
  protected final String TEXT_2 = ".callMethod(\"TTSCommit\");" + NL + "\taxapta3_";
  protected final String TEXT_3 = ".callMethod(\"Logoff\");" + NL + "\t" + NL + "\tnb_line_deleted_";
  protected final String TEXT_4 = "=nb_line_deleted_";
  protected final String TEXT_5 = "+ deletedCount_";
  protected final String TEXT_6 = ";" + NL + "\tnb_line_update_";
  protected final String TEXT_7 = "=nb_line_update_";
  protected final String TEXT_8 = " + updatedCount_";
  protected final String TEXT_9 = ";" + NL + "\tnb_line_inserted_";
  protected final String TEXT_10 = "=nb_line_inserted_";
  protected final String TEXT_11 = " + insertedCount_";
  protected final String TEXT_12 = ";" + NL + "" + NL + "    globalMap.put(\"";
  protected final String TEXT_13 = "_NB_LINE\",nb_line_";
  protected final String TEXT_14 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_15 = "_NB_LINE_UPDATED\",nb_line_update_";
  protected final String TEXT_16 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_17 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_18 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_19 = "_NB_LINE_DELETED\",nb_line_deleted_";
  protected final String TEXT_20 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    return stringBuffer.toString();
  }
}
