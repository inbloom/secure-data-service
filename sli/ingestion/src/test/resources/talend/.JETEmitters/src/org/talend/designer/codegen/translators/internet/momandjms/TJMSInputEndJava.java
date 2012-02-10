package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TJMSInputEndJava
{
  protected static String nl;
  public static synchronized TJMSInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJMSInputEndJava result = new TJMSInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tnbline_";
  protected final String TEXT_3 = "++;" + NL + "\tmessage_";
  protected final String TEXT_4 = ".acknowledge();" + NL + "\tif(";
  protected final String TEXT_5 = " > 0 && nbline_";
  protected final String TEXT_6 = " >= ";
  protected final String TEXT_7 = "){" + NL + "\t\tbreak;" + NL + "\t}" + NL + "\t" + NL + "}" + NL + "consumer_";
  protected final String TEXT_8 = ".close();" + NL + "session_";
  protected final String TEXT_9 = ".close();" + NL + "connection_";
  protected final String TEXT_10 = ".close();" + NL + "" + NL + "globalMap.put(\"";
  protected final String TEXT_11 = "_NB_LINE\", nbline_";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String timeout=ElementParameterParser.getValue(node, "__TIMEOUT__");
String maxMsg=ElementParameterParser.getValue(node, "__MAX_MSG__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(maxMsg );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(maxMsg );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
