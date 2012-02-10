package org.talend.designer.codegen.translators.technical;

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

public class TDenormalizeOutMainJava
{
  protected static String nl;
  public static synchronized TDenormalizeOutMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TDenormalizeOutMainJava result = new TDenormalizeOutMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "if(hash_";
  protected final String TEXT_3 = "_";
  protected final String TEXT_4 = ".containsKey(";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ")){" + NL + "\thash_";
  protected final String TEXT_7 = "_";
  protected final String TEXT_8 = " = hash_";
  protected final String TEXT_9 = "_";
  protected final String TEXT_10 = ".get(";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = ");                    " + NL + "}else{" + NL + "\thash_";
  protected final String TEXT_13 = "_";
  protected final String TEXT_14 = " = new ";
  protected final String TEXT_15 = "java.util.";
  protected final String TEXT_16 = "Hash";
  protected final String TEXT_17 = "Map<";
  protected final String TEXT_18 = ", DenormalizeStruct";
  protected final String TEXT_19 = "java.util.";
  protected final String TEXT_20 = "Hash";
  protected final String TEXT_21 = "Map<";
  protected final String TEXT_22 = ",";
  protected final String TEXT_23 = ">";
  protected final String TEXT_24 = "();" + NL + "\thash_";
  protected final String TEXT_25 = "_";
  protected final String TEXT_26 = ".put(";
  protected final String TEXT_27 = ".";
  protected final String TEXT_28 = ",hash_";
  protected final String TEXT_29 = "_";
  protected final String TEXT_30 = ");" + NL + "}";
  protected final String TEXT_31 = NL + "if(hash_";
  protected final String TEXT_32 = "_";
  protected final String TEXT_33 = ".containsKey(";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = ")){" + NL + "\tdenormalize_result_";
  protected final String TEXT_36 = " = hash_";
  protected final String TEXT_37 = "_";
  protected final String TEXT_38 = ".get(";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = ");";
  protected final String TEXT_41 = NL + "\tif(!denormalize_result_";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = ".contains(";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = ")){" + NL + "\t\tdenormalize_result_";
  protected final String TEXT_46 = ".";
  protected final String TEXT_47 = ".add(";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = ");" + NL + "\t}";
  protected final String TEXT_50 = NL + "\tdenormalize_result_";
  protected final String TEXT_51 = ".";
  protected final String TEXT_52 = ".add(";
  protected final String TEXT_53 = ".";
  protected final String TEXT_54 = ");";
  protected final String TEXT_55 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_56 = ".";
  protected final String TEXT_57 = ".append(";
  protected final String TEXT_58 = ").append(FormatterUtils.format_Date(";
  protected final String TEXT_59 = ".";
  protected final String TEXT_60 = ", ";
  protected final String TEXT_61 = "));" + NL + "\t\t\t";
  protected final String TEXT_62 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_63 = ".";
  protected final String TEXT_64 = ".append(";
  protected final String TEXT_65 = ").append(";
  protected final String TEXT_66 = ".";
  protected final String TEXT_67 = ");" + NL + "\t\t\t";
  protected final String TEXT_68 = "                  " + NL + "}else{" + NL + "\tdenormalize_result_";
  protected final String TEXT_69 = " = new DenormalizeStruct";
  protected final String TEXT_70 = "();";
  protected final String TEXT_71 = NL + "\tif(!denormalize_result_";
  protected final String TEXT_72 = ".";
  protected final String TEXT_73 = ".contains(";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = ")){" + NL + "\t\tdenormalize_result_";
  protected final String TEXT_76 = ".";
  protected final String TEXT_77 = ".add(";
  protected final String TEXT_78 = ".";
  protected final String TEXT_79 = ");" + NL + "\t}";
  protected final String TEXT_80 = NL + "\tdenormalize_result_";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = ".add(";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = ");";
  protected final String TEXT_85 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_86 = ".";
  protected final String TEXT_87 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_88 = ".";
  protected final String TEXT_89 = ", ";
  protected final String TEXT_90 = "));" + NL + "\t\t\t";
  protected final String TEXT_91 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_92 = ".";
  protected final String TEXT_93 = ".append(";
  protected final String TEXT_94 = ".";
  protected final String TEXT_95 = ");" + NL + "\t\t\t";
  protected final String TEXT_96 = NL + "\thash_";
  protected final String TEXT_97 = "_";
  protected final String TEXT_98 = ".put(";
  protected final String TEXT_99 = ".";
  protected final String TEXT_100 = ", denormalize_result_";
  protected final String TEXT_101 = ");" + NL + "}";
  protected final String TEXT_102 = NL + "if(denormalize_result_";
  protected final String TEXT_103 = " == null){" + NL + "\tdenormalize_result_";
  protected final String TEXT_104 = " = new DenormalizeStruct";
  protected final String TEXT_105 = "();";
  protected final String TEXT_106 = NL + "\tif(!denormalize_result_";
  protected final String TEXT_107 = ".";
  protected final String TEXT_108 = ".contains(";
  protected final String TEXT_109 = ".";
  protected final String TEXT_110 = ")){" + NL + "\t\tdenormalize_result_";
  protected final String TEXT_111 = ".";
  protected final String TEXT_112 = ".add(";
  protected final String TEXT_113 = ".";
  protected final String TEXT_114 = ");" + NL + "\t}";
  protected final String TEXT_115 = NL + "\tdenormalize_result_";
  protected final String TEXT_116 = ".";
  protected final String TEXT_117 = ".add(";
  protected final String TEXT_118 = ".";
  protected final String TEXT_119 = ");";
  protected final String TEXT_120 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_121 = ".";
  protected final String TEXT_122 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_123 = ".";
  protected final String TEXT_124 = ", ";
  protected final String TEXT_125 = "));" + NL + "\t\t\t";
  protected final String TEXT_126 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_127 = ".";
  protected final String TEXT_128 = ".append(";
  protected final String TEXT_129 = ".";
  protected final String TEXT_130 = ");" + NL + "\t\t\t";
  protected final String TEXT_131 = NL + "}else{";
  protected final String TEXT_132 = NL + "\tif(!denormalize_result_";
  protected final String TEXT_133 = ".";
  protected final String TEXT_134 = ".contains(";
  protected final String TEXT_135 = ".";
  protected final String TEXT_136 = ")){" + NL + "\t\tdenormalize_result_";
  protected final String TEXT_137 = ".";
  protected final String TEXT_138 = ".add(";
  protected final String TEXT_139 = ".";
  protected final String TEXT_140 = ");" + NL + "\t}";
  protected final String TEXT_141 = NL + "\tdenormalize_result_";
  protected final String TEXT_142 = ".";
  protected final String TEXT_143 = ".add(";
  protected final String TEXT_144 = ".";
  protected final String TEXT_145 = ");";
  protected final String TEXT_146 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_147 = ".";
  protected final String TEXT_148 = ".append(";
  protected final String TEXT_149 = ").append(FormatterUtils.format_Date(";
  protected final String TEXT_150 = ".";
  protected final String TEXT_151 = ", ";
  protected final String TEXT_152 = "));" + NL + "\t\t\t";
  protected final String TEXT_153 = "\t\t" + NL + "\tdenormalize_result_";
  protected final String TEXT_154 = ".";
  protected final String TEXT_155 = ".append(";
  protected final String TEXT_156 = ").append(";
  protected final String TEXT_157 = ".";
  protected final String TEXT_158 = ");" + NL + "\t\t\t";
  protected final String TEXT_159 = NL + "}";
  protected final String TEXT_160 = NL;

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
    			String type = JavaTypesManager.getTypeToGenerate(column.getTalendType(), true);
    			typesMap.put(column.getLabel(), type);
    			String pattern = ((column.getPattern() == null) || (column.getPattern().trim().length() == 0)) ? "" : column.getPattern();
    			patternsMap.put(column.getLabel(), pattern);
    		}
        	Map<String, String> outTypesMap = new HashMap<String, String>();
        	for(IMetadataColumn outColumn : metadata.getListColumns()){
        		String type = JavaTypesManager.getTypeToGenerate(outColumn.getTalendType(), true);
        		outTypesMap.put(outColumn.getLabel(), type);
        	}
			List<Map<String, String>> denormalizes = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__DENORMALIZE_COLUMNS__");
        	List<String> denormalizeColumns = new ArrayList<String>();
        	List<String> denormalizeDelimiters = new ArrayList<String>();
        	List<Boolean> denormalizeMergeFlags = new ArrayList<Boolean>();
       	 	List<String> groupColumns = new ArrayList<String>();
        	for(Map<String, String> denormalize : denormalizes){
        		String columnName = denormalize.get("INPUT_COLUMN");
        		if(denormalizeColumns.contains(columnName)){
        			continue;
        		}
        		denormalizeColumns.add(columnName);
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
        	
        	String tInputColumn =null;
        	String lastInputColumn = null;
			if(groupColumns.size() > 0){
				lastInputColumn = groupColumns.get(groupColumns.size() - 1);
			}
			for (int i=0; i < groupColumns.size(); i++) {
				String inputColumn = groupColumns.get(i);
				String nextInputColumn = null;
				if(i != groupColumns.size() - 1){
					nextInputColumn = groupColumns.get(i+1);
				}
				if(i < groupColumns.size()-1){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(nextInputColumn  );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(nextInputColumn  );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    
//start
//
					for(int j = i+1; j < groupColumns.size(); j++){
						if(j == groupColumns.size() - 1){
//
//end

    stringBuffer.append(TEXT_15);
    if(j == i+1){
    stringBuffer.append(TEXT_16);
    }
    stringBuffer.append(TEXT_17);
    stringBuffer.append(typesMap.get(groupColumns.get(j)) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    
//start
//
						}else{
//
//end

    stringBuffer.append(TEXT_19);
    if(j == i+1){
    stringBuffer.append(TEXT_20);
    }
    stringBuffer.append(TEXT_21);
    stringBuffer.append(typesMap.get(groupColumns.get(j)) );
    stringBuffer.append(TEXT_22);
    
//start
//
						}
					}
					for(int j = i+1; j < groupColumns.size(); j++){
//
//end

    stringBuffer.append(TEXT_23);
    
//start
//
					}
//
//end

    stringBuffer.append(TEXT_24);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(nextInputColumn );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    //start
//
				}else{
					tInputColumn = inputColumn;
//
//end
    stringBuffer.append(TEXT_31);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_40);
    
	//denormalize
	for(int k = 0; k < denormalizeColumns.size(); k++){
		String denormalizeColumn = denormalizeColumns.get(k);
		if(denormalizeMergeFlags.get(k)){

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_49);
    
		}else{
			if("List".equals(outTypesMap.get(denormalizeColumn))){

    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_54);
    
				continue;
    		}
    		
    		if(typesMap.get(denormalizeColumn) !=null && patternsMap.get(denormalizeColumn) != null){
				if((("java.util.Date").equals(typesMap.get(denormalizeColumn))) && (patternsMap.get(denormalizeColumn).length() != 0)){

    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(denormalizeDelimiters.get(k) );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(patternsMap.get(denormalizeColumn) );
    stringBuffer.append(TEXT_61);
    
				}else{

    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(denormalizeDelimiters.get(k) );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_67);
    
				}
			}
		}
	}

    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    
	//denormalize
	for(int k = 0; k < denormalizeColumns.size(); k++){
		String denormalizeColumn = denormalizeColumns.get(k);
		if(denormalizeMergeFlags.get(k)){

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_79);
    
		}else{
			if("List".equals(outTypesMap.get(denormalizeColumn))){

    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_84);
    
				continue;
    		}
    		
			if(typesMap.get(denormalizeColumn) !=null && patternsMap.get(denormalizeColumn) != null){
				if((("java.util.Date").equals(typesMap.get(denormalizeColumn))) && (patternsMap.get(denormalizeColumn).length() != 0)){

    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(patternsMap.get(denormalizeColumn) );
    stringBuffer.append(TEXT_90);
    
				}else{

    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_95);
    
				}
			}
		}
	}

    stringBuffer.append(TEXT_96);
    stringBuffer.append(tInputColumn );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(lastInputColumn );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    //start
//
				}
			}
			if(groupColumns.size() == 0 && denormalizeColumns.size() > 0){

    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    
				//denormalize
				for(int k = 0; k < denormalizeColumns.size(); k++){
					String denormalizeColumn = denormalizeColumns.get(k);
					if(denormalizeMergeFlags.get(k)){

    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_114);
    
					}else{
						if("List".equals(outTypesMap.get(denormalizeColumn))){

    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_119);
    
            				continue;
                		}
						if(typesMap.get(denormalizeColumn) !=null && patternsMap.get(denormalizeColumn) != null){
							if((("java.util.Date").equals(typesMap.get(denormalizeColumn))) && (patternsMap.get(denormalizeColumn).length() != 0)){

    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(patternsMap.get(denormalizeColumn) );
    stringBuffer.append(TEXT_125);
    
							}else{

    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_130);
    
							}
						}
					}
				}

    stringBuffer.append(TEXT_131);
    
				//denormalize
				for(int k = 0; k < denormalizeColumns.size(); k++){
					String denormalizeColumn = denormalizeColumns.get(k);
					if(denormalizeMergeFlags.get(k)){

    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_140);
    
					}else{
						if("List".equals(outTypesMap.get(denormalizeColumn))){

    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_145);
    
							continue;
    					}
						if(typesMap.get(denormalizeColumn) !=null && patternsMap.get(denormalizeColumn) != null){
							if((("java.util.Date").equals(typesMap.get(denormalizeColumn))) && (patternsMap.get(denormalizeColumn).length() != 0)){

    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_147);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(denormalizeDelimiters.get(k) );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(patternsMap.get(denormalizeColumn) );
    stringBuffer.append(TEXT_152);
    
							}else{

    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_155);
    stringBuffer.append(denormalizeDelimiters.get(k) );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(denormalizeColumn );
    stringBuffer.append(TEXT_158);
    
							}
						}
					}
				}

    stringBuffer.append(TEXT_159);
    
			}
		}
	}
}
//
//end
    stringBuffer.append(TEXT_160);
    return stringBuffer.toString();
  }
}
