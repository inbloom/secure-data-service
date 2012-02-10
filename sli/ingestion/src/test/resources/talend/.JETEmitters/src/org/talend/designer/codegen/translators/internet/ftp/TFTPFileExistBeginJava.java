package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TFTPFileExistBeginJava
{
  protected static String nl;
  public static synchronized TFTPFileExistBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPFileExistBeginJava result = new TFTPFileExistBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  java.util.Properties props_";
  protected final String TEXT_2 = " = System.getProperties();" + NL + "  props_";
  protected final String TEXT_3 = ".put(\"socksProxyPort\", ";
  protected final String TEXT_4 = ");" + NL + "  props_";
  protected final String TEXT_5 = ".put(\"socksProxyHost\", ";
  protected final String TEXT_6 = ");" + NL + "  props_";
  protected final String TEXT_7 = ".put(\"java.net.socks.username\", ";
  protected final String TEXT_8 = ");" + NL + "  props_";
  protected final String TEXT_9 = ".put(\"java.net.socks.password\", ";
  protected final String TEXT_10 = ");        ";
  protected final String TEXT_11 = NL + "    class MyUserInfo_";
  protected final String TEXT_12 = " implements com.jcraft.jsch.UserInfo, com.jcraft.jsch.UIKeyboardInteractive {" + NL + "" + NL + "      String passphrase_";
  protected final String TEXT_13 = " = ";
  protected final String TEXT_14 = ";" + NL + "" + NL + "      public String getPassphrase() { return passphrase_";
  protected final String TEXT_15 = "; }" + NL + "" + NL + "      public String getPassword() { return null; } " + NL + "" + NL + "      public boolean promptPassword(String arg0) { return true; } " + NL + "" + NL + "      public boolean promptPassphrase(String arg0) { return true; } " + NL + "" + NL + "      public boolean promptYesNo(String arg0) { return true; } " + NL + "" + NL + "      public void showMessage(String arg0) { } " + NL + "" + NL + "      public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt," + NL + "        boolean[] echo) {" + NL + "        String[] password_";
  protected final String TEXT_16 = " = {";
  protected final String TEXT_17 = "};" + NL + "        return password_";
  protected final String TEXT_18 = ";" + NL + "      }" + NL + "    }; " + NL + "    final com.jcraft.jsch.UserInfo defaultUserInfo_";
  protected final String TEXT_19 = " = new MyUserInfo_";
  protected final String TEXT_20 = "();" + NL + "    com.jcraft.jsch.JSch jsch_";
  protected final String TEXT_21 = "=new com.jcraft.jsch.JSch(); " + NL;
  protected final String TEXT_22 = NL + "      jsch_";
  protected final String TEXT_23 = ".addIdentity(";
  protected final String TEXT_24 = ", defaultUserInfo_";
  protected final String TEXT_25 = ".getPassphrase());";
  protected final String TEXT_26 = NL + "    com.jcraft.jsch.Session session_";
  protected final String TEXT_27 = "=jsch_";
  protected final String TEXT_28 = ".getSession(";
  protected final String TEXT_29 = ", ";
  protected final String TEXT_30 = ", ";
  protected final String TEXT_31 = ");" + NL;
  protected final String TEXT_32 = " " + NL + "      session_";
  protected final String TEXT_33 = ".setPassword(";
  protected final String TEXT_34 = "); ";
  protected final String TEXT_35 = NL + "    session_";
  protected final String TEXT_36 = ".setUserInfo(defaultUserInfo_";
  protected final String TEXT_37 = "); " + NL + "    session_";
  protected final String TEXT_38 = ".connect();" + NL + "    com.jcraft.jsch. Channel channel_";
  protected final String TEXT_39 = "=session_";
  protected final String TEXT_40 = ".openChannel(\"sftp\");" + NL + "    channel_";
  protected final String TEXT_41 = ".connect();" + NL + "    com.jcraft.jsch.ChannelSftp c_";
  protected final String TEXT_42 = "=(com.jcraft.jsch.ChannelSftp)channel_";
  protected final String TEXT_43 = ";" + NL + "    c_";
  protected final String TEXT_44 = ".setFilenameEncoding(";
  protected final String TEXT_45 = ");";
  protected final String TEXT_46 = "    " + NL + "    com.jcraft.jsch.ChannelSftp c_";
  protected final String TEXT_47 = " = (com.jcraft.jsch.ChannelSftp)globalMap.get(\"";
  protected final String TEXT_48 = "\");";
  protected final String TEXT_49 = NL + "  com.enterprisedt.net.ftp.FTPClient ftp_";
  protected final String TEXT_50 = " = null;" + NL;
  protected final String TEXT_51 = NL + "    ftp_";
  protected final String TEXT_52 = " = (com.enterprisedt.net.ftp.FTPClient)globalMap.get(\"";
  protected final String TEXT_53 = "\");";
  protected final String TEXT_54 = "    " + NL + "    ftp_";
  protected final String TEXT_55 = " = new com.enterprisedt.net.ftp.FTPClient();" + NL + "    ftp_";
  protected final String TEXT_56 = ".setRemoteHost(";
  protected final String TEXT_57 = ");" + NL + "    ftp_";
  protected final String TEXT_58 = ".setRemotePort(";
  protected final String TEXT_59 = ");" + NL;
  protected final String TEXT_60 = NL + "      ftp_";
  protected final String TEXT_61 = ".setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.ACTIVE);";
  protected final String TEXT_62 = NL + "      ftp_";
  protected final String TEXT_63 = ".setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.PASV);";
  protected final String TEXT_64 = NL + "    ftp_";
  protected final String TEXT_65 = ".setControlEncoding(";
  protected final String TEXT_66 = ");" + NL + "    ftp_";
  protected final String TEXT_67 = ".connect();  " + NL + "    ftp_";
  protected final String TEXT_68 = ".login(";
  protected final String TEXT_69 = ", ";
  protected final String TEXT_70 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String host=ElementParameterParser.getValue(node, "__HOST__");
String port=ElementParameterParser.getValue(node, "__PORT__");
String username=ElementParameterParser.getValue(node, "__USERNAME__");
String password=ElementParameterParser.getValue(node, "__PASSWORD__");
String remoteDir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
String filename = ElementParameterParser.getValue(node, "__FILENAME__");
boolean useProxy = ("true").equals(ElementParameterParser.getValue(node, "__USE_PROXY__"));
String proxyHost = ElementParameterParser.getValue(node, "__PROXY_HOST__");
String proxyPort = ElementParameterParser.getValue(node, "__PROXY_PORT__");
String proxyUser = ElementParameterParser.getValue(node, "__PROXY_USERNAME__");
String proxyPass = ElementParameterParser.getValue(node, "__PROXY_PASSWORD__");
String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
String user = ElementParameterParser.getValue(node, "__USERNAME__");
String pass = ElementParameterParser.getValue(node, "__PASSWORD__");
String authMethod = ElementParameterParser.getValue(node, "__AUTH_METHOD__");
String privateKey = ElementParameterParser.getValue(node, "__PRIVATEKEY__");
String passPhrase = ElementParameterParser.getValue(node, "__PASSPHRASE__");    
String connectMode = ElementParameterParser.getValue(node, "__CONNECT_MODE__");
String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
String conn= "conn_" + connection;
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

//The following part support the socks proxy for FTP and SFTP (Socks V4 or V5, they are all OK). 
//And it can not work with the FTP proxy directly, only support the socks proxy.
if (useProxy) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(proxyPort );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(proxyUser );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(proxyPass );
    stringBuffer.append(TEXT_10);
    
}

if (sftp) {
  if (("false").equals(useExistingConn)) {
  
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(passPhrase );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    if (("PUBLICKEY").equals(authMethod)) {
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(privateKey );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    }
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_31);
    if (("PASSWORD").equals(authMethod)) {
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_34);
    }
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_45);
    
  } else {
  
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_48);
    
  }
} else {
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
     if (("true").equals(useExistingConn)) {
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_53);
    } else {
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(host );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(port );
    stringBuffer.append(TEXT_59);
    if (("ACTIVE").equals(connectMode)) {
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    } else {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    }
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_70);
      
  }
}
    return stringBuffer.toString();
  }
}
