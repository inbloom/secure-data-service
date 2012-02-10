package org.talend.designer.codegen.translators.business.vtigercrm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TVtigerCRMOutputMainJava
{
  protected static String nl;
  public static synchronized TVtigerCRMOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TVtigerCRMOutputMainJava result = new TVtigerCRMOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "                org.talend.vtiger.module.outlook.Clndrdetail clndrdetail_";
  protected final String TEXT_2 = " = new org.talend.vtiger.module.outlook.Clndrdetail();";
  protected final String TEXT_3 = NL + "                org.talend.vtiger.module.outlook.Contactdetail contactdetail_";
  protected final String TEXT_4 = " = new org.talend.vtiger.module.outlook.Contactdetail();";
  protected final String TEXT_5 = NL + "                org.talend.vtiger.module.outlook.Taskdetail taskdetail_";
  protected final String TEXT_6 = " = new org.talend.vtiger.module.outlook.Taskdetail();";
  protected final String TEXT_7 = NL + "                String contactid_";
  protected final String TEXT_8 = " = null;" + NL + "                org.talend.vtiger.module.outlook.Emailmsgdetail emailmsgdetail_";
  protected final String TEXT_9 = " = new org.talend.vtiger.module.outlook.Emailmsgdetail();";
  protected final String TEXT_10 = NL + "                    clndrdetail_";
  protected final String TEXT_11 = ".set";
  protected final String TEXT_12 = "(";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = ");            ";
  protected final String TEXT_15 = NL + "                    contactdetail_";
  protected final String TEXT_16 = ".set";
  protected final String TEXT_17 = "(";
  protected final String TEXT_18 = ".";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + "                    taskdetail_";
  protected final String TEXT_21 = ".set";
  protected final String TEXT_22 = "(";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = NL + "                        contactid_";
  protected final String TEXT_26 = " = ";
  protected final String TEXT_27 = ".";
  protected final String TEXT_28 = ";";
  protected final String TEXT_29 = NL + "                        emailmsgdetail_";
  protected final String TEXT_30 = ".set";
  protected final String TEXT_31 = "(";
  protected final String TEXT_32 = ".";
  protected final String TEXT_33 = ");";
  protected final String TEXT_34 = NL + "                clndrDetais_";
  protected final String TEXT_35 = ".add(clndrdetail_";
  protected final String TEXT_36 = ");";
  protected final String TEXT_37 = NL + "                contactdetails_";
  protected final String TEXT_38 = ".add(contactdetail_";
  protected final String TEXT_39 = ");";
  protected final String TEXT_40 = NL + "                taskdetails_";
  protected final String TEXT_41 = ".add(taskdetail_";
  protected final String TEXT_42 = ");";
  protected final String TEXT_43 = NL + "                vtigerManager_";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = "(contactid_";
  protected final String TEXT_46 = ",emailmsgdetail_";
  protected final String TEXT_47 = ");";
  protected final String TEXT_48 = NL + "                    vtigerManager_";
  protected final String TEXT_49 = ".";
  protected final String TEXT_50 = "(";
  protected final String TEXT_51 = ");";
  protected final String TEXT_52 = NL + "                    vtigerManager_";
  protected final String TEXT_53 = ".";
  protected final String TEXT_54 = "();";
  protected final String TEXT_55 = NL + "    \t\t\t\tjava.util.Map valueMap_";
  protected final String TEXT_56 = " = new java.util.HashMap();" + NL + "    \t\t\t\t";
  protected final String TEXT_57 = NL + "        \t\t\t\tif(";
  protected final String TEXT_58 = ".";
  protected final String TEXT_59 = " != null){" + NL + "\t\t\t\t\t\t\tvalueMap_";
  protected final String TEXT_60 = ".put(\"";
  protected final String TEXT_61 = "\",FormatterUtils.format_Date(";
  protected final String TEXT_62 = ".";
  protected final String TEXT_63 = ", ";
  protected final String TEXT_64 = ") );" + NL + "\t\t\t\t\t\t}" + NL + "        \t\t\t\t";
  protected final String TEXT_65 = NL + "        \t\t\t\tif(";
  protected final String TEXT_66 = ".";
  protected final String TEXT_67 = " != null){" + NL + "\t\t\t\t\t\t\tvalueMap_";
  protected final String TEXT_68 = ".put(\"";
  protected final String TEXT_69 = "\",java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_70 = ".";
  protected final String TEXT_71 = ")).toString() );" + NL + "\t\t\t\t\t\t}" + NL + "        \t\t\t\t";
  protected final String TEXT_72 = NL + "        \t\t\t\t";
  protected final String TEXT_73 = NL + "\t\t    \t\t\tif(";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = " != null) { " + NL + "\t\t    \t\t\t";
  protected final String TEXT_76 = NL + "\t\t    \t\t\tif(!\"\".equals(String.valueOf(";
  protected final String TEXT_77 = ".";
  protected final String TEXT_78 = "))) { " + NL + "\t\t    \t\t\t";
  protected final String TEXT_79 = NL + "\t\t\t\t\t\t\tvalueMap_";
  protected final String TEXT_80 = ".put(\"";
  protected final String TEXT_81 = "\",String.valueOf(";
  protected final String TEXT_82 = ".";
  protected final String TEXT_83 = ") );" + NL + "\t\t\t\t\t\t}" + NL + "        \t\t\t\t";
  protected final String TEXT_84 = NL + "\t\t\t\t\tObject result_";
  protected final String TEXT_85 = " = null;" + NL + "\t\t\t\t\t";
  protected final String TEXT_86 = NL + "\t\t\t\t\tresult_";
  protected final String TEXT_87 = " = vtMgr_";
  protected final String TEXT_88 = ".doCreate(\"";
  protected final String TEXT_89 = "\", valueMap_";
  protected final String TEXT_90 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_91 = NL + "\t\t\t\t\tjava.util.Map elementMap_";
  protected final String TEXT_92 = " = new java.util.HashMap();" + NL + "        \t\t\telementMap_";
  protected final String TEXT_93 = ".put(\"element\", vtMgr_";
  protected final String TEXT_94 = ".toJSONString(valueMap_";
  protected final String TEXT_95 = "));" + NL + "        \t\t\tresult_";
  protected final String TEXT_96 = " = vtMgr_";
  protected final String TEXT_97 = ".doInvoke(\"update\", elementMap_";
  protected final String TEXT_98 = ", \"POST\");" + NL + "\t\t\t\t\t";
  protected final String TEXT_99 = NL + "\t\t\t\t\tresult_";
  protected final String TEXT_100 = " = vtMgr_";
  protected final String TEXT_101 = ".doInvoke(\"delete\", valueMap_";
  protected final String TEXT_102 = ", \"POST\");" + NL + "\t\t\t\t\t";
  protected final String TEXT_103 = NL + "\t\t\t\t\tif (vtMgr_";
  protected final String TEXT_104 = ".hasError(result_";
  protected final String TEXT_105 = ")) {" + NL + "\t\t\t\t\t";
  protected final String TEXT_106 = NL + "\t                \tthrow new RuntimeException(vtMgr_";
  protected final String TEXT_107 = ".lastError()==null? \"No response from vtigerCRM\": vtMgr_";
  protected final String TEXT_108 = ".lastError().toString());" + NL + "\t                ";
  protected final String TEXT_109 = NL + "                        System.err.println(vtMgr_";
  protected final String TEXT_110 = ".lastError()==null? \"No response from vtigerCRM\": vtMgr_";
  protected final String TEXT_111 = ".lastError().toString());";
  protected final String TEXT_112 = NL + "                    } " + NL + "\t\t\t\t\t";
  protected final String TEXT_113 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String version_selection = ElementParameterParser.getValue(node, "__VERSION_SELECTION__");
if(version_selection.equals("VTIGER_50")){

    String method = ElementParameterParser.getValue(node, "__METHODNAME__");
    String incomingConnName = null;
    List<? extends IConnection> conns = node.getIncomingConnections();
    if(conns != null && conns.size() > 0) {
        IConnection conn = conns.get(0);
        incomingConnName = conn.getName();
    }
    List<IMetadataColumn> metadataColumns = null;
    List<IMetadataTable> metadataTables = node.getMetadataList();
    if(metadataTables != null && metadataTables.size() > 0) {
        IMetadataTable metadataTable = metadataTables.get(0);
        if(metadataTable != null) {
            metadataColumns = metadataTable.getListColumns();
            StringBuilder paramString = null;
            if(("addClndr").equals(method) || ("updateClndr").equals(method)) {
                
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
            } else if(("addContacts").equals(method) || ("updateContacts").equals(method)) {
                
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
            } else if(("addTasks").equals(method) || ("updateTasks").equals(method)) {
                
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
            } else if (("addMessageToContact").equals(method)) {
                
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
            } else {
                paramString = new StringBuilder();
            }
            for(IMetadataColumn metadataColumn : metadataColumns) {
                if(("addClndr").equals(method) || ("updateClndr").equals(method)) {
                    
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(metadataColumn.getLabel().substring(0,1).toUpperCase());
    stringBuffer.append(metadataColumn.getLabel().substring(1));
    stringBuffer.append(TEXT_12);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_14);
    
                } else if(("addContacts").equals(method) || ("updateContacts").equals(method)) {
                    
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(metadataColumn.getLabel().substring(0,1).toUpperCase());
    stringBuffer.append(metadataColumn.getLabel().substring(1));
    stringBuffer.append(TEXT_17);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_19);
    
                } else if(("addTasks").equals(method) || ("updateTasks").equals(method)) {
                    
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(metadataColumn.getLabel().substring(0,1).toUpperCase());
    stringBuffer.append(metadataColumn.getLabel().substring(1));
    stringBuffer.append(TEXT_22);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_24);
    
                } else if(("addMessageToContact").equals(method)) {
                    if(("contactid").equals(metadataColumn.getLabel())) {
                        
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_28);
    
                    } else {
                        
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(metadataColumn.getLabel().substring(0,1).toUpperCase());
    stringBuffer.append(metadataColumn.getLabel().substring(1));
    stringBuffer.append(TEXT_31);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_33);
    
                    }
                } else {
                    paramString.append(incomingConnName + "." + metadataColumn.getLabel() + ", ");
                }
            }
            if(("addClndr").equals(method) || ("updateClndr").equals(method)) {
                
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
            } else if(("addContacts").equals(method) || ("updateContacts").equals(method)) {
                
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    
            } else if(("addTasks").equals(method) || ("updateTasks").equals(method)) {
                
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    
            } else if(("addMessageToContact").equals(method)) {
                
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    
            } else {
                if(paramString.toString().length() > 0) {            
                    
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(paramString.toString().substring(0,paramString.toString().lastIndexOf(",")));
    stringBuffer.append(TEXT_51);
    
                } else {
                    
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_54);
    
                }
            }  
        }
    }
//*****************************************************version 5.1 start**************************************
}else{
	String action  = ElementParameterParser.getValue(node, "__ACTION__");
	String moduleName  = ElementParameterParser.getValue(node, "__MODULENAME_510__");
	String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {//1
        IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {//2
        	List< ? extends IConnection> conns = node.getIncomingConnections();
        	for (IConnection conn : conns) {//3
        		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4
        			List<IMetadataColumn> columns = metadata.getListColumns();
    				int sizeColumns = columns.size();
    				
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    
    				for (int i = 0; i < sizeColumns; i++) {//5  			
        				IMetadataColumn column = columns.get(i);
        				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
    					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
        				String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
        				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {//Date
        				
    stringBuffer.append(TEXT_57);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_63);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_64);
    
        				} else if (javaType == JavaTypesManager.BYTE_ARRAY) {//byte[]
        				
    stringBuffer.append(TEXT_65);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_71);
    
        				} else {//others
        				
    stringBuffer.append(TEXT_72);
    if(!isPrimitive) {
    stringBuffer.append(TEXT_73);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_75);
    }else{
    stringBuffer.append(TEXT_76);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_78);
    }
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_83);
    
        				}
					}//5
					
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    
					if ("insert".equals(action)){
					
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(moduleName);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    
					}else if("update".equals(action)){
					
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
    
					}else if("delete".equals(action)){
					
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    						
					}
					
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    
	                   if (("true").equals(dieOnError)) {
	                
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    
	                 	} else {
	                
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    
                    	}
                    
    stringBuffer.append(TEXT_112);
    
        		}//4
        	}//3
        }//2
    }//1

}

    stringBuffer.append(TEXT_113);
    return stringBuffer.toString();
  }
}
