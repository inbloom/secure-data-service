package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import java.util.List;

public class TMemorizeRowsBeginJava
{
  protected static String nl;
  public static synchronized TMemorizeRowsBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMemorizeRowsBeginJava result = new TMemorizeRowsBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  int iRows_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = ";";
  protected final String TEXT_4 = NL + "      byte[][] ";
  protected final String TEXT_5 = "_";
  protected final String TEXT_6 = " = new byte[iRows_";
  protected final String TEXT_7 = "][];";
  protected final String TEXT_8 = NL + "      ";
  protected final String TEXT_9 = "[] ";
  protected final String TEXT_10 = "_";
  protected final String TEXT_11 = " = new ";
  protected final String TEXT_12 = "[iRows_";
  protected final String TEXT_13 = "];";
  protected final String TEXT_14 = NL + "    globalMap.put(\"";
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = "\", ";
  protected final String TEXT_17 = "_";
  protected final String TEXT_18 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {

  IMetadataTable metadata = metadatas.get(0);
  List<? extends IConnection> connsIn = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
  IMetadataTable preMetadata = connsIn.get(0).getMetadataTable();
  List<IMetadataColumn> preColumns = preMetadata.getListColumns();
  String sRowsCount = ElementParameterParser.getValue(node, "__ROW_COUNT__");
  
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(sRowsCount);
    stringBuffer.append(TEXT_3);
    
  for (IMetadataColumn column : preColumns){
    String sType = JavaTypesManager.getTypeToGenerate(column.getTalendType(), true);
    String sColumnName = column.getLabel();
    
    if ("byte[]".equals(sType)){
    
    stringBuffer.append(TEXT_4);
    stringBuffer.append(sColumnName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
    } else{
    
    stringBuffer.append(TEXT_8);
    stringBuffer.append(sType);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(sColumnName);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(sType);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    
    }
    
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(sColumnName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sColumnName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    
  }
}

    return stringBuffer.toString();
  }
}
