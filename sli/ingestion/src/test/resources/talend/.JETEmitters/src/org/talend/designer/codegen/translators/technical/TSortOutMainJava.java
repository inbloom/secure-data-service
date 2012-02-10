package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.List;

public class TSortOutMainJava
{
  protected static String nl;
  public static synchronized TSortOutMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSortOutMainJava result = new TSortOutMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + NL + "\tComparable";
  protected final String TEXT_4 = "Struct arrayRow";
  protected final String TEXT_5 = " = new Comparable";
  protected final String TEXT_6 = "Struct();" + NL;
  protected final String TEXT_7 = NL + "\tarrayRow";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ".clone()";
  protected final String TEXT_12 = ";";
  protected final String TEXT_13 = "\t" + NL + "\tlist_";
  protected final String TEXT_14 = ".add(arrayRow";
  protected final String TEXT_15 = ");";
  protected final String TEXT_16 = NL + "\t";
  protected final String TEXT_17 = "StructILightSerializable current_";
  protected final String TEXT_18 = " = new ";
  protected final String TEXT_19 = "StructILightSerializable();";
  protected final String TEXT_20 = NL + "\tcurrent_";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = " = ";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = ".clone()";
  protected final String TEXT_25 = ";";
  protected final String TEXT_26 = "\t" + NL + "\titerator_";
  protected final String TEXT_27 = ".put(\"\", current_";
  protected final String TEXT_28 = ");";
  protected final String TEXT_29 = NL;

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

String isExternalSort = ElementParameterParser.getValue(node, "__EXTERNAL__");


    stringBuffer.append(TEXT_2);
    
if(("false").equals(isExternalSort)){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_6);
     
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {
			for (IMetadataColumn column : metadata.getListColumns()) { 

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel() );
    if(("id_Dynamic").equals(column.getTalendType())){
    stringBuffer.append(TEXT_11);
    }
    stringBuffer.append(TEXT_12);
    
			}
		}
	}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    
}else{

    stringBuffer.append(TEXT_16);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_19);
     
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {
			for (IMetadataColumn column : metadata.getListColumns()) { 

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(rowName );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel() );
    if(("id_Dynamic").equals(column.getTalendType())){
    stringBuffer.append(TEXT_24);
    }
    stringBuffer.append(TEXT_25);
    
			}
		}
	}

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    
}

    stringBuffer.append(TEXT_29);
    return stringBuffer.toString();
  }
}
