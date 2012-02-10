package org.talend.designer.codegen.translators.data_quality;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TIntervalMatchMainJava
{
  protected static String nl;
  public static synchronized TIntervalMatchMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TIntervalMatchMainJava result = new TIntervalMatchMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "///////////////////////";
  protected final String TEXT_2 = NL + "                    ";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = "; ";
  protected final String TEXT_7 = NL + "//***" + NL + "\t\t\t\t\t";
  protected final String TEXT_8 = ".LOOKUP = util_";
  protected final String TEXT_9 = ".getLookup(";
  protected final String TEXT_10 = ");" + NL + "\t\t\t\t\tnb_line_";
  protected final String TEXT_11 = "++;" + NL + "//***";
  protected final String TEXT_12 = NL + "      \t\t\t";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = ";";
  protected final String TEXT_17 = NL + NL + "///////////////////////    \t\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//11
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//22
    
        String cid = node.getUniqueName();
            	
    	//get the input Main and Lookup connection
    	IConnection inMainCon = null;
    	IConnection inRefCon = null;   
    	List< ? extends IConnection> connsIn = node.getIncomingConnections();     
    	for (IConnection conn : connsIn) {//3
    		if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
    			inMainCon = conn;
    		}
    		else if(conn.getLineStyle().equals(EConnectionType.FLOW_REF))
    		{
    			inRefCon = conn;
    		}
        }//3

    stringBuffer.append(TEXT_1);
    
	List< ? extends IConnection> connsOut = node.getOutgoingSortedConnections();
	String firstConnName = "";
	if (connsOut!=null) {//1
		if (connsOut.size()>0) {//2
		
			IConnection conn = connsOut.get(0); //the first connection
			firstConnName = conn.getName();			
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA) && inMainCon!= null) {//3
				List<IMetadataColumn> listColumns = inMainCon.getMetadataTable().getListColumns();
				int sizeListColumns = listColumns.size();
				for (int valueN=0; valueN<sizeListColumns; valueN++) {//4
					IMetadataColumn column = listColumns.get(valueN);

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inMainCon.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_6);
    		
			}//4

    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(inMainCon.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_11);
    		
		}//3`
		
		if (connsOut.size()>1) {
			for (int i=1;i<connsOut.size();i++) {
				IConnection conn2 = connsOut.get(i);
				if ((conn2.getName().compareTo(firstConnName)!=0)&&(conn2.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
			    	for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn2.getName() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_16);
    
				 	}
				}
			}
		}
		
	}//2
	
}//1

    stringBuffer.append(TEXT_17);
    
    }//22
}//11

    return stringBuffer.toString();
  }
}
