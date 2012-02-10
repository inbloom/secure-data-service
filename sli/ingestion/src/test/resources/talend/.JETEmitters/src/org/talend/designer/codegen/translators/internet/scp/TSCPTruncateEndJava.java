package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TSCPTruncateEndJava
{
  protected static String nl;
  public static synchronized TSCPTruncateEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPTruncateEndJava result = new TSCPTruncateEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "}";
  protected final String TEXT_3 = NL + "            /* Close the connection */" + NL + "            conn_";
  protected final String TEXT_4 = ".close();";
  protected final String TEXT_5 = NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_6 = "_NB_FILE\",nb_file_";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
            CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
            INode node = (INode)codeGenArgument.getArgument();
            String cid = node.getUniqueName();
            String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");

    stringBuffer.append(TEXT_2);
    			if(!("true").equals(useExistingConn)){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
              }
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
