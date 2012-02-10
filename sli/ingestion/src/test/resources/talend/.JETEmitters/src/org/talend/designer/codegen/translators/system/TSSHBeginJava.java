package org.talend.designer.codegen.translators.system;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TSSHBeginJava
{
  protected static String nl;
  public static synchronized TSSHBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSSHBeginJava result = new TSSHBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\tfinal java.util.Vector<String> output_";
  protected final String TEXT_3 = " = new java.util.Vector<String>();";
  protected final String TEXT_4 = NL + "    String hostname_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "    String username_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "" + NL + "\tglobalMap.remove(\"";
  protected final String TEXT_9 = "_STDOUT\");" + NL + "\tglobalMap.remove(\"";
  protected final String TEXT_10 = "_STDERR\");" + NL + "" + NL + "\t/* Create a connection instance */";
  protected final String TEXT_11 = NL + "\t\t\t  ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_12 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL + "\t\t\t  ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_15 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_16 = ",Integer.parseInt(";
  protected final String TEXT_17 = " + \"\"));";
  protected final String TEXT_18 = NL + "\t\t/* Create a session */" + NL + "\t\tch.ethz.ssh2.Session sess_";
  protected final String TEXT_19 = " = null;" + NL + "              /* Now connect */";
  protected final String TEXT_20 = NL + "\t\t\t  conn_";
  protected final String TEXT_21 = ".connect(null,0,";
  protected final String TEXT_22 = "*1000);";
  protected final String TEXT_23 = NL + "\t\t\t  conn_";
  protected final String TEXT_24 = ".connect();";
  protected final String TEXT_25 = NL + "        java.io.File keyfile_";
  protected final String TEXT_26 = " = new java.io.File(";
  protected final String TEXT_27 = "); " + NL + "\t\t    boolean isAuthenticated_";
  protected final String TEXT_28 = " = conn_";
  protected final String TEXT_29 = ".authenticateWithPublicKey(username_";
  protected final String TEXT_30 = ", keyfile_";
  protected final String TEXT_31 = ", ";
  protected final String TEXT_32 = ");" + NL + "\t\t\t  if (isAuthenticated_";
  protected final String TEXT_33 = " == false)" + NL + "\t\t\t\t      throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_34 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_35 = " = conn_";
  protected final String TEXT_36 = ".authenticateWithPassword(username_";
  protected final String TEXT_37 = ", ";
  protected final String TEXT_38 = ");" + NL + "\t\tif (isAuthenticated_";
  protected final String TEXT_39 = " == false)" + NL + "\t\t\tthrow new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_40 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_41 = " = conn_";
  protected final String TEXT_42 = ".authenticateWithKeyboardInteractive(username_";
  protected final String TEXT_43 = ", " + NL + "            new ch.ethz.ssh2.InteractiveCallback() {" + NL + "        " + NL + "        \t\tpublic String[] replyToChallenge(String name," + NL + "        \t\t\t\tString instruction, int numPrompts," + NL + "        \t\t\t\tString[] prompt, boolean[] echo)" + NL + "        \t\t\t\tthrows Exception {" + NL + "        \t\t\t// TODO Auto-generated method stub" + NL + "        \t\t\tString[] reply = new String[numPrompts];" + NL + "        \t\t\tfor (int i = 0; i < reply.length; i++) {" + NL + "        \t\t\t\treply[i] = ";
  protected final String TEXT_44 = ";" + NL + "        \t\t\t}" + NL + "        " + NL + "        \t\t\treturn reply;" + NL + "        \t\t}" + NL + "        \t}\t" + NL + "        );" + NL + "        if (isAuthenticated_";
  protected final String TEXT_45 = " == false)" + NL + "            throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_46 = NL + "        ";
  protected final String TEXT_47 = NL + "\t\tStringBuilder stringStdout_";
  protected final String TEXT_48 = " =new StringBuilder();" + NL + "        StringBuilder stringStderr_";
  protected final String TEXT_49 = " =new StringBuilder();";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
boolean stats = codeGenArgument.isStatistics();

List<IMetadataTable> metadatas = node.getMetadataList();
String cid = node.getUniqueName();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {

        String host = ElementParameterParser.getValue(
            node,
            "__HOST__"
        );

        String port = ElementParameterParser.getValue(
            node,
            "__PORT__"
        );

        String user = ElementParameterParser.getValue(
            node,
            "__USER__"
        );

        String authMethod = ElementParameterParser.getValue(
            node,
            "__AUTH_METHOD__"
        );

        String privatekey = ElementParameterParser.getValue(
            node,
            "__PRIVATEKEY__"
        );

        String password = ElementParameterParser.getValue(
            node,
            "__PASSWORD__"
        );
        
        String passphrase = ElementParameterParser.getValue(
            node,
            "__PASSPHRASE__"
        );
        
        String useTimeout = ElementParameterParser.getValue(
            node,
            "__USE_TIMEOUT__"
        );

        String timeout = ElementParameterParser.getValue(
            node,
            "__TIMEOUT__"
        );
        
        String standardOutput  = ElementParameterParser.getValue(node, "__STANDARDOUTPUT__");
		String errorOuput  = ElementParameterParser.getValue(node, "__ERROROUTPUT__");
		if (("NORMAL_OUTPUT").equals(standardOutput)||("NORMAL_OUTPUT").equals(errorOuput)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
		}

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
        if(("").equals(port)){

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    
        } else {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(port );
    stringBuffer.append(TEXT_17);
    
        }

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
        if(("true").equals(useTimeout)){

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(timeout);
    stringBuffer.append(TEXT_22);
    
        } else {

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    
        }
        
        if (("PUBLICKEY").equals(authMethod)) {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(privatekey);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(passphrase);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    
        }
        if (("PASSWORD").equals(authMethod)) {

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    
        }
        
        if (("KEYBOARDINTERACTIVE").equals(authMethod)) {

    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    
        }

    stringBuffer.append(TEXT_46);
    
    }
}

    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    return stringBuffer.toString();
  }
}
