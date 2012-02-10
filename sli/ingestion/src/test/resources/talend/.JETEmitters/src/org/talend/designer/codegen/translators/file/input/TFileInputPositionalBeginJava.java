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
import org.talend.core.model.utils.TalendTextUtils;
import java.util.List;
import java.util.Map;

public class TFileInputPositionalBeginJava
{
  protected static String nl;
  public static synchronized TFileInputPositionalBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputPositionalBeginJava result = new TFileInputPositionalBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "int footer_";
  protected final String TEXT_4 = "  = ";
  protected final String TEXT_5 = ";" + NL + "int nb_limit_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL;
  protected final String TEXT_8 = NL + "class Arrays_";
  protected final String TEXT_9 = "{" + NL + "    public byte[] copyOfRange(byte[] original, int from, int to) {" + NL + "        int newLength = to - from;" + NL + "        if (newLength < 0)" + NL + "            throw new IllegalArgumentException(from + \" > \" + to);" + NL + "        byte[] copy = new byte[newLength];" + NL + "        System.arraycopy(original, from, copy, 0," + NL + "                         Math.min(original.length - from, newLength));" + NL + "        return copy;" + NL + "    }" + NL + "}" + NL + "Arrays_";
  protected final String TEXT_10 = " arrays_";
  protected final String TEXT_11 = " = new Arrays_";
  protected final String TEXT_12 = "();";
  protected final String TEXT_13 = NL + NL + NL + "class PositionalUtil_";
  protected final String TEXT_14 = "{";
  protected final String TEXT_15 = NL + "                  void setValue_";
  protected final String TEXT_16 = "(";
  protected final String TEXT_17 = "Struct ";
  protected final String TEXT_18 = ",int[] begins_";
  protected final String TEXT_19 = ",int[] ends_";
  protected final String TEXT_20 = ",int rowLen_";
  protected final String TEXT_21 = ",";
  protected final String TEXT_22 = "byte[] byteArray_";
  protected final String TEXT_23 = ",Arrays_";
  protected final String TEXT_24 = " arrays_";
  protected final String TEXT_25 = ",";
  protected final String TEXT_26 = "String column_";
  protected final String TEXT_27 = ",String row_";
  protected final String TEXT_28 = ")throws Exception {             ";
  protected final String TEXT_29 = NL + "    if(begins_";
  protected final String TEXT_30 = "[";
  protected final String TEXT_31 = "] < rowLen_";
  protected final String TEXT_32 = "){";
  protected final String TEXT_33 = NL + "\t\tbyteArray_";
  protected final String TEXT_34 = "=arrays_";
  protected final String TEXT_35 = ".copyOfRange(row_";
  protected final String TEXT_36 = ".getBytes(";
  protected final String TEXT_37 = "),begins_";
  protected final String TEXT_38 = "[";
  protected final String TEXT_39 = "],rowLen_";
  protected final String TEXT_40 = ");" + NL + "    \tcolumn_";
  protected final String TEXT_41 = " = TalendString.talendTrim(new String(byteArray_";
  protected final String TEXT_42 = ",";
  protected final String TEXT_43 = "), ";
  protected final String TEXT_44 = ", ";
  protected final String TEXT_45 = ");";
  protected final String TEXT_46 = NL + "\t\tcolumn_";
  protected final String TEXT_47 = " = TalendString.talendTrim(row_";
  protected final String TEXT_48 = ".substring(begins_";
  protected final String TEXT_49 = "[";
  protected final String TEXT_50 = "]), ";
  protected final String TEXT_51 = ", ";
  protected final String TEXT_52 = ");";
  protected final String TEXT_53 = NL + "    }else{" + NL + "    \tcolumn_";
  protected final String TEXT_54 = " = \"\";" + NL + "    }";
  protected final String TEXT_55 = NL + "\tif(begins_";
  protected final String TEXT_56 = "[";
  protected final String TEXT_57 = "] < rowLen_";
  protected final String TEXT_58 = "){" + NL + "        if(ends_";
  protected final String TEXT_59 = "[";
  protected final String TEXT_60 = "] <= rowLen_";
  protected final String TEXT_61 = "){" + NL + "        \tbyteArray_";
  protected final String TEXT_62 = " = arrays_";
  protected final String TEXT_63 = ".copyOfRange(row_";
  protected final String TEXT_64 = ".getBytes(";
  protected final String TEXT_65 = "),begins_";
  protected final String TEXT_66 = "[";
  protected final String TEXT_67 = "],ends_";
  protected final String TEXT_68 = "[";
  protected final String TEXT_69 = "]);" + NL + "        }else{" + NL + "        \tbyteArray_";
  protected final String TEXT_70 = " = arrays_";
  protected final String TEXT_71 = ".copyOfRange(row_";
  protected final String TEXT_72 = ".getBytes(";
  protected final String TEXT_73 = "),begins_";
  protected final String TEXT_74 = "[";
  protected final String TEXT_75 = "],rowLen_";
  protected final String TEXT_76 = ");" + NL + "\t\t}" + NL + "\t\tcolumn_";
  protected final String TEXT_77 = " = TalendString.talendTrim(new String(byteArray_";
  protected final String TEXT_78 = ",";
  protected final String TEXT_79 = "), ";
  protected final String TEXT_80 = ", ";
  protected final String TEXT_81 = ");" + NL + "    }else{" + NL + "    \tcolumn_";
  protected final String TEXT_82 = " = \"\";" + NL + "    }";
  protected final String TEXT_83 = NL + "\tif(begins_";
  protected final String TEXT_84 = "[";
  protected final String TEXT_85 = "] < rowLen_";
  protected final String TEXT_86 = "){" + NL + "        if(ends_";
  protected final String TEXT_87 = "[";
  protected final String TEXT_88 = "] <= rowLen_";
  protected final String TEXT_89 = "){" + NL + "        \tcolumn_";
  protected final String TEXT_90 = " = TalendString.talendTrim(row_";
  protected final String TEXT_91 = ".substring(begins_";
  protected final String TEXT_92 = "[";
  protected final String TEXT_93 = "], ends_";
  protected final String TEXT_94 = "[";
  protected final String TEXT_95 = "]), ";
  protected final String TEXT_96 = ", ";
  protected final String TEXT_97 = ");" + NL + "        }else{" + NL + "        \tcolumn_";
  protected final String TEXT_98 = " = TalendString.talendTrim(row_";
  protected final String TEXT_99 = ".substring(begins_";
  protected final String TEXT_100 = "[";
  protected final String TEXT_101 = "]), ";
  protected final String TEXT_102 = ", ";
  protected final String TEXT_103 = ");" + NL + "\t\t}" + NL + "    }else{" + NL + "    \tcolumn_";
  protected final String TEXT_104 = " = \"\";" + NL + "    }";
  protected final String TEXT_105 = NL + "\tcolumn_";
  protected final String TEXT_106 = " = column_";
  protected final String TEXT_107 = ".trim();";
  protected final String TEXT_108 = NL + "\t";
  protected final String TEXT_109 = ".";
  protected final String TEXT_110 = " = column_";
  protected final String TEXT_111 = ";";
  protected final String TEXT_112 = NL + "\tif(column_";
  protected final String TEXT_113 = ".length() > 0) {";
  protected final String TEXT_114 = NL + "\t\t";
  protected final String TEXT_115 = ".";
  protected final String TEXT_116 = " = ParserUtils.parseTo_Date(column_";
  protected final String TEXT_117 = ", ";
  protected final String TEXT_118 = ",false);";
  protected final String TEXT_119 = NL + "\t\t";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = " = ParserUtils.parseTo_Date(column_";
  protected final String TEXT_122 = ", ";
  protected final String TEXT_123 = ");";
  protected final String TEXT_124 = NL + "\t\t";
  protected final String TEXT_125 = ".";
  protected final String TEXT_126 = " = ParserUtils.parseTo_";
  protected final String TEXT_127 = "(ParserUtils.parseTo_Number(column_";
  protected final String TEXT_128 = ", ";
  protected final String TEXT_129 = ", ";
  protected final String TEXT_130 = "));";
  protected final String TEXT_131 = NL + "\t\t";
  protected final String TEXT_132 = ".";
  protected final String TEXT_133 = " = column_";
  protected final String TEXT_134 = ".getBytes(";
  protected final String TEXT_135 = ");";
  protected final String TEXT_136 = NL + "\t\t";
  protected final String TEXT_137 = ".";
  protected final String TEXT_138 = " = ParserUtils.parseTo_";
  protected final String TEXT_139 = "(column_";
  protected final String TEXT_140 = ");";
  protected final String TEXT_141 = NL + "    }else{" + NL + "    \t";
  protected final String TEXT_142 = ".";
  protected final String TEXT_143 = " = ";
  protected final String TEXT_144 = ";" + NL + "    }";
  protected final String TEXT_145 = NL + "                    }";
  protected final String TEXT_146 = NL + "                }";
  protected final String TEXT_147 = NL + "                     void setValue_";
  protected final String TEXT_148 = "(";
  protected final String TEXT_149 = "Struct ";
  protected final String TEXT_150 = ",String[] columnValue";
  protected final String TEXT_151 = ")throws Exception{";
  protected final String TEXT_152 = NL + "\t                    ";
  protected final String TEXT_153 = ".";
  protected final String TEXT_154 = " = columnValue";
  protected final String TEXT_155 = "[";
  protected final String TEXT_156 = "];";
  protected final String TEXT_157 = NL + "\t                    if(columnValue";
  protected final String TEXT_158 = "[";
  protected final String TEXT_159 = "].length() > 0) {";
  protected final String TEXT_160 = "\t" + NL + "\t\t                            ";
  protected final String TEXT_161 = ".";
  protected final String TEXT_162 = " = ParserUtils.parseTo_Date(columnValue";
  protected final String TEXT_163 = "[";
  protected final String TEXT_164 = "], ";
  protected final String TEXT_165 = ",false);";
  protected final String TEXT_166 = NL + "\t\t                            ";
  protected final String TEXT_167 = ".";
  protected final String TEXT_168 = " = ParserUtils.parseTo_Date(columnValue";
  protected final String TEXT_169 = "[";
  protected final String TEXT_170 = "], ";
  protected final String TEXT_171 = ");\t";
  protected final String TEXT_172 = NL + "\t\t                        ";
  protected final String TEXT_173 = ".";
  protected final String TEXT_174 = " = ParserUtils.parseTo_";
  protected final String TEXT_175 = "(ParserUtils.parseTo_Number(columnValue";
  protected final String TEXT_176 = "[";
  protected final String TEXT_177 = "], ";
  protected final String TEXT_178 = ", ";
  protected final String TEXT_179 = "));";
  protected final String TEXT_180 = "\t" + NL + "\t\t                        ";
  protected final String TEXT_181 = ".";
  protected final String TEXT_182 = " = columnValue";
  protected final String TEXT_183 = "[";
  protected final String TEXT_184 = "].getBytes(";
  protected final String TEXT_185 = ");";
  protected final String TEXT_186 = "\t" + NL + "\t\t                        ";
  protected final String TEXT_187 = ".";
  protected final String TEXT_188 = " = ParserUtils.parseTo_";
  protected final String TEXT_189 = "(columnValue";
  protected final String TEXT_190 = "[";
  protected final String TEXT_191 = "].trim());";
  protected final String TEXT_192 = NL + "                        }else{" + NL + "        \t                ";
  protected final String TEXT_193 = ".";
  protected final String TEXT_194 = " = ";
  protected final String TEXT_195 = ";" + NL + "                        }";
  protected final String TEXT_196 = NL;
  protected final String TEXT_197 = NL + "                     }";
  protected final String TEXT_198 = NL;
  protected final String TEXT_199 = NL + "                     }";
  protected final String TEXT_200 = NL;
  protected final String TEXT_201 = NL + "}" + NL + "" + NL + "PositionalUtil_";
  protected final String TEXT_202 = " positionalUtil_";
  protected final String TEXT_203 = "=new PositionalUtil_";
  protected final String TEXT_204 = "();" + NL;
  protected final String TEXT_205 = NL + NL + "int[] sizes_";
  protected final String TEXT_206 = " = new int[";
  protected final String TEXT_207 = "];" + NL + "int[] begins_";
  protected final String TEXT_208 = " = new int[";
  protected final String TEXT_209 = "];" + NL + "int[] ends_";
  protected final String TEXT_210 = " = new int[";
  protected final String TEXT_211 = "];";
  protected final String TEXT_212 = NL + "sizes_";
  protected final String TEXT_213 = "[";
  protected final String TEXT_214 = "] = ";
  protected final String TEXT_215 = ";";
  protected final String TEXT_216 = NL + "sizes_";
  protected final String TEXT_217 = "[";
  protected final String TEXT_218 = "] = ";
  protected final String TEXT_219 = ";\t\t\t\t";
  protected final String TEXT_220 = NL + "begins_";
  protected final String TEXT_221 = "[";
  protected final String TEXT_222 = "] = 0;" + NL + "ends_";
  protected final String TEXT_223 = "[";
  protected final String TEXT_224 = "] = sizes_";
  protected final String TEXT_225 = "[";
  protected final String TEXT_226 = "];";
  protected final String TEXT_227 = NL + "begins_";
  protected final String TEXT_228 = "[";
  protected final String TEXT_229 = "] = begins_";
  protected final String TEXT_230 = "[";
  protected final String TEXT_231 = "] + sizes_";
  protected final String TEXT_232 = "[";
  protected final String TEXT_233 = "];" + NL + "ends_";
  protected final String TEXT_234 = "[";
  protected final String TEXT_235 = "] = -1;";
  protected final String TEXT_236 = NL + "begins_";
  protected final String TEXT_237 = "[";
  protected final String TEXT_238 = "] = begins_";
  protected final String TEXT_239 = "[";
  protected final String TEXT_240 = "] + sizes_";
  protected final String TEXT_241 = "[";
  protected final String TEXT_242 = "];" + NL + "ends_";
  protected final String TEXT_243 = "[";
  protected final String TEXT_244 = "] = ends_";
  protected final String TEXT_245 = "[";
  protected final String TEXT_246 = "] + sizes_";
  protected final String TEXT_247 = "[";
  protected final String TEXT_248 = "];";
  protected final String TEXT_249 = NL + "Object filename_";
  protected final String TEXT_250 = " = ";
  protected final String TEXT_251 = ";" + NL + "java.io.BufferedReader in_";
  protected final String TEXT_252 = " = null;" + NL + "org.talend.fileprocess.delimited.RowParser reader_";
  protected final String TEXT_253 = " = null; ";
  protected final String TEXT_254 = NL + "java.util.zip.ZipInputStream zis_";
  protected final String TEXT_255 = " = null;" + NL + "try {" + NL + "\tif(filename_";
  protected final String TEXT_256 = " instanceof java.io.InputStream){" + NL + "\t\tzis_";
  protected final String TEXT_257 = " = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream((java.io.InputStream)filename_";
  protected final String TEXT_258 = "));" + NL + "\t}else{" + NL + "\t\tzis_";
  protected final String TEXT_259 = " = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(String.valueOf(filename_";
  protected final String TEXT_260 = "))));" + NL + "\t}" + NL + "} catch(Exception e) {" + NL + "\t";
  protected final String TEXT_261 = NL + "\tthrow e;" + NL + "\t";
  protected final String TEXT_262 = NL + "\tSystem.err.println(e.getMessage());" + NL + "\t";
  protected final String TEXT_263 = NL + "}" + NL + "java.util.zip.ZipEntry entry_";
  protected final String TEXT_264 = " = null;" + NL + "while (true) {" + NL + "\ttry {" + NL + "\t\tentry_";
  protected final String TEXT_265 = " = zis_";
  protected final String TEXT_266 = ".getNextEntry();" + NL + "\t} catch(Exception e) {" + NL + "\t\t";
  protected final String TEXT_267 = NL + "\t\tthrow e;" + NL + "\t\t";
  protected final String TEXT_268 = NL + "\t\tSystem.err.println(e.getMessage());" + NL + "\t\tbreak;" + NL + "\t\t";
  protected final String TEXT_269 = NL + "\t}" + NL + "\tif(entry_";
  protected final String TEXT_270 = " == null) {" + NL + "\t\tbreak;" + NL + "\t}" + NL + "\tif(entry_";
  protected final String TEXT_271 = ".isDirectory()){ //directory" + NL + "\t\tcontinue;" + NL + "\t}" + NL + "\tString row_";
  protected final String TEXT_272 = " = null;" + NL + "\tint rowLen_";
  protected final String TEXT_273 = " = 0;" + NL + "\t";
  protected final String TEXT_274 = NL + "\tbyte[] byteArray_";
  protected final String TEXT_275 = " = new byte[0];" + NL + "\t";
  protected final String TEXT_276 = NL + "\tString column_";
  protected final String TEXT_277 = " = null;" + NL + "\t";
  protected final String TEXT_278 = NL + "\tbyte[][] byteArray_";
  protected final String TEXT_279 = " = new byte[";
  protected final String TEXT_280 = "][];" + NL + "\t";
  protected final String TEXT_281 = NL + "\tString[] columnValue";
  protected final String TEXT_282 = "=new String[";
  protected final String TEXT_283 = "];" + NL + "\t";
  protected final String TEXT_284 = NL + "\ttry {//TD110 begin" + NL + "\t\tin_";
  protected final String TEXT_285 = " = new java.io.BufferedReader(new java.io.InputStreamReader(zis_";
  protected final String TEXT_286 = ", ";
  protected final String TEXT_287 = "));";
  protected final String TEXT_288 = NL + "String row_";
  protected final String TEXT_289 = " = null;" + NL + "int rowLen_";
  protected final String TEXT_290 = " = 0;";
  protected final String TEXT_291 = NL + "byte[] byteArray_";
  protected final String TEXT_292 = " = new byte[0];";
  protected final String TEXT_293 = NL + "String column_";
  protected final String TEXT_294 = " = null;";
  protected final String TEXT_295 = NL + "byte[][] byteArray_";
  protected final String TEXT_296 = " = new byte[";
  protected final String TEXT_297 = "][];";
  protected final String TEXT_298 = NL + "String[] columnValue";
  protected final String TEXT_299 = "=new String[";
  protected final String TEXT_300 = "];";
  protected final String TEXT_301 = " " + NL + "try {//TD110 begin" + NL + "\tif(filename_";
  protected final String TEXT_302 = " instanceof java.io.InputStream){" + NL + "\t\tin_";
  protected final String TEXT_303 = " = " + NL + "\t\t\t\tnew java.io.BufferedReader(new java.io.InputStreamReader((java.io.InputStream)filename_";
  protected final String TEXT_304 = ", ";
  protected final String TEXT_305 = "));" + NL + "\t}else{" + NL + "\t\tin_";
  protected final String TEXT_306 = " = " + NL + "\t\t\tnew java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(String.valueOf(filename_";
  protected final String TEXT_307 = ")), ";
  protected final String TEXT_308 = "));" + NL + "\t}" + NL;
  protected final String TEXT_309 = NL + "int rowLength_";
  protected final String TEXT_310 = " = 0;";
  protected final String TEXT_311 = NL + "rowLength_";
  protected final String TEXT_312 = " += sizes_";
  protected final String TEXT_313 = "[";
  protected final String TEXT_314 = "];";
  protected final String TEXT_315 = NL + "rowLength_";
  protected final String TEXT_316 = " += ";
  protected final String TEXT_317 = ";";
  protected final String TEXT_318 = NL + "reader_";
  protected final String TEXT_319 = " = new org.talend.fileprocess.delimited.RowParser(in_";
  protected final String TEXT_320 = ", rowLength_";
  protected final String TEXT_321 = ");";
  protected final String TEXT_322 = NL + "reader_";
  protected final String TEXT_323 = " = new org.talend.fileprocess.delimited.RowParser(in_";
  protected final String TEXT_324 = ", ";
  protected final String TEXT_325 = ", ";
  protected final String TEXT_326 = ");";
  protected final String TEXT_327 = NL + "reader_";
  protected final String TEXT_328 = ".setSafetySwitch(";
  protected final String TEXT_329 = ");" + NL + "reader_";
  protected final String TEXT_330 = ".skipHeaders(";
  protected final String TEXT_331 = ");" + NL + "if(footer_";
  protected final String TEXT_332 = " > 0){" + NL + "\tint available_";
  protected final String TEXT_333 = " = (int)reader_";
  protected final String TEXT_334 = ".getAvailableRowCount(footer_";
  protected final String TEXT_335 = ");" + NL + "\treader_";
  protected final String TEXT_336 = ".close();" + NL + "\tif(filename_";
  protected final String TEXT_337 = " instanceof java.io.InputStream){" + NL + "\t\tin_";
  protected final String TEXT_338 = " = new java.io.BufferedReader(new java.io.InputStreamReader((java.io.InputStream)filename_";
  protected final String TEXT_339 = ", ";
  protected final String TEXT_340 = "));" + NL + "\t}else{" + NL + "\t\tin_";
  protected final String TEXT_341 = " = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(String.valueOf(filename_";
  protected final String TEXT_342 = ")), ";
  protected final String TEXT_343 = "));" + NL + "\t}";
  protected final String TEXT_344 = NL + "\treader_";
  protected final String TEXT_345 = " = new org.talend.fileprocess.delimited.RowParser(in_";
  protected final String TEXT_346 = ", rowLength_";
  protected final String TEXT_347 = ");";
  protected final String TEXT_348 = NL + "\treader_";
  protected final String TEXT_349 = " = new org.talend.fileprocess.delimited.RowParser(in_";
  protected final String TEXT_350 = ", ";
  protected final String TEXT_351 = ", ";
  protected final String TEXT_352 = ");";
  protected final String TEXT_353 = "\t" + NL + "\treader_";
  protected final String TEXT_354 = ".skipHeaders(";
  protected final String TEXT_355 = ");" + NL + "\t" + NL + "\tif ( nb_limit_";
  protected final String TEXT_356 = " >= 0 ){" + NL + "\t\tnb_limit_";
  protected final String TEXT_357 = " = ( nb_limit_";
  protected final String TEXT_358 = " > available_";
  protected final String TEXT_359 = ") ? available_";
  protected final String TEXT_360 = " : nb_limit_";
  protected final String TEXT_361 = ";" + NL + "\t}else{" + NL + "\t\tnb_limit_";
  protected final String TEXT_362 = " = available_";
  protected final String TEXT_363 = ";" + NL + "\t}" + NL + "}" + NL + "" + NL + "} catch(Exception e) {//TD110 end" + NL + "\t";
  protected final String TEXT_364 = NL + "\tthrow e;" + NL + "\t";
  protected final String TEXT_365 = NL + "\tSystem.err.println(e.getMessage());" + NL + "\t";
  protected final String TEXT_366 = NL + "}" + NL;
  protected final String TEXT_367 = NL + "\t\tString arrFieldSeparator";
  protected final String TEXT_368 = "[] = \"";
  protected final String TEXT_369 = "\".split(\",\");";
  protected final String TEXT_370 = "\t\t" + NL + "\t\tString arrFieldSeparator";
  protected final String TEXT_371 = "[] = ";
  protected final String TEXT_372 = ".split(\",\");";
  protected final String TEXT_373 = " " + NL + "" + NL + "Integer star_";
  protected final String TEXT_374 = " = Integer.valueOf(-1);\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "Integer[] tmpAry_";
  protected final String TEXT_375 = " = new Integer[arrFieldSeparator";
  protected final String TEXT_376 = ".length];\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "for (int i = 0; i < arrFieldSeparator";
  protected final String TEXT_377 = ".length; i++ ){\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "\tif ((\"*\").equals(arrFieldSeparator";
  protected final String TEXT_378 = "[i])) {\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "\t\ttmpAry_";
  protected final String TEXT_379 = "[i] = star_";
  protected final String TEXT_380 = ";\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "\t}else{\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "\t\ttmpAry_";
  protected final String TEXT_381 = "[i] = Integer.parseInt(arrFieldSeparator";
  protected final String TEXT_382 = "[i]);\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "\t}\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + NL + "}\t";
  protected final String TEXT_383 = NL + "while (nb_limit_";
  protected final String TEXT_384 = " != 0 && reader_";
  protected final String TEXT_385 = "!=null && reader_";
  protected final String TEXT_386 = ".readRecord()) {" + NL + "\trow_";
  protected final String TEXT_387 = " = reader_";
  protected final String TEXT_388 = ".getRowRecord();";
  protected final String TEXT_389 = NL + "\trowLen_";
  protected final String TEXT_390 = " = row_";
  protected final String TEXT_391 = ".getBytes(";
  protected final String TEXT_392 = ").length;";
  protected final String TEXT_393 = NL + "\trowLen_";
  protected final String TEXT_394 = " = row_";
  protected final String TEXT_395 = ".length();";
  protected final String TEXT_396 = NL + "    \t\t";
  protected final String TEXT_397 = " = null;\t\t\t";
  protected final String TEXT_398 = NL + "\t\t\t" + NL + "\t\t\tboolean whetherReject_";
  protected final String TEXT_399 = " = false;" + NL + "\t\t\t";
  protected final String TEXT_400 = " = new ";
  protected final String TEXT_401 = "Struct();" + NL + "\t\t\ttry {" + NL + "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_402 = NL + "                  positionalUtil_";
  protected final String TEXT_403 = ".setValue_";
  protected final String TEXT_404 = "(";
  protected final String TEXT_405 = ",begins_";
  protected final String TEXT_406 = ",ends_";
  protected final String TEXT_407 = ",rowLen_";
  protected final String TEXT_408 = ",";
  protected final String TEXT_409 = "byteArray_";
  protected final String TEXT_410 = ",arrays_";
  protected final String TEXT_411 = ",";
  protected final String TEXT_412 = "column_";
  protected final String TEXT_413 = ",row_";
  protected final String TEXT_414 = ");          ";
  protected final String TEXT_415 = NL + "\tint substringBegin";
  protected final String TEXT_416 = "=0,substringEnd";
  protected final String TEXT_417 = "=0;" + NL + "\tint[]begin_end_";
  protected final String TEXT_418 = "=new int[2];" + NL + "\t";
  protected final String TEXT_419 = "\t" + NL + "\t\t\t\tcolumnValue";
  protected final String TEXT_420 = "[";
  protected final String TEXT_421 = "]=\"\";";
  protected final String TEXT_422 = NL + "\t\t    if(substringBegin";
  protected final String TEXT_423 = " >= rowLen_";
  protected final String TEXT_424 = "){" + NL + "\t\t    \tcolumnValue";
  protected final String TEXT_425 = "[";
  protected final String TEXT_426 = "]= \"\";" + NL + "    \t\t}else{";
  protected final String TEXT_427 = NL + "        \t\t\t \tsubstringEnd";
  protected final String TEXT_428 = "=rowLen_";
  protected final String TEXT_429 = ";";
  protected final String TEXT_430 = NL + "        \t\t\t\tsubstringEnd";
  protected final String TEXT_431 = " = substringEnd";
  protected final String TEXT_432 = " + ";
  protected final String TEXT_433 = ";" + NL + "        " + NL + "\t\t\t\t        if(substringEnd";
  protected final String TEXT_434 = " > rowLen_";
  protected final String TEXT_435 = "){" + NL + "\t\t\t\t        \tsubstringEnd";
  protected final String TEXT_436 = " = rowLen_";
  protected final String TEXT_437 = ";" + NL + "\t\t\t\t    \t}";
  protected final String TEXT_438 = NL + "\t\t\t\t\tbyteArray_";
  protected final String TEXT_439 = "[";
  protected final String TEXT_440 = "] = arrays_";
  protected final String TEXT_441 = ".copyOfRange(row_";
  protected final String TEXT_442 = ".getBytes(";
  protected final String TEXT_443 = "),substringBegin";
  protected final String TEXT_444 = ", substringEnd";
  protected final String TEXT_445 = ");" + NL + "\t\t\t\t\tcolumnValue";
  protected final String TEXT_446 = "[";
  protected final String TEXT_447 = "] = new String(byteArray_";
  protected final String TEXT_448 = "[";
  protected final String TEXT_449 = "],";
  protected final String TEXT_450 = ");";
  protected final String TEXT_451 = NL + "        \t\t\tcolumnValue";
  protected final String TEXT_452 = "[";
  protected final String TEXT_453 = "] = row_";
  protected final String TEXT_454 = ".substring(substringBegin";
  protected final String TEXT_455 = ", substringEnd";
  protected final String TEXT_456 = ");";
  protected final String TEXT_457 = NL + "\t\t\t\t\t\tcolumnValue";
  protected final String TEXT_458 = "[";
  protected final String TEXT_459 = "] = columnValue";
  protected final String TEXT_460 = "[";
  protected final String TEXT_461 = "].trim();";
  protected final String TEXT_462 = "    \t" + NL + "        \t\t\tsubstringBegin";
  protected final String TEXT_463 = " = substringEnd";
  protected final String TEXT_464 = ";" + NL + "\t\t\t}";
  protected final String TEXT_465 = NL + "\t\tfor (int i_";
  protected final String TEXT_466 = " = 0; i_";
  protected final String TEXT_467 = " < ";
  protected final String TEXT_468 = "; i_";
  protected final String TEXT_469 = "++) {" + NL + "\t\t\tif (i_";
  protected final String TEXT_470 = " >= arrFieldSeparator";
  protected final String TEXT_471 = ".length ){" + NL + "\t\t\t\tcolumnValue";
  protected final String TEXT_472 = "[i_";
  protected final String TEXT_473 = "]=\"\";" + NL + "\t\t\t\tcontinue;" + NL + "\t\t\t}" + NL + "\t\t\t " + NL + "\t\t    if (substringBegin";
  protected final String TEXT_474 = " >= rowLen_";
  protected final String TEXT_475 = ") {" + NL + "\t\t    \tcolumnValue";
  protected final String TEXT_476 = "[i_";
  protected final String TEXT_477 = "] = \"\";" + NL + "\t\t    } else{" + NL + "\t\t    " + NL + "\t\t\t\tif ((star_";
  protected final String TEXT_478 = ").equals(tmpAry_";
  protected final String TEXT_479 = "[i_";
  protected final String TEXT_480 = "])){" + NL + "\t\t\t\t\tsubstringEnd";
  protected final String TEXT_481 = " = rowLen_";
  protected final String TEXT_482 = ";" + NL + "\t\t\t\t} else{" + NL + "\t    \t\t\tsubstringEnd";
  protected final String TEXT_483 = " = substringEnd";
  protected final String TEXT_484 = " +tmpAry_";
  protected final String TEXT_485 = "[i_";
  protected final String TEXT_486 = "];" + NL + "\t\t\t        if(substringEnd";
  protected final String TEXT_487 = " > rowLen_";
  protected final String TEXT_488 = ")" + NL + "\t\t\t        \tsubstringEnd";
  protected final String TEXT_489 = " = rowLen_";
  protected final String TEXT_490 = ";" + NL + "\t\t\t\t}";
  protected final String TEXT_491 = NL + "\t\t\t\tbyteArray_";
  protected final String TEXT_492 = "[i_";
  protected final String TEXT_493 = "] = arrays_";
  protected final String TEXT_494 = ".copyOfRange(row_";
  protected final String TEXT_495 = ".getBytes(";
  protected final String TEXT_496 = "),substringBegin";
  protected final String TEXT_497 = ", substringEnd";
  protected final String TEXT_498 = ");" + NL + "\t\t\t\tcolumnValue";
  protected final String TEXT_499 = "[i_";
  protected final String TEXT_500 = "] = new String(byteArray_";
  protected final String TEXT_501 = "[i_";
  protected final String TEXT_502 = "],";
  protected final String TEXT_503 = ");";
  protected final String TEXT_504 = NL + "\t        \tcolumnValue";
  protected final String TEXT_505 = "[i_";
  protected final String TEXT_506 = "] = row_";
  protected final String TEXT_507 = ".substring(substringBegin";
  protected final String TEXT_508 = ", substringEnd";
  protected final String TEXT_509 = ");\t";
  protected final String TEXT_510 = NL + "\t        \tif (";
  protected final String TEXT_511 = ")" + NL + "\t        \t\tcolumnValue";
  protected final String TEXT_512 = "[i_";
  protected final String TEXT_513 = "] = columnValue";
  protected final String TEXT_514 = "[i_";
  protected final String TEXT_515 = "].trim();" + NL + "\t        \t" + NL + "\t        \tsubstringBegin";
  protected final String TEXT_516 = " = substringEnd";
  protected final String TEXT_517 = ";" + NL + "\t        }" + NL + "\t\t}" + NL + "\t";
  protected final String TEXT_518 = NL + "   positionalUtil_";
  protected final String TEXT_519 = ".setValue_";
  protected final String TEXT_520 = "(";
  protected final String TEXT_521 = ",columnValue";
  protected final String TEXT_522 = ");";
  protected final String TEXT_523 = NL + "        \t\t\t";
  protected final String TEXT_524 = " ";
  protected final String TEXT_525 = " = null; ";
  protected final String TEXT_526 = "        \t\t\t" + NL + "        \t\t\t" + NL + "    } catch (Exception e) {" + NL + "        whetherReject_";
  protected final String TEXT_527 = " = true;";
  protected final String TEXT_528 = NL + "            throw(e);";
  protected final String TEXT_529 = NL + "                    ";
  protected final String TEXT_530 = " = new ";
  protected final String TEXT_531 = "Struct();";
  protected final String TEXT_532 = NL + "                    ";
  protected final String TEXT_533 = ".";
  protected final String TEXT_534 = " = ";
  protected final String TEXT_535 = ".";
  protected final String TEXT_536 = ";";
  protected final String TEXT_537 = NL + "                ";
  protected final String TEXT_538 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_539 = ";";
  protected final String TEXT_540 = NL + "                ";
  protected final String TEXT_541 = " = null;";
  protected final String TEXT_542 = NL + "                System.err.println(e.getMessage());";
  protected final String TEXT_543 = NL + "                ";
  protected final String TEXT_544 = " = null;";
  protected final String TEXT_545 = NL + "            \t";
  protected final String TEXT_546 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_547 = ";";
  protected final String TEXT_548 = NL + "    }" + NL + "        \t\t\t" + NL + "        \t\t\t";
  protected final String TEXT_549 = NL + "\t\t";
  protected final String TEXT_550 = "if(!whetherReject_";
  protected final String TEXT_551 = ") { ";
  protected final String TEXT_552 = "      " + NL + "             if(";
  protected final String TEXT_553 = " == null){ " + NL + "            \t ";
  protected final String TEXT_554 = " = new ";
  protected final String TEXT_555 = "Struct();" + NL + "             }\t\t\t\t";
  protected final String TEXT_556 = NL + "\t    \t ";
  protected final String TEXT_557 = ".";
  protected final String TEXT_558 = " = ";
  protected final String TEXT_559 = ".";
  protected final String TEXT_560 = ";    \t\t\t\t";
  protected final String TEXT_561 = NL + "\t\t";
  protected final String TEXT_562 = " } ";
  protected final String TEXT_563 = "\t";
  protected final String TEXT_564 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	List<? extends IConnection> connsFlow = node.getOutgoingConnections("FLOW");

    String rejectConnName = "";
    List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {
			String rowSeparator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
			
			List<Map<String, String>> formats =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__FORMATS__"
            );
            
            List<Map<String, String>> trimSelects =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__TRIMSELECT__"
            );
            
            String pattern1 = ElementParameterParser.getValue(node, "__PATTERN__");
            
            boolean useByte = ("true").equals(ElementParameterParser.getValue(node, "__USE_BYTE__"));
            boolean advanced = ("true").equals(ElementParameterParser.getValue(node, "__ADVANCED_OPTION__"));
            
			String filename = ElementParameterParser.getValueWithUIFieldKey(node,"__FILENAME__", "FILENAME");
			
			String trimAll = ElementParameterParser.getValue(node,"__TRIMALL__");
			boolean isTrimAll = true;
			if(trimAll != null && ("false").equals(trimAll)){
				isTrimAll = false;
			}
			
   			String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
   			
    		String header = ElementParameterParser.getValue(node, "__HEADER__");
    		
    		String footer = ElementParameterParser.getValue(node, "__FOOTER__");
    		
    		String limit = ElementParameterParser.getValue(node, "__LIMIT__");    		
    		if ("".equals(limit.trim())) limit = "-1";
    		
    		String removeEmptyRow = ElementParameterParser.getValue(node, "__REMOVE_EMPTY_ROW__");
    		
        	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
    		boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;
    		
    		//need to process rows longger than 100 000 characters, the property SafetySwitch(in talend_file_enhanced_20070724.jar) should be sent to false.(the default is true)
    		//that means if check the option(true), the logic value of bSafetySwitch should be changed to false (XOR with 'true')
    		boolean bSafetySwitch = (("true").equals(ElementParameterParser.getValue(node, "__PROCESS_LONG_ROW__")) ^ true);    		
    		String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
    		boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
    		String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
    		String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);    		  
			
			String checkDateStr = ElementParameterParser.getValue(node,"__CHECK_DATE__");
			boolean checkDate = (checkDateStr!=null&&!("").equals(checkDateStr))?("true").equals(checkDateStr):false;
			
			boolean uncompress = ("true").equals(ElementParameterParser.getValue(node,"__UNCOMPRESS__"));
			
    		if(("").equals(header)){
    			header = "0";
    		}
    			
    		if(("").equals(footer)){
    			footer = "0";
    		}
    		
    		boolean useStar = false;
    		
    		String pattern=TalendTextUtils.removeQuotes(pattern1);
    		String[] positions=(pattern.trim()).split(",");
    		for(int i=0;i<positions.length;i++){
            	if(("").equals(positions[i])){
               	 	positions[i]="0";
            	}
            	if(("*").equals(positions[i])){
            		useStar = true;
            	}
            }

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(footer);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(limit);
    stringBuffer.append(TEXT_7);
    
		if(useByte){

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
		}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
String firstConnName = "";
if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			firstConnName = conn.getName();
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                List<IMetadataColumn> listColumns = metadata.getListColumns();
				int sizeListColumns = listColumns.size();
    			if(advanced){// custom part start
    				for (int valueN=0; valueN<sizeListColumns; valueN++) {
    					String paddingChar = formats.get(valueN).get("PADDING_CHAR");
    					String align = formats.get(valueN).get("ALIGN");
    					if(("'L'").equals(align)){
    						align = "-1";
    					}else if(("'C'").equals(align)){
    						align = "0";
    					}else{
    						align = "1";
    					}
    					if(valueN%100==0){

    stringBuffer.append(TEXT_15);
    stringBuffer.append((valueN/100));
    stringBuffer.append(TEXT_16);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
     if(useByte){ 
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
     } 
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    
}
    					if(valueN == sizeListColumns - 1 && useStar){ //last column

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    
							if(useByte){

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(paddingChar );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(align );
    stringBuffer.append(TEXT_45);
    
							}else{

    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(paddingChar );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(align );
    stringBuffer.append(TEXT_52);
    
							}

    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    
						}else{//other column
							if(useByte){

    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(paddingChar );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(align );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    
							}else{

    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(paddingChar );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(align );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(paddingChar );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(align );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    
							}
						}
						if(isTrimAll || ("true").equals(trimSelects.get(valueN).get("TRIM"))){

    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    
						}
					IMetadataColumn column = listColumns.get(valueN);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_108);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    
					} else {

    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    
						if(javaType == JavaTypesManager.DATE) {
							if(checkDate) {

    stringBuffer.append(TEXT_114);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_118);
    
							} else {

    stringBuffer.append(TEXT_119);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_123);
    
							}
						}else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 

    stringBuffer.append(TEXT_124);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_126);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_128);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_129);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_130);
    
					}else if(javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_131);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_135);
    
							} else {

    stringBuffer.append(TEXT_136);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_138);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_140);
    
							}

    stringBuffer.append(TEXT_141);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate));
    stringBuffer.append(TEXT_144);
    					
					}
					if((valueN+1)%100==0){

    stringBuffer.append(TEXT_145);
                      }
				}//end for_
				if(sizeListColumns>0&&(sizeListColumns%100)>0){

    stringBuffer.append(TEXT_146);
    
				}
	}//custom end
    else{//custom not check
				for (int valueN=0; valueN<sizeListColumns; valueN++) {	
				    if(valueN%100==0){

    stringBuffer.append(TEXT_147);
    stringBuffer.append((valueN/100));
    stringBuffer.append(TEXT_148);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_151);
    
                    }	

    
                    IMetadataColumn column = listColumns.get(valueN);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_152);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_156);
    
					} else {

    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_159);
    
					        if(javaType == JavaTypesManager.DATE) {
								if(checkDate) {

    stringBuffer.append(TEXT_160);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_164);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_165);
    
								} else {

    stringBuffer.append(TEXT_166);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_170);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_171);
    
								}
							}else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 

    stringBuffer.append(TEXT_172);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_174);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_177);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_178);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_179);
    
					        }else if(javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_180);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_185);
    
							} else {

    stringBuffer.append(TEXT_186);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_188);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_191);
    
							}

    stringBuffer.append(TEXT_192);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_194);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate));
    stringBuffer.append(TEXT_195);
    
        			}

    stringBuffer.append(TEXT_196);
    
             if((valueN+1)%100==0){

    stringBuffer.append(TEXT_197);
    
             }
         }

    stringBuffer.append(TEXT_198);
    
            if(sizeListColumns>0&&(sizeListColumns%100)>0){

    stringBuffer.append(TEXT_199);
    
             }

    stringBuffer.append(TEXT_200);
              }//custom not check
		}
	}
}

    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_204);
    
		if(advanced){

    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(formats.size() );
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(formats.size() );
    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_210);
    stringBuffer.append(formats.size() );
    stringBuffer.append(TEXT_211);
    
			for(int i = 0; i < formats.size(); i++){ 
				if(i == formats.size() - 1 && !(("").equals(rowSeparator) || ("\"\"").equals(rowSeparator))){
					if(("*").equals(formats.get(i).get("SIZE"))){ 
						useStar = true;
					}

    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_213);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(useStar ? -1 :  formats.get(i).get("SIZE") );
    stringBuffer.append(TEXT_215);
    
				}else{

    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_217);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_218);
    stringBuffer.append(formats.get(i).get("SIZE") );
    stringBuffer.append(TEXT_219);
    
				}
			}
			for(int i = 0; i < formats.size(); i++){ 
				if(i == 0){

    stringBuffer.append(TEXT_220);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_221);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_226);
    
				}else if(i == formats.size() - 1 && useStar){

    stringBuffer.append(TEXT_227);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_228);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(i-1 );
    stringBuffer.append(TEXT_231);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_232);
    stringBuffer.append(i-1 );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_234);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_235);
    
				}else{

    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_237);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_239);
    stringBuffer.append(i-1 );
    stringBuffer.append(TEXT_240);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_241);
    stringBuffer.append(i-1 );
    stringBuffer.append(TEXT_242);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_243);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_244);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_245);
    stringBuffer.append(i-1 );
    stringBuffer.append(TEXT_246);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_247);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_248);
    
				}
			}
		}//end if(advanced)

    stringBuffer.append(TEXT_249);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_250);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_251);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_252);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_253);
    
		if(uncompress){

    stringBuffer.append(TEXT_254);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_258);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_260);
     if(dieOnError) {
    stringBuffer.append(TEXT_261);
     } else { 
    stringBuffer.append(TEXT_262);
     } 
    stringBuffer.append(TEXT_263);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_264);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_265);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_266);
     if(dieOnError) {
    stringBuffer.append(TEXT_267);
     } else { 
    stringBuffer.append(TEXT_268);
     } 
    stringBuffer.append(TEXT_269);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_270);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_271);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_272);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_273);
    
	if(advanced){
		if(useByte){
	
    stringBuffer.append(TEXT_274);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_275);
    
	}
	
    stringBuffer.append(TEXT_276);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_277);
    
	}else{
		if(useByte){
	
    stringBuffer.append(TEXT_278);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_279);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_280);
    
		}
	
    stringBuffer.append(TEXT_281);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_283);
    
	}
	
    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_286);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_287);
    
		}else{

    stringBuffer.append(TEXT_288);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_290);
    
if(advanced){
	if(useByte){

    stringBuffer.append(TEXT_291);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_292);
    
}

    stringBuffer.append(TEXT_293);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_294);
    
}else{
	if(useByte){

    stringBuffer.append(TEXT_295);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_296);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_297);
    
	}

    stringBuffer.append(TEXT_298);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_299);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_300);
    
}

    stringBuffer.append(TEXT_301);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_302);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_303);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_304);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_305);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_308);
    
		}
			if(("").equals(rowSeparator) || ("\"\"").equals(rowSeparator) ){

    stringBuffer.append(TEXT_309);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_310);
    
				if(advanced){ 
					for(int i = 0; i < formats.size(); i++){ 

    stringBuffer.append(TEXT_311);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_312);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_313);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_314);
    
					}
				}else{
    				for(int i=0;i<positions.length;i++){

    stringBuffer.append(TEXT_315);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_316);
    stringBuffer.append(positions[i] );
    stringBuffer.append(TEXT_317);
    
        			}
				}

    stringBuffer.append(TEXT_318);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_319);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_320);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_321);
    
			}else{

    stringBuffer.append(TEXT_322);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_323);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_324);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_325);
    stringBuffer.append(removeEmptyRow );
    stringBuffer.append(TEXT_326);
    
			}

    stringBuffer.append(TEXT_327);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_328);
    stringBuffer.append(bSafetySwitch);
    stringBuffer.append(TEXT_329);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_330);
    stringBuffer.append(header );
    stringBuffer.append(TEXT_331);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_332);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_333);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_334);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_335);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_336);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_337);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_338);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_339);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_340);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_341);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_342);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_343);
    
			if(("").equals(rowSeparator) || ("\"\"").equals(rowSeparator) ){

    stringBuffer.append(TEXT_344);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_345);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_346);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_347);
    
			}else{

    stringBuffer.append(TEXT_348);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_349);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_350);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_351);
    stringBuffer.append(removeEmptyRow );
    stringBuffer.append(TEXT_352);
    
			}

    stringBuffer.append(TEXT_353);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_354);
    stringBuffer.append(header );
    stringBuffer.append(TEXT_355);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_356);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_357);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_358);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_359);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_360);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_361);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_362);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_363);
     if(dieOnError) {
    stringBuffer.append(TEXT_364);
     } else { 
    stringBuffer.append(TEXT_365);
     } 
    stringBuffer.append(TEXT_366);
    
if ( !(java.util.regex.Pattern.compile("[0-9]*").matcher(positions[0]).matches() )){
		if(pattern1.startsWith("\"")){

    stringBuffer.append(TEXT_367);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_368);
    stringBuffer.append(positions[0] );
    stringBuffer.append(TEXT_369);
     
		}else{

    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_371);
    stringBuffer.append(positions[0]);
    stringBuffer.append(TEXT_372);
     
		}

    stringBuffer.append(TEXT_373);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_381);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_382);
    
}	

    stringBuffer.append(TEXT_383);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_384);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_385);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_386);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_387);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_388);
    
	if(useByte){

    stringBuffer.append(TEXT_389);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_390);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_391);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_392);
    
	}else{

    stringBuffer.append(TEXT_393);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_394);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_395);
    
	}

    
    if(rejectConns != null && rejectConns.size() > 0) {
        IConnection rejectConn = rejectConns.get(0);
        rejectConnName = rejectConn.getName();
    }
    List<IMetadataColumn> rejectColumnList = null;
    IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
    if(metadataTable != null) {
        rejectColumnList = metadataTable.getListColumns();      
    }

    	if (conns!=null) {
    		if (conns.size()>0) {
    			for (int i=0;i<conns.size();i++) {
    				IConnection connTemp = conns.get(i);
    				if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_396);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_397);
    
    				}
    			}
    		}
    	}
    	
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			firstConnName = conn.getName();
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    stringBuffer.append(TEXT_398);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_399);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_400);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_401);
    
    			if(advanced){
    				List<IMetadataColumn> listColumns = metadata.getListColumns();
   				   int sizeListColumns = listColumns.size();
    				for (int valueN=0; valueN<sizeListColumns; valueN++) {
    					String paddingChar = formats.get(valueN).get("PADDING_CHAR");
    					String align = formats.get(valueN).get("ALIGN");
    					if(("'L'").equals(align)){
    						align = "-1";
    					}else if(("'C'").equals(align)){
    						align = "0";
    					}else{
    						align = "1";
    					}
                   if(valueN%100==0){

    stringBuffer.append(TEXT_402);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_403);
    stringBuffer.append((valueN/100));
    stringBuffer.append(TEXT_404);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_405);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_406);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_407);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_408);
     if(useByte){ 
    stringBuffer.append(TEXT_409);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_410);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_411);
     } 
    stringBuffer.append(TEXT_412);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_413);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_414);
    
                  }
				}
			}else{// end if(advance)

    stringBuffer.append(TEXT_415);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_416);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_417);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_418);
    
	///////////////////
	//Branch: first item is a numberic, not '((String)globalMap.get("global_variable"))' or 'context.Separator'
	if ( java.util.regex.Pattern.compile("[0-9]*").matcher(positions[0]).matches() ){
	///////////////////
	
		for(int i=0;i <	metadata.getListColumns().size();i++){
			if(i >=positions.length){

    stringBuffer.append(TEXT_419);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_420);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_421);
    
				continue;
			}

    stringBuffer.append(TEXT_422);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_423);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_424);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_425);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_426);
    
					if(("*").equals(positions[i])){

    stringBuffer.append(TEXT_427);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_428);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_429);
    
					} else{

    stringBuffer.append(TEXT_430);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_431);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_432);
    stringBuffer.append(positions[i]);
    stringBuffer.append(TEXT_433);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_434);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_435);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_436);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_437);
    
					}
					if(useByte){

    stringBuffer.append(TEXT_438);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_439);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_440);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_441);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_442);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_443);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_444);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_445);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_446);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_447);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_448);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_449);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_450);
    
					}else{

    stringBuffer.append(TEXT_451);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_452);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_453);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_454);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_455);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_456);
     
					}
       				if(isTrimAll || ("true").equals(trimSelects.get(i).get("TRIM"))){

    stringBuffer.append(TEXT_457);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_458);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_459);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_460);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_461);
    
					}

    stringBuffer.append(TEXT_462);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_463);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_464);
    
	
		}//for(...)
				
	///////////////
	} else{

    stringBuffer.append(TEXT_465);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_466);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_467);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_468);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_469);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_470);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_489);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_490);
    
		if(useByte){

    stringBuffer.append(TEXT_491);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_492);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_493);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_494);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_495);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_496);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_497);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_498);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_499);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_500);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_501);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_502);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_503);
    
		}else{

    stringBuffer.append(TEXT_504);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_505);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_506);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_507);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_508);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_509);
    
		}

    stringBuffer.append(TEXT_510);
    stringBuffer.append(isTrimAll);
    stringBuffer.append(TEXT_511);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_512);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_513);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_514);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_515);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_516);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_517);
    
	}
	///////////////
	List<IMetadataColumn> listColumns = metadata.getListColumns();
	int sizeListColumns = listColumns.size();
	for (int valueN=0; valueN<sizeListColumns; valueN++) {	
		if(valueN%100==0){

    stringBuffer.append(TEXT_518);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_519);
    stringBuffer.append((valueN/100));
    stringBuffer.append(TEXT_520);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_521);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_522);
    
	    
	    }
				
        }
    }

    stringBuffer.append(TEXT_523);
    if(rejectConnName.equals(firstConnName)) {
    stringBuffer.append(TEXT_524);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_525);
    }
    stringBuffer.append(TEXT_526);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_527);
    
        if (dieOnError) {
            
    stringBuffer.append(TEXT_528);
    
        } else {
            if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

                
    stringBuffer.append(TEXT_529);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_530);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_531);
    
                for(IMetadataColumn column : metadata.getListColumns()) {
                    
    stringBuffer.append(TEXT_532);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_533);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_534);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_535);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_536);
    
                }
                
    stringBuffer.append(TEXT_537);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_538);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_539);
    stringBuffer.append(TEXT_540);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_541);
    
            } else if(("").equals(rejectConnName)){
                
    stringBuffer.append(TEXT_542);
    stringBuffer.append(TEXT_543);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_544);
    
            } else if(rejectConnName.equals(firstConnName)){
    stringBuffer.append(TEXT_545);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_546);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_547);
    }
        } 
        
    stringBuffer.append(TEXT_548);
            			
        		}
		if (conns.size()>0) {	
			boolean isFirstEnter = true;
			for (int i=0;i<conns.size();i++) {
				conn = conns.get(i);
				if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getName().compareTo(rejectConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {

    stringBuffer.append(TEXT_549);
     if(isFirstEnter) {
    stringBuffer.append(TEXT_550);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_551);
     isFirstEnter = false; } 
    stringBuffer.append(TEXT_552);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_553);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_554);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_555);
    
			    	 for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_556);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_557);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_558);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_559);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_560);
    
				 	}
				}
			}

    stringBuffer.append(TEXT_561);
     if(!isFirstEnter) {
    stringBuffer.append(TEXT_562);
     } 
    stringBuffer.append(TEXT_563);
    
		}
        	}
		}
	}
}

    stringBuffer.append(TEXT_564);
    return stringBuffer.toString();
  }
}
