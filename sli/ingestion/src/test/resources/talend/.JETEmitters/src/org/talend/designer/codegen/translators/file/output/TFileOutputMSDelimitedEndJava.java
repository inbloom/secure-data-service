package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TFileOutputMSDelimitedEndJava
{
  protected static String nl;
  public static synchronized TFileOutputMSDelimitedEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputMSDelimitedEndJava result = new TFileOutputMSDelimitedEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String tempStringM";
  protected final String TEXT_3 = " = null;" + NL + "            " + NL + "int tempLengthM";
  protected final String TEXT_4 = " = 0;" + NL + "" + NL + "StringBuilder sb_";
  protected final String TEXT_5 = " = new StringBuilder();";
  protected final String TEXT_6 = NL;
  protected final String TEXT_7 = NL + "out";
  protected final String TEXT_8 = ".close();" + NL + "globalMap.put(\"";
  protected final String TEXT_9 = "_NB_LINE\",nb_line_";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "if(nb_line_";
  protected final String TEXT_12 = " == 0){" + NL + "\tnew java.io.File(fileName_";
  protected final String TEXT_13 = ").delete();" + NL + "}";
  protected final String TEXT_14 = NL;
  protected final String TEXT_15 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

final String cid = node.getUniqueName();

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

final String separator = ElementParameterParser.getValue(node,"__ROWSEPARATOR__");
final String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
final boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
final String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
final String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);   

final String fieldSeparator = ElementParameterParser.getValue(node,"__FIELDSEPARATOR__");
final String csvOption = ElementParameterParser.getValue(node, "__CSV_OPTION__");
final String escapeChar = ("\"\"\"").equals(ElementParameterParser.getValue(node, "__ESCAPE_CHAR__"))?"\"\\\"\"":ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
final String textEnclosure = ("\"\"\"").equals(ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__"))?"\"\\\"\"":ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
final boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));
final boolean isUseMultiSeparator = ("true").equals(ElementParameterParser.getValue(node, "__USEMULTISEPARATOR__"));

class Gen{//HSS_____inner class
	List<Map<String, String>> schemas;
	List<String> connections;
	Map<String, List<IMetadataColumn>> connectionMapColumnList;
	Set<String> connectionSet;
	StringBuilder sb;
	
	Gen(List<Map<String, String>> schemas, List<String> connections, Map<String, List<IMetadataColumn>> connectionMapColumnList){
		this.schemas = schemas;
		this.connections = connections;
		this.connectionMapColumnList = connectionMapColumnList;
		
		sb = new StringBuilder();
		connectionSet = new java.util.HashSet<String>();
	}
	
	private void genError(String msg){
		sb = new StringBuilder();
		sb.append("if(true){\nthrow new RuntimeException(\"").append(msg).append("\");\n}\n");
		connectionSet.addAll(connections);
	}
	
	void generate(int index){
		String connection = connections.get(index);
		if(connectionSet.contains(connection)){
			return;
		}else{
			connectionSet.add(connection);
		}
		Map<String, String> schema = schemas.get(index);
		String parent = schema.get("PARENT_ROW");
		boolean hasParent = !(("").equals(parent) || parent == null);
		if(hasParent){//assign key....
			sb.append("String key_").append(connection).append("_").append(parent).append(" = String.valueOf(").append(parent).append("_e.").append(schema.get("PARENT_KEY_COLUMN")).append(");\n");
			//sb.append("Object v_").append(connection).append("_").append(cid).append(" = ").append(connection).append("Map_").append(cid).append(".get(key_").append(connection).append("_").append(schema.get("PARENT_ROW")).append(");\n");
			sb.append("Object v_").append(connection).append("_").append(cid).append(" = ").append(connection).append("Map_").append(cid).append(".get(key_").append(connection).append("_").append(schema.get("PARENT_ROW")).append(");\n");
            sb.append(connection).append("Struct[] ra_").append(connection).append("_").append(cid).append(" = EMPTY_ARRAY_").append(connection).append("_").append(cid).append(";\n");
            sb.append("if (v_").append(connection).append("_").append(cid).append(" != null) {\n");
			sb.append("if (v_").append(connection).append("_").append(cid).append(" instanceof List) {\n");
			sb.append("ra_").append(connection).append("_").append(cid).append(" = ((List<").append(connection).append("Struct>) v_").append(connection).append("_").append(cid).append(").toArray(ra_").append(connection).append("_").append(cid).append(");\n");
            sb.append("} else {\n");
			sb.append("ra_").append(connection).append("_").append(cid).append(" = new ").append(connection).append("Struct[1];\n");
			sb.append("ra_").append(connection).append("_").append(cid).append("[0] = (").append(connection).append("Struct) v_").append(connection).append("_").append(cid).append(";\n");
            sb.append("}\n");
            sb.append("}\n");

            sb.append("for (int ").append(connection).append("_index_").append(cid).append(" = 0; ").append(connection).append("_index_").append(cid).append(" < ra_").append(connection).append("_").append(cid).append(".length; ").append(connection).append("_index_").append(cid).append("++) {\n");
			sb.append(connection).append("Struct ").append(connection).append("_e = ra_").append(connection).append("_").append(cid).append("[").append(connection).append("_index_").append(cid).append("];\n");
		}else{
			sb.append("for (int ").append(connection).append("_index_").append(cid).append("= 0; ").append(connection).append("_index_").append(cid).append(" < ").append(connection).append("List_").append(cid).append(".size(); ").append(connection).append("_index_").append(cid).append("++) {\n");
			sb.append("").append(connection).append("Struct ").append(connection).append("_e = ").append(connection).append("List_").append(cid).append(".get(").append(connection).append("_index_").append(cid).append(");\n");
		}
		
		List<IMetadataColumn> columns = connectionMapColumnList.get(connection);
		int sizeColumns = columns.size();
		
		if("true".equals(csvOption)){ //csv
			if(isUseMultiSeparator){
				sb.append("String field_" + connection + " = " + schema.get("FIELD_SEPARATOR") + ";\n");
				sb.append("if(field_" + connection + ".length() > 0){\n");
				sb.append("out" + cid + ".setDelimiter(field_" + connection + ".charAt(0));\n");
				sb.append("}else{throw new IllegalArgumentException(\"Field delimited must be assigned a char.\");};\n");
				
//				sb.append("String row_" + connection + " = " + separator + ";\n");
//				sb.append("if(row_" + connection + ".length() > 0){\n");
//				sb.append("out" + cid + ".setRecordDelimiter(row_" + connection + ".charAt(0));\n");
//				sb.append("}else{throw new IllegalArgumentException(\"Row delimited must be assigned a char.\");};\n");
				
				sb.append("String escapeChar_" + connection + " = " + escapeChar + ";\n");
				sb.append("if(escapeChar_" + connection + ".length() > 0){\n");
				sb.append("}else{throw new IllegalArgumentException(\"Escape Char must be assigned a char.\");};\n");
				
				sb.append("String textEnclosure_" + connection + " = " + textEnclosure + ";\n");
				sb.append("if(textEnclosure_" + connection + ".length() > 0){\n");
				sb.append("out" + cid + ".setTextQualifier(textEnclosure_" + connection + ".charAt(0));\n");
				sb.append("}else{throw new IllegalArgumentException(\"Text Enclosure must be assigned a char.\");};\n");
				
				sb.append("if ((\"\\\\\\\\\").equals(escapeChar_"+connection+")) {");
				sb.append("out" + cid + ".setEscapeMode(com.csvreader.CsvWriter.ESCAPE_MODE_BACKSLASH);\n");
				sb.append("} else if (escapeChar_"+connection+".equals(textEnclosure_"+connection+")) {\n");
				sb.append("out"+cid+".setEscapeMode(com.csvreader.CsvWriter.ESCAPE_MODE_DOUBLED);\n");
				sb.append("}\n");
				
				sb.append("out"+cid+".setForceQualifier(true);\n");
			}
			
			sb.append("String[] rowField_" + connection + " = new String[" + sizeColumns + "];");
		}
		
		for (int i = 0; i < sizeColumns; i++) {
			IMetadataColumn column = columns.get(i);
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
			
			///////////////////////////////////
			sb.append("//get  and format output String begin\n");
    		sb.append("tempStringM").append(cid).append("=");
			if(JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable()) ) {
				sb.append("String.valueOf(").append(connection).append("_e.").append(column.getLabel()).append(")");
			} else {
				sb.append("(").append(connection).append("_e.").append(column.getLabel()).append(" == null) ? \"\": ");
				if(javaType == JavaTypesManager.DATE && patternValue!=null){
					sb.append("FormatterUtils.format_Date(").append(connection).append("_e.").append(column.getLabel()).append(", ").append(patternValue).append(")");
				}else if(javaType == JavaTypesManager.BYTE_ARRAY){
					sb.append("java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(").append(connection).append("_e.").append(column.getLabel()).append(")).toString()");
				}else if(javaType == JavaTypesManager.STRING){
					sb.append(connection).append("_e.").append(column.getLabel());
				} else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 
        			if(javaType == JavaTypesManager.BIGDECIMAL) {
        				sb.append("FormatterUtils.format_Number(String.valueOf(").append(column.getPrecision() == null? connection + "_e." + column.getLabel() : connection + "_e." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)").append("), ").append(thousandsSeparator).append(", ").append(decimalSeparator).append(")");					
					} else {
						sb.append("FormatterUtils.format_Number(String.valueOf(").append(connection).append("_e.").append(column.getLabel()).append("), ").append(thousandsSeparator).append(", ").append(decimalSeparator).append(")");
					}
				}else if (javaType == JavaTypesManager.BIGDECIMAL) {
					sb.append("String.valueOf(").append(column.getPrecision() == null? connection + "_e." + column.getLabel() : connection + "_e." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" ).append(")");
				}else{
					sb.append("String.valueOf(").append(connection).append("_e.").append(column.getLabel()).append(")");
				}
			}
			sb.append(";\n");
			if("true".equals(csvOption)){ //csv
				sb.append("rowField_" + connection + "[" + i +"] = tempStringM" + cid + ";\n");
				
			}else{
				sb.append("sb_").append(cid).append(".append(");
				sb.append("tempStringM").append(cid);
				sb.append(");\n");
				if(i < sizeColumns -1 ){
					if(isUseMultiSeparator){
						sb.append("sb_").append(cid).append(".append(").append(schema.get("FIELD_SEPARATOR")).append(");\n");
					}else {
						sb.append("sb_").append(cid).append(".append(").append(fieldSeparator).append(");\n");
					}
				}
			}
		}
		if("true".equals(csvOption)){ //csv
			sb.append("out"+cid+".writeRecord(rowField_"+connection+");\n");
		}else{
			sb.append("sb_").append(cid).append(".append(").append(separator).append(");\n");
			sb.append("out").append(cid).append(".write(sb_").append(cid).append(".toString());\n");
			sb.append("sb_").append(cid).append(" = new StringBuilder();\n");
		}
		
		for(int i = 0; i < schemas.size(); i++){//generate children row code....
			Map<String,String> schema2 = schemas.get(i);
			if(schema2.get("PARENT_ROW").equals(connection)){
				generate(i);
			}
		}
		sb.append("}\n");
	}
	
	void generateCode(){
		for(int i = 0; i < connections.size(); i++){
			generate(i);
		}
	}
	
	String getCode(){
		return sb.toString();
	}
}//HSS_____inner class

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
		schema.put("FIELD_SEPARATOR", schema_o.get("FIELD_SEPARATOR"));
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
			for(int i=0;i<conns.size();i++){//HSS_____0_____1_____2
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
	    	}//HSS_____0_____1_____2
			schemas.add(schema);
		}
	}//HSS_____0_____1
	

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
	Gen gen = new Gen(schemas, connections, connectionMapColumnList);
	gen.generateCode();

    stringBuffer.append(TEXT_6);
    stringBuffer.append(gen.getCode() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    if(isDeleteEmptyFile){
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    }
    stringBuffer.append(TEXT_14);
    
}//HSS_____0

    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
