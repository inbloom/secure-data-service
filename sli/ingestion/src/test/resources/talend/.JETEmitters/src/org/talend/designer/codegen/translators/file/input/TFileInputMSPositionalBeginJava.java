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

public class TFileInputMSPositionalBeginJava
{
  protected static String nl;
  public static synchronized TFileInputMSPositionalBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputMSPositionalBeginJava result = new TFileInputMSPositionalBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\tclass AdvancedPositionalParser_";
  protected final String TEXT_2 = " {" + NL + "\t\tprivate String headerValue;" + NL + "\t\tprivate String connName;" + NL + "\t\tprivate boolean hasStar = false;" + NL + "\t\tprivate boolean checkRowSize = false;" + NL + "\t\tprivate boolean trimAll = false;" + NL + "\t\tprivate String pattern;" + NL + "\t\tprivate int[] begins;" + NL + "\t\tprivate int[] ends;" + NL + "\t\tprivate int[] sizes;" + NL + "\t\tprivate String padding;" + NL + "\t\tprivate String alignment;" + NL + "\t\tprivate int minimumSize = 0;" + NL + "\t\tprivate int numberOfFields = 0;" + NL + "\t\t" + NL + "\t\tpublic AdvancedPositionalParser_";
  protected final String TEXT_3 = "(String connName, String headerValue, String pattern, boolean checkRowSize, boolean trimAll) throws java.lang.Exception {" + NL + "\t\t\tif (connName == null || headerValue == null || pattern == null)" + NL + "\t\t\t\tthrow new java.lang.RuntimeException(\"invalid connName, headerValue, or pattern\");" + NL + "\t\t\tthis.connName = connName;" + NL + "\t\t\tthis.headerValue = headerValue;" + NL + "\t\t\tthis.pattern = pattern;" + NL + "\t\t\tthis.checkRowSize = checkRowSize;" + NL + "\t\t\tthis.trimAll = trimAll;" + NL + "\t\t\tthis.setPattern(pattern);" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t/**" + NL + "\t\t * this function returns true if the header passed is the same as the header of the class" + NL + "\t\t */" + NL + "\t\tpublic boolean headerMatches(String header) {" + NL + "\t\t\treturn headerValue.equals(header);" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t/** " + NL + "\t\t * this function is used to set the pattern specified by user. it also parses the pattern and " + NL + "\t\t * determines where each column position would begin and end, and how many columns does the " + NL + "\t\t * pattern have!" + NL + "\t\t * example: pattern = \"2,4,4,*\"" + NL + "\t\t *                     this pattern indicates that there are 4 columns in the schema." + NL + "\t\t *                     also column[1] has size of 2 and it starts from index 0 to 2." + NL + "\t\t *                     column[2] is from index 2 to 6, with length of 4" + NL + "\t\t *                     and so on..." + NL + "\t\t *                     note: star represents the remaining length, and can only be used in the last column" + NL + "\t\t */" + NL + "\t\tpublic void setPattern(String pattern) throws java.lang.RuntimeException {" + NL + "\t\t\tint beginIndex = 0;" + NL + "\t\t\tint endIndex = 0;" + NL + "\t\t\tint size = 0;" + NL + "\t\t\tString[] patternSplit = pattern.split(\",\");" + NL + "\t\t\tbegins = new int[patternSplit.length];" + NL + "\t\t\tends = new int[patternSplit.length];" + NL + "\t\t\tsizes = new int[patternSplit.length];" + NL + "\t\t\tminimumSize = 0;" + NL + "\t\t\t" + NL + "\t\t\tfor (int i=0; i < patternSplit.length; i++) {" + NL + "\t\t\t\tnumberOfFields++;" + NL + "\t\t\t\tif ((\"*\").equals(patternSplit[i]) ) {" + NL + "\t\t\t\t\tif (i != (patternSplit.length -1)) {\t\t// the star can only be used for the size of the last column" + NL + "\t\t\t\t\t\tthrow new java.lang.RuntimeException(\"The star (*) in the pattern can only be at the end of the pattern string.\");" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\telse {" + NL + "\t\t\t\t\t\thasStar = true;" + NL + "\t\t\t\t\t\tbegins[i] = beginIndex;" + NL + "\t\t\t\t\t\tends[i] = -1;" + NL + "\t\t\t\t\t\tsizes[i] = -1;" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t\telse {" + NL + "\t\t\t\t\ttry { size = Integer.parseInt(patternSplit[i]); }" + NL + "\t\t\t\t\tcatch (Exception e) { throw new java.lang.RuntimeException(\"'\" + patternSplit[i] + \"' is not a valid integer value  in the pattern: \" + pattern); }" + NL + "\t\t\t\t\tif (size <= 0) {" + NL + "\t\t\t\t\t\tthrow new java.lang.RuntimeException(\"'\" + patternSplit[i] + \"' is not a valid integer value  in the pattern: \" + pattern);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tendIndex += size;" + NL + "\t\t\t\t\tbegins[i] = beginIndex;" + NL + "\t\t\t\t\tends[i] = endIndex;" + NL + "\t\t\t\t\tbeginIndex += size;" + NL + "\t\t\t\t\tminimumSize += size;" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t/** " + NL + "\t\t * this function parses the row into fields based on the values of beings and ends" + NL + "\t\t */" + NL + "\t\tpublic String[] parseRow(String row) throws java.lang.Exception {" + NL + "\t\t\tString[] fields = new String[numberOfFields];" + NL + "\t\t\tif (begins == null || ends == null || numberOfFields > begins.length || numberOfFields > ends.length) {\t\t// this should never happen, unless outside code has changed either numberOfFields, beings, ends" + NL + "\t\t\t\tthrow new java.lang.Exception(\"The PositionalSchema object is not configured correctly. please contact talend support (support@talend.com)\");" + NL + "\t\t\t}" + NL + "\t\t\tif (row != null && row.length() > 0) {" + NL + "\t\t\t\tif (checkRowSize) {" + NL + "\t\t\t\t\tif (hasStar == false && row.length() != minimumSize) {" + NL + "\t\t\t\t\t\tthrow new java.lang.Exception(\"row size does not match the pattern ('\" + pattern + \"'), expected size is \" + minimumSize + \".row size is: \" + row.length());" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\telse if (minimumSize > row.length()) {" + NL + "\t\t\t\t\t\tthrow new java.lang.Exception(\"row size too small, expected size is \" + minimumSize);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t\tfor (int i=0; i < numberOfFields; i++) {" + NL + "\t\t\t\t\tfields[i] = \"\";" + NL + "\t\t\t\t\tif (ends[i] == -1) {" + NL + "\t\t\t\t\t\tif (row.length() > begins[i]) {" + NL + "\t\t\t\t\t\t\tfields[i] = row.substring(begins[i]);" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\telse {" + NL + "\t\t\t\t\t\tif (row.length() > ends[i]) {" + NL + "\t\t\t\t\t\t\tfields[i] = row.substring(begins[i], ends[i]);" + NL + "\t\t\t\t\t\t} else if (row.length() > begins[i]) {" + NL + "\t\t\t\t\t\t\tfields[i] = row.substring(begins[i]);" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tif (trimAll) {" + NL + "\t\t\t\t\t\tfields[i] = fields[i].trim();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\treturn fields;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t/**" + NL + "\t\t * returns the minimum size required to contain the record. this value is driven from the pattern" + NL + "\t\t * star at the end of the pattern does not accumulate to the size" + NL + "\t\t */" + NL + "\t\tpublic int getMinimumRowSize() {" + NL + "\t\t\treturn minimumSize;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t/**" + NL + "\t\t * returns the number of fields specified in the pattern" + NL + "\t\t */" + NL + "\t\tpublic int getNumberOfFieldsExpected() {" + NL + "\t\t\treturn numberOfFields;" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\tint nb_line_";
  protected final String TEXT_4 = " = 0;" + NL + "\tint nb_line_rejected_";
  protected final String TEXT_5 = " = 0;" + NL + "\tint nb_line_unknownHeader_";
  protected final String TEXT_6 = " = 0;" + NL + "\tint nb_line_parseError_";
  protected final String TEXT_7 = " = 0;" + NL + "\t" + NL + "\tint skipHeader_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ";" + NL + "\tint skipFooter_";
  protected final String TEXT_10 = "  = ";
  protected final String TEXT_11 = ";" + NL + "\tint limit_";
  protected final String TEXT_12 = " = ";
  protected final String TEXT_13 = ";" + NL + "\t" + NL + "\tString rowSeparator_";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ";" + NL + "\tif(rowSeparator_";
  protected final String TEXT_16 = ".length()< 1){" + NL + "\t\tthrow new Exception(\"Row Separator must include at least one character\");" + NL + "\t}" + NL + "\t" + NL + "\tif (skipFooter_";
  protected final String TEXT_17 = " > 0) {" + NL + "\t\tjava.io.BufferedReader temp_in_";
  protected final String TEXT_18 = " = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(";
  protected final String TEXT_19 = "), ";
  protected final String TEXT_20 = "));" + NL + "\t\torg.talend.fileprocess.delimited.RowParser temp_reader_";
  protected final String TEXT_21 = " = new org.talend.fileprocess.delimited.RowParser(temp_in_";
  protected final String TEXT_22 = ", ";
  protected final String TEXT_23 = ", ";
  protected final String TEXT_24 = ");" + NL + "\t\tint available_";
  protected final String TEXT_25 = " = (int)temp_reader_";
  protected final String TEXT_26 = ".getAvailableRowCount(skipFooter_";
  protected final String TEXT_27 = ");" + NL + "\t\ttemp_reader_";
  protected final String TEXT_28 = ".close();" + NL + "\t\ttemp_in_";
  protected final String TEXT_29 = ".close();" + NL + "\t\ttemp_reader_";
  protected final String TEXT_30 = " = null;" + NL + "\t\ttemp_in_";
  protected final String TEXT_31 = " = null;" + NL + "\t\tif(limit_";
  protected final String TEXT_32 = " < 0){" + NL + "\t\t\tlimit_";
  protected final String TEXT_33 = " = available_";
  protected final String TEXT_34 = "; " + NL + "\t\t}else{" + NL + "\t\t\tlimit_";
  protected final String TEXT_35 = " = (limit_";
  protected final String TEXT_36 = " > available_";
  protected final String TEXT_37 = ") ? available_";
  protected final String TEXT_38 = " : limit_";
  protected final String TEXT_39 = ";" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\tjava.io.BufferedReader in_";
  protected final String TEXT_40 = " = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(";
  protected final String TEXT_41 = "), ";
  protected final String TEXT_42 = "));" + NL + "\torg.talend.fileprocess.delimited.RowParser reader_";
  protected final String TEXT_43 = " = new org.talend.fileprocess.delimited.RowParser(in_";
  protected final String TEXT_44 = ", ";
  protected final String TEXT_45 = ", ";
  protected final String TEXT_46 = ");" + NL + "\treader_";
  protected final String TEXT_47 = ".setSafetySwitch(";
  protected final String TEXT_48 = ");" + NL + "\treader_";
  protected final String TEXT_49 = ".skipHeaders(skipHeader_";
  protected final String TEXT_50 = ");" + NL + "\t";
  protected final String TEXT_51 = NL + "\tAdvancedPositionalParser_";
  protected final String TEXT_52 = " schema_";
  protected final String TEXT_53 = "_";
  protected final String TEXT_54 = " = new AdvancedPositionalParser_";
  protected final String TEXT_55 = "(\"";
  protected final String TEXT_56 = "\",";
  protected final String TEXT_57 = ",";
  protected final String TEXT_58 = ",";
  protected final String TEXT_59 = ",";
  protected final String TEXT_60 = ");" + NL + "\tif (schema_";
  protected final String TEXT_61 = "_";
  protected final String TEXT_62 = ".getNumberOfFieldsExpected() != ";
  protected final String TEXT_63 = ") {" + NL + "\t\tthrow new java.lang.RuntimeException(\"Number of columns in the schema does not match the pattern specified for the connection '";
  protected final String TEXT_64 = "' of ";
  protected final String TEXT_65 = " component.\");" + NL + "\t}";
  protected final String TEXT_66 = NL + "\tthrow new java.lang.RuntimeException(\"Please correctly specify the pattern, header, and the schema for the connection '";
  protected final String TEXT_67 = "' of ";
  protected final String TEXT_68 = " component.\");";
  protected final String TEXT_69 = NL + NL + "\tString hdrpos_";
  protected final String TEXT_70 = " = ";
  protected final String TEXT_71 = ";" + NL + "\tif (hdrpos_";
  protected final String TEXT_72 = ".indexOf(\"-\") < 0) {" + NL + "\t\tthrow new java.lang.RuntimeException(\"Please input the header position corretly. for exmaple for first 3 characters enter: '0-3'\");" + NL + "\t}" + NL + "\tint hdrStartIndex_";
  protected final String TEXT_73 = " = Integer.parseInt(hdrpos_";
  protected final String TEXT_74 = ".split(\"-\")[0]);" + NL + "\tint hdrEndIndex_";
  protected final String TEXT_75 = " = Integer.parseInt(hdrpos_";
  protected final String TEXT_76 = ".split(\"-\")[1]);" + NL + "\tboolean foundMatchingHeader_";
  protected final String TEXT_77 = " = false;" + NL + "\tString row_";
  protected final String TEXT_78 = " = null;" + NL + "\tString header_";
  protected final String TEXT_79 = " = null;" + NL + "\t";
  protected final String TEXT_80 = NL + "String key_";
  protected final String TEXT_81 = "_";
  protected final String TEXT_82 = "_";
  protected final String TEXT_83 = " = \"\";";
  protected final String TEXT_84 = NL + "\t" + NL + "\twhile (reader_";
  protected final String TEXT_85 = ".readRecord()) {";
  protected final String TEXT_86 = NL + "\t\t";
  protected final String TEXT_87 = " = null;";
  protected final String TEXT_88 = NL + "\t\t// parse the header record and match with the associated connection(s)" + NL + "\t\trow_";
  protected final String TEXT_89 = " = reader_";
  protected final String TEXT_90 = ".getRowRecord();" + NL + "\t\tfoundMatchingHeader_";
  protected final String TEXT_91 = " = false;" + NL + "\t\tif (row_";
  protected final String TEXT_92 = " != null && row_";
  protected final String TEXT_93 = ".length() >= hdrEndIndex_";
  protected final String TEXT_94 = ") {" + NL + "\t\t\theader_";
  protected final String TEXT_95 = " = row_";
  protected final String TEXT_96 = ".substring(hdrStartIndex_";
  protected final String TEXT_97 = ", hdrEndIndex_";
  protected final String TEXT_98 = ")";
  protected final String TEXT_99 = ";";
  protected final String TEXT_100 = NL + "\t\t\tif (schema_";
  protected final String TEXT_101 = "_";
  protected final String TEXT_102 = ".headerMatches(header_";
  protected final String TEXT_103 = ")) {" + NL + "\t\t\t\tfoundMatchingHeader_";
  protected final String TEXT_104 = " = true;" + NL + "\t\t\t\t";
  protected final String TEXT_105 = " = new ";
  protected final String TEXT_106 = "Struct();" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tString[] fields = schema_";
  protected final String TEXT_107 = "_";
  protected final String TEXT_108 = ".parseRow(row_";
  protected final String TEXT_109 = ");";
  protected final String TEXT_110 = NL + "\t\t\t\t\t";
  protected final String TEXT_111 = ".";
  protected final String TEXT_112 = " = ";
  protected final String TEXT_113 = ";";
  protected final String TEXT_114 = NL + "\t\t\t\t\t";
  protected final String TEXT_115 = ".";
  protected final String TEXT_116 = " = ParserUtils.parseTo_Date(";
  protected final String TEXT_117 = ", ";
  protected final String TEXT_118 = ",false);";
  protected final String TEXT_119 = NL + "\t\t\t\t\t";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = " = ParserUtils.parseTo_Date(";
  protected final String TEXT_122 = ", ";
  protected final String TEXT_123 = ");";
  protected final String TEXT_124 = NL + "\t\t\t\t\t";
  protected final String TEXT_125 = ".";
  protected final String TEXT_126 = " = ";
  protected final String TEXT_127 = ".getBytes(";
  protected final String TEXT_128 = ");";
  protected final String TEXT_129 = NL + "\t\t\t\t\tif((\"\").equals(";
  protected final String TEXT_130 = ".trim())){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_131 = ".";
  protected final String TEXT_132 = " = null;" + NL + "\t\t\t\t\t}else{";
  protected final String TEXT_133 = NL + "\t\t\t\t\t";
  protected final String TEXT_134 = ".";
  protected final String TEXT_135 = " = ParserUtils.parseTo_";
  protected final String TEXT_136 = "(ParserUtils.parseTo_Number(";
  protected final String TEXT_137 = ".trim(), ";
  protected final String TEXT_138 = ", ";
  protected final String TEXT_139 = "));";
  protected final String TEXT_140 = NL + "\t\t\t\t\t}";
  protected final String TEXT_141 = NL + "\t\t\t\t\tif((\"\").equals(";
  protected final String TEXT_142 = ".trim())){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_143 = ".";
  protected final String TEXT_144 = " = null;" + NL + "\t\t\t\t\t}else{";
  protected final String TEXT_145 = NL + "\t\t\t\t\t";
  protected final String TEXT_146 = ".";
  protected final String TEXT_147 = " = ParserUtils.parseTo_";
  protected final String TEXT_148 = "(";
  protected final String TEXT_149 = ".trim());";
  protected final String TEXT_150 = NL + "\t\t\t\t\t}";
  protected final String TEXT_151 = NL + "\t\t\t\t\t";
  protected final String TEXT_152 = ".";
  protected final String TEXT_153 = " = ParserUtils.parseTo_";
  protected final String TEXT_154 = "(";
  protected final String TEXT_155 = ".trim());";
  protected final String TEXT_156 = NL + "\t\t\t\t\tfields = null;";
  protected final String TEXT_157 = NL + "\t\t\t\t\tkey_";
  protected final String TEXT_158 = "_";
  protected final String TEXT_159 = "_";
  protected final String TEXT_160 = " = String.valueOf(";
  protected final String TEXT_161 = ".";
  protected final String TEXT_162 = ");";
  protected final String TEXT_163 = NL + "\t\t\t\t} catch (java.lang.Exception e) {" + NL + "\t\t\t\t\t";
  protected final String TEXT_164 = " = null;";
  protected final String TEXT_165 = NL + "\t\t\t\t\tnb_line_rejected_";
  protected final String TEXT_166 = "++;" + NL + "\t\t\t\t\tnb_line_parseError_";
  protected final String TEXT_167 = "++;" + NL + "\t\t\t\t\tthrow e;";
  protected final String TEXT_168 = NL + "\t\t\t\t\tnb_line_rejected_";
  protected final String TEXT_169 = "++;" + NL + "\t\t\t\t\tnb_line_parseError_";
  protected final String TEXT_170 = "++;" + NL + "\t\t\t\t\t";
  protected final String TEXT_171 = " = new ";
  protected final String TEXT_172 = "Struct();" + NL + "\t\t\t\t\t";
  protected final String TEXT_173 = ".errorCode = 1;" + NL + "\t\t\t\t\t";
  protected final String TEXT_174 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_175 = "; " + NL + "\t\t\t\t\t";
  protected final String TEXT_176 = ".line = row_";
  protected final String TEXT_177 = ";";
  protected final String TEXT_178 = NL + "\t\t\t\t} //catch\t\t\t\t" + NL + "\t\t\t} //if header matches";
  protected final String TEXT_179 = NL + "\t\t\tif (foundMatchingHeader_";
  protected final String TEXT_180 = " == false) {";
  protected final String TEXT_181 = NL + "\t\t\t\tnb_line_rejected_";
  protected final String TEXT_182 = "++;" + NL + "\t\t\t\tnb_line_unknownHeader_";
  protected final String TEXT_183 = "++;" + NL + "\t\t\t\tthrow new java.lang.RuntimeException(\"Unknown header value '\" + header_";
  protected final String TEXT_184 = " + \"' in component ";
  protected final String TEXT_185 = "\");";
  protected final String TEXT_186 = NL + "\t\t\t\tnb_line_rejected_";
  protected final String TEXT_187 = "++;" + NL + "\t\t\t\tnb_line_unknownHeader_";
  protected final String TEXT_188 = "++;" + NL + "\t\t\t\t";
  protected final String TEXT_189 = " = new ";
  protected final String TEXT_190 = "Struct();" + NL + "\t\t\t\t";
  protected final String TEXT_191 = ".errorCode = 2;" + NL + "\t\t\t\t";
  protected final String TEXT_192 = ".errorMessage = \"Unknown header value '\" + header_";
  protected final String TEXT_193 = " + \"'\";" + NL + "\t\t\t\t";
  protected final String TEXT_194 = ".line = row_";
  protected final String TEXT_195 = ";";
  protected final String TEXT_196 = NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_197 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
	
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	if (conns!=null && conns.size()>0) { //1
	
	//-------------------------------------------------------------
	// get all the properties and do some checks
	String filename = ElementParameterParser.getValueWithUIFieldKey(node,"__FILE_NAME__", "FILE_NAME");
	//String filename = ElementParameterParser.getValue(node,"__FILE_NAME__", "FILE_NAME");
	String rowSeparator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
	String header = ElementParameterParser.getValue(node, "__HEADER__");
    String footer = ElementParameterParser.getValue(node, "__FOOTER__");
	String limit = ElementParameterParser.getValue(node, "__LIMIT__");
	String recordTypePosition = ElementParameterParser.getValue(node, "__RECORD_TYPE_POSITION__");
	String headerRecordPosition = ElementParameterParser.getValue(node, "__HEADER_RECORD_POSITION__");
	String headerRecordLength = ElementParameterParser.getValue(node, "__HEADER_RECORD_LENGTH__");
	boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));
	boolean dieOnUnknownHeader = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_UNKNOWN_HEADER__"));
    String skipEmptyRows = "true"; //("true").equals(ElementParameterParser.getValue(node, "__SKIP_EMPTY_ROWS__"));
	boolean trimAll = ("true").equals(ElementParameterParser.getValue(node,"__TRIMALL__"));
	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
	//need to process rows longger than 100,000 characters, the property SafetySwitch(in talend_file_enhanced_20070724.jar) should be sent to false.(the default is true)
    //that means if check the option(true), the logic value of bSafetySwitch should be changed to false (negate the property)
	boolean bSafetySwitch = !(("true").equals(ElementParameterParser.getValue(node, "__PROCESS_LONG_ROW__")));
	String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
	boolean advancedSeparator = ("true").equals(ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__"));
	String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
	String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
	
	String checkDateStr = ElementParameterParser.getValue(node,"__CHECK_DATE__");
	boolean checkDate = (checkDateStr!=null&&!("").equals(checkDateStr))?("true").equals(checkDateStr):false;
	
	List<Map<String, String>> schemas_o = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SCHEMAS__");
	
	List<Map<String, String>> schemas = new java.util.ArrayList<Map<String, String>>();
	
	for(Map<String, String> schema_o : schemas_o){
		Map<String, String> schema = new java.util.HashMap<String, String>();
		schema.put("SCHEMA", schema_o.get("SCHEMA"));
		schema.put("PARENT_ROW", TalendTextUtils.removeQuotes(schema_o.get("PARENT_ROW")));
		schema.put("KEY_COLUMN", TalendTextUtils.removeQuotes(schema_o.get("KEY_COLUMN")));
		schema.put("PARENT_KEY_COLUMN", TalendTextUtils.removeQuotes(schema_o.get("PARENT_KEY_COLUMN")));
		schema.put("PATTERN", schema_o.get("PATTERN"));
		schema.put("CHECK_ROW_SIZE", schema_o.get("CHECK_ROW_SIZE"));
		schema.put("HEADER", schema_o.get("HEADER"));
		schemas.add(schema);
	}
	
	String rejectConnName = null;
    List< ? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
    if(rejectConns != null && rejectConns.size() > 0) {
        IConnection rejectConn = rejectConns.get(0);
        rejectConnName = rejectConn.getName();
    }
	
	if (("").equals(header)) {
		header = "0";
	}
	
	if (("").equals(limit) || ("0").equals(limit)) {
		limit = "-1";
	}
	
	if (("").equals(footer)) {
		footer = "0";
	}
	

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(header);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(footer);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(limit);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append( filename );
    stringBuffer.append(TEXT_19);
    stringBuffer.append( encoding );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(skipEmptyRows );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(skipEmptyRows );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(bSafetySwitch);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    
    for (IConnection conn : conns) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			String headerValue = null;
			String patternValue = null;
			boolean checkRowSize = false;
			for(Map<String, String> schema : schemas) {
				if (conn.getName().equals(schema.get("SCHEMA"))) {
					headerValue = schema.get("HEADER");
					patternValue = schema.get("PATTERN");
					checkRowSize = ("true").equals(schema.get("CHECK_ROW_SIZE"));
				}
			}
    		IMetadataTable tempMetadataTable = conn.getMetadataTable();
    		List<IMetadataColumn> listColumns = tempMetadataTable.getListColumns();
			if (patternValue != null && headerValue != null && (!conn.getName().equals(rejectConnName)) ) {

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(headerValue);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(patternValue);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(String.valueOf(checkRowSize));
    stringBuffer.append(TEXT_59);
    stringBuffer.append(String.valueOf(trimAll));
    stringBuffer.append(TEXT_60);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(listColumns.size());
    stringBuffer.append(TEXT_63);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
			}
			else if (!conn.getName().equals(rejectConnName)) {

    stringBuffer.append(TEXT_66);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    
			}
		}
	}

    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(headerRecordPosition);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    
		for(Map<String,String> schema : schemas){
			if(!("").equals(schema.get("PARENT_ROW"))){

    stringBuffer.append(TEXT_80);
    stringBuffer.append(schema.get("SCHEMA") );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(schema.get("PARENT_ROW") );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    
			}
		}

    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    
		for (IConnection conn : conns) {
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_86);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_87);
    
			}
		}

    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(trimAll? ".trim()":"");
    stringBuffer.append(TEXT_99);
    
    for (IConnection conn : conns) {//_____0
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//_____0_____1
    		IMetadataTable tempMetadataTable = conn.getMetadataTable();
    		List<IMetadataColumn> listColumns = tempMetadataTable.getListColumns();
			if (!conn.getName().equals(rejectConnName)) {//_____0_____1_____1
				String parent = "";
				String keyColumn = "";
				for(Map<String, String> schema : schemas) {
					if (conn.getName().equals(schema.get("SCHEMA"))) {
						parent = schema.get("PARENT_ROW");
						keyColumn = schema.get("KEY_COLUMN");
					}
				}
				
				if(!("").equals(parent)){
					int keyIndex = listColumns.size() - 1;
					for(int k=0; k < listColumns.size(); k++){
						if(listColumns.get(k).getLabel().equals(keyColumn)){
							keyIndex = k;
							break;
						}
					}
					if(keyIndex != listColumns.size() - 1){
						IMetadataColumn column = listColumns.get(keyIndex);
						listColumns.set(keyIndex, listColumns.get(listColumns.size() - 1));
						listColumns.set(listColumns.size() - 1, column);
					}
				}

    stringBuffer.append(TEXT_100);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_105);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_106);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    
				for (int i=0; i < listColumns.size(); i++) {//_____0_____1_____1_____1
					boolean isKeyColumn = false;
					if(i == listColumns.size() - 1 && !("").equals(parent)){
						isKeyColumn = true;
					}
					IMetadataColumn column = listColumns.get(i);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String datePattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();

					if (javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_110);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_111);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_112);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_113);
    
					} else if (javaType == JavaTypesManager.DATE) {
						if(checkDate) {

    stringBuffer.append(TEXT_114);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_115);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_116);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(datePattern);
    stringBuffer.append(TEXT_118);
    						
						} else {

    stringBuffer.append(TEXT_119);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_121);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(datePattern);
    stringBuffer.append(TEXT_123);
    
						}

					} else if (javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_124);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_125);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_126);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_128);
    
					} else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) {
						if(column.isNullable()){

    stringBuffer.append(TEXT_129);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_131);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_132);
    
						}

    stringBuffer.append(TEXT_133);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_134);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_139);
    
						if(column.isNullable()){

    stringBuffer.append(TEXT_140);
    
						}

    
					} else if (JavaTypesManager.isNumberType(javaType, column.isNullable())) {
						if(column.isNullable()){

    stringBuffer.append(TEXT_141);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_143);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_144);
    
						}

    stringBuffer.append(TEXT_145);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_146);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_147);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_149);
    
						if(column.isNullable()){

    stringBuffer.append(TEXT_150);
    
						}

    
					} else { 

    stringBuffer.append(TEXT_151);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_152);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_153);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_154);
    stringBuffer.append(isKeyColumn ? "key_"+conn.getName()+"_"+parent+"_"+cid : "fields["+i+"]" );
    stringBuffer.append(TEXT_155);
    
					}
				} //all columns//_____0_____1_____1_____1

    stringBuffer.append(TEXT_156);
    
				for(int t = 0; t < schemas.size(); t++){//assign key values begin
					Map<String, String> schema = schemas.get(t);
					if(schema.get("PARENT_ROW").equals(conn.getName())){

    stringBuffer.append(TEXT_157);
    stringBuffer.append(schema.get("SCHEMA") );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(schema.get("PARENT_KEY_COLUMN") );
    stringBuffer.append(TEXT_162);
    
					}
				}//assign key values end

    stringBuffer.append(TEXT_163);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_164);
    
					if (dieOnError) {

    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    
					}
					else {
						if (rejectConnName != null) {

    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    
						} //if rejectConnName is not empty
					} //if not die on error

    stringBuffer.append(TEXT_178);
    
			}//_____0_____1_____1
    	}//_____0_____1
    }//_____0

    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_180);
    
	if (dieOnUnknownHeader) {

    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_185);
    
	} else if (rejectConnName != null) {

    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_188);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_189);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_191);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_192);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_193);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_195);
    
	}

    stringBuffer.append(TEXT_196);
    
	} //1

    stringBuffer.append(TEXT_197);
    return stringBuffer.toString();
  }
}
