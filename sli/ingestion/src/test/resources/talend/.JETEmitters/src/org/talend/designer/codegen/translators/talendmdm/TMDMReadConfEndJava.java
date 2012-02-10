package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import java.util.List;

public class TMDMReadConfEndJava
{
  protected static String nl;
  public static synchronized TMDMReadConfEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMDMReadConfEndJava result = new TMDMReadConfEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t} " + NL + "\t\t\t\t";
  protected final String TEXT_2 = NL + NL + "\t\t\t\t";
  protected final String TEXT_3 = NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\tstub_";
  protected final String TEXT_4 = ".logout(new org.talend.mdm.webservice.WSLogout());" + NL + "\t\t\t\t\t} catch(Exception e) {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_5 = NL + "\t\t\t\t\t\tthrow e;" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_6 = NL + "\t\t\t\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_7 = NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_8 = NL + "\t\t\t\tglobalMap.put(\"";
  protected final String TEXT_9 = "_NB_LINE\",nb_line_";
  protected final String TEXT_10 = ");   " + NL + "\t\t\t";
  protected final String TEXT_11 = NL + "          ";
  protected final String TEXT_12 = NL + "            ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
	boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;
	System.out.println(dieOnError);
	if(destination!=null && !"".equals(destination)){
		cid = destination;
	}
	boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
	
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);
	
	if (metadata != null) {
	
		List<IMetadataColumn> columnList = metadata.getListColumns();
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		// if output columns are defined
		if (outgoingConns != null && outgoingConns.size() > 0){
		
			IConnection outgoingConn = outgoingConns.get(0);
			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { // start 1
				boolean useWhere = ("true").equals(ElementParameterParser.getValue(node, "__USE_ITEMS__"));
				if(useWhere){
				
    stringBuffer.append(TEXT_1);
    
				}
				
    stringBuffer.append(TEXT_2);
    if(!useExistingConn){
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    if(dieOnError) {
    stringBuffer.append(TEXT_5);
     } else { 
    stringBuffer.append(TEXT_6);
     } 
    stringBuffer.append(TEXT_7);
    }
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
			}
		}
	}
}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
