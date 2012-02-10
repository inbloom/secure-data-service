package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TStewardshipTaskInputEndJava
{
  protected static String nl;
  public static synchronized TStewardshipTaskInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TStewardshipTaskInputEndJava result = new TStewardshipTaskInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_2 = NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_3 = "_NB_LINE\",nb_line_";
  protected final String TEXT_4 = ");";

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
	
			// if output columns are defined
			if (nbSchemaColumns > 0 && outgoingConns != null && outgoingConns.size() > 0){
				IConnection outgoingConn = outgoingConns.get(0);
	
				if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				
    stringBuffer.append(TEXT_1);
    
				}
			}
		}
	}
	
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
