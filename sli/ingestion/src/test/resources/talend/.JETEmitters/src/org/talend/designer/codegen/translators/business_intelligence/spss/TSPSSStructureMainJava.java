package org.talend.designer.codegen.translators.business_intelligence.spss;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TSPSSStructureMainJava
{
  protected static String nl;
  public static synchronized TSPSSStructureMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSPSSStructureMainJava result = new TSPSSStructureMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = " = new ";
  protected final String TEXT_3 = "Struct();" + NL + "\torg.talend.jspss.spssvariable ";
  protected final String TEXT_4 = "_spVar = ";
  protected final String TEXT_5 = "_spVars.getVariabelAtPos(i);" + NL + "\tnb_line_";
  protected final String TEXT_6 = " ++;" + NL + "\t";
  protected final String TEXT_7 = ".Name=";
  protected final String TEXT_8 = "_spVar.getName();" + NL + "\t";
  protected final String TEXT_9 = ".Type=";
  protected final String TEXT_10 = "_spVar.getStringType();" + NL + "\t";
  protected final String TEXT_11 = ".Width=";
  protected final String TEXT_12 = "_spVar.getDecimals();" + NL + "\t";
  protected final String TEXT_13 = ".Decimals=";
  protected final String TEXT_14 = "_spVar.getPrecision();" + NL + "\t";
  protected final String TEXT_15 = ".Lable=";
  protected final String TEXT_16 = "_spVar.getLabel();" + NL + "\t";
  protected final String TEXT_17 = ".Values=\"None\";" + NL + "\t";
  protected final String TEXT_18 = ".Missing=null;" + NL + "\t";
  protected final String TEXT_19 = ".Columns=";
  protected final String TEXT_20 = "_spVar.getWidth();" + NL + "\t";
  protected final String TEXT_21 = ".Align=";
  protected final String TEXT_22 = "_spVar.getStringAlign();" + NL + "\t";
  protected final String TEXT_23 = ".Measure=";
  protected final String TEXT_24 = "_spVar.getStringMeasure();" + NL + "\t" + NL + "\tif(";
  protected final String TEXT_25 = "_spVar.hasLabels()) ";
  protected final String TEXT_26 = ".Values=";
  protected final String TEXT_27 = "_spVar.getSPSSValueLable().getValuesAsString();" + NL;
  protected final String TEXT_28 = NL;
  protected final String TEXT_29 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
String filename = ElementParameterParser.getValue(node, "__FILENAME__");

String outputConnName = null;
List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
if (conns!=null) {
	if (conns.size()>0) {
		for (int i=0;i<conns.size();i++) {
			IConnection connTemp = conns.get(i);
			if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				outputConnName = connTemp.getName();
				break;
			}
		}
	}
}

if (outputConnName != null){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    
}

    stringBuffer.append(TEXT_28);
    stringBuffer.append(TEXT_29);
    return stringBuffer.toString();
  }
}
