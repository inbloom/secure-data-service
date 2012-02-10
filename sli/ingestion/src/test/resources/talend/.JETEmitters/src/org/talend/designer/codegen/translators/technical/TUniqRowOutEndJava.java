package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import java.util.Map;
import java.util.List;

public class TUniqRowOutEndJava
{
  protected static String nl;
  public static synchronized TUniqRowOutEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TUniqRowOutEndJava result = new TUniqRowOutEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "            if (rowsInBuffer_1_";
  protected final String TEXT_2 = " > 0) {" + NL + "                java.util.Arrays.<rowStruct_";
  protected final String TEXT_3 = "> sort(buffer_1_";
  protected final String TEXT_4 = ", 0, rowsInBuffer_1_";
  protected final String TEXT_5 = ", comparator_1_";
  protected final String TEXT_6 = ");" + NL + "                " + NL + "                java.io.File file_";
  protected final String TEXT_7 = " = new java.io.File(workDirectory_";
  protected final String TEXT_8 = " + \"/";
  protected final String TEXT_9 = "_TEMP_\" + files_1_";
  protected final String TEXT_10 = ".size());" + NL + "                " + NL + "                file_";
  protected final String TEXT_11 = ".deleteOnExit();" + NL + "                " + NL + "                java.io.ObjectOutputStream rw_";
  protected final String TEXT_12 = " = new java.io.ObjectOutputStream(new java.io.BufferedOutputStream(" + NL + "                        new java.io.FileOutputStream(file_";
  protected final String TEXT_13 = ")));" + NL + "                for (int i = 0; i < rowsInBuffer_1_";
  protected final String TEXT_14 = "; i++) {" + NL + "                    buffer_1_";
  protected final String TEXT_15 = "[i].writeData(rw_";
  protected final String TEXT_16 = ");" + NL + "                }" + NL + "                " + NL + "                rw_";
  protected final String TEXT_17 = ".close();" + NL + "" + NL + "                files_1_";
  protected final String TEXT_18 = ".add(file_";
  protected final String TEXT_19 = ");" + NL + "" + NL + "                rowsInBuffer_1_";
  protected final String TEXT_20 = " = 0;" + NL + "            }" + NL + "            buffer_1_";
  protected final String TEXT_21 = " = null;" + NL + "            " + NL + "" + NL + "            // ////////////////////////////////////" + NL + "            class FileRowIterator_";
  protected final String TEXT_22 = " implements java.util.Iterator<rowStruct_";
  protected final String TEXT_23 = "> {" + NL + "\t\t\t\t" + NL + "\t\t\t\tboolean isEndOfFile = false;" + NL + "\t\t\t\t" + NL + "                rowStruct_";
  protected final String TEXT_24 = "[] buffer;" + NL + "" + NL + "                ObjectInputStream ois;" + NL + "                " + NL + "                java.io.BufferedInputStream bis;" + NL + "                " + NL + "                rowStruct_";
  protected final String TEXT_25 = " tempRow;" + NL + "" + NL + "                int count = 0;" + NL + "" + NL + "                int index = 0;" + NL + "" + NL + "                public FileRowIterator_";
  protected final String TEXT_26 = "(java.io.File file, int bufferSize) throws IOException {" + NL + "                \tisEndOfFile = false;" + NL + "                \ttempRow = null;" + NL + "                \tbis = new java.io.BufferedInputStream(new java.io.FileInputStream(file));" + NL + "                    ois = new java.io.ObjectInputStream(bis);" + NL + "                    buffer = new rowStruct_";
  protected final String TEXT_27 = "[bufferSize];" + NL + "                }" + NL + "" + NL + "                private void load() {" + NL + "                    count = 0;" + NL + "                    index = 0;" + NL + "                    try {" + NL + "\t\t\t\t\t\tif (tempRow!=null) {" + NL + "\t\t\t\t\t\t\tbuffer[count++] = tempRow;" + NL + "\t\t\t\t\t\t\ttempRow = null;" + NL + "\t\t\t\t\t\t}" + NL + "                        while (!isEndOfFile && count < buffer.length) {" + NL + "                            buffer[count] = new rowStruct_";
  protected final String TEXT_28 = "();" + NL + "                            buffer[count].readData(ois);" + NL + "                            count++;" + NL + "                        }" + NL + "\t\t\t\t\t\tif (!isEndOfFile && count >= buffer.length && bis.available() == 0) {" + NL + "\t\t\t\t\t\t\ttempRow = new rowStruct_";
  protected final String TEXT_29 = "();" + NL + "\t\t\t\t\t\t\ttempRow.readData(ois);" + NL + "\t\t\t\t\t\t}" + NL + "                    } catch (Exception e) {" + NL + "\t\t\t\t\t\tif(e.getCause() instanceof java.io.EOFException){" + NL + "\t\t\t\t\t\t\tthis.isEndOfFile = true; // the EOFException" + NL + "\t\t\t\t\t\t\ttempRow = null;" + NL + "\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t\tthrow new RuntimeException(e);" + NL + "\t\t\t\t\t\t}" + NL + "                    }" + NL + "                }" + NL + "" + NL + "                public boolean hasNext() {" + NL + "                \treturn index < count || !isEndOfFile;" + NL + "                }" + NL + "" + NL + "                public rowStruct_";
  protected final String TEXT_30 = " next() {" + NL + "                    if (index >= count) {" + NL + "                        load();" + NL + "                    }" + NL + "" + NL + "                    return buffer[index++];" + NL + "                }" + NL + "" + NL + "                public void remove() {" + NL + "                    throw new UnsupportedOperationException();" + NL + "                }" + NL + "" + NL + "                public void close() throws IOException {" + NL + "                    if (ois != null) {" + NL + "                        ois.close();" + NL + "                        ois = null;" + NL + "                    }" + NL + "                }" + NL + "            }" + NL;
  protected final String TEXT_31 = NL;

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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    
}//HSS_____0

    stringBuffer.append(TEXT_31);
    return stringBuffer.toString();
  }
}
