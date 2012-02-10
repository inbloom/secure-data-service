package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.IConnection;
import org.talend.core.utils.TalendQuoteUtils;

public class TFileOutputXMLMainJava
{
  protected static String nl;
  public static synchronized TFileOutputXMLMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputXMLMainJava result = new TFileOutputXMLMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String tempSt_";
  protected final String TEXT_3 = " = null;" + NL + "" + NL + "boolean flag_";
  protected final String TEXT_4 = " = true;" + NL + " " + NL + "groupby_new_";
  protected final String TEXT_5 = " = 0;" + NL;
  protected final String TEXT_6 = "tempSt_";
  protected final String TEXT_7 = " = \"<\"+";
  protected final String TEXT_8 = "+\" \"+";
  protected final String TEXT_9 = "+\"=\\\"\"+";
  protected final String TEXT_10 = "FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_11 = "), ";
  protected final String TEXT_12 = ", ";
  protected final String TEXT_13 = ")";
  protected final String TEXT_14 = "FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = "), ";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ")";
  protected final String TEXT_19 = ".";
  protected final String TEXT_20 = "((";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = " == null)?\"\":(";
  protected final String TEXT_23 = "TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_24 = "FormatterUtils.format_Date(";
  protected final String TEXT_25 = ".";
  protected final String TEXT_26 = ", ";
  protected final String TEXT_27 = ")";
  protected final String TEXT_28 = ")";
  protected final String TEXT_29 = "TalendString.replaceSpecialCharForXML(java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_30 = ".";
  protected final String TEXT_31 = ")).toString())";
  protected final String TEXT_32 = "TalendString.replaceSpecialCharForXML(String.valueOf(";
  protected final String TEXT_33 = "))";
  protected final String TEXT_34 = "TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_35 = ".";
  protected final String TEXT_36 = ")";
  protected final String TEXT_37 = "))";
  protected final String TEXT_38 = "+\"\\\">\";" + NL + "" + NL + "if(!groupby_";
  protected final String TEXT_39 = "[";
  protected final String TEXT_40 = "][0].equals(tempSt_";
  protected final String TEXT_41 = ")){" + NL + "\t" + NL + "\tif(flag_";
  protected final String TEXT_42 = "){" + NL + "\t" + NL + "\t\tgroupby_new_";
  protected final String TEXT_43 = " = ";
  protected final String TEXT_44 = ";" + NL + "" + NL + "\t\tflag_";
  protected final String TEXT_45 = " = false;" + NL + "\t\t" + NL + "\t\tfor(int i_";
  protected final String TEXT_46 = " = ";
  protected final String TEXT_47 = "; i_";
  protected final String TEXT_48 = " >= groupby_new_";
  protected final String TEXT_49 = " && start_";
  protected final String TEXT_50 = "; i_";
  protected final String TEXT_51 = "--){" + NL + "" + NL + "\t\t\tout_";
  protected final String TEXT_52 = ".write(groupby_";
  protected final String TEXT_53 = "[i_";
  protected final String TEXT_54 = "][1]);" + NL + "\t" + NL + "\t\t\tout_";
  protected final String TEXT_55 = ".newLine();" + NL + "\t" + NL + "\t\t}\t" + NL + "\t}" + NL + "" + NL + "\tgroupby_";
  protected final String TEXT_56 = "[";
  protected final String TEXT_57 = "][0] = tempSt_";
  protected final String TEXT_58 = ";" + NL + "\t" + NL + "\tout_";
  protected final String TEXT_59 = ".write(tempSt_";
  protected final String TEXT_60 = ");" + NL + "" + NL + "\tout_";
  protected final String TEXT_61 = ".newLine();" + NL + "\t" + NL + "\tif(!start_";
  protected final String TEXT_62 = "){" + NL + "\t\t\t" + NL + "\t\tstart_";
  protected final String TEXT_63 = " = true;" + NL + "\t\t\t" + NL + "\t}" + NL + "\t\t" + NL + "}else if(!flag_";
  protected final String TEXT_64 = "){" + NL + "\t" + NL + "\tout_";
  protected final String TEXT_65 = ".write(tempSt_";
  protected final String TEXT_66 = ");" + NL + "\t" + NL + "\tout_";
  protected final String TEXT_67 = ".newLine();" + NL + "\t" + NL + "\tif(!start_";
  protected final String TEXT_68 = "){" + NL + "\t\t\t" + NL + "\t\tstart_";
  protected final String TEXT_69 = " = true;" + NL + "\t\t\t" + NL + "\t}" + NL + "\t" + NL + "}" + NL;
  protected final String TEXT_70 = "StringBuilder tempRes_";
  protected final String TEXT_71 = " = new StringBuilder(\"<\"+";
  protected final String TEXT_72 = ");" + NL;
  protected final String TEXT_73 = "tempRes_";
  protected final String TEXT_74 = ".append(\" \"+";
  protected final String TEXT_75 = "+\"=\\\"\"+";
  protected final String TEXT_76 = "FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_77 = "), ";
  protected final String TEXT_78 = ", ";
  protected final String TEXT_79 = ")";
  protected final String TEXT_80 = "FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = "), ";
  protected final String TEXT_83 = ", ";
  protected final String TEXT_84 = ")";
  protected final String TEXT_85 = ".";
  protected final String TEXT_86 = "((";
  protected final String TEXT_87 = ".";
  protected final String TEXT_88 = " == null)?\"\":(";
  protected final String TEXT_89 = "TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_90 = "FormatterUtils.format_Date(";
  protected final String TEXT_91 = ".";
  protected final String TEXT_92 = ", ";
  protected final String TEXT_93 = ")";
  protected final String TEXT_94 = ")";
  protected final String TEXT_95 = "TalendString.replaceSpecialCharForXML(java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_96 = ".";
  protected final String TEXT_97 = ")).toString())";
  protected final String TEXT_98 = "TalendString.replaceSpecialCharForXML(String.valueOf(";
  protected final String TEXT_99 = "))";
  protected final String TEXT_100 = "TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_101 = ".";
  protected final String TEXT_102 = ")";
  protected final String TEXT_103 = "))";
  protected final String TEXT_104 = "+\"\\\"\");" + NL;
  protected final String TEXT_105 = "tempRes_";
  protected final String TEXT_106 = ".append(\">\");" + NL;
  protected final String TEXT_107 = "tempRes_";
  protected final String TEXT_108 = ".append(\"/>\");" + NL;
  protected final String TEXT_109 = "out_";
  protected final String TEXT_110 = ".write(tempRes_";
  protected final String TEXT_111 = ".toString());" + NL + "" + NL + "out_";
  protected final String TEXT_112 = ".newLine();" + NL;
  protected final String TEXT_113 = "out_";
  protected final String TEXT_114 = ".write(\"<\"+";
  protected final String TEXT_115 = "+\">\"+";
  protected final String TEXT_116 = "FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_117 = "), ";
  protected final String TEXT_118 = ", ";
  protected final String TEXT_119 = ")";
  protected final String TEXT_120 = "FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_121 = ".";
  protected final String TEXT_122 = "), ";
  protected final String TEXT_123 = ", ";
  protected final String TEXT_124 = ")";
  protected final String TEXT_125 = ".";
  protected final String TEXT_126 = "((";
  protected final String TEXT_127 = ".";
  protected final String TEXT_128 = " == null)?\"\":(";
  protected final String TEXT_129 = "TalendString.checkCDATAForXML(";
  protected final String TEXT_130 = "FormatterUtils.format_Date(";
  protected final String TEXT_131 = ".";
  protected final String TEXT_132 = ", ";
  protected final String TEXT_133 = ")";
  protected final String TEXT_134 = ")";
  protected final String TEXT_135 = "TalendString.checkCDATAForXML(java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_136 = ".";
  protected final String TEXT_137 = ")).toString())";
  protected final String TEXT_138 = "TalendString.checkCDATAForXML(String.valueOf(";
  protected final String TEXT_139 = "))";
  protected final String TEXT_140 = "TalendString.checkCDATAForXML(";
  protected final String TEXT_141 = ".";
  protected final String TEXT_142 = ")";
  protected final String TEXT_143 = ".getDocument().getRootElement().asXML()";
  protected final String TEXT_144 = "TalendString.checkCDATAForXML(";
  protected final String TEXT_145 = ".";
  protected final String TEXT_146 = ")";
  protected final String TEXT_147 = "))";
  protected final String TEXT_148 = "+\"</\"+";
  protected final String TEXT_149 = "+\">\");" + NL + "" + NL + "out_";
  protected final String TEXT_150 = ".newLine();" + NL;
  protected final String TEXT_151 = "out_";
  protected final String TEXT_152 = ".write(\"</\"+";
  protected final String TEXT_153 = "+\">\");" + NL + "" + NL + "out_";
  protected final String TEXT_154 = ".newLine();" + NL;
  protected final String TEXT_155 = NL;
  protected final String TEXT_156 = NL + "    if(nb_line_";
  protected final String TEXT_157 = "%";
  protected final String TEXT_158 = " == 0) {" + NL + "    out_";
  protected final String TEXT_159 = ".flush();" + NL + "    }";
  protected final String TEXT_160 = NL + "nb_line_";
  protected final String TEXT_161 = "++;" + NL;
  protected final String TEXT_162 = NL + "currentRowCount_";
  protected final String TEXT_163 = "++;" + NL + "if(currentRowCount_";
  protected final String TEXT_164 = " == ";
  protected final String TEXT_165 = "){";
  protected final String TEXT_166 = NL + "out_";
  protected final String TEXT_167 = ".write(groupby_";
  protected final String TEXT_168 = "[";
  protected final String TEXT_169 = "][1]);" + NL + "out_";
  protected final String TEXT_170 = ".newLine();\t\t";
  protected final String TEXT_171 = NL + "out_";
  protected final String TEXT_172 = ".write(footers_";
  protected final String TEXT_173 = "[";
  protected final String TEXT_174 = "]);" + NL + "out_";
  protected final String TEXT_175 = ".newLine();";
  protected final String TEXT_176 = "\t" + NL + "\tout_";
  protected final String TEXT_177 = ".close();" + NL + "\tcurrentFileCount_";
  protected final String TEXT_178 = "++;\t" + NL + "" + NL + "    sb_";
  protected final String TEXT_179 = " = new StringBuffer(canonicalPath_";
  protected final String TEXT_180 = ");" + NL + "    lastIndexOf_";
  protected final String TEXT_181 = " = canonicalPath_";
  protected final String TEXT_182 = ".lastIndexOf('.');" + NL + "    position_";
  protected final String TEXT_183 = " = lastIndexOf_";
  protected final String TEXT_184 = " > -1 ? lastIndexOf_";
  protected final String TEXT_185 = " : canonicalPath_";
  protected final String TEXT_186 = ".length();" + NL + "    sb_";
  protected final String TEXT_187 = ".insert(position_";
  protected final String TEXT_188 = ", currentFileCount_";
  protected final String TEXT_189 = ");" + NL + "    " + NL + "    fileName_";
  protected final String TEXT_190 = " = sb_";
  protected final String TEXT_191 = ".toString();" + NL + "    file_";
  protected final String TEXT_192 = " = new java.io.File(fileName_";
  protected final String TEXT_193 = ");" + NL + "    " + NL + "\tout_";
  protected final String TEXT_194 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(file_";
  protected final String TEXT_195 = "), ";
  protected final String TEXT_196 = "));    " + NL;
  protected final String TEXT_197 = NL + "\tstart_";
  protected final String TEXT_198 = " = false;  ";
  protected final String TEXT_199 = "\t" + NL + "\tout_";
  protected final String TEXT_200 = ".write(headers_";
  protected final String TEXT_201 = "[";
  protected final String TEXT_202 = "]);" + NL + "\tout_";
  protected final String TEXT_203 = ".newLine();";
  protected final String TEXT_204 = "\t" + NL + "\tgroupby_";
  protected final String TEXT_205 = "[";
  protected final String TEXT_206 = "][0] = \"\";";
  protected final String TEXT_207 = "\t" + NL + "    currentRowCount_";
  protected final String TEXT_208 = " = 0;" + NL + "}";
  protected final String TEXT_209 = NL;

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
    if (metadata!=null) {
    boolean flushOnRow = ("true").equals(ElementParameterParser.getValue(node, "__FLUSHONROW__")); 
    String flushMod = ElementParameterParser.getValue(node, "__FLUSHONROW_NUM__");       
    String split = ElementParameterParser.getValue(node, "__SPLIT__");
	String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
	String fileName = ElementParameterParser.getValue(node, "__FILENAME__");
	List rootTags = (List)ElementParameterParser.getObjectValue(node, "__ROOT_TAGS__");
    int footers = rootTags.size();
    List<Map<String, String>> columnMapping = 
    		(List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__MAPPING__"
            );
    String useDynamicGrouping = ElementParameterParser.getValue(
            node,
            "__USE_DYNAMIC_GROUPING__"
        );
    List<Map<String, String>> groupBys =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__GROUP_BY__"
            );
            
    String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
	boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
	String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
	String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
	
    if (("false").equals(useDynamicGrouping)) {
        groupBys.clear();
    }
    if (encoding!=null) {
        if (("").equals(encoding)) {
                encoding = "ISO-8859-15";
        }
    }
    String groupby[][] = new String[groupBys.size()][3];
    for(int i = 0; i < groupBys.size(); i++){
    	groupby[i][0] = groupBys.get(i).get("COLUMN");
    	groupby[i][1] = groupBys.get(i).get("LABEL");
    }
    int atts = 0;
    int tags = 0;
    outter1:
    for(int i = 0; i < columnMapping.size(); i++){
    	Map<String, String> map = columnMapping.get(i);
    	String col = metadata.getListColumns().get(i).getLabel();
    	for(int j = 0; j < groupby.length; j++){
    		if(groupby[j][0].equals(col)){
    			if(("true").equals(map.get("SCHEMA_COLUMN_NAME"))){
    				groupby[j][2] = col;
    				groupby[j][2] = TalendQuoteUtils.addQuotes(groupby[j][2]);
    			}else{
    				groupby[j][2] = map.get("LABEL");
    			}
    			continue outter1;
    		}
    	}
    	if(("true").equals(map.get("AS_ATTRIBUTE"))){
    		atts ++;
    	}else{
    		tags ++;
    	}
    }
    String[][] attribute = new String[atts][2];
    String[][] tag = new String[tags][2];
    int ia=0;
    int it=0;
    outter2:
    for(int i = 0; i < columnMapping.size(); i++){
    	Map<String, String> map = columnMapping.get(i);
    	String col = metadata.getListColumns().get(i).getLabel();
    	for(int j = 0; j < groupby.length; j++){
    		if(groupby[j][0].equals(col)){
    			continue outter2;
    		}
    	}
    	if(("true").equals(map.get("AS_ATTRIBUTE"))){
    		if(("true").equals(map.get("SCHEMA_COLUMN_NAME"))){
    			attribute[ia][1] = col;
    			attribute[ia][1] = TalendQuoteUtils.addQuotes(attribute[ia][1]);
    		}else{
    			attribute[ia][1] = map.get("LABEL");
    		}
    		attribute[ia++][0] = col;
    	}else{
    		if(("true").equals(map.get("SCHEMA_COLUMN_NAME"))){
    			tag[it][1] = col;
    			tag[it][1] = TalendQuoteUtils.addQuotes(tag[it][1]);
    		}else{
    			tag[it][1] = map.get("LABEL");
    		}
    	    tag[it++][0] = col;
    	}
    }
	if(groupby.length>0){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
	}

    
	List< ? extends IConnection> conns = node.getIncomingConnections();
	if(conns!=null && conns.size()>0){
		IConnection conn = conns.get(0);
		for(int i = 0; i < groupby.length; i++){
			boolean needReplace = false;
			boolean isDate = false;
			boolean isByteArray = false;
			String pattern = null;
			boolean isPrimitive = false;
			boolean isBigDecimal = false;
			boolean isAdvancedSeparator = false;
			Integer precision = null;
			for(IMetadataColumn column:metadata.getListColumns()) {
				if(column.getLabel().equals(groupby[i][0])){
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.STRING){
						needReplace = true;
					}
					if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.DATE){
						pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
						if(pattern != null && (pattern.contains("&") || pattern.contains("<") || pattern.contains(">") || pattern.contains("'") || pattern.contains("\""))){
							needReplace = true;
						}
						isDate = true;
					}
					if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.BYTE_ARRAY){
						isByteArray = true;
					}else if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.BIGDECIMAL){
						isBigDecimal = true;
					}
					isPrimitive = JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable());
					
					isAdvancedSeparator = advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable());
					
					precision = column.getPrecision();
					break;
				}
			}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(groupby[i][2] );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(groupby[i][1] );
    stringBuffer.append(TEXT_9);
    
							if(isAdvancedSeparator){
								if(isBigDecimal){
								
    stringBuffer.append(TEXT_10);
    stringBuffer.append(precision == null? conn.getName() + "." + groupby[i][0] : conn.getName() + "." + groupby[i][0] + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_12);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_13);
    
								}else {
								
    stringBuffer.append(TEXT_14);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(groupby[i][0] );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_17);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_18);
    
								}
							}else if(isPrimitive){
								
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(groupby[i][0] );
    
							}else{
								
    stringBuffer.append(TEXT_20);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(groupby[i][0] );
    stringBuffer.append(TEXT_22);
    
									if(isDate && pattern != null){
									
    if(needReplace){
    stringBuffer.append(TEXT_23);
    }
    stringBuffer.append(TEXT_24);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(groupby[i][0] );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(pattern );
    stringBuffer.append(TEXT_27);
    if(needReplace){
    stringBuffer.append(TEXT_28);
    }
    
									}else if(isByteArray){
									
    stringBuffer.append(TEXT_29);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(groupby[i][0] );
    stringBuffer.append(TEXT_31);
    
									}else if(isBigDecimal){
									
    stringBuffer.append(TEXT_32);
    stringBuffer.append(precision == null? conn.getName() + "." + tag[i][0] : conn.getName() + "." + tag[i][0] + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_33);
    
									}else{
									
    if(needReplace){
    stringBuffer.append(TEXT_34);
    }
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(groupby[i][0] );
    if(needReplace){
    stringBuffer.append(TEXT_36);
    }
    
									}
    stringBuffer.append(TEXT_37);
    
							}
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(groupby.length-1 );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    
		}

    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(ElementParameterParser.getValue(node, "__ROW_TAG__"));
    stringBuffer.append(TEXT_72);
    
for(int i = 0; i < attribute.length; i++){
	boolean needReplace = false;
	boolean isDate = false;
	boolean isByteArray = false;
	String pattern = null;
	boolean isPrimitive = false;
	boolean isBigDecimal = false;
	boolean isAdvancedSeparator = false;
	Integer precision = null;
	for(IMetadataColumn column:metadata.getListColumns()) {
		if(column.getLabel().equals(attribute[i][0])){
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.STRING){
				needReplace = true;
			}
			if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.DATE){
				pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
				if(pattern != null && (pattern.contains("&") || pattern.contains("<") || pattern.contains(">") || pattern.contains("'") || pattern.contains("\""))){
					needReplace = true;
				}
				isDate = true;
			}
			if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.BYTE_ARRAY){
				isByteArray = true;
			}else if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.BIGDECIMAL){
				isBigDecimal = true;
			}
			isPrimitive = JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable());
			
			isAdvancedSeparator = advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable());
			
			precision = column.getPrecision();
			break;
		}
	}

    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(attribute[i][1] );
    stringBuffer.append(TEXT_75);
    
							if(isAdvancedSeparator){
								if(isBigDecimal){
								
    stringBuffer.append(TEXT_76);
    stringBuffer.append(precision == null? conn.getName() + "." + attribute[i][0] : conn.getName() + "." + attribute[i][0] + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_77);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_78);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_79);
    
								}else {
								
    stringBuffer.append(TEXT_80);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(attribute[i][0] );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_83);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_84);
    
								}
							}else if(isPrimitive){
								
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(attribute[i][0] );
    
							}else{
								
    stringBuffer.append(TEXT_86);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(attribute[i][0] );
    stringBuffer.append(TEXT_88);
    
									if(isDate && pattern != null){
									
    if(needReplace){
    stringBuffer.append(TEXT_89);
    }
    stringBuffer.append(TEXT_90);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(attribute[i][0] );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(pattern );
    stringBuffer.append(TEXT_93);
    if(needReplace){
    stringBuffer.append(TEXT_94);
    }
    
									}else if(isByteArray){
									
    stringBuffer.append(TEXT_95);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(attribute[i][0] );
    stringBuffer.append(TEXT_97);
    
									}else if(isBigDecimal){
									
    stringBuffer.append(TEXT_98);
    stringBuffer.append(precision == null? conn.getName() + "." + tag[i][0] : conn.getName() + "." + tag[i][0] + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_99);
    
									}else{
									
    if(needReplace){
    stringBuffer.append(TEXT_100);
    }
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(attribute[i][0] );
    if(needReplace){
    stringBuffer.append(TEXT_102);
    }
    
									}
    stringBuffer.append(TEXT_103);
    
							}
    stringBuffer.append(TEXT_104);
    
}

    
if(tags > 0){

    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    
}else{

    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    
}

    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    
for(int i = 0; i < tag.length; i++){
	boolean needReplace = false;
	boolean isDate = false;
	boolean isByteArray = false;
	String pattern = null;
	boolean isPrimitive = false;
	boolean isBigDecimal = false;
	boolean isAdvancedSeparator = false;
	boolean isDocument = false;
	Integer precision = null;
	for(IMetadataColumn column:metadata.getListColumns()) {
		if(column.getLabel().equals(tag[i][0])){
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.STRING){
				needReplace = true;
			}
			if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.DATE){
				pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
				if(pattern != null && (pattern.contains("&") || pattern.contains("<") || pattern.contains(">") || pattern.contains("'") || pattern.contains("\""))){
					needReplace = true;
				}
				isDate = true;
			}
			if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.BYTE_ARRAY){
				isByteArray = true;
			}else if(JavaTypesManager.getJavaTypeFromId(column.getTalendType()) == JavaTypesManager.BIGDECIMAL){
				isBigDecimal = true;
			}
			isPrimitive = JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable());
			isAdvancedSeparator = advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable());
						
			precision = column.getPrecision();

			isDocument = "id_Document".equals(column.getTalendType());
			break;
		}
	}

    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(tag[i][1]);
    stringBuffer.append(TEXT_115);
    
							if(isAdvancedSeparator){
								if(isBigDecimal){
								
    stringBuffer.append(TEXT_116);
    stringBuffer.append(precision == null? conn.getName() + "." + tag[i][0] : conn.getName() + "." + tag[i][0] + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_117);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_118);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_119);
    
								}else {
								
    stringBuffer.append(TEXT_120);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(tag[i][0] );
    stringBuffer.append(TEXT_122);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_123);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_124);
    
								}
							}else if(isPrimitive){
								
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(tag[i][0] );
    
							}else{
								
    stringBuffer.append(TEXT_126);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(tag[i][0] );
    stringBuffer.append(TEXT_128);
    
									if(isDate && pattern != null){
									
    if(needReplace){
    stringBuffer.append(TEXT_129);
    }
    stringBuffer.append(TEXT_130);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(tag[i][0] );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(pattern );
    stringBuffer.append(TEXT_133);
    if(needReplace){
    stringBuffer.append(TEXT_134);
    }
    
									}else if(isByteArray){
									
    stringBuffer.append(TEXT_135);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(tag[i][0] );
    stringBuffer.append(TEXT_137);
    
									}else if(isBigDecimal){
									
    stringBuffer.append(TEXT_138);
    stringBuffer.append(precision == null? conn.getName() + "." + tag[i][0] : conn.getName() + "." + tag[i][0] + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_139);
    
									}else if(isDocument){
									
    if(needReplace){
    stringBuffer.append(TEXT_140);
    }
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(tag[i][0] );
    if(needReplace){
    stringBuffer.append(TEXT_142);
    }
    stringBuffer.append(TEXT_143);
    
									}else{
									
    if(needReplace){
    stringBuffer.append(TEXT_144);
    }
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(tag[i][0] );
    if(needReplace){
    stringBuffer.append(TEXT_146);
    }
    
									}
    stringBuffer.append(TEXT_147);
    
							}
    stringBuffer.append(TEXT_148);
    stringBuffer.append(tag[i][1]);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    
	if(i == tag.length -1){

    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(ElementParameterParser.getValue(node, "__ROW_TAG__"));
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    
	}
}

    stringBuffer.append(TEXT_155);
     if(flushOnRow) { 
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    
	}

    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    
    if(("true").equals(split)){

    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(ElementParameterParser.getValue(node, "__SPLIT_EVERY__") );
    stringBuffer.append(TEXT_165);
    
		for(int i = groupby.length - 1; i >=0; i--){

    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    
		}
		for(int i = footers - 1; i >= 0 ;i--){

    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_175);
    
		}

    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_192);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_195);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_196);
    
	if(groupby.length > 0){

    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_198);
    
	}
	for(int i = 0; i <= footers;i++){

    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_201);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_203);
    
	}
	for(int i = 0; i < groupby.length; i++){

    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_206);
    
	}

    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_208);
    
	}
		}
	}
}

    stringBuffer.append(TEXT_209);
    return stringBuffer.toString();
  }
}
