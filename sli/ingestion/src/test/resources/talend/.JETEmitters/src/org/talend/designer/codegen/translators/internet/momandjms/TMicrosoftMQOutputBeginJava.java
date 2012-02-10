package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TMicrosoftMQOutputBeginJava
{
  protected static String nl;
  public static synchronized TMicrosoftMQOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMicrosoftMQOutputBeginJava result = new TMicrosoftMQOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    org.talend.msmq.MsmqUtil msgu_";
  protected final String TEXT_2 = " = new org.talend.msmq.MsmqUtil();" + NL + "    msgu_";
  protected final String TEXT_3 = ".setHost(";
  protected final String TEXT_4 = ");" + NL + "    msgu_";
  protected final String TEXT_5 = ".setQueue(";
  protected final String TEXT_6 = "); " + NL + "    msgu_";
  protected final String TEXT_7 = ".createIfNotExists(true);" + NL + "    msgu_";
  protected final String TEXT_8 = ".open();";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List< ? extends IConnection> inConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
    
if (inConns != null && inConns.size() > 0){
  IMetadataTable inTable = inConns.get(0).getMetadataTable();
  List<IMetadataColumn> columns = inTable.getListColumns();

  if (columns != null && columns.size() > 0){
    String host = ElementParameterParser.getValue(node, "__HOST__");
    String queue = ElementParameterParser.getValue(node, "__QUEUE__");
    
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(queue);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
  }
}

    return stringBuffer.toString();
  }
}
