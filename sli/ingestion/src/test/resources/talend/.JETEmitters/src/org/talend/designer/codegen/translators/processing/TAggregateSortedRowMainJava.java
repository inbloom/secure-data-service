package org.talend.designer.codegen.translators.processing;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TAggregateSortedRowMainJava
{
  protected static String nl;
  public static synchronized TAggregateSortedRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAggregateSortedRowMainJava result = new TAggregateSortedRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "currentRowIndex_";
  protected final String TEXT_3 = "++;";
  protected final String TEXT_4 = NL + "boolean sameGroup_";
  protected final String TEXT_5 = " = true;";
  protected final String TEXT_6 = "if(flag_";
  protected final String TEXT_7 = "){" + NL + "\tflag_";
  protected final String TEXT_8 = " = false;";
  protected final String TEXT_9 = "group_";
  protected final String TEXT_10 = "_";
  protected final String TEXT_11 = " = ";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = ";";
  protected final String TEXT_14 = NL;
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = "_";
  protected final String TEXT_17 = "_";
  protected final String TEXT_18 = " = ";
  protected final String TEXT_19 = ".";
  protected final String TEXT_20 = ";";
  protected final String TEXT_21 = "\tif(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = " != null){";
  protected final String TEXT_24 = "count_";
  protected final String TEXT_25 = "_";
  protected final String TEXT_26 = "_";
  protected final String TEXT_27 = " = 1;";
  protected final String TEXT_28 = "\t}else{" + NL + "count_";
  protected final String TEXT_29 = "_";
  protected final String TEXT_30 = "_";
  protected final String TEXT_31 = " = 0;" + NL + "}";
  protected final String TEXT_32 = "\tif(";
  protected final String TEXT_33 = ".";
  protected final String TEXT_34 = " != null){";
  protected final String TEXT_35 = NL + "\t\t";
  protected final String TEXT_36 = "_";
  protected final String TEXT_37 = "_";
  protected final String TEXT_38 = "_";
  protected final String TEXT_39 = " = (double)";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = ";";
  protected final String TEXT_42 = "\t}else{" + NL + "\t\t";
  protected final String TEXT_43 = "_";
  protected final String TEXT_44 = "_";
  protected final String TEXT_45 = "_";
  protected final String TEXT_46 = " = (double)0;" + NL + "\t}";
  protected final String TEXT_47 = "\tif(";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = " != null){";
  protected final String TEXT_50 = NL + "\t\t";
  protected final String TEXT_51 = "_";
  protected final String TEXT_52 = "_";
  protected final String TEXT_53 = "_";
  protected final String TEXT_54 = " = ";
  protected final String TEXT_55 = ".";
  protected final String TEXT_56 = ";";
  protected final String TEXT_57 = "\t}else{" + NL + "\t\t";
  protected final String TEXT_58 = "_";
  protected final String TEXT_59 = "_";
  protected final String TEXT_60 = "_";
  protected final String TEXT_61 = " = new BigDecimal(\"0.0\");" + NL + "\t}";
  protected final String TEXT_62 = NL + "if(";
  protected final String TEXT_63 = ".";
  protected final String TEXT_64 = " != null){" + NL + "\tsum_";
  protected final String TEXT_65 = "_";
  protected final String TEXT_66 = "_";
  protected final String TEXT_67 = " = (double)";
  protected final String TEXT_68 = ".";
  protected final String TEXT_69 = ";" + NL + "\tcount_";
  protected final String TEXT_70 = "_";
  protected final String TEXT_71 = "_";
  protected final String TEXT_72 = " = 1;" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_73 = "_";
  protected final String TEXT_74 = "_";
  protected final String TEXT_75 = " = (double)0;" + NL + "\tcount_";
  protected final String TEXT_76 = "_";
  protected final String TEXT_77 = "_";
  protected final String TEXT_78 = " = 0;" + NL + "}";
  protected final String TEXT_79 = NL + "if(";
  protected final String TEXT_80 = ".";
  protected final String TEXT_81 = " != null){" + NL + "\tsum_";
  protected final String TEXT_82 = "_";
  protected final String TEXT_83 = "_";
  protected final String TEXT_84 = " = (double)";
  protected final String TEXT_85 = ".";
  protected final String TEXT_86 = ";" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_87 = "_";
  protected final String TEXT_88 = "_";
  protected final String TEXT_89 = " = (double)0;" + NL + "}" + NL + "count_";
  protected final String TEXT_90 = "_";
  protected final String TEXT_91 = "_";
  protected final String TEXT_92 = " = 1;";
  protected final String TEXT_93 = NL + "sum_";
  protected final String TEXT_94 = "_";
  protected final String TEXT_95 = "_";
  protected final String TEXT_96 = " = (double)";
  protected final String TEXT_97 = ".";
  protected final String TEXT_98 = ";" + NL + "count_";
  protected final String TEXT_99 = "_";
  protected final String TEXT_100 = "_";
  protected final String TEXT_101 = " = 1;";
  protected final String TEXT_102 = "\t" + NL + "if(";
  protected final String TEXT_103 = ".";
  protected final String TEXT_104 = " != null){" + NL + "\tsum_";
  protected final String TEXT_105 = "_";
  protected final String TEXT_106 = "_";
  protected final String TEXT_107 = " = ";
  protected final String TEXT_108 = ".";
  protected final String TEXT_109 = ";" + NL + "\tcount_";
  protected final String TEXT_110 = "_";
  protected final String TEXT_111 = "_";
  protected final String TEXT_112 = " = 1;" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_113 = "_";
  protected final String TEXT_114 = "_";
  protected final String TEXT_115 = " = new BigDecimal(\"0.0\");" + NL + "\tcount_";
  protected final String TEXT_116 = "_";
  protected final String TEXT_117 = "_";
  protected final String TEXT_118 = " = 0;" + NL + "}";
  protected final String TEXT_119 = NL + "if(";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = " != null){" + NL + "\tsum_";
  protected final String TEXT_122 = "_";
  protected final String TEXT_123 = "_";
  protected final String TEXT_124 = " = ";
  protected final String TEXT_125 = ".";
  protected final String TEXT_126 = ";" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_127 = "_";
  protected final String TEXT_128 = "_";
  protected final String TEXT_129 = " = new BigDecimal(\"0.0\");" + NL + "}" + NL + "count_";
  protected final String TEXT_130 = "_";
  protected final String TEXT_131 = "_";
  protected final String TEXT_132 = " = 1;";
  protected final String TEXT_133 = "set_";
  protected final String TEXT_134 = "_";
  protected final String TEXT_135 = "_";
  protected final String TEXT_136 = " = new java.util.HashSet();";
  protected final String TEXT_137 = "\tif(";
  protected final String TEXT_138 = ".";
  protected final String TEXT_139 = " != null){";
  protected final String TEXT_140 = NL + "set_";
  protected final String TEXT_141 = "_";
  protected final String TEXT_142 = "_";
  protected final String TEXT_143 = ".add(";
  protected final String TEXT_144 = ".";
  protected final String TEXT_145 = ");";
  protected final String TEXT_146 = "\t}";
  protected final String TEXT_147 = "list_object_";
  protected final String TEXT_148 = "_";
  protected final String TEXT_149 = "_";
  protected final String TEXT_150 = " = new java.util.ArrayList();";
  protected final String TEXT_151 = "\tif(";
  protected final String TEXT_152 = ".";
  protected final String TEXT_153 = " != null){";
  protected final String TEXT_154 = NL + "\t\tlist_object_";
  protected final String TEXT_155 = "_";
  protected final String TEXT_156 = "_";
  protected final String TEXT_157 = ".add(";
  protected final String TEXT_158 = ".";
  protected final String TEXT_159 = ");";
  protected final String TEXT_160 = "\t}";
  protected final String TEXT_161 = "list_";
  protected final String TEXT_162 = "_";
  protected final String TEXT_163 = "_";
  protected final String TEXT_164 = " = new StringBuilder();";
  protected final String TEXT_165 = "\tif(";
  protected final String TEXT_166 = ".";
  protected final String TEXT_167 = " != null){";
  protected final String TEXT_168 = NL + "\t\tlist_";
  protected final String TEXT_169 = "_";
  protected final String TEXT_170 = "_";
  protected final String TEXT_171 = ".append(";
  protected final String TEXT_172 = ".";
  protected final String TEXT_173 = ");";
  protected final String TEXT_174 = "\t}";
  protected final String TEXT_175 = NL + "}else{";
  protected final String TEXT_176 = NL + "while(true){";
  protected final String TEXT_177 = NL + "if(group_";
  protected final String TEXT_178 = "_";
  protected final String TEXT_179 = " != ";
  protected final String TEXT_180 = ".";
  protected final String TEXT_181 = "){" + NL + "\tsameGroup_";
  protected final String TEXT_182 = " = false;" + NL + "\tbreak;" + NL + "}";
  protected final String TEXT_183 = "if(group_";
  protected final String TEXT_184 = "_";
  protected final String TEXT_185 = " == null){" + NL + "\tif(";
  protected final String TEXT_186 = ".";
  protected final String TEXT_187 = " != null){" + NL + "\t\tsameGroup_";
  protected final String TEXT_188 = " = false;" + NL + "\t\tbreak;" + NL + "\t}" + NL + "}else{" + NL + "\tif(group_";
  protected final String TEXT_189 = "_";
  protected final String TEXT_190 = " == null || !group_";
  protected final String TEXT_191 = "_";
  protected final String TEXT_192 = ".equals(";
  protected final String TEXT_193 = ".";
  protected final String TEXT_194 = ")){" + NL + "\t\tsameGroup_";
  protected final String TEXT_195 = " = false;" + NL + "\t\tbreak;" + NL + "\t}" + NL + "}";
  protected final String TEXT_196 = "break;";
  protected final String TEXT_197 = "}" + NL + "if(sameGroup_";
  protected final String TEXT_198 = "){" + NL;
  protected final String TEXT_199 = NL + "if(";
  protected final String TEXT_200 = "_";
  protected final String TEXT_201 = "_";
  protected final String TEXT_202 = "_";
  protected final String TEXT_203 = " == null){" + NL + "\t";
  protected final String TEXT_204 = "_";
  protected final String TEXT_205 = "_";
  protected final String TEXT_206 = "_";
  protected final String TEXT_207 = " = ";
  protected final String TEXT_208 = ".";
  protected final String TEXT_209 = ";" + NL + "}";
  protected final String TEXT_210 = NL + "if(";
  protected final String TEXT_211 = ".";
  protected final String TEXT_212 = " != null){";
  protected final String TEXT_213 = NL + "\t";
  protected final String TEXT_214 = "_";
  protected final String TEXT_215 = "_";
  protected final String TEXT_216 = "_";
  protected final String TEXT_217 = " = ";
  protected final String TEXT_218 = ".";
  protected final String TEXT_219 = ";";
  protected final String TEXT_220 = NL + "}";
  protected final String TEXT_221 = NL + "\tif(";
  protected final String TEXT_222 = ".";
  protected final String TEXT_223 = " !=null){" + NL + "\t\tif(";
  protected final String TEXT_224 = "_";
  protected final String TEXT_225 = "_";
  protected final String TEXT_226 = "_";
  protected final String TEXT_227 = " == null || ";
  protected final String TEXT_228 = "_";
  protected final String TEXT_229 = "_";
  protected final String TEXT_230 = "_";
  protected final String TEXT_231 = ".compareTo(";
  protected final String TEXT_232 = ".";
  protected final String TEXT_233 = ") > 0){" + NL + "\t\t\t";
  protected final String TEXT_234 = "_";
  protected final String TEXT_235 = "_";
  protected final String TEXT_236 = "_";
  protected final String TEXT_237 = " = ";
  protected final String TEXT_238 = ".";
  protected final String TEXT_239 = ";" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_240 = NL + "if(";
  protected final String TEXT_241 = ".";
  protected final String TEXT_242 = " !=null){" + NL + "\tif(";
  protected final String TEXT_243 = "_";
  protected final String TEXT_244 = "_";
  protected final String TEXT_245 = "_";
  protected final String TEXT_246 = " == null || ";
  protected final String TEXT_247 = "_";
  protected final String TEXT_248 = "_";
  protected final String TEXT_249 = "_";
  protected final String TEXT_250 = ".compareTo(";
  protected final String TEXT_251 = ".";
  protected final String TEXT_252 = ") > 0){" + NL + "\t\t";
  protected final String TEXT_253 = "_";
  protected final String TEXT_254 = "_";
  protected final String TEXT_255 = "_";
  protected final String TEXT_256 = " = ";
  protected final String TEXT_257 = ".";
  protected final String TEXT_258 = ";" + NL + "\t}" + NL + "}";
  protected final String TEXT_259 = NL + "if(!";
  protected final String TEXT_260 = ".";
  protected final String TEXT_261 = " && ";
  protected final String TEXT_262 = "_";
  protected final String TEXT_263 = "_";
  protected final String TEXT_264 = "_";
  protected final String TEXT_265 = "){" + NL + "\t";
  protected final String TEXT_266 = "_";
  protected final String TEXT_267 = "_";
  protected final String TEXT_268 = "_";
  protected final String TEXT_269 = " = false;" + NL + "}";
  protected final String TEXT_270 = NL + "if(";
  protected final String TEXT_271 = ".";
  protected final String TEXT_272 = " !=null){" + NL + "\tif(";
  protected final String TEXT_273 = "_";
  protected final String TEXT_274 = "_";
  protected final String TEXT_275 = "_";
  protected final String TEXT_276 = " == null || ";
  protected final String TEXT_277 = "_";
  protected final String TEXT_278 = "_";
  protected final String TEXT_279 = "_";
  protected final String TEXT_280 = ".compareTo(";
  protected final String TEXT_281 = ".";
  protected final String TEXT_282 = ") > 0){" + NL + "\t\t";
  protected final String TEXT_283 = "_";
  protected final String TEXT_284 = "_";
  protected final String TEXT_285 = "_";
  protected final String TEXT_286 = " = ";
  protected final String TEXT_287 = ".";
  protected final String TEXT_288 = ";" + NL + "\t}" + NL + "}";
  protected final String TEXT_289 = NL + "if(";
  protected final String TEXT_290 = "_";
  protected final String TEXT_291 = "_";
  protected final String TEXT_292 = "_";
  protected final String TEXT_293 = " > ";
  protected final String TEXT_294 = ".";
  protected final String TEXT_295 = "){" + NL + "\t";
  protected final String TEXT_296 = "_";
  protected final String TEXT_297 = "_";
  protected final String TEXT_298 = "_";
  protected final String TEXT_299 = " = ";
  protected final String TEXT_300 = ".";
  protected final String TEXT_301 = ";" + NL + "}";
  protected final String TEXT_302 = "if(";
  protected final String TEXT_303 = ".";
  protected final String TEXT_304 = " !=null){" + NL + "\tif(";
  protected final String TEXT_305 = "_";
  protected final String TEXT_306 = "_";
  protected final String TEXT_307 = "_";
  protected final String TEXT_308 = " == null || ";
  protected final String TEXT_309 = "_";
  protected final String TEXT_310 = "_";
  protected final String TEXT_311 = "_";
  protected final String TEXT_312 = ".compareTo(";
  protected final String TEXT_313 = ".";
  protected final String TEXT_314 = ") < 0){" + NL + "\t\t";
  protected final String TEXT_315 = "_";
  protected final String TEXT_316 = "_";
  protected final String TEXT_317 = "_";
  protected final String TEXT_318 = " = ";
  protected final String TEXT_319 = ".";
  protected final String TEXT_320 = ";" + NL + "\t}" + NL + "}";
  protected final String TEXT_321 = NL + "if(";
  protected final String TEXT_322 = ".";
  protected final String TEXT_323 = " !=null){" + NL + "\tif(";
  protected final String TEXT_324 = "_";
  protected final String TEXT_325 = "_";
  protected final String TEXT_326 = "_";
  protected final String TEXT_327 = " == null || ";
  protected final String TEXT_328 = "_";
  protected final String TEXT_329 = "_";
  protected final String TEXT_330 = "_";
  protected final String TEXT_331 = ".compareTo(";
  protected final String TEXT_332 = ".";
  protected final String TEXT_333 = ") < 0){" + NL + "\t\t";
  protected final String TEXT_334 = "_";
  protected final String TEXT_335 = "_";
  protected final String TEXT_336 = "_";
  protected final String TEXT_337 = " = ";
  protected final String TEXT_338 = ".";
  protected final String TEXT_339 = ";" + NL + "\t}" + NL + "}";
  protected final String TEXT_340 = NL + "if(";
  protected final String TEXT_341 = ".";
  protected final String TEXT_342 = " && !";
  protected final String TEXT_343 = "_";
  protected final String TEXT_344 = "_";
  protected final String TEXT_345 = "_";
  protected final String TEXT_346 = "){" + NL + "\t";
  protected final String TEXT_347 = "_";
  protected final String TEXT_348 = "_";
  protected final String TEXT_349 = "_";
  protected final String TEXT_350 = " = true;" + NL + "}";
  protected final String TEXT_351 = NL + "if(";
  protected final String TEXT_352 = ".";
  protected final String TEXT_353 = " !=null){" + NL + "\tif(";
  protected final String TEXT_354 = "_";
  protected final String TEXT_355 = "_";
  protected final String TEXT_356 = "_";
  protected final String TEXT_357 = " == null || ";
  protected final String TEXT_358 = "_";
  protected final String TEXT_359 = "_";
  protected final String TEXT_360 = "_";
  protected final String TEXT_361 = ".compareTo(";
  protected final String TEXT_362 = ".";
  protected final String TEXT_363 = ") < 0){" + NL + "\t\t";
  protected final String TEXT_364 = "_";
  protected final String TEXT_365 = "_";
  protected final String TEXT_366 = "_";
  protected final String TEXT_367 = " = ";
  protected final String TEXT_368 = ".";
  protected final String TEXT_369 = ";" + NL + "\t}" + NL + "}";
  protected final String TEXT_370 = NL + "if(";
  protected final String TEXT_371 = "_";
  protected final String TEXT_372 = "_";
  protected final String TEXT_373 = "_";
  protected final String TEXT_374 = " < ";
  protected final String TEXT_375 = ".";
  protected final String TEXT_376 = "){" + NL + "\t";
  protected final String TEXT_377 = "_";
  protected final String TEXT_378 = "_";
  protected final String TEXT_379 = "_";
  protected final String TEXT_380 = " = ";
  protected final String TEXT_381 = ".";
  protected final String TEXT_382 = ";" + NL + "}";
  protected final String TEXT_383 = "\tif(";
  protected final String TEXT_384 = ".";
  protected final String TEXT_385 = " != null){";
  protected final String TEXT_386 = "count_";
  protected final String TEXT_387 = "_";
  protected final String TEXT_388 = "_";
  protected final String TEXT_389 = " ++;";
  protected final String TEXT_390 = "\t}";
  protected final String TEXT_391 = "\tif(";
  protected final String TEXT_392 = ".";
  protected final String TEXT_393 = " != null){";
  protected final String TEXT_394 = "sum_";
  protected final String TEXT_395 = "_";
  protected final String TEXT_396 = "_";
  protected final String TEXT_397 = " += ";
  protected final String TEXT_398 = ".";
  protected final String TEXT_399 = ";";
  protected final String TEXT_400 = "\t}";
  protected final String TEXT_401 = "if(";
  protected final String TEXT_402 = ".";
  protected final String TEXT_403 = " != null){" + NL + "\tif(sum_";
  protected final String TEXT_404 = "_";
  protected final String TEXT_405 = "_";
  protected final String TEXT_406 = " == null){" + NL + "\t\tsum_";
  protected final String TEXT_407 = "_";
  protected final String TEXT_408 = "_";
  protected final String TEXT_409 = " = ";
  protected final String TEXT_410 = ".";
  protected final String TEXT_411 = ";" + NL + "\t}else{" + NL + "\t\tsum_";
  protected final String TEXT_412 = "_";
  protected final String TEXT_413 = "_";
  protected final String TEXT_414 = " = sum_";
  protected final String TEXT_415 = "_";
  protected final String TEXT_416 = "_";
  protected final String TEXT_417 = ".add(";
  protected final String TEXT_418 = ".";
  protected final String TEXT_419 = ");" + NL + "\t}" + NL + "}";
  protected final String TEXT_420 = "\tif(";
  protected final String TEXT_421 = ".";
  protected final String TEXT_422 = " != null){";
  protected final String TEXT_423 = "\tif(";
  protected final String TEXT_424 = ".";
  protected final String TEXT_425 = " != null){";
  protected final String TEXT_426 = "sum_";
  protected final String TEXT_427 = "_";
  protected final String TEXT_428 = "_";
  protected final String TEXT_429 = " += ";
  protected final String TEXT_430 = ".";
  protected final String TEXT_431 = ";";
  protected final String TEXT_432 = "\t}";
  protected final String TEXT_433 = NL + "count_";
  protected final String TEXT_434 = "_";
  protected final String TEXT_435 = "_";
  protected final String TEXT_436 = "++;";
  protected final String TEXT_437 = "\t}";
  protected final String TEXT_438 = "if(";
  protected final String TEXT_439 = ".";
  protected final String TEXT_440 = " != null){" + NL + "\tif(sum_";
  protected final String TEXT_441 = "_";
  protected final String TEXT_442 = "_";
  protected final String TEXT_443 = " == null){" + NL + "\t\tsum_";
  protected final String TEXT_444 = "_";
  protected final String TEXT_445 = "_";
  protected final String TEXT_446 = " = ";
  protected final String TEXT_447 = ".";
  protected final String TEXT_448 = ";" + NL + "\t}else{" + NL + "\t\tsum_";
  protected final String TEXT_449 = "_";
  protected final String TEXT_450 = "_";
  protected final String TEXT_451 = " = sum_";
  protected final String TEXT_452 = "_";
  protected final String TEXT_453 = "_";
  protected final String TEXT_454 = ".add(";
  protected final String TEXT_455 = ".";
  protected final String TEXT_456 = ");" + NL + "\t}";
  protected final String TEXT_457 = "count_";
  protected final String TEXT_458 = "_";
  protected final String TEXT_459 = "_";
  protected final String TEXT_460 = "++;";
  protected final String TEXT_461 = NL + "}";
  protected final String TEXT_462 = "count_";
  protected final String TEXT_463 = "_";
  protected final String TEXT_464 = "_";
  protected final String TEXT_465 = "++;";
  protected final String TEXT_466 = "\tif(";
  protected final String TEXT_467 = ".";
  protected final String TEXT_468 = " != null){";
  protected final String TEXT_469 = "_";
  protected final String TEXT_470 = "_";
  protected final String TEXT_471 = "_";
  protected final String TEXT_472 = ".append(\",\");";
  protected final String TEXT_473 = NL;
  protected final String TEXT_474 = "_";
  protected final String TEXT_475 = "_";
  protected final String TEXT_476 = "_";
  protected final String TEXT_477 = ".append(";
  protected final String TEXT_478 = ".";
  protected final String TEXT_479 = ");";
  protected final String TEXT_480 = "\t}";
  protected final String TEXT_481 = "\tif(";
  protected final String TEXT_482 = ".";
  protected final String TEXT_483 = " != null){";
  protected final String TEXT_484 = "_";
  protected final String TEXT_485 = "_";
  protected final String TEXT_486 = "_";
  protected final String TEXT_487 = ".add(";
  protected final String TEXT_488 = ".";
  protected final String TEXT_489 = ");";
  protected final String TEXT_490 = "\t}";
  protected final String TEXT_491 = "\tif(";
  protected final String TEXT_492 = ".";
  protected final String TEXT_493 = " != null){";
  protected final String TEXT_494 = NL + "set_";
  protected final String TEXT_495 = "_";
  protected final String TEXT_496 = "_";
  protected final String TEXT_497 = ".add(";
  protected final String TEXT_498 = ".";
  protected final String TEXT_499 = ");";
  protected final String TEXT_500 = "\t}";
  protected final String TEXT_501 = "}//if_same_group" + NL;
  protected final String TEXT_502 = NL + "}" + NL + "" + NL + "" + NL + "int tempCount_";
  protected final String TEXT_503 = " = -1;";
  protected final String TEXT_504 = NL + "if( !sameGroup_";
  protected final String TEXT_505 = " ){" + NL + "\ttempCount_";
  protected final String TEXT_506 = "++;";
  protected final String TEXT_507 = "emmitArray_";
  protected final String TEXT_508 = "[tempCount_";
  protected final String TEXT_509 = "].";
  protected final String TEXT_510 = " = group_";
  protected final String TEXT_511 = "_";
  protected final String TEXT_512 = ";";
  protected final String TEXT_513 = "String temp_";
  protected final String TEXT_514 = " = \"\";";
  protected final String TEXT_515 = "temp_";
  protected final String TEXT_516 = " = new String(group_";
  protected final String TEXT_517 = "_";
  protected final String TEXT_518 = ");";
  protected final String TEXT_519 = "temp_";
  protected final String TEXT_520 = " = \"\"+group_";
  protected final String TEXT_521 = "_";
  protected final String TEXT_522 = ";";
  protected final String TEXT_523 = "if(temp_";
  protected final String TEXT_524 = ".length() > 0) {";
  protected final String TEXT_525 = "emmitArray_";
  protected final String TEXT_526 = "[tempCount_";
  protected final String TEXT_527 = "].";
  protected final String TEXT_528 = " = temp_";
  protected final String TEXT_529 = ";";
  protected final String TEXT_530 = "emmitArray_";
  protected final String TEXT_531 = "[tempCount_";
  protected final String TEXT_532 = "].";
  protected final String TEXT_533 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_534 = ", ";
  protected final String TEXT_535 = ");";
  protected final String TEXT_536 = "emmitArray_";
  protected final String TEXT_537 = "[tempCount_";
  protected final String TEXT_538 = "].";
  protected final String TEXT_539 = " = ParserUtils.parseTo_";
  protected final String TEXT_540 = "(temp_";
  protected final String TEXT_541 = ");";
  protected final String TEXT_542 = "} else {\t\t\t\t\t\t";
  protected final String TEXT_543 = "throw new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_544 = "' in '";
  protected final String TEXT_545 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_546 = "emmitArray_";
  protected final String TEXT_547 = "[tempCount_";
  protected final String TEXT_548 = "].";
  protected final String TEXT_549 = " = ";
  protected final String TEXT_550 = ";";
  protected final String TEXT_551 = "}";
  protected final String TEXT_552 = "emmitArray_";
  protected final String TEXT_553 = "[tempCount_";
  protected final String TEXT_554 = "].";
  protected final String TEXT_555 = " = ";
  protected final String TEXT_556 = "_";
  protected final String TEXT_557 = "_";
  protected final String TEXT_558 = "_";
  protected final String TEXT_559 = ";";
  protected final String TEXT_560 = "emmitArray_";
  protected final String TEXT_561 = "[tempCount_";
  protected final String TEXT_562 = "].";
  protected final String TEXT_563 = " = (";
  protected final String TEXT_564 = ")";
  protected final String TEXT_565 = "_";
  protected final String TEXT_566 = "_";
  protected final String TEXT_567 = "_";
  protected final String TEXT_568 = ";";
  protected final String TEXT_569 = "String temp_";
  protected final String TEXT_570 = " = \"\";";
  protected final String TEXT_571 = "temp_";
  protected final String TEXT_572 = " = new String(";
  protected final String TEXT_573 = "_";
  protected final String TEXT_574 = "_";
  protected final String TEXT_575 = "_";
  protected final String TEXT_576 = ");";
  protected final String TEXT_577 = "temp_";
  protected final String TEXT_578 = " = \"\"+";
  protected final String TEXT_579 = "_";
  protected final String TEXT_580 = "_";
  protected final String TEXT_581 = "_";
  protected final String TEXT_582 = ";";
  protected final String TEXT_583 = "if(temp_";
  protected final String TEXT_584 = ".length() > 0) {";
  protected final String TEXT_585 = "emmitArray_";
  protected final String TEXT_586 = "[tempCount_";
  protected final String TEXT_587 = "].";
  protected final String TEXT_588 = " = temp_";
  protected final String TEXT_589 = ";";
  protected final String TEXT_590 = "emmitArray_";
  protected final String TEXT_591 = "[tempCount_";
  protected final String TEXT_592 = "].";
  protected final String TEXT_593 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_594 = ", ";
  protected final String TEXT_595 = ");";
  protected final String TEXT_596 = "emmitArray_";
  protected final String TEXT_597 = "[tempCount_";
  protected final String TEXT_598 = "].";
  protected final String TEXT_599 = " = temp_";
  protected final String TEXT_600 = ".getBytes();";
  protected final String TEXT_601 = "emmitArray_";
  protected final String TEXT_602 = "[tempCount_";
  protected final String TEXT_603 = "].";
  protected final String TEXT_604 = " = ParserUtils.parseTo_";
  protected final String TEXT_605 = "(temp_";
  protected final String TEXT_606 = ");";
  protected final String TEXT_607 = "} else {\t\t\t\t\t\t";
  protected final String TEXT_608 = "throw new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_609 = "', value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_610 = "emmitArray_";
  protected final String TEXT_611 = "[tempCount_";
  protected final String TEXT_612 = "].";
  protected final String TEXT_613 = " = ";
  protected final String TEXT_614 = ";";
  protected final String TEXT_615 = "}";
  protected final String TEXT_616 = "emmitArray_";
  protected final String TEXT_617 = "[tempCount_";
  protected final String TEXT_618 = "].";
  protected final String TEXT_619 = " = (";
  protected final String TEXT_620 = ")";
  protected final String TEXT_621 = "_";
  protected final String TEXT_622 = "_";
  protected final String TEXT_623 = "_";
  protected final String TEXT_624 = ";";
  protected final String TEXT_625 = "emmitArray_";
  protected final String TEXT_626 = "[tempCount_";
  protected final String TEXT_627 = "].";
  protected final String TEXT_628 = " = \"\"+";
  protected final String TEXT_629 = "_";
  protected final String TEXT_630 = "_";
  protected final String TEXT_631 = "_";
  protected final String TEXT_632 = ";";
  protected final String TEXT_633 = "emmitArray_";
  protected final String TEXT_634 = "[tempCount_";
  protected final String TEXT_635 = "].";
  protected final String TEXT_636 = " = (\"\"+";
  protected final String TEXT_637 = "_";
  protected final String TEXT_638 = "_";
  protected final String TEXT_639 = "_";
  protected final String TEXT_640 = ").getBytes();";
  protected final String TEXT_641 = "emmitArray_";
  protected final String TEXT_642 = "[tempCount_";
  protected final String TEXT_643 = "].";
  protected final String TEXT_644 = " = (";
  protected final String TEXT_645 = ")";
  protected final String TEXT_646 = "_";
  protected final String TEXT_647 = "_";
  protected final String TEXT_648 = "_";
  protected final String TEXT_649 = ";";
  protected final String TEXT_650 = "emmitArray_";
  protected final String TEXT_651 = "[tempCount_";
  protected final String TEXT_652 = "].";
  protected final String TEXT_653 = " = ((";
  protected final String TEXT_654 = "))0;" + NL + "if(sum_";
  protected final String TEXT_655 = "_";
  protected final String TEXT_656 = "_";
  protected final String TEXT_657 = " != null){" + NL + "\temmitArray_";
  protected final String TEXT_658 = "[tempCount_";
  protected final String TEXT_659 = "].";
  protected final String TEXT_660 = " = ((";
  protected final String TEXT_661 = "))sum_";
  protected final String TEXT_662 = "_";
  protected final String TEXT_663 = "_";
  protected final String TEXT_664 = ".doubleValue();" + NL + "}";
  protected final String TEXT_665 = "emmitArray_";
  protected final String TEXT_666 = "[tempCount_";
  protected final String TEXT_667 = "].";
  protected final String TEXT_668 = " = BigDecimal.valueOf(sum_";
  protected final String TEXT_669 = "_";
  protected final String TEXT_670 = "_";
  protected final String TEXT_671 = ");";
  protected final String TEXT_672 = "emmitArray_";
  protected final String TEXT_673 = "[tempCount_";
  protected final String TEXT_674 = "].";
  protected final String TEXT_675 = " = sum_";
  protected final String TEXT_676 = "_";
  protected final String TEXT_677 = "_";
  protected final String TEXT_678 = ";";
  protected final String TEXT_679 = "emmitArray_";
  protected final String TEXT_680 = "[tempCount_";
  protected final String TEXT_681 = "].";
  protected final String TEXT_682 = " = String.valueOf(sum_";
  protected final String TEXT_683 = "_";
  protected final String TEXT_684 = "_";
  protected final String TEXT_685 = ");";
  protected final String TEXT_686 = "emmitArray_";
  protected final String TEXT_687 = "[tempCount_";
  protected final String TEXT_688 = "].";
  protected final String TEXT_689 = " = String.valueOf(sum_";
  protected final String TEXT_690 = "_";
  protected final String TEXT_691 = "_";
  protected final String TEXT_692 = ");";
  protected final String TEXT_693 = "double avg_";
  protected final String TEXT_694 = "_";
  protected final String TEXT_695 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_696 = "_";
  protected final String TEXT_697 = "_";
  protected final String TEXT_698 = " > 0){" + NL + "\tavg_";
  protected final String TEXT_699 = "_";
  protected final String TEXT_700 = " = sum_";
  protected final String TEXT_701 = "_";
  protected final String TEXT_702 = "_";
  protected final String TEXT_703 = "/count_";
  protected final String TEXT_704 = "_";
  protected final String TEXT_705 = "_";
  protected final String TEXT_706 = ";" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_707 = "[tempCount_";
  protected final String TEXT_708 = "].";
  protected final String TEXT_709 = " = (";
  protected final String TEXT_710 = ")avg_";
  protected final String TEXT_711 = "_";
  protected final String TEXT_712 = ";";
  protected final String TEXT_713 = "double avg_";
  protected final String TEXT_714 = "_";
  protected final String TEXT_715 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_716 = "_";
  protected final String TEXT_717 = "_";
  protected final String TEXT_718 = " > 0 && sum_";
  protected final String TEXT_719 = "_";
  protected final String TEXT_720 = "_";
  protected final String TEXT_721 = " != null){" + NL + "\tavg_";
  protected final String TEXT_722 = "_";
  protected final String TEXT_723 = " = sum_";
  protected final String TEXT_724 = "_";
  protected final String TEXT_725 = "_";
  protected final String TEXT_726 = ".divide(BigDecimal.valueOf(count_";
  protected final String TEXT_727 = "_";
  protected final String TEXT_728 = "_";
  protected final String TEXT_729 = "), ";
  protected final String TEXT_730 = ", BigDecimal.ROUND_HALF_UP).doubleValue();" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_731 = "[tempCount_";
  protected final String TEXT_732 = "].";
  protected final String TEXT_733 = " = (";
  protected final String TEXT_734 = ")avg_";
  protected final String TEXT_735 = "_";
  protected final String TEXT_736 = ";";
  protected final String TEXT_737 = "BigDecimal avg_";
  protected final String TEXT_738 = "_";
  protected final String TEXT_739 = " = new BigDecimal(\"0.0\");" + NL + "if(count_";
  protected final String TEXT_740 = "_";
  protected final String TEXT_741 = "_";
  protected final String TEXT_742 = " > 0){" + NL + "\tavg_";
  protected final String TEXT_743 = "_";
  protected final String TEXT_744 = " = BigDecimal.valueOf(sum_";
  protected final String TEXT_745 = "_";
  protected final String TEXT_746 = "_";
  protected final String TEXT_747 = ").divide(BigDecimal.valueOf(count_";
  protected final String TEXT_748 = "_";
  protected final String TEXT_749 = "_";
  protected final String TEXT_750 = "), ";
  protected final String TEXT_751 = ", BigDecimal.ROUND_HALF_UP);" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_752 = "[tempCount_";
  protected final String TEXT_753 = "].";
  protected final String TEXT_754 = " = avg_";
  protected final String TEXT_755 = "_";
  protected final String TEXT_756 = ";";
  protected final String TEXT_757 = "BigDecimal avg_";
  protected final String TEXT_758 = "_";
  protected final String TEXT_759 = " = new BigDecimal(\"0.0\");" + NL + "if(count_";
  protected final String TEXT_760 = "_";
  protected final String TEXT_761 = "_";
  protected final String TEXT_762 = " > 0 && sum_";
  protected final String TEXT_763 = "_";
  protected final String TEXT_764 = "_";
  protected final String TEXT_765 = " != null){" + NL + "\tavg_";
  protected final String TEXT_766 = "_";
  protected final String TEXT_767 = " = sum_";
  protected final String TEXT_768 = "_";
  protected final String TEXT_769 = "_";
  protected final String TEXT_770 = ".divide(BigDecimal.valueOf(count_";
  protected final String TEXT_771 = "_";
  protected final String TEXT_772 = "_";
  protected final String TEXT_773 = "), ";
  protected final String TEXT_774 = ", BigDecimal.ROUND_HALF_UP);" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_775 = "[tempCount_";
  protected final String TEXT_776 = "].";
  protected final String TEXT_777 = " = avg_";
  protected final String TEXT_778 = "_";
  protected final String TEXT_779 = ";";
  protected final String TEXT_780 = "double avg_";
  protected final String TEXT_781 = "_";
  protected final String TEXT_782 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_783 = "_";
  protected final String TEXT_784 = "_";
  protected final String TEXT_785 = " > 0){" + NL + "\tavg_";
  protected final String TEXT_786 = "_";
  protected final String TEXT_787 = " = sum_";
  protected final String TEXT_788 = "_";
  protected final String TEXT_789 = "_";
  protected final String TEXT_790 = "/count_";
  protected final String TEXT_791 = "_";
  protected final String TEXT_792 = "_";
  protected final String TEXT_793 = ";" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_794 = "[tempCount_";
  protected final String TEXT_795 = "].";
  protected final String TEXT_796 = " = String.valueOf(avg_";
  protected final String TEXT_797 = "_";
  protected final String TEXT_798 = ");";
  protected final String TEXT_799 = "double avg_";
  protected final String TEXT_800 = "_";
  protected final String TEXT_801 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_802 = "_";
  protected final String TEXT_803 = "_";
  protected final String TEXT_804 = " > 0 && sum_";
  protected final String TEXT_805 = "_";
  protected final String TEXT_806 = "_";
  protected final String TEXT_807 = " != null){" + NL + "\tavg_";
  protected final String TEXT_808 = "_";
  protected final String TEXT_809 = " = sum_";
  protected final String TEXT_810 = "_";
  protected final String TEXT_811 = "_";
  protected final String TEXT_812 = ".divide(BigDecimal.valueOf(count_";
  protected final String TEXT_813 = "_";
  protected final String TEXT_814 = "_";
  protected final String TEXT_815 = "), ";
  protected final String TEXT_816 = ", BigDecimal.ROUND_HALF_UP).doubleValue();" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_817 = "[tempCount_";
  protected final String TEXT_818 = "].";
  protected final String TEXT_819 = " = String.valueOf(avg_";
  protected final String TEXT_820 = "_";
  protected final String TEXT_821 = ");";
  protected final String TEXT_822 = NL + "if(true){" + NL + "\tthrow new Exception(\"In column ";
  protected final String TEXT_823 = ", the data type \\\"";
  protected final String TEXT_824 = "\\\" is not applicable for \\\"avg\\\" result.\");" + NL + "}";
  protected final String TEXT_825 = "emmitArray_";
  protected final String TEXT_826 = "[tempCount_";
  protected final String TEXT_827 = "].";
  protected final String TEXT_828 = " = (";
  protected final String TEXT_829 = ")set_";
  protected final String TEXT_830 = "_";
  protected final String TEXT_831 = "_";
  protected final String TEXT_832 = ".size();";
  protected final String TEXT_833 = "emmitArray_";
  protected final String TEXT_834 = "[tempCount_";
  protected final String TEXT_835 = "].";
  protected final String TEXT_836 = " = BigDecimal.valueOf(set_";
  protected final String TEXT_837 = "_";
  protected final String TEXT_838 = "_";
  protected final String TEXT_839 = ".size());";
  protected final String TEXT_840 = "emmitArray_";
  protected final String TEXT_841 = "[tempCount_";
  protected final String TEXT_842 = "].";
  protected final String TEXT_843 = " = \"\"+set_";
  protected final String TEXT_844 = "_";
  protected final String TEXT_845 = "_";
  protected final String TEXT_846 = ".size();";
  protected final String TEXT_847 = "emmitArray_";
  protected final String TEXT_848 = "[tempCount_";
  protected final String TEXT_849 = "].";
  protected final String TEXT_850 = " = (\"\"+set_";
  protected final String TEXT_851 = "_";
  protected final String TEXT_852 = "_";
  protected final String TEXT_853 = ".size()).getBytes();";
  protected final String TEXT_854 = "emmitArray_";
  protected final String TEXT_855 = "[tempCount_";
  protected final String TEXT_856 = "].";
  protected final String TEXT_857 = " = ";
  protected final String TEXT_858 = "_";
  protected final String TEXT_859 = "_";
  protected final String TEXT_860 = "_";
  protected final String TEXT_861 = ".toString();";
  protected final String TEXT_862 = "emmitArray_";
  protected final String TEXT_863 = "[tempCount_";
  protected final String TEXT_864 = "].";
  protected final String TEXT_865 = " = ";
  protected final String TEXT_866 = "_";
  protected final String TEXT_867 = "_";
  protected final String TEXT_868 = "_";
  protected final String TEXT_869 = ";";
  protected final String TEXT_870 = "emmitArray_";
  protected final String TEXT_871 = "[tempCount_";
  protected final String TEXT_872 = "].";
  protected final String TEXT_873 = " = ";
  protected final String TEXT_874 = "_";
  protected final String TEXT_875 = "_";
  protected final String TEXT_876 = "_";
  protected final String TEXT_877 = ";";
  protected final String TEXT_878 = "emmitArray_";
  protected final String TEXT_879 = "[tempCount_";
  protected final String TEXT_880 = "].";
  protected final String TEXT_881 = " = ";
  protected final String TEXT_882 = "_";
  protected final String TEXT_883 = "_";
  protected final String TEXT_884 = "_";
  protected final String TEXT_885 = ".toString();";
  protected final String TEXT_886 = "if(true){" + NL + "\tthrow new Exception(\"In column ";
  protected final String TEXT_887 = ", data type \\\"List\\\" is not applicable for aggregate function \\\"list\\\" result. Please try aggregate function \\\"list(object)\\\"!\");" + NL + "}";
  protected final String TEXT_888 = "if(true){" + NL + "\tthrow new Exception(\"In column ";
  protected final String TEXT_889 = ", the data type \\\"";
  protected final String TEXT_890 = "\\\" is not applicable for \\\"list\\\" result.\");" + NL + "}";
  protected final String TEXT_891 = "group_";
  protected final String TEXT_892 = "_";
  protected final String TEXT_893 = " = ";
  protected final String TEXT_894 = ".";
  protected final String TEXT_895 = ";";
  protected final String TEXT_896 = "_";
  protected final String TEXT_897 = "_";
  protected final String TEXT_898 = "_";
  protected final String TEXT_899 = " = ";
  protected final String TEXT_900 = ".";
  protected final String TEXT_901 = ";";
  protected final String TEXT_902 = "\tif(";
  protected final String TEXT_903 = ".";
  protected final String TEXT_904 = " != null){";
  protected final String TEXT_905 = "count_";
  protected final String TEXT_906 = "_";
  protected final String TEXT_907 = "_";
  protected final String TEXT_908 = " = 1;";
  protected final String TEXT_909 = "\t}else{" + NL + "count_";
  protected final String TEXT_910 = "_";
  protected final String TEXT_911 = "_";
  protected final String TEXT_912 = " = 0;" + NL + "}";
  protected final String TEXT_913 = "\tif(";
  protected final String TEXT_914 = ".";
  protected final String TEXT_915 = " != null){";
  protected final String TEXT_916 = NL + "\t\t";
  protected final String TEXT_917 = "_";
  protected final String TEXT_918 = "_";
  protected final String TEXT_919 = "_";
  protected final String TEXT_920 = " = (double)";
  protected final String TEXT_921 = ".";
  protected final String TEXT_922 = ";";
  protected final String TEXT_923 = "\t}else{" + NL + "\t\t";
  protected final String TEXT_924 = "_";
  protected final String TEXT_925 = "_";
  protected final String TEXT_926 = "_";
  protected final String TEXT_927 = " = (double)0;" + NL + "\t}";
  protected final String TEXT_928 = "\tif(";
  protected final String TEXT_929 = ".";
  protected final String TEXT_930 = " != null){";
  protected final String TEXT_931 = NL + "\t\t";
  protected final String TEXT_932 = "_";
  protected final String TEXT_933 = "_";
  protected final String TEXT_934 = "_";
  protected final String TEXT_935 = " = ";
  protected final String TEXT_936 = ".";
  protected final String TEXT_937 = ";";
  protected final String TEXT_938 = "\t}else{" + NL + "\t\t";
  protected final String TEXT_939 = "_";
  protected final String TEXT_940 = "_";
  protected final String TEXT_941 = "_";
  protected final String TEXT_942 = " = new BigDecimal(\"0.0\");" + NL + "\t}";
  protected final String TEXT_943 = NL + "if(";
  protected final String TEXT_944 = ".";
  protected final String TEXT_945 = " != null){" + NL + "\tsum_";
  protected final String TEXT_946 = "_";
  protected final String TEXT_947 = "_";
  protected final String TEXT_948 = " = (double)";
  protected final String TEXT_949 = ".";
  protected final String TEXT_950 = ";" + NL + "\tcount_";
  protected final String TEXT_951 = "_";
  protected final String TEXT_952 = "_";
  protected final String TEXT_953 = " = 1;" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_954 = "_";
  protected final String TEXT_955 = "_";
  protected final String TEXT_956 = " = (double)0;" + NL + "\tcount_";
  protected final String TEXT_957 = "_";
  protected final String TEXT_958 = "_";
  protected final String TEXT_959 = " = 0;" + NL + "}";
  protected final String TEXT_960 = NL + "if(";
  protected final String TEXT_961 = ".";
  protected final String TEXT_962 = " != null){" + NL + "\tsum_";
  protected final String TEXT_963 = "_";
  protected final String TEXT_964 = "_";
  protected final String TEXT_965 = " = (double)";
  protected final String TEXT_966 = ".";
  protected final String TEXT_967 = ";" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_968 = "_";
  protected final String TEXT_969 = "_";
  protected final String TEXT_970 = " = (double)0;" + NL + "}" + NL + "count_";
  protected final String TEXT_971 = "_";
  protected final String TEXT_972 = "_";
  protected final String TEXT_973 = " = 1;";
  protected final String TEXT_974 = NL + "sum_";
  protected final String TEXT_975 = "_";
  protected final String TEXT_976 = "_";
  protected final String TEXT_977 = " = (double)";
  protected final String TEXT_978 = ".";
  protected final String TEXT_979 = ";" + NL + "count_";
  protected final String TEXT_980 = "_";
  protected final String TEXT_981 = "_";
  protected final String TEXT_982 = " = 1;";
  protected final String TEXT_983 = "\t" + NL + "if(";
  protected final String TEXT_984 = ".";
  protected final String TEXT_985 = " != null){" + NL + "\tsum_";
  protected final String TEXT_986 = "_";
  protected final String TEXT_987 = "_";
  protected final String TEXT_988 = " = ";
  protected final String TEXT_989 = ".";
  protected final String TEXT_990 = ";" + NL + "\tcount_";
  protected final String TEXT_991 = "_";
  protected final String TEXT_992 = "_";
  protected final String TEXT_993 = " = 1;" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_994 = "_";
  protected final String TEXT_995 = "_";
  protected final String TEXT_996 = " = new BigDecimal(\"0.0\");" + NL + "\tcount_";
  protected final String TEXT_997 = "_";
  protected final String TEXT_998 = "_";
  protected final String TEXT_999 = " = 0;" + NL + "}";
  protected final String TEXT_1000 = NL + "if(";
  protected final String TEXT_1001 = ".";
  protected final String TEXT_1002 = " != null){" + NL + "\tsum_";
  protected final String TEXT_1003 = "_";
  protected final String TEXT_1004 = "_";
  protected final String TEXT_1005 = " = ";
  protected final String TEXT_1006 = ".";
  protected final String TEXT_1007 = ";" + NL + "}else{" + NL + "\tsum_";
  protected final String TEXT_1008 = "_";
  protected final String TEXT_1009 = "_";
  protected final String TEXT_1010 = " = new BigDecimal(\"0.0\");" + NL + "}" + NL + "count_";
  protected final String TEXT_1011 = "_";
  protected final String TEXT_1012 = "_";
  protected final String TEXT_1013 = " = 1;";
  protected final String TEXT_1014 = "set_";
  protected final String TEXT_1015 = "_";
  protected final String TEXT_1016 = "_";
  protected final String TEXT_1017 = " = new java.util.HashSet();";
  protected final String TEXT_1018 = "\tif(";
  protected final String TEXT_1019 = ".";
  protected final String TEXT_1020 = " != null){";
  protected final String TEXT_1021 = NL + "set_";
  protected final String TEXT_1022 = "_";
  protected final String TEXT_1023 = "_";
  protected final String TEXT_1024 = ".add(";
  protected final String TEXT_1025 = ".";
  protected final String TEXT_1026 = ");";
  protected final String TEXT_1027 = "\t}";
  protected final String TEXT_1028 = "list_object_";
  protected final String TEXT_1029 = "_";
  protected final String TEXT_1030 = "_";
  protected final String TEXT_1031 = " = new java.util.ArrayList();";
  protected final String TEXT_1032 = "\tif(";
  protected final String TEXT_1033 = ".";
  protected final String TEXT_1034 = " != null){";
  protected final String TEXT_1035 = NL + "\t\tlist_object_";
  protected final String TEXT_1036 = "_";
  protected final String TEXT_1037 = "_";
  protected final String TEXT_1038 = ".add(";
  protected final String TEXT_1039 = ".";
  protected final String TEXT_1040 = ");";
  protected final String TEXT_1041 = "\t}";
  protected final String TEXT_1042 = "list_";
  protected final String TEXT_1043 = "_";
  protected final String TEXT_1044 = "_";
  protected final String TEXT_1045 = " = new StringBuilder();";
  protected final String TEXT_1046 = "\tif(";
  protected final String TEXT_1047 = ".";
  protected final String TEXT_1048 = " != null){";
  protected final String TEXT_1049 = NL + "\t\tlist_";
  protected final String TEXT_1050 = "_";
  protected final String TEXT_1051 = "_";
  protected final String TEXT_1052 = ".append(";
  protected final String TEXT_1053 = ".";
  protected final String TEXT_1054 = ");";
  protected final String TEXT_1055 = "\t}";
  protected final String TEXT_1056 = NL + "}";
  protected final String TEXT_1057 = NL + "if( currentRowIndex_";
  protected final String TEXT_1058 = "  == ";
  protected final String TEXT_1059 = " ){" + NL + "\ttempCount_";
  protected final String TEXT_1060 = "++;";
  protected final String TEXT_1061 = "emmitArray_";
  protected final String TEXT_1062 = "[tempCount_";
  protected final String TEXT_1063 = "].";
  protected final String TEXT_1064 = " = group_";
  protected final String TEXT_1065 = "_";
  protected final String TEXT_1066 = ";";
  protected final String TEXT_1067 = "String temp_";
  protected final String TEXT_1068 = " = \"\";";
  protected final String TEXT_1069 = "temp_";
  protected final String TEXT_1070 = " = new String(group_";
  protected final String TEXT_1071 = "_";
  protected final String TEXT_1072 = ");";
  protected final String TEXT_1073 = "temp_";
  protected final String TEXT_1074 = " = \"\"+group_";
  protected final String TEXT_1075 = "_";
  protected final String TEXT_1076 = ";";
  protected final String TEXT_1077 = "if(temp_";
  protected final String TEXT_1078 = ".length() > 0) {";
  protected final String TEXT_1079 = "emmitArray_";
  protected final String TEXT_1080 = "[tempCount_";
  protected final String TEXT_1081 = "].";
  protected final String TEXT_1082 = " = temp_";
  protected final String TEXT_1083 = ";";
  protected final String TEXT_1084 = "emmitArray_";
  protected final String TEXT_1085 = "[tempCount_";
  protected final String TEXT_1086 = "].";
  protected final String TEXT_1087 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_1088 = ", ";
  protected final String TEXT_1089 = ");";
  protected final String TEXT_1090 = "emmitArray_";
  protected final String TEXT_1091 = "[tempCount_";
  protected final String TEXT_1092 = "].";
  protected final String TEXT_1093 = " = ParserUtils.parseTo_";
  protected final String TEXT_1094 = "(temp_";
  protected final String TEXT_1095 = ");";
  protected final String TEXT_1096 = "} else {\t\t\t\t\t\t";
  protected final String TEXT_1097 = "throw new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_1098 = "' in '";
  protected final String TEXT_1099 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_1100 = "emmitArray_";
  protected final String TEXT_1101 = "[tempCount_";
  protected final String TEXT_1102 = "].";
  protected final String TEXT_1103 = " = ";
  protected final String TEXT_1104 = ";";
  protected final String TEXT_1105 = "}";
  protected final String TEXT_1106 = "emmitArray_";
  protected final String TEXT_1107 = "[tempCount_";
  protected final String TEXT_1108 = "].";
  protected final String TEXT_1109 = " = ";
  protected final String TEXT_1110 = "_";
  protected final String TEXT_1111 = "_";
  protected final String TEXT_1112 = "_";
  protected final String TEXT_1113 = ";";
  protected final String TEXT_1114 = "emmitArray_";
  protected final String TEXT_1115 = "[tempCount_";
  protected final String TEXT_1116 = "].";
  protected final String TEXT_1117 = " = (";
  protected final String TEXT_1118 = ")";
  protected final String TEXT_1119 = "_";
  protected final String TEXT_1120 = "_";
  protected final String TEXT_1121 = "_";
  protected final String TEXT_1122 = ";";
  protected final String TEXT_1123 = "String temp_";
  protected final String TEXT_1124 = " = \"\";";
  protected final String TEXT_1125 = "temp_";
  protected final String TEXT_1126 = " = new String(";
  protected final String TEXT_1127 = "_";
  protected final String TEXT_1128 = "_";
  protected final String TEXT_1129 = "_";
  protected final String TEXT_1130 = ");";
  protected final String TEXT_1131 = "temp_";
  protected final String TEXT_1132 = " = \"\"+";
  protected final String TEXT_1133 = "_";
  protected final String TEXT_1134 = "_";
  protected final String TEXT_1135 = "_";
  protected final String TEXT_1136 = ";";
  protected final String TEXT_1137 = "if(temp_";
  protected final String TEXT_1138 = ".length() > 0) {";
  protected final String TEXT_1139 = "emmitArray_";
  protected final String TEXT_1140 = "[tempCount_";
  protected final String TEXT_1141 = "].";
  protected final String TEXT_1142 = " = temp_";
  protected final String TEXT_1143 = ";";
  protected final String TEXT_1144 = "emmitArray_";
  protected final String TEXT_1145 = "[tempCount_";
  protected final String TEXT_1146 = "].";
  protected final String TEXT_1147 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_1148 = ", ";
  protected final String TEXT_1149 = ");";
  protected final String TEXT_1150 = "emmitArray_";
  protected final String TEXT_1151 = "[tempCount_";
  protected final String TEXT_1152 = "].";
  protected final String TEXT_1153 = " = temp_";
  protected final String TEXT_1154 = ".getBytes();";
  protected final String TEXT_1155 = "emmitArray_";
  protected final String TEXT_1156 = "[tempCount_";
  protected final String TEXT_1157 = "].";
  protected final String TEXT_1158 = " = ParserUtils.parseTo_";
  protected final String TEXT_1159 = "(temp_";
  protected final String TEXT_1160 = ");";
  protected final String TEXT_1161 = "} else {\t\t\t\t\t\t";
  protected final String TEXT_1162 = "throw new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_1163 = "' in '";
  protected final String TEXT_1164 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_1165 = "emmitArray_";
  protected final String TEXT_1166 = "[tempCount_";
  protected final String TEXT_1167 = "].";
  protected final String TEXT_1168 = " = ";
  protected final String TEXT_1169 = ";";
  protected final String TEXT_1170 = "}";
  protected final String TEXT_1171 = "emmitArray_";
  protected final String TEXT_1172 = "[tempCount_";
  protected final String TEXT_1173 = "].";
  protected final String TEXT_1174 = " = (";
  protected final String TEXT_1175 = ")";
  protected final String TEXT_1176 = "_";
  protected final String TEXT_1177 = "_";
  protected final String TEXT_1178 = "_";
  protected final String TEXT_1179 = ";";
  protected final String TEXT_1180 = "emmitArray_";
  protected final String TEXT_1181 = "[tempCount_";
  protected final String TEXT_1182 = "].";
  protected final String TEXT_1183 = " = \"\"+";
  protected final String TEXT_1184 = "_";
  protected final String TEXT_1185 = "_";
  protected final String TEXT_1186 = "_";
  protected final String TEXT_1187 = ";";
  protected final String TEXT_1188 = "emmitArray_";
  protected final String TEXT_1189 = "[tempCount_";
  protected final String TEXT_1190 = "].";
  protected final String TEXT_1191 = " = (\"\"+";
  protected final String TEXT_1192 = "_";
  protected final String TEXT_1193 = "_";
  protected final String TEXT_1194 = "_";
  protected final String TEXT_1195 = ").getBytes();";
  protected final String TEXT_1196 = "emmitArray_";
  protected final String TEXT_1197 = "[tempCount_";
  protected final String TEXT_1198 = "].";
  protected final String TEXT_1199 = " = (";
  protected final String TEXT_1200 = ")";
  protected final String TEXT_1201 = "_";
  protected final String TEXT_1202 = "_";
  protected final String TEXT_1203 = "_";
  protected final String TEXT_1204 = ";";
  protected final String TEXT_1205 = "emmitArray_";
  protected final String TEXT_1206 = "[tempCount_";
  protected final String TEXT_1207 = "].";
  protected final String TEXT_1208 = " = ((";
  protected final String TEXT_1209 = "))0;" + NL + "if(sum_";
  protected final String TEXT_1210 = "_";
  protected final String TEXT_1211 = "_";
  protected final String TEXT_1212 = " != null){" + NL + "\temmitArray_";
  protected final String TEXT_1213 = "[tempCount_";
  protected final String TEXT_1214 = "].";
  protected final String TEXT_1215 = " = ((";
  protected final String TEXT_1216 = "))sum_";
  protected final String TEXT_1217 = "_";
  protected final String TEXT_1218 = "_";
  protected final String TEXT_1219 = ".doubleValue();" + NL + "}";
  protected final String TEXT_1220 = "emmitArray_";
  protected final String TEXT_1221 = "[tempCount_";
  protected final String TEXT_1222 = "].";
  protected final String TEXT_1223 = " = BigDecimal.valueOf(sum_";
  protected final String TEXT_1224 = "_";
  protected final String TEXT_1225 = "_";
  protected final String TEXT_1226 = ");";
  protected final String TEXT_1227 = "emmitArray_";
  protected final String TEXT_1228 = "[tempCount_";
  protected final String TEXT_1229 = "].";
  protected final String TEXT_1230 = " = sum_";
  protected final String TEXT_1231 = "_";
  protected final String TEXT_1232 = "_";
  protected final String TEXT_1233 = ";";
  protected final String TEXT_1234 = "emmitArray_";
  protected final String TEXT_1235 = "[tempCount_";
  protected final String TEXT_1236 = "].";
  protected final String TEXT_1237 = " = String.valueOf(sum_";
  protected final String TEXT_1238 = "_";
  protected final String TEXT_1239 = "_";
  protected final String TEXT_1240 = ");";
  protected final String TEXT_1241 = "emmitArray_";
  protected final String TEXT_1242 = "[tempCount_";
  protected final String TEXT_1243 = "].";
  protected final String TEXT_1244 = " = String.valueOf(sum_";
  protected final String TEXT_1245 = "_";
  protected final String TEXT_1246 = "_";
  protected final String TEXT_1247 = ");";
  protected final String TEXT_1248 = "double avg_";
  protected final String TEXT_1249 = "_";
  protected final String TEXT_1250 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_1251 = "_";
  protected final String TEXT_1252 = "_";
  protected final String TEXT_1253 = " > 0){" + NL + "\tavg_";
  protected final String TEXT_1254 = "_";
  protected final String TEXT_1255 = " = sum_";
  protected final String TEXT_1256 = "_";
  protected final String TEXT_1257 = "_";
  protected final String TEXT_1258 = "/count_";
  protected final String TEXT_1259 = "_";
  protected final String TEXT_1260 = "_";
  protected final String TEXT_1261 = ";" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_1262 = "[tempCount_";
  protected final String TEXT_1263 = "].";
  protected final String TEXT_1264 = " = (";
  protected final String TEXT_1265 = ")avg_";
  protected final String TEXT_1266 = "_";
  protected final String TEXT_1267 = ";";
  protected final String TEXT_1268 = "double avg_";
  protected final String TEXT_1269 = "_";
  protected final String TEXT_1270 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_1271 = "_";
  protected final String TEXT_1272 = "_";
  protected final String TEXT_1273 = " > 0 && sum_";
  protected final String TEXT_1274 = "_";
  protected final String TEXT_1275 = "_";
  protected final String TEXT_1276 = " != null){" + NL + "\tavg_";
  protected final String TEXT_1277 = "_";
  protected final String TEXT_1278 = " = sum_";
  protected final String TEXT_1279 = "_";
  protected final String TEXT_1280 = "_";
  protected final String TEXT_1281 = ".divide(BigDecimal.valueOf(count_";
  protected final String TEXT_1282 = "_";
  protected final String TEXT_1283 = "_";
  protected final String TEXT_1284 = "), ";
  protected final String TEXT_1285 = ", BigDecimal.ROUND_HALF_UP).doubleValue();" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_1286 = "[tempCount_";
  protected final String TEXT_1287 = "].";
  protected final String TEXT_1288 = " = (";
  protected final String TEXT_1289 = ")avg_";
  protected final String TEXT_1290 = "_";
  protected final String TEXT_1291 = ";";
  protected final String TEXT_1292 = "BigDecimal avg_";
  protected final String TEXT_1293 = "_";
  protected final String TEXT_1294 = " = new BigDecimal(\"0.0\");" + NL + "if(count_";
  protected final String TEXT_1295 = "_";
  protected final String TEXT_1296 = "_";
  protected final String TEXT_1297 = " > 0){" + NL + "\tavg_";
  protected final String TEXT_1298 = "_";
  protected final String TEXT_1299 = " = BigDecimal.valueOf(sum_";
  protected final String TEXT_1300 = "_";
  protected final String TEXT_1301 = "_";
  protected final String TEXT_1302 = ").divide(BigDecimal.valueOf(count_";
  protected final String TEXT_1303 = "_";
  protected final String TEXT_1304 = "_";
  protected final String TEXT_1305 = "), ";
  protected final String TEXT_1306 = ", BigDecimal.ROUND_HALF_UP);" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_1307 = "[tempCount_";
  protected final String TEXT_1308 = "].";
  protected final String TEXT_1309 = " = avg_";
  protected final String TEXT_1310 = "_";
  protected final String TEXT_1311 = ";";
  protected final String TEXT_1312 = "BigDecimal avg_";
  protected final String TEXT_1313 = "_";
  protected final String TEXT_1314 = " = new BigDecimal(\"0.0\");" + NL + "if(count_";
  protected final String TEXT_1315 = "_";
  protected final String TEXT_1316 = "_";
  protected final String TEXT_1317 = " > 0 && sum_";
  protected final String TEXT_1318 = "_";
  protected final String TEXT_1319 = "_";
  protected final String TEXT_1320 = " != null){" + NL + "\tavg_";
  protected final String TEXT_1321 = "_";
  protected final String TEXT_1322 = " = sum_";
  protected final String TEXT_1323 = "_";
  protected final String TEXT_1324 = "_";
  protected final String TEXT_1325 = ".divide(BigDecimal.valueOf(count_";
  protected final String TEXT_1326 = "_";
  protected final String TEXT_1327 = "_";
  protected final String TEXT_1328 = "), ";
  protected final String TEXT_1329 = ", BigDecimal.ROUND_HALF_UP);" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_1330 = "[tempCount_";
  protected final String TEXT_1331 = "].";
  protected final String TEXT_1332 = " = avg_";
  protected final String TEXT_1333 = "_";
  protected final String TEXT_1334 = ";";
  protected final String TEXT_1335 = "double avg_";
  protected final String TEXT_1336 = "_";
  protected final String TEXT_1337 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_1338 = "_";
  protected final String TEXT_1339 = "_";
  protected final String TEXT_1340 = " > 0){" + NL + "\tavg_";
  protected final String TEXT_1341 = "_";
  protected final String TEXT_1342 = " = sum_";
  protected final String TEXT_1343 = "_";
  protected final String TEXT_1344 = "_";
  protected final String TEXT_1345 = "/count_";
  protected final String TEXT_1346 = "_";
  protected final String TEXT_1347 = "_";
  protected final String TEXT_1348 = ";" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_1349 = "[tempCount_";
  protected final String TEXT_1350 = "].";
  protected final String TEXT_1351 = " = String.valueOf(avg_";
  protected final String TEXT_1352 = "_";
  protected final String TEXT_1353 = ");";
  protected final String TEXT_1354 = "double avg_";
  protected final String TEXT_1355 = "_";
  protected final String TEXT_1356 = " = 0d;" + NL + "if(count_";
  protected final String TEXT_1357 = "_";
  protected final String TEXT_1358 = "_";
  protected final String TEXT_1359 = " > 0 && sum_";
  protected final String TEXT_1360 = "_";
  protected final String TEXT_1361 = "_";
  protected final String TEXT_1362 = " != null){" + NL + "\tavg_";
  protected final String TEXT_1363 = "_";
  protected final String TEXT_1364 = " = sum_";
  protected final String TEXT_1365 = "_";
  protected final String TEXT_1366 = "_";
  protected final String TEXT_1367 = ".divide(BigDecimal.valueOf(count_";
  protected final String TEXT_1368 = "_";
  protected final String TEXT_1369 = "_";
  protected final String TEXT_1370 = "), ";
  protected final String TEXT_1371 = ", BigDecimal.ROUND_HALF_UP).doubleValue();" + NL + "}" + NL + "emmitArray_";
  protected final String TEXT_1372 = "[tempCount_";
  protected final String TEXT_1373 = "].";
  protected final String TEXT_1374 = " = String.valueOf(avg_";
  protected final String TEXT_1375 = "_";
  protected final String TEXT_1376 = ");";
  protected final String TEXT_1377 = "if(true){" + NL + "\tthrow new Exception(\"In column ";
  protected final String TEXT_1378 = ", the data type \\\"";
  protected final String TEXT_1379 = "\\\" is not applicable for \\\"avg\\\" result.\");" + NL + "}";
  protected final String TEXT_1380 = "emmitArray_";
  protected final String TEXT_1381 = "[tempCount_";
  protected final String TEXT_1382 = "].";
  protected final String TEXT_1383 = " = (";
  protected final String TEXT_1384 = ")set_";
  protected final String TEXT_1385 = "_";
  protected final String TEXT_1386 = "_";
  protected final String TEXT_1387 = ".size();";
  protected final String TEXT_1388 = "emmitArray_";
  protected final String TEXT_1389 = "[tempCount_";
  protected final String TEXT_1390 = "].";
  protected final String TEXT_1391 = " = BigDecimal.valueOf(set_";
  protected final String TEXT_1392 = "_";
  protected final String TEXT_1393 = "_";
  protected final String TEXT_1394 = ".size());";
  protected final String TEXT_1395 = "emmitArray_";
  protected final String TEXT_1396 = "[tempCount_";
  protected final String TEXT_1397 = "].";
  protected final String TEXT_1398 = " = \"\"+set_";
  protected final String TEXT_1399 = "_";
  protected final String TEXT_1400 = "_";
  protected final String TEXT_1401 = ".size();";
  protected final String TEXT_1402 = "emmitArray_";
  protected final String TEXT_1403 = "[tempCount_";
  protected final String TEXT_1404 = "].";
  protected final String TEXT_1405 = " = (\"\"+set_";
  protected final String TEXT_1406 = "_";
  protected final String TEXT_1407 = "_";
  protected final String TEXT_1408 = ".size()).getBytes();";
  protected final String TEXT_1409 = "emmitArray_";
  protected final String TEXT_1410 = "[tempCount_";
  protected final String TEXT_1411 = "].";
  protected final String TEXT_1412 = " = ";
  protected final String TEXT_1413 = "_";
  protected final String TEXT_1414 = "_";
  protected final String TEXT_1415 = "_";
  protected final String TEXT_1416 = ".toString();";
  protected final String TEXT_1417 = "emmitArray_";
  protected final String TEXT_1418 = "[tempCount_";
  protected final String TEXT_1419 = "].";
  protected final String TEXT_1420 = " = ";
  protected final String TEXT_1421 = "_";
  protected final String TEXT_1422 = "_";
  protected final String TEXT_1423 = "_";
  protected final String TEXT_1424 = ";";
  protected final String TEXT_1425 = "emmitArray_";
  protected final String TEXT_1426 = "[tempCount_";
  protected final String TEXT_1427 = "].";
  protected final String TEXT_1428 = " = ";
  protected final String TEXT_1429 = "_";
  protected final String TEXT_1430 = "_";
  protected final String TEXT_1431 = "_";
  protected final String TEXT_1432 = ";";
  protected final String TEXT_1433 = "emmitArray_";
  protected final String TEXT_1434 = "[tempCount_";
  protected final String TEXT_1435 = "].";
  protected final String TEXT_1436 = " = ";
  protected final String TEXT_1437 = "_";
  protected final String TEXT_1438 = "_";
  protected final String TEXT_1439 = "_";
  protected final String TEXT_1440 = ".toString();";
  protected final String TEXT_1441 = "if(true){" + NL + "\tthrow new Exception(\"In column ";
  protected final String TEXT_1442 = ", data type \\\"List\\\" is not applicable for aggregate function \\\"list\\\" result. Please try aggregate function \\\"list(object)\\\"!\");" + NL + "}";
  protected final String TEXT_1443 = "if(true){" + NL + "\tthrow new Exception(\"In column ";
  protected final String TEXT_1444 = ", the data type \\\"";
  protected final String TEXT_1445 = "\\\" is not applicable for \\\"list\\\" result.\");" + NL + "}";
  protected final String TEXT_1446 = NL + "}" + NL + "for(int i_";
  protected final String TEXT_1447 = "=0; i_";
  protected final String TEXT_1448 = " <= tempCount_";
  protected final String TEXT_1449 = "; i_";
  protected final String TEXT_1450 = "++){";
  protected final String TEXT_1451 = ".";
  protected final String TEXT_1452 = " = emmitArray_";
  protected final String TEXT_1453 = "[i_";
  protected final String TEXT_1454 = "].";
  protected final String TEXT_1455 = ";    \t\t\t\t";
  protected final String TEXT_1456 = "nb_line_";
  protected final String TEXT_1457 = "++;";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    List< ? extends IConnection> conns = node.getIncomingConnections();
    IMetadataTable inMetadata = null;
    String connName = "";
    if(conns != null){
    for (IConnection conn : conns) { 
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 
			inMetadata = conn.getMetadataTable();
			connName = conn.getName();
    		break;
		}
	}
    if (metadata != null && inMetadata != null) { 
		List<IMetadataColumn> columns = metadata.getListColumns();
    	List<IMetadataColumn> inColumns = inMetadata.getListColumns();
		List<Map<String, String>> operations = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__OPERATIONS__");
		IMetadataColumn[][] column_op = new IMetadataColumn[operations.size()][2];
		String[] functions = new String[operations.size()];
		boolean[] needTestForNull = new boolean[operations.size()];
		List<Map<String, String>> groupbys = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUPBYS__");
		String rowCount = ElementParameterParser.getValue(node,"__ROW_COUNT__");
		IMetadataColumn[][] column_gr = new IMetadataColumn[groupbys.size()][2];
	    for(int i = 0; i < column_gr.length; i++){
			Map<String, String> groupby = groupbys.get(i);
			String in = groupby.get("INPUT_COLUMN");
			String out = groupby.get("OUTPUT_COLUMN");
			for (IMetadataColumn column: inColumns) {
				if(column.getLabel().equals(in)){
					column_gr[i][0] = column;
					break;
				}
			}
    		for (IMetadataColumn column: columns) {
				if(column.getLabel().equals(out)){
					column_gr[i][1] = column;
					break;
				}
			}
		}
		for(int i = 0; i < column_op.length; i++){
			Map<String, String> operation = operations.get(i);
			String in = operation.get("INPUT_COLUMN");
			String out = operation.get("OUTPUT_COLUMN");
			functions[i] = operation.get("FUNCTION");
			for (IMetadataColumn column: inColumns) {
				if(column.getLabel().equals(in)){
					column_op[i][0] = column;
					JavaType inputJavaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					needTestForNull[i] = !(JavaTypesManager.isJavaPrimitiveType(inputJavaType, column.isNullable())) && (operation.get("IGNORE_NULL").equals("true"));
					break;
				}
			}
    		for (IMetadataColumn column: columns) {
				if(column.getLabel().equals(out)){
					column_op[i][1] = column;
					break;
				}
			}
		}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
if(column_gr.length > 0){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
	for(int i = 0; i < column_gr.length; i++){

    stringBuffer.append(TEXT_9);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_13);
    	}
	for(int i = 0; i < column_op.length; i++){//__HSS_tAR_001
		boolean duplicate = false;
		for(int j = 0; j < i; j++){
			if(functions[j].equals(functions[i]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
				duplicate = true;
				break;
			}
		}
		if(duplicate){
			continue;
		}
		
		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
		if(("min").equals(functions[i]) || ("max").equals(functions[i]) || ("first").equals(functions[i]) || ("last").equals(functions[i])){ 

    stringBuffer.append(TEXT_14);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_20);
    
		}else if(("count").equals(functions[i])){
			boolean countHasAvg = false;
			for(int j = 0; j < functions.length; j++){
				if(("avg").equals(functions[j]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
					countHasAvg = true;
					break;
				}
			}
			if(!countHasAvg){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_21);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_23);
    
				}

    stringBuffer.append(TEXT_24);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    			
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_28);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    
				}
			}
		}else if(("sum").equals(functions[i])){
			boolean sumHasAvg = false;
			for(int j = 0; j < functions.length; j++){
				if(("avg").equals(functions[j]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
					sumHasAvg = true;
					break;
				}
			}
			if(!sumHasAvg){
				if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
					|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
					|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
					|| javaType == JavaTypesManager.DOUBLE ){
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_32);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_34);
    
					}

    stringBuffer.append(TEXT_35);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_41);
    					
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_42);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    
					}
				}else if(javaType == JavaTypesManager.BIGDECIMAL){
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_47);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_49);
    
					}

    stringBuffer.append(TEXT_50);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_56);
    				
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_57);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    
					}
				}else{
//generate nothing.
				}
			}
		}else if(("avg").equals(functions[i])){
			if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
					|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
					|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
					|| javaType == JavaTypesManager.DOUBLE ){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_62);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    
				}else{
					if(column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_79);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    
					}else{

    stringBuffer.append(TEXT_93);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    
					}
				}
			}else if(javaType == JavaTypesManager.BIGDECIMAL){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_102);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_118);
    
				}else{

    stringBuffer.append(TEXT_119);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_132);
    
				}
			}else{
//generate nothing.
			}
		}else if(("distinct").equals(functions[i])){

    stringBuffer.append(TEXT_133);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_137);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_139);
    
			}

    stringBuffer.append(TEXT_140);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_145);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_146);
    
			}
		}else if(("list_object").equals(functions[i])){

    stringBuffer.append(TEXT_147);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_151);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_153);
    
			}

    stringBuffer.append(TEXT_154);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_159);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_160);
    
			}
		}else  if(("list").equals(functions[i])){

    stringBuffer.append(TEXT_161);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_165);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_167);
    
			}

    stringBuffer.append(TEXT_168);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_173);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_174);
    
			}
		}
	}//__HSS_tAR_001

    stringBuffer.append(TEXT_175);
    
if(column_gr.length > 0){//while loop

    stringBuffer.append(TEXT_176);
    	for(int i = 0; i < column_gr.length; i++){
		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column_gr[i][0].getTalendType());
		if(JavaTypesManager.isNumberType(javaType) && javaType != JavaTypesManager.BIGDECIMAL && !column_gr[i][0].isNullable()){

    stringBuffer.append(TEXT_177);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    
		} else {

    stringBuffer.append(TEXT_183);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_192);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_195);
    		}
		
		if(i+1 == column_gr.length){

    stringBuffer.append(TEXT_196);
    		}
	}

    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_198);
    
}//while loop end
	
    
	for(int i = 0; i < column_op.length; i++){//__HSS_tAR_002
		boolean duplicate = false;
		for(int j = 0; j < i; j++){
			if(functions[j].equals(functions[i]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
				duplicate = true;
				break;
			}
		}
		if(duplicate){
			continue;
		}
		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
		if(("first").equals(functions[i])){
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_199);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_201);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_204);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_207);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_209);
    
			}
		}else if(("last").equals(functions[i])){
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_210);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_211);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_212);
    
			}

    stringBuffer.append(TEXT_213);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_215);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_217);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_218);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_219);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_220);
    
			}
		}else if(("min").equals(functions[i])){
			if(javaType == JavaTypesManager.LIST || javaType == JavaTypesManager.OBJECT || javaType == JavaTypesManager.BYTE_ARRAY){
			//do nothing
			}else if(javaType == JavaTypesManager.BIGDECIMAL || javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.DATE){

    stringBuffer.append(TEXT_221);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_222);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_227);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_228);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_229);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_231);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_232);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_234);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_235);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_237);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_238);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_239);
    			}else if(javaType == JavaTypesManager.BOOLEAN){
				if(needTestForNull[i] || column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_240);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_241);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_242);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_243);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_244);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_245);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_246);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_247);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_248);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_249);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_250);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_251);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_252);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_253);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_254);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_256);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_258);
    
				}else{

    stringBuffer.append(TEXT_259);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_260);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_261);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_262);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_263);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_264);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_265);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_266);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_267);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_268);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_269);
    
				}
			}else{
				if(needTestForNull[i] || column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_270);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_271);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_272);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_273);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_275);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_276);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_277);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_278);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_279);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_280);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_281);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_283);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_284);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_286);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_287);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_288);
    
				}else{

    stringBuffer.append(TEXT_289);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_290);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_291);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_292);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_293);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_294);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_295);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_296);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_297);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_298);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_299);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_300);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_301);
    
				}
			}
		}else if(("max").equals(functions[i])){
			if(javaType == JavaTypesManager.LIST || javaType == JavaTypesManager.OBJECT || javaType == JavaTypesManager.BYTE_ARRAY){
			//do nothing
			}else if(javaType == JavaTypesManager.BIGDECIMAL || javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.DATE){

    stringBuffer.append(TEXT_302);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_303);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_304);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_305);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_308);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_309);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_310);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_311);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_312);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_313);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_314);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_315);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_316);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_317);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_318);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_319);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_320);
    			}else if(javaType == JavaTypesManager.BOOLEAN){
				if(needTestForNull[i] || column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_321);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_322);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_323);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_324);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_325);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_326);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_327);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_328);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_329);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_330);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_331);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_332);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_333);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_334);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_335);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_336);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_337);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_338);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_339);
    
				}else{

    stringBuffer.append(TEXT_340);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_341);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_342);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_343);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_344);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_345);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_346);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_347);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_348);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_349);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_350);
    
				}
			}else{
				if(needTestForNull[i] || column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_351);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_352);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_353);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_354);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_355);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_356);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_357);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_358);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_359);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_360);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_361);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_362);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_363);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_364);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_365);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_366);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_367);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_368);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_369);
    
				}else{

    stringBuffer.append(TEXT_370);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_371);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_372);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_373);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_374);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_375);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_376);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_377);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_378);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_379);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_380);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_381);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_382);
    
				}
			}
		}else if(("count").equals(functions[i])){
			boolean countHasAvg = false;
			for(int j = 0; j < functions.length; j++){
				if(("avg").equals(functions[j]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
					countHasAvg = true;
					break;
				}
			}
			if(!countHasAvg){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_383);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_384);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_385);
    
				}

    stringBuffer.append(TEXT_386);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_387);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_388);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_389);
    			
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_390);
    
				}
			}
		}else if(("sum").equals(functions[i])){
			boolean sumHasAvg = false;
			for(int j = 0; j < functions.length; j++){
				if(("avg").equals(functions[j]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
					sumHasAvg = true;
					break;
				}
			}
			if(!sumHasAvg){
				if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
					|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
					|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
					|| javaType == JavaTypesManager.DOUBLE){
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_391);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_392);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_393);
    
					}

    stringBuffer.append(TEXT_394);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_395);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_396);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_397);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_398);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_399);
    
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_400);
    
					}
				}else if(javaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_401);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_402);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_403);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_404);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_405);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_406);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_407);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_408);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_409);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_410);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_411);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_412);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_413);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_414);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_415);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_416);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_417);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_418);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_419);
    
				}
			}
		}else if(("avg").equals(functions[i])){
			if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
					|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
					|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
					|| javaType == JavaTypesManager.DOUBLE){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_420);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_421);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_422);
    
				}
				if(!needTestForNull[i] && column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_423);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_424);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_425);
    
				}

    stringBuffer.append(TEXT_426);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_427);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_428);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_429);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_430);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_431);
    				if(!needTestForNull[i] && column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_432);
    
				}

    stringBuffer.append(TEXT_433);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_434);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_435);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_436);
    
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_437);
    
				}
			}else if(javaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_438);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_439);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_440);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_441);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_442);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_443);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_444);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_445);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_446);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_447);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_448);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_449);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_450);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_451);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_452);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_453);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_454);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_455);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_456);
    
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_457);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_458);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_459);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_460);
    
				}

    stringBuffer.append(TEXT_461);
    
				if(!needTestForNull[i]){

    stringBuffer.append(TEXT_462);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_463);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_464);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_465);
    
				}
			}
		}else if(("list").equals(functions[i])){
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_466);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_467);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_468);
    
			}

    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_469);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_470);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_471);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_472);
    stringBuffer.append(TEXT_473);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_474);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_475);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_476);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_477);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_478);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_479);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_480);
    
			}
		}else if(("list_object").equals(functions[i])){
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_481);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_482);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_483);
    
			}

    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_484);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_485);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_486);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_487);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_488);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_489);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_490);
    
			}
		}else if(("distinct").equals(functions[i])){
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_491);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_492);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_493);
    
			}

    stringBuffer.append(TEXT_494);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_495);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_496);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_497);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_498);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_499);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_500);
    
			}
		}
	}//__HSS_tAR_002

if(column_gr.length > 0){

    stringBuffer.append(TEXT_501);
    
}

    stringBuffer.append(TEXT_502);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_503);
    



if(column_gr.length > 0){
	//??

    stringBuffer.append(TEXT_504);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_505);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_506);
    
	//do out start ...
	conns = null;
	conns = node.getOutgoingSortedConnections();
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 
				List<IMetadataColumn> listColumns = metadata.getListColumns();
				int sizeListColumns = listColumns.size();
				boolean flag = true;
				next_column:
				for (int valueN=0; valueN < sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
					for(int i = 0; i < column_gr.length; i++){
						if(column.getLabel().equals(column_gr[i][1].getLabel())){
							JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_gr[i][0].getTalendType());
							if(inJavaType == javaType){

    stringBuffer.append(TEXT_507);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_508);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_509);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_510);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_511);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_512);
    							}else{//for different data type
								if(flag){
									flag = false;

    stringBuffer.append(TEXT_513);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_514);
    								}
								if(inJavaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_515);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_516);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_517);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_518);
    								}else{

    stringBuffer.append(TEXT_519);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_520);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_521);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_522);
    								}

    stringBuffer.append(TEXT_523);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_524);
    								if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_525);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_526);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_527);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_528);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_529);
    								} else if(javaType == JavaTypesManager.DATE) { 

    stringBuffer.append(TEXT_530);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_531);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_532);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_533);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_534);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_535);
    								} else {

    stringBuffer.append(TEXT_536);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_537);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_538);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_539);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_540);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_541);
    								}

    stringBuffer.append(TEXT_542);
    								String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
								if(defaultValue == null) {

    stringBuffer.append(TEXT_543);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_544);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_545);
    								} else {

    stringBuffer.append(TEXT_546);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_547);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_548);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_549);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_550);
    								}

    stringBuffer.append(TEXT_551);
    							}							
							continue next_column;
						}
					}					
					for(int i = 0; i < column_op.length; i++){
						if(column.getLabel().equals(column_op[i][1].getLabel())){
							if(("min").equals(functions[i]) || ("max").equals(functions[i]) || ("first").equals(functions[i]) || ("last").equals(functions[i])){
								JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
								if(inJavaType == javaType){

    stringBuffer.append(TEXT_552);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_553);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_554);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_555);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_556);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_557);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_558);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_559);
    								}else{//for different data type
									if((javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
										|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
										|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
										|| javaType == JavaTypesManager.DOUBLE)&&(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE)){

    stringBuffer.append(TEXT_560);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_561);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_562);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_563);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_564);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_565);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_566);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_567);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_568);
    									}else{
										if(flag){
											flag = false;

    stringBuffer.append(TEXT_569);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_570);
    										}
										if(inJavaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_571);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_572);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_573);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_574);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_575);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_576);
    										}else{

    stringBuffer.append(TEXT_577);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_578);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_579);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_580);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_581);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_582);
    										}

    stringBuffer.append(TEXT_583);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_584);
    										if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_585);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_586);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_587);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_588);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_589);
    										} else if(javaType == JavaTypesManager.DATE) { 

    stringBuffer.append(TEXT_590);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_591);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_592);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_593);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_594);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_595);
    										} else if(javaType == JavaTypesManager.BYTE_ARRAY) { 

    stringBuffer.append(TEXT_596);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_597);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_598);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_599);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_600);
    										} else {

    stringBuffer.append(TEXT_601);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_602);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_603);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_604);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_605);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_606);
    										}

    stringBuffer.append(TEXT_607);
    										String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
										if(defaultValue == null) {

    stringBuffer.append(TEXT_608);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_609);
    										} else {

    stringBuffer.append(TEXT_610);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_611);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_612);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_613);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_614);
    										}

    stringBuffer.append(TEXT_615);
    									}
								}
							}else if(("count").equals(functions[i])){
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_616);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_617);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_618);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_619);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_620);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_621);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_622);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_623);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_624);
    								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_625);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_626);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_627);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_628);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_629);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_630);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_631);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_632);
    								}else if(javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_633);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_634);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_635);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_636);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_637);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_638);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_639);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_640);
    								}
							}else if(("sum").equals(functions[i])){
								JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE ){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_641);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_642);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_643);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_644);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_645);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_646);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_647);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_648);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_649);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_650);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_651);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_652);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_653);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_654);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_655);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_656);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_657);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_658);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_659);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_660);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_661);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_662);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_663);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_664);
    
									}
								}else if(javaType == JavaTypesManager.BIGDECIMAL) {
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_665);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_666);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_667);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_668);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_669);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_670);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_671);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_672);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_673);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_674);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_675);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_676);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_677);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_678);
    
									}
								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_679);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_680);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_681);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_682);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_683);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_684);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_685);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_686);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_687);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_688);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_689);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_690);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_691);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_692);
    
									}
								}else{
//generate nothing
								}
							}else if(("avg").equals(functions[i])){
								JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_693);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_694);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_695);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_696);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_697);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_698);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_699);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_700);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_701);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_702);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_703);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_704);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_705);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_706);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_707);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_708);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_709);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_710);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_711);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_712);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){
										int pre = 10;
                       		 			Integer precision = column.getPrecision();
                           		 		if(precision!=null && precision!=0) { 
                           		 			pre = precision;
                           		 		}

    stringBuffer.append(TEXT_713);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_714);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_715);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_716);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_717);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_718);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_719);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_720);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_721);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_722);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_723);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_724);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_725);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_726);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_727);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_728);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_729);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_730);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_731);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_732);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_733);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_734);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_735);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_736);
    
									}

    							}else if(javaType == JavaTypesManager.BIGDECIMAL) {
									int pre = 10;
                       		 		Integer precision = column.getPrecision();
                           		 	if(precision!=null && precision!=0) { 
                           		 		pre = precision;
                           		 	}
                           		 	if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_737);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_738);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_739);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_740);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_741);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_742);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_743);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_744);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_745);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_746);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_747);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_748);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_749);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_750);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_751);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_752);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_753);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_754);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_755);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_756);
    
                           		 	}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_757);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_758);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_759);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_760);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_761);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_762);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_763);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_764);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_765);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_766);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_767);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_768);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_769);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_770);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_771);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_772);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_773);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_774);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_775);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_776);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_777);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_778);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_779);
    
                           		 	}
								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_780);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_781);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_782);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_783);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_784);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_785);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_786);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_787);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_788);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_789);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_790);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_791);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_792);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_793);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_794);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_795);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_796);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_797);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_798);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){
										int pre = 10;
                       		 			Integer precision = column.getPrecision();
                           		 		if(precision!=null && precision!=0) { 
                           		 			pre = precision;
                           		 		}

    stringBuffer.append(TEXT_799);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_800);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_801);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_802);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_803);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_804);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_805);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_806);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_807);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_808);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_809);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_810);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_811);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_812);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_813);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_814);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_815);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_816);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_817);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_818);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_819);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_820);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_821);
    
									}
								}else{

    stringBuffer.append(TEXT_822);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_823);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_824);
    								}
							}else if(("distinct").equals(functions[i])){
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_825);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_826);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_827);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_828);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_829);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_830);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_831);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_832);
    								}else if(javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_833);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_834);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_835);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_836);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_837);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_838);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_839);
    								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_840);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_841);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_842);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_843);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_844);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_845);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_846);
    								}else if(javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_847);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_848);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_849);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_850);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_851);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_852);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_853);
    								}
							}else if(("list_object").equals(functions[i])){
								if(javaType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_854);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_855);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_856);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_857);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_858);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_859);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_860);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_861);
    
								}else if(javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_862);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_863);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_864);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_865);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_866);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_867);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_868);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_869);
    								}else if(javaType == JavaTypesManager.LIST) {

    stringBuffer.append(TEXT_870);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_871);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_872);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_873);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_874);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_875);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_876);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_877);
    								}
							}else if(("list").equals(functions[i])){
								if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_878);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_879);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_880);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_881);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_882);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_883);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_884);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_885);
    								} else if(javaType == JavaTypesManager.LIST){

    stringBuffer.append(TEXT_886);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_887);
    
								}else{

    stringBuffer.append(TEXT_888);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_889);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_890);
    								}
							}
							continue next_column;
						}
					}
				}
			}
		}
	}
	//do out end ...
	
	//do first
	for(int i = 0; i < column_gr.length; i++){

    stringBuffer.append(TEXT_891);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_892);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_893);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_894);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_895);
    	}
	for(int i = 0; i < column_op.length && column_gr.length > 0; i++){
		boolean duplicate = false;
		for(int j = 0; j < i; j++){
			if(functions[j].equals(functions[i]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
				duplicate = true;
				break;
			}
		}
		if(duplicate){
			continue;
		}
		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
		if(("min").equals(functions[i]) || ("max").equals(functions[i]) || ("first").equals(functions[i]) || ("last").equals(functions[i])){ 

    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_896);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_897);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_898);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_899);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_900);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_901);
    		}else if(("count").equals(functions[i])){
			boolean countHasAvg = false;
			for(int j = 0; j < functions.length; j++){
				if(("avg").equals(functions[j]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
					countHasAvg = true;
					break;
				}
			}
			if(!countHasAvg){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_902);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_903);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_904);
    
				}

    stringBuffer.append(TEXT_905);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_906);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_907);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_908);
    			
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_909);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_910);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_911);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_912);
    
				}
			}
		}else if(("sum").equals(functions[i])){
			boolean sumHasAvg = false;
			for(int j = 0; j < functions.length; j++){
				if(("avg").equals(functions[j]) && column_op[j][0].getLabel().equals(column_op[i][0].getLabel()) && needTestForNull[i] == needTestForNull[j]){
					sumHasAvg = true;
					break;
				}
			}
			if(!sumHasAvg){
				if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
					|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
					|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
					|| javaType == JavaTypesManager.DOUBLE ){
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_913);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_914);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_915);
    
					}

    stringBuffer.append(TEXT_916);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_917);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_918);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_919);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_920);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_921);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_922);
    					
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_923);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_924);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_925);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_926);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_927);
    
					}
				}else if(javaType == JavaTypesManager.BIGDECIMAL){
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_928);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_929);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_930);
    
					}

    stringBuffer.append(TEXT_931);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_932);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_933);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_934);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_935);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_936);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_937);
    				
					if(needTestForNull[i]){

    stringBuffer.append(TEXT_938);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_939);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_940);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_941);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_942);
    
					}
				}else{
//generate nothing.
				}
			}
		}else if(("avg").equals(functions[i])){
			if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
					|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
					|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
					|| javaType == JavaTypesManager.DOUBLE ){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_943);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_944);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_945);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_946);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_947);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_948);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_949);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_950);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_951);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_952);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_953);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_954);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_955);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_956);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_957);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_958);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_959);
    
				}else{
					if(column_op[i][0].isNullable()){

    stringBuffer.append(TEXT_960);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_961);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_962);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_963);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_964);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_965);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_966);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_967);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_968);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_969);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_970);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_971);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_972);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_973);
    
					}else{

    stringBuffer.append(TEXT_974);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_975);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_976);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_977);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_978);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_979);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_980);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_981);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_982);
    
					}
				}
			}else if(javaType == JavaTypesManager.BIGDECIMAL){
				if(needTestForNull[i]){

    stringBuffer.append(TEXT_983);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_984);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_985);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_986);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_987);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_988);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_989);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_990);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_991);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_992);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_993);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_994);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_995);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_996);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_997);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_998);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_999);
    
				}else{

    stringBuffer.append(TEXT_1000);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1001);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1002);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1003);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1004);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1005);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1006);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1007);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1008);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1009);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1010);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1011);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1012);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1013);
    
				}
			}else{
//generate nothing.
			}
		}else if(("distinct").equals(functions[i])){

    stringBuffer.append(TEXT_1014);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1015);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1016);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1017);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_1018);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1019);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1020);
    
			}

    stringBuffer.append(TEXT_1021);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1022);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1023);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1024);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1025);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1026);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_1027);
    
			}
		}else if(("list_object").equals(functions[i])){

    stringBuffer.append(TEXT_1028);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1029);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1030);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1031);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_1032);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1033);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1034);
    
			}

    stringBuffer.append(TEXT_1035);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1036);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1037);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1038);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1039);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1040);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_1041);
    
			}
		}else  if(("list").equals(functions[i])){

    stringBuffer.append(TEXT_1042);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1043);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1044);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1045);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_1046);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1047);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1048);
    
			}

    stringBuffer.append(TEXT_1049);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1050);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1051);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1052);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_1053);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1054);
    
			if(needTestForNull[i]){

    stringBuffer.append(TEXT_1055);
    
			}
		}
	}

    stringBuffer.append(TEXT_1056);
    
	//??
}
/////////////////////////////////////////////////
	//??

    stringBuffer.append(TEXT_1057);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_1058);
    stringBuffer.append(rowCount );
    stringBuffer.append(TEXT_1059);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1060);
    
	//do out start ...
	conns = null;
	conns = node.getOutgoingSortedConnections();
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 
				List<IMetadataColumn> listColumns = metadata.getListColumns();
				int sizeListColumns = listColumns.size();
				boolean flag = true;
				next_column:
				for (int valueN=0; valueN < sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
					for(int i = 0; i < column_gr.length; i++){
						if(column.getLabel().equals(column_gr[i][1].getLabel())){
							JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_gr[i][0].getTalendType());
							if(inJavaType == javaType){

    stringBuffer.append(TEXT_1061);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1062);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1063);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1064);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_1065);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1066);
    							}else{//for different data type
								if(flag){
									flag = false;

    stringBuffer.append(TEXT_1067);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1068);
    								}
								if(inJavaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_1069);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1070);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_1071);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1072);
    								}else{

    stringBuffer.append(TEXT_1073);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1074);
    stringBuffer.append(column_gr[i][0].getLabel() );
    stringBuffer.append(TEXT_1075);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1076);
    								}

    stringBuffer.append(TEXT_1077);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1078);
    								if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_1079);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1080);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1081);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1082);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1083);
    								} else if(javaType == JavaTypesManager.DATE) { 

    stringBuffer.append(TEXT_1084);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1085);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1086);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1087);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1088);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_1089);
    								} else {

    stringBuffer.append(TEXT_1090);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1091);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1092);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1093);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_1094);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1095);
    								}

    stringBuffer.append(TEXT_1096);
    								String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
								if(defaultValue == null) {

    stringBuffer.append(TEXT_1097);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_1098);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_1099);
    								} else {

    stringBuffer.append(TEXT_1100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1102);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1103);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_1104);
    								}

    stringBuffer.append(TEXT_1105);
    							}							
							continue next_column;
						}
					}					
					for(int i = 0; i < column_op.length; i++){
						if(column.getLabel().equals(column_op[i][1].getLabel())){
							if(("min").equals(functions[i]) || ("max").equals(functions[i]) || ("first").equals(functions[i]) || ("last").equals(functions[i])){ 
								JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
								if(inJavaType == javaType){

    stringBuffer.append(TEXT_1106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1108);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1109);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1110);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1111);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1113);
    								}else{//for different data type
									if((javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
										|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
										|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
										|| javaType == JavaTypesManager.DOUBLE)&&(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE)){

    stringBuffer.append(TEXT_1114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1116);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1117);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1118);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1119);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1120);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1122);
    									}else{
										if(flag){
											flag = false;

    stringBuffer.append(TEXT_1123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1124);
    										}
										if(inJavaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_1125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1126);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1127);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1128);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1129);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1130);
    										}else{

    stringBuffer.append(TEXT_1131);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1132);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1133);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1134);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1136);
    										}

    stringBuffer.append(TEXT_1137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1138);
    										if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_1139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1141);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1143);
    										} else if(javaType == JavaTypesManager.DATE) { 

    stringBuffer.append(TEXT_1144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1146);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1148);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_1149);
    										} else if(javaType == JavaTypesManager.BYTE_ARRAY) { 

    stringBuffer.append(TEXT_1150);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1152);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1154);
    										} else {

    stringBuffer.append(TEXT_1155);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1157);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1158);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_1159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1160);
    										}

    stringBuffer.append(TEXT_1161);
    										String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
										if(defaultValue == null) {

    stringBuffer.append(TEXT_1162);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_1163);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_1164);
    										} else {

    stringBuffer.append(TEXT_1165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1167);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1168);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_1169);
    										}

    stringBuffer.append(TEXT_1170);
    									}
								}
							}else if(("count").equals(functions[i])){
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1171);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1173);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1174);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1175);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1176);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1177);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1179);
    								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_1180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1182);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1183);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1184);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1185);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1187);
    								}else if(javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_1188);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1190);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1191);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1192);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1193);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1194);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1195);
    								}
							}else if(("sum").equals(functions[i])){
								JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE ){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1196);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1197);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1198);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1199);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1200);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1201);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1202);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1203);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1204);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_1205);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1206);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1207);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1208);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1209);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1210);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1211);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1212);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1213);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1214);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1215);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1216);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1217);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1218);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1219);
    
									}
								}else if(javaType == JavaTypesManager.BIGDECIMAL) {
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1220);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1221);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1222);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1223);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1224);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1225);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1226);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_1227);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1228);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1229);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1230);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1231);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1232);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1233);
    
									}
								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1234);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1235);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1236);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1237);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1238);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1239);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1240);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_1241);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1242);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1243);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1244);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1245);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1246);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1247);
    
									}
								}else{
//generate nothing...
								}
							}else if(("avg").equals(functions[i])){
								JavaType inJavaType = JavaTypesManager.getJavaTypeFromId(column_op[i][0].getTalendType());
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1248);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1249);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1250);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1251);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1252);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1253);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1254);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1255);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1256);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1257);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1258);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1259);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1260);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1261);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1262);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1263);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1264);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1265);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1266);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1267);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){
										int pre = 10;
                       		 			Integer precision = column.getPrecision();
                           		 		if(precision!=null && precision!=0) { 
                           		 			pre = precision;
                           		 		}

    stringBuffer.append(TEXT_1268);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1269);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1270);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1271);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1272);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1273);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1274);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1275);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1276);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1277);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1278);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1279);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1280);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1281);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1282);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1283);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1284);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_1285);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1286);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1287);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1288);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1289);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1290);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1291);
    
									}

    							}else if(javaType == JavaTypesManager.BIGDECIMAL) {
									int pre = 10;
                       		 		Integer precision = column.getPrecision();
                           		 	if(precision!=null && precision!=0) { 
                           		 		pre = precision;
                           		 	}
                           		 	if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1292);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1293);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1294);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1295);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1296);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1297);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1298);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1299);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1300);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1301);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1302);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1303);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1304);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1305);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_1306);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1307);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1308);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1309);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1310);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1311);
    
                           		 	}else if(inJavaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_1312);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1313);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1314);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1315);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1316);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1317);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1318);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1319);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1320);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1321);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1322);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1323);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1324);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1325);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1326);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1327);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1328);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_1329);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1330);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1331);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1332);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1333);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1334);
    
                           		 	}
								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
									if(inJavaType == JavaTypesManager.BYTE || inJavaType == JavaTypesManager.CHARACTER 
										|| inJavaType == JavaTypesManager.SHORT || inJavaType == JavaTypesManager.INTEGER 
										|| inJavaType == JavaTypesManager.LONG || inJavaType == JavaTypesManager.FLOAT 
										|| inJavaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1335);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1336);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1337);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1338);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1339);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1340);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1341);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1342);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1343);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1344);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1345);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1346);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1347);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1348);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1349);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1350);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1351);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1352);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1353);
    
									}else if(inJavaType == JavaTypesManager.BIGDECIMAL){
										int pre = 10;
                       		 			Integer precision = column.getPrecision();
                           		 		if(precision!=null && precision!=0) { 
                           		 			pre = precision;
                           		 		}

    stringBuffer.append(TEXT_1354);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1355);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1356);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1357);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1358);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1359);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1360);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1361);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1362);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1363);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1364);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1365);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1366);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1367);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1368);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1369);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1370);
    stringBuffer.append(pre );
    stringBuffer.append(TEXT_1371);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1372);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1373);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1374);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_1375);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1376);
    
									}
								}else{

    stringBuffer.append(TEXT_1377);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1378);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_1379);
    								}
							}else if(("distinct").equals(functions[i])){
								if(javaType == JavaTypesManager.BYTE || javaType == JavaTypesManager.CHARACTER 
									|| javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.INTEGER 
									|| javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.FLOAT 
									|| javaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_1380);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1381);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1382);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1383);
    stringBuffer.append(javaType.getPrimitiveClass() );
    stringBuffer.append(TEXT_1384);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1385);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1386);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1387);
    								}else if(javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_1388);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1389);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1390);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1391);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1392);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1393);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1394);
    								}else if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_1395);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1396);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1397);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1398);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1399);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1400);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1401);
    								}else if(javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_1402);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1403);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1404);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1405);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1406);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1407);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1408);
    								}
							}else if(("list_object").equals(functions[i])){
								if(javaType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_1409);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1410);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1411);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1412);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1413);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1414);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1415);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1416);
    
								}else if(javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_1417);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1418);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1419);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1420);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1421);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1422);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1423);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1424);
    								}else if(javaType == JavaTypesManager.LIST) {

    stringBuffer.append(TEXT_1425);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1426);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1427);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1428);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1429);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1430);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1431);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1432);
    								}
							}else{//"list"
								if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_1433);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1434);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1435);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1436);
    stringBuffer.append(functions[i] );
    stringBuffer.append(TEXT_1437);
    stringBuffer.append(column_op[i][0].getLabel() );
    stringBuffer.append(TEXT_1438);
    stringBuffer.append(needTestForNull[i] );
    stringBuffer.append(TEXT_1439);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1440);
    								} else if(javaType == JavaTypesManager.LIST){

    stringBuffer.append(TEXT_1441);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1442);
    
								}else{

    stringBuffer.append(TEXT_1443);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1444);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_1445);
    								}
							}
							continue next_column;
						}
					}
				}
			}
		}
	}
	//do out end ...

	//??	

    stringBuffer.append(TEXT_1446);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1447);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1448);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1449);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1450);
    
conns = null;
conns = node.getOutgoingSortedConnections();
String firstConnName = "";
if (conns!=null) {
	for (int i=0;i<conns.size();i++) {
		IConnection conn = conns.get(i);
		if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
			for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_1451);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1452);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1453);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_1454);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_1455);
    			}
		}
	}
}

    stringBuffer.append(TEXT_1456);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_1457);
    
	}
	}
}

    return stringBuffer.toString();
  }
}
