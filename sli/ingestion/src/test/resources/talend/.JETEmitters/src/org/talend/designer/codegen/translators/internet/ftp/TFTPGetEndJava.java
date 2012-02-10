package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TFTPGetEndJava
{
  protected static String nl;
  public static synchronized TFTPGetEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPGetEndJava result = new TFTPGetEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "}" + NL + "nb_file_";
  protected final String TEXT_2 = " = getter_";
  protected final String TEXT_3 = ".count;" + NL;
  protected final String TEXT_4 = NL + "  msg_";
  protected final String TEXT_5 = ".add(getter_";
  protected final String TEXT_6 = ".count + \" files have been downloaded.\");" + NL + "  StringBuffer sb_";
  protected final String TEXT_7 = " = new StringBuffer();" + NL + "" + NL + "  for (String item_";
  protected final String TEXT_8 = " : msg_";
  protected final String TEXT_9 = ") {" + NL + "    sb_";
  protected final String TEXT_10 = ".append(item_";
  protected final String TEXT_11 = ").append(\"\\n\");" + NL + "  }" + NL + "  globalMap.put(\"";
  protected final String TEXT_12 = "_TRANSFER_MESSAGES\", sb_";
  protected final String TEXT_13 = ".toString());" + NL;
  protected final String TEXT_14 = NL + "    session_";
  protected final String TEXT_15 = ".disconnect(); ";
  protected final String TEXT_16 = "    ";
  protected final String TEXT_17 = NL + "  msg_";
  protected final String TEXT_18 = ".add(ftp_";
  protected final String TEXT_19 = ".getDownloadCount() + \" files have been downloaded.\");" + NL + "  String[] msgAll_";
  protected final String TEXT_20 = " = msg_";
  protected final String TEXT_21 = ".getAll();" + NL + "  StringBuffer sb_";
  protected final String TEXT_22 = " = new StringBuffer();" + NL + "" + NL + "  if (msgAll_";
  protected final String TEXT_23 = " != null) {" + NL + "    for (String item_";
  protected final String TEXT_24 = " : msgAll_";
  protected final String TEXT_25 = ") {" + NL + "      sb_";
  protected final String TEXT_26 = ".append(item_";
  protected final String TEXT_27 = ").append(\"\\n\");" + NL + "    }" + NL + "  }" + NL + "  globalMap.put(\"";
  protected final String TEXT_28 = "_TRANSFER_MESSAGES\", sb_";
  protected final String TEXT_29 = ".toString());" + NL;
  protected final String TEXT_30 = NL + "      try {" + NL + "        ftp_";
  protected final String TEXT_31 = ".quit();" + NL + "      } catch(java.net.SocketException se_";
  protected final String TEXT_32 = ") {" + NL + "        //ignore failure" + NL + "      }";
  protected final String TEXT_33 = NL + "      ftp_";
  protected final String TEXT_34 = ".quit();";
  protected final String TEXT_35 = NL + "    ftp_";
  protected final String TEXT_36 = ".disconnect(true);";
  protected final String TEXT_37 = NL + "globalMap.put(\"";
  protected final String TEXT_38 = "_NB_FILE\",nb_file_";
  protected final String TEXT_39 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String ignoreFailureAtQuit= ElementParameterParser.getValue(node,"__IGNORE_FAILURE_AT_QUIT__");
String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
boolean sftp = false;
boolean ftps = false;

if (("true").equals(useExistingConn)) {
  List<? extends INode> nodeList = node.getProcess().getGeneratingNodes();

  for(INode n : nodeList){
    if(n.getUniqueName().equals(connection)){
      sftp = ("true").equals(ElementParameterParser.getValue(n, "__SFTP__"));
      ftps = ("true").equals(ElementParameterParser.getValue(n, "__FTPS__"));
    }
  }
} else {
  sftp = ("true").equals(ElementParameterParser.getValue(node, "__SFTP__"));
  ftps = ("true").equals(ElementParameterParser.getValue(node, "__FTPS__"));
}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    if (sftp) {
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    if (!("true").equals(useExistingConn)) {
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    }
    stringBuffer.append(TEXT_16);
    } else if (!ftps) {
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    
  if (!("true").equals(useExistingConn)) {
  
    if(("true").equals(ignoreFailureAtQuit)){
    
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
    } else {
    
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    
    }
  }
} else {

  if (!("true").equals(useExistingConn)){
  
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
  }
}

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    return stringBuffer.toString();
  }
}
