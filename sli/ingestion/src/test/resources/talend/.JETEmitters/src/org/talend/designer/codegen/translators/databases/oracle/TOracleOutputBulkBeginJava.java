package org.talend.designer.codegen.translators.databases.oracle;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TOracleOutputBulkBeginJava
{
  protected static String nl;
  public static synchronized TOracleOutputBulkBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOracleOutputBulkBeginJava result = new TOracleOutputBulkBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tString fileName_";
  protected final String TEXT_3 = " = (new java.io.File(";
  protected final String TEXT_4 = ")).getAbsolutePath().replace(\"\\\\\",\"/\");";
  protected final String TEXT_5 = NL + "\t\tString directory_";
  protected final String TEXT_6 = " = null;" + NL + "\t\tif((fileName_";
  protected final String TEXT_7 = ".indexOf(\"/\") != -1)) {     " + NL + "\t\t    directory_";
  protected final String TEXT_8 = " = fileName_";
  protected final String TEXT_9 = ".substring(0, fileName_";
  protected final String TEXT_10 = ".lastIndexOf(\"/\"));            " + NL + "\t\t} else {" + NL + "\t\t    directory_";
  protected final String TEXT_11 = " = \"\";" + NL + "\t\t}" + NL + "\t\t//create directory only if not exists" + NL + "\t\tif(directory_";
  protected final String TEXT_12 = " != null && directory_";
  protected final String TEXT_13 = ".trim().length() != 0) {" + NL + " \t\t\tjava.io.File dir_";
  protected final String TEXT_14 = " = new java.io.File(directory_";
  protected final String TEXT_15 = ");" + NL + "\t\t\tif(!dir_";
  protected final String TEXT_16 = ".exists()) {" + NL + "    \t\t\tdir_";
  protected final String TEXT_17 = ".mkdirs();" + NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_18 = NL + "\t\tint nb_line_";
  protected final String TEXT_19 = " = 0;" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ";" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_ROWSEP_";
  protected final String TEXT_22 = " = ";
  protected final String TEXT_23 = ";" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_ROWSEP_WITH_LOB_";
  protected final String TEXT_24 = " = \"|\";";
  protected final String TEXT_25 = "\t\t\t\t" + NL + "\t\tfinal String OUT_FIELDS_ENCLOSURE_LEFT_";
  protected final String TEXT_26 = " = ";
  protected final String TEXT_27 = ";" + NL + "\t\t" + NL + "\t\tfinal String OUT_FIELDS_ENCLOSURE_RIGHT_";
  protected final String TEXT_28 = " = ";
  protected final String TEXT_29 = ";";
  protected final String TEXT_30 = "\t\t" + NL + "\t\tfinal java.io.BufferedWriter out";
  protected final String TEXT_31 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        \t\tnew java.io.FileOutputStream(fileName_";
  protected final String TEXT_32 = ", ";
  protected final String TEXT_33 = "),";
  protected final String TEXT_34 = "),";
  protected final String TEXT_35 = ");";
  protected final String TEXT_36 = NL;

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
        String filename = ElementParameterParser.getValueWithUIFieldKey(
            node,
            "__FILENAME__",
            "FILENAME"
        );
        
        String fieldSeparator = ElementParameterParser.getValueWithUIFieldKey(
            node,
            "__FIELDSEPARATOR__",
            "FIELDSEPARATOR"
        );
        
        String rowSeparator = ElementParameterParser.getValueWithUIFieldKey(
            node,
            "__ROWSEPARATOR__",
            "ROWSEPARATOR"
        );

        String encoding = ElementParameterParser.getValue(
            node,
            "__ENCODING__"
        );
        
        boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
        
        boolean useFieldsEnclosure = ("true").equals(ElementParameterParser.getValue(node,"__USE_FIELDS_ENCLOSURE__"));
        String fieldsEnclosureLift = ElementParameterParser.getValue(node,"__FIELDS_ENCLOSURE_LEFT__");
        String fieldsEnclosureRight = ElementParameterParser.getValue(node,"__FIELDS_ENCLOSURE_RIGHT__");
        String bufferSize = ElementParameterParser.getValue(node,"__BUFFER_SIZE__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_4);
    
	if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){

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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
    }

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    
	if (useFieldsEnclosure) {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(fieldsEnclosureLift);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(fieldsEnclosureRight);
    stringBuffer.append(TEXT_29);
    
	}

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append( isAppend);
    stringBuffer.append(TEXT_33);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(bufferSize);
    stringBuffer.append(TEXT_35);
    
    }
}

    stringBuffer.append(TEXT_36);
    return stringBuffer.toString();
  }
}
