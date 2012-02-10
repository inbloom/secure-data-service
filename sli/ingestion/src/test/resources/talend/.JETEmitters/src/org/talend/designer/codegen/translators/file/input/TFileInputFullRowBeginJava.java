package org.talend.designer.codegen.translators.file.input;

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
import java.util.Map;

public class TFileInputFullRowBeginJava
{
  protected static String nl;
  public static synchronized TFileInputFullRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputFullRowBeginJava result = new TFileInputFullRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "org.talend.fileprocess.FileInputDelimited fid_";
  protected final String TEXT_3 = " = null;" + NL + "try{" + NL + "fid_";
  protected final String TEXT_4 = " =new org.talend.fileprocess.FileInputDelimited(";
  protected final String TEXT_5 = ",";
  protected final String TEXT_6 = ",\"\",";
  protected final String TEXT_7 = ",";
  protected final String TEXT_8 = ",";
  protected final String TEXT_9 = ",";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = ",";
  protected final String TEXT_12 = ",false);" + NL + "while (fid_";
  protected final String TEXT_13 = ".nextRecord()) {";
  protected final String TEXT_14 = NL + "    \t\t";
  protected final String TEXT_15 = " = null;\t\t\t";
  protected final String TEXT_16 = "\t\t\t" + NL + "\t\t\tboolean whetherReject_";
  protected final String TEXT_17 = " = false;" + NL + "\t\t\t";
  protected final String TEXT_18 = " = new ";
  protected final String TEXT_19 = "Struct();" + NL + "\t\t\ttry {" + NL + "\t\t\t";
  protected final String TEXT_20 = "String temp_";
  protected final String TEXT_21 = " = \"\";" + NL + "\t\t\t\t";
  protected final String TEXT_22 = NL + "\t\t\t\t\t";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = " = fid_";
  protected final String TEXT_25 = ".get(";
  protected final String TEXT_26 = ");";
  protected final String TEXT_27 = "\t\t\t" + NL;
  protected final String TEXT_28 = " ";
  protected final String TEXT_29 = " = null; ";
  protected final String TEXT_30 = NL + "\t\t\t\t" + NL + "    } catch (Exception e) {" + NL + "        whetherReject_";
  protected final String TEXT_31 = " = true;";
  protected final String TEXT_32 = NL + "            throw(e);";
  protected final String TEXT_33 = "                    ";
  protected final String TEXT_34 = NL + "                    ";
  protected final String TEXT_35 = " = new ";
  protected final String TEXT_36 = "Struct();";
  protected final String TEXT_37 = NL + "                    ";
  protected final String TEXT_38 = ".";
  protected final String TEXT_39 = " = ";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = ";";
  protected final String TEXT_42 = NL + "                ";
  protected final String TEXT_43 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_44 = ";";
  protected final String TEXT_45 = NL + "                ";
  protected final String TEXT_46 = " = null;";
  protected final String TEXT_47 = NL + "                System.err.println(e.getMessage());";
  protected final String TEXT_48 = NL + "                ";
  protected final String TEXT_49 = " = null;";
  protected final String TEXT_50 = NL + "            \t";
  protected final String TEXT_51 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_52 = ";";
  protected final String TEXT_53 = NL + "    }" + NL + "" + NL + "\t\t\t\t";
  protected final String TEXT_54 = NL + "\t\t";
  protected final String TEXT_55 = "if(!whetherReject_";
  protected final String TEXT_56 = ") { ";
  protected final String TEXT_57 = "      " + NL + "             if(";
  protected final String TEXT_58 = " == null){ " + NL + "            \t ";
  protected final String TEXT_59 = " = new ";
  protected final String TEXT_60 = "Struct();" + NL + "             }\t\t\t\t";
  protected final String TEXT_61 = NL + "\t    \t ";
  protected final String TEXT_62 = ".";
  protected final String TEXT_63 = " = ";
  protected final String TEXT_64 = ".";
  protected final String TEXT_65 = ";    \t\t\t\t";
  protected final String TEXT_66 = NL + "\t\t";
  protected final String TEXT_67 = " } ";
  protected final String TEXT_68 = "\t";
  protected final String TEXT_69 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	

    stringBuffer.append(TEXT_1);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {
		String filename = ElementParameterParser.getValue(node,"__FILENAME__");
    	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
    	String header = ElementParameterParser.getValue(node, "__HEADER__");
    	if(("").equals(header)){
    		header="0";
    	}
    	String limit = ElementParameterParser.getValue(node, "__LIMIT__");
		if(("").equals(limit)){
			limit = "-1";
		}
    	String footer = ElementParameterParser.getValue(node, "__FOOTER__");
    	if(("").equals(footer)){
    		footer="0";
    	}
    	String random = "-1";
    	String ran = ElementParameterParser.getValue(node, "__RANDOM__");
    	if(("true").equals(ran)){
    		random = ElementParameterParser.getValue(node, "__NB_RANDOM__");
    		if(("").equals(random)){
    			random="0";
    		}
    	}
		
    	
    	String rowSeparator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
    	String removeEmptyRowFlag =  ElementParameterParser.getValue(node, "__REMOVE_EMPTY_ROW__");
    	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
		boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false; 

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(removeEmptyRowFlag );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(header );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(footer );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(limit );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(random );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();

    String rejectConnName = "";
    List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
    if(rejectConns != null && rejectConns.size() > 0) {
        IConnection rejectConn = rejectConns.get(0);
        rejectConnName = rejectConn.getName();
    }
    List<IMetadataColumn> rejectColumnList = null;
    IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
    if(metadataTable != null) {
        rejectColumnList = metadataTable.getListColumns();
    }

    	if (conns!=null) {
    		if (conns.size()>0) {
    			for (int i=0;i<conns.size();i++) {
    				IConnection connTemp = conns.get(i);
    				if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_15);
    
    				}
    			}
    		}
    	}
    	
	String firstConnName = "";
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			firstConnName = conn.getName();			
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_19);
    	
				List<IMetadataColumn> listColumns = metadata.getListColumns();
				int sizeListColumns = listColumns.size();
				boolean noStringTypeExist = false;
				for (int valueN=0; valueN<sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
					}else{
						noStringTypeExist = true;
						break;
					}
				}
				if(noStringTypeExist){
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
				}
				for (int valueN=0; valueN<sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					
    stringBuffer.append(TEXT_22);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_26);
    				}
    stringBuffer.append(TEXT_27);
    if(rejectConnName.equals(firstConnName)) {
    stringBuffer.append(TEXT_28);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_29);
    }
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    
        if (dieOnError) {
            
    stringBuffer.append(TEXT_32);
    
        } else {
            if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

                
    stringBuffer.append(TEXT_33);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_36);
    
                for(IMetadataColumn column : metadata.getListColumns()) {
                    
    stringBuffer.append(TEXT_37);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_39);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_41);
    
                }
                
    stringBuffer.append(TEXT_42);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_46);
    
            } else if(("").equals(rejectConnName)){
                
    stringBuffer.append(TEXT_47);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_49);
    
            } else if(rejectConnName.equals(firstConnName)){
    stringBuffer.append(TEXT_50);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_52);
    }
        } 
        
    stringBuffer.append(TEXT_53);
    
			}
		}
		if (conns.size()>0) {	
			boolean isFirstEnter = true;
			for (int i=0;i<conns.size();i++) {
				IConnection conn = conns.get(i);
				if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getName().compareTo(rejectConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {

    stringBuffer.append(TEXT_54);
     if(isFirstEnter) {
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
     isFirstEnter = false; } 
    stringBuffer.append(TEXT_57);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_60);
    
			    	 for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_61);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_65);
    
				 	}
				}
			}

    stringBuffer.append(TEXT_66);
     if(!isFirstEnter) {
    stringBuffer.append(TEXT_67);
     } 
    stringBuffer.append(TEXT_68);
    
		}
	  }
	}
}

    stringBuffer.append(TEXT_69);
    return stringBuffer.toString();
  }
}
