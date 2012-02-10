package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TFileListEndJava
{
  protected static String nl;
  public static synchronized TFileListEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileListEndJava result = new TFileListEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    }" + NL + "  }" + NL + "  globalMap.put(\"";
  protected final String TEXT_2 = "_NB_FILE\", NB_FILE";
  protected final String TEXT_3 = ");" + NL;
  protected final String TEXT_4 = NL + "    if (NB_FILE";
  protected final String TEXT_5 = " == 0) throw new RuntimeException(\"No file found in directory \" + directory_";
  protected final String TEXT_6 = ");";
  protected final String TEXT_7 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
  CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
  INode node = (INode)codeGenArgument.getArgument();
  String cid = node.getUniqueName();
  boolean generateError = ("true").equals(ElementParameterParser.getValue(node, "__ERROR__"));

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    if (generateError){
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    }
    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
