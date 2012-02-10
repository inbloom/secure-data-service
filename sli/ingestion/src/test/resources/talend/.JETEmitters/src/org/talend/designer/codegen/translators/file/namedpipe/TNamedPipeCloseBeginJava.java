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

public class TNamedPipeCloseBeginJava
{
  protected static String nl;
  public static synchronized TNamedPipeCloseBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TNamedPipeCloseBeginJava result = new TNamedPipeCloseBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tcom.infobright.io.NamedPipe pipe_";
  protected final String TEXT_3 = " = (com.infobright.io.NamedPipe)globalMap.get(\"";
  protected final String TEXT_4 = "_PIPE_OUTPUTSTREAM\");" + NL + "\tString pipeName_";
  protected final String TEXT_5 = " = (String)globalMap.get(\"";
  protected final String TEXT_6 = "_PIPE_NAME\");" + NL + "\tString pipeNativeName_";
  protected final String TEXT_7 = " = (String)globalMap.get(\"";
  protected final String TEXT_8 = "_PIPE_NATIVE_NAME\");" + NL + "" + NL + "\ttry {" + NL + "\t\tpipe_";
  protected final String TEXT_9 = ".disconnect();" + NL + "\t} " + NL + "\tcatch (Exception e) {}" + NL + "\tfinally {" + NL + "\t\tpipe_";
  protected final String TEXT_10 = " = null;" + NL + "\t}" + NL + "" + NL + "\tjava.io.File file_";
  protected final String TEXT_11 = " = new java.io.File(pipeNativeName_";
  protected final String TEXT_12 = ");" + NL + "\tif (file_";
  protected final String TEXT_13 = ".exists())" + NL + "\t\tfile_";
  protected final String TEXT_14 = ".delete();" + NL + NL;
  protected final String TEXT_15 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String pipe = ElementParameterParser.getValue(node, "__PIPE__");
	

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(pipe);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(pipe);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(pipe);
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
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
