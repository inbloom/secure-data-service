package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TFileExistMainJava
{
  protected static String nl;
  public static synchronized TFileExistMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileExistMainJava result = new TFileExistMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "java.io.File file_";
  protected final String TEXT_3 = " = new java.io.File(";
  protected final String TEXT_4 = ");" + NL + "if (!file_";
  protected final String TEXT_5 = ".exists()) {" + NL + "    globalMap.put(\"";
  protected final String TEXT_6 = "_EXISTS\",false);" + NL + "}else{" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_7 = "_EXISTS\",true);" + NL + "}" + NL + "" + NL + "globalMap.put(\"";
  protected final String TEXT_8 = "_FILENAME\",";
  protected final String TEXT_9 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String fileName = ElementParameterParser.getValue(node, "__FILE_NAME__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
