package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TSCPGetBeginJava
{
  protected static String nl;
  public static synchronized TSCPGetBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPGetBeginJava result = new TSCPGetBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tint nb_file_";
  protected final String TEXT_3 = " = 0;" + NL + "" + NL + "    /* Create a connection instance */" + NL + "\t";
  protected final String TEXT_4 = NL + "\t" + NL + "\tch.ethz.ssh2.Connection conn_";
  protected final String TEXT_5 = " = (ch.ethz.ssh2.Connection)globalMap.get(\"";
  protected final String TEXT_6 = "\");" + NL;
  protected final String TEXT_7 = NL + NL + "\t    String hostname_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ";" + NL + "\t    String username_";
  protected final String TEXT_10 = " = ";
  protected final String TEXT_11 = ";" + NL;
  protected final String TEXT_12 = NL + "              ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_13 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_14 = ");";
  protected final String TEXT_15 = NL + "              ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_16 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_17 = ",";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + "              /* Now connect */" + NL + "        conn_";
  protected final String TEXT_20 = ".connect();";
  protected final String TEXT_21 = NL + "        java.io.File keyfile_";
  protected final String TEXT_22 = " = new java.io.File(";
  protected final String TEXT_23 = "); " + NL + "            boolean isAuthenticated_";
  protected final String TEXT_24 = " = conn_";
  protected final String TEXT_25 = ".authenticateWithPublicKey(username_";
  protected final String TEXT_26 = ", keyfile_";
  protected final String TEXT_27 = ", ";
  protected final String TEXT_28 = ");" + NL + "              if (isAuthenticated_";
  protected final String TEXT_29 = " == false)" + NL + "                      throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_30 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_31 = " = conn_";
  protected final String TEXT_32 = ".authenticateWithPassword(username_";
  protected final String TEXT_33 = ", ";
  protected final String TEXT_34 = ");" + NL + "        if (isAuthenticated_";
  protected final String TEXT_35 = " == false)" + NL + "            throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_36 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_37 = " = conn_";
  protected final String TEXT_38 = ".authenticateWithKeyboardInteractive(username_";
  protected final String TEXT_39 = ", " + NL + "            new ch.ethz.ssh2.InteractiveCallback() {" + NL + "        " + NL + "        \t\tpublic String[] replyToChallenge(String name," + NL + "        \t\t\t\tString instruction, int numPrompts," + NL + "        \t\t\t\tString[] prompt, boolean[] echo)" + NL + "        \t\t\t\tthrows Exception {" + NL + "        \t\t\t// TODO Auto-generated method stub" + NL + "        \t\t\tString[] reply = new String[numPrompts];" + NL + "        \t\t\tfor (int i = 0; i < reply.length; i++) {" + NL + "        \t\t\t\treply[i] = ";
  protected final String TEXT_40 = ";" + NL + "        \t\t\t}" + NL + "        " + NL + "        \t\t\treturn reply;" + NL + "        \t\t}" + NL + "        \t}\t" + NL + "        );" + NL + "        if (isAuthenticated_";
  protected final String TEXT_41 = " == false)" + NL + "            throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_42 = NL + "        ch.ethz.ssh2.SCPClient scp_";
  protected final String TEXT_43 = " = new  ch.ethz.ssh2.SCPClient(conn_";
  protected final String TEXT_44 = ");";
  protected final String TEXT_45 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
        CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
        INode node = (INode)codeGenArgument.getArgument();
        String cid = node.getUniqueName();

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
            "__USERNAME__"
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

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
	String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
	
	if(("true").equals(useExistingConn)){
		String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
		String conn= "conn_" + connection;
	
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_6);
    }else{
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_11);
    
        if(("").equals(port)){

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
        } else {

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_18);
    
        }

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
            
        if (("PUBLICKEY").equals(authMethod)) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(privatekey);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(passphrase);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    
        }
        if (("PASSWORD").equals(authMethod)) {

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    
        }
        if (("KEYBOARDINTERACTIVE").equals(authMethod)) {

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    
        }
	}//if(useExistingConn)        

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(TEXT_45);
    return stringBuffer.toString();
  }
}
