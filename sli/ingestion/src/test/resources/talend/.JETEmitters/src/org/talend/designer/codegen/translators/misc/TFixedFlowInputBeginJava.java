package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TFixedFlowInputBeginJava
{
  protected static String nl;
  public static synchronized TFixedFlowInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFixedFlowInputBeginJava result = new TFixedFlowInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t    for (int i_";
  protected final String TEXT_3 = " = 0 ; i_";
  protected final String TEXT_4 = " < ";
  protected final String TEXT_5 = " ; i_";
  protected final String TEXT_6 = "++) {" + NL + "\t        ";
  protected final String TEXT_7 = "        \t            \t" + NL + "        \t            \t\t";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ";        \t            \t" + NL + "        \t            \t";
  protected final String TEXT_11 = "        \t            \t" + NL + "        \t            \t\t";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = " = ";
  protected final String TEXT_14 = ";" + NL + "        \t            \t";
  protected final String TEXT_15 = NL + "\tint nb_line_";
  protected final String TEXT_16 = " = 0;";
  protected final String TEXT_17 = NL + "\tList<";
  protected final String TEXT_18 = "Struct> cacheList_";
  protected final String TEXT_19 = " = new java.util.ArrayList<";
  protected final String TEXT_20 = "Struct>();";
  protected final String TEXT_21 = NL + "\t";
  protected final String TEXT_22 = " = new ";
  protected final String TEXT_23 = "Struct();";
  protected final String TEXT_24 = "        \t            \t";
  protected final String TEXT_25 = NL + "    ";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = " = ";
  protected final String TEXT_28 = ";        \t            \t";
  protected final String TEXT_29 = "        \t            \t" + NL + "\t";
  protected final String TEXT_30 = ".";
  protected final String TEXT_31 = " = ";
  protected final String TEXT_32 = ";";
  protected final String TEXT_33 = NL + "\tcacheList_";
  protected final String TEXT_34 = ".add(";
  protected final String TEXT_35 = ");";
  protected final String TEXT_36 = NL + "\tfor (int i_";
  protected final String TEXT_37 = " = 0 ; i_";
  protected final String TEXT_38 = " < ";
  protected final String TEXT_39 = " ; i_";
  protected final String TEXT_40 = "++) {" + NL + "\t\tfor(";
  protected final String TEXT_41 = "Struct tmpRow_";
  protected final String TEXT_42 = " : cacheList_";
  protected final String TEXT_43 = "){" + NL + "\t" + NL + "\t\t\tnb_line_";
  protected final String TEXT_44 = " ++;\t\t" + NL + "\t\t\t";
  protected final String TEXT_45 = " = tmpRow_";
  protected final String TEXT_46 = ";";
  protected final String TEXT_47 = NL + NL + "StringBuilder result_";
  protected final String TEXT_48 = " = new StringBuilder();";
  protected final String TEXT_49 = NL + "\tresult_";
  protected final String TEXT_50 = ".append(\"";
  protected final String TEXT_51 = "\");";
  protected final String TEXT_52 = NL + "String originalFileContent_";
  protected final String TEXT_53 = " = \"\";" + NL + "try {" + NL + "    originalFileContent_";
  protected final String TEXT_54 = " = new String((new sun.misc.BASE64Decoder()).decodeBuffer(result_";
  protected final String TEXT_55 = ".toString()), utf8Charset);" + NL + "} catch (Exception e) {" + NL + "    e.printStackTrace();" + NL + "}" + NL + "" + NL + "int nb_line_";
  protected final String TEXT_56 = " = 0;" + NL + "" + NL + "for (int i_";
  protected final String TEXT_57 = " = 0 ; i_";
  protected final String TEXT_58 = " < ";
  protected final String TEXT_59 = " ; i_";
  protected final String TEXT_60 = "++) {\t" + NL + "" + NL + "java.io.InputStream ins_";
  protected final String TEXT_61 = " = new java.io.ByteArrayInputStream(originalFileContent_";
  protected final String TEXT_62 = ".getBytes(utf8Charset));        " + NL + "org.talend.fileprocess.FileInputDelimited fid_";
  protected final String TEXT_63 = " = new org.talend.fileprocess.FileInputDelimited(ins_";
  protected final String TEXT_64 = ", utf8Charset,";
  protected final String TEXT_65 = ",";
  protected final String TEXT_66 = ",true, 0, 0, -1, -1, false);" + NL + "" + NL + "while (fid_";
  protected final String TEXT_67 = ".nextRecord()) {" + NL + "\tnb_line_";
  protected final String TEXT_68 = "++;";
  protected final String TEXT_69 = NL + "\t\t\t";
  protected final String TEXT_70 = " = new ";
  protected final String TEXT_71 = "Struct();";
  protected final String TEXT_72 = NL + NL + "\t\t\t\tif(";
  protected final String TEXT_73 = " < fid_";
  protected final String TEXT_74 = ".getColumnsCountOfCurrentRow()){" + NL + "\t\t\t\t\tString colContent = fid_";
  protected final String TEXT_75 = ".get(";
  protected final String TEXT_76 = ");\t\t\t\t";
  protected final String TEXT_77 = NL + "                    \t\t";
  protected final String TEXT_78 = ".";
  protected final String TEXT_79 = " = (colContent == null || colContent.length() == 0) ? ";
  protected final String TEXT_80 = ": colContent;";
  protected final String TEXT_81 = NL + "                    \t\t";
  protected final String TEXT_82 = ".";
  protected final String TEXT_83 = " = (colContent == null || colContent.length() == 0) ? ParserUtils.parseTo_Date(";
  protected final String TEXT_84 = "(String)";
  protected final String TEXT_85 = " ";
  protected final String TEXT_86 = ", ";
  protected final String TEXT_87 = ") : ParserUtils.parseTo_Date(colContent, ";
  protected final String TEXT_88 = ");";
  protected final String TEXT_89 = NL + "                    \t\t";
  protected final String TEXT_90 = ".";
  protected final String TEXT_91 = " = (colContent == null || colContent.length() == 0) ? ";
  protected final String TEXT_92 = " : colContent.getBytes();";
  protected final String TEXT_93 = NL + "                    \t\t";
  protected final String TEXT_94 = ".";
  protected final String TEXT_95 = " = (colContent == null || colContent.trim().length() == 0) ? ";
  protected final String TEXT_96 = " : ParserUtils.parseTo_";
  protected final String TEXT_97 = "(colContent);";
  protected final String TEXT_98 = "\t\t\t" + NL + "\t\t\t\t} else { " + NL + "\t\t\t\t";
  protected final String TEXT_99 = "\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_100 = ".";
  protected final String TEXT_101 = " = ";
  protected final String TEXT_102 = ";" + NL + "\t\t\t\t";
  protected final String TEXT_103 = NL + "\t\t\t\t\t";
  protected final String TEXT_104 = ".";
  protected final String TEXT_105 = " = ParserUtils.parseTo_Date(";
  protected final String TEXT_106 = "(String)";
  protected final String TEXT_107 = ", ";
  protected final String TEXT_108 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_109 = NL + "\t\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String nbRows = ElementParameterParser.getValue(node, "__NB_ROWS__");

	List<Map<String, String>> tableValues =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__VALUES__"
    );
    
    boolean use_singleMode = ("true").equals(ElementParameterParser.getValue(node, "__USE_SINGLEMODE__"));
    boolean use_inTable = ("true").equals(ElementParameterParser.getValue(node, "__USE_INTABLE__"));
    boolean use_inlineContent = ("true").equals(ElementParameterParser.getValue(node, "__USE_INLINECONTENT__"));
    
	String fieldSeparator = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
	String rowSeparator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
    
	List<Map<String, String>> inTableValues =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__INTABLE__"
    );
    
	List<IMetadataTable> metadatas = node.getMetadataList();
	IMetadataTable metadata = null;
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		metadata = metadatas.get(0);    
    }
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	
//************** the original part that get only one value for each column ********************
	if(use_singleMode){
		if(tableValues != null && tableValues.size() > 0 && conns != null && conns.size() > 0) {
	    
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(nbRows);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
    	    for(IConnection conn : conns) {
    	        if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {    	        	
    	            for(Map<String, String> tableValue : tableValues) {
    	            	String lable = tableValue.get("SCHEMA_COLUMN");
        	            String value = tableValue.get("VALUE");
        	            if(value == null || value.length() == 0){ //use the default value
        	            	IMetadataColumn column = metadata.getColumn(lable);
    	            		String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
    	            		String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
    	            		
    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(lable );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_10);
    
        	            }else{
        	            	
    stringBuffer.append(TEXT_11);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(lable );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(value );
    stringBuffer.append(TEXT_14);
    
        	            }
    	            }
    	        }
    	    }
		}
//***************************the original part end ***************************************
	}else if(use_inTable){
//******************the new part ---get several values for each column*********************
		if(inTableValues != null && inTableValues.size() > 0 && conns != null && conns.size() > 0) {

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
            String firstConnName = "";	
            for(IConnection conn : conns) {
                if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
             		firstConnName = conn.getName();
             		break;
                }
            }
    	    if(firstConnName!=null && !firstConnName.equals("")){//XXX

    stringBuffer.append(TEXT_17);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_20);
        	        	
    	            for(Map<String, String> tableValue : inTableValues) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_23);
    
    	            	for(IMetadataColumn column: metadata.getListColumns()){
    	            		String label = column.getLabel();
	    	            	String value = tableValue.get(label);
	        	            if(value == null || value.length() == 0){ //use the default value
	    	            		String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
	    	            		String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());

    stringBuffer.append(TEXT_24);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_28);
    
        	            	}else{

    stringBuffer.append(TEXT_29);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(value );
    stringBuffer.append(TEXT_32);
    
        	            	}
        	            }

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_35);
    
    	            }

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(nbRows);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    
    	    }
		}
//********************the new part end*****************************************************
	}else if(use_inlineContent){
	
	String fileContent = ElementParameterParser.getValue(node,"__INLINECONTENT__");
	
	String lineSeparator = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));
	//lineSeparator.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n")
	
	String fileContentEnCodeStr = "";
	try {
		fileContentEnCodeStr = (new sun.misc.BASE64Encoder()).encode(fileContent.getBytes("UTF-8"));
	} catch (java.io.UnsupportedEncodingException e) {
		e.printStackTrace();
	}	

    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    
String[] arrayStr = fileContentEnCodeStr.split(lineSeparator);
for(String item : arrayStr){

    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(item );
    stringBuffer.append(TEXT_51);
    }
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(nbRows);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    
    String firstConnName = "";	
    for(IConnection conn : conns) {
        if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
     		firstConnName = conn.getName();
     		break;
        }
    }
    
    if(!firstConnName.equals("")){//XXX

    stringBuffer.append(TEXT_69);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_71);
    
        	List<IMetadataColumn> columns = metadata.getListColumns();
        	int sizeListColumns = columns.size();

			for (int i=0;i<sizeListColumns;i++) {//AAA
					IMetadataColumn column=columns.get(i);
					String label = column.getLabel();
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();					
					String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());

    stringBuffer.append(TEXT_72);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_76);
    if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
    stringBuffer.append(TEXT_77);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_80);
    }else if(javaType == JavaTypesManager.DATE){
    stringBuffer.append(TEXT_81);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_83);
     if((defaultValue==null)||"".equals(defaultValue) || "null".equals(defaultValue)){
    stringBuffer.append(TEXT_84);
    }
    stringBuffer.append(TEXT_85);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_86);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_87);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_88);
    }else if(javaType == JavaTypesManager.BYTE_ARRAY){
    stringBuffer.append(TEXT_89);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_92);
    }else{
    stringBuffer.append(TEXT_93);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_96);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_97);
    }
    stringBuffer.append(TEXT_98);
    if(javaType != JavaTypesManager.DATE){
    stringBuffer.append(TEXT_99);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_102);
    }else {
    stringBuffer.append(TEXT_103);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_105);
     if(defaultValue==null||"".equals(defaultValue)|| "null".equals(defaultValue)){
    stringBuffer.append(TEXT_106);
    }
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_107);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_108);
    }
    stringBuffer.append(TEXT_109);
    
				}//AAA
			
    
	}//XXX
//********************the use_inTable part end*****************************************************
	}

    return stringBuffer.toString();
  }
}
