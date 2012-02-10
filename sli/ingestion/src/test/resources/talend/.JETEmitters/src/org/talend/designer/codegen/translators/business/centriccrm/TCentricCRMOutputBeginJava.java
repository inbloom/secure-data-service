package org.talend.designer.codegen.translators.business.centriccrm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TCentricCRMOutputBeginJava
{
  protected static String nl;
  public static synchronized TCentricCRMOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TCentricCRMOutputBeginJava result = new TCentricCRMOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "       int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "       org.aspcfs.utils.CRMConnection crm";
  protected final String TEXT_4 = " = new org.aspcfs.utils.CRMConnection();" + NL + "       crm";
  protected final String TEXT_5 = ".setUrl(";
  protected final String TEXT_6 = ");" + NL + "       crm";
  protected final String TEXT_7 = ".setId(";
  protected final String TEXT_8 = ");" + NL + "       crm";
  protected final String TEXT_9 = ".setCode(";
  protected final String TEXT_10 = ");" + NL + "       crm";
  protected final String TEXT_11 = ".setClientId(";
  protected final String TEXT_12 = ");" + NL + "       crm";
  protected final String TEXT_13 = ".setAutoCommit(false);" + NL + "       org.aspcfs.apps.transfer.DataRecord ";
  protected final String TEXT_14 = " = new org.aspcfs.apps.transfer.DataRecord();";
  protected final String TEXT_15 = NL + "       ";
  protected final String TEXT_16 = " .setName(\"";
  protected final String TEXT_17 = "\");";
  protected final String TEXT_18 = NL + "       ";
  protected final String TEXT_19 = " .setAction(org.aspcfs.apps.transfer.DataRecord.";
  protected final String TEXT_20 = ");" + NL + "      " + NL + "       ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String url = ElementParameterParser.getValue(node, "__CENTRIC-URL__");
	String modulename = ElementParameterParser.getValue(node, "__MODULENAME__");
	String userid = ElementParameterParser.getValue(node, "__CLIENT__");
	String password = ElementParameterParser.getValue(node, "__CODE__");
	String server = ElementParameterParser.getValue(node, "__SERVER-ID__");
	String action=ElementParameterParser.getValue(node, "__ACTION__");

    stringBuffer.append(TEXT_1);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {    

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(url);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(server);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(userid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(modulename);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(modulename);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(modulename);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(modulename);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(action);
    stringBuffer.append(TEXT_20);
     }
 }
    return stringBuffer.toString();
  }
}
