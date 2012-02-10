package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.utils.NodeUtil;

public class TFileOutputMSXMLEndJava
{
  protected static String nl;
  public static synchronized TFileOutputMSXMLEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputMSXMLEndJava result = new TFileOutputMSXMLEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.io.FileOutputStream stream_";
  protected final String TEXT_3 = " = new java.io.FileOutputStream(fileName_";
  protected final String TEXT_4 = ");" + NL + " \t" + NL + "\torg.dom4j.io.XMLWriter output_";
  protected final String TEXT_5 = " = null;" + NL + "\tif(isXML10Char_";
  protected final String TEXT_6 = ") {" + NL + "\t\toutput_";
  protected final String TEXT_7 = " = new org.dom4j.io.XMLWriter(stream_";
  protected final String TEXT_8 = ", format_";
  protected final String TEXT_9 = ");" + NL + "\t} else {" + NL + "\t\toutput_";
  protected final String TEXT_10 = " = new org.dom4j.io.XMLWriter(" + NL + "\t\t\t\t\t\t\tstream_";
  protected final String TEXT_11 = ", format_";
  protected final String TEXT_12 = ") {" + NL + "\t\t\t\t\t\t" + NL + "\t\t\tprivate org.dom4j.io.OutputFormat format = null;" + NL + "\t\t\t" + NL + "\t\t\t{" + NL + "\t\t\t\tformat = format_";
  protected final String TEXT_13 = ";" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\tpublic void writeDeclaration() throws IOException {" + NL + "\t\t        String encoding = format.getEncoding();" + NL + "" + NL + "\t\t        // Only print of declaration is not suppressed" + NL + "\t\t        if (!format.isSuppressDeclaration()) {" + NL + "\t\t            // Assume 1.0 version" + NL + "\t\t            if (encoding.equals(\"UTF8\")) {" + NL + "\t\t                writer.write(\"<?xml version=\\\"1.1\\\"\");" + NL + "" + NL + "\t\t                if (!format.isOmitEncoding()) {" + NL + "\t\t                    writer.write(\" encoding=\\\"UTF-8\\\"\");" + NL + "\t\t                }" + NL + "" + NL + "\t\t                writer.write(\"?>\");" + NL + "\t\t            } else {" + NL + "\t\t                writer.write(\"<?xml version=\\\"1.1\\\"\");" + NL + "" + NL + "\t\t                if (!format.isOmitEncoding()) {" + NL + "\t\t                    writer.write(\" encoding=\\\"\" + encoding + \"\\\"\");" + NL + "\t\t                }" + NL + "" + NL + "\t\t                writer.write(\"?>\");" + NL + "\t\t            }" + NL + "" + NL + "\t\t            if (format.isNewLineAfterDeclaration()) {" + NL + "\t\t                println();" + NL + "\t\t            }" + NL + "\t\t        }" + NL + "\t\t    }" + NL + "\t\t};" + NL + "\t}" + NL + "\t" + NL + "    nestXMLTool_";
  protected final String TEXT_14 = ".replaceDefaultNameSpace(doc_";
  protected final String TEXT_15 = ".getRootElement());";
  protected final String TEXT_16 = NL + "    nestXMLTool_";
  protected final String TEXT_17 = ".removeEmptyElement(doc_";
  protected final String TEXT_18 = ".getRootElement());";
  protected final String TEXT_19 = NL + "    output_";
  protected final String TEXT_20 = ".write(doc_";
  protected final String TEXT_21 = ");" + NL + "    output_";
  protected final String TEXT_22 = ".close();" + NL + "    " + NL + "    globalMap.put(\"";
  protected final String TEXT_23 = "_NB_LINE\",nb_line_";
  protected final String TEXT_24 = ");" + NL;
  protected final String TEXT_25 = NL + "\tif(nb_line_";
  protected final String TEXT_26 = " == 0){" + NL + "\t\tnew java.io.File(";
  protected final String TEXT_27 = ").delete();" + NL + "\t}";
  protected final String TEXT_28 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    

// ------------------- *** Common code start *** ------------------- //
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String allowEmpty = ElementParameterParser.getValue(node, "__CREATE_EMPTY_ELEMENT__");

String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
String cid = node.getUniqueName();
String cid_original = cid;
cid = cid_original.replace("tFileOutputXMLMultiSchema","tOXMLMS");

String filename = ElementParameterParser.getValue(node, "__FILENAME__");
boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));

List<IMetadataTable> metadatas = NodeUtil.getIncomingMetadataTable(node, IConnectionCategory.FLOW);
if ((metadatas!=null)&&(metadatas.size()>0)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
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
    
	if(!("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    
	}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    	if(isDeleteEmptyFile){
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_27);
    
	}
}

    stringBuffer.append(TEXT_28);
    return stringBuffer.toString();
  }
}
