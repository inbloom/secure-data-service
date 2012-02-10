package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;
import java.util.Enumeration;

public class TFileInputMailBeginJava
{
  protected static String nl;
  public static synchronized TFileInputMailBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputMailBeginJava result = new TFileInputMailBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = " " + NL + "\tif(!(";
  protected final String TEXT_3 = ").endsWith(\"/\")){" + NL + "           globalMap.put(\"";
  protected final String TEXT_4 = "_EXPORTED_FILE_PATH\",";
  protected final String TEXT_5 = " + \"/\");" + NL + "        \t\t}else" + NL + "        \t\t{" + NL + "        globalMap.put(\"";
  protected final String TEXT_6 = "_EXPORTED_FILE_PATH\",";
  protected final String TEXT_7 = ");" + NL + "        \t\t}" + NL + "" + NL + "///////////////////////////////////     " + NL + "   String [] mailParts_";
  protected final String TEXT_8 = " = new String [] {";
  protected final String TEXT_9 = NL + "\t \t";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = NL + "\t};" + NL + "   String [] mailChecked_";
  protected final String TEXT_12 = " = new String [] {";
  protected final String TEXT_13 = NL + "\t \t\"";
  protected final String TEXT_14 = "\",";
  protected final String TEXT_15 = NL + "\t};" + NL + "\t   String [] mailSeparator_";
  protected final String TEXT_16 = " = new String [] {";
  protected final String TEXT_17 = NL + "\t\t\t";
  protected final String TEXT_18 = ",";
  protected final String TEXT_19 = NL + "\t \t\t";
  protected final String TEXT_20 = ",";
  protected final String TEXT_21 = NL + "\t};" + NL + "java.io.FileInputStream fileInput";
  protected final String TEXT_22 = "=null;\t" + NL + "\t" + NL + "try{" + NL + "\tfileInput";
  protected final String TEXT_23 = " = new java.io.FileInputStream(";
  protected final String TEXT_24 = ");" + NL + "\tjavax.mail.Session session_";
  protected final String TEXT_25 = " = javax.mail.Session.getInstance(System.getProperties(), null);" + NL + "    javax.mail.internet.MimeMessage msg_";
  protected final String TEXT_26 = " = new javax.mail.internet.MimeMessage(session_";
  protected final String TEXT_27 = ", fileInput";
  protected final String TEXT_28 = ");" + NL + "\tjava.util.List<String> list_";
  protected final String TEXT_29 = " = new java.util.ArrayList<String>();" + NL + "" + NL + "\tfor (int i_";
  protected final String TEXT_30 = " =0;i_";
  protected final String TEXT_31 = " < mailParts_";
  protected final String TEXT_32 = ".length;i_";
  protected final String TEXT_33 = "++)" + NL + "\t{\t\t\t\t" + NL + "\t\tString part_";
  protected final String TEXT_34 = " = mailParts_";
  protected final String TEXT_35 = "[i_";
  protected final String TEXT_36 = "];" + NL + "       \tString sep_";
  protected final String TEXT_37 = "= mailSeparator_";
  protected final String TEXT_38 = "[i_";
  protected final String TEXT_39 = "];" + NL + "        if(part_";
  protected final String TEXT_40 = ".equalsIgnoreCase(\"body\"))" + NL + "        {        " + NL + "             if(msg_";
  protected final String TEXT_41 = ".isMimeType(\"multipart/*\")) {" + NL + "                 javax.mail.Multipart mp";
  protected final String TEXT_42 = " = (javax.mail.Multipart) msg_";
  protected final String TEXT_43 = ".getContent();" + NL + "                 String attachfileName";
  protected final String TEXT_44 = " = \"\";" + NL + "                 String path";
  protected final String TEXT_45 = " = \"\";" + NL + "                 java.io.BufferedOutputStream out";
  protected final String TEXT_46 = " = null;" + NL + "                 java.io.BufferedInputStream in";
  protected final String TEXT_47 = " = null;" + NL + "                 for (int i = 0; i < mp";
  protected final String TEXT_48 = ".getCount(); i++) {" + NL + "                     javax.mail.BodyPart mpart";
  protected final String TEXT_49 = " = mp";
  protected final String TEXT_50 = ".getBodyPart(i);" + NL + "                     String disposition";
  protected final String TEXT_51 = " = mpart";
  protected final String TEXT_52 = ".getDisposition();" + NL + "                     if (!((disposition";
  protected final String TEXT_53 = " != null) && ((disposition";
  protected final String TEXT_54 = NL + "                                .equals(javax.mail.Part.ATTACHMENT)) || (disposition";
  protected final String TEXT_55 = ".equals(javax.mail.Part.INLINE)))))" + NL + "                                " + NL + "                        {   " + NL + "                        \t// the following extract the body part(text/plain + text/html)" + NL + "    \t\t\t\t\t\tObject content_";
  protected final String TEXT_56 = " = mpart";
  protected final String TEXT_57 = ".getContent();" + NL + "    \t\t\t\t\t\tif (content_";
  protected final String TEXT_58 = " instanceof javax.mail.internet.MimeMultipart) {" + NL + "    \t\t\t\t\t\t\tjavax.mail.internet.MimeMultipart mimeMultipart_";
  protected final String TEXT_59 = " = (javax.mail.internet.MimeMultipart) content_";
  protected final String TEXT_60 = ";" + NL + "    \t\t\t\t\t\t\tfor (int j_";
  protected final String TEXT_61 = " = 0; j_";
  protected final String TEXT_62 = " < mimeMultipart_";
  protected final String TEXT_63 = " .getCount(); j_";
  protected final String TEXT_64 = "++) {" + NL + "    \t\t\t\t\t\t\t\tjavax.mail.BodyPart bodyPart_";
  protected final String TEXT_65 = " = mimeMultipart_";
  protected final String TEXT_66 = " .getBodyPart(j_";
  protected final String TEXT_67 = ");" + NL + "    \t\t\t\t\t\t\t\tif (bodyPart_";
  protected final String TEXT_68 = ".isMimeType(\"text/plain\")) {" + NL + "    \t\t\t\t\t\t\t\t\tlist_";
  protected final String TEXT_69 = ".add(bodyPart_";
  protected final String TEXT_70 = ".getContent().toString());" + NL + "    \t\t\t\t\t\t\t\t} else {    " + NL + "    \t\t\t\t\t\t\t\t\tSystem.out.println(\"Ignore the part \" + bodyPart_";
  protected final String TEXT_71 = ".getContentType());" + NL + "    \t\t\t\t\t\t\t\t}" + NL + "    \t\t\t\t\t\t\t}" + NL + "    \t\t\t\t\t\t} else {    " + NL + "    \t\t\t\t\t\t\tlist_";
  protected final String TEXT_72 = ".add(mpart";
  protected final String TEXT_73 = ".getContent().toString());" + NL + "    \t\t\t\t\t\t}    " + NL + "    \t\t\t\t\t}" + NL + "                 }" + NL + "     \t\t } else {" + NL + "     \t\t    java.io.InputStream in_";
  protected final String TEXT_74 = " = msg_";
  protected final String TEXT_75 = ".getInputStream();" + NL + "     \t\t    byte[] buffer_";
  protected final String TEXT_76 = " = new byte[1024];" + NL + "     \t\t    int length_";
  protected final String TEXT_77 = " = 0;" + NL + "     \t\t    java.io.ByteArrayOutputStream baos_";
  protected final String TEXT_78 = " =  new java.io.ByteArrayOutputStream();" + NL + "     \t\t    while ((length_";
  protected final String TEXT_79 = " = in_";
  protected final String TEXT_80 = ".read(buffer_";
  protected final String TEXT_81 = ", 0, 1024)) != -1) {" + NL + "     \t\t        baos_";
  protected final String TEXT_82 = ".write(buffer_";
  protected final String TEXT_83 = ", 0, length_";
  protected final String TEXT_84 = ");" + NL + "     \t\t    }" + NL + "     \t\t    list_";
  protected final String TEXT_85 = ".add(baos_";
  protected final String TEXT_86 = ".toString());" + NL + "     \t\t    in_";
  protected final String TEXT_87 = ".close();" + NL + "     \t\t    baos_";
  protected final String TEXT_88 = ".close();" + NL + "     \t\t }" + NL + "        }else if(part_";
  protected final String TEXT_89 = ".equalsIgnoreCase(\"header\")){" + NL + "            java.util.Enumeration em = msg_";
  protected final String TEXT_90 = ".getAllHeaderLines();" + NL + "            int em_count=0;" + NL + "            " + NL + "            String tempStr_";
  protected final String TEXT_91 = "=\"\";" + NL + "            " + NL + "\t\t\twhile (em.hasMoreElements()) {" + NL + "\t\t\t\ttempStr_";
  protected final String TEXT_92 = " = tempStr_";
  protected final String TEXT_93 = " + (String) em.nextElement() + sep_";
  protected final String TEXT_94 = " ;" + NL + "\t\t\t}" + NL + "\t\t\tlist_";
  protected final String TEXT_95 = ".add(tempStr_";
  protected final String TEXT_96 = ");" + NL + "        }else{" + NL + "        \tif((\"true\").equals(mailChecked_";
  protected final String TEXT_97 = "[i_";
  protected final String TEXT_98 = "])){   " + NL + "\t\t\t\tString[] sa_";
  protected final String TEXT_99 = " = msg_";
  protected final String TEXT_100 = ".getHeader(part_";
  protected final String TEXT_101 = ");" + NL + "\t\t\t\tString tempStr_";
  protected final String TEXT_102 = "=\"\";" + NL + "\t\t\t\tfor(int i=0;i<sa_";
  protected final String TEXT_103 = ".length;i++){" + NL + "\t\t\t\t\ttempStr_";
  protected final String TEXT_104 = "=tempStr_";
  protected final String TEXT_105 = "+sa_";
  protected final String TEXT_106 = "[i] + sep_";
  protected final String TEXT_107 = ";" + NL + "\t\t\t\t}" + NL + "\t\t\t\tlist_";
  protected final String TEXT_108 = ".add(tempStr_";
  protected final String TEXT_109 = ");" + NL + "        \t}else{ " + NL + "\t           String content_";
  protected final String TEXT_110 = " = msg_";
  protected final String TEXT_111 = ".getHeader(part_";
  protected final String TEXT_112 = ", null);" + NL + "\t           list_";
  protected final String TEXT_113 = ".add(content_";
  protected final String TEXT_114 = ");" + NL + "           \t}    " + NL + "        }   " + NL + " \t}   " + NL + "\t" + NL + "" + NL + "\t" + NL + " \t//attachment Deal" + NL + " \tif(msg_";
  protected final String TEXT_115 = ".isMimeType(\"multipart/*\")){" + NL + " \t      javax.mail.Multipart mp";
  protected final String TEXT_116 = " = (javax.mail.Multipart) msg_";
  protected final String TEXT_117 = ".getContent();" + NL + " \t      String attachfileName";
  protected final String TEXT_118 = " = \"\";" + NL + " \t      String path";
  protected final String TEXT_119 = " = \"\";" + NL + " \t      java.io.BufferedOutputStream out";
  protected final String TEXT_120 = " = null;" + NL + " \t      java.io.BufferedInputStream in";
  protected final String TEXT_121 = " = null;" + NL + "            for (int i = 0; i < mp";
  protected final String TEXT_122 = ".getCount(); i++) {" + NL + "                javax.mail.BodyPart mpart";
  protected final String TEXT_123 = " = mp";
  protected final String TEXT_124 = ".getBodyPart(i);" + NL + "                String disposition";
  protected final String TEXT_125 = " = mpart";
  protected final String TEXT_126 = ".getDisposition();" + NL + "                if (disposition";
  protected final String TEXT_127 = " != null" + NL + "                        && disposition";
  protected final String TEXT_128 = ".equals(javax.mail.Part.ATTACHMENT)) {" + NL + "                    attachfileName";
  protected final String TEXT_129 = " = mpart";
  protected final String TEXT_130 = ".getFileName();" + NL + "                    " + NL + "                    if (attachfileName";
  protected final String TEXT_131 = ".indexOf(\"=?\") != -1){" + NL + "                      int m_";
  protected final String TEXT_132 = " = 2, n_";
  protected final String TEXT_133 = ";" + NL + "                      n_";
  protected final String TEXT_134 = " = attachfileName";
  protected final String TEXT_135 = ".indexOf(63, m_";
  protected final String TEXT_136 = "); // the first ? location " + NL + "                      String sCharSet_";
  protected final String TEXT_137 = " = attachfileName";
  protected final String TEXT_138 = ".substring(attachfileName";
  protected final String TEXT_139 = ".indexOf(\"=?\") + 2, n_";
  protected final String TEXT_140 = "); // the character set value         " + NL + "                      m_";
  protected final String TEXT_141 = " = n_";
  protected final String TEXT_142 = " + 1;                      " + NL + "                      n_";
  protected final String TEXT_143 = " = attachfileName";
  protected final String TEXT_144 = ".indexOf(63, m_";
  protected final String TEXT_145 = "); // the second ? location                      " + NL + "                      String flag_";
  protected final String TEXT_146 = " = attachfileName";
  protected final String TEXT_147 = ".substring(m_";
  protected final String TEXT_148 = ", n_";
  protected final String TEXT_149 = ");" + NL + "                      m_";
  protected final String TEXT_150 = " = n_";
  protected final String TEXT_151 = " + 1;" + NL + "                      n_";
  protected final String TEXT_152 = " = attachfileName";
  protected final String TEXT_153 = ".indexOf(\"?=\", m_";
  protected final String TEXT_154 = ");                      " + NL + "                      String sNameCode_";
  protected final String TEXT_155 = " = attachfileName";
  protected final String TEXT_156 = ".substring(m_";
  protected final String TEXT_157 = ", n_";
  protected final String TEXT_158 = ");                  " + NL + "                      java.io.ByteArrayInputStream byteArrIS_";
  protected final String TEXT_159 = " = new java.io.ByteArrayInputStream(sNameCode_";
  protected final String TEXT_160 = ".getBytes());                      " + NL + "                      Object obj_";
  protected final String TEXT_161 = " = null;" + NL + "                      " + NL + "                      if (flag_";
  protected final String TEXT_162 = ".equalsIgnoreCase(\"B\")) {" + NL + "                        obj_";
  protected final String TEXT_163 = " = new com.sun.mail.util.BASE64DecoderStream(byteArrIS_";
  protected final String TEXT_164 = ");                    " + NL + "                      } else if (flag_";
  protected final String TEXT_165 = ".equalsIgnoreCase(\"Q\")) {" + NL + "                        obj_";
  protected final String TEXT_166 = " = new com.sun.mail.util.QDecoderStream(byteArrIS_";
  protected final String TEXT_167 = ");" + NL + "                      }" + NL + "                      " + NL + "                      if (obj_";
  protected final String TEXT_168 = " != null){" + NL + "                        int k_";
  protected final String TEXT_169 = " = byteArrIS_";
  protected final String TEXT_170 = ".available();" + NL + "                        byte[] arrByte_";
  protected final String TEXT_171 = " = new byte[k_";
  protected final String TEXT_172 = "];" + NL + "                        k_";
  protected final String TEXT_173 = " = ((java.io.InputStream) (obj_";
  protected final String TEXT_174 = ")).read(arrByte_";
  protected final String TEXT_175 = ", 0, k_";
  protected final String TEXT_176 = ");" + NL + "                        attachfileName";
  protected final String TEXT_177 = " = new String(arrByte_";
  protected final String TEXT_178 = ", 0, k_";
  protected final String TEXT_179 = ", sCharSet_";
  protected final String TEXT_180 = ");" + NL + "                      }" + NL + "                    }" + NL + "                    " + NL + "                     if(!(";
  protected final String TEXT_181 = ").endsWith(\"/\")){" + NL + "           \t\t\t\t path";
  protected final String TEXT_182 = " = ";
  protected final String TEXT_183 = " + \"/\";" + NL + "        \t\t\t\t}else" + NL + "        \t\t\t\t{" + NL + "        \t\t\t\t\tpath";
  protected final String TEXT_184 = " =";
  protected final String TEXT_185 = ";" + NL + "        \t\t\t\t}" + NL + "                    path";
  protected final String TEXT_186 = " = path";
  protected final String TEXT_187 = " + attachfileName";
  protected final String TEXT_188 = ";" + NL + "                    java.io.File attachFile  = new java.io.File(path";
  protected final String TEXT_189 = ");" + NL + "                    out";
  protected final String TEXT_190 = " = new java.io.BufferedOutputStream(new java.io.FileOutputStream(attachFile));" + NL + "                    in";
  protected final String TEXT_191 = " = new java.io.BufferedInputStream(mpart";
  protected final String TEXT_192 = ".getInputStream());" + NL + "                    int buffer";
  protected final String TEXT_193 = " = 0;" + NL + "                    while ((buffer";
  protected final String TEXT_194 = " = in";
  protected final String TEXT_195 = ".read()) != -1) {" + NL + "                           out";
  protected final String TEXT_196 = ".write(buffer";
  protected final String TEXT_197 = ");" + NL + "                           out";
  protected final String TEXT_198 = ".flush();" + NL + "                         }   " + NL + "                        out";
  protected final String TEXT_199 = ".close();" + NL + "                        in";
  protected final String TEXT_200 = ".close();    " + NL + "                }" + NL + "            }" + NL + " \t}" + NL + " \t         " + NL + " \t          " + NL + "// for output";
  protected final String TEXT_201 = NL + "\t\t\t\t\t\t" + NL + "\t\t\t" + NL + "\t\t\tif(";
  protected final String TEXT_202 = " < list_";
  protected final String TEXT_203 = ".size() && list_";
  protected final String TEXT_204 = ".get(";
  protected final String TEXT_205 = ")!=null){\t\t\t\t";
  protected final String TEXT_206 = NL + "\t\t\t\t\t";
  protected final String TEXT_207 = ".";
  protected final String TEXT_208 = " = (String)list_";
  protected final String TEXT_209 = ".get(";
  protected final String TEXT_210 = ");";
  protected final String TEXT_211 = NL + "\t\t\t\t\t";
  protected final String TEXT_212 = ".";
  protected final String TEXT_213 = " = ParserUtils.parseTo_Date(list_";
  protected final String TEXT_214 = ".get(";
  protected final String TEXT_215 = "), ";
  protected final String TEXT_216 = ");";
  protected final String TEXT_217 = NL + "\t\t\t\t\t";
  protected final String TEXT_218 = ".";
  protected final String TEXT_219 = " = list_";
  protected final String TEXT_220 = ".get(";
  protected final String TEXT_221 = ").getBytes();";
  protected final String TEXT_222 = "\t\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_223 = ".";
  protected final String TEXT_224 = " = ParserUtils.parseTo_";
  protected final String TEXT_225 = "(list_";
  protected final String TEXT_226 = ".get(";
  protected final String TEXT_227 = "));";
  protected final String TEXT_228 = NL + "\t\t\t" + NL + "\t\t\t} else { " + NL + "\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_229 = ".";
  protected final String TEXT_230 = " = ";
  protected final String TEXT_231 = ";" + NL + "\t\t\t}" + NL;
  protected final String TEXT_232 = NL + "      \t\t\t";
  protected final String TEXT_233 = ".";
  protected final String TEXT_234 = " = ";
  protected final String TEXT_235 = ".";
  protected final String TEXT_236 = ";" + NL + "\t\t\t\t ";
  protected final String TEXT_237 = NL + "}" + NL;
  protected final String TEXT_238 = NL + "catch (Exception e){" + NL + "//nothing to do, ignore the exception if don't die on error" + NL + "System.err.println(\"Exception in component ";
  protected final String TEXT_239 = ": \" + e.getMessage());" + NL + "}";
  protected final String TEXT_240 = NL + NL + "finally{" + NL + "\tif(fileInput";
  protected final String TEXT_241 = "!=null)" + NL + "\t\t\tfileInput";
  protected final String TEXT_242 = ".close();" + NL + "} " + NL + "////////////////////////////";
  protected final String TEXT_243 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        // component id
        String cid = node.getUniqueName();
        String filename = ElementParameterParser.getValue(node,"__FILENAME__");
        String directory = ElementParameterParser.getValue(node,"__ATTACHMENT_PATH__");
        List<Map<String, String>> mailParts = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__MAIL_PARTS__");
        
    	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
		boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;

    stringBuffer.append(TEXT_2);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    
	for (int i=0; i<mailParts.size(); i++) {
	   Map<String, String> lineValue = mailParts.get(i);

    stringBuffer.append(TEXT_9);
    stringBuffer.append( lineValue.get("MAIL_PART") );
    stringBuffer.append(TEXT_10);
    
	}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
	for (int i=0; i<mailParts.size(); i++) {
	   Map<String, String> lineValue = mailParts.get(i);

    stringBuffer.append(TEXT_13);
    stringBuffer.append( lineValue.get("MULTI_VALUE") );
    stringBuffer.append(TEXT_14);
    
	}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
	for (int i=0; i<mailParts.size(); i++) {
	   Map<String, String> lineValue = mailParts.get(i);
	   if(("").equals(lineValue.get("PART_SEPARATOR"))){

    stringBuffer.append(TEXT_17);
    stringBuffer.append("\"\"");
    stringBuffer.append(TEXT_18);
    		}else{
    stringBuffer.append(TEXT_19);
    stringBuffer.append( lineValue.get("PART_SEPARATOR") );
    stringBuffer.append(TEXT_20);
    
		}
	}

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_180);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_182);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_192);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	String firstConnName = "";
	if (conns!=null) {//1
		if (conns.size()>0) {//2
		
			IConnection conn = conns.get(0); //the first connection
			firstConnName = conn.getName();			
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//3

			List<IMetadataColumn> columns=metadata.getListColumns();
			int columnSize = columns.size();
			for (int i=0;i<columnSize;i++) {//4
					IMetadataColumn column=columns.get(i);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
			
    stringBuffer.append(TEXT_201);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_204);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_205);
    
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) { //String and Object

    stringBuffer.append(TEXT_206);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_207);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_209);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_210);
    
					} else if(javaType == JavaTypesManager.DATE) { //Date

    stringBuffer.append(TEXT_211);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_215);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_216);
    
					} else if(javaType == JavaTypesManager.BYTE_ARRAY) { //byte[]

    stringBuffer.append(TEXT_217);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_218);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_220);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_221);
    
					}else  { //other

    stringBuffer.append(TEXT_222);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_224);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_226);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_227);
    
					}

    stringBuffer.append(TEXT_228);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_229);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate));
    stringBuffer.append(TEXT_231);
    			
			} //4
		}//3
		
		
		if (conns.size()>1) {
			for (int i=1;i<conns.size();i++) {
				IConnection conn2 = conns.get(i);
				if ((conn2.getName().compareTo(firstConnName)!=0)&&(conn2.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
			    	for (IMetadataColumn column: metadata.getListColumns()) {
    stringBuffer.append(TEXT_232);
    stringBuffer.append(conn2.getName() );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_234);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_235);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_236);
    
				 	}
				}
			}
		}
		
		
	}//2
	
}//1


    stringBuffer.append(TEXT_237);
     if(!dieOnError){ 
    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_239);
    
  }

    stringBuffer.append(TEXT_240);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_241);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_242);
    
  }
}  
 
    stringBuffer.append(TEXT_243);
    return stringBuffer.toString();
  }
}
