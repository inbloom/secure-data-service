package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TSCPRenameEndJava
{
  protected static String nl;
  public static synchronized TSCPRenameEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPRenameEndJava result = new TSCPRenameEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "  \t" + NL + "\t\t\t/* Close the connection */" + NL + "            conn_";
  protected final String TEXT_3 = ".close();" + NL + "\t";
  protected final String TEXT_4 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
            CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
            INode node = (INode)codeGenArgument.getArgument();
            String cid = node.getUniqueName();
            String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");

	if(!("true").equals(useExistingConn)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    }
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
