package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;;

public class TBufferOutputMainJava
{
  protected static String nl;
  public static synchronized TBufferOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TBufferOutputMainJava result = new TBufferOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + NL + "String[] row_";
  protected final String TEXT_4 = "=new String[]{";
  protected final String TEXT_5 = "\"\",";
  protected final String TEXT_6 = "};\t\t";
  protected final String TEXT_7 = NL + "\t    row_";
  protected final String TEXT_8 = "[";
  protected final String TEXT_9 = "] = String.valueOf(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = "); " + NL + "\t    ";
  protected final String TEXT_12 = NL + "\t    if(";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = " != null){" + NL + "\t        ";
  protected final String TEXT_15 = NL + "\t            row_";
  protected final String TEXT_16 = "[";
  protected final String TEXT_17 = "] = ";
  protected final String TEXT_18 = ".";
  protected final String TEXT_19 = ";" + NL + "\t        ";
  protected final String TEXT_20 = NL + "\t            row_";
  protected final String TEXT_21 = "[";
  protected final String TEXT_22 = "] = FormatterUtils.format_Date(";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = ", ";
  protected final String TEXT_25 = ");" + NL + "\t        ";
  protected final String TEXT_26 = NL + "\t            row_";
  protected final String TEXT_27 = "[";
  protected final String TEXT_28 = "] = String.valueOf(";
  protected final String TEXT_29 = ");" + NL + "\t        ";
  protected final String TEXT_30 = NL + "\t            row_";
  protected final String TEXT_31 = "[";
  protected final String TEXT_32 = "] = java.nio.charset.Charset.defaultCharset().decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_33 = ".";
  protected final String TEXT_34 = ")).toString();" + NL + "\t        ";
  protected final String TEXT_35 = NL + "\t            row_";
  protected final String TEXT_36 = "[";
  protected final String TEXT_37 = "] = String.valueOf(";
  protected final String TEXT_38 = ".";
  protected final String TEXT_39 = ");" + NL + "\t        ";
  protected final String TEXT_40 = "                \t\t\t    " + NL + "\t    }else{" + NL + "\t    \trow_";
  protected final String TEXT_41 = "[";
  protected final String TEXT_42 = "] = null;" + NL + "\t    }";
  protected final String TEXT_43 = NL + "\tglobalBuffer.add(row_";
  protected final String TEXT_44 = ");\t" + NL + "\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();	
	String cid = node.getUniqueName();
	
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas != null) && (metadatas.size() > 0)) {//b
        IMetadataTable metadata = metadatas.get(0);
        if (metadata != null) {//a	

    stringBuffer.append(TEXT_2);
    

    String incomingName = "";
        
  	List<? extends IConnection> inputConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
  	if ((inputConns!=null)&&(inputConns.size()>0)) {
  		IConnection incomingConn = inputConns.get(0); 
  		incomingName = incomingConn.getName();

  	}else{
  		return "";
  	}
	
	List<IMetadataColumn> columns = metadata.getListColumns();
	int columnSize = columns.size();

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    for(int j=0;j<columnSize;j++){
    stringBuffer.append(TEXT_5);
    }
    stringBuffer.append(TEXT_6);
    
for (int i = 0; i < columnSize; i++) {
	IMetadataColumn column = columns.get(i);
	String label = column.getLabel();
	JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
	String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
	if(JavaTypesManager.isJavaPrimitiveType( column.getTalendType(), column.isNullable())){
	    
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_11);
    
	}else {
	    
    stringBuffer.append(TEXT_12);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_14);
    					
	        if(javaType == JavaTypesManager.STRING ){
	        
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_19);
    
	        }else if(javaType == JavaTypesManager.DATE && pattern != null){
	        
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_24);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_25);
    
	        }else if(javaType == JavaTypesManager.BIGDECIMAL){
	        
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(column.getPrecision() == null? incomingName + "." + column.getLabel() : incomingName + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_29);
    
	        }else if(javaType == JavaTypesManager.BYTE_ARRAY){
	        
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_34);
    
	        }else{
	        
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_39);
    
	        }
	        
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_42);
     
	}
}


    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
  	}//b
 }//a	

    return stringBuffer.toString();
  }
}
