package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.utils.TalendTextUtils;
import java.util.List;
import java.util.Map;

public class TFileOutputMSDelimitedBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputMSDelimitedBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputMSDelimitedBeginJava result = new TFileOutputMSDelimitedBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "String fileName_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "java.io.File createFile";
  protected final String TEXT_6 = " = new java.io.File(fileName_";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = NL + "//create directory only if not exists" + NL + "java.io.File parentFile_";
  protected final String TEXT_9 = " = createFile";
  protected final String TEXT_10 = ".getParentFile();" + NL + "if(parentFile_";
  protected final String TEXT_11 = " != null && !parentFile_";
  protected final String TEXT_12 = ".exists()) {" + NL + "    parentFile_";
  protected final String TEXT_13 = ".mkdirs();" + NL + "}" + NL;
  protected final String TEXT_14 = NL;
  protected final String TEXT_15 = NL;
  protected final String TEXT_16 = NL + "    \t\t" + NL + "final String OUT_DELIM_ROWSEP_";
  protected final String TEXT_17 = " = ";
  protected final String TEXT_18 = ";" + NL + "    " + NL + "java.io.Writer out";
  protected final String TEXT_19 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "\tnew java.io.FileOutputStream(fileName_";
  protected final String TEXT_20 = ", false),";
  protected final String TEXT_21 = "));";
  protected final String TEXT_22 = NL;
  protected final String TEXT_23 = NL + "String field_";
  protected final String TEXT_24 = " = ";
  protected final String TEXT_25 = ";" + NL + "if(field_";
  protected final String TEXT_26 = ".length() <= 0){" + NL + "\tthrow new IllegalArgumentException(\"Field delimited must be assigned a char.\");" + NL + "}";
  protected final String TEXT_27 = NL + "com.csvreader.CsvWriter out";
  protected final String TEXT_28 = " = new com.csvreader.CsvWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "    new java.io.FileOutputStream(fileName_";
  protected final String TEXT_29 = ", false), ";
  protected final String TEXT_30 = ")),";
  protected final String TEXT_31 = ");" + NL + " ";
  protected final String TEXT_32 = NL + "String row_";
  protected final String TEXT_33 = " = ";
  protected final String TEXT_34 = ";" + NL + "if(row_";
  protected final String TEXT_35 = ".length() > 0){" + NL + "\tout";
  protected final String TEXT_36 = ".setRecordDelimiter(row_";
  protected final String TEXT_37 = ".charAt(0));" + NL + "}else{" + NL + "\tthrow new IllegalArgumentException(\"Row delimited must be assigned a char.\");" + NL + "}" + NL + "" + NL + "String escapeChar_";
  protected final String TEXT_38 = " = ";
  protected final String TEXT_39 = ";" + NL + "if(escapeChar_";
  protected final String TEXT_40 = ".length() <= 0){" + NL + "\tthrow new IllegalArgumentException(\"Escape Char must be assigned a char.\");" + NL + "}" + NL + "" + NL + "String textEnclosure_";
  protected final String TEXT_41 = " = ";
  protected final String TEXT_42 = ";" + NL + "if(textEnclosure_";
  protected final String TEXT_43 = ".length() > 0){" + NL + "\tout";
  protected final String TEXT_44 = ".setTextQualifier(textEnclosure_";
  protected final String TEXT_45 = ".charAt(0));" + NL + "}else{" + NL + "\tthrow new IllegalArgumentException(\"Text Enclosure must be assigned a char.\");" + NL + "}" + NL + "" + NL + "if ((\"\\\\\\\\\").equals(escapeChar_";
  protected final String TEXT_46 = ")) {" + NL + "\tout";
  protected final String TEXT_47 = ".setEscapeMode(com.csvreader.CsvWriter.ESCAPE_MODE_BACKSLASH);" + NL + "} else if (escapeChar_";
  protected final String TEXT_48 = ".equals(textEnclosure_";
  protected final String TEXT_49 = ")) {" + NL + "\tout";
  protected final String TEXT_50 = ".setEscapeMode(com.csvreader.CsvWriter.ESCAPE_MODE_DOUBLED);" + NL + "}" + NL + "out";
  protected final String TEXT_51 = ".setForceQualifier(true);" + NL;
  protected final String TEXT_52 = NL;
  protected final String TEXT_53 = NL + "\tjava.util.List<";
  protected final String TEXT_54 = "Struct> ";
  protected final String TEXT_55 = "List_";
  protected final String TEXT_56 = " = new java.util.ArrayList<";
  protected final String TEXT_57 = "Struct>();";
  protected final String TEXT_58 = NL + "\torg.talend.commons.utils.data.map.MultiLazyValuesMap ";
  protected final String TEXT_59 = "Map_";
  protected final String TEXT_60 = " = new org.talend.commons.utils.data.map.MultiLazyValuesMap(" + NL + "                    new java.util.HashMap()) {" + NL + "" + NL + "                public java.util.Collection instanciateNewCollection() {" + NL + "                    return new org.apache.commons.collections.list.GrowthList(3);" + NL + "                }" + NL + "" + NL + "            };" + NL + "   final ";
  protected final String TEXT_61 = "Struct[] EMPTY_ARRAY_";
  protected final String TEXT_62 = "_";
  protected final String TEXT_63 = " = new ";
  protected final String TEXT_64 = "Struct[0];";
  protected final String TEXT_65 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<String> connList = new java.util.ArrayList<String>();
List< ? extends IConnection> conns = node.getIncomingConnections();
if(conns!=null){
	for(int i=0;i<conns.size();i++){
		IConnection connTemp = conns.get(i);
	    if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	   		connList.add(connTemp.getName());
	    }
	}
}

if (connList.size()>0) {//HSS_____0

    List<Map<String, String>> schemas_o = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SCHEMAS__");
	
	List<Map<String, String>> schemas = new java.util.ArrayList<Map<String, String>>();
	List<String> connections = new java.util.ArrayList<String>();
    Map<String, List<IMetadataColumn>> connectionMapColumnList = new java.util.HashMap<String, List<IMetadataColumn>>();
	
	for(Map<String, String> schema_o : schemas_o){//HSS_____0_____1
		Map<String, String> schema = new java.util.HashMap<String, String>();
		schema.put("SCHEMA", TalendTextUtils.removeQuotes(schema_o.get("SCHEMA")));
		schema.put("PARENT_ROW", TalendTextUtils.removeQuotes(schema_o.get("PARENT_ROW")));
		schema.put("KEY_COLUMN", TalendTextUtils.removeQuotes(schema_o.get("KEY_COLUMN")));
		schema.put("PARENT_KEY_COLUMN", TalendTextUtils.removeQuotes(schema_o.get("PARENT_KEY_COLUMN")));
		schema.put("FIELD_SEPARATOR", TalendTextUtils.removeQuotes(schema_o.get("FIELD_SEPARATOR")));
		if(("").equals(schema.get("PARENT_ROW"))){//put the root schema in the first place on list.
			for(int i=0;i<conns.size();i++){//HSS_____0_____1_____1
	    		IConnection connTemp = conns.get(i);
	    		if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	    			IMetadataTable tempMetadataTable = connTemp.getMetadataTable();
					if(tempMetadataTable.getTableName().equals(schema.get("SCHEMA"))){
						schema.put("CONNECTION", connTemp.getName());
		    			List<IMetadataColumn> listColumns = tempMetadataTable.getListColumns();
		    			connections.add(0, connTemp.getName());
		    			connectionMapColumnList.put(connTemp.getName(), listColumns);
					}
	    		}
	    	}//HSS_____0_____1_____1
			schemas.add(0, schema);
		}else{
			for(int i=0;i<conns.size();i++){//HSS_____0_____1_____1
	    		IConnection connTemp = conns.get(i);
	    		if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	    			IMetadataTable tempMetadataTable = connTemp.getMetadataTable();
					if(tempMetadataTable.getTableName().equals(schema.get("SCHEMA"))){
						schema.put("CONNECTION", connTemp.getName());
		    			List<IMetadataColumn> listColumns = tempMetadataTable.getListColumns();
		    			connections.add(connTemp.getName());
		    			connectionMapColumnList.put(connTemp.getName(), listColumns);
					}
	    		}
	    	}//HSS_____0_____1_____1
			schemas.add(schema);
		}
	}//HSS_____0_____1


String fileName = ElementParameterParser.getValue(node,"__FILENAME__");
String separator = ElementParameterParser.getValue(node,"__ROWSEPARATOR__");
String fieldSeparator = ElementParameterParser.getValue(node,"__FIELDSEPARATOR__");
boolean isUseMultiSeparator = ("true").equals(ElementParameterParser.getValue(node, "__USEMULTISEPARATOR__"));
String escapeChar = ("\"\"\"").equals(ElementParameterParser.getValue(node, "__ESCAPE_CHAR__"))?"\"\\\"\"":ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
String textEnclosure = ("\"\"\"").equals(ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__"))?"\"\\\"\"":ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
	if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    	}
    stringBuffer.append(TEXT_14);
    
if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {	
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_15);
    
            String rowSeparator = ElementParameterParser.getValueWithUIFieldKey(
                node,
                "__ROWSEPARATOR__",
                "ROWSEPARATOR"
            );
//            boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_21);
    
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}else{// the following is the tFileOutputCSV component
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_22);
    
        if(isUseMultiSeparator==false){

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    		}
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(isUseMultiSeparator==false?"field_"+cid+".charAt(0)":"\';\'" );
    stringBuffer.append(TEXT_31);
    
        if(isUseMultiSeparator==false){

    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(separator );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(escapeChar );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
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
    
		}
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
// //////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_52);
    
	for(int i=0; i < connections.size(); i++){//HSS_____0_____3
		String conn = connections.get(i);
		if(i==0){

    stringBuffer.append(TEXT_53);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_57);
     
		}else{

    stringBuffer.append(TEXT_58);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_64);
    
		}
    }//HSS_____0_____3
    
}//HSS_____0

    stringBuffer.append(TEXT_65);
    return stringBuffer.toString();
  }
}
