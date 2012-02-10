package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.Map;
import java.util.List;

public class TSOAPMainJava
{
  protected static String nl;
  public static synchronized TSOAPMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSOAPMainJava result = new TSOAPMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "String soapVersion_";
  protected final String TEXT_3 = " = org.talend.soap.SOAPUtil.SOAP12;";
  protected final String TEXT_4 = NL + "String soapVersion_";
  protected final String TEXT_5 = " = org.talend.soap.SOAPUtil.SOAP11;";
  protected final String TEXT_6 = NL + "\t\t" + NL + "soapUtil_";
  protected final String TEXT_7 = ".invokeSOAP(soapVersion_";
  protected final String TEXT_8 = ",";
  protected final String TEXT_9 = ",";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = ");" + NL + "\t\t// for output";
  protected final String TEXT_12 = "\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_13 = " = new ";
  protected final String TEXT_14 = "Struct();" + NL + "\t\t\t\t";
  protected final String TEXT_15 = ".Header = soapUtil_";
  protected final String TEXT_16 = ".getReHeaderMessage();" + NL + "\t\t\t\tif(soapUtil_";
  protected final String TEXT_17 = ".hasFault()){" + NL + "\t\t\t\t\t";
  protected final String TEXT_18 = ".Fault = soapUtil_";
  protected final String TEXT_19 = ".getReFaultMessage();" + NL + "\t\t\t\t}else{" + NL + "\t\t\t\t\t";
  protected final String TEXT_20 = ".Body = soapUtil_";
  protected final String TEXT_21 = ".getReBodyMessage();" + NL + "\t\t\t\t}";
  protected final String TEXT_22 = NL + "        " + NL;
  protected final String TEXT_23 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String endpoint = ElementParameterParser.getValue(node,"__ENDPOINT__");
String action = ElementParameterParser.getValue(node,"__ACTION__");
String soapMessageStr = ElementParameterParser.getValue(node,"__SOAPMESSAGE__");
soapMessageStr = soapMessageStr.replaceAll("[\r\n]", " ");
        
String soapVersion = ElementParameterParser.getValue(node,"__SOAP_VERSION__");

    if("Soap12".equals(soapVersion)){
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    }else{
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    }
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(action);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(soapMessageStr);
    stringBuffer.append(TEXT_11);
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	if (conns!=null) {//1
		if (conns.size()>0) {//2
			IConnection conn = conns.get(0); //the first connection
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//3

    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    			
			}//3
		}//2
	}//1

    stringBuffer.append(TEXT_22);
    stringBuffer.append(TEXT_23);
    return stringBuffer.toString();
  }
}
