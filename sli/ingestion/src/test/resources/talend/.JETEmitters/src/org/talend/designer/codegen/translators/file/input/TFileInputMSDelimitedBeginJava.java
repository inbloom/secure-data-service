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

public class TFileInputMSDelimitedBeginJava
{
  protected static String nl;
  public static synchronized TFileInputMSDelimitedBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputMSDelimitedBeginJava result = new TFileInputMSDelimitedBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "int key_";
  protected final String TEXT_2 = "_";
  protected final String TEXT_3 = " = 0;";
  protected final String TEXT_4 = NL + "String key_";
  protected final String TEXT_5 = "_";
  protected final String TEXT_6 = " = \"\";";
  protected final String TEXT_7 = NL + "int count_";
  protected final String TEXT_8 = "_";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ";" + NL + "final int lowBound_";
  protected final String TEXT_11 = "_";
  protected final String TEXT_12 = " = ";
  protected final String TEXT_13 = ";";
  protected final String TEXT_14 = "final int highBound_";
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = " = ";
  protected final String TEXT_17 = ";";
  protected final String TEXT_18 = NL + "org.talend.fileprocess.FileInputDelimited fid_";
  protected final String TEXT_19 = " = new org.talend.fileprocess.FileInputDelimited(";
  protected final String TEXT_20 = ",";
  protected final String TEXT_21 = ",";
  protected final String TEXT_22 = ",";
  protected final String TEXT_23 = ", true, 0, 0,-1, -1, false);" + NL + "String temp_";
  protected final String TEXT_24 = " = null;" + NL + "while (fid_";
  protected final String TEXT_25 = ".nextRecord()) {";
  protected final String TEXT_26 = NL + "\t";
  protected final String TEXT_27 = " = null;";
  protected final String TEXT_28 = NL + "\ttry{";
  protected final String TEXT_29 = NL + "\t";
  protected final String TEXT_30 = "if(fid_";
  protected final String TEXT_31 = ".get(Integer.parseInt(";
  protected final String TEXT_32 = "))";
  protected final String TEXT_33 = ".equals(";
  protected final String TEXT_34 = ")){";
  protected final String TEXT_35 = NL + "\t\tkey_";
  protected final String TEXT_36 = "_";
  protected final String TEXT_37 = "++;";
  protected final String TEXT_38 = NL + "\t\tkey_";
  protected final String TEXT_39 = "_";
  protected final String TEXT_40 = " = fid_";
  protected final String TEXT_41 = ".get(";
  protected final String TEXT_42 = ")";
  protected final String TEXT_43 = ";";
  protected final String TEXT_44 = NL + "\t\tif((count_";
  protected final String TEXT_45 = "_";
  protected final String TEXT_46 = " < lowBound_";
  protected final String TEXT_47 = "_";
  protected final String TEXT_48 = ")";
  protected final String TEXT_49 = " || (count_";
  protected final String TEXT_50 = "_";
  protected final String TEXT_51 = " > highBound_";
  protected final String TEXT_52 = "_";
  protected final String TEXT_53 = ")";
  protected final String TEXT_54 = "){" + NL + "\t\t\tthrow new Exception(\"Data error in file: The cardinality for \" + ";
  protected final String TEXT_55 = " + \" is ";
  protected final String TEXT_56 = ", but we have found \" + count_";
  protected final String TEXT_57 = "_";
  protected final String TEXT_58 = " + \".\");//thow exception............" + NL + "\t\t}" + NL + "\t\tcount_";
  protected final String TEXT_59 = "_";
  protected final String TEXT_60 = " = 0;";
  protected final String TEXT_61 = NL + "\t\tcount_";
  protected final String TEXT_62 = "_";
  protected final String TEXT_63 = "++;" + NL + "\t\tif((count_";
  protected final String TEXT_64 = "_";
  protected final String TEXT_65 = " < lowBound_";
  protected final String TEXT_66 = "_";
  protected final String TEXT_67 = ")";
  protected final String TEXT_68 = " || (count_";
  protected final String TEXT_69 = "_";
  protected final String TEXT_70 = " > highBound_";
  protected final String TEXT_71 = "_";
  protected final String TEXT_72 = ")";
  protected final String TEXT_73 = "){" + NL + "\t\t\tthrow new Exception(\"Data error in file: The cardinality for \" + ";
  protected final String TEXT_74 = " + \" is ";
  protected final String TEXT_75 = ", but we have found \" + count_";
  protected final String TEXT_76 = "_";
  protected final String TEXT_77 = " + \".\");//thow exception............" + NL + "\t\t}";
  protected final String TEXT_78 = NL + "\t\t";
  protected final String TEXT_79 = " = new ";
  protected final String TEXT_80 = "Struct();";
  protected final String TEXT_81 = NL + "\t\t";
  protected final String TEXT_82 = ".";
  protected final String TEXT_83 = " = String.valueOf(key_";
  protected final String TEXT_84 = "_";
  protected final String TEXT_85 = ");";
  protected final String TEXT_86 = NL + "\t\t";
  protected final String TEXT_87 = ".";
  protected final String TEXT_88 = " = fid_";
  protected final String TEXT_89 = ".get(";
  protected final String TEXT_90 = ")";
  protected final String TEXT_91 = ";";
  protected final String TEXT_92 = NL + "\t\ttemp_";
  protected final String TEXT_93 = " = String.valueOf(key_";
  protected final String TEXT_94 = "_";
  protected final String TEXT_95 = ");";
  protected final String TEXT_96 = NL + "\t\ttemp_";
  protected final String TEXT_97 = " = fid_";
  protected final String TEXT_98 = ".get(";
  protected final String TEXT_99 = ")";
  protected final String TEXT_100 = ";";
  protected final String TEXT_101 = NL + "\t\tif(temp_";
  protected final String TEXT_102 = ".length() > 0) {";
  protected final String TEXT_103 = NL + "\t\t\t";
  protected final String TEXT_104 = ".";
  protected final String TEXT_105 = " = temp_";
  protected final String TEXT_106 = ".getBytes(";
  protected final String TEXT_107 = ");";
  protected final String TEXT_108 = "\t\t\t";
  protected final String TEXT_109 = ".";
  protected final String TEXT_110 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_111 = ", ";
  protected final String TEXT_112 = ",false);";
  protected final String TEXT_113 = NL + "\t\t\t";
  protected final String TEXT_114 = ".";
  protected final String TEXT_115 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_116 = ", ";
  protected final String TEXT_117 = ");";
  protected final String TEXT_118 = NL + "\t\t\t";
  protected final String TEXT_119 = ".";
  protected final String TEXT_120 = " = ParserUtils.parseTo_";
  protected final String TEXT_121 = "(ParserUtils.parseTo_Number(temp_";
  protected final String TEXT_122 = ", ";
  protected final String TEXT_123 = ", ";
  protected final String TEXT_124 = "));";
  protected final String TEXT_125 = NL + "\t\t\t";
  protected final String TEXT_126 = ".";
  protected final String TEXT_127 = " = ParserUtils.parseTo_";
  protected final String TEXT_128 = "(temp_";
  protected final String TEXT_129 = ");";
  protected final String TEXT_130 = NL + "\t\t} else {\t\t\t\t\t\t";
  protected final String TEXT_131 = NL + "\t\t\tthrow new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_132 = "' in '";
  protected final String TEXT_133 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_134 = "\t\t\t";
  protected final String TEXT_135 = ".";
  protected final String TEXT_136 = " = ";
  protected final String TEXT_137 = ";";
  protected final String TEXT_138 = "\t\t}";
  protected final String TEXT_139 = NL + "\t}";
  protected final String TEXT_140 = NL + "\t} catch (Exception e) {";
  protected final String TEXT_141 = NL + "            throw(e);";
  protected final String TEXT_142 = NL + "        System.err.println(e.getMessage());";
  protected final String TEXT_143 = NL + "\t\t";
  protected final String TEXT_144 = " = null;";
  protected final String TEXT_145 = NL + "\t}";
  protected final String TEXT_146 = NL + "int key_";
  protected final String TEXT_147 = "_";
  protected final String TEXT_148 = " = 0;";
  protected final String TEXT_149 = NL + "String key_";
  protected final String TEXT_150 = "_";
  protected final String TEXT_151 = " = \"\";";
  protected final String TEXT_152 = NL + "int count_";
  protected final String TEXT_153 = "_";
  protected final String TEXT_154 = " = ";
  protected final String TEXT_155 = ";" + NL + "final int lowBound_";
  protected final String TEXT_156 = "_";
  protected final String TEXT_157 = " = ";
  protected final String TEXT_158 = ";";
  protected final String TEXT_159 = "final int highBound_";
  protected final String TEXT_160 = "_";
  protected final String TEXT_161 = " = ";
  protected final String TEXT_162 = ";";
  protected final String TEXT_163 = NL + "char fieldSeparator_";
  protected final String TEXT_164 = "[] = null;" + NL + "\t\t" + NL + "//support passing value (property: Field Separator) by 'context.fs' or 'globalMap.get(\"fs\")'. " + NL + "if ( ((String)";
  protected final String TEXT_165 = ").length() > 0 ){" + NL + "\tfieldSeparator_";
  protected final String TEXT_166 = " = ((String)";
  protected final String TEXT_167 = ").toCharArray();" + NL + "}else {\t\t\t" + NL + "\tthrow new IllegalArgumentException(\"Field Separator must be assigned a char.\"); " + NL + "}" + NL + "\t\t" + NL + "char rowSeparator_";
  protected final String TEXT_168 = "[] = null;" + NL + "\t\t" + NL + "//support passing value (property: Row Separator) by 'context.rs' or 'globalMap.get(\"rs\")'. " + NL + "if ( ((String)";
  protected final String TEXT_169 = ").length() > 0 ){" + NL + "\trowSeparator_";
  protected final String TEXT_170 = " = ((String)";
  protected final String TEXT_171 = ").toCharArray();" + NL + "}else {" + NL + "\tthrow new IllegalArgumentException(\"Row Separator must be assigned a char.\"); " + NL + "}" + NL + "\t\t" + NL + "com.csvreader.CsvReader ";
  protected final String TEXT_172 = "csvReader";
  protected final String TEXT_173 = "=new com.csvreader.CsvReader(new java.io.BufferedReader(new java.io.InputStreamReader( new java.io.FileInputStream(";
  protected final String TEXT_174 = "),";
  protected final String TEXT_175 = ")), ";
  protected final String TEXT_176 = ");";
  protected final String TEXT_177 = NL;
  protected final String TEXT_178 = "csvReader";
  protected final String TEXT_179 = ".setTrimWhitespace(false);" + NL + "if ( (rowSeparator_";
  protected final String TEXT_180 = "[0] != '\\n') && (rowSeparator_";
  protected final String TEXT_181 = "[0] != '\\r') )" + NL + "\t";
  protected final String TEXT_182 = "csvReader";
  protected final String TEXT_183 = ".setRecordDelimiter(rowSeparator_";
  protected final String TEXT_184 = "[0]);" + NL;
  protected final String TEXT_185 = NL;
  protected final String TEXT_186 = "csvReader";
  protected final String TEXT_187 = ".setTextQualifier('";
  protected final String TEXT_188 = "');" + NL;
  protected final String TEXT_189 = NL;
  protected final String TEXT_190 = "csvReader";
  protected final String TEXT_191 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_BACKSLASH);";
  protected final String TEXT_192 = NL;
  protected final String TEXT_193 = "csvReader";
  protected final String TEXT_194 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);";
  protected final String TEXT_195 = NL + "//?????doesn't work for other escapeChar";
  protected final String TEXT_196 = "\t" + NL + "String[] delim_";
  protected final String TEXT_197 = " = new String[]{";
  protected final String TEXT_198 = NL;
  protected final String TEXT_199 = NL + "};" + NL + "" + NL + "//java.io.BufferedReader reader_";
  protected final String TEXT_200 = " = new java.io.BufferedReader(new java.io.FileReader(";
  protected final String TEXT_201 = "));" + NL + "" + NL + "String tmp";
  protected final String TEXT_202 = " = \"\";";
  protected final String TEXT_203 = "      " + NL + "" + NL + "int index_";
  protected final String TEXT_204 = " = Integer.parseInt(";
  protected final String TEXT_205 = ");" + NL + "" + NL + "String[] row";
  protected final String TEXT_206 = "=null;" + NL + "  " + NL + "String temp_";
  protected final String TEXT_207 = " = null;" + NL + "" + NL + "while ( ";
  protected final String TEXT_208 = "csvReader";
  protected final String TEXT_209 = ".readRecord() ) {";
  protected final String TEXT_210 = NL + "\trow";
  protected final String TEXT_211 = "=csvReader";
  protected final String TEXT_212 = ".getValues();" + NL + "\t";
  protected final String TEXT_213 = NL + "\tfor(int ii_";
  protected final String TEXT_214 = "=0;ii_";
  protected final String TEXT_215 = " < row";
  protected final String TEXT_216 = ".length;ii_";
  protected final String TEXT_217 = "++){" + NL + "\t\trow";
  protected final String TEXT_218 = "[ii_";
  protected final String TEXT_219 = "] = row";
  protected final String TEXT_220 = "[ii_";
  protected final String TEXT_221 = "].trim(); " + NL + "\t}" + NL + "\t";
  protected final String TEXT_222 = NL + "\ttmp";
  protected final String TEXT_223 = " = ";
  protected final String TEXT_224 = "csvReader";
  protected final String TEXT_225 = ".getRawRecord();" + NL + "\tfor(int i_";
  protected final String TEXT_226 = "=0; i_";
  protected final String TEXT_227 = "<delim_";
  protected final String TEXT_228 = ".length; i_";
  protected final String TEXT_229 = "++){ //NS000" + NL + "\t\tcom.csvreader.CsvReader csvReader";
  protected final String TEXT_230 = " = new com.csvreader.CsvReader(new java.io.BufferedReader(new java.io.InputStreamReader(" + NL + "\t\t\tnew java.io.ByteArrayInputStream(tmp";
  protected final String TEXT_231 = ".getBytes()), ";
  protected final String TEXT_232 = ")), delim_";
  protected final String TEXT_233 = "[i_";
  protected final String TEXT_234 = "].charAt(0));" + NL + "\t\tcsvReader";
  protected final String TEXT_235 = ".setTrimWhitespace(false);" + NL + "\t\tif ( (rowSeparator_";
  protected final String TEXT_236 = "[0] != '\\n') && (rowSeparator_";
  protected final String TEXT_237 = "[0] != '\\r') )" + NL + "\t\t\tcsvReader";
  protected final String TEXT_238 = ".setRecordDelimiter(rowSeparator_";
  protected final String TEXT_239 = "[0]);" + NL + "\t\t" + NL + "\t\tcsvReader";
  protected final String TEXT_240 = ".setTextQualifier('";
  protected final String TEXT_241 = "');";
  protected final String TEXT_242 = NL + "\t\tcsvReader";
  protected final String TEXT_243 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_BACKSLASH);";
  protected final String TEXT_244 = NL + "\t\tcsvReader";
  protected final String TEXT_245 = ".setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);";
  protected final String TEXT_246 = NL + "\t\t//?????doesn't work for other escapeChar";
  protected final String TEXT_247 = NL;
  protected final String TEXT_248 = NL + "\t";
  protected final String TEXT_249 = " = null;";
  protected final String TEXT_250 = "\t\t";
  protected final String TEXT_251 = NL + NL + "\t\tif(csvReader";
  protected final String TEXT_252 = ".readRecord()==false) {" + NL + "\t\t\tcsvReader";
  protected final String TEXT_253 = ".close();" + NL + "\t\t\tbreak;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\trow";
  protected final String TEXT_254 = " = csvReader";
  protected final String TEXT_255 = ".getValues();" + NL + "    \t";
  protected final String TEXT_256 = NL + "    \tfor(int ii_";
  protected final String TEXT_257 = "=0;ii_";
  protected final String TEXT_258 = " < row";
  protected final String TEXT_259 = ".length;ii_";
  protected final String TEXT_260 = "++){" + NL + "    \t\trow";
  protected final String TEXT_261 = "[ii_";
  protected final String TEXT_262 = "] = row";
  protected final String TEXT_263 = "[ii_";
  protected final String TEXT_264 = "].trim(); " + NL + "    \t}" + NL + "    \t";
  protected final String TEXT_265 = NL + "\t\tif (row";
  protected final String TEXT_266 = " == null" + NL + "\t\t\t\t|| row";
  protected final String TEXT_267 = ".length <= index_";
  protected final String TEXT_268 = ") {" + NL + "\t\t\tcontinue;" + NL + "\t\t}";
  protected final String TEXT_269 = NL + "\t\ttry{";
  protected final String TEXT_270 = NL + "\t";
  protected final String TEXT_271 = "if((row";
  protected final String TEXT_272 = ".length == 0) ? (\"\".equals(";
  protected final String TEXT_273 = ")) : " + NL + "\t(row";
  protected final String TEXT_274 = "[index_";
  protected final String TEXT_275 = "].equals(";
  protected final String TEXT_276 = ")";
  protected final String TEXT_277 = ")){";
  protected final String TEXT_278 = NL + "\t\tkey_";
  protected final String TEXT_279 = "_";
  protected final String TEXT_280 = "++;";
  protected final String TEXT_281 = NL + "\t\tkey_";
  protected final String TEXT_282 = "_";
  protected final String TEXT_283 = " = (row";
  protected final String TEXT_284 = ".length <= ";
  protected final String TEXT_285 = ") ? \"\" : row";
  protected final String TEXT_286 = "[";
  protected final String TEXT_287 = "];";
  protected final String TEXT_288 = NL + "\t\tif((count_";
  protected final String TEXT_289 = "_";
  protected final String TEXT_290 = " < lowBound_";
  protected final String TEXT_291 = "_";
  protected final String TEXT_292 = ") || (count_";
  protected final String TEXT_293 = "_";
  protected final String TEXT_294 = " > highBound_";
  protected final String TEXT_295 = "_";
  protected final String TEXT_296 = ")){" + NL + "\t\t\tthrow new Exception(\"Data error in file: The cardinality for \" + ";
  protected final String TEXT_297 = " + \" is ";
  protected final String TEXT_298 = ", but we have found \" + count_";
  protected final String TEXT_299 = "_";
  protected final String TEXT_300 = " + \".\");//thow exception............" + NL + "\t\t}" + NL + "\t\tcount_";
  protected final String TEXT_301 = "_";
  protected final String TEXT_302 = " = 0;";
  protected final String TEXT_303 = NL + "\t\tcount_";
  protected final String TEXT_304 = "_";
  protected final String TEXT_305 = "++;" + NL + "\t\tif((count_";
  protected final String TEXT_306 = "_";
  protected final String TEXT_307 = " < lowBound_";
  protected final String TEXT_308 = "_";
  protected final String TEXT_309 = ") || (count_";
  protected final String TEXT_310 = "_";
  protected final String TEXT_311 = " > highBound_";
  protected final String TEXT_312 = "_";
  protected final String TEXT_313 = ")){" + NL + "\t\t\tthrow new Exception(\"Data error in file: The cardinality for \" + ";
  protected final String TEXT_314 = " + \" is ";
  protected final String TEXT_315 = ", but we have found \" + count_";
  protected final String TEXT_316 = "_";
  protected final String TEXT_317 = " + \".\");//thow exception............" + NL + "\t\t}";
  protected final String TEXT_318 = NL + "\t\t";
  protected final String TEXT_319 = " = new ";
  protected final String TEXT_320 = "Struct();";
  protected final String TEXT_321 = NL + "\t\t";
  protected final String TEXT_322 = ".";
  protected final String TEXT_323 = " = String.valueOf(key_";
  protected final String TEXT_324 = "_";
  protected final String TEXT_325 = ");";
  protected final String TEXT_326 = NL + "\t\t";
  protected final String TEXT_327 = ".";
  protected final String TEXT_328 = " = (row";
  protected final String TEXT_329 = ".length <= ";
  protected final String TEXT_330 = ") ? \"\" : row";
  protected final String TEXT_331 = "[";
  protected final String TEXT_332 = "];";
  protected final String TEXT_333 = NL + "\t\ttemp_";
  protected final String TEXT_334 = " = String.valueOf(key_";
  protected final String TEXT_335 = "_";
  protected final String TEXT_336 = ");";
  protected final String TEXT_337 = NL + "\t\ttemp_";
  protected final String TEXT_338 = " = (row";
  protected final String TEXT_339 = ".length <= ";
  protected final String TEXT_340 = ") ? \"\" : row";
  protected final String TEXT_341 = "[";
  protected final String TEXT_342 = "];";
  protected final String TEXT_343 = NL + "\t\tif(temp_";
  protected final String TEXT_344 = ".length() > 0) {";
  protected final String TEXT_345 = NL + "\t\t\t";
  protected final String TEXT_346 = ".";
  protected final String TEXT_347 = " = temp_";
  protected final String TEXT_348 = ".getBytes(";
  protected final String TEXT_349 = ");";
  protected final String TEXT_350 = "\t\t\t";
  protected final String TEXT_351 = ".";
  protected final String TEXT_352 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_353 = ", ";
  protected final String TEXT_354 = ",false);";
  protected final String TEXT_355 = NL + "\t\t\t";
  protected final String TEXT_356 = ".";
  protected final String TEXT_357 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_358 = ", ";
  protected final String TEXT_359 = ");";
  protected final String TEXT_360 = NL + "\t\t\t";
  protected final String TEXT_361 = ".";
  protected final String TEXT_362 = " = ParserUtils.parseTo_";
  protected final String TEXT_363 = "(ParserUtils.parseTo_Number(temp_";
  protected final String TEXT_364 = ", ";
  protected final String TEXT_365 = ", ";
  protected final String TEXT_366 = "));";
  protected final String TEXT_367 = NL + "\t\t\t";
  protected final String TEXT_368 = ".";
  protected final String TEXT_369 = " = ParserUtils.parseTo_";
  protected final String TEXT_370 = "(temp_";
  protected final String TEXT_371 = ");";
  protected final String TEXT_372 = NL + "\t\t} else {\t\t\t\t\t\t";
  protected final String TEXT_373 = NL + "\t\t\tthrow new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_374 = "' in '";
  protected final String TEXT_375 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_376 = "\t\t\t";
  protected final String TEXT_377 = ".";
  protected final String TEXT_378 = " = ";
  protected final String TEXT_379 = ";";
  protected final String TEXT_380 = "\t\t}";
  protected final String TEXT_381 = NL + "\t\tcsvReader";
  protected final String TEXT_382 = ".close();" + NL + "\t\tbreak;";
  protected final String TEXT_383 = NL + "\t}";
  protected final String TEXT_384 = NL + "\t} catch (Exception e) {";
  protected final String TEXT_385 = NL + "            throw(e);";
  protected final String TEXT_386 = NL + "        System.err.println(e.getMessage());";
  protected final String TEXT_387 = NL + "\t\t";
  protected final String TEXT_388 = " = null;";
  protected final String TEXT_389 = NL + "\t}";
  protected final String TEXT_390 = NL;
  protected final String TEXT_391 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
boolean hasDataLink = false;
if(conns!=null){
	for(int i=0;i<conns.size();i++){
		IConnection connTemp = conns.get(i);
	    if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	   		hasDataLink = true;
	   		break;
	    }
	}
}
List<Map<String, String>> schemas_o = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SCHEMAS__");
List<Map<String, String>> schemas = new java.util.ArrayList<Map<String, String>>();
for(Map<String, String> schema_o : schemas_o){
	Map<String, String> schema = new java.util.HashMap<String, String>();
	schemas.add(schema);
	schema.put("SCHEMA", TalendTextUtils.removeQuotes(schema_o.get("SCHEMA")));
	schema.put("RECORD", schema_o.get("RECORD"));
	schema.put("PARENT_RECORD", schema_o.get("PARENT_RECORD"));
	schema.put("KEY_COLUMN_INDEX", TalendTextUtils.removeQuotes(schema_o.get("KEY_COLUMN_INDEX")));
	schema.put("CARDINALITY", TalendTextUtils.removeQuotes(schema_o.get("CARDINALITY")));
	schema.put("FIELDDELIMITED",schema_o.get("FIELDDELIMITED"));
	String strInfinitude = "false"; // used to support *
	if(!("\"\"").equals(schema.get("PARENT_RECORD"))){//has parent node
		String cardString = schema.get("CARDINALITY");
		if(("*").equals(cardString)){
			strInfinitude = "true";
		}else if(cardString.contains(",")){
			String[] cards = cardString.split(",");
			if(("*").equals(cards[1])){
				strInfinitude = "true";
			}
		}
	}
	schema.put("IS_INFINITUDE",strInfinitude);
}

String filename_o = ElementParameterParser.getValue(node,"__FILENAME__");
String filename = filename_o;
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
String rowSeparator = ElementParameterParser.getValue(node,"__ROWSEPARATOR__");
String fieldSeparator = ElementParameterParser.getValue(node,"__FIELDSEPARATOR__");
String columnIndex = ElementParameterParser.getValue(node,"__COLUMNINDEX__");

String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false; 
		
boolean trimAll = ("true").equals(ElementParameterParser.getValue(node,"__TRIMALL__"));

String checkDateStr = ElementParameterParser.getValue(node,"__CHECK_DATE__");
boolean checkDate = (checkDateStr!=null&&!("").equals(checkDateStr))?("true").equals(checkDateStr):false;

boolean useMulSeprators = "true".equals(ElementParameterParser.getValue(node,"__USE_MULTISEPARATORS__"));

String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);

if (hasDataLink) {//FIM_000
////////////////////////
if(useMulSeprators==false && ("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {//FIM_0000
	List<String> connections = new java.util.ArrayList<String>();
    Map<String, List<IMetadataColumn>> connectionMapColumnList = new java.util.HashMap<String, List<IMetadataColumn>>();
    Map<String, String> schemaConnectionMap = new java.util.HashMap<String, String>();
    
    for(int i=0;i<conns.size();i++){
    	IConnection connTemp = conns.get(i);
    	if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    		IMetadataTable tempMetadataTable = connTemp.getMetadataTable();
    		for(Map<String, String> schema : schemas){
				if(tempMetadataTable.getLabel().equals(schema.get("SCHEMA"))){
					schemaConnectionMap.put(schema.get("SCHEMA"), tempMetadataTable.getTableName());
					break;
				}
			}
    		List<IMetadataColumn> listColumns = tempMetadataTable.getListColumns();
    		connections.add(connTemp.getName());
    		connectionMapColumnList.put(connTemp.getName(), listColumns);
    	}
	}
	
	for(int i=0; i<schemas.size();i++){//FIM_001
		Map<String, String> schema = schemas.get(i);
		String schemaName = schema.get("SCHEMA");
		for(int j=0; j<schemas.size();j++){
			if(i == j){
				continue;
			}
			Map<String, String> schema2 = schemas.get(j);
			if(schema.get("RECORD").equals(schema2.get("PARENT_RECORD"))){//has child node
				String keyIndex = schema.get("KEY_COLUMN_INDEX");
				if(("").equals(keyIndex)){//no key __________PROBLEM_2_________

    stringBuffer.append(TEXT_1);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
				}else{

    stringBuffer.append(TEXT_4);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
				}
				break;
			}
		}
		if(!("\"\"").equals(schema.get("PARENT_RECORD"))){//has parent node
			String cardString = schema.get("CARDINALITY");
			String lowBound = String.valueOf(0);
			String highBound = String.valueOf(Integer.MAX_VALUE);
			//way to parse the cardinality string.............__________PROBLEM_3_________
			if(("").equals(cardString)){
				//do nothing
			}else if(cardString.contains(",")){
				String[] cards = cardString.split(",");
				lowBound = cards[0];
				highBound = cards[1];
			}else{
				lowBound = cardString;
				highBound = cardString;
			}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(lowBound );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(lowBound );
    stringBuffer.append(TEXT_13);
    if(("false").equals(schema.get("IS_INFINITUDE"))){
    stringBuffer.append(TEXT_14);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(highBound );
    stringBuffer.append(TEXT_17);
    }
    
		}
	}//FIM_001

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
	for(String conName : connections){

    stringBuffer.append(TEXT_26);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_27);
    
	}

    stringBuffer.append(TEXT_28);
    
	for(int i=0; i<schemas.size();i++){//FIM_002
		Map<String, String> schema = schemas.get(i);
		String record = schema.get("RECORD");

    stringBuffer.append(TEXT_29);
    stringBuffer.append((i==0) ? "" : "else " );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(columnIndex );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(trimAll?".trim()":"");
    stringBuffer.append(TEXT_33);
    stringBuffer.append(record );
    stringBuffer.append(TEXT_34);
    
		String schemaName = schema.get("SCHEMA");
		boolean keyUpdated = false;
		for(int j=0; j<schemas.size();j++){
			if(i == j){
				continue;
			}
			Map<String, String> schema2 = schemas.get(j);
			if(schema.get("RECORD").equals(schema2.get("PARENT_RECORD"))){//FIM_003
			//get child node
				if(!keyUpdated){//FIM_003_1
					keyUpdated = true;
					String keyIndex = schema.get("KEY_COLUMN_INDEX");
					if(("").equals(keyIndex)){//no key ??? 

    stringBuffer.append(TEXT_35);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    
					}else{

    stringBuffer.append(TEXT_38);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(keyIndex);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(trimAll?".trim()":"");
    stringBuffer.append(TEXT_43);
    
					}
				}//FIM_003_1
				String schemaNameChild = schema2.get("SCHEMA");
				String cardinality = schema2.get("CARDINALITY");
				String childRecord = schema2.get("RECORD");

    stringBuffer.append(TEXT_44);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    if(("false").equals(schema2.get("IS_INFINITUDE"))){
    stringBuffer.append(TEXT_49);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    }
    stringBuffer.append(TEXT_54);
    stringBuffer.append(childRecord );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cardinality );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    
			}//FIM_003
		}
		if(!("\"\"").equals(schema.get("PARENT_RECORD"))){//has parent node
			String cardinality = schema.get("CARDINALITY");

    stringBuffer.append(TEXT_61);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    if(("false").equals(schema.get("IS_INFINITUDE"))){
    stringBuffer.append(TEXT_68);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    }
    stringBuffer.append(TEXT_73);
    stringBuffer.append(record );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cardinality );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    
		}
		String connection = schemaConnectionMap.get(schemaName);
		if(connection != null){//FIM_004 has connection......

    stringBuffer.append(TEXT_78);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_80);
    
			String parentRecord = schema.get("PARENT_RECORD");
			String parentSchema = null;
			for(int j=0; j<schemas.size();j++){
				if(i == j){
					continue;
				}
				Map<String, String> schema2 = schemas.get(j);
				if(schema2.get("RECORD").equals(parentRecord)){//has child node
					parentSchema = schema2.get("SCHEMA");
					break;
				}
			}
			
			List<IMetadataColumn> listColumns = connectionMapColumnList.get(connection);
			//
			for (int valueN=0; valueN < listColumns.size(); valueN++) {//FIM_005
				IMetadataColumn column = listColumns.get(valueN);
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
				if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){//FIM_006
					if(valueN + 1 == listColumns.size() && parentSchema != null){

    stringBuffer.append(TEXT_81);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(parentSchema );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    
					}else{

    stringBuffer.append(TEXT_86);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(trimAll?".trim()":"");
    stringBuffer.append(TEXT_91);
    
					}
				}else{ //FIM_006
					if(valueN + 1 == listColumns.size() && parentSchema != null){

    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(parentSchema );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    
					}else{

    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(trimAll?".trim()":"");
    stringBuffer.append(TEXT_100);
    
					}

    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
					} else if(javaType == JavaTypesManager.BYTE_ARRAY){ 

    stringBuffer.append(TEXT_103);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_107);
    
					}else if(javaType == JavaTypesManager.DATE) { 
						if(checkDate) {

    stringBuffer.append(TEXT_108);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_112);
    
						} else {

    stringBuffer.append(TEXT_113);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_117);
    
						}
					} else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 

    stringBuffer.append(TEXT_118);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_120);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_123);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_124);
    
					} else {

    stringBuffer.append(TEXT_125);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_127);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    
					}

    stringBuffer.append(TEXT_130);
    
					String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
					if(defaultValue == null) {

    stringBuffer.append(TEXT_131);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_133);
    
					} else {

    stringBuffer.append(TEXT_134);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_137);
    
					}

    stringBuffer.append(TEXT_138);
    
				} //FIM_006
			}//FIM_005
			//
		}//FIM_004

    stringBuffer.append(TEXT_139);
    
	}//FIN_002

    stringBuffer.append(TEXT_140);
    
        if (dieOnError) {
 
    stringBuffer.append(TEXT_141);
    
        } else {

    stringBuffer.append(TEXT_142);
    
			for(String conName : connections){

    stringBuffer.append(TEXT_143);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_144);
    
			}
        }

    stringBuffer.append(TEXT_145);
    
}else{//FIM_0000
//CVS OPTION
	String delim1 = fieldSeparator;     	
    String rowSeparator1 = rowSeparator;
    	
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
    if ("".equals(textEnclosure)) textEnclosure = "\0";
    if(("'").equals(textEnclosure)){
    	textEnclosure = "\\'";
    }
    
    String strIndex = ElementParameterParser.getValue(node, "__COLUMNINDEX__");
    	
	List<String> connections = new java.util.ArrayList<String>();
    Map<String, List<IMetadataColumn>> connectionMapColumnList = new java.util.HashMap<String, List<IMetadataColumn>>();
    Map<String, String> schemaConnectionMap = new java.util.HashMap<String, String>();
    
    for(int i=0;i<conns.size();i++){
    	IConnection connTemp = conns.get(i);
    	if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    		IMetadataTable tempMetadataTable = connTemp.getMetadataTable();
    		for(Map<String, String> schema : schemas){
				if(tempMetadataTable.getLabel().equals(schema.get("SCHEMA"))){
					schemaConnectionMap.put(schema.get("SCHEMA"), tempMetadataTable.getTableName());
					break;
				}
			}
    		List<IMetadataColumn> listColumns = tempMetadataTable.getListColumns();
    		connections.add(connTemp.getName());
    		connectionMapColumnList.put(connTemp.getName(), listColumns);
    	}
	}
	
	for(int i=0; i<schemas.size();i++){//FIM_001
		Map<String, String> schema = schemas.get(i);
		String schemaName = schema.get("SCHEMA");
		for(int j=0; j<schemas.size();j++){
			if(i == j){
				continue;
			}
			Map<String, String> schema2 = schemas.get(j);
			if(schema.get("RECORD").equals(schema2.get("PARENT_RECORD"))){//has child node
				String keyIndex = schema.get("KEY_COLUMN_INDEX");
				if(("").equals(keyIndex)){//no key __________PROBLEM_2_________

    stringBuffer.append(TEXT_146);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    
				}else{

    stringBuffer.append(TEXT_149);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_151);
    
				}
				break;
			}
		}
		if(!("\"\"").equals(schema.get("PARENT_RECORD"))){//has parent node
			String cardString = schema.get("CARDINALITY");
			String lowBound = String.valueOf(0);
			String highBound = String.valueOf(Integer.MAX_VALUE);
			//way to parse the cardinality string.............__________PROBLEM_3_________
			if(("").equals(cardString)){
				//do nothing
			}else if(cardString.contains(",")){
				String[] cards = cardString.split(",");
				lowBound = cards[0];
				highBound = cards[1];
			}else{
				lowBound = cardString;
				highBound = cardString;
			}

    stringBuffer.append(TEXT_152);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(lowBound );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(lowBound );
    stringBuffer.append(TEXT_158);
    if(("false").equals(schema.get("IS_INFINITUDE"))){
    stringBuffer.append(TEXT_159);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(highBound );
    stringBuffer.append(TEXT_162);
    }
    
		}
	}//FIM_001

    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(delim1);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(delim1);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(rowSeparator1);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(rowSeparator1);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(useMulSeprators?"';'" : "fieldSeparator_"+cid+"[0]" );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(TEXT_185);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_188);
    
	if(("\\\\").equals(escapeChar)){
        
    stringBuffer.append(TEXT_189);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_191);
    
	}else if(escapeChar.equals(textEnclosure)){
        
    stringBuffer.append(TEXT_192);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_194);
    
	}else{
        
    stringBuffer.append(TEXT_195);
    
	}
if(useMulSeprators==true){
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_197);
    
		for(int i=0; i<schemas.size(); i++){
			Map<String, String> schema = schemas.get(i);

    stringBuffer.append(TEXT_198);
    stringBuffer.append((i==0? "":","));
    stringBuffer.append(schema.get("FIELDDELIMITED") );
    
		}

    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_202);
    }
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_204);
    stringBuffer.append(strIndex );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_207);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_209);
    
if(useMulSeprators==false){

    stringBuffer.append(TEXT_210);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_212);
    if(trimAll){
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_215);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_220);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_221);
    }
    
}else{

    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_227);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_228);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_231);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_232);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_234);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_237);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_239);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_240);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_241);
    
	if(("\\\\").equals(escapeChar)){
        
    stringBuffer.append(TEXT_242);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_243);
    
	}else if(escapeChar.equals(textEnclosure)){
        
    stringBuffer.append(TEXT_244);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_245);
    
	}else{
        
    stringBuffer.append(TEXT_246);
    
	}
}
    stringBuffer.append(TEXT_247);
     
	for(String conName : connections){

    stringBuffer.append(TEXT_248);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_249);
     
	}

    stringBuffer.append(TEXT_250);
    if(useMulSeprators==true){
    stringBuffer.append(TEXT_251);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_252);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_253);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_254);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_255);
    if(trimAll){
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_258);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_260);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_261);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_262);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_263);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_264);
    }
    stringBuffer.append(TEXT_265);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_268);
    }
    stringBuffer.append(TEXT_269);
    
	for(int i=0; i<schemas.size();i++){//FIM_002
		Map<String, String> schema = schemas.get(i);
		String record = schema.get("RECORD");

    stringBuffer.append(TEXT_270);
    stringBuffer.append((i==0) ? "" : "else " );
    stringBuffer.append(TEXT_271);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_272);
    stringBuffer.append(record );
    stringBuffer.append(TEXT_273);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_275);
    stringBuffer.append(record );
    stringBuffer.append(TEXT_276);
    stringBuffer.append(useMulSeprators? " && delim_"+cid+"[i_"+cid+"].equals("+ schema.get("FIELDDELIMITED") +")":"" );
    stringBuffer.append(TEXT_277);
    
		String schemaName = schema.get("SCHEMA");
		boolean keyUpdated = false;
		for(int j=0; j<schemas.size();j++){
			if(i == j){
				continue;
			}
			Map<String, String> schema2 = schemas.get(j);
			if(schema.get("RECORD").equals(schema2.get("PARENT_RECORD"))){//FIM_003
			//get child node
				if(!keyUpdated){//FIM_003_1
					keyUpdated = true;
					String keyIndex = schema.get("KEY_COLUMN_INDEX");
					if(("").equals(keyIndex)){//no key ??? 

    stringBuffer.append(TEXT_278);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_279);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_280);
    
				}else{

    stringBuffer.append(TEXT_281);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_283);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_284);
    stringBuffer.append(keyIndex);
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_286);
    stringBuffer.append(keyIndex );
    stringBuffer.append(TEXT_287);
    
					}
				}//FIM_003_1
				String schemaNameChild = schema2.get("SCHEMA");
				String cardinality = schema2.get("CARDINALITY");
				String childRecord = schema2.get("RECORD");

    stringBuffer.append(TEXT_288);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_290);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_291);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_292);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_293);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_294);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_295);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_296);
    stringBuffer.append(childRecord );
    stringBuffer.append(TEXT_297);
    stringBuffer.append(cardinality );
    stringBuffer.append(TEXT_298);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_299);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_300);
    stringBuffer.append(schemaNameChild );
    stringBuffer.append(TEXT_301);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_302);
    
			}//FIM_003
		}
		if(!("\"\"").equals(schema.get("PARENT_RECORD"))){//has parent node
			String cardinality = schema.get("CARDINALITY");

    stringBuffer.append(TEXT_303);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_304);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_305);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_308);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_309);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_310);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_311);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_312);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_313);
    stringBuffer.append(record );
    stringBuffer.append(TEXT_314);
    stringBuffer.append(cardinality );
    stringBuffer.append(TEXT_315);
    stringBuffer.append(schemaName );
    stringBuffer.append(TEXT_316);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_317);
    
		}
		String connection = schemaConnectionMap.get(schemaName);
		if(connection != null){//FIM_004 has connection......

    stringBuffer.append(TEXT_318);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_319);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_320);
    
			String parentRecord = schema.get("PARENT_RECORD");
			String parentSchema = null;
			for(int j=0; j<schemas.size();j++){
				if(i == j){
					continue;
				}
				Map<String, String> schema2 = schemas.get(j);
				if(schema2.get("RECORD").equals(parentRecord)){//has child node
					parentSchema = schema2.get("SCHEMA");
					break;
				}
			}
			
			List<IMetadataColumn> listColumns = connectionMapColumnList.get(connection);
			//
			for (int valueN=0; valueN < listColumns.size(); valueN++) {//FIM_005
				IMetadataColumn column = listColumns.get(valueN);
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
				if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){//FIM_006
					if(valueN + 1 == listColumns.size() && parentSchema != null){

    stringBuffer.append(TEXT_321);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_322);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_323);
    stringBuffer.append(parentSchema );
    stringBuffer.append(TEXT_324);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_325);
    
					}else{

    stringBuffer.append(TEXT_326);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_327);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_328);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_329);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_330);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_331);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_332);
    
					}
				}else{ //FIM_006
					if(valueN + 1 == listColumns.size() && parentSchema != null){

    stringBuffer.append(TEXT_333);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_334);
    stringBuffer.append(parentSchema );
    stringBuffer.append(TEXT_335);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_336);
    
					}else{

    stringBuffer.append(TEXT_337);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_338);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_339);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_340);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_341);
    stringBuffer.append(valueN );
    stringBuffer.append(TEXT_342);
    
					}

    stringBuffer.append(TEXT_343);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_344);
    
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
					} else if(javaType == JavaTypesManager.BYTE_ARRAY){ 

    stringBuffer.append(TEXT_345);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_346);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_347);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_348);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_349);
    
					}else if(javaType == JavaTypesManager.DATE) { 
						if(checkDate) {

    stringBuffer.append(TEXT_350);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_351);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_352);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_353);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_354);
    
						} else {

    stringBuffer.append(TEXT_355);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_356);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_357);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_358);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_359);
    
						}
					} else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 

    stringBuffer.append(TEXT_360);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_361);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_362);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_363);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_364);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_365);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_366);
    
					} else {

    stringBuffer.append(TEXT_367);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_368);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_369);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_371);
    
					}

    stringBuffer.append(TEXT_372);
    
					String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
					if(defaultValue == null) {

    stringBuffer.append(TEXT_373);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_374);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_375);
    
					} else {

    stringBuffer.append(TEXT_376);
    stringBuffer.append(connection );
    stringBuffer.append(TEXT_377);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_378);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_379);
    
					}

    stringBuffer.append(TEXT_380);
    
				} //FIM_006
			}//FIM_005
			if(useMulSeprators==true){

    stringBuffer.append(TEXT_381);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_382);
    
			}
		}//FIM_004

    stringBuffer.append(TEXT_383);
    
	}//FIN_002

    stringBuffer.append(TEXT_384);
    
        if (dieOnError) {
 
    stringBuffer.append(TEXT_385);
    
        } else {

    stringBuffer.append(TEXT_386);
    
			for(String conName : connections){

    stringBuffer.append(TEXT_387);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_388);
    
			}
        }

    stringBuffer.append(TEXT_389);
    stringBuffer.append(TEXT_390);
    stringBuffer.append(useMulSeprators==true? "}":"");
    
}
}//FIM_000
////////////////////////

    stringBuffer.append(TEXT_391);
    return stringBuffer.toString();
  }
}
