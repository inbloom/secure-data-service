package org.talend.designer.codegen.translators.databases.oracle;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TOracleSPEndJava
{
  protected static String nl;
  public static synchronized TOracleSPEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOracleSPEndJava result = new TOracleSPEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\tstmtNLS_";
  protected final String TEXT_4 = ".close();";
  protected final String TEXT_5 = NL + "\tstatement_";
  protected final String TEXT_6 = ".close();";
  protected final String TEXT_7 = NL + "\tconnection_";
  protected final String TEXT_8 = " .close();" + NL + "\t";
  protected final String TEXT_9 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
	String nlsLanguage = ElementParameterParser.getValue(node, "__NLS_LANGUAGE__");
	String nlsTerritory = ElementParameterParser.getValue(node, "__NLS_TERRITORY__");

    stringBuffer.append(TEXT_2);
    	
	if (!("NONE").equals(nlsLanguage) || !("NONE").equals(nlsTerritory) ) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
	}

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
if (!("true").equals(useExistingConn)) {
	
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
}

    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
