package org.talend.designer.codegen.translators.databases.oracle;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.Map;
import java.util.List;

public class TOracleInputBeginJava
{
  protected static String nl;
  public static synchronized TOracleInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOracleInputBeginJava result = new TOracleInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = NL + "\t\t\t    java.lang.Class.forName(\"";
  protected final String TEXT_3 = "\");" + NL + "\t\t\t\t";
  protected final String TEXT_4 = NL + "\t\t        String dbUser_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "\t\t        String dbPwd_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "\t\t        conn_";
  protected final String TEXT_9 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_10 = ",dbUser_";
  protected final String TEXT_11 = ",dbPwd_";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL + "\t\t\tjava.sql.Statement stmt_";
  protected final String TEXT_14 = " = conn_";
  protected final String TEXT_15 = ".createStatement();";
  protected final String TEXT_16 = NL + "\t\t\tjava.sql.Statement stmt_";
  protected final String TEXT_17 = " = conn_";
  protected final String TEXT_18 = ".createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY," + NL + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tjava.sql.ResultSet.CONCUR_READ_ONLY);" + NL + "\t\t\t";
  protected final String TEXT_19 = NL + "\t\t\t\t\ttmpContent_";
  protected final String TEXT_20 = " = rs_";
  protected final String TEXT_21 = ".getString(";
  protected final String TEXT_22 = ");";
  protected final String TEXT_23 = NL + "                        if(tmpContent_";
  protected final String TEXT_24 = " != null) {" + NL + "                            tmpContent_";
  protected final String TEXT_25 = " = tmpContent_";
  protected final String TEXT_26 = ";" + NL + "                        }";
  protected final String TEXT_27 = NL + "                    if(tmpContent_";
  protected final String TEXT_28 = " != null && tmpContent_";
  protected final String TEXT_29 = ".length() > 0) {\t\t\t  \t";
  protected final String TEXT_30 = NL + "                        ";
  protected final String TEXT_31 = ".";
  protected final String TEXT_32 = " = tmpContent_";
  protected final String TEXT_33 = ".charAt(0);\t\t\t  \t\t" + NL + "                    } else {\t\t\t  \t";
  protected final String TEXT_34 = "\t\t\t  \t    " + NL + "                            if(tmpContent_";
  protected final String TEXT_35 = " == null) {\t\t\t  \t   \t";
  protected final String TEXT_36 = NL + "                                ";
  protected final String TEXT_37 = ".";
  protected final String TEXT_38 = " = null;\t\t\t  \t\t\t" + NL + "                            } else {\t\t\t  \t\t";
  protected final String TEXT_39 = NL + "                                ";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = " = '\\0';\t\t\t  \t\t\t" + NL + "                            }";
  protected final String TEXT_42 = "\t\t\t  \t\t" + NL + "                            if((\"\").equals(tmpContent_";
  protected final String TEXT_43 = ")) {\t\t\t  \t\t";
  protected final String TEXT_44 = NL + "                                ";
  protected final String TEXT_45 = ".";
  protected final String TEXT_46 = " = '\\0';\t\t\t  \t\t\t" + NL + "                            } else {\t\t\t  \t\t" + NL + "        \t\t\t  \t\t\tthrow new RuntimeException(" + NL + "        \t\t\t\t\t\t\t\"Value is empty for column : '";
  protected final String TEXT_47 = "' in '";
  protected final String TEXT_48 = "' connection, value is invalid or this column should be nullable or have a default value.\");\t\t\t\t\t\t\t" + NL + "                            }\t\t\t  \t\t";
  protected final String TEXT_49 = NL + "                    }";
  protected final String TEXT_50 = NL + "\t\t\tif(rs_";
  protected final String TEXT_51 = ".getTimestamp(";
  protected final String TEXT_52 = ") != null) {" + NL + "\t\t\t    ";
  protected final String TEXT_53 = ".";
  protected final String TEXT_54 = " = new java.util.Date(rs_";
  protected final String TEXT_55 = ".getTimestamp(";
  protected final String TEXT_56 = ").getTime());" + NL + "\t\t\t} else {" + NL + "\t\t\t    ";
  protected final String TEXT_57 = ".";
  protected final String TEXT_58 = " =  null;" + NL + "\t\t\t}\t\t\t ";
  protected final String TEXT_59 = NL + "            tmpContent_";
  protected final String TEXT_60 = " = rs_";
  protected final String TEXT_61 = ".getString(";
  protected final String TEXT_62 = ");" + NL + "            if(tmpContent_";
  protected final String TEXT_63 = " != null) {";
  protected final String TEXT_64 = NL + "                ";
  protected final String TEXT_65 = ".";
  protected final String TEXT_66 = " = tmpContent_";
  protected final String TEXT_67 = ";" + NL + "            } else {";
  protected final String TEXT_68 = NL + "                ";
  protected final String TEXT_69 = ".";
  protected final String TEXT_70 = " = null;" + NL + "            }";
  protected final String TEXT_71 = NL + "            if(rs_";
  protected final String TEXT_72 = ".getObject(";
  protected final String TEXT_73 = ") != null) {";
  protected final String TEXT_74 = NL + "                ";
  protected final String TEXT_75 = ".";
  protected final String TEXT_76 = " = rs_";
  protected final String TEXT_77 = ".get";
  protected final String TEXT_78 = "(";
  protected final String TEXT_79 = ");" + NL + "            } else {";
  protected final String TEXT_80 = NL + "                    ";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = " = null;";
  protected final String TEXT_83 = "    " + NL + "                    throw new RuntimeException(\"Null value in non-Nullable column\");";
  protected final String TEXT_84 = NL + "            }";
  protected final String TEXT_85 = NL + "            \tString url_";
  protected final String TEXT_86 = " = null;";
  protected final String TEXT_87 = NL + "        \t\t\turl_";
  protected final String TEXT_88 = " = ";
  protected final String TEXT_89 = ";        ";
  protected final String TEXT_90 = NL + "            \t\turl_";
  protected final String TEXT_91 = " = \"jdbc:oracle:thin:@\" + ";
  protected final String TEXT_92 = " + \":\" + ";
  protected final String TEXT_93 = " + \":\" + ";
  protected final String TEXT_94 = ";";
  protected final String TEXT_95 = NL + "            \t\turl_";
  protected final String TEXT_96 = " = \"jdbc:oracle:thin:@(description=(address=(protocol=tcp)(host=\" + ";
  protected final String TEXT_97 = " + \")(port=\" + ";
  protected final String TEXT_98 = " + \"))(connect_data=(service_name=\" + ";
  protected final String TEXT_99 = " + \")))\";";
  protected final String TEXT_100 = NL + "            \t    url_";
  protected final String TEXT_101 = " = \"jdbc:oracle:oci8:@\" + ";
  protected final String TEXT_102 = ";";
  protected final String TEXT_103 = NL + "\t    \t\t\turl_";
  protected final String TEXT_104 = " = ";
  protected final String TEXT_105 = ";";
  protected final String TEXT_106 = NL;
  protected final String TEXT_107 = NL + "\t\t\tjava.lang.Class.forName(\"";
  protected final String TEXT_108 = "\");" + NL + "\t\t\t";
  protected final String TEXT_109 = NL + "\t        String dbUser_";
  protected final String TEXT_110 = " = ";
  protected final String TEXT_111 = ";" + NL + "\t        String dbPwd_";
  protected final String TEXT_112 = " = ";
  protected final String TEXT_113 = ";";
  protected final String TEXT_114 = NL + "\t\t\tconn_";
  protected final String TEXT_115 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_116 = ");";
  protected final String TEXT_117 = NL + "\t\t\tconn_";
  protected final String TEXT_118 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_119 = ",dbUser_";
  protected final String TEXT_120 = ",dbPwd_";
  protected final String TEXT_121 = ");";
  protected final String TEXT_122 = NL + "\t\t\tString atnParams_";
  protected final String TEXT_123 = " = ";
  protected final String TEXT_124 = ";" + NL + "\t\t\tatnParams_";
  protected final String TEXT_125 = " = atnParams_";
  protected final String TEXT_126 = ".replaceAll(\"&\", \"\\n\");" + NL + "\t\t\tjava.util.Properties atnParamsPrope_";
  protected final String TEXT_127 = " = new java.util.Properties();" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_128 = ".put(\"user\",dbUser_";
  protected final String TEXT_129 = ");" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_130 = ".put(\"password\",dbPwd_";
  protected final String TEXT_131 = ");" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_132 = ".load(new java.io.ByteArrayInputStream(atnParams_";
  protected final String TEXT_133 = ".getBytes()));" + NL + "\t\t\tconn_";
  protected final String TEXT_134 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_135 = ", atnParamsPrope_";
  protected final String TEXT_136 = ");";
  protected final String TEXT_137 = NL + "\t\t\t\tjava.sql.Statement stmtGetTZ_";
  protected final String TEXT_138 = " = conn_";
  protected final String TEXT_139 = ".createStatement();" + NL + "\t\t\t\tjava.sql.ResultSet rsGetTZ_";
  protected final String TEXT_140 = " = stmtGetTZ_";
  protected final String TEXT_141 = ".executeQuery(\"select sessiontimezone from dual\");" + NL + "\t\t\t\tString sessionTimezone_";
  protected final String TEXT_142 = " = java.util.TimeZone.getDefault().getID();" + NL + "\t\t\t\twhile (rsGetTZ_";
  protected final String TEXT_143 = ".next()) {" + NL + "\t\t\t\t\tsessionTimezone_";
  protected final String TEXT_144 = " = rsGetTZ_";
  protected final String TEXT_145 = ".getString(1);" + NL + "\t\t\t\t}" + NL + "\t\t\t\t((oracle.jdbc.OracleConnection)conn_";
  protected final String TEXT_146 = ").setSessionTimeZone(sessionTimezone_";
  protected final String TEXT_147 = ");" + NL + "\t\t\t";
  protected final String TEXT_148 = NL + "\t\t\t\tif(((oracle.jdbc.OracleConnection)conn_";
  protected final String TEXT_149 = ").getSessionTimeZone() == null){" + NL + "\t\t\t\t\tjava.sql.Statement stmtGetTZ_";
  protected final String TEXT_150 = " = conn_";
  protected final String TEXT_151 = ".createStatement();" + NL + "\t\t\t\t\tjava.sql.ResultSet rsGetTZ_";
  protected final String TEXT_152 = " = stmtGetTZ_";
  protected final String TEXT_153 = ".executeQuery(\"select sessiontimezone from dual\");" + NL + "\t\t\t\t\tString sessionTimezone_";
  protected final String TEXT_154 = " = java.util.TimeZone.getDefault().getID();" + NL + "\t\t\t\t\twhile (rsGetTZ_";
  protected final String TEXT_155 = ".next()) {" + NL + "\t\t\t\t\t\tsessionTimezone_";
  protected final String TEXT_156 = " = rsGetTZ_";
  protected final String TEXT_157 = ".getString(1);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t((oracle.jdbc.OracleConnection)conn_";
  protected final String TEXT_158 = ").setSessionTimeZone(sessionTimezone_";
  protected final String TEXT_159 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_160 = NL + "                stmt_";
  protected final String TEXT_161 = ".setFetchSize(";
  protected final String TEXT_162 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_163 = NL + "\t\t\t\tjava.io.Reader reader_";
  protected final String TEXT_164 = " = rs_";
  protected final String TEXT_165 = ".getCharacterStream(";
  protected final String TEXT_166 = ");" + NL + "\t\t\t\tjava.io.BufferedReader br_";
  protected final String TEXT_167 = " = null;" + NL + "\t\t\t\tif(reader_";
  protected final String TEXT_168 = " != null) {" + NL + "\t\t\t\t\tbr_";
  protected final String TEXT_169 = " = new java.io.BufferedReader(reader_";
  protected final String TEXT_170 = ");                 \t                        " + NL + " \t                StringBuffer content_";
  protected final String TEXT_171 = " = new StringBuffer();" + NL + " \t                int c_";
  protected final String TEXT_172 = " =0;" + NL + " \t                while( (c_";
  protected final String TEXT_173 = " = br_";
  protected final String TEXT_174 = ".read()) != -1) {" + NL + " \t                \tcontent_";
  protected final String TEXT_175 = ".append((char)c_";
  protected final String TEXT_176 = ");" + NL + "\t\t\t\t\t}" + NL + " \t                br_";
  protected final String TEXT_177 = ".close();" + NL + "\t\t\t\t\t";
  protected final String TEXT_178 = ".";
  protected final String TEXT_179 = " = content_";
  protected final String TEXT_180 = ".toString();" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\t";
  protected final String TEXT_181 = ".";
  protected final String TEXT_182 = " = null;" + NL + "\t\t\t\t}                 \t                    ";
  protected final String TEXT_183 = NL + "\t\t\t \t    tmpContent_";
  protected final String TEXT_184 = " = rs_";
  protected final String TEXT_185 = ".getString(";
  protected final String TEXT_186 = ");" + NL + "\t\t\t        if(tmpContent_";
  protected final String TEXT_187 = " != null) {" + NL + "\t\t\t            ";
  protected final String TEXT_188 = ".";
  protected final String TEXT_189 = " = tmpContent_";
  protected final String TEXT_190 = ";" + NL + "\t\t\t        } else {" + NL + "\t\t\t            ";
  protected final String TEXT_191 = ".";
  protected final String TEXT_192 = " = null;" + NL + "\t\t\t        }                 \t                    ";
  protected final String TEXT_193 = NL + "\t\t\tbyte [] tmpBytes_";
  protected final String TEXT_194 = " = rs_";
  protected final String TEXT_195 = ".getBytes(";
  protected final String TEXT_196 = ");" + NL + "\t\t\tif(tmpBytes_";
  protected final String TEXT_197 = " != null) {" + NL + "\t\t\t\t";
  protected final String TEXT_198 = ".";
  protected final String TEXT_199 = " = tmpBytes_";
  protected final String TEXT_200 = ";" + NL + "\t\t\t} else {" + NL + "\t\t\t\t";
  protected final String TEXT_201 = ".";
  protected final String TEXT_202 = " = null;" + NL + "\t\t\t}\t\t";
  protected final String TEXT_203 = NL + "\t\t\tjava.sql.Timestamp timestamp = rs_";
  protected final String TEXT_204 = ".getTimestamp(";
  protected final String TEXT_205 = ");" + NL + "\t\t\tif(timestamp != null) {" + NL + "\t\t\t    ";
  protected final String TEXT_206 = ".";
  protected final String TEXT_207 = " = new java.util.Date(timestamp.getTime());" + NL + "\t\t\t} else {" + NL + "\t\t\t    ";
  protected final String TEXT_208 = ".";
  protected final String TEXT_209 = " =  null;" + NL + "\t\t\t}\t\t\t ";
  protected final String TEXT_210 = NL + "\t\t\t\tObject tmpObj_";
  protected final String TEXT_211 = "_";
  protected final String TEXT_212 = " = rs_";
  protected final String TEXT_213 = ".getObject(";
  protected final String TEXT_214 = ");" + NL + "\t\t\t\tif(tmpObj_";
  protected final String TEXT_215 = "_";
  protected final String TEXT_216 = " != null) {";
  protected final String TEXT_217 = NL + "\t\t\t\t\t";
  protected final String TEXT_218 = ".";
  protected final String TEXT_219 = " = oracle.xdb.XMLType.createXML((oracle.sql.OPAQUE) tmpObj_";
  protected final String TEXT_220 = "_";
  protected final String TEXT_221 = ").getStringVal();";
  protected final String TEXT_222 = NL + "\t\t\t\t\t";
  protected final String TEXT_223 = ".";
  protected final String TEXT_224 = " = tmpObj_";
  protected final String TEXT_225 = "_";
  protected final String TEXT_226 = ";";
  protected final String TEXT_227 = NL + "\t\t\t\t} else {";
  protected final String TEXT_228 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_229 = ".";
  protected final String TEXT_230 = " = null;";
  protected final String TEXT_231 = "    " + NL + "\t\t\t\t\t\tthrow new RuntimeException(\"Null value in non-Nullable column\");";
  protected final String TEXT_232 = NL + "\t\t\t\t}";
  protected final String TEXT_233 = NL + "\t\t\t\t\t";
  protected final String TEXT_234 = ".";
  protected final String TEXT_235 = " = rs_";
  protected final String TEXT_236 = ".get";
  protected final String TEXT_237 = "(";
  protected final String TEXT_238 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_239 = NL + "\t\t\t\t\t\tif(!";
  protected final String TEXT_240 = ".";
  protected final String TEXT_241 = " && rs_";
  protected final String TEXT_242 = ".getObject(";
  protected final String TEXT_243 = ") == null){" + NL + "\t\t\t\t\t";
  protected final String TEXT_244 = NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_245 = ".";
  protected final String TEXT_246 = " == null){" + NL + "\t\t\t\t\t";
  protected final String TEXT_247 = NL + "\t\t\t\t\t\tif(";
  protected final String TEXT_248 = ".";
  protected final String TEXT_249 = " == 0 && rs_";
  protected final String TEXT_250 = ".getObject(";
  protected final String TEXT_251 = ") == null){" + NL + "\t\t\t\t\t";
  protected final String TEXT_252 = NL + "\t\t\t\t\t\tif(rs_";
  protected final String TEXT_253 = ".getObject(";
  protected final String TEXT_254 = ") == null){" + NL + "\t\t\t\t\t";
  protected final String TEXT_255 = NL + "\t\t\t\t";
  protected final String TEXT_256 = NL + "\t\t\t\t\tif(rs_";
  protected final String TEXT_257 = ".getObject(";
  protected final String TEXT_258 = ") != null) {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_259 = ".";
  protected final String TEXT_260 = " = rs_";
  protected final String TEXT_261 = ".get";
  protected final String TEXT_262 = "(";
  protected final String TEXT_263 = ");" + NL + "\t\t\t\t\t} else {" + NL + "\t\t\t\t";
  protected final String TEXT_264 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_265 = ".";
  protected final String TEXT_266 = " = null;";
  protected final String TEXT_267 = "    " + NL + " \t                \tthrow new RuntimeException(\"Null value in non-Nullable column\");";
  protected final String TEXT_268 = NL + "\t\t\t\t\t}";
  protected final String TEXT_269 = NL + NL + "    " + NL + "\t";
  protected final String TEXT_270 = NL + "\t\t    int nb_line_";
  protected final String TEXT_271 = " = 0;" + NL + "\t\t    java.sql.Connection conn_";
  protected final String TEXT_272 = " = null;";
  protected final String TEXT_273 = NL + "\t\t        conn_";
  protected final String TEXT_274 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_275 = "\");";
  protected final String TEXT_276 = NL + NL + "\t\t    ";
  protected final String TEXT_277 = NL + "\t\t    " + NL + "\t\t    String dbquery_";
  protected final String TEXT_278 = " = ";
  protected final String TEXT_279 = ";" + NL + "\t\t    " + NL + "\t\t    globalMap.put(\"";
  protected final String TEXT_280 = "_QUERY\",dbquery_";
  protected final String TEXT_281 = ");" + NL + "\t\t    " + NL + "\t\t    java.sql.ResultSet rs_";
  protected final String TEXT_282 = " = stmt_";
  protected final String TEXT_283 = ".executeQuery(dbquery_";
  protected final String TEXT_284 = ");" + NL + "\t\t    java.sql.ResultSetMetaData rsmd_";
  protected final String TEXT_285 = " = rs_";
  protected final String TEXT_286 = ".getMetaData();" + NL + "\t\t    int colQtyInRs_";
  protected final String TEXT_287 = " = rsmd_";
  protected final String TEXT_288 = ".getColumnCount();" + NL;
  protected final String TEXT_289 = NL + "\t\t    routines.system.Dynamic dcg_";
  protected final String TEXT_290 = " =  new routines.system.Dynamic();" + NL + "\t\t    dcg_";
  protected final String TEXT_291 = ".setDbmsId(\"";
  protected final String TEXT_292 = "\");" + NL + "\t\t    List<String> listSchema_";
  protected final String TEXT_293 = "=new java.util.ArrayList<String>();" + NL + "\t\t    for(int i_";
  protected final String TEXT_294 = "=1; i_";
  protected final String TEXT_295 = "<";
  protected final String TEXT_296 = "; i_";
  protected final String TEXT_297 = "++) {" + NL + "\t\t    \tlistSchema_";
  protected final String TEXT_298 = ".add(rsmd_";
  protected final String TEXT_299 = ".getColumnName(i_";
  protected final String TEXT_300 = ").toUpperCase());" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\tint fixedColumnCount_";
  protected final String TEXT_301 = " = ";
  protected final String TEXT_302 = ";" + NL + "\t\t\t" + NL + "            for (int i = ";
  protected final String TEXT_303 = "; i <= rsmd_";
  protected final String TEXT_304 = ".getColumnCount(); i++) {" + NL + "                if (!(listSchema_";
  protected final String TEXT_305 = ".contains(rsmd_";
  protected final String TEXT_306 = ".getColumnName(i).toUpperCase()) )) {" + NL + "                \troutines.system.DynamicMetadata dcm_";
  protected final String TEXT_307 = "=new routines.system.DynamicMetadata();" + NL + "                \tdcm_";
  protected final String TEXT_308 = ".setName(rsmd_";
  protected final String TEXT_309 = ".getColumnName(i));" + NL + "                \tdcm_";
  protected final String TEXT_310 = ".setDbName(rsmd_";
  protected final String TEXT_311 = ".getColumnName(i));" + NL + "                \tdcm_";
  protected final String TEXT_312 = ".setType(routines.system.Dynamic.getTalendTypeFromDBType(\"";
  protected final String TEXT_313 = "\", rsmd_";
  protected final String TEXT_314 = ".getColumnTypeName(i).toUpperCase(), rsmd_";
  protected final String TEXT_315 = ".getPrecision(i), rsmd_";
  protected final String TEXT_316 = ".getScale(i)));" + NL + "                \tdcm_";
  protected final String TEXT_317 = ".setDbType(rsmd_";
  protected final String TEXT_318 = ".getColumnTypeName(i));";
  protected final String TEXT_319 = NL + "                \tdcm_";
  protected final String TEXT_320 = ".setFormat(";
  protected final String TEXT_321 = ");";
  protected final String TEXT_322 = NL + "                \tdcm_";
  protected final String TEXT_323 = ".setLength(rsmd_";
  protected final String TEXT_324 = ".getPrecision(i));" + NL + "                \tdcm_";
  protected final String TEXT_325 = ".setPrecision(rsmd_";
  protected final String TEXT_326 = ".getScale(i));" + NL + "                \tdcm_";
  protected final String TEXT_327 = ".setNullable(rsmd_";
  protected final String TEXT_328 = ".isNullable(i) == 0 ? false : true);" + NL + "                \tdcm_";
  protected final String TEXT_329 = ".setKey(false);" + NL + "                \tdcm_";
  protected final String TEXT_330 = ".setSourceType(DynamicMetadata.sourceTypes.database);" + NL + "                \tdcm_";
  protected final String TEXT_331 = ".setColumnPosition(i);" + NL + "                \tdcg_";
  protected final String TEXT_332 = ".metadatas.add(dcm_";
  protected final String TEXT_333 = ");" + NL + "                }" + NL + "            }";
  protected final String TEXT_334 = NL + "\t\t    String tmpContent_";
  protected final String TEXT_335 = " = null;" + NL + "\t\t    while (rs_";
  protected final String TEXT_336 = ".next()) {" + NL + "\t\t        nb_line_";
  protected final String TEXT_337 = "++;" + NL + "\t\t        ";
  protected final String TEXT_338 = " \t" + NL + "\t\t                    if(colQtyInRs_";
  protected final String TEXT_339 = " < ";
  protected final String TEXT_340 = ") { \t\t" + NL + "\t\t                        ";
  protected final String TEXT_341 = ".";
  protected final String TEXT_342 = " = ";
  protected final String TEXT_343 = "; \t\t\t" + NL + "\t\t                    } else {";
  protected final String TEXT_344 = NL + "\t\t                 \t\t\t";
  protected final String TEXT_345 = "\t\t\t";
  protected final String TEXT_346 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_347 = NL + "\t\t                            ";
  protected final String TEXT_348 = ".";
  protected final String TEXT_349 = " = (List)rs_";
  protected final String TEXT_350 = ".getObject(";
  protected final String TEXT_351 = ");";
  protected final String TEXT_352 = NL + "\t\t                         ";
  protected final String TEXT_353 = NL + "                                    oracle.spatial.geometry.JGeometry jGeom = oracle.spatial.geometry.JGeometry.load((oracle.sql.STRUCT) rs_";
  protected final String TEXT_354 = ".getObject(";
  protected final String TEXT_355 = "));" + NL + "                                    oracle.spatial.util.WKT wkt = new oracle.spatial.util.WKT();" + NL + "                                    String wktValue = new String(wkt.fromJGeometry(jGeom));" + NL;
  protected final String TEXT_356 = NL + "                                    ";
  protected final String TEXT_357 = ".";
  protected final String TEXT_358 = " = new Geometry(wktValue);";
  protected final String TEXT_359 = NL + "                                        ";
  protected final String TEXT_360 = ".";
  protected final String TEXT_361 = ".setEPSG(";
  protected final String TEXT_362 = ");";
  protected final String TEXT_363 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_364 = NL + "                                  ";
  protected final String TEXT_365 = ".";
  protected final String TEXT_366 = "=dcg_";
  protected final String TEXT_367 = ";" + NL + "                                  routines.system.DynamicUtils.readColumnsFromDatabase(";
  protected final String TEXT_368 = ".";
  protected final String TEXT_369 = ", rs_";
  protected final String TEXT_370 = ", fixedColumnCount_";
  protected final String TEXT_371 = ");";
  protected final String TEXT_372 = NL + "                                \torg.postgis.Geometry o = org.postgis.PGgeometry.geomFromString(rs_";
  protected final String TEXT_373 = ".getObject(";
  protected final String TEXT_374 = ").toString());" + NL + "                                \tStringBuffer sb = new StringBuffer();" + NL + "                                \to.outerWKT(sb, false);" + NL + "                                \t";
  protected final String TEXT_375 = ".";
  protected final String TEXT_376 = " = new Geometry(sb.toString());";
  protected final String TEXT_377 = NL + "\t\t                          ";
  protected final String TEXT_378 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_379 = NL + "\t\t                    }";
  protected final String TEXT_380 = NL + "\t\t                            ";
  protected final String TEXT_381 = ".";
  protected final String TEXT_382 = " = ";
  protected final String TEXT_383 = ".";
  protected final String TEXT_384 = ";" + NL + "\t\t                            ";
  protected final String TEXT_385 = NL + NL + "\t" + NL + "\t" + NL;
  protected final String TEXT_386 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	//this util class use by connection component
	class DefaultDBInputUtil {
	
		protected String cid ;
		protected String dbproperties ;
		protected String dbhost;
	    protected String dbport;
	    protected String dbname;
	    protected String dbuser;
		protected String dbpwd ;
	    
	    public void beforeComponentProcess(INode node){
	    	cid = node.getUniqueName();
	    }
	    
	    public void afterUseExistConnection(INode node) {
	    }
	    
	    public String getDirverClassName(INode node){
			return "";
		}
	    
	    public void setURL(INode node) {
	    }

		public void createConnection(INode node) {
			cid = node.getUniqueName();
			dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
			dbhost = ElementParameterParser.getValue(node, "__HOST__");
	    	dbport = ElementParameterParser.getValue(node, "__PORT__");
	    	dbname = ElementParameterParser.getValue(node, "__DBNAME__");
	    	dbuser = ElementParameterParser.getValue(node, "__USER__");
	 		dbpwd = ElementParameterParser.getValue(node, "__PASS__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(this.getDirverClassName(node) );
    stringBuffer.append(TEXT_3);
    this.setURL(node);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
		}
		
		public String getQueryString(INode node) {
			String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
			dbquery = dbquery.replaceAll("\n"," ");
			dbquery = dbquery.replaceAll("\r"," ");
			
			return dbquery;
		}
		
		public void createStatement(INode node) {

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    
		}
		public void createMinValueStatement(INode node){

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    }
		public String mappingType(String typeToGenerate) {
		
            if(("byte[]").equals(typeToGenerate)) {
                return "Bytes";
            } else if(("java.util.Date").equals(typeToGenerate)) {
                return "Timestamp";
            } else if(("Integer").equals(typeToGenerate)) {
               return "Int";
            } else {
                return typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
            }
		}
		//-----------according schema type to generate ResultSet
		public void generateStringCharAndCharacterSet(String firstConnName, IMetadataColumn column, int currentColNo,
					String trimMethod, String typeToGenerate, boolean whetherTrimAllCol, boolean whetherTrimCol) {

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_22);
    
                    if(whetherTrimAllCol || whetherTrimCol) {

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(trimMethod);
    stringBuffer.append(TEXT_26);
    
                    }

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    
                        if(("Character").equals(typeToGenerate)) {

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_41);
    
                        } else {

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_46);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_47);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_48);
    
                        }

    stringBuffer.append(TEXT_49);
    
		}
		
	    public void generateTimestampResultSet(String firstConnName, IMetadataColumn column, int currentColNo) {

    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_58);
    
	    }
	    
	    public void generateStringResultSet(String firstConnName, IMetadataColumn column, int currentColNo, String trimMethod) {

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(trimMethod);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_70);
    
	    }
	    
	    public void generateBytesResultSet(String firstConnName, IMetadataColumn column, int currentColNo) {
	    }
	    
	    public void generateOthersResultSet(String firstConnName, IMetadataColumn column, int currentColNo, String typeToGenerate) {

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_79);
    
                if(column.isNullable()) {
                    
    stringBuffer.append(TEXT_80);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_82);
    
                } else {
                    
    stringBuffer.append(TEXT_83);
        
                }
                
    stringBuffer.append(TEXT_84);
    
	    }
	    //---------end according schema type to generate ResultSet
	    
		public void afterGenertorType(String firstConnName, IMetadataColumn column, int currentColNo) {
	    }
	    
		public void afterComponentProcess(INode node){
	    }
	}//end DefaultDBInputUtil class
	
	DefaultDBInputUtil dbInputBeginUtil = new DefaultDBInputUtil();
	
	

    

	class DBInputBeginUtil extends DefaultDBInputUtil{
	
		private INode node = null;
		
		public void beforeComponentProcess(INode node){
	    	super.beforeComponentProcess(node);
	    	this.node = node;
	    }
	
		public void setURL(INode node) {
			
			String localServiceName = ElementParameterParser.getValue(node, "__LOCAL_SERVICE_NAME__");
			String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__");
			String jdbcURL = ElementParameterParser.getValue(node, "__JDBC_URL__");
    		String rac_url = ElementParameterParser.getValue(node, "__RAC_URL__");

    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    
				if("ORACLE_RAC".equals(connectionType)) {

    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(rac_url);
    stringBuffer.append(TEXT_89);
    
    			} else if(("ORACLE_SID").equals(connectionType)) {

    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_94);
    
            	} else if(("ORACLE_SERVICE_NAME").equals(connectionType)) {

    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_99);
    
            	} else if(("ORACLE_OCI").equals(connectionType)) {
            	    
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(localServiceName);
    stringBuffer.append(TEXT_102);
    
            	}else if(("ORACLE_WALLET").equals(connectionType)) {

    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(jdbcURL);
    stringBuffer.append(TEXT_105);
    
				}

    stringBuffer.append(TEXT_106);
    
		}
		
		public String getDirverClassName(INode node){
			String dbVersion =  ElementParameterParser.getValue(node, "__DB_VERSION__"); 
			if("ojdbc5-11g.jar".equals(dbVersion) || "ojdbc6-11g.jar".equals(dbVersion) ){
			    return "oracle.jdbc.OracleDriver";	
			}else {
				return "oracle.jdbc.driver.OracleDriver";	
			}
		}
		
		public void createConnection(INode node) {
			cid = node.getUniqueName();
			dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
			dbhost = ElementParameterParser.getValue(node, "__HOST__");
	    	dbport = ElementParameterParser.getValue(node, "__PORT__");
	    	dbname = ElementParameterParser.getValue(node, "__DBNAME__");
	    	dbuser = ElementParameterParser.getValue(node, "__USER__");
	 		dbpwd = ElementParameterParser.getValue(node, "__PASS__");
	 		String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__");
	 		String dbVersion =  ElementParameterParser.getValue(node, "__DB_VERSION__"); 

    stringBuffer.append(TEXT_107);
    stringBuffer.append(this.getDirverClassName(node) );
    stringBuffer.append(TEXT_108);
    this.setURL(node);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_113);
    
			if(("ORACLE_WALLET").equals(connectionType)) {

    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    
			}else if (dbproperties == null || ("\"\"").equals(dbproperties) || ("").equals(dbproperties)) {

    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_121);
    
			} else {

    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
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
    
			}
			if(!"ojdbc12-8i.jar".equals(dbVersion)){
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    
			}
		}
		
		public void afterUseExistConnection(INode node) {
			String dbVersion =  ElementParameterParser.getValue(node, "__DB_VERSION__");
			if(!"ojdbc12-8i.jar".equals(dbVersion)){
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_159);
    
			}
	    }
		
		public void createStatement(INode node) {
			
			String useCursor= ElementParameterParser.getValue(node, "__USE_CURSOR__");
			String cursorSize= ElementParameterParser.getValue(node, "__CURSOR_SIZE__");
			if(!("true").equals(useCursor)) {
				 super.createStatement(node);
			}else {
            	super.createMinValueStatement(node);

    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cursorSize );
    stringBuffer.append(TEXT_162);
    
            }
		}
		//-----------according schema type to generate ResultSet
		public void generateStringResultSet(String firstConnName, IMetadataColumn column, int currentColNo, String trimMethod) {
			String columnType = column.getType();
			if(columnType != null && ("CLOB").equals(columnType)) {

    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_178);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_180);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_181);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_182);
    
			} else {

    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_188);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid);
    stringBuffer.append(trimMethod);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_191);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_192);
    
			}
	    }
	    
	   	public void generateBytesResultSet(String firstConnName, IMetadataColumn column, int currentColNo) {

    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_195);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_197);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_202);
    
	    }
	    
	    public void generateTimestampResultSet(String firstConnName, IMetadataColumn column, int currentColNo) {
	    	boolean noNullValues = ("true").equals(ElementParameterParser.getValue(node, "__NO_NULL_VALUES__"));    
	    	if(noNullValues){

    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_204);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_205);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_206);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_207);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_208);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_209);
    
			}else{
				super.generateTimestampResultSet(firstConnName,column,currentColNo);
			}
	    }

	   	public void generateOthersResultSet(String firstConnName, IMetadataColumn column, int currentColNo, String typeToGenerate) {
		   	boolean noNullValues = ("true").equals(ElementParameterParser.getValue(node, "__NO_NULL_VALUES__"));
	   		boolean isConvert = ("true").equals(ElementParameterParser.getValue(node, "__IS_CONVERT_XMLTYPE__"));    
			List<Map<String, String>> convertColumns = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__CONVERT_XMLTYPE__");  
	   		
			if(("Object").equals(typeToGenerate)){
			
				boolean needConvert = false;
	   			String convertToJavaType = "";
				if (isConvert && convertColumns.size() > 0) {
					for(java.util.Map<String, String> convertcolumn : convertColumns) {
						if (convertcolumn.get("REFCOL").equals(column.getLabel()) ) {
							needConvert = true;
							convertToJavaType = convertcolumn.get("CONVETTYPE");
						}
					}
				} 
				

    stringBuffer.append(TEXT_210);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_213);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_214);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_215);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_216);
    
				if (needConvert && "String".equals(convertToJavaType)) {

    stringBuffer.append(TEXT_217);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_218);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_219);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_220);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_221);
    
				} else {

    stringBuffer.append(TEXT_222);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_223);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_224);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_225);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_226);
    
				}

    stringBuffer.append(TEXT_227);
    
					if(column.isNullable()) {

    stringBuffer.append(TEXT_228);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_229);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_230);
    
					} else {

    stringBuffer.append(TEXT_231);
        
					}

    stringBuffer.append(TEXT_232);
    
			} else {
				if(noNullValues){

    stringBuffer.append(TEXT_233);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_234);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_236);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_237);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_238);
    
					if(("Boolean").equals(typeToGenerate)){
					
    stringBuffer.append(TEXT_239);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_240);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_241);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_242);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_243);
    
					}else if(("Bytes").equals(typeToGenerate) || ("BigDecimal").equals(typeToGenerate)){
					
    stringBuffer.append(TEXT_244);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_245);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_246);
    
					}else if(("Byte").equals(typeToGenerate) || ("Double").equals(typeToGenerate) || ("Float").equals(typeToGenerate) || ("Int").equals(typeToGenerate) || ("Long").equals(typeToGenerate) || ("Short").equals(typeToGenerate)){
					
    stringBuffer.append(TEXT_247);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_248);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_249);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_250);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_251);
    
					}else{
					
    stringBuffer.append(TEXT_252);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_253);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_254);
    
					}
					
    stringBuffer.append(TEXT_255);
    
				}else{
				
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_258);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_259);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_260);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_261);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_263);
    
				}
				
    		
				if(column.isNullable()) {

    stringBuffer.append(TEXT_264);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_265);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_266);
    
				} else {

    stringBuffer.append(TEXT_267);
        
				}

    stringBuffer.append(TEXT_268);
    
			}
	    }	    
	    //---------end according schema type to generate ResultSet
	}//end class
	
	dbInputBeginUtil = new DBInputBeginUtil();

    stringBuffer.append(TEXT_269);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	        
	String type = ElementParameterParser.getValue(node, "__TYPE__");
	String dbhost = ElementParameterParser.getValue(node, "__HOST__");
	String dbport = ElementParameterParser.getValue(node, "__PORT__");
	String dbname = ElementParameterParser.getValue(node, "__DBNAME__");
	String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
	String dbuser = ElementParameterParser.getValue(node, "__USER__");
	String dbpwd = ElementParameterParser.getValue(node, "__PASS__");
	String dbencoding = ElementParameterParser.getValue(node, "__ENCODING__");
	String enableStream = ElementParameterParser.getValue(node, "__ENABLE_STREAM__");
	String dbms=ElementParameterParser.getValue(node, "__MAPPING__");
	
    boolean whetherTrimAllCol = ("true").equals(ElementParameterParser.getValue(node, "__TRIM_ALL_COLUMN__"));
    List<Map<String, String>> trimColumnList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__TRIM_COLUMN__");
    
    dbInputBeginUtil.beforeComponentProcess(node);
    
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas != null) && (metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata != null) {

    stringBuffer.append(TEXT_270);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_271);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_272);
    
		    String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
		    if(("true").equals(useExistingConn)) {
		        String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
		        String conn = "conn_" + connection;

    stringBuffer.append(TEXT_273);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_274);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_275);
    
				dbInputBeginUtil.afterUseExistConnection(node);
		
		    } else {
				dbInputBeginUtil.createConnection(node);
		    }

    stringBuffer.append(TEXT_276);
    dbInputBeginUtil.createStatement(node);
    stringBuffer.append(TEXT_277);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_278);
    stringBuffer.append(dbInputBeginUtil.getQueryString(node));
    stringBuffer.append(TEXT_279);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_280);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_281);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_283);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_286);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_287);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_288);
    
		    List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
		    List<IMetadataColumn> columnList = metadata.getListColumns();
		    boolean isDynamic = metadata.isDynamicSchema();
		    if(isDynamic){
		    	String DynamicDatePattern = "\"dd-MM-yyyy\"";
		    	for(IMetadataColumn column : columnList) {
		    		if("id_Dynamic".equals(column.getTalendType())) {
		    			DynamicDatePattern = column.getPattern();
		    			break;
		    		}
		    	}
		    
    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_290);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_291);
    stringBuffer.append(dbms );
    stringBuffer.append(TEXT_292);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_293);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_294);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_295);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_296);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_297);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_298);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_299);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_300);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_301);
    stringBuffer.append(metadata.getListColumns().size()-1);
    stringBuffer.append(TEXT_302);
    stringBuffer.append(metadata.getListColumns().size());
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_310);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_311);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_312);
    stringBuffer.append(dbms );
    stringBuffer.append(TEXT_313);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_314);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_315);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_316);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_317);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_318);
     if((DynamicDatePattern!=null) && (!"".equals(DynamicDatePattern)) && (!"\"\"".equals(DynamicDatePattern))) {
    stringBuffer.append(TEXT_319);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_320);
    stringBuffer.append(DynamicDatePattern);
    stringBuffer.append(TEXT_321);
     } 
    stringBuffer.append(TEXT_322);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_323);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_324);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_325);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_326);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_327);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_328);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_329);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_330);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_331);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_332);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_333);
    
		    }
		    
    stringBuffer.append(TEXT_334);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_335);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_336);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_337);
    
		        if(conns != null && conns.size() > 0) {
		            IConnection conn = conns.get(0);
		            String firstConnName = conn.getName();
		            int currentColNo = 1;
		            for(IMetadataColumn column : columnList) {
		                boolean whetherTrimCol = false;
		                if((trimColumnList != null && trimColumnList.size() > 0) && !whetherTrimAllCol) {
		                    for(Map<String, String> trimColumn : trimColumnList) {
		                        if(column.getLabel().equals(trimColumn.get("SCHEMA_COLUMN"))) {
		                            if(("true").equals(trimColumn.get("TRIM"))) {
		                                whetherTrimCol = true;
		                                break;
		                            }
		                        }
		                    }
		                }
		                String trimMethod = "";
		                if(whetherTrimAllCol || whetherTrimCol) {
		                    trimMethod = ".trim()";
		                }
		                String columnType = column.getType();
		                 
		                String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
		                String defVal = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate); 	
		                if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
		                    
    stringBuffer.append(TEXT_338);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_339);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_340);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_341);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_342);
    stringBuffer.append(defVal);
    stringBuffer.append(TEXT_343);
    
		                        typeToGenerate = dbInputBeginUtil.mappingType(typeToGenerate);
		  
		                        if(("Char").equals(typeToGenerate) || ("Character").equals(typeToGenerate)) {

    stringBuffer.append(TEXT_344);
    dbInputBeginUtil.generateStringCharAndCharacterSet(firstConnName, column,currentColNo, trimMethod, typeToGenerate, whetherTrimAllCol, whetherTrimCol);
    stringBuffer.append(TEXT_345);
    
		                        } else if(("Timestamp").equals(typeToGenerate)) {

    stringBuffer.append(TEXT_346);
    dbInputBeginUtil.generateTimestampResultSet(firstConnName, column, currentColNo);
    
		                         } else if (("List").equals(typeToGenerate)) {

    stringBuffer.append(TEXT_347);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_348);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_349);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_350);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_351);
    
		                        } else if(("String").equals(typeToGenerate)) {

    stringBuffer.append(TEXT_352);
    dbInputBeginUtil.generateStringResultSet(firstConnName, column, currentColNo,trimMethod);
    
								} else if("Geometry".equals(typeToGenerate) && type.indexOf("ORACLE") >= 0) {

    stringBuffer.append(TEXT_353);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_354);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_355);
    stringBuffer.append(TEXT_356);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_357);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_358);
    
                                    String sourceCRS = ElementParameterParser.getValue(node,"__CRS__");
                                    String forceCRS = ElementParameterParser.getValue(node,"__FORCE_CRS__");
                                    if (forceCRS.equals("true")) {

    stringBuffer.append(TEXT_359);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_360);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_361);
    stringBuffer.append(sourceCRS);
    stringBuffer.append(TEXT_362);
    
                                    }
								} else if(("Bytes").equals(typeToGenerate) && (columnType != null && (("LONG RAW").equals(columnType) || ("RAW").equals(columnType)))) {//oracle

    stringBuffer.append(TEXT_363);
    dbInputBeginUtil.generateBytesResultSet(firstConnName, column, currentColNo);
    
								} else if(("Dynamic").equals(typeToGenerate)) {

    stringBuffer.append(TEXT_364);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_365);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_366);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_367);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_368);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_369);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_370);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_371);
    
								} else if(typeToGenerate.equals("Geometry")) {

    stringBuffer.append(TEXT_372);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_373);
    stringBuffer.append(currentColNo);
    stringBuffer.append(TEXT_374);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_375);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_376);
    
                 	            } else {

    stringBuffer.append(TEXT_377);
    dbInputBeginUtil.generateOthersResultSet( firstConnName, column,  currentColNo,  typeToGenerate);
    
		                        }

    stringBuffer.append(TEXT_378);
    dbInputBeginUtil.afterGenertorType( firstConnName, column,  currentColNo);
    stringBuffer.append(TEXT_379);
      
		                    currentColNo++;
		                }
		            }
		            if(conns.size() > 1) {
		                for(int connNO = 1 ; connNO < conns.size() ; connNO++) {
		                    IConnection conn2 = conns.get(connNO);
		                    if((conn2.getName().compareTo(firstConnName) != 0) && (conn2.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
		                        for(IMetadataColumn column:columnList){
		                            
    stringBuffer.append(TEXT_380);
    stringBuffer.append(conn2.getName());
    stringBuffer.append(TEXT_381);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_382);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_383);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_384);
     
		                        }
		                    }
		                }
		            }
		        }
		}
	}

    stringBuffer.append(TEXT_385);
    stringBuffer.append(TEXT_386);
    return stringBuffer.toString();
  }
}
