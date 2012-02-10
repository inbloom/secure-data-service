package org.talend.designer.codegen.translators.logs_errors;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TChronometerStopBeginJava
{
  protected static String nl;
  public static synchronized TChronometerStopBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TChronometerStopBeginJava result = new TChronometerStopBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "long time";
  protected final String TEXT_2 = ";";
  protected final String TEXT_3 = NL + "  time";
  protected final String TEXT_4 = " = System.currentTimeMillis() - ((Long)globalMap.get(\"";
  protected final String TEXT_5 = "\")).longValue();";
  protected final String TEXT_6 = NL + "  time";
  protected final String TEXT_7 = " = System.currentTimeMillis() - startTime;";
  protected final String TEXT_8 = NL + "    System.out.print(\"[ ";
  protected final String TEXT_9 = " ]  \");";
  protected final String TEXT_10 = NL + "    System.out.print(\"   \" + time";
  protected final String TEXT_11 = "/1000 + \"seconds   \");" + NL + "\t    ";
  protected final String TEXT_12 = NL + "  System.out.println(";
  protected final String TEXT_13 = " + \"  \" + time";
  protected final String TEXT_14 = " + \" milliseconds\"); ";
  protected final String TEXT_15 = NL + "Long currentTime";
  protected final String TEXT_16 = " = System.currentTimeMillis();" + NL + "globalMap.put(\"";
  protected final String TEXT_17 = "\", currentTime";
  protected final String TEXT_18 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_19 = "_STOPTIME\", currentTime";
  protected final String TEXT_20 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_21 = "_DURATION\", time";
  protected final String TEXT_22 = ");";
  protected final String TEXT_23 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
boolean sinceStarter = ("true").equals(ElementParameterParser.getValue(node, "__SINCE_STARTER__"));
String starter = ElementParameterParser.getValue(node, "__STARTER__");
boolean display = ("true").equals(ElementParameterParser.getValue(node, "__DISPLAY__"));
boolean displayComponentName = ("true").equals(ElementParameterParser.getValue(node, "__DISPLAY_COMPONENT_NAME__"));
boolean displayReadableDuration = ("true").equals(ElementParameterParser.getValue(node, "__DISPLAY_READABLE_DURATION__"));
String caption = ElementParameterParser.getValue(node, "__CAPTION__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
if (sinceStarter) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(starter);
    stringBuffer.append(TEXT_5);
    
}
else {

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
}

if (display) {
  if (displayComponentName) {
  
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
  }
  if (displayReadableDuration) {
  
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
  }
  
    stringBuffer.append(TEXT_12);
    stringBuffer.append(caption);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(TEXT_23);
    return stringBuffer.toString();
  }
}
