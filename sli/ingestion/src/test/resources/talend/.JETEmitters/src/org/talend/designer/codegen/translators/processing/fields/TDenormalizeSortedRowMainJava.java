package org.talend.designer.codegen.translators.processing.fields;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class TDenormalizeSortedRowMainJava
{
  protected static String nl;
  public static synchronized TDenormalizeSortedRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TDenormalizeSortedRowMainJava result = new TDenormalizeSortedRowMainJava();
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
  protected final String TEXT_9 = NL + "\tgroup_";
  protected final String TEXT_10 = "_";
  protected final String TEXT_11 = " = ";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = ";";
  protected final String TEXT_14 = NL + "\t\tdenormalize_";
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = ".add(";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + "\t\tdenormalize_";
  protected final String TEXT_20 = "_";
  protected final String TEXT_21 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ", ";
  protected final String TEXT_24 = "));";
  protected final String TEXT_25 = NL + "\t\tdenormalize_";
  protected final String TEXT_26 = "_";
  protected final String TEXT_27 = ".append(";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL + "}else{";
  protected final String TEXT_31 = NL + "while(true){";
  protected final String TEXT_32 = "if(group_";
  protected final String TEXT_33 = "_";
  protected final String TEXT_34 = " == null){" + NL + "\tif(";
  protected final String TEXT_35 = ".";
  protected final String TEXT_36 = " != null){" + NL + "\t\tsameGroup_";
  protected final String TEXT_37 = " = false;" + NL + "\t\tbreak;" + NL + "\t}" + NL + "}else{" + NL + "\tif(group_";
  protected final String TEXT_38 = "_";
  protected final String TEXT_39 = " == null || !group_";
  protected final String TEXT_40 = "_";
  protected final String TEXT_41 = ".equals(";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = ")){" + NL + "\t\tsameGroup_";
  protected final String TEXT_44 = " = false;" + NL + "\t\tbreak;" + NL + "\t}" + NL + "}";
  protected final String TEXT_45 = "if(group_";
  protected final String TEXT_46 = "_";
  protected final String TEXT_47 = " != ";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = "){" + NL + "\tsameGroup_";
  protected final String TEXT_50 = " = false;" + NL + "\tbreak;" + NL + "}";
  protected final String TEXT_51 = "break;";
  protected final String TEXT_52 = "}" + NL + "if(sameGroup_";
  protected final String TEXT_53 = "){";
  protected final String TEXT_54 = NL + "\tif(!denormalize_";
  protected final String TEXT_55 = "_";
  protected final String TEXT_56 = ".contains(";
  protected final String TEXT_57 = ".";
  protected final String TEXT_58 = ")){" + NL + "\t\tdenormalize_";
  protected final String TEXT_59 = "_";
  protected final String TEXT_60 = ".add(";
  protected final String TEXT_61 = ".";
  protected final String TEXT_62 = ");" + NL + "\t}";
  protected final String TEXT_63 = NL + "\tdenormalize_";
  protected final String TEXT_64 = "_";
  protected final String TEXT_65 = ".append(";
  protected final String TEXT_66 = ");";
  protected final String TEXT_67 = NL + "\t\tdenormalize_";
  protected final String TEXT_68 = "_";
  protected final String TEXT_69 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_70 = ".";
  protected final String TEXT_71 = ", ";
  protected final String TEXT_72 = "));";
  protected final String TEXT_73 = NL + "\t\tdenormalize_";
  protected final String TEXT_74 = "_";
  protected final String TEXT_75 = ".append(";
  protected final String TEXT_76 = ".";
  protected final String TEXT_77 = ");";
  protected final String TEXT_78 = "}";
  protected final String TEXT_79 = NL + "}" + NL + "" + NL + "int tempCount_";
  protected final String TEXT_80 = " = -1;";
  protected final String TEXT_81 = NL + "if( !sameGroup_";
  protected final String TEXT_82 = " ){" + NL + "\ttempCount_";
  protected final String TEXT_83 = "++;";
  protected final String TEXT_84 = NL + "\temmitArray_";
  protected final String TEXT_85 = "[tempCount_";
  protected final String TEXT_86 = "].";
  protected final String TEXT_87 = " = group_";
  protected final String TEXT_88 = "_";
  protected final String TEXT_89 = ";";
  protected final String TEXT_90 = NL + "\ttempSB_";
  protected final String TEXT_91 = ".delete(0, tempSB_";
  protected final String TEXT_92 = ".length());" + NL + "\t" + NL + "\tfor(";
  protected final String TEXT_93 = " tmp_";
  protected final String TEXT_94 = " : denormalize_";
  protected final String TEXT_95 = "_";
  protected final String TEXT_96 = "){" + NL + "\t\tif(tempSB_";
  protected final String TEXT_97 = ".length() > 0){" + NL + "\t\t\ttempSB_";
  protected final String TEXT_98 = ".append(";
  protected final String TEXT_99 = ");" + NL + "\t\t}";
  protected final String TEXT_100 = NL + "\t\ttempSB_";
  protected final String TEXT_101 = ".append(FormatterUtils.format_Date(tmp_";
  protected final String TEXT_102 = ", ";
  protected final String TEXT_103 = "));";
  protected final String TEXT_104 = NL + "\t\ttempSB_";
  protected final String TEXT_105 = ".append(tmp_";
  protected final String TEXT_106 = ");";
  protected final String TEXT_107 = NL + "\t}" + NL + "\t" + NL + "\tdenormalize_";
  protected final String TEXT_108 = "_";
  protected final String TEXT_109 = ".clear();" + NL + "\temmitArray_";
  protected final String TEXT_110 = "[tempCount_";
  protected final String TEXT_111 = "].";
  protected final String TEXT_112 = " = tempSB_";
  protected final String TEXT_113 = ".toString();";
  protected final String TEXT_114 = NL + "\temmitArray_";
  protected final String TEXT_115 = "[tempCount_";
  protected final String TEXT_116 = "].";
  protected final String TEXT_117 = " = denormalize_";
  protected final String TEXT_118 = "_";
  protected final String TEXT_119 = ".toString();" + NL + "\tdenormalize_";
  protected final String TEXT_120 = "_";
  protected final String TEXT_121 = ".delete(0, denormalize_";
  protected final String TEXT_122 = "_";
  protected final String TEXT_123 = ".length());";
  protected final String TEXT_124 = NL + "\tgroup_";
  protected final String TEXT_125 = "_";
  protected final String TEXT_126 = " = ";
  protected final String TEXT_127 = ".";
  protected final String TEXT_128 = ";";
  protected final String TEXT_129 = NL + "\t\tdenormalize_";
  protected final String TEXT_130 = "_";
  protected final String TEXT_131 = ".add(";
  protected final String TEXT_132 = ".";
  protected final String TEXT_133 = ");";
  protected final String TEXT_134 = NL + "\tdenormalize_";
  protected final String TEXT_135 = "_";
  protected final String TEXT_136 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_137 = ".";
  protected final String TEXT_138 = ", ";
  protected final String TEXT_139 = "));";
  protected final String TEXT_140 = NL + "\tdenormalize_";
  protected final String TEXT_141 = "_";
  protected final String TEXT_142 = ".append(";
  protected final String TEXT_143 = ".";
  protected final String TEXT_144 = ");";
  protected final String TEXT_145 = NL + "}";
  protected final String TEXT_146 = NL + "if( currentRowIndex_";
  protected final String TEXT_147 = "  == ";
  protected final String TEXT_148 = " ){" + NL + "\ttempCount_";
  protected final String TEXT_149 = "++;";
  protected final String TEXT_150 = NL + "\temmitArray_";
  protected final String TEXT_151 = "[tempCount_";
  protected final String TEXT_152 = "].";
  protected final String TEXT_153 = " = group_";
  protected final String TEXT_154 = "_";
  protected final String TEXT_155 = ";";
  protected final String TEXT_156 = NL + "\ttempSB_";
  protected final String TEXT_157 = ".delete(0, tempSB_";
  protected final String TEXT_158 = ".length());" + NL + "\tfor(";
  protected final String TEXT_159 = " tmp_";
  protected final String TEXT_160 = " : denormalize_";
  protected final String TEXT_161 = "_";
  protected final String TEXT_162 = "){" + NL + "\t\tif(tempSB_";
  protected final String TEXT_163 = ".length() > 0){" + NL + "\t\t\ttempSB_";
  protected final String TEXT_164 = ".append(";
  protected final String TEXT_165 = ");" + NL + "\t\t}";
  protected final String TEXT_166 = NL + "\t\ttempSB_";
  protected final String TEXT_167 = ".append(FormatterUtils.format_Date(tmp_";
  protected final String TEXT_168 = ", ";
  protected final String TEXT_169 = "));";
  protected final String TEXT_170 = NL + "\t\ttempSB_";
  protected final String TEXT_171 = ".append(tmp_";
  protected final String TEXT_172 = ");";
  protected final String TEXT_173 = NL + "\t}" + NL + "\tdenormalize_";
  protected final String TEXT_174 = "_";
  protected final String TEXT_175 = ".clear();" + NL + "\temmitArray_";
  protected final String TEXT_176 = "[tempCount_";
  protected final String TEXT_177 = "].";
  protected final String TEXT_178 = " = tempSB_";
  protected final String TEXT_179 = ".toString();";
  protected final String TEXT_180 = NL + "\temmitArray_";
  protected final String TEXT_181 = "[tempCount_";
  protected final String TEXT_182 = "].";
  protected final String TEXT_183 = " = denormalize_";
  protected final String TEXT_184 = "_";
  protected final String TEXT_185 = ".toString();" + NL + "\tdenormalize_";
  protected final String TEXT_186 = "_";
  protected final String TEXT_187 = ".delete(0, denormalize_";
  protected final String TEXT_188 = "_";
  protected final String TEXT_189 = ".length());";
  protected final String TEXT_190 = NL + "}" + NL + "for(int i_";
  protected final String TEXT_191 = "=0; i_";
  protected final String TEXT_192 = " <= tempCount_";
  protected final String TEXT_193 = "; i_";
  protected final String TEXT_194 = "++){";
  protected final String TEXT_195 = ".";
  protected final String TEXT_196 = " = emmitArray_";
  protected final String TEXT_197 = "[i_";
  protected final String TEXT_198 = "].";
  protected final String TEXT_199 = ";    \t\t\t\t";
  protected final String TEXT_200 = "nb_line_";
  protected final String TEXT_201 = "++;";
  protected final String TEXT_202 = NL;

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
				connName = conn.getName();
				inMetadata = conn.getMetadataTable();
    			break;
			}
		}
    if (metadata != null && inMetadata != null) { 
		List<IMetadataColumn> columns = inMetadata.getListColumns();
    	Map<String, String> typesMap = new HashMap<String, String>();
    	Map<String, String> patternsMap = new HashMap<String, String>();
    	for(IMetadataColumn column : columns){
    		String type = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
    		typesMap.put(column.getLabel(), type);
    		String pattern = ((column.getPattern() == null) || (column.getPattern().trim().length() == 0)) ? "" : column.getPattern();
    		patternsMap.put(column.getLabel(), pattern);
    	}
		String rowCount = ElementParameterParser.getValue(node,"__ROW_COUNT__");//?? i think we need it
		
		List<Map<String, String>> denormalizes = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__DENORMALIZE_COLUMNS__");
        List<String> denormalizeColumns = new ArrayList<String>();
        List<String> denormalizeColumnsType = new ArrayList<String>();
        List<String> denormalizeDelimiters = new ArrayList<String>();
        List<Boolean> denormalizeMergeFlags = new ArrayList<Boolean>();
        List<String> groupColumns = new ArrayList<String>();
        for(Map<String, String> denormalize : denormalizes){
        	String columnName = denormalize.get("INPUT_COLUMN");
        	if(denormalizeColumns.contains(columnName)){
        		continue;
        	}
        	denormalizeColumns.add(columnName);
        	denormalizeColumnsType.add(typesMap.get(columnName));
        	denormalizeDelimiters.add(denormalize.get("DELIMITER"));
        	denormalizeMergeFlags.add(("true").equals(denormalize.get("MERGE")));
        }
        for(IMetadataColumn column : columns){
        	String columnName = column.getLabel();
        	if(denormalizeColumns.contains(columnName)){
        		continue;
        	}
        	groupColumns.add(column.getLabel());
        }
        

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
if(groupColumns.size() > 0){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
	for(String columnName : groupColumns){

    stringBuffer.append(TEXT_9);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_13);
    	}
	for(int i = 0; i < denormalizeColumns.size(); i++){
		String columnName = denormalizeColumns.get(i);
		String columnType = denormalizeColumnsType.get(i);
		if(denormalizeMergeFlags.get(i)){

    stringBuffer.append(TEXT_14);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_18);
    
		}else{
			if((("java.util.Date").equals(columnType)) && (patternsMap.get(columnName).length() != 0)){

    stringBuffer.append(TEXT_19);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(patternsMap.get(columnName) );
    stringBuffer.append(TEXT_24);
    
			}else{

    stringBuffer.append(TEXT_25);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_29);
    
			}
		}
	}

    stringBuffer.append(TEXT_30);
    
if(groupColumns.size() > 0){//while loop

    stringBuffer.append(TEXT_31);
    	for(int i = 0; i < groupColumns.size(); i++){
		String columnName = groupColumns.get(i);
		String type = typesMap.get(columnName);
		if(("byte[]").equals(type) || ("Object").equals(type) || ("String").equals(type) || ("java.util.Date").equals(type)){

    stringBuffer.append(TEXT_32);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    		}else{

    stringBuffer.append(TEXT_45);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    		}
		if(i+1 == groupColumns.size()){

    stringBuffer.append(TEXT_51);
    		}
	}

    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    
}//while loop end

    
	for(int i = 0; i < denormalizeColumns.size(); i++){
		String columnName = denormalizeColumns.get(i);
		String columnType = denormalizeColumnsType.get(i);
		if(denormalizeMergeFlags.get(i)){

    stringBuffer.append(TEXT_54);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_62);
    
		}else{

    stringBuffer.append(TEXT_63);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(denormalizeDelimiters.get(i) );
    stringBuffer.append(TEXT_66);
    
			if((("java.util.Date").equals(columnType)) && (patternsMap.get(columnName).length() != 0)){

    stringBuffer.append(TEXT_67);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(patternsMap.get(columnName) );
    stringBuffer.append(TEXT_72);
    
			}else{

    stringBuffer.append(TEXT_73);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_77);
    
			}
		}
	}

if(groupColumns.size() > 0){

    stringBuffer.append(TEXT_78);
    
}

    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    

if(groupColumns.size() > 0){
	//??

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    
	//do out start ...
	conns = null;
	conns = node.getOutgoingSortedConnections();
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 
				for(String columnName : groupColumns){

    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    
				}
				
				for(int i = 0; i < denormalizeColumns.size(); i++){
					String columnName = denormalizeColumns.get(i);
					String columnType = denormalizeColumnsType.get(i);
					if(denormalizeMergeFlags.get(i)){

    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(typesMap.get(columnName) );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(denormalizeDelimiters.get(i) );
    stringBuffer.append(TEXT_99);
    
						if((("java.util.Date").equals(columnType)) && (patternsMap.get(columnName).length() != 0)){

    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(patternsMap.get(columnName) );
    stringBuffer.append(TEXT_103);
    
						}else{

    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    
						}

    stringBuffer.append(TEXT_107);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    
					}else{

    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    
					}
				}
			}
		}
	}
	//do out end ...
	
	
	//do first
	for(String columnName : groupColumns){

    stringBuffer.append(TEXT_124);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_128);
    	}
	for(int i = 0; i < denormalizeColumns.size(); i++){
		String columnName = denormalizeColumns.get(i);
		String columnType = denormalizeColumnsType.get(i);
		if(denormalizeMergeFlags.get(i)){

    stringBuffer.append(TEXT_129);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_133);
    
		}else{
			if((("java.util.Date").equals(columnType)) && (patternsMap.get(columnName).length() != 0)){

    stringBuffer.append(TEXT_134);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(patternsMap.get(columnName) );
    stringBuffer.append(TEXT_139);
    
			}else{

    stringBuffer.append(TEXT_140);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_144);
    
			}
		}
	}

    stringBuffer.append(TEXT_145);
    
}
/////////////////////////////////////////////////

    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(rowCount );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_149);
    
	//do out start ...
	conns = null;
	conns = node.getOutgoingSortedConnections();
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 
				for(String columnName : groupColumns){

    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_155);
    
				}
				
				for(int i = 0; i < denormalizeColumns.size(); i++){
					String columnName = denormalizeColumns.get(i);
					String columnType = denormalizeColumnsType.get(i);
					if(denormalizeMergeFlags.get(i)){

    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(typesMap.get(columnName) );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(denormalizeDelimiters.get(i) );
    stringBuffer.append(TEXT_165);
    
						if((("java.util.Date").equals(columnType)) && (patternsMap.get(columnName).length() != 0)){

    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(patternsMap.get(columnName) );
    stringBuffer.append(TEXT_169);
    
						}else{

    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_172);
    
						}

    stringBuffer.append(TEXT_173);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    
					}else{

    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_189);
    
					}
				}
			}
		}
	}
	//do out end ...


    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_192);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_194);
    
conns = null;
conns = node.getOutgoingSortedConnections();
if (conns!=null) {
	for (int i=0;i<conns.size();i++) {
		IConnection conn = conns.get(i);
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_195);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_198);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_199);
    			}
		}
	}
}

    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_201);
    
	}
	}
}

    stringBuffer.append(TEXT_202);
    return stringBuffer.toString();
  }
}
