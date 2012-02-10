package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;

public class TFileArchiveMainJava
{
  protected static String nl;
  public static synchronized TFileArchiveMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileArchiveMainJava result = new TFileArchiveMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + NL + "    String sourceDir_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = ";      " + NL + "    java.io.File file_";
  protected final String TEXT_4 = " = new java.io.File(sourceDir_";
  protected final String TEXT_5 = ");" + NL + "    String zipFile_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";";
  protected final String TEXT_8 = NL + "    if(new java.io.File(zipFile_";
  protected final String TEXT_9 = ").exists()){" + NL + "    \tthrow(new Exception(\"File already exist!\"));" + NL + "    }";
  protected final String TEXT_10 = NL + "    String zipDir_";
  protected final String TEXT_11 = " = (";
  protected final String TEXT_12 = ").contains(\"\\\\\")?" + NL + "    (";
  protected final String TEXT_13 = ").substring(0,(";
  protected final String TEXT_14 = ").lastIndexOf(\"\\\\\")): (";
  protected final String TEXT_15 = ").substring(0,(";
  protected final String TEXT_16 = ").lastIndexOf(\"/\"));" + NL + "    java.io.File zDir_";
  protected final String TEXT_17 = " = new java.io.File(zipDir_";
  protected final String TEXT_18 = ");" + NL + "    zDir_";
  protected final String TEXT_19 = ".mkdirs();";
  protected final String TEXT_20 = NL + "    int level_";
  protected final String TEXT_21 = " = ";
  protected final String TEXT_22 = ";        " + NL + "    final java.util.List<java.io.File> list_";
  protected final String TEXT_23 = " = new java.util.ArrayList<java.io.File>();  ";
  protected final String TEXT_24 = "  " + NL + "\t    String[] patterns_";
  protected final String TEXT_25 = " = new String[";
  protected final String TEXT_26 = "];" + NL + "\t    ";
  protected final String TEXT_27 = NL + "\t        patterns_";
  protected final String TEXT_28 = "[";
  protected final String TEXT_29 = "] = ";
  protected final String TEXT_30 = ";" + NL + "\t        ";
  protected final String TEXT_31 = NL + "  \t\tStringBuilder sbf_";
  protected final String TEXT_32 = " = new StringBuilder(); " + NL + "        sbf_";
  protected final String TEXT_33 = ".append(\"(\");" + NL + "        for (int i_";
  protected final String TEXT_34 = " = 0 ; i_";
  protected final String TEXT_35 = " < patterns_";
  protected final String TEXT_36 = ".length ; i_";
  protected final String TEXT_37 = "++) {" + NL + "            sbf_";
  protected final String TEXT_38 = ".append(patterns_";
  protected final String TEXT_39 = "[i_";
  protected final String TEXT_40 = "].replaceAll(\"\\\\.\", \"\\\\\\\\.\").replaceAll(\"\\\\*\", \".*\"));" + NL + "            if(i_";
  protected final String TEXT_41 = " != patterns_";
  protected final String TEXT_42 = ".length-1) sbf_";
  protected final String TEXT_43 = ".append(\"|\");" + NL + "        }        " + NL + "        sbf_";
  protected final String TEXT_44 = ".append(\")\");" + NL + "        final String maskStr_";
  protected final String TEXT_45 = " = new String(sbf_";
  protected final String TEXT_46 = ".toString());        " + NL + "        file_";
  protected final String TEXT_47 = ".listFiles(new java.io.FilenameFilter() {" + NL + "            public boolean accept(java.io.File dir, String name) {" + NL + "                java.io.File file = new java.io.File(dir, name);" + NL + "                if (file.isFile()) {                            " + NL + "                    if (name.matches(maskStr_";
  protected final String TEXT_48 = ")) {" + NL + "                        list_";
  protected final String TEXT_49 = ".add(file);" + NL + "                        return true;" + NL + "                    }            " + NL + "                }";
  protected final String TEXT_50 = "  " + NL + "                    else" + NL + "                    {   " + NL + "                    \tfile.listFiles(this);                    " + NL + "                    }";
  protected final String TEXT_51 = "                  " + NL + "                return false;" + NL + "            }" + NL + "        });";
  protected final String TEXT_52 = " " + NL + "       file_";
  protected final String TEXT_53 = ".listFiles(new java.io.FilenameFilter() {" + NL + "           public boolean accept(java.io.File dir, String name) {" + NL + "               java.io.File file = new java.io.File(dir, name);" + NL + "                   if (file.isFile()) {" + NL + "                       list_";
  protected final String TEXT_54 = ".add(file);" + NL + "                       return true;" + NL + "                   }";
  protected final String TEXT_55 = "  " + NL + "                       else" + NL + "                       {   " + NL + "                           file.listFiles(this);                    " + NL + "                       }";
  protected final String TEXT_56 = "   " + NL + "                   return false;" + NL + "                }" + NL + "            });";
  protected final String TEXT_57 = "     " + NL + "      org.apache.tools.zip.ZipOutputStream out_";
  protected final String TEXT_58 = " = null;" + NL + "      if (list_";
  protected final String TEXT_59 = ".size() > 0) {" + NL + "      \tint beginIndex_";
  protected final String TEXT_60 = " = file_";
  protected final String TEXT_61 = ".getPath().length()+1;" + NL + "      \tjava.io.OutputStream output_stream_";
  protected final String TEXT_62 = " = new java.io.FileOutputStream(zipFile_";
  protected final String TEXT_63 = ");" + NL + "      \tif( ";
  protected final String TEXT_64 = " && !";
  protected final String TEXT_65 = ".equals(\"\")){" + NL + "      \t\toutput_stream_";
  protected final String TEXT_66 = " = new javax.crypto.CipherOutputStream(output_stream_";
  protected final String TEXT_67 = ", " + NL + "      \t\t\t\t                       org.talend.archive.IntegrityUtil.createCipher( javax.crypto.Cipher.ENCRYPT_MODE, ";
  protected final String TEXT_68 = ")); " + NL + "      \t}" + NL + "        out_";
  protected final String TEXT_69 = " = new org.apache.tools.zip.ZipOutputStream(new java.io.BufferedOutputStream(output_stream_";
  protected final String TEXT_70 = ")); " + NL + "        out_";
  protected final String TEXT_71 = ".setLevel(level_";
  protected final String TEXT_72 = ");" + NL + "        out_";
  protected final String TEXT_73 = ".setEncoding(";
  protected final String TEXT_74 = ");" + NL + "        // here get the file list" + NL + "        for (int i_";
  protected final String TEXT_75 = " = 0; i_";
  protected final String TEXT_76 = " < list_";
  protected final String TEXT_77 = ".size(); i_";
  protected final String TEXT_78 = "++) {" + NL + "            java.io.BufferedInputStream in_";
  protected final String TEXT_79 = " = new java.io.BufferedInputStream(new java.io.FileInputStream(list_";
  protected final String TEXT_80 = ".get(i_";
  protected final String TEXT_81 = ")));" + NL + "            org.apache.tools.zip.ZipEntry entry = new org.apache.tools.zip.ZipEntry(list_";
  protected final String TEXT_82 = ".get(i_";
  protected final String TEXT_83 = ").getPath().substring(beginIndex_";
  protected final String TEXT_84 = "));    " + NL + "            entry.setTime(list_";
  protected final String TEXT_85 = ".get(i_";
  protected final String TEXT_86 = ").lastModified()); " + NL + "            out_";
  protected final String TEXT_87 = ".putNextEntry(entry);   " + NL + "           " + NL + "            int readLen_";
  protected final String TEXT_88 = ";" + NL + "            byte[] buf_";
  protected final String TEXT_89 = "=new byte[1024];     " + NL + "            while ((readLen_";
  protected final String TEXT_90 = " = in_";
  protected final String TEXT_91 = ".read(buf_";
  protected final String TEXT_92 = ",0,1024)) != -1)  {    " + NL + "                  out_";
  protected final String TEXT_93 = ".write(buf_";
  protected final String TEXT_94 = ", 0, readLen_";
  protected final String TEXT_95 = ");    " + NL + "            }    " + NL + "            out_";
  protected final String TEXT_96 = ".flush();    " + NL + "            in_";
  protected final String TEXT_97 = ".close();       " + NL + "        }        " + NL + "      }" + NL + "      " + NL + "      if (out_";
  protected final String TEXT_98 = " != null)  out_";
  protected final String TEXT_99 = ".close();" + NL + "      globalMap.put(\"";
  protected final String TEXT_100 = "_ARCHIVE_FILEPATH\",zipFile_";
  protected final String TEXT_101 = ");" + NL + "      globalMap.put(\"";
  protected final String TEXT_102 = "_ARCHIVE_FILENAME\",(";
  protected final String TEXT_103 = ").contains(\"\\\\\")?" + NL + "    (";
  protected final String TEXT_104 = ").substring(((";
  protected final String TEXT_105 = ").lastIndexOf(\"\\\\\")+2)): (";
  protected final String TEXT_106 = ").substring(((";
  protected final String TEXT_107 = ").lastIndexOf(\"/\"))+1));";
  protected final String TEXT_108 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();	

	String directory = ElementParameterParser.getValue(node, "__SOURCE__");
	String zipFile = ElementParameterParser.getValue(node, "__TARGET__");
	String level = ElementParameterParser.getValue(node, "__LEVEL__");
	boolean mkDir = ("true").equals(ElementParameterParser.getValue(node, "__MKDIR__"));
	boolean isAllFile = ("true").equals(ElementParameterParser.getValue(node, "__ALL_FILES__"));
	boolean subDir = ("true").equals(ElementParameterParser.getValue(node, "__SUB_DIRECTROY__"));
	boolean overwrite = ("true").equals(ElementParameterParser.getValue(node, "__OVERWRITE__"));
	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
	boolean isEncrypted = ("true").equals(ElementParameterParser.getValue(node, "__ENCRYPT_FILES__"));
	String password = ElementParameterParser.getValue(node,"__PASSWORD__");
	
	List<Map<String, String>> masks = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MASK__");	
	
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(directory );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_7);
    
    if(!overwrite){
    
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
    }
    
    if(mkDir){
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    }
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(level );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
     
	if (isAllFile == false) { 
	    
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(masks.size());
    stringBuffer.append(TEXT_26);
    
	    for(int i=0 ; i < masks.size() ; i++) {
	        Map<String, String> line = masks.get(i);
	        
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(line.get("FILEMASK"));
    stringBuffer.append(TEXT_30);
    
	    }
	    
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
     
                if (subDir==true) { 
                    
    stringBuffer.append(TEXT_50);
    
                }
                
    stringBuffer.append(TEXT_51);
     
   } else {
       
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
     
                   if (subDir==true) { 
                       
    stringBuffer.append(TEXT_55);
    
                   }
                   
    stringBuffer.append(TEXT_56);
    
       }
   
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(isEncrypted);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(encoding);
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
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(zipFile );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(TEXT_108);
    return stringBuffer.toString();
  }
}
