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
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IDataConnection;
import org.talend.designer.xmlmap.XmlMapComponent;
import org.eclipse.emf.common.util.EList;
import org.talend.designer.xmlmap.model.emf.xmlmap.InputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.TreeNode;
import org.talend.designer.xmlmap.model.emf.xmlmap.LookupConnection;
import org.talend.designer.xmlmap.model.emf.xmlmap.XmlMapData;

public class TAdvancedHashBeginJava {

  protected static String nl;
  public static synchronized TAdvancedHashBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAdvancedHashBeginJava result = new TAdvancedHashBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t   \t\t// connection name:";
  protected final String TEXT_3 = NL + "\t\t\t   \t\t// source node:";
  protected final String TEXT_4 = " | target node:";
  protected final String TEXT_5 = NL + "\t\t\t   \t\t// linked node: ";
  protected final String TEXT_6 = NL + "\t\t\t   ";
  protected final String TEXT_7 = NL + "\t\t\t   \t\torg.talend.designer.components.lookup.common.ICommonLookup.MATCHING_MODE matchingModeEnum_";
  protected final String TEXT_8 = " = " + NL + "\t\t\t   \t\t\torg.talend.designer.components.lookup.common.ICommonLookup.MATCHING_MODE.";
  protected final String TEXT_9 = ";" + NL + "\t\t\t   " + NL + "\t\t\t   ";
  protected final String TEXT_10 = NL + "\t\t\t\t\torg.talend.designer.components.lookup.persistent.Persistent";
  protected final String TEXT_11 = "LookupManager<";
  protected final String TEXT_12 = "Struct> tHash_Lookup_";
  protected final String TEXT_13 = " = " + NL + "\t   \t\t\t\t\tnew org.talend.designer.components.lookup.persistent.Persistent";
  protected final String TEXT_14 = "LookupManager<";
  protected final String TEXT_15 = "Struct>(matchingModeEnum_";
  protected final String TEXT_16 = ", ";
  protected final String TEXT_17 = " + \"/\"+ jobName +\"_tMapData_\" + pid +\"_Lookup_";
  protected final String TEXT_18 = "_\"" + NL + "\t   \t\t\t\t\t, new org.talend.designer.components.persistent.IRowCreator() {" + NL + "\t   \t\t\t\t\t\tpublic ";
  protected final String TEXT_19 = "Struct createRowInstance() {" + NL + "\t   \t\t\t\t\t\t\treturn new ";
  protected final String TEXT_20 = "Struct();" + NL + "\t   \t\t\t\t\t\t}" + NL + "\t   \t\t\t\t\t}" + NL + "\t   \t\t\t\t\t";
  protected final String TEXT_21 = NL + "\t   \t\t\t\t\t\t, ";
  protected final String TEXT_22 = NL + "\t   \t\t\t\t\t";
  protected final String TEXT_23 = NL + "\t   \t\t\t\t\t); " + NL + "" + NL + "\t   \t\t\t\ttHash_Lookup_";
  protected final String TEXT_24 = ".initPut();" + NL + "" + NL + "\t\t   \t   \t   globalMap.put(\"tHash_Lookup_";
  protected final String TEXT_25 = "\", tHash_Lookup_";
  protected final String TEXT_26 = ");" + NL + "\t   \t\t\t";
  protected final String TEXT_27 = NL + "\t   \t\t\t\torg.talend.designer.components.lookup.memory.AdvancedMemoryLookup<";
  protected final String TEXT_28 = "Struct> tHash_Lookup_";
  protected final String TEXT_29 = " = " + NL + "\t   \t\t\t\t\torg.talend.designer.components.lookup.memory.AdvancedMemoryLookup." + NL + "\t   \t\t\t\t\t\t<";
  protected final String TEXT_30 = "Struct>getLookup(matchingModeEnum_";
  protected final String TEXT_31 = ");" + NL + "" + NL + "\t\t   \t   \t   globalMap.put(\"tHash_Lookup_";
  protected final String TEXT_32 = "\", tHash_Lookup_";
  protected final String TEXT_33 = ");" + NL + "\t\t   \t   \t   " + NL + "\t\t\t\t";
  protected final String TEXT_34 = " " + NL + "\t\t\t\t/*" + NL + "\t\t\t\t * Valid target not found for connection \"";
  protected final String TEXT_35 = "\"" + NL + "\t\t\t\t */ " + NL + "\t\t\t\t";
  protected final String TEXT_36 = NL + "            ";
  protected final String TEXT_37 = NL;

	void getAllLeaf(TreeNode node,List<TreeNode> allLeaf) {
		EList<TreeNode> children = node.getChildren();
		if(children==null || children.size() == 0) {
			allLeaf.add(node);
		} else {
			for(TreeNode child : children) {
				if(child!=null) {
					getAllLeaf(child,allLeaf);
				}
			}
		}
	}
	
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();


    List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();
    
	if (connections != null && connections.size() > 0) {
        for (IConnection connection : connections) {
        	String connectionName = connection.getName();
        	
    stringBuffer.append(TEXT_2);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(connection.getSource());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(connection.getTarget());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(((IDataConnection) connection).getLinkNodeForHash());
    stringBuffer.append(TEXT_6);
    
			INode validTarget = ((IDataConnection) connection).getLinkNodeForHash();
			if(validTarget != null) {
				boolean isXMLMapComponent = "tXMLMap".equals(validTarget.getComponent().getName());
				boolean findFromBasicType = false;
				boolean findFromDocumentType = false;
				if(isXMLMapComponent) {//TD110 
					XmlMapComponent xmlMapComponent = (XmlMapComponent)validTarget;
					XmlMapData xmlMapData=(XmlMapData)ElementParameterParser.getObjectValueXMLTree(xmlMapComponent);
					EList<InputXmlTree> inputTables = xmlMapData.getInputTrees();
					Map<String, InputXmlTree> nameToTable = new HashMap<String, InputXmlTree>();
					for(InputXmlTree inputTable : inputTables) {
						nameToTable.put(inputTable.getName(),inputTable);
					}
					InputXmlTree currentInputTree = nameToTable.get(connectionName);
					if(currentInputTree!=null && currentInputTree.isLookup()) {
						EList<TreeNode> treeNodes = currentInputTree.getNodes();
						for(TreeNode treeNode : treeNodes) {
							String columnType = treeNode.getType();
							List<TreeNode> allLeaf = new ArrayList<TreeNode>();
							getAllLeaf(treeNode,allLeaf);
							for(TreeNode leaf : allLeaf) {
								if(leaf == null) {
									continue;
								}
								EList<LookupConnection> lookupConnections = leaf.getLookupIncomingConnections();
								if(lookupConnections!=null && lookupConnections.size()>0) {
									if("id_Document".equals(columnType)) {
										findFromDocumentType = true; 
									} else {
										findFromBasicType = true;
									}	
								}
								
							}
						}
					}
				}//TD110
				List<IHashableColumn> hashableColumns = null;
				IMatchingMode matchingMode = null;
				String tempFolder = null;
				String rowsBufferSize = null;
				IHashConfiguration hashConfiguration = null;				
				String matchingModeStr = null;
				boolean bSortOnDisk = "true".equals(ElementParameterParser.getValue(node, "__SORT_ON_DISK__")); 
				
			    if (validTarget instanceof IHashableInputConnections){
					IHashableInputConnections target = (IHashableInputConnections) validTarget;
					hashConfiguration = target.getHashConfiguration(connection.getName());
				} else{
				    List<java.util.Map<String, String>> listBlockings = (List<java.util.Map<String, String>>)ElementParameterParser.getObjectValue(node, "__BLOCKING_DEFINITION__");
				  	matchingModeStr = (listBlockings == null || listBlockings.size() == 0) ? "ALL_ROWS" : "ALL_MATCHES";
                    tempFolder = ElementParameterParser.getValue(node, "__TMP_DIRECTORY__");
				  	rowsBufferSize = ElementParameterParser.getValue(node, "__ROWS_BUFFER_SIZE__");
				}

					if(hashConfiguration == null) {
						hashableColumns = new ArrayList(0);
						
						//System.out.println(connectionName + " ### " + hashConfiguration + "IS NULL ##### " + validTarget + " " + validTarget.getClass());
						
					} else {
						tempFolder = hashConfiguration.getTemporaryDataDirectory();
						hashableColumns = hashConfiguration.getHashableColumns();
						matchingMode = hashConfiguration.getMatchingMode();
						rowsBufferSize = hashConfiguration.getRowsBufferSize();
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
					if(isXMLMapComponent && findFromBasicType && findFromDocumentType) {
						matchingModeStr = "ALL_MATCHES";
					}
					boolean isAllRows = "ALL_ROWS".equals(matchingModeStr);
			   
    stringBuffer.append(TEXT_7);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append( matchingModeStr );
    stringBuffer.append(TEXT_9);
    
				if (hashConfiguration != null && hashConfiguration.isPersistent() || bSortOnDisk) {
				
    stringBuffer.append(TEXT_10);
    stringBuffer.append( isAllRows ? "" : "Sorted" );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append( isAllRows ? "" : "Sorted" );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append( tempFolder );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_20);
     if(!isAllRows) { 
    stringBuffer.append(TEXT_21);
    stringBuffer.append( rowsBufferSize );
    stringBuffer.append(TEXT_22);
     } 
    stringBuffer.append(TEXT_23);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_26);
    	
	   			} else {
	   			
    stringBuffer.append(TEXT_27);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_33);
    
				}
			} else {
				
    stringBuffer.append(TEXT_34);
    stringBuffer.append( connectionName);
    stringBuffer.append(TEXT_35);
    
			}
		}
	}


    stringBuffer.append(TEXT_36);
    stringBuffer.append(TEXT_37);
    return stringBuffer.toString();
  }
}
