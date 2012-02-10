package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;

public class TRowGeneratorBeginJava
{
  protected static String nl;
  public static synchronized TRowGeneratorBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRowGeneratorBeginJava result = new TRowGeneratorBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "int nb_max_row_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL;
  protected final String TEXT_6 = NL + NL + "class ";
  protected final String TEXT_7 = "Randomizer {";
  protected final String TEXT_8 = NL + "\tpublic ";
  protected final String TEXT_9 = " getRandom";
  protected final String TEXT_10 = "() {" + NL + "\t\t";
  protected final String TEXT_11 = NL + "\t\treturn ";
  protected final String TEXT_12 = ";" + NL + "\t\t";
  protected final String TEXT_13 = NL + "\t\t";
  protected final String TEXT_14 = "[] ";
  protected final String TEXT_15 = "Table = new ";
  protected final String TEXT_16 = "[] { ";
  protected final String TEXT_17 = " };" + NL + "\t\tjava.util.Random random";
  protected final String TEXT_18 = " = new java.util.Random();" + NL + "\t\treturn ";
  protected final String TEXT_19 = "Table[random";
  protected final String TEXT_20 = ".nextInt(";
  protected final String TEXT_21 = "Table.length)];" + NL + "\t\t";
  protected final String TEXT_22 = " " + NL + "\t\treturn ";
  protected final String TEXT_23 = ";" + NL + "\t\t";
  protected final String TEXT_24 = NL + "\t}";
  protected final String TEXT_25 = NL + "}" + NL + "\t";
  protected final String TEXT_26 = "Randomizer rand";
  protected final String TEXT_27 = " = new ";
  protected final String TEXT_28 = "Randomizer();" + NL + "" + NL + "\tfor (int i";
  protected final String TEXT_29 = "=0; i";
  protected final String TEXT_30 = "<nb_max_row_";
  protected final String TEXT_31 = " ;i";
  protected final String TEXT_32 = "++) {";
  protected final String TEXT_33 = NL + "\t\t";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = " = rand";
  protected final String TEXT_36 = ".getRandom";
  protected final String TEXT_37 = "();";
  protected final String TEXT_38 = NL + "\t\tnb_line_";
  protected final String TEXT_39 = "++;";
  protected final String TEXT_40 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	List<Map<String, String>> tableValues = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__VALUES__");


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append((("").equals(ElementParameterParser.getValue(node, "__NB_ROWS__"))? 100: ElementParameterParser.getValue(node, "__NB_ROWS__")));
    stringBuffer.append(TEXT_5);
    
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
		List<IMetadataColumn> listColumns = metadata.getListColumns(); 
		for (int i=0; i<tableValues.size(); i++) {
			Map<String, String> lineValue = tableValues.get(i);
			//lineValue.get("ARRAY")

    stringBuffer.append(TEXT_8);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(listColumns.get(i).getTalendType(), listColumns.get(i).isNullable()) );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(listColumns.get(i).getLabel() );
    stringBuffer.append(TEXT_10);
    
			/* if column parameter looks like abcd(efgh,...) )  */
			if (lineValue.get("ARRAY").indexOf("(") >0) {
		
    stringBuffer.append(TEXT_11);
    stringBuffer.append(lineValue.get("ARRAY") );
    stringBuffer.append(TEXT_12);
     
			/* else if parameter is  separated by , */
			} else if (lineValue.get("ARRAY").indexOf(",") >0) {
		
    stringBuffer.append(TEXT_13);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(listColumns.get(i).getTalendType(), listColumns.get(i).isNullable()) );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(listColumns.get(i).getLabel() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(listColumns.get(i).getTalendType(), listColumns.get(i).isNullable()) );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(lineValue.get("ARRAY") );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(listColumns.get(i).getLabel() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(listColumns.get(i).getLabel() );
    stringBuffer.append(TEXT_21);
     } else { 
    stringBuffer.append(TEXT_22);
    stringBuffer.append(lineValue.get("ARRAY") );
    stringBuffer.append(TEXT_23);
     } 
    stringBuffer.append(TEXT_24);
    
		}

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    
		List< ? extends IConnection> conns = node.getOutgoingConnections();
		for (int i=0;i<conns.size();i++) {
			IConnection conn = conns.get(i);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA) 
			   && (!conn.getLineStyle().hasConnectionCategory(IConnectionCategory.USE_HASH))) {
				for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_33);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_37);
    
				}
			}
		}

    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    
	}

    stringBuffer.append(TEXT_40);
    return stringBuffer.toString();
  }
}
