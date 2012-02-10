package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TMomMessageIdListEndJava
{
  protected static String nl;
  public static synchronized TMomMessageIdListEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMomMessageIdListEndJava result = new TMomMessageIdListEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t\t\t" + NL + "\t}" + NL + "\tflag";
  protected final String TEXT_3 = "=false;" + NL + "\tinput";
  protected final String TEXT_4 = ".close();" + NL + "}";
  protected final String TEXT_5 = NL + "}";
  protected final String TEXT_6 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String serverType=ElementParameterParser.getValue(node, "__SERVER__");
String timeOut=ElementParameterParser.getValue(node, "__TIMEOUT__");
if(("JBoss").equals(serverType)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
}else{

    stringBuffer.append(TEXT_5);
    
	}

    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
