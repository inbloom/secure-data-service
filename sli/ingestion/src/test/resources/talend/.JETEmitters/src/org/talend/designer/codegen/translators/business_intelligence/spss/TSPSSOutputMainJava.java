package org.talend.designer.codegen.translators.business_intelligence.spss;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TSPSSOutputMainJava
{
  protected static String nl;
  public static synchronized TSPSSOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSPSSOutputMainJava result = new TSPSSOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t";
  protected final String TEXT_3 = "_spR.clearRecord();";
  protected final String TEXT_4 = NL + "\t\t\t\t\t";
  protected final String TEXT_5 = "_spR.setString(";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = NL + "\t\t\t\t\t";
  protected final String TEXT_9 = "_spR.setDate(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL + "\t\t\t\t\tif(null==";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = ")";
  protected final String TEXT_15 = "_spR.setDouble(null);" + NL + "\t\t\t\t\telse ";
  protected final String TEXT_16 = "_spR.setDouble(new Double(";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = "));";
  protected final String TEXT_19 = NL + "\t\t\t\t\t";
  protected final String TEXT_20 = "_spR.setDouble(new Double(";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = "));";
  protected final String TEXT_23 = NL + "\t\t\t";
  protected final String TEXT_24 = "_sf.writeLine(";
  protected final String TEXT_25 = "_spR); ";
  protected final String TEXT_26 = NL + NL;
  protected final String TEXT_27 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String writeType = ElementParameterParser.getValue(node, "__WRITE_TYPE__");

String inputConnName = null;
List< ? extends IConnection> conns = node.getIncomingConnections();
if (conns!=null) {
	if (conns.size()>0) {
		for (int i=0;i<conns.size();i++) {
			IConnection connTemp = conns.get(i);
			if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				inputConnName = connTemp.getName();
				break;
			}
		}
	}
}
if (inputConnName != null){
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null && conns.size()>0) {
			List<IMetadataColumn> columns = metadata.getListColumns();
			int sizeColumns = columns.size();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
	    		for (int i = 0; i < sizeColumns; i++) {
	    			IMetadataColumn column = columns.get(i);
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				if(javaType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inputConnName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_7);
     
				}else if(javaType == JavaTypesManager.DATE){ 

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(inputConnName);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_11);
    
				}else if(javaType == JavaTypesManager.BOOLEAN){
				}else if(javaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_12);
    stringBuffer.append(inputConnName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(inputConnName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_18);
     
				}else if(javaType == JavaTypesManager.DOUBLE || javaType == JavaTypesManager.INTEGER || javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.FLOAT){

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(inputConnName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_22);
    	
				}

			}

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
		}
	}
}

    stringBuffer.append(TEXT_26);
    stringBuffer.append(TEXT_27);
    return stringBuffer.toString();
  }
}
