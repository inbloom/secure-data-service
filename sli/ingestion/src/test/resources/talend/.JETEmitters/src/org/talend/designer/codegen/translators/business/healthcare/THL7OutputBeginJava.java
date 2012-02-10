package org.talend.designer.codegen.translators.business.healthcare;

import java.util.List;
import java.util.Map;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class THL7OutputBeginJava
{
  protected static String nl;
  public static synchronized THL7OutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    THL7OutputBeginJava result = new THL7OutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "System.setProperty(\"org.apache.commons.logging.Log\", \"org.apache.commons.logging.impl.NoOpLog\");" + NL;
  protected final String TEXT_3 = NL + "int i_";
  protected final String TEXT_4 = "_";
  protected final String TEXT_5 = " = 0;";
  protected final String TEXT_6 = NL + "ca.uhn.hl7v2.util.Terser terser_";
  protected final String TEXT_7 = " = null;" + NL + "ca.uhn.hl7v2.model.Message msg_";
  protected final String TEXT_8 = " = null;" + NL + "" + NL + "String tmpValue_";
  protected final String TEXT_9 = " = \"\";" + NL;
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

    stringBuffer.append(TEXT_2);
    
	List< ? extends IConnection> incomingConns = node.getIncomingConnections();
	for (IConnection incomingConn : incomingConns) {
		if ( incomingConn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(incomingConn.getName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
		}
	}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
