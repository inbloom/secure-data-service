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

public class TStewardshipTaskOutputEndJava
{
  protected static String nl;
  public static synchronized TStewardshipTaskOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TStewardshipTaskOutputEndJava result = new TStewardshipTaskOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tif(tasks_";
  protected final String TEXT_2 = ".getTaskCount()>=1){" + NL + "\t\tString content = tasks_";
  protected final String TEXT_3 = ".getTasksAndClear();" + NL + "\t\t//System.out.println(content);" + NL + "\t\tboolean loadResult_";
  protected final String TEXT_4 = " = taskLoadClient_";
  protected final String TEXT_5 = ".doLoad(content);" + NL + "\t\tif(!loadResult_";
  protected final String TEXT_6 = "){" + NL + "\t\t\tSystem.err.println(\"An error occured while uploading tasks.\");" + NL + "\t\t}" + NL + "\t}";
  protected final String TEXT_7 = NL + "globalMap.put(\"";
  protected final String TEXT_8 = "_NB_LINE\",nb_line_";
  protected final String TEXT_9 = ");";
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	

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
    
			}	
		}
	}	
}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
