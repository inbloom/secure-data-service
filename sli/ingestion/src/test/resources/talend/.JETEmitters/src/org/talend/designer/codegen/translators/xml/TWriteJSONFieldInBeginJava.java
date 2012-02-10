package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import java.util.List;
import org.talend.core.model.process.IConnection;

public class TWriteJSONFieldInBeginJava
{
  protected static String nl;
  public static synchronized TWriteJSONFieldInBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWriteJSONFieldInBeginJava result = new TWriteJSONFieldInBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\tnet.sf.json.xml.XMLSerializer xmlSerializer_";
  protected final String TEXT_4 = " = new net.sf.json.xml.XMLSerializer(); " + NL + "    xmlSerializer_";
  protected final String TEXT_5 = ".clearNamespaces();" + NL + "    xmlSerializer_";
  protected final String TEXT_6 = ".setSkipNamespaces(true);" + NL + "    xmlSerializer_";
  protected final String TEXT_7 = ".setForceTopLevelObject(true);" + NL + "\tjava.util.Queue<String> queue_";
  protected final String TEXT_8 = " = (java.util.Queue<String>) globalMap.get(\"queue_";
  protected final String TEXT_9 = "\");" + NL + "\t" + NL + "\tString str_";
  protected final String TEXT_10 = " = null;" + NL + "\t" + NL + "\tdo {" + NL + "\t\t\tif (!queue_";
  protected final String TEXT_11 = ".isEmpty()) {";
  protected final String TEXT_12 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();


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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
		}
	}
}

    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
