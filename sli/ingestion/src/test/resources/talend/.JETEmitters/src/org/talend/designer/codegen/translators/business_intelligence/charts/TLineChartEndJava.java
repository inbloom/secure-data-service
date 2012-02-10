package org.talend.designer.codegen.translators.business_intelligence.charts;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TLineChartEndJava
{
  protected static String nl;
  public static synchronized TLineChartEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLineChartEndJava result = new TLineChartEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "        for (org.jfree.data.xy.XYSeries serie: series";
  protected final String TEXT_2 = ".values()){" + NL + "          dataset";
  protected final String TEXT_3 = ".addSeries(serie);" + NL + "        }" + NL + "" + NL + "        // create the chart..." + NL + "        org.jfree.chart.JFreeChart chart";
  protected final String TEXT_4 = " = org.jfree.chart.ChartFactory.createXYLineChart(";
  protected final String TEXT_5 = ",";
  protected final String TEXT_6 = NL + "                ";
  protected final String TEXT_7 = ",";
  protected final String TEXT_8 = NL + "                ";
  protected final String TEXT_9 = "," + NL + "                dataset";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = NL + "                ";
  protected final String TEXT_12 = ",";
  protected final String TEXT_13 = NL + "                ";
  protected final String TEXT_14 = "," + NL + "                true," + NL + "                false" + NL + "                );" + NL + "" + NL + "        final org.jfree.chart.plot.XYPlot plot";
  protected final String TEXT_15 = " = (org.jfree.chart.plot.XYPlot) chart";
  protected final String TEXT_16 = ".getPlot();" + NL;
  protected final String TEXT_17 = NL + "        plot";
  protected final String TEXT_18 = ".setBackgroundPaint(";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + "        chart";
  protected final String TEXT_21 = ".setBackgroundPaint(";
  protected final String TEXT_22 = ");";
  protected final String TEXT_23 = NL;
  protected final String TEXT_24 = NL + "            plot";
  protected final String TEXT_25 = ".getRangeAxis().setLowerBound(";
  protected final String TEXT_26 = ");";
  protected final String TEXT_27 = NL + "            plot";
  protected final String TEXT_28 = ".getRangeAxis().setUpperBound(";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL;
  protected final String TEXT_31 = NL + "        org.jfree.data.xy.XYDataset movingAverage";
  protected final String TEXT_32 = " = org.jfree.data.time.MovingAverage.createMovingAverage(dataset";
  protected final String TEXT_33 = ", \" (average)\", ";
  protected final String TEXT_34 = ", 10);" + NL + "        plot";
  protected final String TEXT_35 = ".setDataset(1, movingAverage";
  protected final String TEXT_36 = ");" + NL + "        plot";
  protected final String TEXT_37 = ".setRenderer(1, new org.jfree.chart.renderer.xy.StandardXYItemRenderer());";
  protected final String TEXT_38 = NL + NL + "        try {" + NL + "            org.jfree.chart.ChartUtilities.saveChartAsPNG(new java.io.File(";
  protected final String TEXT_39 = "), chart";
  protected final String TEXT_40 = ", ";
  protected final String TEXT_41 = ", ";
  protected final String TEXT_42 = ");" + NL + "        } catch (java.io.IOException e) {" + NL + "            // TODO Auto-generated catch block" + NL + "            e.printStackTrace();" + NL + "        }";
  protected final String TEXT_43 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String filePath = ElementParameterParser.getValue(node,"__GENERATED_IMAGE_PATH__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(ElementParameterParser.getValue(node,"__CHART_TITLE__"));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(ElementParameterParser.getValue(node,"__DOMAIN_AXIS_LABEL__"));
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(ElementParameterParser.getValue(node,"__RANGE_AXIS_LABEL__"));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(ElementParameterParser.getValue(node,"__PLOT_ORIENTATION__"));
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    stringBuffer.append("true".equals(ElementParameterParser.getValue(node,"__INCLUDE_LEGEND__")));
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    if (!("").equals(ElementParameterParser.getValue(node,"__BACKGROUND_PLOT__"))){
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(ElementParameterParser.getValue(node,"__BACKGROUND_PLOT__"));
    stringBuffer.append(TEXT_19);
    }
    if (!("").equals(ElementParameterParser.getValue(node,"__BACKGROUND_CHART__"))){
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(ElementParameterParser.getValue(node,"__BACKGROUND_CHART__"));
    stringBuffer.append(TEXT_22);
    }
    stringBuffer.append(TEXT_23);
    if (!("").equals(ElementParameterParser.getValue(node,"__LOWER_BOUND__"))){
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(ElementParameterParser.getValue(node,"__LOWER_BOUND__"));
    stringBuffer.append(TEXT_26);
    }
    if (!("").equals(ElementParameterParser.getValue(node,"__UPPER_BOUND__"))) {
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(ElementParameterParser.getValue(node,"__UPPER_BOUND__"));
    stringBuffer.append(TEXT_29);
    }
    stringBuffer.append(TEXT_30);
    if (("true").equals(ElementParameterParser.getValue(node,"__MOVING_AVERAGE__"))){
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(ElementParameterParser.getValue(node,"__MOVING_AVERAGE_PERIOD__"));
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    }
    stringBuffer.append(TEXT_38);
    stringBuffer.append(filePath);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(ElementParameterParser.getValue(node,"__IMAGE_WIDTH__"));
    stringBuffer.append(TEXT_41);
    stringBuffer.append(ElementParameterParser.getValue(node,"__IMAGE_HEIGHT__"));
    stringBuffer.append(TEXT_42);
    stringBuffer.append(TEXT_43);
    return stringBuffer.toString();
  }
}
