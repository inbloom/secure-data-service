package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TAggregateOutBeginJava
{
  protected static String nl;
  public static synchronized TAggregateOutBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAggregateOutBeginJava result = new TAggregateOutBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "// ------------" + NL + "" + NL + "java.util.Map hashAggreg_";
  protected final String TEXT_3 = " = new java.util.HashMap(); " + NL + "" + NL + "// ------------";
  protected final String TEXT_4 = NL + NL + "\tclass UtilClass_";
  protected final String TEXT_5 = " { // G_OutBegin_AggR_144" + NL + "" + NL + "\t\tpublic double sd(Double[] data) {" + NL + "\t        final int n = data.length;" + NL + "        \tif (n < 2) {" + NL + "\t            return Double.NaN;" + NL + "        \t}" + NL + "        \tdouble d1 = 0d;" + NL + "        \tdouble d2 =0d;" + NL + "\t        " + NL + "\t        for (int i = 0; i < data.length; i++) {" + NL + "            \td1 += (data[i]*data[i]);" + NL + "            \td2 += data[i];" + NL + "        \t}" + NL + "        " + NL + "\t        return Math.sqrt((n*d1 - d2*d2)/n/(n-1));" + NL + "\t    }" + NL + "\t    " + NL + "\t\tpublic void checkedIADD(byte a, byte b, boolean checkTypeOverFlow, boolean checkUlp) {" + NL + "\t\t    byte r = (byte) (a + b);" + NL + "\t\t    if (checkTypeOverFlow && ((a ^ r) & (b ^ r)) < 0) {" + NL + "\t\t        throw new RuntimeException(buildOverflowMessage(String.valueOf(a), String.valueOf(b), \"'short/Short'\", \"'int/Integer'\"));" + NL + "\t\t    }" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void checkedIADD(short a, short b, boolean checkTypeOverFlow, boolean checkUlp) {" + NL + "\t\t    short r = (short) (a + b);" + NL + "\t\t    if (checkTypeOverFlow && ((a ^ r) & (b ^ r)) < 0) {" + NL + "\t\t        throw new RuntimeException(buildOverflowMessage(String.valueOf(a), String.valueOf(b), \"'int/Integer'\", \"'short/Short'\"));" + NL + "\t\t    }" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void checkedIADD(int a, int b, boolean checkTypeOverFlow, boolean checkUlp) {" + NL + "\t\t    int r = a + b;" + NL + "\t\t    if (checkTypeOverFlow && ((a ^ r) & (b ^ r)) < 0) {" + NL + "\t\t        throw new RuntimeException(buildOverflowMessage(String.valueOf(a), String.valueOf(b), \"'long/Long'\", \"'int/Integer'\"));" + NL + "\t\t    }" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void checkedIADD(long a, long b, boolean checkTypeOverFlow, boolean checkUlp) {" + NL + "\t\t    long r = a + b;" + NL + "\t\t    if (checkTypeOverFlow && ((a ^ r) & (b ^ r)) < 0) {" + NL + "\t\t        throw new RuntimeException(buildOverflowMessage(String.valueOf(a), String.valueOf(b), \"'BigDecimal'\", \"'long/Long'\"));" + NL + "\t\t    }" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void checkedIADD(float a, float b, boolean checkTypeOverFlow, boolean checkUlp) {" + NL + "\t\t" + NL + "\t\t\tif(checkUlp) {" + NL + "\t\t\t    float minAddedValue = Math.ulp(a);" + NL + "\t\t\t    if (minAddedValue > b) {" + NL + "\t\t\t        throw new RuntimeException(buildPrecisionMessage(String.valueOf(a), String.valueOf(b), \"'double' or 'BigDecimal'\", \"'float/Float'\"));" + NL + "\t\t\t    }" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t    if (checkTypeOverFlow && ((double) a + (double) b > (double) Float.MAX_VALUE) || ((double) a + (double) b < (double) Float.MIN_VALUE)) {" + NL + "\t\t        throw new RuntimeException(buildOverflowMessage(String.valueOf(a), String.valueOf(b), \"'double' or 'BigDecimal'\", \"'float/Float'\"));" + NL + "\t\t    }" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void checkedIADD(double a, double b, boolean checkTypeOverFlow, boolean checkUlp) {" + NL + "\t\t" + NL + "\t\t\tif(checkUlp) {" + NL + "\t\t\t    double minAddedValue = Math.ulp(a);" + NL + "\t\t\t    if (minAddedValue > b) {" + NL + "\t\t\t        throw new RuntimeException(buildPrecisionMessage(String.valueOf(a), String.valueOf(a), \"'BigDecimal'\", \"'double/Double'\"));" + NL + "\t\t\t    }" + NL + "\t\t\t}" + NL + "\t\t" + NL + "\t\t    if (checkTypeOverFlow && (a + b > (double) Double.MAX_VALUE) || (a + b < Double.MIN_VALUE)) {" + NL + "\t\t        throw new RuntimeException(buildOverflowMessage(String.valueOf(a), String.valueOf(b), \"'BigDecimal'\", \"'double/Double'\"));" + NL + "\t\t    }" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tprivate String buildOverflowMessage(String a, String b, String advicedTypes, String originalType) {" + NL + "\t\t    return \"Type overflow when adding \" + b + \" to \" + a" + NL + "\t\t    + \", to resolve this problem, increase the precision by using \"+ advicedTypes +\" type in place of \"+ originalType +\".\";" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tprivate String buildPrecisionMessage(String a, String b, String advicedTypes, String originalType) {" + NL + "\t\t    return \"The double precision is unsufficient to add the value \" + b + \" to \" + a" + NL + "\t\t    + \", to resolve this problem, increase the precision by using \"+ advicedTypes +\" type in place of \"+ originalType +\".\";" + NL + "\t\t}" + NL + "" + NL + "\t} // G_OutBegin_AggR_144" + NL + "" + NL + "\tUtilClass_";
  protected final String TEXT_6 = " utilClass_";
  protected final String TEXT_7 = " = new UtilClass_";
  protected final String TEXT_8 = "();" + NL + "" + NL + "\t";
  protected final String TEXT_9 = NL + "\t" + NL + "\t\tclass AggCountDistinctValuesStruct_";
  protected final String TEXT_10 = "_";
  protected final String TEXT_11 = " { // G_OutBegin_AggR_1100" + NL + "\t" + NL + "\t\t\tprivate static final int DEFAULT_HASHCODE = 1;" + NL + "\t\t    private static final int PRIME = 31;" + NL + "\t\t    private int hashCode = DEFAULT_HASHCODE;" + NL + "\t\t    public boolean hashCodeDirty = true;" + NL + "\t" + NL + "\t        ";
  protected final String TEXT_12 = NL + "    \t\t\t\t\t";
  protected final String TEXT_13 = " ";
  protected final String TEXT_14 = ";";
  protected final String TEXT_15 = "        " + NL + "\t        " + NL + "\t\t    @Override" + NL + "\t\t\tpublic int hashCode() {" + NL + "\t\t\t\tif (this.hashCodeDirty) {" + NL + "\t\t\t\t\tfinal int prime = PRIME;" + NL + "\t\t\t\t\tint result = DEFAULT_HASHCODE;" + NL + "\t\t\t";
  protected final String TEXT_16 = NL + "\t\t\t\t        \t\t\tresult = prime * result + (this.";
  protected final String TEXT_17 = " ? 1231 : 1237);" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\t\t\t\t\t\tresult = prime * result + (int) this.";
  protected final String TEXT_19 = ";" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_20 = NL + "\t\t\t\t\t\t\t\tresult = prime * result + java.util.Arrays.hashCode(this.";
  protected final String TEXT_21 = ");" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_22 = NL + "\t\t\t\t\t\t\t\tresult = prime * result + ((this.";
  protected final String TEXT_23 = " == null) ? 0 : this.";
  protected final String TEXT_24 = ".hashCode());" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_25 = NL + "\t\t    \t\tthis.hashCode = result;" + NL + "\t\t    \t\tthis.hashCodeDirty = false;\t\t" + NL + "\t\t\t\t}" + NL + "\t\t\t\treturn this.hashCode;" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\t@Override" + NL + "\t\t\tpublic boolean equals(Object obj) {" + NL + "\t\t\t\tif (this == obj) return true;" + NL + "\t\t\t\tif (obj == null) return false;" + NL + "\t\t\t\tif (getClass() != obj.getClass()) return false;" + NL + "\t\t\t\tfinal AggCountDistinctValuesStruct_";
  protected final String TEXT_26 = "_";
  protected final String TEXT_27 = " other = (AggCountDistinctValuesStruct_";
  protected final String TEXT_28 = "_";
  protected final String TEXT_29 = ") obj;" + NL + "\t\t\t\t";
  protected final String TEXT_30 = NL + "\t\t\t\t\t\t\t\t\tif (this.";
  protected final String TEXT_31 = " != other.";
  protected final String TEXT_32 = ") " + NL + "\t\t\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_33 = NL + "\t\t\t\t\t\t\t\t\tif(!java.util.Arrays.equals(this.";
  protected final String TEXT_34 = ", other.";
  protected final String TEXT_35 = ")) {" + NL + "\t\t\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_36 = NL + "\t\t\t\t\t\t\t\t\tif (this.";
  protected final String TEXT_37 = " == null) {" + NL + "\t\t\t\t\t\t\t\t\t\tif (other.";
  protected final String TEXT_38 = " != null) " + NL + "\t\t\t\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t\t\t\t} else if (!this.";
  protected final String TEXT_39 = ".equals(other.";
  protected final String TEXT_40 = ")) " + NL + "\t\t\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_41 = NL + "\t\t\t\t" + NL + "\t\t\t\treturn true;" + NL + "\t\t\t}" + NL + "\t  " + NL + "\t        " + NL + "\t\t} // G_OutBegin_AggR_1100" + NL + "" + NL + "\t";
  protected final String TEXT_42 = NL;
  protected final String TEXT_43 = NL + "\tclass AggOperationStruct_";
  protected final String TEXT_44 = " { // G_OutBegin_AggR_100" + NL + "" + NL + "\t\tprivate static final int DEFAULT_HASHCODE = 1;" + NL + "\t    private static final int PRIME = 31;" + NL + "\t    private int hashCode = DEFAULT_HASHCODE;" + NL + "\t    public boolean hashCodeDirty = true;" + NL;
  protected final String TEXT_45 = NL + "    \t\t\t\t";
  protected final String TEXT_46 = " ";
  protected final String TEXT_47 = ";";
  protected final String TEXT_48 = "int count = 0;" + NL + "       \t\t\t";
  protected final String TEXT_49 = "java.util.Set<AggCountDistinctValuesStruct_";
  protected final String TEXT_50 = "_";
  protected final String TEXT_51 = "> distinctValues_";
  protected final String TEXT_52 = " = new java.util.HashSet<AggCountDistinctValuesStruct_";
  protected final String TEXT_53 = "_";
  protected final String TEXT_54 = ">();" + NL + "           \t\t\t";
  protected final String TEXT_55 = "BigDecimal ";
  protected final String TEXT_56 = "_";
  protected final String TEXT_57 = ";" + NL + "           \t\t\t";
  protected final String TEXT_58 = "int ";
  protected final String TEXT_59 = "_clmCount = 0;" + NL + "           \t\t\t";
  protected final String TEXT_60 = "int ";
  protected final String TEXT_61 = "_count = 0;" + NL + "           \t\t\t";
  protected final String TEXT_62 = "StringBuilder ";
  protected final String TEXT_63 = "_";
  protected final String TEXT_64 = " = new StringBuilder();" + NL + "           \t\t\tboolean ";
  protected final String TEXT_65 = "_";
  protected final String TEXT_66 = "_firstEmpty = false;" + NL + "           \t\t\t";
  protected final String TEXT_67 = "java.util.List ";
  protected final String TEXT_68 = "_";
  protected final String TEXT_69 = " = new java.util.ArrayList();" + NL + "           \t\t\t";
  protected final String TEXT_70 = "org.talend.sdi.geometry.Geometry ";
  protected final String TEXT_71 = "_";
  protected final String TEXT_72 = " = null;" + NL + "           \t\t\t";
  protected final String TEXT_73 = "java.util.List<Double> ";
  protected final String TEXT_74 = "_";
  protected final String TEXT_75 = " = new java.util.ArrayList<Double>();" + NL + "           \t\t\t";
  protected final String TEXT_76 = NL + "         \t\t\t";
  protected final String TEXT_77 = " ";
  protected final String TEXT_78 = "_";
  protected final String TEXT_79 = " = (";
  protected final String TEXT_80 = ") ";
  protected final String TEXT_81 = ";";
  protected final String TEXT_82 = NL + "        " + NL + "\t    @Override" + NL + "\t\tpublic int hashCode() {" + NL + "\t\t\tif (this.hashCodeDirty) {" + NL + "\t\t\t\tfinal int prime = PRIME;" + NL + "\t\t\t\tint result = DEFAULT_HASHCODE;" + NL + "\t\t";
  protected final String TEXT_83 = NL + "\t\t\t        \t\t\tresult = prime * result + (this.";
  protected final String TEXT_84 = " ? 1231 : 1237);" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_85 = NL + "\t\t\t\t\t\t\t\tresult = prime * result + (int) this.";
  protected final String TEXT_86 = ";" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_87 = NL + "\t\t\t\t\t\t\tresult = prime * result + java.util.Arrays.hashCode(this.";
  protected final String TEXT_88 = ");" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_89 = NL + "\t\t\t\t\t\t\tresult = prime * result + ((this.";
  protected final String TEXT_90 = " == null) ? 0 : this.";
  protected final String TEXT_91 = ".hashCode());" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_92 = NL + "\t    \t\tthis.hashCode = result;" + NL + "\t    \t\tthis.hashCodeDirty = false;\t\t" + NL + "\t\t\t}" + NL + "\t\t\treturn this.hashCode;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t@Override" + NL + "\t\tpublic boolean equals(Object obj) {" + NL + "\t\t\tif (this == obj) return true;" + NL + "\t\t\tif (obj == null) return false;" + NL + "\t\t\tif (getClass() != obj.getClass()) return false;" + NL + "\t\t\tfinal AggOperationStruct_";
  protected final String TEXT_93 = " other = (AggOperationStruct_";
  protected final String TEXT_94 = ") obj;" + NL + "\t\t\t";
  protected final String TEXT_95 = NL + "\t\t\t\t\t\t\tif (this.";
  protected final String TEXT_96 = " != other.";
  protected final String TEXT_97 = ") " + NL + "\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_98 = NL + "\t\t\t\t\t\t\tif(!java.util.Arrays.equals(this.";
  protected final String TEXT_99 = ", other.";
  protected final String TEXT_100 = ")) {" + NL + "\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_101 = NL + "\t\t\t\t\t\t\tif (this.";
  protected final String TEXT_102 = " == null) {" + NL + "\t\t\t\t\t\t\t\tif (other.";
  protected final String TEXT_103 = " != null) " + NL + "\t\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t\t} else if (!this.";
  protected final String TEXT_104 = ".equals(other.";
  protected final String TEXT_105 = ")) " + NL + "\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_106 = NL + "\t\t\t" + NL + "\t\t\treturn true;" + NL + "\t\t}" + NL + "  " + NL + "        " + NL + "\t} // G_OutBegin_AggR_100" + NL;
  protected final String TEXT_107 = NL + "\tSystem.err.println(" + NL + "\t";
  protected final String TEXT_108 = NL + "\t\t";
  protected final String TEXT_109 = "\"";
  protected final String TEXT_110 = "\"";
  protected final String TEXT_111 = NL + "\t);" + NL + "\t";
  protected final String TEXT_112 = NL + "\tAggOperationStruct_";
  protected final String TEXT_113 = " operation_result_";
  protected final String TEXT_114 = " = null;" + NL + "\tAggOperationStruct_";
  protected final String TEXT_115 = " operation_finder_";
  protected final String TEXT_116 = " = new AggOperationStruct_";
  protected final String TEXT_117 = "();" + NL + "\tjava.util.Map<AggOperationStruct_";
  protected final String TEXT_118 = ",AggOperationStruct_";
  protected final String TEXT_119 = "> hash_";
  protected final String TEXT_120 = " = new java.util.HashMap<AggOperationStruct_";
  protected final String TEXT_121 = ",AggOperationStruct_";
  protected final String TEXT_122 = ">();  " + NL + "\t";
  protected final String TEXT_123 = NL + "\tString delimiter_";
  protected final String TEXT_124 = " = ";
  protected final String TEXT_125 = ";" + NL + "\t";
  protected final String TEXT_126 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String origin = ElementParameterParser.getValue(node, "__DESTINATION__");
String cid = origin;

boolean useFinancialPrecision = "true".equals(ElementParameterParser.getValue(node, "__USE_FINANCIAL_PRECISION__"));

List<Map<String, String>> operations = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__OPERATIONS__");
List<Map<String, String>> groupbys = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUPBYS__");

List<String> warnings = new ArrayList<String>();
        


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    

int FUNCTION = 0;
int INPUT_COLUMN = 1;
int OUTPUT_COLUMN = 2;

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

int sizeOperations = operations.size();

//pretreatment opreations before aggregating
List<String[]> funinOperations = new java.util.ArrayList<String[]>();
boolean listFlag = false;
boolean haveSTD_DEV = false;
boolean hasCountDistinctOperation = false;
java.util.Map<String, String> distinctCols = new java.util.HashMap<String, String>();
next:
for(int i=0; i<sizeOperations; i++){
	Map<String, String> operation = operations.get(i);
	String fun = operation.get("FUNCTION");
	String in = operation.get("INPUT_COLUMN");
	String out = operation.get("OUTPUT_COLUMN");
	if(!listFlag && fun.equals(LIST)){
		listFlag = true;
	}
	if(!haveSTD_DEV && fun.equals(STD_DEV)){
		haveSTD_DEV = true;
	}
	/*
	if(("sum").equals(fun) || ("count").equals(fun)){
		for(int j=0; j<sizeOperations; j++){
			Map<String, String> tOperation = operations.get(j);
			if(("avg").equals(tOperation.get("FUNCTION")) && tOperation.get("INPUT_COLUMN").equals(in)){
				continue next;
			}
		}
	}
	for(int j = 0; j < i; j++){
		Map<String, String> tOperation = operations.get(j);
		if(tOperation.get("FUNCTION").equals(fun) && tOperation.get("INPUT_COLUMN").equals(in)){
			continue next;
		}
	}
	*/
	
	if(fun.equals(COUNT_DISTINCT)){
		distinctCols.put(in, in);
		hasCountDistinctOperation = true;
	}
	if(("avg").equals(fun)){
		String[] funin = new String[3];
		funin[FUNCTION]=SUM;
		funin[INPUT_COLUMN]=in;
		funin[OUTPUT_COLUMN]=out;
		funinOperations.add(funin);
		funin=new String[3];
		funin[FUNCTION]=AVG;
		funin[INPUT_COLUMN]=in;
		funin[OUTPUT_COLUMN]=out;
		funinOperations.add(funin);
	}else{
		String[] funin = new String[3];
		funin[FUNCTION]=fun;
		funin[INPUT_COLUMN]=in;
		funin[OUTPUT_COLUMN]=out;
		funinOperations.add(funin);
	}
}

java.util.Map<String,IMetadataColumn> keysColumns = new java.util.HashMap<String,IMetadataColumn>();
java.util.Map<String,IMetadataColumn> inputValuesColumns = new java.util.HashMap<String,IMetadataColumn>();
java.util.Map<String,IMetadataColumn> outputValuesColumns = new java.util.HashMap<String,IMetadataColumn>();
IMetadataTable inputMetadataTable = null;
IMetadataTable outputMetadataTable = null;
java.util.List<IMetadataColumn> inputColumns = null;
java.util.List<IMetadataColumn> outputColumns = null;

int sizeOps = funinOperations.size();

List<? extends IConnection> incomingConnections = node.getIncomingConnections();
if (incomingConnections != null && !incomingConnections.isEmpty()) {
	for (IConnection conn : incomingConnections) {
		inputMetadataTable = conn.getMetadataTable();
		inputColumns = inputMetadataTable.getListColumns();
		break;
	}
}

List<IMetadataTable> mestadataTableListOut = node.getMetadataList();
if (mestadataTableListOut!=null && mestadataTableListOut.size()>0) { // T_OutBegin_AggR_600
    outputMetadataTable = mestadataTableListOut.get(0);
	if(outputMetadataTable != null) {
		outputColumns = outputMetadataTable.getListColumns();
	}
}


int sizeGroupbys = groupbys.size();
String[] groupby_type = new String[sizeGroupbys];

if(inputColumns != null) { // T_AggR_144
	for (IMetadataColumn column: inputColumns) { // T_AggR_145

		for(int i = 0; i < sizeGroupbys; i++){ // T_AggR_113
			String columnname = groupbys.get(i).get("INPUT_COLUMN");
			if(column.getLabel().equals(columnname)){ // T_AggR_114
				keysColumns.put(columnname, column);
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


if(sizeOps>0){ // T_OutBegin_AggR_114

	
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    
	if(hasCountDistinctOperation) { // T_OutBegin_AggR_160
		java.util.Iterator<String> iter = distinctCols.values().iterator();
		while(iter.hasNext()){
			String distinctName = iter.next();
	
    stringBuffer.append(TEXT_9);
    stringBuffer.append(distinctName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    
	        if (inputMetadataTable !=null) {
    			for (IMetadataColumn column: inputMetadataTable.getListColumns()) { // G_OutBegin_AggR_1143
    				if(keysColumns.containsKey(column.getLabel()) || column.getLabel().equals(distinctName)){
    					
    stringBuffer.append(TEXT_12);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    
    					
    				}
    			} // G_OutBegin_AggR_1143
	        }
			
    stringBuffer.append(TEXT_15);
    
				if (inputMetadataTable !=null) {
					for (IMetadataColumn column: inputMetadataTable.getListColumns()) {
						if (keysColumns.containsKey(column.getLabel()) || column.getLabel().equals(distinctName)) {

							JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
		
							if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
							
							 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
							 	if(javaType == JavaTypesManager.BOOLEAN) {
									
    stringBuffer.append(TEXT_16);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_17);
    
								} else {	
									
    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_19);
    
								}
								
							} else if(javaType == JavaTypesManager.BYTE_ARRAY) {
		
								
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_21);
    
							
							} else {
								
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_24);
    
							}
						
						}
					}
				}
		    
    stringBuffer.append(TEXT_25);
    stringBuffer.append(distinctName );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(distinctName );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    	
				if (inputMetadataTable !=null) {
					for (IMetadataColumn column: inputMetadataTable.getListColumns()) {
						if (keysColumns.containsKey(column.getLabel()) || column.getLabel().equals(distinctName)) {
						
							JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
						
							if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
								
    stringBuffer.append(TEXT_30);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_32);
    
							} else if(javaType == JavaTypesManager.BYTE_ARRAY) {
								
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_35);
    
							} else {
								
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_40);
    
							}
						}
					}
				}
				
    stringBuffer.append(TEXT_41);
    
		}
	} // T_OutBegin_AggR_160
	
    stringBuffer.append(TEXT_42);
    
} // T_OutBegin_AggR_114

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
        if (inputMetadataTable !=null) {
    		for (IMetadataColumn column: inputMetadataTable.getListColumns()) { // G_OutBegin_AggR_143
    			if(keysColumns.containsKey(column.getLabel())){
    
    				
    stringBuffer.append(TEXT_45);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_47);
    
    				
    			}
    		} // G_OutBegin_AggR_143
        }
        boolean hasAlreadyCountProperty = false;
        boolean hasAlreadyDistinctCountProperty = false;
    	for (int i = 0; i<sizeOps; i++) { // T_OutBegin_AggR_131
    		String[] funin = funinOperations.get(i);
               		
			String inputColumnName = funin[INPUT_COLUMN];
			String outputColumnName = funin[OUTPUT_COLUMN];
			String function = funin[FUNCTION];

			IMetadataColumn outputColumn = outputValuesColumns.get(outputColumnName);
			IMetadataColumn inputColumn = inputValuesColumns.get(inputColumnName);
			
			JavaType outputJavaType = JavaTypesManager.getJavaTypeFromId(outputColumn.getTalendType());
			JavaType inputJavaType = JavaTypesManager.getJavaTypeFromId(inputColumn.getTalendType());
			boolean isBasePrimitive = JavaTypesManager.isJavaPrimitiveType(outputJavaType, false);
			boolean isSelectedPrimitive = JavaTypesManager.isJavaPrimitiveType(outputJavaType, outputColumn.isNullable());
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
			
			if(!isValidTypeForOperation) {
				warnings.add("Warning:the operation '" + function + "' for the output column '"+ outputColumn.getLabel() +"' can't be processed because of incompatible input and/or output types");
			}
			
			if(!hasAlreadyCountProperty && function.equals(COUNT)) {
				hasAlreadyCountProperty = true;

				
    stringBuffer.append(TEXT_48);
    
				
			}
       		
       		if(function.equals(COUNT_DISTINCT)) {
           			
    stringBuffer.append(TEXT_49);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(outputColumnName );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(inputColumnName );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    
           	} 
           	else if(isValidTypeForOperation) { // T_OutBegin_AggR_138

				if(forceUseBigDecimal && function.equals(SUM)){

					
    stringBuffer.append(TEXT_55);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_57);
    

           		}else if(function.equals(COUNT)){
           			
    stringBuffer.append(TEXT_58);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_59);
    
           		} else if(function.equals(AVG)) {
					
					
    stringBuffer.append(TEXT_60);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_61);
    
           			 
           		} else if(LIST.equals(function)) {
           			
           			
    stringBuffer.append(TEXT_62);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(LIST );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(LIST );
    stringBuffer.append(TEXT_66);
    
           			
           		 } else if(LIST_OBJECT.equals(function)) {
           		 
           		 	
    stringBuffer.append(TEXT_67);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_69);
    
				
				} else if(UNION.equals(function)) {
           		 
           		 	
    stringBuffer.append(TEXT_70);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_72);
    
  			
           		} else if(STD_DEV.equals(function)) {
           		
           			
    stringBuffer.append(TEXT_73);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(function );
    stringBuffer.append(TEXT_75);
    
           			
           		 } else {  // T_OutBegin_AggR_137
           		
           			// START OF INIT LINE
         			
    stringBuffer.append(TEXT_76);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(outputColumn.getTalendType(), outputColumn.isNullable()) );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(funin[OUTPUT_COLUMN] );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(function );
    
         			
           			if(
           				outputColumn.isNullable() 
           				&& JavaTypesManager.isJavaPrimitiveType(outputColumn.getTalendType(), false) 
           				&& !function.equals(SUM) 
           				&& !function.equals(MIN) 
           				&& !function.equals(MAX) 
           				&& !function.equals(FIRST) 
           				&& !function.equals(LAST) 
           				|| outputJavaType == JavaTypesManager.CHARACTER) { 
           				
           				
    stringBuffer.append(TEXT_79);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(outputColumn.getTalendType(), false) );
    stringBuffer.append(TEXT_80);
    stringBuffer.append( JavaTypesManager.getDefaultValueFromJavaIdType(outputColumn.getTalendType(), false) );
    
           				
               		}
           			
           			
    stringBuffer.append(TEXT_81);
    
           			// END OF INIT LINE
           			
           		} // T_OutBegin_AggR_137
           	} // T_OutBegin_AggR_138
    	} // T_OutBegin_AggR_131
		
    stringBuffer.append(TEXT_82);
    
			if (inputMetadataTable !=null) {
				for (IMetadataColumn column: inputMetadataTable.getListColumns()) {
					if (keysColumns.containsKey(column.getLabel())) {

						JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());

						if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
						
						 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
						 	if(javaType == JavaTypesManager.BOOLEAN) {
							
    stringBuffer.append(TEXT_83);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_84);
    
							} else {	
							
    stringBuffer.append(TEXT_85);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_86);
    
							}
							
						} else if(javaType == JavaTypesManager.BYTE_ARRAY) {

							
    stringBuffer.append(TEXT_87);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_88);
    
						
						} else {
						
							
    stringBuffer.append(TEXT_89);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_91);
    
							
						}
					}
				}
			}
	    
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    	
			if (inputMetadataTable !=null) {
				for (IMetadataColumn column: inputMetadataTable.getListColumns()) {
					if (keysColumns.containsKey(column.getLabel())) {
					
						JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					
						if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
						
    stringBuffer.append(TEXT_95);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_97);
    
						} else if(javaType == JavaTypesManager.BYTE_ARRAY) {
						
    stringBuffer.append(TEXT_98);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_100);
    
						} else {
						
    stringBuffer.append(TEXT_101);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_105);
    
						}
					}
				}
			}
			
    stringBuffer.append(TEXT_106);
    
if(warnings.size() > 0) {
	
    stringBuffer.append(TEXT_107);
    
	boolean isFirst = true;
	for(String warn : warnings) {
		
    stringBuffer.append(TEXT_108);
    stringBuffer.append( !isFirst ? " + \"\\" + "n\" + " : "" );
    stringBuffer.append(TEXT_109);
    stringBuffer.append( warn );
    stringBuffer.append(TEXT_110);
    
		isFirst = false;
	}
    stringBuffer.append(TEXT_111);
    
}
	
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    

if(listFlag){ // G_OutBegin_AggR_192
	
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(ElementParameterParser.getValue(node, "__LIST_DELIMITER__") );
    stringBuffer.append(TEXT_125);
    
} // G_OutBegin_AggR_192

    stringBuffer.append(TEXT_126);
    return stringBuffer.toString();
  }
}
