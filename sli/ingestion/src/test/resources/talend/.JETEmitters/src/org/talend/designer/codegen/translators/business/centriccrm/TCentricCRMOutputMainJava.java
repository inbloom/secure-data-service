package org.talend.designer.codegen.translators.business.centriccrm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import org.talend.core.model.process.IConnectionCategory;

public class TCentricCRMOutputMainJava
{
  protected static String nl;
  public static synchronized TCentricCRMOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TCentricCRMOutputMainJava result = new TCentricCRMOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " ";
  protected final String TEXT_2 = NL + "    \t\t";
  protected final String TEXT_3 = ".addField(\"";
  protected final String TEXT_4 = "\",String.valueOf(";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = "));";
  protected final String TEXT_7 = NL + "   \t\t\t";
  protected final String TEXT_8 = ".addField(\"";
  protected final String TEXT_9 = "\",";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = "\t" + NL + "\t\t crm";
  protected final String TEXT_13 = ".save(";
  protected final String TEXT_14 = ");" + NL + "  \t\t boolean result";
  protected final String TEXT_15 = " = crm";
  protected final String TEXT_16 = ".commit();" + NL + "  \t\t System.out.println(crm";
  protected final String TEXT_17 = ".getLastResponse());" + NL + "   \t\t nb_line_";
  protected final String TEXT_18 = "++;  " + NL + "       ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {  
        List< ? extends IConnection> conns = node.getIncomingConnections(); 
        String cid = node.getUniqueName();
    	String modulename = ElementParameterParser.getValue(node, "__MODULENAME__");
    	

    stringBuffer.append(TEXT_1);
        	
    	if(conns!=null){
    		if (conns.size()>0){
       		IConnection conn =conns.get(0);
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			for (int i = 0; i < sizeColumns; i++) {
    			IMetadataColumn column = columns.get(i);
    			String coluLabel=column.getLabel();
    			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
    			if(javaType == JavaTypesManager.DATE)
				{
    			
   
    stringBuffer.append(TEXT_2);
    stringBuffer.append(modulename);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_6);
     			}else{
   
    stringBuffer.append(TEXT_7);
    stringBuffer.append(modulename);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(coluLabel);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_11);
    
   						 }
    			
    				}
    				
    			}
    		}
    	}		
	
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(modulename);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
     }
 }
    return stringBuffer.toString();
  }
}
