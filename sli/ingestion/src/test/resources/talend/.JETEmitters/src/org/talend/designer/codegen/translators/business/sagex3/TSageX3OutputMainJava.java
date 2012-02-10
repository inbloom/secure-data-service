package org.talend.designer.codegen.translators.business.sagex3;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSageX3OutputMainJava
{
  protected static String nl;
  public static synchronized TSageX3OutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSageX3OutputMainJava result = new TSageX3OutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t\torg.talend.sage.Parameter sageParam_";
  protected final String TEXT_2 = " = client_";
  protected final String TEXT_3 = ".addParameter(";
  protected final String TEXT_4 = ");\t\t\t";
  protected final String TEXT_5 = NL + "    \t\t\tjava.util.Map<String,String> keyValues_";
  protected final String TEXT_6 = " = new java.util.HashMap<String,String>();";
  protected final String TEXT_7 = NL + "\t\t\t\t\t\t\tObject value_";
  protected final String TEXT_8 = "_";
  protected final String TEXT_9 = " = FormatterUtils.format_Date(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ", ";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL + "\t\t\t\t\t\t\tObject value_";
  protected final String TEXT_14 = "_";
  protected final String TEXT_15 = " = ";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = ";";
  protected final String TEXT_18 = NL + "\t\t\t\t\t\t\tObject value_";
  protected final String TEXT_19 = "_";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = ";";
  protected final String TEXT_23 = NL + "\t\t\t\t\t\t\tObject value_";
  protected final String TEXT_24 = "_";
  protected final String TEXT_25 = " = String.valueOf(";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = ");";
  protected final String TEXT_28 = NL + "\t\t\t\t\t\t\tkeyValues_";
  protected final String TEXT_29 = ".put(";
  protected final String TEXT_30 = ",String.valueOf(value_";
  protected final String TEXT_31 = "_";
  protected final String TEXT_32 = "));\t\t\t" + NL + "    \t\t\t\t\t";
  protected final String TEXT_33 = NL + "    \t\t\t\tsageParam_";
  protected final String TEXT_34 = ".setValue(";
  protected final String TEXT_35 = ",";
  protected final String TEXT_36 = ",value_";
  protected final String TEXT_37 = "_";
  protected final String TEXT_38 = ");";
  protected final String TEXT_39 = NL + NL + "\t\t\t\t\t//System.out.println(sageParam_";
  protected final String TEXT_40 = ".toString());";
  protected final String TEXT_41 = NL + "\t\t\t\t\tclient_";
  protected final String TEXT_42 = ".insert(";
  protected final String TEXT_43 = ",sageParam_";
  protected final String TEXT_44 = ".toString());";
  protected final String TEXT_45 = NL + "\t\t\t\t\tclient_";
  protected final String TEXT_46 = ".update(";
  protected final String TEXT_47 = ",client_";
  protected final String TEXT_48 = ".createCAdxKeyValues(keyValues_";
  protected final String TEXT_49 = "),sageParam_";
  protected final String TEXT_50 = ".toString());";
  protected final String TEXT_51 = NL + "\t\t\t\t\tclient_";
  protected final String TEXT_52 = ".delete(";
  protected final String TEXT_53 = ",client_";
  protected final String TEXT_54 = ".createCAdxKeyValues(keyValues_";
  protected final String TEXT_55 = "));";
  protected final String TEXT_56 = "\t" + NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2

    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4
			
				String action = ElementParameterParser.getValue(node,"__ACTION__");
			    String publicName = ElementParameterParser.getValue(node,"__PUBLICATION_NAME__");
			    
			    List<Map<String, String>> mappingList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MAPPING_OUTPUT__");
    			Map<String,Map<String,String>> paramMapping = new HashMap<String,Map<String,String>>();
    			Map<String,String> fldMapping;
    			for(Map<String,String> outputMapping:mappingList){	
    				fldMapping = new HashMap<String,String>();
    				fldMapping.put("key",outputMapping.get("KEY"));
    				fldMapping.put("gID",outputMapping.get("GROUP_TABLE_ID"));
    				fldMapping.put("fID",outputMapping.get("PARAMETER_NAME"));
    				paramMapping.put(outputMapping.get("SCHEMA_COLUMN"),fldMapping);
    			}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(publicName);
    stringBuffer.append(TEXT_4);
    
    			if("update".equals(action) || "delete".equals(action)){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
    			}
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			for (int i = 0; i < sizeColumns; i++) {//5  	
    					
    				IMetadataColumn column = columns.get(i);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
    				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
    				String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    				
    				Map<String,String> fieldMapping = paramMapping.get(column.getLabel());

    				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {//Date

    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_12);
    					
					}else if(javaType == JavaTypesManager.LIST){

    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_17);
    					
					}else if(javaType == JavaTypesManager.OBJECT){

    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_22);
    
					} else {//others

    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_27);
    				
					}

    				if("update".equals(action) || "delete".equals(action)){
    					if("true".equals(fieldMapping.get("key"))){
    					
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(fieldMapping.get("fID"));
    stringBuffer.append(TEXT_30);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
    					}
    				}
    				if(!"delete".equals(action)){

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(fieldMapping.get("gID"));
    stringBuffer.append(TEXT_35);
    stringBuffer.append(fieldMapping.get("fID"));
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    
					}
				}

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    			
				if("insert".equals(action)){

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(publicName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    					
				}else if("update".equals(action)){

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(publicName);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    
				}else if("delete".equals(action)){

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(publicName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    					
				}

    stringBuffer.append(TEXT_56);
    
			}
		}
	}
}	

    return stringBuffer.toString();
  }
}
