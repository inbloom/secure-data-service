package org.talend.designer.codegen.translators.business.marketo;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TMarketoInputEndJava
{
  protected static String nl;
  public static synchronized TMarketoInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMarketoInputEndJava result = new TMarketoInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}\t";
  protected final String TEXT_2 = NL + "\t\t\t\t\t\t\t}" + NL + "\t    \t\t\t\t\tif (leads_";
  protected final String TEXT_3 = ".getRemainingCount() != 0) {" + NL + "\t                            streamPosition_";
  protected final String TEXT_4 = " = leads_";
  protected final String TEXT_5 = ".getNewStreamPosition();" + NL + "\t                        } else {" + NL + "\t                            break;" + NL + "\t                        }" + NL + "\t                    }\t" + NL + "                    }\t\t\t";
  protected final String TEXT_6 = NL + "\t            \t\t\t}" + NL + "\t                        if (activities_";
  protected final String TEXT_7 = ".getRemainingCount() != 0) {" + NL + "\t                            streamPosition_";
  protected final String TEXT_8 = " = activities_";
  protected final String TEXT_9 = ".getNewStartPosition();" + NL + "\t                        } else {" + NL + "\t                            break;" + NL + "\t                        }" + NL + "                    \t}else{" + NL + "                    \t\tbreak;" + NL + "                    \t}" + NL + "                  \t}";
  protected final String TEXT_10 = NL + "\t\t\t\t\t\t\t}" + NL + "\t                        if (changes_";
  protected final String TEXT_11 = ".getRemainingCount() != 0) {" + NL + "\t                            streamPosition_";
  protected final String TEXT_12 = " = changes_";
  protected final String TEXT_13 = ".getNewStartPosition();" + NL + "\t                        } else {" + NL + "\t                            break;" + NL + "\t                        }" + NL + "                        }" + NL + "                    }\t\t\t\t";
  protected final String TEXT_14 = "\t" + NL + "globalMap.put(\"";
  protected final String TEXT_15 = "_NB_CALL\",nb_call_";
  protected final String TEXT_16 = ");     ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) { //1
	IMetadataTable metadata = metadatas.get(0);
	if (metadata != null) { //2
		List<IMetadataColumn> columnList = metadata.getListColumns();
		int nbSchemaColumns = columnList.size();			
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
		if (nbSchemaColumns > 0 && outgoingConns != null && outgoingConns.size() > 0){ //3
			IConnection outgoingConn = outgoingConns.get(0);
			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { //4
				String operation = ElementParameterParser.getValue(node, "__OPERATION__");

				if("getLead".equals(operation)){

    stringBuffer.append(TEXT_1);
    			
				}else if("getMutipleLeads".equals(operation)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
				}else if("getLeadActivity".equals(operation)){

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
				}else if("getLeadChanges".equals(operation)){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    
				}
			}
		}
	}
}	

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    return stringBuffer.toString();
  }
}
