package org.talend.designer.codegen.translators.custom_code;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;

public class TGroovyFileBeginJava
{
  protected static String nl;
  public static synchronized TGroovyFileBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TGroovyFileBeginJava result = new TGroovyFileBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "groovy.lang.Binding binding";
  protected final String TEXT_3 = " = new groovy.lang.Binding();" + NL;
  protected final String TEXT_4 = NL + NL + "        binding";
  protected final String TEXT_5 = ".setVariable(";
  protected final String TEXT_6 = ", ";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = NL + NL + NL + "groovy.lang.GroovyShell shell_";
  protected final String TEXT_9 = " = new groovy.lang.GroovyShell(binding";
  protected final String TEXT_10 = ");" + NL + "shell_";
  protected final String TEXT_11 = ".evaluate(new java.io.FileInputStream(new java.io.File(";
  protected final String TEXT_12 = ")));" + NL;
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String groovyFile = ElementParameterParser.getValue(node, "__GROOVY_FILE__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
	List<Map<String, String>> tableValues =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__VARIABLES__"
    );
    
    for(Map<String, String> tableValue : tableValues) {
        	            
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(tableValue.get("NAME"));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(tableValue.get("VALUE"));
    stringBuffer.append(TEXT_7);
    
	}

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(groovyFile);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
