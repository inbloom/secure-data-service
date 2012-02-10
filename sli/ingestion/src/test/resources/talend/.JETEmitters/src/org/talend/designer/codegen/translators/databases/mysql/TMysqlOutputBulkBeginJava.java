package org.talend.designer.codegen.translators.databases.mysql;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TMysqlOutputBulkBeginJava
{
  protected static String nl;
  public static synchronized TMysqlOutputBulkBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMysqlOutputBulkBeginJava result = new TMysqlOutputBulkBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\t\tString rowSeparator_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "\t\tString fieldSeparator_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + "\t\tchar escapeChar_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ";" + NL + "\t\tchar textEnclosure_";
  protected final String TEXT_10 = " = ";
  protected final String TEXT_11 = ";" + NL + "\t\t";
  protected final String TEXT_12 = NL + "\t\t\tescapeChar_";
  protected final String TEXT_13 = " = textEnclosure_";
  protected final String TEXT_14 = ";";
  protected final String TEXT_15 = NL + "\t\tint escape_mode_";
  protected final String TEXT_16 = " = com.csvreader.CsvWriter.ESCAPE_MODE_DOUBLED;" + NL + "\t\tif(escapeChar_";
  protected final String TEXT_17 = "=='\\\\'){" + NL + "\t\t\tescape_mode_";
  protected final String TEXT_18 = " = com.csvreader.CsvWriter.ESCAPE_MODE_BACKSLASH;" + NL + "\t\t}else if(escapeChar_";
  protected final String TEXT_19 = " == textEnclosure_";
  protected final String TEXT_20 = "){" + NL + "\t\t\tescape_mode_";
  protected final String TEXT_21 = " = com.csvreader.CsvWriter.ESCAPE_MODE_DOUBLED;" + NL + "\t\t}else {" + NL + "\t\t\tthrow new RuntimeException(\"The escape mode only support the '\\\\' or double text enclosure.\");" + NL + "\t\t}" + NL + "\t\t\t\t" + NL + "\t\tjava.io.File file_";
  protected final String TEXT_22 = " = new java.io.File(";
  protected final String TEXT_23 = ");\t\t" + NL + "\t\t";
  protected final String TEXT_24 = NL + "\t\tfile_";
  protected final String TEXT_25 = ".getParentFile().mkdirs();" + NL + "\t\t";
  protected final String TEXT_26 = NL + "\t\t\t\t" + NL + "        java.io.BufferedWriter out_";
  protected final String TEXT_27 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        \t\tnew java.io.FileOutputStream(";
  protected final String TEXT_28 = ", ";
  protected final String TEXT_29 = "),";
  protected final String TEXT_30 = "));";
  protected final String TEXT_31 = NL + "      \tlong diskSpace_";
  protected final String TEXT_32 = " = 0;";
  protected final String TEXT_33 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        String filename = ElementParameterParser.getValue(node,"__FILENAME__");
        
        String rowSeparator = ElementParameterParser.getValue(node,"__ROWSEPARATOR__");
        
        String fieldSeparator = ElementParameterParser.getValue(node,"__FIELDSEPARATOR__");
        
        String escapeChar = ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
        
        String textEnclosure = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
        
        String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
        
        boolean checkDiskSpace = ("true").equals(ElementParameterParser.getValue(node,"__CHECK_DISK_SPACE__"));
        
        boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
        
        boolean isCreateDir = ("true").equals(ElementParameterParser.getValue(node,"__CREATE__"));
        
        boolean containNULL = "true".equalsIgnoreCase(ElementParameterParser.getValue(node,"__RECORDS_CONTAIN_NULL_VALUE__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(rowSeparator.length()==0||("").equals(rowSeparator)?"\"\\n\"" : rowSeparator);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(fieldSeparator.length()==0||("").equals(fieldSeparator)?"\",\"" : fieldSeparator);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(escapeChar.length()==0||("").equals(escapeChar)?"'\\\\'" : escapeChar + ".charAt(0)");
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(textEnclosure.length()==0||("").equals(textEnclosure)?"'\"'" : textEnclosure + ".charAt(0)");
    stringBuffer.append(TEXT_11);
    
		if (containNULL) {//bug 7978 if input have a empty value,tMysqloutputbulk output "\N" instance of "" 

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    
		}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_23);
    if(isCreateDir) {
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    }
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( isAppend);
    stringBuffer.append(TEXT_29);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_30);
    
		if(checkDiskSpace){

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    
		}
    }
}

    stringBuffer.append(TEXT_33);
    return stringBuffer.toString();
  }
}
