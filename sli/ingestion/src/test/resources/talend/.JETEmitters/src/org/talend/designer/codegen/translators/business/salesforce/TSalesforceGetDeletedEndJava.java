package org.talend.designer.codegen.translators.business.salesforce;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TSalesforceGetDeletedEndJava
{
  protected static String nl;
  public static synchronized TSalesforceGetDeletedEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceGetDeletedEndJava result = new TSalesforceGetDeletedEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t\t\t}\t" + NL + "\t\t\t\t\t// handle the loop + 1 problem by checking the most recent queryResult" + NL + "\t\t            if (qr_";
  protected final String TEXT_2 = ".getDone()) {" + NL + "\t\t                bContinue_";
  protected final String TEXT_3 = " = false;" + NL + "\t\t            } else {" + NL + "\t\t                qr_";
  protected final String TEXT_4 = " = sfMgr_";
  protected final String TEXT_5 = ".queryMore(qr_";
  protected final String TEXT_6 = ".getQueryLocator(), batchSize_";
  protected final String TEXT_7 = ");" + NL + "\t\t            }" + NL + "\t\t\t\t}" + NL + "\t\t\t\tsoql_";
  protected final String TEXT_8 = " = new StringBuilder();" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_9 = NL + "\t\tsfMgr_";
  protected final String TEXT_10 = ".logout();\t";
  protected final String TEXT_11 = NL + "globalMap.put(\"";
  protected final String TEXT_12 = "_NB_LINE\", nb_line_";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();

    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata != null) {		
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
		if (outgoingConns != null && outgoingConns.size() > 0){
			IConnection outgoingConn = outgoingConns.get(0);
			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
			}

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
		}
	}
}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
