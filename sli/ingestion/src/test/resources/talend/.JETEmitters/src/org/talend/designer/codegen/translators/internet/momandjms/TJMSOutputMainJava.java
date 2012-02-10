package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TJMSOutputMainJava
{
  protected static String nl;
  public static synchronized TJMSOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJMSOutputMainJava result = new TJMSOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "producer_";
  protected final String TEXT_3 = ".send((javax.jms.Message)";
  protected final String TEXT_4 = ".message);" + NL + "nbline_";
  protected final String TEXT_5 = "++;";
  protected final String TEXT_6 = NL + "javax.jms.TextMessage message_";
  protected final String TEXT_7 = " = session_";
  protected final String TEXT_8 = ".createTextMessage();" + NL + "message_";
  protected final String TEXT_9 = ".setText(";
  protected final String TEXT_10 = ".messageContent);" + NL + "producer_";
  protected final String TEXT_11 = ".send(message_";
  protected final String TEXT_12 = ");" + NL + "" + NL + "nbline_";
  protected final String TEXT_13 = "++;";
  protected final String TEXT_14 = NL + NL + NL + "            ";
  protected final String TEXT_15 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String processingMode = ElementParameterParser.getValue(node, "__PROCESSING_MODE__");
List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		List<IMetadataColumn> columns = metadata.getListColumns();
		List< ? extends IConnection> conns = node.getIncomingConnections();
		if((conns!=null)&&(conns.size()>0)) {
			IConnection conn = conns.get(0);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
				if("RAW".equals(processingMode)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
				}else{

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
				}
			}
		}
	}

    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
