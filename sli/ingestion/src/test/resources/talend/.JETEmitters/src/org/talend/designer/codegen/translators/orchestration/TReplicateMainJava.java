package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TReplicateMainJava
{
  protected static String nl;
  public static synchronized TReplicateMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TReplicateMainJava result = new TReplicateMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\t";
  protected final String TEXT_4 = " = new ";
  protected final String TEXT_5 = "Struct();" + NL + "\t";
  protected final String TEXT_6 = "\t\t\t\t\t" + NL + "\t";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = ";\t\t\t";
  protected final String TEXT_11 = NL;
  protected final String TEXT_12 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();	

    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas != null) && (metadatas.size() > 0)) {//b
        IMetadataTable metadata = metadatas.get(0);
        if (metadata != null) {//a	

    stringBuffer.append(TEXT_2);
    
    IMetadataTable preMetadata = null;
    String incomingName = "";
        
  	List<? extends IConnection> inputConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
  	if ((inputConns!=null)&&(inputConns.size()>0)) {
  		IConnection incomingConn = inputConns.get(0); 
  		incomingName = incomingConn.getName();
  		preMetadata = incomingConn.getMetadataTable();
  	}else{
  		return "";
  	}
	
	List<IMetadataColumn> columns = metadata.getListColumns();
	int columnSize = columns.size();
	List<IMetadataColumn> preColumns = preMetadata.getListColumns();
	int preColumnSize = preColumns.size();
	int minSize = Math.min(columnSize, preColumnSize);
	
	List< ? extends IConnection> outConns = node.getOutgoingConnections();
	for (IConnection conn : outConns) {	
		if(conn.getLineStyle().equals(EConnectionType.FLOW_MAIN)||conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)){
		String outputConnName = conn.getName();				

    stringBuffer.append(TEXT_3);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_5);
    
		for (int i = 0; i < minSize; i++) {//4
			IMetadataColumn column = columns.get(i);
			IMetadataColumn preColumn = preColumns.get(i);

    stringBuffer.append(TEXT_6);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(preColumn.getLabel() );
    stringBuffer.append(TEXT_10);
    			
			} //4
		}
	}

    stringBuffer.append(TEXT_11);
    
  	}//b
 }//a	

    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
