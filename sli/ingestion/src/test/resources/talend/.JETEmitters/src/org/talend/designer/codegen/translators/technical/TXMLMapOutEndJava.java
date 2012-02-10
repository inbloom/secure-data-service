package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.xmlmap.XmlMapComponent;
import org.talend.designer.xmlmap.model.emf.xmlmap.XmlMapData;
import org.eclipse.emf.common.util.EList;
import org.talend.designer.xmlmap.model.emf.xmlmap.VarTable;
import org.talend.designer.xmlmap.model.emf.xmlmap.InputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputXmlTree;
import org.talend.designer.xmlmap.model.emf.xmlmap.TreeNode;
import org.talend.designer.xmlmap.model.emf.xmlmap.OutputTreeNode;

public class TXMLMapOutEndJava
{
  protected static String nl;
  public static synchronized TXMLMapOutEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TXMLMapOutEndJava result = new TXMLMapOutEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL;
  protected final String TEXT_2 = NL + NL;
  protected final String TEXT_3 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	XmlMapComponent node = (XmlMapComponent) codeGenArgument.getArgument();
//	String cid = node.getUniqueName();
	
	XmlMapData xmlMapData =(XmlMapData)ElementParameterParser.getObjectValueXMLTree(node);

//	boolean isXpath = false;	
	boolean isPlainNode = true;
	boolean outputHasDocument = false;
	EList<OutputXmlTree> outputTables = xmlMapData.getOutputTrees();
//	EList<VarTable> varTables = xmlMapData.getVarTables();
	EList<InputXmlTree> inputTables = xmlMapData.getInputTrees();
	if(inputTables.size()>0) {
		InputXmlTree mainInputTable = inputTables.get(0);
		for(TreeNode tmpNode : mainInputTable.getNodes()){
			if(tmpNode.getType().equals("id_Document"))
				isPlainNode = false;
		}
	}

for (OutputXmlTree table : outputTables) {

    
    EList<OutputTreeNode> tableEntries = table.getNodes();
    if (tableEntries == null ) {
        continue;
    }
    for(OutputTreeNode tableEntry : tableEntries) {
        	if(!("id_Document").equals(tableEntry.getType())){

//        		String resultExpression = tableEntry.getExpression();
//        		if(null !=resultExpression && resultExpression.indexOf("/") != -1)
//        		   isXpath = true;        	
			} else {
					outputHasDocument = true;
			}
	}
}

    stringBuffer.append(TEXT_1);
    
	if((!outputHasDocument && isPlainNode) || (!isPlainNode &&  !outputHasDocument) || (isPlainNode && outputHasDocument) || (!isPlainNode && outputHasDocument)) {
		// Nothing to generate
	}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(TEXT_3);
    return stringBuffer.toString();
  }
}
