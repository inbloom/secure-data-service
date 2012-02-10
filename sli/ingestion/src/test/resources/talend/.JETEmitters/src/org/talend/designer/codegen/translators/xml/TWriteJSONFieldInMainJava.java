package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import org.talend.core.model.process.IConnection;

public class TWriteJSONFieldInMainJava
{
  protected static String nl;
  public static synchronized TWriteJSONFieldInMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWriteJSONFieldInMainJava result = new TWriteJSONFieldInMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tstr_";
  protected final String TEXT_3 = " = queue_";
  protected final String TEXT_4 = ".poll();" + NL + "\t\t//Convert XML to JSON" + NL + "        net.sf.json.JSON json_";
  protected final String TEXT_5 = " = xmlSerializer_";
  protected final String TEXT_6 = ".read(str_";
  protected final String TEXT_7 = ");" + NL + "\t\t";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = " = json_";
  protected final String TEXT_10 = ".toString();" + NL + "\t" + NL + "\t\tnb_line_";
  protected final String TEXT_11 = "++;";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();


String jsonField = ElementParameterParser.getValue(node, "__JSONFIELD__");
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    	List< ? extends IConnection> conns = node.getOutgoingConnections();
		if(conns!=null && conns.size()>0){
    		IConnection conn = conns.get(0);

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(jsonField );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
		}
	}
}

    return stringBuffer.toString();
  }
}
