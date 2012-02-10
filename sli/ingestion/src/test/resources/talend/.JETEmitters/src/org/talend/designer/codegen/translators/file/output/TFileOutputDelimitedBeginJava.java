package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TFileOutputDelimitedBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputDelimitedBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputDelimitedBeginJava result = new TFileOutputDelimitedBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String fileName_";
  protected final String TEXT_3 = " = \"\";";
  protected final String TEXT_4 = NL + "fileName_";
  protected final String TEXT_5 = " = (new java.io.File(";
  protected final String TEXT_6 = ")).getAbsolutePath().replace(\"\\\\\",\"/\");" + NL + "String fullName_";
  protected final String TEXT_7 = " = null;" + NL + "String extension_";
  protected final String TEXT_8 = " = null;" + NL + "String directory_";
  protected final String TEXT_9 = " = null;" + NL + "if((fileName_";
  protected final String TEXT_10 = ".indexOf(\"/\") != -1)) {" + NL + "    if(fileName_";
  protected final String TEXT_11 = ".lastIndexOf(\".\") < fileName_";
  protected final String TEXT_12 = ".lastIndexOf(\"/\")) {" + NL + "        fullName_";
  protected final String TEXT_13 = " = fileName_";
  protected final String TEXT_14 = ";" + NL + "        extension_";
  protected final String TEXT_15 = " = \"\";" + NL + "    } else {" + NL + "        fullName_";
  protected final String TEXT_16 = " = fileName_";
  protected final String TEXT_17 = ".substring(0, fileName_";
  protected final String TEXT_18 = ".lastIndexOf(\".\"));" + NL + "        extension_";
  protected final String TEXT_19 = " = fileName_";
  protected final String TEXT_20 = ".substring(fileName_";
  protected final String TEXT_21 = ".lastIndexOf(\".\"));" + NL + "    }           " + NL + "    directory_";
  protected final String TEXT_22 = " = fileName_";
  protected final String TEXT_23 = ".substring(0, fileName_";
  protected final String TEXT_24 = ".lastIndexOf(\"/\"));            " + NL + "} else {" + NL + "    if(fileName_";
  protected final String TEXT_25 = ".lastIndexOf(\".\") != -1) {" + NL + "        fullName_";
  protected final String TEXT_26 = " = fileName_";
  protected final String TEXT_27 = ".substring(0, fileName_";
  protected final String TEXT_28 = ".lastIndexOf(\".\"));" + NL + "        extension_";
  protected final String TEXT_29 = " = fileName_";
  protected final String TEXT_30 = ".substring(fileName_";
  protected final String TEXT_31 = ".lastIndexOf(\".\"));" + NL + "    } else {" + NL + "        fullName_";
  protected final String TEXT_32 = " = fileName_";
  protected final String TEXT_33 = ";" + NL + "        extension_";
  protected final String TEXT_34 = " = \"\";" + NL + "    }" + NL + "    directory_";
  protected final String TEXT_35 = " = \"\";" + NL + "}" + NL + "boolean isFileGenerated_";
  protected final String TEXT_36 = " = true;" + NL + "java.io.File file";
  protected final String TEXT_37 = " = new java.io.File(fileName_";
  protected final String TEXT_38 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_39 = "_FILE_NAME\",fileName_";
  protected final String TEXT_40 = ");";
  protected final String TEXT_41 = NL + "if(file";
  protected final String TEXT_42 = ".exists()){" + NL + "\tisFileGenerated_";
  protected final String TEXT_43 = " = false;" + NL + "}";
  protected final String TEXT_44 = NL;
  protected final String TEXT_45 = NL + "            boolean isFirstCheckDyn_";
  protected final String TEXT_46 = "= true;";
  protected final String TEXT_47 = NL + "    \t\tint nb_line_";
  protected final String TEXT_48 = " = 0;" + NL + "            int splitEvery_";
  protected final String TEXT_49 = " = ";
  protected final String TEXT_50 = ";" + NL + "            int splitedFileNo_";
  protected final String TEXT_51 = " = 0;" + NL + "            int currentRow_";
  protected final String TEXT_52 = " = 0;\t\t" + NL + "    \t\t" + NL + "    \t\tfinal String OUT_DELIM_";
  protected final String TEXT_53 = " = ";
  protected final String TEXT_54 = ";" + NL + "    \t\t" + NL + "    \t\tfinal String OUT_DELIM_ROWSEP_";
  protected final String TEXT_55 = " = ";
  protected final String TEXT_56 = ";" + NL;
  protected final String TEXT_57 = "         " + NL + "                //create directory only if not exists" + NL + "                if(directory_";
  protected final String TEXT_58 = " != null && directory_";
  protected final String TEXT_59 = ".trim().length() != 0) {" + NL + "                    java.io.File dir_";
  protected final String TEXT_60 = " = new java.io.File(directory_";
  protected final String TEXT_61 = ");" + NL + "                    if(!dir_";
  protected final String TEXT_62 = ".exists()) {" + NL + "                        dir_";
  protected final String TEXT_63 = ".mkdirs();" + NL + "                    }" + NL + "                }";
  protected final String TEXT_64 = NL + "    ";
  protected final String TEXT_65 = NL + "\t\t\t\tfile";
  protected final String TEXT_66 = " = new java.io.File(fileName_";
  protected final String TEXT_67 = ");" + NL + "\t\t\t\tString zipName_";
  protected final String TEXT_68 = " = fullName_";
  protected final String TEXT_69 = " + \".zip\";" + NL + "\t\t\t\tjava.io.File file_";
  protected final String TEXT_70 = " = new java.io.File(zipName_";
  protected final String TEXT_71 = ");" + NL + "    \t\t    //routines.system.Row" + NL + "    \t\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_72 = "= null;    \t\t    " + NL + "    \t\t    java.io.Writer out";
  protected final String TEXT_73 = " = null;" + NL + "    \t\t    try {" + NL + "    \t\t    \tzipOut_";
  protected final String TEXT_74 = "= new java.util.zip.ZipOutputStream(" + NL + "    \t\t    \t\t\tnew java.io.BufferedOutputStream(new java.io.FileOutputStream(zipName_";
  protected final String TEXT_75 = ")));" + NL + "    \t\t    \tzipOut_";
  protected final String TEXT_76 = ".putNextEntry(new java.util.zip.ZipEntry(file";
  protected final String TEXT_77 = ".getName()));" + NL + "    \t\t    \tout";
  protected final String TEXT_78 = " = new ";
  protected final String TEXT_79 = "(new java.io.OutputStreamWriter(zipOut_";
  protected final String TEXT_80 = ",";
  protected final String TEXT_81 = "));";
  protected final String TEXT_82 = "\t\t" + NL + "    \t\t    //routines.system.Row" + NL + "    \t\t    java.io.Writer out";
  protected final String TEXT_83 = " = null;" + NL + "            \ttry {" + NL + "            \t\tout";
  protected final String TEXT_84 = " = new ";
  protected final String TEXT_85 = "(new java.io.OutputStreamWriter(" + NL + "            \t\tnew java.io.FileOutputStream(fileName_";
  protected final String TEXT_86 = ", ";
  protected final String TEXT_87 = "),";
  protected final String TEXT_88 = "));";
  protected final String TEXT_89 = NL + "                java.io.Writer out";
  protected final String TEXT_90 = " = null;" + NL + "                file";
  protected final String TEXT_91 = " = new java.io.File(fullName_";
  protected final String TEXT_92 = " + splitedFileNo_";
  protected final String TEXT_93 = " + extension_";
  protected final String TEXT_94 = ");" + NL + "                try {" + NL + "                \tout";
  protected final String TEXT_95 = " = new ";
  protected final String TEXT_96 = "(new java.io.OutputStreamWriter(" + NL + "                        new java.io.FileOutputStream(fullName_";
  protected final String TEXT_97 = " + splitedFileNo_";
  protected final String TEXT_98 = " + extension_";
  protected final String TEXT_99 = ", ";
  protected final String TEXT_100 = "),";
  protected final String TEXT_101 = "));";
  protected final String TEXT_102 = NL + "\t\t\t\t";
  protected final String TEXT_103 = NL + "\t\t\t\tsynchronized (multiThreadLockWrite) {" + NL + "\t\t\t\t";
  protected final String TEXT_104 = NL + "\t\t\t\tsynchronized (lockWrite) {" + NL + "\t\t        ";
  protected final String TEXT_105 = NL + "\t\t\t\tObject[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "\t\t\t\tsynchronized (pLockWrite) {" + NL + "\t\t\t\t";
  protected final String TEXT_106 = NL + "    \t\t    if(file_";
  protected final String TEXT_107 = ".length()==0)  " + NL + "    \t\t    {" + NL + "    \t\t    ";
  protected final String TEXT_108 = NL + "    \t\t    if(file";
  protected final String TEXT_109 = ".length()==0)  " + NL + "    \t\t    {" + NL + "    \t\t        ";
  protected final String TEXT_110 = NL + "\t    \t\t            out";
  protected final String TEXT_111 = ".write(\"";
  protected final String TEXT_112 = "\");" + NL + "\t    \t\t            ";
  protected final String TEXT_113 = NL + "\t    \t\t                out";
  protected final String TEXT_114 = ".write(OUT_DELIM_";
  protected final String TEXT_115 = ");" + NL + "\t    \t\t                ";
  protected final String TEXT_116 = NL + "    \t\t        out";
  protected final String TEXT_117 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_118 = ");" + NL + "    \t\t        out";
  protected final String TEXT_119 = ".flush();" + NL + "    \t\t    }" + NL + "\t\t        ";
  protected final String TEXT_120 = NL + "\t\t\t\t} " + NL + "\t\t        ";
  protected final String TEXT_121 = NL + "\t\t\t\t} " + NL + "\t\t        ";
  protected final String TEXT_122 = NL + "\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_123 = NL + "    \t\t    //routines.system.Row" + NL + "    \t\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_124 = "= null;    \t\t    " + NL + "    \t\t    java.io.OutputStreamWriter writer_";
  protected final String TEXT_125 = " = null;" + NL + "    \t\t    java.io.Writer out";
  protected final String TEXT_126 = " = null;" + NL + "    \t\t    try {" + NL + "    \t\t    \tzipOut_";
  protected final String TEXT_127 = "=new java.util.zip.ZipOutputStream(new java.io.BufferedOutputStream(";
  protected final String TEXT_128 = "));" + NL + "    \t\t    \tzipOut_";
  protected final String TEXT_129 = ".putNextEntry(new java.util.zip.ZipEntry(\"TalendOutputDelimited\"));" + NL + "    \t\t    \twriter_";
  protected final String TEXT_130 = " = new java.io.OutputStreamWriter(zipOut_";
  protected final String TEXT_131 = ",";
  protected final String TEXT_132 = ");" + NL + "    \t\t    \tout";
  protected final String TEXT_133 = " = new ";
  protected final String TEXT_134 = "(writer_";
  protected final String TEXT_135 = ");";
  protected final String TEXT_136 = "\t\t" + NL + "    \t\t    //routines.system.Row" + NL + "    \t\t    java.io.OutputStreamWriter writer_";
  protected final String TEXT_137 = " = null;" + NL + "    \t\t    java.io.Writer out";
  protected final String TEXT_138 = " = null;" + NL + "    \t\t    try {" + NL + "    \t\t    \twriter_";
  protected final String TEXT_139 = " = new java.io.OutputStreamWriter(";
  protected final String TEXT_140 = ",";
  protected final String TEXT_141 = ");" + NL + "    \t\t    \tout";
  protected final String TEXT_142 = " = new ";
  protected final String TEXT_143 = "(writer_";
  protected final String TEXT_144 = ");";
  protected final String TEXT_145 = NL + "    \t\t            out";
  protected final String TEXT_146 = ".write(\"";
  protected final String TEXT_147 = "\");" + NL + "    \t\t            ";
  protected final String TEXT_148 = NL + "    \t\t                out";
  protected final String TEXT_149 = ".write(OUT_DELIM_";
  protected final String TEXT_150 = ");" + NL + "    \t\t                ";
  protected final String TEXT_151 = NL + "    \t\t        out";
  protected final String TEXT_152 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_153 = ");";
  protected final String TEXT_154 = NL;
  protected final String TEXT_155 = NL;
  protected final String TEXT_156 = NL + "            boolean isFirstCheckDyn_";
  protected final String TEXT_157 = "= true;" + NL + "            String[] headColu";
  protected final String TEXT_158 = " = null;";
  protected final String TEXT_159 = NL + "            String[] headColu";
  protected final String TEXT_160 = "=new String[";
  protected final String TEXT_161 = "];";
  protected final String TEXT_162 = "   \t    \t\t" + NL + "            class CSVBasicSet_";
  protected final String TEXT_163 = "{          \t" + NL + "            \tprivate char field_Delim;            \t" + NL + "            \tprivate char row_Delim;            \t" + NL + "            \tprivate char escape;            \t" + NL + "            \tprivate char textEnclosure;" + NL + "            \t          \t" + NL + "            \tpublic void setFieldSeparator(String fieldSep) throws IllegalArgumentException{" + NL + "                    char field_Delim_";
  protected final String TEXT_164 = "[] = null;" + NL + "                    " + NL + "            \t\t//support passing value (property: Field Separator) by 'context.fs' or 'globalMap.get(\"fs\")'." + NL + "            \t\tif (fieldSep.length() > 0 ){" + NL + "            \t\t\tfield_Delim_";
  protected final String TEXT_165 = " = fieldSep.toCharArray();" + NL + "            \t\t}else { " + NL + "            \t\t\tthrow new IllegalArgumentException(\"Field Separator must be assigned a char.\");" + NL + "            \t\t}" + NL + "            \t\tthis.field_Delim = field_Delim_";
  protected final String TEXT_166 = "[0];" + NL + "            \t}" + NL + "            \t" + NL + "            \tpublic char getFieldDelim(){" + NL + "            \t\tif(this.field_Delim==0){" + NL + "            \t\t\tsetFieldSeparator(";
  protected final String TEXT_167 = ");" + NL + "            \t\t}" + NL + "            \t\treturn this.field_Delim;" + NL + "            \t}" + NL + "            \t" + NL + "            \tpublic void setRowSeparator(String rowSep){" + NL + "            \t\tchar row_Delim";
  protected final String TEXT_168 = "[] = null;" + NL + "                 " + NL + "            \t\t//support passing value (property: Row Separator) by 'context.rs' or 'globalMap.get(\"rs\")'.  " + NL + "            \t\tif (rowSep.length() > 0 ){ " + NL + "            \t\t\trow_Delim";
  protected final String TEXT_169 = " = rowSep.toCharArray();" + NL + "            \t\t}else {" + NL + "            \t\t\tthrow new IllegalArgumentException(\"Row Separator must be assigned a char.\");" + NL + "            \t\t}" + NL + "            \t\tthis.row_Delim = row_Delim";
  protected final String TEXT_170 = "[0];" + NL + "            \t}" + NL + "            \t" + NL + "            \tpublic char getRowDelim(){" + NL + "            \t\tif(this.row_Delim==0){" + NL + "            \t\t\tsetRowSeparator(";
  protected final String TEXT_171 = ");" + NL + "            \t\t}" + NL + "            \t\treturn this.row_Delim;" + NL + "            \t}" + NL + "            \t       \t        " + NL + "        \t    public void setEscapeAndTextEnclosure(String strEscape, String strTextEnclosure) throws IllegalArgumentException{" + NL + "        \t        if(strEscape.length() <= 0 ){ " + NL + "        \t            throw new IllegalArgumentException(\"Escape Char must be assigned a char.\"); " + NL + "        \t        }" + NL + "        \t        " + NL + "                \tif (\"\".equals(strTextEnclosure)) strTextEnclosure = \"\\0\";" + NL + "        \t\t\tchar textEnclosure_";
  protected final String TEXT_172 = "[] = null;" + NL + "        \t        " + NL + "        \t        if(strTextEnclosure.length() > 0 ){ " + NL + "              \t\t\ttextEnclosure_";
  protected final String TEXT_173 = " = strTextEnclosure.toCharArray(); " + NL + "        \t\t\t}else { " + NL + "        \t            throw new IllegalArgumentException(\"Text Enclosure must be assigned a char.\"); " + NL + "        \t        }" + NL + "" + NL + "        \t\t\tif((\"\\\\\\\\\").equals(strEscape)){" + NL + "        \t\t\t\tthis.escape = com.csvreader.CsvWriter.ESCAPE_MODE_BACKSLASH;" + NL + "        \t\t\t}else if(strEscape.equals(strTextEnclosure)){" + NL + "        \t\t\t\tthis.escape = com.csvreader.CsvWriter.ESCAPE_MODE_DOUBLED;" + NL + "        \t\t\t}" + NL + "        \t\t\t" + NL + "        \t\t\tthis.textEnclosure = textEnclosure_";
  protected final String TEXT_174 = "[0];" + NL + "            \t}" + NL + "            \t" + NL + "            \tpublic char getEscapeChar(){" + NL + "            \t\treturn (char)this.escape;" + NL + "            \t}" + NL + "            \t" + NL + "            \tpublic char getTextEnclosure(){" + NL + "            \t\treturn this.textEnclosure;" + NL + "            \t}" + NL + "            }" + NL + "" + NL + "            int nb_line_";
  protected final String TEXT_175 = " = 0;" + NL + "            int splitEvery_";
  protected final String TEXT_176 = " = ";
  protected final String TEXT_177 = ";" + NL + "            int splitedFileNo_";
  protected final String TEXT_178 = " =0;" + NL + "            int currentRow_";
  protected final String TEXT_179 = " = 0;" + NL + "            " + NL + "            " + NL + "            CSVBasicSet_";
  protected final String TEXT_180 = " csvSettings_";
  protected final String TEXT_181 = " = new CSVBasicSet_";
  protected final String TEXT_182 = "();" + NL + "            csvSettings_";
  protected final String TEXT_183 = ".setFieldSeparator(";
  protected final String TEXT_184 = ");" + NL + "            csvSettings_";
  protected final String TEXT_185 = ".setRowSeparator(";
  protected final String TEXT_186 = ");" + NL + "\t\t\tcsvSettings_";
  protected final String TEXT_187 = ".setEscapeAndTextEnclosure(";
  protected final String TEXT_188 = ",";
  protected final String TEXT_189 = ");";
  protected final String TEXT_190 = "         " + NL + "                //create directory only if not exists" + NL + "                if(directory_";
  protected final String TEXT_191 = " != null && directory_";
  protected final String TEXT_192 = ".trim().length() != 0) {" + NL + "                    java.io.File dir_";
  protected final String TEXT_193 = " = new java.io.File(directory_";
  protected final String TEXT_194 = ");" + NL + "                    if(!dir_";
  protected final String TEXT_195 = ".exists()) {" + NL + "                        dir_";
  protected final String TEXT_196 = ".mkdirs();" + NL + "                    }" + NL + "                }";
  protected final String TEXT_197 = NL + "\t\t\t\tfile";
  protected final String TEXT_198 = " = new java.io.File(fileName_";
  protected final String TEXT_199 = ");" + NL + "\t\t\t\tString zipName_";
  protected final String TEXT_200 = " = fullName_";
  protected final String TEXT_201 = " + \".zip\";" + NL + "\t\t\t\tjava.io.File file_";
  protected final String TEXT_202 = " = new java.io.File(zipName_";
  protected final String TEXT_203 = ");\t\t\t\t" + NL + "    \t\t    //routines.system.Row" + NL + "    \t\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_204 = " = null;" + NL + "                java.io.Writer out";
  protected final String TEXT_205 = " = null;" + NL + "                com.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_206 = " = null; " + NL + "                try {" + NL + "                \tzipOut_";
  protected final String TEXT_207 = "=new java.util.zip.ZipOutputStream(" + NL + "    \t\t    \t\t\tnew java.io.BufferedOutputStream(new java.io.FileOutputStream(zipName_";
  protected final String TEXT_208 = ")));" + NL + "\t    \t\t    zipOut_";
  protected final String TEXT_209 = ".putNextEntry(new java.util.zip.ZipEntry(file";
  protected final String TEXT_210 = ".getName()));" + NL + "\t    \t\t    out";
  protected final String TEXT_211 = " = new routines.system.BufferedOutput(new java.io.OutputStreamWriter(zipOut_";
  protected final String TEXT_212 = ", ";
  protected final String TEXT_213 = "));" + NL + "\t    \t\t    java.io.StringWriter strWriter";
  protected final String TEXT_214 = " = new java.io.StringWriter();" + NL + "\t    \t\t    CsvWriter";
  protected final String TEXT_215 = " = new com.csvreader.CsvWriter(strWriter";
  protected final String TEXT_216 = ", csvSettings_";
  protected final String TEXT_217 = ".getFieldDelim());";
  protected final String TEXT_218 = "\t\t" + NL + "                java.io.Writer out";
  protected final String TEXT_219 = " = null;" + NL + "                com.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_220 = " = null;" + NL + "                try {" + NL + "                \tout";
  protected final String TEXT_221 = " = new routines.system.BufferedOutput(new java.io.OutputStreamWriter(" + NL + "                    new java.io.FileOutputStream(fileName_";
  protected final String TEXT_222 = ", ";
  protected final String TEXT_223 = "), ";
  protected final String TEXT_224 = "));" + NL + "\t\t\t\t\tjava.io.StringWriter strWriter";
  protected final String TEXT_225 = " = new java.io.StringWriter();" + NL + "\t\t\t\t\tCsvWriter";
  protected final String TEXT_226 = " = new com.csvreader.CsvWriter(strWriter";
  protected final String TEXT_227 = ", csvSettings_";
  protected final String TEXT_228 = ".getFieldDelim());";
  protected final String TEXT_229 = NL + "\t\t\t\tfile";
  protected final String TEXT_230 = " = new java.io.File(fileName_";
  protected final String TEXT_231 = ");" + NL + "\t\t\t\tString zipName_";
  protected final String TEXT_232 = " = fullName_";
  protected final String TEXT_233 = " + \".zip\";" + NL + "\t\t\t\tjava.io.File file_";
  protected final String TEXT_234 = " = new java.io.File(zipName_";
  protected final String TEXT_235 = ");" + NL + "    \t\t    //routines.system.Row" + NL + "    \t\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_236 = " = null;" + NL + "\t\t\t\tcom.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_237 = " = null;" + NL + "                try {" + NL + "                \tzipOut_";
  protected final String TEXT_238 = "=new java.util.zip.ZipOutputStream(" + NL + "    \t\t    \t\t\tnew java.io.BufferedOutputStream(new java.io.FileOutputStream(zipName_";
  protected final String TEXT_239 = ")));" + NL + "    \t\t    \tzipOut_";
  protected final String TEXT_240 = ".putNextEntry(new java.util.zip.ZipEntry(file";
  protected final String TEXT_241 = ".getName()));" + NL + "    \t\t    \tCsvWriter";
  protected final String TEXT_242 = " = new com.csvreader.CsvWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "                    zipOut_";
  protected final String TEXT_243 = ", ";
  protected final String TEXT_244 = ")), csvSettings_";
  protected final String TEXT_245 = ".getFieldDelim());" + NL + "\t\t\t\t";
  protected final String TEXT_246 = NL + "\t\t\t\tcom.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_247 = " = null;" + NL + "                try {" + NL + "                \tCsvWriter";
  protected final String TEXT_248 = " = new com.csvreader.CsvWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "                    new java.io.FileOutputStream(fileName_";
  protected final String TEXT_249 = ", ";
  protected final String TEXT_250 = "), ";
  protected final String TEXT_251 = ")), csvSettings_";
  protected final String TEXT_252 = ".getFieldDelim());" + NL + "\t\t\t\t";
  protected final String TEXT_253 = NL + "                file";
  protected final String TEXT_254 = " = new java.io.File(fullName_";
  protected final String TEXT_255 = " + splitedFileNo_";
  protected final String TEXT_256 = " + extension_";
  protected final String TEXT_257 = ");" + NL + "                java.io.Writer out";
  protected final String TEXT_258 = " = null;" + NL + "                com.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_259 = " = null;" + NL + "                try {" + NL + "                \tout";
  protected final String TEXT_260 = " = new routines.system.BufferedOutput(new java.io.OutputStreamWriter(" + NL + "                        new java.io.FileOutputStream(fullName_";
  protected final String TEXT_261 = " + splitedFileNo_";
  protected final String TEXT_262 = " + extension_";
  protected final String TEXT_263 = ", ";
  protected final String TEXT_264 = "),";
  protected final String TEXT_265 = "));" + NL + "                \tjava.io.StringWriter strWriter";
  protected final String TEXT_266 = " = new java.io.StringWriter();" + NL + "                \tCsvWriter";
  protected final String TEXT_267 = " = new com.csvreader.CsvWriter(strWriter";
  protected final String TEXT_268 = ", csvSettings_";
  protected final String TEXT_269 = ".getFieldDelim());";
  protected final String TEXT_270 = NL + "                file";
  protected final String TEXT_271 = " = new java.io.File(fullName_";
  protected final String TEXT_272 = " + splitedFileNo_";
  protected final String TEXT_273 = " + extension_";
  protected final String TEXT_274 = ");" + NL + "                com.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_275 = " = null; " + NL + "                try {" + NL + "                \tCsvWriter";
  protected final String TEXT_276 = " = new com.csvreader.CsvWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "                        new java.io.FileOutputStream(fullName_";
  protected final String TEXT_277 = " + splitedFileNo_";
  protected final String TEXT_278 = " + extension_";
  protected final String TEXT_279 = ", ";
  protected final String TEXT_280 = "),";
  protected final String TEXT_281 = ")), csvSettings_";
  protected final String TEXT_282 = ".getFieldDelim());";
  protected final String TEXT_283 = NL + "        \t    if(csvSettings_";
  protected final String TEXT_284 = ".getRowDelim()!='\\r' && csvSettings_";
  protected final String TEXT_285 = ".getRowDelim()!='\\n')" + NL + "        \t    \tCsvWriter";
  protected final String TEXT_286 = ".setRecordDelimiter(csvSettings_";
  protected final String TEXT_287 = ".getRowDelim());" + NL + "\t    \t\t";
  protected final String TEXT_288 = "\t\t   " + NL + "\t\t\t";
  protected final String TEXT_289 = NL + "\t\t\tsynchronized (multiThreadLockWrite) {" + NL + "\t\t\t";
  protected final String TEXT_290 = NL + "\t\t\tsynchronized (lockWrite) {" + NL + "\t        ";
  protected final String TEXT_291 = NL + "\t\t\tObject[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "\t\t\tsynchronized (pLockWrite) {" + NL + "\t\t\t";
  protected final String TEXT_292 = NL + "\t        \tif(file_";
  protected final String TEXT_293 = ".length()==0)" + NL + "\t        \t{" + NL + "\t        ";
  protected final String TEXT_294 = NL + "    \t        if(file";
  protected final String TEXT_295 = ".length()==0)  " + NL + "    \t        {" + NL + "    \t            ";
  protected final String TEXT_296 = "\t      \t" + NL + "        \t\t\t\theadColu";
  protected final String TEXT_297 = "[";
  protected final String TEXT_298 = "]=\"";
  protected final String TEXT_299 = "\";" + NL + "        \t\t\t\t";
  protected final String TEXT_300 = "\t " + NL + "    \t            CsvWriter";
  protected final String TEXT_301 = ".writeRecord(headColu";
  protected final String TEXT_302 = ");" + NL + "    \t            CsvWriter";
  protected final String TEXT_303 = ".flush();" + NL + "    \t            \t";
  protected final String TEXT_304 = NL + "    \t            out";
  protected final String TEXT_305 = ".write(strWriter";
  protected final String TEXT_306 = ".getBuffer().toString());" + NL + "    \t            out";
  protected final String TEXT_307 = ".flush();" + NL + "                \tstrWriter";
  protected final String TEXT_308 = ".getBuffer().delete(0, strWriter";
  protected final String TEXT_309 = ".getBuffer().length()); \t" + NL + "                \t\t";
  protected final String TEXT_310 = NL + "                }" + NL + "\t        ";
  protected final String TEXT_311 = NL + "\t\t\t} " + NL + "\t        ";
  protected final String TEXT_312 = NL + "\t\t\t} " + NL + "\t        ";
  protected final String TEXT_313 = NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_314 = NL + "    \t\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_315 = " = null;" + NL + "\t\t\t\tjava.io.OutputStreamWriter outWriter_";
  protected final String TEXT_316 = " = null;" + NL + "                java.io.Writer out";
  protected final String TEXT_317 = " = null;\t\t\t\t" + NL + "                com.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_318 = " = null;" + NL + "                try {" + NL + "                \tzipOut_";
  protected final String TEXT_319 = "=new java.util.zip.ZipOutputStream(" + NL + "    \t\t    \t\t\tnew java.io.BufferedOutputStream(";
  protected final String TEXT_320 = "));" + NL + "    \t\t    \tzipOut_";
  protected final String TEXT_321 = ".putNextEntry(new java.util.zip.ZipEntry(\"TalendOutputDelimited\"));" + NL + "    \t\t    \toutWriter_";
  protected final String TEXT_322 = " = new java.io.OutputStreamWriter(zipOut_";
  protected final String TEXT_323 = ", ";
  protected final String TEXT_324 = ");" + NL + "    \t\t    \tout";
  protected final String TEXT_325 = " = new routines.system.BufferedOutput(outWriter_";
  protected final String TEXT_326 = ");" + NL + "    \t\t    \tjava.io.StringWriter strWriter";
  protected final String TEXT_327 = " = new java.io.StringWriter();" + NL + "    \t\t    \tCsvWriter";
  protected final String TEXT_328 = " = new com.csvreader.CsvWriter(strWriter";
  protected final String TEXT_329 = ", csvSettings_";
  protected final String TEXT_330 = ".getFieldDelim());";
  protected final String TEXT_331 = NL + "\t\t\t\tjava.io.OutputStreamWriter outWriter_";
  protected final String TEXT_332 = " = null;" + NL + "                java.io.Writer out";
  protected final String TEXT_333 = " = null;\t\t\t\t" + NL + "                com.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_334 = " = null;  " + NL + "                try {" + NL + "                \toutWriter_";
  protected final String TEXT_335 = " = new java.io.OutputStreamWriter(";
  protected final String TEXT_336 = ", ";
  protected final String TEXT_337 = ");" + NL + "                \tout";
  protected final String TEXT_338 = " = new routines.system.BufferedOutput(outWriter_";
  protected final String TEXT_339 = ");" + NL + "                \tjava.io.StringWriter strWriter";
  protected final String TEXT_340 = " = new java.io.StringWriter();" + NL + "                \tCsvWriter";
  protected final String TEXT_341 = " = new com.csvreader.CsvWriter(strWriter";
  protected final String TEXT_342 = ", csvSettings_";
  protected final String TEXT_343 = ".getFieldDelim());";
  protected final String TEXT_344 = NL + "    \t\t    java.util.zip.ZipOutputStream zipOut_";
  protected final String TEXT_345 = " = null;" + NL + "\t\t\t\tjava.io.OutputStreamWriter outWriter_";
  protected final String TEXT_346 = " = null;" + NL + "\t\t\t\tjava.io.BufferedWriter bufferWriter_";
  protected final String TEXT_347 = " = null;" + NL + "\t\t\t\tcom.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_348 = " = null;" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tzipOut_";
  protected final String TEXT_349 = "=new java.util.zip.ZipOutputStream(" + NL + "    \t\t    \t\t\tnew java.io.BufferedOutputStream(";
  protected final String TEXT_350 = "));" + NL + "    \t\t   \t\tzipOut_";
  protected final String TEXT_351 = ".putNextEntry(new java.util.zip.ZipEntry(\"TalendOutputDelimited\"));" + NL + "    \t\t   \t\toutWriter_";
  protected final String TEXT_352 = " = new java.io.OutputStreamWriter(zipOut_";
  protected final String TEXT_353 = ", ";
  protected final String TEXT_354 = ");" + NL + "    \t\t   \t\tbufferWriter_";
  protected final String TEXT_355 = " = new java.io.BufferedWriter(outWriter_";
  protected final String TEXT_356 = ");" + NL + "    \t\t   \t\tCsvWriter";
  protected final String TEXT_357 = " = new com.csvreader.CsvWriter(bufferWriter_";
  protected final String TEXT_358 = ", csvSettings_";
  protected final String TEXT_359 = ".getFieldDelim());";
  protected final String TEXT_360 = NL + "\t\t\t\tjava.io.OutputStreamWriter outWriter_";
  protected final String TEXT_361 = " = null;" + NL + "\t\t\t\tjava.io.BufferedWriter bufferWriter_";
  protected final String TEXT_362 = " = null;" + NL + "\t\t\t\tcom.csvreader.CsvWriter CsvWriter";
  protected final String TEXT_363 = " = null;" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\toutWriter_";
  protected final String TEXT_364 = " = new java.io.OutputStreamWriter(";
  protected final String TEXT_365 = ", ";
  protected final String TEXT_366 = ");" + NL + "\t\t\t\t\tbufferWriter_";
  protected final String TEXT_367 = " = new java.io.BufferedWriter(outWriter_";
  protected final String TEXT_368 = ");" + NL + "\t\t\t\t\tCsvWriter";
  protected final String TEXT_369 = " = new com.csvreader.CsvWriter(bufferWriter_";
  protected final String TEXT_370 = ", csvSettings_";
  protected final String TEXT_371 = ".getFieldDelim());";
  protected final String TEXT_372 = NL + "        \t    if(csvSettings_";
  protected final String TEXT_373 = ".getRowDelim()!='\\r' && csvSettings_";
  protected final String TEXT_374 = ".getRowDelim()!='\\n')" + NL + "        \t    \tCsvWriter";
  protected final String TEXT_375 = ".setRecordDelimiter(csvSettings_";
  protected final String TEXT_376 = ".getRowDelim());   \t    \t" + NL + "    \t    ";
  protected final String TEXT_377 = NL + "       \t\t\t\theadColu";
  protected final String TEXT_378 = "[";
  protected final String TEXT_379 = "]=\"";
  protected final String TEXT_380 = "\";" + NL + "       \t\t\t\t";
  protected final String TEXT_381 = NL + "        \t\tCsvWriter";
  protected final String TEXT_382 = ".writeRecord(headColu";
  protected final String TEXT_383 = ");\t" + NL + "        \t\t\t";
  protected final String TEXT_384 = NL + "        \t\tout";
  protected final String TEXT_385 = ".write(strWriter";
  protected final String TEXT_386 = ".getBuffer().toString());" + NL + "                strWriter";
  protected final String TEXT_387 = ".getBuffer().delete(0, strWriter";
  protected final String TEXT_388 = ".getBuffer().length());" + NL + "        \t\t\t";
  protected final String TEXT_389 = NL + "\t\t\t\tCsvWriter";
  protected final String TEXT_390 = ".setEscapeMode(csvSettings_";
  protected final String TEXT_391 = ".getEscapeChar());" + NL + "\t\t\t\tCsvWriter";
  protected final String TEXT_392 = ".setTextQualifier(csvSettings_";
  protected final String TEXT_393 = ".getTextEnclosure());" + NL + "\t\t\t\tCsvWriter";
  protected final String TEXT_394 = ".setForceQualifier(true);";
  protected final String TEXT_395 = NL + NL;
  protected final String TEXT_396 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
String outStream = ElementParameterParser.getValue(node,"__STREAMNAME__");
String fileName = ElementParameterParser.getValue(node,"__FILENAME__");

boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));

String parallelize = ElementParameterParser.getValue(node,"__PARALLELIZE__");
boolean isParallelize = (parallelize!=null&&!("").equals(parallelize))?("true").equals(parallelize):false;
	

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
	
if(!useStream){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    
	if(isAppend){

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    
	}
}
if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {	
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    stringBuffer.append(TEXT_44);
    
    
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {
            String fullName = null;
            String extensionName = null;
            String directoryName = null;    
            
            String fieldSeparator = ElementParameterParser.getValueWithUIFieldKey(
                node,
                "__FIELDSEPARATOR__",
                "FIELDSEPARATOR"
            );
            
            String rowSeparator = ElementParameterParser.getValueWithUIFieldKey(
                node,
                "__ROWSEPARATOR__",
                "ROWSEPARATOR"
            );
            
            String encoding = ElementParameterParser.getValue(
                node,
                "__ENCODING__"
            );
            
            boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));
    		String fileNewname = ElementParameterParser.getValue(node,"__FILENAME__");
    		
    		boolean isInRowMode = ("true").equals(ElementParameterParser.getValue(node,"__ROW_MODE__"));
    		
    		boolean split = ("true").equals(ElementParameterParser.getValue(node, "__SPLIT__"));
            String splitEvery = ElementParameterParser.getValue(node, "__SPLIT_EVERY__");
            
            boolean compress = ("true").equals(ElementParameterParser.getValue(node,"__COMPRESS__"));
            
            boolean hasDynamic = metadata.isDynamicSchema();
				if(hasDynamic){
            
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    	}
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(splitEvery );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_56);
    
			if(!useStream){
			//**************************** the following is the part of file Path***************************************
			
    			if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){

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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    
    			}

    stringBuffer.append(TEXT_64);
     	
    			String writerClass = null;
    			if(isInRowMode){
    				writerClass = "routines.system.BufferedOutput";
    			}else{
    				writerClass = "java.io.BufferedWriter";
    			}
    			if(!split){
    				if(compress && !isAppend){// compress the dest file

    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(writerClass);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_81);
    
    				}else{

    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(writerClass);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_88);
     
					}
    			} else {

    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(writerClass);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_101);
    
    			}
    			
    			if(isIncludeHeader && !hasDynamic){
    		    
    stringBuffer.append(TEXT_102);
    
					if(codeGenArgument.getIsRunInMultiThread()){
				
    stringBuffer.append(TEXT_103);
    
					}
					if (codeGenArgument.subTreeContainsParallelIterate()) {
				
    stringBuffer.append(TEXT_104);
     
		        	}
		        	if (isParallelize) {
				
    stringBuffer.append(TEXT_105);
     
					}
					if(!split && compress && !isAppend){
		        
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    
    		    	}else{
    		    	
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    
    		        }		
	    		        List<IMetadataColumn> columns = metadata.getListColumns();
	    		        int sizeColumns = columns.size();
	    		        for (int i = 0; i < sizeColumns; i++) {
	    		            IMetadataColumn column = columns.get(i);
	    		            
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_112);
    
	    		            if(i != sizeColumns - 1) {
	    		                
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    
	    		            }
	    		        }
    		        
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
     
		        	if (isParallelize) {
				
    stringBuffer.append(TEXT_120);
    
		        	}
					if (codeGenArgument.subTreeContainsParallelIterate()) {
				
    stringBuffer.append(TEXT_121);
    
		        	}
		        	if(codeGenArgument.getIsRunInMultiThread()){
				
    stringBuffer.append(TEXT_122);
    
					}
		        
    
    			}

    		}else{
    		//***********************the following is the part of output Stream**************************************
    		
    			String writerClass = null;
    			if(isInRowMode){
    				writerClass = "routines.system.BufferedOutput";
    			}else{
    				writerClass = "java.io.BufferedWriter";
    			}
    			if(compress){// compress the dest output stream
 
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(writerClass);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_135);
    
    			}else{

    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(writerClass);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    
				}
    			if(isIncludeHeader && !hasDynamic){
    		        List<IMetadataColumn> columns = metadata.getListColumns();
    		        int sizeColumns = columns.size();
    		        for (int i = 0; i < sizeColumns; i++) {
    		            IMetadataColumn column = columns.get(i);
    		            
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_147);
    
    		            if(i != sizeColumns - 1) {
    		                
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    
    		            }
    		        }
    		        
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_153);
    
    		    }
    		}// ****************************output Stream end*************************************
        }
    }
    
    stringBuffer.append(TEXT_154);
    
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}else{// the following is the tFileOutputCSV component
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    stringBuffer.append(TEXT_155);
    
    
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {                       
        	List<IMetadataColumn> columns = metadata.getListColumns();
	    	int sizeColumns = columns.size(); 
            String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
    		String delim = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
    		String rowSeparator = ElementParameterParser.getValue(node, "__CSVROWSEPARATOR__");
    		
        	String escapeChar1 = ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
        	
 			if(escapeChar1.equals("\"\"\"")){
 				escapeChar1 = "\"\\\"\"";
 			}
        	
        	String textEnclosure1 = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
 			if(textEnclosure1.equals("\"\"\"")){
 				textEnclosure1 = "\"\\\"\"";
 			}
 			
        	boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));
        	boolean split = ("true").equals(ElementParameterParser.getValue(node, "__SPLIT__"));
        	boolean isInRowMode = ("true").equals(ElementParameterParser.getValue(node,"__ROW_MODE__"));
            String splitEvery = ElementParameterParser.getValue(node, "__SPLIT_EVERY__");
            
            boolean compress = ("true").equals(ElementParameterParser.getValue(node,"__COMPRESS__"));
            
            boolean hasDynamic = metadata.isDynamicSchema();
				if(hasDynamic){
            
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_158);
    
            	}else{
            
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(sizeColumns);
    stringBuffer.append(TEXT_161);
    
            	}
    	    
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(delim );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(splitEvery );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(delim);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(escapeChar1 );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(textEnclosure1 );
    stringBuffer.append(TEXT_189);
    
			if(!useStream){
			//**************************** the following is the part of file Path***************************************
            	if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){
                
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
    
            	}
    	
	            if(!split){
	            	if(isInRowMode){
    					if(compress && !isAppend){// compress the dest file

    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_210);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_214);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_215);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_217);
    
    					}else{

    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_220);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_221);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_222);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_223);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_225);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_227);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_228);
    
                		}
                	}else{
                		if(compress && !isAppend){// compress the dest file
				
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_230);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_231);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_232);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_234);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_237);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_239);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_240);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_241);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_242);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_243);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_244);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_245);
    
						}else{
				
    stringBuffer.append(TEXT_246);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_247);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_248);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_249);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_250);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_251);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_252);
    
						}
                	}
	            }else{
	            	if(isInRowMode){
                
    stringBuffer.append(TEXT_253);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_254);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_255);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_258);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_260);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_261);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_263);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_264);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_265);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_268);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_269);
    
                	}else{
                
    stringBuffer.append(TEXT_270);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_271);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_272);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_273);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_274);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_275);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_276);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_277);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_278);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_279);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_280);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_281);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_282);
    
                	}
	            }
	    		
    stringBuffer.append(TEXT_283);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_286);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_287);
    
	    	    if(isIncludeHeader && !hasDynamic)
	    	    {
    	        
    stringBuffer.append(TEXT_288);
    
				if(codeGenArgument.getIsRunInMultiThread()){
			
    stringBuffer.append(TEXT_289);
    
				}
				if (codeGenArgument.subTreeContainsParallelIterate()) {
			
    stringBuffer.append(TEXT_290);
     
	        	}
	        	if (isParallelize) {
			
    stringBuffer.append(TEXT_291);
     
				}
				if(!split && compress && !isAppend){
	        
    stringBuffer.append(TEXT_292);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_293);
    
	        	}else{
	        
    stringBuffer.append(TEXT_294);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_295);
    
    	        }			
	         			for(int i = 0 ; i < sizeColumns ; i++)
	        			{
	        				IMetadataColumn column = columns.get(i);
        				
    stringBuffer.append(TEXT_296);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_297);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_298);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_299);
    
         				}
    	            
    stringBuffer.append(TEXT_300);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_301);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_302);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_303);
    if(isInRowMode){
    stringBuffer.append(TEXT_304);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_305);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_306);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_307);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_308);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_309);
    }
    stringBuffer.append(TEXT_310);
     
	        	if (isParallelize) {
			
    stringBuffer.append(TEXT_311);
    
	        	}
				if (codeGenArgument.subTreeContainsParallelIterate()) {
			
    stringBuffer.append(TEXT_312);
    
	        	}
	        	if(codeGenArgument.getIsRunInMultiThread()){
			
    stringBuffer.append(TEXT_313);
    
				}
	        
    		  
	    	    }
	    	}else{
    		//***********************the following is the part of output Stream**************************************
	            if(isInRowMode){
	            	if(compress){// compress the dest output stream

    stringBuffer.append(TEXT_314);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_315);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_316);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_317);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_318);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_319);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_320);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_321);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_322);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_323);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_324);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_325);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_326);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_327);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_328);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_329);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_330);
    
	            	}else{

    stringBuffer.append(TEXT_331);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_332);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_333);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_334);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_335);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_336);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_337);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_338);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_339);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_340);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_341);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_342);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_343);
    
					}
                }else{
                	if(compress){// compress the dest output stream

    stringBuffer.append(TEXT_344);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_345);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_346);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_347);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_348);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_349);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_350);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_351);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_352);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_353);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_354);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_355);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_356);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_357);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_358);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_359);
    
                	}else{

    stringBuffer.append(TEXT_360);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_361);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_362);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_363);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_364);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_365);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_366);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_367);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_368);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_369);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_371);
    
					}
                }

    stringBuffer.append(TEXT_372);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_373);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_374);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_375);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_376);
     		
	    	    if(isIncludeHeader && !hasDynamic)
	    	    {
	    	        for(int i = 0; i < sizeColumns; i++)
	        		{
	        			IMetadataColumn column = columns.get(i);
        			
    stringBuffer.append(TEXT_377);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_378);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_379);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_380);
    
	        		}
    	        
    stringBuffer.append(TEXT_381);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_382);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_383);
    if(isInRowMode){
    stringBuffer.append(TEXT_384);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_385);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_386);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_387);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_388);
    }
	    	    }
	    	}//*****************************csv mode under output stream end********************************************************
	    	
	    	if(!(isIncludeHeader && hasDynamic)){//when there is dynamic schema, it won't be enclosed with "\""

    stringBuffer.append(TEXT_389);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_390);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_391);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_392);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_393);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_394);
    
			}
	    }
    }
    
    stringBuffer.append(TEXT_395);
    
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

    stringBuffer.append(TEXT_396);
    return stringBuffer.toString();
  }
}
