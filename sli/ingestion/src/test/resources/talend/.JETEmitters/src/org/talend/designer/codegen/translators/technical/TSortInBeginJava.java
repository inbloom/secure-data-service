package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TSortInBeginJava
{
  protected static String nl;
  public static synchronized TSortInBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSortInBeginJava result = new TSortInBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = "Struct[] array_";
  protected final String TEXT_5 = " = (";
  protected final String TEXT_6 = "Struct[]) globalMap.get(\"";
  protected final String TEXT_7 = "\");" + NL + "" + NL + "int nb_line_";
  protected final String TEXT_8 = " = 0;" + NL;
  protected final String TEXT_9 = NL;
  protected final String TEXT_10 = "Struct current_";
  protected final String TEXT_11 = " = null;" + NL + "" + NL + "for(int i_";
  protected final String TEXT_12 = " = 0; i_";
  protected final String TEXT_13 = " < array_";
  protected final String TEXT_14 = ".length; i_";
  protected final String TEXT_15 = "++){" + NL + "\tcurrent_";
  protected final String TEXT_16 = " = array_";
  protected final String TEXT_17 = "[i_";
  protected final String TEXT_18 = "];";
  protected final String TEXT_19 = NL + "\t";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = " = current_";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ";";
  protected final String TEXT_24 = NL + "\t// increase number of line sorted" + NL + "\tnb_line_";
  protected final String TEXT_25 = "++;";
  protected final String TEXT_26 = NL + "java.util.Iterator<";
  protected final String TEXT_27 = "Struct> iterator_";
  protected final String TEXT_28 = " = (java.util.Iterator<";
  protected final String TEXT_29 = "Struct>) globalMap.get(\"";
  protected final String TEXT_30 = "\");" + NL + "int nb_line_";
  protected final String TEXT_31 = " = 0;";
  protected final String TEXT_32 = NL;
  protected final String TEXT_33 = "Struct current_";
  protected final String TEXT_34 = " = null;" + NL + "" + NL + "while (iterator_";
  protected final String TEXT_35 = ".hasNext()) {" + NL + "\tcurrent_";
  protected final String TEXT_36 = " = iterator_";
  protected final String TEXT_37 = ".next();";
  protected final String TEXT_38 = NL + "\t";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = " = current_";
  protected final String TEXT_41 = ".";
  protected final String TEXT_42 = ";";
  protected final String TEXT_43 = "\t" + NL + "\t// increase number of line sorted" + NL + "\tnb_line_";
  protected final String TEXT_44 = "++;";
  protected final String TEXT_45 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();
String rowName = null;
String origin = ElementParameterParser.getValue(node, "__ORIGIN__");

	for (INode pNode : node.getProcess().getNodesOfType("tSortOut")) {
		if (!pNode.getUniqueName().equals(origin + "_SortOut")) continue;
		for (IConnection conn : pNode.getIncomingConnections()) {
			rowName = conn.getName();
			break;
		}
		
	}

    stringBuffer.append(TEXT_2);
    
String outRowName = "";
if ((node.getOutgoingSortedConnections()!=null)&&(node.getOutgoingSortedConnections().size()>0)) {
    for(IConnection outgoingConn : node.getOutgoingSortedConnections()) {
        if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            outRowName = outgoingConn.getName();
            break;
        }
    }
} else {
	outRowName = "defaultRow";
}

String isExternalSort = ElementParameterParser.getValue(node, "__EXTERNAL__");

    
if(("false").equals(isExternalSort)){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
 	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {	
	for (IMetadataColumn column : metadata.getListColumns()) { 

    stringBuffer.append(TEXT_19);
    stringBuffer.append(outRowName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_23);
    
	}

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
	}
}
}else{

    stringBuffer.append(TEXT_26);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(origin );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
 	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {	
	for (IMetadataColumn column : metadata.getListColumns()) { 

    stringBuffer.append(TEXT_38);
    stringBuffer.append(outRowName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_42);
    
	}

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
}

    
	}
}

    stringBuffer.append(TEXT_45);
    return stringBuffer.toString();
  }
}
