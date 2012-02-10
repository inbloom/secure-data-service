package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TExternalSortOutputBeginJava
{
  protected static String nl;
  public static synchronized TExternalSortOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TExternalSortOutputBeginJava result = new TExternalSortOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "//create directory only if not exists" + NL + "\t";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = "\t" + NL + "\t\tString fileNewName_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";\t" + NL + "\t\t java.io.File createFile";
  protected final String TEXT_6 = " = new java.io.File(fileNewName_";
  protected final String TEXT_7 = ");" + NL + "\t\t if(!createFile";
  protected final String TEXT_8 = ".exists()){" + NL + "\t\t \t(new java.io.File(fileNewName_";
  protected final String TEXT_9 = ".substring(0,fileNewName_";
  protected final String TEXT_10 = ".lastIndexOf(\"/\")))).mkdirs();" + NL + "\t\t \tcreateFile";
  protected final String TEXT_11 = ".createNewFile();" + NL + "\t\t }" + NL + "\t";
  protected final String TEXT_12 = NL + "\t\tint nb_line_";
  protected final String TEXT_13 = " = 0;" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ";" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_ROWSEP_";
  protected final String TEXT_16 = " = \"\\r\\n\";\t\t";
  protected final String TEXT_17 = NL + "\t\t\tfinal java.io.File tempFile_";
  protected final String TEXT_18 = " = java.io.File.createTempFile(\"";
  protected final String TEXT_19 = "\", null, new java.io.File(";
  protected final String TEXT_20 = "));";
  protected final String TEXT_21 = NL + "\t\t\tfinal java.io.File tempFile_";
  protected final String TEXT_22 = " = java.io.File.createTempFile(\"";
  protected final String TEXT_23 = "\", null);";
  protected final String TEXT_24 = NL + "\t\ttempFile_";
  protected final String TEXT_25 = ".deleteOnExit();" + NL + "\t\t" + NL + "\t\tfinal java.io.BufferedWriter out_";
  protected final String TEXT_26 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        \t\tnew java.io.FileOutputStream(tempFile_";
  protected final String TEXT_27 = ")));";
  protected final String TEXT_28 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();	
	String fieldSeparator = ElementParameterParser.getValue(node,"__FIELDSEPARATOR__");
	String tempDir = ElementParameterParser.getValue(node,"__TEMP_DIR__");

    stringBuffer.append(TEXT_1);
    if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){
    stringBuffer.append(TEXT_2);
    String fileNewname = ElementParameterParser.getValue(node,"__FILENAME__");
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(fileNewname);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    }
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
		if("true".equals(ElementParameterParser.getValue(node,"__SET_INPUT_TEMP_DIR__"))){

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(ElementParameterParser.getValue(node,"__INPUT_TEMP_DIR__"));
    stringBuffer.append(TEXT_20);
    
		}else{

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    
		}

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(TEXT_28);
    return stringBuffer.toString();
  }
}
