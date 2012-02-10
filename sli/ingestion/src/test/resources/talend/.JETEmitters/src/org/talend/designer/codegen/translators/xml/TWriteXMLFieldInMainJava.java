package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TWriteXMLFieldInMainJava
{
  protected static String nl;
  public static synchronized TWriteXMLFieldInMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWriteXMLFieldInMainJava result = new TWriteXMLFieldInMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ParserUtils.parseTo_Document(queue_";
  protected final String TEXT_5 = ".poll());";
  protected final String TEXT_6 = NL + "\t";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = " = queue_";
  protected final String TEXT_9 = ".poll();";
  protected final String TEXT_10 = NL + "nb_line_";
  protected final String TEXT_11 = "++;";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();


String xmlField = ElementParameterParser.getValue(node, "__XMLFIELD__");
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    	List< ? extends IConnection> conns = node.getOutgoingConnections();
		if(conns!=null && conns.size()>0){
    		IConnection conn = conns.get(0);
if("id_Document".equals(metadata.getColumn(xmlField).getTalendType())) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(xmlField );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
} else {

    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(xmlField );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
}

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
		}
	}
}

    return stringBuffer.toString();
  }
}
