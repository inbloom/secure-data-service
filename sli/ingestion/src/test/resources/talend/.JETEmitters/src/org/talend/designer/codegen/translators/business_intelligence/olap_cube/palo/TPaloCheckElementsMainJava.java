package org.talend.designer.codegen.translators.business_intelligence.olap_cube.palo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TPaloCheckElementsMainJava
{
  protected static String nl;
  public static synchronized TPaloCheckElementsMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloCheckElementsMainJava result = new TPaloCheckElementsMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tbRejectRow_";
  protected final String TEXT_3 = " = false;" + NL + "\tsbErrorText_";
  protected final String TEXT_4 = " = new StringBuilder();" + NL + "\tiErrorCounter_";
  protected final String TEXT_5 = " = 0;";
  protected final String TEXT_6 = NL + "\t\t\t\tif(!lsElements_";
  protected final String TEXT_7 = "[";
  protected final String TEXT_8 = "].contains(";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = ")){" + NL + "\t\t\t\t\tif(iErrorCounter_";
  protected final String TEXT_11 = ">0) sbErrorText_";
  protected final String TEXT_12 = ".append(\",\");" + NL + "\t\t\t\t\tsbErrorText_";
  protected final String TEXT_13 = ".append(pDIMs_";
  protected final String TEXT_14 = ".getDimension(";
  protected final String TEXT_15 = ").getName()+\":\"+";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = ");" + NL + "\t\t\t\t\tbRejectRow_";
  protected final String TEXT_18 = " = true;" + NL + "\t\t\t\t\tiErrorCounter_";
  protected final String TEXT_19 = "++;" + NL + "\t\t\t\t\t";
  protected final String TEXT_20 = NL + "\t \t\t\t\t\t";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = " = ";
  protected final String TEXT_23 = ";" + NL + "\t\t\t\t\t\tbRejectRow_";
  protected final String TEXT_24 = " = false;" + NL + "\t\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_25 = NL + " \t\t\t\t\t\tthrow(new org.talend.jpalo.paloexception(pDIMs_";
  protected final String TEXT_26 = ".getDimension(";
  protected final String TEXT_27 = ").getName()+\" does not contain \"+";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = "));" + NL + "\t\t\t\t\t";
  protected final String TEXT_30 = NL + NL + "\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_31 = NL + "\t" + NL;
  protected final String TEXT_32 = NL + "\t\t\t\tif(!bRejectRow_";
  protected final String TEXT_33 = "){" + NL + "\t\t\t\t\t";
  protected final String TEXT_34 = " = new ";
  protected final String TEXT_35 = "Struct();" + NL;
  protected final String TEXT_36 = NL + "\t\t\t\t\t";
  protected final String TEXT_37 = ".";
  protected final String TEXT_38 = "=";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = ";" + NL + "\t\t\t\t\t";
  protected final String TEXT_41 = NL + "\t\t\t\t}else{" + NL + "\t\t\t\t\t";
  protected final String TEXT_42 = " = null;" + NL + "\t\t\t\t}";
  protected final String TEXT_43 = NL + "\t\t\t\tif(bRejectRow_";
  protected final String TEXT_44 = "){" + NL + "\t\t\t\t\t";
  protected final String TEXT_45 = " = new ";
  protected final String TEXT_46 = "Struct();";
  protected final String TEXT_47 = NL + "\t\t\t\t\t";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = "=";
  protected final String TEXT_50 = ".";
  protected final String TEXT_51 = ";" + NL + "\t\t\t\t\t";
  protected final String TEXT_52 = NL + "\t\t\t\t";
  protected final String TEXT_53 = ".errorMessage=sbErrorText_";
  protected final String TEXT_54 = ".toString();";
  protected final String TEXT_55 = NL + "\t\t\t\t}else{" + NL + "\t\t\t\t\t";
  protected final String TEXT_56 = " = null;" + NL + "\t\t\t\t}";
  protected final String TEXT_57 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String sCommitSize= ElementParameterParser.getValue(node,"__COMMITSIZE__");
	String sMeasureColumn= ElementParameterParser.getValue(node,"__MEASURE_COLUMN__");


	String strInputConnectionName="";
	List< ? extends IConnection> conns = node.getIncomingConnections();
 	if(conns!=null){
		if (conns.size()>0){
                IConnection conn =conns.get(0);
		    strInputConnectionName=conn.getName();
		}
	}

	String strOutputConnectionName="";
	conns = node.getOutgoingConnections();
 	if(conns!=null){
		if (conns.size()>0){
                IConnection conn =conns.get(0);
		    strOutputConnectionName=conn.getName();
		}
	}

	String rejectConnName = "";
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


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
	

	if(strInputConnectionName.length()>0){	
		List<Map<String, String>> checkElements = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ELEMENTS_TO_CHECK__");
		int iNbOfElements = checkElements.size();
		StringBuilder sbX=new StringBuilder();
		int iCount=0;
		for(int i=0; i<iNbOfElements; i++){
			Map<String, String> mpElement = checkElements.get(i);
			String strElement = mpElement.get("SCHEMA_COLUMN");
			String strElementType = mpElement.get("ELEMENT_TYPE");
			String strDefaultElement = mpElement.get("DEFAULT_ELEMENT");
			if(!strElementType.equals("ELEMENT_MEASURE")){
				
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(iCount);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(strElement);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(iCount);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(strElement);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    
					if(ElementParameterParser.getObjectValue(node, "__ERROR_HANDLING__").equals("USE_DEFAULT")){
					
    stringBuffer.append(TEXT_20);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(strElement);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(strDefaultElement );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    
					}

					if(ElementParameterParser.getObjectValue(node, "__ERROR_HANDLING__").equals("DIE_ON_MISSING_ELEMENT")){
					
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(iCount);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(strElement);
    stringBuffer.append(TEXT_29);
    
					}
					
    stringBuffer.append(TEXT_30);
    
				iCount++;
			
			}
		}
	}


    stringBuffer.append(TEXT_31);
    
	if(strInputConnectionName.length()>0 && strOutputConnectionName.length()>0){
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
			IMetadataTable metadata = metadatas.get(0);
			if (metadata!=null) {

    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(strOutputConnectionName );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(strOutputConnectionName );
    stringBuffer.append(TEXT_35);
    
				List<IMetadataColumn> columns = metadata.getListColumns();
				int sizeColumns = columns.size();
		    		for (int i = 0; i < sizeColumns; i++) {
		    			IMetadataColumn column = columns.get(i);
					
    stringBuffer.append(TEXT_36);
    stringBuffer.append(strOutputConnectionName);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_40);
    
				}
			}

    stringBuffer.append(TEXT_41);
    stringBuffer.append(strOutputConnectionName );
    stringBuffer.append(TEXT_42);
    

		}
	}

	if(strInputConnectionName.length()>0 && rejectConnName.length()>0){
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
			IMetadataTable metadata = metadatas.get(0);
			if (metadata!=null) {

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_46);
    
				List<IMetadataColumn> columns = metadata.getListColumns();
				int sizeColumns = columns.size();
		    		for (int i = 0; i < sizeColumns; i++) {
		    			IMetadataColumn column = columns.get(i);
					
    stringBuffer.append(TEXT_47);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_49);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_51);
    
				}

    stringBuffer.append(TEXT_52);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    
			}

    stringBuffer.append(TEXT_55);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_56);
    
		}
	}

    stringBuffer.append(TEXT_57);
    return stringBuffer.toString();
  }
}
