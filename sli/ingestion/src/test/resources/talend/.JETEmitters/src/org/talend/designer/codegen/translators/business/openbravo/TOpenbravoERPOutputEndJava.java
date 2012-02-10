package org.talend.designer.codegen.translators.business.openbravo;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TOpenbravoERPOutputEndJava
{
  protected static String nl;
  public static synchronized TOpenbravoERPOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOpenbravoERPOutputEndJava result = new TOpenbravoERPOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    globalMap.put(\"";
  protected final String TEXT_3 = "_NB_LINE\",nb_line_";
  protected final String TEXT_4 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_5 = "_NB_LINE_UPDATED\",nb_line_updated_";
  protected final String TEXT_6 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_7 = "_NB_LINE_CREATED\",nb_line_created_";
  protected final String TEXT_8 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_9 = "_NB_LINE_REMOVED\",nb_line_removed_";
  protected final String TEXT_10 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_11 = "_NB_LINE_UNAUTHORIZED\",nb_line_unauthorized_";
  protected final String TEXT_12 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_13 = "_NB_LINE_FAILED\",nb_line_failed_";
  protected final String TEXT_14 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	String action = ElementParameterParser.getValue(node, "__ACTION__");
	String usingExistingFile = ElementParameterParser.getValue(node, "__USE_EXISTING_FILE__");
	if("UPDATE_CREATE".equals(action) && "true".equals(usingExistingFile)){
		//do nothing
	}else{
    
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    
	}
	
    return stringBuffer.toString();
  }
}
