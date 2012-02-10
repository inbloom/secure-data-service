package org.talend.designer.codegen.translators.business_intelligence.olap_cube.mondrian;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TMondrianInputEndJava
{
  protected static String nl;
  public static synchronized TMondrianInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMondrianInputEndJava result = new TMondrianInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "}" + NL + "" + NL + "result_";
  protected final String TEXT_3 = ".close();" + NL + "connection_";
  protected final String TEXT_4 = ".close();" + NL + "" + NL + "//globalMap.put(\"";
  protected final String TEXT_5 = "_NB_LINE\",nb_line_";
  protected final String TEXT_6 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode) codeGenArgument.getArgument();
String cid = node.getUniqueName();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
