package org.talend.designer.codegen.translators.system;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import java.util.List;
import org.talend.core.model.process.IConnectionCategory;

public class TSystemMainJava
{
  protected static String nl;
  public static synchronized TSystemMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSystemMainJava result = new TSystemMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "\t" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
	    IMetadataTable metadata = metadatas.get(0);
	    if (metadata!=null) {
	    List<? extends IConnection> conns = node.getOutgoingConnections(EConnectionType.FLOW_MAIN);
			if (conns!=null && conns.size()>0) {
				IConnection conn = conns.get(0);
				String inputConnName = null;
				if (node.getIncomingConnections()!=null) {
					for (IConnection incomingConn : node.getIncomingConnections()) {
						if (incomingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
							inputConnName = incomingConn.getName();
							IMetadataTable inputMetadataTable = incomingConn.getMetadataTable();
							for (IMetadataColumn inputCol : inputMetadataTable.getListColumns()) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(inputCol.getLabel() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inputConnName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inputCol.getLabel() );
    stringBuffer.append(TEXT_6);
    
							}
						}
					}
				}
			}
		}
	}

    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
