package org.talend.designer.codegen.translators.business_intelligence.charts;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.EConnectionType;
import java.util.List;

public class TLineChartMainJava
{
  protected static String nl;
  public static synchronized TLineChartMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLineChartMainJava result = new TLineChartMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t\tString serieLabel";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = ";" + NL + "\t\t\t\torg.jfree.data.xy.XYSeries serie";
  protected final String TEXT_5 = ";" + NL + "\t" + NL + "\t\t\t\tif (series";
  protected final String TEXT_6 = ".containsKey(serieLabel";
  protected final String TEXT_7 = ")) {" + NL + "\t\t\t\t\tserie";
  protected final String TEXT_8 = " = series";
  protected final String TEXT_9 = ".get(serieLabel";
  protected final String TEXT_10 = ");" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\tserie";
  protected final String TEXT_11 = " = new org.jfree.data.xy.XYSeries(serieLabel";
  protected final String TEXT_12 = ", true, false);" + NL + "\t\t\t\t\tseries";
  protected final String TEXT_13 = ".put(serieLabel";
  protected final String TEXT_14 = ", serie";
  protected final String TEXT_15 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t\tserie";
  protected final String TEXT_16 = ".add(";
  protected final String TEXT_17 = ".x, ";
  protected final String TEXT_18 = ".y);" + NL + "\t\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List< ? extends IConnection> conns = node.getIncomingConnections();

if (conns != null && !conns.isEmpty()) {

	for (IConnection conn : conns) {

		if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) && conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			List<IMetadataTable> metadatas = node.getMetadataList();
			
			if (metadatas != null && !metadatas.isEmpty()) {
				// C_01 make old job works with out a migration
				String sFieldName = "series";
			    IMetadataTable metadata = metadatas.get(0);
			    List<IMetadataColumn> columns = metadata.getListColumns();
			    
			    for (IMetadataColumn column : columns) {
					
					if ("serie".equals(column.getLabel())) {
						sFieldName = "serie";
						break;
					}
				}
				// C_01
				
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(sFieldName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_17);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_18);
    
			}
		}
	}
}

    return stringBuffer.toString();
  }
}
