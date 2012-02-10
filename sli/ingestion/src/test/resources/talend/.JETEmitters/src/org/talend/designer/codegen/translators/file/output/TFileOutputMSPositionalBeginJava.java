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

public class TFileOutputMSPositionalBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputMSPositionalBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputMSPositionalBeginJava result = new TFileOutputMSPositionalBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tString fileNewName_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "\t\tjava.io.File createFile";
  protected final String TEXT_5 = " = new java.io.File(fileNewName_";
  protected final String TEXT_6 = ");";
  protected final String TEXT_7 = NL + "        //create directory only if not exists" + NL + "        java.io.File parentFile_";
  protected final String TEXT_8 = " = createFile";
  protected final String TEXT_9 = ".getParentFile();" + NL + "        if(parentFile_";
  protected final String TEXT_10 = " != null && !parentFile_";
  protected final String TEXT_11 = ".exists()) {" + NL + "            parentFile_";
  protected final String TEXT_12 = ".mkdirs();" + NL + "        }";
  protected final String TEXT_13 = NL + "  " + NL + "\tint nb_line_";
  protected final String TEXT_14 = " = 0;" + NL + "\tfinal java.io.BufferedWriter out";
  protected final String TEXT_15 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(";
  protected final String TEXT_16 = "),";
  protected final String TEXT_17 = "));" + NL + "        " + NL + "        ";
  protected final String TEXT_18 = NL + "\tjava.util.List<";
  protected final String TEXT_19 = "Struct> ";
  protected final String TEXT_20 = "List_";
  protected final String TEXT_21 = " = new java.util.ArrayList<";
  protected final String TEXT_22 = "Struct>();";
  protected final String TEXT_23 = NL + "\torg.talend.commons.utils.data.map.MultiLazyValuesMap ";
  protected final String TEXT_24 = "Map_";
  protected final String TEXT_25 = " = new org.talend.commons.utils.data.map.MultiLazyValuesMap(" + NL + "                    new java.util.HashMap()) {" + NL + "" + NL + "                public java.util.Collection instanciateNewCollection() {" + NL + "                    return new org.apache.commons.collections.list.GrowthList(3);" + NL + "                }" + NL + "" + NL + "            };" + NL + "   final ";
  protected final String TEXT_26 = "Struct[] EMPTY_ARRAY_";
  protected final String TEXT_27 = "_";
  protected final String TEXT_28 = " = new ";
  protected final String TEXT_29 = "Struct[0];";
  protected final String TEXT_30 = NL;

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
		schema.put("PATTERN", TalendTextUtils.removeQuotes(schema_o.get("PATTERN")));
		schema.put("PADDING_CHAR", TalendTextUtils.removeQuotes(schema_o.get("PADDING_CHAR")));
		schema.put("KEEP", TalendTextUtils.removeQuotes(schema_o.get("KEEP")));
		schema.put("ALIGN", TalendTextUtils.removeQuotes(schema_o.get("ALIGN")));
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
    
    
    String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
    if (encoding!=null) {
    	if (("").equals(encoding)) {
        	encoding = "undef";
        }
    }
        
	String filename = ElementParameterParser.getValue(node, "__FILE_NAME__");
        

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
	if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){//HSS_____0_____2

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
	}//HSS_____0_____2

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_17);
    
	for(int i=0; i < connections.size(); i++){//HSS_____0_____3
		String conn = connections.get(i);
		if(i==0){

    stringBuffer.append(TEXT_18);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_22);
    
		}else{

    stringBuffer.append(TEXT_23);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_29);
    
		}
    }//HSS_____0_____3
}//HSS_____0

    stringBuffer.append(TEXT_30);
    return stringBuffer.toString();
  }
}
