package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSCPConnectionBeginJava
{
  protected static String nl;
  public static synchronized TSCPConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPConnectionBeginJava result = new TSCPConnectionBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "\t" + NL + "" + NL + "    String hostname_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "    String username_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "    " + NL + "    /* Create a connection instance */";
  protected final String TEXT_7 = NL + "              ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_8 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_9 = ");";
  protected final String TEXT_10 = NL + "              ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_11 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_12 = ",";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL + "              /* Now connect */" + NL + "        conn_";
  protected final String TEXT_15 = ".connect();";
  protected final String TEXT_16 = NL + "        java.io.File keyfile_";
  protected final String TEXT_17 = " = new java.io.File(";
  protected final String TEXT_18 = "); " + NL + "            boolean isAuthenticated_";
  protected final String TEXT_19 = " = conn_";
  protected final String TEXT_20 = ".authenticateWithPublicKey(username_";
  protected final String TEXT_21 = ", keyfile_";
  protected final String TEXT_22 = ", ";
  protected final String TEXT_23 = ");" + NL + "              if (isAuthenticated_";
  protected final String TEXT_24 = " == false)" + NL + "                      throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_25 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_26 = " = conn_";
  protected final String TEXT_27 = ".authenticateWithPassword(username_";
  protected final String TEXT_28 = ", ";
  protected final String TEXT_29 = ");" + NL + "        if (isAuthenticated_";
  protected final String TEXT_30 = " == false)" + NL + "            throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_31 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_32 = " = conn_";
  protected final String TEXT_33 = ".authenticateWithKeyboardInteractive(username_";
  protected final String TEXT_34 = ", " + NL + "            new ch.ethz.ssh2.InteractiveCallback() {" + NL + "        " + NL + "        \t\tpublic String[] replyToChallenge(String name," + NL + "        \t\t\t\tString instruction, int numPrompts," + NL + "        \t\t\t\tString[] prompt, boolean[] echo)" + NL + "        \t\t\t\tthrows Exception {" + NL + "        \t\t\t// TODO Auto-generated method stub" + NL + "        \t\t\tString[] reply = new String[numPrompts];" + NL + "        \t\t\tfor (int i = 0; i < reply.length; i++) {" + NL + "        \t\t\t\treply[i] = ";
  protected final String TEXT_35 = ";" + NL + "        \t\t\t}" + NL + "        " + NL + "        \t\t\treturn reply;" + NL + "        \t\t}" + NL + "        \t}\t" + NL + "        );" + NL + "        if (isAuthenticated_";
  protected final String TEXT_36 = " == false)" + NL + "            throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_37 = NL + NL + "\t\tglobalMap.put(\"conn_";
  protected final String TEXT_38 = "\",conn_";
  protected final String TEXT_39 = ");";
  protected final String TEXT_40 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
	
    String cid = node.getUniqueName();
    String host = ElementParameterParser.getValue(node, "__HOST__");
    String port = ElementParameterParser.getValue(node, "__PORT__");
    String user = ElementParameterParser.getValue(node, "__USERNAME__");
    
    String authMethod = ElementParameterParser.getValue(node,"__AUTH_METHOD__");
   
    String privatekey = ElementParameterParser.getValue(node,"__PRIVATEKEY__");

    String password = ElementParameterParser.getValue(node,"__PASSWORD__");
    
    String passphrase = ElementParameterParser.getValue(node,"__PASSPHRASE__");
    

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_6);
    
        if(("").equals(port)){

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
     
        } else {

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_13);
    
        }

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
            
        if (("PUBLICKEY").equals(authMethod)) {

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(privatekey);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(passphrase);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    
        }
        if (("PASSWORD").equals(authMethod)) {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    
        }
        if (("KEYBOARDINTERACTIVE").equals(authMethod)) {

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
        }
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(TEXT_40);
    return stringBuffer.toString();
  }
}
