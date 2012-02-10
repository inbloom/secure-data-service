package org.talend.designer.codegen.translators.cloud.amazonrds.oracle;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.Map;

public class TAmazonOracleRowMainJava
{
  protected static String nl;
  public static synchronized TAmazonOracleRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAmazonOracleRowMainJava result = new TAmazonOracleRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "    " + NL + "\t\t";
  protected final String TEXT_3 = " = null;            " + NL + "\t\t";
  protected final String TEXT_4 = NL + "query_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "whetherReject_";
  protected final String TEXT_7 = " = false;" + NL;
  protected final String TEXT_8 = NL + "globalMap.put(\"";
  protected final String TEXT_9 = "_QUERY\",query_";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "\tjava.sql.ResultSet rs_";
  protected final String TEXT_12 = " = null;" + NL + "\t";
  protected final String TEXT_13 = NL + "try {";
  protected final String TEXT_14 = NL + "\t\t\t\tif((";
  protected final String TEXT_15 = "==null))" + NL + "\t\t\t\t\t{" + NL + "\t\t\t\t\tpstmt_";
  protected final String TEXT_16 = ".setNull(";
  protected final String TEXT_17 = ", java.sql.Types.TIMESTAMP);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\telse{" + NL + "\t\t\t\t\tpstmt_";
  protected final String TEXT_18 = ".setTimestamp(";
  protected final String TEXT_19 = ",new java.sql.Timestamp(";
  protected final String TEXT_20 = ".getTime()));" + NL + "\t\t\t\t\t}";
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
  protected final String TEXT_38 = NL;
  protected final String TEXT_39 = NL + "\t\tnb_line_inserted_";
  protected final String TEXT_40 = " += pstmt_";
  protected final String TEXT_41 = ".getUpdateCount();";
  protected final String TEXT_42 = NL + "\t\tnb_line_update_";
  protected final String TEXT_43 = " += pstmt_";
  protected final String TEXT_44 = ".getUpdateCount();";
  protected final String TEXT_45 = NL + "\t\tnb_line_deleted_";
  protected final String TEXT_46 = " += pstmt_";
  protected final String TEXT_47 = ".getUpdateCount();";
  protected final String TEXT_48 = NL + "\t\tnb_line_inserted_";
  protected final String TEXT_49 = " += stmt_";
  protected final String TEXT_50 = ".getUpdateCount();";
  protected final String TEXT_51 = NL + "\t\tnb_line_update_";
  protected final String TEXT_52 = " += stmt_";
  protected final String TEXT_53 = ".getUpdateCount();";
  protected final String TEXT_54 = NL + "\t\tnb_line_deleted_";
  protected final String TEXT_55 = " += stmt_";
  protected final String TEXT_56 = ".getUpdateCount();";
  protected final String TEXT_57 = NL + NL + "    } catch (Exception e) {" + NL + "        whetherReject_";
  protected final String TEXT_58 = " = true;";
  protected final String TEXT_59 = NL + "            throw(e);";
  protected final String TEXT_60 = NL + "                ";
  protected final String TEXT_61 = " = new ";
  protected final String TEXT_62 = "Struct();";
  protected final String TEXT_63 = NL + "                    ";
  protected final String TEXT_64 = ".";
  protected final String TEXT_65 = " = ";
  protected final String TEXT_66 = ".";
  protected final String TEXT_67 = ";";
  protected final String TEXT_68 = NL + "                ";
  protected final String TEXT_69 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_70 = ";";
  protected final String TEXT_71 = NL + "                System.err.print(e.getMessage());";
  protected final String TEXT_72 = NL + "    }" + NL + "\t";
  protected final String TEXT_73 = NL + "    if(!whetherReject_";
  protected final String TEXT_74 = ") {";
  protected final String TEXT_75 = NL + "                    ";
  protected final String TEXT_76 = " = new ";
  protected final String TEXT_77 = "Struct();";
  protected final String TEXT_78 = NL + "                    \t    ";
  protected final String TEXT_79 = ".";
  protected final String TEXT_80 = " = rs_";
  protected final String TEXT_81 = ";" + NL + "                    \t    ";
  protected final String TEXT_82 = NL + "                        ";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = " = ";
  protected final String TEXT_85 = ".";
  protected final String TEXT_86 = ";";
  protected final String TEXT_87 = NL + "    }";
  protected final String TEXT_88 = NL + "\t    commitCounter_";
  protected final String TEXT_89 = "++;" + NL + "        if(commitEvery_";
  protected final String TEXT_90 = " <= commitCounter_";
  protected final String TEXT_91 = ") {" + NL + "        " + NL + "        \tconn_";
  protected final String TEXT_92 = ".commit();" + NL + "        \t" + NL + "        \tcommitCounter_";
  protected final String TEXT_93 = "=0;" + NL + "        \t" + NL + "        }";
  protected final String TEXT_94 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid =  node.getUniqueName();
String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
String propagateRecordset = ElementParameterParser.getValue(node,"__PROPAGATE_RECORD_SET__");
String recordsetColumn = ElementParameterParser.getValue(node,"__RECORD_SET_COLUMN__");
boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
		dbquery = dbquery.replaceAll("\n"," ");
		dbquery = dbquery.replaceAll("\r"," ");
List<Map<String, String>> prepareStatementParameters = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SET_PREPAREDSTATEMENT_PARAMETERS__");  

String use_NB_Line = ElementParameterParser.getValue(node, "__USE_NB_LINE__");

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
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
          
    }
}

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
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

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
if(("true").equals(propagateRecordset)){
    
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    		
	if (usePrepareStatement) {
	
		for (Map<String, String> param : prepareStatementParameters) {
			
			if ("Date".equals(param.get("PARAMETER_TYPE"))) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(param.get("PARAMETER_VALUE"));
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(param.get("PARAMETER_INDEX"));
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
    	//feature 0010425
	if(usePrepareStatement){
		if ("NB_LINE_INSERTED".equals(use_NB_Line)) {

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    
		} else if ("NB_LINE_UPDATED".equals(use_NB_Line)) {

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
		} else if ("NB_LINE_DELETED".equals(use_NB_Line)) {

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    
		}
	} else {
		if ("NB_LINE_INSERTED".equals(use_NB_Line)) {

    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    
		} else if ("NB_LINE_UPDATED".equals(use_NB_Line)) {

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    
		} else if ("NB_LINE_DELETED".equals(use_NB_Line)) {

    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    
		}
	}//end feature 0010425

    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    
        if (("true").equals(dieOnError)) {
            
    stringBuffer.append(TEXT_59);
    
        } else {
            if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                
    stringBuffer.append(TEXT_60);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_62);
    
                for(IMetadataColumn column : columnList) {
                    
    stringBuffer.append(TEXT_63);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_65);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_67);
    
                }
                
    stringBuffer.append(TEXT_68);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_70);
    
            } else {
                
    stringBuffer.append(TEXT_71);
    
            }
        } 
        
    stringBuffer.append(TEXT_72);
    	
if(outgoingConns != null && outgoingConns.size() > 0) {
    
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    
        for(IConnection outgoingConn : outgoingConns) {
            if(rejectConnName == null || (rejectConnName != null && !outgoingConn.getName().equals(rejectConnName))) {
                if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                    
    stringBuffer.append(TEXT_75);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_76);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_77);
    
                    for(IMetadataColumn column : columnList) {
                    	if(("true").equals(propagateRecordset) && column.getLabel().equals(recordsetColumn)){
                    	    
    stringBuffer.append(TEXT_78);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_79);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    
                    	} else {
                        
    stringBuffer.append(TEXT_82);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_83);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_84);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_86);
    
						}
                    }
                }
            }
        }
        
    stringBuffer.append(TEXT_87);
    
}	
	
if(!("true").equals(useExistingConn)) {
	if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
	    
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    
	}
}

    stringBuffer.append(TEXT_94);
    return stringBuffer.toString();
  }
}
