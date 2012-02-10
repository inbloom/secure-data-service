package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TMDMBulkLoadBeginJava
{
  protected static String nl;
  public static synchronized TMDMBulkLoadBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMDMBulkLoadBeginJava result = new TMDMBulkLoadBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "org.talend.mdm.bulkload.client.BulkloadClient bulkloadClient_";
  protected final String TEXT_2 = " = new org.talend.mdm.bulkload.client.BulkloadClient(";
  protected final String TEXT_3 = ", ";
  protected final String TEXT_4 = ", ";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ", ";
  protected final String TEXT_7 = ", ";
  protected final String TEXT_8 = ", ";
  protected final String TEXT_9 = ");" + NL + "bulkloadClient_";
  protected final String TEXT_10 = ".startThreadCount();" + NL + "bulkloadClient_";
  protected final String TEXT_11 = ".setOptions(new org.talend.mdm.bulkload.client.BulkloadOptions(";
  protected final String TEXT_12 = ", ";
  protected final String TEXT_13 = ", ";
  protected final String TEXT_14 = "));" + NL + "int count_";
  protected final String TEXT_15 = " = 0;" + NL + "org.talend.mdm.bulkload.client.InputStreamMerger inputStreamMerger_";
  protected final String TEXT_16 = " = null;";
  protected final String TEXT_17 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String mdmUrl = ElementParameterParser.getValue(node, "__MDMURL__");
	String username = ElementParameterParser.getValue(node, "__USERNAME__");
	String password = ElementParameterParser.getValue(node, "__PASSWORD__");
	String version = ElementParameterParser.getValue(node, "__UNIVERSE__");
	String dataModule = ElementParameterParser.getValue(node, "__DATAMODEL__");
	String dataCluster = ElementParameterParser.getValue(node, "__DATACLUSTER__");
	String dataEntity = ElementParameterParser.getValue(node, "__DATACONCEPT__");
	String arraySize = ElementParameterParser.getValue(node,"__MASS_LEVEL__");
	
	boolean validate = ("true").equals(ElementParameterParser.getValue(node,"__VALIDATE__"));
	boolean isGenerateId = ("true").equals(ElementParameterParser.getValue(node,"__GENERATE_ID__"));
	

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(mdmUrl);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(version==null || "".equals(version)? "null":version);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(dataCluster );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dataEntity );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dataModule);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(isGenerateId );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(validate );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(arraySize);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(TEXT_17);
    return stringBuffer.toString();
  }
}
