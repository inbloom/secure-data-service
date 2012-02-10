package org.talend.designer.codegen.translators.business_intelligence.jasper;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.ArrayList;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TJasperOutputMainJava
{
  protected static String nl;
  public static synchronized TJasperOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJasperOutputMainJava result = new TJasperOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  \t" + NL + "            String[] row_";
  protected final String TEXT_2 = "=new String[";
  protected final String TEXT_3 = "];\t\t";
  protected final String TEXT_4 = NL + "\t\t\trow_";
  protected final String TEXT_5 = "[";
  protected final String TEXT_6 = "] =String.valueOf(";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = "); ";
  protected final String TEXT_9 = NL + " \t\t\tif(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = " == null){" + NL + " \t\t\t\trow_";
  protected final String TEXT_12 = "[";
  protected final String TEXT_13 = "]=\"\";" + NL + " \t\t\t}else{";
  protected final String TEXT_14 = NL + "\t\t\t\trow_";
  protected final String TEXT_15 = "[";
  protected final String TEXT_16 = "] = ";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = ";";
  protected final String TEXT_19 = NL + "\t\t\t\trow_";
  protected final String TEXT_20 = "[";
  protected final String TEXT_21 = "] = FormatterUtils.format_Date(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ", ";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = NL + "\t\t\t\trow_";
  protected final String TEXT_26 = "[";
  protected final String TEXT_27 = "] = java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = ")).toString();";
  protected final String TEXT_30 = NL + "\t\t\t\trow_";
  protected final String TEXT_31 = "[";
  protected final String TEXT_32 = "] = String.valueOf(";
  protected final String TEXT_33 = ".";
  protected final String TEXT_34 = ");";
  protected final String TEXT_35 = NL + "\t\t\t}";
  protected final String TEXT_36 = NL + " \t\t\tCsvWriter_";
  protected final String TEXT_37 = ".writeRecord(row_";
  protected final String TEXT_38 = ");\t" + NL + " \t\t\tnb_line_";
  protected final String TEXT_39 = "++;";

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
        String filename = ElementParameterParser.getValue(node,"__TEMP_FILE__");
 		List< ? extends IConnection> conns = node.getIncomingConnections();
    	if(conns!=null && conns.size()>0){
       		IConnection conn =conns.get(0);
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    			List<IMetadataColumn> columns = metadata.getListColumns();
        		int sizeColumns = columns.size();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(sizeColumns);
    stringBuffer.append(TEXT_3);
    
    			for (int i = 0; i < sizeColumns; i++) {
        			IMetadataColumn column = columns.get(i);
        			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
        			String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
        			if(JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable())){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_8);
    
    				}else { 

    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_13);
    						if(javaType == JavaTypesManager.STRING ){

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_18);
    
						}else if(javaType == JavaTypesManager.DATE && pattern != null){

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_24);
    
						}else if(javaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_29);
    
						}else{

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_34);
    
 						}

    stringBuffer.append(TEXT_35);
    
  					}
				}

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    
			}
 	 	}
	}
}

    return stringBuffer.toString();
  }
}
