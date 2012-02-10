package org.talend.designer.codegen.translators.business_intelligence.charts;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TBarChartEndJava
{
  protected static String nl;
  public static synchronized TBarChartEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TBarChartEndJava result = new TBarChartEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    org.jfree.chart.JFreeChart chart_";
  protected final String TEXT_2 = " = " + NL + "      org.jfree.chart.ChartFactory.";
  protected final String TEXT_3 = "(";
  protected final String TEXT_4 = NL + "        ";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ", ";
  protected final String TEXT_7 = ", dataset_";
  protected final String TEXT_8 = ", ";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = ", false, false" + NL + "    );" + NL + "    " + NL + "    // Foreground value" + NL + "    final org.jfree.chart.plot.Plot plot_";
  protected final String TEXT_11 = " = chart_";
  protected final String TEXT_12 = ".getPlot();" + NL + "    plot_";
  protected final String TEXT_13 = ".setForegroundAlpha(";
  protected final String TEXT_14 = "f);" + NL + "    org.jfree.chart.ChartUtilities.saveChartAsPNG(new java.io.File(";
  protected final String TEXT_15 = "), chart_";
  protected final String TEXT_16 = ", ";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = " ";
  protected final String TEXT_20 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String filePath = ElementParameterParser.getValue(node, "__GENERATED_IMAGE_PATH__");
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
  
    boolean bThreeDFormat = "true".equals(ElementParameterParser.getValue(node, "__3D_FORMAT__"));
    String sChartTitle = ElementParameterParser.getValue(node, "__CHART_TITLE__");
    String sGateGoryAxis = ElementParameterParser.getValue(node, "__CATEGORY_AXIS__");
    String sValueAxis = ElementParameterParser.getValue(node, "__VALUE_AXIS__");
    String sPlotOrientation = ElementParameterParser.getValue(node, "__PLOT_ORIENTATION__");
    String sIncludeLegend = ElementParameterParser.getValue(node, "__INCLUDE_LEGEND__");
    String imageWidth = ElementParameterParser.getValue(node, "__IMAGE_WIDTH__");
    String imageHeight = ElementParameterParser.getValue(node, "__IMAGE_HEIGHT__");

    // create the chart...
    
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append( bThreeDFormat ? "createStackedBarChart3D" : "createBarChart");
    stringBuffer.append(TEXT_3);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(sChartTitle);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(sGateGoryAxis);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(sValueAxis);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(sPlotOrientation);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(sIncludeLegend);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(ElementParameterParser.getValue(node, "__FORE_GROUND_ALPHA__"));
    stringBuffer.append(TEXT_14);
    stringBuffer.append(filePath);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(imageWidth);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(imageHeight);
    stringBuffer.append(TEXT_18);
    
  }
}
    stringBuffer.append(TEXT_19);
    stringBuffer.append(TEXT_20);
    return stringBuffer.toString();
  }
}
