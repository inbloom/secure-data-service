package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.INode;
import org.talend.core.model.temp.ECodePart;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class Component_part_headerJava
{
  protected static String nl;
  public static synchronized Component_part_headerJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Component_part_headerJava result = new Component_part_headerJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "/**" + NL + " * [";
  protected final String TEXT_2 = " ";
  protected final String TEXT_3 = " ] start" + NL + " */" + NL;
  protected final String TEXT_4 = NL + "\t\t\tTalendThreadPool mtp_";
  protected final String TEXT_5 = " = new TalendThreadPool(";
  protected final String TEXT_6 = ");" + NL;
  protected final String TEXT_7 = NL + "\t\t\t\t\t" + NL + "\t\t\tfinal Object[] lockWrite = new Object[0];" + NL + "\t\t\tint threadIdCounter =0;";
  protected final String TEXT_8 = "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_9 = "\t\t\t" + NL + "\t\t\tint NB_ITERATE_";
  protected final String TEXT_10 = " = 0; //for statistics";
  protected final String TEXT_11 = NL;
  protected final String TEXT_12 = NL + "globalMap.put(\"ENABLE_TRACES_CONNECTION_";
  protected final String TEXT_13 = "\",Boolean.FALSE);";
  protected final String TEXT_14 = NL + "ok_Hash.put(\"";
  protected final String TEXT_15 = "\", false);" + NL + "start_Hash.put(\"";
  protected final String TEXT_16 = "\", System.currentTimeMillis());";
  protected final String TEXT_17 = NL;
  protected final String TEXT_18 = ".addMessage(\"begin\",\"";
  protected final String TEXT_19 = "\");";
  protected final String TEXT_20 = NL;
  protected final String TEXT_21 = "Process(globalMap);";
  protected final String TEXT_22 = NL + "    \t";
  protected final String TEXT_23 = ".addLineToRow(\"";
  protected final String TEXT_24 = "_count\");";
  protected final String TEXT_25 = " ";
  protected final String TEXT_26 = NL + "    \t";
  protected final String TEXT_27 = ".addLineToRow(\"";
  protected final String TEXT_28 = "_count\");";
  protected final String TEXT_29 = " ";
  protected final String TEXT_30 = NL + "currentComponent=\"";
  protected final String TEXT_31 = "\";" + NL;
  protected final String TEXT_32 = NL + "if(execStat){" + NL + "\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_33 = "\"+iterateId,0, 0);" + NL + "}\t ";
  protected final String TEXT_34 = NL + NL + "//";
  protected final String TEXT_35 = NL + "//";
  protected final String TEXT_36 = NL + NL;
  protected final String TEXT_37 = NL + "if(execStat){" + NL + "  runStat.updateStatOnConnection(\"";
  protected final String TEXT_38 = "\"+iterateId,1, 1);" + NL + " } ";
  protected final String TEXT_39 = NL + "if(execStat){" + NL + " runStat.updateStatOnConnection(\"";
  protected final String TEXT_40 = "\"+iterateId,1, 1);" + NL + "} ";
  protected final String TEXT_41 = NL;
  protected final String TEXT_42 = NL;
  protected final String TEXT_43 = NL + "\tint tos_count_";
  protected final String TEXT_44 = " = 0;";
  protected final String TEXT_45 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String startNodeCid=node.getDesignSubjobStartNode().getUniqueName();
	ECodePart codePart = codeGenArgument.getCodePart();
	boolean trace = codeGenArgument.isTrace();
	boolean stat = codeGenArgument.isStatistics();
	Set<IConnection> connSet =  new HashSet<IConnection>();
	connSet.addAll(node.getOutgoingConnections(EConnectionType.FLOW_MAIN));
	connSet.addAll(node.getOutgoingConnections(EConnectionType.FLOW_MERGE));
	String incomingName = codeGenArgument.getIncomingName();
	
	Set<IConnection> iterateConnSet =  new HashSet<IConnection>();
	iterateConnSet.addAll(node.getOutgoingConnections(EConnectionType.ITERATE));
	
	List<IConnection> allSubProcessConnection = codeGenArgument.getAllMainSubTreeConnections();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(codePart );
    stringBuffer.append(TEXT_3);
    
    //This part in order to feedback with the iterate_subprocess_header.javajet and iterate_subprocess_footer.javajet
    
	if (codePart.equals(ECodePart.BEGIN)) {
		boolean parallelIterate = false;
		boolean hasParallelIterate = false;
		for (IConnection iterateConn : iterateConnSet) { 
			parallelIterate = "true".equals(ElementParameterParser.getValue(iterateConn, "__ENABLE_PARALLEL__"));
	        if (parallelIterate) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(iterateConn.getTarget().getUniqueName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(ElementParameterParser.getValue(iterateConn,"__NUMBER_PARALLEL__") );
    stringBuffer.append(TEXT_6);
    
if(!hasParallelIterate){
	hasParallelIterate = true;

    stringBuffer.append(TEXT_7);
    }
    stringBuffer.append(TEXT_8);
    
			}
			
			String iterateNodeName = iterateConn.getTarget().getUniqueName();

    stringBuffer.append(TEXT_9);
    stringBuffer.append(iterateNodeName );
    stringBuffer.append(TEXT_10);
    			
			continue;
		}
	}	

    stringBuffer.append(TEXT_11);
    
	if (codePart.equals(ECodePart.BEGIN)) {

     if(trace){ 
    stringBuffer.append(TEXT_12);
    stringBuffer.append(startNodeCid);
    stringBuffer.append(TEXT_13);
     } 
    stringBuffer.append(TEXT_14);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_16);
    
		String statCatcher = ElementParameterParser.getValue(node,"__TSTATCATCHER_STATS__");
		if ((node.getProcess().getNodesOfType("tStatCatcher").size() > 0) && (statCatcher.equals("true"))) {
			for (INode statCatcherNode : node.getProcess().getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_17);
    stringBuffer.append(statCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(statCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_21);
    
			}
		}
	}
	if(codePart.equals(ECodePart.MAIN)) {
		List<INode> meterCatchers = (List<INode>)node.getProcess().getNodesOfType("tFlowMeterCatcher");	
	    if ((node.getProcess().getNodesOfType("tFlowMeter").size() > 0))
	    {
        	for(IConnection temp_conn : node.getIncomingConnections(EConnectionType.FLOW_MAIN))
        	{
        	    String name_conn = temp_conn.getUniqueName();
        	    if(temp_conn.isUseByMetter())
        	    { 	    	

    
            if (meterCatchers != null) {
        		for (INode meterCatcher : meterCatchers) {
    
    stringBuffer.append(TEXT_22);
    stringBuffer.append(meterCatcher.getUniqueName() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(name_conn);
    stringBuffer.append(TEXT_24);
    
        		}
        	}
    
    stringBuffer.append(TEXT_25);
    
				}
			}
			
			for(IConnection temp_conn : node.getIncomingConnections(EConnectionType.FLOW_MERGE))
        	{
        	    String name_conn = temp_conn.getUniqueName();
        	    if(name_conn == incomingName && temp_conn.isUseByMetter())
        	    { 	    	

    
            if (meterCatchers != null) {
        		for (INode meterCatcher : meterCatchers) {
    
    stringBuffer.append(TEXT_26);
    stringBuffer.append(meterCatcher.getUniqueName() );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(name_conn);
    stringBuffer.append(TEXT_28);
    
        		}
        	}
    
    stringBuffer.append(TEXT_29);
    
				}
			}			
	    }
	}

    stringBuffer.append(TEXT_30);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_31);
    
	connSet =  new HashSet<IConnection>();
	connSet.addAll(node.getIncomingConnections(EConnectionType.FLOW_MAIN));
	connSet.addAll(node.getIncomingConnections(EConnectionType.FLOW_MERGE));

	if ((codePart.equals(ECodePart.BEGIN))&&(stat)&&connSet.size()>0) {
		for(IConnection con:connSet){

    stringBuffer.append(TEXT_32);
    stringBuffer.append(con.getUniqueName() );
    stringBuffer.append(TEXT_33);
    		}
	}
	
	if((codePart.equals(ECodePart.MAIN))&&(stat)&&connSet.size()>0){
		for(IConnection con:connSet){

    stringBuffer.append(TEXT_34);
    stringBuffer.append(con.getUniqueName());
    stringBuffer.append(TEXT_35);
    stringBuffer.append((String)codeGenArgument.getIncomingName());
    stringBuffer.append(TEXT_36);
    if (!node.getComponent().useMerge()) {
    stringBuffer.append(TEXT_37);
    stringBuffer.append(con.getUniqueName() );
    stringBuffer.append(TEXT_38);
    
	} else if(con.getUniqueName().equals((String)codeGenArgument.getIncomingName())){

    stringBuffer.append(TEXT_39);
    stringBuffer.append(con.getUniqueName() );
    stringBuffer.append(TEXT_40);
    }
    stringBuffer.append(TEXT_41);
    
		}
	}

    stringBuffer.append(TEXT_42);
    if(codePart.equals(ECodePart.BEGIN)){ 
    stringBuffer.append(TEXT_43);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_44);
    }
    stringBuffer.append(TEXT_45);
    return stringBuffer.toString();
  }
}
