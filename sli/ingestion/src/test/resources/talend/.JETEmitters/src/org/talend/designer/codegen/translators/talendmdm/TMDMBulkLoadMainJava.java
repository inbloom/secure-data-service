package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TMDMBulkLoadMainJava
{
  protected static String nl;
  public static synchronized TMDMBulkLoadMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMDMBulkLoadMainJava result = new TMDMBulkLoadMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "if (count_";
  protected final String TEXT_2 = " % ";
  protected final String TEXT_3 = " == 0) {" + NL + "\tif(inputStreamMerger_";
  protected final String TEXT_4 = " != null)" + NL + "\t\tinputStreamMerger_";
  protected final String TEXT_5 = ".close();" + NL + "\tinputStreamMerger_";
  protected final String TEXT_6 = " = bulkloadClient_";
  protected final String TEXT_7 = ".load();" + NL + "}" + NL + "" + NL + "inputStreamMerger_";
  protected final String TEXT_8 = ".push(new java.io.ByteArrayInputStream(";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = ".toString().getBytes(\"UTF-8\")));" + NL + "" + NL + "count_";
  protected final String TEXT_11 = "++;" + NL;
  protected final String TEXT_12 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String xmlField = ElementParameterParser.getValue(node,"__XMLFIELD__");
String arraySize = ElementParameterParser.getValue(node,"__MASS_LEVEL__");

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(arraySize);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_9);
    stringBuffer.append(xmlField );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
			}//4
		}//3
	}//2
}//1

    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
