package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TMicrosoftMQInputBeginJava
{
  protected static String nl;
  public static synchronized TMicrosoftMQInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMicrosoftMQInputBeginJava result = new TMicrosoftMQInputBeginJava();
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
  protected final String TEXT_7 = ".open();" + NL + "    if (msgu_";
  protected final String TEXT_8 = ".isOpen()){";
  protected final String TEXT_9 = NL + "      ";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = " = msgu_";
  protected final String TEXT_12 = ".receive();" + NL + "    }";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();  
List< ? extends IConnection> outConns = node.getOutgoingConnections(EConnectionType.FLOW_MAIN);
    
if (outConns != null && outConns.size() > 0){
  String connNameOut = outConns.get(0).getName();
  IMetadataTable outTable = outConns.get(0).getMetadataTable();
  List<IMetadataColumn> columns = outTable.getListColumns();
      
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
    stringBuffer.append(TEXT_9);
    stringBuffer.append(connNameOut);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(columns.get(0).getLabel());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
  }
}

    return stringBuffer.toString();
  }
}
