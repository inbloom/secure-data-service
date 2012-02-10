package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TPOPBeginJava
{
  protected static String nl;
  public static synchronized TPOPBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPOPBeginJava result = new TPOPBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  " + NL + "String server_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = ";" + NL + "String mbox_";
  protected final String TEXT_4 = " = \"INBOX\";" + NL + "String user_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "String password_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "int port_";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ";" + NL + "int nb_email_";
  protected final String TEXT_11 = " = 0;" + NL + "javax.mail.Folder folder_";
  protected final String TEXT_12 = ";" + NL + "javax.mail.Session session_";
  protected final String TEXT_13 = ";" + NL + "javax.mail.Store store_";
  protected final String TEXT_14 = ";" + NL + "javax.mail.URLName url_";
  protected final String TEXT_15 = ";" + NL + "java.util.Properties props_";
  protected final String TEXT_16 = ";" + NL + "" + NL + "try {" + NL + "  props_";
  protected final String TEXT_17 = " = System.getProperties();" + NL + "} catch (SecurityException sex) {" + NL + "  props_";
  protected final String TEXT_18 = " = new java.util.Properties();" + NL + "}";
  protected final String TEXT_19 = NL + "    java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());" + NL + "    props_";
  protected final String TEXT_20 = ".setProperty(\"mail.pop3.socketFactory.class\", \"javax.net.ssl.SSLSocketFactory\");      " + NL + "    props_";
  protected final String TEXT_21 = ".setProperty(\"mail.pop3.socketFactory.fallback\", \"false\");" + NL + "    props_";
  protected final String TEXT_22 = ".setProperty(\"mail.pop3.socketFactory.port\", port_";
  protected final String TEXT_23 = " + \"\");";
  protected final String TEXT_24 = NL + "    java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());" + NL + "    props_";
  protected final String TEXT_25 = ".setProperty(\"mail.imap.socketFactory.class\", \"javax.net.ssl.SSLSocketFactory\");" + NL + "    props_";
  protected final String TEXT_26 = ".setProperty(\"mail.imap.socketFactory.fallback\", \"false\");" + NL + "    props_";
  protected final String TEXT_27 = ".setProperty(\"mail.imap.socketFactory.port\", port_";
  protected final String TEXT_28 = " + \"\");    ";
  protected final String TEXT_29 = NL + "  session_";
  protected final String TEXT_30 = " = javax.mail.Session.getInstance(props_";
  protected final String TEXT_31 = ", null);" + NL + "  store_";
  protected final String TEXT_32 = " = session_";
  protected final String TEXT_33 = ".getStore(\"";
  protected final String TEXT_34 = "\");" + NL + "  store_";
  protected final String TEXT_35 = ".connect(server_";
  protected final String TEXT_36 = ", port_";
  protected final String TEXT_37 = ", user_";
  protected final String TEXT_38 = ", password_";
  protected final String TEXT_39 = ");" + NL + "  folder_";
  protected final String TEXT_40 = " = store_";
  protected final String TEXT_41 = ".getDefaultFolder();" + NL + "" + NL + "  if (folder_";
  protected final String TEXT_42 = " == null) {" + NL + "    throw new RuntimeException(\"No default folder\");" + NL + "  }" + NL + "  // its INBOX" + NL + "  folder_";
  protected final String TEXT_43 = " = folder_";
  protected final String TEXT_44 = ".getFolder(mbox_";
  protected final String TEXT_45 = ");" + NL + "" + NL + "  if (folder_";
  protected final String TEXT_46 = " == null){" + NL + "    throw new RuntimeException(\"No POP3 INBOX\");" + NL + "  }";
  protected final String TEXT_47 = NL + "    mbox_";
  protected final String TEXT_48 = " = ";
  protected final String TEXT_49 = ";";
  protected final String TEXT_50 = NL + "  url_";
  protected final String TEXT_51 = " = new javax.mail.URLName(\"";
  protected final String TEXT_52 = "\", server_";
  protected final String TEXT_53 = ", -1, mbox_";
  protected final String TEXT_54 = ", user_";
  protected final String TEXT_55 = ", password_";
  protected final String TEXT_56 = ");" + NL + "  session_";
  protected final String TEXT_57 = " = javax.mail.Session.getInstance(props_";
  protected final String TEXT_58 = ", null);" + NL + "  store_";
  protected final String TEXT_59 = " = new com.sun.mail.imap.IMAPStore(session_";
  protected final String TEXT_60 = ", url_";
  protected final String TEXT_61 = ");" + NL + "  store_";
  protected final String TEXT_62 = ".connect();" + NL + "  folder_";
  protected final String TEXT_63 = " = store_";
  protected final String TEXT_64 = ".getFolder(mbox_";
  protected final String TEXT_65 = ");";
  protected final String TEXT_66 = " " + NL + "" + NL + "try {" + NL + "  folder_";
  protected final String TEXT_67 = ".open(javax.mail.Folder.READ_WRITE);" + NL + "} catch (Exception e){" + NL + "  folder_";
  protected final String TEXT_68 = ".open(javax.mail.Folder.READ_ONLY);" + NL + "}" + NL + "javax.mail.Message[] msgs_";
  protected final String TEXT_69 = " = folder_";
  protected final String TEXT_70 = ".getMessages();" + NL + "" + NL + "for (int counter_";
  protected final String TEXT_71 = " = 0; counter_";
  protected final String TEXT_72 = " < msgs_";
  protected final String TEXT_73 = ".length; counter_";
  protected final String TEXT_74 = "++) {";
  protected final String TEXT_75 = NL + "    if(nb_email_";
  protected final String TEXT_76 = " >= ";
  protected final String TEXT_77 = ") break; ";
  protected final String TEXT_78 = NL + "  javax.mail.Message message_";
  protected final String TEXT_79 = " = msgs_";
  protected final String TEXT_80 = "[counter_";
  protected final String TEXT_81 = "];" + NL + "  " + NL + "  try {" + NL + "    boolean isMatch_";
  protected final String TEXT_82 = " = ";
  protected final String TEXT_83 = ";" + NL + "  ";
  protected final String TEXT_84 = NL + "        java.util.regex.Pattern pattern_";
  protected final String TEXT_85 = "_";
  protected final String TEXT_86 = " = java.util.regex.Pattern.compile(";
  protected final String TEXT_87 = ");" + NL + "        java.util.regex.Matcher matcher_";
  protected final String TEXT_88 = "_";
  protected final String TEXT_89 = " = pattern_";
  protected final String TEXT_90 = "_";
  protected final String TEXT_91 = ".matcher(message_";
  protected final String TEXT_92 = ".getSubject()==null?\"\":message_";
  protected final String TEXT_93 = ".getSubject());" + NL + "        isMatch_";
  protected final String TEXT_94 = " = isMatch_";
  protected final String TEXT_95 = " ";
  protected final String TEXT_96 = " matcher_";
  protected final String TEXT_97 = "_";
  protected final String TEXT_98 = ".find();";
  protected final String TEXT_99 = NL + "        java.util.regex.Pattern pattern_";
  protected final String TEXT_100 = "_";
  protected final String TEXT_101 = " = java.util.regex.Pattern.compile(";
  protected final String TEXT_102 = ");" + NL + "        java.util.regex.Matcher matcher_";
  protected final String TEXT_103 = "_";
  protected final String TEXT_104 = " = pattern_";
  protected final String TEXT_105 = "_";
  protected final String TEXT_106 = ".matcher(message_";
  protected final String TEXT_107 = ".getFrom()[0]==null?\"\":message_";
  protected final String TEXT_108 = ".getFrom()[0].toString());" + NL + "        isMatch_";
  protected final String TEXT_109 = " = isMatch_";
  protected final String TEXT_110 = " ";
  protected final String TEXT_111 = " matcher_";
  protected final String TEXT_112 = "_";
  protected final String TEXT_113 = ".find();";
  protected final String TEXT_114 = NL + "        java.util.regex.Pattern pattern_";
  protected final String TEXT_115 = "_";
  protected final String TEXT_116 = " = java.util.regex.Pattern.compile(";
  protected final String TEXT_117 = ");" + NL + "        java.util.regex.Matcher matcher_";
  protected final String TEXT_118 = "_";
  protected final String TEXT_119 = " = pattern_";
  protected final String TEXT_120 = "_";
  protected final String TEXT_121 = ".matcher(message_";
  protected final String TEXT_122 = ".getReplyTo()[0]==null?\"\":message_";
  protected final String TEXT_123 = ".getReplyTo()[0].toString());" + NL + "        isMatch_";
  protected final String TEXT_124 = " = isMatch_";
  protected final String TEXT_125 = " ";
  protected final String TEXT_126 = " matcher_";
  protected final String TEXT_127 = "_";
  protected final String TEXT_128 = ".find();";
  protected final String TEXT_129 = NL + "        java.util.Date date_";
  protected final String TEXT_130 = "_";
  protected final String TEXT_131 = " = null;" + NL + "  " + NL + "        try {" + NL + "          date_";
  protected final String TEXT_132 = "_";
  protected final String TEXT_133 = " = ParserUtils.parseTo_Date(";
  protected final String TEXT_134 = ", \"dd-MM-yyyy HH:mm:ss\");" + NL + "        } catch (Exception e){" + NL + "          date_";
  protected final String TEXT_135 = "_";
  protected final String TEXT_136 = " = ParserUtils.parseTo_Date(";
  protected final String TEXT_137 = ", \"dd-MM-yyyy\");" + NL + "        }" + NL + "  " + NL + "        if (date_";
  protected final String TEXT_138 = "_";
  protected final String TEXT_139 = " != null) {" + NL + "          isMatch_";
  protected final String TEXT_140 = " = isMatch_";
  protected final String TEXT_141 = " ";
  protected final String TEXT_142 = " (message_";
  protected final String TEXT_143 = ".getSentDate()!=null && message_";
  protected final String TEXT_144 = ".getSentDate().";
  protected final String TEXT_145 = "before";
  protected final String TEXT_146 = "after";
  protected final String TEXT_147 = "(date_";
  protected final String TEXT_148 = "_";
  protected final String TEXT_149 = "));" + NL + "        }";
  protected final String TEXT_150 = NL + "      if (isMatch_";
  protected final String TEXT_151 = ") {";
  protected final String TEXT_152 = " " + NL + "      message_";
  protected final String TEXT_153 = ".setFlag(javax.mail.Flags.Flag.DELETED, true);";
  protected final String TEXT_154 = NL + "    String filename_";
  protected final String TEXT_155 = " = ";
  protected final String TEXT_156 = ";" + NL + "    java.util.Enumeration enums_";
  protected final String TEXT_157 = " = message_";
  protected final String TEXT_158 = ".getAllHeaders();" + NL + "    java.io.File file_";
  protected final String TEXT_159 = " = new java.io.File(";
  protected final String TEXT_160 = ", filename_";
  protected final String TEXT_161 = ");" + NL + "    java.io.OutputStream os_";
  protected final String TEXT_162 = " = new java.io.FileOutputStream(file_";
  protected final String TEXT_163 = ");" + NL + "    " + NL + "    while (enums_";
  protected final String TEXT_164 = ".hasMoreElements()) {" + NL + "      javax.mail.Header header_";
  protected final String TEXT_165 = " = (javax.mail.Header) enums_";
  protected final String TEXT_166 = ".nextElement();" + NL + "      os_";
  protected final String TEXT_167 = ".write(new StringBuilder(header_";
  protected final String TEXT_168 = ".getName()).append(\": \").append(header_";
  protected final String TEXT_169 = ".getValue()).append(\"\\r\\n\").toString().getBytes());" + NL + "    }" + NL + "    os_";
  protected final String TEXT_170 = ".write(\"\\r\\n\".getBytes());" + NL + "    java.io.InputStream in_";
  protected final String TEXT_171 = " = message_";
  protected final String TEXT_172 = ".getInputStream();" + NL + "    byte[] buffer_";
  protected final String TEXT_173 = " = new byte[1024];" + NL + "    int length_";
  protected final String TEXT_174 = " = 0;" + NL + "  " + NL + "    while ((length_";
  protected final String TEXT_175 = " = in_";
  protected final String TEXT_176 = ".read(buffer_";
  protected final String TEXT_177 = ", 0, 1024)) != -1) {" + NL + "      os_";
  protected final String TEXT_178 = ".write(buffer_";
  protected final String TEXT_179 = ", 0, length_";
  protected final String TEXT_180 = ");" + NL + "    }" + NL + "    os_";
  protected final String TEXT_181 = ".close();" + NL + "    nb_email_";
  protected final String TEXT_182 = "++;" + NL + "    globalMap.put(\"";
  protected final String TEXT_183 = "_CURRENT_FILE\", filename_";
  protected final String TEXT_184 = ");" + NL + "    globalMap.put(\"";
  protected final String TEXT_185 = "_CURRENT_FILEPATH\", file_";
  protected final String TEXT_186 = ".getAbsolutePath());";
  protected final String TEXT_187 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String host = ElementParameterParser.getValue(node, "__HOST__");
String username = ElementParameterParser.getValue(node, "__USERNAME__");
String password = ElementParameterParser.getValue(node, "__PASSWORD__");
String port = ElementParameterParser.getValue(node, "__PORT__");
String outputDirectory = ElementParameterParser.getValue(node, "__OUTPUT_DIRECTORY__");
String filenamePattern = ElementParameterParser.getValue(node, "__FILENAME_PATTERN__");
String allEmails = ElementParameterParser.getValue(node, "__ALL_EMAILS__");
String maxEmails = ElementParameterParser.getValue(node, "__MAX_EMAILS__");
boolean bDeleteFromServer = "true".equals(ElementParameterParser.getValue(node, "__DELETE_FROM_SERVER__"));
String protocol = ElementParameterParser.getValue(node, "__PROTOCOL__");
List<Map<String, String>> filterList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ADVANCED_FILTER__");
String condition = ElementParameterParser.getValue(node, "__FILTER_RELATION__");
boolean useSSL = "true".equals(ElementParameterParser.getValue(node, "__USE_SSL__"));

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(host );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(port );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    
if (useSSL) {
  if ("pop3".equals(protocol)){
  
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    
  } else {
  
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    
  }
}

if ("pop3".equals(protocol)){

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(protocol);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    
} else {
  if ("true".equals(ElementParameterParser.getValue(node, "__SPECIFY_MAIL_FOLDER__"))){
  
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(ElementParameterParser.getValue(node, "__MAIL_FOLDER__"));
    stringBuffer.append(TEXT_49);
    
  }
  
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(protocol);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
}

    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    if ("false".equals(allEmails)) {
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(maxEmails);
    stringBuffer.append(TEXT_77);
    }
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(("&&").equals(condition) ? "true" : "false");
    stringBuffer.append(TEXT_83);
    
    for (int i = 0;i < filterList.size(); i++) {
      String filterItem = filterList.get(i).get("FILTER_TIEM");
      String filterptn = filterList.get(i).get("PATTERN");
  
      if ("Subject".equals(filterItem)) {
      
    stringBuffer.append(TEXT_84);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(filterptn);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(condition);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    
      } else if ("From".equals(filterItem)) {
      
    stringBuffer.append(TEXT_99);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(filterptn);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(condition);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    
      } else if ("To".equals(filterItem)) {
      
    stringBuffer.append(TEXT_114);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(filterptn);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(condition);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_128);
    
      } else {
      
    stringBuffer.append(TEXT_129);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(filterptn);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(filterptn);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(condition);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    if ("BeforeDate".equals(filterItem)){
    stringBuffer.append(TEXT_145);
    }else if("AfterDate".equals(filterItem)){
    stringBuffer.append(TEXT_146);
    }
    stringBuffer.append(TEXT_147);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    
      }
    }
          
    if (filterList.size()>0){
    
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    
    }
    
    if (bDeleteFromServer){
    
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_153);
    }
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(filenamePattern );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(outputDirectory );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(TEXT_187);
    return stringBuffer.toString();
  }
}
