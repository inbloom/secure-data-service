package org.talend.designer.codegen.translators.custom_code;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TSetGlobalVarMainJava
{
  protected static String nl;
  public static synchronized TSetGlobalVarMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSetGlobalVarMainJava result = new TSetGlobalVarMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "globalMap.put(";
  protected final String TEXT_3 = ", ";
  protected final String TEXT_4 = ");";
  protected final String TEXT_5 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<Map<String, String>> variables =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__VARIABLES__"
    );

for (Map<String, String> variable : variables) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(variable.get("KEY"));
    stringBuffer.append(TEXT_3);
    stringBuffer.append(variable.get("VALUE"));
    stringBuffer.append(TEXT_4);
    
}

    stringBuffer.append(TEXT_5);
    return stringBuffer.toString();
  }
}
