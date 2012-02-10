package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TExternalSortOutputMainJava
{
  protected static String nl;
  public static synchronized TExternalSortOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TExternalSortOutputMainJava result = new TExternalSortOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "   \t\t\t\t" + NL + "\t    \t\t\t\tif(";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " != null) {" + NL + "    \t\t\t\t";
  protected final String TEXT_5 = NL + "    \t\t\t\t\t\tout_";
  protected final String TEXT_6 = ".write(" + NL + "    \t\t\t\t";
  protected final String TEXT_7 = NL + "\t\t\t\t\t\t\t\tFormatterUtils.format_Date(";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = ")";
  protected final String TEXT_11 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = NL + "\t\t\t\t\t\t\t\tString.valueOf(";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = ")";
  protected final String TEXT_16 = NL + "\t\t\t\t\t\t\t);";
  protected final String TEXT_17 = NL + "\t    \t\t\t\t} " + NL + "\t\t\t\t\t";
  protected final String TEXT_18 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\t\tout_";
  protected final String TEXT_19 = ".write(OUT_DELIM_";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL + "    \t\t\tout_";
  protected final String TEXT_22 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_23 = ");" + NL + "    \t\t\tnb_line_";
  protected final String TEXT_24 = "++;";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        String cid = node.getUniqueName();

    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			for (int i = 0; i < sizeColumns; i++) {
    				IMetadataColumn column = columns.get(i);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
					if(!isPrimitive) {
    				
    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_4);
    
    				} 
    				
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
    				String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {

    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_10);
    				
					} else if(javaType == JavaTypesManager.STRING) {

    stringBuffer.append(TEXT_11);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(column.getLabel() );
    				
					} else {

    stringBuffer.append(TEXT_13);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_15);
    				
					}

    stringBuffer.append(TEXT_16);
    
					if(!isPrimitive) {
    				
    stringBuffer.append(TEXT_17);
    
    				} 
					if(i != sizeColumns - 1) {

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    
    				}
    			}
    			
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    
    		}
    	}
    }
}

    return stringBuffer.toString();
  }
}
