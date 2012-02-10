package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

/**
 * add by xzhang
 */
public class TWriteXMLFieldOutMainJava {

  protected static String nl;
  public static synchronized TWriteXMLFieldOutMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWriteXMLFieldOutMainJava result = new TWriteXMLFieldOutMainJava();
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
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = ".doubleValue()), ";
  protected final String TEXT_11 = ",";
  protected final String TEXT_12 = ")\t\t\t\t\t";
  protected final String TEXT_13 = NL + "    \t\tFormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = "), ";
  protected final String TEXT_16 = ",";
  protected final String TEXT_17 = ")\t\t\t\t\t\t";
  protected final String TEXT_18 = NL + "            String.valueOf(";
  protected final String TEXT_19 = ".";
  protected final String TEXT_20 = ")";
  protected final String TEXT_21 = NL + "            FormatterUtils.format_Date(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ",";
  protected final String TEXT_24 = ")";
  protected final String TEXT_25 = NL + "\t\t\t";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = NL + "\t\t\tString.valueOf(";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = ".doubleValue())";
  protected final String TEXT_30 = NL + "            ";
  protected final String TEXT_31 = ".";
  protected final String TEXT_32 = ".toString()";
  protected final String TEXT_33 = ":";
  protected final String TEXT_34 = "null";
  protected final String TEXT_35 = NL + "\t\t)";
  protected final String TEXT_36 = NL + "\t\t\t";
  protected final String TEXT_37 = "_";
  protected final String TEXT_38 = ".setName(\"";
  protected final String TEXT_39 = "\");";
  protected final String TEXT_40 = NL + "\t\t\tif (";
  protected final String TEXT_41 = "_";
  protected final String TEXT_42 = ".content().size() == 0 " + NL + "\t\t\t\t&& ";
  protected final String TEXT_43 = "_";
  protected final String TEXT_44 = ".attributes().size() == 0 " + NL + "\t\t\t\t&& ";
  protected final String TEXT_45 = "_";
  protected final String TEXT_46 = ".declaredNamespaces().size() == 0) {";
  protected final String TEXT_47 = NL + "                ";
  protected final String TEXT_48 = "_";
  protected final String TEXT_49 = ".remove(";
  protected final String TEXT_50 = "_";
  protected final String TEXT_51 = ");" + NL + "            }" + NL + "\t\t\t";
  protected final String TEXT_52 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_53 = "_";
  protected final String TEXT_54 = ";" + NL + "\t\tif (";
  protected final String TEXT_55 = "_";
  protected final String TEXT_56 = ".getNamespaceForPrefix(\"";
  protected final String TEXT_57 = "\") == null) {";
  protected final String TEXT_58 = NL + "            ";
  protected final String TEXT_59 = "_";
  protected final String TEXT_60 = " = org.dom4j.DocumentHelper.createElement(\"";
  protected final String TEXT_61 = "\");" + NL + "        } else {" + NL + "        \t";
  protected final String TEXT_62 = "_";
  protected final String TEXT_63 = " = org.dom4j.DocumentHelper.createElement(\"";
  protected final String TEXT_64 = "\");" + NL + "        }";
  protected final String TEXT_65 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_66 = "_";
  protected final String TEXT_67 = " = org.dom4j.DocumentHelper.createElement(\"";
  protected final String TEXT_68 = "\");";
  protected final String TEXT_69 = NL + "        if(orders_";
  protected final String TEXT_70 = "[";
  protected final String TEXT_71 = "]==0){" + NL + "        \torders_";
  protected final String TEXT_72 = "[";
  protected final String TEXT_73 = "] = ";
  protected final String TEXT_74 = ";" + NL + "        }" + NL + "        if(";
  protected final String TEXT_75 = " < orders_";
  protected final String TEXT_76 = ".length){" + NL + "        \t\torders_";
  protected final String TEXT_77 = "[";
  protected final String TEXT_78 = "] = 0;" + NL + "        }";
  protected final String TEXT_79 = NL + "        ";
  protected final String TEXT_80 = "_";
  protected final String TEXT_81 = ".elements().add(orders_";
  protected final String TEXT_82 = "[";
  protected final String TEXT_83 = "]++,";
  protected final String TEXT_84 = "_";
  protected final String TEXT_85 = ");";
  protected final String TEXT_86 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_87 = "_";
  protected final String TEXT_88 = ";" + NL + "\t\tif (";
  protected final String TEXT_89 = "_";
  protected final String TEXT_90 = ".getNamespaceForPrefix(\"";
  protected final String TEXT_91 = "\") == null) {";
  protected final String TEXT_92 = NL + "            ";
  protected final String TEXT_93 = "_";
  protected final String TEXT_94 = " = ";
  protected final String TEXT_95 = "_";
  protected final String TEXT_96 = ".addElement(\"";
  protected final String TEXT_97 = "\");" + NL + "        } else {" + NL + "        \t";
  protected final String TEXT_98 = "_";
  protected final String TEXT_99 = " = ";
  protected final String TEXT_100 = "_";
  protected final String TEXT_101 = ".addElement(\"";
  protected final String TEXT_102 = "\");" + NL + "        }";
  protected final String TEXT_103 = NL + "\t\torg.dom4j.Element ";
  protected final String TEXT_104 = "_";
  protected final String TEXT_105 = " = ";
  protected final String TEXT_106 = "_";
  protected final String TEXT_107 = ".addElement(\"";
  protected final String TEXT_108 = "\");";
  protected final String TEXT_109 = NL + "\t\tsubTreeRootParent_";
  protected final String TEXT_110 = " = ";
  protected final String TEXT_111 = "_";
  protected final String TEXT_112 = ";";
  protected final String TEXT_113 = NL + "\t\tif(";
  protected final String TEXT_114 = "!=null){" + NL + "\t\t\tnestXMLTool_";
  protected final String TEXT_115 = " .parseAndAdd(";
  protected final String TEXT_116 = "_";
  protected final String TEXT_117 = ",";
  protected final String TEXT_118 = ");" + NL + "\t\t}";
  protected final String TEXT_119 = NL + "\t\telse{" + NL + "\t\t\tnestXMLTool_";
  protected final String TEXT_120 = " .parseAndAdd(";
  protected final String TEXT_121 = "_";
  protected final String TEXT_122 = ",\"\");" + NL + "\t\t\t";
  protected final String TEXT_123 = "_";
  protected final String TEXT_124 = ".addAttribute(\"xsi:nil\",\"true\");" + NL + "\t\t}";
  protected final String TEXT_125 = NL + "\t\tif(";
  protected final String TEXT_126 = "!=null){" + NL + "\t\t\tnestXMLTool_";
  protected final String TEXT_127 = " .setText(";
  protected final String TEXT_128 = "_";
  protected final String TEXT_129 = ",";
  protected final String TEXT_130 = ");" + NL + "\t\t}";
  protected final String TEXT_131 = NL + "\t\telse{" + NL + "\t\t\t";
  protected final String TEXT_132 = "_";
  protected final String TEXT_133 = ".setText(\"\");" + NL + "\t\t\t";
  protected final String TEXT_134 = "_";
  protected final String TEXT_135 = ".addAttribute(\"xsi:nil\",\"true\");" + NL + "\t\t}";
  protected final String TEXT_136 = NL + "\t\tnestXMLTool_";
  protected final String TEXT_137 = ".parseAndAdd(";
  protected final String TEXT_138 = "_";
  protected final String TEXT_139 = ",\"";
  protected final String TEXT_140 = "\");";
  protected final String TEXT_141 = NL + "\t\tif(";
  protected final String TEXT_142 = "!=null){" + NL + "\t\t\t";
  protected final String TEXT_143 = "_";
  protected final String TEXT_144 = ".addAttribute(\"";
  protected final String TEXT_145 = "\",";
  protected final String TEXT_146 = ");" + NL + "\t\t}";
  protected final String TEXT_147 = NL + "\t\t";
  protected final String TEXT_148 = "_";
  protected final String TEXT_149 = ".addAttribute(\"";
  protected final String TEXT_150 = "\", \"";
  protected final String TEXT_151 = "\");";
  protected final String TEXT_152 = NL + "\t\tif(";
  protected final String TEXT_153 = "!=null){" + NL + "\t\t\t";
  protected final String TEXT_154 = "_";
  protected final String TEXT_155 = ".addNamespace(\"";
  protected final String TEXT_156 = "\",TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_157 = "));";
  protected final String TEXT_158 = NL + "        \t";
  protected final String TEXT_159 = "_";
  protected final String TEXT_160 = ".setQName(org.dom4j.DocumentHelper.createQName(";
  protected final String TEXT_161 = "_";
  protected final String TEXT_162 = ".getName()," + NL + "        \torg.dom4j.DocumentHelper.createNamespace(\"\",TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_163 = "))));";
  protected final String TEXT_164 = NL + "\t\t}";
  protected final String TEXT_165 = NL + "\t\t\t";
  protected final String TEXT_166 = "_";
  protected final String TEXT_167 = ".addNamespace(\"";
  protected final String TEXT_168 = "\",TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_169 = "\"));";
  protected final String TEXT_170 = NL + "        \t";
  protected final String TEXT_171 = "_";
  protected final String TEXT_172 = ".setQName(org.dom4j.DocumentHelper.createQName(";
  protected final String TEXT_173 = "_";
  protected final String TEXT_174 = ".getName()," + NL + "        \torg.dom4j.DocumentHelper.createNamespace(\"\",TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_175 = "\"))));";
  protected final String TEXT_176 = NL + "    \t// buffer the start tabs to group buffer" + NL + "    \tgroupBuffer_";
  protected final String TEXT_177 = "[";
  protected final String TEXT_178 = "] = buf_";
  protected final String TEXT_179 = ".toString();" + NL + "        buf_";
  protected final String TEXT_180 = " = new StringBuffer();";
  protected final String TEXT_181 = NL + "\t\tstartTabs_";
  protected final String TEXT_182 = "[";
  protected final String TEXT_183 = "] = buf_";
  protected final String TEXT_184 = ".toString();" + NL + "        buf_";
  protected final String TEXT_185 = " = new StringBuffer();";
  protected final String TEXT_186 = NL + "\t\tout_";
  protected final String TEXT_187 = ".write(buf_";
  protected final String TEXT_188 = ".toString());" + NL + "        buf_";
  protected final String TEXT_189 = " = new StringBuffer();";
  protected final String TEXT_190 = NL + "\t\tif( false";
  protected final String TEXT_191 = " || valueMap_";
  protected final String TEXT_192 = ".get(\"";
  protected final String TEXT_193 = "\") != null";
  protected final String TEXT_194 = " || true " + NL + "                    \t";
  protected final String TEXT_195 = NL + "\t\t){";
  protected final String TEXT_196 = NL + "\t\t}";
  protected final String TEXT_197 = NL + "\t\tbuf_";
  protected final String TEXT_198 = ".append(\"";
  protected final String TEXT_199 = "\");" + NL + "\t\tbuf_";
  protected final String TEXT_200 = ".append(\"";
  protected final String TEXT_201 = "<";
  protected final String TEXT_202 = "\");";
  protected final String TEXT_203 = NL + "\t\tbuf_";
  protected final String TEXT_204 = ".append(\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"\");" + NL + "\t\tbuf_";
  protected final String TEXT_205 = ".append(\" xsi:noNamespaceSchemaLocation= \\\"\"+";
  protected final String TEXT_206 = "+\"\\\"\");";
  protected final String TEXT_207 = NL + "\t\tif(";
  protected final String TEXT_208 = "==null){" + NL + "\t\t\tbuf_";
  protected final String TEXT_209 = ".append(\" xsi:nil=\\\"true\\\"\");" + NL + "\t\t}";
  protected final String TEXT_210 = NL + "\t\tbuf_";
  protected final String TEXT_211 = ".append(\">\");";
  protected final String TEXT_212 = NL + "\t\tbuf_";
  protected final String TEXT_213 = ".append(\"";
  protected final String TEXT_214 = "\");" + NL + "\t\tbuf_";
  protected final String TEXT_215 = ".append(\"";
  protected final String TEXT_216 = "</";
  protected final String TEXT_217 = ">\");";
  protected final String TEXT_218 = NL + "\t\tbuf_";
  protected final String TEXT_219 = ".append(\"</";
  protected final String TEXT_220 = ">\");";
  protected final String TEXT_221 = NL + "\t\tif(";
  protected final String TEXT_222 = "!=null){" + NL + "\t\t\tbuf_";
  protected final String TEXT_223 = ".append(";
  protected final String TEXT_224 = ");" + NL + "\t\t}";
  protected final String TEXT_225 = NL + "\t\tif(";
  protected final String TEXT_226 = "!=null){" + NL + "\t\t\tbuf_";
  protected final String TEXT_227 = ".append(TalendString.checkCDATAForXML(";
  protected final String TEXT_228 = "));" + NL + "\t\t}";
  protected final String TEXT_229 = NL + "\t\tbuf_";
  protected final String TEXT_230 = ".append(\"";
  protected final String TEXT_231 = "\");";
  protected final String TEXT_232 = NL + "\t\tif(";
  protected final String TEXT_233 = "!=null){" + NL + "\t\t\tbuf_";
  protected final String TEXT_234 = ".append(\" ";
  protected final String TEXT_235 = "=\\\"\"+TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_236 = ")+\"\\\"\");" + NL + "\t\t}";
  protected final String TEXT_237 = NL + "\t\tbuf_";
  protected final String TEXT_238 = ".append(\" ";
  protected final String TEXT_239 = "=\\\"\"+TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_240 = "\")+\"\\\"\");";
  protected final String TEXT_241 = NL + "\t\tif(";
  protected final String TEXT_242 = "!=null){";
  protected final String TEXT_243 = NL + "        \tbuf_";
  protected final String TEXT_244 = ".append(\" xmlns=\\\"\"+TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_245 = ")+\"\\\"\");";
  protected final String TEXT_246 = NL + "\t\t\tbuf_";
  protected final String TEXT_247 = ".append(\" xmlns:";
  protected final String TEXT_248 = "=\\\"\"+TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_249 = ")+\"\\\"\");";
  protected final String TEXT_250 = NL + "\t\t}";
  protected final String TEXT_251 = NL + "        \tbuf_";
  protected final String TEXT_252 = ".append(\" xmlns=\\\"\"+TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_253 = "\")+\"\\\"\");";
  protected final String TEXT_254 = NL + "\t\t\tbuf_";
  protected final String TEXT_255 = ".append(\" xmlns:";
  protected final String TEXT_256 = "=\\\"\"+TalendString.replaceSpecialCharForXML(\"";
  protected final String TEXT_257 = "\")+\"\\\"\");";
  protected final String TEXT_258 = NL + "\tnb_line_";
  protected final String TEXT_259 = "++;" + NL + "\tvalueMap_";
  protected final String TEXT_260 = ".clear();";
  protected final String TEXT_261 = NL + "\tvalueMap_";
  protected final String TEXT_262 = ".put(\"";
  protected final String TEXT_263 = "\",";
  protected final String TEXT_264 = ");";
  protected final String TEXT_265 = NL + "\tflowValues_";
  protected final String TEXT_266 = " = new java.util.HashMap<String,String>();" + NL + "\tflowValues_";
  protected final String TEXT_267 = ".putAll(valueMap_";
  protected final String TEXT_268 = ");" + NL + "\tflows_";
  protected final String TEXT_269 = ".add(flowValues_";
  protected final String TEXT_270 = ");";
  protected final String TEXT_271 = NL + "\t\tString strTemp_";
  protected final String TEXT_272 = " = \"\";";
  protected final String TEXT_273 = "\t\tstrTemp_";
  protected final String TEXT_274 = " = strTemp_";
  protected final String TEXT_275 = " + valueMap_";
  protected final String TEXT_276 = ".get(\"";
  protected final String TEXT_277 = "\")" + NL + "\t\t\t\t\t\t\t+ valueMap_";
  protected final String TEXT_278 = ".get(\"";
  protected final String TEXT_279 = "\").length();";
  protected final String TEXT_280 = NL + "\tif(strCompCache_";
  protected final String TEXT_281 = "==null){" + NL + "\t\tstrCompCache_";
  protected final String TEXT_282 = "=strTemp_";
  protected final String TEXT_283 = ";" + NL + "\t}else{";
  protected final String TEXT_284 = NL + "\t\t//the data read is different from the data read last time. " + NL + "\t\tif(strCompCache_";
  protected final String TEXT_285 = ".equals(strTemp_";
  protected final String TEXT_286 = ")==false){\t";
  protected final String TEXT_287 = NL + "\t\t\tdoc_";
  protected final String TEXT_288 = ".getRootElement().addAttribute(\"xsi:noNamespaceSchemaLocation\", ";
  protected final String TEXT_289 = ");" + NL + "\t\t    doc_";
  protected final String TEXT_290 = ".getRootElement().addNamespace(\"xsi\", \"http://www.w3.org/2001/XMLSchema-instance\");";
  protected final String TEXT_291 = "  " + NL + "    \t\tnestXMLTool_";
  protected final String TEXT_292 = ".replaceDefaultNameSpace(doc_";
  protected final String TEXT_293 = ".getRootElement());";
  protected final String TEXT_294 = NL + "    \t\tnestXMLTool_";
  protected final String TEXT_295 = ".removeEmptyElement(doc_";
  protected final String TEXT_296 = ".getRootElement());";
  protected final String TEXT_297 = "\t\t\t" + NL + "\t\t\tjava.io.StringWriter strWriter_";
  protected final String TEXT_298 = " = new java.io.StringWriter();\t" + NL + "\t\t\torg.dom4j.io.XMLWriter output_";
  protected final String TEXT_299 = " = new org.dom4j.io.XMLWriter(strWriter_";
  protected final String TEXT_300 = ", format_";
  protected final String TEXT_301 = ");" + NL + "\t\t\toutput_";
  protected final String TEXT_302 = ".write(doc_";
  protected final String TEXT_303 = ");" + NL + "\t\t    output_";
  protected final String TEXT_304 = ".close();" + NL + "\t\t\t";
  protected final String TEXT_305 = NL + "\t\t\tString removeHeader_";
  protected final String TEXT_306 = " = strWriter_";
  protected final String TEXT_307 = ".toString();" + NL + "\t\t\tif(removeHeader_";
  protected final String TEXT_308 = ".indexOf(\"<?xml\") >=0 ){" + NL + "\t\t\t\tremoveHeader_";
  protected final String TEXT_309 = " = removeHeader_";
  protected final String TEXT_310 = ".substring(removeHeader_";
  protected final String TEXT_311 = ".indexOf(\"?>\")+3);" + NL + "\t\t\t}" + NL + "\t\t\tlistGroupby_";
  protected final String TEXT_312 = ".add(removeHeader_";
  protected final String TEXT_313 = ");";
  protected final String TEXT_314 = NL + "\t\t    listGroupby_";
  protected final String TEXT_315 = ".add(strWriter_";
  protected final String TEXT_316 = ".toString());";
  protected final String TEXT_317 = NL + "\t\t    doc_";
  protected final String TEXT_318 = ".clearContent();" + NL + "\t\t\tneedRoot_";
  protected final String TEXT_319 = " = true;" + NL + "\t\t\tfor(int i_";
  protected final String TEXT_320 = "=0;i_";
  protected final String TEXT_321 = "<orders_";
  protected final String TEXT_322 = ".length;i_";
  protected final String TEXT_323 = "++){" + NL + "\t\t\t\torders_";
  protected final String TEXT_324 = "[i_";
  protected final String TEXT_325 = "] = 0;" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\tif(groupbyList_";
  protected final String TEXT_326 = " != null && groupbyList_";
  protected final String TEXT_327 = ".size() >= 0){" + NL + "\t\t\t\tgroupbyList_";
  protected final String TEXT_328 = ".clear();" + NL + "\t\t\t}" + NL + "\t\t\tstrCompCache_";
  protected final String TEXT_329 = "=strTemp_";
  protected final String TEXT_330 = ";";
  protected final String TEXT_331 = NL + "\t\t}";
  protected final String TEXT_332 = NL + "\t}" + NL;
  protected final String TEXT_333 = NL + "\torg.dom4j.Element subTreeRootParent_";
  protected final String TEXT_334 = " = null;" + NL + "\t" + NL + "\t// build root xml tree " + NL + "\tif (needRoot_";
  protected final String TEXT_335 = ") {" + NL + "\t\tneedRoot_";
  protected final String TEXT_336 = "=false;";
  protected final String TEXT_337 = NL + "\t\troot4Group_";
  protected final String TEXT_338 = " = subTreeRootParent_";
  protected final String TEXT_339 = ";" + NL + "\t}else{" + NL + "\t\tsubTreeRootParent_";
  protected final String TEXT_340 = "=root4Group_";
  protected final String TEXT_341 = ";" + NL + "\t}" + NL + "\t// build group xml tree ";
  protected final String TEXT_342 = NL + "\tboolean isNewElememt = false;";
  protected final String TEXT_343 = NL + "\tif(isNewElememt || groupbyList_";
  protected final String TEXT_344 = ".size()<=";
  protected final String TEXT_345 = " || groupbyList_";
  protected final String TEXT_346 = ".get(";
  protected final String TEXT_347 = ")==null";
  protected final String TEXT_348 = NL + "\t|| ( groupbyList_";
  protected final String TEXT_349 = ".get(";
  protected final String TEXT_350 = ").get(";
  protected final String TEXT_351 = ")!=null " + NL + "\t\t? !groupbyList_";
  protected final String TEXT_352 = ".get(";
  protected final String TEXT_353 = ").get(";
  protected final String TEXT_354 = ").equals(";
  protected final String TEXT_355 = ") " + NL + "\t\t: ";
  protected final String TEXT_356 = "!=null )";
  protected final String TEXT_357 = NL + "\t){";
  protected final String TEXT_358 = NL + "\t\tif(groupbyList_";
  protected final String TEXT_359 = ".size()<=";
  protected final String TEXT_360 = "){" + NL + "        \tgroupbyList_";
  protected final String TEXT_361 = ".add(new java.util.ArrayList<String>());" + NL + "        }else{" + NL + "        \tgroupbyList_";
  protected final String TEXT_362 = ".get(";
  protected final String TEXT_363 = ").clear();" + NL + "        }";
  protected final String TEXT_364 = NL + "\t\tgroupbyList_";
  protected final String TEXT_365 = ".get(";
  protected final String TEXT_366 = ").add(";
  protected final String TEXT_367 = ");";
  protected final String TEXT_368 = NL + "        isNewElememt=true;" + NL + "        if(groupElementList_";
  protected final String TEXT_369 = ".size()<=";
  protected final String TEXT_370 = "){" + NL + "\t\t\tgroupElementList_";
  protected final String TEXT_371 = ".add(group";
  protected final String TEXT_372 = "__";
  protected final String TEXT_373 = ");" + NL + "        }else{" + NL + "        \tgroupElementList_";
  protected final String TEXT_374 = ".set(";
  protected final String TEXT_375 = ",group";
  protected final String TEXT_376 = "__";
  protected final String TEXT_377 = ");" + NL + "        }" + NL + "        " + NL + "\t}else{" + NL + "\t\tsubTreeRootParent_";
  protected final String TEXT_378 = "=groupElementList_";
  protected final String TEXT_379 = ".get(";
  protected final String TEXT_380 = ");" + NL + "\t}";
  protected final String TEXT_381 = NL + "\t// build loop xml tree";
  protected final String TEXT_382 = NL + "\t\t//the data read is different from the data read last time. " + NL + "\t\tif(strCompCache_";
  protected final String TEXT_383 = ".equals(strTemp_";
  protected final String TEXT_384 = ")==false){";
  protected final String TEXT_385 = NL + NL + "\t\t // write the endtag to the StringWriter:strWriter_tWriteXMLField_1_Out" + NL + "\t\t // close the bufferWriter" + NL + "\t\t // add the data in strWriter_tWriteXMLField_1_Out to listGroupby\t\t\t\t\t\t\t " + NL + "" + NL + "\t\tif (preUnNullMaxIndex_";
  protected final String TEXT_386 = " >= 0) {" + NL + "\t        // output all buffer" + NL + "\t        for (int j_";
  protected final String TEXT_387 = " = 0; j_";
  protected final String TEXT_388 = " <= preUnNullMaxIndex_";
  protected final String TEXT_389 = "; j_";
  protected final String TEXT_390 = "++) {" + NL + "\t            if (startTabs_";
  protected final String TEXT_391 = "[j_";
  protected final String TEXT_392 = "] != null)" + NL + "\t                out_";
  protected final String TEXT_393 = ".write(startTabs_";
  protected final String TEXT_394 = "[j_";
  protected final String TEXT_395 = "]);" + NL + "\t        }" + NL + "\t" + NL + "\t        if (preUnNullMaxIndex_";
  protected final String TEXT_396 = " < preNewTabIndex_";
  protected final String TEXT_397 = " ) {" + NL + "\t\t\t\tfor (int i_";
  protected final String TEXT_398 = " = preNewTabIndex_";
  protected final String TEXT_399 = " - 1; i_";
  protected final String TEXT_400 = " >= 0; i_";
  protected final String TEXT_401 = "--) {" + NL + "                \tif(endTabs_";
  protected final String TEXT_402 = "[i_";
  protected final String TEXT_403 = "]!=null){" + NL + "                \t\tout_";
  protected final String TEXT_404 = ".write(endTabs_";
  protected final String TEXT_405 = "[i_";
  protected final String TEXT_406 = "]);" + NL + "                \t}                \t" + NL + "\t                out_";
  protected final String TEXT_407 = ".write(\"";
  protected final String TEXT_408 = "\");" + NL + "\t                out_";
  protected final String TEXT_409 = ".write(endTabStrs_";
  protected final String TEXT_410 = ".get(i_";
  protected final String TEXT_411 = "));" + NL + "\t            }" + NL + "\t        } else {" + NL + "\t            for (int i_";
  protected final String TEXT_412 = " = preUnNullMaxIndex_";
  protected final String TEXT_413 = "; i_";
  protected final String TEXT_414 = " >= 0; i_";
  protected final String TEXT_415 = "--) {" + NL + "                \tif(endTabs_";
  protected final String TEXT_416 = "[i_";
  protected final String TEXT_417 = "]!=null){" + NL + "                \t\tout_";
  protected final String TEXT_418 = ".write(endTabs_";
  protected final String TEXT_419 = "[i_";
  protected final String TEXT_420 = "]);" + NL + "                \t}" + NL + "                \tout_";
  protected final String TEXT_421 = ".write(\"";
  protected final String TEXT_422 = "\");" + NL + "\t                out_";
  protected final String TEXT_423 = ".write(endTabStrs_";
  protected final String TEXT_424 = ".get(i_";
  protected final String TEXT_425 = "));" + NL + "\t            }" + NL + "\t        }" + NL + "\t    }";
  protected final String TEXT_426 = NL + "\t\tfor (int i_";
  protected final String TEXT_427 = " = endTabStrs_";
  protected final String TEXT_428 = ".size() - 1; i_";
  protected final String TEXT_429 = " >= 0; i_";
  protected final String TEXT_430 = "--) {" + NL + "        \tif(endTabs_";
  protected final String TEXT_431 = "[i_";
  protected final String TEXT_432 = "]!=null){" + NL + "        \t\tout_";
  protected final String TEXT_433 = ".write(endTabs_";
  protected final String TEXT_434 = "[i_";
  protected final String TEXT_435 = "]);" + NL + "        \t}" + NL + "\t        out_";
  protected final String TEXT_436 = ".write(\"";
  protected final String TEXT_437 = "\");" + NL + "\t        out_";
  protected final String TEXT_438 = ".write(endTabStrs_";
  protected final String TEXT_439 = ".get(i_";
  protected final String TEXT_440 = "));" + NL + "\t    }";
  protected final String TEXT_441 = NL + "\t\tfor (int i_";
  protected final String TEXT_442 = " = 0; i_";
  protected final String TEXT_443 = " < endTabs_";
  protected final String TEXT_444 = ".length; i_";
  protected final String TEXT_445 = "++) {" + NL + "\t\t\tstartTabs_";
  protected final String TEXT_446 = "[i_";
  protected final String TEXT_447 = "] = null;" + NL + "\t\t\tendTabs_";
  protected final String TEXT_448 = "[i_";
  protected final String TEXT_449 = "] = null;" + NL + "\t\t}" + NL + "//\t\tendTabStrs_";
  protected final String TEXT_450 = ".clear();" + NL + "\t\tout_";
  protected final String TEXT_451 = ".write(\"";
  protected final String TEXT_452 = "\");" + NL + "\t\tout_";
  protected final String TEXT_453 = ".close();" + NL + "\t\tlistGroupby_";
  protected final String TEXT_454 = ".add(strWriter_";
  protected final String TEXT_455 = ".toString());" + NL + "" + NL + "\t\t//create a new StringWriter and BufferWriter" + NL + "\t\t//write the head title to the StringWriter\t\t" + NL + "\t\tstrWriter_";
  protected final String TEXT_456 = " = new java.io.StringWriter();" + NL + "\t\tout_";
  protected final String TEXT_457 = " = new java.io.BufferedWriter(strWriter_";
  protected final String TEXT_458 = ");";
  protected final String TEXT_459 = NL + "\t\tout_";
  protected final String TEXT_460 = ".write(\"<?xml version=\\\"1.0\\\" encoding=\\\"\"+";
  protected final String TEXT_461 = "+\"\\\"?>\");" + NL + "\t\tout_";
  protected final String TEXT_462 = ".write(\"";
  protected final String TEXT_463 = "\");";
  protected final String TEXT_464 = NL + NL + "\t\tneedRoot_";
  protected final String TEXT_465 = " = true;" + NL + "\t\tstrCompCache_";
  protected final String TEXT_466 = "=strTemp_";
  protected final String TEXT_467 = ";" + NL + "\t\tpreNewTabIndex_";
  protected final String TEXT_468 = " = -1;";
  protected final String TEXT_469 = NL + "\t\t}";
  protected final String TEXT_470 = "\t" + NL + "\t}\t" + NL + "\t" + NL + "\tStringBuffer buf_";
  protected final String TEXT_471 = " = new StringBuffer();" + NL + "\t//init value is 0 not -1, because it will output the root tab when all the row value is null." + NL + "\tint unNullMaxIndex_";
  protected final String TEXT_472 = " = 0;" + NL + "" + NL + "\t// build root xml tree " + NL + "\tif (needRoot_";
  protected final String TEXT_473 = ") {" + NL + "\t\tneedRoot_";
  protected final String TEXT_474 = "=false;";
  protected final String TEXT_475 = NL + "\t\tif( false";
  protected final String TEXT_476 = " || valueMap_";
  protected final String TEXT_477 = ".get(\"";
  protected final String TEXT_478 = "\") != null";
  protected final String TEXT_479 = NL + "\t\t){" + NL + "\t\t\tunNullMaxIndex_";
  protected final String TEXT_480 = " = ";
  protected final String TEXT_481 = ";" + NL + "\t\t}";
  protected final String TEXT_482 = NL + "\t\tendTabs_";
  protected final String TEXT_483 = "[";
  protected final String TEXT_484 = "] = buf_";
  protected final String TEXT_485 = ".toString();" + NL + "\t\tbuf_";
  protected final String TEXT_486 = " = new StringBuffer();";
  protected final String TEXT_487 = NL + "\t}" + NL + "\t" + NL + "\t// build group xml tree ";
  protected final String TEXT_488 = NL + "\tboolean isNewElememt = false;" + NL + "\t//The index of group element which is the first new group in groups." + NL + "\tint newTabIndex_";
  protected final String TEXT_489 = " = -1;" + NL + "\t//Buffer all group tab XML, then set to startTabBuffer." + NL + "    String[] groupBuffer_";
  protected final String TEXT_490 = " = new String[";
  protected final String TEXT_491 = "];" + NL + "    String[] groupEndBuffer_";
  protected final String TEXT_492 = " = new String[";
  protected final String TEXT_493 = "];";
  protected final String TEXT_494 = NL + NL + "\t// need a new group element ";
  protected final String TEXT_495 = " or not" + NL + "\tif(isNewElememt || groupbyList_";
  protected final String TEXT_496 = ".size()<=";
  protected final String TEXT_497 = " || groupbyList_";
  protected final String TEXT_498 = ".get(";
  protected final String TEXT_499 = ")==null";
  protected final String TEXT_500 = NL + "\t|| ( groupbyList_";
  protected final String TEXT_501 = ".get(";
  protected final String TEXT_502 = ").get(";
  protected final String TEXT_503 = ")!=null " + NL + "\t\t? !groupbyList_";
  protected final String TEXT_504 = ".get(";
  protected final String TEXT_505 = ").get(";
  protected final String TEXT_506 = ").equals(";
  protected final String TEXT_507 = ") " + NL + "\t\t: ";
  protected final String TEXT_508 = "!=null )";
  protected final String TEXT_509 = NL + "\t){" + NL + "\t\t// Is the first new element in groups." + NL + "\t\tif(isNewElememt == false && groupbyList_";
  protected final String TEXT_510 = ".size()>";
  protected final String TEXT_511 = "){" + NL + "\t\t\tnewTabIndex_";
  protected final String TEXT_512 = " = ";
  protected final String TEXT_513 = ";" + NL + "\t\t}" + NL + "" + NL + "\t\t// count the groupby element" + NL + "\t\tif(groupbyList_";
  protected final String TEXT_514 = ".size()<=";
  protected final String TEXT_515 = "){" + NL + "        \tgroupbyList_";
  protected final String TEXT_516 = ".add(new java.util.ArrayList<String>());" + NL + "        }else{" + NL + "        \tgroupbyList_";
  protected final String TEXT_517 = ".get(";
  protected final String TEXT_518 = ").clear();" + NL + "        }";
  protected final String TEXT_519 = NL + "\t\tgroupbyList_";
  protected final String TEXT_520 = ".get(";
  protected final String TEXT_521 = ").add(";
  protected final String TEXT_522 = ");";
  protected final String TEXT_523 = NL + "        isNewElememt=true;" + NL + "\t}" + NL + "\t" + NL + "\t// subtree XML string generate";
  protected final String TEXT_524 = NL + "\tif( false";
  protected final String TEXT_525 = " || valueMap_";
  protected final String TEXT_526 = ".get(\"";
  protected final String TEXT_527 = "\") != null";
  protected final String TEXT_528 = NL + "\t){" + NL + "\t\tunNullMaxIndex_";
  protected final String TEXT_529 = " = ";
  protected final String TEXT_530 = ";" + NL + "\t}";
  protected final String TEXT_531 = NL + "\t// buffer the end tabs to group buffer" + NL + "\tgroupEndBuffer_";
  protected final String TEXT_532 = "[";
  protected final String TEXT_533 = "] = buf_";
  protected final String TEXT_534 = ".toString();" + NL + "    buf_";
  protected final String TEXT_535 = " = new StringBuffer();";
  protected final String TEXT_536 = NL + "\t//output the previous groups as there's a new group" + NL + "    if (newTabIndex_";
  protected final String TEXT_537 = " >= 0 && preNewTabIndex_";
  protected final String TEXT_538 = "!=-1) {" + NL + "        //out_";
  protected final String TEXT_539 = ".newLine();//Track code";
  protected final String TEXT_540 = NL + "\t\t// output unNull tabs in start tabs buffer" + NL + "        if (preUnNullMaxIndex_";
  protected final String TEXT_541 = " >= 0) {" + NL + "            for (int i_";
  protected final String TEXT_542 = " = 0; i_";
  protected final String TEXT_543 = " < startTabs_";
  protected final String TEXT_544 = ".length; i_";
  protected final String TEXT_545 = "++) {" + NL + "                if (i_";
  protected final String TEXT_546 = " <= preUnNullMaxIndex_";
  protected final String TEXT_547 = ") {" + NL + "                    if (startTabs_";
  protected final String TEXT_548 = "[i_";
  protected final String TEXT_549 = "] != null) {" + NL + "                        out_";
  protected final String TEXT_550 = ".write(startTabs_";
  protected final String TEXT_551 = "[i_";
  protected final String TEXT_552 = "]);" + NL + "                    }" + NL + "                    startTabs_";
  protected final String TEXT_553 = "[i_";
  protected final String TEXT_554 = "] = null;" + NL + "                }" + NL + "            }" + NL + "        }";
  protected final String TEXT_555 = NL + "\t\t//output all start tabs buffer" + NL + "\t\tfor (int i_";
  protected final String TEXT_556 = " = 0; i_";
  protected final String TEXT_557 = " < startTabs_";
  protected final String TEXT_558 = ".length; i_";
  protected final String TEXT_559 = "++) {" + NL + "            if (startTabs_";
  protected final String TEXT_560 = "[i_";
  protected final String TEXT_561 = "] != null) {" + NL + "                out_";
  protected final String TEXT_562 = ".write(startTabs_";
  protected final String TEXT_563 = "[i_";
  protected final String TEXT_564 = "]);" + NL + "            }" + NL + "            startTabs_";
  protected final String TEXT_565 = "[i_";
  protected final String TEXT_566 = "] = null;" + NL + "        }";
  protected final String TEXT_567 = NL + "        // output endtabs" + NL + "        if (preUnNullMaxIndex_";
  protected final String TEXT_568 = " >= preNewTabIndex_";
  protected final String TEXT_569 = NL + "            && preUnNullMaxIndex_";
  protected final String TEXT_570 = " >= ";
  protected final String TEXT_571 = " + newTabIndex_";
  protected final String TEXT_572 = ") {" + NL + "            for (int i_";
  protected final String TEXT_573 = " = preUnNullMaxIndex_";
  protected final String TEXT_574 = "; i_";
  protected final String TEXT_575 = " >= ";
  protected final String TEXT_576 = " + newTabIndex_";
  protected final String TEXT_577 = "; i_";
  protected final String TEXT_578 = "--) {" + NL + "            \tif(endTabs_";
  protected final String TEXT_579 = "[i_";
  protected final String TEXT_580 = "]!=null){" + NL + "            \t\tout_";
  protected final String TEXT_581 = ".write(endTabs_";
  protected final String TEXT_582 = "[i_";
  protected final String TEXT_583 = "]);" + NL + "            \t}" + NL + "            \tendTabs_";
  protected final String TEXT_584 = "[i_";
  protected final String TEXT_585 = "] = null;" + NL + "                out_";
  protected final String TEXT_586 = ".write(\"";
  protected final String TEXT_587 = "\");" + NL + "                out_";
  protected final String TEXT_588 = ".write(endTabStrs_";
  protected final String TEXT_589 = NL + "                        .get(i_";
  protected final String TEXT_590 = "));" + NL + "            }" + NL + "        } else {";
  protected final String TEXT_591 = NL + "            for (int i_";
  protected final String TEXT_592 = " = preNewTabIndex_";
  protected final String TEXT_593 = " - 1; i_";
  protected final String TEXT_594 = " >= ";
  protected final String TEXT_595 = " + newTabIndex_";
  protected final String TEXT_596 = "; i_";
  protected final String TEXT_597 = "--) {" + NL + "            \tif(endTabs_";
  protected final String TEXT_598 = "[i_";
  protected final String TEXT_599 = "]!=null){" + NL + "            \t\tout_";
  protected final String TEXT_600 = ".write(endTabs_";
  protected final String TEXT_601 = "[i_";
  protected final String TEXT_602 = "]);" + NL + "            \t}" + NL + "            \tendTabs_";
  protected final String TEXT_603 = "[i_";
  protected final String TEXT_604 = "] = null;" + NL + "                out_";
  protected final String TEXT_605 = ".write(\"";
  protected final String TEXT_606 = "\");" + NL + "                out_";
  protected final String TEXT_607 = ".write(endTabStrs_";
  protected final String TEXT_608 = NL + "                        .get(i_";
  protected final String TEXT_609 = "));" + NL + "            }";
  protected final String TEXT_610 = NL + "        }";
  protected final String TEXT_611 = NL + "        preNewTabIndex_";
  protected final String TEXT_612 = " = newTabIndex_";
  protected final String TEXT_613 = " + ";
  protected final String TEXT_614 = ";" + NL + "    }" + NL + "" + NL + "    // set new element groupbuffer to startbuffer" + NL + "    for (int i_";
  protected final String TEXT_615 = " = 0; i_";
  protected final String TEXT_616 = " < groupBuffer_";
  protected final String TEXT_617 = ".length; i_";
  protected final String TEXT_618 = "++) {" + NL + "        // when newTabIndex is null, must use the perNewTabIndex" + NL + "        if (i_";
  protected final String TEXT_619 = " >= preNewTabIndex_";
  protected final String TEXT_620 = " - ";
  protected final String TEXT_621 = ") {" + NL + "            startTabs_";
  protected final String TEXT_622 = "[i_";
  protected final String TEXT_623 = " + ";
  protected final String TEXT_624 = "] = groupBuffer_";
  protected final String TEXT_625 = "[i_";
  protected final String TEXT_626 = "];" + NL + "            endTabs_";
  protected final String TEXT_627 = "[i_";
  protected final String TEXT_628 = " + ";
  protected final String TEXT_629 = "] = groupEndBuffer_";
  protected final String TEXT_630 = "[i_";
  protected final String TEXT_631 = "];" + NL + "        }" + NL + "    }";
  protected final String TEXT_632 = NL + "\t//reset the preUnNullMaxIndex" + NL + "\tif(unNullMaxIndex_";
  protected final String TEXT_633 = ">=0){" + NL + "    \tpreUnNullMaxIndex_";
  protected final String TEXT_634 = "=unNullMaxIndex_";
  protected final String TEXT_635 = ";" + NL + "\t}else{" + NL + "\t\tif(preUnNullMaxIndex_";
  protected final String TEXT_636 = ">";
  protected final String TEXT_637 = "){" + NL + "\t\t\tpreUnNullMaxIndex_";
  protected final String TEXT_638 = "=";
  protected final String TEXT_639 = ";" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_640 = NL + "\t// build loop xml tree";
  protected final String TEXT_641 = NL + "\t\tif( false";
  protected final String TEXT_642 = " || valueMap_";
  protected final String TEXT_643 = ".get(\"";
  protected final String TEXT_644 = "\") != null";
  protected final String TEXT_645 = " || true " + NL + "    \t";
  protected final String TEXT_646 = NL + "\t\t){";
  protected final String TEXT_647 = NL + "\t\t// output all buffer" + NL + "\t\tfor (int i_";
  protected final String TEXT_648 = " = 0; i_";
  protected final String TEXT_649 = " < startTabs_";
  protected final String TEXT_650 = ".length; i_";
  protected final String TEXT_651 = "++) {" + NL + "            if (startTabs_";
  protected final String TEXT_652 = "[i_";
  protected final String TEXT_653 = "] != null && startTabs_";
  protected final String TEXT_654 = "[i_";
  protected final String TEXT_655 = "].length() > 0) {" + NL + "                out_";
  protected final String TEXT_656 = ".write(startTabs_";
  protected final String TEXT_657 = "[i_";
  protected final String TEXT_658 = "]);" + NL + "                startTabs_";
  protected final String TEXT_659 = "[i_";
  protected final String TEXT_660 = "] = null;" + NL + "            }" + NL + "        }" + NL + "\t\tout_";
  protected final String TEXT_661 = ".write(buf_";
  protected final String TEXT_662 = ".toString());" + NL + "\t\tpreNewTabIndex_";
  protected final String TEXT_663 = " = ";
  protected final String TEXT_664 = ";";
  protected final String TEXT_665 = NL + "            preUnNullMaxIndex_";
  protected final String TEXT_666 = " = ";
  protected final String TEXT_667 = ";" + NL + "\t\t}";
  protected final String TEXT_668 = NL;

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
    
//==========common part 1 begin===================================================================
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

boolean isCompactFormat = ("true").equals(ElementParameterParser.getValue(node, "__COMPACT_FORMAT__"));
final String whiteSpace;
final String rowSeparator;
if(!isCompactFormat) { // pretty format
	whiteSpace = "  ";
	rowSeparator = "\\n";
} else { // compact format
	whiteSpace = "";
	rowSeparator = "";
}
//===========common part 1 end=============================================================

    
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
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_10);
    stringBuffer.append( thousandsSeparator);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(decimalSeparator );
    stringBuffer.append(TEXT_12);
    
    		} else {

    stringBuffer.append(TEXT_13);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_15);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(decimalSeparator );
    stringBuffer.append(TEXT_17);
    
	   		}
        } else if(JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable())){

    stringBuffer.append(TEXT_18);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_20);
    
        }else if(javaType == JavaTypesManager.DATE){
            if( column.getPattern() != null && column.getPattern().trim().length() != 0 ){

    stringBuffer.append(TEXT_21);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getPattern());
    stringBuffer.append(TEXT_24);
    
            }else{

    stringBuffer.append(TEXT_25);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel());
    
           }
        }else if (javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_27);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_29);
    
        }else{

    stringBuffer.append(TEXT_30);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_32);
    
		}
		if(column.isNullable()){
			
    stringBuffer.append(TEXT_33);
     
			if(isNotSetDefault == false){
				
    stringBuffer.append(column.getDefault());
    
			}else{
				
    stringBuffer.append(TEXT_34);
    
			}
		}

    stringBuffer.append(TEXT_35);
    
	}
}

// ------------------- *** Dom4j generation mode start *** ------------------- //
class GenerateToolByDom4j{
	String cid = null;
	boolean allowEmpty = false;
	boolean outputAsXSD = false;
	XMLTool tool = null;
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

    stringBuffer.append(TEXT_36);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_39);
    
			}
			int index = 0;
			for(XMLNode child:node.elements){
				if(0==(child.special & 1)){
					generateCode(child,currEleName+"_"+index++,currEleName);
				}
			}
			if(node.relatedColumn != null && (node.special & 2)==0 && (node.special & 1)==0){
				if(!outputAsXSD && !allowEmpty){

    stringBuffer.append(TEXT_40);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    
				}
			}
		}
	}
	private void createElement(String currEleName, XMLNode node, String parentName){
		int index = node.name.indexOf(":");
		if(5==(node.special & 5)){
			int currPos = node.getCurrGroupPos();
			if(index>0 && node.parent!=null){

    stringBuffer.append(TEXT_52);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(node.name.substring(0,index));
    stringBuffer.append(TEXT_57);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(node.name.substring(index+1));
    stringBuffer.append(TEXT_61);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_64);
    
			}else{

    stringBuffer.append(TEXT_65);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_68);
    
			}

    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(node.getNodeInsertIndex() );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(currPos +1 );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(currPos +1 );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(currPos );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    
		}else{
			if(index>0 && node.parent!=null){

    stringBuffer.append(TEXT_86);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(node.name.substring(0,index));
    stringBuffer.append(TEXT_91);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(node.name.substring(index+1));
    stringBuffer.append(TEXT_97);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_102);
    
			}else{

    stringBuffer.append(TEXT_103);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(parentName);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_108);
    
			}
		}
		if(0!=(node.special & 2)){

    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    
		}
	}
	private void setText(String currEleName, XMLNode node){
		if(node.relatedColumn!=null){
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(node.relatedColumn.getTalendType());
			if(javaType == JavaTypesManager.OBJECT){

    stringBuffer.append(TEXT_113);
    tool.getValue(node);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    tool.getValue(node);
    stringBuffer.append(TEXT_118);
    
				if(outputAsXSD){

    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_124);
    
				}
			}else{

    stringBuffer.append(TEXT_125);
    tool.getValue(node);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    tool.getValue(node);
    stringBuffer.append(TEXT_130);
    
				if(outputAsXSD){

    stringBuffer.append(TEXT_131);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    
				}
			}
		}else if(node.defaultValue != null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_140);
    
		}
	}
	private void addAttribute(String currEleName, XMLNode node){
		if(node.relatedColumn!=null){

    stringBuffer.append(TEXT_141);
    tool.getValue(node);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_145);
    tool.getValue(node);
    stringBuffer.append(TEXT_146);
    
		}else if(node.defaultValue != null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_147);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_151);
    
		}
	}
	private void addNameSpace(String currEleName, XMLNode node){
		if(node.relatedColumn!=null){

    stringBuffer.append(TEXT_152);
    tool.getValue(node);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_156);
    tool.getValue(node);
    stringBuffer.append(TEXT_157);
    
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_158);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(currEleName);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_162);
    tool.getValue(node);
    stringBuffer.append(TEXT_163);
    
			}

    stringBuffer.append(TEXT_164);
    
		}else if(node.defaultValue != null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_165);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(node.path );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_169);
    
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_170);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(currEleName );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_175);
    
			}
		}
	}
}
// ------------------- *** Dom4j generation mode end *** ------------------- //

// ------------------- *** Null generation mode start *** ------------------- //
class GenerateToolByNull{
	String cid = null;
	boolean allowEmpty = false;
	boolean outputAsXSD = false;
	String fileNameXSD = "";
	XMLTool tool = null;
	
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

    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(node.getCurrGroupPos());
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_180);
    
					}else{// root
    					int num = node.path.split("/").length-2;
    					if(!outputAsXSD && !allowEmpty){

    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_182);
    stringBuffer.append(num);
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_185);
    
						}else{

    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_189);
    
						}
					}
					mainChild = null;
				}
				if(!child.isMainNode()){ //make the main node output last
					if(!outputAsXSD && !allowEmpty && (child.relatedColumn != null || child.childrenColumnList.size()>0 || child.hasDefaultValue == true)){

    stringBuffer.append(TEXT_190);
    
                    	for(IMetadataColumn column : child.childrenColumnList){
                    		
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_192);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_193);
    
                    	}
                    	if(child.hasDefaultValue == true){
    stringBuffer.append(TEXT_194);
    }
    stringBuffer.append(TEXT_195);
    
						generateCode(child,emptySpace+whiteSpace);

    stringBuffer.append(TEXT_196);
    
            		}else{
            			generateCode(child,emptySpace+whiteSpace);
            		}
				}
			}

			if(!node.isMainNode()){ // is not main node
				endElement(node,emptySpace);
			}
		}
	}
	private void startElement(XMLNode node, String emptySpace){

    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(emptySpace);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_202);
    
		if(outputAsXSD && node.parent==null){

    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_205);
    stringBuffer.append(fileNameXSD);
    stringBuffer.append(TEXT_206);
    
		}
		for(XMLNode ns:node.namespaces){
			addNameSpace(ns);
		}
		for(XMLNode attri:node.attributes){
			addAttribute(attri);
		}
		if(outputAsXSD && node.relatedColumn != null){

    stringBuffer.append(TEXT_207);
    tool.getValue(node);
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_209);
    
		}

    stringBuffer.append(TEXT_210);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_211);
    
	}
	
	public void endElement(XMLNode node, String emptySpace){
		if(node.elements.size()>0){

    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_213);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_214);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_215);
    stringBuffer.append(emptySpace);
    stringBuffer.append(TEXT_216);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_217);
    
		}else{

    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_219);
    stringBuffer.append(node.name);
    stringBuffer.append(TEXT_220);
    
		}
	}
	private void setText(XMLNode node){
		if(node.relatedColumn!=null){
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(node.relatedColumn.getTalendType());
			if(javaType == JavaTypesManager.OBJECT){

    stringBuffer.append(TEXT_221);
    tool.getValue(node);
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_223);
    tool.getValue(node);
    stringBuffer.append(TEXT_224);
    
			}else{

    stringBuffer.append(TEXT_225);
    tool.getValue(node);
    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_227);
    tool.getValue(node);
    stringBuffer.append(TEXT_228);
    
			}
		}else if(node.defaultValue !=null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_231);
    
		}
	}
	private void addAttribute(XMLNode node){
		if(node.relatedColumn!=null){

    stringBuffer.append(TEXT_232);
    tool.getValue(node);
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_234);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_235);
    tool.getValue(node);
    stringBuffer.append(TEXT_236);
    
		}else if(node.defaultValue !=null && !("").equals(node.defaultValue) ){

    stringBuffer.append(TEXT_237);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_238);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_239);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_240);
    
		}
	}
	private void addNameSpace(XMLNode node){
		if(node.relatedColumn!=null){

    stringBuffer.append(TEXT_241);
    tool.getValue(node);
    stringBuffer.append(TEXT_242);
    
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_243);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_244);
    tool.getValue(node);
    stringBuffer.append(TEXT_245);
    
			}else{

    stringBuffer.append(TEXT_246);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_247);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_248);
    tool.getValue(node);
    stringBuffer.append(TEXT_249);
    
			}

    stringBuffer.append(TEXT_250);
    
		}else if(node.defaultValue !=null && !("").equals(node.defaultValue) ){
			if(node.path ==null || node.path.length()==0){

    stringBuffer.append(TEXT_251);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_252);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_253);
    
			}else{

    stringBuffer.append(TEXT_254);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_255);
    stringBuffer.append(node.path);
    stringBuffer.append(TEXT_256);
    stringBuffer.append(node.defaultValue );
    stringBuffer.append(TEXT_257);
    
			}
		}
	}
}
// ------------------- *** Null generation mode end *** ------------------- //

// ------------------- *** Common code start *** ------------------- //
IMetadataTable metadata = null;
IConnection inConn = null;
for (IConnection conn : node.getIncomingConnections()) {
	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.FLOW)) {
		inConn = conn;
		break;
	}
}
if (inConn != null) {
	metadata = inConn.getMetadataTable();
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
                
                IMetadataTable inputMetadataTable= conn.getMetadataTable();
                List<IMetadataColumn> inputColumns= inputMetadataTable.getListColumns();
                
                List<Map<String,String>> groupbys = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUPBYS__");
				
				String removeHeader = ElementParameterParser.getValue(node, "__REMOVE_HEADER__"); // add for feature7788
                String allowEmpty = ElementParameterParser.getValue(node, "__CREATE_EMPTY_ELEMENT__");
                String outputAsXSD = ElementParameterParser.getValue(node, "__OUTPUT_AS_XSD__");
                String fileNameXSD = ElementParameterParser.getValue(node, "__XSD_FILE__");
                String encoding = ElementParameterParser.getValue(node, "__ENCODING__");                
                
	            String rowNumber = ElementParameterParser.getValue(node, "__ROW_NUMBER__");
                
                String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
        		boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
        		String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
        		String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER); 
        		
        		String mode = ElementParameterParser.getValue(node, "__GENERATION_MODE__");
        		
        		boolean storeFlow = ("true").equals(ElementParameterParser.getValue(node, "__STORE_FLOW__"));
        		                
                java.util.Map<String,IMetadataColumn> inputKeysColumns = new java.util.HashMap<String,IMetadataColumn>();
                if(inputColumns!=null){
                	for(IMetadataColumn column :inputColumns){
                		for(int i=0;i<groupbys.size();i++){
                			String columnName=groupbys.get(i).get("INPUT_COLUMN");
                			if(column.getLabel().equals(columnName)){
                				inputKeysColumns.put(columnName,column);
                				break;
                			}
                		}
                	}
                }
        		
        		
        		// init tool
                XMLTool tool = new XMLTool();
                tool.connName = conn.getName();
                tool.advancedSeparator=advancedSeparator;
                tool.thousandsSeparator=thousandsSeparator;
                tool.decimalSeparator=decimalSeparator;
                tool.cid=cid;
                
                // change tables to a tree 
				Object[] treeObjs = getTree(rootTable, groupTable, loopTable, metadata.getListColumns());
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

    stringBuffer.append(TEXT_258);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_260);
    
				for(IMetadataColumn column :inputColumns){

    stringBuffer.append(TEXT_261);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_263);
    tool.getValue(column);
    stringBuffer.append(TEXT_264);
    
				}

    if(storeFlow){
    stringBuffer.append(TEXT_265);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_268);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_269);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_270);
    }
    stringBuffer.append(TEXT_271);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_272);
    
	if(inputKeysColumns.size() !=0){
		for (IMetadataColumn column : inputColumns) {
			if(inputKeysColumns.containsKey(column.getLabel())) {

    stringBuffer.append(TEXT_273);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_275);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_276);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_277);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_278);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_279);
    			}
		}
	}

    stringBuffer.append(TEXT_280);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_281);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_283);
    
// ------------------- *** Common code end *** ------------------- //

// ------------------- *** Dom4j generation mode start *** ------------------- //
if(("Dom4j").equals(mode)){
		if(inputKeysColumns.size() !=0){

    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_286);
    		}
		if(("true").equals(outputAsXSD)){

    stringBuffer.append(TEXT_287);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_288);
    stringBuffer.append(fileNameXSD);
    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_290);
    
		}

    stringBuffer.append(TEXT_291);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_292);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_293);
    
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_294);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_295);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_296);
    
		}

    stringBuffer.append(TEXT_297);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_298);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_299);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_300);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_301);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_302);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_303);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_304);
    
		if(("true").equals(removeHeader)){

    stringBuffer.append(TEXT_305);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_308);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_309);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_310);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_311);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_312);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_313);
    
		}else{

    stringBuffer.append(TEXT_314);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_315);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_316);
    
		}

    stringBuffer.append(TEXT_317);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_318);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_319);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_320);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_321);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_322);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_323);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_324);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_325);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_326);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_327);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_328);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_329);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_330);
    
		if(inputKeysColumns.size() !=0){

    stringBuffer.append(TEXT_331);
    
		}

    stringBuffer.append(TEXT_332);
    
	//init the generate tool.
	GenerateToolByDom4j generateToolByDom4j = new GenerateToolByDom4j();
    if(("true").equals(outputAsXSD)){
    	generateToolByDom4j.outputAsXSD = true;
    }
    if(("true").equals(allowEmpty)){
    	generateToolByDom4j.allowEmpty = true;
    }
    generateToolByDom4j.cid = cid;
    generateToolByDom4j.tool = tool;
    
    //start generate code

    stringBuffer.append(TEXT_333);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_334);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_335);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_336);
    
	generateToolByDom4j.generateCode(root,"root","doc");

    stringBuffer.append(TEXT_337);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_338);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_339);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_340);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_341);
    
	if(groupTable.size()>0){

    stringBuffer.append(TEXT_342);
    
	}
	for(int i=0;i<groupList.size();i++){
		XMLNode groupRootNode = groupList.get(i);

    stringBuffer.append(TEXT_343);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_344);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_345);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_346);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_347);
    
		for(int j=0;j<groupbyNodeList.get(i).size();j++){
			XMLNode attr = groupbyNodeList.get(i).get(j);
			if(attr.relatedColumn!=null){

    stringBuffer.append(TEXT_348);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_349);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_350);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_351);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_352);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_353);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_354);
    tool.getValue(attr);
    stringBuffer.append(TEXT_355);
    tool.getValue(attr);
    stringBuffer.append(TEXT_356);
    
			}
		}

    stringBuffer.append(TEXT_357);
    
		generateToolByDom4j.generateCode(groupList.get(i),"group"+i+"_","subTreeRootParent");

    stringBuffer.append(TEXT_358);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_359);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_360);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_361);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_362);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_363);
    
		for(int j=0;j<groupbyNodeList.get(i).size();j++){
			XMLNode attr = groupbyNodeList.get(i).get(j);

    stringBuffer.append(TEXT_364);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_365);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_366);
    tool.getValue(attr);
    stringBuffer.append(TEXT_367);
    
		}

    stringBuffer.append(TEXT_368);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_369);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_371);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_372);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_373);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_374);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_375);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_376);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_377);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_378);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_379);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_380);
    
	}

    stringBuffer.append(TEXT_381);
    
	generateToolByDom4j.generateCode(loop,"loop","subTreeRootParent");
}
// ------------------- *** Dom4j generation mode end *** ------------------- //

// ------------------- *** Null generation mode start *** ------------------- //
else if(("Null").equals(mode)){
//	String fileNameXSD = ElementParameterParser.getValue(node, "__XSD_FILE__");
	//init the generate tool.
	GenerateToolByNull generateToolByNull = new GenerateToolByNull();
    if(("true").equals(outputAsXSD)){
    	generateToolByNull.outputAsXSD = true;
    	generateToolByNull.fileNameXSD = fileNameXSD;
    }
    if(("true").equals(allowEmpty)){
    	generateToolByNull.allowEmpty = true;
    }
    generateToolByNull.cid = cid;
    generateToolByNull.tool = tool;

	if(inputKeysColumns.size() !=0){
	
    stringBuffer.append(TEXT_382);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_383);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_384);
    	}
    
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_385);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_386);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_387);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_388);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_389);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_396);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_397);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_398);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_399);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_400);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_401);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_402);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_403);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_404);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_405);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_406);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_407);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_408);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_409);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_410);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_411);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_412);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_413);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_414);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_415);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_416);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_417);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_418);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_419);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_420);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_421);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_422);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_423);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_424);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_425);
    
		}else{
			if(loopTable.size()>0){

    stringBuffer.append(TEXT_426);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_427);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_428);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_429);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_430);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_431);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_432);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_433);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_434);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_435);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_436);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_437);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_438);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_439);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_440);
    
			}
		}

    stringBuffer.append(TEXT_441);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_442);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_443);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_444);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_445);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_446);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_447);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_448);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_449);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_450);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_451);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_452);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_453);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_454);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_455);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_456);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_457);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_458);
    
	if(!("true").equals(removeHeader)){

    stringBuffer.append(TEXT_459);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_460);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_461);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_462);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_463);
    
	}

    stringBuffer.append(TEXT_464);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_465);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_466);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_467);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_468);
    	if(inputKeysColumns.size() !=0){
    stringBuffer.append(TEXT_469);
    	}
    stringBuffer.append(TEXT_470);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_471);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_472);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_473);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_474);
    
	String rootEmptySpace = "";
	for(int i=0;i<mainList.size();i++){
		generateToolByNull.generateCode(mainList.get(i),rootEmptySpace);
		rootEmptySpace+=whiteSpace;
		
		if(!generateToolByNull.outputAsXSD && !generateToolByNull.allowEmpty){
			if(mainList.get(i).relatedColumn != null || mainList.get(i).childrenColumnList.size()>0){

    stringBuffer.append(TEXT_475);
    
                	for(IMetadataColumn column : mainList.get(i).childrenColumnList){
                		
    stringBuffer.append(TEXT_476);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_477);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_478);
    
                	}

    stringBuffer.append(TEXT_479);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_480);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_481);
    
			}
		}

    stringBuffer.append(TEXT_482);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_483);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_484);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_485);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_486);
    

	}

    stringBuffer.append(TEXT_487);
    
	if(groupTable.size()>0){

    stringBuffer.append(TEXT_488);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_489);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_490);
    stringBuffer.append(groupList.size());
    stringBuffer.append(TEXT_491);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_492);
    stringBuffer.append(groupList.size());
    stringBuffer.append(TEXT_493);
    
	}
	for(int i=0;i<groupList.size();i++){
		XMLNode groupRootNode = groupList.get(i);

    stringBuffer.append(TEXT_494);
    stringBuffer.append(groupRootNode.name);
    stringBuffer.append(TEXT_495);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_496);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_497);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_498);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_499);
    
		for(int j=0;j<groupbyNodeList.get(i).size();j++){
			XMLNode attr = groupbyNodeList.get(i).get(j);
			if(attr.relatedColumn!=null){

    stringBuffer.append(TEXT_500);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_501);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_502);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_503);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_504);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_505);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_506);
    tool.getValue(attr);
    stringBuffer.append(TEXT_507);
    tool.getValue(attr);
    stringBuffer.append(TEXT_508);
    
			}
		}

    stringBuffer.append(TEXT_509);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_510);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_511);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_512);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_513);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_514);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_515);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_516);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_517);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_518);
    
		for(int j=0;j<groupbyNodeList.get(i).size();j++){
			XMLNode attr = groupbyNodeList.get(i).get(j);

    stringBuffer.append(TEXT_519);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_520);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_521);
    tool.getValue(attr);
    stringBuffer.append(TEXT_522);
    
		}

    stringBuffer.append(TEXT_523);
    
		String emptySpace = "";
		for(int len = groupList.get(i).path.split("/").length-1;len>1;len--){
			emptySpace +=whiteSpace;
		}
		generateToolByNull.generateCode(groupList.get(i),emptySpace);
		
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){
			if((groupList.get(i).relatedColumn != null || groupList.get(i).childrenColumnList.size()>0)){

    stringBuffer.append(TEXT_524);
    
            	for(IMetadataColumn column : groupList.get(i).childrenColumnList){
            		
    stringBuffer.append(TEXT_525);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_526);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_527);
    
            	}

    stringBuffer.append(TEXT_528);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_529);
    stringBuffer.append(i+mainList.size());
    stringBuffer.append(TEXT_530);
    
			}
		}

    stringBuffer.append(TEXT_531);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_532);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_533);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_534);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_535);
    
	}//End of groupList loop
	
	if(groupTable.size()>0){

    stringBuffer.append(TEXT_536);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_537);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_538);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_539);
    
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_540);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_541);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_542);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_543);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_544);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_545);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_546);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_547);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_548);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_549);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_550);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_551);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_552);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_553);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_554);
    
		}else{

    stringBuffer.append(TEXT_555);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_556);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_557);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_558);
    stringBuffer.append(cid);
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
    
		}
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_567);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_568);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_569);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_570);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_571);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_572);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_573);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_574);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_575);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_576);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_577);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_578);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_579);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_580);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_581);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_582);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_583);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_584);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_585);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_586);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_587);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_588);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_589);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_590);
    
		}

    stringBuffer.append(TEXT_591);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_592);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_593);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_594);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_595);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_596);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_597);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_598);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_599);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_600);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_601);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_602);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_603);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_604);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_605);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_606);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_607);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_608);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_609);
    
		if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_610);
    
		}

    stringBuffer.append(TEXT_611);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_612);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_613);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_614);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_615);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_616);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_617);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_618);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_619);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_620);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_621);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_622);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_623);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_624);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_625);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_626);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_627);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_628);
    stringBuffer.append(mainList.size());
    stringBuffer.append(TEXT_629);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_630);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_631);
    
	}
	if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_632);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_633);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_634);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_635);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_636);
    stringBuffer.append(mainList.size()-1);
    stringBuffer.append(TEXT_637);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_638);
    stringBuffer.append(mainList.size()-1);
    stringBuffer.append(TEXT_639);
    
	}

    stringBuffer.append(TEXT_640);
    
	String emptySpace = "";
	for(int len =loop.path.split("/").length-1;len>1;len--){
		emptySpace +=whiteSpace;
	}
	if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_641);
    
    	for(IMetadataColumn column : loop.childrenColumnList){
    		
    stringBuffer.append(TEXT_642);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_643);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_644);
    
    	}
    	if(loop.hasDefaultValue == true){
    stringBuffer.append(TEXT_645);
    }
    stringBuffer.append(TEXT_646);
    
	}
	generateToolByNull.generateCode(loop,emptySpace);
	generateToolByNull.endElement(loop,emptySpace);

    stringBuffer.append(TEXT_647);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_648);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_649);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_650);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_651);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_652);
    stringBuffer.append(cid);
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
    stringBuffer.append(groupList.size()+mainList.size());
    stringBuffer.append(TEXT_664);
    
	if(!("true").equals(outputAsXSD) && !("true").equals(allowEmpty)){

    stringBuffer.append(TEXT_665);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_666);
    stringBuffer.append(groupList.size()+mainList.size()-1);
    stringBuffer.append(TEXT_667);
    
	}
}
// ------------------- *** Null generation mode end *** ------------------- //

// ------------------- *** Common code start *** ------------------- //
			}
		}
	}
}
// ------------------- *** Common code end *** ------------------- //

    stringBuffer.append(TEXT_668);
    return stringBuffer.toString();
  }
}
