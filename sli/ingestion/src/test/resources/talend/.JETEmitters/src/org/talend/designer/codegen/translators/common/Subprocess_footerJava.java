package org.talend.designer.codegen.translators.common;

import org.talend.designer.codegen.config.NodesSubTree;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Iterator;

public class Subprocess_footerJava
{
  protected static String nl;
  public static synchronized Subprocess_footerJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Subprocess_footerJava result = new Subprocess_footerJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t\t\t\t\t} catch (Exception e) {" + NL + "\t\t\t\t\t\tthis.status = \"failure\";" + NL + "\t\t\t\t\t\tInteger localErrorCode = (Integer) (((java.util.Map) threadLocal.get()).get(\"errorCode\"));" + NL + "\t\t\t\t\t\tif (localErrorCode != null) {" + NL + "\t\t\t\t\t\t\tif (this.errorCode == null || localErrorCode.compareTo(this.errorCode) > 0) {" + NL + "\t\t\t\t\t\t\t\tthis.errorCode = localErrorCode;" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}\t\t\t\t\t" + NL + "\t\t\t            pool.setErrorThread(this, new TalendException(e, currentComponent, globalMap));" + NL + "\t\t\t            //pool.setErrorThread(this,e);" + NL + "\t\t\t            pool.stopAllThreads();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tthis.isRunning = false;" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\tInteger localErrorCode = (Integer) (((java.util.Map) threadLocal.get()).get(\"errorCode\"));" + NL + "\t\t\t\t\tString localStatus = (String) (((java.util.Map) threadLocal.get()).get(\"status\"));" + NL + "\t\t\t\t\tif (localErrorCode != null) {" + NL + "\t\t\t\t\t\tif (this.errorCode == null || localErrorCode.compareTo(this.errorCode) > 0) {" + NL + "\t\t\t\t\t\t\tthis.errorCode = localErrorCode;" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t} " + NL + "\t\t\t\t\tif (!this.status.equals(\"failure\")) {" + NL + "\t\t\t\t\t\tthis.status = localStatus;" + NL + "\t\t\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\t\t\tpool.getTalendThreadResult().setErrorCode(this.errorCode);" + NL + "\t\t\t\t\tpool.getTalendThreadResult().setStatus(this.status);\t" + NL + "\t\t\t\t}//Run method" + NL + "\t\t\t}//ParallelThread class" + NL + "" + NL + "\t\t\tList<String[]> buffer = (List<String[]>) globalMap" + NL + "\t\t\t\t\t.get(\"PARALLEL_FLOW_BUFFER_";
  protected final String TEXT_2 = "\");" + NL + "" + NL + "\t\t\tif (pool.isFull()) {" + NL + "\t\t\t\tParallelThread pt = pool.getFreeThread();// wait for Free Thread" + NL + "\t\t\t\tif (pt!= null) {" + NL + "\t\t\t\t\tpt.putBuffer(buffer);// notify the ParallelThread" + NL + "\t\t\t\t}" + NL + "\t\t\t} else {" + NL + "\t\t\t\t// Start a new thread" + NL + "\t\t\t\t";
  protected final String TEXT_3 = "_ParallelThread pt = new ";
  protected final String TEXT_4 = "_ParallelThread(" + NL + "\t\t\t\t\t\tglobalMap, (Object[]) globalMap" + NL + "\t\t\t\t\t\t\t\t.get(\"PARALLEL_FLOW_LOCK_";
  protected final String TEXT_5 = "\"));" + NL + "\t\t\t\tpt.putBuffer(buffer);" + NL + "\t\t\t\tpool.execThread(pt);" + NL + "\t\t\t}" + NL + "\t\t} catch (InterruptedException e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t} catch (Exception te) {" + NL + "\t\t\tthrow new TalendException(te, currentComponent, globalMap);" + NL + "\t\t}";
  protected final String TEXT_6 = NL + NL + NL;
  protected final String TEXT_7 = NL + "}//end the resume" + NL;
  protected final String TEXT_8 = NL + "\t    \t\t\tif( resumeEntryMethodName == null || globalResumeTicket){" + NL + "\t    \t\t\t\tresumeUtil.addLog(\"CHECKPOINT\", \"CONNECTION:";
  protected final String TEXT_9 = ":";
  protected final String TEXT_10 = ":";
  protected final String TEXT_11 = "\", \"\", Thread.currentThread().getId() + \"\", \"\", \"\", \"\", \"\", \"\");" + NL + "\t\t\t\t\t}\t    \t\t\t\t    \t\t\t" + NL + "\t    \t\t";
  protected final String TEXT_12 = NL + "    \t\t";
  protected final String TEXT_13 = NL + "\t\t\t\tif(execStat){    \t" + NL + "\t\t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_14 = "\", 0, \"ok\");" + NL + "\t\t\t\t} " + NL + "\t\t\t";
  protected final String TEXT_15 = NL + "\t\t\t";
  protected final String TEXT_16 = "Process(globalMap); ";
  protected final String TEXT_17 = NL;
  protected final String TEXT_18 = NL + "\t";
  protected final String TEXT_19 = "Process(globalMap);";
  protected final String TEXT_20 = NL + NL + "\t" + NL + "\t} catch(Exception e) {\t" + NL + "" + NL + "\t\tthrow new TalendException(e, currentComponent, globalMap);" + NL + "\t" + NL + "\t} catch(java.lang.Error error) {" + NL + "\t";
  protected final String TEXT_21 = NL + "\t\trunStat.stopThreadStat();";
  protected final String TEXT_22 = NL + "\t\tthrow new java.lang.Error(error);" + NL + "" + NL + "\t}";
  protected final String TEXT_23 = NL + "\t\t\t\t";
  protected final String TEXT_24 = " finally{ ";
  protected final String TEXT_25 = NL + "\t\t\t\t//free memory for \"";
  protected final String TEXT_26 = "\"" + NL + "\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_27 = "\", null);" + NL + "\t";
  protected final String TEXT_28 = NL + "     \t\t\t";
  protected final String TEXT_29 = " finally{ ";
  protected final String TEXT_30 = NL + "     \t\t\t//free memory for \"";
  protected final String TEXT_31 = "\"" + NL + "     \t\t\tglobalMap.put(\"tHash_Lookup_";
  protected final String TEXT_32 = "\", null); ";
  protected final String TEXT_33 = "      \t" + NL + "      \t\t\t";
  protected final String TEXT_34 = " finally{ ";
  protected final String TEXT_35 = NL + "      \t\t\t//free memory for \"";
  protected final String TEXT_36 = "\"" + NL + "\t\t\t\tglobalMap.put(\"tHash_";
  protected final String TEXT_37 = "\", null);";
  protected final String TEXT_38 = "\t\t" + NL + "\t\t\t\t" + NL + "\t\t}\t\t" + NL + "\t";
  protected final String TEXT_39 = NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_40 = "_SUBPROCESS_STATE\", 1);" + NL + "}";
  protected final String TEXT_41 = NL + "/**" + NL + " * End of Function: ";
  protected final String TEXT_42 = "Process " + NL + " */";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	boolean stat = codeGenArgument.isStatistics();
	NodesSubTree subTree = (NodesSubTree) codeGenArgument.getArgument();
	
	for (INode node : subTree.getNodes()) {
		List<IMetadataTable> metadatas = node.getMetadataList();
		List< ? extends IConnection> conns = node.getOutgoingConnections();
	}

	boolean isParallelize = false;
	for (INode node : subTree.getNodes()) {
		if(node!=null){
			String parallelize = ElementParameterParser.getValue(node, "__PARALLELIZE__");
			if(parallelize!=null &&parallelize.equals("true")){
				// temporary modification : if tAsyncIn is available, this is always a parallelized process
				isParallelize = true;
			}
		}
	}
	if(isParallelize){

    stringBuffer.append(TEXT_1);
    stringBuffer.append((subTree.getName()).replaceAll("tAsyncIn", "tAsyncOut"));
    stringBuffer.append(TEXT_2);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(subTree.getName());
    stringBuffer.append(TEXT_4);
    stringBuffer.append((subTree.getName()).replaceAll("tAsyncIn", "tAsyncOut"));
    stringBuffer.append(TEXT_5);
    
	}else{

    stringBuffer.append(TEXT_6);
    
//this part for "Resume Management"
{
    INode firstNode = subTree.getNode(subTree.getName());
    
    List<String> beforeSubProcesses = subTree.getBeforeSubProcesses();
    
    //System.out.println(firstNode.getUniqueName());
    List<? extends IConnection> outConns = firstNode.getOutgoingConnections();
    
    //System.out.println(inConns.size());
    for(IConnection conn : outConns){
    
//    	boolean activeResume = "true".equals(ElementParameterParser.getValue(conn, "__RESUMING_CHECKPOINT__"));
    	boolean activeResume = true;
    	
    	//System.out.println("Test:" + ElementParameterParser.getValue(conn, "__RESUMING_CHECKPOINT__"));
    	
    	String uniqueNameTargetNode = conn.getTarget().getUniqueName();
    	
   		EConnectionType lineStyle = conn.getLineStyle();

    }
}

    stringBuffer.append(TEXT_7);
    
//this part for "Resume Management"
{
    INode firstNode = subTree.getNode(subTree.getName());
    
    List<String> beforeSubProcesses = subTree.getBeforeSubProcesses();
    
    //System.out.println(firstNode.getUniqueName());
    List<? extends IConnection> outConns = firstNode.getOutgoingConnections();
    
    //System.out.println(inConns.size());
    for(IConnection conn : outConns){
    
//    	boolean activeResume = "true".equals(ElementParameterParser.getValue(conn, "__RESUMING_CHECKPOINT__"));
    	boolean activeResume = true;
    	
    	//System.out.println("Test:" + ElementParameterParser.getValue(conn, "__RESUMING_CHECKPOINT__"));
    	
    	String uniqueNameTargetNode = conn.getTarget().getUniqueName();
    	
   		EConnectionType lineStyle = conn.getLineStyle();

		if(beforeSubProcesses.indexOf(uniqueNameTargetNode) != -1) {   		
	    	if(activeResume){
	    		
    stringBuffer.append(TEXT_8);
    stringBuffer.append(lineStyle.getName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(conn.getOutputId() > 0 ? conn.getOutputId() : "" );
    stringBuffer.append(TEXT_11);
    
	    	}
	    	
    stringBuffer.append(TEXT_12);
    
			if(stat){
			
    stringBuffer.append(TEXT_13);
    stringBuffer.append(conn.getUniqueName() );
    stringBuffer.append(TEXT_14);
    
			}

    stringBuffer.append(TEXT_15);
    stringBuffer.append( uniqueNameTargetNode );
    stringBuffer.append(TEXT_16);
    
    	}
    }
}

    stringBuffer.append(TEXT_17);
    	
	if (subTree.getRootNode().getProcess().getNodesOfType("tFlowMeterCatcher").size() > 0 
		&& subTree.getRootNode().getProcess().getNodesOfType("tFlowMeter").size() >0 ) {
		List<INode> metterCatchers = (List<INode>)subTree.getRootNode().getProcess().getNodesOfType("tFlowMeterCatcher");
		List<INode> metters = (List<INode>)subTree.getRootNode().getProcess().getNodesOfType("tFlowMeter");
		List<INode> nodes = (List<INode>)subTree.getNodes();
		
		boolean hasMetterProcess = false;
		for(INode tmp : nodes)
		{
			for(INode metter : metters)
			{
				if(tmp.getUniqueName().equals(metter.getUniqueName()))
				{
					hasMetterProcess = true;
					break;
				}
			}
			if(hasMetterProcess)
			{
				break;
			}
		}
		if(hasMetterProcess)
		{
			for (INode metterCatcher : metterCatchers) {
				

    stringBuffer.append(TEXT_18);
    stringBuffer.append(metterCatcher.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_19);
          

			}
		}
	}

    stringBuffer.append(TEXT_20);
    
	if(stat){

    stringBuffer.append(TEXT_21);
    
	}

    stringBuffer.append(TEXT_22);
    
	//generate the code to free memory for lookup link and virtual component buffered datas 
	boolean firstIn = true;
	boolean needGc = false;
	List<INode> nodes = subTree.getNodes();
	for(INode node:nodes){
	
	boolean isVirtualGenerateNode = node.isVirtualGenerateNode();
	
	//check the virtual component generate node
	if(isVirtualGenerateNode){
		String origin  = ElementParameterParser.getValue(node, "__ORIGIN__");
		if(origin != null && !"".equals(origin)){
			if(!needGc) needGc = true;
	
    stringBuffer.append(TEXT_23);
    if(firstIn){
    stringBuffer.append(TEXT_24);
     firstIn = false;}
    stringBuffer.append(TEXT_25);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_27);
    
		}
	}else{
	//check the Lookup link
		List<? extends IConnection> lookupInput = node.getIncomingConnections(EConnectionType.FLOW_REF);
		if(lookupInput!=null && lookupInput.size()>0){
			if(!needGc) needGc = true;
			//check the tMap like this, instanceof can't work			
			if(node.getClass().getName().equals("org.talend.designer.mapper.MapperComponent")){
				for(IConnection connection:lookupInput){						
     
    stringBuffer.append(TEXT_28);
    if(firstIn){
    stringBuffer.append(TEXT_29);
     firstIn = false;}
    stringBuffer.append(TEXT_30);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(connection.getName() );
    stringBuffer.append(TEXT_32);
    
            	}
            }else{
            	for(IConnection connection:lookupInput){
      
    stringBuffer.append(TEXT_33);
    if(firstIn){
    stringBuffer.append(TEXT_34);
     firstIn = false;}
    stringBuffer.append(TEXT_35);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(connection.getName() );
    stringBuffer.append(TEXT_37);
          		 
				} 
			} 
		}
	}
}

	if(needGc) {
	
    stringBuffer.append(TEXT_38);
    
	}
}//isParallelize
	
    stringBuffer.append(TEXT_39);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_40);
     if (subTree.isMethodSizeNeeded()){ 
    stringBuffer.append(TEXT_41);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_42);
     } 
    return stringBuffer.toString();
  }
}
