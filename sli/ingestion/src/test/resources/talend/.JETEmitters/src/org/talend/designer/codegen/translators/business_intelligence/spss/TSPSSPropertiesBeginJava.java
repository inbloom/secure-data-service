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

public class TSPSSPropertiesBeginJava
{
  protected static String nl;
  public static synchronized TSPSSPropertiesBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSPSSPropertiesBeginJava result = new TSPSSPropertiesBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "boolean ";
  protected final String TEXT_2 = "_bIsValidFile=false;";
  protected final String TEXT_3 = NL + "\t" + NL + "" + NL + "\tjava.io.File ";
  protected final String TEXT_4 = "_file = new java.io.File(";
  protected final String TEXT_5 = ");" + NL + "\t";
  protected final String TEXT_6 = " = new ";
  protected final String TEXT_7 = "Struct();" + NL + "\tif(";
  protected final String TEXT_8 = "_file.exists() && ";
  protected final String TEXT_9 = ".toLowerCase().contains(\"sav\")){" + NL + "\t\torg.talend.jspss.spss ";
  protected final String TEXT_10 = "_sp = new org.talend.jspss.spss();" + NL + "\t\torg.talend.jspss.spssfile ";
  protected final String TEXT_11 = "_sf = ";
  protected final String TEXT_12 = "_sp.openFile(";
  protected final String TEXT_13 = ");" + NL + "\t\t";
  protected final String TEXT_14 = ".abs_path=";
  protected final String TEXT_15 = "_file.getAbsolutePath();" + NL + "\t\t";
  protected final String TEXT_16 = ".dirname=";
  protected final String TEXT_17 = "_file.getParent();" + NL + "\t\t";
  protected final String TEXT_18 = ".basename=";
  protected final String TEXT_19 = "_file.getName();" + NL + "\t\t";
  protected final String TEXT_20 = ".system_info=";
  protected final String TEXT_21 = "_sf.getSystemInfo();" + NL + "\t\t";
  protected final String TEXT_22 = ".system_id=";
  protected final String TEXT_23 = "_sf.getSystemID();" + NL + "\t\t";
  protected final String TEXT_24 = ".file_date_time=";
  protected final String TEXT_25 = "_sf.getFileDateTime();" + NL + "\t\t";
  protected final String TEXT_26 = ".weight_variable=";
  protected final String TEXT_27 = "_sf.getCaseWeightVariable();" + NL + "\t\t";
  protected final String TEXT_28 = ".is_compressed=";
  protected final String TEXT_29 = "_sf.getIsCompressed();" + NL + "\t\t";
  protected final String TEXT_30 = ".variable_sets=";
  protected final String TEXT_31 = "_sf.getVariableSets();" + NL + "\t\t";
  protected final String TEXT_32 = ".number_of_variables=(long)";
  protected final String TEXT_33 = "_sf.getNumberOfVariables();" + NL + "\t\t";
  protected final String TEXT_34 = ".number_of_cases=(long)";
  protected final String TEXT_35 = "_sf.getNumberOfCases();" + NL + "\t\t";
  protected final String TEXT_36 = "_bIsValidFile=true;\t\t" + NL + "\t";
  protected final String TEXT_37 = NL;
  protected final String TEXT_38 = NL;

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

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
if (outputConnName != null){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
}

    stringBuffer.append(TEXT_37);
    stringBuffer.append(TEXT_38);
    return stringBuffer.toString();
  }
}
