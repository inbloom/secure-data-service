package org.talend.designer.codegen.translators.databases.ms_sql_server;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TMSSqlSPEndJava
{
  protected static String nl;
  public static synchronized TMSSqlSPEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSSqlSPEndJava result = new TMSSqlSPEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "//stmt_";
  protected final String TEXT_3 = ".execute(\"SET NOCOUNT OFF\");" + NL + "" + NL + "//stmt_";
  protected final String TEXT_4 = ".close();" + NL + "" + NL + "statement_";
  protected final String TEXT_5 = ".close();";
  protected final String TEXT_6 = NL + "\tconn_";
  protected final String TEXT_7 = " .close();" + NL + "\t";
  protected final String TEXT_8 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
if (!("true").equals(useExistingConn)) {
	
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
}

    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
