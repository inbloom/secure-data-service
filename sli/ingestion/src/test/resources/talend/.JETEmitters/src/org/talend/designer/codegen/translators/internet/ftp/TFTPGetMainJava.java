package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TFTPGetMainJava
{
  protected static String nl;
  public static synchronized TFTPGetMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPGetMainJava result = new TFTPGetMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "try {" + NL + "  globalMap.put(\"";
  protected final String TEXT_2 = "_CURRENT_STATUS\", \"No file transfered.\");" + NL + "  String dir_";
  protected final String TEXT_3 = " = root_";
  protected final String TEXT_4 = ";" + NL + "  String mask_";
  protected final String TEXT_5 = " = maskStr_";
  protected final String TEXT_6 = ".replaceAll(\"\\\\\\\\\", \"/\") ;" + NL + "  int i_";
  protected final String TEXT_7 = " = mask_";
  protected final String TEXT_8 = ".lastIndexOf('/'); " + NL + "" + NL + "  if (i_";
  protected final String TEXT_9 = " != -1){" + NL + "    dir_";
  protected final String TEXT_10 = " = mask_";
  protected final String TEXT_11 = ".substring(0, i_";
  protected final String TEXT_12 = "); " + NL + "    mask_";
  protected final String TEXT_13 = " = mask_";
  protected final String TEXT_14 = ".substring(i_";
  protected final String TEXT_15 = "+1);  " + NL + "  }" + NL + "  mask_";
  protected final String TEXT_16 = " = org.apache.oro.text.GlobCompiler.globToPerl5(mask_";
  protected final String TEXT_17 = ".toCharArray(), org.apache.oro.text.GlobCompiler.DEFAULT_MASK);" + NL + "  " + NL + "  if (dir_";
  protected final String TEXT_18 = "!=null && !\"\".equals(dir_";
  protected final String TEXT_19 = ")){" + NL + "    if ((\".*\").equals(mask_";
  protected final String TEXT_20 = ")) {" + NL + "      getter_";
  protected final String TEXT_21 = ".getAllFiles(dir_";
  protected final String TEXT_22 = ", localdir_";
  protected final String TEXT_23 = ");" + NL + "    } else {" + NL + "      getter_";
  protected final String TEXT_24 = ".getFiles(dir_";
  protected final String TEXT_25 = ", localdir_";
  protected final String TEXT_26 = " ,mask_";
  protected final String TEXT_27 = ");" + NL + "    }" + NL + "  }" + NL + "  getter_";
  protected final String TEXT_28 = ".chdir(root_";
  protected final String TEXT_29 = ");" + NL + "} catch(Exception e) {";
  protected final String TEXT_30 = NL + "    throw(e);";
  protected final String TEXT_31 = NL + "    System.err.print(e.getMessage());";
  protected final String TEXT_32 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String overwrite=ElementParameterParser.getValue(node, "__OVERWRITE__");
String sftpoverwrite=ElementParameterParser.getValue(node, "__SFTPOVERWRITE__");
String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
String remotedir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
boolean sftp = false;

if ("true".equals(useExistingConn)) {
  List<? extends INode> nodeList = node.getProcess().getGeneratingNodes();

  for (INode n : nodeList) {

    if (n.getUniqueName().equals(connection)) {
      sftp = "true".equals(ElementParameterParser.getValue(n, "__SFTP__"));
    }
  }
} else {
  sftp = "true".equals(ElementParameterParser.getValue(node, "__SFTP__"));
}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
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
    if ("true".equals(dieOnError)){
    stringBuffer.append(TEXT_30);
    }else{
    stringBuffer.append(TEXT_31);
    }
    stringBuffer.append(TEXT_32);
    return stringBuffer.toString();
  }
}
