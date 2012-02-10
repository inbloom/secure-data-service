package org.talend.designer.codegen.translators.business.microsoft_crm;

import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TMicrosoftCrmOutputMainJava
{
  protected static String nl;
  public static synchronized TMicrosoftCrmOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMicrosoftCrmOutputMainJava result = new TMicrosoftCrmOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "StringBuffer OperXml_";
  protected final String TEXT_2 = " = new StringBuffer();" + NL;
  protected final String TEXT_3 = NL + "\t\t";
  protected final String TEXT_4 = NL + "    \tOperXml_";
  protected final String TEXT_5 = ".append(\"<Create xmlns=\\\"http://schemas.microsoft.com/crm/2007/WebServices\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\">\");";
  protected final String TEXT_6 = NL + "    \tOperXml_";
  protected final String TEXT_7 = ".append(\"<Update xmlns=\\\"http://schemas.microsoft.com/crm/2007/WebServices\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\">\");";
  protected final String TEXT_8 = NL + "        OperXml_";
  protected final String TEXT_9 = ".append(\"\\n\");" + NL + "    \tOperXml_";
  protected final String TEXT_10 = ".append(\"<entity xsi:type=\\\"web:\");" + NL + "    \tOperXml_";
  protected final String TEXT_11 = ".append(\"";
  protected final String TEXT_12 = "\");" + NL + "    \tOperXml_";
  protected final String TEXT_13 = ".append(\"\\\" xmlns:web=\\\"http://schemas.microsoft.com/crm/2007/WebServices\\\">\");" + NL + "    \tOperXml_";
  protected final String TEXT_14 = ".append(\"\\n\");" + NL + "    \t\t";
  protected final String TEXT_15 = NL;
  protected final String TEXT_16 = "   \t\t\t\t" + NL + "\t    \t\tif(";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = " != null ) { //";
  protected final String TEXT_19 = "   " + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_20 = ".append(\"<\");" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_21 = ".append(\"";
  protected final String TEXT_22 = "\");" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_23 = NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_24 = ".append(\" type=\\\"\");\t" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_25 = ".append(";
  protected final String TEXT_26 = ");" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_27 = ".append(\"\\\"\");\t" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_28 = NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_29 = ".append(\">\");" + NL + "\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_30 = ".append(\"<![CDATA[\");";
  protected final String TEXT_31 = NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_32 = ".append(FormatterUtils.format_Date(";
  protected final String TEXT_33 = ".";
  protected final String TEXT_34 = ", ";
  protected final String TEXT_35 = "));";
  protected final String TEXT_36 = NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_37 = ".append(String.valueOf(";
  protected final String TEXT_38 = ".";
  protected final String TEXT_39 = "));";
  protected final String TEXT_40 = NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_41 = ".append(\"]]>\");" + NL + "\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_42 = ".append(\"</\");" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_43 = ".append(\"";
  protected final String TEXT_44 = "\");" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_45 = ".append(\">\");" + NL + "\t\t\t\t\t\t\t\tOperXml_";
  protected final String TEXT_46 = ".append(\"\\n\");";
  protected final String TEXT_47 = NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_48 = ".append(\"<\");" + NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_49 = ".append(\"";
  protected final String TEXT_50 = "\");" + NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_51 = ".append(\"id>\");" + NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_52 = ".append(";
  protected final String TEXT_53 = ".Id);" + NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_54 = ".append(\"</\");" + NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_55 = ".append(\"";
  protected final String TEXT_56 = "\");" + NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_57 = ".append(\"id>\");" + NL + "\t\t\t\t\t\tOperXml_";
  protected final String TEXT_58 = ".append(\"\\n\");";
  protected final String TEXT_59 = NL + "\t    \t\t} //";
  protected final String TEXT_60 = "  \t\t\t\t\t\t\t\t" + NL;
  protected final String TEXT_61 = " ";
  protected final String TEXT_62 = NL + "OperXml_";
  protected final String TEXT_63 = ".append(\"</entity>\");" + NL + "OperXml_";
  protected final String TEXT_64 = ".append(\"\\n\");" + NL + "OperXml_";
  protected final String TEXT_65 = ".append(\"</Create>\");" + NL + "com.microsoft.schemas.crm._2007.webservices.CreateDocument createDoc_";
  protected final String TEXT_66 = " =  com.microsoft.schemas.crm._2007.webservices.CreateDocument.Factory.parse(OperXml_";
  protected final String TEXT_67 = ".toString());" + NL + "service_";
  protected final String TEXT_68 = ".create(createDoc_";
  protected final String TEXT_69 = ", catd_";
  protected final String TEXT_70 = ", null, null);";
  protected final String TEXT_71 = NL + "OperXml_";
  protected final String TEXT_72 = ".append(\"</entity>\");" + NL + "OperXml_";
  protected final String TEXT_73 = ".append(\"\\n\");" + NL + "OperXml_";
  protected final String TEXT_74 = ".append(\"</Update>\");" + NL + "com.microsoft.schemas.crm._2007.webservices.UpdateDocument updateDoc_";
  protected final String TEXT_75 = " = com.microsoft.schemas.crm._2007.webservices.UpdateDocument.Factory.parse(OperXml_";
  protected final String TEXT_76 = ".toString());" + NL + "service_";
  protected final String TEXT_77 = ".update(updateDoc_";
  protected final String TEXT_78 = ",catd_";
  protected final String TEXT_79 = ",null,null);\t\t\t";
  protected final String TEXT_80 = NL + "    \t\t\t";
  protected final String TEXT_81 = NL + "\tOperXml_";
  protected final String TEXT_82 = ".append(\"<Delete xmlns=\\\"http://schemas.microsoft.com/crm/2007/WebServices\\\">\");" + NL + "\tOperXml_";
  protected final String TEXT_83 = ".append(\"\\n\");" + NL + "\tOperXml_";
  protected final String TEXT_84 = ".append(\"<entityName>\");" + NL + "\tOperXml_";
  protected final String TEXT_85 = ".append(\"";
  protected final String TEXT_86 = "\");" + NL + "\tOperXml_";
  protected final String TEXT_87 = ".append(\"</entityName>\");" + NL + "\tOperXml_";
  protected final String TEXT_88 = ".append(\"\\n\");" + NL + "\tOperXml_";
  protected final String TEXT_89 = ".append(\"<id>\");" + NL + "\tOperXml_";
  protected final String TEXT_90 = ".append(";
  protected final String TEXT_91 = ".Id);" + NL + "\tOperXml_";
  protected final String TEXT_92 = ".append(\"</id>\\n\");" + NL + "\tOperXml_";
  protected final String TEXT_93 = ".append(\"</Delete>\");" + NL + "\tcom.microsoft.schemas.crm._2007.webservices.DeleteDocument deleteDoc_";
  protected final String TEXT_94 = " = com.microsoft.schemas.crm._2007.webservices.DeleteDocument.Factory.parse(OperXml_";
  protected final String TEXT_95 = ".toString());" + NL + "\tservice_";
  protected final String TEXT_96 = ".delete(deleteDoc_";
  protected final String TEXT_97 = ",catd_";
  protected final String TEXT_98 = ",null,null);" + NL;
  protected final String TEXT_99 = NL + "\tcom.microsoft.schemas.crm._2007.webservices.ExecuteDocument.Execute execute_";
  protected final String TEXT_100 = " = com.microsoft.schemas.crm._2007.webservices.ExecuteDocument.Execute.Factory" + NL + "            .newInstance();" + NL + "    com.microsoft.schemas.crm._2007.webservices.ExecuteDocument executeDoc_";
  protected final String TEXT_101 = " = com.microsoft.schemas.crm._2007.webservices.ExecuteDocument.Factory" + NL + "            .newInstance();" + NL + "    com.microsoft.schemas.crm._2007.webservices.AssignRequest assignRequest_";
  protected final String TEXT_102 = " = com.microsoft.schemas.crm._2007.webservices.AssignRequest.Factory" + NL + "            .newInstance();" + NL + "    com.microsoft.schemas.crm._2006.coretypes.SecurityPrincipal assignee_";
  protected final String TEXT_103 = " = com.microsoft.schemas.crm._2006.coretypes.SecurityPrincipal.Factory.newInstance();" + NL + "    assignee_";
  protected final String TEXT_104 = ".setPrincipalId(String.valueOf(";
  protected final String TEXT_105 = ".ownerid));" + NL + "    assignee_";
  protected final String TEXT_106 = ".setType(com.microsoft.schemas.crm._2006.coretypes.SecurityPrincipalType.Enum.forString(\"User\"));" + NL + "    assignRequest_";
  protected final String TEXT_107 = ".setAssignee(assignee_";
  protected final String TEXT_108 = ");" + NL + "    " + NL + "    com.microsoft.schemas.crm._2007.webservices.TargetOwnedDynamic dynamicTarget_";
  protected final String TEXT_109 = " = com.microsoft.schemas.crm._2007.webservices.TargetOwnedDynamic.Factory" + NL + "            .newInstance();" + NL + "    dynamicTarget_";
  protected final String TEXT_110 = ".setEntityName(\"";
  protected final String TEXT_111 = "\");" + NL + "    dynamicTarget_";
  protected final String TEXT_112 = ".setEntityId(String.valueOf(";
  protected final String TEXT_113 = ".Id));" + NL + "    " + NL + "    assignRequest_";
  protected final String TEXT_114 = ".setTarget(dynamicTarget_";
  protected final String TEXT_115 = ");" + NL + "" + NL + "    execute_";
  protected final String TEXT_116 = ".setRequest(assignRequest_";
  protected final String TEXT_117 = ");" + NL + "    executeDoc_";
  protected final String TEXT_118 = ".setExecute(execute_";
  protected final String TEXT_119 = ");" + NL + "    " + NL + "    executeDoc_";
  protected final String TEXT_120 = " = com.microsoft.schemas.crm._2007.webservices.ExecuteDocument.Factory.parse(executeDoc_";
  protected final String TEXT_121 = ".toString());" + NL + "" + NL + "    service_";
  protected final String TEXT_122 = ".execute(executeDoc_";
  protected final String TEXT_123 = ", catd_";
  protected final String TEXT_124 = ", null,null);" + NL + "            ";
  protected final String TEXT_125 = " \t " + NL + "\t\tnb_line_";
  protected final String TEXT_126 = "++; " + NL + "\t\t" + NL + "\t\t///////////////////////    \t\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();


    
    String entityname = ElementParameterParser.getValue(node, "__ENTITYNAME__").trim();
    String customEntityname = ElementParameterParser.getValue(node, "__CUSTOM_ENTITY_NAME__");
    if("CustomEntity".equals(entityname)){
		entityname = customEntityname.replaceAll("\"","");
	}
    String action = ElementParameterParser.getValue(node,"__ACTION__");

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2
        String cid = node.getUniqueName();

    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4
    			
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			
    			if("update".equals(action) && sizeColumns == 2 && "ownerid".equals(columns.get(1).getLabel())){
    				 action = "reassignOwnerID";
    			}
    			boolean hasOwnerID = false;
				
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
 if ("insert".equals(action) || "update".equals(action)) {//************
 	List<Map<String, String>> lookupMapping = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__LOOKUP_MAPPING__");
 	List activityEntities = java.util.Arrays.asList(new String[]{"activitypointer","appointment","bulkoperation","campaignactivity","campaignresponse","email","fax","incidentresolution","letter","opportunityclose","orderclose","phonecall","quoteclose","serviceappointment","task"});

    stringBuffer.append(TEXT_3);
    
    if("insert".equals(action))	{

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
    }else if("update".equals(action))	{

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
    }

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(entityname.toLowerCase());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
        			
    			for (int i = 0; i < sizeColumns; i++) {//5  			

    				IMetadataColumn column = columns.get(i);
    				
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					
					String entityMethodTemp = column.getLabel();
					String entityMethod = "";
					
					if("update".equals(action) && "ownerid".equals(column.getLabel()))	{
						hasOwnerID = true;
						continue;
					}

    stringBuffer.append(TEXT_15);
    
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
					if(!isPrimitive) { //begin

    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_18);
    
    				}
    				if(!"Id".equals(column.getLabel())){ 

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_22);
    
						if(lookupMapping.size()>0){
							for(Map<String, String> lookupMapper:lookupMapping){
								if(column.getLabel().equals(lookupMapper.get("INPUT_COLUMN"))){
								
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(lookupMapper.get("TYPE"));
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    
								}
							}
						}						
						
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
         
    					String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
        				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {//Date

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_35);
    				
						} else {//others

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_39);
    				
						}

    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    
					}else{
						String entityIdStr = entityname.toLowerCase();
						if(activityEntities.contains(entityIdStr)){
							entityIdStr = "activity";
						}

    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(entityIdStr);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(entityIdStr);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    
					}	

    
					if(!isPrimitive) {//end

    stringBuffer.append(TEXT_59);
    
					} 

    stringBuffer.append(TEXT_60);
    
				}//5	

    stringBuffer.append(TEXT_61);
    
 if ("insert".equals(action)) {//#######

    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    
	} else if ("update".equals(action)) {//#######

    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
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
    
  }//#######

    stringBuffer.append(TEXT_80);
    
	} else if ("delete".equals(action)) {//*************	

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(entityname.toLowerCase());
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    
  }//************
  if("reassignOwnerID".equals(action) || ("update".equals(action) && hasOwnerID)){

    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(entityname.toLowerCase());
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_124);
      
  }

    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    
    		}//4
    	}//3
    }//2
}//1


    return stringBuffer.toString();
  }
}
