package org.talend.designer.codegen.translators.logs_errors;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.EConnectionType;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

public class TFlowMeterEndJava
{
  protected static String nl;
  public static synchronized TFlowMeterEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFlowMeterEndJava result = new TFlowMeterEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = ".addMessage(";
  protected final String TEXT_4 = ", new Integer(count_";
  protected final String TEXT_5 = "), \"";
  protected final String TEXT_6 = "\", \"";
  protected final String TEXT_7 = "\", \"";
  protected final String TEXT_8 = "\");";
  protected final String TEXT_9 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        Boolean useRowLabel = (Boolean)ElementParameterParser.getObjectValue(
                node,"__USEROWLABEL__"); 
        
        String label = "";
        if(useRowLabel.equals(Boolean.TRUE))
        {
        	List<IConnection> connList = (List<IConnection>)node.getIncomingConnections();
        	if(connList.size()>0)
        	{
        		IConnection conn = (IConnection)connList.get(0);
        		label = "\""+conn.getUniqueName()+"\"";
        	}
        	else
        	{
        		label = "\"No input Connection\"";
        	}
        }else{
			label = (String)ElementParameterParser.getObjectValue(
                node,"__METTERLABEL__");
        } 
               
        String absolute = (String)ElementParameterParser.getObjectValue(
                node,"__ABSOLUTE__");  
           
        String reference = null;     
        if(("Relative").equals(absolute))
        { 
        	reference = (String)ElementParameterParser.getObjectValue(
                      node,"__CONNECTIONS__");  
        }

        List<Map<String, String>> shreshlods =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,"__THRESHLODS__");  
                
        StringBuilder shreshlods_str = new StringBuilder();
        if(shreshlods.size() > 0)
        {
            for(Map<String, String> map : shreshlods)  
            {
                if(shreshlods_str.length() > 0)
                {
                	shreshlods_str.append("#");
                }
       		      shreshlods_str.append(map.get("LABEL") + "|")
       		                    .append(map.get("BOTTOM")+ "|")
       		                    .append(map.get("TOP")+ "|")
       		                    .append(map.get("COLOR").replace(";","|"));
            } 
        } 
        
	    if (node.getProcess().getNodesOfType("tFlowMeterCatcher").size() > 0) {
			List<INode> meterCatchers = (List<INode>)node.getProcess().getNodesOfType("tFlowMeterCatcher");
			for (INode meterCatcher : meterCatchers) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(meterCatcher.getUniqueName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(label);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(reference);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(shreshlods_str);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
          
				}
			}
		}
	}

    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
