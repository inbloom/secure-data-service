package org.talend.designer.codegen.translators.business.sagex3;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TSageX3InputEndJava
{
  protected static String nl;
  public static synchronized TSageX3InputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSageX3InputEndJava result = new TSageX3InputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_2 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);
	
	if (metadata != null) {
		List<IMetadataColumn> columnList = metadata.getListColumns();
		int nbSchemaColumns = columnList.size();
			
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		if (nbSchemaColumns > 0 && outgoingConns != null && outgoingConns.size() > 0){

			IConnection outgoingConn = outgoingConns.get(0);

			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_1);
    
			}
		}
	}
}	

    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
