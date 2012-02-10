package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TFTPPutEndJava
{
  protected static String nl;
  public static synchronized TFTPPutEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPPutEndJava result = new TFTPPutEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + " \t}";
  protected final String TEXT_3 = NL + "    \tsession_";
  protected final String TEXT_4 = ".disconnect(); ";
  protected final String TEXT_5 = NL + "    msg_";
  protected final String TEXT_6 = ".add(nb_file_";
  protected final String TEXT_7 = " + \" files have been uploaded.\");  " + NL + "    \t" + NL + "\tStringBuffer sb_";
  protected final String TEXT_8 = " = new StringBuffer();" + NL + "    for (String item_";
  protected final String TEXT_9 = " : msg_";
  protected final String TEXT_10 = ") {" + NL + "        sb_";
  protected final String TEXT_11 = ".append(item_";
  protected final String TEXT_12 = ").append(\"\\n\");" + NL + "    }" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_13 = "_TRANSFER_MESSAGES\", sb_";
  protected final String TEXT_14 = ".toString());" + NL + "    ";
  protected final String TEXT_15 = NL + "\t}" + NL + "\t" + NL + "\tmsg_";
  protected final String TEXT_16 = ".add(ftp_";
  protected final String TEXT_17 = ".getUploadCount() + \" files have been uploaded.\");" + NL + "\t" + NL + "\tString[] msgAll_";
  protected final String TEXT_18 = " = msg_";
  protected final String TEXT_19 = ".getAll();" + NL + "    StringBuffer sb_";
  protected final String TEXT_20 = " = new StringBuffer();" + NL + "    if (msgAll_";
  protected final String TEXT_21 = " != null) {" + NL + "        for (String item_";
  protected final String TEXT_22 = " : msgAll_";
  protected final String TEXT_23 = ") {" + NL + "            sb_";
  protected final String TEXT_24 = ".append(item_";
  protected final String TEXT_25 = ").append(\"\\n\");" + NL + "        }" + NL + "    }" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_26 = "_TRANSFER_MESSAGES\", sb_";
  protected final String TEXT_27 = ".toString());" + NL + "\t";
  protected final String TEXT_28 = NL + "\t\t\t\ttry{" + NL + "\t\t\t\t\tftp_";
  protected final String TEXT_29 = ".quit();" + NL + "\t\t\t\t}catch(java.net.SocketException se_";
  protected final String TEXT_30 = "){" + NL + "\t\t\t\t\t//ignore failure" + NL + "\t\t\t\t}";
  protected final String TEXT_31 = NL + "\t\t\t\tftp_";
  protected final String TEXT_32 = ".quit();";
  protected final String TEXT_33 = NL + "\t" + NL + "\t" + NL + "globalMap.put(\"";
  protected final String TEXT_34 = "_NB_FILE\",nb_file_";
  protected final String TEXT_35 = ");" + NL;
  protected final String TEXT_36 = NL;

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

    stringBuffer.append(TEXT_2);
    
	if(!("true").equals(useExistingConn)){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
	}

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    }else{
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    
		if(!("true").equals(useExistingConn)){
 			if(("true").equals(ignoreFailureAtQuit)){

    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    
			}else{

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    
			}
		}
	}

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(TEXT_36);
    return stringBuffer.toString();
  }
}
