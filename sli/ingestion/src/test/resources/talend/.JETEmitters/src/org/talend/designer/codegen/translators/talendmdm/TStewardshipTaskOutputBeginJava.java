package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TStewardshipTaskOutputBeginJava
{
  protected static String nl;
  public static synchronized TStewardshipTaskOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TStewardshipTaskOutputBeginJava result = new TStewardshipTaskOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "String taskFlag_";
  protected final String TEXT_2 = "=\"\";" + NL + "int nb_line_";
  protected final String TEXT_3 = " = 0;\t" + NL + "org.talend.stewardship.TaskGenerator tasks_";
  protected final String TEXT_4 = " = new org.talend.stewardship.TaskGenerator();" + NL + "org.talend.stewardship.Task task_";
  protected final String TEXT_5 = " = null;" + NL + "int port_";
  protected final String TEXT_6 = " =  ";
  protected final String TEXT_7 = ";" + NL + "String url_";
  protected final String TEXT_8 = " = org.talend.datastewardship.server.task.creation.TaskLoadClientTools.getUrl(";
  protected final String TEXT_9 = ",String.valueOf(port_";
  protected final String TEXT_10 = "));" + NL + "org.talend.datastewardship.server.task.creation.TaskLoadClient taskLoadClient_";
  protected final String TEXT_11 = " = new org.talend.datastewardship.server.task.creation.TaskLoadClient(url_";
  protected final String TEXT_12 = ",";
  protected final String TEXT_13 = ",";
  protected final String TEXT_14 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	
    
    String host = ElementParameterParser.getValue(node, "__HOST__");
    String port = ElementParameterParser.getValue(node, "__PORT__");
    String username = ElementParameterParser.getValue(node, "__USERNAME__");
    String password = ElementParameterParser.getValue(node, "__PASSWORD__");
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2

    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_14);
    
			}	
		}
	}	
}

    return stringBuffer.toString();
  }
}
