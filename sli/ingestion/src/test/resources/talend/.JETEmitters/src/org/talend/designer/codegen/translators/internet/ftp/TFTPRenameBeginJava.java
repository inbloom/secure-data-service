package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TFTPRenameBeginJava
{
  protected static String nl;
  public static synchronized TFTPRenameBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPRenameBeginJava result = new TFTPRenameBeginJava();
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
  protected final String TEXT_11 = NL + "int nb_file_";
  protected final String TEXT_12 = " = 0;" + NL;
  protected final String TEXT_13 = NL + "    class MyUserInfo_";
  protected final String TEXT_14 = " implements com.jcraft.jsch.UserInfo, com.jcraft.jsch.UIKeyboardInteractive {" + NL + "      String passphrase_";
  protected final String TEXT_15 = " = ";
  protected final String TEXT_16 = ";" + NL + "      public String getPassphrase() { return passphrase_";
  protected final String TEXT_17 = "; }" + NL + "      public String getPassword() { return null; } " + NL + "      public boolean promptPassword(String arg0) { return true; } " + NL + "      public boolean promptPassphrase(String arg0) { return true; } " + NL + "      public boolean promptYesNo(String arg0) { return true; } " + NL + "      public void showMessage(String arg0) { } " + NL + "" + NL + "      public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt," + NL + "        boolean[] echo) {" + NL + "        String[] password_";
  protected final String TEXT_18 = " = {";
  protected final String TEXT_19 = "};" + NL + "        return password_";
  protected final String TEXT_20 = ";" + NL + "      }" + NL + "    }; " + NL + "    final com.jcraft.jsch.UserInfo defaultUserInfo_";
  protected final String TEXT_21 = " = new MyUserInfo_";
  protected final String TEXT_22 = "();" + NL + "    com.jcraft.jsch.JSch jsch_";
  protected final String TEXT_23 = "=new com.jcraft.jsch.JSch(); " + NL;
  protected final String TEXT_24 = NL + "      jsch_";
  protected final String TEXT_25 = ".addIdentity(";
  protected final String TEXT_26 = ", defaultUserInfo_";
  protected final String TEXT_27 = ".getPassphrase());";
  protected final String TEXT_28 = NL + "    com.jcraft.jsch.Session session_";
  protected final String TEXT_29 = "=jsch_";
  protected final String TEXT_30 = ".getSession(";
  protected final String TEXT_31 = ", ";
  protected final String TEXT_32 = ", ";
  protected final String TEXT_33 = ");" + NL;
  protected final String TEXT_34 = " " + NL + "      session_";
  protected final String TEXT_35 = ".setPassword(";
  protected final String TEXT_36 = "); ";
  protected final String TEXT_37 = NL + NL + "    session_";
  protected final String TEXT_38 = ".setUserInfo(defaultUserInfo_";
  protected final String TEXT_39 = "); " + NL + "    session_";
  protected final String TEXT_40 = ".connect();" + NL + "    com.jcraft.jsch. Channel channel_";
  protected final String TEXT_41 = "=session_";
  protected final String TEXT_42 = ".openChannel(\"sftp\");" + NL + "    channel_";
  protected final String TEXT_43 = ".connect();" + NL + "    com.jcraft.jsch.ChannelSftp c_";
  protected final String TEXT_44 = "=(com.jcraft.jsch.ChannelSftp)channel_";
  protected final String TEXT_45 = ";" + NL + "    c_";
  protected final String TEXT_46 = ".setFilenameEncoding(";
  protected final String TEXT_47 = ");";
  protected final String TEXT_48 = "    " + NL + "    com.jcraft.jsch.ChannelSftp c_";
  protected final String TEXT_49 = " = (com.jcraft.jsch.ChannelSftp)globalMap.get(\"";
  protected final String TEXT_50 = "\");";
  protected final String TEXT_51 = NL + "  java.util.List<java.util.Map<String,String>> list";
  protected final String TEXT_52 = " = new java.util.ArrayList<java.util.Map<String,String>>();" + NL + "  ";
  protected final String TEXT_53 = "    " + NL + "    java.util.Map<String,String> map";
  protected final String TEXT_54 = " = new java.util.HashMap<String,String>();  " + NL + "    map";
  protected final String TEXT_55 = ".put(";
  protected final String TEXT_56 = ",";
  protected final String TEXT_57 = ");" + NL + "    list";
  protected final String TEXT_58 = ".add(map";
  protected final String TEXT_59 = ");       ";
  protected final String TEXT_60 = NL + NL + "  for (java.util.Map<String, String> map";
  protected final String TEXT_61 = " : list";
  protected final String TEXT_62 = ") {" + NL;
  protected final String TEXT_63 = NL + "  com.enterprisedt.net.ftp.FTPClient ftp_";
  protected final String TEXT_64 = " =null;" + NL;
  protected final String TEXT_65 = NL + "    ftp_";
  protected final String TEXT_66 = " = (com.enterprisedt.net.ftp.FTPClient)globalMap.get(\"";
  protected final String TEXT_67 = "\");";
  protected final String TEXT_68 = "    " + NL + "    ftp_";
  protected final String TEXT_69 = " = new com.enterprisedt.net.ftp.FTPClient();" + NL + "    ftp_";
  protected final String TEXT_70 = ".setRemoteHost(";
  protected final String TEXT_71 = ");" + NL + "    ftp_";
  protected final String TEXT_72 = ".setRemotePort(";
  protected final String TEXT_73 = ");" + NL;
  protected final String TEXT_74 = NL + "      ftp_";
  protected final String TEXT_75 = ".setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.ACTIVE);";
  protected final String TEXT_76 = NL + "      ftp_";
  protected final String TEXT_77 = ".setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.PASV);";
  protected final String TEXT_78 = NL + "    ftp_";
  protected final String TEXT_79 = ".setControlEncoding(";
  protected final String TEXT_80 = ");" + NL + "    ftp_";
  protected final String TEXT_81 = ".connect();  " + NL + "    ftp_";
  protected final String TEXT_82 = ".login(";
  protected final String TEXT_83 = ", ";
  protected final String TEXT_84 = "); ";
  protected final String TEXT_85 = NL + "  java.util.List<java.util.Map<String,String>> list";
  protected final String TEXT_86 = " = new java.util.ArrayList<java.util.Map<String,String>>();" + NL;
  protected final String TEXT_87 = "    " + NL + "    java.util.Map<String,String> map";
  protected final String TEXT_88 = " = new java.util.HashMap<String,String>();  " + NL + "    map";
  protected final String TEXT_89 = ".put(";
  protected final String TEXT_90 = ",";
  protected final String TEXT_91 = ");" + NL + "    list";
  protected final String TEXT_92 = ".add(map";
  protected final String TEXT_93 = ");       ";
  protected final String TEXT_94 = "  " + NL + "  String remotedir";
  protected final String TEXT_95 = " = ";
  protected final String TEXT_96 = ";" + NL + "  ftp_";
  protected final String TEXT_97 = ".chdir(remotedir";
  protected final String TEXT_98 = ");" + NL + "" + NL + "  for (java.util.Map<String, String> map";
  protected final String TEXT_99 = " : list";
  protected final String TEXT_100 = ") { ";
  protected final String TEXT_101 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String host = ElementParameterParser.getValue(node, "__HOST__");
String port = ElementParameterParser.getValue(node, "__PORT__");
String user = ElementParameterParser.getValue(node, "__USERNAME__");
String pass = ElementParameterParser.getValue(node, "__PASSWORD__");
String remotedir = ElementParameterParser.getValue(node, "__REMOTEDIR__");
List<Map<String, String>> files = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILES__");  
String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
String authMethod = ElementParameterParser.getValue(node,"__AUTH_METHOD__");
String privateKey = ElementParameterParser.getValue(node,"__PRIVATEKEY__");
String passPhrase = ElementParameterParser.getValue(node,"__PASSPHRASE__");  
boolean useProxy = ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
String proxyHost = ElementParameterParser.getValue(node,"__PROXY_HOST__");
String proxyPort = ElementParameterParser.getValue(node,"__PROXY_PORT__");
String proxyUser = ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
String proxyPassword = ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");
String connectMode = ElementParameterParser.getValue(node,"__CONNECT_MODE__");
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
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_10);
    }
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
        
if (sftp) {

  if (("false").equals(useExistingConn)) {
  
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(passPhrase );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    if (("PUBLICKEY").equals(authMethod)) {
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(privateKey );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    }
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_33);
    if (("PASSWORD").equals(authMethod)) {
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_36);
    }
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_47);
    
  } else {
  
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_50);
    
  }
  
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    
  for (int i = 0; i < files.size(); i++) {
    Map<String, String> line = files.get(i);
    
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(i );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(i );
    stringBuffer.append(TEXT_55);
    stringBuffer.append( line.get("FILEMASK") );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( line.get("NEWNAME") );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(i );
    stringBuffer.append(TEXT_59);
    
  }
  
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    } else {
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    if (("true").equals(useExistingConn)) {
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_67);
    } else {
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(host );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(port );
    stringBuffer.append(TEXT_73);
    if (("ACTIVE").equals(connectMode)) {
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    } else {
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    }
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(user );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(pass );
    stringBuffer.append(TEXT_84);
    }
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    
  for (int i = 0; i < files.size(); i++) {
    Map<String, String> line = files.get(i);
    
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(i );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(i );
    stringBuffer.append(TEXT_89);
    stringBuffer.append( line.get("FILEMASK") );
    stringBuffer.append(TEXT_90);
    stringBuffer.append( line.get("NEWNAME") );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(i );
    stringBuffer.append(TEXT_93);
    
  }
  
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(remotedir);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    }
    stringBuffer.append(TEXT_101);
    return stringBuffer.toString();
  }
}
