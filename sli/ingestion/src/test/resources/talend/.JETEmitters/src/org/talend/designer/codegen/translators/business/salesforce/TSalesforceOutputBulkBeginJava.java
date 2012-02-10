package org.talend.designer.codegen.translators.business.salesforce;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TSalesforceOutputBulkBeginJava
{
  protected static String nl;
  public static synchronized TSalesforceOutputBulkBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceOutputBulkBeginJava result = new TSalesforceOutputBulkBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "" + NL + "java.io.File file_";
  protected final String TEXT_3 = " = new java.io.File(";
  protected final String TEXT_4 = ");\t\t" + NL + "file_";
  protected final String TEXT_5 = ".getParentFile().mkdirs();" + NL + "" + NL + "com.csvreader.CsvWriter csvWriter_";
  protected final String TEXT_6 = " = new com.csvreader.CsvWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        new java.io.FileOutputStream(file_";
  protected final String TEXT_7 = ", ";
  protected final String TEXT_8 = "), \"UTF-8\")), ',');" + NL;
  protected final String TEXT_9 = "  \t" + NL + "\t                    \tString[] header_";
  protected final String TEXT_10 = "=new String[";
  protected final String TEXT_11 = "];" + NL + "\t                    \t";
  protected final String TEXT_12 = NL + "\t\t\t\t\t\t\t\theader_";
  protected final String TEXT_13 = "[";
  protected final String TEXT_14 = "] = \"";
  protected final String TEXT_15 = "\";" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_16 = NL + "\tcsvWriter_";
  protected final String TEXT_17 = ".writeRecord(header_";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + "        ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    
	    String filename = ElementParameterParser.getValue(node,"__FILENAME__");
	    boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
        

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(isAppend );
    stringBuffer.append(TEXT_8);
    		if(!isAppend){
			List< ? extends IConnection> conns = node.getIncomingConnections();
	        	if(conns!=null){
	        		if (conns.size()>0){
	        		    IConnection conn =conns.get(0);
	            		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	            			List<IMetadataColumn> columns = metadata.getListColumns();
	                		int sizeColumns = columns.size();
	            			
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(sizeColumns);
    stringBuffer.append(TEXT_11);
    
	                    
	            			for (int i = 0; i < sizeColumns; i++) {
	                			IMetadataColumn column = columns.get(i);
							
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_15);
    
							}
						}
					}
				}

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    
		}
	}
}

    stringBuffer.append(TEXT_19);
    return stringBuffer.toString();
  }
}
