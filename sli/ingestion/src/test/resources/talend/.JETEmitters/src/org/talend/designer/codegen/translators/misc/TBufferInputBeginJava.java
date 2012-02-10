package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TBufferInputBeginJava
{
  protected static String nl;
  public static synchronized TBufferInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TBufferInputBeginJava result = new TBufferInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL;
  protected final String TEXT_3 = NL + "String[] row_";
  protected final String TEXT_4 = " = new String[";
  protected final String TEXT_5 = "];" + NL + "for (int n = 0; n < globalBuffer.size(); n++)" + NL + "{" + NL + "\trow_";
  protected final String TEXT_6 = " = (String[])globalBuffer.get(n);";
  protected final String TEXT_7 = "\t" + NL + "\tif(";
  protected final String TEXT_8 = " < row_";
  protected final String TEXT_9 = ".length){" + NL + "\t";
  protected final String TEXT_10 = NL + "\t\t";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = " = row_";
  protected final String TEXT_13 = "[";
  protected final String TEXT_14 = "];\t" + NL + "\t";
  protected final String TEXT_15 = NL + "\t\t";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = " = row_";
  protected final String TEXT_18 = "[";
  protected final String TEXT_19 = "].getBytes(";
  protected final String TEXT_20 = ");\t" + NL + "\t";
  protected final String TEXT_21 = NL + "\t\t";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = " = ParserUtils.parseTo_Date(row_";
  protected final String TEXT_24 = "[";
  protected final String TEXT_25 = "], ";
  protected final String TEXT_26 = ");" + NL + "\t";
  protected final String TEXT_27 = " " + NL + "\t\t";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = " = ParserUtils.parseTo_";
  protected final String TEXT_30 = "(ParserUtils.parseTo_Number(row_";
  protected final String TEXT_31 = "[";
  protected final String TEXT_32 = "], ";
  protected final String TEXT_33 = ", ";
  protected final String TEXT_34 = "));" + NL + "\t";
  protected final String TEXT_35 = NL + "\t\t";
  protected final String TEXT_36 = ".";
  protected final String TEXT_37 = " = ParserUtils.parseTo_";
  protected final String TEXT_38 = "(row_";
  protected final String TEXT_39 = "[";
  protected final String TEXT_40 = "]);" + NL + "\t";
  protected final String TEXT_41 = NL + "\t}" + NL + "\t";
  protected final String TEXT_42 = NL + "\telse{" + NL + "\t\t";
  protected final String TEXT_43 = ".";
  protected final String TEXT_44 = " = null;" + NL + "\t}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
List<IMetadataTable> metadatas = node.getMetadataList();

if ((metadatas!=null) && (metadatas.size() > 0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata != null) {
    	
    	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
    	String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
		boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
		String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
		String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
		
    	
		List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
		if (conns != null){
		
			if (conns.size()>0){
		
				IConnection conn =conns.get(0);
				String connName = conn.getName();
		
				if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
		
					List<IMetadataColumn> columns = metadata.getListColumns();
					int nbColumns = columns.size();
//-----------

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(nbColumns);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
//-----------
					for ( int i = 0; i < nbColumns; i++ ){
					
						IMetadataColumn column = columns.get(i);
						String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
						JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
						String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
						String columnName = column.getLabel();	
//-----------

    stringBuffer.append(TEXT_7);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
		if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
	
    stringBuffer.append(TEXT_10);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_14);
    
		}else if(javaType == JavaTypesManager.BYTE_ARRAY){
	
    stringBuffer.append(TEXT_15);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_20);
    	
		}else if(javaType == JavaTypesManager.DATE){
	
    stringBuffer.append(TEXT_21);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(patternValue );
    stringBuffer.append(TEXT_26);
    
		}else if(advancedSeparator && JavaTypesManager.isNumberType(javaType)) {
	
    stringBuffer.append(TEXT_27);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(thousandsSeparator);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(decimalSeparator);
    stringBuffer.append(TEXT_34);
    
		} else { 
	
    stringBuffer.append(TEXT_35);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_40);
    
		}
	
    stringBuffer.append(TEXT_41);
    if(JavaTypesManager.isJavaPrimitiveType(javaType,false)==false){
    stringBuffer.append(TEXT_42);
    stringBuffer.append(connName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_44);
    	}
//-----------
					}
				}		
			}
		}			 
	}	    
}

    return stringBuffer.toString();
  }
}
