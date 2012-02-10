package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

/**
 * add by xzhang
 */
public class TAdvancedFileOutputXMLMainJava {

  protected static String nl;
  public static synchronized TAdvancedFileOutputXMLMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAdvancedFileOutputXMLMainJava result = new TAdvancedFileOutputXMLMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\tvalueMap_";
  protected final String TEXT_2 = ".get(\"";
  protected final String TEXT_3 = "\")";
  protected final String TEXT_4 = NL + "\t(";
  protected final String TEXT_5 = NL + "\t\t";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = " != null?";
  protected final String TEXT_8 = NL + "    \t\tFormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_9 = "), ";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = ")\t\t\t\t\t";
  protected final String TEXT_12 = NL + "    \t\tFormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = "), ";
  protected final String TEXT_15 = ",";
  protected final String TEXT_16 = ")\t\t\t\t\t\t";
  protected final String TEXT_17 = NL + "            String.valueOf(";
  protected final String TEXT_18 = ".";
  protected final String TEXT_19 = ")";
  protected final String TEXT_20 = NL + "            FormatterUtils.format_Date(";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = ",";
  protected final String TEXT_23 = ")";
  protected final String TEXT_24 = NL + "\t\t\t";
  protected final String TEXT_25 = ".";
  protected final String TEXT_26 = NL + "\t\t\tString.valueOf(";
  protected final String TEXT_27 = ")";
  protected final String TEXT_28 = NL + "            ";
  protected final String TEXT_29 = ".";
  protected final String TEXT_30 = ".toString()";
  protected final String TEXT_31 = ":";
  protected final String TEXT_32 = "null";
  protected final String TEXT_33 = NL + "\t\t)";
  protected final String TEXT_34 = NL + "\t\t\t";
  protected final String TEXT_35 = "_";
  protected final String TEXT_36 = ".setName(\"";
  protected final String TEXT_37 = "\");";
  protected final String TEXT_38 = NL + "\t\t\tif (";
  protected final String TEXT_39 = "_";
  protected final String TEXT_40 = ".content().size() == 0 " + NL + "\t\t\t\t&& ";
  protected final String TEXT_41 = "_";
  protected final String TEXT_42 = ".attributes().size() == 0 " + NL + "\t\t\t\t&& ";
  protected final String TEXT_43 = "_";
  protected final String TEXT_44 = ".declaredNamespaces().size() == 0) {";
  protected final String TEXT_45 = NL + "                ";
  protected final String TEXT_46 = "_";
  protected final String TEXT_47 = ".remove(";
  protected final String TEXT_48 = "_";
  protected final String TEXT_49 = ");" + NL + "            }" + NL + "\t\t\t";
  protected final String TEXT_50 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_51 = "_";
  protected final String TEXT_52 = ";" + NL + "\t\tif (";
  protected final String TEXT_53 = "_";
  protected final String TEXT_54 = ".getNamespaceForPrefix(\"";
  protected final String TEXT_55 = "\") == null) {";
  protected final String TEXT_56 = NL + "            ";
  protected final String TEXT_57 = "_";
  protected final String TEXT_58 = " = org.dom4j.DocumentHelper.createElement(\"";
  protected final String TEXT_59 = "\");" + NL + "        } else {" + NL + "        \t";
  protected final String TEXT_60 = "_";
  protected final String TEXT_61 = " = org.dom4j.DocumentHelper.createElement(\"";
  protected final String TEXT_62 = "\");" + NL + "        }";
  protected final String TEXT_63 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_64 = "_";
  protected final String TEXT_65 = " = org.dom4j.DocumentHelper.createElement(\"";
  protected final String TEXT_66 = "\");";
  protected final String TEXT_67 = NL + "\t\tint app_size_";
  protected final String TEXT_68 = "=";
  protected final String TEXT_69 = "_";
  protected final String TEXT_70 = ".elements(\"";
  protected final String TEXT_71 = "\").size();" + NL + "\t\tif(app_size_";
  protected final String TEXT_72 = " > 0){" + NL + "\t\t\torders_";
  protected final String TEXT_73 = "[";
  protected final String TEXT_74 = "] =1+ ";
  protected final String TEXT_75 = "_";
  protected final String TEXT_76 = ".elements().indexOf(";
  protected final String TEXT_77 = "_";
  protected final String TEXT_78 = ".elements(\"";
  protected final String TEXT_79 = "\").get(app_size_";
  protected final String TEXT_80 = "-1));" + NL + "\t\t}else{";
  protected final String TEXT_81 = NL + "\t\t\torders_";
  protected final String TEXT_82 = "[";
  protected final String TEXT_83 = "] = ";
  protected final String TEXT_84 = "_";
  protected final String TEXT_85 = ".elements().size();";
  protected final String TEXT_86 = NL + "\t\t\torders_";
  protected final String TEXT_87 = "[";
  protected final String TEXT_88 = "] = ";
  protected final String TEXT_89 = ";" + NL + "\t\t\tif(orders_";
  protected final String TEXT_90 = "[";
  protected final String TEXT_91 = "] == 0 && ";
  protected final String TEXT_92 = "_";
  protected final String TEXT_93 = ".elements().size() != 0 && !bl_";
  protected final String TEXT_94 = ") {" + NL + "\t\t\t\torders_";
  protected final String TEXT_95 = "[";
  protected final String TEXT_96 = "] = ";
  protected final String TEXT_97 = "_";
  protected final String TEXT_98 = ".elements().size();" + NL + "\t\t\t}";
  protected final String TEXT_99 = NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_100 = "_";
  protected final String TEXT_101 = ".elements().add(orders_";
  protected final String TEXT_102 = "[";
  protected final String TEXT_103 = "],";
  protected final String TEXT_104 = "_";
  protected final String TEXT_105 = ");";
  protected final String TEXT_106 = NL + "        if(orders_";
  protected final String TEXT_107 = "[";
  protected final String TEXT_108 = "]==0){" + NL + "        \torders_";
  protected final String TEXT_109 = "[";
  protected final String TEXT_110 = "] = ";
  protected final String TEXT_111 = ";" + NL + "        }" + NL + "        if(";
  protected final String TEXT_112 = " < orders_";
  protected final String TEXT_113 = ".length){" + NL + "        \t\torders_";
  protected final String TEXT_114 = "[";
  protected final String TEXT_115 = "] = 0;" + NL + "        }";
  protected final String TEXT_116 = NL + "        ";
  protected final String TEXT_117 = "_";
  protected final String TEXT_118 = ".elements().add(orders_";
  protected final String TEXT_119 = "[";
  protected final String TEXT_120 = "]++,";
  protected final String TEXT_121 = "_";
  protected final String TEXT_122 = ");";
  protected final String TEXT_123 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_124 = "_";
  protected final String TEXT_125 = ";" + NL + "\t\tif (";
  protected final String TEXT_126 = "_";
  protected final String TEXT_127 = ".getNamespaceForPrefix(\"";
  protected final String TEXT_128 = "\") == null) {";
  protected final String TEXT_129 = NL + "            ";
  protected final String TEXT_130 = "_";
  protected final String TEXT_131 = " = ";
  protected final String TEXT_132 = "_";
  protected final String TEXT_133 = ".addElement(\"";
  protected final String TEXT_134 = "\");" + NL + "        } else {" + NL + "        \t";
  protected final String TEXT_135 = "_";
  protected final String TEXT_136 = " = ";
  protected final String TEXT_137 = "_";
  protected final String TEXT_138 = ".addElement(\"";
  protected final String TEXT_139 = "\");" + NL + "        }";
  protected final String TEXT_140 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_141 = "_";
  protected final String TEXT_142 = " = ";
  protected final String TEXT_143 = "_";
  protected final String TEXT_144 = ".addElement(\"";
  protected final String TEXT_145 = "\");";
  protected final String TEXT_146 = NL + "\t\tsubTreeRootParent_";
  protected final String TEXT_147 = " = ";
  protected final String TEXT_148 = "_";
  protected final String TEXT_149 = ";";
  protected final String TEXT_150 = NL + "\t\tif(";
  protected final String TEXT_151 = "!=null){" + NL + "\t\t\tnestXMLTool_";
  protected final String TEXT_152 = " .parseAndAdd(";
  protected final String TEXT_153 = "_";
  protected final String TEXT_154 = ",";
  protected final String TEXT_155 = ");" + NL + "\t\t}";
  protected final String TEXT_156 = NL + "\t\telse{" + NL + "\t\t\tnestXMLTool_";
  protected final String TEXT_157 = " .parseAndAdd(";
  protected final String TEXT_158 = "_";
  protected final String TEXT_159 = ",\"\");" + NL + "\t\t\t";
  protected final String TEXT_160 = "_";
  protected final String TEXT_161 = ".addAttribute(\"xsi:nil\",\"true\");" + NL + "\t\t}";
  protected final String TEXT_162 = NL + "\t\t\t\t\tif(";
  protected final String TEXT_163 = "!=null){" + NL + "\t\t\t\t\t\tnestXMLTool_";
  protected final String TEXT_164 = " .setText(";
  protected final String TEXT_165 = "_";
  protected final String TEXT_166 = ", ParserUtils.parseTo_Document(";
  protected final String TEXT_167 = ").getDocument().getRootElement().asXML());" + NL + "\t\t\t\t\t}";
  protected final String TEXT_168 = NL + "\t\t\t\t\tif(";
  protected final String TEXT_169 = "!=null){" + NL + "\t\t\t\t\t\tnestXMLTool_";
  protected final String TEXT_170 = " .setText(";
  protected final String TEXT_171 = "_";
  protected final String TEXT_172 = ",";
  protected final String TEXT_173 = ");" + NL + "\t\t\t\t\t}";
  protected final String TEXT_174 = NL + "\t\telse{" + NL + "\t\t\t";
  protected final String TEXT_175 = "_";
  protected final String TEXT_176 = ".setText(\"\");" + NL + "\t\t\t";
  protected final String TEXT_177 = "_";
  protected final String TEXT_178 = ".addAttribute(\"xsi:nil\",\"true\");" + NL + "\t\t}";
  protected final String TEXT_179 = NL + "\t\tnestXMLTool_";
  protected final String TEXT_180 = ".parseAndAdd(";
  protected final String TEXT_181 = "_";
  protected final String TEXT_182 = ",\"";
  protected final String TEXT_183 = "\");" + NL;
  protected final String TEXT_184 = " " + NL + "\t\t  if (";
  protected final String TEXT_185 = " != null){" + NL + "\t\t\t";
  protected final String TEXT_186 = "_";
  protected final String TEXT_187 = ".addAttribute(\"";
  protected final String TEXT_188 = "\", ";
  protected final String TEXT_189 = ");" + NL + "\t\t  } ";
  protected final String TEXT_190 = " else {" + NL + "\t\t    ";
  protected final String TEXT_191 = "_";
  protected final String TEXT_192 = ".addAttribute(\"";
  protected final String TEXT_193 = "\", \"\");" + NL + "          }";
  protected final String TEXT_194 = NL + "\t\t    ";
  protected final String TEXT_195 = "_";
  protected final String TEXT_196 = ".addAttribute(\"";
  protected final String TEXT_197 = "\", \"";
  protected final String TEXT_198 = "\");";
  protected final String TEXT_199 = NL + "\t\t    ";
  protected final String TEXT_200 = "_";
  protected final String TEXT_201 = ".addAttribute(\"";
  protected final String TEXT_202 = "\", \"\");" + NL + "\t\t  ";
  protected final String TEXT_203 = NL + "\t\tif(";
  protected final String TEXT_204 = "!=null){" + NL + "\t\t\t";
  protected final String TEXT_205 = "_";
  protected final String TEXT_206 = ".addNamespace(\"";
  protected final String TEXT_207 = "\",TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_208 = "));";
  protected final String TEXT_209 = NL + "        \t";
  protected final String TEXT_210 = "_";
  protected final String TEXT_211 = ".setQName(org.dom4j.DocumentHelper.createQName(";
  protected final String TEXT_212 = "_";
  protected final String TEXT_213 = ".getName()," + NL + "        \torg.dom4j.DocumentHelper.createNamespace(\"\",TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_214 = "))));";
  protected final String TEXT_215 = NL + "\t\t}";
  protected final String TEXT_216 = NL + "\t\t\t";
  protected final String TEXT_217 = "_";
  protected final String TEXT_218 = ".addNamespace(\"";
  protected final String TEXT_219 = "\",TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_220 = "\"));";
  protected final String TEXT_221 = NL + "        \t";
  protected final String TEXT_222 = "_";
  protected final String TEXT_223 = ".setQName(org.dom4j.DocumentHelper.createQName(";
  protected final String TEXT_224 = "_";
  protected final String TEXT_225 = ".getName()," + NL + "        \torg.dom4j.DocumentHelper.createNamespace(\"\",TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_226 = "\"))));";
  protected final String TEXT_227 = "true";
  protected final String TEXT_228 = " && (";
  protected final String TEXT_229 = "(";
  protected final String TEXT_230 = "==null && ";
  protected final String TEXT_231 = " == null) || (true &&";
  protected final String TEXT_232 = "!=null" + NL + " && ";
  protected final String TEXT_233 = ".getText()!=null" + NL + " && ";
  protected final String TEXT_234 = ".getText().equals(";
  protected final String TEXT_235 = ")";
  protected final String TEXT_236 = ")";
  protected final String TEXT_237 = NL + ")";
  protected final String TEXT_238 = " && (";
  protected final String TEXT_239 = "(";
  protected final String TEXT_240 = "==null && ";
  protected final String TEXT_241 = ".attribute(\"";
  protected final String TEXT_242 = "\") == null) || (true && ";
  protected final String TEXT_243 = ".attribute(\"";
  protected final String TEXT_244 = "\")!=null" + NL + "&& ";
  protected final String TEXT_245 = ".attribute(\"";
  protected final String TEXT_246 = "\").getText()!=null" + NL + "&& ";
  protected final String TEXT_247 = ".attribute(\"";
  protected final String TEXT_248 = "\").getText().equals(";
  protected final String TEXT_249 = ")";
  protected final String TEXT_250 = ")";
  protected final String TEXT_251 = ")";
  protected final String TEXT_252 = NL;
  protected final String TEXT_253 = ".element(";
  protected final String TEXT_254 = ".getQName(\"";
  protected final String TEXT_255 = "\"))";
  protected final String TEXT_256 = NL + "    \t// buffer the start tabs to group buffer" + NL + "    \tgroupBuffer_";
  protected final String TEXT_257 = "[";
  protected final String TEXT_258 = "] = buf_";
  protected final String TEXT_259 = ".toString();" + NL + "        buf_";
  protected final String TEXT_260 = " = new StringBuffer();";
  protected final String TEXT_261 = NL + "\t\tstartTabs_";
  protected final String TEXT_262 = "[";
  protected final String TEXT_263 = "] = buf_";
  protected final String TEXT_264 = ".toString();" + NL + "        buf_";
  protected final String TEXT_265 = " = new StringBuffer();";
  protected final String TEXT_266 = NL + "\t\tout_";
  protected final String TEXT_267 = ".write(buf_";
  protected final String TEXT_268 = ".toString());" + NL + "        buf_";
  protected final String TEXT_269 = " = new StringBuffer();";
  protected final String TEXT_270 = NL + "\t\tif( false";
  protected final String TEXT_271 = " || valueMap_";
  protected final String TEXT_272 = ".get(\"";
  protected final String TEXT_273 = "\") != null";
  protected final String TEXT_274 = " || true " + NL + "                    \t";
  protected final String TEXT_275 = NL + "\t\t){";
  protected final String TEXT_276 = NL + "\t\t}";
  protected final String TEXT_277 = NL + "\t\tbuf_";
  protected final String TEXT_278 = ".append(\"";
  protected final String TEXT_279 = "\");" + NL + "\t\tbuf_";
  protected final String TEXT_280 = ".append(\"";
  protected final String TEXT_281 = "<";
  protected final String TEXT_282 = "\");";
  protected final String TEXT_283 = NL + "\t\tbuf_";
  protected final String TEXT_284 = ".append(\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"\");" + NL + "\t\tbuf_";
  protected final String TEXT_285 = ".append(\" xsi:noNamespaceSchemaLocation= \\\"\"+ file_";
  protected final String TEXT_286 = ".substring(file_";
  protected final String TEXT_287 = ".lastIndexOf(\"/\")+1)+\".xsd\"+\"\\\"\");";
  protected final String TEXT_288 = NL + "\t\tif(";
  protected final String TEXT_289 = " == null){" + NL + "\t\t\tbuf_";
  protected final String TEXT_290 = ".append(\" xsi:nil=\\\"true\\\"\");" + NL + "\t\t}";
  protected final String TEXT_291 = NL + "\t\tbuf_";
  protected final String TEXT_292 = ".append(\">\");";
  protected final String TEXT_293 = NL + "\t\tbuf_";
  protected final String TEXT_294 = ".append(\"";
  protected final String TEXT_295 = "\");" + NL + "\t\tbuf_";
  protected final String TEXT_296 = ".append(\"";
  protected final String TEXT_297 = "</";
  protected final String TEXT_298 = ">\");";
  protected final String TEXT_299 = NL + "\t\tbuf_";
  protected final String TEXT_300 = ".append(\"</";
  protected final String TEXT_301 = ">\");";
  protected final String TEXT_302 = NL + "\t\tif(";
  protected final String TEXT_303 = "!=null){" + NL + "\t\t\tif(routines.system.XMLHelper.getInstance().isValid(";
  protected final String TEXT_304 = ")){" + NL + "\t\t\t\tbuf_";
  protected final String TEXT_305 = ".append(";
  protected final String TEXT_306 = ");" + NL + "\t\t\t} else {" + NL + "\t\t\t\tbuf_";
  protected final String TEXT_307 = ".append(TalendString.checkCDATAForXML(";
  protected final String TEXT_308 = "));" + NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_309 = NL + "\t\tif(";
  protected final String TEXT_310 = "!=null){" + NL + "\t\t\tbuf_";
  protected final String TEXT_311 = ".append(TalendString.checkCDATAForXML(";
  protected final String TEXT_312 = "));" + NL + "\t\t}";
  protected final String TEXT_313 = NL + "\t\tif(routines.system.XMLHelper.getInstance().isValid(\"";
  protected final String TEXT_314 = "\")){" + NL + "\t\t\tbuf_";
  protected final String TEXT_315 = ".append(\"";
  protected final String TEXT_316 = "\");" + NL + "\t\t} else {" + NL + "\t\t\tbuf_";
  protected final String TEXT_317 = ".append(TalendString.checkCDATAForXML(\"";
  protected final String TEXT_318 = "\"));" + NL + "\t\t}";
  protected final String TEXT_319 = NL + "\t\t  if (";
  protected final String TEXT_320 = "!=null){" + NL + "\t\t\tbuf_";
  protected final String TEXT_321 = ".append(\" ";
  protected final String TEXT_322 = "=\\\"\"+TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_323 = ")+\"\\\"\");" + NL + "\t\t  } ";
  protected final String TEXT_324 = " else{" + NL + "\t\t    buf_";
  protected final String TEXT_325 = ".append(\" ";
  protected final String TEXT_326 = "=\\\"\\\"\");" + NL + "\t\t  }";
  protected final String TEXT_327 = NL + "\t\t    buf_";
  protected final String TEXT_328 = ".append(\" ";
  protected final String TEXT_329 = "=\\\"\"+TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_330 = "\")+\"\\\"\");";
  protected final String TEXT_331 = NL + "\t\t    buf_";
  protected final String TEXT_332 = ".append(\" ";
  protected final String TEXT_333 = "=\\\"\\\"\");" + NL + "\t\t  ";
  protected final String TEXT_334 = NL + "\t\tif(";
  protected final String TEXT_335 = "!=null){";
  protected final String TEXT_336 = NL + "        \tbuf_";
  protected final String TEXT_337 = ".append(\" xmlns=\\\"\"+TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_338 = ")+\"\\\"\");";
  protected final String TEXT_339 = NL + "\t\t\tbuf_";
  protected final String TEXT_340 = ".append(\" xmlns:";
  protected final String TEXT_341 = "=\\\"\"+TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_342 = ")+\"\\\"\");";
  protected final String TEXT_343 = NL + "\t\t}";
  protected final String TEXT_344 = NL + "        \tbuf_";
  protected final String TEXT_345 = ".append(\" xmlns=\\\"\"+TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_346 = "\")+\"\\\"\");";
  protected final String TEXT_347 = NL + "\t\t\tbuf_";
  protected final String TEXT_348 = ".append(\" xmlns:";
  protected final String TEXT_349 = "=\\\"\"+TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_350 = "\")+\"\\\"\");";
  protected final String TEXT_351 = NL + "\tnb_line_";
  protected final String TEXT_352 = "++;" + NL + "\tvalueMap_";
  protected final String TEXT_353 = ".clear();";
  protected final String TEXT_354 = NL + "\tvalueMap_";
  protected final String TEXT_355 = ".put(\"";
  protected final String TEXT_356 = "\", ";
  protected final String TEXT_357 = ");";
  protected final String TEXT_358 = NL + "\torg.dom4j.Element subTreeRootParent_";
  protected final String TEXT_359 = " = null;" + NL + "\t" + NL + "\t// build root xml tree " + NL + "\tif (needRoot_";
  protected final String TEXT_360 = ") {" + NL + "\t\tneedRoot_";
  protected final String TEXT_361 = "=false;" + NL + "\t\tif(orders_";
  protected final String TEXT_362 = ".length>0){" + NL + "\t\t\torders_";
  protected final String TEXT_363 = "[0] = 0;" + NL + "\t\t}";
  protected final String TEXT_364 = NL + "\t\troot4Group_";
  protected final String TEXT_365 = " = subTreeRootParent_";
  protected final String TEXT_366 = ";" + NL + "\t}else{" + NL + "\t\tsubTreeRootParent_";
  protected final String TEXT_367 = "=root4Group_";
  protected final String TEXT_368 = ";" + NL + "\t}" + NL + "\t// build group xml tree ";
  protected final String TEXT_369 = NL + "\t}";
  protected final String TEXT_370 = NL + "\t\t" + NL + "\t\tsubTreeRootParent_";
  protected final String TEXT_371 = "=(org.dom4j.Element)doc_";
  protected final String TEXT_372 = ".selectSingleNode(\"";
  protected final String TEXT_373 = "\");" + NL + "\t\t" + NL + "\t\tif(subTreeRootParent_";
  protected final String TEXT_374 = "==null){" + NL + "\t\t\tsubTreeRootParent_";
  protected final String TEXT_375 = " = (org.dom4j.Element)doc_";
  protected final String TEXT_376 = ".selectSingleNode(\"";
  protected final String TEXT_377 = "\");" + NL + "\t\t\tif(subTreeRootParent_";
  protected final String TEXT_378 = "==null) {" + NL + "\t\t\t\tSystem.err.println(\"WARN : the root tag is not same when appending\");" + NL + "\t\t\t} else { " + NL + "\t\t\t\tString subRootPath = subTreeRootParent_";
  protected final String TEXT_379 = ".getPath().replace(\"*[name()='\",\"\").replace(\"']\", \"\");" + NL + "\t\t\t\tif(!\"";
  protected final String TEXT_380 = "\".equals(subRootPath)) {" + NL + "\t\t\t\t\tSystem.err.println(\"WARN : the root tag is not same on namespace prefix when appending\");" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t}else{" + NL + "\t\t\tsubTreeRootParent_";
  protected final String TEXT_381 = " =subTreeRootParent_";
  protected final String TEXT_382 = ".getParent();" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tboolean bl_";
  protected final String TEXT_383 = "= false;//true:find the insert node;false:not";
  protected final String TEXT_384 = NL + "\t\tif(bl_";
  protected final String TEXT_385 = "==false){" + NL + "\t\t\tjava.util.List<org.dom4j.Element> listNodes= subTreeRootParent_";
  protected final String TEXT_386 = ".elements();" + NL + "\t\t\tif(listNodes!=null && listNodes.size()>0){" + NL + "\t\t\t\tint j=0;" + NL + "\t\t\t\tfor(j=0;j<listNodes.size();j++){" + NL + "\t\t\t\t\torg.dom4j.Element tempElem =listNodes.get(j);" + NL + "\t\t\t\t\tif((\"";
  protected final String TEXT_387 = "\").equals(tempElem.getPath()!=null ? tempElem.getPath().replace(\"*[name()='\",\"\").replace(\"']\", \"\") : \"\")){" + NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_388 = "){" + NL + "\t\t\t\t\t\t\tsubTreeRootParent_";
  protected final String TEXT_389 = " =  tempElem;" + NL + "\t\t\t\t\t\t\tbreak;" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t\tif(j>=listNodes.size()){" + NL + "\t\t\t\t\tbl_";
  protected final String TEXT_390 = "=true;" + NL + "\t\t\t\t}" + NL + "\t\t\t}else{" + NL + "\t\t\t\tbl_";
  protected final String TEXT_391 = "=true;" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\tif(bl_";
  protected final String TEXT_392 = "==true){";
  protected final String TEXT_393 = NL + "\t\t}";
  protected final String TEXT_394 = NL + "\tboolean isNewElememt_";
  protected final String TEXT_395 = " = false;";
  protected final String TEXT_396 = NL + "\tif(isNewElememt_";
  protected final String TEXT_397 = " || groupbyList_";
  protected final String TEXT_398 = ".size()<=";
  protected final String TEXT_399 = " || groupbyList_";
  protected final String TEXT_400 = ".get(";
  protected final String TEXT_401 = ")==null";
  protected final String TEXT_402 = NL + "\t|| ( groupbyList_";
  protected final String TEXT_403 = ".get(";
  protected final String TEXT_404 = ").get(";
  protected final String TEXT_405 = ")!=null " + NL + "\t\t? !groupbyList_";
  protected final String TEXT_406 = ".get(";
  protected final String TEXT_407 = ").get(";
  protected final String TEXT_408 = ").equals(";
  protected final String TEXT_409 = ") " + NL + "\t\t: ";
  protected final String TEXT_410 = "!=null )";
  protected final String TEXT_411 = NL + "\t){";
  protected final String TEXT_412 = NL + "\t\tif(groupbyList_";
  protected final String TEXT_413 = ".size()<=";
  protected final String TEXT_414 = "){" + NL + "        \tgroupbyList_";
  protected final String TEXT_415 = ".add(new java.util.ArrayList<String>());" + NL + "        }else{" + NL + "        \tgroupbyList_";
  protected final String TEXT_416 = ".get(";
  protected final String TEXT_417 = ").clear();" + NL + "        }";
  protected final String TEXT_418 = NL + "\t\tgroupbyList_";
  protected final String TEXT_419 = ".get(";
  protected final String TEXT_420 = ").add(";
  protected final String TEXT_421 = ");";
  protected final String TEXT_422 = NL + "        isNewElememt_";
  protected final String TEXT_423 = "=true;" + NL + "        if(groupElementList_";
  protected final String TEXT_424 = ".size()<=";
  protected final String TEXT_425 = "){" + NL + "\t\t\tgroupElementList_";
  protected final String TEXT_426 = ".add(group";
  protected final String TEXT_427 = "__";
  protected final String TEXT_428 = ");" + NL + "        }else{" + NL + "        \tgroupElementList_";
  protected final String TEXT_429 = ".set(";
  protected final String TEXT_430 = ",group";
  protected final String TEXT_431 = "__";
  protected final String TEXT_432 = ");" + NL + "        }" + NL + "        " + NL + "\t}else{" + NL + "\t\tsubTreeRootParent_";
  protected final String TEXT_433 = "=groupElementList_";
  protected final String TEXT_434 = ".get(";
  protected final String TEXT_435 = ");" + NL + "\t}" + NL;
  protected final String TEXT_436 = NL + "\t// build loop xml tree";
  protected final String TEXT_437 = NL + "    currentRowCount_";
  protected final String TEXT_438 = "++;" + NL + "    if(currentRowCount_";
  protected final String TEXT_439 = " == ";
  protected final String TEXT_440 = "){" + NL + "    \tneedRoot_";
  protected final String TEXT_441 = "  = true;" + NL + "    \tfileName_";
  protected final String TEXT_442 = " = file_";
  protected final String TEXT_443 = " + currentFileCount_";
  protected final String TEXT_444 = " + suffix_";
  protected final String TEXT_445 = ";" + NL + "        currentRowCount_";
  protected final String TEXT_446 = " = 0;" + NL + "        currentFileCount_";
  protected final String TEXT_447 = "++;" + NL + "    \tgroupbyList_";
  protected final String TEXT_448 = ".clear();" + NL + "" + NL + "    \tjava.io.FileOutputStream stream_";
  protected final String TEXT_449 = " = new java.io.FileOutputStream(fileName_";
  protected final String TEXT_450 = ");" + NL + "        org.dom4j.io.XMLWriter output_";
  protected final String TEXT_451 = " = new org.dom4j.io.XMLWriter(stream_";
  protected final String TEXT_452 = ", format_";
  protected final String TEXT_453 = ");";
  protected final String TEXT_454 = NL + "\t\tdoc_";
  protected final String TEXT_455 = ".getRootElement().addAttribute(\"xsi:noNamespaceSchemaLocation\", file_";
  protected final String TEXT_456 = ".substring(file_";
  protected final String TEXT_457 = ".lastIndexOf(\"/\")+1)+\".xsd\");" + NL + "        doc_";
  protected final String TEXT_458 = ".getRootElement().addNamespace(\"xsi\", \"http://www.w3.org/2001/XMLSchema-instance\");";
  protected final String TEXT_459 = "        " + NL + "        nestXMLTool_";
  protected final String TEXT_460 = ".replaceDefaultNameSpace(doc_";
  protected final String TEXT_461 = ".getRootElement());";
  protected final String TEXT_462 = NL + "\t\tnestXMLTool_";
  protected final String TEXT_463 = ".removeEmptyElement(doc_";
  protected final String TEXT_464 = ".getRootElement());";
  protected final String TEXT_465 = NL + "        output_";
  protected final String TEXT_466 = ".write(doc_";
  protected final String TEXT_467 = ");" + NL + "        output_";
  protected final String TEXT_468 = ".close();" + NL + "        doc_";
  protected final String TEXT_469 = "  = org.dom4j.DocumentHelper.createDocument();" + NL + "    \tgroupElementList_";
  protected final String TEXT_470 = ".clear();" + NL + "    }";
  protected final String TEXT_471 = NL + "\tStringBuffer buf_";
  protected final String TEXT_472 = " = new StringBuffer();" + NL + "\t//init value is 0 not -1, because it will output the root tab when all the row value is null." + NL + "\tint unNullMaxIndex_";
  protected final String TEXT_473 = " = 0;" + NL + "" + NL + "\t// build root xml tree " + NL + "\tif (needRoot_";
  protected final String TEXT_474 = ") {" + NL + "\t\tneedRoot_";
  protected final String TEXT_475 = "=false;";
  protected final String TEXT_476 = NL + "\t\tif( false";
  protected final String TEXT_477 = " || valueMap_";
  protected final String TEXT_478 = ".get(\"";
  protected final String TEXT_479 = "\") != null";
  protected final String TEXT_480 = NL + "\t\t){" + NL + "\t\t\tunNullMaxIndex_";
  protected final String TEXT_481 = " = ";
  protected final String TEXT_482 = ";" + NL + "\t\t}";
  protected final String TEXT_483 = NL + "\t\tendTabs_";
  protected final String TEXT_484 = "[";
  protected final String TEXT_485 = "] = buf_";
  protected final String TEXT_486 = ".toString();" + NL + "\t\tbuf_";
  protected final String TEXT_487 = " = new StringBuffer();";
  protected final String TEXT_488 = NL + "\t}" + NL + "\t" + NL + "\t// build group xml tree ";
  protected final String TEXT_489 = NL + "\tboolean isNewElememt_";
  protected final String TEXT_490 = " = false;" + NL + "\t//The index of group element which is the first new group in groups." + NL + "\tint newTabIndex_";
  protected final String TEXT_491 = " = -1;" + NL + "\t//Buffer all group tab XML, then set to startTabBuffer." + NL + "    String[] groupBuffer_";
  protected final String TEXT_492 = " = new String[";
  protected final String TEXT_493 = "];" + NL + "    String[] groupEndBuffer_";
  protected final String TEXT_494 = " = new String[";
  protected final String TEXT_495 = "];";
  protected final String TEXT_496 = NL + NL + "\t// need a new group element ";
  protected final String TEXT_497 = " or not" + NL + "\tif(isNewElememt_";
  protected final String TEXT_498 = " || groupbyList_";
  protected final String TEXT_499 = ".size()<=";
  protected final String TEXT_500 = " || groupbyList_";
  protected final String TEXT_501 = ".get(";
  protected final String TEXT_502 = ")==null";
  protected final String TEXT_503 = NL + "\t|| ( groupbyList_";
  protected final String TEXT_504 = ".get(";
  protected final String TEXT_505 = ").get(";
  protected final String TEXT_506 = ")!=null " + NL + "\t\t? !groupbyList_";
  protected final String TEXT_507 = ".get(";
  protected final String TEXT_508 = ").get(";
  protected final String TEXT_509 = ").equals(";
  protected final String TEXT_510 = ") " + NL + "\t\t: ";
  protected final String TEXT_511 = "!=null )";
  protected final String TEXT_512 = NL + "\t){" + NL + "\t\t// Is the first new element in groups." + NL + "\t\tif(isNewElememt_";
  protected final String TEXT_513 = " == false && groupbyList_";
  protected final String TEXT_514 = ".size()>";
  protected final String TEXT_515 = "){" + NL + "\t\t\tnewTabIndex_";
  protected final String TEXT_516 = " = ";
  protected final String TEXT_517 = ";" + NL + "\t\t}" + NL + "" + NL + "\t\t// count the groupby element" + NL + "\t\tif(groupbyList_";
  protected final String TEXT_518 = ".size()<=";
  protected final String TEXT_519 = "){" + NL + "        \tgroupbyList_";
  protected final String TEXT_520 = ".add(new java.util.ArrayList<String>());" + NL + "        }else{" + NL + "        \tgroupbyList_";
  protected final String TEXT_521 = ".get(";
  protected final String TEXT_522 = ").clear();" + NL + "        }";
  protected final String TEXT_523 = NL + "\t\tgroupbyList_";
  protected final String TEXT_524 = ".get(";
  protected final String TEXT_525 = ").add(";
  protected final String TEXT_526 = ");";
  protected final String TEXT_527 = NL + "        isNewElememt_";
  protected final String TEXT_528 = "=true;" + NL + "\t}" + NL + "\t" + NL + "\t// subtree XML string generate";
  protected final String TEXT_529 = NL + "\tif( false";
  protected final String TEXT_530 = " || valueMap_";
  protected final String TEXT_531 = ".get(\"";
  protected final String TEXT_532 = "\") != null";
  protected final String TEXT_533 = NL + "\t){" + NL + "\t\tunNullMaxIndex_";
  protected final String TEXT_534 = " = ";
  protected final String TEXT_535 = ";" + NL + "\t}";
  protected final String TEXT_536 = NL + "\t// buffer the end tabs to groupEnd buffer" + NL + "\tgroupEndBuffer_";
  protected final String TEXT_537 = "[";
  protected final String TEXT_538 = "] = buf_";
  protected final String TEXT_539 = ".toString();" + NL + "    buf_";
  protected final String TEXT_540 = " = new StringBuffer();";
  protected final String TEXT_541 = NL + "\t//output the previous groups as there's a new group" + NL + "    if (newTabIndex_";
  protected final String TEXT_542 = " >= 0) {" + NL + "        //out_";
  protected final String TEXT_543 = ".newLine();//Track code";
  protected final String TEXT_544 = NL + "\t\t// output unNull tabs in start tabs buffer" + NL + "        if (preUnNullMaxIndex_";
  protected final String TEXT_545 = " >= 0) {" + NL + "            for (int i_";
  protected final String TEXT_546 = " = 0; i_";
  protected final String TEXT_547 = " < startTabs_";
  protected final String TEXT_548 = ".length; i_";
  protected final String TEXT_549 = "++) {" + NL + "                if (i_";
  protected final String TEXT_550 = " <= preUnNullMaxIndex_";
  protected final String TEXT_551 = ") {" + NL + "                    if (startTabs_";
  protected final String TEXT_552 = "[i_";
  protected final String TEXT_553 = "] != null) {" + NL + "                        out_";
  protected final String TEXT_554 = ".write(startTabs_";
  protected final String TEXT_555 = "[i_";
  protected final String TEXT_556 = "]);" + NL + "                    }" + NL + "                    startTabs_";
  protected final String TEXT_557 = "[i_";
  protected final String TEXT_558 = "] = null;" + NL + "                }" + NL + "            }" + NL + "        }";
  protected final String TEXT_559 = NL + "\t\t//output all start tabs buffer" + NL + "\t\tfor (int i_";
  protected final String TEXT_560 = " = 0; i_";
  protected final String TEXT_561 = " < startTabs_";
  protected final String TEXT_562 = ".length; i_";
  protected final String TEXT_563 = "++) {" + NL + "            if (startTabs_";
  protected final String TEXT_564 = "[i_";
  protected final String TEXT_565 = "] != null) {" + NL + "                out_";
  protected final String TEXT_566 = ".write(startTabs_";
  protected final String TEXT_567 = "[i_";
  protected final String TEXT_568 = "]);" + NL + "            }" + NL + "            startTabs_";
  protected final String TEXT_569 = "[i_";
  protected final String TEXT_570 = "] = null;" + NL + "        }";
  protected final String TEXT_571 = NL + "        // output endtabs" + NL + "        if (preUnNullMaxIndex_";
  protected final String TEXT_572 = " >= preNewTabIndex_";
  protected final String TEXT_573 = NL + "            && preUnNullMaxIndex_";
  protected final String TEXT_574 = " >= ";
  protected final String TEXT_575 = " + newTabIndex_";
  protected final String TEXT_576 = ") {" + NL + "            for (int i_";
  protected final String TEXT_577 = " = preUnNullMaxIndex_";
  protected final String TEXT_578 = "; i_";
  protected final String TEXT_579 = " >= ";
  protected final String TEXT_580 = " + newTabIndex_";
  protected final String TEXT_581 = "; i_";
  protected final String TEXT_582 = "--) {" + NL + "            \tif(endTabs_";
  protected final String TEXT_583 = "[i_";
  protected final String TEXT_584 = "]!=null){" + NL + "            \t\tout_";
  protected final String TEXT_585 = ".write(endTabs_";
  protected final String TEXT_586 = "[i_";
  protected final String TEXT_587 = "]);" + NL + "            \t}" + NL + "            \tendTabs_";
  protected final String TEXT_588 = "[i_";
  protected final String TEXT_589 = "] = null;";
  protected final String TEXT_590 = "out_";
  protected final String TEXT_591 = ".newLine(); ";
  protected final String TEXT_592 = NL + "                out_";
  protected final String TEXT_593 = ".write(endTabStrs_";
  protected final String TEXT_594 = NL + "                        .get(i_";
  protected final String TEXT_595 = "));" + NL + "            }" + NL + "        } else {";
  protected final String TEXT_596 = NL + "            for (int i_";
  protected final String TEXT_597 = " = preNewTabIndex_";
  protected final String TEXT_598 = " - 1; i_";
  protected final String TEXT_599 = " >= ";
  protected final String TEXT_600 = " + newTabIndex_";
  protected final String TEXT_601 = "; i_";
  protected final String TEXT_602 = "--) {" + NL + "            \tif(endTabs_";
  protected final String TEXT_603 = "[i_";
  protected final String TEXT_604 = "]!=null){" + NL + "            \t\tout_";
  protected final String TEXT_605 = ".write(endTabs_";
  protected final String TEXT_606 = "[i_";
  protected final String TEXT_607 = "]);" + NL + "            \t}" + NL + "            \tendTabs_";
  protected final String TEXT_608 = "[i_";
  protected final String TEXT_609 = "] = null;" + NL + "            \t";
  protected final String TEXT_610 = "out_";
  protected final String TEXT_611 = ".newLine(); ";
  protected final String TEXT_612 = NL + "                out_";
  protected final String TEXT_613 = ".write(endTabStrs_";
  protected final String TEXT_614 = NL + "                        .get(i_";
  protected final String TEXT_615 = "));" + NL + "            }";
  protected final String TEXT_616 = NL + "        }";
  protected final String TEXT_617 = NL + "        preNewTabIndex_";
  protected final String TEXT_618 = " = newTabIndex_";
  protected final String TEXT_619 = " + ";
  protected final String TEXT_620 = ";" + NL + "    }" + NL + "" + NL + "    // set new element groupbuffer to startbuffer" + NL + "    for (int i_";
  protected final String TEXT_621 = " = 0; i_";
  protected final String TEXT_622 = " < groupBuffer_";
  protected final String TEXT_623 = ".length; i_";
  protected final String TEXT_624 = "++) {" + NL + "        // when newTabIndex is null, must use the perNewTabIndex" + NL + "        if (i_";
  protected final String TEXT_625 = " >= preNewTabIndex_";
  protected final String TEXT_626 = " - ";
  protected final String TEXT_627 = ") {" + NL + "            startTabs_";
  protected final String TEXT_628 = "[i_";
  protected final String TEXT_629 = " + ";
  protected final String TEXT_630 = "] = groupBuffer_";
  protected final String TEXT_631 = "[i_";
  protected final String TEXT_632 = "];" + NL + "            endTabs_";
  protected final String TEXT_633 = "[i_";
  protected final String TEXT_634 = " + ";
  protected final String TEXT_635 = "] = groupEndBuffer_";
  protected final String TEXT_636 = "[i_";
  protected final String TEXT_637 = "];" + NL + "        }" + NL + "    }";
  protected final String TEXT_638 = NL + "\t//reset the preUnNullMaxIndex" + NL + "\tif(unNullMaxIndex_";
  protected final String TEXT_639 = ">=0){" + NL + "    \tpreUnNullMaxIndex_";
  protected final String TEXT_640 = "=unNullMaxIndex_";
  protected final String TEXT_641 = ";" + NL + "\t}else{" + NL + "\t\tif(preUnNullMaxIndex_";
  protected final String TEXT_642 = ">";
  protected final String TEXT_643 = "){" + NL + "\t\t\tpreUnNullMaxIndex_";
  protected final String TEXT_644 = "=";
  protected final String TEXT_645 = ";" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_646 = NL + "\t// build loop xml tree";
  protected final String TEXT_647 = NL + "\t\tif( false";
  protected final String TEXT_648 = " || valueMap_";
  protected final String TEXT_649 = ".get(\"";
  protected final String TEXT_650 = "\") != null";
  protected final String TEXT_651 = " || true " + NL + "    \t";
  protected final String TEXT_652 = NL + "\t\t){";
  protected final String TEXT_653 = NL + "\t\t// output all buffer" + NL + "\t\tfor (int i_";
  protected final String TEXT_654 = " = 0; i_";
  protected final String TEXT_655 = " < startTabs_";
  protected final String TEXT_656 = ".length; i_";
  protected final String TEXT_657 = "++) {" + NL + "            if (startTabs_";
  protected final String TEXT_658 = "[i_";
  protected final String TEXT_659 = "] != null && startTabs_";
  protected final String TEXT_660 = "[i_";
  protected final String TEXT_661 = "].length() > 0) {" + NL + "                out_";
  protected final String TEXT_662 = ".write(startTabs_";
  protected final String TEXT_663 = "[i_";
  protected final String TEXT_664 = "]);" + NL + "                startTabs_";
  protected final String TEXT_665 = "[i_";
  protected final String TEXT_666 = "] = null;" + NL + "            }" + NL + "        }" + NL + "\t\tout_";
  protected final String TEXT_667 = ".write(buf_";
  protected final String TEXT_668 = ".toString());" + NL + "\t\tpreNewTabIndex_";
  protected final String TEXT_669 = " = ";
  protected final String TEXT_670 = ";";
  protected final String TEXT_671 = NL + "            preUnNullMaxIndex_";
  protected final String TEXT_672 = " = ";
  protected final String TEXT_673 = ";" + NL + "\t\t}";
  protected final String TEXT_674 = NL + "    currentRowCount_";
  protected final String TEXT_675 = "++;" + NL + "    if(currentRowCount_";
  protected final String TEXT_676 = " == ";
  protected final String TEXT_677 = "){" + NL + "    \tneedRoot_";
  protected final String TEXT_678 = "  = true;" + NL + "    \tfileName_";
  protected final String TEXT_679 = " = file_";
  protected final String TEXT_680 = " + currentFileCount_";
  protected final String TEXT_681 = " + suffix_";
  protected final String TEXT_682 = ";" + NL + "        currentRowCount_";
  protected final String TEXT_683 = " = 0;" + NL + "        currentFileCount_";
  protected final String TEXT_684 = "++;" + NL + "    \tgroupbyList_";
  protected final String TEXT_685 = ".clear();" + NL + " ";
  protected final String TEXT_686 = "   \t" + NL + "\tif (preUnNullMaxIndex_";
  protected final String TEXT_687 = " >= 0) {" + NL + "        // output all buffer" + NL + "        for (int j_";
  protected final String TEXT_688 = " = 0; j_";
  protected final String TEXT_689 = " <= preUnNullMaxIndex_";
  protected final String TEXT_690 = "; j_";
  protected final String TEXT_691 = "++) {" + NL + "            if (startTabs_";
  protected final String TEXT_692 = "[j_";
  protected final String TEXT_693 = "] != null)" + NL + "                out_";
  protected final String TEXT_694 = ".write(startTabs_";
  protected final String TEXT_695 = "[j_";
  protected final String TEXT_696 = "]);" + NL + "        }" + NL + "" + NL + "        if (preUnNullMaxIndex_";
  protected final String TEXT_697 = " < preNewTabIndex_";
  protected final String TEXT_698 = " ) {" + NL + "\t\t\tfor (int i_";
  protected final String TEXT_699 = " = preNewTabIndex_";
  protected final String TEXT_700 = " - 1; i_";
  protected final String TEXT_701 = " >= 0; i_";
  protected final String TEXT_702 = "--) {" + NL + "            \tif(endTabs_";
  protected final String TEXT_703 = "[i_";
  protected final String TEXT_704 = "]!=null){" + NL + "            \t\tout_";
  protected final String TEXT_705 = ".write(endTabs_";
  protected final String TEXT_706 = "[i_";
  protected final String TEXT_707 = "]);" + NL + "            \t}";
  protected final String TEXT_708 = "out_";
  protected final String TEXT_709 = ".newLine(); ";
  protected final String TEXT_710 = NL + "                out_";
  protected final String TEXT_711 = ".write(endTabStrs_";
  protected final String TEXT_712 = ".get(i_";
  protected final String TEXT_713 = "));" + NL + "            }" + NL + "        } else {" + NL + "            for (int i_";
  protected final String TEXT_714 = " = preUnNullMaxIndex_";
  protected final String TEXT_715 = "; i_";
  protected final String TEXT_716 = " >= 0; i_";
  protected final String TEXT_717 = "--) {" + NL + "            \tif(endTabs_";
  protected final String TEXT_718 = "[i_";
  protected final String TEXT_719 = "]!=null){" + NL + "            \t\tout_";
  protected final String TEXT_720 = ".write(endTabs_";
  protected final String TEXT_721 = "[i_";
  protected final String TEXT_722 = "]);" + NL + "            \t}";
  protected final String TEXT_723 = "out_";
  protected final String TEXT_724 = ".newLine(); ";
  protected final String TEXT_725 = NL + "                out_";
  protected final String TEXT_726 = ".write(endTabStrs_";
  protected final String TEXT_727 = ".get(i_";
  protected final String TEXT_728 = "));" + NL + "            }" + NL + "        }" + NL + "    }";
  protected final String TEXT_729 = NL + "\tfor (int i_";
  protected final String TEXT_730 = " = endTabStrs_";
  protected final String TEXT_731 = ".size() - 1; i_";
  protected final String TEXT_732 = " >= 0; i_";
  protected final String TEXT_733 = "--) {" + NL + "    \tif(endTabs_";
  protected final String TEXT_734 = "[i_";
  protected final String TEXT_735 = "]!=null){" + NL + "    \t\tout_";
  protected final String TEXT_736 = ".write(endTabs_";
  protected final String TEXT_737 = "[i_";
  protected final String TEXT_738 = "]);" + NL + "    \t}";
  protected final String TEXT_739 = "out_";
  protected final String TEXT_740 = ".newLine(); ";
  protected final String TEXT_741 = NL + "        out_";
  protected final String TEXT_742 = ".write(endTabStrs_";
  protected final String TEXT_743 = ".get(i_";
  protected final String TEXT_744 = "));" + NL + "    }";
  protected final String TEXT_745 = NL + "\t    preUnNullMaxIndex_";
  protected final String TEXT_746 = " = -1;" + NL + "        preNewTabIndex_";
  protected final String TEXT_747 = " = -1;" + NL + "    \tstartTabs_";
  protected final String TEXT_748 = " = new String[endTabStrs_";
  protected final String TEXT_749 = ".size()];" + NL + "    \tendTabs_";
  protected final String TEXT_750 = " = new String[endTabStrs_";
  protected final String TEXT_751 = ".size()];" + NL + "    \t" + NL + "\t\tout_";
  protected final String TEXT_752 = ".close();" + NL + "\t\tout_";
  protected final String TEXT_753 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(file_";
  protected final String TEXT_754 = " + currentFileCount_";
  protected final String TEXT_755 = " + suffix_";
  protected final String TEXT_756 = "), ";
  protected final String TEXT_757 = "));" + NL + "        out_";
  protected final String TEXT_758 = ".write(\"<?xml version=\\\"1.0\\\" encoding=\\\"\"+";
  protected final String TEXT_759 = "+\"\\\"?>\");" + NL + "        out_";
  protected final String TEXT_760 = ".newLine();" + NL + "\t}";
  protected final String TEXT_761 = NL;

    static class XMLNode {

        // table parameter of component
        public String name = null;

        public String path = null;

        public String type = null;

        public String column = null;
        
        public String defaultValue = null;
        
        public int order = 0;
        
        public boolean hasDefaultValue = false;

        // special node
        public int special = 0; // 1 is subtree root, 2 is subtree root parent, 4 is main

        // column
        public IMetadataColumn relatedColumn = null;

        public List<IMetadataColumn> childrenColumnList = new ArrayList<IMetadataColumn>();

        // tree variable
        public XMLNode parent = null;

        public List<XMLNode> attributes = new LinkedList<XMLNode>();

        public List<XMLNode> namespaces = new LinkedList<XMLNode>();

        public List<XMLNode> elements = new LinkedList<XMLNode>(); // the main element is the last element

        public XMLNode(String path, String type, XMLNode parent, String column, String value, int order) {
        	this.order = order;
            this.path = path;
            this.parent = parent;
            this.type = type;
            this.column = column;
            this.defaultValue = value;
            if (type.equals("ELEMENT")) {
                this.name = path.substring(path.lastIndexOf("/") + 1);
            } else {
                this.name = path;
            }
        }
        
        public boolean isMainNode(){
            return 4 == (special & 4);
        }
        
        public boolean isSubTreeRoot(){
            return 1 == (special & 1);
        }
        
        public boolean isSubTreeParent(){
            return 2 == (special & 2);
        }
    
        public int getNodeInsertIndex(){
        	int insertIndex =0;
        	if(5==(special & 5)){//group and loop main node
        		if(parent!=null && parent.elements!=null){
            		for(XMLNode tmpNode: parent.elements){
            			if(order <= tmpNode.order){
            				break;
            			}
            			insertIndex++;
            		}
        		}
        	}
        	return insertIndex;
        }
        
        public int getCurrGroupPos(){
        	int currPos =0;
        	if(5==(special & 5)){//group and loop main node
    			XMLNode tmpNode = parent;
    			while(tmpNode!=null && (5==(tmpNode.special & 5))){
    				currPos++;
    				tmpNode = tmpNode.parent;
    			}
        	}
        	return currPos;
        }
    }

    
    // return [0] is root(XMLNode), [1] is groups(List<XMLNode>), [2] loop(XMLNode)
    public Object[] getTree(List<Map<String, String>> rootTable, List<Map<String, String>> groupTable,
            List<Map<String, String>> loopTable, List<IMetadataColumn> colList) {
        List<List<Map<String, String>>> tables = new ArrayList<List<Map<String, String>>>();
        tables.add(rootTable);
        tables.add(groupTable);
        tables.add(loopTable);

        XMLNode root = null;
        List<XMLNode> mains = new ArrayList<XMLNode>();
        List<XMLNode> groups = new ArrayList<XMLNode>();
        XMLNode loop = null;

        XMLNode tmpParent = null;
        XMLNode tmpMainNode = null;
        if (loopTable == null || loopTable.size() == 0) {
            return null;
        }
        int index =0;
        int currOrder = 0;
        String mainPath = loopTable.get(0).get("PATH");
        for (List<Map<String, String>> tmpTable : tables) {
            tmpParent = tmpMainNode;
            for (Map<String, String> tmpMap : tmpTable) {
            	index++;
            	if(tmpMap.get("ORDER")!=null && !"".equals(tmpMap.get("ORDER").trim())){
            		currOrder = Integer.parseInt(tmpMap.get("ORDER"));
            	}else{
            		currOrder = index;
            	}
                XMLNode tmpNew = null;
                if (tmpMap.get("ATTRIBUTE").equals("attri")) {
                    tmpNew = new XMLNode(tmpMap.get("PATH"), "ATTRIBUTE", tmpParent, tmpMap.get("COLUMN"), tmpMap.get("VALUE"), currOrder);
                    tmpParent.attributes.add(tmpNew);
                } else if (tmpMap.get("ATTRIBUTE").equals("ns")) {
                    tmpNew = new XMLNode(tmpMap.get("PATH"), "NAMESPACE", tmpParent, tmpMap.get("COLUMN"), tmpMap.get("VALUE"), currOrder);
                    tmpParent.namespaces.add(tmpNew);
                } else {
                    if (tmpParent == null) {
                        tmpNew = new XMLNode(tmpMap.get("PATH"), "ELEMENT", tmpParent, tmpMap.get("COLUMN"), tmpMap.get("VALUE"), currOrder);
//                        tmpNew.special |= 1;
                        root = tmpNew;
                        mains.add(root);
                    } else {
                        String tmpParentPath = tmpMap.get("PATH").substring(0, tmpMap.get("PATH").lastIndexOf("/"));
                        while (tmpParent != null && !tmpParentPath.equals(tmpParent.path)) {
                            tmpParent = tmpParent.parent;
                        }
                        tmpNew = new XMLNode(tmpMap.get("PATH"), "ELEMENT", tmpParent, tmpMap.get("COLUMN"), tmpMap.get("VALUE"), currOrder);
                        tmpParent.elements.add(tmpNew);
                        if (tmpMap.get("ATTRIBUTE").equals("main")) {
                            if (tmpTable == groupTable) {
                                tmpNew.special |= 1;
                                tmpParent.special |= 2;
                                groups.add(tmpNew);
                            } else if (tmpTable == loopTable) {
                                tmpNew.special |= 1;
                                tmpParent.special |= 2;
                                loop = tmpNew;
                            }else if (tmpTable == rootTable){
                                mains.add(tmpNew);
                            }
                        }
                    }
                    if (tmpMap.get("ATTRIBUTE").equals("main")) {
                        tmpMainNode = tmpNew;
                        tmpNew.special |= 4;
                    }
                    tmpParent = tmpNew;
                }
                setIMetadataColumn(tmpNew, colList);
                setDefaultValues(tmpNew);//add by wliu
            }
        }
        return new Object[] { mains, groups, loop };
    }
    
    private void setDefaultValues(XMLNode node){
    	if(node.defaultValue != null && !"".equals(node.defaultValue)){
    		XMLNode tmp = node;
    		while(tmp !=null){
    			tmp.hasDefaultValue = true;
    			if(tmp.isMainNode()){
    				break;
    			}
    			tmp = tmp.parent;
    		}
    	}
    }

    private void setIMetadataColumn(XMLNode node, List<IMetadataColumn> colList) {
        String value = null;
        JavaType javaType = null;
        if (node.column != null && node.column.length() > 0) {
            for (IMetadataColumn column : colList) {
                if (column.getLabel().equals(node.column)) {
                    node.relatedColumn = column;
                    XMLNode tmp = node;
                    while (tmp != null) {
                        if (!tmp.childrenColumnList.contains(column)) {
                            tmp.childrenColumnList.add(column);
                        }
                        if(tmp.isMainNode()){
                            break;
                        }
                        tmp = tmp.parent;
                    }
                }
            }
        }
    }

    public List<XMLNode> getGroupByNodeList(XMLNode group) {
        List<XMLNode> list = new ArrayList<XMLNode>();
        for (XMLNode attri : group.attributes) {
            if (attri.column != null && attri.column.length() != 0) {
                list.add(attri);
            }
        }
        if (group.relatedColumn != null) {
            list.add(group);
        } else {
            for (XMLNode element : group.elements) {
                if (!element.isMainNode()) {
                    list.addAll(getGroupByNodeList(element));
                }
            }
        }
        return list;
    }

    public XMLNode removeEmptyElement(XMLNode root) {
        List<XMLNode> removeNodes = new LinkedList<XMLNode>();
        for (XMLNode attri : root.attributes) {
            if ((attri.column == null || attri.column.length() == 0) && 
            		(attri.defaultValue == null || "".equals(attri.defaultValue)) ) {
                attri.parent = null;
                removeNodes.add(attri);
            }
        }
        root.attributes.removeAll(removeNodes);

        removeNodes.clear();
        for (XMLNode ns : root.namespaces) {
            if ( (ns.column == null || ns.column.length() == 0)
            		&& (ns.defaultValue == null || "".equals(ns.defaultValue)) ) {
                ns.parent = null;
                removeNodes.add(ns);
            }
        }
        root.namespaces.removeAll(removeNodes);

        removeNodes.clear();
        for (XMLNode child : root.elements) {
            removeNodes.add(removeEmptyElement(child));
        }
        root.elements.removeAll(removeNodes);

        if (root.attributes.size() == 0 && root.namespaces.size() == 0 && root.elements.size() == 0
                && (root.column == null || root.column.length() == 0)
                && (root.defaultValue == null || "".equals(root.defaultValue)) ) {
            return root;
        } else {
            return null;
        }
    }

    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
//XMLTool
class XMLTool{
	public boolean advancedSeparator = false;
	public String thousandsSeparator = null;
 	public String decimalSeparator =null;
	public String connName = null;
	public String cid = null;
	
	public void getValue(XMLNode node){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(node.relatedColumn.getLabel());
    stringBuffer.append(TEXT_3);
    
	}

	public void getValue(IMetadataColumn column){
		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
		String defaultValue=column.getDefault();
		boolean isNotSetDefault = false;
		if(defaultValue!=null){
			isNotSetDefault = defaultValue.length()==0;
		}else{
			isNotSetDefault=true;
		}

    stringBuffer.append(TEXT_4);
    
		if(column.isNullable()){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_7);
    
		}
		
        if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 
        	if(javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getPrecision() == null? connName + "." + column.getLabel() : connName + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( thousandsSeparator);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(decimalSeparator );
    stringBuffer.append(TEXT_11);
    
    		} else {

    stringBuffer.append(TEXT_12);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_14);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(decimalSeparator );
    stringBuffer.append(TEXT_16);
    
	   		}
        } else if(JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable())){

    stringBuffer.append(TEXT_17);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_19);
    
        }else if(javaType == JavaTypesManager.DATE){
            if( column.getPattern() != null && column.getPattern().trim().length() != 0 ){

    stringBuffer.append(TEXT_20);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getPattern());
    stringBuffer.append(TEXT_23);
    
            }else{

    stringBuffer.append(TEXT_24);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(column.getLabel());
    
           }
        }else if (javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getPrecision() == null? connName + "." + column.getLabel() : connName + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_27);
    
        }else{

    stringBuffer.append(TEXT_28);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_30);
    
		}
		if(column.isNullable()){
			
    stringBuffer.append(TEXT_31);
     
			if(isNotSetDefault == false){
				
    stringBuffer.append(column.getDefault());
    
			}else{
				
    stringBuffer.append(TEXT_32);
    
			}
		}

    stringBuffer.append(TEXT_33);
    
	}
}

// ------------------- *** Dom4j generation mode start *** ------------------- //
class GenerateToolByDom4j{
	String cid = null;
	boolean allowEmpty = false;
	boolean bAddEmptyAttr = false, bAddUnmappedAttr = false;
	boolean outputAsXSD = false;
	XMLTool tool = null;
	boolean isAppend = false;
	public void generateCode(XMLNode node, String currEleName, String parentName){
		if(("ELEMENT").equals(node.type)){
			createElement(currEleName,node,parentName);
			setText(currEleName,node);
			for(XMLNode ns:node.namespaces){
				addNameSpace(currEleName,ns);
			}
			for(XMLNode attri:node.attributes){
				addAttribute(currEleName,attri);
			}
			if(node.name.indexOf(":")>0){

    stringBuffer.append(TEXT_34);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_37);
    
			}
			int index = 0;
			for(XMLNode child:node.elements){
				if(0==(child.special & 1)){
					generateCode(child,currEleName+"_"+index++,currEleName);
				}
			}
			if(node.relatedColumn != null && (node.special & 2)==0 && (node.special & 1)==0){
				if(isAppend && !outputAsXSD && !allowEmpty){

    stringBuffer.append(TEXT_38);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    
				}
			}
		}
	}
	private void createElement(String currEleName, XMLNode node, String parentName){
		int index = node.name.indexOf(":");
		if(5==(node.special & 5)){
			int currPos = node.getCurrGroupPos();
			if(index>0 && node.parent!=null){

    stringBuffer.append(TEXT_50);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(node.name.substring(0,index));
    stringBuffer.append(TEXT_55);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(node.name.substring(index+1));
    stringBuffer.append(TEXT_59);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_62);
    
			}else{

    stringBuffer.append(TEXT_63);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_66);
    
			}
			if(isAppend){

    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    
				if(currPos==0){

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    
				}else{

    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(node.getNodeInsertIndex() );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    
				}

    stringBuffer.append(TEXT_99);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    
			}else{

    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(node.getNodeInsertIndex() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(currPos +1 );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(currPos +1 );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    
			}
		}else{
			if(index>0 && node.parent!=null){

    stringBuffer.append(TEXT_123);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(node.name.substring(0,index));
    stringBuffer.append(TEXT_128);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(node.name.substring(index+1));
    stringBuffer.append(TEXT_134);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_139);
    
			}else{

    stringBuffer.append(TEXT_140);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_145);
    
			}
		}
		if(0!=(node.special & 2)){

    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    
		}
	}
	private void setText(String currEleName, XMLNode node){
		if(node.relatedColumn!=null){
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(node.relatedColumn.getTalendType());
			if(javaType == JavaTypesManager.OBJECT){

    stringBuffer.append(TEXT_150);
    tool.getValue(node); 
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_154);
    tool.getValue(node);
    stringBuffer.append(TEXT_155);
    
				if(outputAsXSD){

    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_157);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_161);
    
				}
			}else{
				if("id_Document".equals(node.relatedColumn.getTalendType())) {

    stringBuffer.append(TEXT_162);
    tool.getValue(node); 
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_166);
    tool.getValue(node);
    stringBuffer.append(TEXT_167);
    
				} else {

    stringBuffer.append(TEXT_168);
    tool.getValue(node); 
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    tool.getValue(node);
    stringBuffer.append(TEXT_173);
    
				}
				if(outputAsXSD){

    stringBuffer.append(TEXT_174);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    
				}
			}
		}else if(node.defaultValue != null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_183);
    
		}
	}
	private void addAttribute(String currEleName, XMLNode node){
		if (node.relatedColumn != null){
        
    stringBuffer.append(TEXT_184);
    tool.getValue(node);
    stringBuffer.append(TEXT_185);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_188);
    tool.getValue(node);
    stringBuffer.append(TEXT_189);
     if (bAddEmptyAttr) { 
    stringBuffer.append(TEXT_190);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_192);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_193);
    }
		} else { 
		  if (node.defaultValue != null && !("").equals(node.defaultValue) ){
          
    stringBuffer.append(TEXT_194);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_196);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_197);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_198);
    
		  } else if (bAddUnmappedAttr){
		  
    stringBuffer.append(TEXT_199);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_202);
    
		  }
		}
	}
	private void addNameSpace(String currEleName, XMLNode node){
		if(node.relatedColumn!=null){

    stringBuffer.append(TEXT_203);
    tool.getValue(node);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_206);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_207);
    tool.getValue(node);
    stringBuffer.append(TEXT_208);
    
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_209);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_210);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_211);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_213);
    tool.getValue(node);
    stringBuffer.append(TEXT_214);
    
			}

    stringBuffer.append(TEXT_215);
    
		}else if(node.defaultValue != null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_216);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_218);
    stringBuffer.append(node.path );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_220);
    
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_221);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_226);
    
			}
		}
	}
}

//----------** add by wliu dom4j to genenrate get function for node **-------//
class GenerateExprCmpByDom4j{
//	String cid = null;
	XMLTool tool = null;
	XMLNode groupNode = null;
	boolean needEmptyNode = true;
	public void generateCode(XMLNode node, String parentName){
		String tmpPath = node.path.replaceFirst(groupNode.path,"");
		String[] arrNames = tmpPath.split("/");
		if(node==groupNode){

    stringBuffer.append(TEXT_227);
    
		}
		
		if(node.relatedColumn != null){

    stringBuffer.append(TEXT_228);
    
			if(!needEmptyNode){

    stringBuffer.append(TEXT_229);
    tool.getValue(node); 
    stringBuffer.append(TEXT_230);
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_231);
    			}
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_232);
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_233);
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_234);
    tool.getValue(node); 
    stringBuffer.append(TEXT_235);
    if(!needEmptyNode){
    stringBuffer.append(TEXT_236);
    }
    stringBuffer.append(TEXT_237);
    
		}
		
		//first generate the attribute comparision	
		if(node.attributes!=null){
			for(XMLNode attri:node.attributes){
				if(attri.relatedColumn !=null){

    stringBuffer.append(TEXT_238);
    
					if(!needEmptyNode){

    stringBuffer.append(TEXT_239);
    tool.getValue(attri); 
    stringBuffer.append(TEXT_240);
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_241);
    stringBuffer.append(attri.name );
    stringBuffer.append(TEXT_242);
    					}
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_243);
    stringBuffer.append(attri.name );
    stringBuffer.append(TEXT_244);
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_245);
    stringBuffer.append(attri.name );
    stringBuffer.append(TEXT_246);
    generateCmnExpr(arrNames, parentName); 
    stringBuffer.append(TEXT_247);
    stringBuffer.append(attri.name );
    stringBuffer.append(TEXT_248);
    tool.getValue(attri); 
    stringBuffer.append(TEXT_249);
    
					if(!needEmptyNode){
    stringBuffer.append(TEXT_250);
    }

    stringBuffer.append(TEXT_251);
    
				}
			}
		}
		
		if(node.elements!=null){
			for(XMLNode child:node.elements){
				if(!child.isMainNode()){
					generateCode(child,parentName);
				}
			}
		}		
	}
	
	private void generateCmnExpr(String[] arrNames, String parentName){

    stringBuffer.append(TEXT_252);
    stringBuffer.append(parentName );
    
		for(int i=1;arrNames != null && i<arrNames.length; i++){

    stringBuffer.append(TEXT_253);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_254);
    stringBuffer.append(arrNames[i]);
    stringBuffer.append(TEXT_255);
    
		}
	}
}

// ------------------- *** Dom4j generation mode end *** ------------------- //

// ------------------- *** Null generation mode start *** ------------------- //
class GenerateToolByNull{
	String cid = null;
	boolean allowEmpty = false;
	boolean bAddEmptyAttr = false, bAddUnmappedAttr = false;
	boolean outputAsXSD = false;
	XMLTool tool = null;
	
	boolean isCompact = false;
	
	public void generateCode(XMLNode node, String emptySpace){	
		if(("ELEMENT").equals(node.type)){
			startElement(node,emptySpace);
			setText(node);
			XMLNode mainChild = null;
			for(XMLNode child:node.elements){
				if(child.isMainNode()){ //loop dosen't have a main child node
					mainChild = child;
					break;
				}
			}
			for(XMLNode child:node.elements){
				if(mainChild!=null && mainChild.order<=child.order){ //loop dosen't have a main child node
					if(1==(node.special & 1)){ // group

    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_257);
    stringBuffer.append(node.getCurrGroupPos());
    stringBuffer.append(TEXT_258);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_260);
    
					}else{// root
    					int num = node.path.split("/").length-2;
    					if(!outputAsXSD && !allowEmpty){

    stringBuffer.append(TEXT_261);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(num);
    stringBuffer.append(TEXT_263);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_264);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_265);
    
						}else{

    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_268);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_269);
    
						}
					}
					mainChild = null;
				}
				if(!child.isMainNode()){ //make the main node output last
					if(!outputAsXSD && !allowEmpty 
						&& (child.relatedColumn != null || child.childrenColumnList.size()>0
							 || child.hasDefaultValue == true ) ){

    stringBuffer.append(TEXT_270);
    
                    	for(IMetadataColumn column : child.childrenColumnList){
                    		
    stringBuffer.append(TEXT_271);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_272);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_273);
    
                    	}
                    	if(child.hasDefaultValue == true){
    stringBuffer.append(TEXT_274);
    }
    stringBuffer.append(TEXT_275);
    
						if(isCompact==false)
							generateCode(child,emptySpace+"  ");
						else
							generateCode(child,emptySpace);

    stringBuffer.append(TEXT_276);
    
            		}else{
            			if(isCompact==false)
            				generateCode(child,emptySpace+"  ");
            			else
            				generateCode(child,emptySpace);
            		}
				}
			}

			if(!node.isMainNode()){ // is not main node
				endElement(node,emptySpace);
			}
		}
	}
	private void startElement(XMLNode node, String emptySpace){

    stringBuffer.append(TEXT_277);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_278);
    stringBuffer.append(isCompact?"":"\\n");
    stringBuffer.append(TEXT_279);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_280);
    stringBuffer.append(emptySpace);
    stringBuffer.append(TEXT_281);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_282);
    
		if(outputAsXSD && node.parent==null){

    stringBuffer.append(TEXT_283);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_286);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_287);
    
		}
		for(XMLNode ns:node.namespaces){
			addNameSpace(ns);
		}
		for(XMLNode attri:node.attributes){
			addAttribute(attri);
		}
		if(outputAsXSD && node.relatedColumn != null){

    stringBuffer.append(TEXT_288);
    tool.getValue(node); 
    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_290);
    
		}

    stringBuffer.append(TEXT_291);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_292);
    
	}
	
	public void endElement(XMLNode node, String emptySpace){
		if(node.elements.size()>0){

    stringBuffer.append(TEXT_293);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_294);
    stringBuffer.append(isCompact?"":"\\n");
    stringBuffer.append(TEXT_295);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_296);
    stringBuffer.append(emptySpace);
    stringBuffer.append(TEXT_297);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_298);
    
		}else{

    stringBuffer.append(TEXT_299);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_300);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_301);
    
		}
	}
	private void setText(XMLNode node){
		if(node.relatedColumn!=null){
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(node.relatedColumn.getTalendType());
			if(javaType == JavaTypesManager.OBJECT){

    stringBuffer.append(TEXT_302);
    tool.getValue(node);
    stringBuffer.append(TEXT_303);
    tool.getValue(node);
    stringBuffer.append(TEXT_304);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_305);
    tool.getValue(node);
    stringBuffer.append(TEXT_306);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_307);
    tool.getValue(node);
    stringBuffer.append(TEXT_308);
    
			}else{

    stringBuffer.append(TEXT_309);
    tool.getValue(node);
    stringBuffer.append(TEXT_310);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_311);
    tool.getValue(node);
    stringBuffer.append(TEXT_312);
    
			}
		}else if(node.defaultValue !=null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_313);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_314);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_315);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_316);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_317);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_318);
    
		}
	}
	private void addAttribute(XMLNode node){
		if (node.relatedColumn != null){
        
    stringBuffer.append(TEXT_319);
    tool.getValue(node); 
    stringBuffer.append(TEXT_320);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_321);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_322);
    tool.getValue(node);
    stringBuffer.append(TEXT_323);
     if (bAddEmptyAttr){
    stringBuffer.append(TEXT_324);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_325);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_326);
    }
		} else {
		  if (node.defaultValue != null && !("").equals(node.defaultValue)){
          
    stringBuffer.append(TEXT_327);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_328);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_329);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_330);
    
		  } else if (bAddUnmappedAttr){
		  
    stringBuffer.append(TEXT_331);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_332);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_333);
    
		  }
		}
	}
	private void addNameSpace(XMLNode node){
		if(node.relatedColumn!=null){

    stringBuffer.append(TEXT_334);
    tool.getValue(node);
    stringBuffer.append(TEXT_335);
    
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_336);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_337);
    tool.getValue(node);
    stringBuffer.append(TEXT_338);
    
			}else{

    stringBuffer.append(TEXT_339);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_340);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_341);
    tool.getValue(node);
    stringBuffer.append(TEXT_342);
    
			}

    stringBuffer.append(TEXT_343);
    
		}else if(node.defaultValue !=null && !("").equals(node.defaultValue) ){
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_344);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_345);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_346);
    
			}else{

    stringBuffer.append(TEXT_347);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_348);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_349);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_350);
    
			}
		}
	}
}
// ------------------- *** Null generation mode end *** ------------------- //

// ------------------- *** Common code start *** ------------------- //
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String cid_original = cid;
cid = cid_original.replace("tAdvancedFileOutputXML","tAFOX");

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	if(conns!=null && conns.size()>0){
    		IConnection conn = conns.get(0);
    		if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){ 
    		
            	List<Map<String, String>> rootTable = 
                	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ROOT__");
                List<Map<String, String>> groupTable = 
                	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUP__");
                List<Map<String, String>> loopTable = 
                	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__LOOP__");
                
                String allowEmpty = ElementParameterParser.getValue(node, "__CREATE_EMPTY_ELEMENT__");
                boolean bAddEmptyAttr = "true".equals(ElementParameterParser.getValue(node, "__ADD_EMPTY_ATTRIBUTE__"));
                boolean bAddUnmappedAttr = "true".equals(ElementParameterParser.getValue(node, "__ADD_UNMAPPED_ATTRIBUTE__"));
                
                String outputAsXSD = ElementParameterParser.getValue(node, "__OUTPUT_AS_XSD__");
                String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
                
                String split = ElementParameterParser.getValue(node, "__SPLIT__");
                String splitEvery = ElementParameterParser.getValue(node, "__SPLIT_EVERY__");
                
                String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
        		boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
        		String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
        		String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER); 
        		
        		String mode = ElementParameterParser.getValue(node, "__GENERATION_MODE__");
				
        		boolean isMerge= ("true").equals(ElementParameterParser.getValue(node, "__MERGE__"));        		
        		boolean isCompact = ("true").equals(ElementParameterParser.getValue(node, "__PRETTY_COMPACT__"));
        		
        		//*****************add for the feature:8873 to support output steam *****************
        		boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
				String outStream = ElementParameterParser.getValue(node,"__STREAMNAME__");
				//*******************add for feature:8873 end ****************
        		
        		// init tool
                XMLTool tool = new XMLTool();
                tool.connName = conn.getName();
                tool.advancedSeparator=advancedSeparator;
                tool.thousandsSeparator=thousandsSeparator;
                tool.decimalSeparator=decimalSeparator;
                tool.cid=cid;
                
                // change tables to a tree 
				Object[] treeObjs = getTree(rootTable, groupTable, loopTable, metadata.getListColumns());
				
				if(treeObjs == null || treeObjs.length == 0){
					return "";
				}
				
            	List<XMLNode> mainList = (ArrayList<XMLNode>)treeObjs[0];
                List<XMLNode> groupList = (ArrayList<XMLNode>)treeObjs[1];
                XMLNode root = mainList.get(0);                
            	XMLNode loop = (XMLNode)treeObjs[2];
                
                if(!("true").equals(allowEmpty)){
                	removeEmptyElement(root);
                }
                
                List<List<XMLNode>> groupbyNodeList = new ArrayList<List<XMLNode>>();
                for(XMLNode group:groupList){
                	groupbyNodeList.add(getGroupByNodeList(group));
                }

    stringBuffer.append(TEXT_351);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_352);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_353);
    
				for(IMetadataColumn column :metadata.getListColumns()){

    stringBuffer.append(TEXT_354);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_355);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_356);
     tool.getValue(column); 
    stringBuffer.append(TEXT_357);
    
				}
// ------------------- *** Common code end *** ------------------- //

// ------------------- *** Dom4j generation mode start *** ------------------- //
if(("Dom4j").equals(mode)){

	//init the generate tool.
	GenerateToolByDom4j generateToolByDom4j = new GenerateToolByDom4j();
    if(!useStream && ("true").equals(outputAsXSD)){ // add a new prerequisite:useStream to ignore generating the xsd file
    	generateToolByDom4j.outputAsXSD = true;
    }
    if(("true").equals(allowEmpty)){
    	generateToolByDom4j.allowEmpty = true;
    }
    generateToolByDom4j.bAddEmptyAttr = bAddEmptyAttr;
    generateToolByDom4j.bAddUnmappedAttr = bAddUnmappedAttr;
    generateToolByDom4j.cid = cid;
    generateToolByDom4j.tool = tool;
    
    //start generate code

    stringBuffer.append(TEXT_358);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_359);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_360);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_361);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_362);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_363);
     
	generateToolByDom4j.generateCode(root,"root","doc");
	if(isMerge==false || useStream){ // add a prerequisite useStream to ignore the append mode

    stringBuffer.append(TEXT_364);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_365);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_366);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_367);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_368);
    
	}else{

    stringBuffer.append(TEXT_369);
    
	}
	
if(groupTable.size()>0){					//init the generate tool.
	if(!useStream && isMerge==true){//merge into the file and add a prerequisite:useStream to ignore the append mode
		generateToolByDom4j.isAppend= true;
	    String firstGroupPath = groupList.get(0).path;

    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_371);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_372);
    stringBuffer.append(firstGroupPath );
    stringBuffer.append(TEXT_373);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_374);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_375);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_376);
    stringBuffer.append(firstGroupPath.substring(0,firstGroupPath.lastIndexOf("/")) );
    stringBuffer.append(TEXT_377);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_378);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_379);
    stringBuffer.append(firstGroupPath.substring(0,firstGroupPath.lastIndexOf("/")) );
    stringBuffer.append(TEXT_380);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_381);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_382);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_383);
    
		for( int i=0; i<groupList.size();i++){
			XMLNode groupNode= groupList.get(i);
			GenerateExprCmpByDom4j generateExprCmpByDom4j = new GenerateExprCmpByDom4j();
			generateExprCmpByDom4j.tool = tool;
			generateExprCmpByDom4j.groupNode = groupNode;
			generateExprCmpByDom4j.needEmptyNode = ("true").equals(allowEmpty);

    stringBuffer.append(TEXT_384);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_385);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_386);
    stringBuffer.append(groupNode.path);
    stringBuffer.append(TEXT_387);
    generateExprCmpByDom4j.generateCode(groupNode, "tempElem"); 
    stringBuffer.append(TEXT_388);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_389);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_390);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_391);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_392);
     
			generateToolByDom4j.generateCode(groupList.get(i),"group"+i+"_","subTreeRootParent");

    stringBuffer.append(TEXT_393);
    
 		}//for
 	}else{//last group code

    stringBuffer.append(TEXT_394);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_395);
     
		for(int i=0;i<groupList.size();i++){
			XMLNode groupRootNode = groupList.get(i);

    stringBuffer.append(TEXT_396);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_397);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_398);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_399);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_400);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_401);
     
			for(int j=0;j<groupbyNodeList.get(i).size();j++){
				XMLNode attr = groupbyNodeList.get(i).get(j);
				if(attr.relatedColumn!=null){

    stringBuffer.append(TEXT_402);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_403);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_404);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_405);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_406);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_407);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_408);
    tool.getValue(attr);
    stringBuffer.append(TEXT_409);
    tool.getValue(attr);
    stringBuffer.append(TEXT_410);
     
				}
			}

    stringBuffer.append(TEXT_411);
     
			generateToolByDom4j.generateCode(groupList.get(i),"group"+i+"_","subTreeRootParent");

    stringBuffer.append(TEXT_412);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_413);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_414);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_415);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_416);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_417);
     
			for(int j=0;j<groupbyNodeList.get(i).size();j++){
				XMLNode attr = groupbyNodeList.get(i).get(j);

    stringBuffer.append(TEXT_418);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_419);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_420);
    tool.getValue(attr);
    stringBuffer.append(TEXT_421);
     
			}

    stringBuffer.append(TEXT_422);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_423);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_424);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_425);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_426);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_427);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_428);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_429);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_430);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_431);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_432);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_433);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_434);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_435);
    
		}//for
 	}//if merge=true 	
}


    stringBuffer.append(TEXT_436);
    
	if(loopTable!=null && loopTable.size()>0){
		generateToolByDom4j.generateCode(loop,"loop","subTreeRootParent");
	}
	//file split
	if(!useStream && ("true").equals(split)){ //add a prerequisite:useStream to ignore the append mode

    stringBuffer.append(TEXT_437);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_438);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_439);
    stringBuffer.append( splitEvery);
    stringBuffer.append(TEXT_440);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_441);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_442);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_443);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_444);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_445);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_446);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_447);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_448);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_449);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_450);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_451);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_452);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_453);
    
		if(("true").equals(outputAsXSD)){

    stringBuffer.append(TEXT_454);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_455);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_456);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_457);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_458);
    
		}

    stringBuffer.append(TEXT_459);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_460);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_461);
    
		if(!("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_462);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_463);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_464);
    
		}

    stringBuffer.append(TEXT_465);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_466);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_467);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_468);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_469);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_470);
    
	}
}
// ------------------- *** Dom4j generation mode end *** ------------------- //

// ------------------- *** Null generation mode start *** ------------------- //
else if(("Null").equals(mode)){

	//init the generate tool.
	GenerateToolByNull generateToolByNull = new GenerateToolByNull();
    if(!useStream && ("true").equals(outputAsXSD)){ // add a new prerequisite:useStream to ignore generating the xsd file
    	generateToolByNull.outputAsXSD = true;
    }
    if(("true").equals(allowEmpty)){
    	generateToolByNull.allowEmpty = true;
    }
    generateToolByNull.bAddEmptyAttr = bAddEmptyAttr;
    generateToolByNull.bAddUnmappedAttr = bAddUnmappedAttr;
    generateToolByNull.cid = cid;
    generateToolByNull.tool = tool;
    generateToolByNull.isCompact = isCompact;

    stringBuffer.append(TEXT_471);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_472);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_473);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_474);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_475);
    
	String rootEmptySpace = "";
	for(int i=0;i < mainList.size();i++){
		generateToolByNull.generateCode(mainList.get(i),rootEmptySpace);
		if(isCompact==false){//generate pretty file
			rootEmptySpace+="  ";
		}
		
		if(!generateToolByNull.outputAsXSD && !generateToolByNull.allowEmpty){
			if(mainList.get(i).relatedColumn != null || mainList.get(i).childrenColumnList.size()>0){

    stringBuffer.append(TEXT_476);
    
                	for(IMetadataColumn column : mainList.get(i).childrenColumnList){
                		
    stringBuffer.append(TEXT_477);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_478);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_479);
    
                	}

    stringBuffer.append(TEXT_480);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_481);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_482);
    
			}
		}

    stringBuffer.append(TEXT_483);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_484);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_485);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_486);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_487);
    
	}

    stringBuffer.append(TEXT_488);
    
	if(groupTable.size()>0){

    stringBuffer.append(TEXT_489);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_490);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_491);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_492);
    stringBuffer.append(groupList.size());
    stringBuffer.append(TEXT_493);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_494);
    stringBuffer.append(groupList.size());
    stringBuffer.append(TEXT_495);
    
	}
	for(int i=0;i<groupList.size();i++){
		XMLNode groupRootNode = groupList.get(i);

    stringBuffer.append(TEXT_496);
    stringBuffer.append(groupRootNode.name);
    stringBuffer.append(TEXT_497);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_498);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_499);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_500);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_501);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_502);
    
		for(int j=0;j<groupbyNodeList.get(i).size();j++){
			XMLNode attr = groupbyNodeList.get(i).get(j);
			if(attr.relatedColumn!=null){

    stringBuffer.append(TEXT_503);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_504);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_505);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_506);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_507);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_508);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_509);
    tool.getValue(attr);
    stringBuffer.append(TEXT_510);
    tool.getValue(attr);
    stringBuffer.append(TEXT_511);
    
			}
		}

    stringBuffer.append(TEXT_512);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_513);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_514);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_515);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_516);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_517);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_518);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_519);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_520);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_521);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_522);
    
		for(int j=0;j<groupbyNodeList.get(i).size();j++){
			XMLNode attr = groupbyNodeList.get(i).get(j);

    stringBuffer.append(TEXT_523);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_524);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_525);
    tool.getValue(attr);
    stringBuffer.append(TEXT_526);
    
		}

    stringBuffer.append(TEXT_527);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_528);
    
		String emptySpace = "";
		
		if(isCompact==false){//generate pretty file
			for(int len = groupList.get(i).path.split("/").length-1;len>1;len--){
				emptySpace +="  ";
			}
		}
		
		generateToolByNull.generateCode(groupList.get(i),emptySpace);
		
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){
			if((groupList.get(i).relatedColumn != null || groupList.get(i).childrenColumnList.size()>0)){

    stringBuffer.append(TEXT_529);
    
            	for(IMetadataColumn column : groupList.get(i).childrenColumnList){
            		
    stringBuffer.append(TEXT_530);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_531);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_532);
    
            	}

    stringBuffer.append(TEXT_533);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_534);
    stringBuffer.append(i+mainList.size());
    stringBuffer.append(TEXT_535);
    
			}
		}

    stringBuffer.append(TEXT_536);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_537);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_538);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_539);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_540);
    
	}//End of groupList loop
	
	if(groupTable.size()>0){

    stringBuffer.append(TEXT_541);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_542);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_543);
    
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_544);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_545);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_546);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_547);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_548);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_549);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_550);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_551);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_552);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_553);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_554);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_555);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_556);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_557);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_558);
    
		}else{

    stringBuffer.append(TEXT_559);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_560);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_561);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_562);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_563);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_564);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_565);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_566);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_567);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_568);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_569);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_570);
    
		}
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_571);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_572);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_573);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_574);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_575);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_576);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_577);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_578);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_579);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_580);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_581);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_582);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_583);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_584);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_585);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_586);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_587);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_588);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_589);
    if(isCompact==false){
    stringBuffer.append(TEXT_590);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_591);
    }
    stringBuffer.append(TEXT_592);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_593);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_594);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_595);
    
		}

    stringBuffer.append(TEXT_596);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_597);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_598);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_599);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_600);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_601);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_602);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_603);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_604);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_605);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_606);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_607);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_608);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_609);
    if(isCompact==false){
    stringBuffer.append(TEXT_610);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_611);
    }
    stringBuffer.append(TEXT_612);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_613);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_614);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_615);
    
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_616);
    
		}

    stringBuffer.append(TEXT_617);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_618);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_619);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_620);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_621);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_622);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_623);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_624);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_625);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_626);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_627);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_628);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_629);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_630);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_631);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_632);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_633);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_634);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_635);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_636);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_637);
    
	}
	if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_638);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_639);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_640);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_641);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_642);
    stringBuffer.append(mainList.size()-1);
    stringBuffer.append(TEXT_643);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_644);
    stringBuffer.append(mainList.size()-1);
    stringBuffer.append(TEXT_645);
    
	}

    stringBuffer.append(TEXT_646);
    
	String emptySpace = "";
	if(isCompact==false){//generate pretty file
		for(int len =loop.path.split("/").length-1;len>1;len--){
			emptySpace +="  ";
		}
	}
	if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_647);
    
    	for(IMetadataColumn column : loop.childrenColumnList){
    		
    stringBuffer.append(TEXT_648);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_649);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_650);
    
    	}
    	if(loop.hasDefaultValue == true){
    stringBuffer.append(TEXT_651);
    }
    stringBuffer.append(TEXT_652);
    
	}
	generateToolByNull.generateCode(loop,emptySpace);
	generateToolByNull.endElement(loop,emptySpace);

    stringBuffer.append(TEXT_653);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_654);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_655);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_656);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_657);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_658);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_659);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_660);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_661);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_662);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_663);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_664);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_665);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_666);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_667);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_668);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_669);
    stringBuffer.append(groupList.size()+mainList.size());
    stringBuffer.append(TEXT_670);
    
	if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_671);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_672);
    stringBuffer.append(groupList.size()+mainList.size()-1);
    stringBuffer.append(TEXT_673);
    
	}
	
	//file split 
	if(!useStream && ("true").equals(split)){ // add a new prerequisite:useStream to ignore the split condition

    stringBuffer.append(TEXT_674);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_675);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_676);
    stringBuffer.append( splitEvery);
    stringBuffer.append(TEXT_677);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_678);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_679);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_680);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_681);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_682);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_683);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_684);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_685);
    
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_686);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_687);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_688);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_689);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_690);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_691);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_692);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_693);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_694);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_695);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_696);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_697);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_698);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_699);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_700);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_701);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_702);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_703);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_704);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_705);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_706);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_707);
    if(isCompact==false){
    stringBuffer.append(TEXT_708);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_709);
    }
    stringBuffer.append(TEXT_710);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_711);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_712);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_713);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_714);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_715);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_716);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_717);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_718);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_719);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_720);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_721);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_722);
    if(isCompact==false){
    stringBuffer.append(TEXT_723);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_724);
    }
    stringBuffer.append(TEXT_725);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_726);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_727);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_728);
    
		}else{
			if(loopTable.size()>0){

    stringBuffer.append(TEXT_729);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_730);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_731);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_732);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_733);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_734);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_735);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_736);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_737);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_738);
    if(isCompact==false){
    stringBuffer.append(TEXT_739);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_740);
    }
    stringBuffer.append(TEXT_741);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_742);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_743);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_744);
    
			}
		}

    stringBuffer.append(TEXT_745);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_746);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_747);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_748);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_749);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_750);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_751);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_752);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_753);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_754);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_755);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_756);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_757);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_758);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_759);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_760);
    
	}
}
// ------------------- *** Null generation mode end *** ------------------- //

// ------------------- *** Common code start *** ------------------- //
			}
		}
	}
}
// ------------------- *** Common code end *** ------------------- //

    stringBuffer.append(TEXT_761);
    return stringBuffer.toString();
  }
}
