package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TFTPFileExistMainJava
{
  protected static String nl;
  public static synchronized TFTPFileExistMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPFileExistMainJava result = new TFTPFileExistMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  String remoteDir_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = ".replaceAll(\"\\\\\\\\\",\"/\");" + NL + "  " + NL + "  try{" + NL + "    if (c_";
  protected final String TEXT_4 = ".stat(";
  protected final String TEXT_5 = " + \"/\" + ";
  protected final String TEXT_6 = ").getAtimeString() != null) {" + NL + "      globalMap.put(\"";
  protected final String TEXT_7 = "_EXISTS\", true);" + NL + "    }" + NL + "  } catch (Exception e) {" + NL + "    globalMap.put(\"";
  protected final String TEXT_8 = "_EXISTS\", false);" + NL + "  }" + NL + "  globalMap.put(\"";
  protected final String TEXT_9 = "_FILENAME\", ";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "  String remoteDir_";
  protected final String TEXT_12 = " = ";
  protected final String TEXT_13 = ".replaceAll(\"\\\\\\\\\",\"/\");" + NL + "" + NL + "  if (ftp_";
  protected final String TEXT_14 = ".exists(";
  protected final String TEXT_15 = " + \"/\" + ";
  protected final String TEXT_16 = ")) {" + NL + "    globalMap.put(\"";
  protected final String TEXT_17 = "_EXISTS\", true);" + NL + "  } else {" + NL + "    globalMap.put(\"";
  protected final String TEXT_18 = "_EXISTS\", false);" + NL + "  }" + NL + "  globalMap.put(\"";
  protected final String TEXT_19 = "_FILENAME\", ";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String remoteDir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
String filename = ElementParameterParser.getValue(node, "__FILENAME__");
String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
boolean sftp = false;

if (("true").equals(useExistingConn)) {
  List<? extends INode> nodeList = node.getProcess().getGeneratingNodes();

  for (INode n : nodeList) {
    if (n.getUniqueName().equals(connection)) {
      sftp = ("true").equals(ElementParameterParser.getValue(n, "__SFTP__"));
    }
  }
} else {
  sftp = ("true").equals(ElementParameterParser.getValue(node, "__SFTP__"));
}

if (sftp) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(remoteDir );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(remoteDir );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_10);
    } else {
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(remoteDir );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(remoteDir );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_20);
    }
    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
