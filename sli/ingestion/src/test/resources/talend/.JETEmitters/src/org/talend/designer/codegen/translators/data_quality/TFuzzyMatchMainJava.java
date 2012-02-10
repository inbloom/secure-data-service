package org.talend.designer.codegen.translators.data_quality;

import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TFuzzyMatchMainJava
{
  protected static String nl;
  public static synchronized TFuzzyMatchMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFuzzyMatchMainJava result = new TFuzzyMatchMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t\t\t";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ";" + NL + "" + NL + "\t\t\t\t";
  protected final String TEXT_7 = NL + "\t\t\t\t";
  protected final String TEXT_8 = "            " + NL + "" + NL + "        \t";
  protected final String TEXT_9 = "                 ";
  protected final String TEXT_10 = NL + "                ";
  protected final String TEXT_11 = ".VALUE = ";
  protected final String TEXT_12 = ";" + NL + "        \t";
  protected final String TEXT_13 = NL + "        \t\t";
  protected final String TEXT_14 = ".VALUE = -1;        \t\t" + NL + "        \t";
  protected final String TEXT_15 = NL + "        \t\t";
  protected final String TEXT_16 = ".VALUE = null;" + NL + "        \t";
  protected final String TEXT_17 = NL + "                \t";
  protected final String TEXT_18 = NL + "                ";
  protected final String TEXT_19 = ".MATCHING = ";
  protected final String TEXT_20 = ";\t\t\t" + NL + "\t\t\t\t\t\t\t" + NL + "\t\t\t\tjava.util.Iterator<";
  protected final String TEXT_21 = "Struct> tItr_";
  protected final String TEXT_22 = "_";
  protected final String TEXT_23 = " = tSet_";
  protected final String TEXT_24 = "_";
  protected final String TEXT_25 = ".iterator();" + NL;
  protected final String TEXT_26 = "  " + NL + "                    String tomatch_";
  protected final String TEXT_27 = " = (\"\" + ";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = ")";
  protected final String TEXT_30 = ".toLowerCase()";
  protected final String TEXT_31 = ";" + NL + "                ";
  protected final String TEXT_32 = NL + "                \t\tint closeValue_";
  protected final String TEXT_33 = " = -1;" + NL + "                        String matching_";
  protected final String TEXT_34 = " = null;" + NL + "                        " + NL + "                        while(tItr_";
  protected final String TEXT_35 = "_";
  protected final String TEXT_36 = ".hasNext()){" + NL + "                        " + NL + "                        \tString lookUpValue_";
  protected final String TEXT_37 = " = \"\" + tItr_";
  protected final String TEXT_38 = "_";
  protected final String TEXT_39 = ".next().";
  protected final String TEXT_40 = ";" + NL + "                        \t" + NL + "                        \tint distance_";
  protected final String TEXT_41 = " = org.apache.commons.lang.StringUtils.getLevenshteinDistance(tomatch_";
  protected final String TEXT_42 = ", lookUpValue_";
  protected final String TEXT_43 = ".toLowerCase()";
  protected final String TEXT_44 = ");" + NL + "                        \t" + NL + "                        \tif(";
  protected final String TEXT_45 = " <= distance_";
  protected final String TEXT_46 = " && distance_";
  protected final String TEXT_47 = " <= ";
  protected final String TEXT_48 = "){" + NL + "                        \t\t//it will get the close value to the min " + NL + "                        \t\tif(closeValue_";
  protected final String TEXT_49 = " == -1 || distance_";
  protected final String TEXT_50 = " < closeValue_";
  protected final String TEXT_51 = "){" + NL + "                        \t\t" + NL + "                        \t\t\tcloseValue_";
  protected final String TEXT_52 = " = distance_";
  protected final String TEXT_53 = ";" + NL + "                        \t\t\t" + NL + "                        \t\t\tmatching_";
  protected final String TEXT_54 = " = lookUpValue_";
  protected final String TEXT_55 = ";\t" + NL + "                        \t\t\t" + NL + "                            \t\tif(closeValue_";
  protected final String TEXT_56 = " == ";
  protected final String TEXT_57 = "){                            \t\t" + NL + "                            \t\t\tbreak;                            \t\t\t\t\t\t\t" + NL + "                            \t\t}" + NL + "                        \t\t}" + NL + "                        \t}" + NL + "                        \t" + NL + "                        }" + NL + "                        " + NL + "                        if(closeValue_";
  protected final String TEXT_58 = " != -1){                        " + NL + "                    " + NL + "                        \t";
  protected final String TEXT_59 = ".VALUE = closeValue_";
  protected final String TEXT_60 = " + \"\"";
  protected final String TEXT_61 = ";" + NL + "                        " + NL + "                        \t";
  protected final String TEXT_62 = ".MATCHING = matching_";
  protected final String TEXT_63 = ";" + NL + "                        \t" + NL + "                        }" + NL + "                ";
  protected final String TEXT_64 = NL + "                \t\tStringBuilder matching_";
  protected final String TEXT_65 = " = null;" + NL + "                \t\tStringBuilder value_";
  protected final String TEXT_66 = " = null;" + NL + "                " + NL + "                        while(tItr_";
  protected final String TEXT_67 = "_";
  protected final String TEXT_68 = ".hasNext()){" + NL + "                        " + NL + "                        \tString lookUpValue_";
  protected final String TEXT_69 = " = \"\" + tItr_";
  protected final String TEXT_70 = "_";
  protected final String TEXT_71 = ".next().";
  protected final String TEXT_72 = ";" + NL + "                        \t" + NL + "                        \tint distance_";
  protected final String TEXT_73 = " = org.apache.commons.lang.StringUtils.getLevenshteinDistance(tomatch_";
  protected final String TEXT_74 = ", lookUpValue_";
  protected final String TEXT_75 = ".toLowerCase()";
  protected final String TEXT_76 = ");" + NL + "                        \t" + NL + "                        \tif(";
  protected final String TEXT_77 = " <= distance_";
  protected final String TEXT_78 = " && distance_";
  protected final String TEXT_79 = " <= ";
  protected final String TEXT_80 = "){" + NL + "                        \t                        \t\t " + NL + "                        \t\tif(matching_";
  protected final String TEXT_81 = " == null){" + NL + "                        \t\t" + NL + "                        \t\t\tvalue_";
  protected final String TEXT_82 = " = new StringBuilder(distance_";
  protected final String TEXT_83 = " + \"\");" + NL + "                        \t\t\t                        \t\t                        \t\t" + NL + "\t\t\t\t\t\t\t\t\tmatching_";
  protected final String TEXT_84 = " = new StringBuilder(lookUpValue_";
  protected final String TEXT_85 = ");\t\t\t\t\t\t\t\t\t" + NL + "                        \t\t}else{" + NL + "                        \t\t" + NL + "     \t\t\t\t\t\t\t\tvalue_";
  protected final String TEXT_86 = ".append(";
  protected final String TEXT_87 = ").append(distance_";
  protected final String TEXT_88 = ");" + NL + "" + NL + "                        \t\t\tmatching_";
  protected final String TEXT_89 = ".append(";
  protected final String TEXT_90 = ").append(lookUpValue_";
  protected final String TEXT_91 = ");" + NL + "                        \t\t\t                        \t\t" + NL + "                        \t\t}" + NL + "                        \t}" + NL + "                        \t" + NL + "                        }" + NL + "                        " + NL + "                        if(matching_";
  protected final String TEXT_92 = " != null){" + NL + "                        " + NL + "                        \t";
  protected final String TEXT_93 = ".VALUE = value_";
  protected final String TEXT_94 = ".toString();" + NL + "                        " + NL + "                        \t";
  protected final String TEXT_95 = ".MATCHING = matching_";
  protected final String TEXT_96 = ".toString();" + NL + "                        \t" + NL + "                        }" + NL + "                ";
  protected final String TEXT_97 = NL + "                " + NL + "                \t";
  protected final String TEXT_98 = ".VALUE = metaphone_";
  protected final String TEXT_99 = ".metaphone(\"\" + ";
  protected final String TEXT_100 = ".";
  protected final String TEXT_101 = ");" + NL + "                ";
  protected final String TEXT_102 = NL + "                        while(tItr_";
  protected final String TEXT_103 = "_";
  protected final String TEXT_104 = ".hasNext()){" + NL + "                        " + NL + "                        \tString lookUpValue_";
  protected final String TEXT_105 = " = \"\" + tItr_";
  protected final String TEXT_106 = "_";
  protected final String TEXT_107 = ".next().";
  protected final String TEXT_108 = ";" + NL + "                        \t" + NL + "                        \tif(metaphone_";
  protected final String TEXT_109 = ".metaphone(lookUpValue_";
  protected final String TEXT_110 = ").equals(";
  protected final String TEXT_111 = ".VALUE)){" + NL + "                        \t" + NL + "                        \t\t";
  protected final String TEXT_112 = ".MATCHING = lookUpValue_";
  protected final String TEXT_113 = ";" + NL + "                        \t\t" + NL + "                        \t\tbreak;" + NL + "                        \t" + NL + "                        \t}" + NL + "                        \t" + NL + "                        }" + NL + "                ";
  protected final String TEXT_114 = NL + "                        StringBuilder matching_";
  protected final String TEXT_115 = " = null;" + NL + "                        " + NL + "                        while(tItr_";
  protected final String TEXT_116 = "_";
  protected final String TEXT_117 = ".hasNext()){" + NL + "                        " + NL + "                        \tString lookUpValue_";
  protected final String TEXT_118 = " = \"\" + tItr_";
  protected final String TEXT_119 = "_";
  protected final String TEXT_120 = ".next().";
  protected final String TEXT_121 = ";" + NL + "                        \t" + NL + "                        \tif(metaphone_";
  protected final String TEXT_122 = ".metaphone(lookUpValue_";
  protected final String TEXT_123 = ").equals(";
  protected final String TEXT_124 = ".VALUE)){" + NL + "                        \t" + NL + "                        \t\tif(matching_";
  protected final String TEXT_125 = " == null){" + NL + "                        \t\t\t" + NL + "                        \t\t\tmatching_";
  protected final String TEXT_126 = " = new StringBuilder(lookUpValue_";
  protected final String TEXT_127 = ");" + NL + "                        \t\t" + NL + "                        \t\t}else{" + NL + "                        \t\t" + NL + "                        \t\t\tmatching_";
  protected final String TEXT_128 = ".append(";
  protected final String TEXT_129 = ").append(lookUpValue_";
  protected final String TEXT_130 = ");" + NL + "                        \t\t\t" + NL + "                        \t\t}" + NL + "                        \t" + NL + "                        \t}" + NL + "                        \t" + NL + "                        }" + NL + "                        " + NL + "                        if(matching_";
  protected final String TEXT_131 = " != null){" + NL + "                        " + NL + "                        \t";
  protected final String TEXT_132 = ".MATCHING = matching_";
  protected final String TEXT_133 = ".toString();" + NL + "                        \t" + NL + "                        }" + NL + "                ";
  protected final String TEXT_134 = NL + "                \t";
  protected final String TEXT_135 = ".VALUE = doublemetaphone_";
  protected final String TEXT_136 = ".doubleMetaphone(\"\" + ";
  protected final String TEXT_137 = ".";
  protected final String TEXT_138 = ");" + NL + "                ";
  protected final String TEXT_139 = NL + "                        while(tItr_";
  protected final String TEXT_140 = "_";
  protected final String TEXT_141 = ".hasNext()){" + NL + "                        " + NL + "                        \tString lookUpValue_";
  protected final String TEXT_142 = " = \"\" + tItr_";
  protected final String TEXT_143 = "_";
  protected final String TEXT_144 = ".next().";
  protected final String TEXT_145 = ";" + NL + "                        \t" + NL + "                        \tif(doublemetaphone_";
  protected final String TEXT_146 = ".doubleMetaphone(lookUpValue_";
  protected final String TEXT_147 = ").equals(";
  protected final String TEXT_148 = ".VALUE)){" + NL + "                        \t" + NL + "                        \t\t";
  protected final String TEXT_149 = ".MATCHING = lookUpValue_";
  protected final String TEXT_150 = ";" + NL + "                        \t\t" + NL + "                        \t\tbreak;" + NL + "                        \t" + NL + "                        \t}" + NL + "                        \t" + NL + "                        }" + NL + "                ";
  protected final String TEXT_151 = NL + "                        StringBuilder matching_";
  protected final String TEXT_152 = " = null;" + NL + "                        " + NL + "                        while(tItr_";
  protected final String TEXT_153 = "_";
  protected final String TEXT_154 = ".hasNext()){" + NL + "                        " + NL + "                        \tString lookUpValue_";
  protected final String TEXT_155 = " = \"\" + tItr_";
  protected final String TEXT_156 = "_";
  protected final String TEXT_157 = ".next().";
  protected final String TEXT_158 = ";" + NL + "                        \t" + NL + "                        \tif(doublemetaphone_";
  protected final String TEXT_159 = ".doubleMetaphone(lookUpValue_";
  protected final String TEXT_160 = ").equals(";
  protected final String TEXT_161 = ".VALUE)){" + NL + "                        \t" + NL + "                        \t\tif(matching_";
  protected final String TEXT_162 = " == null){" + NL + "                        \t\t\t" + NL + "                        \t\t\tmatching_";
  protected final String TEXT_163 = " = new StringBuilder(lookUpValue_";
  protected final String TEXT_164 = ");" + NL + "                        \t\t" + NL + "                        \t\t}else{" + NL + "                        \t\t" + NL + "                        \t\t\tmatching_";
  protected final String TEXT_165 = ".append(";
  protected final String TEXT_166 = ").append(lookUpValue_";
  protected final String TEXT_167 = ");" + NL + "                        \t\t\t" + NL + "                        \t\t}" + NL + "                        \t" + NL + "                        \t}" + NL + "                        \t" + NL + "                        }" + NL + "                        " + NL + "                        if(matching_";
  protected final String TEXT_168 = " != null){" + NL + "                        " + NL + "                        \t";
  protected final String TEXT_169 = ".MATCHING = matching_";
  protected final String TEXT_170 = ".toString();" + NL + "                        \t" + NL + "                        }" + NL + "                ";
  protected final String TEXT_171 = NL + "                nb_line_";
  protected final String TEXT_172 = "++;" + NL + "" + NL + "\t";
  protected final String TEXT_173 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_174 = ".";
  protected final String TEXT_175 = " = ";
  protected final String TEXT_176 = ".";
  protected final String TEXT_177 = ";    \t\t\t\t";
  protected final String TEXT_178 = NL;

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
    
    String colName = ElementParameterParser.getValue(node, "__MATCH__");
    String matchingType = ElementParameterParser.getValue(node, "__MATCHING_TYPE__");
    String min = ElementParameterParser.getValue(node, "__MIN__");
    if(("").equals(min)){
    	min="0";
    }
    String max = ElementParameterParser.getValue(node, "__MAX__");
    if(("").equals(max)){
    	max="0";
    }

	String uniqueS = ElementParameterParser.getValue(node, "__GET_UNIQUE__");
	boolean unique = ("true").equals(uniqueS);
	String caseS = ElementParameterParser.getValue(node, "__CASE_SENSITIVE__");
	boolean caseSens = ("true").equals(caseS);
	String separator = ElementParameterParser.getValue(node, "__ITEMSEPARATOR__");
	String lookUpColumn = ElementParameterParser.getValue(node, "__LOOKUP_COLUMN__");

    List< ? extends IConnection> conns = node.getIncomingConnections();
    IConnection inRefCon = null;
    IConnection inMainCon = null;
    for (IConnection connection : conns) {
        if (connection == null) {
            continue;
        }
        EConnectionType connectionType = connection.getLineStyle();
        if (connectionType == EConnectionType.FLOW_MAIN) {
            inMainCon = connection;
        } else if (connectionType == EConnectionType.FLOW_REF) {
            inRefCon = connection;
        }
    }
    
    if(inRefCon==null) return "";
    
	conns = null;

	conns = node.getOutgoingSortedConnections();
	String firstConnName = "";
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			firstConnName = conn.getName();
			
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				List<IMetadataColumn> listColumns = inMainCon.getMetadataTable().getListColumns();
				int sizeListColumns = listColumns.size();
				for (int valueN=0; valueN<sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inMainCon.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_6);
    				
				}
				
    stringBuffer.append(TEXT_7);
    
	//get the VALUE column type ( int or String ), the purpose is to handle this case: 
	//when "Levenshtein" and "Unique matching" and "user assign the VALUE column is int type"
	boolean isIntType = false;
	boolean isPrimitive = false;
	String defaultValue_VALUE = null;
	String defaultValue_MATCHING = null;
    for (IMetadataColumn column: metadata.getListColumns()) {
    	if(("VALUE").equals(column.getLabel())) {
    		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
            String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
            defaultValue_VALUE = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
            
    		//it can check the Integer and int
    		if(javaType == JavaTypesManager.INTEGER){
        		isIntType = true;
        		if(JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())){
        			isPrimitive = true;
        		}
    		}
    	}else if(("MATCHING").equals(column.getLabel())){    	
            String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
            defaultValue_MATCHING = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());    	
    	}
    }

    stringBuffer.append(TEXT_8);
    if(defaultValue_VALUE != null){ 
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(defaultValue_VALUE );
    stringBuffer.append(TEXT_12);
    }else if(isIntType){ 
    stringBuffer.append(TEXT_13);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_14);
    }else{ 
    stringBuffer.append(TEXT_15);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_16);
    }
    stringBuffer.append(TEXT_17);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(defaultValue_MATCHING );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
                if(("matchLevenshteinDistance").equals(matchingType)){
                
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(inMainCon.getName() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_29);
    if(!caseSens){
    stringBuffer.append(TEXT_30);
    }
    stringBuffer.append(TEXT_31);
    
                	if(unique){
                
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(lookUpColumn);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    if(!caseSens){
    stringBuffer.append(TEXT_43);
    }
    stringBuffer.append(TEXT_44);
    stringBuffer.append(min);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(max);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(min);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    if(!isIntType){
    stringBuffer.append(TEXT_60);
    }
    stringBuffer.append(TEXT_61);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    
                	}else{
                
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(lookUpColumn);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    if(!caseSens){
    stringBuffer.append(TEXT_75);
    }
    stringBuffer.append(TEXT_76);
    stringBuffer.append(min);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(max);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    
                	}
                }else if(("matchMetaphone").equals(matchingType)){
    stringBuffer.append(TEXT_97);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(inMainCon.getName() );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_101);
    
                	if(unique){
                
    stringBuffer.append(TEXT_102);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(lookUpColumn);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_111);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_113);
    
                	}else{
                
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(inRefCon.getName());
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(lookUpColumn);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_133);
    
                	}
                }else{
    stringBuffer.append(TEXT_134);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(inMainCon.getName() );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(colName );
    stringBuffer.append(TEXT_138);
    
                	if(unique){
                
    stringBuffer.append(TEXT_139);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(lookUpColumn);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_148);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    
                	}else{
                
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(inRefCon.getName() );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(lookUpColumn);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_168);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    
                	}
                }
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    
			}
		}
		if (conns.size()>1) {
			for (int i=1;i<conns.size();i++) {
				IConnection conn = conns.get(i);
				if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
			    	 for (IMetadataColumn column: metadata.getListColumns()) {
    stringBuffer.append(TEXT_173);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_177);
    
				 	}
				}
			}
		}
	}
}

    stringBuffer.append(TEXT_178);
    return stringBuffer.toString();
  }
}
