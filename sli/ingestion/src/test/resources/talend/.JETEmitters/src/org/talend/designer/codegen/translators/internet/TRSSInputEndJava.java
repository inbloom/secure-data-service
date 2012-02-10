package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TRSSInputEndJava
{
  protected static String nl;
  public static synchronized TRSSInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRSSInputEndJava result = new TRSSInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    nb_line_";
  protected final String TEXT_2 = "++;" + NL + "    }//end if (!ifInvalid";
  protected final String TEXT_3 = ")" + NL + "}" + NL + "" + NL + "\t" + NL + "globalMap.put(\"";
  protected final String TEXT_4 = "_NB_LINE\",nb_line_";
  protected final String TEXT_5 = ");";
  protected final String TEXT_6 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	//boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node,"__DIE_ON_ERROR__"));
		

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
