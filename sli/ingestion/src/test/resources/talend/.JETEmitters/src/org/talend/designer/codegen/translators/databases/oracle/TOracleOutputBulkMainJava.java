package org.talend.designer.codegen.translators.databases.oracle;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TOracleOutputBulkMainJava
{
  protected static String nl;
  public static synchronized TOracleOutputBulkMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOracleOutputBulkMainJava result = new TOracleOutputBulkMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    \t\t\tStringBuilder sb_";
  protected final String TEXT_2 = " = new StringBuilder(";
  protected final String TEXT_3 = ");" + NL + "    \t\t\t";
  protected final String TEXT_4 = "   \t\t\t\t" + NL + "\t    \t\t\t\tif(";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = " != null) {" + NL + "    \t\t\t\t";
  protected final String TEXT_7 = NL + "    \t\t\t\t" + NL + "    \t\t\t\t";
  protected final String TEXT_8 = "\t\t" + NL + "\t\t\t\t\t\tsb_";
  protected final String TEXT_9 = ".append(OUT_FIELDS_ENCLOSURE_LEFT_";
  protected final String TEXT_10 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_11 = NL + "    \t\t\t\t\t\tsb_";
  protected final String TEXT_12 = ".append(" + NL + "\t\t\t\t\t";
  protected final String TEXT_13 = NL + "\t\t\t\t\t\t\"<startlob>\" +" + NL + "\t\t\t\t\t";
  protected final String TEXT_14 = "    \t\t\t\t\t\t";
  protected final String TEXT_15 = NL + "\t\t\t\t\t\tFormatterUtils.format_Date_Locale(";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ", \"";
  protected final String TEXT_19 = "\")";
  protected final String TEXT_20 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_21 = " " + NL + "                                      FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_22 = "), ";
  protected final String TEXT_23 = ", ";
  protected final String TEXT_24 = ")                                  ";
  protected final String TEXT_25 = " " + NL + "                                      FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = "), ";
  protected final String TEXT_28 = ", ";
  protected final String TEXT_29 = ")                                           ";
  protected final String TEXT_30 = " " + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_31 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_32 = ".";
  protected final String TEXT_33 = NL + "\t\t\t\t\t\t\t new String(((byte[])(";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = ")))";
  protected final String TEXT_36 = NL + "\t\t\t\t\t\t\t\tString.valueOf(";
  protected final String TEXT_37 = ".";
  protected final String TEXT_38 = ")";
  protected final String TEXT_39 = NL + "\t\t\t\t\t\t+\"<endlob>\"";
  protected final String TEXT_40 = "   " + NL + "\t\t\t\t\t\t);";
  protected final String TEXT_41 = "\t\t" + NL + "\t\t\t\t\t\tsb_";
  protected final String TEXT_42 = ".append(OUT_FIELDS_ENCLOSURE_RIGHT_";
  protected final String TEXT_43 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_44 = NL + "\t    \t\t\t\t} " + NL + "\t\t\t\t\t";
  protected final String TEXT_45 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\t\tsb_";
  protected final String TEXT_46 = ".append(OUT_DELIM_";
  protected final String TEXT_47 = ");";
  protected final String TEXT_48 = NL + "    \t\t\t" + NL + "    \t\t\t" + NL + "    \t\t\t";
  protected final String TEXT_49 = NL + "    \t\t\tsb_";
  protected final String TEXT_50 = ".append(OUT_DELIM_ROWSEP_";
  protected final String TEXT_51 = ");" + NL + "    \t\t\t";
  protected final String TEXT_52 = NL + "    \t\t\tsb_";
  protected final String TEXT_53 = ".append(OUT_DELIM_ROWSEP_WITH_LOB_";
  protected final String TEXT_54 = ");" + NL + "    \t\t\t";
  protected final String TEXT_55 = NL + "    \t\t\tout";
  protected final String TEXT_56 = ".write(sb_";
  protected final String TEXT_57 = ".toString());" + NL + "    \t\t\tnb_line_";
  protected final String TEXT_58 = "++;";
  protected final String TEXT_59 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();

boolean hasLobflag=false;

if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	
    	String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
    	boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
    	String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
    	String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER); 
    	String locale = ElementParameterParser.getValue(node, "__DATE_LOCALE__");
    	boolean useFieldsEnclosure = ("true").equals(ElementParameterParser.getValue(node,"__USE_FIELDS_ENCLOSURE__"));
    	String stringBuilderSize = ElementParameterParser.getValue(node, "__STRING_BUILDER_SIZE__");
    	
    	for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    		
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(stringBuilderSize);
    stringBuffer.append(TEXT_3);
    
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			for (int i = 0; i < sizeColumns; i++) {
    				IMetadataColumn column = columns.get(i);
    				
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String dbType = columns.get(i).getType();
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
					if(!isPrimitive) {
					
    				
    stringBuffer.append(TEXT_4);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_6);
    
    				} 
    				
    stringBuffer.append(TEXT_7);
    
					if (useFieldsEnclosure && !( "BLOB".equals (dbType) || "CLOB".equals(dbType))) {
					
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
					}
					
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
     if ("BLOB".equals (dbType) || "CLOB".equals(dbType)) {
						hasLobflag=true;
					
    stringBuffer.append(TEXT_13);
    }
    stringBuffer.append(TEXT_14);
    
    				String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {
    					if(("DEFAULT").equals(locale)){
							locale = java.util.Locale.getDefault().toString();
						}else if(("CUSTOM").equals(locale)){
							locale = ElementParameterParser.getValue(node, "__CUSTOM_DATE_LOCALE__");
							locale = locale.substring(1,locale.length()-1);
						}else{
							locale = locale;
						}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(locale);
    stringBuffer.append(TEXT_19);
    
					}  else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) {  										
							
    stringBuffer.append(TEXT_20);
     if(javaType == JavaTypesManager.BIGDECIMAL) {
    stringBuffer.append(TEXT_21);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_22);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_23);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_24);
     } else { 
    stringBuffer.append(TEXT_25);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_27);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_29);
     } 
    stringBuffer.append(TEXT_30);
    
					} else if(javaType == JavaTypesManager.STRING) {

    stringBuffer.append(TEXT_31);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_32);
    stringBuffer.append(column.getLabel());
    				
					} else if (javaType == JavaTypesManager.BYTE_ARRAY ) {					

    stringBuffer.append(TEXT_33);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_35);
    					
					} else  {

    stringBuffer.append(TEXT_36);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_38);
    				
					}

     
					if ("BLOB".equals (dbType) || "CLOB".equals(dbType)) {
    stringBuffer.append(TEXT_39);
    
					}

    stringBuffer.append(TEXT_40);
    	
					if (useFieldsEnclosure && !( "BLOB".equals (dbType) || "CLOB".equals(dbType))) {
					
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    
					}
					if(!isPrimitive) {
    				
    stringBuffer.append(TEXT_44);
    
    				} 
					if(i != sizeColumns - 1) {

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    
    				}
    			}
    			
    stringBuffer.append(TEXT_48);
     if (!hasLobflag) {
    			
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    
    			}else { 
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    }
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    
    		}
    	}
    }
}

    stringBuffer.append(TEXT_59);
    return stringBuffer.toString();
  }
}
