package org.talend.designer.codegen.translators.business.marketo;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TMarketoOutputMainJava
{
  protected static String nl;
  public static synchronized TMarketoOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMarketoOutputMainJava result = new TMarketoOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL + "\t\t";
  protected final String TEXT_2 = " = null;\t\t\t" + NL + "\t";
  protected final String TEXT_3 = NL + "//////////////////////////////" + NL + "" + NL + "\t\tjava.util.Map<String, String> leadAllAttrList_";
  protected final String TEXT_4 = " = new java.util.HashMap<String,String>();";
  protected final String TEXT_5 = NL + "\t\t\t\t\t";
  protected final String TEXT_6 = NL + "\t\t\t\t\tif(";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = " != null){" + NL + "\t\t\t\t\t";
  protected final String TEXT_9 = NL + "\t\t\t\t\t\tleadAllAttrList_";
  protected final String TEXT_10 = ".put(";
  protected final String TEXT_11 = ",FormatterUtils.format_Date(";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = ", ";
  protected final String TEXT_14 = "));";
  protected final String TEXT_15 = NL + "\t\t\t\t\t\tleadAllAttrList_";
  protected final String TEXT_16 = ".put(";
  protected final String TEXT_17 = ",String.valueOf(";
  protected final String TEXT_18 = ".";
  protected final String TEXT_19 = "));";
  protected final String TEXT_20 = NL + "\t\t\t\t\t";
  protected final String TEXT_21 = NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_22 = NL;
  protected final String TEXT_23 = NL + "\t\t\t\t\tboolean hasFault_";
  protected final String TEXT_24 = " = false;" + NL + "\t\t\t\t\tString errorMsg_";
  protected final String TEXT_25 = " = null;" + NL + "\t\t\t\t\tInteger marketoId_";
  protected final String TEXT_26 = " = null;" + NL + "\t\t\t\t\ttry{" + NL + "\t\t\t\t\t\tcom.marketo.www.mktows.ResultSyncLead syncLead_";
  protected final String TEXT_27 = " = client_";
  protected final String TEXT_28 = ".syncLead(false,null,client_";
  protected final String TEXT_29 = ".buildLead(leadAllAttrList_";
  protected final String TEXT_30 = "));" + NL + "\t\t\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_31 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_32 = ");" + NL + "\t\t\t\t\t\tcom.marketo.www.mktows.SyncStatus syncStatus_";
  protected final String TEXT_33 = " = syncLead_";
  protected final String TEXT_34 = ".getSyncStatus();" + NL + "\t\t\t\t\t\tif (!com.marketo.www.mktows.LeadSyncStatus.FAILED.equals(syncStatus_";
  protected final String TEXT_35 = ".getStatus())) {" + NL + "\t\t\t\t\t\t\thasFault_";
  protected final String TEXT_36 = " = false;" + NL + "\t\t\t\t\t\t\tmarketoId_";
  protected final String TEXT_37 = " = syncStatus_";
  protected final String TEXT_38 = ".getLeadId();" + NL + "\t\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\t\thasFault_";
  protected final String TEXT_39 = " = true;" + NL + "\t\t\t\t\t\t\terrorMsg_";
  protected final String TEXT_40 = " = syncStatus_";
  protected final String TEXT_41 = ".getError();" + NL + "\t\t\t\t\t\t\tmarketoId_";
  protected final String TEXT_42 = " = syncStatus_";
  protected final String TEXT_43 = ".getLeadId();" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}catch(Exception ex_";
  protected final String TEXT_44 = "){" + NL + "\t\t\t\t\t\tif(ex_";
  protected final String TEXT_45 = " instanceof org.apache.axis.AxisFault){" + NL + "\t\t\t    \t\t\tif(!client_";
  protected final String TEXT_46 = ".isSystemError((org.apache.axis.AxisFault)ex_";
  protected final String TEXT_47 = ")){" + NL + "\t\t\t\t\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_48 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_49 = ");\t\t" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t    \t\t}" + NL + "\t\t\t\t\t\thasFault_";
  protected final String TEXT_50 = " = true;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_51 = NL + "\t\t                    throw(ex_";
  protected final String TEXT_52 = ");" + NL + "\t\t                    ";
  protected final String TEXT_53 = NL + "\t\t\t\t\t\t\t\terrorMsg_";
  protected final String TEXT_54 = " = ex_";
  protected final String TEXT_55 = ".getMessage();" + NL + "\t\t\t\t\t\t\t\tmarketoId_";
  protected final String TEXT_56 = " = ";
  protected final String TEXT_57 = ".Id;" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_58 = NL + "\t\t\t\t\t\t\t\tSystem.err.println(ex_";
  protected final String TEXT_59 = ".getMessage());" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_60 = NL + "\t\t\t\t\t}";
  protected final String TEXT_61 = NL + "\t\t\t\t\tleadRecordList_";
  protected final String TEXT_62 = ".add(client_";
  protected final String TEXT_63 = ".buildLead(leadAllAttrList_";
  protected final String TEXT_64 = "));" + NL + "\t\t\t\t\tif(leadRecordList_";
  protected final String TEXT_65 = ".size()>=";
  protected final String TEXT_66 = "){" + NL + "\t\t\t\t\t\tcom.marketo.www.mktows.LeadRecord[] leadRecords_";
  protected final String TEXT_67 = " = leadRecordList_";
  protected final String TEXT_68 = ".toArray(new com.marketo.www.mktows.LeadRecord[leadRecordList_";
  protected final String TEXT_69 = ".size()]);" + NL + "\t\t\t\t\t\ttry{" + NL + "\t\t\t\t\t\t\tcom.marketo.www.mktows.ResultSyncMultipleLeads syncLeads_";
  protected final String TEXT_70 = " = client_";
  protected final String TEXT_71 = ".syncMultipleLeads(";
  protected final String TEXT_72 = ",leadRecords_";
  protected final String TEXT_73 = ");" + NL + "\t\t\t\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_74 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_75 = ");" + NL + "\t\t\t\t\t\t }catch(Exception ex_";
  protected final String TEXT_76 = "){" + NL + "\t\t\t\t\t\t \tif(ex_";
  protected final String TEXT_77 = " instanceof org.apache.axis.AxisFault){" + NL + "\t\t\t\t    \t\t\tif(!client_";
  protected final String TEXT_78 = ".isSystemError((org.apache.axis.AxisFault)ex_";
  protected final String TEXT_79 = ")){" + NL + "\t\t\t\t\t\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_80 = "_NB_CALL\",++nb_call_";
  protected final String TEXT_81 = ");\t\t" + NL + "\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t    \t\t}" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_82 = NL + "\t\t\t\t\t            throw(ex_";
  protected final String TEXT_83 = ");" + NL + "\t\t\t\t\t        ";
  protected final String TEXT_84 = NL + "\t\t\t\t\t        \tSystem.err.println(ex_";
  protected final String TEXT_85 = ".getMessage());" + NL + "\t\t\t\t\t        ";
  protected final String TEXT_86 = NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tleadRecordList_";
  protected final String TEXT_87 = ".clear();" + NL + "\t\t\t\t\t}";
  protected final String TEXT_88 = NL + "\t\t\t\t";
  protected final String TEXT_89 = NL + "\t\t\t\t\t\t\t\tif (!hasFault_";
  protected final String TEXT_90 = ") {" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_91 = " = new ";
  protected final String TEXT_92 = "Struct();" + NL + "\t\t\t\t\t\t\t\t\t//";
  protected final String TEXT_93 = ".SYNC_STATUS = syncStatus_";
  protected final String TEXT_94 = ".getStatus().toString();" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_95 = ".Id = marketoId_";
  protected final String TEXT_96 = ";" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_97 = "\t\t\t" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_98 = ".";
  protected final String TEXT_99 = " = ";
  protected final String TEXT_100 = ".";
  protected final String TEXT_101 = ";" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_102 = NL + "\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_103 = NL + "\t\t\t\t\t\tif(hasFault_";
  protected final String TEXT_104 = "){\t" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_105 = " = new ";
  protected final String TEXT_106 = "Struct();" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_107 = ".Id = marketoId_";
  protected final String TEXT_108 = ";" + NL + "\t            \t\t\t";
  protected final String TEXT_109 = ".ERROR_MSG = errorMsg_";
  protected final String TEXT_110 = ";" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_111 = "\t\t\t" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_112 = ".";
  protected final String TEXT_113 = " = ";
  protected final String TEXT_114 = ".";
  protected final String TEXT_115 = ";" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_116 = NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_117 = NL + "\t\t\t\t";
  protected final String TEXT_118 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_119 = " = new ";
  protected final String TEXT_120 = "Struct();";
  protected final String TEXT_121 = "\t\t\t" + NL + "\t\t\t   \t\t\t";
  protected final String TEXT_122 = ".";
  protected final String TEXT_123 = " = ";
  protected final String TEXT_124 = ".";
  protected final String TEXT_125 = ";\t\t\t";
  protected final String TEXT_126 = "\t\t \t";
  protected final String TEXT_127 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String operation = ElementParameterParser.getValue(node, "__OPERATION__");
	boolean deDupenabled = ("true").equals(ElementParameterParser.getValue(node,"__DE_DUPENABLED__"));
	String batchSize = ElementParameterParser.getValue(node, "__BATCH_SIZE__");
	boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node,"__DIE_ON_ERROR__"));
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
	if(outgoingConns!=null){
		for (int i=0;i<outgoingConns.size();i++) {
	    IConnection outgoingConn = outgoingConns.get(i);
	    	if (outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	
    stringBuffer.append(TEXT_1);
    stringBuffer.append(outgoingConn.getName() );
    stringBuffer.append(TEXT_2);
    
	    	}
	    }
	}   	
	
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    			
				List<Map<String, String>> mappingList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MAPPING_INPUT__");
    			Map<String,String> fldMapping = new HashMap<String,String>();
    			for(Map<String,String> inputMapping:mappingList){
    				String mapStr = inputMapping.get("PARAMETER_NAME");
    				if("\"\"".equals(mapStr)){
    					fldMapping.put(inputMapping.get("SCHEMA_COLUMN"),"\""+inputMapping.get("SCHEMA_COLUMN")+"\"");
    				}else{	
    					fldMapping.put(inputMapping.get("SCHEMA_COLUMN"),inputMapping.get("PARAMETER_NAME"));
    				}
    			}
				List<IMetadataColumn> columns = metadata.getListColumns();
				int sizeColumns = columns.size();
				for (int i = 0; i < sizeColumns; i++) {
					IMetadataColumn column = columns.get(i);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
					String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
					

    stringBuffer.append(TEXT_5);
    if(!isPrimitive) {
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_8);
    }
    
					if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {//Date

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(fldMapping.get(column.getLabel()));
    stringBuffer.append(TEXT_11);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(pattern );
    stringBuffer.append(TEXT_14);
    
					} else {//others	

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(fldMapping.get(column.getLabel()));
    stringBuffer.append(TEXT_17);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_19);
    
					}

    stringBuffer.append(TEXT_20);
    if(!isPrimitive) {
    stringBuffer.append(TEXT_21);
    }
    					
				}

    stringBuffer.append(TEXT_22);
    
				if("syncLead".equals(operation)){

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    
						if (dieOnError) {
		                    
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    
		                } else {
							if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_57);
    }else{
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    }
						}
    stringBuffer.append(TEXT_60);
    				
				}else if("syncMultipleLeads".equals(operation)){

    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(batchSize);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(deDupenabled);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    
					        if (dieOnError) {
					        
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    
					        } else {
					        
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    
					        }
					        
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    				
				}

    
			if("syncLead".equals(operation)){
				

    stringBuffer.append(TEXT_88);
    			
					 for(IConnection outgoingConn : outgoingConns) {
	                	if(rejectConnName == null || (rejectConnName != null && !outgoingConn.getName().equals(rejectConnName))) {
	                    	if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
							
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_91);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_92);
    stringBuffer.append(outgoingConn.getName() );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    
									for (IMetadataColumn column: metadata.getListColumns()) {
										if("Id".equals(column.getLabel()))
											continue;
											
    stringBuffer.append(TEXT_97);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_98);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_101);
    					 
									}
									
    stringBuffer.append(TEXT_102);
    
							}
						}
					}

    
					if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    
							for (IMetadataColumn column: metadata.getListColumns()) {
								if("Id".equals(column.getLabel()))
									continue;
									
    stringBuffer.append(TEXT_111);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_115);
    					 
							}
							
    stringBuffer.append(TEXT_116);
    
					}

    stringBuffer.append(TEXT_117);
    			}else{//syncLead end 

    
			 for(IConnection outgoingConn : outgoingConns) {
            	if(rejectConnName == null || (rejectConnName != null && !outgoingConn.getName().equals(rejectConnName))) {
                	if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_118);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_119);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_120);
    
					for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_121);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_122);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_125);
    					 
					}
					}
				}
			}

    stringBuffer.append(TEXT_126);
    			
				}//syncMultipleLeads end

    				
				}//4
			}//3
		}//2
	}//1

    stringBuffer.append(TEXT_127);
    return stringBuffer.toString();
  }
}
