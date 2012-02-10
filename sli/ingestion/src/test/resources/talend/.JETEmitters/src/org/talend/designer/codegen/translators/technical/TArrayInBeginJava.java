package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TArrayInBeginJava
{
  protected static String nl;
  public static synchronized TArrayInBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TArrayInBeginJava result = new TArrayInBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "        " + NL + "        int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "        java.util.List<";
  protected final String TEXT_4 = "Struct";
  protected final String TEXT_5 = "> list_";
  protected final String TEXT_6 = " = (java.util.List<";
  protected final String TEXT_7 = "Struct";
  protected final String TEXT_8 = ">)globalMap.get(\"";
  protected final String TEXT_9 = "\");" + NL + "        if(list_";
  protected final String TEXT_10 = " == null) {" + NL + "            list_";
  protected final String TEXT_11 = " = new java.util.ArrayList<";
  protected final String TEXT_12 = "Struct";
  protected final String TEXT_13 = ">();" + NL + "        }        " + NL + "        for(";
  protected final String TEXT_14 = "Struct";
  protected final String TEXT_15 = " row_";
  protected final String TEXT_16 = " : list_";
  protected final String TEXT_17 = "){" + NL + "        ";
  protected final String TEXT_18 = "\t\t\t\t\t" + NL + "    \t\t\t\t\t\t";
  protected final String TEXT_19 = ".";
  protected final String TEXT_20 = " = row_";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = ";" + NL + "    \t\t\t\t\t\t";
  protected final String TEXT_23 = NL + "    \t\t\t    \t\t    ";
  protected final String TEXT_24 = ".";
  protected final String TEXT_25 = " = ";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = ";" + NL + "    \t\t\t    \t\t    ";
  protected final String TEXT_28 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {

        //get the input connection
        IConnection inputConn = null;
        String inComingName = "";  
        List< ? extends IConnection> connsIn = node.getIncomingConnections();     
        for (IConnection conn : connsIn) {//3
			inputConn = conn;
			break; //only get the first connection
        }//3 
        
        if(inputConn != null){
        	inComingName = inputConn.getName();
        }
            
        String origin = ElementParameterParser.getValue(node, "__ORIGIN__");
        
    stringBuffer.append(TEXT_2);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(inComingName );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(inComingName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(inComingName );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(inComingName );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
            //begin
            //
    		List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
    		String firstConnName = "";
    		if (conns!=null) {
    			if (conns.size()>0) {
    				IConnection conn = conns.get(0);
    				firstConnName = conn.getName();
    				List<IMetadataColumn> listColumns = metadata.getListColumns();
    				int size = listColumns.size();
    				if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    					for (int i=0; i<size; i++) {
    						IMetadataColumn column = listColumns.get(i);
    						//
    						//end
    						
    stringBuffer.append(TEXT_18);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_22);
    
    						//start
    						//
    					}
    				}
    			}
    			if (conns.size()>1) {
    				for (int i=1;i<conns.size();i++) {
    					IConnection connection = conns.get(i);
    					if ((connection.getName().compareTo(firstConnName)!=0)&&(connection.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
    			    		for (IMetadataColumn column: metadata.getListColumns()) {
    			    		    //
    			    		    //end
    			    		    
    stringBuffer.append(TEXT_23);
    stringBuffer.append(connection.getName() );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_27);
    
    			    		    //start
    			    		    //
    						}
    					}
    				}
    			}
    		}
    }
}

    stringBuffer.append(TEXT_28);
    return stringBuffer.toString();
  }
}
