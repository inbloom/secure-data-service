package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TTeradataTPTOutputBeginJava
{
  protected static String nl;
  public static synchronized TTeradataTPTOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataTPTOutputBeginJava result = new TTeradataTPTOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "\t\t" + NL + "\t\tfinal String OUT_DELIM_ROWSEP_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + "\t\t" + NL + "\t\tfinal java.io.BufferedWriter out";
  protected final String TEXT_8 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        \t\tnew java.io.FileOutputStream(";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = "),";
  protected final String TEXT_11 = "));";
  protected final String TEXT_12 = NL + "    \t\t\tout";
  protected final String TEXT_13 = ".write(\"";
  protected final String TEXT_14 = "\");" + NL + "    \t\t";
  protected final String TEXT_15 = "out";
  protected final String TEXT_16 = ".write(OUT_DELIM_";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL + "    \t\tout";
  protected final String TEXT_19 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL;

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
        
        boolean isIncludeHeader = ("true").equals(ElementParameterParser.getValue(node,"__INCLUDEHEADER__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(rowSeparator );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( isAppend);
    stringBuffer.append(TEXT_10);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_11);
    
		if(isIncludeHeader&&!isAppend){
    		List<IMetadataColumn> columns = metadata.getListColumns();
    		int sizeColumns = columns.size();
    		for (int i = 0; i < sizeColumns; i++) {
    			IMetadataColumn column = columns.get(i);
    		
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    
    			if(i != sizeColumns - 1) {
    				
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    
    			}
    		}
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    
		}
    }
}

    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
