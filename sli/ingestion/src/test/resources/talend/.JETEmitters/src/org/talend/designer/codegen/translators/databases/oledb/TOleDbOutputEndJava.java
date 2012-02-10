package org.talend.designer.codegen.translators.databases.oledb;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TOleDbOutputEndJava
{
  protected static String nl;
  public static synchronized TOleDbOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOleDbOutputEndJava result = new TOleDbOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    conn_";
  protected final String TEXT_3 = ".invoke(\"Close\");" + NL + "\tnb_line_deleted_";
  protected final String TEXT_4 = " = nb_line_deleted_";
  protected final String TEXT_5 = " + deletedCount_";
  protected final String TEXT_6 = ";" + NL + "\tnb_line_update_";
  protected final String TEXT_7 = " = nb_line_update_";
  protected final String TEXT_8 = " + updatedCount_";
  protected final String TEXT_9 = ";" + NL + "\tnb_line_inserted_";
  protected final String TEXT_10 = " = nb_line_inserted_";
  protected final String TEXT_11 = " + insertedCount_";
  protected final String TEXT_12 = ";" + NL + "\tnb_line_rejected_";
  protected final String TEXT_13 = " = nb_line_rejected_";
  protected final String TEXT_14 = " + rejectedCount_";
  protected final String TEXT_15 = ";" + NL + "" + NL + "    globalMap.put(\"";
  protected final String TEXT_16 = "_NB_LINE\",nb_line_";
  protected final String TEXT_17 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_18 = "_NB_LINE_UPDATED\",nb_line_update_";
  protected final String TEXT_19 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_20 = "_NB_LINE_INSERTED\",nb_line_inserted_";
  protected final String TEXT_21 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_22 = "_NB_LINE_DELETED\",nb_line_deleted_";
  protected final String TEXT_23 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_24 = "_NB_LINE_REJECTED\",nb_line_rejected_";
  protected final String TEXT_25 = ");";
  protected final String TEXT_26 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
    	
    	
    
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(TEXT_26);
    return stringBuffer.toString();
  }
}
