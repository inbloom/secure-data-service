package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import org.talend.core.model.utils.TalendTextUtils;
import java.util.Map;

public class TFileOutputMSPositionalMainJava
{
  protected static String nl;
  public static synchronized TFileOutputMSPositionalMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputMSPositionalMainJava result = new TFileOutputMSPositionalMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "Struct ";
  protected final String TEXT_3 = "_";
  protected final String TEXT_4 = " = new ";
  protected final String TEXT_5 = "Struct(); ";
  protected final String TEXT_6 = NL;
  protected final String TEXT_7 = "_";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = NL;
  protected final String TEXT_13 = "List_";
  protected final String TEXT_14 = ".add(";
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = ");" + NL;
  protected final String TEXT_17 = NL;
  protected final String TEXT_18 = "Map_";
  protected final String TEXT_19 = ".put(String.valueOf(";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = "), ";
  protected final String TEXT_22 = "_";
  protected final String TEXT_23 = ");";
  protected final String TEXT_24 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String incomingName = (String)codeGenArgument.getIncomingName();
    
String cid = node.getUniqueName();
        
List< ? extends IConnection> conns = node.getIncomingConnections();
boolean hasDataLink = false;
if(conns!=null){
	for(int i=0;i<conns.size();i++){
		IConnection connTemp = conns.get(i);
	    if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	   		hasDataLink = true;
	   		break;
	    }
	}
}
    
if(hasDataLink){//HSS_____0
    
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
    
	//get the right input connection and the previous input node and metadatas
	    
    List< ? extends IConnection> incomingConns = node.getIncomingConnections();
    	
    if (incomingName == null && incomingConns.size() > 0) {
    	   incomingName = incomingConns.get(0).getName(); 
    }
    
    int i = 0;
    for(; i<connections.size(); i++){
    	if(connections.get(i).equals(incomingName)){
    		break;
    	}
    }

    stringBuffer.append(TEXT_1);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_5);
    
    List<IMetadataColumn> listColumns = connectionMapColumnList.get(incomingName);
    if(!listColumns.isEmpty()){
		for(IMetadataColumn column : listColumns){

    stringBuffer.append(TEXT_6);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_11);
    
		}
	}
	
    if(i==0){

    stringBuffer.append(TEXT_12);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
	}else{
		

    stringBuffer.append(TEXT_17);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(schemas.get(i).get("KEY_COLUMN") );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    
	}
}//HSS_____0

    stringBuffer.append(TEXT_24);
    return stringBuffer.toString();
  }
}
