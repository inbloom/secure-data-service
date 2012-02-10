package org.talend.designer.codegen.translators.databases.netezza;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TNetezzaInputEndJava
{
  protected static String nl;
  public static synchronized TNetezzaInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TNetezzaInputEndJava result = new TNetezzaInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "}" + NL + "stmt_";
  protected final String TEXT_3 = ".close();" + NL;
  protected final String TEXT_4 = NL + "\tconn_";
  protected final String TEXT_5 = ".commit();" + NL + "\t";
  protected final String TEXT_6 = NL + "\tconn_";
  protected final String TEXT_7 = " .close();" + NL + "\t";
  protected final String TEXT_8 = NL + NL + "globalMap.put(\"";
  protected final String TEXT_9 = "_NB_LINE\",nb_line_";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
if(!("true").equals(useExistingConn))
{
	String useCursor= ElementParameterParser.getValue(node, "__USE_CURSOR__");
	if(("true").equals(useCursor)) {
	
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    }
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
}

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
