package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.xmlmap.XmlMapComponent;
import org.talend.designer.xmlmap.model.emf.xmlmap.XmlMapData;
import org.eclipse.emf.common.util.EList;
import org.talend.designer.xmlmap.generation.GenerationManager;
import org.talend.designer.xmlmap.model.emf.xmlmap.VarTable;
import org.talend.designer.xmlmap.model.emf.xmlmap.InputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputTreeNode;
import org.talend.core.model.process.IConnection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TXMLMapInMainJava
{
  protected static String nl;
  public static synchronized TXMLMapInMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TXMLMapInMainJava result = new TXMLMapInMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = " = null;" + NL + "\tif(row_out_";
  protected final String TEXT_4 = "!=null && row_out_";
  protected final String TEXT_5 = " instanceof ";
  protected final String TEXT_6 = "Struct) {" + NL + "\t\t";
  protected final String TEXT_7 = " = (";
  protected final String TEXT_8 = "Struct)row_out_";
  protected final String TEXT_9 = ";";
  protected final String TEXT_10 = NL + "\t\tgenerateDocumentHelper_";
  protected final String TEXT_11 = ".generateOk(";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = "\t\t" + NL + "\t}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	XmlMapComponent node = (XmlMapComponent) codeGenArgument.getArgument();
	GenerationManager gm =  (GenerationManager)node.initGenerationManager();
	String cid = node.getUniqueName();
	
	XmlMapData xmlMapData = (XmlMapData)ElementParameterParser.getObjectValueXMLTree(node);
	
	EList<InputXmlTree> inputTables = xmlMapData.getInputTrees();
	EList<OutputXmlTree> outputTables = xmlMapData.getOutputTrees();
	EList<VarTable> varTables = xmlMapData.getVarTables();

	List<IConnection> outputConnections = (List<IConnection>) node.getOutgoingConnections();
	
	Map<String, IConnection> nameToOutputConnection = new HashMap<String, IConnection>();
    for (IConnection connection : outputConnections) {
	  		nameToOutputConnection.put(connection.getName(), connection);
	}
	
	List<String> tableNames = new ArrayList<String>();
	Map<String,String> tableNameToDocumentColumnName = new HashMap<String,String>();
	
	for(OutputXmlTree outputTable : outputTables) {
		String outputTableName = outputTable.getName();
		if(nameToOutputConnection.get(outputTableName) == null) {
			continue;
		}
		tableNames.add(outputTableName);
		
		for(OutputTreeNode outputNode: outputTable.getNodes()) {
			if(("id_Document").equals(outputNode.getType())){
				tableNameToDocumentColumnName.put(outputTableName,outputNode.getName());
				break;
			}
		}
	}
	
	for(String tableName : tableNames) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
	String docColumnName = tableNameToDocumentColumnName.get(tableName);
	if(docColumnName!=null) {

    stringBuffer.append(TEXT_10);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(docColumnName);
    stringBuffer.append(TEXT_13);
    
	}

    stringBuffer.append(TEXT_14);
    
	}

    return stringBuffer.toString();
  }
}
