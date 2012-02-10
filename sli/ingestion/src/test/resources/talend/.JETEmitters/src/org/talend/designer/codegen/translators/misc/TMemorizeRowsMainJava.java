package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TMemorizeRowsMainJava
{
  protected static String nl;
  public static synchronized TMemorizeRowsMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMemorizeRowsMainJava result = new TMemorizeRowsMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    for (int i_";
  protected final String TEXT_2 = " = iRows_";
  protected final String TEXT_3 = " - 1 ; i_";
  protected final String TEXT_4 = " > 0; i_";
  protected final String TEXT_5 = "--) {";
  protected final String TEXT_6 = NL + "        ";
  protected final String TEXT_7 = "_";
  protected final String TEXT_8 = "[i_";
  protected final String TEXT_9 = "] = ";
  protected final String TEXT_10 = "_";
  protected final String TEXT_11 = "[i_";
  protected final String TEXT_12 = " - 1];  ";
  protected final String TEXT_13 = NL + "    }";
  protected final String TEXT_14 = NL + "      ";
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = "[0] = ";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = ";    ";
  protected final String TEXT_19 = NL + "globalMap.put(\"";
  protected final String TEXT_20 = "_NB_LINE_ROWS\", iRows_";
  protected final String TEXT_21 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();

if ((metadatas != null) && (metadatas.size() > 0)) {
  IMetadataTable metadata = metadatas.get(0);
  IConnection connIn = node.getIncomingConnections(EConnectionType.FLOW_MAIN).get(0);
  String sFlowName = connIn.getName();
  List<IMetadataColumn> preColumns = connIn.getMetadataTable().getListColumns();
  List<Map<String, String>> listTableCols = (List<Map<String, String>>)ElementParameterParser.getObjectValue(node, "__SPECIFY_COLS__");
  List<String> listCheckedColsName = null;
  
  for (IMetadataColumn column : preColumns){
    String sColumnName = column.getLabel();
    int iInColIndex = preColumns.indexOf(column);
    Map<String, String> checkedColumn = listTableCols.get(iInColIndex);
    boolean bMemorize = "true".equals(checkedColumn.get("MEMORIZE_IT")); 
    
    if (bMemorize){
      if (listCheckedColsName == null){
        listCheckedColsName = new java.util.ArrayList();
      }
      listCheckedColsName.add(sColumnName);
    }
  }
  
  if (listCheckedColsName != null){
  
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
      for (String columnName : listCheckedColsName){
      
    stringBuffer.append(TEXT_6);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
      }
      
    stringBuffer.append(TEXT_13);
    
    for (String columnName : listCheckedColsName){
    
    stringBuffer.append(TEXT_14);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sFlowName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_18);
    
    }
  }
}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
