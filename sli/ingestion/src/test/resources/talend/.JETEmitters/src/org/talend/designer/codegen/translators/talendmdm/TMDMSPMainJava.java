package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import java.util.List;
import java.util.Map;

public class TMDMSPMainJava
{
  protected static String nl;
  public static synchronized TMDMSPMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMDMSPMainJava result = new TMDMSPMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "wsExeProc_";
  protected final String TEXT_2 = ".setParameters(new String[]{";
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = ".";
  protected final String TEXT_5 = NL + "});";
  protected final String TEXT_6 = NL + "for(String xmlField_";
  protected final String TEXT_7 = " : xtentisWS_";
  protected final String TEXT_8 = ".executeStoredProcedure(wsExeProc_";
  protected final String TEXT_9 = ")){" + NL + "\t";
  protected final String TEXT_10 = ".xmlField = xmlField_";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = NL + "          ";
  protected final String TEXT_13 = NL + "            ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String spName = ElementParameterParser.getValue(node, "__SPNAME__");
	
	List<Map<String, String>> spArgs = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SP_ARGS__");

List<? extends IConnection> inConnections = node.getIncomingConnections();
IConnection inConnection = null;
IMetadataTable metadata = null;
if (inConnections != null) {
	for (int i = 0; i < inConnections.size(); i++) {
		IConnection connection = inConnections.get(i);
    	if (connection.getLineStyle().hasConnectionCategory(
    			IConnectionCategory.DATA)) {
    		inConnection = connection;
		    metadata=connection.getMetadataTable();
		    break;
    	}
	}
}

		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		// if output columns are defined
		if (outgoingConns != null && outgoingConns.size() > 0){
		
			IConnection outgoingConn = outgoingConns.get(0);
			String outputCol = null;
			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { // start 1
				if(inConnection!=null){
					if(spArgs.size()>0){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    
    				}
    				for(int i=0;i<spArgs.size();i++){
    					Map<String, String> map = spArgs.get(i);
    					String clmName = map.get("COLUMN");
    					for(IMetadataColumn column:metadata.getListColumns()){
    						if(column.getLabel().equals(clmName)){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(i==0?"":",");
    stringBuffer.append(inConnection.getName());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(column.getLabel() );
    
    						}
    					}
    				}
    				if(spArgs.size()>0){

    stringBuffer.append(TEXT_5);
    
					}
				}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    
			}
		}

    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
