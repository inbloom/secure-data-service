package org.talend.designer.codegen.translators.technical;

import java.util.ArrayList;
import java.util.List;
import org.talend.designer.mapper.external.data.ExternalMapperData;
import org.talend.designer.mapper.external.data.ExternalMapperTable;
import org.talend.designer.mapper.external.data.ExternalMapperTableEntry;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.mapper.MapperComponent;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import java.util.HashMap;
import java.util.Map;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;

public class TMapInEndJava {


  protected static String nl;
  public static synchronized TMapInEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMapInEndJava result = new TMapInEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL;
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "// ###############################" + NL + "// # Lookup hashes releasing";
  protected final String TEXT_4 = NL + "\t\t\t\t\tfsi_";
  protected final String TEXT_5 = ".endGet();" + NL + "\t\t\t\t";
  protected final String TEXT_6 = NL + "\t\t\t\t\tif(tHash_Lookup_";
  protected final String TEXT_7 = " != null) {" + NL + "\t\t\t\t\t\ttHash_Lookup_";
  protected final String TEXT_8 = ".endGet();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tglobalMap.remove( \"tHash_Lookup_";
  protected final String TEXT_9 = "\" );" + NL + "" + NL + "\t\t\t\t\t";
  protected final String TEXT_10 = NL + NL + "\t\t\t\t\t\ttHash_Lookup_Cache_";
  protected final String TEXT_11 = ".endGet();" + NL + "\t\t\t\t\t\ttHash_Lookup_Cache_";
  protected final String TEXT_12 = " = null;" + NL + "\t\t\t\t\t\ttHash_Lookup_Real_";
  protected final String TEXT_13 = " = null;" + NL + "" + NL + "\t\t\t\t\t";
  protected final String TEXT_14 = NL + "\t\t\t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_15 = NL + "// ###############################        " + NL;
  protected final String TEXT_16 = NL;

	public boolean hasConcurrencyContext(List<IConnection> inputConnections, List<? extends INode> graphicalNodes) {

		for(IConnection connection : inputConnections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            INode node = connection.getSource();
				return recursiveSearchIterateForConcurrency(node, graphicalNodes);
			}
		}
		return false;
	
	}

	public boolean hasIterateConnectionBefore(List<IConnection> inputConnections, List<? extends INode> graphicalNodes) {

		for(IConnection connection : inputConnections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            INode node = connection.getSource();
				return recursiveSearchIterate(node, graphicalNodes);
			}
		}
		return false;
	
	}
	
	public boolean recursiveSearchIterate(INode node, List<? extends INode> graphicalNodes) {
	
		//System.out.println(node);
	
		List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();

		for(IConnection connection : connections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            node = connection.getSource();
				//System.out.println(connection.getName() + " connectionType=" + connectionType + " connection=" + String.valueOf(connection));
    	        return recursiveSearchIterate(node, graphicalNodes);
            } else if(connectionType == EConnectionType.ITERATE) {
				//System.out.println("ITERATE return true");
    	        return true;
            }else{
            	//for virtual component
            	boolean find = false;
				for(INode loopNode : graphicalNodes) {
					if(loopNode.getUniqueName().equals(node.getUniqueName())){
						find = true;
					}
				}
				if(!find){
					List<IConnection> vConnections = (List<IConnection>) node.getIncomingConnections();
					for(IConnection vConnection : vConnections) {
						node = vConnection.getSource();
						break;
					}
					return recursiveSearchIterate(node, graphicalNodes);
				}
            }
		}
		
		//System.out.println("return false");
		return false;
	
	}

	public boolean recursiveSearchIterateForConcurrency(INode node, List<? extends INode> graphicalNodes) {
		//System.out.println(node);
	
		List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();

		for(IConnection connection : connections) {
            EConnectionType connectionType = connection.getLineStyle();
            if (connectionType == EConnectionType.FLOW_MAIN) {
	            node = connection.getSource();
				//System.out.println(connection.getName() + " connectionType=" + connectionType + " connection=" + String.valueOf(connection));
    	        return recursiveSearchIterateForConcurrency(node, graphicalNodes);
            } else if(connectionType == EConnectionType.ITERATE) {
				//System.out.println("ITERATE return true");
				boolean parallelIterate = "true".equals(ElementParameterParser.getValue(connection, "__ENABLE_PARALLEL__"));
    	        return parallelIterate;
            }else{
            	//for virtual component
            	boolean find = false;
				for(INode loopNode : graphicalNodes) {
					if(loopNode.getUniqueName().equals(node.getUniqueName())){
						find = true;
					}
				}
				if(!find){
					List<IConnection> vConnections = (List<IConnection>) node.getIncomingConnections();
					for(IConnection vConnection : vConnections) {
						node = vConnection.getSource();
						break;
					}
					return recursiveSearchIterateForConcurrency(node, graphicalNodes);
				}
            }
		}
		
		//System.out.println("return false");
		return false;
	
	}

	public INode searchSubProcessStartNode(IConnection connection) {
       	INode source = connection.getSource();
    	//System.out.println(" source=" +  source);
    	INode subprocessStartNode = null;
    	if(source != null) {
			String searchedComponentName = source.getUniqueName();
        	//System.out.println(" searchedComponentName=" +  searchedComponentName);
			List<? extends INode> generatedNodes = source.getProcess().getGeneratingNodes();
			for(INode loopNode : generatedNodes) {
				if(loopNode.getUniqueName().equals(searchedComponentName)) {
					subprocessStartNode = loopNode.getSubProcessStartNode(false);
		        	//System.out.println(" subprocessStartNode=" +  subprocessStartNode.getUniqueName());
				}
			}
		}
	
		return subprocessStartNode;
	}
    public boolean hasJoinedTable(String tableNameToTest, ExternalMapperData data) {
        for (ExternalMapperTable table : data.getOutputTables()) {
            if (table.getIsJoinTableOf() != null && table.getIsJoinTableOf().equals(tableNameToTest)) {
                return true;
            }
        }
        return false;
    }
    
    
  public boolean isErrorReject(ExternalMapperTable table){
        String errorMessage = null;
        String errorStackTrace = null; 
        if(table!=null&&table.getName()!=null&&table.getName().endsWith("ErrorReject")){
            for(ExternalMapperTableEntry entry:table.getMetadataTableEntries()){
                if("errorMessage".equals(entry.getName())){
                    errorMessage = entry.getName();
                }else if("errorStackTrace".equals(entry.getName())){
                    errorStackTrace = entry.getName();
                }
            }
            if(errorMessage!=null&&errorStackTrace!=null){
                return true;
            }
        }
        return false;
    }



    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
     

	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	MapperComponent node = (MapperComponent) codeGenArgument.getArgument();
	boolean stats = codeGenArgument.isStatistics();
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ExternalMapperData data = (ExternalMapperData) node.getExternalData();

	String componentName = node.getUniqueName();
	boolean isVirtualIn = componentName.endsWith("TMAP_IN");
	boolean isVirtualOut = componentName.endsWith("TMAP_OUT");

    List<IConnection> inputConnections = (List<IConnection>) node.getIncomingConnections();

	if(isVirtualIn) {
		String searchedComponentName = componentName.replaceAll("TMAP_IN", "TMAP_OUT");
		List<? extends INode> generatedNodes = node.getProcess().getGeneratingNodes();
		for(INode loopNode : generatedNodes) {
			if(loopNode.getUniqueName().equals(searchedComponentName)) {
				inputConnections = (List<IConnection>) loopNode.getIncomingConnections();
				break;
			}
		}
	}


		// Bug partially fixed, see bug:785	
		// Bug partially fixed, see bug:3966	
		
		boolean hasIterate = hasIterateConnectionBefore(inputConnections, node.getProcess().getGraphicalNodes());
        
        

    stringBuffer.append(TEXT_3);
    

		if(!isVirtualOut) {

	        HashMap<String, IConnection> hNameToConnection = new HashMap<String, IConnection>();
        	for (IConnection connection : inputConnections) {
	            hNameToConnection.put(connection.getName(), connection);
        	}

	        List<ExternalMapperTable> inputTablesList = new ArrayList<ExternalMapperTable>(data.getInputTables());
	        int lstSizeInputs = inputTablesList.size();
	        
	        if(lstSizeInputs > 1) {
	        	String mainTableName = inputTablesList.get(0).getName();
	    
	    		String tmpJoinedTableNames = "";    	
	    		String joinedTableNames = "";    	
	    		int tmpIndexLastJoined = -1;
	    		boolean hasPersistentLookup = false;
		        for (int i = 0; i < lstSizeInputs; i++) {
		            ExternalMapperTable inputTable = (ExternalMapperTable) inputTablesList.get(i);

					if(hNameToConnection.get(inputTable.getName()) != null) {
	        			if(inputTable.isPersistent() 
		        			&& !"ALL_ROWS".equals(inputTable.getMatchingMode()) 
	        			) {
		        			tmpJoinedTableNames = joinedTableNames;
	        				tmpIndexLastJoined = i;
	        				hasPersistentLookup = true;
	        			}

	        			joinedTableNames += ((i > 0) ? "__" : "") + inputTable.getName();
	        		}

	        	}

	        	if(hasPersistentLookup) {
				
    stringBuffer.append(TEXT_4);
    stringBuffer.append( tmpJoinedTableNames );
    stringBuffer.append(TEXT_5);
    
	        	}
	        	
	        }
	        
	        for (int i = 1; i < lstSizeInputs; i++) {
	            ExternalMapperTable inputTable = (ExternalMapperTable) inputTablesList.get(i);


	            List<ExternalMapperTableEntry> tableEntries = inputTable.getMetadataTableEntries();
	            if (tableEntries == null) {
	                continue;
	            }
	            String tableName = inputTable.getName();
	            IConnection connection = hNameToConnection.get(tableName);
	            if(connection == null) {
	            	continue;
	            }

				String lookupMode = inputTable.getLookupMode();
				boolean isCacheOrReload = org.talend.designer.mapper.model.table.LOOKUP_MODE.CACHE_OR_RELOAD.name().equals(lookupMode);

				if(!hasIterate) {
				
    stringBuffer.append(TEXT_6);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_9);
    					
					if(isCacheOrReload) {
					
    stringBuffer.append(TEXT_10);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_13);
    					
					}
					
    stringBuffer.append(TEXT_14);
    
				}
	        }
		}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(TEXT_16);
    return stringBuffer.toString();
  }
}