package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class THashBeginJava
{
  protected static String nl;
  public static synchronized THashBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    THashBeginJava result = new THashBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "            java.util.Map<";
  protected final String TEXT_3 = "Struct, ";
  protected final String TEXT_4 = "Struct> tHash_";
  protected final String TEXT_5 = " = new java.util.LinkedHashMap<";
  protected final String TEXT_6 = "Struct, ";
  protected final String TEXT_7 = "Struct>();" + NL + "            globalMap.put(\"tHash_";
  protected final String TEXT_8 = "\", tHash_";
  protected final String TEXT_9 = ");" + NL;
  protected final String TEXT_10 = NL + "            ";
  protected final String TEXT_11 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();

    List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();

	if (connections != null && connections.size() > 0) {
        for (IConnection connection : connections) {
        	String connectionName = connection.getName();


    stringBuffer.append(TEXT_2);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(connectionName);
    stringBuffer.append(TEXT_9);
    
		}
	}


    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
