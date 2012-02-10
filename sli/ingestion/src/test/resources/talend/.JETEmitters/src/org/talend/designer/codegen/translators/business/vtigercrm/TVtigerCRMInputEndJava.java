package org.talend.designer.codegen.translators.business.vtigercrm;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TVtigerCRMInputEndJava
{
  protected static String nl;
  public static synchronized TVtigerCRMInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TVtigerCRMInputEndJava result = new TVtigerCRMInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "                    }";
  protected final String TEXT_3 = NL + "    \t    }";
  protected final String TEXT_4 = NL + "    globalMap.put(\"";
  protected final String TEXT_5 = "_NB_LINE\", nb_line_";
  protected final String TEXT_6 = ");";
  protected final String TEXT_7 = NL;
  protected final String TEXT_8 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String version_selection = ElementParameterParser.getValue(node, "__VERSION_SELECTION__");
if(version_selection.equals("VTIGER_50")){
    String method = ElementParameterParser.getValue(node, "__METHODNAME__");
    List<IMetadataColumn> metadataColumns = null;
    List<IMetadataTable> metadataTables = node.getMetadataList();
    if(metadataTables != null && metadataTables.size() > 0) {
        IMetadataTable metadataTable = metadataTables.get(0);
        if(metadataTable != null) {
            metadataColumns = metadataTable.getListColumns();
            if(metadataColumns != null && metadataColumns.size() > 0) {
                if(("searchContactsByEmail").equals(method) || ("getContacts").equals(method) || ("getTasks").equals(method) || ("getClndr").equals(method) || ("get_KBase_details").equals(method)) {
                    
    stringBuffer.append(TEXT_2);
    
                }
            }
        }
    }
}else{
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas != null) && (metadatas.size() > 0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata != null) {
    	List<IMetadataColumn> columnList = metadata.getListColumns();
    	int nbSchemaColumns = columnList.size();
    	List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
    	// if output columns are defined
    	if (nbSchemaColumns > 0 && outgoingConns != null && outgoingConns.size() > 0){
    
    	    IConnection outgoingConn = outgoingConns.get(0);
    	    if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    	

    stringBuffer.append(TEXT_3);
    
        	}
    	}
    	}
    }

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
