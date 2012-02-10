package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.codegen.config.SubTreeArgument;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.utils.NodeUtil;
import java.util.List;

public class Subtree_beginJava
{
  protected static String nl;
  public static synchronized Subtree_beginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Subtree_beginJava result = new Subtree_beginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "// Start of branch \"";
  protected final String TEXT_2 = "\"" + NL + "if(";
  protected final String TEXT_3 = " != null) { ";
  protected final String TEXT_4 = NL + "\t\t\t";
  protected final String TEXT_5 = " = null;";
  protected final String TEXT_6 = NL + "\t // start of joined table loop" + NL + "\tfor (";
  protected final String TEXT_7 = "Struct cur_";
  protected final String TEXT_8 = " :";
  protected final String TEXT_9 = "_List) {" + NL + "\t\t";
  protected final String TEXT_10 = " = cur_";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = NL + NL;
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	SubTreeArgument subTreeArgument = (SubTreeArgument)codeGenArgument.getArgument();
	IConnection connection = subTreeArgument.getInputSubtreeConnection();
	if(subTreeArgument.isSourceComponentHasConditionnalOutputs() 
	&& connection.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 

    stringBuffer.append(TEXT_1);
    stringBuffer.append( connection.getName());
    stringBuffer.append(TEXT_2);
    stringBuffer.append( connection.getName());
    stringBuffer.append(TEXT_3);
    
	java.util.Set<? extends IConnection> conns = NodeUtil.getAllInLineJobConnections(connection.getTarget());
	//Bug15479, if there is any non-Main_Flow link(OK/ERROR/IF/ITERATOR...) followed, REJECT will not assigned to NULL at here
	boolean flag = true;
	for(IConnection conn : conns){
		if(conn.getLineStyle().compareTo(org.talend.core.model.process.EConnectionType.FLOW_MAIN) > 0){
			flag = false;
		}
	}
    for (Object obj : conns) {
    	IConnection conn = (IConnection)obj;
    	// Bug16902, because conn.isMonitorConnection() always return false in AbstractConnection, use conn.getMetadataTable().getTableName().contains("vFlowMeter_") instead
    	if("REJECT".equals(conn.getConnectorName()) && flag && !conn.getMetadataTable().getTableName().contains("vFlowMeter_")) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append( conn.getName());
    stringBuffer.append(TEXT_5);
    
    	}
    }
	if (connection.getSource().isUseLoopOnConditionalOutput(connection.getName())) {

    stringBuffer.append(TEXT_6);
    stringBuffer.append( connection.getName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append( connection.getName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append( connection.getName());
    stringBuffer.append(TEXT_9);
    stringBuffer.append( connection.getName());
    stringBuffer.append(TEXT_10);
    stringBuffer.append( connection.getName());
    stringBuffer.append(TEXT_11);
    
	}

    stringBuffer.append(TEXT_12);
     
	}

    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
