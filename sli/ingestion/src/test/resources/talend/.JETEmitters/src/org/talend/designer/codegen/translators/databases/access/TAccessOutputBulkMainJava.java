package org.talend.designer.codegen.translators.databases.access;

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

public class TAccessOutputBulkMainJava
{
  protected static String nl;
  public static synchronized TAccessOutputBulkMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAccessOutputBulkMainJava result = new TAccessOutputBulkMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "   \t\t\t\t" + NL + "\t    \t\t\t\tif(";
  protected final String TEXT_2 = ".";
  protected final String TEXT_3 = " != null) {" + NL + "    \t\t\t\t";
  protected final String TEXT_4 = NL + "    \t\t\t\t";
  protected final String TEXT_5 = "\t\t" + NL + "\t\t\t\t\t\tout";
  protected final String TEXT_6 = ".write(OUT_FIELDS_ENCLOSURE_";
  protected final String TEXT_7 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_8 = NL + "    \t\t\t\t\tout";
  protected final String TEXT_9 = ".write(" + NL + "    \t\t\t\t";
  protected final String TEXT_10 = NL + "\t\t\t\t\t\t\t\tFormatterUtils.format_Date(";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = ", ";
  protected final String TEXT_13 = ")";
  protected final String TEXT_14 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = ".toPlainString()";
  protected final String TEXT_19 = NL + "\t\t\t\t\t\t\t\tString.valueOf(";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = ")";
  protected final String TEXT_22 = NL + "\t\t\t\t\t\t\t);";
  protected final String TEXT_23 = "\t\t" + NL + "\t\t\t\t\t\tout";
  protected final String TEXT_24 = ".write(OUT_FIELDS_ENCLOSURE_";
  protected final String TEXT_25 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_26 = NL + "\t    \t\t\t\t} " + NL + "\t\t\t\t\t";
  protected final String TEXT_27 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\t\tout";
  protected final String TEXT_28 = ".write(OUT_DELIM_";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL + "    \t\t\tout";
  protected final String TEXT_31 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_32 = ");" + NL + "    \t\t\tnb_line_";
  protected final String TEXT_33 = "++;";

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
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	boolean isUseTextEnclosure = ("true").equals(ElementParameterParser.getValue(node,"__USE_FIELDS_ENCLOSURE__"));
    	for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			for (int i = 0; i < sizeColumns; i++) {
    				IMetadataColumn column = columns.get(i);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
					if(!isPrimitive) {
    				
    stringBuffer.append(TEXT_1);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_3);
    
    				} 
    				
    stringBuffer.append(TEXT_4);
    
					if (isUseTextEnclosure) {
					
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
					}
					
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
    				String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {

    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_13);
    				
					} else if(javaType == JavaTypesManager.STRING) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(column.getLabel() );
    				
					} else if(javaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_18);
    
					} else {

    stringBuffer.append(TEXT_19);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_21);
    				
					}

    stringBuffer.append(TEXT_22);
    
					if (isUseTextEnclosure) {
					
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
					}
					if(!isPrimitive) {
    				
    stringBuffer.append(TEXT_26);
    
    				} 
					if(i != sizeColumns - 1) {

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    
    				}
    			}
    			
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    
    		}
    	}
    }
}

    return stringBuffer.toString();
  }
}
