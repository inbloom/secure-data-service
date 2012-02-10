package org.talend.designer.codegen.translators.databases.db2;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TDB2BulkExecEndJava
{
  protected static String nl;
  public static synchronized TDB2BulkExecEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TDB2BulkExecEndJava result = new TDB2BulkExecEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    " + NL + "\t\tconn_";
  protected final String TEXT_2 = ".commit();" + NL + "        conn_";
  protected final String TEXT_3 = ".close();";
  protected final String TEXT_4 = NL;
  protected final String TEXT_5 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
    
                
    if(!("true").equals(useExistingConn)) { 

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
	}

    stringBuffer.append(TEXT_4);
    stringBuffer.append(TEXT_5);
    return stringBuffer.toString();
  }
}
