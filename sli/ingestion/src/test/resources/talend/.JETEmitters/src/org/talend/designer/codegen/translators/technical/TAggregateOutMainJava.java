package org.talend.designer.codegen.translators.technical;

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

public class TAggregateOutMainJava
{
  protected static String nl;
  public static synchronized TAggregateOutMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAggregateOutMainJava result = new TAggregateOutMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = "operation_finder_";
  protected final String TEXT_4 = ".";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ".clone();" + NL + "\t\t\t";
  protected final String TEXT_8 = "operation_finder_";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = " = ";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = ";" + NL + "\t\t\t";
  protected final String TEXT_13 = NL + NL + "\toperation_finder_";
  protected final String TEXT_14 = ".hashCodeDirty = true;" + NL + "\t" + NL + "\toperation_result_";
  protected final String TEXT_15 = " = hash_";
  protected final String TEXT_16 = ".get(operation_finder_";
  protected final String TEXT_17 = ");" + NL + "" + NL + "\t";
  protected final String TEXT_18 = NL + "\t\tboolean isFirstAdd_";
  protected final String TEXT_19 = " = false;" + NL + "\t";
  protected final String TEXT_20 = NL + NL + "\tif(operation_result_";
  protected final String TEXT_21 = " == null) { // G_OutMain_AggR_001" + NL + "" + NL + "\t\toperation_result_";
  protected final String TEXT_22 = " = new AggOperationStruct_";
  protected final String TEXT_23 = "();" + NL + "" + NL + "\t\t";
  protected final String TEXT_24 = "operation_result_";
  protected final String TEXT_25 = ".";
  protected final String TEXT_26 = " = operation_finder_";
  protected final String TEXT_27 = ".";
  protected final String TEXT_28 = ".clone();" + NL + "\t\t\t\t";
  protected final String TEXT_29 = "operation_result_";
  protected final String TEXT_30 = ".";
  protected final String TEXT_31 = " = operation_finder_";
  protected final String TEXT_32 = ".";
  protected final String TEXT_33 = ";" + NL + "\t\t\t\t";
  protected final String TEXT_34 = NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_35 = NL + "\t\t\tisFirstAdd_";
  protected final String TEXT_36 = " = true;" + NL + "\t\t";
  protected final String TEXT_37 = NL + NL + "\t\thash_";
  protected final String TEXT_38 = ".put(operation_result_";
  protected final String TEXT_39 = ", operation_result_";
  protected final String TEXT_40 = ");" + NL + "\t" + NL + "\t} // G_OutMain_AggR_001" + NL + "" + NL + "" + NL + "\t";
  protected final String TEXT_41 = NL + "\t\t\t\tif(";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = " != null) { // G_OutMain_AggR_546" + NL + "\t\t\t\t";
  protected final String TEXT_44 = NL + "\t\t\t\t" + NL + "\t\t\t\tAggCountDistinctValuesStruct_";
  protected final String TEXT_45 = "_";
  protected final String TEXT_46 = " countDistinctValues_";
  protected final String TEXT_47 = "_";
  protected final String TEXT_48 = " = new AggCountDistinctValuesStruct_";
  protected final String TEXT_49 = "_";
  protected final String TEXT_50 = "();" + NL + "\t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_51 = "countDistinctValues_";
  protected final String TEXT_52 = "_";
  protected final String TEXT_53 = ".";
  protected final String TEXT_54 = " = ";
  protected final String TEXT_55 = ".";
  protected final String TEXT_56 = ";" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_57 = NL + "\t\t\t\tcountDistinctValues_";
  protected final String TEXT_58 = "_";
  protected final String TEXT_59 = ".";
  protected final String TEXT_60 = " = ";
  protected final String TEXT_61 = ".";
  protected final String TEXT_62 = ";" + NL + "\t\t\t\toperation_result_";
  protected final String TEXT_63 = ".distinctValues_";
  protected final String TEXT_64 = ".add(countDistinctValues_";
  protected final String TEXT_65 = "_";
  protected final String TEXT_66 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_67 = NL + "\t\t\t\toperation_result_";
  protected final String TEXT_68 = ".";
  protected final String TEXT_69 = "_clmCount++;";
  protected final String TEXT_70 = NL + "\t\t\t\toperation_result_";
  protected final String TEXT_71 = ".count++;" + NL + "\t\t\t\t";
  protected final String TEXT_72 = NL + "\t\t\t\toperation_result_";
  protected final String TEXT_73 = ".";
  protected final String TEXT_74 = "_count++;" + NL + "\t\t\t\t";
  protected final String TEXT_75 = NL + "\t\t\t\t\tif( " + NL + "\t\t\t\t\t\t";
  protected final String TEXT_76 = NL + "\t\t\t\t\t\t\toperation_result_";
  protected final String TEXT_77 = ".";
  protected final String TEXT_78 = "_";
  protected final String TEXT_79 = " == null || operation_result_";
  protected final String TEXT_80 = ".";
  protected final String TEXT_81 = "_";
  protected final String TEXT_82 = " instanceof java.lang.Comparable && " + NL + "\t\t\t\t\t\t\t((java.lang.Comparable) ";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = ").compareTo(operation_result_";
  protected final String TEXT_85 = ".";
  protected final String TEXT_86 = "_";
  protected final String TEXT_87 = ") ";
  protected final String TEXT_88 = " 0" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_89 = NL + "\t\t\t\t\t\t\toperation_result_";
  protected final String TEXT_90 = ".";
  protected final String TEXT_91 = "_";
  protected final String TEXT_92 = " == null || ";
  protected final String TEXT_93 = ".";
  protected final String TEXT_94 = ".compareTo(operation_result_";
  protected final String TEXT_95 = ".";
  protected final String TEXT_96 = "_";
  protected final String TEXT_97 = ") ";
  protected final String TEXT_98 = " 0" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_99 = NL + "\t\t\t\t\t) {" + NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_100 = ".";
  protected final String TEXT_101 = "_";
  protected final String TEXT_102 = " = ";
  protected final String TEXT_103 = ".";
  protected final String TEXT_104 = ";" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_105 = NL + "\t\t\t\t\tif(" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_106 = "operation_result_";
  protected final String TEXT_107 = ".";
  protected final String TEXT_108 = "_";
  protected final String TEXT_109 = " == null || ";
  protected final String TEXT_110 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_111 = ".";
  protected final String TEXT_112 = " ";
  protected final String TEXT_113 = " operation_result_";
  protected final String TEXT_114 = ".";
  protected final String TEXT_115 = "_";
  protected final String TEXT_116 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_117 = " || isFirstAdd_";
  protected final String TEXT_118 = " ";
  protected final String TEXT_119 = NL + "\t\t\t\t\t) {" + NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = "_";
  protected final String TEXT_122 = " = ";
  protected final String TEXT_123 = ".";
  protected final String TEXT_124 = ";" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_125 = NL + "\t\t\t\t\tif(operation_result_";
  protected final String TEXT_126 = ".";
  protected final String TEXT_127 = "_";
  protected final String TEXT_128 = " == null) {" + NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_129 = ".";
  protected final String TEXT_130 = "_";
  protected final String TEXT_131 = " = (";
  protected final String TEXT_132 = ") 0;" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_133 = NL + "\t\t\t\t\tif(operation_result_";
  protected final String TEXT_134 = ".";
  protected final String TEXT_135 = "_";
  protected final String TEXT_136 = " == null) {" + NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_137 = ".";
  protected final String TEXT_138 = "_";
  protected final String TEXT_139 = " = new BigDecimal(0)";
  protected final String TEXT_140 = ";" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_141 = ".";
  protected final String TEXT_142 = "_";
  protected final String TEXT_143 = " = operation_result_";
  protected final String TEXT_144 = ".";
  protected final String TEXT_145 = "_";
  protected final String TEXT_146 = ".add(" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_147 = "new BigDecimal(";
  protected final String TEXT_148 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_149 = "String.valueOf(";
  protected final String TEXT_150 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_151 = ".";
  protected final String TEXT_152 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_153 = ")";
  protected final String TEXT_154 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_155 = ")";
  protected final String TEXT_156 = NL + "\t\t\t\t\t);" + NL + "\t\t\t\t\t";
  protected final String TEXT_157 = "utilClass_";
  protected final String TEXT_158 = ".checkedIADD( (";
  protected final String TEXT_159 = ") operation_result_";
  protected final String TEXT_160 = ".";
  protected final String TEXT_161 = "_";
  protected final String TEXT_162 = ", (";
  protected final String TEXT_163 = ") ";
  protected final String TEXT_164 = ".";
  protected final String TEXT_165 = ", ";
  protected final String TEXT_166 = ".doubleValue(), ";
  protected final String TEXT_167 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_168 = NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_169 = ".";
  protected final String TEXT_170 = "_";
  protected final String TEXT_171 = " += ";
  protected final String TEXT_172 = ".";
  protected final String TEXT_173 = ".doubleValue();" + NL + "\t\t\t\t\t";
  protected final String TEXT_174 = "utilClass_";
  protected final String TEXT_175 = ".checkedIADD( (";
  protected final String TEXT_176 = ") operation_result_";
  protected final String TEXT_177 = ".";
  protected final String TEXT_178 = "_";
  protected final String TEXT_179 = ", (";
  protected final String TEXT_180 = ") ";
  protected final String TEXT_181 = ".";
  protected final String TEXT_182 = ", ";
  protected final String TEXT_183 = ", ";
  protected final String TEXT_184 = ");" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_185 = "operation_result_";
  protected final String TEXT_186 = ".";
  protected final String TEXT_187 = "_";
  protected final String TEXT_188 = " = (";
  protected final String TEXT_189 = ")(operation_result_";
  protected final String TEXT_190 = ".";
  protected final String TEXT_191 = "_";
  protected final String TEXT_192 = ".";
  protected final String TEXT_193 = "Value() + ";
  protected final String TEXT_194 = ".";
  protected final String TEXT_195 = ".";
  protected final String TEXT_196 = "Value());" + NL + "\t\t\t\t\t";
  protected final String TEXT_197 = NL + "\t\t\t\t\tif( ";
  protected final String TEXT_198 = ".";
  protected final String TEXT_199 = " != null)" + NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_200 = ".";
  protected final String TEXT_201 = "_";
  protected final String TEXT_202 = " += ";
  protected final String TEXT_203 = ".";
  protected final String TEXT_204 = ";";
  protected final String TEXT_205 = NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_206 = ".";
  protected final String TEXT_207 = "_";
  protected final String TEXT_208 = " += ";
  protected final String TEXT_209 = ".";
  protected final String TEXT_210 = ";" + NL + "\t\t\t\t\t";
  protected final String TEXT_211 = NL + "\t\t\t\tif(isFirstAdd_";
  protected final String TEXT_212 = " ";
  protected final String TEXT_213 = " || operation_result_";
  protected final String TEXT_214 = ".";
  protected final String TEXT_215 = "_";
  protected final String TEXT_216 = " == null";
  protected final String TEXT_217 = ") {" + NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_218 = ".";
  protected final String TEXT_219 = "_";
  protected final String TEXT_220 = " = ";
  protected final String TEXT_221 = ".";
  protected final String TEXT_222 = ";" + NL + "\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_223 = NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_224 = ".";
  protected final String TEXT_225 = "_";
  protected final String TEXT_226 = " = ";
  protected final String TEXT_227 = ".";
  protected final String TEXT_228 = ";" + NL + "\t\t\t\t";
  protected final String TEXT_229 = NL + "\t\t\t\tif(operation_result_";
  protected final String TEXT_230 = ".";
  protected final String TEXT_231 = "_";
  protected final String TEXT_232 = ".length() > 0) {" + NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_233 = ".";
  protected final String TEXT_234 = "_";
  protected final String TEXT_235 = ".append(";
  protected final String TEXT_236 = ");" + NL + "\t\t\t\t} " + NL + "\t\t\t\t";
  protected final String TEXT_237 = NL + "\t\t\t\t\tif(operation_result_";
  protected final String TEXT_238 = ".";
  protected final String TEXT_239 = "_";
  protected final String TEXT_240 = " != null) {" + NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_241 = ".";
  protected final String TEXT_242 = "_";
  protected final String TEXT_243 = " = operation_result_";
  protected final String TEXT_244 = ".";
  protected final String TEXT_245 = "_";
  protected final String TEXT_246 = ".append(java.util.Arrays.toString(";
  protected final String TEXT_247 = ".";
  protected final String TEXT_248 = "));" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_249 = "else if(operation_result_";
  protected final String TEXT_250 = ".";
  protected final String TEXT_251 = "_";
  protected final String TEXT_252 = "_firstEmpty){" + NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_253 = ".";
  protected final String TEXT_254 = "_";
  protected final String TEXT_255 = ".append(";
  protected final String TEXT_256 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t\t\tif(operation_result_";
  protected final String TEXT_257 = ".";
  protected final String TEXT_258 = "_";
  protected final String TEXT_259 = " != null) {" + NL + "\t\t\t\t\t\tif(operation_result_";
  protected final String TEXT_260 = ".";
  protected final String TEXT_261 = "_";
  protected final String TEXT_262 = "_firstEmpty==false && (\"\").equals(String.valueOf(";
  protected final String TEXT_263 = ".";
  protected final String TEXT_264 = "))){" + NL + "\t\t\t\t\t\t\toperation_result_";
  protected final String TEXT_265 = ".";
  protected final String TEXT_266 = "_";
  protected final String TEXT_267 = "_firstEmpty = true;" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\toperation_result_";
  protected final String TEXT_268 = ".";
  protected final String TEXT_269 = "_";
  protected final String TEXT_270 = " = operation_result_";
  protected final String TEXT_271 = ".";
  protected final String TEXT_272 = "_";
  protected final String TEXT_273 = ".append(String.valueOf(";
  protected final String TEXT_274 = ".";
  protected final String TEXT_275 = "));" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_276 = "// Load first one or union" + NL + "\t\t\t\tif (operation_result_";
  protected final String TEXT_277 = ".";
  protected final String TEXT_278 = "_";
  protected final String TEXT_279 = " == null) {" + NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_280 = ".";
  protected final String TEXT_281 = "_";
  protected final String TEXT_282 = " = ";
  protected final String TEXT_283 = ".";
  protected final String TEXT_284 = ";" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\toperation_result_";
  protected final String TEXT_285 = ".";
  protected final String TEXT_286 = "_";
  protected final String TEXT_287 = " = operation_result_";
  protected final String TEXT_288 = ".";
  protected final String TEXT_289 = "_";
  protected final String TEXT_290 = ".union(";
  protected final String TEXT_291 = ".";
  protected final String TEXT_292 = ");" + NL + "\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_293 = NL + "\t\t\t\toperation_result_";
  protected final String TEXT_294 = ".";
  protected final String TEXT_295 = "_";
  protected final String TEXT_296 = ".add(";
  protected final String TEXT_297 = ".";
  protected final String TEXT_298 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_299 = "operation_result_";
  protected final String TEXT_300 = ".";
  protected final String TEXT_301 = "_";
  protected final String TEXT_302 = ".add(";
  protected final String TEXT_303 = ".";
  protected final String TEXT_304 = ".doubleValue());" + NL + "\t\t\t\t\t";
  protected final String TEXT_305 = "operation_result_";
  protected final String TEXT_306 = ".";
  protected final String TEXT_307 = "_";
  protected final String TEXT_308 = ".add((double)";
  protected final String TEXT_309 = ".";
  protected final String TEXT_310 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_311 = NL + "\t\t\t\t} // G_OutMain_AggR_546" + NL + "\t\t\t\t";
  protected final String TEXT_312 = NL;
  protected final String TEXT_313 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String origin = ElementParameterParser.getValue(node, "__DESTINATION__");
String cid = origin;

boolean useFinancialPrecision = "true".equals(ElementParameterParser.getValue(node, "__USE_FINANCIAL_PRECISION__"));

boolean checkTypeOverflow = "true".equals(ElementParameterParser.getValue(node, "__CHECK_TYPE_OVERFLOW__"));
boolean checkUlp = "true".equals(ElementParameterParser.getValue(node, "__CHECK_ULP__"));
String listDelimiter = ElementParameterParser.getValue(node, "__LIST_DELIMITER__");

IConnection inputConn = null;
IMetadataTable inputMetadataTable = null;
IMetadataTable outputMetadataTable = null;
java.util.List<IMetadataColumn> inputColumns = null;
java.util.List<IMetadataColumn> outputColumns = null;


int FUNCTION = 0;
int INPUT_COLUMN = 1;
int IGNORE_NULL = 2;
int OUTPUT_COLUMN = 3;

String SUM = "sum";
String COUNT = "count";
String MAX = "max";
String MIN = "min";
String FIRST = "first";
String LAST = "last";
String AVG = "avg";
String COUNT_DISTINCT = "distinct";
String LIST = "list";
String LIST_OBJECT = "list_object";
String STD_DEV = "std_dev";
String UNION = "union";


List<? extends IConnection> incomingConnections = node.getIncomingConnections();
if (incomingConnections != null && !incomingConnections.isEmpty()) {
	for (IConnection conn : incomingConnections) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			inputConn = conn;
			inputMetadataTable = conn.getMetadataTable();
			inputColumns = inputMetadataTable.getListColumns();
			break;
		}
	}
}

List<IMetadataTable> mestadataTableListOut = node.getMetadataList();
if (mestadataTableListOut!=null && mestadataTableListOut.size()>0) { // T_AggR_600
    outputMetadataTable = mestadataTableListOut.get(0);
	if(outputMetadataTable != null) {
		outputColumns = outputMetadataTable.getListColumns();
	}
}

if(inputConn != null) { // T_OutMain_AggR_501

	List<Map<String, String>> operations = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__OPERATIONS__");
	List<Map<String, String>> groupbys = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUPBYS__");
	
	java.util.Map<String,IMetadataColumn> inputKeysColumns = new java.util.HashMap<String,IMetadataColumn>();
	java.util.Map<String,IMetadataColumn> inputValuesColumns = new java.util.HashMap<String,IMetadataColumn>();
	java.util.Map<String,IMetadataColumn> outputValuesColumns = new java.util.HashMap<String,IMetadataColumn>();
	
	int sizeOperations = operations.size();
	int sizeGroupbys = groupbys.size();
	String lastInputColumn = null;
	if(sizeGroupbys>0){
		lastInputColumn = groupbys.get(sizeGroupbys-1).get("INPUT_COLUMN");
	}


	if(inputColumns != null) { // T_AggR_144
		for (IMetadataColumn column: inputColumns) { // T_AggR_145
	
			for(int i = 0; i < sizeGroupbys; i++){ // T_AggR_113
				String columnname = groupbys.get(i).get("INPUT_COLUMN");
				if(column.getLabel().equals(columnname)){ // T_AggR_114
					inputKeysColumns.put(columnname, column);
					break;
	        	} // T_AggR_114
			} // T_AggR_113
					
			for(int i = 0; i < sizeOperations; i++){ // T_AggR_713
				String columnname = operations.get(i).get("INPUT_COLUMN");
	        	if(column.getLabel().equals(columnname)){ // T_AggR_714
	       			inputValuesColumns.put(columnname, column);
					break;
	       		} // T_AggR_714
			} // T_AggR_713
					
		} // T_AggR_145
	} // T_AggR_144
	
	if(outputColumns != null) { // T_AggR_744
		for (IMetadataColumn column: outputColumns) { // T_AggR_745
	
			for(int i = 0; i < sizeOperations; i++){ // T_AggR_713
				String columnname = operations.get(i).get("OUTPUT_COLUMN");
	        	if(column.getLabel().equals(columnname)){ // T_AggR_714
	       			outputValuesColumns.put(columnname, column);
					break;
	       		} // T_AggR_714
			} // T_AggR_713
	
		} // T_AggR_745
	} // T_AggR_744
	
	
	for (IMetadataColumn column : inputColumns) {
		if(inputKeysColumns.containsKey(column.getLabel())) {
			if (column.getTalendType().equals("id_Dynamic")) {
			
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_7);
    
			} else {
			
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_12);
    
			}
		}
	}
	
	
	List<String[]> funinOperations = new java.util.ArrayList<String[]>();
	boolean hasOperationFirst = false;
	next:
	for(int i=0; i<sizeOperations; i++){
		Map<String, String> operation = operations.get(i);
		String fun = operation.get("FUNCTION");
		if(FIRST.equals(fun) || LAST.equals(fun) || MIN.equals(fun) || MAX.equals(fun)) {
			hasOperationFirst = true;
		}
		
		String in = operation.get("INPUT_COLUMN");
		String out = operation.get("OUTPUT_COLUMN");
		String ignoreNull = operation.get("IGNORE_NULL");
		
		/*
		if(("sum").equals(fun) || ("count").equals(fun)){
			for(int j=0; j<sizeOperations; j++){
				Map<String, String> tOperation = operations.get(j);
				if(("avg").equals(tOperation.get("FUNCTION")) && tOperation.get("INPUT_COLUMN").equals(in)){
					continue next;
				}
			}
		}
		for(int j = 0; j < i; j++){ //skip duplicate operation
			Map<String, String> tOperation = operations.get(j);
			if(tOperation.get("FUNCTION").equals(fun) && tOperation.get("INPUT_COLUMN").equals(in)){
				continue next;
			}
		}
		*/
		
		String[] funin = new String[4];
		funin[FUNCTION]=fun;
		funin[INPUT_COLUMN]=in;
		funin[OUTPUT_COLUMN]=out;
		funin[IGNORE_NULL]=ignoreNull;
		funinOperations.add(funin);
	}
	
	
	
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    
	if(hasOperationFirst) {
	
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    
	}
	
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    
		for (IMetadataColumn column : inputColumns) {
			if(inputKeysColumns.containsKey(column.getLabel())) {
				if (column.getTalendType().equals("id_Dynamic")) {
				
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_28);
    
				} else {
				
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_33);
    
				}
			}
		}
		
    stringBuffer.append(TEXT_34);
    
		if(hasOperationFirst) {
		
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    
		}
		
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    
		
	
	
	
	int sizeOps = funinOperations.size();
	String tInputColumn =null;

	boolean hasAlreadyCountProperty = false;
	boolean hasAlreadyCountDistinctProperty = false;
	for(int j = 0; j < sizeOps; j++){ // T_OutMain_AggR_546
		String[] funin = funinOperations.get(j);
		
		String function = funin[FUNCTION];
		String inputColumnName = funin[INPUT_COLUMN];
		String outputColumnName = funin[OUTPUT_COLUMN];
		boolean ignoreNull = ("true").equals(funin[IGNORE_NULL]);

		IMetadataColumn outputColumn = outputValuesColumns.get(outputColumnName);
		IMetadataColumn inputColumn = inputValuesColumns.get(inputColumnName);
		JavaType outputJavaType = JavaTypesManager.getJavaTypeFromId(outputColumn.getTalendType());
		JavaType inputJavaType = JavaTypesManager.getJavaTypeFromId(inputColumn.getTalendType());
		boolean isBasePrimitive = JavaTypesManager.isJavaPrimitiveType(outputJavaType, false);
		boolean isSelectedPrimitive = JavaTypesManager.isJavaPrimitiveType(outputJavaType, outputColumn.isNullable());
		boolean isInputColumnPrimitive = JavaTypesManager.isJavaPrimitiveType(inputJavaType, inputColumn.isNullable());
		String primitiveTypeToGenerate = JavaTypesManager.getTypeToGenerate(outputJavaType.getId(), false);

		boolean outputIsNumber = JavaTypesManager.isNumberType(outputJavaType, false);
		boolean outputIsObject = outputJavaType == JavaTypesManager.OBJECT;
		boolean outputIsGeometry = false;
		boolean inputIsGeometry = false;
		try {
			outputIsGeometry = outputJavaType == JavaTypesManager.getJavaTypeFromId("id_Geometry");
		} catch (IllegalArgumentException e) {
		}
		boolean outputIsList = outputJavaType == JavaTypesManager.LIST;
		boolean outputIsString = outputJavaType == JavaTypesManager.STRING;
		boolean outputIsBigDecimal = outputJavaType == JavaTypesManager.BIGDECIMAL;
		boolean outputIsDate = outputJavaType == JavaTypesManager.DATE;
		boolean outputIsDecimal = outputJavaType == JavaTypesManager.FLOAT || outputJavaType == JavaTypesManager.DOUBLE || outputIsBigDecimal;
		
		boolean outputIsByte = outputJavaType == JavaTypesManager.BYTE;
		boolean outputIsShort = outputJavaType == JavaTypesManager.SHORT;
		
		boolean inputIsNumber = JavaTypesManager.isNumberType(inputJavaType, false);
		boolean inputIsObject = inputJavaType == JavaTypesManager.OBJECT;
		try {
			inputIsGeometry = inputJavaType == JavaTypesManager.getJavaTypeFromId("id_Geometry");
		} catch (IllegalArgumentException e) {
		}
		boolean inputIsBoolean = inputJavaType == JavaTypesManager.BOOLEAN;
		boolean inputIsList = inputJavaType == JavaTypesManager.LIST;
		boolean inputIsString = inputJavaType == JavaTypesManager.STRING;
		boolean inputIsDate = inputJavaType == JavaTypesManager.DATE;
		boolean inputIsBigDecimal = inputJavaType == JavaTypesManager.BIGDECIMAL;
		boolean inputIsByteArray = inputJavaType == JavaTypesManager.BYTE_ARRAY;
		boolean inputIsDecimal = inputJavaType == JavaTypesManager.FLOAT || inputJavaType == JavaTypesManager.DOUBLE || inputIsBigDecimal;

		boolean forceUseBigDecimal = 
			(function.equals(SUM) || function.equals(AVG)) 
			&& inputIsDecimal
			&& outputIsDecimal
			&& useFinancialPrecision
		;
	
		boolean sameInOutType = outputJavaType == inputJavaType;

		boolean isValidTypeForOperation = 
			(function.equals(SUM) || function.equals(AVG)) && inputIsNumber && outputIsNumber
			|| function.equals(MIN) && sameInOutType && !inputIsList && !inputIsByteArray && !inputIsBoolean 
			|| function.equals(MAX) && sameInOutType && !inputIsList && !inputIsByteArray && !inputIsBoolean
			|| function.equals(FIRST) && sameInOutType
			|| function.equals(LAST) && sameInOutType
			|| function.equals(LIST) && outputIsString
			|| function.equals(LIST_OBJECT) && outputIsList
			|| function.equals(COUNT) && outputIsNumber
			|| function.equals(UNION) && outputIsGeometry
			|| function.equals(COUNT_DISTINCT) && outputIsNumber
			|| function.equals(STD_DEV) && inputIsNumber && outputIsNumber
		;
		
		if(isValidTypeForOperation) { // T_OutMain_AggR_745
		
			if(ignoreNull && !isInputColumnPrimitive) { // T_OutMain_AggR_545
			
				
    stringBuffer.append(TEXT_41);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_43);
    
				
			} // T_OutMain_AggR_545
			

			if(function.equals(COUNT_DISTINCT)){
				
    stringBuffer.append(TEXT_44);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    
				for (IMetadataColumn column : inputColumns) {
					if(inputKeysColumns.containsKey(column.getLabel())) {
				
						
    stringBuffer.append(TEXT_51);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_54);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_56);
    
						
					}
				}
				
    stringBuffer.append(TEXT_57);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    
			}
			
			if(function.equals(COUNT)) {

    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_69);
    
			}

			if(!hasAlreadyCountProperty && function.equals(COUNT)) {
				hasAlreadyCountProperty = true;
				
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    
			}
			
			if(outputIsNumber && function.equals(AVG)){
					
				
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_74);
    
				
			}
			if(function.equals(MIN) || function.equals(MAX)){
			
				String operator = ">";
				if(function.equals(MIN)) {
					operator = "<";
				}
	
	
	
				if(inputIsString || inputIsDate || inputIsObject || inputIsBigDecimal) {
				
					
    stringBuffer.append(TEXT_75);
    if(inputIsObject) {
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(operator);
    stringBuffer.append(TEXT_88);
    } else {
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(operator);
    stringBuffer.append(TEXT_98);
    }
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_104);
    
				
				} else {
				
					
    stringBuffer.append(TEXT_105);
     if(outputColumn.isNullable()) { 
							
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_109);
    
						}
    stringBuffer.append(TEXT_110);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(operator);
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_116);
     if(!outputColumn.isNullable()) { 
							
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_118);
    
						}
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_124);
    
				}
			
			} else if(function.equals(SUM) || function.equals(AVG)){
	
				if(!isSelectedPrimitive && isBasePrimitive && !forceUseBigDecimal) {
					
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_127);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_130);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_131);
    stringBuffer.append( primitiveTypeToGenerate );
    stringBuffer.append(TEXT_132);
    
				}
	
				if(outputIsBigDecimal || forceUseBigDecimal) {
	
					
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_135);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_138);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(outputColumn.getPrecision()==null? "":".setScale(" + outputColumn.getPrecision().intValue()+")" );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_142);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_145);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_146);
    if(!inputIsBigDecimal || forceUseBigDecimal) {
							
    stringBuffer.append(TEXT_147);
    
						}
    stringBuffer.append(TEXT_148);
    if(forceUseBigDecimal) {
								
    stringBuffer.append(TEXT_149);
    
							}
    stringBuffer.append(TEXT_150);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_152);
    if(forceUseBigDecimal) {
								
    stringBuffer.append(TEXT_153);
    
							}
    stringBuffer.append(TEXT_154);
    if(!inputIsBigDecimal || forceUseBigDecimal) {
							
    stringBuffer.append(TEXT_155);
    
						}
    stringBuffer.append(TEXT_156);
    
			
				} else if(inputIsBigDecimal && !outputIsBigDecimal) {
				
					if(checkTypeOverflow || checkUlp) {
					
						
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append( primitiveTypeToGenerate);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_162);
    stringBuffer.append( primitiveTypeToGenerate);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_165);
    stringBuffer.append( checkTypeOverflow );
    stringBuffer.append(TEXT_166);
    stringBuffer.append( checkUlp );
    stringBuffer.append(TEXT_167);
    
					}
					
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_170);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_173);
    
				
				} else {
				
					if(checkTypeOverflow || checkUlp) {
					
						
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append( primitiveTypeToGenerate);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_178);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_179);
    stringBuffer.append( primitiveTypeToGenerate);
    stringBuffer.append(TEXT_180);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_182);
    stringBuffer.append( checkTypeOverflow );
    stringBuffer.append(TEXT_183);
    stringBuffer.append( checkUlp );
    stringBuffer.append(TEXT_184);
    
					}
					if(outputColumn.isNullable() && (outputIsByte || outputIsShort)){
					
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_187);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(primitiveTypeToGenerate);
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_191);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_192);
    stringBuffer.append(primitiveTypeToGenerate);
    stringBuffer.append(TEXT_193);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_194);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_195);
    stringBuffer.append(primitiveTypeToGenerate);
    stringBuffer.append(TEXT_196);
    
					}else{
						if(inputColumn.isNullable()  ){
					
    stringBuffer.append(TEXT_197);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_198);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_201);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_202);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_204);
    
					} else {
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_207);
    stringBuffer.append( SUM );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_209);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_210);
    
				    }
				   }
				}
			} else if(function.equals(FIRST)){
					
				
    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_212);
    if(ignoreNull) {
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_215);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_216);
    }
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_218);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_220);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_221);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_222);
    
				
			} else if(function.equals(LAST)){
					
				
    stringBuffer.append(TEXT_223);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_226);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_227);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_228);
    
				
			} else if(function.equals(LIST)){
					
				
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_231);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_232);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_234);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_235);
    stringBuffer.append( listDelimiter );
    stringBuffer.append(TEXT_236);
    
				if(inputIsByteArray) {
				
    stringBuffer.append(TEXT_237);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_238);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_239);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_240);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_241);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_242);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_243);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_244);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_245);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_246);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_247);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_248);
    
				} else {
				
    stringBuffer.append(TEXT_249);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_250);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_251);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_252);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_253);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_254);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_255);
    stringBuffer.append( listDelimiter );
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_258);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_260);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_261);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_262);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_263);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_264);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_265);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_266);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_268);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_269);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_270);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_271);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_272);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_273);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_275);
    
				}
			} else if(function.equals(UNION)){
				
    stringBuffer.append(TEXT_276);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_277);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_278);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_279);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_280);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_281);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_282);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_283);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_285);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_286);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_287);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_288);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_289);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_290);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_291);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_292);
    
 			} else if(function.equals(LIST_OBJECT)){
					
				
    stringBuffer.append(TEXT_293);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_294);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_295);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_296);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_297);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_298);
    
				
			} else if(function.equals(STD_DEV)){

				if(inputIsBigDecimal) {
	
					
    stringBuffer.append(TEXT_299);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_300);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_301);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_302);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_303);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_304);
    
			
				} else {
				
					
    stringBuffer.append(TEXT_305);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_308);
    stringBuffer.append(inputConn.getName() );
    stringBuffer.append(TEXT_309);
    stringBuffer.append(inputColumnName  );
    stringBuffer.append(TEXT_310);
    
					
				}
				
			}
			
			if(ignoreNull && !isInputColumnPrimitive) { // T_OutMain_AggR_545
			
				
    stringBuffer.append(TEXT_311);
    
				
			} // T_OutMain_AggR_545

		} // T_OutMain_AggR_745	
	} // T_OutMain_AggR_546

} // T_OutMain_AggR_501


    stringBuffer.append(TEXT_312);
    stringBuffer.append(TEXT_313);
    return stringBuffer.toString();
  }
}
