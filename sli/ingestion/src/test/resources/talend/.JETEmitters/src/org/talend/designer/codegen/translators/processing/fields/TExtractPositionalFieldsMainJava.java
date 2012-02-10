package org.talend.designer.codegen.translators.processing.fields;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TExtractPositionalFieldsMainJava
{
  protected static String nl;
  public static synchronized TExtractPositionalFieldsMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TExtractPositionalFieldsMainJava result = new TExtractPositionalFieldsMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = " = null;";
  protected final String TEXT_4 = NL + "\t//String field_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ";" + NL + "\tString field_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = ";";
  protected final String TEXT_11 = NL + "try{" + NL + "\t";
  protected final String TEXT_12 = " = new ";
  protected final String TEXT_13 = "Struct();";
  protected final String TEXT_14 = NL + "\t\t\t\t";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = " = ";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = ";";
  protected final String TEXT_19 = NL + "\tjava.util.Map<String,String> newFields_";
  protected final String TEXT_20 = " = new java.util.HashMap<String,String>();";
  protected final String TEXT_21 = NL + "\tnewFields_";
  protected final String TEXT_22 = ".put(\"";
  protected final String TEXT_23 = "\", TalendString.talendTrim(field_";
  protected final String TEXT_24 = ".substring(0," + NL + "\t\tindexs_";
  protected final String TEXT_25 = "[";
  protected final String TEXT_26 = "]>field_";
  protected final String TEXT_27 = ".length()||indexs_";
  protected final String TEXT_28 = "[";
  protected final String TEXT_29 = "]<0?field_";
  protected final String TEXT_30 = ".length():indexs_";
  protected final String TEXT_31 = "[";
  protected final String TEXT_32 = "])";
  protected final String TEXT_33 = "," + NL + "     \t\t\t";
  protected final String TEXT_34 = ", ";
  protected final String TEXT_35 = "));";
  protected final String TEXT_36 = NL + "\tif(";
  protected final String TEXT_37 = "<indexs_";
  protected final String TEXT_38 = ".length && indexs_";
  protected final String TEXT_39 = "[";
  protected final String TEXT_40 = "]>=0 && indexs_";
  protected final String TEXT_41 = "[";
  protected final String TEXT_42 = "]<= field_";
  protected final String TEXT_43 = ".length()){" + NL + "    \tnewFields_";
  protected final String TEXT_44 = ".put(\"";
  protected final String TEXT_45 = "\"," + NL + "     \t\tTalendString.talendTrim(field_";
  protected final String TEXT_46 = ".substring(indexs_";
  protected final String TEXT_47 = "[";
  protected final String TEXT_48 = "]," + NL + "     \t\t\tindexs_";
  protected final String TEXT_49 = "[";
  protected final String TEXT_50 = "]>field_";
  protected final String TEXT_51 = ".length()||indexs_";
  protected final String TEXT_52 = "[";
  protected final String TEXT_53 = "]<0?field_";
  protected final String TEXT_54 = ".length():indexs_";
  protected final String TEXT_55 = "[";
  protected final String TEXT_56 = "])";
  protected final String TEXT_57 = "," + NL + "     \t\t\t";
  protected final String TEXT_58 = ", ";
  protected final String TEXT_59 = "));" + NL + " \t}";
  protected final String TEXT_60 = NL + "\tnewFields_";
  protected final String TEXT_61 = ".put(\"";
  protected final String TEXT_62 = "\", field_";
  protected final String TEXT_63 = ".substring(0," + NL + "\t\tindexs_";
  protected final String TEXT_64 = "[";
  protected final String TEXT_65 = "]>field_";
  protected final String TEXT_66 = ".length()||indexs_";
  protected final String TEXT_67 = "[";
  protected final String TEXT_68 = "]<0?field_";
  protected final String TEXT_69 = ".length():indexs_";
  protected final String TEXT_70 = "[";
  protected final String TEXT_71 = "])";
  protected final String TEXT_72 = ");";
  protected final String TEXT_73 = NL + "\tif(";
  protected final String TEXT_74 = "<indexs_";
  protected final String TEXT_75 = ".length && indexs_";
  protected final String TEXT_76 = "[";
  protected final String TEXT_77 = "]>=0 && indexs_";
  protected final String TEXT_78 = "[";
  protected final String TEXT_79 = "]<= field_";
  protected final String TEXT_80 = ".length()){" + NL + "    \tnewFields_";
  protected final String TEXT_81 = ".put(\"";
  protected final String TEXT_82 = "\",field_";
  protected final String TEXT_83 = ".substring(indexs_";
  protected final String TEXT_84 = "[";
  protected final String TEXT_85 = "]," + NL + "     \t\tindexs_";
  protected final String TEXT_86 = "[";
  protected final String TEXT_87 = "]>field_";
  protected final String TEXT_88 = ".length()||indexs_";
  protected final String TEXT_89 = "[";
  protected final String TEXT_90 = "]<0?field_";
  protected final String TEXT_91 = ".length():indexs_";
  protected final String TEXT_92 = "[";
  protected final String TEXT_93 = "])";
  protected final String TEXT_94 = ");" + NL + " \t}";
  protected final String TEXT_95 = NL + "\tString temp_";
  protected final String TEXT_96 = " = null;";
  protected final String TEXT_97 = NL + "\t";
  protected final String TEXT_98 = ".";
  protected final String TEXT_99 = " = newFields_";
  protected final String TEXT_100 = ".get(\"";
  protected final String TEXT_101 = "\");";
  protected final String TEXT_102 = NL + "\ttemp_";
  protected final String TEXT_103 = " = newFields_";
  protected final String TEXT_104 = ".get(\"";
  protected final String TEXT_105 = "\");" + NL + "\tif(temp_";
  protected final String TEXT_106 = "!=null && temp_";
  protected final String TEXT_107 = ".length() > 0) {";
  protected final String TEXT_108 = NL + "\t\t";
  protected final String TEXT_109 = ".";
  protected final String TEXT_110 = " = temp_";
  protected final String TEXT_111 = ".getBytes();";
  protected final String TEXT_112 = NL + "\t\t";
  protected final String TEXT_113 = ".";
  protected final String TEXT_114 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_115 = ", ";
  protected final String TEXT_116 = ", false);";
  protected final String TEXT_117 = NL + "\t\t";
  protected final String TEXT_118 = ".";
  protected final String TEXT_119 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_120 = ", ";
  protected final String TEXT_121 = ");";
  protected final String TEXT_122 = NL + "\t\t";
  protected final String TEXT_123 = ".";
  protected final String TEXT_124 = " = ParserUtils.parseTo_";
  protected final String TEXT_125 = "(ParserUtils.parseTo_Number(temp_";
  protected final String TEXT_126 = ", ";
  protected final String TEXT_127 = ", ";
  protected final String TEXT_128 = "));";
  protected final String TEXT_129 = NL + "\t\t";
  protected final String TEXT_130 = ".";
  protected final String TEXT_131 = " = ParserUtils.parseTo_";
  protected final String TEXT_132 = "(temp_";
  protected final String TEXT_133 = ");";
  protected final String TEXT_134 = NL + "\t} else {\t\t\t\t\t\t";
  protected final String TEXT_135 = NL + "\t\tthrow new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_136 = "' in '";
  protected final String TEXT_137 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_138 = NL + "\t\t";
  protected final String TEXT_139 = ".";
  protected final String TEXT_140 = " = ";
  protected final String TEXT_141 = ";";
  protected final String TEXT_142 = NL + "\t}";
  protected final String TEXT_143 = NL + "\t int filedsum_";
  protected final String TEXT_144 = " = newFields_";
  protected final String TEXT_145 = ".size();" + NL + "\t if(filedsum_";
  protected final String TEXT_146 = " < ";
  protected final String TEXT_147 = "){" + NL + "\t \tthrow new RuntimeException(\"Column(s) missing\");" + NL + "\t } else if(filedsum_";
  protected final String TEXT_148 = " > ";
  protected final String TEXT_149 = ") {" + NL + "\t \tthrow new RuntimeException(\"Too many columns\");" + NL + "\t }";
  protected final String TEXT_150 = NL + "\t";
  protected final String TEXT_151 = " = null;";
  protected final String TEXT_152 = NL + "\tnb_line_";
  protected final String TEXT_153 = "++;" + NL + "}catch(Exception ex_";
  protected final String TEXT_154 = "){";
  protected final String TEXT_155 = NL + "\tthrow(ex_";
  protected final String TEXT_156 = ");";
  protected final String TEXT_157 = NL + "\t";
  protected final String TEXT_158 = " = new ";
  protected final String TEXT_159 = "Struct();";
  protected final String TEXT_160 = NL + "    ";
  protected final String TEXT_161 = ".";
  protected final String TEXT_162 = " = ";
  protected final String TEXT_163 = ".";
  protected final String TEXT_164 = ";";
  protected final String TEXT_165 = "    ";
  protected final String TEXT_166 = NL + "    ";
  protected final String TEXT_167 = ".errorMessage = ex_";
  protected final String TEXT_168 = ".getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_169 = ";";
  protected final String TEXT_170 = NL + "    ";
  protected final String TEXT_171 = " = null;";
  protected final String TEXT_172 = NL + "    System.err.println(ex_";
  protected final String TEXT_173 = ".getMessage());";
  protected final String TEXT_174 = NL + "    ";
  protected final String TEXT_175 = " = null;";
  protected final String TEXT_176 = NL + "\t";
  protected final String TEXT_177 = ".errorMessage = ex_";
  protected final String TEXT_178 = ".getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_179 = ";";
  protected final String TEXT_180 = NL + "}";
  protected final String TEXT_181 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String field = ElementParameterParser.getValue(node, "__FIELD__");
String pattern = ElementParameterParser.getValue(node, "__PATTERN__");
boolean advanced = ("true").equals(ElementParameterParser.getValue(node, "__ADVANCED_OPTION__"));
List<Map<String, String>> formats =
	(List<Map<String,String>>)ElementParameterParser.getObjectValue( node, "__FORMATS__");

String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);

boolean trim = ("true").equals(ElementParameterParser.getValue(node, "__TRIM__"));

String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;

String checkNumStr = ElementParameterParser.getValue(node, "__CHECK_FIELDS_NUM__");
boolean checkNum = (checkNumStr!=null&&!("").equals(checkNumStr))?("true").equals(checkNumStr):false; 

IConnection inConn = null;
List< ? extends IConnection> inConns = node.getIncomingConnections();
if(inConns!=null){
    for (IConnection conn : inConns) {
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    		inConn = conn;
    		break;
    	}
    }
}

String rejectConnName = "";
List<IMetadataColumn> rejectColumnList = null;
List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
if(rejectConns != null && rejectConns.size() > 0) {
    IConnection rejectConn = rejectConns.get(0);
    rejectColumnList = rejectConn.getMetadataTable().getListColumns();
    rejectConnName = rejectConn.getName();
}

IConnection outConn = null;
String firstConnName = "";
List< ? extends IConnection> outConns = node.getOutgoingConnections();
if(outConns!=null){
    for (IConnection conn : outConns) {
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    		outConn = conn;
    		firstConnName = outConn.getName();
    		break;
    	}
    }
}

if(outConns!=null){
    for (IConnection conn : outConns) {
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    
    	}
    }
}

//get field column
if(inConn!=null){
	IMetadataTable inputMetadataTable = inConn.getMetadataTable();
	for (IMetadataColumn inputCol : inputMetadataTable.getListColumns()) {
		if(inputCol.getLabel().equals(field)){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(field);
    stringBuffer.append(trim?".trim()":"");
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_9);
    stringBuffer.append(field);
    stringBuffer.append(TEXT_10);
    
			break;
		}
	}
}

//set original columns
List<IMetadataColumn> newColumnList = new ArrayList<IMetadataColumn>();
if(outConn!=null && inConn!=null){

    stringBuffer.append(TEXT_11);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_13);
    
	IMetadataTable outputMetadataTable = outConn.getMetadataTable();
	IMetadataTable inputMetadataTable = inConn.getMetadataTable();
	for (IMetadataColumn outputCol : outputMetadataTable.getListColumns()) {
		if(outputCol.getLabel().equals(field)){
			continue;
		}
		boolean isOirginalColumn = false;
		for (IMetadataColumn inputCol : inputMetadataTable.getListColumns()) {
			JavaType stringType =  JavaTypesManager.getJavaTypeFromId(inputCol.getTalendType());
			if( outputCol.getLabel().equals( inputCol.getLabel()) ){
				isOirginalColumn = true;
				

    stringBuffer.append(TEXT_14);
    stringBuffer.append(outConn.getName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(outputCol.getLabel());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_17);
    stringBuffer.append(inputCol.getLabel());
    stringBuffer.append(TEXT_18);
    
				break;
				}
		    }
		
		if(!isOirginalColumn){
			if(!("").equals(rejectConnName)&&rejectConnName.equals(firstConnName)
				&& (outputCol.getLabel().equals("errorMessage") || outputCol.getLabel().equals("errorCode"))){
			}else{
				newColumnList.add(outputCol);
			}
		}
	}


    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    
	if(advanced){
		for(int i=0; i<formats.size(); i++){
			Map<String,String> tmp = formats.get(i);
			if(i==0){

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(tmp.get("COLUMN"));
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(trim?".trim()":"");
    stringBuffer.append(TEXT_33);
    stringBuffer.append(tmp.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_34);
    stringBuffer.append(tmp.get("ALIGN"));
    stringBuffer.append(TEXT_35);
    
			}else{

    stringBuffer.append(TEXT_36);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(tmp.get("COLUMN"));
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(trim?".trim()":"");
    stringBuffer.append(TEXT_57);
    stringBuffer.append(tmp.get("PADDING_CHAR"));
    stringBuffer.append(TEXT_58);
    stringBuffer.append(tmp.get("ALIGN"));
    stringBuffer.append(TEXT_59);
    
			}
		}
	}else{
		for(int i=0;i<newColumnList.size();i++){
			IMetadataColumn tmp = newColumnList.get(i);
			if(i==0){

    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(tmp.getLabel());
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(trim?".trim()":"");
    stringBuffer.append(TEXT_72);
    
			}else{

    stringBuffer.append(TEXT_73);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(tmp.getLabel());
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(i-1);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(trim?".trim()":"");
    stringBuffer.append(TEXT_94);
    
			}
		}
	}

    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    
	for(IMetadataColumn column:newColumnList){
		String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
		String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    
		if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){

    stringBuffer.append(TEXT_97);
    stringBuffer.append(outConn.getName());
    stringBuffer.append(TEXT_98);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_101);
    
		}else{ 

    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    
			if(javaType == JavaTypesManager.BYTE_ARRAY){ 

    stringBuffer.append(TEXT_108);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    
			}else if(javaType == JavaTypesManager.DATE) {
				if(checkNum){

    stringBuffer.append(TEXT_112);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_116);
    
				}else{

    stringBuffer.append(TEXT_117);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_120);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_121);
    
				}
			}else if(advancedSeparator && JavaTypesManager.isNumberType(javaType)) { 

    stringBuffer.append(TEXT_122);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_124);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_127);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_128);
    
			} else {

    stringBuffer.append(TEXT_129);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_131);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    
			}

    stringBuffer.append(TEXT_134);
    
			String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
			if(defaultValue == null) {

    stringBuffer.append(TEXT_135);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_137);
    
			} else {

    stringBuffer.append(TEXT_138);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_141);
    
			}

    stringBuffer.append(TEXT_142);
    
		}
	}
	
	if(checkNum) {

    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_146);
    stringBuffer.append(newColumnList.size() );
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(newColumnList.size() );
    stringBuffer.append(TEXT_149);
    
	}
	
	if(!("").equals(rejectConnName) && rejectConnName.equals(firstConnName)){

    stringBuffer.append(TEXT_150);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_151);
    
	}

    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    
	if(dieOnError){

    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_156);
    
	}else{
		if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_157);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_159);
    
            for(IMetadataColumn column : outConn.getMetadataTable().getListColumns()) {

    stringBuffer.append(TEXT_160);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_164);
    
			}

    stringBuffer.append(TEXT_165);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_171);
    
		} else if(("").equals(rejectConnName)){

    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_175);
    
		} else if(rejectConnName.equals(firstConnName)){

    stringBuffer.append(TEXT_176);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_179);
    
		}
	}

    stringBuffer.append(TEXT_180);
    
}

    stringBuffer.append(TEXT_181);
    return stringBuffer.toString();
  }
}
