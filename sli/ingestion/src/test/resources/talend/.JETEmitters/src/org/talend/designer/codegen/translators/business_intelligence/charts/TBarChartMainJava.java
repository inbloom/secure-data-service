package org.talend.designer.codegen.translators.business_intelligence.charts;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TBarChartMainJava
{
  protected static String nl;
  public static synchronized TBarChartMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TBarChartMainJava result = new TBarChartMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    dataset_";
  protected final String TEXT_2 = ".setValue(";
  protected final String TEXT_3 = ".value, ";
  protected final String TEXT_4 = ".series, ";
  protected final String TEXT_5 = ".category);";
  protected final String TEXT_6 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<? extends IConnection> inConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);

if (inConns != null && !inConns.isEmpty()) {
  IConnection inConn = inConns.get(0);
  String sInConnName = inConn.getName();
  List<IMetadataColumn> columns = inConn.getMetadataTable().getListColumns();
  List<String> columnsName = new java.util.ArrayList<String>();
  
  for (IMetadataColumn column : columns) {
    columnsName.add(column.getLabel());
  }
  
  if (columnsName.contains("value") && columnsName.contains("series") && columnsName.contains("category")) {
  
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(sInConnName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(sInConnName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(sInConnName);
    stringBuffer.append(TEXT_5);
    }
}

    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
