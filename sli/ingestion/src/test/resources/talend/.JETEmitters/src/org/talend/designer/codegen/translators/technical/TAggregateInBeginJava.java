package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TAggregateInBeginJava
{
  protected static String nl;
  public static synchronized TAggregateInBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAggregateInBeginJava result = new TAggregateInBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + NL + "java.util.Collection<AggOperationStruct_";
  protected final String TEXT_3 = "> values_";
  protected final String TEXT_4 = " = hash_";
  protected final String TEXT_5 = ".values();" + NL + "" + NL + "globalMap.put(\"";
  protected final String TEXT_6 = "_NB_LINE\", values_";
  protected final String TEXT_7 = ".size());" + NL + "" + NL + "for(AggOperationStruct_";
  protected final String TEXT_8 = " aggregated_row_";
  protected final String TEXT_9 = " : values_";
  protected final String TEXT_10 = ") { // G_AggR_600" + NL + NL;
  protected final String TEXT_11 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String origin = ElementParameterParser.getValue(node, "__ORIGIN__");
String cid = origin;



    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
