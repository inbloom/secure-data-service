package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TFileInputDelimitedBeginJava
{
  protected static String nl;
  public static synchronized TFileInputDelimitedBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputDelimitedBeginJava result = new TFileInputDelimitedBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = NL + "\t\t\trowHelper_";
  protected final String TEXT_4 = ".valueToConn(";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ");" + NL + "\t\t";
  protected final String TEXT_7 = NL + "\t\t\trowHelper_";
  protected final String TEXT_8 = ".valueToConnWithD(";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = ", ";
  protected final String TEXT_11 = ");" + NL + "\t\t";
  protected final String TEXT_12 = NL + "\t\t\trowHelper_";
  protected final String TEXT_13 = ".connToConn(";
  protected final String TEXT_14 = ",";
  protected final String TEXT_15 = ");" + NL + "\t\t";
  protected final String TEXT_16 = NL + "\t\t\tclass RowHelper_";
  protected final String TEXT_17 = "{" + NL + "\t\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\t\tpublic void valueToConn(";
  protected final String TEXT_19 = " ";
  protected final String TEXT_20 = ",";
  protected final String TEXT_21 = "Struct ";
  protected final String TEXT_22 = ") throws Exception{" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_23 = NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_24 = NL + "\t\t\t\t\tpublic void valueToConnWithD(";
  protected final String TEXT_25 = " ";
  protected final String TEXT_26 = ",";
  protected final String TEXT_27 = "Struct ";
  protected final String TEXT_28 = ", routines.system.Dynamic ";
  protected final String TEXT_29 = ") throws Exception{" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_30 = NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_31 = NL + "\t\t\t\t\tpublic void connToConn(";
  protected final String TEXT_32 = "Struct ";
  protected final String TEXT_33 = ",";
  protected final String TEXT_34 = "Struct ";
  protected final String TEXT_35 = ") throws Exception{" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_36 = NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_37 = NL + "\t\t\t}" + NL + "\t\t\tRowHelper_";
  protected final String TEXT_38 = " rowHelper_";
  protected final String TEXT_39 = "  = new RowHelper_";
  protected final String TEXT_40 = "();" + NL + "\t\t";
  protected final String TEXT_41 = NL + "\t";
  protected final String TEXT_42 = NL + "\t\t\tif(isFirstCheckDyn_";
  protected final String TEXT_43 = "){// for the header line" + NL + "\t\t\t\t";
  protected final String TEXT_44 = NL + "\t\t\t\t\tint colsLen_";
  protected final String TEXT_45 = " = row";
  protected final String TEXT_46 = ".length;" + NL + "\t\t\t\t";
  protected final String TEXT_47 = NL + "\t\t\t\t\tint colsLen_";
  protected final String TEXT_48 = " = fid_";
  protected final String TEXT_49 = ".getColumnsCountOfCurrentRow();" + NL + "\t\t\t\t";
  protected final String TEXT_50 = NL + "    \t\t\tfor (int i = ";
  protected final String TEXT_51 = "-1; i < colsLen_";
  protected final String TEXT_52 = "; i++) {" + NL + "\t\t\t    \troutines.system.DynamicMetadata dynamicMetadata_";
  protected final String TEXT_53 = " = new routines.system.DynamicMetadata();" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_54 = ".setName(";
  protected final String TEXT_55 = "row";
  protected final String TEXT_56 = "[i]";
  protected final String TEXT_57 = "fid_";
  protected final String TEXT_58 = ".get(i)";
  protected final String TEXT_59 = ".replaceAll(\"[ .-]+\", \"_\"));" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_60 = ".setDbName(dynamicMetadata_";
  protected final String TEXT_61 = ".getName());" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_62 = ".setType(\"id_String\");" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_63 = ".setDbType(\"VARCHAR\");" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_64 = ".setLength(100);" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_65 = ".setPrecision(0);" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_66 = ".setNullable(true);" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_67 = ".setKey(false);" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_68 = ".setSourceType(routines.system.DynamicMetadata.sourceTypes.demilitedFile);" + NL + "\t\t\t    \tdynamicMetadata_";
  protected final String TEXT_69 = ".setColumnPosition(i);" + NL + "\t\t\t    \tdynamic_";
  protected final String TEXT_70 = ".metadatas.add(dynamicMetadata_";
  protected final String TEXT_71 = ");" + NL + "\t\t\t    }" + NL + "    \t\t\tisFirstCheckDyn_";
  protected final String TEXT_72 = " = false;" + NL + "    \t\t\tcontinue;" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_73 = NL + "\t\t\tint footer_value_";
  protected final String TEXT_74 = " = ";
  protected final String TEXT_75 = ", random_value_";
  protected final String TEXT_76 = " = ";
  protected final String TEXT_77 = ";" + NL + "\t\t\tif(footer_value_";
  protected final String TEXT_78 = " >0 || random_value_";
  protected final String TEXT_79 = " > 0){" + NL + "\t\t\t\tthrow new Exception(\"When the input source is a stream,footer and random shouldn't be bigger than 0.\");\t\t\t\t" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_80 = NL + "\t\t\tint footer_value_";
  protected final String TEXT_81 = " = ";
  protected final String TEXT_82 = ";" + NL + "\t\t\tif(footer_value_";
  protected final String TEXT_83 = " > 0){" + NL + "\t\t\t\tthrow new Exception(\"When the input source is a stream,footer shouldn't be bigger than 0.\");" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_84 = NL + "\t\t\t\t\tString temp = \"\"; " + NL + "\t\t\t\t";
  protected final String TEXT_85 = NL + "\t\t\t\t\t\t\ttemp  = ";
  protected final String TEXT_86 = ".get(";
  protected final String TEXT_87 = ")";
  protected final String TEXT_88 = ";" + NL + "\t\t\t\t\t\t\tif(temp.length() > 0){" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_89 = ".";
  protected final String TEXT_90 = " = temp;" + NL + "\t\t\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_91 = ".";
  protected final String TEXT_92 = " = ";
  protected final String TEXT_93 = ";" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_94 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_95 = ".";
  protected final String TEXT_96 = " = ";
  protected final String TEXT_97 = ".get(";
  protected final String TEXT_98 = ")";
  protected final String TEXT_99 = ";" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_100 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_101 = ".clearColumnValues();" + NL + "\t\t\t\t\t\tint fieldCount = ";
  protected final String TEXT_102 = ".getColumnsCountOfCurrentRow();" + NL + "\t\t\t\t\t\tfor (int i = 0; i < ";
  protected final String TEXT_103 = ".getColumnCount(); i++) {" + NL + "\t\t\t\t\t\t\tif ((";
  protected final String TEXT_104 = "-1+i) < fieldCount){" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_105 = ".addColumnValue(";
  protected final String TEXT_106 = ".get(";
  protected final String TEXT_107 = "-1+i)";
  protected final String TEXT_108 = ");" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\telse{" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_109 = ".addColumnValue(\"\");" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_110 = ".";
  protected final String TEXT_111 = "=";
  protected final String TEXT_112 = ";" + NL + "\t\t\t\t\t";
  protected final String TEXT_113 = NL + "\t\t\t\t\t\ttemp = ";
  protected final String TEXT_114 = ".get(";
  protected final String TEXT_115 = ")";
  protected final String TEXT_116 = ";" + NL + "\t\t\t\t\t\tif(temp.length() > 0) {" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_117 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_118 = ".";
  protected final String TEXT_119 = " = temp.getBytes(";
  protected final String TEXT_120 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_121 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_122 = ".";
  protected final String TEXT_123 = " = ParserUtils.parseTo_Date(temp, ";
  protected final String TEXT_124 = ", false);" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_125 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_126 = ".";
  protected final String TEXT_127 = " = ParserUtils.parseTo_Date(temp, ";
  protected final String TEXT_128 = ");" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_129 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_130 = ".";
  protected final String TEXT_131 = " = ParserUtils.parseTo_";
  protected final String TEXT_132 = "(ParserUtils.parseTo_Number(temp, ";
  protected final String TEXT_133 = ", ";
  protected final String TEXT_134 = "));" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_135 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_136 = ".";
  protected final String TEXT_137 = " = ParserUtils.parseTo_";
  protected final String TEXT_138 = "(temp);" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_139 = NL + "\t\t\t\t\t\t} else {\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_140 = NL + "\t\t\t\t\t\t\t\tthrow new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_141 = "' in '";
  protected final String TEXT_142 = "' connection, value is invalid or this column should be nullable or have a default value.\");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_143 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_144 = ".";
  protected final String TEXT_145 = " = ";
  protected final String TEXT_146 = ";" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_147 = NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_148 = NL + "\t\t\t\t";
  protected final String TEXT_149 = NL + "\t\t\t\t \tint filedsum = ";
  protected final String TEXT_150 = ".getColumnsCountOfCurrentRow();" + NL + "\t\t\t\t \tif(filedsum < ";
  protected final String TEXT_151 = "){" + NL + "\t\t\t\t\t \tthrow new RuntimeException(\"Column(s) missing\");" + NL + "\t\t\t\t\t } else if(filedsum > ";
  protected final String TEXT_152 = ") {" + NL + "\t\t\t\t\t \tthrow new RuntimeException(\"Too many columns\");" + NL + "\t\t\t\t\t }     " + NL + "\t\t\t\t";
  protected final String TEXT_153 = NL + "    \t\t\t\t";
  protected final String TEXT_154 = ".";
  protected final String TEXT_155 = " = ";
  protected final String TEXT_156 = ".";
  protected final String TEXT_157 = ";" + NL + "\t\t\t\t";
  protected final String TEXT_158 = NL + "\t\t\t";
  protected final String TEXT_159 = NL + "\t\t\t\t\t\t\tboolean isFirstCheckDyn_";
  protected final String TEXT_160 = " = true;" + NL + "\t\t\t\t\t\t\troutines.system.Dynamic dynamic_";
  protected final String TEXT_161 = " = new routines.system.Dynamic();" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_162 = NL + "\t\t\t\tint nb_line_";
  protected final String TEXT_163 = " = 0;" + NL + "\t\t\t\torg.talend.fileprocess.FileInputDelimited fid_";
  protected final String TEXT_164 = " = null;" + NL + "\t\t\t\ttry{" + NL + "\t\t\t\t\t";
  protected final String TEXT_165 = NL + "\t\t\t\t\t\tObject filename_";
  protected final String TEXT_166 = " = ";
  protected final String TEXT_167 = ";\t" + NL + "\t\t\t\t\t\tjava.util.zip.ZipInputStream zis_";
  protected final String TEXT_168 = " = null;" + NL + "\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\tif(filename_";
  protected final String TEXT_169 = " instanceof java.io.InputStream){" + NL + "\t\t\t\t\t\t\t\tzis_";
  protected final String TEXT_170 = " = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream((java.io.InputStream)filename_";
  protected final String TEXT_171 = "));" + NL + "\t\t\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\t\t\tzis_";
  protected final String TEXT_172 = " = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(String.valueOf(filename_";
  protected final String TEXT_173 = "))));" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t} catch(Exception e) {" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_174 = NL + "\t\t\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_175 = NL + "\t\t\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_176 = NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tjava.util.zip.ZipEntry entry_";
  protected final String TEXT_177 = " = null;" + NL + "\t\t" + NL + "\t\t\t\t\t\twhile (true) {" + NL + "\t\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\t\tentry_";
  protected final String TEXT_178 = " = zis_";
  protected final String TEXT_179 = ".getNextEntry();" + NL + "\t\t\t\t\t\t\t} catch(Exception e) {" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_180 = NL + "\t\t\t\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_181 = NL + "\t\t\t\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t\t\t\t\tbreak;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_182 = NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\tif(entry_";
  protected final String TEXT_183 = " == null) {" + NL + "\t\t\t\t\t\t\t\tbreak;" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\tif(entry_";
  protected final String TEXT_184 = ".isDirectory()){ //directory" + NL + "\t\t\t\t\t\t\t\tcontinue;" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\t\tfid_";
  protected final String TEXT_185 = " = new org.talend.fileprocess.FileInputDelimited(zis_";
  protected final String TEXT_186 = ", ";
  protected final String TEXT_187 = ",";
  protected final String TEXT_188 = ",";
  protected final String TEXT_189 = ",";
  protected final String TEXT_190 = ",";
  protected final String TEXT_191 = ",";
  protected final String TEXT_192 = ",";
  protected final String TEXT_193 = ",";
  protected final String TEXT_194 = ", ";
  protected final String TEXT_195 = ");" + NL + "\t\t\t\t\t\t\t} catch(Exception e) {" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_196 = NL + "\t\t\t\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_197 = NL + "\t\t\t\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_198 = NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_199 = NL + "\t\t\t\t\t\tObject filename_";
  protected final String TEXT_200 = " = ";
  protected final String TEXT_201 = ";" + NL + "\t\t\t\t\t\tif(filename_";
  protected final String TEXT_202 = " instanceof java.io.InputStream){" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_203 = NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\tfid_";
  protected final String TEXT_204 = " = new org.talend.fileprocess.FileInputDelimited(";
  protected final String TEXT_205 = ", ";
  protected final String TEXT_206 = ",";
  protected final String TEXT_207 = ",";
  protected final String TEXT_208 = ",";
  protected final String TEXT_209 = ",";
  protected final String TEXT_210 = ",";
  protected final String TEXT_211 = ",";
  protected final String TEXT_212 = ",";
  protected final String TEXT_213 = ", ";
  protected final String TEXT_214 = ");" + NL + "\t\t\t\t\t\t} catch(Exception e) {" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_215 = NL + "\t\t\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_216 = NL + "\t\t\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_217 = NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_218 = NL + "\t\t\t\t\twhile (fid_";
  protected final String TEXT_219 = "!=null && fid_";
  protected final String TEXT_220 = ".nextRecord()) {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_221 = NL + "\t\t\t    \t\t\t\t\t\t";
  protected final String TEXT_222 = " = null;\t\t\t" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_223 = "\t\t\t" + NL + "\t\t\t\t\t\t\t\t\tboolean whetherReject_";
  protected final String TEXT_224 = " = false;" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_225 = " = new ";
  protected final String TEXT_226 = "Struct();" + NL + "\t\t\t\t\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_227 = " " + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_228 = " = null; " + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_229 = NL + "\t\t\t\t\t\t\t" + NL + "\t\t\t    \t\t\t\t\t} catch (Exception e) {" + NL + "\t\t\t        \t\t\t\t\twhetherReject_";
  protected final String TEXT_230 = " = true;" + NL + "\t\t\t        \t\t\t\t\t";
  protected final String TEXT_231 = NL + "\t\t\t            \t\t\t\t\tthrow(e);" + NL + "\t\t\t            \t\t\t\t";
  protected final String TEXT_232 = "                    " + NL + "\t\t\t                    \t\t\t\t";
  protected final String TEXT_233 = " = new ";
  protected final String TEXT_234 = "Struct();" + NL + "\t\t\t\t                \t\t\t\t";
  protected final String TEXT_235 = NL + "\t\t\t\t                \t\t\t\t";
  protected final String TEXT_236 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_237 = ";" + NL + "\t\t\t                \t\t\t\t\t";
  protected final String TEXT_238 = " = null;" + NL + "\t\t\t                \t\t\t\t";
  protected final String TEXT_239 = NL + "\t\t\t                \t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t                \t\t\t\t\t";
  protected final String TEXT_240 = " = null;" + NL + "\t\t\t                \t\t\t\t";
  protected final String TEXT_241 = NL + "\t\t\t            \t\t\t\t\t\t";
  protected final String TEXT_242 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_243 = ";" + NL + "\t\t\t            \t\t\t\t\t";
  protected final String TEXT_244 = NL + "\t\t\t    \t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_245 = NL + "\t\t\t\t\t\t\t\t\t\t\tif(!whetherReject_";
  protected final String TEXT_246 = ") { " + NL + "\t\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_247 = "      " + NL + "\t\t\t             \t\t\t\tif(";
  protected final String TEXT_248 = " == null){ " + NL + "\t\t\t            \t \t\t\t\t";
  protected final String TEXT_249 = " = new ";
  protected final String TEXT_250 = "Struct();" + NL + "\t\t\t             \t\t\t\t}\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_251 = " " + NL + "\t\t\t\t\t\t\t\t\t} " + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_252 = "\t\t\t\t" + NL + "\t\t\t\tif(";
  protected final String TEXT_253 = ".length == 1 && (\"\\015\").equals(";
  protected final String TEXT_254 = "[0])){//empty line when row separator is '\\n'" + NL + "\t\t\t\t\t";
  protected final String TEXT_255 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_256 = ".";
  protected final String TEXT_257 = " = ";
  protected final String TEXT_258 = ";" + NL + "\t\t\t\t\t";
  protected final String TEXT_259 = NL + "\t\t\t\t}else{" + NL + "\t\t\t\t\t";
  protected final String TEXT_260 = NL + "\t\t\t\t\t\tString temp = \"\";" + NL + "\t\t\t\t\t";
  protected final String TEXT_261 = NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_262 = " < ";
  protected final String TEXT_263 = ".length){\t\t\t\t" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_264 = NL + "\t\t\t\t\t\t\t\t\ttemp = ";
  protected final String TEXT_265 = "[";
  protected final String TEXT_266 = "]";
  protected final String TEXT_267 = ";" + NL + "\t\t\t\t\t\t\t\t\tif(temp.length() > 0){" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_268 = ".";
  protected final String TEXT_269 = " = temp;" + NL + "\t\t\t\t\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_270 = ".";
  protected final String TEXT_271 = " = ";
  protected final String TEXT_272 = ";" + NL + "\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_273 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_274 = ".";
  protected final String TEXT_275 = " = ";
  protected final String TEXT_276 = "[";
  protected final String TEXT_277 = "]";
  protected final String TEXT_278 = ";" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_279 = NL + "\t\t\t\t\t\t\t\tif(";
  protected final String TEXT_280 = "[";
  protected final String TEXT_281 = "].length() > 0) {" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_282 = NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_283 = ".";
  protected final String TEXT_284 = " = ParserUtils.parseTo_Date(";
  protected final String TEXT_285 = "[";
  protected final String TEXT_286 = "]";
  protected final String TEXT_287 = ", ";
  protected final String TEXT_288 = ", false);" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_289 = NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_290 = ".";
  protected final String TEXT_291 = " = ParserUtils.parseTo_Date(";
  protected final String TEXT_292 = "[";
  protected final String TEXT_293 = "]";
  protected final String TEXT_294 = ", ";
  protected final String TEXT_295 = ");" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_296 = NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_297 = ".";
  protected final String TEXT_298 = " = ParserUtils.parseTo_";
  protected final String TEXT_299 = "(ParserUtils.parseTo_Number(";
  protected final String TEXT_300 = "[";
  protected final String TEXT_301 = "]";
  protected final String TEXT_302 = ", ";
  protected final String TEXT_303 = ", ";
  protected final String TEXT_304 = "));" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_305 = "\t\t\t\t\t\t\t" + NL + "\t\t    \t\t\t\t\t\t\t";
  protected final String TEXT_306 = ".";
  protected final String TEXT_307 = " = ";
  protected final String TEXT_308 = "[";
  protected final String TEXT_309 = "]";
  protected final String TEXT_310 = ".getBytes(";
  protected final String TEXT_311 = ");" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_312 = NL + "\t\t\t\t\t\t\t\t\t\tfor (int i = 0; i < ";
  protected final String TEXT_313 = ".getColumnCount(); i++) {" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_314 = ".clearColumnValues();" + NL + "\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t\tint fieldCount = ";
  protected final String TEXT_315 = ".length;\t" + NL + "\t\t\t\t\t\t\t\t\t\tfor (int i = 0; i < ";
  protected final String TEXT_316 = ".getColumnCount(); i++) {" + NL + "\t\t\t\t\t\t\t\t\t\t\tif ((";
  protected final String TEXT_317 = "-1+i) < fieldCount)" + NL + "\t\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_318 = ".addColumnValue(";
  protected final String TEXT_319 = "[";
  protected final String TEXT_320 = "-1+i]";
  protected final String TEXT_321 = ");" + NL + "\t\t\t\t\t\t\t\t\t\t\telse" + NL + "\t\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_322 = ".addColumnValue(\"\");" + NL + "\t\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_323 = ".";
  protected final String TEXT_324 = " = ";
  protected final String TEXT_325 = ";" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_326 = NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_327 = ".";
  protected final String TEXT_328 = " = ParserUtils.parseTo_";
  protected final String TEXT_329 = "(";
  protected final String TEXT_330 = "[";
  protected final String TEXT_331 = "]";
  protected final String TEXT_332 = ");" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_333 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_334 = ".";
  protected final String TEXT_335 = " = ";
  protected final String TEXT_336 = ";" + NL + "\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_337 = "\t" + NL + "\t\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_338 = ".";
  protected final String TEXT_339 = " = ";
  protected final String TEXT_340 = ";" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_341 = NL + "\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_342 = NL + " \t\t\t\t\tint filedsum = ";
  protected final String TEXT_343 = ".length;" + NL + " \t\t\t\t\tif(filedsum < ";
  protected final String TEXT_344 = "){" + NL + " \t\t\t\t\t\tthrow new Exception(\"Column(s) missing\");" + NL + " \t\t\t\t\t} else if(filedsum > ";
  protected final String TEXT_345 = ") {" + NL + " \t\t\t\t\t\tthrow new RuntimeException(\"Too many columns\");" + NL + " \t\t\t\t\t}     " + NL + "\t\t\t\t";
  protected final String TEXT_346 = NL + "    \t\t\t\t";
  protected final String TEXT_347 = ".";
  protected final String TEXT_348 = " = ";
  protected final String TEXT_349 = ".";
  protected final String TEXT_350 = ";" + NL + "\t\t\t\t";
  protected final String TEXT_351 = NL + "\t\t\t";
  protected final String TEXT_352 = NL + "\t\t\t\t\t\t\tboolean isFirstCheckDyn_";
  protected final String TEXT_353 = " = true;" + NL + "\t\t\t\t\t\t\troutines.system.Dynamic dynamic_";
  protected final String TEXT_354 = " = new routines.system.Dynamic();" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_355 = NL + "\t\t\t\tint nb_line_";
  protected final String TEXT_356 = " = 0;" + NL + "\t\t\t\tint footer_";
  protected final String TEXT_357 = " = ";
  protected final String TEXT_358 = ";" + NL + "\t\t\t\tint totalLine";
  protected final String TEXT_359 = " = 0;" + NL + "\t\t\t\tint limit";
  protected final String TEXT_360 = " = ";
  protected final String TEXT_361 = ";" + NL + "\t\t\t\tint lastLine";
  protected final String TEXT_362 = " = -1;\t" + NL + "\t\t\t\t" + NL + "\t\t\t\tchar fieldSeparator_";
  protected final String TEXT_363 = "[] = null;" + NL + "\t\t\t\t" + NL + "\t\t\t\t//support passing value (property: Field Separator) by 'context.fs' or 'globalMap.get(\"fs\")'. " + NL + "\t\t\t\tif ( ((String)";
  protected final String TEXT_364 = ").length() > 0 ){" + NL + "\t\t\t\t\tfieldSeparator_";
  protected final String TEXT_365 = " = ((String)";
  protected final String TEXT_366 = ").toCharArray();" + NL + "\t\t\t\t}else {\t\t\t" + NL + "\t\t\t\t\tthrow new IllegalArgumentException(\"Field Separator must be assigned a char.\"); " + NL + "\t\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\t\tchar rowSeparator_";
  protected final String TEXT_367 = "[] = null;" + NL + "\t\t\t" + NL + "\t\t\t\t//support passing value (property: Row Separator) by 'context.rs' or 'globalMap.get(\"rs\")'. " + NL + "\t\t\t\tif ( ((String)";
  protected final String TEXT_368 = ").length() > 0 ){" + NL + "\t\t\t\t\trowSeparator_";
  protected final String TEXT_369 = " = ((String)";
  protected final String TEXT_370 = ").toCharArray();" + NL + "\t\t\t\t}else {" + NL + "\t\t\t\t\tthrow new IllegalArgumentException(\"Row Separator must be assigned a char.\"); " + NL + "\t\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\t\tObject filename_";
  protected final String TEXT_371 = " = ";
  protected final String TEXT_372 = ";\t\t" + NL + "\t\t\t\tcom.csvreader.CsvReader csvReader";
  protected final String TEXT_373 = " = null;" + NL + "\t" + NL + "\t\t\t\ttry{" + NL + "\t\t\t\t\t";
  protected final String TEXT_374 = NL + "\t        \t\t\tjava.util.zip.ZipInputStream zis_";
  protected final String TEXT_375 = " = null;" + NL + "\t        \t\t\ttry {" + NL + "\t\t        \t\t\tif(filename_";
  protected final String TEXT_376 = " instanceof java.io.InputStream){" + NL + "\t\t        \t\t\t\tzis_";
  protected final String TEXT_377 = " = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream((java.io.InputStream)filename_";
  protected final String TEXT_378 = "));" + NL + "\t\t        \t\t\t}else{" + NL + "\t\t        \t\t\t\tzis_";
  protected final String TEXT_379 = " = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(String.valueOf(filename_";
  protected final String TEXT_380 = "))));" + NL + "\t\t        \t\t\t}" + NL + "\t        \t\t\t} catch(Exception e) {" + NL + "\t        \t\t\t\t";
  protected final String TEXT_381 = NL + "\t\t\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_382 = NL + "\t\t\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_383 = NL + "\t        \t\t\t}" + NL + "\t        \t\t\tjava.util.zip.ZipEntry entry_";
  protected final String TEXT_384 = " = null;" + NL + "\t\t        " + NL + "\t\t\t\t        while (true) {" + NL + "\t\t\t        \t\ttry {" + NL + "\t        \t\t\t\t\tentry_";
  protected final String TEXT_385 = " = zis_";
  protected final String TEXT_386 = ".getNextEntry();" + NL + "\t        \t\t\t\t} catch(Exception e) {" + NL + "\t        \t\t\t\t\t";
  protected final String TEXT_387 = NL + "\t\t\t\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_388 = NL + "\t\t\t\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t\t\t\t\tbreak;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_389 = NL + "\t        \t\t\t\t}" + NL + "\t        \t\t\t\tif(entry_";
  protected final String TEXT_390 = " == null) {" + NL + "\t\t\t\t\t\t\t\tbreak;" + NL + "\t\t\t\t\t\t\t}" + NL + "\t        \t\t\t\tif(entry_";
  protected final String TEXT_391 = ".isDirectory()){ //directory" + NL + "\t        \t\t\t\t\tcontinue;" + NL + "\t        \t\t\t\t}" + NL + "\t        \t\t\t\tString[] row";
  protected final String TEXT_392 = "=null;" + NL + "\t        \t\t\t\tint currentLine";
  protected final String TEXT_393 = " = 0;" + NL + "\t        \t\t\t\tint outputLine";
  protected final String TEXT_394 = " = 0;" + NL + "\t        \t\t\t\ttry {//TD110 begin" + NL + "\t        \t\t\t\t\tcsvReader";
  protected final String TEXT_395 = "=new com.csvreader.CsvReader(zis_";
  protected final String TEXT_396 = ", fieldSeparator_";
  protected final String TEXT_397 = "[0], java.nio.charset.Charset.forName(";
  protected final String TEXT_398 = "));" + NL + "\t\t\t\t\t";
  protected final String TEXT_399 = NL + "\t\t\t\t\t\tString[] row";
  protected final String TEXT_400 = "=null;" + NL + "\t\t\t\t\t\tint currentLine";
  protected final String TEXT_401 = " = 0;" + NL + "\t        \t\t\tint outputLine";
  protected final String TEXT_402 = " = 0;" + NL + "\t\t\t\t\t\ttry {//TD110 begin" + NL + "\t\t\t\t\t\t\tif(filename_";
  protected final String TEXT_403 = " instanceof java.io.InputStream){" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_404 = NL + "\t\t\t\t\t\t\t\tcsvReader";
  protected final String TEXT_405 = "=new com.csvreader.CsvReader((java.io.InputStream)filename_";
  protected final String TEXT_406 = ", fieldSeparator_";
  protected final String TEXT_407 = "[0], java.nio.charset.Charset.forName(";
  protected final String TEXT_408 = "));" + NL + "\t\t\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\t\t\tcsvReader";
  protected final String TEXT_409 = "=new com.csvreader.CsvReader(new java.io.BufferedReader(new java.io.InputStreamReader(" + NL + "\t\t                \t\tnew java.io.FileInputStream(String.valueOf(filename_";
  protected final String TEXT_410 = ")),";
  protected final String TEXT_411 = ")), fieldSeparator_";
  protected final String TEXT_412 = "[0]);" + NL + "\t\t        \t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_413 = NL + "\t\t\t\t\t" + NL + "\t\t\t\t\tcsvReader";
  protected final String TEXT_414 = ".setTrimWhitespace(false);" + NL + "\t\t\t\t\tif ( (rowSeparator_";
  protected final String TEXT_415 = "[0] != '\\n') && (rowSeparator_";
  protected final String TEXT_416 = "[0] != '\\r') )" + NL + "\t        \t\t\tcsvReader";
  protected final String TEXT_417 = ".setRecordDelimiter(rowSeparator_";
  protected final String TEXT_418 = "[0]);" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_419 = NL + "\t        \t\t\t\tcsvReader";
  protected final String TEXT_420 = ".setTextQualifier('";
  protected final String TEXT_421 = "');" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_422 = NL + "\t\t\t\t\t\t\tString textEnclosure1_";
  protected final String TEXT_423 = " = ";
  protected final String TEXT_424 = "; " + NL + "\t\t\t\t\t\t\tchar textEnclosure_";
  protected final String TEXT_425 = "[] = null;" + NL + "\t\t\t        " + NL + "\t    \t\t\t\t    if(((String)textEnclosure1_";
  protected final String TEXT_426 = ").length() > 0 ){ " + NL + "\t  \t\t\t\t\t\t\ttextEnclosure_";
  protected final String TEXT_427 = " = ((String)textEnclosure1_";
  protected final String TEXT_428 = ").toCharArray(); " + NL + "\t\t\t\t\t\t\t}else { " + NL + "\t            \t\t\t\tthrow new IllegalArgumentException(\"Text Enclosure must be assigned a char.\"); " + NL + "\t        \t\t\t\t}" + NL + "\t\t\t\t\t\t\tcsvReader";
  protected final String TEXT_429 = ".setTextQualifier(textEnclosure_";
  protected final String TEXT_430 = "[0]); " + NL + "\t\t\t\t\t\t";
  protected final String TEXT_431 = NL + "\t            \t\t\t\tcsvReader";
  protected final String TEXT_432 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_BACKSLASH);" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_433 = NL + "\t            \t\t\t\tcsvReader";
  protected final String TEXT_434 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_435 = NL + "\t            \t\t\t\t//?????doesn't work for other escapeChar" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_436 = " " + NL + "\t\t        \t\t\tString escapeChar1_";
  protected final String TEXT_437 = " = ";
  protected final String TEXT_438 = ";          " + NL + "\t\t        \t\t\tchar escapeChar_";
  protected final String TEXT_439 = "[] = null;" + NL + "\t\t\t        " + NL + "\t\t\t\t\t        if(((String)escapeChar1_";
  protected final String TEXT_440 = ").length() > 0 ){ " + NL + "\t\t\t\t\t        \tescapeChar_";
  protected final String TEXT_441 = " = ((String)escapeChar1_";
  protected final String TEXT_442 = ").toCharArray(); " + NL + "\t\t\t\t\t  \t\t}else { " + NL + "\t\t        \t\t\t    throw new IllegalArgumentException(\"Escape Char must be assigned a char.\"); " + NL + "\t\t        \t\t\t}" + NL + "\t\t       \t\t\t\tif(escapeChar_";
  protected final String TEXT_443 = "[0] == '\\\\'){" + NL + "\t\t       \t\t\t\t\tcsvReader";
  protected final String TEXT_444 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_BACKSLASH);" + NL + "\t\t       \t\t\t\t}else if(escapeChar_";
  protected final String TEXT_445 = "[0] ==";
  protected final String TEXT_446 = "'";
  protected final String TEXT_447 = "'";
  protected final String TEXT_448 = "textEnclosure_";
  protected final String TEXT_449 = "[0]";
  protected final String TEXT_450 = "){" + NL + "\t\t       \t\t\t\t\tcsvReader";
  protected final String TEXT_451 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);" + NL + "\t\t       \t\t\t\t}" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_452 = "      " + NL + "\t\t" + NL + "\t\t\t" + NL + "\t\t\t\t\t\tif(footer_";
  protected final String TEXT_453 = " > 0){" + NL + "\t\t\t\t\t\tfor(totalLine";
  protected final String TEXT_454 = "=0;totalLine";
  protected final String TEXT_455 = " < ";
  protected final String TEXT_456 = "; totalLine";
  protected final String TEXT_457 = "++){" + NL + "\t\t\t\t\t\t\tcsvReader";
  protected final String TEXT_458 = ".readRecord();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tcsvReader";
  protected final String TEXT_459 = ".setSkipEmptyRecords(";
  protected final String TEXT_460 = ");" + NL + "\t\t\t            while (csvReader";
  protected final String TEXT_461 = ".readRecord()) {" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_462 = NL + "\t\t\t\t\t\t\t\trow";
  protected final String TEXT_463 = "=csvReader";
  protected final String TEXT_464 = ".getValues();" + NL + "\t\t\t\t\t\t\t\tif(!(row";
  protected final String TEXT_465 = ".length == 1 && (\"\\015\").equals(row";
  protected final String TEXT_466 = "[0]))){//empty line when row separator is '\\n'" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_467 = NL + "\t                " + NL + "\t                \t\ttotalLine";
  protected final String TEXT_468 = "++;" + NL + "\t                " + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_469 = NL + "\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_470 = NL + "\t                " + NL + "\t\t\t            }" + NL + "\t            \t\tint lastLineTemp";
  protected final String TEXT_471 = " = totalLine";
  protected final String TEXT_472 = " - footer_";
  protected final String TEXT_473 = "   < 0? 0 : totalLine";
  protected final String TEXT_474 = " - footer_";
  protected final String TEXT_475 = " ;" + NL + "\t            \t\tif(lastLine";
  protected final String TEXT_476 = " > 0){" + NL + "\t                \t\tlastLine";
  protected final String TEXT_477 = " = lastLine";
  protected final String TEXT_478 = " < lastLineTemp";
  protected final String TEXT_479 = " ? lastLine";
  protected final String TEXT_480 = " : lastLineTemp";
  protected final String TEXT_481 = "; " + NL + "\t            \t\t}else {" + NL + "\t                \t\tlastLine";
  protected final String TEXT_482 = " = lastLineTemp";
  protected final String TEXT_483 = ";" + NL + "\t            \t\t}" + NL + "\t         " + NL + "\t\t\t          \tcsvReader";
  protected final String TEXT_484 = ".close();" + NL + "\t\t\t\t        if(filename_";
  protected final String TEXT_485 = " instanceof java.io.InputStream){" + NL + "\t\t\t\t \t\t\tcsvReader";
  protected final String TEXT_486 = "=new com.csvreader.CsvReader((java.io.InputStream)filename_";
  protected final String TEXT_487 = ", fieldSeparator_";
  protected final String TEXT_488 = "[0], java.nio.charset.Charset.forName(";
  protected final String TEXT_489 = "));" + NL + "\t\t        \t\t}else{" + NL + "\t\t\t\t \t\t\tcsvReader";
  protected final String TEXT_490 = "=new com.csvreader.CsvReader(new java.io.BufferedReader(new java.io.InputStreamReader(" + NL + "\t\t\t\t          \tnew java.io.FileInputStream(String.valueOf(filename_";
  protected final String TEXT_491 = ")),";
  protected final String TEXT_492 = ")), fieldSeparator_";
  protected final String TEXT_493 = "[0]);" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tcsvReader";
  protected final String TEXT_494 = ".setTrimWhitespace(false);" + NL + "\t\t\t\t\t\tif ( (rowSeparator_";
  protected final String TEXT_495 = "[0] != '\\n') && (rowSeparator_";
  protected final String TEXT_496 = "[0] != '\\r') )\t" + NL + "\t        \t\t\t\tcsvReader";
  protected final String TEXT_497 = ".setRecordDelimiter(rowSeparator_";
  protected final String TEXT_498 = "[0]);" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_499 = NL + "\t\t\t\t\t\t\tcsvReader";
  protected final String TEXT_500 = ".setTextQualifier('";
  protected final String TEXT_501 = "');" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_502 = NL + "\t\t\t\t\t\t\tcsvReader";
  protected final String TEXT_503 = ".setTextQualifier(textEnclosure_";
  protected final String TEXT_504 = "[0]);" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_505 = NL + "\t        \t\t\t\tcsvReader";
  protected final String TEXT_506 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_BACKSLASH);" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_507 = NL + "\t        \t\t\t\tcsvReader";
  protected final String TEXT_508 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_509 = NL + "\t        \t\t\t\t//?????doesn't work for other escapeChar" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_510 = " " + NL + "\t       \t\t\t\t\tif(escapeChar_";
  protected final String TEXT_511 = "[0] == '\\\\'){" + NL + "\t       \t\t\t\t\t\tcsvReader";
  protected final String TEXT_512 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_BACKSLASH);" + NL + "\t       \t\t\t\t\t}else if(escapeChar_";
  protected final String TEXT_513 = "[0] == ";
  protected final String TEXT_514 = "'";
  protected final String TEXT_515 = "'";
  protected final String TEXT_516 = "textEnclosure_";
  protected final String TEXT_517 = "[0]";
  protected final String TEXT_518 = "){" + NL + "\t       \t\t\t\t\t\tcsvReader";
  protected final String TEXT_519 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);" + NL + "\t       \t\t\t\t\t}" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_520 = "  " + NL + "\t        \t\t}" + NL + "\t        " + NL + "\t\t\t        if(limit";
  protected final String TEXT_521 = " != 0){" + NL + "\t\t\t        \tfor(currentLine";
  protected final String TEXT_522 = "=0;currentLine";
  protected final String TEXT_523 = " < ";
  protected final String TEXT_524 = ";currentLine";
  protected final String TEXT_525 = "++){" + NL + "\t\t\t        \t\tcsvReader";
  protected final String TEXT_526 = ".readRecord();" + NL + "\t\t\t        \t}" + NL + "\t\t\t        }" + NL + "\t\t\t        csvReader";
  protected final String TEXT_527 = ".setSkipEmptyRecords(";
  protected final String TEXT_528 = ");" + NL + "\t        " + NL + "\t    \t\t} catch(Exception e) {" + NL + "\t\t\t\t\t";
  protected final String TEXT_529 = NL + "\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t";
  protected final String TEXT_530 = NL + "\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t";
  protected final String TEXT_531 = NL + "\t    \t\t}//TD110 end" + NL + "\t        " + NL + "\t        \twhile ( limit";
  protected final String TEXT_532 = " != 0 && csvReader";
  protected final String TEXT_533 = "!=null && csvReader";
  protected final String TEXT_534 = ".readRecord() ) { " + NL + "\t        " + NL + "\t\t        \trow";
  protected final String TEXT_535 = "=csvReader";
  protected final String TEXT_536 = ".getValues();" + NL + "\t        \t" + NL + "\t\t\t\t\t";
  protected final String TEXT_537 = NL + "\t        \t\t\tif(row";
  protected final String TEXT_538 = ".length == 1 && (\"\\015\").equals(row";
  protected final String TEXT_539 = "[0])){//empty line when row separator is '\\n'" + NL + "\t        \t\t\t\tcontinue;" + NL + "\t        \t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_540 = NL + "\t        \t" + NL + "\t        \t" + NL + "\t        \t\tcurrentLine";
  protected final String TEXT_541 = "++;" + NL + "\t            " + NL + "\t\t            if(lastLine";
  protected final String TEXT_542 = " > -1 && currentLine";
  protected final String TEXT_543 = " > lastLine";
  protected final String TEXT_544 = ") {" + NL + "\t\t                break;" + NL + "\t    \t        }" + NL + "\t        \t    outputLine";
  protected final String TEXT_545 = "++;" + NL + "\t            \tif (limit";
  protected final String TEXT_546 = " > 0 && outputLine";
  protected final String TEXT_547 = " > limit";
  protected final String TEXT_548 = ") {" + NL + "\t                \tbreak;" + NL + "\t            \t}  " + NL + "\t                                                                      " + NL + "\t\t\t\t\t";
  protected final String TEXT_549 = NL + "\t    \t\t\t\t\t\t\t";
  protected final String TEXT_550 = " = null;\t\t\t" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_551 = NL + "\t\t\t\t\t\t\t\tboolean whetherReject_";
  protected final String TEXT_552 = " = false;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_553 = " = new ";
  protected final String TEXT_554 = "Struct();" + NL + "\t\t\t\t\t\t\t\ttry {\t\t\t" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_555 = " " + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_556 = " = null; " + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_557 = NL + "\t    \t\t\t\t\t\t} catch (Exception e) {" + NL + "\t\t\t\t\t\t\t        whetherReject_";
  protected final String TEXT_558 = " = true;" + NL + "        \t\t\t\t\t\t\t";
  protected final String TEXT_559 = NL + "            \t\t\t\t\t\t\tthrow(e);" + NL + "            \t\t\t\t\t\t";
  protected final String TEXT_560 = NL + "\t\t\t\t\t\t                    ";
  protected final String TEXT_561 = " = new ";
  protected final String TEXT_562 = "Struct();" + NL + "                \t\t\t\t\t\t\t";
  protected final String TEXT_563 = NL + "                \t\t\t\t\t\t\t";
  protected final String TEXT_564 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_565 = ";" + NL + "                \t\t\t\t\t\t\t";
  protected final String TEXT_566 = " = null;" + NL + "                \t\t\t\t\t\t";
  protected final String TEXT_567 = NL + "                \t\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "                \t\t\t\t\t\t\t";
  protected final String TEXT_568 = " = null;" + NL + "                \t\t\t\t\t\t";
  protected final String TEXT_569 = NL + "            \t\t\t\t\t\t\t\t";
  protected final String TEXT_570 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_571 = ";" + NL + "            \t\t\t\t\t\t\t";
  protected final String TEXT_572 = NL + "\t    \t\t\t\t\t\t}" + NL + "\t" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_573 = NL + "\t\t\t\t\t\t\t\t\t\tif(!whetherReject_";
  protected final String TEXT_574 = ") { " + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_575 = "      " + NL + "\t             \t\t\t\t\tif(";
  protected final String TEXT_576 = " == null){ " + NL + "\t            \t \t\t\t\t\t";
  protected final String TEXT_577 = " = new ";
  protected final String TEXT_578 = "Struct();" + NL + "\t             \t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_579 = " " + NL + "\t\t\t\t\t\t\t\t} " + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_580 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    
	class DefaultRowUtil {
		String cid = "";
	
		boolean useV2C = false;
		IMetadataTable V2CMetadata;
		String V2CTargetConnName;
		String V2CSourceValueClass;
		String V2CSourceValueName;
		
		public void prepareValueToConn(IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName){
			this.V2CMetadata = metadata;
			this.V2CTargetConnName = targetConnName;
			this.V2CSourceValueClass = sourceValueClass;
			this.V2CSourceValueName = sourceValueName;
			this.useV2C = true;
		}
		
		public void codeForValueToConn(INode node, IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName){
		}
		
		public void callValueToConn(String sourceValueName, String targetConnName){
		
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(targetConnName);
    stringBuffer.append(TEXT_6);
    
		}
		
		boolean useV2CWithD = false;
		IMetadataTable V2CWithDMetadata;
		String V2CWithDTargetConnName;
		String V2CWithDSourceValueClass;
		String V2CWithDSourceValueName;
		String V2CWithDDynamicName;
		public void prepareValueToConnWithD(IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName, String dynamicName){
			this.V2CWithDMetadata = metadata;
			this.V2CWithDTargetConnName = targetConnName;
			this.V2CWithDSourceValueClass = sourceValueClass;
			this.V2CWithDSourceValueName = sourceValueName;
			this.V2CWithDDynamicName = dynamicName;
			this.useV2CWithD = true;
		}
		
		public void codeForValueToConnWithD(INode node, IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName, String dynamicName){
		}
		
		public void callValueToConnWithD(String sourceValueName, String targetConnName, String dynamicName){
		
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(targetConnName);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_11);
    
		}
		
		IMetadataTable C2CMetadata;
		String C2CTargetConnName;
		String C2CSourceConnName;
		boolean useC2C = false;
		public void prepareConnToConn(IMetadataTable metadata, String sourceConnName, String targetConnName){
			this.C2CMetadata = metadata;
			this.C2CTargetConnName = targetConnName;
			this.C2CSourceConnName = sourceConnName;
			this.useC2C = true;
		}
		
		public void codeForConnToConn(INode node, IMetadataTable metadata, String sourceConnName, String targetConnName){
		}
		
		public void callConnToConn(String sourceConnName, String targetConnName){
		
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(sourceConnName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(targetConnName);
    stringBuffer.append(TEXT_15);
    
		}
		
		
		public void generateClass(INode node){
			cid = node.getUniqueName();
			
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
				if(useV2C){
				
    stringBuffer.append(TEXT_18);
    stringBuffer.append(V2CSourceValueClass);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(V2CSourceValueName);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(V2CTargetConnName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(V2CTargetConnName);
    stringBuffer.append(TEXT_22);
    codeForValueToConn(node, V2CMetadata, V2CSourceValueClass, V2CSourceValueName, V2CTargetConnName);
    stringBuffer.append(TEXT_23);
    	
				}
				if(useV2CWithD){
				
    stringBuffer.append(TEXT_24);
    stringBuffer.append(V2CWithDSourceValueClass);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(V2CWithDSourceValueName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(V2CWithDTargetConnName);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(V2CWithDTargetConnName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(V2CWithDDynamicName);
    stringBuffer.append(TEXT_29);
    codeForValueToConnWithD(node, V2CWithDMetadata, V2CWithDSourceValueClass, V2CWithDSourceValueName, V2CWithDTargetConnName, V2CWithDDynamicName);
    stringBuffer.append(TEXT_30);
    		
				}
				if(useC2C){
				
    stringBuffer.append(TEXT_31);
    stringBuffer.append(C2CSourceConnName);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(C2CSourceConnName);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(C2CTargetConnName);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(C2CTargetConnName);
    stringBuffer.append(TEXT_35);
    codeForConnToConn(node, C2CMetadata, C2CSourceConnName, C2CTargetConnName);
    stringBuffer.append(TEXT_36);
    
				}
				
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    
		}
	}
	
    stringBuffer.append(TEXT_41);
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    final String cid = node.getUniqueName();	
	
	String projectName = codeGenArgument.getCurrentProjectName();
	String jobName = codeGenArgument.getJobName();
	String jobVersion = codeGenArgument.getJobVersion();
	
	String tempDir = ElementParameterParser.getValue(node, "__TEMP_DIR__");
	
	String vcid = "";

	String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
	if(destination!=null && !"".equals(destination)){
		vcid = destination;
	}
	
		
	class GenerateCode{
		public int colLen = 0; // the length of the column in the input schema
		public void generateDynamicSchemaCode(boolean isCsv){
		
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    
				if(isCsv){
				
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    
				}else{
				
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    
				}
				
    stringBuffer.append(TEXT_50);
    stringBuffer.append(colLen );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    if(isCsv){
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    }else{
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    }
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    
		}
		public void checkFooterAndRandom(String footer,String random){
		
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(footer );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(random );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    
		}
		
		public void checkFooter(String footer){
		
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(footer );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    	
		}
	}
	GenerateCode generateCode = new GenerateCode();

	if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		class RowUtil extends DefaultRowUtil{
			public void codeForValueToConn(INode node, IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName){
				codeForValueToConnWithD(node, metadata, sourceValueClass, sourceValueName, targetConnName, null);
			}
			public void codeForValueToConnWithD(INode node, IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName, String dynamicName){
				
				String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
				
				List<Map<String, String>> trimSelects = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__TRIMSELECT__");
				String isTrimAllStr = ElementParameterParser.getValue(node,"__TRIMALL__");
				boolean isTrimAll = (isTrimAllStr!=null&&!("").equals(isTrimAllStr))?("true").equals(isTrimAllStr):true;
				
				String checkDateStr = ElementParameterParser.getValue(node,"__CHECK_DATE__");
				boolean checkDate = (checkDateStr!=null&&!("").equals(checkDateStr))?("true").equals(checkDateStr):false;
				
				String checkNumStr = ElementParameterParser.getValue(node, "__CHECK_FIELDS_NUM__");
				boolean checkNum = (checkNumStr!=null&&!("").equals(checkNumStr))?("true").equals(checkNumStr):false; 
				
				String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
				boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
				String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
				String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
				
				List<IMetadataColumn> listColumns = metadata.getListColumns();
				int sizeListColumns = listColumns.size();
				boolean noStringTypeExist = false;
				
				for (int valueN=0; valueN<sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
					}else{
						noStringTypeExist = true;
						break;
					}
				}
				boolean hasStringDefault = false;
				for (int valueM=0; valueM<sizeListColumns; valueM++) {
					IMetadataColumn column = listColumns.get(valueM);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
						if(hasStringDefault==false && column.getDefault()!=null && column.getDefault().length() > 0 ){
							hasStringDefault = true;
							break;
						}
					}
				}
				if(noStringTypeExist || hasStringDefault){
				
    stringBuffer.append(TEXT_84);
    
				}
				for (int valueN=0; valueN<sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
						String defaultValue = column.getDefault();
						if(defaultValue!=null && defaultValue.length()>0){
						
    stringBuffer.append(TEXT_85);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_87);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(valueN).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_93);
    
						}else{
						
    stringBuffer.append(TEXT_94);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_98);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(valueN).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_99);
    
						}
					}else if(column.getTalendType().equals("id_Dynamic")){
					
    stringBuffer.append(TEXT_100);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(sizeListColumns);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(sizeListColumns);
    stringBuffer.append(TEXT_107);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(valueN).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_112);
    
					}else{
					
    stringBuffer.append(TEXT_113);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_115);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(valueN).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_116);
    
							if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
							} else if(javaType == JavaTypesManager.BYTE_ARRAY){ 
							
    stringBuffer.append(TEXT_117);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_120);
    
							}else if(javaType == JavaTypesManager.DATE) { 
								if(checkNum || checkDate){
								
    stringBuffer.append(TEXT_121);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_123);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_124);
    
								}else{
								
    stringBuffer.append(TEXT_125);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_127);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_128);
    
								}
							}else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 
							
    stringBuffer.append(TEXT_129);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_131);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_132);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_133);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_134);
    
							} else { 
							
    stringBuffer.append(TEXT_135);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_137);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_138);
    
							}
							
    stringBuffer.append(TEXT_139);
    
							String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
							if(defaultValue == null) {
							
    stringBuffer.append(TEXT_140);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_142);
    
							} else {
							
    stringBuffer.append(TEXT_143);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_146);
    
							}
							
    stringBuffer.append(TEXT_147);
    
					}
				}
				
    stringBuffer.append(TEXT_148);
    if(checkNum) {
    stringBuffer.append(TEXT_149);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(metadata.getListColumns().size() );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(metadata.getListColumns().size() );
    stringBuffer.append(TEXT_152);
    
				}
			}
			
			public void codeForConnToConn(INode node, IMetadataTable metadata, String sourceConnName, String targetConnName){
    			for(IMetadataColumn column : metadata.getListColumns()) {
    			
    stringBuffer.append(TEXT_153);
    stringBuffer.append(targetConnName);
    stringBuffer.append(TEXT_154);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_155);
    stringBuffer.append(sourceConnName);
    stringBuffer.append(TEXT_156);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_157);
    
				}
				
    stringBuffer.append(TEXT_158);
    
			}
		}//class defined end

		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
			IMetadataTable metadata = metadatas.get(0);
			if (metadata!=null) {
				String filename = ElementParameterParser.getValue(node,"__FILENAME__");
				
				if(!("".equals(vcid))) {
					filename = "\"/"+filename.substring(1, filename.length()-1)+vcid+"_"+projectName+"_"+jobName+"_"+jobVersion+"\"";
					filename = tempDir+"+"+filename;
				}
				
		    	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
		    	String header = ElementParameterParser.getValue(node, "__HEADER__");
		    	if(("").equals(header)){
		    		header="0";
		    	}
		    	String limit = ElementParameterParser.getValue(node, "__LIMIT__");
				if(("").equals(limit)){
					limit = "-1";
				}
		    	String footer = ElementParameterParser.getValue(node, "__FOOTER__");
				boolean uncompress = ("true").equals(ElementParameterParser.getValue(node,"__UNCOMPRESS__"));
		    	if(("").equals(footer) || uncompress){
		    		footer="0";
		    	}
		    	String random = "-1";
		    	String ran = ElementParameterParser.getValue(node, "__RANDOM__");
		    	if(("true").equals(ran)){
		    		random = ElementParameterParser.getValue(node, "__NB_RANDOM__");
		    		if(("").equals(random)){
		    			random="0";
		    		}
		    	}
		    	if(uncompress){
		    		random="-1";
		    	}
				
		    	String fieldSeparator = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
		    	String rowSeparator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
		    	String removeEmptyRowFlag =  ElementParameterParser.getValue(node, "__REMOVE_EMPTY_ROW__");
		    	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
				boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false; 
				
				String splitRecordStr = ElementParameterParser.getValue(node, "__SPLITRECORD__");
				boolean splitRecord = (splitRecordStr!=null&&!("").equals(splitRecordStr))?("true").equals(splitRecordStr):false;
				
				//find main & reject conns;
				List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
			    String rejectConnName = "";
			    List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
			    if(rejectConns != null && rejectConns.size() > 0) {
			        IConnection rejectConn = rejectConns.get(0);
			        rejectConnName = rejectConn.getName();
			    }
			    List<IMetadataColumn> rejectColumnList = null;
			    IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
			    if(metadataTable != null) {
			        rejectColumnList = metadataTable.getListColumns();      
			    }
			    String firstConnName = "";
				if (conns!=null) {
					if (conns.size()>0) {
						IConnection conn = conns.get(0);
						firstConnName = conn.getName();		
					}
				}			    
				
				RowUtil rowUtil = new RowUtil(); 
				boolean hasDynamic = metadata.isDynamicSchema();
				if (conns!=null) {
				    if (conns.size()>0) {
						if(hasDynamic){
							rowUtil.prepareValueToConnWithD(metadata, "org.talend.fileprocess.FileInputDelimited", "fid", firstConnName, "dynamic");
							
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    
						}else{
							rowUtil.prepareValueToConn(metadata, "org.talend.fileprocess.FileInputDelimited", "fid", firstConnName);
						}
						if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {
							rowUtil.prepareConnToConn(metadata, firstConnName, rejectConnName);
						}
						rowUtil.generateClass(node);
					}
				}
				
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    
					if(uncompress){
					
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(filename );
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
     if(dieOnError) {
    stringBuffer.append(TEXT_174);
     } else { 
    stringBuffer.append(TEXT_175);
     } 
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
     if(dieOnError) {
    stringBuffer.append(TEXT_180);
     } else { 
    stringBuffer.append(TEXT_181);
     } 
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_189);
    stringBuffer.append(removeEmptyRowFlag );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(header );
    stringBuffer.append(hasDynamic?"-1":"");
    stringBuffer.append(TEXT_191);
    stringBuffer.append(footer );
    stringBuffer.append(TEXT_192);
    stringBuffer.append(limit );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(random );
    stringBuffer.append(TEXT_194);
    stringBuffer.append( splitRecord);
    stringBuffer.append(TEXT_195);
     if(dieOnError) {
    stringBuffer.append(TEXT_196);
     } else { 
    stringBuffer.append(TEXT_197);
     } 
    stringBuffer.append(TEXT_198);
    
					}else{
					
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_202);
    
									generateCode.checkFooterAndRandom(footer,random);
							
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_204);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_207);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(removeEmptyRowFlag );
    stringBuffer.append(TEXT_209);
    stringBuffer.append(header );
    stringBuffer.append(hasDynamic?"-1":"");
    stringBuffer.append(TEXT_210);
    stringBuffer.append(footer );
    stringBuffer.append(TEXT_211);
    stringBuffer.append(limit );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(random );
    stringBuffer.append(TEXT_213);
    stringBuffer.append( splitRecord);
    stringBuffer.append(TEXT_214);
     if(dieOnError) {
    stringBuffer.append(TEXT_215);
     } else { 
    stringBuffer.append(TEXT_216);
     } 
    stringBuffer.append(TEXT_217);
    
					}
					
    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_220);
    
				    	if (conns!=null) {
				    		if (conns.size()>0) {
				    			for (int i=0;i<conns.size();i++) {
				    				IConnection connTemp = conns.get(i);
				    				if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
									
    stringBuffer.append(TEXT_221);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_222);
    
				    				}
				    			}
				    	
								IConnection conn = conns.get(0);
								if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
								
    stringBuffer.append(TEXT_223);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_226);
    
										List<IMetadataColumn> listColumns = metadata.getListColumns();
										int sizeListColumns = listColumns.size();
										if(hasDynamic){// generate the dynamic schema code
											generateCode.colLen = sizeListColumns;
											generateCode.generateDynamicSchemaCode(false); //false: delimited mode
											rowUtil.callValueToConnWithD("fid_"+cid, firstConnName, "dynamic_"+cid);
										}else{
											rowUtil.callValueToConn("fid_"+cid, firstConnName);
										}
										
			
										
										if(rejectConnName.equals(firstConnName)) {
										
    stringBuffer.append(TEXT_227);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_228);
    
										}
										
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_230);
    
			        					if (dieOnError) {
			            				
    stringBuffer.append(TEXT_231);
    
			        					} else {
			            					if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {
							                
    stringBuffer.append(TEXT_232);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_234);
    rowUtil.callConnToConn(firstConnName, rejectConnName);
    stringBuffer.append(TEXT_235);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_236);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_237);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_238);
    
			            					} else if(("").equals(rejectConnName)){
			                				
    stringBuffer.append(TEXT_239);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_240);
    
			            					} else if(rejectConnName.equals(firstConnName)){
    stringBuffer.append(TEXT_241);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_242);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_243);
    
									}
			        					} 
			        					
    stringBuffer.append(TEXT_244);
    
								}
							}
							if (conns.size()>0) {
								boolean isFirstEnter = true;
								for (int i=0;i<conns.size();i++) {
									IConnection conn = conns.get(i);
									if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getName().compareTo(rejectConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
									
										if(isFirstEnter) {
										
    stringBuffer.append(TEXT_245);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_246);
    isFirstEnter = false; 
										}
										
    stringBuffer.append(TEXT_247);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_248);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_249);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_250);
    
						    	 		rowUtil.callConnToConn(firstConnName, conn.getName());
									}
								}
								 
								if(!isFirstEnter) {
								
    stringBuffer.append(TEXT_251);
    
								}
								
							}
				  		}
			}
		}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}else{//the following is the tFileInputCSV component
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		class RowUtil extends DefaultRowUtil{
			public void codeForValueToConn(INode node, IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName){
				codeForValueToConnWithD(node, metadata, sourceValueClass, sourceValueName, targetConnName, null);
			}
			public void codeForValueToConnWithD(INode node, IMetadataTable metadata, String sourceValueClass, String sourceValueName, String targetConnName, String dynamicName){
				
				String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
				
				List<Map<String, String>> trimSelects = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__TRIMSELECT__");
				String isTrimAllStr = ElementParameterParser.getValue(node,"__TRIMALL__");
				boolean isTrimAll = (isTrimAllStr!=null&&!("").equals(isTrimAllStr))?("true").equals(isTrimAllStr):true;
				
				String checkDateStr = ElementParameterParser.getValue(node,"__CHECK_DATE__");
				boolean checkDate = (checkDateStr!=null&&!("").equals(checkDateStr))?("true").equals(checkDateStr):false;
				
				String checkNumStr = ElementParameterParser.getValue(node, "__CHECK_FIELDS_NUM__");
				boolean checkNum = (checkNumStr!=null&&!("").equals(checkNumStr))?("true").equals(checkNumStr):false; 
				
				String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
				boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
				String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
				String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
				
				List<IMetadataColumn> columns=metadata.getListColumns();
				int columnSize = columns.size();
				
    stringBuffer.append(TEXT_252);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_253);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_254);
    
					for (IMetadataColumn column1: metadata.getListColumns()) {
					
    stringBuffer.append(TEXT_255);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_256);
    stringBuffer.append(column1.getLabel() );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaIdType(column1.getTalendType(), column1.isNullable()));
    stringBuffer.append(TEXT_258);
    
					}
					
    stringBuffer.append(TEXT_259);
    
					boolean hasStringDefault = false;
					for (int valueM=0; valueM<columnSize; valueM++) {
						IMetadataColumn column = columns.get(valueM);
						JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
						if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
							if(hasStringDefault==false && column.getDefault()!=null && column.getDefault().length() > 0 ){
								hasStringDefault = true;
								break;
							}
						}
					}
					
					if(hasStringDefault==true){
					
    stringBuffer.append(TEXT_260);
    
					}
					for (int i=0;i<columnSize;i++) {
						IMetadataColumn column=columns.get(i);
						String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
						JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
						String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
						
    stringBuffer.append(TEXT_261);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_262);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_263);
    
							if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
								String defaultValue = column.getDefault();
								if(defaultValue!=null && defaultValue.length()>0){
								
    stringBuffer.append(TEXT_264);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_265);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_266);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_267);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_268);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_269);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_270);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_271);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_272);
    
								}else{
								
    stringBuffer.append(TEXT_273);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_275);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_276);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_277);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_278);
    
								}
							} else {
							
    stringBuffer.append(TEXT_279);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_280);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_281);
    
									if(javaType == JavaTypesManager.DATE) {
										if(checkNum || checkDate){
										
    stringBuffer.append(TEXT_282);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_283);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_284);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_285);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_286);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_287);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_288);
    
										}else{
										
    stringBuffer.append(TEXT_289);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_290);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_291);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_292);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_293);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_294);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_295);
    
										}
									} else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 
									
    stringBuffer.append(TEXT_296);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_297);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_298);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_299);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_300);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_301);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_302);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_303);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_304);
    
									}else if(javaType == JavaTypesManager.BYTE_ARRAY){ 
									
    stringBuffer.append(TEXT_305);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_308);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_309);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_310);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_311);
    
									}else if(column.getTalendType().equals("id_Dynamic")){
									
    stringBuffer.append(TEXT_312);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_313);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_314);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_315);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_316);
    stringBuffer.append(columnSize);
    stringBuffer.append(TEXT_317);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_318);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_319);
    stringBuffer.append(columnSize);
    stringBuffer.append(TEXT_320);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_321);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_322);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_323);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_324);
    stringBuffer.append(dynamicName);
    stringBuffer.append(TEXT_325);
    
									}else {
									
    stringBuffer.append(TEXT_326);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_327);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_328);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_329);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_330);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_331);
    stringBuffer.append((isTrimAll || (!trimSelects.isEmpty() && ("true").equals(trimSelects.get(i).get("TRIM"))))?".trim()":"" );
    stringBuffer.append(TEXT_332);
    
									}
									
    stringBuffer.append(TEXT_333);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_334);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_335);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault()));
    stringBuffer.append(TEXT_336);
    					
							}
							
    stringBuffer.append(TEXT_337);
    stringBuffer.append(targetConnName );
    stringBuffer.append(TEXT_338);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_339);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault()) );
    stringBuffer.append(TEXT_340);
    
					}
					
    stringBuffer.append(TEXT_341);
    if(checkNum) {
    stringBuffer.append(TEXT_342);
    stringBuffer.append(sourceValueName);
    stringBuffer.append(TEXT_343);
    stringBuffer.append(metadata.getListColumns().size() );
    stringBuffer.append(TEXT_344);
    stringBuffer.append(metadata.getListColumns().size() );
    stringBuffer.append(TEXT_345);
    
				}
			}
			
			public void codeForConnToConn(INode node, IMetadataTable metadata, String sourceConnName, String targetConnName){
    			for(IMetadataColumn column : metadata.getListColumns()) {
    			
    stringBuffer.append(TEXT_346);
    stringBuffer.append(targetConnName);
    stringBuffer.append(TEXT_347);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_348);
    stringBuffer.append(sourceConnName);
    stringBuffer.append(TEXT_349);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_350);
    
				}
				
    stringBuffer.append(TEXT_351);
    
			}
		}//class defined end
		
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
		    IMetadataTable metadata = metadatas.get(0);
		    if (metadata!=null) {
		    
		        String filename = ElementParameterParser.getValueWithUIFieldKey(node,"__FILENAME__", "FILENAME");
		    	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
		    	String header = ElementParameterParser.getValue(node, "__HEADER__");
		    	String footer = ElementParameterParser.getValue(node, "__FOOTER__");
		    	String limit = ElementParameterParser.getValue(node, "__LIMIT__");
		    	if(("").equals(limit)){
		    		limit="-1";
		    	}
		    	String delim1 = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");     	
		    	String rowSeparator1 = ElementParameterParser.getValue(node, "__CSVROWSEPARATOR__");
		    	
		    	
		    	String escapeChar1 = ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
		    	
		    	if(("").equals(escapeChar1)){
		    		escapeChar1 = "\"\"";
		    	}
		    	String escapeChar = escapeChar1.substring(1,escapeChar1.length()-1);
		    	if(("'").equals(escapeChar)){
		    		escapeChar = "\\'";
		    	}
		    	String textEnclosure1 = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
		    	if(("").equals(textEnclosure1)){
		    		textEnclosure1 = "\"\"";
		    	}
		    	String textEnclosure = textEnclosure1.substring(1,textEnclosure1.length()-1);
		    	if ("".equals(textEnclosure)) 
		    		textEnclosure = "\0";
		    	if(("'").equals(textEnclosure)){
		    		textEnclosure = "\\'";
		    	}
		    	
		    	String removeEmptyRow = ElementParameterParser.getValue(node, "__REMOVE_EMPTY_ROW__");
		    	
		    	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
				boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;
				
				boolean uncompress = ("true").equals(ElementParameterParser.getValue(node,"__UNCOMPRESS__"));
				if(uncompress){
					footer = "0";
				}
				
				List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
			    String rejectConnName = "";
    			List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
    			if(rejectConns != null && rejectConns.size() > 0) {
        			IConnection rejectConn = rejectConns.get(0);
        			rejectConnName = rejectConn.getName();
   				}
    			List<IMetadataColumn> rejectColumnList = null;
    			IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
    			if(metadataTable != null) {
        			rejectColumnList = metadataTable.getListColumns();      
    			}
    			String firstConnName = "";
				if (conns!=null) {
					if (conns.size()>0) {
						IConnection conn = conns.get(0);
						firstConnName = conn.getName();		
					}
				}
				
				RowUtil rowUtil = new RowUtil(); 
				boolean hasDynamic = metadata.isDynamicSchema();
				if (conns!=null) {
				    if (conns.size()>0) {
						if(hasDynamic){
							rowUtil.prepareValueToConnWithD(metadata, "String[]", "row", firstConnName, "dynamic");
							
    stringBuffer.append(TEXT_352);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_353);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_354);
    
						}else{
							rowUtil.prepareValueToConn(metadata, "String[]", "row", firstConnName);
						}
						if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {
							rowUtil.prepareConnToConn(metadata, firstConnName, rejectConnName);
						}
						rowUtil.generateClass(node);
					}
				}	
				
    stringBuffer.append(TEXT_355);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_356);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_357);
    stringBuffer.append( footer);
    stringBuffer.append(TEXT_358);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_359);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_360);
    stringBuffer.append( limit );
    stringBuffer.append(TEXT_361);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_362);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_363);
    stringBuffer.append(delim1);
    stringBuffer.append(TEXT_364);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_365);
    stringBuffer.append(delim1);
    stringBuffer.append(TEXT_366);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_367);
    stringBuffer.append(rowSeparator1);
    stringBuffer.append(TEXT_368);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_369);
    stringBuffer.append(rowSeparator1);
    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_371);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_372);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_373);
    
					if(uncompress){
					
    stringBuffer.append(TEXT_374);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_375);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_376);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_377);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_378);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_379);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_380);
     if(dieOnError) {
    stringBuffer.append(TEXT_381);
     } else { 
    stringBuffer.append(TEXT_382);
     } 
    stringBuffer.append(TEXT_383);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_384);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_385);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_386);
     if(dieOnError) {
    stringBuffer.append(TEXT_387);
     } else { 
    stringBuffer.append(TEXT_388);
     } 
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_395);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_396);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_397);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_398);
    
					}else{
					
    stringBuffer.append(TEXT_399);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_400);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_401);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_402);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_403);
    
								generateCode.checkFooter(footer);
							
    stringBuffer.append(TEXT_404);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_405);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_406);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_407);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_408);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_409);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_410);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_411);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_412);
    
					}
					
    stringBuffer.append(TEXT_413);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_414);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_415);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_416);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_417);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_418);
    
						if(("").equals(textEnclosure1) || textEnclosure1.startsWith("\"")){//normal situation
						
    stringBuffer.append(TEXT_419);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_420);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_421);
    
						}else{ //context and global variables
						
    stringBuffer.append(TEXT_422);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_423);
    stringBuffer.append(textEnclosure1 );
    stringBuffer.append(TEXT_424);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_425);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_426);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_427);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_428);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_429);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_430);
    
						}
					
						if(("").equals(escapeChar1) || escapeChar1.startsWith("\"")){//normal situation
	        				if(("\\\\").equals(escapeChar)){
	        				
    stringBuffer.append(TEXT_431);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_432);
    
	        				}else if(escapeChar.equals(textEnclosure)){
	        				
    stringBuffer.append(TEXT_433);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_434);
    
	        				}else{
	        				
    stringBuffer.append(TEXT_435);
    
	        				}
	        			}else{//context and global variables
	 					
    stringBuffer.append(TEXT_436);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_437);
    stringBuffer.append(escapeChar1 );
    stringBuffer.append(TEXT_438);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_439);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_440);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_441);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_442);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_443);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_444);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_445);
    if(("").equals(textEnclosure1) || textEnclosure1.startsWith("\"")){
    stringBuffer.append(TEXT_446);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_447);
    }else{
    stringBuffer.append(TEXT_448);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_449);
    }
    stringBuffer.append(TEXT_450);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_451);
    
						}
						
    stringBuffer.append(TEXT_452);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_453);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_454);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_455);
    stringBuffer.append( header );
    stringBuffer.append(TEXT_456);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_457);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_458);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_459);
    stringBuffer.append(removeEmptyRow );
    stringBuffer.append(TEXT_460);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_461);
    
						   	if(("true").equals(removeEmptyRow)){
						   	
    stringBuffer.append(TEXT_462);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_463);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_464);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_465);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_466);
    
							}
							
    stringBuffer.append(TEXT_467);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_468);
    
							if(("true").equals(removeEmptyRow)){
							
    stringBuffer.append(TEXT_469);
    
							}
							
    stringBuffer.append(TEXT_470);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_471);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_472);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_473);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_474);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_475);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_476);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_477);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_478);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_479);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_480);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_481);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_482);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_483);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_484);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_485);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_486);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_487);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_488);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_489);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_490);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_491);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_492);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_493);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_494);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_495);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_496);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_497);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_498);
    
						if(("").equals(textEnclosure1) || textEnclosure1.startsWith("\"")){//normal situation
						
    stringBuffer.append(TEXT_499);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_500);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_501);
    
						}else{
						
    stringBuffer.append(TEXT_502);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_503);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_504);
    
						}
						if(("").equals(escapeChar1) || escapeChar1.startsWith("\"")){//normal situation
	        				if(("\\\\").equals(escapeChar)){
	        				
    stringBuffer.append(TEXT_505);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_506);
    
	        				}else if(escapeChar.equals(textEnclosure)){
	        				
    stringBuffer.append(TEXT_507);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_508);
    
	        				}else{
	        				
    stringBuffer.append(TEXT_509);
    
	        				}
	        			}else{//context and global variables
						
    stringBuffer.append(TEXT_510);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_511);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_512);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_513);
    if(("").equals(textEnclosure1) || textEnclosure1.startsWith("\"")){
    stringBuffer.append(TEXT_514);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_515);
    }else{
    stringBuffer.append(TEXT_516);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_517);
    }
    stringBuffer.append(TEXT_518);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_519);
    
	        			}
	        			
    stringBuffer.append(TEXT_520);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_521);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_522);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_523);
    stringBuffer.append( header );
    stringBuffer.append(hasDynamic?"-1":"");
    stringBuffer.append(TEXT_524);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_525);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_526);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_527);
    stringBuffer.append(removeEmptyRow );
    stringBuffer.append(TEXT_528);
     if(dieOnError) {
    stringBuffer.append(TEXT_529);
     } else { 
    stringBuffer.append(TEXT_530);
     } 
    stringBuffer.append(TEXT_531);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_532);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_533);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_534);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_535);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_536);
    
	   				if(("true").equals(removeEmptyRow)){
	   				
    stringBuffer.append(TEXT_537);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_538);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_539);
    
					}
					
    stringBuffer.append(TEXT_540);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_541);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_542);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_543);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_544);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_545);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_546);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_547);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_548);
    
			    	if (conns!=null) {
	    				if (conns.size()>0) {
	    					for (int i=0;i<conns.size();i++) {
	    						IConnection connTemp = conns.get(i);
	    						if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
								
    stringBuffer.append(TEXT_549);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_550);
    
	    						}
	    					}
	    					
							IConnection conn = conns.get(0);
							if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
							
    stringBuffer.append(TEXT_551);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_552);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_553);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_554);
    
									List<IMetadataColumn> columns=metadata.getListColumns();
									int columnSize = columns.size();
									if(hasDynamic){// generate the dynamic schema code
										generateCode.colLen = columnSize;
										generateCode.generateDynamicSchemaCode(true); //true: CSV mode
										rowUtil.callValueToConnWithD("row"+cid, firstConnName, "dynamic_"+cid);
									}else{
										rowUtil.callValueToConn("row"+cid, firstConnName);
									}
										
	
									if(rejectConnName.equals(firstConnName)) {
									
    stringBuffer.append(TEXT_555);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_556);
    
									}
									
    stringBuffer.append(TEXT_557);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_558);
    
        							if (dieOnError) {
            						
    stringBuffer.append(TEXT_559);
    
        							} else {
            							if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {
						                
    stringBuffer.append(TEXT_560);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_561);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_562);
    
                							rowUtil.callConnToConn(firstConnName, rejectConnName);
                							
    stringBuffer.append(TEXT_563);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_564);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_565);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_566);
    
            							} else if(("").equals(rejectConnName)){
                						
    stringBuffer.append(TEXT_567);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_568);
    	
            							} else if(rejectConnName.equals(firstConnName)){
            							
    stringBuffer.append(TEXT_569);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_570);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_571);
    
            							}
        							} 
        							
    stringBuffer.append(TEXT_572);
    			
							}
						}
						if (conns.size()>0) {	
							boolean isFirstEnter = true;
							for (int i=0;i<conns.size();i++) {
								IConnection conn = conns.get(i);
								if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getName().compareTo(rejectConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
									if(isFirstEnter) {
									
    stringBuffer.append(TEXT_573);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_574);
    isFirstEnter = false; 
									}
									
    stringBuffer.append(TEXT_575);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_576);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_577);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_578);
    
        							rowUtil.callConnToConn(firstConnName, conn.getName());
								}
							}
							if(!isFirstEnter) {
							
    stringBuffer.append(TEXT_579);
    
							}
						}
					}
			}
		}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  	}
	
    stringBuffer.append(TEXT_580);
    return stringBuffer.toString();
  }
}
