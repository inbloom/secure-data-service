package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TLoopBeginJava
{
  protected static String nl;
  public static synchronized TLoopBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLoopBeginJava result = new TLoopBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "int current_iteration_";
  protected final String TEXT_3 = " = 0;";
  protected final String TEXT_4 = NL + NL + "for(int loop";
  protected final String TEXT_5 = " =";
  protected final String TEXT_6 = "; loop";
  protected final String TEXT_7 = "<=";
  protected final String TEXT_8 = "; loop";
  protected final String TEXT_9 = "=loop";
  protected final String TEXT_10 = "+";
  protected final String TEXT_11 = "){";
  protected final String TEXT_12 = NL + "for(int loop";
  protected final String TEXT_13 = " =";
  protected final String TEXT_14 = "; loop";
  protected final String TEXT_15 = ">=";
  protected final String TEXT_16 = "; loop";
  protected final String TEXT_17 = "=loop";
  protected final String TEXT_18 = "+";
  protected final String TEXT_19 = "){";
  protected final String TEXT_20 = NL + "current_iteration_";
  protected final String TEXT_21 = "++;" + NL + "globalMap.put(\"";
  protected final String TEXT_22 = "_CURRENT_VALUE\",loop";
  protected final String TEXT_23 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_24 = "_CURRENT_ITERATION\",current_iteration_";
  protected final String TEXT_25 = ");" + NL;
  protected final String TEXT_26 = NL;
  protected final String TEXT_27 = NL;
  protected final String TEXT_28 = ";" + NL + "" + NL + "while(";
  protected final String TEXT_29 = "){" + NL + "" + NL + "current_iteration_";
  protected final String TEXT_30 = "++;" + NL + "globalMap.put(\"";
  protected final String TEXT_31 = "_CURRENT_ITERATION\",current_iteration_";
  protected final String TEXT_32 = ");" + NL;
  protected final String TEXT_33 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

boolean forLoop = ("true").equals(ElementParameterParser.getValue(node,"__FORLOOP__"));

boolean whileLoop = ("true").equals(ElementParameterParser.getValue(node,"__WHILELOOP__"));


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
if (forLoop) {

String from = ElementParameterParser.getValue(node, "__FROM__");
if (("").equals(from)) from = "1";
String to   = ElementParameterParser.getValue(node, "__TO__");
if (("").equals(to)) to = "1";
String step   = ElementParameterParser.getValue(node, "__STEP__");
if (("").equals(step)) step = "1";
boolean increase = ("true").equals(ElementParameterParser.getValue(node, "__INCREASE__"));
if(increase){
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(from);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(to);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(step);
    stringBuffer.append(TEXT_11);
    
}else{
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(from);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(to);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(step);
    stringBuffer.append(TEXT_19);
    }
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
}else{// While type

String condition = ElementParameterParser.getValue(node, "__CONDITION__");
if (condition.startsWith("\"") && condition.endsWith("\"")) {
    condition = condition.substring(1, condition.length()-1);
}

String declaration = ElementParameterParser.getValue(node, "__DECLARATION__");
if (declaration.startsWith("\"") && declaration.endsWith("\"")) {
    declaration = declaration.substring(1, declaration.length()-1);
}




    stringBuffer.append(TEXT_26);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(declaration);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(condition);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
}

    stringBuffer.append(TEXT_33);
    return stringBuffer.toString();
  }
}
