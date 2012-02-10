package org.talend.designer.codegen.translators.processing;

import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class TFilterColumnsMainJava
{
  protected static String nl;
  public static synchronized TFilterColumnsMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFilterColumnsMainJava result = new TFilterColumnsMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL;
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ";" + NL + "" + NL + "\t";
  protected final String TEXT_7 = NL + "    nb_line_";
  protected final String TEXT_8 = "++;";
  protected final String TEXT_9 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String inputConnName = null;
	Set<String> inputCols = new HashSet<String>();
 
 	
	if (node.getIncomingConnections()!=null) {
		for (IConnection incomingConn : node.getIncomingConnections()) {
			if (incomingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				inputConnName = incomingConn.getName();
				IMetadataTable inputMetadataTable = incomingConn.getMetadataTable();
				for (IMetadataColumn inputCol : inputMetadataTable.getListColumns()) {
					inputCols.add(inputCol.getLabel());
				}
				break;
			}
		}
	}

    stringBuffer.append(TEXT_1);
    	
	
	for (IConnection conn : node.getOutgoingConnections()) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			IMetadataTable outputMetadataTable = conn.getMetadataTable();
			if (outputMetadataTable!=null) {
				for (IMetadataColumn outputCol : outputMetadataTable.getListColumns()) { 
					if (inputCols.contains(outputCol.getLabel())) {
					

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(outputCol.getLabel() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inputConnName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(outputCol.getLabel() );
    stringBuffer.append(TEXT_6);
    
					}
				}
			}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    			
		}
	}

    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
