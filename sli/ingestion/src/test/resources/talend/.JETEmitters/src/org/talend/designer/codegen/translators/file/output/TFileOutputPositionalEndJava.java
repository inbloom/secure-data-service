package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TFileOutputPositionalEndJava
{
  protected static String nl;
  public static synchronized TFileOutputPositionalEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputPositionalEndJava result = new TFileOutputPositionalEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tsynchronized (multiThreadLockWrite) {";
  protected final String TEXT_2 = NL + "\tsynchronized (lockWrite) {";
  protected final String TEXT_3 = NL + "\tObject[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "\tsynchronized (pLockWrite) {";
  protected final String TEXT_4 = NL + "\tout";
  protected final String TEXT_5 = ".close();";
  protected final String TEXT_6 = NL + "\tout";
  protected final String TEXT_7 = ".flush();" + NL + "\toutWriter_";
  protected final String TEXT_8 = ".flush();" + NL + "\toutWriter_";
  protected final String TEXT_9 = " = null;";
  protected final String TEXT_10 = NL + "    } ";
  protected final String TEXT_11 = NL + "\t}";
  protected final String TEXT_12 = NL + "\t}";
  protected final String TEXT_13 = NL + "\tglobalMap.put(\"";
  protected final String TEXT_14 = "_NB_LINE\",nb_line_";
  protected final String TEXT_15 = ");";
  protected final String TEXT_16 = NL;
  protected final String TEXT_17 = NL + "\tif(nb_line_";
  protected final String TEXT_18 = " == 0){" + NL + "\t\tnew java.io.File(";
  protected final String TEXT_19 = ").delete();" + NL + "\t}\t\t";
  protected final String TEXT_20 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
    String filename = ElementParameterParser.getValue(node, "__FILENAME__");
    boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));
	boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
	
	boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));

	String parallelize = ElementParameterParser.getValue(node,"__PARALLELIZE__");
	boolean isParallelize = (parallelize!=null&&!("").equals(parallelize))?("true").equals(parallelize):false;

	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {

     
	if(codeGenArgument.getIsRunInMultiThread()){

    stringBuffer.append(TEXT_1);
    
	}
	if (codeGenArgument.subTreeContainsParallelIterate()) {

    stringBuffer.append(TEXT_2);
     
	}
	if (isParallelize) {

    stringBuffer.append(TEXT_3);
     
	}
	if(!useStream){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
     
	}else{

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
	}
	if ( isParallelize) {

    stringBuffer.append(TEXT_10);
    
    }
	if (codeGenArgument.subTreeContainsParallelIterate()) {

    stringBuffer.append(TEXT_11);
     
	}
	if(codeGenArgument.getIsRunInMultiThread()){

    stringBuffer.append(TEXT_12);
    
	}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    
		}
	}

    stringBuffer.append(TEXT_16);
    if(!useStream && !isAppend && isDeleteEmptyFile){
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_19);
    }
    stringBuffer.append(TEXT_20);
    return stringBuffer.toString();
  }
}
