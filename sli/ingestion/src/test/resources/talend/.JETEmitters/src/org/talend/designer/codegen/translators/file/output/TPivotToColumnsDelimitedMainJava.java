package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TPivotToColumnsDelimitedMainJava
{
  protected static String nl;
  public static synchronized TPivotToColumnsDelimitedMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPivotToColumnsDelimitedMainJava result = new TPivotToColumnsDelimitedMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "                   " + NL + "    //pivot key  " + NL + "    pivot_Keys_Split";
  protected final String TEXT_3 = " = routines.system.StringUtils.splitNotRegex(pivot_Keys";
  protected final String TEXT_4 = ",";
  protected final String TEXT_5 = ");" + NL + "    " + NL + "    pivot_Exists";
  protected final String TEXT_6 = " = false;" + NL + "    " + NL + "    int pivotNum";
  protected final String TEXT_7 = " = 0;" + NL + "    " + NL + "    for(int i = 0;i<pivot_Keys_Split";
  protected final String TEXT_8 = ".length;i++){" + NL;
  protected final String TEXT_9 = NL + "\t\t\t\tif(ParserUtils.parseTo_";
  protected final String TEXT_10 = "(pivot_Keys_Split";
  protected final String TEXT_11 = "[i]).equals(";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = ")){";
  protected final String TEXT_14 = NL + "\t\t\t\tif(ParserUtils.parseTo_";
  protected final String TEXT_15 = "(pivot_Keys_Split";
  protected final String TEXT_16 = "[i]) == ";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = "){";
  protected final String TEXT_19 = " " + NL + " \t \t\t    if(pivot_Keys_Split";
  protected final String TEXT_20 = "[i].equals(FormatterUtils.format_Date(";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = ",";
  protected final String TEXT_23 = "))){ ";
  protected final String TEXT_24 = NL + "\t \t\tif(pivot_Keys_Split";
  protected final String TEXT_25 = "[i].equals(";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = ")){";
  protected final String TEXT_28 = NL + "\t            pivot_Exists";
  protected final String TEXT_29 = " = true;" + NL + "\t            " + NL + "\t            pivotNum";
  protected final String TEXT_30 = " = i + 1;" + NL + "\t            " + NL + "\t            break;" + NL + "            " + NL + "        \t }" + NL + "    }" + NL + "    " + NL + "    if(!pivot_Exists";
  protected final String TEXT_31 = "){" + NL + "        " + NL + "        pivot_Key";
  protected final String TEXT_32 = ".delete(0, pivot_Key";
  protected final String TEXT_33 = ".length());";
  protected final String TEXT_34 = " " + NL + " \t  \t    pivot_Keys";
  protected final String TEXT_35 = " = pivot_Keys";
  protected final String TEXT_36 = " + pivot_Key";
  protected final String TEXT_37 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_38 = ".";
  protected final String TEXT_39 = ",";
  protected final String TEXT_40 = ")) + ";
  protected final String TEXT_41 = "; ";
  protected final String TEXT_42 = " " + NL + "            pivot_Keys";
  protected final String TEXT_43 = " = pivot_Keys";
  protected final String TEXT_44 = " + pivot_Key";
  protected final String TEXT_45 = ".append(";
  protected final String TEXT_46 = ".";
  protected final String TEXT_47 = ").toString() + ";
  protected final String TEXT_48 = "; ";
  protected final String TEXT_49 = " " + NL + "" + NL + "        pivot_Keys_Split";
  protected final String TEXT_50 = " = routines.system.StringUtils.splitNotRegex(pivot_Keys";
  protected final String TEXT_51 = ",";
  protected final String TEXT_52 = ");" + NL + "        " + NL + "        pivotNum";
  protected final String TEXT_53 = " = pivot_Keys_Split";
  protected final String TEXT_54 = ".length;" + NL + "        " + NL + "    }" + NL + "" + NL + "    " + NL + "    //group key" + NL + "" + NL + "    group_Keys_Split";
  protected final String TEXT_55 = " = routines.system.StringUtils.splitNotRegex(group_Keys";
  protected final String TEXT_56 = ",";
  protected final String TEXT_57 = ");" + NL + "    " + NL + "    group_Exists";
  protected final String TEXT_58 = " = false;" + NL + "    " + NL + "    gKvalue";
  protected final String TEXT_59 = " = \"\";" + NL + "    " + NL + "    group_Key";
  protected final String TEXT_60 = ".delete(0,group_Key";
  protected final String TEXT_61 = ".length());" + NL + "        ";
  protected final String TEXT_62 = NL + "\t\t\t" + NL + "\t\t\t\tgKvalue";
  protected final String TEXT_63 = " = gKvalue";
  protected final String TEXT_64 = " + group_Key";
  protected final String TEXT_65 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_66 = ".";
  protected final String TEXT_67 = ", ";
  protected final String TEXT_68 = "))";
  protected final String TEXT_69 = "+";
  protected final String TEXT_70 = ";" + NL + "\t\t\t" + NL + "\t\t";
  protected final String TEXT_71 = NL + "\t\t\t\t\t" + NL + "        \t\tgKvalue";
  protected final String TEXT_72 = " = gKvalue";
  protected final String TEXT_73 = " + group_Key";
  protected final String TEXT_74 = ".append(";
  protected final String TEXT_75 = ".";
  protected final String TEXT_76 = ").toString()";
  protected final String TEXT_77 = "+";
  protected final String TEXT_78 = ";" + NL + "        ";
  protected final String TEXT_79 = NL + "        " + NL + "        group_Key";
  protected final String TEXT_80 = ".delete(0,group_Key";
  protected final String TEXT_81 = ".length());";
  protected final String TEXT_82 = NL + "    " + NL + "    for(int i = 0;i<group_Keys_Split";
  protected final String TEXT_83 = ".length;i++){" + NL + "    " + NL + "        if(group_Keys_Split";
  protected final String TEXT_84 = "[i].equals(gKvalue";
  protected final String TEXT_85 = ")){" + NL + "            " + NL + "            group_Exists";
  protected final String TEXT_86 = " = true;" + NL + "            " + NL + "            break;" + NL + "            " + NL + "        }" + NL + "        " + NL + "    }" + NL + "" + NL + "    if(!group_Exists";
  protected final String TEXT_87 = "){" + NL + "        " + NL + "        group_Key";
  protected final String TEXT_88 = ".delete(0, group_Key";
  protected final String TEXT_89 = ".length());" + NL + "        " + NL + "        group_Keys";
  protected final String TEXT_90 = " = group_Keys";
  protected final String TEXT_91 = " + gKvalue";
  protected final String TEXT_92 = " + ";
  protected final String TEXT_93 = ";" + NL + "        " + NL + "        group_Keys_Split";
  protected final String TEXT_94 = " = routines.system.StringUtils.splitNotRegex(group_Keys";
  protected final String TEXT_95 = ",";
  protected final String TEXT_96 = ");" + NL + "    " + NL + "        aggValues";
  protected final String TEXT_97 = ".delete(0, aggValues";
  protected final String TEXT_98 = ".length());" + NL + "        " + NL + "        for(int i = 0; i<pivotNum";
  protected final String TEXT_99 = "-1; i++)" + NL + "            " + NL + "            aggValues";
  protected final String TEXT_100 = ".append(";
  protected final String TEXT_101 = ");" + NL + "        ";
  protected final String TEXT_102 = NL + NL + "        aggregation";
  protected final String TEXT_103 = ".put(gKvalue";
  protected final String TEXT_104 = ",aggValues";
  protected final String TEXT_105 = ".append(1).append(";
  protected final String TEXT_106 = ").toString());" + NL;
  protected final String TEXT_107 = NL + "        " + NL + "        aggregation";
  protected final String TEXT_108 = ".put(gKvalue";
  protected final String TEXT_109 = ",aggValues";
  protected final String TEXT_110 = ".append(FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_111 = ".";
  protected final String TEXT_112 = "),";
  protected final String TEXT_113 = ",";
  protected final String TEXT_114 = ")).append(";
  protected final String TEXT_115 = ").toString());" + NL + "  ";
  protected final String TEXT_116 = NL + "\t\t\t" + NL + "\t\t\t\taggregation";
  protected final String TEXT_117 = ".put(gKvalue";
  protected final String TEXT_118 = ",aggValues";
  protected final String TEXT_119 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = ", ";
  protected final String TEXT_122 = ")).append(";
  protected final String TEXT_123 = ").toString());" + NL + "\t\t\t  " + NL + "       \t \t";
  protected final String TEXT_124 = NL + "        " + NL + "        \t\taggregation";
  protected final String TEXT_125 = ".put(gKvalue";
  protected final String TEXT_126 = ",aggValues";
  protected final String TEXT_127 = ".append(";
  protected final String TEXT_128 = ".";
  protected final String TEXT_129 = ").append(";
  protected final String TEXT_130 = ").toString());" + NL + "        \t";
  protected final String TEXT_131 = NL + "    }else{" + NL + "" + NL + "        String aggStr";
  protected final String TEXT_132 = " = (String)aggregation";
  protected final String TEXT_133 = ".get(gKvalue";
  protected final String TEXT_134 = ");" + NL + "        " + NL + "        String[] aggStrSpli";
  protected final String TEXT_135 = " = routines.system.StringUtils.splitNotRegex(aggStr";
  protected final String TEXT_136 = ",";
  protected final String TEXT_137 = ");" + NL + "        " + NL + "        if(aggStrSpli";
  protected final String TEXT_138 = ".length < pivotNum";
  protected final String TEXT_139 = "){" + NL + "            " + NL + "" + NL + "    \t\tfor(int i = 0 ; i<pivotNum";
  protected final String TEXT_140 = " - aggStrSpli";
  protected final String TEXT_141 = ".length-1; i++){" + NL + "                    " + NL + "                aggStr";
  protected final String TEXT_142 = " = aggStr";
  protected final String TEXT_143 = " + ";
  protected final String TEXT_144 = ";" + NL + "            " + NL + "            }    " + NL + "               " + NL + "            aggValues";
  protected final String TEXT_145 = ".delete(0,aggValues";
  protected final String TEXT_146 = ".length());" + NL + "            ";
  protected final String TEXT_147 = NL + NL + "            aggStr";
  protected final String TEXT_148 = " = aggStr";
  protected final String TEXT_149 = " + aggValues";
  protected final String TEXT_150 = ".append(1).toString() + ";
  protected final String TEXT_151 = ";" + NL;
  protected final String TEXT_152 = NL + "            " + NL + "            aggStr";
  protected final String TEXT_153 = " = aggStr";
  protected final String TEXT_154 = " + aggValues";
  protected final String TEXT_155 = ".append(FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_156 = ".";
  protected final String TEXT_157 = "),";
  protected final String TEXT_158 = ",";
  protected final String TEXT_159 = ")).toString() + ";
  protected final String TEXT_160 = ";" + NL;
  protected final String TEXT_161 = NL + "\t\t\t\t" + NL + "\t\t\t\taggStr";
  protected final String TEXT_162 = " = aggStr";
  protected final String TEXT_163 = " + aggValues";
  protected final String TEXT_164 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_165 = ".";
  protected final String TEXT_166 = ", ";
  protected final String TEXT_167 = ")) + ";
  protected final String TEXT_168 = ";" + NL + "\t\t\t\t  " + NL + "        \t";
  protected final String TEXT_169 = NL + "            " + NL + "            \taggStr";
  protected final String TEXT_170 = " = aggStr";
  protected final String TEXT_171 = " + aggValues";
  protected final String TEXT_172 = ".append(";
  protected final String TEXT_173 = ".";
  protected final String TEXT_174 = ").toString() + ";
  protected final String TEXT_175 = ";" + NL + "            ";
  protected final String TEXT_176 = NL + "  " + NL + "            aggregation";
  protected final String TEXT_177 = ".remove(gKvalue";
  protected final String TEXT_178 = ");" + NL + "            " + NL + "            aggregation";
  protected final String TEXT_179 = ".put(gKvalue";
  protected final String TEXT_180 = ",aggStr";
  protected final String TEXT_181 = ");" + NL + "" + NL + "        }else{" + NL + "            " + NL + "            //step1: get the index of the pivot" + NL + "            int pivotIndex";
  protected final String TEXT_182 = " = 0 ;" + NL + "            " + NL + "            for(int i = 0; i<pivot_Keys_Split";
  protected final String TEXT_183 = ".length; i++){" + NL;
  protected final String TEXT_184 = NL + "\t\t\t\tif(ParserUtils.parseTo_";
  protected final String TEXT_185 = "(pivot_Keys_Split";
  protected final String TEXT_186 = "[i]).equals(";
  protected final String TEXT_187 = ".";
  protected final String TEXT_188 = ")){";
  protected final String TEXT_189 = NL + "\t\t\t\tif(ParserUtils.parseTo_";
  protected final String TEXT_190 = "(pivot_Keys_Split";
  protected final String TEXT_191 = "[i]) == ";
  protected final String TEXT_192 = ".";
  protected final String TEXT_193 = "){";
  protected final String TEXT_194 = " " + NL + " \t \t\t\tif(pivot_Keys_Split";
  protected final String TEXT_195 = "[i].equals(FormatterUtils.format_Date(";
  protected final String TEXT_196 = ".";
  protected final String TEXT_197 = ",";
  protected final String TEXT_198 = "))){ ";
  protected final String TEXT_199 = NL + "\t \t\tif(pivot_Keys_Split";
  protected final String TEXT_200 = "[i].equals(";
  protected final String TEXT_201 = ".";
  protected final String TEXT_202 = ")){";
  protected final String TEXT_203 = NL + "                    pivotIndex";
  protected final String TEXT_204 = " = i;" + NL + "                    " + NL + "                    break;" + NL + "                    " + NL + "                }" + NL + "                " + NL + "            }" + NL + "            " + NL + "            //step2: process the splitted data according to function" + NL + "            " + NL + "            String[] aggStrSplit";
  protected final String TEXT_205 = " = routines.system.StringUtils.splitNotRegex(aggStr";
  protected final String TEXT_206 = ",";
  protected final String TEXT_207 = ");" + NL + "            " + NL + "            aggValues";
  protected final String TEXT_208 = ".delete(0,aggValues";
  protected final String TEXT_209 = ".length());" + NL + "        ";
  protected final String TEXT_210 = NL;
  protected final String TEXT_211 = NL + "                ";
  protected final String TEXT_212 = " sum";
  protected final String TEXT_213 = " ;" + NL + "" + NL + "                if ((\"\").equals(aggStrSplit";
  protected final String TEXT_214 = "[pivotIndex";
  protected final String TEXT_215 = "])){" + NL + "                " + NL + "                \t";
  protected final String TEXT_216 = NL + "                \t\tsum";
  protected final String TEXT_217 = " = new java.math.BigDecimal(";
  protected final String TEXT_218 = ".";
  protected final String TEXT_219 = ".toPlainString());" + NL + "                \t";
  protected final String TEXT_220 = NL + "                \t\taggStrSplit";
  protected final String TEXT_221 = "[pivotIndex";
  protected final String TEXT_222 = "] = FormatterUtils.format_Number(sum";
  protected final String TEXT_223 = ".toPlainString(),";
  protected final String TEXT_224 = ",";
  protected final String TEXT_225 = ");" + NL + "                \t";
  protected final String TEXT_226 = NL + "                \t\taggStrSplit";
  protected final String TEXT_227 = "[pivotIndex";
  protected final String TEXT_228 = "] = sum";
  protected final String TEXT_229 = ".toPlainString();" + NL + "                \t\t";
  protected final String TEXT_230 = NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t\t  sum";
  protected final String TEXT_231 = " = ";
  protected final String TEXT_232 = ".parse";
  protected final String TEXT_233 = "(";
  protected final String TEXT_234 = ".";
  protected final String TEXT_235 = " + \"\");" + NL + "\t                    " + NL + "\t                      aggStrSplit";
  protected final String TEXT_236 = "[pivotIndex";
  protected final String TEXT_237 = "] = FormatterUtils.format_Number(String.valueOf(sum";
  protected final String TEXT_238 = "),";
  protected final String TEXT_239 = ",";
  protected final String TEXT_240 = ");" + NL + "                      " + NL + "\t\t\t\t\t\t";
  protected final String TEXT_241 = NL + "\t\t\t\t\t  " + NL + "\t\t\t\t\t\t  sum";
  protected final String TEXT_242 = " = ";
  protected final String TEXT_243 = ".parse";
  protected final String TEXT_244 = "(";
  protected final String TEXT_245 = ".";
  protected final String TEXT_246 = " + \"\");" + NL + "\t                    " + NL + "\t                      aggStrSplit";
  protected final String TEXT_247 = "[pivotIndex";
  protected final String TEXT_248 = "] = String.valueOf(sum";
  protected final String TEXT_249 = ");" + NL + "                      " + NL + "\t\t\t\t\t\t";
  protected final String TEXT_250 = NL + NL + "\t\t\t\t\t  sum";
  protected final String TEXT_251 = " = ";
  protected final String TEXT_252 = ".parse";
  protected final String TEXT_253 = "(";
  protected final String TEXT_254 = ".";
  protected final String TEXT_255 = " + \"\");" + NL + "                    " + NL + "                      aggStrSplit";
  protected final String TEXT_256 = "[pivotIndex";
  protected final String TEXT_257 = "] = String.valueOf(sum";
  protected final String TEXT_258 = ");                      " + NL + "                      " + NL + "\t\t\t\t\t";
  protected final String TEXT_259 = NL + "                    " + NL + "                }else{" + NL + "               \t\t";
  protected final String TEXT_260 = NL + "               \t\t\t" + NL + "\t\t\t\t\t\t\tjava.math.BigDecimal sourceBD_";
  protected final String TEXT_261 = " = new java.math.BigDecimal(FormatterUtils.unformat_Number(aggStrSplit";
  protected final String TEXT_262 = "[pivotIndex";
  protected final String TEXT_263 = "],";
  protected final String TEXT_264 = ",";
  protected final String TEXT_265 = "));" + NL + "\t\t\t\t\t\t\tjava.math.BigDecimal targetBD_";
  protected final String TEXT_266 = " = new java.math.BigDecimal(aggValues";
  protected final String TEXT_267 = ".append(";
  protected final String TEXT_268 = ".";
  protected final String TEXT_269 = ").toString());" + NL + "\t\t\t\t\t  \t\taggStrSplit";
  protected final String TEXT_270 = "[pivotIndex";
  protected final String TEXT_271 = "] = FormatterUtils.format_Number(sourceBD_";
  protected final String TEXT_272 = ".add(targetBD_";
  protected final String TEXT_273 = ").toString(),";
  protected final String TEXT_274 = ",";
  protected final String TEXT_275 = ");" + NL + "\t\t\t\t\t  " + NL + "\t\t\t\t\t\t";
  protected final String TEXT_276 = NL + NL + "\t\t\t\t\t  \t\taggStrSplit";
  protected final String TEXT_277 = "[pivotIndex";
  protected final String TEXT_278 = "] = (new java.math.BigDecimal(aggStrSplit";
  protected final String TEXT_279 = "[pivotIndex";
  protected final String TEXT_280 = "])).add(new java.math.BigDecimal(aggValues";
  protected final String TEXT_281 = ".append(";
  protected final String TEXT_282 = ".";
  protected final String TEXT_283 = ").toString())).toString();" + NL + "\t\t\t\t\t  " + NL + "\t\t\t\t\t\t";
  protected final String TEXT_284 = NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t  sum";
  protected final String TEXT_285 = " = (";
  protected final String TEXT_286 = ")(";
  protected final String TEXT_287 = ".parse";
  protected final String TEXT_288 = "(aggStrSplit";
  protected final String TEXT_289 = "[pivotIndex";
  protected final String TEXT_290 = "]) + ";
  protected final String TEXT_291 = ".parse";
  protected final String TEXT_292 = "(aggValues";
  protected final String TEXT_293 = ".append(";
  protected final String TEXT_294 = ".";
  protected final String TEXT_295 = ").toString()));" + NL + "                \t" + NL + "                \t  aggStrSplit";
  protected final String TEXT_296 = "[pivotIndex";
  protected final String TEXT_297 = "] = String.valueOf(sum";
  protected final String TEXT_298 = ");" + NL + "                \t  " + NL + "\t\t\t\t\t";
  protected final String TEXT_299 = NL + "\t\t\t\t" + NL + "\t\t\t\t}" + NL + "            ";
  protected final String TEXT_300 = NL + "                int count";
  protected final String TEXT_301 = " = 1;" + NL + "                " + NL + "                if (!(\"\").equals(aggStrSplit";
  protected final String TEXT_302 = "[pivotIndex";
  protected final String TEXT_303 = "]))" + NL + "            " + NL + "                    count";
  protected final String TEXT_304 = " = Integer.parseInt(aggStrSplit";
  protected final String TEXT_305 = "[pivotIndex";
  protected final String TEXT_306 = "]) + 1;" + NL + "                    " + NL + "                aggStrSplit";
  protected final String TEXT_307 = "[pivotIndex";
  protected final String TEXT_308 = "] = String.valueOf(count";
  protected final String TEXT_309 = ");" + NL + "            ";
  protected final String TEXT_310 = NL + "               \t\t" + NL + "               \t\tString unformatedSourceStr_";
  protected final String TEXT_311 = " = FormatterUtils.unformat_Number(aggValues";
  protected final String TEXT_312 = ".append(";
  protected final String TEXT_313 = ".";
  protected final String TEXT_314 = ").toString(),";
  protected final String TEXT_315 = ",";
  protected final String TEXT_316 = ");" + NL + "            \t\tString unformatedTargetStr_";
  protected final String TEXT_317 = " = FormatterUtils.unformat_Number(aggStrSplit";
  protected final String TEXT_318 = "[pivotIndex";
  protected final String TEXT_319 = "],";
  protected final String TEXT_320 = ",";
  protected final String TEXT_321 = ");" + NL + "\t                if(Float.parseFloat(unformatedSourceStr_";
  protected final String TEXT_322 = ")>Float.parseFloat(unformatedTargetStr_";
  protected final String TEXT_323 = "))" + NL + "\t                    aggStrSplit";
  protected final String TEXT_324 = "[pivotIndex";
  protected final String TEXT_325 = "] = FormatterUtils.format_Number(aggValues";
  protected final String TEXT_326 = ".toString(),";
  protected final String TEXT_327 = ",";
  protected final String TEXT_328 = ");" + NL + "\t                    " + NL + "\t            ";
  protected final String TEXT_329 = NL + "\t            " + NL + "\t                if(Float.parseFloat(aggValues";
  protected final String TEXT_330 = ".append(";
  protected final String TEXT_331 = ".";
  protected final String TEXT_332 = ").toString())>Float.parseFloat(aggStrSplit";
  protected final String TEXT_333 = "[pivotIndex";
  protected final String TEXT_334 = "]))" + NL + "\t                    aggStrSplit";
  protected final String TEXT_335 = "[pivotIndex";
  protected final String TEXT_336 = "] = aggValues";
  protected final String TEXT_337 = ".toString();" + NL + "            ";
  protected final String TEXT_338 = NL + "                " + NL + "               \t\tString unformatedSourceStr_";
  protected final String TEXT_339 = " = FormatterUtils.unformat_Number(aggValues";
  protected final String TEXT_340 = ".append(";
  protected final String TEXT_341 = ".";
  protected final String TEXT_342 = ").toString(),";
  protected final String TEXT_343 = ",";
  protected final String TEXT_344 = ");" + NL + "            \t\tString unformatedTargetStr_";
  protected final String TEXT_345 = " = FormatterUtils.unformat_Number(aggStrSplit";
  protected final String TEXT_346 = "[pivotIndex";
  protected final String TEXT_347 = "],";
  protected final String TEXT_348 = ",";
  protected final String TEXT_349 = ");" + NL + "\t                if(Float.parseFloat(unformatedSourceStr_";
  protected final String TEXT_350 = ")<Float.parseFloat(unformatedTargetStr_";
  protected final String TEXT_351 = "))" + NL + "\t                    aggStrSplit";
  protected final String TEXT_352 = "[pivotIndex";
  protected final String TEXT_353 = "] = FormatterUtils.format_Number(aggValues";
  protected final String TEXT_354 = ".toString(),";
  protected final String TEXT_355 = ",";
  protected final String TEXT_356 = ");" + NL + "                    ";
  protected final String TEXT_357 = NL + "                " + NL + "\t                if(Float.parseFloat(aggValues";
  protected final String TEXT_358 = ".append(";
  protected final String TEXT_359 = ".";
  protected final String TEXT_360 = ").toString())<Float.parseFloat(aggStrSplit";
  protected final String TEXT_361 = "[pivotIndex";
  protected final String TEXT_362 = "]))" + NL + "\t                    aggStrSplit";
  protected final String TEXT_363 = "[pivotIndex";
  protected final String TEXT_364 = "] = aggValues";
  protected final String TEXT_365 = ".toString();" + NL + "            ";
  protected final String TEXT_366 = NL + "            \t" + NL + "                if ((\"\").equals(aggStrSplit";
  protected final String TEXT_367 = "[pivotIndex";
  protected final String TEXT_368 = "]))" + NL + "                ";
  protected final String TEXT_369 = NL + "\t\t\t\t" + NL + "\t\t\t\t\t    aggStrSplit";
  protected final String TEXT_370 = "[pivotIndex";
  protected final String TEXT_371 = "] = aggValues";
  protected final String TEXT_372 = ".append(FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_373 = ".";
  protected final String TEXT_374 = "),";
  protected final String TEXT_375 = ",";
  protected final String TEXT_376 = ")).toString();" + NL + "                    ";
  protected final String TEXT_377 = NL + "\t\t\t\t" + NL + "\t\t\t\t\t\t\taggStrSplit";
  protected final String TEXT_378 = "[pivotIndex";
  protected final String TEXT_379 = "] = aggValues";
  protected final String TEXT_380 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_381 = ".";
  protected final String TEXT_382 = ", ";
  protected final String TEXT_383 = ")).toString();" + NL + "\t\t\t\t  " + NL + "           \t\t\t\t";
  protected final String TEXT_384 = NL + "                " + NL + "                    \taggStrSplit";
  protected final String TEXT_385 = "[pivotIndex";
  protected final String TEXT_386 = "] = aggValues";
  protected final String TEXT_387 = ".append(String.valueOf(";
  protected final String TEXT_388 = ".";
  protected final String TEXT_389 = ")).toString();" + NL + "                    " + NL + "                    \t";
  protected final String TEXT_390 = NL + "\t\t\t\t" + NL + "                    \taggStrSplit";
  protected final String TEXT_391 = "[pivotIndex";
  protected final String TEXT_392 = "] = aggValues";
  protected final String TEXT_393 = ".append(FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_394 = ".";
  protected final String TEXT_395 = "),";
  protected final String TEXT_396 = ",";
  protected final String TEXT_397 = ")).toString();" + NL + "                    ";
  protected final String TEXT_398 = NL + "\t\t\t\t" + NL + "\t\t\t\t\t\taggStrSplit";
  protected final String TEXT_399 = "[pivotIndex";
  protected final String TEXT_400 = "] = aggValues";
  protected final String TEXT_401 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_402 = ".";
  protected final String TEXT_403 = ", ";
  protected final String TEXT_404 = ")).toString();" + NL + "\t\t\t\t  " + NL + "\t\t\t\t\t";
  protected final String TEXT_405 = NL + "            " + NL + "                    \taggStrSplit";
  protected final String TEXT_406 = "[pivotIndex";
  protected final String TEXT_407 = "] = aggValues";
  protected final String TEXT_408 = ".append(String.valueOf(";
  protected final String TEXT_409 = ".";
  protected final String TEXT_410 = ")).toString();";
  protected final String TEXT_411 = " " + NL + "            " + NL + "            //step3: write new data back to string" + NL + "            " + NL + "            aggValues";
  protected final String TEXT_412 = ".delete(0, aggValues";
  protected final String TEXT_413 = ".length());" + NL + "            " + NL + "            for(int i = 0; i<aggStrSplit";
  protected final String TEXT_414 = ".length; i++){" + NL + "                " + NL + "                aggValues";
  protected final String TEXT_415 = ".append(aggStrSplit";
  protected final String TEXT_416 = "[i]);" + NL + "                aggValues";
  protected final String TEXT_417 = ".append(";
  protected final String TEXT_418 = ");" + NL + "                " + NL + "            }" + NL + "            " + NL + "            aggregation";
  protected final String TEXT_419 = ".remove(gKvalue";
  protected final String TEXT_420 = ");" + NL + "          " + NL + "            aggregation";
  protected final String TEXT_421 = ".put(gKvalue";
  protected final String TEXT_422 = ",aggValues";
  protected final String TEXT_423 = ".toString());            " + NL + "            " + NL + "        }" + NL + "        " + NL + "        } " + NL + "" + NL + "    nb_line_";
  protected final String TEXT_424 = "++;" + NL + "   ";
  protected final String TEXT_425 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER); 
   		    
List<Map<String, String>> groupbys = 
    ( List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUPBYS__");
	
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {
                
            String pivotColumn = ElementParameterParser.getValue(node, "__PIVOT_COLUMN__");

            String aggColumn = ElementParameterParser.getValue(node, "__AGGREGATION_COLUMN__");

            String aggFunction = ElementParameterParser.getValue(node, "__AGGREGATION_FUNCTION__");
                
            String fieldSeparator = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
            
            String rowSeparator = ElementParameterParser.getValue(node,"__ROWSEPARATOR__");

            //String fieldSeparator = delim1.substring(1,delim1.length()-1);
            
            List<? extends IConnection> incomingConnections = node.getIncomingConnections();
            
                if (incomingConnections != null && !incomingConnections.isEmpty()) {
                    
                    IMetadataTable inMetadata = incomingConnections.get(0).getMetadataTable();
                    
                    String inConnName = incomingConnections.get(0).getName();
                    
                    List<IMetadataColumn> columnList = metadata.getListColumns();
                    
                    JavaType javaType = null;
                    
                    String columnType = null;
                    
                    String columnTypeValue = null;
                    
                    String columnTypeFunName = null;
                    
                    String pattern = null;
                    
                    JavaType pivotJavaType = null;
                    
					String pivotTypeToGenerate = null;
					
					IMetadataColumn columnPivot = null;
					
					String pivotpattern = null; 
					
             		for(IMetadataColumn column:columnList){
             			
             			if(column.getLabel().equals(aggColumn)){
             			
             				javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
             			
							columnType = column.getTalendType().substring(3);
							
							columnTypeValue = JavaTypesManager.getShortNameFromJavaType(JavaTypesManager.getJavaTypeFromId(column.getTalendType()));
							
							columnTypeFunName = columnTypeValue.substring(0,1).toUpperCase()+columnTypeValue.substring(1);
							
							pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();

							break;
						}
             		}
	
					for(IMetadataColumn column:columnList){
					
					   	if(column.getLabel().equals(pivotColumn)){
					   	
					    		pivotJavaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					    		
					    		pivotTypeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					    		
					    		columnPivot = column;
					    		
					    		pivotpattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern(); 
					    							    		
					    		break;
					    }
					}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
	if(JavaTypesManager.isNumberType(pivotJavaType,columnPivot.isNullable())){
	
			if(columnPivot.isNullable() || "BigDecimal".equals(pivotTypeToGenerate)){

    stringBuffer.append(TEXT_9);
    stringBuffer.append( pivotTypeToGenerate );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_13);
    
			}else{

    stringBuffer.append(TEXT_14);
    stringBuffer.append( pivotTypeToGenerate );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_18);
    
			}
	}else if(pivotJavaType == JavaTypesManager.DATE) { 

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(pivotpattern);
    stringBuffer.append(TEXT_23);
    
	}else{

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_27);
    	
	}

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
     
		if(pivotJavaType == JavaTypesManager.DATE) { 

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(pivotpattern);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_41);
     
        }else{

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_48);
     
		} 

    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    
    for (int i=0; i<groupbys.size(); i++) {
       
    	Map<String, String> groupby = groupbys.get(i);
      
      	String pattern_groupby = null;
      
      	JavaType columnType_groupby = null;
      	
		for(IMetadataColumn column:columnList){
		
			if(column.getLabel().equals(groupby.get("INPUT_COLUMN"))){
			
				columnType_groupby = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				
				pattern_groupby = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
				
				break;
			}
		}
		if(columnType_groupby == JavaTypesManager.DATE){
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(groupby.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_67);
    stringBuffer.append( pattern_groupby );
    stringBuffer.append(TEXT_68);
    if(i!=groupbys.size()-1){
    stringBuffer.append(TEXT_69);
    stringBuffer.append(fieldSeparator);
    }
    stringBuffer.append(TEXT_70);
    }else{
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(groupby.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_76);
    if(i!=groupbys.size()-1){
    stringBuffer.append(TEXT_77);
    stringBuffer.append(fieldSeparator);
    }
    stringBuffer.append(TEXT_78);
    }
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    

     }
    
    
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_101);
    if( ("count").equals(aggFunction)){
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_106);
    }else{
	if(advancedSeparator && JavaTypesManager.isNumberType(javaType)){
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_113);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_115);
    }else{
  
  			if(("Date").equals(columnType)){
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_121);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_123);
    }else{
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_130);
    }
		}
  }
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
    stringBuffer.append(fieldSeparator);
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
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    if( ("count").equals(aggFunction)){
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_151);
    }else{
	if(advancedSeparator && JavaTypesManager.isNumberType(javaType)){
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_156);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_157);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_160);
    }else{
  
  			if(("Date").equals(columnType)){
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_166);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_168);
    }else{
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_175);
    			}
        }
  }
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_183);
    
	if(JavaTypesManager.isNumberType(pivotJavaType,columnPivot.isNullable())){
	
			if(columnPivot.isNullable() || "BigDecimal".equals(pivotTypeToGenerate)){

    stringBuffer.append(TEXT_184);
    stringBuffer.append( pivotTypeToGenerate );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_186);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_188);
    
			}else{

    stringBuffer.append(TEXT_189);
    stringBuffer.append( pivotTypeToGenerate );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_191);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_192);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_193);
    
			}
	}else if(pivotJavaType == JavaTypesManager.DATE) { 

    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_196);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_197);
    stringBuffer.append(pivotpattern);
    stringBuffer.append(TEXT_198);
    
	}else{

    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(pivotColumn);
    stringBuffer.append(TEXT_202);
    	
	}

    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_206);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_209);
      
            if( ("sum").equals(aggFunction) ){
            
    stringBuffer.append(TEXT_210);
    stringBuffer.append(TEXT_211);
    stringBuffer.append(columnTypeValue);
    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_214);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_215);
    
                	if(("BigDecimal").equals(columnType)) {
                	
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_217);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_218);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_219);
    
                		if(advancedSeparator){
    stringBuffer.append(TEXT_220);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_221);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_223);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_224);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_225);
    
                		} else {
    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_227);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_228);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_229);
    
                		}
					} else if(("Double").equals(columnType)||("Float").equals(columnType)){
						if(advancedSeparator){
						
    stringBuffer.append(TEXT_230);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_231);
    stringBuffer.append(columnType);
    stringBuffer.append(TEXT_232);
    stringBuffer.append(columnTypeFunName);
    stringBuffer.append(TEXT_233);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_234);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_237);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_238);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_239);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_240);
    }else{
    stringBuffer.append(TEXT_241);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_242);
    stringBuffer.append(columnType);
    stringBuffer.append(TEXT_243);
    stringBuffer.append(columnTypeFunName);
    stringBuffer.append(TEXT_244);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_245);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_246);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_247);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_248);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_249);
    }
					  }else{
    stringBuffer.append(TEXT_250);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_251);
    stringBuffer.append(columnType);
    stringBuffer.append(TEXT_252);
    stringBuffer.append(columnTypeFunName);
    stringBuffer.append(TEXT_253);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_254);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_255);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_258);
    }
    stringBuffer.append(TEXT_259);
    if(("BigDecimal").equals(columnType) || ("Double").equals(columnType)||("Float").equals(columnType)){
               			if(advancedSeparator){
    stringBuffer.append(TEXT_260);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_261);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_263);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_264);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_265);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_267);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_268);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_269);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_270);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_271);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_272);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_273);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_274);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_275);
    }else{
    stringBuffer.append(TEXT_276);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_277);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_278);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_279);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_280);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_281);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_282);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_283);
    }
					}else{
    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_285);
    stringBuffer.append(columnTypeValue);
    stringBuffer.append(TEXT_286);
    stringBuffer.append(columnType);
    stringBuffer.append(TEXT_287);
    stringBuffer.append(columnTypeFunName);
    stringBuffer.append(TEXT_288);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_290);
    stringBuffer.append(columnType);
    stringBuffer.append(TEXT_291);
    stringBuffer.append(columnTypeFunName);
    stringBuffer.append(TEXT_292);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_293);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_294);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_295);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_296);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_297);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_298);
    }
    stringBuffer.append(TEXT_299);
    
            }else if( ("count").equals(aggFunction) ){
            
    stringBuffer.append(TEXT_300);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_301);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_302);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_303);
    stringBuffer.append(cid);
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
    
            }else if( ("max").equals(aggFunction) ){
               	if(advancedSeparator){
    stringBuffer.append(TEXT_310);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_311);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_312);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_313);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_314);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_315);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_316);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_317);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_318);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_319);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_320);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_321);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_322);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_323);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_324);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_325);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_326);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_327);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_328);
    }else{
    stringBuffer.append(TEXT_329);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_330);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_331);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_332);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_333);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_334);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_335);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_336);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_337);
    
            	}
            }else if( ("min").equals(aggFunction) ){
               	if(advancedSeparator){
    stringBuffer.append(TEXT_338);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_339);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_340);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_341);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_342);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_343);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_344);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_345);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_346);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_347);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_348);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_349);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_350);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_351);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_352);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_353);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_354);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_355);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_356);
    }else{
    stringBuffer.append(TEXT_357);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_358);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_359);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_360);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_361);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_362);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_363);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_364);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_365);
    
            	}
            }else if( ("first").equals(aggFunction) ){
            
    stringBuffer.append(TEXT_366);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_367);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_368);
    if(advancedSeparator){
    stringBuffer.append(TEXT_369);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_371);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_372);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_373);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_374);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_375);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_376);
    }else{
                    	if(("Date").equals(columnType)){
    stringBuffer.append(TEXT_377);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_378);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_379);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_380);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_381);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_382);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_383);
    }else{
    stringBuffer.append(TEXT_384);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_385);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_386);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_387);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_388);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_389);
    }
            		}
            }else if( ("last").equals(aggFunction) ){
            	if(advancedSeparator){
    stringBuffer.append(TEXT_390);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_391);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_392);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_393);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_394);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_395);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_396);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_397);
    }else{
           			if(("Date").equals(columnType)){
    stringBuffer.append(TEXT_398);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_399);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_400);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_401);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_402);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_403);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_404);
    }else{
    stringBuffer.append(TEXT_405);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_406);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_407);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_408);
    stringBuffer.append(inConnName);
    stringBuffer.append(TEXT_409);
    stringBuffer.append(aggColumn);
    stringBuffer.append(TEXT_410);
    }
            	}
            }
            
    stringBuffer.append(TEXT_411);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_412);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_413);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_414);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_415);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_416);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_417);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_418);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_419);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_420);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_421);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_422);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_423);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_424);
    
        }
    }
}

    stringBuffer.append(TEXT_425);
    return stringBuffer.toString();
  }
}
