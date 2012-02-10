package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.List;
import java.util.Map;

public class TFileOutputExcelEndJava
{
  protected static String nl;
  public static synchronized TFileOutputExcelEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputExcelEndJava result = new TFileOutputExcelEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t//modif start" + NL + "\t\t";
  protected final String TEXT_2 = NL + "\t\t\twritableSheet_";
  protected final String TEXT_3 = ".setColumnView(";
  protected final String TEXT_4 = " + ";
  protected final String TEXT_5 = ", fitWidth_";
  protected final String TEXT_6 = "[";
  protected final String TEXT_7 = "]+2);" + NL + "\t\t";
  protected final String TEXT_8 = NL + "\t\t\twritableSheet_";
  protected final String TEXT_9 = ".setColumnView(";
  protected final String TEXT_10 = ", fitWidth_";
  protected final String TEXT_11 = "[";
  protected final String TEXT_12 = "]+2);" + NL + "\t\t";
  protected final String TEXT_13 = NL + "\t//modif end" + NL + "\t";
  protected final String TEXT_14 = NL + "\t\twritableSheet_";
  protected final String TEXT_15 = ".setColumnView(";
  protected final String TEXT_16 = ", fitWidth_";
  protected final String TEXT_17 = "[";
  protected final String TEXT_18 = "]+2);" + NL + "\t";
  protected final String TEXT_19 = NL + "\t\twriteableWorkbook_";
  protected final String TEXT_20 = ".write();" + NL + "\t\twriteableWorkbook_";
  protected final String TEXT_21 = ".close();" + NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_22 = "_NB_LINE\",nb_line_";
  protected final String TEXT_23 = ");" + NL + "\t\t" + NL + "\t";
  protected final String TEXT_24 = NL + "\t\tif(nb_line_";
  protected final String TEXT_25 = " ==1 && needDel_";
  protected final String TEXT_26 = "){" + NL + "\t";
  protected final String TEXT_27 = NL + "\t\tif(nb_line_";
  protected final String TEXT_28 = " == 0){" + NL + "\t";
  protected final String TEXT_29 = NL + "\t\t\tnew java.io.File(";
  protected final String TEXT_30 = ").delete();" + NL + "\t\t}\t\t" + NL + "\t";
  protected final String TEXT_31 = NL + "\t\t\t\t\txlsxTool_";
  protected final String TEXT_32 = ".setColAutoSize(";
  protected final String TEXT_33 = ");" + NL + "\t";
  protected final String TEXT_34 = NL + "\t\t\t\t\txlsxTool_";
  protected final String TEXT_35 = ".setColAutoSize(";
  protected final String TEXT_36 = ");" + NL + "\t";
  protected final String TEXT_37 = NL + "\t";
  protected final String TEXT_38 = NL + "\t\t\txlsxTool_";
  protected final String TEXT_39 = ".writeExcel(";
  protected final String TEXT_40 = ");" + NL + "\t";
  protected final String TEXT_41 = NL + "\t\t\txlsxTool_";
  protected final String TEXT_42 = ".writeExcel(";
  protected final String TEXT_43 = ",";
  protected final String TEXT_44 = ");" + NL + "\t";
  protected final String TEXT_45 = NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_46 = "_NB_LINE\",nb_line_";
  protected final String TEXT_47 = ");" + NL + "\t\t" + NL + "\t";
  protected final String TEXT_48 = NL + "\t\tif(nb_line_";
  protected final String TEXT_49 = " ==1 && needDel_";
  protected final String TEXT_50 = "){" + NL + "\t";
  protected final String TEXT_51 = NL + "\t\tif(nb_line_";
  protected final String TEXT_52 = " == 0){" + NL + "\t";
  protected final String TEXT_53 = NL + "\t\t\tnew java.io.File(";
  protected final String TEXT_54 = ").delete();" + NL + "\t\t}\t\t" + NL + "\t";
  protected final String TEXT_55 = "\t";
  protected final String TEXT_56 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	boolean version07 = ("true").equals(ElementParameterParser.getValue(node,"__VERSION_2007__"));

	boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
	String outStream = ElementParameterParser.getValue(node,"__STREAMNAME__");
	String filename = ElementParameterParser.getValue(node, "__FILENAME__");
	boolean createDir = ("true").equals(ElementParameterParser.getValue(node,"__CREATE__"));	
	boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));	
	boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND_FILE__"));
	boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node, "__INCLUDEHEADER__"));
	
	String allColumnAutoSize = ElementParameterParser.getValue(node, "__IS_ALL_AUTO_SZIE__");
	
	//modif start
	boolean firstCellYAbsolute = ("true").equals(ElementParameterParser.getValue(node, "__FIRST_CELL_Y_ABSOLUTE__"));
	String firstCellXStr = ElementParameterParser.getValue(node, "__FIRST_CELL_X__");
	String firstCellYStr = ElementParameterParser.getValue(node, "__FIRST_CELL_Y__");
	//modif end
	
	boolean isAllColumnAutoSize = (allColumnAutoSize!=null&&!("").equals(allColumnAutoSize))?("true").equals(allColumnAutoSize):false;
	List<Map<String, String>> autoSizeList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__AUTO_SZIE_SETTING__");
	
	if(!version07){//version judgement
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
	    	IMetadataTable metadata = metadatas.get(0);
	        if (metadata!=null) {
	        	List<IMetadataColumn> columns = metadata.getListColumns();
	        	if(isAllColumnAutoSize){
	        		for(int i=0;i<columns.size();i++){
	
    stringBuffer.append(TEXT_1);
    if(firstCellYAbsolute){
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(firstCellYStr);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_7);
    }else{
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_12);
    }
    stringBuffer.append(TEXT_13);
    
	    			}
	    		}else{
	    			if(autoSizeList.size() == columns.size()){
	                	for(int i=0;i<columns.size();i++){
	                		Map<String,String> tmp= autoSizeList.get(i);
	                		if(("true").equals(tmp.get("IS_AUTO_SIZE"))){ 
	
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_18);
    
	                		}
	                	}
	                }
	    		}
	    	}
	    }
	
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    if(!useStream && !isAppend && isDeleteEmptyFile){
		if(isIncludeHeader){
	
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    	}else{
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    	}
    stringBuffer.append(TEXT_29);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_30);
    }
	}else{//version judgement /***excel 2007 xlsx*****/
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
	    	IMetadataTable metadata = metadatas.get(0);
	        if (metadata!=null) {
	        	List<IMetadataColumn> columns = metadata.getListColumns();
	        	if(isAllColumnAutoSize){
	        		for(int i=0;i<columns.size();i++){
	
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_33);
    
	    			}
	    		}else{
	    			if(autoSizeList.size() == columns.size()){
	                	for(int i=0;i<columns.size();i++){
	                		Map<String,String> tmp= autoSizeList.get(i);
	                		if(("true").equals(tmp.get("IS_AUTO_SIZE"))){ 
	
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_36);
    
	                		}
	                	}
	                }
	    		}
	    	}
	    }
	
    stringBuffer.append(TEXT_37);
    
		if(useStream){
	
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(outStream);
    stringBuffer.append(TEXT_40);
    
		}else{	
	
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(createDir);
    stringBuffer.append(TEXT_44);
    
		}
	
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    if(!useStream && !isAppend && isDeleteEmptyFile){
		if(isIncludeHeader){
	
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    	}else{
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    	}
    stringBuffer.append(TEXT_53);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_54);
    }
	}
	
    stringBuffer.append(TEXT_55);
    stringBuffer.append(TEXT_56);
    return stringBuffer.toString();
  }
}
