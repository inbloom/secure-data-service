package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TFTPFileListEndJava
{
  protected static String nl;
  public static synchronized TFTPFileListEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPFileListEndJava result = new TFTPFileListEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "}";
  protected final String TEXT_2 = NL + "  session_";
  protected final String TEXT_3 = ".disconnect(); ";
  protected final String TEXT_4 = NL + "      try {" + NL + "        ftp_";
  protected final String TEXT_5 = ".quit();" + NL + "      } catch (java.net.SocketException se_";
  protected final String TEXT_6 = ") {" + NL + "        //ignore failure" + NL + "      }";
  protected final String TEXT_7 = NL + "      ftp_";
  protected final String TEXT_8 = ".quit();";
  protected final String TEXT_9 = NL + "globalMap.put(\"";
  protected final String TEXT_10 = "_NB_FILE\",nb_file_";
  protected final String TEXT_11 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
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

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
} else {

  if (!("true").equals(useExistingConn)) {
    if (("true").equals(ignoreFailureAtQuit)) {
    
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
    } else {
    
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    
    }
  }
}

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
