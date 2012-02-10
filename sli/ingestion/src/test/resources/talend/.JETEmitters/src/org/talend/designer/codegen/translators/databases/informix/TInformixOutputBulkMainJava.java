package org.talend.designer.codegen.translators.databases.informix;

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

public class TInformixOutputBulkMainJava
{
  protected static String nl;
  public static synchronized TInformixOutputBulkMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TInformixOutputBulkMainJava result = new TInformixOutputBulkMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  \t" + NL + "                    \tString[] row_";
  protected final String TEXT_2 = "=new String[]{";
  protected final String TEXT_3 = "null,";
  protected final String TEXT_4 = "};" + NL + "                    \t";
  protected final String TEXT_5 = NL + "                \t\t\t\t\trow_";
  protected final String TEXT_6 = "[";
  protected final String TEXT_7 = "] = String.valueOf(" + NL + "                \t\t\t\t\t\t\ttrue == ";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = " ?\"t\":\"f\"" + NL + "                \t\t\t\t\t\t\t);" + NL + "                \t\t\t\t";
  protected final String TEXT_10 = NL + "                \t\t\t    \trow_";
  protected final String TEXT_11 = "[";
  protected final String TEXT_12 = "] = String.valueOf(";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = ");" + NL + "                \t\t\t    ";
  protected final String TEXT_15 = NL + "                \t\t\t    if(";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = " != null){" + NL + "                \t\t\t        ";
  protected final String TEXT_18 = NL + "                \t\t\t            row_";
  protected final String TEXT_19 = "[";
  protected final String TEXT_20 = "] = FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_21 = "), null, ";
  protected final String TEXT_22 = ");" + NL + "                \t\t\t            ";
  protected final String TEXT_23 = NL + "                \t\t\t            row_";
  protected final String TEXT_24 = "[";
  protected final String TEXT_25 = "] = FormatterUtils.format_Number(String.valueOf(";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = "), null, ";
  protected final String TEXT_28 = ");" + NL + "                \t\t\t            ";
  protected final String TEXT_29 = NL + "                \t\t\t            row_";
  protected final String TEXT_30 = "[";
  protected final String TEXT_31 = "] = FormatterUtils.format_Date(";
  protected final String TEXT_32 = ".";
  protected final String TEXT_33 = ", \"";
  protected final String TEXT_34 = "\");" + NL + "                \t\t\t            ";
  protected final String TEXT_35 = NL + "                \t\t\t            row_";
  protected final String TEXT_36 = "[";
  protected final String TEXT_37 = "] = (";
  protected final String TEXT_38 = ".";
  protected final String TEXT_39 = "==true ? \"t\" : \"f\");" + NL + "                \t\t\t            ";
  protected final String TEXT_40 = NL + "                \t\t\t            row_";
  protected final String TEXT_41 = "[";
  protected final String TEXT_42 = "] = String.valueOf(";
  protected final String TEXT_43 = ".";
  protected final String TEXT_44 = ");" + NL + "                \t\t\t            " + NL + "                \t\t\t            ";
  protected final String TEXT_45 = "                \t\t\t    " + NL + "                \t\t\t    }" + NL + "                \t\t ";
  protected final String TEXT_46 = NL + "            \t\t\t    csvWriter_";
  protected final String TEXT_47 = ".writeRecord(row_";
  protected final String TEXT_48 = ",true);\t" + NL + "            \t\t\t    ";
  protected final String TEXT_49 = NL + "        \t\t                if(nb_line_";
  protected final String TEXT_50 = "%";
  protected final String TEXT_51 = " == 0) {            \t\t\t    " + NL + "            \t\t\t    \t\tcsvWriter_";
  protected final String TEXT_52 = ".flush();" + NL + "            \t\t\t    \t}\t " + NL + "            \t\t\t    ";
  protected final String TEXT_53 = NL + "            \t\t\t\tnb_line_";
  protected final String TEXT_54 = "++;" + NL + "            \t\t\t";
  protected final String TEXT_55 = NL;

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
    
    	boolean flushOnRow = ("true").equals(ElementParameterParser.getValue(node, "__FLUSHONROW__"));
    	
    	String flushMod = ElementParameterParser.getValue(node, "__FLUSHONROW_NUM__");
    	
        String encoding = ElementParameterParser.getValue(node,"__ENCODING__");    	
        
    	boolean setDbmoney = ("true").equals(ElementParameterParser.getValue(node,"__SET_DBMONEY__"));
    	String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
        
        String datePattern = ElementParameterParser.getValue(node,"__DATE_FORMAT__");
        datePattern = datePattern.split(",")[0];
        String datePatternCustom = ElementParameterParser.getValue(node,"__DATE_FORMAT_CUSTOM__");                
        if ("CUSTOM".equals(datePattern))
        	datePattern = datePatternCustom.substring(1,datePatternCustom.length()-1);
        
    	List< ? extends IConnection> conns = node.getIncomingConnections();
        	if(conns!=null){
        		if (conns.size()>0){
        		    IConnection conn =conns.get(0);
            		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            			List<IMetadataColumn> columns = metadata.getListColumns();
                		int sizeColumns = columns.size();
            			
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    for(int j=0;j<sizeColumns;j++){
    stringBuffer.append(TEXT_3);
    }
    stringBuffer.append(TEXT_4);
    
            			for (int i = 0; i < sizeColumns; i++) {
                			IMetadataColumn column = columns.get(i);
                			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
                			//String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
                			if(JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable())){
                			
                				if(javaType == JavaTypesManager.BOOLEAN ){
                				
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_9);
    
                				} else {
                			    
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    
                			    }
                			}else {
                			    
    stringBuffer.append(TEXT_15);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_17);
    					
                			        if(setDbmoney && javaType == JavaTypesManager.BIGDECIMAL ){
                			            
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_21);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_22);
    
                			        }else if(setDbmoney && javaType == JavaTypesManager.FLOAT ){
                			            
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_27);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_28);
    
                			        }else if(javaType == JavaTypesManager.DATE){
                			            
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append( datePattern );
    stringBuffer.append(TEXT_34);
    
                			        }else if(javaType == JavaTypesManager.BOOLEAN){
                			            
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_39);
    
                			        }else{
                			            
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_44);
    
                			        }
                			        
    stringBuffer.append(TEXT_45);
     
                			}
            			} 

            			
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
     if(flushOnRow) { 
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    }
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
       		
            		}
        		
        		}
        	}
    }
}

    stringBuffer.append(TEXT_55);
    return stringBuffer.toString();
  }
}
