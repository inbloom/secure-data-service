package org.talend.designer.codegen.translators.databases.exasolution;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TEXARowMainJava
{
  protected static String nl;
  public static synchronized TEXARowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TEXARowMainJava result = new TEXARowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "    " + NL + "\t\t";
  protected final String TEXT_3 = " = null;            " + NL + "\t\t";
  protected final String TEXT_4 = NL + NL + "whetherReject_";
  protected final String TEXT_5 = " = false;";
  protected final String TEXT_6 = NL + "query_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "globalMap.put(\"";
  protected final String TEXT_9 = "_QUERY\",query_";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "\tjava.sql.ResultSet rs_";
  protected final String TEXT_12 = " = null;" + NL + "\t";
  protected final String TEXT_13 = NL + "try {";
  protected final String TEXT_14 = NL + "\t\trs_";
  protected final String TEXT_15 = " = stmt_";
  protected final String TEXT_16 = ".executeQuery(query_";
  protected final String TEXT_17 = ");" + NL + "\t\t";
  protected final String TEXT_18 = NL + "    \tstmt_";
  protected final String TEXT_19 = ".execute(query_";
  protected final String TEXT_20 = ");" + NL + "    \t";
  protected final String TEXT_21 = NL + "    } catch (Exception e) {" + NL + "        whetherReject_";
  protected final String TEXT_22 = " = true;";
  protected final String TEXT_23 = NL + "            throw(e);";
  protected final String TEXT_24 = NL + "                ";
  protected final String TEXT_25 = " = new ";
  protected final String TEXT_26 = "Struct();";
  protected final String TEXT_27 = NL + "                    ";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = " = ";
  protected final String TEXT_30 = ".";
  protected final String TEXT_31 = ";";
  protected final String TEXT_32 = NL + "                ";
  protected final String TEXT_33 = ".errorMessage = e.getMessage();";
  protected final String TEXT_34 = NL + "                System.err.print(e.getMessage());";
  protected final String TEXT_35 = NL + "    }";
  protected final String TEXT_36 = NL + "    if(!whetherReject_";
  protected final String TEXT_37 = ") {";
  protected final String TEXT_38 = NL + "                    ";
  protected final String TEXT_39 = " = new ";
  protected final String TEXT_40 = "Struct();";
  protected final String TEXT_41 = NL + "                    \t    ";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = " = rs_";
  protected final String TEXT_44 = ";" + NL + "                    \t    ";
  protected final String TEXT_45 = NL + "                            ";
  protected final String TEXT_46 = ".";
  protected final String TEXT_47 = " = ";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = ";";
  protected final String TEXT_50 = NL + "    }";
  protected final String TEXT_51 = NL + "        commitCounter_";
  protected final String TEXT_52 = "++;" + NL + "        if(commitEvery_";
  protected final String TEXT_53 = " <= commitCounter_";
  protected final String TEXT_54 = ") {        " + NL + "        \tconn_";
  protected final String TEXT_55 = ".commit();        \t" + NL + "        \tcommitCounter_";
  protected final String TEXT_56 = "=0;        \t" + NL + "        }";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid =  node.getUniqueName();
String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
String propagateRecordset = ElementParameterParser.getValue(node,"__PROPAGATE_RECORD_SET__");
String recordsetColumn = ElementParameterParser.getValue(node,"__RECORD_SET_COLUMN__");
String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");

String incomingConnName = null;

List<IMetadataColumn> columnList = null;

String rejectConnName = null;
List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
if(rejectConns != null && rejectConns.size() > 0) {
    IConnection rejectConn = rejectConns.get(0);
    rejectConnName = rejectConn.getName();
}
List<IMetadataColumn> rejectColumnList = null;
IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
if(metadataTable != null) {
    rejectColumnList = metadataTable.getListColumns();      
}

List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

boolean hasOutgoingDataConnection = false;
for(IConnection conn : outgoingConns) {
    if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
		if(!hasOutgoingDataConnection) {
			hasOutgoingDataConnection = true;
		}
		
    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_3);
          
    }
}
String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
       dbquery = dbquery.replaceAll("\n"," ");
	   dbquery = dbquery.replaceAll("\r"," ");


    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {
		List< ? extends IConnection> conns = node.getIncomingConnections();
		columnList = metadata.getListColumns();
		if(conns != null && conns.size()>0){
			IConnection conn = conns.get(0);
			incomingConnName = conn.getName();
		}//end of connection size.
	}//end of metadatas
}
if(!hasOutgoingDataConnection || columnList == null || columnList.size() < 1) {
	propagateRecordset = "false";
}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
if(propagateRecordset.equals("true")) {
    
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    
    if(propagateRecordset.equals("true")) {
        
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    
	}else{
	    
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    
	}
    
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    
        if (dieOnError.equals("true")) {
            
    stringBuffer.append(TEXT_23);
    
        } else {
            if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                
    stringBuffer.append(TEXT_24);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_26);
    
                for(IMetadataColumn column : columnList) {
                    
    stringBuffer.append(TEXT_27);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_29);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_31);
    
                }
                
    stringBuffer.append(TEXT_32);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_33);
    
            } else {
                
    stringBuffer.append(TEXT_34);
    
            }
        } 
        
    stringBuffer.append(TEXT_35);
    
	
if(outgoingConns != null && outgoingConns.size() > 0) {
    
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    
        for(IConnection outgoingConn : outgoingConns) {
            if(rejectConnName == null || (rejectConnName != null && !outgoingConn.getName().equals(rejectConnName))) {
                if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                    
    stringBuffer.append(TEXT_38);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_39);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_40);
    
                    for(IMetadataColumn column : columnList) {
                    	if(propagateRecordset.equals("true") && column.getLabel().equals(recordsetColumn)){
                    	    
    stringBuffer.append(TEXT_41);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_42);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
                    	} else {
                            
    stringBuffer.append(TEXT_45);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_46);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_47);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_49);
                      
						}
                    }
                }
            }
        }
        
    stringBuffer.append(TEXT_50);
    
}	
if(!useExistingConn.equals("true")) {	
    if(!commitEvery.equals("") && !commitEvery.equals("0")) {
        
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    
    }
}

    return stringBuffer.toString();
  }
}
