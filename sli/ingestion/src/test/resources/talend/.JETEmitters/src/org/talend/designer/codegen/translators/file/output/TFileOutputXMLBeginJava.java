package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.utils.TalendQuoteUtils;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TFileOutputXMLBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputXMLBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputXMLBeginJava result = new TFileOutputXMLBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String originalFileName_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "java.io.File originalFile_";
  protected final String TEXT_5 = " = new java.io.File(originalFileName_";
  protected final String TEXT_6 = "); " + NL + "" + NL + "String fileName_";
  protected final String TEXT_7 = " = originalFileName_";
  protected final String TEXT_8 = ";" + NL + "java.io.File file_";
  protected final String TEXT_9 = " = new java.io.File(fileName_";
  protected final String TEXT_10 = "); " + NL + "if(!file_";
  protected final String TEXT_11 = ".isAbsolute()) {" + NL + "\tfile_";
  protected final String TEXT_12 = " = file_";
  protected final String TEXT_13 = ".getCanonicalFile();" + NL + "}" + NL;
  protected final String TEXT_14 = NL + "//create directory only if not exists" + NL + "file_";
  protected final String TEXT_15 = ".getParentFile().mkdirs();";
  protected final String TEXT_16 = NL + NL + "String[] headers_";
  protected final String TEXT_17 = " = new String[";
  protected final String TEXT_18 = "];" + NL + "    " + NL + "headers_";
  protected final String TEXT_19 = "[0] = \"<?xml version=\\\"1.0\\\" encoding=\\\"\"+";
  protected final String TEXT_20 = "+\"\\\"?>\";  " + NL;
  protected final String TEXT_21 = "String[] footers_";
  protected final String TEXT_22 = " = new String[";
  protected final String TEXT_23 = "];" + NL;
  protected final String TEXT_24 = "headers_";
  protected final String TEXT_25 = "[";
  protected final String TEXT_26 = "] = \"<\"+";
  protected final String TEXT_27 = "+\">\";" + NL + "" + NL + "footers_";
  protected final String TEXT_28 = "[";
  protected final String TEXT_29 = "] = \"</\"+";
  protected final String TEXT_30 = "+\">\";" + NL;
  protected final String TEXT_31 = "String[][] groupby_";
  protected final String TEXT_32 = " = new String[";
  protected final String TEXT_33 = "][2];" + NL + "" + NL + "int groupby_new_";
  protected final String TEXT_34 = " = 0;" + NL + "" + NL + "boolean start_";
  protected final String TEXT_35 = " = false;" + NL;
  protected final String TEXT_36 = "groupby_";
  protected final String TEXT_37 = "[";
  protected final String TEXT_38 = "][0] = \"\";" + NL + "" + NL + "groupby_";
  protected final String TEXT_39 = "[";
  protected final String TEXT_40 = "][1] = \"</\"+";
  protected final String TEXT_41 = "+\">\";" + NL;
  protected final String TEXT_42 = NL + NL + "int nb_line_";
  protected final String TEXT_43 = " = 0;" + NL;
  protected final String TEXT_44 = NL + "int currentRowCount_";
  protected final String TEXT_45 = " = 0;" + NL + "int currentFileCount_";
  protected final String TEXT_46 = " = 0;" + NL + "" + NL + "String canonicalPath_";
  protected final String TEXT_47 = " = originalFile_";
  protected final String TEXT_48 = ".getCanonicalPath();" + NL + "StringBuffer sb_";
  protected final String TEXT_49 = " = new StringBuffer(canonicalPath_";
  protected final String TEXT_50 = ");" + NL + "int lastIndexOf_";
  protected final String TEXT_51 = " = canonicalPath_";
  protected final String TEXT_52 = ".lastIndexOf('.');" + NL + "int position_";
  protected final String TEXT_53 = " = lastIndexOf_";
  protected final String TEXT_54 = " > -1 ? lastIndexOf_";
  protected final String TEXT_55 = " : canonicalPath_";
  protected final String TEXT_56 = ".length();" + NL + "sb_";
  protected final String TEXT_57 = ".insert(position_";
  protected final String TEXT_58 = ", currentFileCount_";
  protected final String TEXT_59 = ");" + NL + "" + NL + "fileName_";
  protected final String TEXT_60 = " = sb_";
  protected final String TEXT_61 = ".toString();" + NL + "file_";
  protected final String TEXT_62 = " = new java.io.File(fileName_";
  protected final String TEXT_63 = ");";
  protected final String TEXT_64 = NL + NL + "java.io.BufferedWriter out_";
  protected final String TEXT_65 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(file_";
  protected final String TEXT_66 = "), ";
  protected final String TEXT_67 = "));" + NL;
  protected final String TEXT_68 = NL + "\tout_";
  protected final String TEXT_69 = ".write(headers_";
  protected final String TEXT_70 = "[";
  protected final String TEXT_71 = "]);" + NL + "\tout_";
  protected final String TEXT_72 = ".newLine();\t";
  protected final String TEXT_73 = NL;

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
    String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
    List<Map<String, String>> rootTags = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ROOT_TAGS__");
    if (rootTags.size()==0) {
    	Map<String, String> defaultRootNode = new HashMap<String, String>();
    	defaultRootNode.put("TAG","\"root\"");
    	rootTags.add(defaultRootNode);
    }
    int headers = rootTags.size() + 1;
    boolean split = ("true").equals(ElementParameterParser.getValue(node, "__SPLIT__"));
    String fileName = ElementParameterParser.getValue(node, "__FILENAME__");
    
    //**
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
    		}else{
    			attribute[ia][1] = map.get("LABEL");
    		}
    		attribute[ia++][0] = col;
    	}else{
    		if(("true").equals(map.get("SCHEMA_COLUMN_NAME"))){
    			tag[it][1] = col;
    		}else{
    			tag[it][1] = map.get("LABEL");
    		}
    	    tag[it++][0] = col;
    	}
    }
    //**

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(fileName );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    }
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(headers );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append( encoding );
    stringBuffer.append(TEXT_20);
    
if(rootTags.size() > 0){

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(rootTags.size());
    stringBuffer.append(TEXT_23);
    
	for (int i=0; i<rootTags.size(); i++){
		Map<String, String> rootTag = rootTags.get(i);

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(i+1 );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(rootTag.get("TAG") );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(rootTag.get("TAG") );
    stringBuffer.append(TEXT_30);
    
	}
}

    
if(groupby.length>0){

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(groupby.length );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    
	for(int i = 0; i < groupby.length; i++){

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(groupby[i][2] );
    stringBuffer.append(TEXT_41);
    
	}
}

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    if (split) {
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
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
    }
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_67);
    	for(int i = 0; i < headers;i++){
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    

}

    
    }
}


    stringBuffer.append(TEXT_73);
    return stringBuffer.toString();
  }
}
