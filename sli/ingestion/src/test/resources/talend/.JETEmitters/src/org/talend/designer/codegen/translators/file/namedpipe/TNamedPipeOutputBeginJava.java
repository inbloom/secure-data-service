package org.talend.designer.codegen.translators.file.namedpipe;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.MappingTypeRetriever;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;

public class TNamedPipeOutputBeginJava
{
  protected static String nl;
  public static synchronized TNamedPipeOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TNamedPipeOutputBeginJava result = new TNamedPipeOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tboolean missConfiguration_";
  protected final String TEXT_3 = " = true;" + NL + "\tString missConfigurationMsg_";
  protected final String TEXT_4 = " = \"";
  protected final String TEXT_5 = "\";" + NL + "\tif (missConfiguration_";
  protected final String TEXT_6 = ") {" + NL + "\t\tthrow new Exception(missConfigurationMsg_";
  protected final String TEXT_7 = ");" + NL + "\t}" + NL + "\t";
  protected final String TEXT_8 = NL + NL + "\t";
  protected final String TEXT_9 = NL + "\t\tString namedPipeName_";
  protected final String TEXT_10 = " = ";
  protected final String TEXT_11 = ";" + NL + "\t\tString namedPipeNativeName_";
  protected final String TEXT_12 = " = (new com.infobright.io.NamedPipeFactory()).getNativePipeName(namedPipeName_";
  protected final String TEXT_13 = ");" + NL + "\t\t";
  protected final String TEXT_14 = NL + "\t\t\tjava.io.File file_";
  protected final String TEXT_15 = " = new java.io.File(namedPipeNativeName_";
  protected final String TEXT_16 = ");" + NL + "\t\t\tif (file_";
  protected final String TEXT_17 = ".exists())" + NL + "\t\t\t\tfile_";
  protected final String TEXT_18 = ".delete();" + NL + "\t\t";
  protected final String TEXT_19 = NL + "\t\tcom.infobright.io.NamedPipe outputStream_";
  protected final String TEXT_20 = " = (new com.infobright.io.NamedPipeFactory()).createServer(";
  protected final String TEXT_21 = ", true, true, 1024000, 10000);" + NL + "" + NL + "\t\t// in Windows wait for named pipe to get opened" + NL + "\t\tif ((new com.infobright.io.NamedPipeFactory()).isWindowsMode()) { " + NL + "\t\t\tThread.sleep(10000);" + NL + "\t\t}" + NL + "\t";
  protected final String TEXT_22 = NL + "\t\tString namedPipeName_";
  protected final String TEXT_23 = " = (String)globalMap.get(\"";
  protected final String TEXT_24 = "_PIPE_NAME\");" + NL + "\t\tString namedPipeNativeName_";
  protected final String TEXT_25 = " = (String)globalMap.get(\"";
  protected final String TEXT_26 = "_PIPE_NATIVE_NAME\");" + NL + "\t\tcom.infobright.io.NamedPipe outputStream_";
  protected final String TEXT_27 = " = (com.infobright.io.NamedPipe)globalMap.get(\"";
  protected final String TEXT_28 = "_PIPE_OUTPUTSTREAM\");" + NL + "\t";
  protected final String TEXT_29 = NL + NL + "\t" + NL + "\tint rowCout_";
  protected final String TEXT_30 = " = 0;" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_31 = "_PIPE_NAME\", namedPipeName_";
  protected final String TEXT_32 = ");" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_33 = "_PIPE_NATIVE_NAME\",  namedPipeNativeName_";
  protected final String TEXT_34 = ");" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_35 = "_PIPE_OUTPUTSTREAM\", outputStream_";
  protected final String TEXT_36 = ");";
  protected final String TEXT_37 = NL + "\t" + NL;
  protected final String TEXT_38 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

boolean useExistingPipe = ElementParameterParser.getValue(node, "__USE_EXISTING_PIPE__").equals("true");
String existingPipe = ElementParameterParser.getValue(node, "__PIPE__");
String namedPipeName = ElementParameterParser.getValue(node, "__NAMEDPIPE_NAME__");
String rowSeparator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
String fieldSeparator = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
boolean csv = ElementParameterParser.getValue(node, "__CSV__").equals("true");
String escapeChar = ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
String quoteChar = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
boolean quoteAllFields = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE_OPTION__").equals("ALL");
boolean deleteIfExists = ElementParameterParser.getValue(node, "__DELETE_IF_EXISTS__").equals("true");
String nullText = ElementParameterParser.getValue(node, "__NULL_TEXT__");
String boolType = ElementParameterParser.getValue(node, "__BOOLEAN_TYPE__");

List<IMetadataTable> metadatas = node.getMetadataList();
List<IMetadataColumn> columnList = null;
if(metadatas != null && metadatas.size() > 0) {
    IMetadataTable metadata = metadatas.get(0);
    if(metadata != null) {
        columnList = metadata.getListColumns();
    }
}
List< ? extends IConnection> inputConns = node.getIncomingConnections();
List< ? extends IConnection> outputConns = node.getOutgoingConnections();
boolean hasInputRow = false;
boolean hasOutputRow = false;
if (inputConns != null || inputConns.size() > 0) {
	for(IConnection conn : inputConns) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))
			if(!hasInputRow)
				hasInputRow = true;
	}
}
if (outputConns != null || outputConns.size() > 0) {
	for(IConnection conn : outputConns) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))
			if(!hasOutputRow)
				hasOutputRow = true;
	}
}
String inputRowName = (hasInputRow) ? inputConns.get(0).getName() : null;
String outputRowName = (hasOutputRow) ? outputConns.get(0).getName() : null;

// -----------------------------------------------------------
// basic setup check:
// 		if the component is used with named-piped option, it must have an input row
//		if the component is used as a start component, then it must not have an output row
// -----------------------------------------------------------
boolean missConfiguration = false;
String  missConfigurationMsg = "";
if (hasOutputRow && !hasInputRow) {
	missConfiguration = true;
	missConfigurationMsg = cid + " is miss configured. This component can only have an output row when it has an input row.";
}
if (missConfiguration) {
	
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(missConfigurationMsg);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
}

    stringBuffer.append(TEXT_8);
    
if (hasInputRow && columnList != null) {

	if (useExistingPipe == false) { 
	
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(namedPipeName);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
     if (deleteIfExists) { 
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
     } 
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(namedPipeName);
    stringBuffer.append(TEXT_21);
     } else { 
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(existingPipe);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(existingPipe);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(existingPipe);
    stringBuffer.append(TEXT_28);
     } 
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
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
