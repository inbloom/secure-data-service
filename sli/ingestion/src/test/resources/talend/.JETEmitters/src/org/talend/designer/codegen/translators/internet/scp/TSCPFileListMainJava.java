package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSCPFileListMainJava
{
  protected static String nl;
  public static synchronized TSCPFileListMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPFileListMainJava result = new TSCPFileListMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\tnb_line_";
  protected final String TEXT_3 = "++;" + NL + "\t\t\t" + NL + "\t\t\tstringStdout_";
  protected final String TEXT_4 = ".append(line_out_";
  protected final String TEXT_5 = " + \"\\n\");" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_6 = "_NB_LINE\", nb_line_";
  protected final String TEXT_7 = ");" + NL + "    \t\tglobalMap.put(\"";
  protected final String TEXT_8 = "_CURRENT_LINE\", line_out_";
  protected final String TEXT_9 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
        CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
        INode node = (INode)codeGenArgument.getArgument();
        String cid = node.getUniqueName();
        

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
