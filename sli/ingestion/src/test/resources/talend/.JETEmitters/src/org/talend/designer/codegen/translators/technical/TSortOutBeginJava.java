package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class TSortOutBeginJava
{
  protected static String nl;
  public static synchronized TSortOutBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSortOutBeginJava result = new TSortOutBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "if(true){" + NL + "\tthrow new Exception(\"Bad sort criteria: couldn't sort column \\\"";
  protected final String TEXT_3 = "\\\" as num.\");" + NL + "}";
  protected final String TEXT_4 = NL + "if(true){" + NL + "\tthrow new Exception(\"Bad sort criteria: couldn't sort column \\\"";
  protected final String TEXT_5 = "\\\" as date.\");" + NL + "}";
  protected final String TEXT_6 = NL;
  protected final String TEXT_7 = NL + "class Comparable";
  protected final String TEXT_8 = "Struct extends ";
  protected final String TEXT_9 = "Struct implements Comparable<Comparable";
  protected final String TEXT_10 = "Struct> {" + NL + "\t" + NL + "\tpublic int compareTo(Comparable";
  protected final String TEXT_11 = "Struct other) {" + NL;
  protected final String TEXT_12 = NL + "\t\tif(this.";
  protected final String TEXT_13 = " == null && other.";
  protected final String TEXT_14 = " != null){";
  protected final String TEXT_15 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_16 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_17 = NL + "\t\t}else if(this.";
  protected final String TEXT_18 = " != null && other.";
  protected final String TEXT_19 = " == null){";
  protected final String TEXT_20 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_21 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_22 = NL + "\t\t}else if(this.";
  protected final String TEXT_23 = " != null && other.";
  protected final String TEXT_24 = " != null){" + NL + "\t\t\tif(!this.";
  protected final String TEXT_25 = ".equals(other.";
  protected final String TEXT_26 = ")){";
  protected final String TEXT_27 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_28 = ".compareTo(other.";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_31 = ".compareTo(this.";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_34 = NL + "\t\tif(this.";
  protected final String TEXT_35 = " != other.";
  protected final String TEXT_36 = "){";
  protected final String TEXT_37 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_38 = " ? 1 : -1;";
  protected final String TEXT_39 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_40 = " ? -1 : 1;";
  protected final String TEXT_41 = NL + "\t\t}";
  protected final String TEXT_42 = NL + "\t\tif(this.";
  protected final String TEXT_43 = " != other.";
  protected final String TEXT_44 = "){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_45 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_46 = " > other.";
  protected final String TEXT_47 = " ? 1 : -1;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_48 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_49 = " > this.";
  protected final String TEXT_50 = " ? 1 : -1;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_51 = NL + "\t\t}";
  protected final String TEXT_52 = NL + "\t\tString thisS";
  protected final String TEXT_53 = " = this.";
  protected final String TEXT_54 = " == null ? \"null\" : new String(this.";
  protected final String TEXT_55 = ");" + NL + "\t\tString otherS";
  protected final String TEXT_56 = " = other.";
  protected final String TEXT_57 = " == null ? \"null\" : new String(other.";
  protected final String TEXT_58 = ");" + NL + "\t\tif(!thisS";
  protected final String TEXT_59 = ".equals(otherS";
  protected final String TEXT_60 = ")){";
  protected final String TEXT_61 = NL + "\t\t\treturn thisS";
  protected final String TEXT_62 = ".compareTo(otherS";
  protected final String TEXT_63 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_64 = NL + "\t\t\treturn otherS";
  protected final String TEXT_65 = ".compareTo(thisS";
  protected final String TEXT_66 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_67 = NL + "\t\t}";
  protected final String TEXT_68 = NL + "\t\t\tString thisS";
  protected final String TEXT_69 = " = this.";
  protected final String TEXT_70 = " == null ? \"null\" : FormatterUtils.format_Date(this.";
  protected final String TEXT_71 = ", ";
  protected final String TEXT_72 = ");" + NL + "\t\t\tString otherS";
  protected final String TEXT_73 = " = other.";
  protected final String TEXT_74 = " == null ? \"null\" : FormatterUtils.format_Date(other.";
  protected final String TEXT_75 = ", ";
  protected final String TEXT_76 = ");" + NL + "\t\t\tif(!thisS";
  protected final String TEXT_77 = ".equals(otherS";
  protected final String TEXT_78 = ")){";
  protected final String TEXT_79 = NL + "\t\t\t\treturn thisS";
  protected final String TEXT_80 = ".compareTo(otherS";
  protected final String TEXT_81 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_82 = NL + "\t\t\t\treturn otherS";
  protected final String TEXT_83 = ".compareTo(thisS";
  protected final String TEXT_84 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_85 = NL + "\t\t\t}";
  protected final String TEXT_86 = NL + "\t\tif(this.";
  protected final String TEXT_87 = " == null && other.";
  protected final String TEXT_88 = " != null){";
  protected final String TEXT_89 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_90 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_91 = NL + "\t\t}else if(this.";
  protected final String TEXT_92 = " != null && other.";
  protected final String TEXT_93 = " == null){";
  protected final String TEXT_94 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_95 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_96 = NL + "\t\t}else if(this.";
  protected final String TEXT_97 = " != null && other.";
  protected final String TEXT_98 = " != null){" + NL + "\t\t\tif(!this.";
  protected final String TEXT_99 = ".equals(other.";
  protected final String TEXT_100 = ")){";
  protected final String TEXT_101 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_102 = ".compareTo(other.";
  protected final String TEXT_103 = ");";
  protected final String TEXT_104 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_105 = ".compareTo(this.";
  protected final String TEXT_106 = ");";
  protected final String TEXT_107 = NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_108 = NL + "\t\tString thisS";
  protected final String TEXT_109 = " = this.";
  protected final String TEXT_110 = " == null ? \"null\" : String.valueOf(this.";
  protected final String TEXT_111 = ");" + NL + "\t\tString otherS";
  protected final String TEXT_112 = " = other.";
  protected final String TEXT_113 = " == null ? \"null\" : String.valueOf(other.";
  protected final String TEXT_114 = ");" + NL + "\t\tif(!thisS";
  protected final String TEXT_115 = ".equals(otherS";
  protected final String TEXT_116 = ")){";
  protected final String TEXT_117 = NL + "\t\t\treturn thisS";
  protected final String TEXT_118 = ".compareTo(otherS";
  protected final String TEXT_119 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_120 = NL + "\t\t\treturn otherS";
  protected final String TEXT_121 = ".compareTo(thisS";
  protected final String TEXT_122 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_123 = NL + "\t\t}";
  protected final String TEXT_124 = NL + "\t\tString thisS";
  protected final String TEXT_125 = " = String.valueOf(this.";
  protected final String TEXT_126 = ");" + NL + "\t\tString otherS";
  protected final String TEXT_127 = " = String.valueOf(other.";
  protected final String TEXT_128 = ");" + NL + "\t\tif(!thisS";
  protected final String TEXT_129 = ".equals(otherS";
  protected final String TEXT_130 = ")){";
  protected final String TEXT_131 = NL + "\t\t\treturn thisS";
  protected final String TEXT_132 = ".compareTo(otherS";
  protected final String TEXT_133 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_134 = NL + "\t\t\treturn otherS";
  protected final String TEXT_135 = ".compareTo(thisS";
  protected final String TEXT_136 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_137 = NL + "\t\t}";
  protected final String TEXT_138 = NL + "\t\tif(this.";
  protected final String TEXT_139 = " == null && other.";
  protected final String TEXT_140 = " != null){";
  protected final String TEXT_141 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_142 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_143 = NL + "\t\t}else if(this.";
  protected final String TEXT_144 = " != null && other.";
  protected final String TEXT_145 = " == null){";
  protected final String TEXT_146 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_147 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_148 = NL + "\t\t}else if(this.";
  protected final String TEXT_149 = " != null && other.";
  protected final String TEXT_150 = " != null){" + NL + "\t\t\tif(!this.";
  protected final String TEXT_151 = ".equals(other.";
  protected final String TEXT_152 = ")){";
  protected final String TEXT_153 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_154 = ".compareTo(other.";
  protected final String TEXT_155 = ");";
  protected final String TEXT_156 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_157 = ".compareTo(this.";
  protected final String TEXT_158 = ");";
  protected final String TEXT_159 = NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_160 = NL + "\t\treturn 0;" + NL + "\t}" + NL + "}" + NL + "" + NL + "java.util.List<Comparable";
  protected final String TEXT_161 = "Struct> list_";
  protected final String TEXT_162 = " = new java.util.ArrayList<Comparable";
  protected final String TEXT_163 = "Struct>();" + NL;
  protected final String TEXT_164 = NL + "////////////////////////////////////" + NL + "class ";
  protected final String TEXT_165 = "StructILightSerializable extends ";
  protected final String TEXT_166 = "Struct implements" + NL + "                        org.talend.designer.components.tsort.io.beans.ILightSerializable<";
  protected final String TEXT_167 = "StructILightSerializable> {" + NL + "" + NL + "\tpublic int compareTo(";
  protected final String TEXT_168 = "StructILightSerializable other) {" + NL;
  protected final String TEXT_169 = NL + "\t\tif(this.";
  protected final String TEXT_170 = " == null && other.";
  protected final String TEXT_171 = " != null){";
  protected final String TEXT_172 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_173 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_174 = NL + "\t\t}else if(this.";
  protected final String TEXT_175 = " != null && other.";
  protected final String TEXT_176 = " == null){";
  protected final String TEXT_177 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_178 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_179 = NL + "\t\t}else if(this.";
  protected final String TEXT_180 = " != null && other.";
  protected final String TEXT_181 = " != null){" + NL + "\t\t\tif(!this.";
  protected final String TEXT_182 = ".equals(other.";
  protected final String TEXT_183 = ")){";
  protected final String TEXT_184 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_185 = ".compareTo(other.";
  protected final String TEXT_186 = ");";
  protected final String TEXT_187 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_188 = ".compareTo(this.";
  protected final String TEXT_189 = ");";
  protected final String TEXT_190 = NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_191 = NL + "\t\tif(this.";
  protected final String TEXT_192 = " != other.";
  protected final String TEXT_193 = "){";
  protected final String TEXT_194 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_195 = " ? 1 : -1;";
  protected final String TEXT_196 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_197 = " ? -1 : -1;";
  protected final String TEXT_198 = NL + "\t\t}";
  protected final String TEXT_199 = NL + "\t\tif(this.";
  protected final String TEXT_200 = " != other.";
  protected final String TEXT_201 = "){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_202 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_203 = " > other.";
  protected final String TEXT_204 = " ? 1 : -1;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_205 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_206 = " > this.";
  protected final String TEXT_207 = " ? 1 : -1;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_208 = NL + "\t\t}";
  protected final String TEXT_209 = NL + "\t\tString thisS";
  protected final String TEXT_210 = " = this.";
  protected final String TEXT_211 = " == null ? \"null\" : new String(this.";
  protected final String TEXT_212 = ");" + NL + "\t\tString otherS";
  protected final String TEXT_213 = " = other.";
  protected final String TEXT_214 = " == null ? \"null\" : new String(other.";
  protected final String TEXT_215 = ");" + NL + "\t\tif(!thisS";
  protected final String TEXT_216 = ".equals(otherS";
  protected final String TEXT_217 = ")){";
  protected final String TEXT_218 = NL + "\t\t\treturn thisS";
  protected final String TEXT_219 = ".compareTo(otherS";
  protected final String TEXT_220 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_221 = NL + "\t\t\treturn otherS";
  protected final String TEXT_222 = ".compareTo(thisS";
  protected final String TEXT_223 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_224 = NL + "\t\t}";
  protected final String TEXT_225 = NL + "\t\t\tString thisS";
  protected final String TEXT_226 = " = this.";
  protected final String TEXT_227 = " == null ? \"null\" : FormatterUtils.format_Date(this.";
  protected final String TEXT_228 = ", ";
  protected final String TEXT_229 = ");" + NL + "\t\t\tString otherS";
  protected final String TEXT_230 = " = other.";
  protected final String TEXT_231 = " == null ? \"null\" : FormatterUtils.format_Date(other.";
  protected final String TEXT_232 = ", ";
  protected final String TEXT_233 = ");" + NL + "\t\t\tif(!thisS";
  protected final String TEXT_234 = ".equals(otherS";
  protected final String TEXT_235 = ")){";
  protected final String TEXT_236 = NL + "\t\t\t\treturn thisS";
  protected final String TEXT_237 = ".compareTo(otherS";
  protected final String TEXT_238 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_239 = NL + "\t\t\t\treturn otherS";
  protected final String TEXT_240 = ".compareTo(thisS";
  protected final String TEXT_241 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_242 = NL + "\t\t\t}";
  protected final String TEXT_243 = NL + "\t\tif(this.";
  protected final String TEXT_244 = " == null && other.";
  protected final String TEXT_245 = " != null){";
  protected final String TEXT_246 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_247 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_248 = NL + "\t\t}else if(this.";
  protected final String TEXT_249 = " != null && other.";
  protected final String TEXT_250 = " == null){";
  protected final String TEXT_251 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_252 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_253 = NL + "\t\t}else if(this.";
  protected final String TEXT_254 = " != null && other.";
  protected final String TEXT_255 = " != null){" + NL + "\t\t\tif(!this.";
  protected final String TEXT_256 = ".equals(other.";
  protected final String TEXT_257 = ")){";
  protected final String TEXT_258 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_259 = ".compareTo(other.";
  protected final String TEXT_260 = ");";
  protected final String TEXT_261 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_262 = ".compareTo(this.";
  protected final String TEXT_263 = ");";
  protected final String TEXT_264 = NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_265 = NL + "\t\tString thisS";
  protected final String TEXT_266 = " = this.";
  protected final String TEXT_267 = " == null ? \"null\" : String.valueOf(this.";
  protected final String TEXT_268 = ");" + NL + "\t\tString otherS";
  protected final String TEXT_269 = " = other.";
  protected final String TEXT_270 = " == null ? \"null\" : String.valueOf(other.";
  protected final String TEXT_271 = ");" + NL + "\t\tif(!thisS";
  protected final String TEXT_272 = ".equals(otherS";
  protected final String TEXT_273 = ")){";
  protected final String TEXT_274 = NL + "\t\t\treturn thisS";
  protected final String TEXT_275 = ".compareTo(otherS";
  protected final String TEXT_276 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_277 = NL + "\t\t\treturn otherS";
  protected final String TEXT_278 = ".compareTo(thisS";
  protected final String TEXT_279 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_280 = NL + "\t\t}";
  protected final String TEXT_281 = NL + "\t\tString thisS";
  protected final String TEXT_282 = " = String.valueOf(this.";
  protected final String TEXT_283 = ");" + NL + "\t\tString otherS";
  protected final String TEXT_284 = " = String.valueOf(other.";
  protected final String TEXT_285 = ");" + NL + "\t\tif(!thisS";
  protected final String TEXT_286 = ".equals(otherS";
  protected final String TEXT_287 = ")){";
  protected final String TEXT_288 = NL + "\t\t\treturn thisS";
  protected final String TEXT_289 = ".compareTo(otherS";
  protected final String TEXT_290 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_291 = NL + "\t\t\treturn otherS";
  protected final String TEXT_292 = ".compareTo(thisS";
  protected final String TEXT_293 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_294 = NL + "\t\t}";
  protected final String TEXT_295 = NL + "\t\tif(this.";
  protected final String TEXT_296 = " == null && other.";
  protected final String TEXT_297 = " != null){";
  protected final String TEXT_298 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_299 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_300 = NL + "\t\t}else if(this.";
  protected final String TEXT_301 = " != null && other.";
  protected final String TEXT_302 = " == null){";
  protected final String TEXT_303 = NL + "\t\t\treturn 1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_304 = NL + "\t\t\treturn -1;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_305 = NL + "\t\t}else if(this.";
  protected final String TEXT_306 = " != null && other.";
  protected final String TEXT_307 = " != null){" + NL + "\t\t\tif(!this.";
  protected final String TEXT_308 = ".equals(other.";
  protected final String TEXT_309 = ")){";
  protected final String TEXT_310 = NL + "\t\t\t\treturn this.";
  protected final String TEXT_311 = ".compareTo(other.";
  protected final String TEXT_312 = ");";
  protected final String TEXT_313 = NL + "\t\t\t\treturn other.";
  protected final String TEXT_314 = ".compareTo(this.";
  protected final String TEXT_315 = ");";
  protected final String TEXT_316 = NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_317 = NL + "\t\treturn 0;" + NL + "\t}" + NL + "" + NL + "\tpublic org.talend.designer.components.tsort.io.beans.ILightSerializable createInstance(byte[] byteArray) {" + NL + "\t\t";
  protected final String TEXT_318 = "StructILightSerializable result = new ";
  protected final String TEXT_319 = "StructILightSerializable();" + NL + "\t\tjava.io.ByteArrayInputStream bai = null;" + NL + "\t\tjava.io.DataInputStream dis = null;" + NL + "" + NL + "\t\ttry {" + NL + "\t\t\tbai = new java.io.ByteArrayInputStream(byteArray);" + NL + "\t\t\tdis = new java.io.DataInputStream(bai);" + NL + "\t\t\tint length = 0;";
  protected final String TEXT_320 = NL + "\t\t\t            result.";
  protected final String TEXT_321 = " = dis.read";
  protected final String TEXT_322 = "();" + NL + "\t\t\t\t\t";
  protected final String TEXT_323 = NL + "\t\t\t            length = dis.readInt();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tresult.";
  protected final String TEXT_324 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "               \t\t\t\tbyte[] bytes = new byte[length];" + NL + "               \t\t\t\tdis.read(bytes);" + NL + "               \t\t\t\tresult.";
  protected final String TEXT_325 = " = new String(bytes, utf8Charset);" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_326 = NL + "\t\t\t            length = dis.readInt();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tresult.";
  protected final String TEXT_327 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "               \t\t\t\tbyte[] bytes = new byte[length];" + NL + "               \t\t\t\tdis.read(bytes);" + NL + "               \t\t\t\tresult.";
  protected final String TEXT_328 = " = bytes;" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_329 = NL + "\t\t\t            length = dis.readByte();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tresult.";
  protected final String TEXT_330 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t    \tresult.";
  protected final String TEXT_331 = " = new Date(dis.readLong());" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_332 = NL + "\t\t\t            length = dis.readInt();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tresult.";
  protected final String TEXT_333 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "               \t\t\t\tbyte[] bytes = new byte[length];" + NL + "               \t\t\t\tdis.read(bytes);" + NL + "               \t\t\t\tjava.io.ByteArrayInputStream bio_";
  protected final String TEXT_334 = "  = new java.io.ByteArrayInputStream(bytes);" + NL + "            \t\t\t\tjava.io.ObjectInputStream ois_";
  protected final String TEXT_335 = "  = new java.io.ObjectInputStream(bio_";
  protected final String TEXT_336 = " );" + NL + "               \t\t\t\tresult.";
  protected final String TEXT_337 = " = (";
  protected final String TEXT_338 = ") ois_";
  protected final String TEXT_339 = ".readObject();" + NL + "               \t\t\t\tois_";
  protected final String TEXT_340 = ".close();" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_341 = NL + "\t\t\t            length = dis.readByte();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tresult.";
  protected final String TEXT_342 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t    \tresult.";
  protected final String TEXT_343 = " = dis.read";
  protected final String TEXT_344 = "();" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_345 = NL + NL + "\t\t} catch (Exception e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t} finally {" + NL + "\t\t\tif (dis != null) {" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tdis.close();" + NL + "            } catch (java.io.IOException e) {" + NL + "            \te.printStackTrace();" + NL + "         \t}" + NL + "        \t}" + NL + "     \t}" + NL + "" + NL + "   \treturn result;" + NL + "   }" + NL + "" + NL + "\tpublic byte[] toByteArray() {" + NL + " \t\tjava.io.ByteArrayOutputStream bao = null;" + NL + "\t\tjava.io.DataOutputStream dos = null;" + NL + "\t\tbyte[] result = null;" + NL + "" + NL + " \t\ttry {" + NL + "\t\t\tbao = new java.io.ByteArrayOutputStream();" + NL + "\t\t\tdos = new java.io.DataOutputStream(bao);";
  protected final String TEXT_346 = NL + "\t\t\tdos.write";
  protected final String TEXT_347 = "(this.";
  protected final String TEXT_348 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_349 = NL + "\t\t\tif(this.";
  protected final String TEXT_350 = " == null) {" + NL + "\t\t\t\tdos.writeInt(-1);" + NL + "\t\t\t} else {" + NL + "\t\t\t\tbyte[] byteArray = this.";
  protected final String TEXT_351 = ".getBytes(utf8Charset);" + NL + "\t\t\t\tdos.writeInt(byteArray.length);" + NL + "\t\t\t\tdos.write(byteArray);" + NL + "\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_352 = NL + "\t\t\tif(this.";
  protected final String TEXT_353 = " == null) {" + NL + "\t\t\t\tdos.writeInt(-1);" + NL + "\t\t\t} else {" + NL + "\t\t\t\tdos.writeInt(this.";
  protected final String TEXT_354 = ".length);" + NL + "\t\t\t\tdos.write(this.";
  protected final String TEXT_355 = ");" + NL + "\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_356 = NL + "\t\t\tif(this.";
  protected final String TEXT_357 = " == null) {" + NL + "\t\t\t\tdos.writeByte(-1);" + NL + "\t\t\t} else {" + NL + "\t\t\t\tdos.writeByte(0);" + NL + "\t\t\t\tdos.writeLong(this.";
  protected final String TEXT_358 = ".getTime());" + NL + "\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_359 = NL + "\t\t\tif(this.";
  protected final String TEXT_360 = " == null) {" + NL + "\t\t\t\tdos.writeInt(-1);" + NL + "\t\t\t} else {" + NL + "\t\t\t\tjava.io.ByteArrayOutputStream bao_";
  protected final String TEXT_361 = " = new java.io.ByteArrayOutputStream();" + NL + "            \tjava.io.ObjectOutputStream oos_";
  protected final String TEXT_362 = " = new java.io.ObjectOutputStream(bao_";
  protected final String TEXT_363 = ");" + NL + "            \toos_";
  protected final String TEXT_364 = ".writeObject(this.";
  protected final String TEXT_365 = ");" + NL + "            \toos_";
  protected final String TEXT_366 = ".close();" + NL + "            \tbyte[] byteArray = bao_";
  protected final String TEXT_367 = ".toByteArray();" + NL + "\t\t\t\tdos.writeInt(byteArray.length);" + NL + "\t\t\t\tdos.write(byteArray);" + NL + "\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_368 = NL + "\t\t\tif(this.";
  protected final String TEXT_369 = " == null) {" + NL + "\t\t\t\tdos.writeByte(-1);" + NL + "\t\t\t} else {" + NL + "\t\t\t\tdos.writeByte(0);" + NL + "\t\t\t\tdos.write";
  protected final String TEXT_370 = "(this.";
  protected final String TEXT_371 = ");" + NL + "\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_372 = NL + "    \t} catch (Exception e) {" + NL + "     \t\tthrow new RuntimeException(e);" + NL + "\t\t} finally {" + NL + "     \t\tif (dos != null) {" + NL + "         \t\ttry {" + NL + "            \t\tdos.close();" + NL + "           \t\t} catch (java.io.IOException e) {" + NL + "        \t\t\te.printStackTrace();" + NL + "          \t\t}" + NL + "        \t}" + NL + "     \t}" + NL + "     \tresult = bao.toByteArray();" + NL + "    \treturn result;" + NL + "  \t}" + NL + "" + NL + "" + NL + "}" + NL + "// /////////////////////////////////";
  protected final String TEXT_373 = NL + "  java.io.File dir_";
  protected final String TEXT_374 = " = new java.io.File(";
  protected final String TEXT_375 = ");" + NL + "  if (!dir_";
  protected final String TEXT_376 = ".exists()){" + NL + "    dir_";
  protected final String TEXT_377 = ".mkdirs();" + NL + "  }" + NL + "  dir_";
  protected final String TEXT_378 = " = null;";
  protected final String TEXT_379 = NL + NL + "org.talend.designer.components.tsort.io.sortimpl.FlowSorterIterator<";
  protected final String TEXT_380 = "StructILightSerializable> iterator_";
  protected final String TEXT_381 = " = new org.talend.designer.components.tsort.io.sortimpl.FlowSorterIterator<";
  protected final String TEXT_382 = "StructILightSerializable>();" + NL + "iterator_";
  protected final String TEXT_383 = ".setBufferSize(";
  protected final String TEXT_384 = ");" + NL + "iterator_";
  protected final String TEXT_385 = ".setILightSerializable(new ";
  protected final String TEXT_386 = "StructILightSerializable());" + NL + "iterator_";
  protected final String TEXT_387 = ".workDirectory = ";
  protected final String TEXT_388 = " + \"/\" + jobName + \"";
  protected final String TEXT_389 = " _\" + pid;" + NL + "iterator_";
  protected final String TEXT_390 = ".initPut(\"\");" + NL;
  protected final String TEXT_391 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
String rowName= "";
if ((node.getIncomingConnections()!=null)&&(node.getIncomingConnections().size()>0)) {
	rowName = node.getIncomingConnections().get(0).getName();
} else {
	rowName="defaultRow";
}

List<Map<String, String>> criterias = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__CRITERIA__");

String isExternalSort = ElementParameterParser.getValue(node, "__EXTERNAL__");

final Integer SORT_NUM = 0;
final Integer SORT_ALPHA = 1;
final Integer SORT_DATE = 2;
final Integer SORT_ASC = Integer.MAX_VALUE;
final Integer SORT_DESC = Integer.MIN_VALUE;
List<String> listCols = new ArrayList<String>();
List<Integer> listCriterias = new ArrayList<Integer>();
List<Integer> listCriteriaTypes = new ArrayList<Integer>();
List<Boolean> listNullables = new ArrayList<Boolean>();
List<String> listPatterns = new ArrayList<String>();
List<JavaType> listColumnTypes = new ArrayList<JavaType>();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {
        for (int i = 0 ; i < criterias.size() ; i++) {
        	Map<String, String> line = criterias.get(i);
        	String colname = line.get("COLNAME");
        	if(listCols.contains(colname)){
        		continue;//skip dipulicate
        	}
        	listCols.add(colname);
        	if(("asc").equals(line.get("ORDER"))){
        		listCriterias.add(SORT_ASC);
        	}else{
        		listCriterias.add(SORT_DESC);
        	}
        	if(("num").equals(line.get("SORT"))){
        		listCriteriaTypes.add(SORT_NUM);
        	}else if(("alpha").equals(line.get("SORT"))){
        		listCriteriaTypes.add(SORT_ALPHA);
        	}else{
        		listCriteriaTypes.add(SORT_DATE);
        	}
    
        	for (IMetadataColumn column : metadata.getListColumns()) {
        		if (column.getLabel().compareTo(colname)==0) {
        			listColumnTypes.add(JavaTypesManager.getJavaTypeFromId(column.getTalendType()));
        			if(JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), false)){
	        			listNullables.add(column.isNullable());
        			}else{
        				listNullables.add(true);
        			}
        			listPatterns.add(column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern());
        			
        		}
        	}
    	}

    
    	for (int i = 0 ; i < listCols.size() ; i++) {
    		Integer criteriaType = listCriteriaTypes.get(i);
    		JavaType columnType = listColumnTypes.get(i);
    		if(criteriaType == SORT_NUM){
    			if(!columnType.isPrimitive()){
    				if("id_Dynamic".equals(columnType.getId()) || columnType == JavaTypesManager.LIST || columnType == JavaTypesManager.BYTE_ARRAY || columnType == JavaTypesManager.OBJECT || columnType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(listCols.get(i) );
    stringBuffer.append(TEXT_3);
    
					}
				}
			}else if(criteriaType == SORT_DATE){
				if(columnType != JavaTypesManager.DATE){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(listCols.get(i) );
    stringBuffer.append(TEXT_5);
    
				}
			}
		}

    stringBuffer.append(TEXT_6);
    
		if(("false").equals(isExternalSort)){
		//sort in memory begin

    stringBuffer.append(TEXT_7);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_11);
    
			for (int i = 0 ; i < listCols.size() ; i++) {
				String colname = listCols.get(i);	
				JavaType columnType = listColumnTypes.get(i);
				Integer criteriaType = listCriteriaTypes.get(i);
				Integer criteria = listCriterias.get(i);
				if(criteriaType == SORT_NUM){
					if("id_Dynamic".equals(columnType.getId())){
					} else if(listNullables.get(i)){//

    stringBuffer.append(TEXT_12);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_14);
    
						if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_15);
    
						}else{
						
    stringBuffer.append(TEXT_16);
    
						}

    stringBuffer.append(TEXT_17);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_19);
    
						if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_20);
    
						}else{
						
    stringBuffer.append(TEXT_21);
    
						}

    stringBuffer.append(TEXT_22);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_26);
    
						if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_27);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_29);
    
						}else{

    stringBuffer.append(TEXT_30);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_32);
    
						}

    stringBuffer.append(TEXT_33);
    
					}else{//end tag for if(listNullables.get(i))
						if(columnType == JavaTypesManager.BOOLEAN){

    stringBuffer.append(TEXT_34);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_36);
    
							if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_37);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_38);
    
							}else{

    stringBuffer.append(TEXT_39);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_40);
    
							}

    stringBuffer.append(TEXT_41);
    
						}else {

    stringBuffer.append(TEXT_42);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_44);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_45);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_47);
    
							}else{
							
    stringBuffer.append(TEXT_48);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_50);
    
							}
						
    stringBuffer.append(TEXT_51);
    
						}
					}//end of if(listNullables.get(i))
				}else if(criteriaType == SORT_ALPHA){//end tag for if(criteriaType == SORT_NUM)
					if(columnType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_52);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_60);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_61);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_63);
    
							}else{
							
    stringBuffer.append(TEXT_64);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_66);
    
							}

    stringBuffer.append(TEXT_67);
    
					}else if(columnType == JavaTypesManager.DATE){

    stringBuffer.append(TEXT_68);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(listPatterns.get(i) );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(listPatterns.get(i) );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_78);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_79);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_81);
    
							}else{
							
    stringBuffer.append(TEXT_82);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_84);
    
							}

    stringBuffer.append(TEXT_85);
    
					}else if(columnType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_86);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_88);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_89);
    
							}else{
						
    stringBuffer.append(TEXT_90);
    
							}

    stringBuffer.append(TEXT_91);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_93);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_94);
    
							}else{
						
    stringBuffer.append(TEXT_95);
    
							}

    stringBuffer.append(TEXT_96);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_100);
    
							if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_101);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_103);
    
							}else{

    stringBuffer.append(TEXT_104);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_106);
    
							}

    stringBuffer.append(TEXT_107);
    
					}else{
						if(listNullables.get(i)){

    stringBuffer.append(TEXT_108);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_116);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_117);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_119);
    
							}else{
							
    stringBuffer.append(TEXT_120);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_122);
    
							}

    stringBuffer.append(TEXT_123);
    
						}else{

    stringBuffer.append(TEXT_124);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_130);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_131);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_133);
    
							}else{
							
    stringBuffer.append(TEXT_134);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_136);
    
							}

    stringBuffer.append(TEXT_137);
    
						}
					}
				}else if(!"id_Dynamic".equals(columnType.getId())){//end tag for if(criteriaType == SORT_ALPHA) for SORT_DATE

    stringBuffer.append(TEXT_138);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_140);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_141);
    
							}else{
						
    stringBuffer.append(TEXT_142);
    
							}

    stringBuffer.append(TEXT_143);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_145);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_146);
    
							}else{
						
    stringBuffer.append(TEXT_147);
    
							}

    stringBuffer.append(TEXT_148);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_152);
    
							if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_153);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_155);
    
							}else{

    stringBuffer.append(TEXT_156);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_158);
    
							}

    stringBuffer.append(TEXT_159);
    
				}//end of if(criteriaType == SORT_NUM)
			}

    stringBuffer.append(TEXT_160);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_163);
    
		//sort in memory end
		}else{
		//sort out of memory begin
			String tempDirectory = ElementParameterParser.getValue(node, "__TEMPFILE__");
			String bufferSize = ElementParameterParser.getValue(node, "__EXTERNAL_SORT_BUFFERSIZE__");
			boolean bCreateDir = "true".equals(ElementParameterParser.getValue(node, "__CREATEDIR__"));

    stringBuffer.append(TEXT_164);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_168);
    
			for (int i = 0 ; i < listCols.size() ; i++) {
				String colname = listCols.get(i);	
				JavaType columnType = listColumnTypes.get(i);
				Integer criteriaType = listCriteriaTypes.get(i);
				Integer criteria = listCriterias.get(i);
				if(criteriaType == SORT_NUM){
					if("id_Dynamic".equals(columnType.getId())){
					} else if(listNullables.get(i)){//

    stringBuffer.append(TEXT_169);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_171);
    
						if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_172);
    
						}else{
						
    stringBuffer.append(TEXT_173);
    
						}

    stringBuffer.append(TEXT_174);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_176);
    
						if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_177);
    
						}else{
						
    stringBuffer.append(TEXT_178);
    
						}

    stringBuffer.append(TEXT_179);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_183);
    
						if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_184);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_186);
    
						}else{

    stringBuffer.append(TEXT_187);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_189);
    
						}

    stringBuffer.append(TEXT_190);
    
					}else{//end tag for if(listNullables.get(i))
						if(columnType == JavaTypesManager.BOOLEAN){

    stringBuffer.append(TEXT_191);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_192);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_193);
    
							if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_194);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_195);
    
							}else{

    stringBuffer.append(TEXT_196);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_197);
    
							}

    stringBuffer.append(TEXT_198);
    
						}else {

    stringBuffer.append(TEXT_199);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_201);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_202);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_204);
    
							}else{
							
    stringBuffer.append(TEXT_205);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_207);
    
							}
						
    stringBuffer.append(TEXT_208);
    
						}
					}//end of if(listNullables.get(i))
				}else if(criteriaType == SORT_ALPHA){//end tag for if(criteriaType == SORT_NUM)
					if(columnType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_209);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_210);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_211);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_213);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_215);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_216);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_217);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_218);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_220);
    
							}else{
							
    stringBuffer.append(TEXT_221);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_222);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_223);
    
							}

    stringBuffer.append(TEXT_224);
    
					}else if(columnType == JavaTypesManager.DATE){

    stringBuffer.append(TEXT_225);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_226);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_227);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_228);
    stringBuffer.append(listPatterns.get(i) );
    stringBuffer.append(TEXT_229);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_231);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_232);
    stringBuffer.append(listPatterns.get(i) );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_234);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_235);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_236);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_237);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_238);
    
							}else{
							
    stringBuffer.append(TEXT_239);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_240);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_241);
    
							}

    stringBuffer.append(TEXT_242);
    
					}else if(columnType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_243);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_244);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_245);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_246);
    
							}else{
						
    stringBuffer.append(TEXT_247);
    
							}

    stringBuffer.append(TEXT_248);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_249);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_250);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_251);
    
							}else{
						
    stringBuffer.append(TEXT_252);
    
							}

    stringBuffer.append(TEXT_253);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_254);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_255);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_256);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_257);
    
							if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_258);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_259);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_260);
    
							}else{

    stringBuffer.append(TEXT_261);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_262);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_263);
    
							}

    stringBuffer.append(TEXT_264);
    
					}else{
						if(listNullables.get(i)){

    stringBuffer.append(TEXT_265);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_266);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_267);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_268);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_269);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_270);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_271);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_272);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_273);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_274);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_275);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_276);
    
							}else{
							
    stringBuffer.append(TEXT_277);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_278);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_279);
    
							}

    stringBuffer.append(TEXT_280);
    
						}else{

    stringBuffer.append(TEXT_281);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_283);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_284);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_286);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_287);
    
							if(criteria == SORT_ASC){
							
    stringBuffer.append(TEXT_288);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_289);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_290);
    
							}else{
							
    stringBuffer.append(TEXT_291);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_292);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_293);
    
							}

    stringBuffer.append(TEXT_294);
    
						}
					}
				}else if(!"id_Dynamic".equals(columnType.getId())){//end tag for if(criteriaType == SORT_ALPHA) for SORT_DATE

    stringBuffer.append(TEXT_295);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_296);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_297);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_298);
    
							}else{
						
    stringBuffer.append(TEXT_299);
    
							}

    stringBuffer.append(TEXT_300);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_301);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_302);
    
							if(criteria == SORT_ASC){
						
    stringBuffer.append(TEXT_303);
    
							}else{
						
    stringBuffer.append(TEXT_304);
    
							}

    stringBuffer.append(TEXT_305);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_308);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_309);
    
							if(criteria == SORT_ASC){

    stringBuffer.append(TEXT_310);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_311);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_312);
    
							}else{

    stringBuffer.append(TEXT_313);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_314);
    stringBuffer.append(colname );
    stringBuffer.append(TEXT_315);
    
							}

    stringBuffer.append(TEXT_316);
    
				}//end of if(criteriaType == SORT_NUM)
			}

    stringBuffer.append(TEXT_317);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_318);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_319);
    
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_320);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_321);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_322);
    
				} else if(("String").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_323);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_324);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_325);
    
				} else if(("byte[]").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_326);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_327);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_328);
    
				} else if(("java.util.Date").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_329);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_330);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_331);
    
				} else if(("id_Dynamic").equals(column.getTalendType()) || ("Object").equals(typeToGenerate) || ("BigDecimal").equals(typeToGenerate) || ("List").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_332);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_333);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_334);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_335);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_336);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_337);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_338);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_339);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_340);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_341);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_342);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_343);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_344);
    
				}
			}
    	}

    stringBuffer.append(TEXT_345);
    	
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_346);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_347);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_348);
    
				} else if(("String").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_349);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_350);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_351);
    
				} else if(("byte[]").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_352);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_353);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_354);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_355);
    
				} else if(("java.util.Date").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_356);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_357);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_358);
    
				} else if(("id_Dynamic").equals(column.getTalendType()) || ("Object").equals(typeToGenerate) || ("BigDecimal").equals(typeToGenerate) || ("List").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_359);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_360);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_361);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_362);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_363);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_364);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_365);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_366);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_367);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate = typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_368);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_369);
    stringBuffer.append(typeToGenerate );
    stringBuffer.append(TEXT_370);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_371);
    
				
				}
			}
    	}
    	
    stringBuffer.append(TEXT_372);
    
//create directory if doesn't exist
if (bCreateDir){

    stringBuffer.append(TEXT_373);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_374);
    stringBuffer.append(tempDirectory );
    stringBuffer.append(TEXT_375);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_376);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_377);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_378);
    
}

    stringBuffer.append(TEXT_379);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_380);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_381);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_382);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_383);
    stringBuffer.append(bufferSize );
    stringBuffer.append(TEXT_384);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_385);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_386);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_387);
    stringBuffer.append(tempDirectory );
    stringBuffer.append(TEXT_388);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_389);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_390);
    
		//sort out of memory end
		}
	}
}

    stringBuffer.append(TEXT_391);
    return stringBuffer.toString();
  }
}
