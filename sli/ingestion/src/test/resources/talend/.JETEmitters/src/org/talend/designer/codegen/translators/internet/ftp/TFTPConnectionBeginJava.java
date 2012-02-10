package org.talend.designer.codegen.translators.internet.ftp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFTPConnectionBeginJava
{
  protected static String nl;
  public static synchronized TFTPConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFTPConnectionBeginJava result = new TFTPConnectionBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  " + NL + "  java.util.Properties props_";
  protected final String TEXT_2 = " = System.getProperties();" + NL + "  props_";
  protected final String TEXT_3 = ".put(\"socksProxyPort\", ";
  protected final String TEXT_4 = ");" + NL + "  props_";
  protected final String TEXT_5 = ".put(\"socksProxyHost\", ";
  protected final String TEXT_6 = ");" + NL + "  props_";
  protected final String TEXT_7 = ".put(\"java.net.socks.username\", ";
  protected final String TEXT_8 = ");" + NL + "  props_";
  protected final String TEXT_9 = ".put(\"java.net.socks.password\", ";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "  com.enterprisedt.net.ftp.FTPClient ftp_";
  protected final String TEXT_12 = " = new com.enterprisedt.net.ftp.FTPClient();" + NL + "  ftp_";
  protected final String TEXT_13 = ".setRemoteHost(";
  protected final String TEXT_14 = ");" + NL + "  ftp_";
  protected final String TEXT_15 = ".setRemotePort(";
  protected final String TEXT_16 = ");" + NL;
  protected final String TEXT_17 = NL + "    ftp_";
  protected final String TEXT_18 = ".setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.ACTIVE);";
  protected final String TEXT_19 = NL + "    ftp_";
  protected final String TEXT_20 = ".setConnectMode(com.enterprisedt.net.ftp.FTPConnectMode.PASV);";
  protected final String TEXT_21 = NL + "  ftp_";
  protected final String TEXT_22 = ".setControlEncoding(";
  protected final String TEXT_23 = ");" + NL + "  ftp_";
  protected final String TEXT_24 = ".connect();  " + NL + "  ftp_";
  protected final String TEXT_25 = ".login(";
  protected final String TEXT_26 = ", ";
  protected final String TEXT_27 = ");  " + NL + "  globalMap.put(\"conn_";
  protected final String TEXT_28 = "\",ftp_";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL + "  class MyUserInfo implements com.jcraft.jsch.UserInfo, com.jcraft.jsch.UIKeyboardInteractive {" + NL + "" + NL + "    String passphrase_";
  protected final String TEXT_31 = " = ";
  protected final String TEXT_32 = ";" + NL + "" + NL + "    public String getPassphrase() { return passphrase_";
  protected final String TEXT_33 = "; }" + NL + "" + NL + "    public String getPassword() { return null; } " + NL + "" + NL + "    public boolean promptPassword(String arg0) { return true; } " + NL + "" + NL + "    public boolean promptPassphrase(String arg0) { return true; } " + NL + "" + NL + "    public boolean promptYesNo(String arg0) { return true; } " + NL + "" + NL + "    public void showMessage(String arg0) { } " + NL + "" + NL + "    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt," + NL + "    boolean[] echo) {" + NL + "      String[] password_";
  protected final String TEXT_34 = " = {";
  protected final String TEXT_35 = "};" + NL + "      return password_";
  protected final String TEXT_36 = ";" + NL + "    }" + NL + "  };" + NL + "  final com.jcraft.jsch.UserInfo defaultUserInfo_";
  protected final String TEXT_37 = " = new MyUserInfo();" + NL + "  com.jcraft.jsch.JSch jsch_";
  protected final String TEXT_38 = " = new com.jcraft.jsch.JSch(); " + NL;
  protected final String TEXT_39 = NL + "    jsch_";
  protected final String TEXT_40 = ".addIdentity(";
  protected final String TEXT_41 = ", defaultUserInfo_";
  protected final String TEXT_42 = ".getPassphrase());";
  protected final String TEXT_43 = NL + "  com.jcraft.jsch.Session session_";
  protected final String TEXT_44 = " = jsch_";
  protected final String TEXT_45 = ".getSession(";
  protected final String TEXT_46 = ", ";
  protected final String TEXT_47 = ", ";
  protected final String TEXT_48 = ");" + NL;
  protected final String TEXT_49 = " " + NL + "    session_";
  protected final String TEXT_50 = ".setPassword(";
  protected final String TEXT_51 = "); ";
  protected final String TEXT_52 = NL + NL + "  session_";
  protected final String TEXT_53 = ".setUserInfo(defaultUserInfo_";
  protected final String TEXT_54 = "); " + NL + "  session_";
  protected final String TEXT_55 = ".connect();" + NL + "  com.jcraft.jsch. Channel channel_";
  protected final String TEXT_56 = " = session_";
  protected final String TEXT_57 = ".openChannel(\"sftp\"); " + NL + "  channel_";
  protected final String TEXT_58 = ".connect();" + NL + "  com.jcraft.jsch.ChannelSftp c_";
  protected final String TEXT_59 = " = (com.jcraft.jsch.ChannelSftp)channel_";
  protected final String TEXT_60 = ";" + NL + "  c_";
  protected final String TEXT_61 = ".setFilenameEncoding(";
  protected final String TEXT_62 = ");" + NL + "  globalMap.put(\"conn_";
  protected final String TEXT_63 = "\", c_";
  protected final String TEXT_64 = ");";
  protected final String TEXT_65 = NL + "  class MyTrust_";
  protected final String TEXT_66 = "{" + NL + "" + NL + "    private javax.net.ssl.TrustManager[] getTrustManagers() " + NL + "      throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, " + NL + "      java.security.cert.CertificateException, java.security.UnrecoverableKeyException," + NL + "      java.io.FileNotFoundException, java.io.IOException{" + NL + "      java.security.KeyStore ks = java.security.KeyStore.getInstance(\"JKS\");" + NL + "      ks.load(new java.io.FileInputStream(";
  protected final String TEXT_67 = "), ";
  protected final String TEXT_68 = ".toCharArray());" + NL + "      javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());" + NL + "      tmf.init(ks);" + NL + "      return tmf.getTrustManagers();" + NL + "    }" + NL + "  }" + NL + "  javax.net.ssl.SSLContext sslContext = null;" + NL + "  javax.net.ssl.TrustManager[] trustManager = null;" + NL + "  javax.net.ssl.SSLSocketFactory sslSocketFactory = null;" + NL + "  it.sauronsoftware.ftp4j.FTPClient ftp_";
  protected final String TEXT_69 = " =null;" + NL + "  MyTrust_";
  protected final String TEXT_70 = " myTrust_";
  protected final String TEXT_71 = " = null;" + NL + "" + NL + "  try {" + NL + "    sslContext = javax.net.ssl.SSLContext.getInstance(\"SSL\");" + NL + "    myTrust_";
  protected final String TEXT_72 = " = new MyTrust_";
  protected final String TEXT_73 = "();" + NL + "    trustManager = myTrust_";
  protected final String TEXT_74 = ".getTrustManagers();" + NL + "    sslContext.init(null, trustManager, new java.security.SecureRandom());" + NL + "    sslSocketFactory = sslContext.getSocketFactory();" + NL + "    ftp_";
  protected final String TEXT_75 = " = new it.sauronsoftware.ftp4j.FTPClient();" + NL + "    ftp_";
  protected final String TEXT_76 = ".setSSLSocketFactory(sslSocketFactory);";
  protected final String TEXT_77 = NL + "    \tftp_";
  protected final String TEXT_78 = ".setSecurity(it.sauronsoftware.ftp4j.FTPClient.SECURITY_FTPS);";
  protected final String TEXT_79 = NL + "    \tftp_";
  protected final String TEXT_80 = ".setSecurity(it.sauronsoftware.ftp4j.FTPClient.SECURITY_FTPES);";
  protected final String TEXT_81 = NL + "    ftp_";
  protected final String TEXT_82 = ".connect(";
  protected final String TEXT_83 = ",";
  protected final String TEXT_84 = ");" + NL + "    ftp_";
  protected final String TEXT_85 = ".login(";
  protected final String TEXT_86 = ", ";
  protected final String TEXT_87 = ");" + NL + "  } catch (java.lang.IllegalStateException e) {" + NL + "    e.printStackTrace();" + NL + "  } catch (java.io.IOException e) {" + NL + "    e.printStackTrace();" + NL + "  } catch (it.sauronsoftware.ftp4j.FTPIllegalReplyException e) {" + NL + "    e.printStackTrace();" + NL + "  } catch (it.sauronsoftware.ftp4j.FTPException e) {" + NL + "    e.printStackTrace();" + NL + "  }" + NL + "  globalMap.put(\"conn_";
  protected final String TEXT_88 = "\", ftp_";
  protected final String TEXT_89 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String host = ElementParameterParser.getValue(node, "__HOST__");
String port = ElementParameterParser.getValue(node, "__PORT__");
String user = ElementParameterParser.getValue(node, "__USER__");
String pass = ElementParameterParser.getValue(node, "__PASS__");
String connectMode = ElementParameterParser.getValue(node, "__CONNECT_MODE__");
boolean useProxy = ("true").equals(ElementParameterParser.getValue(node, "__USE_PROXY__"));
String proxyHost = ElementParameterParser.getValue(node, "__PROXY_HOST__");
String proxyPort = ElementParameterParser.getValue(node, "__PROXY_PORT__");
String proxyUser = ElementParameterParser.getValue(node, "__PROXY_USERNAME__");
String proxyPassword = ElementParameterParser.getValue(node, "__PROXY_PASSWORD__");
boolean sftp = ("true").equals(ElementParameterParser.getValue(node, "__SFTP__"));
String authMethod = ElementParameterParser.getValue(node, "__AUTH_METHOD__");
String privateKey = ElementParameterParser.getValue(node, "__PRIVATEKEY__");
String passPhrase = ElementParameterParser.getValue(node, "__PASSPHRASE__");
boolean ftps = ("true").equals(ElementParameterParser.getValue(node, "__FTPS__"));
String keystoreFile = ElementParameterParser.getValue(node, "__KEYSTORE_FILE__");
String keystorePass = ElementParameterParser.getValue(node, "__KEYSTORE_PASS__");
String sEncoding = ElementParameterParser.getValue(node, "__ENCODING__");
String securityMode = ElementParameterParser.getValue(node, "__SECURITY_MODE__");

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

if (!sftp && !ftps) { // *** ftp *** //

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(host );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(port );
    stringBuffer.append(TEXT_16);
    if (("ACTIVE").equals(connectMode)) {
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    } else {
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    }
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(sEncoding);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(user );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(pass );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    
} else if (!ftps) { // *** sftp *** //

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(passPhrase );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    if (("PUBLICKEY").equals(authMethod)){
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(privateKey );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    }
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_48);
    if (("PASSWORD").equals(authMethod)) {
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(pass);
    stringBuffer.append(TEXT_51);
    }
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
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
    stringBuffer.append(sEncoding);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    
} else {  // *** ftps *** //

    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(keystoreFile);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(keystorePass);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    
    if("IMPLICIT".equals(securityMode)){

    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    
    }else if("EXPLICIT".equals(securityMode)){

    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    
    }

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(host );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(port );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(user );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(pass );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    
}

    return stringBuffer.toString();
  }
}
