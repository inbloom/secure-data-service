package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TChangeFileEncodingMainJava
{
  protected static String nl;
  public static synchronized TChangeFileEncodingMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TChangeFileEncodingMainJava result = new TChangeFileEncodingMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "String sOutFileN_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = ", sOriFileN_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "java.io.File outFile_";
  protected final String TEXT_6 = " = new java.io.File(sOutFileN_";
  protected final String TEXT_7 = ");" + NL;
  protected final String TEXT_8 = NL + "  //create directory only if not exists" + NL + "  java.io.File parentFile_";
  protected final String TEXT_9 = " = outFile_";
  protected final String TEXT_10 = ".getParentFile();" + NL + "" + NL + "  if (parentFile_";
  protected final String TEXT_11 = " != null && !parentFile_";
  protected final String TEXT_12 = ".exists()) {" + NL + "    parentFile_";
  protected final String TEXT_13 = ".mkdirs();" + NL + "  }";
  protected final String TEXT_14 = NL + "final java.io.BufferedWriter out";
  protected final String TEXT_15 = " = new java.io.BufferedWriter(" + NL + "  new java.io.OutputStreamWriter(" + NL + "    new java.io.FileOutputStream(sOutFileN_";
  protected final String TEXT_16 = ", false), ";
  protected final String TEXT_17 = NL + "));" + NL + "    " + NL + "java.io.BufferedReader in_";
  protected final String TEXT_18 = " = new java.io.BufferedReader(" + NL + "  new java.io.InputStreamReader(" + NL + "    new java.io.FileInputStream(sOriFileN_";
  protected final String TEXT_19 = "), ";
  protected final String TEXT_20 = NL + "));" + NL + "" + NL + "//8192: the default buffer size of BufferedReader" + NL + "char[] cbuf_";
  protected final String TEXT_21 = " = new char[8192];  " + NL + "int readSize_";
  protected final String TEXT_22 = " = 0;" + NL + "" + NL + "while ((readSize_";
  protected final String TEXT_23 = " = in_";
  protected final String TEXT_24 = ".read(cbuf_";
  protected final String TEXT_25 = ")) != -1) {" + NL + "  out";
  protected final String TEXT_26 = ".write(cbuf_";
  protected final String TEXT_27 = ", 0, readSize_";
  protected final String TEXT_28 = ");" + NL + "}" + NL + "out";
  protected final String TEXT_29 = ".flush();" + NL + "out";
  protected final String TEXT_30 = ".close();" + NL + "in_";
  protected final String TEXT_31 = ".close();" + NL;
  protected final String TEXT_32 = NL + "  if (outFile_";
  protected final String TEXT_33 = ".exists()) {" + NL + "    java.io.File oriFile_";
  protected final String TEXT_34 = " = new java.io.File(sOriFileN_";
  protected final String TEXT_35 = ");" + NL + "    " + NL + "    if (!oriFile_";
  protected final String TEXT_36 = ".exists() || (oriFile_";
  protected final String TEXT_37 = ".exists() && oriFile_";
  protected final String TEXT_38 = ".delete())) {" + NL + "      outFile_";
  protected final String TEXT_39 = ".renameTo(oriFile_";
  protected final String TEXT_40 = ");" + NL + "    }" + NL + "  }";
  protected final String TEXT_41 = NL + "globalMap.put(\"";
  protected final String TEXT_42 = "_ISEND\", true);";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String inFileName = ElementParameterParser.getValue(node, "__INFILE_NAME__");
String outFileName = ElementParameterParser.getValue(node, "__OUTFILE_NAME__");
String sInEncoding = ElementParameterParser.getValue(node, "__INENCODING__");
String sOutEncoding = ElementParameterParser.getValue(node, "__ENCODING__");
boolean bNeedEncode = "true".equals(ElementParameterParser.getValue(node, "__USE_INENCODING__"));
boolean bCreateDir = "true".equals(ElementParameterParser.getValue(node, "__CREATE__"));
String sTmpOutFileName = outFileName;

if (outFileName.equals(inFileName)){
  sTmpOutFileName = outFileName + "+ \"_tmp\"";
  bCreateDir = false;
}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(sTmpOutFileName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inFileName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    if (bCreateDir) {
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
    }
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sOutEncoding);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(bNeedEncode ? sInEncoding : "System.getProperty(\"file.encoding\")");
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    if (outFileName.equals(inFileName)){
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    }
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    return stringBuffer.toString();
  }
}
