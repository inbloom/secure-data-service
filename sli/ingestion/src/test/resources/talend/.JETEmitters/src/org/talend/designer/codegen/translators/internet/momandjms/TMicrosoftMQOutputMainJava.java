package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.EConnectionType;
import java.util.List;

public class TMicrosoftMQOutputMainJava
{
  protected static String nl;
  public static synchronized TMicrosoftMQOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMicrosoftMQOutputMainJava result = new TMicrosoftMQOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    if (";
  protected final String TEXT_2 = ".";
  protected final String TEXT_3 = " != null){" + NL + "      if (msgu_";
  protected final String TEXT_4 = ".isOpen()){" + NL + "        msgu_";
  protected final String TEXT_5 = ".setMsg(";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ");" + NL + "        msgu_";
  protected final String TEXT_8 = ".send();" + NL + "      }" + NL + "    }";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();  
List< ? extends IConnection> inConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
    
if (inConns != null && inConns.size() > 0){
  String connNameIn = inConns.get(0).getName();
  IMetadataTable inTable = inConns.get(0).getMetadataTable();
  List<IMetadataColumn> columns = inTable.getListColumns();
  String message = ElementParameterParser.getValue(node, "__MESSAGE__");

  if (columns != null && columns.size() > 0){
  
    stringBuffer.append(TEXT_1);
    stringBuffer.append(connNameIn);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(columns.get(0).getLabel());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(connNameIn);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(message);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
  }
}

    return stringBuffer.toString();
  }
}
