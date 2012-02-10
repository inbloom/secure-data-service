package org.talend.designer.codegen.translators.business.microsoft_crm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TMicrosoftCrmInputEndJava
{
  protected static String nl;
  public static synchronized TMicrosoftCrmInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMicrosoftCrmInputEndJava result = new TMicrosoftCrmInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    \t\t\t\t}" + NL + "    \t\t\t\tif(fetchMore_";
  protected final String TEXT_2 = "){" + NL + "\t                    pageNumber_";
  protected final String TEXT_3 = "++;" + NL + "\t                    pagingInfo_";
  protected final String TEXT_4 = ".setPageNumber(pageNumber_";
  protected final String TEXT_5 = ");" + NL + "\t                    pagingInfo_";
  protected final String TEXT_6 = ".setPagingCookie(pagingCookie_";
  protected final String TEXT_7 = ");" + NL + "\t                    query_";
  protected final String TEXT_8 = ".setPageInfo(pagingInfo_";
  protected final String TEXT_9 = ");" + NL + "\t                    rmr_";
  protected final String TEXT_10 = ".setQuery(query_";
  protected final String TEXT_11 = ");" + NL + "\t                    execute_";
  protected final String TEXT_12 = ".setRequest(rmr_";
  protected final String TEXT_13 = ");" + NL + "\t        \t\t\texecuteDoc_";
  protected final String TEXT_14 = ".setExecute(execute_";
  protected final String TEXT_15 = ");" + NL + "\t        \t\t\texecuteDoc_";
  protected final String TEXT_16 = " = com.microsoft.schemas.crm._2007.webservices.ExecuteDocument.Factory.parse(executeDoc_";
  protected final String TEXT_17 = ".toString());" + NL + "        \t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_19 = "_NB_LINE\",nb_line_";
  protected final String TEXT_20 = ");      ";

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
		List<IMetadataColumn> columnList = metadata.getListColumns();
		int nbSchemaColumns = columnList.size();
			
		List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();

		// if output columns are defined
		if (nbSchemaColumns > 0 && outgoingConns != null && outgoingConns.size() > 0){

			IConnection outgoingConn = outgoingConns.get(0);

			if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
			}
		}
	}
}	

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    return stringBuffer.toString();
  }
}
