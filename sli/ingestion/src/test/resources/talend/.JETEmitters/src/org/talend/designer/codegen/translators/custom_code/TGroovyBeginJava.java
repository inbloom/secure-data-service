package org.talend.designer.codegen.translators.custom_code;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;

public class TGroovyBeginJava
{
  protected static String nl;
  public static synchronized TGroovyBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TGroovyBeginJava result = new TGroovyBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String code";
  protected final String TEXT_3 = " = \"\" ";
  protected final String TEXT_4 = NL + "    + \"";
  protected final String TEXT_5 = "\" + \"\\n\"";
  protected final String TEXT_6 = "; " + NL + "java.io.ByteArrayInputStream is";
  protected final String TEXT_7 = " = new java.io.ByteArrayInputStream(code";
  protected final String TEXT_8 = ".getBytes());" + NL + "groovy.lang.Binding binding";
  protected final String TEXT_9 = " = new groovy.lang.Binding();" + NL;
  protected final String TEXT_10 = NL + NL + "        binding";
  protected final String TEXT_11 = ".setVariable(";
  protected final String TEXT_12 = ", ";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL + NL + "groovy.lang.GroovyShell shell";
  protected final String TEXT_15 = " = new groovy.lang.GroovyShell(binding";
  protected final String TEXT_16 = ");" + NL + "shell";
  protected final String TEXT_17 = ".evaluate(is";
  protected final String TEXT_18 = ");" + NL;
  protected final String TEXT_19 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    String codeStringArray[] = ElementParameterParser.getValue(node, "__CODE__").split("\n");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    for(int i =0 ;i<codeStringArray.length;i++){
    codeStringArray[i] = codeStringArray[i].replace("\\","\\\\");
    codeStringArray[i] = codeStringArray[i].replace("\"","\\\"");    
    
    stringBuffer.append(TEXT_4);
    stringBuffer.append(codeStringArray[i]);
    stringBuffer.append(TEXT_5);
    }
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
	List<Map<String, String>> tableValues =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__VARIABLES__"
    );
    
    for(Map<String, String> tableValue : tableValues) {
        	            
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(tableValue.get("NAME"));
    stringBuffer.append(TEXT_12);
    stringBuffer.append(tableValue.get("VALUE"));
    stringBuffer.append(TEXT_13);
    
	}

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(TEXT_19);
    return stringBuffer.toString();
  }
}
