package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;

public class TSendMailMainJava
{
  protected static String nl;
  public static synchronized TSendMailMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSendMailMainJava result = new TSendMailMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\tString smtpHost_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = ";";
  protected final String TEXT_4 = NL + "        String smtpPort_";
  protected final String TEXT_5 = " = \"";
  protected final String TEXT_6 = "\";";
  protected final String TEXT_7 = NL + "        String smtpPort_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ";";
  protected final String TEXT_10 = NL + "\tString from_";
  protected final String TEXT_11 = " = (";
  protected final String TEXT_12 = ");" + NL + "    String to_";
  protected final String TEXT_13 = " = (";
  protected final String TEXT_14 = ").replace(\";\",\",\");" + NL + "    String cc_";
  protected final String TEXT_15 = " = ((";
  protected final String TEXT_16 = ")==null || \"\".equals(";
  protected final String TEXT_17 = "))?null:(";
  protected final String TEXT_18 = ").replace(\";\",\",\");" + NL + "    String bcc_";
  protected final String TEXT_19 = " = ((";
  protected final String TEXT_20 = ")==null || \"\".equals(";
  protected final String TEXT_21 = "))?null:(";
  protected final String TEXT_22 = ").replace(\";\",\",\");" + NL + "    String subject_";
  protected final String TEXT_23 = " = (";
  protected final String TEXT_24 = ");" + NL + "    " + NL + "\tjava.util.List<java.util.Map<String, String>> headers_";
  protected final String TEXT_25 = " = new java.util.ArrayList<java.util.Map<String,String>>();";
  protected final String TEXT_26 = NL + "\t\tjava.util.Map<String,String> headerMap_";
  protected final String TEXT_27 = "_";
  protected final String TEXT_28 = " = new java.util.HashMap<String,String>();" + NL + "\t\theaderMap_";
  protected final String TEXT_29 = "_";
  protected final String TEXT_30 = ".put(\"KEY\", ";
  protected final String TEXT_31 = ");" + NL + "\t\theaderMap_";
  protected final String TEXT_32 = "_";
  protected final String TEXT_33 = ".put(\"VALUE\", ";
  protected final String TEXT_34 = ");" + NL + "\t\theaders_";
  protected final String TEXT_35 = ".add(headerMap_";
  protected final String TEXT_36 = "_";
  protected final String TEXT_37 = ");\t";
  protected final String TEXT_38 = NL + "\tjava.util.List<String> attachments_";
  protected final String TEXT_39 = " = new java.util.ArrayList<String>();" + NL + "\tjava.util.List<String> contentTransferEncoding_";
  protected final String TEXT_40 = " = new java.util.ArrayList<String>();";
  protected final String TEXT_41 = NL + "\t\tattachments_";
  protected final String TEXT_42 = ".add(";
  protected final String TEXT_43 = ");" + NL + "\t\tcontentTransferEncoding_";
  protected final String TEXT_44 = ".add(\"";
  protected final String TEXT_45 = "\");\t";
  protected final String TEXT_46 = NL + NL + "String message_";
  protected final String TEXT_47 = " = ((";
  protected final String TEXT_48 = ") == null || \"\".equals(";
  protected final String TEXT_49 = ")) ? \"\\\"\\\"\" : (";
  protected final String TEXT_50 = ") ;" + NL + "java.util.Properties props_";
  protected final String TEXT_51 = " = System.getProperties();     " + NL + "props_";
  protected final String TEXT_52 = ".put(\"mail.smtp.host\", smtpHost_";
  protected final String TEXT_53 = ");" + NL + "props_";
  protected final String TEXT_54 = ".put(\"mail.smtp.port\", smtpPort_";
  protected final String TEXT_55 = ");";
  protected final String TEXT_56 = NL + "props_";
  protected final String TEXT_57 = ".put(\"mail.smtp.localhost\", ";
  protected final String TEXT_58 = ");";
  protected final String TEXT_59 = NL + "props_";
  protected final String TEXT_60 = ".put(\"mail.mime.encodefilename\", \"true\");";
  protected final String TEXT_61 = NL + "  props_";
  protected final String TEXT_62 = ".put(\"mail.smtp.starttls.enable\",\"true\");";
  protected final String TEXT_63 = "  " + NL + "  java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());" + NL + "  props_";
  protected final String TEXT_64 = ".put(\"mail.smtp.socketFactory.class\", \"javax.net.ssl.SSLSocketFactory\");" + NL + "  props_";
  protected final String TEXT_65 = ".put(\"mail.smtp.socketFactory.fallback\", \"false\");" + NL + "  props_";
  protected final String TEXT_66 = ".put(\"mail.smtp.socketFactory.port\", smtpPort_";
  protected final String TEXT_67 = ");";
  protected final String TEXT_68 = "     " + NL + "try {";
  protected final String TEXT_69 = "  " + NL + "    props_";
  protected final String TEXT_70 = ".put(\"mail.smtp.auth\", \"false\");" + NL + "    javax.mail.Session session_";
  protected final String TEXT_71 = " = javax.mail.Session.getInstance(props_";
  protected final String TEXT_72 = ", null);    ";
  protected final String TEXT_73 = NL + "    props_";
  protected final String TEXT_74 = ".put(\"mail.smtp.auth\", \"true\");" + NL + "    javax.mail.Session session_";
  protected final String TEXT_75 = " = javax.mail.Session.getInstance(props_";
  protected final String TEXT_76 = ", new javax.mail.Authenticator(){         " + NL + "      protected javax.mail.PasswordAuthentication getPasswordAuthentication() {" + NL + "        return new javax.mail.PasswordAuthentication(";
  protected final String TEXT_77 = ", ";
  protected final String TEXT_78 = "); " + NL + "      }         " + NL + "    });   ";
  protected final String TEXT_79 = NL + "  javax.mail.Message msg_";
  protected final String TEXT_80 = " = new javax.mail.internet.MimeMessage(session_";
  protected final String TEXT_81 = ");" + NL + "  msg_";
  protected final String TEXT_82 = ".setFrom(new javax.mail.internet.InternetAddress(from_";
  protected final String TEXT_83 = ", ";
  protected final String TEXT_84 = "));" + NL + "  msg_";
  protected final String TEXT_85 = ".setRecipients(javax.mail.Message.RecipientType.TO,javax.mail.internet.InternetAddress.parse(to_";
  protected final String TEXT_86 = ", false));" + NL + "  if (cc_";
  protected final String TEXT_87 = " != null) msg_";
  protected final String TEXT_88 = ".setRecipients(javax.mail.Message.RecipientType.CC, javax.mail.internet.InternetAddress.parse(cc_";
  protected final String TEXT_89 = ", false));" + NL + "  if (bcc_";
  protected final String TEXT_90 = " != null) msg_";
  protected final String TEXT_91 = ".setRecipients(javax.mail.Message.RecipientType.BCC, javax.mail.internet.InternetAddress.parse(bcc_";
  protected final String TEXT_92 = ", false));" + NL + "  msg_";
  protected final String TEXT_93 = ".setSubject(subject_";
  protected final String TEXT_94 = ");" + NL + "" + NL + "  for (int i_";
  protected final String TEXT_95 = " = 0; i_";
  protected final String TEXT_96 = " < headers_";
  protected final String TEXT_97 = ".size(); i_";
  protected final String TEXT_98 = "++) {" + NL + "    java.util.Map<String, String> header_";
  protected final String TEXT_99 = " = headers_";
  protected final String TEXT_100 = ".get(i_";
  protected final String TEXT_101 = ");" + NL + "    msg_";
  protected final String TEXT_102 = ".setHeader(header_";
  protected final String TEXT_103 = ".get(\"KEY\"), header_";
  protected final String TEXT_104 = ".get(\"VALUE\"));    " + NL + "  }  " + NL + "  msg_";
  protected final String TEXT_105 = ".setSentDate(new Date());" + NL + "  msg_";
  protected final String TEXT_106 = ".setHeader(\"X-Priority\", \"";
  protected final String TEXT_107 = "\"); //High->1 Normal->3 Low->5" + NL + "  javax.mail.Multipart mp_";
  protected final String TEXT_108 = " = new javax.mail.internet.MimeMultipart();" + NL + "  javax.mail.internet.MimeBodyPart mbpText_";
  protected final String TEXT_109 = " = new javax.mail.internet.MimeBodyPart();" + NL + "  mbpText_";
  protected final String TEXT_110 = ".setText(message_";
  protected final String TEXT_111 = ",";
  protected final String TEXT_112 = ", \"";
  protected final String TEXT_113 = "\");" + NL + "  mp_";
  protected final String TEXT_114 = ".addBodyPart(mbpText_";
  protected final String TEXT_115 = ");" + NL + "  " + NL + "  javax.mail.internet.MimeBodyPart mbpFile_";
  protected final String TEXT_116 = " = null;" + NL + "  " + NL + "  for (int i_";
  protected final String TEXT_117 = " = 0; i_";
  protected final String TEXT_118 = " < attachments_";
  protected final String TEXT_119 = ".size(); i_";
  protected final String TEXT_120 = "++){" + NL + "    String filename_";
  protected final String TEXT_121 = " = attachments_";
  protected final String TEXT_122 = ".get(i_";
  protected final String TEXT_123 = ");" + NL + "    javax.activation.FileDataSource fds_";
  protected final String TEXT_124 = " = null;" + NL + "    java.io.File file_";
  protected final String TEXT_125 = " = new java.io.File(filename_";
  protected final String TEXT_126 = ");";
  protected final String TEXT_127 = NL + "      if (!file_";
  protected final String TEXT_128 = ".exists()){" + NL + "        continue;" + NL + "      }";
  protected final String TEXT_129 = NL + "    if (file_";
  protected final String TEXT_130 = ".isDirectory()){" + NL + "      java.io.File[] subFiles_";
  protected final String TEXT_131 = " = file_";
  protected final String TEXT_132 = ".listFiles();" + NL + "      " + NL + "      for(java.io.File subFile_";
  protected final String TEXT_133 = " : subFiles_";
  protected final String TEXT_134 = "){" + NL + "        if (subFile_";
  protected final String TEXT_135 = ".isFile()){" + NL + "          fds_";
  protected final String TEXT_136 = " = new javax.activation.FileDataSource(subFile_";
  protected final String TEXT_137 = ".getAbsolutePath());" + NL + "          mbpFile_";
  protected final String TEXT_138 = " = new javax.mail.internet.MimeBodyPart();" + NL + "          mbpFile_";
  protected final String TEXT_139 = ".setDataHandler(new javax.activation.DataHandler(fds_";
  protected final String TEXT_140 = "));" + NL + "          mbpFile_";
  protected final String TEXT_141 = ".setFileName(javax.mail.internet.MimeUtility.encodeText(fds_";
  protected final String TEXT_142 = ".getName()));" + NL + "          if(contentTransferEncoding_";
  protected final String TEXT_143 = ".get(i_";
  protected final String TEXT_144 = ").equalsIgnoreCase(\"base64\")){" + NL + "\t      \tmbpFile_";
  protected final String TEXT_145 = ".setHeader(\"Content-Transfer-Encoding\", \"base64\");" + NL + "\t      }" + NL + "          mp_";
  protected final String TEXT_146 = ".addBodyPart(mbpFile_";
  protected final String TEXT_147 = ");" + NL + "        }" + NL + "      }" + NL + "    }else{" + NL + "      mbpFile_";
  protected final String TEXT_148 = " = new javax.mail.internet.MimeBodyPart();" + NL + "      fds_";
  protected final String TEXT_149 = " = new javax.activation.FileDataSource(filename_";
  protected final String TEXT_150 = ");" + NL + "      mbpFile_";
  protected final String TEXT_151 = ".setDataHandler(new javax.activation.DataHandler(fds_";
  protected final String TEXT_152 = ")); " + NL + "      mbpFile_";
  protected final String TEXT_153 = ".setFileName(javax.mail.internet.MimeUtility.encodeText(fds_";
  protected final String TEXT_154 = ".getName()));" + NL + "      if(contentTransferEncoding_";
  protected final String TEXT_155 = ".get(i_";
  protected final String TEXT_156 = ").equalsIgnoreCase(\"base64\")){" + NL + "      \tmbpFile_";
  protected final String TEXT_157 = ".setHeader(\"Content-Transfer-Encoding\", \"base64\");" + NL + "      }" + NL + "      mp_";
  protected final String TEXT_158 = ".addBodyPart(mbpFile_";
  protected final String TEXT_159 = ");" + NL + "    }" + NL + "  }" + NL + "  // -- set the content --" + NL + "  msg_";
  protected final String TEXT_160 = ".setContent(mp_";
  protected final String TEXT_161 = ");" + NL + "   // add handlers for main MIME types" + NL + " javax.activation.MailcapCommandMap mc_";
  protected final String TEXT_162 = " = ( javax.activation.MailcapCommandMap)javax.activation.CommandMap.getDefaultCommandMap();" + NL + " mc_";
  protected final String TEXT_163 = ".addMailcap(\"text/html;; x-java-content-handler=com.sun.mail.handlers.text_html\");" + NL + " mc_";
  protected final String TEXT_164 = ".addMailcap(\"text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml\");" + NL + " mc_";
  protected final String TEXT_165 = ".addMailcap(\"text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain\");" + NL + " mc_";
  protected final String TEXT_166 = ".addMailcap(\"multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed\");" + NL + " mc_";
  protected final String TEXT_167 = ".addMailcap(\"message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822\");" + NL + " javax.activation.CommandMap.setDefaultCommandMap(mc_";
  protected final String TEXT_168 = ");" + NL + "  // -- Send the message --" + NL + "  javax.mail.Transport.send(msg_";
  protected final String TEXT_169 = ");" + NL + "} catch(Exception e){";
  protected final String TEXT_170 = NL + "    throw(e);";
  protected final String TEXT_171 = NL + "    System.err.println(e.toString());";
  protected final String TEXT_172 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String needAuth = ElementParameterParser.getValue(node, "__NEED_AUTH__");
String authUsername = ElementParameterParser.getValue(node, "__AUTH_USERNAME__");
String authPassword = ElementParameterParser.getValue(node, "__AUTH_PASSWORD__");
String message = ElementParameterParser.getValue(node, "__MESSAGE__").replaceAll("\r\n","\\\\n");
String importance = ElementParameterParser.getValue(node, "__IMPORTANCE__");
String priority = "Normal".equals(importance) ? "3" : (("High").equals(importance) ? "1" : "5");
boolean ifSSL = "true".equals(ElementParameterParser.getValue(node, "__SSL__"));
boolean startTLS = "true".equals(ElementParameterParser.getValue(node, "__STARTTLS__"));
boolean checkAttachementOnSend = "true".equals(ElementParameterParser.getValue(node, "__CHECK_ATTACHMENT__"));
boolean needPersonalName = "true".equals(ElementParameterParser.getValue(node, "__NEED_PERSONAL_NAME__"));
String personalName = needPersonalName ? ElementParameterParser.getValue(node, "__PERSONAL_NAME__") : null;
String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
String mimesubtype = ElementParameterParser.getValue(node, "__TEXT_SUBTYPE__");  
boolean setLocalhost = "true".equals(ElementParameterParser.getValue(node, "__SET_LOCALHOST__"));  
String localhost = ElementParameterParser.getValue(node, "__LOCALHOST__");  
boolean isDieOnError = "true".equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));

	//Bug12952, move all begin part to here below
	String smtpHost    = ElementParameterParser.getValue(node, "__SMTP_HOST__");
	String smtpPort    = ElementParameterParser.getValue(node, "__SMTP_PORT__");
	String to          = ElementParameterParser.getValue(node, "__TO__");
	String from        = ElementParameterParser.getValue(node, "__FROM__");
	String cc          = ElementParameterParser.getValue(node, "__CC__");
	String bcc          = ElementParameterParser.getValue(node, "__BCC__");
	String subject     = ElementParameterParser.getValue(node, "__SUBJECT__");
	
	List<Map<String, String>> tableValues = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ATTACHMENTS__");
	List<Map<String, String>> headers = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__HEADERS__");
	
	// component id
	to = to.replace(";",",");
	
	//in order to be compatible with TOS 2.4.1, @see:issue:6282
	if(cc == null || cc.trim().length()==0){
		cc = "\"\"";
	}
	if(bcc == null || bcc.trim().length()==0){
		bcc = "\"\"";
	}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(smtpHost );
    stringBuffer.append(TEXT_3);
    
    try {
        int port = Integer.parseInt(smtpPort);

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(smtpPort );
    stringBuffer.append(TEXT_6);
    
    } catch (Exception e) {

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(smtpPort );
    stringBuffer.append(TEXT_9);
    
    }

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(from );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(to );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cc );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cc );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cc );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(bcc );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(bcc );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(bcc );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(subject );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
	for (int i = 0; i < headers.size(); i++) {
   		Map<String, String> header = headers.get(i);

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(header.get("KEY"));
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(header.get("VALUE"));
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_37);
    
	}

    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    
	for (int i = 0; i < tableValues.size(); i++) {
    	Map<String,String> attachment = tableValues.get(i);

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(attachment.get("FILE") );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(attachment.get("CONTENT_TRANSFER_ENCODING") );
    stringBuffer.append(TEXT_45);
    
	}
	//end of the former begin part

    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(message );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(message );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(message );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
     
if (setLocalhost){
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(localhost);
    stringBuffer.append(TEXT_58);
     }
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
     
if (startTLS){
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    
}
if (ifSSL){

    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    
}

    stringBuffer.append(TEXT_68);
    if ("false".equals(needAuth)) { 
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    } else{
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(authUsername );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(authPassword );
    stringBuffer.append(TEXT_78);
    }
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(personalName);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(priority );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(mimesubtype );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    if (!checkAttachementOnSend) {
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_128);
    }
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    if (isDieOnError){
    stringBuffer.append(TEXT_170);
    }else{
    stringBuffer.append(TEXT_171);
    }
    stringBuffer.append(TEXT_172);
    return stringBuffer.toString();
  }
}
