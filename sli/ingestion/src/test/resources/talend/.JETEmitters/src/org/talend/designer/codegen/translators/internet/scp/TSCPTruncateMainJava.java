package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSCPTruncateMainJava
{
  protected static String nl;
  public static synchronized TSCPTruncateMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPTruncateMainJava result = new TSCPTruncateMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\ttry{" + NL + "        if(destFile_";
  protected final String TEXT_3 = "!=null && destFile_";
  protected final String TEXT_4 = ".length()!=0){" + NL + "            scp_";
  protected final String TEXT_5 = ".put(new byte[]{}, destFile_";
  protected final String TEXT_6 = " , ";
  protected final String TEXT_7 = ", \"0644\");" + NL + "            nb_file_";
  protected final String TEXT_8 = " ++;" + NL + "            globalMap.put(\"";
  protected final String TEXT_9 = "_STATUS\", \"File truncate OK.\");" + NL + "        }" + NL + "    }catch(Exception e){" + NL + "    \t\tglobalMap.put(\"";
  protected final String TEXT_10 = "_STATUS\", \"File truncate fail.\");" + NL + "    }";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
        CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
        INode node = (INode)codeGenArgument.getArgument();
        String cid = node.getUniqueName();      

        String remotedir = ElementParameterParser.getValue(node, "__REMOTEDIR__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(remotedir );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
