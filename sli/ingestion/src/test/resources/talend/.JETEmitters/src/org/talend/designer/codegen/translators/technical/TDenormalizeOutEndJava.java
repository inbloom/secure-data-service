package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class TDenormalizeOutEndJava
{
  protected static String nl;
  public static synchronized TDenormalizeOutEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TDenormalizeOutEndJava result = new TDenormalizeOutEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "java.util.List<OnRowsEndStruct";
  protected final String TEXT_2 = "> result_list_";
  protected final String TEXT_3 = " = new java.util.ArrayList<OnRowsEndStruct";
  protected final String TEXT_4 = ">();" + NL + "if (denormalize_result_";
  protected final String TEXT_5 = " != null) {";
  protected final String TEXT_6 = NL + "StringBuilder sb_";
  protected final String TEXT_7 = " = null;" + NL;
  protected final String TEXT_8 = NL + "//generate result begin";
  protected final String TEXT_9 = NL + "java.util.Iterator<";
  protected final String TEXT_10 = "> ";
  protected final String TEXT_11 = "_iterator_";
  protected final String TEXT_12 = " = hash_";
  protected final String TEXT_13 = "_";
  protected final String TEXT_14 = ".keySet().iterator();" + NL + "" + NL + "while(";
  protected final String TEXT_15 = "_iterator_";
  protected final String TEXT_16 = ".hasNext()){" + NL + "" + NL + "\t";
  protected final String TEXT_17 = " key_";
  protected final String TEXT_18 = "_";
  protected final String TEXT_19 = " = ";
  protected final String TEXT_20 = "_iterator_";
  protected final String TEXT_21 = ".next();" + NL + "\t";
  protected final String TEXT_22 = NL + "\tdenormalize_result_";
  protected final String TEXT_23 = " = hash_";
  protected final String TEXT_24 = "_";
  protected final String TEXT_25 = ".get(key_";
  protected final String TEXT_26 = "_";
  protected final String TEXT_27 = ");" + NL;
  protected final String TEXT_28 = NL + "\thash_";
  protected final String TEXT_29 = "_";
  protected final String TEXT_30 = " = hash_";
  protected final String TEXT_31 = "_";
  protected final String TEXT_32 = ".get(key_";
  protected final String TEXT_33 = "_";
  protected final String TEXT_34 = ");" + NL;
  protected final String TEXT_35 = NL + "\tOnRowsEndStruct";
  protected final String TEXT_36 = " denormalize_row_";
  protected final String TEXT_37 = " = new OnRowsEndStruct";
  protected final String TEXT_38 = "();" + NL + "                ";
  protected final String TEXT_39 = NL + "\tdenormalize_row_";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = " = key_";
  protected final String TEXT_42 = "_";
  protected final String TEXT_43 = ";";
  protected final String TEXT_44 = NL + "\tdenormalize_row_";
  protected final String TEXT_45 = ".";
  protected final String TEXT_46 = " = denormalize_result_";
  protected final String TEXT_47 = ".";
  protected final String TEXT_48 = ";";
  protected final String TEXT_49 = NL + "\tfor(";
  protected final String TEXT_50 = " temp_";
  protected final String TEXT_51 = " : denormalize_result_";
  protected final String TEXT_52 = ".";
  protected final String TEXT_53 = "){" + NL + "\t" + NL + "\t\tif(sb_";
  protected final String TEXT_54 = " == null){" + NL + "\t\t" + NL + "\t\t\tsb_";
  protected final String TEXT_55 = " = new StringBuilder();";
  protected final String TEXT_56 = "\t\t" + NL + "\t\t\tsb_";
  protected final String TEXT_57 = ".append(FormatterUtils.format_Date(temp_";
  protected final String TEXT_58 = ", ";
  protected final String TEXT_59 = "));" + NL + "\t\t\t";
  protected final String TEXT_60 = "\t\t" + NL + "\t\t\tsb_";
  protected final String TEXT_61 = ".append(temp_";
  protected final String TEXT_62 = ");" + NL + "\t\t\t";
  protected final String TEXT_63 = NL + "\t\t\t" + NL + "\t\t}else{";
  protected final String TEXT_64 = "\t\t" + NL + "\t\t\tsb_";
  protected final String TEXT_65 = ".append(";
  protected final String TEXT_66 = ").append(FormatterUtils.format_Date(temp_";
  protected final String TEXT_67 = ", ";
  protected final String TEXT_68 = "));" + NL + "\t\t\t";
  protected final String TEXT_69 = "\t\t" + NL + "\t\t\tsb_";
  protected final String TEXT_70 = ".append(";
  protected final String TEXT_71 = ").append(temp_";
  protected final String TEXT_72 = ");" + NL + "\t\t\t";
  protected final String TEXT_73 = NL + "\t\t}" + NL + "\t\t" + NL + "\t}" + NL + "\t" + NL + "\tdenormalize_row_";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = " = sb_";
  protected final String TEXT_76 = ".toString();" + NL + "\t" + NL + "\tsb_";
  protected final String TEXT_77 = " = null;" + NL + "\t";
  protected final String TEXT_78 = NL + "\tdenormalize_row_";
  protected final String TEXT_79 = ".";
  protected final String TEXT_80 = " = denormalize_result_";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = ".toString();" + NL + "\t";
  protected final String TEXT_83 = NL + "\t//in the deepest end" + NL + "\t" + NL + "\tresult_list_";
  protected final String TEXT_84 = ".add(denormalize_row_";
  protected final String TEXT_85 = ");" + NL;
  protected final String TEXT_86 = NL + "}" + NL;
  protected final String TEXT_87 = NL + "}" + NL + "//generate result end" + NL + "globalMap.put(\"";
  protected final String TEXT_88 = "\", result_list_";
  protected final String TEXT_89 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_90 = "_NB_LINE\", result_list_";
  protected final String TEXT_91 = ".size()); ";
  protected final String TEXT_92 = NL + NL + "        " + NL;
  protected final String TEXT_93 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName(); 
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {

    IMetadataTable metadata = metadatas.get(0);
    List< ? extends IConnection> inConns = node.getIncomingConnections();
    IMetadataTable inMetadata = null;
    if(inConns != null){ 
    	for (IConnection conn : inConns) { 
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 
				inMetadata = conn.getMetadataTable();
    			break;
			}
		}
	}
    if (metadata != null && inMetadata != null) { 
    	List<IMetadataColumn> inColumns = inMetadata.getListColumns();
    	Map<String, String> typesMap = new HashMap<String, String>();
    	Map<String, String> patternsMap = new HashMap<String, String>();
    	for(IMetadataColumn column : inColumns){
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
    	String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
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
        for(IMetadataColumn column : inColumns){
        	String columnName = column.getLabel();
        	if(denormalizeColumns.contains(columnName)){
        		continue;
        	}
        	groupColumns.add(column.getLabel());
        }
		
    stringBuffer.append(TEXT_1);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
	for(boolean b : denormalizeMergeFlags){
		if(b){

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
			break;
		}
	}

    stringBuffer.append(TEXT_8);
    		//start
        //
		if(denormalizeColumns.size() > 0 || groupColumns.size() > 0){
			for(int i = 0; i < groupColumns.size(); i++){
				String inputColumn = groupColumns.get(i);
				String nextInputColumn = null;
				if(i != groupColumns.size() - 1){
					nextInputColumn = groupColumns.get(i+1);
				}
				//gen loops begin begin
                
    stringBuffer.append(TEXT_9);
    stringBuffer.append(typesMap.get(groupColumns.get(i)) );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(inputColumn);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(inputColumn);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(typesMap.get(groupColumns.get(i)) );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(inputColumn);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
    			if(i == groupColumns.size() - 1){
    					    
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(inputColumn);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    
    			}else{
    				    
    stringBuffer.append(TEXT_28);
    stringBuffer.append(nextInputColumn );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    
    			}
    			//gen loops begin end
    		}

    stringBuffer.append(TEXT_35);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_38);
    
			for(int i = 0; i < groupColumns.size(); i++){
				String inputColumn = groupColumns.get(i);

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    
			}
			
			for(int i = 0; i < denormalizeColumns.size(); i++){
				String inputColumn = denormalizeColumns.get(i);
				String delimiter = denormalizeDelimiters.get(i);
				if("List".equals(outTypesMap.get(inputColumn))){

    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_48);
    
					continue;
				}
				if(denormalizeMergeFlags.get(i)){

    stringBuffer.append(TEXT_49);
    stringBuffer.append(typesMap.get(inputColumn) );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    
					if((("java.util.Date").equals(typesMap.get(inputColumn))) && (patternsMap.get(inputColumn).length() != 0)){

    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(patternsMap.get(inputColumn) );
    stringBuffer.append(TEXT_59);
    
					}else{

    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    
					}

    stringBuffer.append(TEXT_63);
    
					if((("java.util.Date").equals(typesMap.get(inputColumn))) && (patternsMap.get(inputColumn).length() != 0)){

    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(delimiter );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(patternsMap.get(inputColumn) );
    stringBuffer.append(TEXT_68);
    
					}else{

    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(delimiter );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    
					}

    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    
				}else{

    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(inputColumn );
    stringBuffer.append(TEXT_82);
    
				}
			}

    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    
			//gen loop end begin
            for(int i = 0; i < groupColumns.size(); i++){

    stringBuffer.append(TEXT_86);
    
			}
			//gen loop end end
		}//i f(denormalizeColumns.size() > 0|| groupColumns.size() > 0) end
		
    stringBuffer.append(TEXT_87);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    
	}
}

    stringBuffer.append(TEXT_92);
    stringBuffer.append(TEXT_93);
    return stringBuffer.toString();
  }
}
