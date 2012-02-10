package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TFileOutputDelimitedEndJava
{
  protected static String nl;
  public static synchronized TFileOutputDelimitedEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputDelimitedEndJava result = new TFileOutputDelimitedEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "} finally {";
  protected final String TEXT_4 = NL + "   \tsynchronized (multiThreadLockWrite) {";
  protected final String TEXT_5 = NL + "\tsynchronized (lockWrite) {";
  protected final String TEXT_6 = NL + "\tObject[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "\tsynchronized (pLockWrite) {";
  protected final String TEXT_7 = NL + "if(out";
  protected final String TEXT_8 = "!=null) {" + NL + "\tout";
  protected final String TEXT_9 = ".flush();" + NL + "\tout";
  protected final String TEXT_10 = ".close();" + NL + "}";
  protected final String TEXT_11 = NL + "if(out";
  protected final String TEXT_12 = "!=null) {" + NL + "\tout";
  protected final String TEXT_13 = ".flush();" + NL + "\twriter_";
  protected final String TEXT_14 = ".flush();" + NL + "\tout";
  protected final String TEXT_15 = " = null;" + NL + "}";
  protected final String TEXT_16 = NL + "\tglobalMap.put(\"";
  protected final String TEXT_17 = "_NB_LINE\",nb_line_";
  protected final String TEXT_18 = ");" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_19 = "_FILE_NAME\",fileName_";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL;
  protected final String TEXT_22 = NL + NL + "    CsvWriter";
  protected final String TEXT_23 = ".close();";
  protected final String TEXT_24 = NL + "if(out";
  protected final String TEXT_25 = "!=null) {" + NL + "\tout";
  protected final String TEXT_26 = ".flush();" + NL + "\tout";
  protected final String TEXT_27 = ".close();" + NL + "}";
  protected final String TEXT_28 = NL + NL + "\tCsvWriter";
  protected final String TEXT_29 = ".close();" + NL + "\tout";
  protected final String TEXT_30 = ".flush();" + NL + "\toutWriter_";
  protected final String TEXT_31 = ".flush();";
  protected final String TEXT_32 = NL + "\tCsvWriter";
  protected final String TEXT_33 = ".flush();" + NL + "\tbufferWriter_";
  protected final String TEXT_34 = ".flush();" + NL + "\toutWriter_";
  protected final String TEXT_35 = ".flush();" + NL + "\tCsvWriter";
  protected final String TEXT_36 = " = null;";
  protected final String TEXT_37 = NL + "    globalMap.put(\"";
  protected final String TEXT_38 = "_NB_LINE\",nb_line_";
  protected final String TEXT_39 = ");";
  protected final String TEXT_40 = NL + "}";
  protected final String TEXT_41 = NL + "}";
  protected final String TEXT_42 = NL + "}";
  protected final String TEXT_43 = NL + "\tif(isFileGenerated_";
  protected final String TEXT_44 = " && nb_line_";
  protected final String TEXT_45 = " == 0){" + NL + "\t";
  protected final String TEXT_46 = NL + "\t\tfile_";
  protected final String TEXT_47 = ".delete();" + NL + "\t";
  protected final String TEXT_48 = NL + "\t\tfile";
  protected final String TEXT_49 = ".delete();" + NL + "\t";
  protected final String TEXT_50 = NL + "\t}\t\t";
  protected final String TEXT_51 = NL + "} // finally";
  protected final String TEXT_52 = NL + NL;
  protected final String TEXT_53 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
    boolean isInRowMode = ("true").equals(ElementParameterParser.getValue(node,"__ROW_MODE__"));
    
    String filename = ElementParameterParser.getValue(node,"__FILENAME__");
	boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__")); 
	boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
	
	boolean compress = ("true").equals(ElementParameterParser.getValue(node,"__COMPRESS__"));
	
	String parallelize = ElementParameterParser.getValue(node,"__PARALLELIZE__");
	boolean isParallelize = (parallelize!=null&&!("").equals(parallelize))?("true").equals(parallelize):false;
	
	boolean split = ("true").equals(ElementParameterParser.getValue(node, "__SPLIT__"));

    stringBuffer.append(TEXT_2);
    
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {

    stringBuffer.append(TEXT_3);
     
    if(codeGenArgument.getIsRunInMultiThread()){

    stringBuffer.append(TEXT_4);
    
	}
	if (codeGenArgument.subTreeContainsParallelIterate()) {

    stringBuffer.append(TEXT_5);
     
	}
	
	if (isParallelize) {

    stringBuffer.append(TEXT_6);
     
	}

    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(!useStream){

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
			}else{

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    
			}

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}else{//the following is the tFileOutputCSV component
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_21);
    
			if(!useStream){

    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
				if(isInRowMode){

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    
				}
			}else{
				if(isInRowMode){

    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    
				}else{

    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
				}
			}

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    
		}

     
	if (isParallelize) {

    stringBuffer.append(TEXT_40);
    
	}
	if (codeGenArgument.subTreeContainsParallelIterate()) {

    stringBuffer.append(TEXT_41);
     
	}
	if(codeGenArgument.getIsRunInMultiThread()){

    stringBuffer.append(TEXT_42);
    
	}

    if(!useStream && isDeleteEmptyFile){
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    if(compress && !isAppend && !split){
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    }else{
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    }
    stringBuffer.append(TEXT_50);
    }
    stringBuffer.append(TEXT_51);
    
	}
}

    stringBuffer.append(TEXT_52);
    stringBuffer.append(TEXT_53);
    return stringBuffer.toString();
  }
}
