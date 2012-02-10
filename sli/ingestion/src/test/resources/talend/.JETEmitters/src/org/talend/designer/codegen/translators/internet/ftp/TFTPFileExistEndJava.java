package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TFTPFileExistEndJava
{
  protected static String nl;
  public static synchronized TFTPFileExistEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPFileExistEndJava result = new TFTPFileExistEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  session_";
  protected final String TEXT_2 = ".disconnect(); ";
  protected final String TEXT_3 = NL + "      try {" + NL + "        ftp_";
  protected final String TEXT_4 = ".quit();" + NL + "      } catch(java.net.SocketException se_";
  protected final String TEXT_5 = ") {" + NL + "        //ignore failure" + NL + "      }";
  protected final String TEXT_6 = NL + "      ftp_";
  protected final String TEXT_7 = ".quit();";
  protected final String TEXT_8 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String remoteDir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
String filename = ElementParameterParser.getValue(node, "__FILENAME__");
String ignoreFailureAtQuit= ElementParameterParser.getValue(node,"__IGNORE_FAILURE_AT_QUIT__");
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

if (sftp && !("true").equals(useExistingConn)) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
} else {
  if (!("true").equals(useExistingConn)) {
    if (("true").equals(ignoreFailureAtQuit)) {
    
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
    } else {
    
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
    }
  }
}

    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
