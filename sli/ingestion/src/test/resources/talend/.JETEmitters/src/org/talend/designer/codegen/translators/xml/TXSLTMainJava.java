package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.util.List;
import java.util.Map;

public class TXSLTMainJava
{
  protected static String nl;
  public static synchronized TXSLTMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TXSLTMainJava result = new TXSLTMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t" + NL + "\tSystem.setProperty(\"javax.xml.transform.TransformerFactory\", \"net.sf.saxon.TransformerFactoryImpl\");" + NL + "" + NL + "\tjava.io.File xsltFile";
  protected final String TEXT_3 = " = new java.io.File(";
  protected final String TEXT_4 = ");" + NL + "\tjavax.xml.transform.stream.StreamSource styleSource";
  protected final String TEXT_5 = " = new javax.xml.transform.stream.StreamSource(xsltFile";
  protected final String TEXT_6 = ");" + NL + "\tjavax.xml.transform.Transformer t";
  protected final String TEXT_7 = " = javax.xml.transform.TransformerFactory.newInstance().newTransformer(styleSource";
  protected final String TEXT_8 = ");" + NL + "\t" + NL + "\t";
  protected final String TEXT_9 = NL + "\t\t\tt";
  protected final String TEXT_10 = ".setParameter(";
  protected final String TEXT_11 = ", ";
  protected final String TEXT_12 = ");" + NL + "\t";
  protected final String TEXT_13 = NL + NL + "\tjavax.xml.transform.stream.StreamSource xmlSource";
  protected final String TEXT_14 = " = new javax.xml.transform.stream.StreamSource(new java.io.File(";
  protected final String TEXT_15 = "));" + NL + "\tt";
  protected final String TEXT_16 = ".transform(xmlSource";
  protected final String TEXT_17 = ", new javax.xml.transform.stream.StreamResult(new java.io.File(";
  protected final String TEXT_18 = ")));" + NL + "\t\t" + NL + "\tif((";
  protected final String TEXT_19 = ").indexOf(\"/\") != -1)" + NL + "\t{" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_20 = "_OUTPUT_FILEPATH\", (";
  protected final String TEXT_21 = ").substring(0,(";
  protected final String TEXT_22 = ").lastIndexOf(\"/\")));" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_23 = "_OUTPUT_FILENAME\", (";
  protected final String TEXT_24 = ").substring((";
  protected final String TEXT_25 = ").lastIndexOf(\"/\") + 1));" + NL + "\t}" + NL + "\telse" + NL + "\t{" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_26 = "_OUTPUT_FILEPATH\", ";
  protected final String TEXT_27 = ");" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_28 = "_OUTPUT_FILENAME\", ";
  protected final String TEXT_29 = ");\t" + NL + "\t}\t\t\t\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String xmlFile = ElementParameterParser.getValue(node, "__XML_FILE__");
String xslFile = ElementParameterParser.getValue(node, "__XSLT_FILE__");
String outputFile = ElementParameterParser.getValue(node, "__OUTPUT_FILE__");

List<Map<String, String>> params = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__PARAMS__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(xslFile);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
	    for (int i = 0; i < params.size(); i++) {
	        Map<String, String> line = params.get(i);
	
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append( line.get("NAME") );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( line.get("VALUE") );
    stringBuffer.append(TEXT_12);
    		
		}
	
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(xmlFile);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(outputFile);
    stringBuffer.append(TEXT_29);
    return stringBuffer.toString();
  }
}
