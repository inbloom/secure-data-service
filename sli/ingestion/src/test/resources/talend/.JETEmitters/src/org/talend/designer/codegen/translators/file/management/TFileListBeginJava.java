package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TFileListBeginJava
{
  protected static String nl;
  public static synchronized TFileListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileListBeginJava result = new TFileListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  " + NL + "        Comparator<java.io.File> fileNameASC_";
  protected final String TEXT_2 = " = new Comparator<java.io.File>() {" + NL + "        " + NL + "          public int compare(java.io.File o1, java.io.File o2) {" + NL + "            boolean bO1IsFile = o1.isFile();" + NL + "            boolean bO2IsFile = o2.isFile();" + NL + "            " + NL + "            if (bO1IsFile && bO2IsFile) {" + NL + "                return (o1.getName()).compareTo(o2.getName());" + NL + "            } else if (bO1IsFile && (!bO2IsFile)) {" + NL + "                return 1;" + NL + "            } else if ((!bO1IsFile) && bO2IsFile) {" + NL + "                return -1;" + NL + "            } else if (!bO1IsFile && !bO2IsFile) {" + NL + "                return (o1.getName()).compareTo(o2.getName());" + NL + "            } else {" + NL + "                return 0;" + NL + "            }" + NL + "          }" + NL + "        };";
  protected final String TEXT_3 = NL + "        Comparator<java.io.File> fileNameDESC_";
  protected final String TEXT_4 = " = new Comparator<java.io.File>() {" + NL + "" + NL + "          public int compare(java.io.File o1, java.io.File o2) {" + NL + "            boolean bO1IsFile = o1.isFile();" + NL + "            boolean bO2IsFile = o2.isFile();" + NL + "            " + NL + "            if (bO1IsFile && bO2IsFile) {" + NL + "                return (o2.getName()).compareTo(o1.getName());" + NL + "            } else if (bO1IsFile && !bO2IsFile) {" + NL + "                return -1;" + NL + "            } else if (!bO1IsFile && bO2IsFile) {" + NL + "                return 1;" + NL + "            } else if (!bO1IsFile && !bO2IsFile) {" + NL + "                return (o2.getName()).compareTo(o1.getName());" + NL + "            } else {" + NL + "                return 0;" + NL + "            }" + NL + "          }" + NL + "        };";
  protected final String TEXT_5 = NL + "        Comparator<java.io.File> lastModifiedASC_";
  protected final String TEXT_6 = " = new Comparator<java.io.File>() {" + NL + "        " + NL + "          public int compare(java.io.File o1, java.io.File o2) {" + NL + "            boolean bO1IsFile = o1.isFile();" + NL + "            boolean bO2IsFile = o2.isFile();" + NL + "            " + NL + "            if ((bO1IsFile && bO2IsFile) || (!bO1IsFile && !bO2IsFile)) {" + NL + "                if (o1.lastModified() == o2.lastModified()) {" + NL + "                    return (o1.getName()).compareTo(o2.getName());" + NL + "                } else if (o1.lastModified() > o2.lastModified()) {" + NL + "                    return 1;" + NL + "                } else {" + NL + "                    return -1;" + NL + "                }" + NL + "            } else if (bO1IsFile && (!bO2IsFile)) {" + NL + "                return 1;" + NL + "            } else if ((!bO1IsFile) && bO2IsFile) {" + NL + "                return -1;" + NL + "            } else {" + NL + "                return 0;" + NL + "            }" + NL + "          }" + NL + "        };";
  protected final String TEXT_7 = NL + "        Comparator<java.io.File> lastModifiedDESC_";
  protected final String TEXT_8 = " = new Comparator<java.io.File>() {" + NL + "        " + NL + "          public int compare(java.io.File o1, java.io.File o2) {" + NL + "            boolean bO1IsFile = o1.isFile();" + NL + "            boolean bO2IsFile = o2.isFile();" + NL + "            " + NL + "            if ((bO1IsFile && bO2IsFile) || (!bO1IsFile && !bO2IsFile)) {" + NL + "                if (o1.lastModified() == o2.lastModified()) {" + NL + "                    return (o2.getName()).compareTo(o1.getName());" + NL + "                } else if (o1.lastModified() < o2.lastModified()) {" + NL + "                    return 1;" + NL + "                } else {" + NL + "                    return -1;" + NL + "                }" + NL + "            } else if (bO1IsFile && (!bO2IsFile)) {" + NL + "                return -1;" + NL + "            } else if ((!bO1IsFile) && bO2IsFile) {" + NL + "                return 1;            " + NL + "            } else {" + NL + "                return 0;" + NL + "            }" + NL + "          }" + NL + "        };";
  protected final String TEXT_9 = NL + "        Comparator<java.io.File> filseSizeASC_";
  protected final String TEXT_10 = " = new Comparator<java.io.File>() {" + NL + "" + NL + "          public int compare(java.io.File o1, java.io.File o2) {" + NL + "            boolean bO1IsFile = o1.isFile();" + NL + "            boolean bO2IsFile = o2.isFile();" + NL + "                      " + NL + "            if (bO1IsFile && bO2IsFile) {" + NL + "                long size_1 = o1.length();" + NL + "                long size_2 = o2.length();                " + NL + "       " + NL + "                if (size_1 == size_2) {" + NL + "                    return (o1.getName()).compareTo(o2.getName());" + NL + "                } else if (size_1 > size_2) {" + NL + "                    return 1;" + NL + "                } else {" + NL + "                    return -1;" + NL + "                }                " + NL + "            } else if (bO1IsFile && (!bO2IsFile)) {" + NL + "                return 1;" + NL + "            } else if ((!bO1IsFile) && bO2IsFile) {" + NL + "                return -1;" + NL + "            } else if ((!bO1IsFile) && (!bO2IsFile)){" + NL + "                return (o1.getName()).compareTo(o2.getName());" + NL + "            } else{" + NL + "                return 0;" + NL + "            }" + NL + "          }" + NL + "        };";
  protected final String TEXT_11 = NL + "        Comparator<java.io.File> filseSizeDESC_";
  protected final String TEXT_12 = " = new Comparator<java.io.File>() {" + NL + "        " + NL + "          public int compare(java.io.File o1, java.io.File o2) {" + NL + "            boolean bO1IsFile = o1.isFile();" + NL + "            boolean bO2IsFile = o2.isFile();" + NL + "            " + NL + "            if (bO1IsFile && bO2IsFile) {" + NL + "                " + NL + "                long size_1 = o1.length();" + NL + "                long size_2 = o2.length();" + NL + "              " + NL + "                if (size_1 == size_2) {" + NL + "                    return (o2.getName()).compareTo(o1.getName());" + NL + "                } else if (size_1 < size_2) {" + NL + "                    return 1;" + NL + "                } else {" + NL + "                    return -1;" + NL + "                }" + NL + "                " + NL + "            } else if (bO1IsFile && (!bO2IsFile)) {" + NL + "                return -1;" + NL + "            } else if ((!bO1IsFile) && bO2IsFile) {" + NL + "                return 1;" + NL + "            } else if ((!bO1IsFile) && (!bO2IsFile)){" + NL + "                return (o2.getName()).compareTo(o1.getName());" + NL + "            } else {" + NL + "                return 0;" + NL + "            }" + NL + "          }" + NL + "        };";
  protected final String TEXT_13 = "   " + NL + "    " + NL + "  String directory_";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ";" + NL + "  java.util.List<String> maskList_";
  protected final String TEXT_16 = " = new java.util.ArrayList<String>();";
  protected final String TEXT_17 = NL + "      maskList_";
  protected final String TEXT_18 = ".add(\"*\");";
  protected final String TEXT_19 = NL + "      maskList_";
  protected final String TEXT_20 = ".add(\".*\");";
  protected final String TEXT_21 = " " + NL + "    maskList_";
  protected final String TEXT_22 = ".add(";
  protected final String TEXT_23 = ");";
  protected final String TEXT_24 = "  " + NL + "  int NB_FILE";
  protected final String TEXT_25 = " = 0;" + NL + "" + NL + "  for (String filemask_";
  protected final String TEXT_26 = " : maskList_";
  protected final String TEXT_27 = ") {" + NL + "  \tboolean case_sensitive_";
  protected final String TEXT_28 = " = ";
  protected final String TEXT_29 = ";" + NL + "  ";
  protected final String TEXT_30 = NL + "      filemask_";
  protected final String TEXT_31 = " = org.apache.oro.text.GlobCompiler.globToPerl5(filemask_";
  protected final String TEXT_32 = ".toCharArray(), org.apache.oro.text.GlobCompiler.DEFAULT_MASK);";
  protected final String TEXT_33 = "   " + NL + "      String excludefilemask_";
  protected final String TEXT_34 = " = ";
  protected final String TEXT_35 = ";" + NL + "\t  List<java.util.regex.Pattern> excludefileNameEachPattern_";
  protected final String TEXT_36 = " = new java.util.ArrayList<java.util.regex.Pattern>();" + NL + "\t  if(excludefilemask_";
  protected final String TEXT_37 = "!=null && !\"\".equals(excludefilemask_";
  protected final String TEXT_38 = ")) {" + NL + "\t  \tfor(String excludefilemaskEach_";
  protected final String TEXT_39 = " : excludefilemask_";
  protected final String TEXT_40 = ".split(\",\")) {" + NL + "\t  \t ";
  protected final String TEXT_41 = NL + "\t  \t\texcludefilemaskEach_";
  protected final String TEXT_42 = " = org.apache.oro.text.GlobCompiler.globToPerl5(excludefilemaskEach_";
  protected final String TEXT_43 = ".toCharArray(), org.apache.oro.text.GlobCompiler.DEFAULT_MASK);" + NL + "\t  \t ";
  protected final String TEXT_44 = NL + "\t  \t \tif (!case_sensitive_";
  protected final String TEXT_45 = "){" + NL + "\t\t\t\texcludefileNameEachPattern_";
  protected final String TEXT_46 = ".add(java.util.regex.Pattern.compile(excludefilemaskEach_";
  protected final String TEXT_47 = ",java.util.regex.Pattern.CASE_INSENSITIVE));" + NL + "\t\t\t} else {" + NL + "\t\t\t\texcludefileNameEachPattern_";
  protected final String TEXT_48 = ".add(java.util.regex.Pattern.compile(excludefilemaskEach_";
  protected final String TEXT_49 = "));" + NL + "\t\t\t}\t  \t \t\t" + NL + "\t  \t}" + NL + "\t  }";
  protected final String TEXT_50 = NL + "    java.util.regex.Pattern fileNamePattern_";
  protected final String TEXT_51 = " = java.util.regex.Pattern.compile(filemask_";
  protected final String TEXT_52 = ");" + NL + "    " + NL + "    if (!case_sensitive_";
  protected final String TEXT_53 = "){" + NL + "      fileNamePattern_";
  protected final String TEXT_54 = " = java.util.regex.Pattern.compile(filemask_";
  protected final String TEXT_55 = ", java.util.regex.Pattern.CASE_INSENSITIVE);" + NL + "    } " + NL + "    java.io.File file_";
  protected final String TEXT_56 = " = new java.io.File(directory_";
  protected final String TEXT_57 = ");" + NL + "    final java.util.List<java.io.File> list_";
  protected final String TEXT_58 = " = new java.util.ArrayList<java.io.File>();" + NL + "    ";
  protected final String TEXT_59 = NL + "      file_";
  protected final String TEXT_60 = ".listFiles(new java.io.FilenameFilter() {" + NL + "          public boolean accept(java.io.File dir, String name) {" + NL + "              java.io.File file = new java.io.File(dir, name);";
  protected final String TEXT_61 = NL + "                if (!file.isDirectory()) {" + NL + "                  list_";
  protected final String TEXT_62 = ".add(file);" + NL + "                  return true;" + NL + "                } else {" + NL + "                  file.listFiles(this);" + NL + "                }";
  protected final String TEXT_63 = NL + "                if (!file.isDirectory()) {" + NL + "                  return true;" + NL + "                } else {" + NL + "                  list_";
  protected final String TEXT_64 = ".add(file);" + NL + "                  file.listFiles(this);" + NL + "                }";
  protected final String TEXT_65 = NL + "                if (!file.isDirectory()) {" + NL + "                  list_";
  protected final String TEXT_66 = ".add(file);" + NL + "                } else {" + NL + "                  list_";
  protected final String TEXT_67 = ".add(file);" + NL + "                  file.listFiles(this);" + NL + "                }";
  protected final String TEXT_68 = NL + "              return false;" + NL + "            }" + NL + "          }" + NL + "      );";
  protected final String TEXT_69 = " " + NL + "      file_";
  protected final String TEXT_70 = ".listFiles(new java.io.FilenameFilter() {" + NL + "          public boolean accept(java.io.File dir, String name) {" + NL + "              java.io.File file = new java.io.File(dir, name);";
  protected final String TEXT_71 = NL + "                if (!file.isDirectory()) {" + NL + "                  list_";
  protected final String TEXT_72 = ".add(file);" + NL + "                }";
  protected final String TEXT_73 = NL + "                if (file.isDirectory()) {" + NL + "                  list_";
  protected final String TEXT_74 = ".add(file);" + NL + "                }";
  protected final String TEXT_75 = NL + "                list_";
  protected final String TEXT_76 = ".add(file);";
  protected final String TEXT_77 = NL + "              return true;" + NL + "            }" + NL + "          }" + NL + "      );";
  protected final String TEXT_78 = " " + NL + "    //int NB_FILE";
  protected final String TEXT_79 = " = 0;" + NL + "    java.io.File [] tempArray_";
  protected final String TEXT_80 = " = list_";
  protected final String TEXT_81 = ".toArray(new java.io.File[list_";
  protected final String TEXT_82 = ".size()]);";
  protected final String TEXT_83 = NL + "      java.util.Arrays.sort(tempArray_";
  protected final String TEXT_84 = ");";
  protected final String TEXT_85 = NL + "      java.util.Arrays.sort(tempArray_";
  protected final String TEXT_86 = ", fileName";
  protected final String TEXT_87 = "_";
  protected final String TEXT_88 = ");";
  protected final String TEXT_89 = NL + "      java.util.Arrays.sort(tempArray_";
  protected final String TEXT_90 = ", filseSize";
  protected final String TEXT_91 = "_";
  protected final String TEXT_92 = ");";
  protected final String TEXT_93 = NL + "      java.util.Arrays.sort(tempArray_";
  protected final String TEXT_94 = ", lastModified";
  protected final String TEXT_95 = "_";
  protected final String TEXT_96 = ");";
  protected final String TEXT_97 = NL + "        " + NL + "    java.util.List<java.io.File> fileList_";
  protected final String TEXT_98 = " = new java.util.ArrayList<java.io.File>();" + NL + "    fileList_";
  protected final String TEXT_99 = " = java.util.Arrays.asList(tempArray_";
  protected final String TEXT_100 = ");" + NL + "    " + NL + "    for (int i_";
  protected final String TEXT_101 = " = 0; i_";
  protected final String TEXT_102 = " < fileList_";
  protected final String TEXT_103 = ".size(); i_";
  protected final String TEXT_104 = "++){" + NL + "      java.io.File files_";
  protected final String TEXT_105 = " = fileList_";
  protected final String TEXT_106 = ".get(i_";
  protected final String TEXT_107 = ");" + NL + "      String fileName_";
  protected final String TEXT_108 = " = files_";
  protected final String TEXT_109 = ".getName();" + NL + "      ";
  protected final String TEXT_110 = " " + NL + "        if (!fileNamePattern_";
  protected final String TEXT_111 = ".matcher(fileName_";
  protected final String TEXT_112 = ").matches()){" + NL + "          continue;" + NL + "        }" + NL + "        boolean isExclude_";
  protected final String TEXT_113 = " = false;" + NL + "        for(java.util.regex.Pattern pattern : excludefileNameEachPattern_";
  protected final String TEXT_114 = ") {" + NL + "        \tif(pattern.matcher(fileName_";
  protected final String TEXT_115 = ").matches()) {" + NL + "        \t\tisExclude_";
  protected final String TEXT_116 = " = true;" + NL + "        \t\tbreak;" + NL + "        \t}" + NL + "        }" + NL + "        if(isExclude_";
  protected final String TEXT_117 = ") {" + NL + "          continue;" + NL + "        }";
  protected final String TEXT_118 = NL + "        if (!fileNamePattern_";
  protected final String TEXT_119 = ".matcher(fileName_";
  protected final String TEXT_120 = ").matches()){" + NL + "          continue;" + NL + "        }";
  protected final String TEXT_121 = " " + NL + "      String currentFileName_";
  protected final String TEXT_122 = " = files_";
  protected final String TEXT_123 = ".getName(); " + NL + "      String currentFilePath_";
  protected final String TEXT_124 = " = files_";
  protected final String TEXT_125 = ".getAbsolutePath();" + NL + "      String currentFileDirectory_";
  protected final String TEXT_126 = " = files_";
  protected final String TEXT_127 = ".getParent();" + NL + "      String currentFileExtension_";
  protected final String TEXT_128 = " = null;" + NL + "      " + NL + "      if (files_";
  protected final String TEXT_129 = ".getName().contains(\".\") && files_";
  protected final String TEXT_130 = ".isFile()){" + NL + "        currentFileExtension_";
  protected final String TEXT_131 = " = files_";
  protected final String TEXT_132 = ".getName().substring(files_";
  protected final String TEXT_133 = ".getName().lastIndexOf(\".\") + 1);" + NL + "      } else{" + NL + "        currentFileExtension_";
  protected final String TEXT_134 = " = \"\";" + NL + "      }";
  protected final String TEXT_135 = NL + "        currentFilePath_";
  protected final String TEXT_136 = " = currentFilePath_";
  protected final String TEXT_137 = ".replaceAll(\"\\\\\\\\\", \"/\");" + NL + "        currentFileDirectory_";
  protected final String TEXT_138 = " = currentFileDirectory_";
  protected final String TEXT_139 = ".replaceAll(\"\\\\\\\\\", \"/\");";
  protected final String TEXT_140 = NL + "      " + NL + "      NB_FILE";
  protected final String TEXT_141 = " ++;" + NL + "      globalMap.put(\"";
  protected final String TEXT_142 = "_CURRENT_FILE\", currentFileName_";
  protected final String TEXT_143 = ");" + NL + "      globalMap.put(\"";
  protected final String TEXT_144 = "_CURRENT_FILEPATH\", currentFilePath_";
  protected final String TEXT_145 = ");" + NL + "      globalMap.put(\"";
  protected final String TEXT_146 = "_CURRENT_FILEDIRECTORY\", currentFileDirectory_";
  protected final String TEXT_147 = ");" + NL + "      globalMap.put(\"";
  protected final String TEXT_148 = "_CURRENT_FILEEXTENSION\", currentFileExtension_";
  protected final String TEXT_149 = ");" + NL + "      globalMap.put(\"";
  protected final String TEXT_150 = "_NB_FILE\", NB_FILE";
  protected final String TEXT_151 = "); ";
  protected final String TEXT_152 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
  CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
  INode node = (INode)codeGenArgument.getArgument();
  String cid = node.getUniqueName();
  boolean incldSubdir = ("true").equals(ElementParameterParser.getValue(node, "__INCLUDSUBDIR__"));
  boolean ifexclude = ("true").equals(ElementParameterParser.getValue(node, "__IFEXCLUDE__"));
  String filelistType = ElementParameterParser.getValue(node, "__LIST_MODE__");
  boolean useGlob = ("true").equals(ElementParameterParser.getValue(node, "__GLOBEXPRESSIONS__"));
  List<Map<String, String>> files = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILES__");
  boolean toSlash = ("true").equals(ElementParameterParser.getValue(node, "__FORMAT_FILEPATH_TO_SLASH__"));
  
  class codeGenerater{
    public void genFileNameOder(String _cid, String _suffix){
      if ("ASC".equals(_suffix)){
      
    stringBuffer.append(TEXT_1);
    stringBuffer.append(_cid);
    stringBuffer.append(TEXT_2);
    
      } else if ("DESC".equals(_suffix)){
      
    stringBuffer.append(TEXT_3);
    stringBuffer.append(_cid);
    stringBuffer.append(TEXT_4);
    
      }
    }
    
    public void genLastModifiedOder(String _cid, String _suffix){
      if ("ASC".equals(_suffix)){
      
    stringBuffer.append(TEXT_5);
    stringBuffer.append(_cid);
    stringBuffer.append(TEXT_6);
    
      } else if ("DESC".equals(_suffix)){
      
    stringBuffer.append(TEXT_7);
    stringBuffer.append(_cid);
    stringBuffer.append(TEXT_8);
    
      }
    }
    
    public void genFileSizeOder(String _cid, String _suffix){
      if ("ASC".equals(_suffix)){
      
    stringBuffer.append(TEXT_9);
    stringBuffer.append(_cid);
    stringBuffer.append(TEXT_10);
    
      } else if ("DESC".equals(_suffix)){
      
    stringBuffer.append(TEXT_11);
    stringBuffer.append(_cid);
    stringBuffer.append(TEXT_12);
    
      }
    }
  }  
  
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(ElementParameterParser.getValue(node, "__DIRECTORY__") );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
  if (files.size() == 0){
    if (useGlob){
    
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    
    } else{
    
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    
    }
  }
  
  for (int i = 0; i < files.size(); i++) {
    Map<String, String> line = files.get(i);
    
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append( line.get("FILEMASK") );
    stringBuffer.append(TEXT_23);
    }
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(("YES").equals(ElementParameterParser.getValue(node, "__CASE_SENSITIVE__")) );
    stringBuffer.append(TEXT_29);
    if (useGlob){
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
    }
    if (ifexclude){
    
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(ElementParameterParser.getValue(node, "__EXCLUDEFILEMASK__"));
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
    if (useGlob){
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    }
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
    }
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    
    if (incldSubdir) {
    
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    
              if (("FILES").equals(filelistType)) {
              
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    
              } else if(("DIRECTORIES").equals(filelistType)) {
              
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    
              } else if(("BOTH").equals(filelistType)) {
              
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    
              }
              
    stringBuffer.append(TEXT_68);
    
    } else {
    
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    
              if (("FILES").equals(filelistType)) {
              
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    
              } else if (("DIRECTORIES").equals(filelistType)) {
              
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    
              } else if (("BOTH").equals(filelistType)) {
              
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    
              }
              
    stringBuffer.append(TEXT_77);
    
    }
    
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    
    boolean bOrdByDefault = "true".equals(ElementParameterParser.getValue(node, "__ORDER_BY_NOTHING__"));
    boolean bOrdByFileName = "true".equals(ElementParameterParser.getValue(node, "__ORDER_BY_FILENAME__"));
    boolean bOrdByFileSize = "true".equals(ElementParameterParser.getValue(node, "__ORDER_BY_FILESIZE__"));
    boolean bOrdByModifiedTime = "true".equals(ElementParameterParser.getValue(node, "__ORDER_BY_MODIFIEDDATE__"));
    
    boolean bOrdASC = "true".equals(ElementParameterParser.getValue(node, "__ORDER_ACTION_ASC__"));
    //boolean bOrdDESC = "true".equals(ElementParameterParser.getValue(node, "__ORDER_ACTION_DESC__"));
    
    String suffix = bOrdASC ? "ASC" : "DESC";
    codeGenerater cg = new codeGenerater();
    
    if (bOrdByDefault){
    
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    
    } else if (bOrdByFileName){
      cg.genFileNameOder(cid, suffix);
      
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(suffix);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    
    } else if (bOrdByFileSize){
      cg.genFileSizeOder(cid, suffix);
      
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(suffix);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    
    } else if (bOrdByModifiedTime){
      cg.genLastModifiedOder(cid, suffix);
      
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(suffix);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    
    }
    
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
    if (ifexclude){
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    } else {
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_120);
    }
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
    if (toSlash){
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(TEXT_152);
    return stringBuffer.toString();
  }
}
