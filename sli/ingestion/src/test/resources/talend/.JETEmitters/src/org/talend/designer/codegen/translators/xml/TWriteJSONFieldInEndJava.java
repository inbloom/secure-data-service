package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.IConnection;

public class TWriteJSONFieldInEndJava
{
  protected static String nl;
  public static synchronized TWriteJSONFieldInEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWriteJSONFieldInEndJava result = new TWriteJSONFieldInEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t}" + NL + "} while (!queue_";
  protected final String TEXT_3 = ".isEmpty() || !(globalMap.containsKey(\"";
  protected final String TEXT_4 = "_FINISH\")) );" + NL + "globalMap.remove(\"";
  protected final String TEXT_5 = "_FINISH\");";
  protected final String TEXT_6 = NL + "globalMap.put(\"";
  protected final String TEXT_7 = "_NB_LINE\",nb_line_";
  protected final String TEXT_8 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String destination = ElementParameterParser.getValue(node, "__DESTINATION__");

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    	List< ? extends IConnection> conns = node.getOutgoingConnections();
		if(conns!=null && conns.size()>0){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
		}
	}
}

String strJobCid="";
if(destination !=null && !("").equals(destination.trim()))
	strJobCid=destination;
else{
	strJobCid= cid.substring(0,cid.length()-3);
}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(strJobCid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
