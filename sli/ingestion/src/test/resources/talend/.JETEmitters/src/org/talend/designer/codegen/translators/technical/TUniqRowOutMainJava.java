package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import java.util.List;

public class TUniqRowOutMainJava
{
  protected static String nl;
  public static synchronized TUniqRowOutMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TUniqRowOutMainJava result = new TUniqRowOutMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tif (rowsInBuffer_1_";
  protected final String TEXT_2 = " >= bufferSize_1_";
  protected final String TEXT_3 = ") {// buffer is full do sort and" + NL + "\t" + NL + "\t\tjava.util.Arrays.<rowStruct_";
  protected final String TEXT_4 = "> sort(buffer_1_";
  protected final String TEXT_5 = ", 0, bufferSize_1_";
  protected final String TEXT_6 = ", comparator_1_";
  protected final String TEXT_7 = ");" + NL + "\t                    " + NL + "\t    java.io.File file_";
  protected final String TEXT_8 = " = new java.io.File(workDirectory_";
  protected final String TEXT_9 = " + \"/";
  protected final String TEXT_10 = "_TEMP_\" + files_1_";
  protected final String TEXT_11 = ".size());" + NL + "\t                    " + NL + "\t    file_";
  protected final String TEXT_12 = ".deleteOnExit();" + NL + "\t                    " + NL + "\t    java.io.ObjectOutputStream rw = new java.io.ObjectOutputStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(file_";
  protected final String TEXT_13 = ")));" + NL + "\t                    " + NL + "\t\tfor (int i_";
  protected final String TEXT_14 = " = 0; i_";
  protected final String TEXT_15 = " < bufferSize_1_";
  protected final String TEXT_16 = "; i_";
  protected final String TEXT_17 = "++) {" + NL + "\t    \tbuffer_1_";
  protected final String TEXT_18 = "[i_";
  protected final String TEXT_19 = "].writeData(rw);" + NL + "\t    }" + NL + "\t                    " + NL + "\t    rw.close();" + NL + "\t" + NL + "\t    files_1_";
  protected final String TEXT_20 = ".add(file_";
  protected final String TEXT_21 = ");" + NL + "\t" + NL + "\t    rowsInBuffer_1_";
  protected final String TEXT_22 = " = 0;" + NL + "\t}" + NL + "\trowStruct_";
  protected final String TEXT_23 = " row_";
  protected final String TEXT_24 = " = buffer_1_";
  protected final String TEXT_25 = "[rowsInBuffer_1_";
  protected final String TEXT_26 = "++];" + NL + "\trow_";
  protected final String TEXT_27 = ".id_";
  protected final String TEXT_28 = " = ++nb_";
  protected final String TEXT_29 = ";";
  protected final String TEXT_30 = NL + "\trow_";
  protected final String TEXT_31 = ".";
  protected final String TEXT_32 = " = ";
  protected final String TEXT_33 = ".";
  protected final String TEXT_34 = ";" + NL + "    \t";
  protected final String TEXT_35 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = ElementParameterParser.getValue(node, "__CID__");

String connName = "";
if (node.getIncomingConnections().size()==1) {
	IConnection conn = node.getIncomingConnections().get(0);
	connName = conn.getName();
}

///////////////
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0) && !("").equals(connName)) {//HSS_____0
	IMetadataTable metadata = metadatas.get(0);
    List<IMetadataColumn> columnList = metadata.getListColumns();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    
    for(IMetadataColumn column : columnList){//HSS_____0_____1
    	
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_34);
    
    }//HSS_____0_____1
}//HSS_____0

    stringBuffer.append(TEXT_35);
    return stringBuffer.toString();
  }
}
