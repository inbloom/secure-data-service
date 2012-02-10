package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TFileOutputLDIFEndJava
{
  protected static String nl;
  public static synchronized TFileOutputLDIFEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputLDIFEndJava result = new TFileOutputLDIFEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tpw_";
  protected final String TEXT_2 = ".flush();" + NL + "\tpw_";
  protected final String TEXT_3 = ".close();" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_4 = "_NB_LINE\",nb_line_";
  protected final String TEXT_5 = ");" + NL + "\t";
  protected final String TEXT_6 = NL + "\tif(nb_line_";
  protected final String TEXT_7 = " == 0){" + NL + "\t\tfile_";
  protected final String TEXT_8 = ".delete();" + NL + "\t}\t\t";
  protected final String TEXT_9 = "\t";
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
		CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
		INode node = (INode)codeGenArgument.getArgument();
		String cid = node.getUniqueName();		
			
		boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));
		boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));		

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    if(!isAppend && isDeleteEmptyFile){
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    }
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
