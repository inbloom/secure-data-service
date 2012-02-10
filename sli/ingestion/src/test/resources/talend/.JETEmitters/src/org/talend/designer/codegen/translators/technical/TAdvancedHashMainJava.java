package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.ArrayList;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IHashableInputConnections;
import org.talend.core.model.process.IHashConfiguration;
import org.talend.core.model.process.IHashableColumn;
import org.talend.core.model.process.IMatchingMode;
import java.util.HashSet;
import java.util.Set;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IDataConnection;

public class TAdvancedHashMainJava
{
  protected static String nl;
  public static synchronized TAdvancedHashMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAdvancedHashMainJava result = new TAdvancedHashMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL;
  protected final String TEXT_2 = NL + "\t\t\t   " + NL + "\t\t\t   ";
  protected final String TEXT_3 = NL + NL + "\t\t\t\t\t";
  protected final String TEXT_4 = "Struct ";
  protected final String TEXT_5 = "_HashRow = tHash_Lookup_";
  protected final String TEXT_6 = ".getNextFreeRow();" + NL + "" + NL + "\t   \t\t\t";
  protected final String TEXT_7 = NL + NL + "\t\t\t\t\t";
  protected final String TEXT_8 = "Struct ";
  protected final String TEXT_9 = "_HashRow = new ";
  protected final String TEXT_10 = "Struct();" + NL + "\t\t   \t   \t   " + NL + "\t\t\t\t";
  protected final String TEXT_11 = " " + NL + "\t\t\t\t/*" + NL + "\t\t\t\t * Valid target not found for connection \"";
  protected final String TEXT_12 = "\"" + NL + "\t\t\t\t */ " + NL + "\t\t\t\t";
  protected final String TEXT_13 = NL + "\t\t\t\t";
  protected final String TEXT_14 = "_HashRow.";
  protected final String TEXT_15 = " = ";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = ".clone();" + NL + "\t\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\t";
  protected final String TEXT_19 = "_HashRow.";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = ";" + NL + "\t\t\t\t";
  protected final String TEXT_23 = NL + "\t\t\ttHash_Lookup_";
  protected final String TEXT_24 = ".put(";
  protected final String TEXT_25 = "_HashRow);" + NL + "\t\t\t";
  protected final String TEXT_26 = NL + "            " + NL + NL + NL;
  protected final String TEXT_27 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();


    List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();
    List<IConnection> outConnections = (List<IConnection>) node.getOutgoingConnections();
    
    List<INode> graphicalNodes = (List<INode>) node.getProcess().getGraphicalNodes();
    
	if (connections != null && connections.size() > 0) { // T_AH_100
        for (IConnection connection : connections) { // T_AH_101
        	String connectionName = connection.getName();
   
			INode validTarget = ((IDataConnection) connection).getLinkNodeForHash();

			if(validTarget != null) { // T_AH_102
					String matchingModeStr = null;
					List<IHashableColumn> hashableColumns = null;
					IMatchingMode matchingMode = null;
					IHashConfiguration hashConfiguration = null;
					boolean bSortOnDisk = "true".equals(ElementParameterParser.getValue(node, "__SORT_ON_DISK__"));
					
					if (validTarget instanceof IHashableInputConnections){
					
					  IHashableInputConnections target = (IHashableInputConnections) validTarget;
					  hashConfiguration = target.getHashConfiguration(connection.getName());
					} else{
				  	  matchingModeStr = "ALL_MATCHES";
				    }
					if(hashConfiguration == null) {
						hashableColumns = new ArrayList(0);
					} else {
						hashableColumns = hashConfiguration.getHashableColumns();
						matchingMode = hashConfiguration.getMatchingMode();
					}
					
					
					if (matchingModeStr == null){
					  if(matchingMode == null) {
						if(hashableColumns.size() > 0) {
							matchingModeStr = "UNIQUE_MATCH";
						} else {
							matchingModeStr = "ALL_ROWS";
						}
					  } else {
						matchingModeStr = matchingMode.toString();
					  }
			        }
			   
    stringBuffer.append(TEXT_2);
    
				if(hashConfiguration != null && hashConfiguration.isPersistent() || bSortOnDisk) {
				
    stringBuffer.append(TEXT_3);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_6);
    	
	   			} else {
	   			
    stringBuffer.append(TEXT_7);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_10);
    
				}
			} // T_AH_103 
            else { // T_AH_104
				
    stringBuffer.append(TEXT_11);
    stringBuffer.append( connectionName);
    stringBuffer.append(TEXT_12);
    
            } // T_AH_104
			
			IMetadataTable metadataTable = connection.getMetadataTable();
			
			List<IMetadataColumn> listColumns = metadataTable.getListColumns();
            for (IMetadataColumn column : listColumns) {
                String columnName = column.getLabel();
				if("id_Dynamic".equals(column.getTalendType())) {
				
    stringBuffer.append(TEXT_13);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_17);
    
				} else {
				
    stringBuffer.append(TEXT_18);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_22);
    	
				}
            } 


			
    stringBuffer.append(TEXT_23);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_25);
    
			
			
		} // T_AH_101
	} // T_AH_100


    stringBuffer.append(TEXT_26);
    stringBuffer.append(TEXT_27);
    return stringBuffer.toString();
  }
}
