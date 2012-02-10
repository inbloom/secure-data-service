package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.IConnection;

public class TFileOutputJSONMainJava
{
  protected static String nl;
  public static synchronized TFileOutputJSONMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputJSONMainJava result = new TFileOutputJSONMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "org.json.simple.JSONObject jsonRow";
  protected final String TEXT_3 = " = new org.json.simple.JSONObject();";
  protected final String TEXT_4 = NL + "\t\t\t\t\t\t\t\t\tjsonRow";
  protected final String TEXT_5 = ".put(\"";
  protected final String TEXT_6 = "\",";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = ");" + NL + "\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_9 = NL + NL + "jsonSet";
  protected final String TEXT_10 = ".add(jsonRow";
  protected final String TEXT_11 = ");" + NL + "nb_line_";
  protected final String TEXT_12 = "++;";
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
	List< ? extends IConnection> conns = node.getIncomingConnections();
	for (IConnection conn : conns) {
			if ((metadatas!=null)&&(metadatas.size()>0)) {
					IMetadataTable metadata = metadatas.get(0);
					if (metadata!=null) {
								List<IMetadataColumn> columns = metadata.getListColumns();
								int sizeColumns = columns.size();
								for (int i = 0; i < sizeColumns; i++) {
							
									IMetadataColumn column = columns.get(i);
	
									
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_8);
    
								}
							}
		}
		}
		
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
