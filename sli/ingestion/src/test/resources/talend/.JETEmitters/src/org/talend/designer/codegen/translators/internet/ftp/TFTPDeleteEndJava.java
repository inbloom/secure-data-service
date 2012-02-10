package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TFTPDeleteEndJava
{
  protected static String nl;
  public static synchronized TFTPDeleteEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPDeleteEndJava result = new TFTPDeleteEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " \t\t}";
  protected final String TEXT_2 = NL + "    \t\tsession_";
  protected final String TEXT_3 = ".disconnect(); " + NL + "    ";
  protected final String TEXT_4 = NL + "\t\t} " + NL + "\t";
  protected final String TEXT_5 = NL + "\t\t\t\ttry{" + NL + "\t\t\t\t\tftp_";
  protected final String TEXT_6 = ".quit();" + NL + "\t\t\t\t}catch(java.net.SocketException se_";
  protected final String TEXT_7 = "){" + NL + "\t\t\t\t\t//ignore failure" + NL + "\t\t\t\t}";
  protected final String TEXT_8 = NL + "\t\t\t\tftp_";
  protected final String TEXT_9 = ".quit();";
  protected final String TEXT_10 = NL + NL + "globalMap.put(\"";
  protected final String TEXT_11 = "_NB_FILE\",nb_file_";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL;

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
	if(("true").equals(useExistingConn)){
		List<? extends INode> nodeList = node.getProcess().getGeneratingNodes();
		for(INode n : nodeList){
			if(n.getUniqueName().equals(connection)){
				sftp = ("true").equals(ElementParameterParser.getValue(n, "__SFTP__"));
			}
		}
	}else{
		sftp = ("true").equals(ElementParameterParser.getValue(node, "__SFTP__"));
	}
	if(sftp){

    stringBuffer.append(TEXT_1);
    
		if(!("true").equals(useExistingConn)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
		}
	}else{

    stringBuffer.append(TEXT_4);
    
		if(!("true").equals(useExistingConn)){
 			if(("true").equals(ignoreFailureAtQuit)){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
			}else{

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
			}
		}
	}

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
