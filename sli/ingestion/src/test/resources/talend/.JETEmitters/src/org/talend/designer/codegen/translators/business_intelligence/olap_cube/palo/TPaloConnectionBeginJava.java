package org.talend.designer.codegen.translators.business_intelligence.olap_cube.palo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TPaloConnectionBeginJava
{
  protected static String nl;
  public static synchronized TPaloConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloConnectionBeginJava result = new TPaloConnectionBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "// Initialize jpalo" + NL + "org.talend.jpalo.palo p_";
  protected final String TEXT_3 = " = new org.talend.jpalo.palo(";
  protected final String TEXT_4 = ");" + NL + "" + NL + "// Open the connection" + NL + "org.talend.jpalo.paloconnection pConn_";
  protected final String TEXT_5 = " = p_";
  protected final String TEXT_6 = ".connect(";
  protected final String TEXT_7 = ", ";
  protected final String TEXT_8 = ", ";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = ");" + NL + "" + NL + "globalMap.put(\"p_";
  protected final String TEXT_11 = "\", p_";
  protected final String TEXT_12 = ");" + NL + "globalMap.put(\"pConn_";
  protected final String TEXT_13 = "\", pConn_";
  protected final String TEXT_14 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    String sServer = ElementParameterParser.getValue(node, "__SERVER__");
    String sServerport = ElementParameterParser.getValue(node, "__SERVERPORT__");
    String sUsername = ElementParameterParser.getValue(node, "__USERNAME__");
    String sPassword = ElementParameterParser.getValue(node, "__PASS__");
    
    String sDeploypalolibs = ElementParameterParser.getValue(node, "__DEPLOY_PALO_LIBS__");
    boolean bDeploypalolibs=false;
    if(sDeploypalolibs.equals("true"))bDeploypalolibs=true;


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(bDeploypalolibs);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(sUsername);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(sPassword);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(sServer);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(sServerport);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
