package org.talend.designer.codegen.translators.business.salesforce;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import java.util.List;

public class TSalesforceGetServerTimestampMainJava
{
  protected static String nl;
  public static synchronized TSalesforceGetServerTimestampMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceGetServerTimestampMainJava result = new TSalesforceGetServerTimestampMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t";
  protected final String TEXT_2 = ".";
  protected final String TEXT_3 = " = sfMgr_";
  protected final String TEXT_4 = ".getServerTimestamp().getTime();" + NL + "\t\t\tnb_line_";
  protected final String TEXT_5 = " ++;";

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
		List<IMetadataColumn> listColumns = metadata.getListColumns(); 
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		if (outgoingConns != null && outgoingConns.size() > 0){	

    stringBuffer.append(TEXT_1);
    stringBuffer.append(outgoingConns.get(0).getName() );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(listColumns.get(0).getLabel() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
		}
	}
}

    return stringBuffer.toString();
  }
}
