package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TWaitForFileBeginJava
{
  protected static String nl;
  public static synchronized TWaitForFileBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWaitForFileBeginJava result = new TWaitForFileBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "       " + NL + "        class Util_";
  protected final String TEXT_2 = " {" + NL + "" + NL + "            private boolean flagt = false;" + NL + "" + NL + "            private java.util.regex.Pattern fileNamePatternt = null;" + NL + "" + NL + "            java.util.List<java.io.File> getFiles() {" + NL + "                String directoryt = ";
  protected final String TEXT_3 = ";" + NL + "                String filemaskt = ";
  protected final String TEXT_4 = "+\"$\";" + NL + "" + NL + "                if (filemaskt.indexOf(\"^\") == -1) {" + NL + "                    filemaskt = \"^\" + filemaskt;" + NL + "                }" + NL + "                if (!(filemaskt.lastIndexOf(\".*$\") == -1) && filemaskt.lastIndexOf(\"*.*$\") == -1) {" + NL + "                    filemaskt = filemaskt.substring(0, filemaskt.length() - 3) + \"$\";" + NL + "                    flagt = true;" + NL + "                } else {" + NL + "                    filemaskt = java.util.regex.Pattern.compile(\"[*]\").matcher(filemaskt).replaceAll(\".*\");" + NL + "                }" + NL + "" + NL + "                boolean case_sensitivet = ";
  protected final String TEXT_5 = ";" + NL + "                fileNamePatternt = java.util.regex.Pattern.compile(filemaskt);" + NL + "                if (!case_sensitivet) {" + NL + "                    fileNamePatternt = java.util.regex.Pattern.compile(filemaskt, java.util.regex.Pattern.CASE_INSENSITIVE);" + NL + "                }" + NL + "                java.io.File filet = new java.io.File(directoryt);" + NL + "                final java.util.List<java.io.File> list = new java.util.ArrayList<java.io.File>();" + NL + "                filet.listFiles(new java.io.FilenameFilter() {" + NL + "" + NL + "                    public boolean accept(java.io.File dir, String name) {" + NL + "                        java.io.File file = new java.io.File(dir, name);" + NL + "                        if (file.isFile()) {" + NL + "                            String fileNamet = name;" + NL + "                            if (flagt == true) {" + NL + "                                if (!(fileNamet.indexOf(\".\") == -1)) {" + NL + "                                    if (fileNamePatternt.matcher(fileNamet.substring(0, fileNamet.indexOf(\".\"))).find()) {" + NL + "                                        list.add(file);" + NL + "" + NL + "                                    }" + NL + "                                } else {" + NL + "                                    if (fileNamePatternt.matcher(fileNamet).find()) {" + NL + "                                        list.add(file);" + NL + "                                    }" + NL + "                                }" + NL + "                            } else {" + NL + "                                if (fileNamePatternt.matcher(fileNamet).find()) {" + NL + "                                    list.add(file);" + NL + "                                }" + NL + "                            }" + NL + "" + NL + "                            return true;" + NL + "                        } ";
  protected final String TEXT_6 = "                          " + NL + "                            else {" + NL + "" + NL + "                                file.listFiles(this);" + NL + "                            }" + NL + "                      ";
  protected final String TEXT_7 = "                        " + NL + "                        return false;" + NL + "                    }" + NL + "" + NL + "                });" + NL + "" + NL + "                return list;" + NL + "            }";
  protected final String TEXT_8 = NL + "\t\t\tjava.util.List<java.io.File> getCreatedFiles(java.util.List<java.io.File> originalFiles, java.util.List<java.io.File> fetchOneTimeFiles) {" + NL + "\t\t\t\tjava.util.List<java.io.File> newCreatedFiles = new java.util.ArrayList<java.io.File>();" + NL + "\t\t\t\tfor(java.io.File file: fetchOneTimeFiles){" + NL + "\t\t\t\t\tif(!originalFiles.contains(file)){" + NL + "\t\t\t\t\t\tnewCreatedFiles.add(file);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t\treturn newCreatedFiles;" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_9 = NL + "                public String getFileChecksum(java.io.File file) {" + NL + "                \tString strMD5 = \"\";" + NL + "                \tjava.security.MessageDigest complete = null;" + NL + "                \tjava.io.InputStream fis = null;" + NL + "                \ttry{" + NL + "                        fis =  new java.io.FileInputStream(file);" + NL + "                        byte[] buffer = new byte[1024];" + NL + "                        complete = java.security.MessageDigest.getInstance(\"MD5\");" + NL + "                        int numRead;" + NL + "                        while((numRead = fis.read(buffer))!= -1){" + NL + "                          \tif (numRead > 0) {" + NL + "                            \tcomplete.update(buffer, 0, numRead);" + NL + "                            }" + NL + "                        }" + NL + "                    } catch(java.lang.Exception ex) {" + NL + "    " + NL + "                    } finally {" + NL + "                    \tif(fis != null) {" + NL + "                    \t\ttry {" + NL + "                    \t\t\tfis.close();" + NL + "                    \t\t} catch(Exception ex) {" + NL + "                    \t\t}" + NL + "                    \t}" + NL + "                    }" + NL + "                   \t" + NL + "                   \tif(complete != null){" + NL + "            \t\t\tbyte[] b = complete.digest();" + NL + "             \t\t\tfor (int i = 0; i < b.length; i++) {" + NL + "              \t\t\t\tstrMD5 += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );" + NL + "              \t\t\t}" + NL + "          \t\t\t}" + NL + "            \t\treturn strMD5;" + NL + "                }";
  protected final String TEXT_10 = NL + "        }" + NL + "            " + NL + "        Util_";
  protected final String TEXT_11 = " util_";
  protected final String TEXT_12 = " = new Util_";
  protected final String TEXT_13 = "();" + NL + "        java.util.List<java.io.File> originalFiles_";
  protected final String TEXT_14 = " = util_";
  protected final String TEXT_15 = ".getFiles();            ";
  protected final String TEXT_16 = NL + "            java.util.Map<String, java.io.File> originalMap_";
  protected final String TEXT_17 = " = new java.util.HashMap<String, java.io.File>();" + NL + "            for(java.io.File file_";
  protected final String TEXT_18 = ":originalFiles_";
  protected final String TEXT_19 = "){" + NL + "            \toriginalMap_";
  protected final String TEXT_20 = ".put(util_";
  protected final String TEXT_21 = ".getFileChecksum(file_";
  protected final String TEXT_22 = "), file_";
  protected final String TEXT_23 = ");" + NL + "            }";
  protected final String TEXT_24 = NL + "      " + NL + "        int count_";
  protected final String TEXT_25 = " = 0;" + NL + "        while (true) {";
  protected final String TEXT_26 = "                " + NL + "                if (count_";
  protected final String TEXT_27 = " == ";
  protected final String TEXT_28 = " ) {" + NL + "                    break;" + NL + "                }";
  protected final String TEXT_29 = "                " + NL + "            boolean found_";
  protected final String TEXT_30 = " = false;";
  protected final String TEXT_31 = "              " + NL + "    \t\t\tif( count_";
  protected final String TEXT_32 = " < originalFiles_";
  protected final String TEXT_33 = ".size()) {" + NL + "                    java.io.File file_";
  protected final String TEXT_34 = " = originalFiles_";
  protected final String TEXT_35 = ".get(count_";
  protected final String TEXT_36 = ");" + NL + "                    globalMap.put(\"";
  protected final String TEXT_37 = "_FILENAME\", file_";
  protected final String TEXT_38 = ".getName());" + NL + "                    globalMap.put(\"";
  protected final String TEXT_39 = "_PRESENT_FILE\", file_";
  protected final String TEXT_40 = ".getAbsolutePath());" + NL + "                    found_";
  protected final String TEXT_41 = " = true;" + NL + "    \t\t\t} else {" + NL + "\t\t\t    ";
  protected final String TEXT_42 = NL + "            java.util.List<java.io.File> fetchFilesOneTime_";
  protected final String TEXT_43 = " = util_";
  protected final String TEXT_44 = ".getFiles();";
  protected final String TEXT_45 = NL + "                java.util.List<java.io.File> newCreatedFiles_";
  protected final String TEXT_46 = " = util_";
  protected final String TEXT_47 = ".getCreatedFiles(originalFiles_";
  protected final String TEXT_48 = ",fetchFilesOneTime_";
  protected final String TEXT_49 = ");" + NL + "" + NL + "               \tfor ( int i=0; i< newCreatedFiles_";
  protected final String TEXT_50 = ".size(); i++) {" + NL + "\t\t\t\t\tjava.io.File file_";
  protected final String TEXT_51 = " = newCreatedFiles_";
  protected final String TEXT_52 = ".get(i);" + NL + "           \t\t\t";
  protected final String TEXT_53 = NL + "           \t\t\tString osName_";
  protected final String TEXT_54 = " = System.getProperty(\"os.name\");" + NL + "\t  \t\t\t\tif (osName_";
  protected final String TEXT_55 = " != null && osName_";
  protected final String TEXT_56 = ".toLowerCase().startsWith(\"win\")){" + NL + "\t  \t\t\t\t\tjava.io.File tempfile=new java.io.File(file_";
  protected final String TEXT_57 = ".getAbsolutePath()+\".talendTempfile\"); " + NL + "\t  \t\t\t\t\tThread.sleep(";
  protected final String TEXT_58 = ");" + NL + "\t  \t\t\t\t\tif(file_";
  protected final String TEXT_59 = ".renameTo(tempfile)) {" + NL + "\t  \t\t\t\t\t\ttempfile.renameTo(file_";
  protected final String TEXT_60 = "); " + NL + "\t  \t\t\t\t\t} else {" + NL + "\t  \t\t\t\t\t\tcontinue;" + NL + "\t  \t\t\t\t\t}" + NL + "\t  \t\t\t\t} else { " + NL + "\t\t\t\t\t\tlong timestamp_";
  protected final String TEXT_61 = " = file_";
  protected final String TEXT_62 = ".lastModified();" + NL + "\t" + NL + "\t\t\t\t\t\tThread.sleep(";
  protected final String TEXT_63 = ");" + NL + "\t" + NL + "\t                \tif(file_";
  protected final String TEXT_64 = ".lastModified() != timestamp_";
  protected final String TEXT_65 = "){" + NL + "\t                \t\tcontinue;" + NL + "\t                \t}" + NL + "\t                }" + NL + "            \t\t";
  protected final String TEXT_66 = NL + "                    globalMap.put(\"";
  protected final String TEXT_67 = "_FILENAME\", file_";
  protected final String TEXT_68 = ".getName());" + NL + "                    globalMap.put(\"";
  protected final String TEXT_69 = "_CREATED_FILE\", file_";
  protected final String TEXT_70 = ".getAbsolutePath());" + NL + "                    found_";
  protected final String TEXT_71 = " = true;" + NL + "                    originalFiles_";
  protected final String TEXT_72 = ".add(file_";
  protected final String TEXT_73 = ");              " + NL + "                    break;" + NL + "            \t}" + NL + "\t\t\t";
  protected final String TEXT_74 = NL + "            \tfor(java.io.File file_";
  protected final String TEXT_75 = " : originalFiles_";
  protected final String TEXT_76 = ") {" + NL + "                    if(!fetchFilesOneTime_";
  protected final String TEXT_77 = ".contains(file_";
  protected final String TEXT_78 = ")) {" + NL + "                    \tglobalMap.put(\"";
  protected final String TEXT_79 = "_FILENAME\", file_";
  protected final String TEXT_80 = ".getName());" + NL + "                        globalMap.put(\"";
  protected final String TEXT_81 = "_DELETED_FILE\", file_";
  protected final String TEXT_82 = ".getAbsolutePath());" + NL + "                        found_";
  protected final String TEXT_83 = " = true;  " + NL + "                        originalFiles_";
  protected final String TEXT_84 = ".remove(file_";
  protected final String TEXT_85 = ");                  " + NL + "                        break;" + NL + "                    }" + NL + "                }";
  protected final String TEXT_86 = NL + "                for(java.io.File file_";
  protected final String TEXT_87 = " : fetchFilesOneTime_";
  protected final String TEXT_88 = ") {";
  protected final String TEXT_89 = "    " + NL + "                    if(null == originalMap_";
  protected final String TEXT_90 = ".get(util_";
  protected final String TEXT_91 = ".getFileChecksum(file_";
  protected final String TEXT_92 = "))) {" + NL + "                    \tglobalMap.put(\"";
  protected final String TEXT_93 = "_FILENAME\", file_";
  protected final String TEXT_94 = ".getName());" + NL + "                        globalMap.put(\"";
  protected final String TEXT_95 = "_UPDATED_FILE\", file_";
  protected final String TEXT_96 = ".getAbsolutePath());" + NL + "                       \tfound_";
  protected final String TEXT_97 = " = true;" + NL + "                        originalMap_";
  protected final String TEXT_98 = ".put(util_";
  protected final String TEXT_99 = ".getFileChecksum(file_";
  protected final String TEXT_100 = "), file_";
  protected final String TEXT_101 = ");" + NL + "                        break;                        " + NL + "                    }" + NL + "            \t";
  protected final String TEXT_102 = NL + "            \t\tif(null == originalMap_";
  protected final String TEXT_103 = ".get(util_";
  protected final String TEXT_104 = ".getFileChecksum(file_";
  protected final String TEXT_105 = "))) {" + NL + "            \t\t\toriginalMap_";
  protected final String TEXT_106 = ".put(util_";
  protected final String TEXT_107 = ".getFileChecksum(file_";
  protected final String TEXT_108 = "), file_";
  protected final String TEXT_109 = ");" + NL + "            \t\t\tglobalMap.put(\"";
  protected final String TEXT_110 = "_NOT_UPDATED_FILE\", null);" + NL + "            \t\t\tfound_";
  protected final String TEXT_111 = " = false;" + NL + "                   \t}else{                   " + NL + "                    \tglobalMap.put(\"";
  protected final String TEXT_112 = "_FILENAME\", file_";
  protected final String TEXT_113 = ".getName());" + NL + "                    \tglobalMap.put(\"";
  protected final String TEXT_114 = "_NOT_UPDATED_FILE\", file_";
  protected final String TEXT_115 = ".getAbsolutePath());" + NL + "                    \tfound_";
  protected final String TEXT_116 = " = true;" + NL + "                    \tbreak;" + NL + "                    }";
  protected final String TEXT_117 = NL + "                }    " + NL + "                ";
  protected final String TEXT_118 = NL + "                for(java.io.File file_";
  protected final String TEXT_119 = " : originalFiles_";
  protected final String TEXT_120 = ") {" + NL + "                    if(!fetchFilesOneTime_";
  protected final String TEXT_121 = ".contains(file_";
  protected final String TEXT_122 = ")) {" + NL + "                    \tglobalMap.put(\"";
  protected final String TEXT_123 = "_FILENAME\", file_";
  protected final String TEXT_124 = ".getName());" + NL + "                        globalMap.put(\"";
  protected final String TEXT_125 = "_DELETED_FILE\", file_";
  protected final String TEXT_126 = ".getAbsolutePath());" + NL + "                        found_";
  protected final String TEXT_127 = " = true;" + NL + "                        if(fetchFilesOneTime_";
  protected final String TEXT_128 = ".size() != originalFiles_";
  protected final String TEXT_129 = ".size()) {" + NL + "                        originalFiles_";
  protected final String TEXT_130 = ".remove(file_";
  protected final String TEXT_131 = ");" + NL + "                        }" + NL + "                        break;" + NL + "                    }" + NL + "                }" + NL + "                java.util.List<java.io.File> newCreatedFiles_";
  protected final String TEXT_132 = " = util_";
  protected final String TEXT_133 = ".getCreatedFiles(originalFiles_";
  protected final String TEXT_134 = ",fetchFilesOneTime_";
  protected final String TEXT_135 = ");" + NL + "                for ( int i=0; i< newCreatedFiles_";
  protected final String TEXT_136 = ".size(); i++) {" + NL + "\t\t\t\t\tjava.io.File file_";
  protected final String TEXT_137 = " = newCreatedFiles_";
  protected final String TEXT_138 = ".get(i);" + NL + "           \t\t\t";
  protected final String TEXT_139 = NL + "           \t\t\tString osName_";
  protected final String TEXT_140 = " = System.getProperty(\"os.name\");" + NL + "\t  \t\t\t\tif (osName_";
  protected final String TEXT_141 = " != null && osName_";
  protected final String TEXT_142 = ".toLowerCase().startsWith(\"win\")){" + NL + "\t  \t\t\t\t\tjava.io.File tempfile=new java.io.File(file_";
  protected final String TEXT_143 = ".getAbsolutePath()+\".talendTempfile\"); " + NL + "\t  \t\t\t\t\tThread.sleep(";
  protected final String TEXT_144 = ");" + NL + "\t  \t\t\t\t\tif(file_";
  protected final String TEXT_145 = ".renameTo(tempfile)) {" + NL + "\t  \t\t\t\t\t\ttempfile.renameTo(file_";
  protected final String TEXT_146 = "); " + NL + "\t  \t\t\t\t\t} else {" + NL + "\t  \t\t\t\t\t\tcontinue;" + NL + "\t  \t\t\t\t\t}" + NL + "\t  \t\t\t\t} else { " + NL + "\t\t\t\t\t\tlong timestamp_";
  protected final String TEXT_147 = " = file_";
  protected final String TEXT_148 = ".lastModified();" + NL + "\t" + NL + "\t\t\t\t\t\tThread.sleep(";
  protected final String TEXT_149 = ");" + NL + "\t" + NL + "\t                \tif(file_";
  protected final String TEXT_150 = ".lastModified() != timestamp_";
  protected final String TEXT_151 = "){" + NL + "\t                \t\tcontinue;" + NL + "\t                \t}" + NL + "\t                }";
  protected final String TEXT_152 = NL + "                    globalMap.put(\"";
  protected final String TEXT_153 = "_FILENAME\", file_";
  protected final String TEXT_154 = ".getName());" + NL + "                    globalMap.put(\"";
  protected final String TEXT_155 = "_CREATED_FILE\", file_";
  protected final String TEXT_156 = ".getAbsolutePath());" + NL + "                    found_";
  protected final String TEXT_157 = " = true;" + NL + "                    originalFiles_";
  protected final String TEXT_158 = ".add(file_";
  protected final String TEXT_159 = ");           " + NL + "                    break;" + NL + "\t\t\t\t}" + NL + "                for(java.io.File file_";
  protected final String TEXT_160 = " : fetchFilesOneTime_";
  protected final String TEXT_161 = ") {" + NL + "                    if(null == originalMap_";
  protected final String TEXT_162 = ".get(util_";
  protected final String TEXT_163 = ".getFileChecksum(file_";
  protected final String TEXT_164 = "))) {" + NL + "                    \tglobalMap.put(\"";
  protected final String TEXT_165 = "_FILENAME\", file_";
  protected final String TEXT_166 = ".getName());" + NL + "                        globalMap.put(\"";
  protected final String TEXT_167 = "_UPDATED_FILE\", file_";
  protected final String TEXT_168 = ".getAbsolutePath());" + NL + "                        found_";
  protected final String TEXT_169 = " = true;" + NL + "                        originalMap_";
  protected final String TEXT_170 = ".put(util_";
  protected final String TEXT_171 = ".getFileChecksum(file_";
  protected final String TEXT_172 = "), file_";
  protected final String TEXT_173 = ");//occur repeat value              " + NL + "                        break;" + NL + "                    }" + NL + "                }               ";
  protected final String TEXT_174 = NL;
  protected final String TEXT_175 = "   " + NL + "                }";
  protected final String TEXT_176 = NL + "            count_";
  protected final String TEXT_177 = "++;" + NL + "            " + NL + "            globalMap.put(\"";
  protected final String TEXT_178 = "_CURRENT_ITERATION\", count_";
  protected final String TEXT_179 = ");" + NL + "            " + NL + "            if (!found_";
  protected final String TEXT_180 = ") {" + NL + "            \tThread.sleep(";
  protected final String TEXT_181 = " * 1000);" + NL + "                continue;" + NL + "            }            " + NL;
  protected final String TEXT_182 = "\t" + NL + "\t\t";
  protected final String TEXT_183 = ".";
  protected final String TEXT_184 = " = ((";
  protected final String TEXT_185 = ")globalMap.get(\"";
  protected final String TEXT_186 = "_";
  protected final String TEXT_187 = "\"));";
  protected final String TEXT_188 = "            ";
  protected final String TEXT_189 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    boolean incldSubdir = ("true").equals(ElementParameterParser.getValue(node, "__INCLUDE_SUBDIR__"));
    boolean present = ("true").equals(ElementParameterParser.getValue(node, "__INCLUDE_PRESENT__"));
    String triggerAction = ElementParameterParser.getValue(node, "__ACTION_ON__");
    boolean waitRelease = ("true").equals(ElementParameterParser.getValue(node, "__WAIT_RELEASE__"));
    boolean nonUpdate = "true".equals(ElementParameterParser.getValue(node, "__NON_UPDATE__"));
    String interval = ElementParameterParser.getValue(node, "__INTERVAL_CHECK__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(ElementParameterParser.getValue(node, "__DIRECTORY__") );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(ElementParameterParser.getValue(node, "__FILEMASK__"));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(ElementParameterParser.getValue(node, "__CASE_SENSITIVE__") );
    stringBuffer.append(TEXT_5);
    
                        if(incldSubdir==true)  {
                            
    stringBuffer.append(TEXT_6);
    
                        }   
                        
    stringBuffer.append(TEXT_7);
    
			if(("filecreated").equals(triggerAction) || ("fileall").equals(triggerAction)) {

    stringBuffer.append(TEXT_8);
    
			}

    
            if(("fileupdated").equals(triggerAction) || ("fileall").equals(triggerAction)) {
                
    stringBuffer.append(TEXT_9);
    
            }
            
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
    
        if(("fileupdated").equals(triggerAction) || ("fileall").equals(triggerAction)) {
            
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
        }
        
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
            if(!("").equals(ElementParameterParser.getValue(node,"__MAX_ITERATIONS__"))){
                
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(ElementParameterParser.getValue(node, "__MAX_ITERATIONS__") );
    stringBuffer.append(TEXT_28);
    
            } 
            
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    
            if(present){
                
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
    
    			}
			
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
            if(("filecreated").equals(triggerAction)) {
            
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    	
   					//fix bug 22123,if we can rename the filename,that means no other program is using the file.
           			if(waitRelease){
           			
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(interval);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(interval);
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
    
            } else if(("filedeleted").equals(triggerAction)) {
            
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
    
            } else if(("fileupdated").equals(triggerAction)) {
                
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    if(!nonUpdate){
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    }else{
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    }
    stringBuffer.append(TEXT_117);
    
            } else if(("fileall").equals(triggerAction)) {
                
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_138);
    	
   					//fix bug 22123,if we can rename the filename,that means no other program is using the file.
           			if(waitRelease) {
           			
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(interval);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(interval);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    
        	    	}
            		
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    
            }
            
    stringBuffer.append(TEXT_174);
    
            if(present) {
                
    stringBuffer.append(TEXT_175);
    
            }
            
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(ElementParameterParser.getValue(node, "__WAIT__"));
    stringBuffer.append(TEXT_181);
    	
	//*************************************************************//	
	//The following part will extract data from globalMap to schema in order to support the MAIN link simply.
	//step 1:
	
	IConnection firstDataConn = null;
	List<IMetadataColumn> firstColumnList = null;

	//1. get first DATA Link
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();	
	if(conns != null && conns.size() > 0){
		for(IConnection conn : conns){
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				firstDataConn = conn;
				break;
			}
		}
	}

	//2. get first columnList (with real columns data) 	
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ( metadatas != null && metadatas.size() > 0 ) {
		IMetadataTable metadata = metadatas.get(0);
		if(metadata != null){
			firstColumnList = metadata.getListColumns(); 
			if ( firstColumnList == null || firstColumnList.size() == 0 ) {
				firstColumnList = null;
			}
		}
	}
	
	//3. check the config Link and Schema
	if(firstDataConn != null && firstColumnList != null)
	{
    	//step 2:
    
        String firstDataConnName = firstDataConn.getName();
        for (IMetadataColumn column: firstColumnList) {
        	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());

    stringBuffer.append(TEXT_182);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(typeToGenerate );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_187);
    
	    }
 	}

    stringBuffer.append(TEXT_188);
    stringBuffer.append(TEXT_189);
    return stringBuffer.toString();
  }
}
