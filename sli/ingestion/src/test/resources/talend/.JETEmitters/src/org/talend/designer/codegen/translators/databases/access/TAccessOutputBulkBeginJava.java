package org.talend.designer.codegen.translators.databases.access;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TAccessOutputBulkBeginJava
{
  protected static String nl;
  public static synchronized TAccessOutputBulkBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAccessOutputBulkBeginJava result = new TAccessOutputBulkBeginJava();
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
  protected final String TEXT_18 = NL + NL + "\t\tint nb_line_";
  protected final String TEXT_19 = " = 0;" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_";
  protected final String TEXT_20 = " = \",\";" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_ROWSEP_";
  protected final String TEXT_21 = " = \"\\n\";" + NL + "\t\t";
  protected final String TEXT_22 = "\t\t\t\t" + NL + "\t\tfinal String OUT_FIELDS_ENCLOSURE_";
  protected final String TEXT_23 = " = ";
  protected final String TEXT_24 = ";" + NL + "\t\t";
  protected final String TEXT_25 = "\t\t" + NL + "\t\t" + NL + "\t\tfinal java.io.BufferedWriter out";
  protected final String TEXT_26 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        \t\tnew java.io.FileOutputStream(fileName_";
  protected final String TEXT_27 = ", ";
  protected final String TEXT_28 = "),";
  protected final String TEXT_29 = "));";
  protected final String TEXT_30 = NL + "    \t\t\tout";
  protected final String TEXT_31 = ".write(\"";
  protected final String TEXT_32 = "\");" + NL + "    \t\t";
  protected final String TEXT_33 = "out";
  protected final String TEXT_34 = ".write(OUT_DELIM_";
  protected final String TEXT_35 = ");";
  protected final String TEXT_36 = NL + "    \t\tout";
  protected final String TEXT_37 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_38 = ");" + NL + "    \t\tout";
  protected final String TEXT_39 = ".flush();";
  protected final String TEXT_40 = NL;

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
        
        String encoding = ElementParameterParser.getValue(
            node,
            "__ENCODING__"
        );
        
        boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
        
        boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));

        boolean isUseTextEnclosure = ("true").equals(ElementParameterParser.getValue(node,"__USE_FIELDS_ENCLOSURE__"));
		String textEnclosure = ElementParameterParser.getValue(node,"__FIELDS_ENCLOSURE__");

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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    
	if (isUseTextEnclosure) {

    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(textEnclosure);
    stringBuffer.append(TEXT_24);
    
	}

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append( isAppend);
    stringBuffer.append(TEXT_28);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_29);
    
		if(isIncludeHeader&&!isAppend){
    		List<IMetadataColumn> columns = metadata.getListColumns();
    		int sizeColumns = columns.size();
    		for (int i = 0; i < sizeColumns; i++) {
    			IMetadataColumn column = columns.get(i);
    		
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_32);
    
    			if(i != sizeColumns - 1) {
    				
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    
    			}
    		}
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    
		}
    }
}

    stringBuffer.append(TEXT_40);
    return stringBuffer.toString();
  }
}
