package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.List;

public class TArrayMainJava
{
  protected static String nl;
  public static synchronized TArrayMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TArrayMainJava result = new TArrayMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "\t";
  protected final String TEXT_3 = "Struct arrayRow";
  protected final String TEXT_4 = " = new ";
  protected final String TEXT_5 = "Struct();" + NL;
  protected final String TEXT_6 = NL + "\tarrayRow";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = ";";
  protected final String TEXT_11 = "\t" + NL + "\t";
  protected final String TEXT_12 = ".add(arrayRow";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();	

String destination = ElementParameterParser.getValue(node, "__DESTINATION__");
String rowName= "";
if ((node.getIncomingConnections()!=null)&&(node.getIncomingConnections().size()>0)) {
	rowName = node.getIncomingConnections().get(0).getName();
} else {
	rowName="defaultRow";
}

    stringBuffer.append(TEXT_2);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_5);
     
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {
			for (IMetadataColumn column : metadata.getListColumns()) { 

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_10);
    
			}
		}
	}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(destination );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
