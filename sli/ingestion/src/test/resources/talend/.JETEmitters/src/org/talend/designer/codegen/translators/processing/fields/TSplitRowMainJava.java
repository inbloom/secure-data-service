package org.talend.designer.codegen.translators.processing.fields;

import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.BlockCode;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSplitRowMainJava
{
  protected static String nl;
  public static synchronized TSplitRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSplitRowMainJava result = new TSplitRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  java.util.List<";
  protected final String TEXT_2 = "Struct> rows_";
  protected final String TEXT_3 = " = new java.util.ArrayList<";
  protected final String TEXT_4 = "Struct>(";
  protected final String TEXT_5 = ");";
  protected final String TEXT_6 = NL + "  ";
  protected final String TEXT_7 = "Struct rowTmp_";
  protected final String TEXT_8 = " = null;" + NL + "" + NL + "  // cache output rows for the loop";
  protected final String TEXT_9 = NL + "    rowTmp_";
  protected final String TEXT_10 = " = new ";
  protected final String TEXT_11 = "Struct();" + NL;
  protected final String TEXT_12 = NL + "      rowTmp_";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ";";
  protected final String TEXT_16 = NL + "    rows_";
  protected final String TEXT_17 = ".add(rowTmp_";
  protected final String TEXT_18 = ");" + NL + "    nb_line_";
  protected final String TEXT_19 = "++;";
  protected final String TEXT_20 = NL + NL + "  for (";
  protected final String TEXT_21 = "Struct row_";
  protected final String TEXT_22 = " : rows_";
  protected final String TEXT_23 = ") {// C_01";
  protected final String TEXT_24 = NL + "    ";
  protected final String TEXT_25 = " = row_";
  protected final String TEXT_26 = ";";
  protected final String TEXT_27 = " ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();  
List<? extends IConnection> inConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
List<? extends IConnection> outConns = node.getOutgoingConnections(EConnectionType.FLOW_MAIN);

if (inConns == null || inConns.isEmpty() || outConns == null || outConns.isEmpty()){
  return "";
}
IConnection inConn = null, outConn = null;
inConn = inConns.get(0);
outConn = outConns.get(0);

if (!inConn.isActivate() || !outConn.isActivate()){
  return "";
}
String inConnName = inConn.getName();
String outConnName = outConn.getName();

if (!inConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)
      || !outConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA) ){
  return "";
}
List<IMetadataColumn> outColumns = outConn.getMetadataTable().getListColumns();
List<Map<String, String>> listColMapping = (List<Map<String, String>>)ElementParameterParser.getObjectValue(node, "__COL_MAPPING__");

if (listColMapping != null && !listColMapping.isEmpty()){
  int rowNumber = listColMapping.size();
  
    stringBuffer.append(TEXT_1);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(rowNumber);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    for (Map<String, String> colMapping : listColMapping) {
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_11);
    for (IMetadataColumn outColumn : outColumns) {
      String columnName = outColumn.getLabel();
      String value = colMapping.get(columnName);
      
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(value);
    stringBuffer.append(TEXT_15);
    }
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    }
    stringBuffer.append(TEXT_20);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(outConnName);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    
    List<BlockCode> blockCodes = new java.util.ArrayList<BlockCode>(1);
  blockCodes.add(new BlockCode("C_01"));
  ((org.talend.core.model.process.AbstractNode) node).setBlocksCodeToClose(blockCodes);
}

    stringBuffer.append(TEXT_27);
    return stringBuffer.toString();
  }
}
