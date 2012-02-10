package org.talend.designer.codegen.translators.business.salesforce;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TSalesforceInputEndJava
{
  protected static String nl;
  public static synchronized TSalesforceInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceInputEndJava result = new TSalesforceInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "                // (for salesforce wizard preview)" + NL + "                if(limit_";
  protected final String TEXT_2 = " > 0 && nb_line_";
  protected final String TEXT_3 = " >= limit_";
  protected final String TEXT_4 = "){ " + NL + "                \tbContinue_";
  protected final String TEXT_5 = " = false;" + NL + "                \tbreak;" + NL + "                }" + NL + "            " + NL + "            }" + NL + "            // handle the loop + 1 problem by checking the most recent queryResult" + NL + "            if (qr_";
  protected final String TEXT_6 = ".getDone()) {" + NL + "                bContinue_";
  protected final String TEXT_7 = " = false;" + NL + "            } else {" + NL + "                qr_";
  protected final String TEXT_8 = " = sfMgr_";
  protected final String TEXT_9 = ".queryMore(qr_";
  protected final String TEXT_10 = ".getQueryLocator(),new Integer(";
  protected final String TEXT_11 = "));" + NL + "            \ttopqr_";
  protected final String TEXT_12 = " = new org.talend.salesforce.TopQueryResult();" + NL + "\t\t\t\ttopqr_";
  protected final String TEXT_13 = ".processTopQueryResult(qr_";
  protected final String TEXT_14 = ");" + NL + "            }" + NL + "        }";
  protected final String TEXT_15 = NL + "\t\t\tsfMgr_";
  protected final String TEXT_16 = ".logout();";
  protected final String TEXT_17 = NL + "globalMap.put(\"";
  protected final String TEXT_18 = "_NB_LINE\", nb_line_";
  protected final String TEXT_19 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();
String batchSize = ElementParameterParser.getValue(node,"__BATCH_SIZE__");
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);
	
	if (metadata != null) {
		List<IMetadataColumn> columnList = metadata.getListColumns();
		int nbSchemaColumns = columnList.size();
			
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		// if output columns are defined
		if (nbSchemaColumns > 0 && outgoingConns != null && outgoingConns.size() > 0){

			IConnection outgoingConn = outgoingConns.get(0);

				if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(batchSize);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    
				}
			}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    
	}
}

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    return stringBuffer.toString();
  }
}
