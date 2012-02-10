package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TSCPFileListBeginJava
{
  protected static String nl;
  public static synchronized TSCPFileListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPFileListBeginJava result = new TSCPFileListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "    /* Create a connection instance */" + NL + "\t";
  protected final String TEXT_3 = NL + "\t" + NL + "\tch.ethz.ssh2.Connection conn_";
  protected final String TEXT_4 = " = (ch.ethz.ssh2.Connection)globalMap.get(\"";
  protected final String TEXT_5 = "\");" + NL + "\t";
  protected final String TEXT_6 = NL + NL + "    String hostname_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "    String username_";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ";" + NL + "    ";
  protected final String TEXT_11 = NL + "              ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_12 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL + "              ch.ethz.ssh2.Connection conn_";
  protected final String TEXT_15 = " = new ch.ethz.ssh2.Connection(hostname_";
  protected final String TEXT_16 = ",";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL + "              /* Now connect */" + NL + "        conn_";
  protected final String TEXT_19 = ".connect();";
  protected final String TEXT_20 = NL + "        java.io.File keyfile_";
  protected final String TEXT_21 = " = new java.io.File(";
  protected final String TEXT_22 = "); " + NL + "            boolean isAuthenticated_";
  protected final String TEXT_23 = " = conn_";
  protected final String TEXT_24 = ".authenticateWithPublicKey(username_";
  protected final String TEXT_25 = ", keyfile_";
  protected final String TEXT_26 = ", ";
  protected final String TEXT_27 = ");" + NL + "              if (isAuthenticated_";
  protected final String TEXT_28 = " == false)" + NL + "                      throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_29 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_30 = " = conn_";
  protected final String TEXT_31 = ".authenticateWithPassword(username_";
  protected final String TEXT_32 = ", ";
  protected final String TEXT_33 = ");" + NL + "        if (isAuthenticated_";
  protected final String TEXT_34 = " == false)" + NL + "            throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_35 = NL + "        boolean isAuthenticated_";
  protected final String TEXT_36 = " = conn_";
  protected final String TEXT_37 = ".authenticateWithKeyboardInteractive(username_";
  protected final String TEXT_38 = ", " + NL + "            new ch.ethz.ssh2.InteractiveCallback() {" + NL + "        " + NL + "        \t\tpublic String[] replyToChallenge(String name," + NL + "        \t\t\t\tString instruction, int numPrompts," + NL + "        \t\t\t\tString[] prompt, boolean[] echo)" + NL + "        \t\t\t\tthrows Exception {" + NL + "        \t\t\t// TODO Auto-generated method stub" + NL + "        \t\t\tString[] reply = new String[numPrompts];" + NL + "        \t\t\tfor (int i = 0; i < reply.length; i++) {" + NL + "        \t\t\t\treply[i] = ";
  protected final String TEXT_39 = ";" + NL + "        \t\t\t}" + NL + "        " + NL + "        \t\t\treturn reply;" + NL + "        \t\t}" + NL + "        \t}\t" + NL + "        );" + NL + "        if (isAuthenticated_";
  protected final String TEXT_40 = " == false)" + NL + "            throw new RuntimeException(\"Authentication failed.\");";
  protected final String TEXT_41 = "     " + NL + "" + NL + "\t\t//initial the command" + NL + "\t\tStringBuilder command_";
  protected final String TEXT_42 = " = new StringBuilder();";
  protected final String TEXT_43 = NL + "         \tif(command_";
  protected final String TEXT_44 = ".length() > 0){" + NL + "         \t\tcommand_";
  protected final String TEXT_45 = ".append(";
  protected final String TEXT_46 = ");" + NL + "         \t} " + NL + "            command_";
  protected final String TEXT_47 = ".append(";
  protected final String TEXT_48 = ");" + NL + "            ";
  protected final String TEXT_49 = NL + "       " + NL + "\t\t/* Create a session */" + NL + "\t\tch.ethz.ssh2.Session sess_";
  protected final String TEXT_50 = " = conn_";
  protected final String TEXT_51 = ".openSession();" + NL + "\t\t" + NL + "\t\t//execute the command" + NL + "\t\tsess_";
  protected final String TEXT_52 = ".execCommand((command_";
  protected final String TEXT_53 = ").toString());" + NL + "\t\t" + NL + "\t\tint nb_line_";
  protected final String TEXT_54 = " = 0;" + NL + "\t\t" + NL + "\t\t//get the return info" + NL + "\t\tjava.io.InputStream stdout_";
  protected final String TEXT_55 = " = sess_";
  protected final String TEXT_56 = ".getStdout();" + NL + "\t\tjava.io.BufferedReader brout_";
  protected final String TEXT_57 = "= new java.io.BufferedReader(new java.io.InputStreamReader(stdout_";
  protected final String TEXT_58 = "));" + NL + "        String line_out_";
  protected final String TEXT_59 = " = \"\";" + NL + "        StringBuilder stringStdout_";
  protected final String TEXT_60 = " =new StringBuilder();" + NL + "\t\twhile((line_out_";
  protected final String TEXT_61 = " = brout_";
  protected final String TEXT_62 = ".readLine()) != null) {";
  protected final String TEXT_63 = NL;

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
        
       	String commandSeparator = ElementParameterParser.getValue(node, "__COMMANDSEPARATOR__");        
        
        List<Map<String, String>> filelist = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILELIST__");       
 
    stringBuffer.append(TEXT_2);
    
	String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
	
	if(("true").equals(useExistingConn)){
		String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
		String conn= "conn_" + connection;
	
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_5);
    }else{
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(user);
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
    stringBuffer.append(port);
    stringBuffer.append(TEXT_17);
    
        }

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
            
        if (("PUBLICKEY").equals(authMethod)) {

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(privatekey);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(passphrase);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    
        }
        if (("PASSWORD").equals(authMethod)) {

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    
        }
        if (("KEYBOARDINTERACTIVE").equals(authMethod)) {

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    
        } 
	}//if(useExistingConn)       

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    
             for (Map<String, String> file : filelist) {
         
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(commandSeparator );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(file.get("SOURCE"));
    stringBuffer.append(TEXT_48);
              
            }
         
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(TEXT_63);
    return stringBuffer.toString();
  }
}
