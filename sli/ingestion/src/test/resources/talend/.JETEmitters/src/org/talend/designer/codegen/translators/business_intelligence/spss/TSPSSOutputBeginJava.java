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

public class TSPSSOutputBeginJava
{
  protected static String nl;
  public static synchronized TSPSSOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSPSSOutputBeginJava result = new TSPSSOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\torg.talend.jspss.spss ";
  protected final String TEXT_4 = "_sp = new org.talend.jspss.spss();";
  protected final String TEXT_5 = NL + "\torg.talend.jspss.spssfile ";
  protected final String TEXT_6 = "_sf = ";
  protected final String TEXT_7 = "_sp.openFile(";
  protected final String TEXT_8 = ", org.talend.jspss.spssfile.SPSS_WRITE);" + NL + "\torg.talend.jspss.spssvariables ";
  protected final String TEXT_9 = "_spVars = ";
  protected final String TEXT_10 = "_sf.getVariables();";
  protected final String TEXT_11 = NL + "\t\t\t\torg.talend.jspss.spssvariable spVar_";
  protected final String TEXT_12 = " = new org.talend.jspss.spssvariable(\"";
  protected final String TEXT_13 = "\");";
  protected final String TEXT_14 = NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_15 = ".setType(org.talend.jspss.spssvariables.SPSS_STRING);" + NL + " \t\t\t\t\tspVar_";
  protected final String TEXT_16 = ".setFormat(1);" + NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_17 = ".setWidth(";
  protected final String TEXT_18 = ");" + NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_19 = ".setDecimals(";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_22 = ".setType(org.talend.jspss.spssvariables.SPSS_NUMERIC);" + NL + " \t\t\t\t\tspVar_";
  protected final String TEXT_23 = ".setFormat(22);" + NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_24 = ".setDecimals(17);" + NL + " \t\t\t\t\tspVar_";
  protected final String TEXT_25 = ".setPrecision(0);" + NL;
  protected final String TEXT_26 = NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_27 = ".setWidth(";
  protected final String TEXT_28 = ");" + NL + " \t\t\t\t\tspVar_";
  protected final String TEXT_29 = ".setFormat(5);" + NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_30 = ".setDecimals(";
  protected final String TEXT_31 = "-";
  protected final String TEXT_32 = ");" + NL + " \t\t\t\t\tspVar_";
  protected final String TEXT_33 = ".setPrecision(";
  protected final String TEXT_34 = ");";
  protected final String TEXT_35 = NL + "\t\t\t\t\tspVar_";
  protected final String TEXT_36 = ".setLabel(\"";
  protected final String TEXT_37 = "\");";
  protected final String TEXT_38 = NL + "\t\t\t\t";
  protected final String TEXT_39 = "_spVars.addVariable(spVar_";
  protected final String TEXT_40 = ");";
  protected final String TEXT_41 = NL + "\t\t\t";
  protected final String TEXT_42 = "_spVars.writeVariables(); ";
  protected final String TEXT_43 = NL;
  protected final String TEXT_44 = NL + "\torg.talend.jspss.spssfile ";
  protected final String TEXT_45 = "_sf = ";
  protected final String TEXT_46 = "_sp.openFile(";
  protected final String TEXT_47 = ", org.talend.jspss.spssfile.SPSS_APPEND);" + NL + "\torg.talend.jspss.spssvariables ";
  protected final String TEXT_48 = "_spVars = ";
  protected final String TEXT_49 = "_sf.getVariables();";
  protected final String TEXT_50 = NL + "\t//";
  protected final String TEXT_51 = NL + "\t//";
  protected final String TEXT_52 = NL + "\torg.talend.jspss.spssrecord ";
  protected final String TEXT_53 = "_spR = new org.talend.jspss.spssrecord(";
  protected final String TEXT_54 = "_spVars, false);";
  protected final String TEXT_55 = NL;
  protected final String TEXT_56 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String filename = ElementParameterParser.getValue(node, "__FILENAME__");
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

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
	if(("write").equals(writeType)){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    	
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null && conns.size()>0) {
			List<IMetadataColumn> columns = metadata.getListColumns();
			int sizeColumns = columns.size();
	    		for (int i = 0; i < sizeColumns; i++) {
	    			IMetadataColumn column = columns.get(i);
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());

    stringBuffer.append(TEXT_11);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_13);
    				
				if(javaType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_14);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_18);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_20);
     
				}else if(javaType == JavaTypesManager.DATE){

    stringBuffer.append(TEXT_21);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_25);
     
				}else if(javaType == JavaTypesManager.BOOLEAN){
				}else if(javaType == JavaTypesManager.DOUBLE || javaType == JavaTypesManager.INTEGER || javaType == JavaTypesManager.LONG || javaType == JavaTypesManager.SHORT || javaType == JavaTypesManager.BIGDECIMAL || javaType == JavaTypesManager.FLOAT){

    stringBuffer.append(TEXT_26);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_28);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getPrecision());
    stringBuffer.append(TEXT_32);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column.getPrecision() );
    stringBuffer.append(TEXT_34);
    	
				}
				if(column.getComment()!=null || column.getComment().length()>0){

    stringBuffer.append(TEXT_35);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getComment());
    stringBuffer.append(TEXT_37);
    
				}

    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_40);
    
			}

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    
		}
	}

    stringBuffer.append(TEXT_43);
    
	}else{

    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    
	}

    stringBuffer.append(TEXT_50);
    stringBuffer.append(inputConnName);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(writeType);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    
}

    stringBuffer.append(TEXT_55);
    stringBuffer.append(TEXT_56);
    return stringBuffer.toString();
  }
}
