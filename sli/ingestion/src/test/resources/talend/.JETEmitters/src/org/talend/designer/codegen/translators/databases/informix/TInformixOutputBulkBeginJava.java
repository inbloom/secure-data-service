package org.talend.designer.codegen.translators.databases.informix;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TInformixOutputBulkBeginJava
{
  protected static String nl;
  public static synchronized TInformixOutputBulkBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TInformixOutputBulkBeginJava result = new TInformixOutputBulkBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\t\tchar rowSeparator_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "\t\tchar fieldSeparator_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + "\t\t" + NL + "\t\tjava.io.File file_";
  protected final String TEXT_8 = " = new java.io.File(";
  protected final String TEXT_9 = ");\t\t" + NL + "\t\t";
  protected final String TEXT_10 = NL + "\t\tfile_";
  protected final String TEXT_11 = ".getParentFile().mkdirs();" + NL + "\t\t";
  protected final String TEXT_12 = NL + "\t\t\t\t" + NL + "\t\tcom.csvreader.CsvWriter csvWriter_";
  protected final String TEXT_13 = " = new com.csvreader.CsvWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        new java.io.FileOutputStream(file_";
  protected final String TEXT_14 = ", ";
  protected final String TEXT_15 = "), ";
  protected final String TEXT_16 = ")), fieldSeparator_";
  protected final String TEXT_17 = ");" + NL + "        " + NL + "        csvWriter_";
  protected final String TEXT_18 = ".setRecordDelimiter(rowSeparator_";
  protected final String TEXT_19 = ");              \t" + NL + "        csvWriter_";
  protected final String TEXT_20 = ".setEscapeMode(com.csvreader.CsvWriter.ESCAPE_MODE_BACKSLASH);      \t\t" + NL + "      \t//csvWriter_";
  protected final String TEXT_21 = ".setTextQualifier(textEnclosure_";
  protected final String TEXT_22 = "); " + NL + "      \t//csvWriter_";
  protected final String TEXT_23 = ".setForceQualifier(true);" + NL + "      \tcsvWriter_";
  protected final String TEXT_24 = ".setForceQualifier(false);  " + NL + "      \tcsvWriter_";
  protected final String TEXT_25 = ".setUseTextQualifier(false);      ";
  protected final String TEXT_26 = NL;

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
        
        String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
        
        boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
        
        boolean isCreateDir = ("true").equals(ElementParameterParser.getValue(node,"__CREATE__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(rowSeparator.length()==0||("").equals(rowSeparator)?"'\\n'" : rowSeparator + ".charAt(0)");
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(fieldSeparator.length()==0||("").equals(fieldSeparator)?"';'" : fieldSeparator + ".charAt(0)");
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_9);
    if(isCreateDir) {
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    }
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(isAppend );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(encoding );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
    }
}

    stringBuffer.append(TEXT_26);
    return stringBuffer.toString();
  }
}
