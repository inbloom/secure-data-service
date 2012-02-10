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

public class TPaloOutputMultiMainJava
{
  protected static String nl;
  public static synchronized TPaloOutputMultiMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloOutputMultiMainJava result = new TPaloOutputMultiMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "       ";
  protected final String TEXT_3 = NL + "            ";
  protected final String TEXT_4 = " = null;            ";
  protected final String TEXT_5 = NL + "\t\t\t\t\tstrArrToAdd_";
  protected final String TEXT_6 = "[";
  protected final String TEXT_7 = "] = String.valueOf(";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_10 = NL + "\t\t\tplDT_";
  protected final String TEXT_11 = ".addToValueList(strArrToAdd_";
  protected final String TEXT_12 = ", ";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = ");" + NL + "\t\t\tiNbOfProcessedRows_";
  protected final String TEXT_15 = "++;" + NL + "" + NL + "\t\t\tif(iNbOfProcessedRows_";
  protected final String TEXT_16 = ">=";
  protected final String TEXT_17 = "){" + NL + "\t            \t\ttry{" + NL + "\t\t\t\t\tplDT_";
  protected final String TEXT_18 = ".setData(pCB_";
  protected final String TEXT_19 = ", org.talend.jpalo.palodata.";
  protected final String TEXT_20 = ", ";
  protected final String TEXT_21 = ",  ";
  protected final String TEXT_22 = " );" + NL + "\t\t\t\t}catch(Exception plE_";
  protected final String TEXT_23 = "){" + NL + "\t\t\t\t\t";
  protected final String TEXT_24 = NL + "\t\t\t\t\t\tthrow(plE_";
  protected final String TEXT_25 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_26 = NL + "\t\t\t\t\t\tSystem.err.print(plE_";
  protected final String TEXT_27 = ".getMessage());" + NL + "\t\t\t\t\t";
  protected final String TEXT_28 = NL + "\t\t\t\t}" + NL + "\t\t\t\tplDT_";
  protected final String TEXT_29 = ".cleanDataSetList();" + NL + "\t\t\t\tiNbOfProcessedRows_";
  protected final String TEXT_30 = "=0;" + NL + "\t\t\t}";
  protected final String TEXT_31 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String sCommitSize= ElementParameterParser.getValue(node,"__COMMITSIZE__");
	String sMeasureColumn= ElementParameterParser.getValue(node,"__MEASURE_COLUMN__");

	String sSplashMode= ElementParameterParser.getValue(node,"__SPLASH_MODE__");

	boolean bUseEventprocessor = "true".equals(ElementParameterParser.getValue(node,"__EVENTPROCESSOR__"));
	boolean bAddValues = "true".equals(ElementParameterParser.getValue(node,"__ADDVALUES__"));
	boolean bDieOnError = "true".equals(ElementParameterParser.getValue(node,"__DIE_ON_ERROR__"));


	String strInputConnectionName="";
	List< ? extends IConnection> conns = node.getIncomingConnections();
 	if(conns!=null){
		if (conns.size()>0){
                IConnection conn =conns.get(0);
		    strInputConnectionName=conn.getName();
		}
	}
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
    		for(IConnection conn : outgoingConns) {
	        if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            
    stringBuffer.append(TEXT_2);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_4);
          
        }
    }


	if(strInputConnectionName.length()>0){
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
			IMetadataTable metadata = metadatas.get(0);
			if (metadata!=null) {
				List<IMetadataColumn> columns = metadata.getListColumns();
				int sizeColumns = columns.size();
				if(sizeColumns > 1) 
				    sizeColumns = sizeColumns-1;
				StringBuilder sbX=new StringBuilder();
		    		for (int i = 0; i < sizeColumns; i++) {
		    			IMetadataColumn column = columns.get(i);
		      		//if(!column.getLabel().equals(sMeasureColumn)){
					
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_9);
    
					//}
				}

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(strInputConnectionName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(sMeasureColumn);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sCommitSize);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(sSplashMode);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(bAddValues);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(bUseEventprocessor);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    
					if(bDieOnError){
					
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
					}else{
					
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    
					}
					
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    
			}
		}
	}

    stringBuffer.append(TEXT_31);
    return stringBuffer.toString();
  }
}
