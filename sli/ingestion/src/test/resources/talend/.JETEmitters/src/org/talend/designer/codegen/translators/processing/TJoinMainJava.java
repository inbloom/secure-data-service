package org.talend.designer.codegen.translators.processing;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.Map;

public class TJoinMainJava
{
  protected static String nl;
  public static synchronized TJoinMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJoinMainJava result = new TJoinMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = " = null;\t";
  protected final String TEXT_4 = NL + "\t" + NL + "\t";
  protected final String TEXT_5 = " = new ";
  protected final String TEXT_6 = "Struct();\t";
  protected final String TEXT_7 = "\t\t\t\t\t" + NL + "\t";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ";\t\t\t";
  protected final String TEXT_12 = NL + NL + "\tif(util_";
  protected final String TEXT_13 = ".isJoined(";
  protected final String TEXT_14 = ")){ ";
  protected final String TEXT_15 = "\t\t\t" + NL + "\t\t";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = " = util_";
  protected final String TEXT_18 = ".lookupValue.";
  protected final String TEXT_19 = ";\t\t";
  protected final String TEXT_20 = NL + "\t   ";
  protected final String TEXT_21 = " ";
  protected final String TEXT_22 = " = null; ";
  protected final String TEXT_23 = NL + "\t} " + NL + "\t";
  protected final String TEXT_24 = "\t" + NL + "\telse{" + NL + "\t   ";
  protected final String TEXT_25 = " ";
  protected final String TEXT_26 = " = null; ";
  protected final String TEXT_27 = NL + "\t}";
  protected final String TEXT_28 = NL + "\t   ";
  protected final String TEXT_29 = " ";
  protected final String TEXT_30 = " = null; ";
  protected final String TEXT_31 = "\t\t";
  protected final String TEXT_32 = NL;
  protected final String TEXT_33 = NL + "\t" + NL + "\t";
  protected final String TEXT_34 = " = new ";
  protected final String TEXT_35 = "Struct();\t";
  protected final String TEXT_36 = "\t\t\t\t\t" + NL + "\t";
  protected final String TEXT_37 = ".";
  protected final String TEXT_38 = " = ";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = ";\t\t\t";
  protected final String TEXT_41 = "\t\t" + NL + "///////////////////////    \t\t\t";
  protected final String TEXT_42 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//11
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//22

    stringBuffer.append(TEXT_1);
        
        String cid = node.getUniqueName();
            	
    	//get the input Main and Lookup connection
    	IConnection inMainCon = null;
    	IConnection inRefCon = null;   
    	List< ? extends IConnection> connsIn = node.getIncomingConnections();     
    	for (IConnection conn : connsIn) {//3
    		if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
    			inMainCon = conn;
    		}
    		else if(conn.getLineStyle().equals(EConnectionType.FLOW_REF))
    		{
    			inRefCon = conn;
    		}
        }//3
        
        if(inMainCon == null){
        	return "";
        }
        
        IMetadataTable preMetadata = inMainCon.getMetadataTable();
        String incomingName = inMainCon.getName();
        
    	List<IMetadataColumn> columns = metadata.getListColumns();
    	int columnSize = columns.size();
    	List<IMetadataColumn> preColumns = preMetadata.getListColumns();
    	int preColumnSize = preColumns.size();
    	int minSize = Math.min(columnSize, preColumnSize);
        
        List<Map<String, String>> lookupCols = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__LOOKUP_COLS__");
        boolean useLookup = ("true").equals(ElementParameterParser.getValue(node, "__USE_LOOKUP_COLS__"));
        boolean useReject = ("true").equals(ElementParameterParser.getValue(node, "__USE_INNER_JOIN__"));
        
    	List< ? extends IConnection> outConns = node.getOutgoingSortedConnections();
		List<? extends IConnection> connsFilter = node.getOutgoingConnections("FLOW");
		List<? extends IConnection> connsReject = node.getOutgoingConnections("REJECT");
    	
    	for (IConnection conn : outConns) {
    		if(!conn.isActivate()) continue;
    		if(!conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) continue;
    		String outputConnName = conn.getName();    		

    stringBuffer.append(TEXT_2);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_3);
    		
		}
if(inRefCon != null){//exist lookup		    	
    	for (IConnection conn : outConns) {
    		if(!conn.isActivate()) continue;
    		if(!conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) continue;
    		String outputConnName = conn.getName();

    stringBuffer.append(TEXT_4);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_6);
    
		//first, iterate with minSize
		for (int i = 0; i < minSize; i++) {//4
			IMetadataColumn column = columns.get(i);
			IMetadataColumn preColumn = preColumns.get(i);
			
			//find the lookup column
			boolean isLookup = false;
			for (int j = 0; j < lookupCols.size(); j++) {
                 Map<String, String> lineValue = lookupCols.get(j);
                 if (column.getLabel().equals(lineValue.get("OUTPUT_COLUMN"))) {
					isLookup = true;
					break;
                 }
            }
            
            if(!isLookup) {

    stringBuffer.append(TEXT_7);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(preColumn.getLabel() );
    stringBuffer.append(TEXT_11);
    
			}
		} //4
	}

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_14);
    
if(useLookup) {
	for (IConnection conn : connsFilter) {
		if(!conn.isActivate()) continue;
		String outputConnName = conn.getName();
		//second, iterate with columnSize
		for (int i = 0; i < columnSize; i++) {//4
			IMetadataColumn column = columns.get(i);
			
			//find the lookup column
			boolean isLookup = false;			
			String lookupName = null;
			for (int j = 0; j < lookupCols.size(); j++) {
                 Map<String, String> lineValue = lookupCols.get(j);
                 if (column.getLabel().equals(lineValue.get("OUTPUT_COLUMN"))) {
					isLookup = true;
					lookupName = lineValue.get("LOOKUP_COLUMN");
					break;
                 }
            }
            
    if(isLookup) {

    stringBuffer.append(TEXT_15);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(lookupName );
    stringBuffer.append(TEXT_19);
    
			}
		} //4
  }
} 

    stringBuffer.append(TEXT_20);
    for (IConnection conn : connsReject) { if(!conn.isActivate()) continue;
    stringBuffer.append(TEXT_21);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_22);
    }
    stringBuffer.append(TEXT_23);
    if(useReject){
    stringBuffer.append(TEXT_24);
    for (IConnection conn : connsFilter) { if(!conn.isActivate()) continue;
    stringBuffer.append(TEXT_25);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_26);
    }
    stringBuffer.append(TEXT_27);
    }else{
    stringBuffer.append(TEXT_28);
    for (IConnection conn : connsReject) { if(!conn.isActivate()) continue;
    stringBuffer.append(TEXT_29);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_30);
    }
    stringBuffer.append(TEXT_31);
    }
    stringBuffer.append(TEXT_32);
    } else {//exist lookup    	

for (IConnection conn : connsFilter) {
			if(!conn.isActivate()) continue;
    		String outputConnName = conn.getName();

    stringBuffer.append(TEXT_33);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_35);
    
		//first, iterate with minSize
		for (int i = 0; i < minSize; i++) {//4
			IMetadataColumn column = columns.get(i);
			IMetadataColumn preColumn = preColumns.get(i);

    stringBuffer.append(TEXT_36);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(preColumn.getLabel() );
    stringBuffer.append(TEXT_40);
    
		} //4
	}
}

    stringBuffer.append(TEXT_41);
    
    }//22
}//11

    stringBuffer.append(TEXT_42);
    return stringBuffer.toString();
  }
}
