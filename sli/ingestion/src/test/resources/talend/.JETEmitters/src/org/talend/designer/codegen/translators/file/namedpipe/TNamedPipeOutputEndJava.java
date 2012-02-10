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

public class TNamedPipeOutputEndJava
{
  protected static String nl;
  public static synchronized TNamedPipeOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TNamedPipeOutputEndJava result = new TNamedPipeOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\toutputStream_";
  protected final String TEXT_4 = ".flush();" + NL + "\toutputStream_";
  protected final String TEXT_5 = ".close();" + NL + "\toutputStream_";
  protected final String TEXT_6 = " = null;";
  protected final String TEXT_7 = NL + NL + "\t";
  protected final String TEXT_8 = NL;

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

if (nullText == null || "".equals(nullText)) {
	nullText = "\"\"";
}

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

    stringBuffer.append(TEXT_2);
    
if (hasInputRow && columnList != null) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
