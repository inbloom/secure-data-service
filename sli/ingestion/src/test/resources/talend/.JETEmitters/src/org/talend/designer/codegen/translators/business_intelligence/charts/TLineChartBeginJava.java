package org.talend.designer.codegen.translators.business_intelligence.charts;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TLineChartBeginJava
{
  protected static String nl;
  public static synchronized TLineChartBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLineChartBeginJava result = new TLineChartBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "//org.jfree.data.category.DefaultCategoryDataset dataset = new org.jfree.data.category.DefaultCategoryDataset();" + NL + "org.jfree.data.xy.XYSeriesCollection dataset";
  protected final String TEXT_2 = " = new org.jfree.data.xy.XYSeriesCollection();" + NL + "" + NL + "java.util.Map<String,org.jfree.data.xy.XYSeries> series";
  protected final String TEXT_3 = " = new java.util.HashMap<String,org.jfree.data.xy.XYSeries>();" + NL;
  protected final String TEXT_4 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
