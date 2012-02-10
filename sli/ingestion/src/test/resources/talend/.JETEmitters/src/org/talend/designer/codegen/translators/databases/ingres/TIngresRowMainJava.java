package org.talend.designer.codegen.translators.databases.ingres;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.Map;

public class TIngresRowMainJava
{
  protected static String nl;
  public static synchronized TIngresRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TIngresRowMainJava result = new TIngresRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    ";
  protected final String TEXT_3 = NL + "    ";
  protected final String TEXT_4 = " = null;            ";
  protected final String TEXT_5 = NL + "\t    commitCounter_";
  protected final String TEXT_6 = "++;" + NL + "\t    ";
  protected final String TEXT_7 = NL + "query_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ";" + NL + "whetherReject_";
  protected final String TEXT_10 = " = false;" + NL;
  protected final String TEXT_11 = NL + "\tglobalMap.put(\"";
  protected final String TEXT_12 = "_QUERY\",query_";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL + "\tjava.sql.ResultSet rs_";
  protected final String TEXT_15 = " = null;";
  protected final String TEXT_16 = NL + "try {";
  protected final String TEXT_17 = NL + "\t\t\tpstmt_";
  protected final String TEXT_18 = ".setTimestamp(";
  protected final String TEXT_19 = ",new java.sql.Timestamp(";
  protected final String TEXT_20 = ".getTime()));";
  protected final String TEXT_21 = NL + "\t\t\tpstmt_";
  protected final String TEXT_22 = ".set";
  protected final String TEXT_23 = "(";
  protected final String TEXT_24 = ",";
  protected final String TEXT_25 = ");";
  protected final String TEXT_26 = NL + "\t\trs_";
  protected final String TEXT_27 = " = pstmt_";
  protected final String TEXT_28 = ".executeQuery();";
  protected final String TEXT_29 = NL + "    \tpstmt_";
  protected final String TEXT_30 = ".execute();" + NL + "    \t";
  protected final String TEXT_31 = NL + "\t\trs_";
  protected final String TEXT_32 = " = stmt_";
  protected final String TEXT_33 = ".executeQuery(query_";
  protected final String TEXT_34 = ");";
  protected final String TEXT_35 = NL + "    \tstmt_";
  protected final String TEXT_36 = ".execute(query_";
  protected final String TEXT_37 = ");" + NL + "    \t";
  protected final String TEXT_38 = NL + "\t} catch (Exception e) {" + NL + "        whetherReject_";
  protected final String TEXT_39 = " = true;";
  protected final String TEXT_40 = NL + "            throw(e);";
  protected final String TEXT_41 = NL + "                ";
  protected final String TEXT_42 = " = new ";
  protected final String TEXT_43 = "Struct();";
  protected final String TEXT_44 = NL + "                    ";
  protected final String TEXT_45 = ".";
  protected final String TEXT_46 = " = ";
  protected final String TEXT_47 = ".";
  protected final String TEXT_48 = ";";
  protected final String TEXT_49 = NL + "                ";
  protected final String TEXT_50 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_51 = ";";
  protected final String TEXT_52 = NL + "                System.err.print(e.getMessage());";
  protected final String TEXT_53 = NL + "    }" + NL + "\t";
  protected final String TEXT_54 = NL + "        if(!whetherReject_";
  protected final String TEXT_55 = ") {";
  protected final String TEXT_56 = NL + "                        ";
  protected final String TEXT_57 = " = new ";
  protected final String TEXT_58 = "Struct();";
  protected final String TEXT_59 = NL;
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = " = rs_";
  protected final String TEXT_62 = ";";
  protected final String TEXT_63 = NL;
  protected final String TEXT_64 = ".";
  protected final String TEXT_65 = " = ";
  protected final String TEXT_66 = ".";
  protected final String TEXT_67 = ";";
  protected final String TEXT_68 = NL + "        }";
  protected final String TEXT_69 = NL + "\t    if(commitEvery_";
  protected final String TEXT_70 = "<=commitCounter_";
  protected final String TEXT_71 = "){" + NL + "\t    " + NL + "\t    \tconn_";
  protected final String TEXT_72 = ".commit();" + NL + "\t    \t" + NL + "\t    \tcommitCounter_";
  protected final String TEXT_73 = "=0;" + NL + "\t    \t" + NL + "\t    }" + NL + "\t    ";
  protected final String TEXT_74 = NL;

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

boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
		dbquery = dbquery.replaceAll("\n"," ");
		dbquery = dbquery.replaceAll("\r"," ");
List<Map<String, String>> prepareStatementParameters = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SET_PREPAREDSTATEMENT_PARAMETERS__");  

boolean useExistingConnection = "true".equalsIgnoreCase(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));

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
		if(!hasOutgoingDataConnection){
			hasOutgoingDataConnection = true;
		}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_4);
          }
}
if(!useExistingConnection) {
	if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){
	    
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
	}
}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
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
	if(!hasOutgoingDataConnection || columnList == null || columnList.size() < 1){
		propagateRecordset = "false";
	}
	
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
	if(("true").equals(propagateRecordset)){

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    
	}

    stringBuffer.append(TEXT_16);
    		
	if (usePrepareStatement) {
	
		for (Map<String, String> param : prepareStatementParameters) {
			
			if ("Date".equals(param.get("PARAMETER_TYPE"))) {

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(param.get("PARAMETER_INDEX"));
    stringBuffer.append(TEXT_19);
    stringBuffer.append(param.get("PARAMETER_VALUE"));
    stringBuffer.append(TEXT_20);
    
			} else {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(param.get("PARAMETER_TYPE"));
    stringBuffer.append(TEXT_23);
    stringBuffer.append(param.get("PARAMETER_INDEX"));
    stringBuffer.append(TEXT_24);
    stringBuffer.append(param.get("PARAMETER_VALUE"));
    stringBuffer.append(TEXT_25);
    
			}
		}
		
		if(("true").equals(propagateRecordset)){

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    
		} else {

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    
		}
		
	} else {
		if(("true").equals(propagateRecordset)){

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    
		} else {

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    
		}
	}

    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    
        if (("true").equals(dieOnError)) {
            
    stringBuffer.append(TEXT_40);
    
        } else {
            if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
            
    stringBuffer.append(TEXT_41);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_43);
    
                for(IMetadataColumn column : columnList) {
                    
    stringBuffer.append(TEXT_44);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_46);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_48);
    
                }
                
    stringBuffer.append(TEXT_49);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_51);
    
            } else {
                
    stringBuffer.append(TEXT_52);
    
            }
        } 
        
    stringBuffer.append(TEXT_53);
    
	
    if(outgoingConns != null && outgoingConns.size() > 0) {
        
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    
            for(IConnection outgoingConn : outgoingConns) {
                if(rejectConnName == null || (rejectConnName != null && !outgoingConn.getName().equals(rejectConnName))) {
                    if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {            
         
    stringBuffer.append(TEXT_56);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_57);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_58);
    
                        for(IMetadataColumn column : columnList) {
                        	if(("true").equals(propagateRecordset) && column.getLabel().equals(recordsetColumn)){

    stringBuffer.append(TEXT_59);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    
                        	}else{
                            
    stringBuffer.append(TEXT_63);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_64);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_65);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_67);
    
                  			}
                        }
                    }
                }
            }
        
    stringBuffer.append(TEXT_68);
    
    }	
    
if(!useExistingConnection) {	
	if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){
	    
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    
	}
}

    stringBuffer.append(TEXT_74);
    return stringBuffer.toString();
  }
}
