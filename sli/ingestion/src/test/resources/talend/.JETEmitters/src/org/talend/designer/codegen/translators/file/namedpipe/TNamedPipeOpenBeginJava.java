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

public class TNamedPipeOpenBeginJava
{
  protected static String nl;
  public static synchronized TNamedPipeOpenBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TNamedPipeOpenBeginJava result = new TNamedPipeOpenBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tString namedPipeName_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "\tString namedPipeNativeName_";
  protected final String TEXT_5 = " = (new com.infobright.io.NamedPipeFactory()).getNativePipeName(namedPipeName_";
  protected final String TEXT_6 = ");" + NL + "\t";
  protected final String TEXT_7 = NL + "\t\tjava.io.File file_";
  protected final String TEXT_8 = " = new java.io.File(namedPipeNativeName_";
  protected final String TEXT_9 = ");" + NL + "\t\tif (file_";
  protected final String TEXT_10 = ".exists())" + NL + "\t\t\tfile_";
  protected final String TEXT_11 = ".delete();" + NL + "\t";
  protected final String TEXT_12 = NL + NL + "\tcom.infobright.io.NamedPipe outputStream_";
  protected final String TEXT_13 = " = (new com.infobright.io.NamedPipeFactory()).createServer(namedPipeName_";
  protected final String TEXT_14 = ", true, true, 1024000, 10000);" + NL + "" + NL + "\t// in Windows, wait for named pipe to get opened" + NL + "\tif ((new com.infobright.io.NamedPipeFactory()).isWindowsMode()) { " + NL + "\t\tThread.sleep(10000);" + NL + "\t}" + NL + "" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_15 = "_PIPE_NAME\", namedPipeName_";
  protected final String TEXT_16 = ");" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_17 = "_PIPE_NATIVE_NAME\",  namedPipeNativeName_";
  protected final String TEXT_18 = ");" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_19 = "_PIPE_OUTPUTSTREAM\", outputStream_";
  protected final String TEXT_20 = ");" + NL;
  protected final String TEXT_21 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String namedPipeName = ElementParameterParser.getValue(node, "__NAMEDPIPE_NAME__");
	boolean deleteIfExists = ElementParameterParser.getValue(node, "__DELETE_IF_EXISTS__").equals("true");
	

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(namedPipeName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
     if (deleteIfExists) { 
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
     } 
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
