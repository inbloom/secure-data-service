package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.INode;
import org.talend.core.model.temp.ECodePart;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.codegen.config.NodesSubTree;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Component_part_footerJava
{
  protected static String nl;
  public static synchronized Component_part_footerJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Component_part_footerJava result = new Component_part_footerJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "if(execStat){" + NL + " runStat.updateStatOnConnection(\"";
  protected final String TEXT_2 = "\"" + NL + " \t+iterateId,2, 0); " + NL + "}";
  protected final String TEXT_3 = NL + " ";
  protected final String TEXT_4 = NL + "     ";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL + "    globalMap.put(\"USE_CONDITION\",Boolean.TRUE);" + NL + "    globalMap.put(\"TRACE_CONDITION\", Boolean.FALSE);";
  protected final String TEXT_8 = NL + "  " + NL + "  if (";
  protected final String TEXT_9 = " !=null) {" + NL + "  globalMap.put(\"ENABLE_TRACES_CONNECTION_";
  protected final String TEXT_10 = "\",Boolean.TRUE);" + NL + "  if (runTrace.isPause()) {" + NL + "   while (runTrace.isPause()) {" + NL + "   " + NL + "   \tThread.sleep(100);" + NL + "   }" + NL + "  }\t else {" + NL + "  \t" + NL + "   // here we dump the line content for trace purpose" + NL + "   runTrace.sendTrace(\"";
  protected final String TEXT_11 = "\",\"";
  protected final String TEXT_12 = "\",\"";
  protected final String TEXT_13 = " ";
  protected final String TEXT_14 = "=\"+";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = "+\"|";
  protected final String TEXT_17 = "\");" + NL + "\t}" + NL + "\t";
  protected final String TEXT_18 = "     \t\t" + NL + "       if (";
  protected final String TEXT_19 = NL + "           ";
  protected final String TEXT_20 = "(";
  protected final String TEXT_21 = "    ";
  protected final String TEXT_22 = NL + "              ";
  protected final String TEXT_23 = NL + "\t              ";
  protected final String TEXT_24 = NL + "                  ";
  protected final String TEXT_25 = "  ";
  protected final String TEXT_26 = NL + "                ";
  protected final String TEXT_27 = NL + "             )";
  protected final String TEXT_28 = NL + "         ";
  protected final String TEXT_29 = NL + "        true";
  protected final String TEXT_30 = "      " + NL + "    ){" + NL + "    \tglobalMap.put(\"TRACE_CONDITION\", Boolean.TRUE);" + NL + "    }";
  protected final String TEXT_31 = NL + "  }";
  protected final String TEXT_32 = NL + NL + "ok_Hash.put(\"";
  protected final String TEXT_33 = "\", true);" + NL + "end_Hash.put(\"";
  protected final String TEXT_34 = "\", System.currentTimeMillis());" + NL;
  protected final String TEXT_35 = NL;
  protected final String TEXT_36 = ".addMessage(\"end\",\"";
  protected final String TEXT_37 = "\", end_Hash.get(\"";
  protected final String TEXT_38 = "\")-start_Hash.get(\"";
  protected final String TEXT_39 = "\"));";
  protected final String TEXT_40 = NL;
  protected final String TEXT_41 = "Process(globalMap);";
  protected final String TEXT_42 = NL + "\t\t\t\tif(execStat){   " + NL + "   \t \t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_43 = "\", 0, \"ok\");" + NL + "\t\t\t\t}";
  protected final String TEXT_44 = NL + "\t\t\t\t";
  protected final String TEXT_45 = "Process(globalMap);";
  protected final String TEXT_46 = NL + "   \t\t\tif (";
  protected final String TEXT_47 = ") {" + NL + "   \t\t\t\t";
  protected final String TEXT_48 = NL + "\t\t\t\t\tif(execStat){   " + NL + "   \t \t\t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_49 = "\", 0, \"true\");" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_50 = NL + "    \t\t\t";
  protected final String TEXT_51 = "Process(globalMap);" + NL + "   \t\t\t}" + NL + "" + NL + "\t\t\t";
  protected final String TEXT_52 = "   " + NL + "   \t\t\t\telse{" + NL + "\t\t\t\t\tif(execStat){   " + NL + "   \t \t\t\t\t\trunStat.updateStatOnConnection(\"";
  protected final String TEXT_53 = "\", 0, \"false\");" + NL + "\t\t\t\t\t}   \t " + NL + "   \t\t\t\t}";
  protected final String TEXT_54 = NL;
  protected final String TEXT_55 = NL + "\t\t\tmtp_";
  protected final String TEXT_56 = ".waitForEndOfQueue();" + NL + "\t\t\t" + NL + "\t\t\tTalendThread errorThread_";
  protected final String TEXT_57 = " = mtp_";
  protected final String TEXT_58 = ".getErrorThread();" + NL;
  protected final String TEXT_59 = NL + "\tif(errorThread_";
  protected final String TEXT_60 = " != null) {" + NL + "\t\tif (errorThread_";
  protected final String TEXT_61 = ".errorCode != null) {" + NL + "\t\t\tif (errorCode == null" + NL + "\t\t\t\t\t|| errorThread_";
  protected final String TEXT_62 = ".errorCode.compareTo(errorCode) > 0) {" + NL + "\t\t\t\terrorCode = errorThread_";
  protected final String TEXT_63 = ".errorCode;" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!status.equals(\"failure\")) {" + NL + "\t\t\tstatus = errorThread_";
  protected final String TEXT_64 = ".status;" + NL + "\t\t}" + NL + "\t}else{\t\t\t\t" + NL + "\t\tInteger threadErrorCode = mtp_";
  protected final String TEXT_65 = ".getTalendThreadResult().getErrorCode();" + NL + "\t\tString threadStatus = mtp_";
  protected final String TEXT_66 = ".getTalendThreadResult().getStatus();" + NL + "\t\t" + NL + "\t\tif (threadErrorCode != null) {" + NL + "\t\t\tif (errorCode == null" + NL + "\t\t\t\t\t|| threadErrorCode.compareTo(errorCode) > 0) {" + NL + "\t\t\t\terrorCode = threadErrorCode;" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!status.equals(\"failure\")) {" + NL + "\t\t\tstatus = threadStatus;" + NL + "\t\t}" + NL + "\t }\t\t\t";
  protected final String TEXT_67 = NL + "\tif(errorThread_";
  protected final String TEXT_68 = " != null) {" + NL + "\t\tInteger localErrorCode = (Integer) (((java.util.Map) threadLocal.get()).get(\"errorCode\"));" + NL + "\t\tString localStatus = (String) (((java.util.Map) threadLocal.get()).get(\"status\"));" + NL + "\t\t" + NL + "\t\tif (errorThread_";
  protected final String TEXT_69 = ".errorCode != null) {" + NL + "\t\t\tif (localErrorCode == null || errorThread_";
  protected final String TEXT_70 = ".errorCode.compareTo(localErrorCode) > 0) {" + NL + "\t\t\t\t((java.util.Map) threadLocal.get()).put(\"errorCode\", errorThread_";
  protected final String TEXT_71 = ".errorCode);" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!localStatus.equals(\"failure\")) {" + NL + "\t\t\t((java.util.Map) threadLocal.get()).put(\"status\", errorThread_";
  protected final String TEXT_72 = ".status);" + NL + "\t\t}" + NL + "\t}else{" + NL + "\t\tInteger threadErrorCode = mtp_";
  protected final String TEXT_73 = ".getTalendThreadResult().getErrorCode();" + NL + "\t\tString threadStatus = mtp_";
  protected final String TEXT_74 = ".getTalendThreadResult().getStatus();" + NL + "\t\t" + NL + "\t\tInteger localErrorCode = (Integer) (((java.util.Map) threadLocal" + NL + "\t\t\t\t.get()).get(\"errorCode\"));" + NL + "\t\tString localStatus = (String) (((java.util.Map) threadLocal" + NL + "\t\t\t\t.get()).get(\"status\"));" + NL + "" + NL + "\t\tif (threadErrorCode != null) {" + NL + "\t\t\tif (localErrorCode == null" + NL + "\t\t\t\t\t|| threadErrorCode.compareTo(localErrorCode) > 0) {" + NL + "\t\t\t\t((java.util.Map) threadLocal.get()).put(\"errorCode\"," + NL + "\t\t\t\t\t\tthreadErrorCode);" + NL + "\t\t\t}" + NL + "\t\t} " + NL + "\t\tif (!localStatus.equals(\"failure\")) {" + NL + "\t\t\t((java.util.Map) threadLocal.get()).put(\"status\"," + NL + "\t\t\t\t\tthreadStatus);" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_75 = NL + "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_76 = NL;
  protected final String TEXT_77 = NL + "\ttos_count_";
  protected final String TEXT_78 = "++;";
  protected final String TEXT_79 = NL + NL + "/**" + NL + " * [";
  protected final String TEXT_80 = " ";
  protected final String TEXT_81 = " ] stop" + NL + " */";

    // add the list of the connection names to avoid to declare two times the same name.
    public String createPrivateClassInstance(INode node, String parentLastRoot, boolean force) {
    	return createPrivateClassInstance(node, parentLastRoot, force, new HashSet<String>());
    }

    public String createPrivateClassInstance(INode n, String parentLastRoot, boolean force, Set<String> names) {
        String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force)||(n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
	                    toReturn += conn.getName() + "Struct " + conn.getName() + " = new " + conn.getName()
	                            + "Struct();\n";
	                    names.add(conn.getName());
	                    parentLastRoot = conn.getName();
                    }
	            } else {
                    // copy
                    if (!names.contains(conn.getName())) {
	                    toReturn += parentLastRoot + "Struct " + conn.getName() + " = " + parentLastRoot + ";\n";
	                    names.add(conn.getName());
                    }
                }
                if(!conn.getTarget().getComponent().useMerge()) {
                   toReturn += createPrivateClassInstance(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)||conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
            	toReturn += createPrivateClassInstance(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
    
    public String createPriveClassMethodDeclaration(INode n, String parentLastRoot, boolean force, Set<String> names) {
    	String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force)||(n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
	                    toReturn += conn.getName() + "Struct " + conn.getName() + ",";
	                    names.add(conn.getName());
	                    parentLastRoot = conn.getName();
                    }
	            } else {
                    // copy
                    if (!names.contains(conn.getName())) {
	                    toReturn += parentLastRoot + "Struct " + conn.getName() + ",";
	                    names.add(conn.getName());
                    }
                }
                if(!conn.getTarget().getComponent().useMerge()) {
                   toReturn += createPriveClassMethodDeclaration(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)||conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
            	toReturn += createPriveClassMethodDeclaration(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
    
    public String createPrivateClassMethodInstance(INode n, String parentLastRoot, boolean force, Set<String> names) {
        String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force) || (n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
                        toReturn += conn.getName() + ",";
                        names.add(conn.getName());
                        parentLastRoot = conn.getName();
                    }
                } else {
                    // copy
                    if (!names.contains(conn.getName())) {
                        toReturn += conn.getName() + ",";
                        names.add(conn.getName());
                    }
                }
                if (!conn.getTarget().getComponent().useMerge()) {
                    toReturn += createPrivateClassMethodInstance(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)
                    || conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
                toReturn += createPrivateClassMethodInstance(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
		 
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
IProcess process = node.getProcess();
String startNodeCid=node.getDesignSubjobStartNode().getUniqueName();
 
NodesSubTree subTree = (NodesSubTree) codeGenArgument.getSubTree();
ECodePart codePart = codeGenArgument.getCodePart();
boolean trace = codeGenArgument.isTrace();
boolean stat = codeGenArgument.isStatistics();

boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();

Set<IConnection> connSet =  new HashSet<IConnection>();
connSet.addAll(node.getIncomingConnections(EConnectionType.FLOW_MAIN));
connSet.addAll(node.getIncomingConnections(EConnectionType.FLOW_MERGE));

Set<IConnection> iterateConnSet =  new HashSet<IConnection>();
iterateConnSet.addAll(node.getOutgoingConnections(EConnectionType.ITERATE));

List<IConnection> allSubProcessConnection = codeGenArgument.getAllMainSubTreeConnections();

if((codePart.equals(ECodePart.END))&&(stat)&&connSet.size()>0){
  for(IConnection con:connSet){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(con.getUniqueName());
    stringBuffer.append(TEXT_2);
    
  }
}
    stringBuffer.append(TEXT_3);
    

 if (codePart.equals(ECodePart.MAIN)) {
 //================================TDI-17183 start========================================
/**
	if the node is autopropagate the schema, then the output row should evaluate the input row
	before the track code part(TDI-17183)
*/
  String inputColName = null;
  if (node.getIncomingConnections()!=null) {
   for (IConnection incomingConn : node.getIncomingConnections()) {
    if (incomingConn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
     inputColName = incomingConn.getName();
     break;
    }
   }
  }
  List<IMetadataTable> metadatas = node.getMetadataList();
  if ((!node.isSubProcessStart())&&(NodeUtil.isDataAutoPropagated(node))) {
   if (inputColName!=null) {
    if ((metadatas != null) && (metadatas.size() > 0)) {
     IMetadataTable outputMetadata = metadatas.get(0);
     if (outputMetadata != null) {
      for (IConnection outputConnection : node.getOutgoingConnections()) {
       if (outputConnection.getLineStyle().equals(EConnectionType.FLOW_MAIN) || outputConnection.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
       
       	//void the case: row1.username=row1.username
       	if(!outputConnection.getName().equals(inputColName)){//111
			//void the case: row1Struct row1 = new row1Struct(); row1Struct row3 = row1;
            //for (IMetadataColumn column : outputMetadata.getListColumns()) {
    
    stringBuffer.append(TEXT_4);
    stringBuffer.append(outputConnection.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inputColName );
    stringBuffer.append(TEXT_6);
     
           	//}
       	}//111
       	
       }
      }
     }
    }
   }
  }
//======================================TDI-17183 end=====================================
  boolean traceCodeGenerated = false;     
  for (IConnection conn : node.getOutgoingConnections()) {
     boolean use_reaceenble = ("true").equals(ElementParameterParser.getValue(conn, "__TRACES_CONNECTION_ENABLE__"));
  	if(node.isSubProcessStart() && node.isSubProcessContainTraceBreakpoint() && !traceCodeGenerated) {
  	  traceCodeGenerated = true;
  
    stringBuffer.append(TEXT_7);
    
    }
   IMetadataTable metadata = conn.getMetadataTable();
   if (metadata!=null) {
    if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) ||
        conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
     if (trace && use_reaceenble) { 
  
    stringBuffer.append(TEXT_8);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(startNodeCid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(startNodeCid );
    stringBuffer.append(TEXT_12);
    
         for (IMetadataColumn column : metadata.getListColumns()) {
   
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_16);
    
         }

    stringBuffer.append(TEXT_17);
    
	    
     boolean use_breakout = ("true").equals(ElementParameterParser.getValue(conn, "__ACTIVEBREAKPOINT__"));
     boolean use_advanced = ("true").equals(ElementParameterParser.getValue(conn, "__USE_ADVANCED__"));
      List<Map<String, String>> keyColumns = (List<Map<String,String>>)ElementParameterParser.getObjectValue( conn,  "__CONDITIONS__");
   		String advancedCondition = ElementParameterParser.getValue(conn, "__ADVANCED_COND__");
     String logical = ElementParameterParser.getValue(conn,"__LOGICAL_OP__");
      if (metadata != null) {
      if(use_breakout && use_reaceenble){
    stringBuffer.append(TEXT_18);
     
      if ((keyColumns!=null &&keyColumns.size() > 0)|| use_advanced) { //111
        int ii = 0;

        for (Map<String, String> keyColumn : keyColumns){ // 222 -> 
          ii++;
          
    stringBuffer.append(TEXT_19);
    stringBuffer.append( ii == 1 ? "" : logical );
    stringBuffer.append(TEXT_20);
     
            if (!("").equals(keyColumn.get("FUNCTION"))) {
            
    stringBuffer.append(TEXT_21);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(keyColumn.get("FUNCTION").replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    
            }else {
              IMetadataColumn rightColumn = metadata.getColumn(keyColumn.get("INPUT_COLUMN"));
              JavaType javaType = JavaTypesManager.getJavaTypeFromId(rightColumn.getTalendType());

              if (!javaType.isPrimitive()) {
                //this is only for bug:8133, when "Oject" type, and "Empty" function, and compare with "null"
                if (keyColumn.get("RVALUE") != null && keyColumn.get("RVALUE").equals("null")){
	            
    stringBuffer.append(TEXT_23);
    stringBuffer.append("$source $operator $target".replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    }else{
    stringBuffer.append(TEXT_24);
    stringBuffer.append("$source == null? false : $source.compareTo($target) $operator 0".replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    
	            }
              } else {
              
    stringBuffer.append(TEXT_25);
    stringBuffer.append(TEXT_26);
    stringBuffer.append("$source $operator $target".replace("$source", conn.getName() + "." + keyColumn.get("INPUT_COLUMN")).replace("$target", keyColumn.get("RVALUE")).replace("$operator", keyColumn.get("OPERATOR")) );
    
	          }
            }
            
            
    stringBuffer.append(TEXT_27);
    
        } // <- 222
        
    stringBuffer.append(TEXT_28);
    stringBuffer.append(use_advanced? (keyColumns.size() < 1 ? "" : logical) + advancedCondition.replace("input_row", conn.getName()) : "");
     
      } else { //111 
      
    stringBuffer.append(TEXT_29);
    
      } //111
      
    stringBuffer.append(TEXT_30);
    
       }

    stringBuffer.append(TEXT_31);
    
      }
     }
    }
   }
  }
  
 }
 if (codePart.equals(ECodePart.END)) {

    stringBuffer.append(TEXT_32);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_34);
    
  String statCatcher = ElementParameterParser.getValue(node,"__TSTATCATCHER_STATS__");
  if ((node.getProcess().getNodesOfType("tStatCatcher").size() > 0) & (statCatcher.equals("true"))) {
   for (INode statCatcherNode : node.getProcess().getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_35);
    stringBuffer.append(statCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(statCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_41);
    
   }
  }
  
  for (IConnection outgoingConn : node.getOutgoingConnections()) {
  	if (outgoingConn.getTarget().isActivate()) {
		
	         
  	    if (outgoingConn.getLineStyle().equals(EConnectionType.ON_COMPONENT_OK)) { 
			//send the ok status to socket
			if(stat){

    stringBuffer.append(TEXT_42);
    stringBuffer.append(outgoingConn.getUniqueName() );
    stringBuffer.append(TEXT_43);
    
			}
			
			
			/*
			This boolean is created for the ON_COMPONENT_OK virtual components. 
			This technic is used on the tWriteXMLField for example. Since the sub method is called in the Thread, we do not have to generate again in the component footer.
			*/
			boolean generateMethodCall = false; 
			if((!node.isVirtualGenerateNode())) {
				generateMethodCall = true;
			} else {
				if(node.getOutgoingConnections(EConnectionType.ON_ROWS_END).size()>0 || node.getOutgoingConnections(EConnectionType.FLOW_MAIN).size()>0) {
					generateMethodCall = true;
				}
				// Else, that means the component is a ON_COMPONENT_OK virtual component typed.
			}
			
			if(generateMethodCall) {

    stringBuffer.append(TEXT_44);
    stringBuffer.append(outgoingConn.getTarget().getUniqueName() );
    stringBuffer.append(TEXT_45);
      
			}
  	    }        
       

   		if (outgoingConn.getLineStyle().equals(EConnectionType.RUN_IF)) {
		
    stringBuffer.append(TEXT_46);
    stringBuffer.append(outgoingConn.getCondition() );
    stringBuffer.append(TEXT_47);
    //send the true status to socket
				if(stat){
				
    stringBuffer.append(TEXT_48);
    stringBuffer.append(outgoingConn.getUniqueName() );
    stringBuffer.append(TEXT_49);
    }
    stringBuffer.append(TEXT_50);
    stringBuffer.append(outgoingConn.getTarget().getUniqueName() );
    stringBuffer.append(TEXT_51);
    
			//send the false status to socket
			if(stat){
			
    stringBuffer.append(TEXT_52);
    stringBuffer.append(outgoingConn.getUniqueName() );
    stringBuffer.append(TEXT_53);
    
			}
   		}
   	}
  }
 }

    stringBuffer.append(TEXT_54);
     
	//This part in order to feedback with the iterate_subprocess_header.javajet and iterate_subprocess_footer.javajet
	
	boolean parallelIterate = false;
	for (IConnection iterateConn : iterateConnSet) { 
		parallelIterate = "true".equals(ElementParameterParser.getValue(iterateConn, "__ENABLE_PARALLEL__"));
		if (parallelIterate) {
			if (codePart.equals(ECodePart.END)) {
			String iterateTargetNodeName = iterateConn.getTarget().getUniqueName();				

    stringBuffer.append(TEXT_55);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_58);
    if(!isRunInMultiThread){
    stringBuffer.append(TEXT_59);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_66);
    }else{
    stringBuffer.append(TEXT_67);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(iterateTargetNodeName );
    stringBuffer.append(TEXT_74);
    }
    stringBuffer.append(TEXT_75);
    
			}
			continue;
		}
	}

    stringBuffer.append(TEXT_76);
    if(codePart.equals(ECodePart.MAIN)){ 
    stringBuffer.append(TEXT_77);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_78);
    }
    stringBuffer.append(TEXT_79);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(codePart );
    stringBuffer.append(TEXT_81);
    return stringBuffer.toString();
  }
}