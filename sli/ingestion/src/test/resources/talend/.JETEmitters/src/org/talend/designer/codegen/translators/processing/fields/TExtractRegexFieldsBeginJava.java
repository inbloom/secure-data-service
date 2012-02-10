package org.talend.designer.codegen.translators.processing.fields;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TExtractRegexFieldsBeginJava
{
  protected static String nl;
  public static synchronized TExtractRegexFieldsBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TExtractRegexFieldsBeginJava result = new TExtractRegexFieldsBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.util.regex.Pattern pattern_";
  protected final String TEXT_3 = " = java.util.regex.Pattern.compile(";
  protected final String TEXT_4 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String reg = ElementParameterParser.getValue(node,"__REGEX__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(reg );
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
