package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;;

public class TMDMTriggerOutputMainJava
{
  protected static String nl;
  public static synchronized TMDMTriggerOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMDMTriggerOutputMainJava result = new TMDMTriggerOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\tMDMOutputMessage = ";
  protected final String TEXT_3 = ".MDM_Message;" + NL + "\t" + NL + "\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();	
	String cid = node.getUniqueName();
	
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas != null) && (metadatas.size() > 0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata != null) {
    		
    		String incomingName = "";
        
		  	List<? extends IConnection> inputConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
		  	if ((inputConns!=null)&&(inputConns.size()>0)) {
		  		IConnection incomingConn = inputConns.get(0); 
		  		incomingName = incomingConn.getName();
		  	}else{
		  		return "";
		  	}
			
    stringBuffer.append(TEXT_2);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_3);
    
  		}
 	}	
	
    return stringBuffer.toString();
  }
}
