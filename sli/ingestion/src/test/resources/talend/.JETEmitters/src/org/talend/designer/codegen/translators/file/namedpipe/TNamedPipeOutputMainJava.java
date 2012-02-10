package org.talend.designer.codegen.translators.file.namedpipe;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.MappingTypeRetriever;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;

public class TNamedPipeOutputMainJava
{
  protected static String nl;
  public static synchronized TNamedPipeOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TNamedPipeOutputMainJava result = new TNamedPipeOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\tStringBuilder sb_";
  protected final String TEXT_4 = " = new StringBuilder(1000);" + NL + "\tString null_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "\t";
  protected final String TEXT_7 = NL + "\t\tString temp_";
  protected final String TEXT_8 = " = null;" + NL + "\t\tString rowSepRepl_";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = " + ";
  protected final String TEXT_11 = ";" + NL + "\t\tString escapeCharRepl_";
  protected final String TEXT_12 = " = ";
  protected final String TEXT_13 = " + ";
  protected final String TEXT_14 = ";" + NL + "\t\tString fieldSeparatorRepl_";
  protected final String TEXT_15 = " = ";
  protected final String TEXT_16 = " + ";
  protected final String TEXT_17 = ";" + NL + "\t\tString quoteCharRepl_";
  protected final String TEXT_18 = " = ";
  protected final String TEXT_19 = " + ";
  protected final String TEXT_20 = ";" + NL + "\t";
  protected final String TEXT_21 = NL + "\t" + NL + "\t";
  protected final String TEXT_22 = NL + "\t\t\tif (";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = " == null)" + NL + "\t\t\t\tsb_";
  protected final String TEXT_25 = ".append(null_";
  protected final String TEXT_26 = ");" + NL + "\t\t\telse {" + NL + "\t\t\t";
  protected final String TEXT_27 = " sb_";
  protected final String TEXT_28 = ".append(";
  protected final String TEXT_29 = "); ";
  protected final String TEXT_30 = " sb_";
  protected final String TEXT_31 = ".append(TalendDate.formatDate(";
  protected final String TEXT_32 = ",";
  protected final String TEXT_33 = ".";
  protected final String TEXT_34 = ")); ";
  protected final String TEXT_35 = " sb_";
  protected final String TEXT_36 = ".append(String.format(\"%-";
  protected final String TEXT_37 = ".";
  protected final String TEXT_38 = "f\",";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = ")); ";
  protected final String TEXT_41 = " sb_";
  protected final String TEXT_42 = ".append(String.format(\"%-";
  protected final String TEXT_43 = "d\",";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = ")); ";
  protected final String TEXT_46 = " sb_";
  protected final String TEXT_47 = ".append(String.valueOf(";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = ")); ";
  protected final String TEXT_50 = " sb_";
  protected final String TEXT_51 = ".append((";
  protected final String TEXT_52 = ".";
  protected final String TEXT_53 = " ? \"1\" : \"0\")); ";
  protected final String TEXT_54 = " sb_";
  protected final String TEXT_55 = ".append((";
  protected final String TEXT_56 = ".";
  protected final String TEXT_57 = " ? \"T\" : \"F\")); ";
  protected final String TEXT_58 = " sb_";
  protected final String TEXT_59 = ".append((";
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = " ? \"Y\" : \"N\")); ";
  protected final String TEXT_62 = " sb_";
  protected final String TEXT_63 = ".append((";
  protected final String TEXT_64 = ".";
  protected final String TEXT_65 = " ? \"TRUE\" : \"FALSE\")); ";
  protected final String TEXT_66 = " sb_";
  protected final String TEXT_67 = ".append((";
  protected final String TEXT_68 = ".";
  protected final String TEXT_69 = " ? \"YES\" : \"NO\")); ";
  protected final String TEXT_70 = " sb_";
  protected final String TEXT_71 = ".append((";
  protected final String TEXT_72 = ".";
  protected final String TEXT_73 = " ? \"1\" : \"0\")); ";
  protected final String TEXT_74 = " sb_";
  protected final String TEXT_75 = ".append(String.valueOf(";
  protected final String TEXT_76 = ".";
  protected final String TEXT_77 = ")); ";
  protected final String TEXT_78 = " " + NL + "\t\t\t\ttemp_";
  protected final String TEXT_79 = " = String.valueOf(";
  protected final String TEXT_80 = ".";
  protected final String TEXT_81 = ");" + NL + "\t\t\t\ttemp_";
  protected final String TEXT_82 = " = temp_";
  protected final String TEXT_83 = ".replace(";
  protected final String TEXT_84 = ",escapeCharRepl_";
  protected final String TEXT_85 = ");" + NL + "\t\t\t\ttemp_";
  protected final String TEXT_86 = " = temp_";
  protected final String TEXT_87 = ".replace(";
  protected final String TEXT_88 = ",quoteCharRepl_";
  protected final String TEXT_89 = ");" + NL + "\t\t\t\ttemp_";
  protected final String TEXT_90 = " = temp_";
  protected final String TEXT_91 = ".replace(";
  protected final String TEXT_92 = ",rowSepRepl_";
  protected final String TEXT_93 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_94 = NL + "\t\t\t\tsb_";
  protected final String TEXT_95 = ".append(";
  protected final String TEXT_96 = " + temp_";
  protected final String TEXT_97 = " + ";
  protected final String TEXT_98 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_99 = NL + "\t\t\t\tsb_";
  protected final String TEXT_100 = ".append(temp_";
  protected final String TEXT_101 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_102 = NL + "\t\t\t\ttemp_";
  protected final String TEXT_103 = " = null;" + NL + "\t\t\t\t";
  protected final String TEXT_104 = " sb_";
  protected final String TEXT_105 = ".append(";
  protected final String TEXT_106 = "); ";
  protected final String TEXT_107 = NL + "\t\t\t} // close if equals to null" + NL + "\t\t\t";
  protected final String TEXT_108 = NL + "\t\t\tsb_";
  protected final String TEXT_109 = ".append(";
  protected final String TEXT_110 = ");" + NL + "\t\t\t";
  protected final String TEXT_111 = NL + "\t\t\tsb_";
  protected final String TEXT_112 = ".append(";
  protected final String TEXT_113 = ");" + NL + "\t\t\t";
  protected final String TEXT_114 = NL + "\toutputStream_";
  protected final String TEXT_115 = ".write(sb_";
  protected final String TEXT_116 = ".toString().getBytes());" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_117 = "_NB_LINE\", ++rowCout_";
  protected final String TEXT_118 = ");" + NL + "\tsb_";
  protected final String TEXT_119 = " = null;" + NL + "\t";
  protected final String TEXT_120 = NL + "\t\t\t";
  protected final String TEXT_121 = ".";
  protected final String TEXT_122 = " = ";
  protected final String TEXT_123 = ".";
  protected final String TEXT_124 = ";" + NL + "\t\t\t";
  protected final String TEXT_125 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

boolean useExistingPipe = ElementParameterParser.getValue(node, "__USE_EXISTING_PIPE__").equals("true");
String existingPipe = ElementParameterParser.getValue(node, "__PIPE__");
String namedPipeName = ElementParameterParser.getValue(node, "__NAMEDPIPE_NAME__");
String rowSeparator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
String fieldSeparator = ElementParameterParser.getValue(node, "__FIELDSEPARATOR__");
boolean csv = ElementParameterParser.getValue(node, "__CSV__").equals("true");
String escapeChar = ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
String quoteChar = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
boolean quoteAllFields = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE_OPTION__").equals("ALL");
boolean deleteIfExists = ElementParameterParser.getValue(node, "__DELETE_IF_EXISTS__").equals("true");
String nullText = ElementParameterParser.getValue(node, "__NULL_TEXT__");
String boolType = ElementParameterParser.getValue(node, "__BOOLEAN_TYPE__");

if (nullText == null || "".equals(nullText)) {
	nullText = "\"\"";
}

List<IMetadataTable> metadatas = node.getMetadataList();
List<IMetadataColumn> columnList = null;
if(metadatas != null && metadatas.size() > 0) {
    IMetadataTable metadata = metadatas.get(0);
    if(metadata != null) {
        columnList = metadata.getListColumns();
    }
}
List< ? extends IConnection> inputConns = node.getIncomingConnections();
List< ? extends IConnection> outputConns = node.getOutgoingConnections();
boolean hasInputRow = false;
boolean hasOutputRow = false;
if (inputConns != null || inputConns.size() > 0) {
	for(IConnection conn : inputConns) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))
			if(!hasInputRow)
				hasInputRow = true;
	}
}
if (outputConns != null || outputConns.size() > 0) {
	for(IConnection conn : outputConns) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))
			if(!hasOutputRow)
				hasOutputRow = true;
	}
}

String inputRowName = (hasInputRow) ? inputConns.get(0).getName() : null;
String outputRowName = (hasOutputRow) ? outputConns.get(0).getName() : null;

    stringBuffer.append(TEXT_2);
    
if (hasInputRow && columnList != null) {
	
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(nullText);
    stringBuffer.append(TEXT_6);
     if (csv) { 
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(escapeChar);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(escapeChar);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(escapeChar);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(escapeChar);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(escapeChar);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(quoteChar);
    stringBuffer.append(TEXT_20);
     } 
    stringBuffer.append(TEXT_21);
    
	for (int i=0; i < columnList.size(); i++) {
		IMetadataColumn column = columnList.get(i);
		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
		boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType(javaType, column.isNullable());
		String datePattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? "\"yyyy-MM-dd\"" : column.getPattern();
		String length = (column.getLength() == null ? "1" : String.valueOf(column.getLength()));
		String precision = (column.getPrecision() == null ? "6" : String.valueOf(column.getPrecision()));
		
		
		if (!isPrimitive) {
			
    stringBuffer.append(TEXT_22);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    
		}

		if (csv && quoteAllFields){
			
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(quoteChar);
    stringBuffer.append(TEXT_29);
    
		}

		if (javaType == JavaTypesManager.DATE) {
			
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(datePattern);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_34);
    
		} else if (javaType == JavaTypesManager.DOUBLE || javaType == JavaTypesManager.FLOAT || javaType == JavaTypesManager.BIGDECIMAL) {
			
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(length);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(precision);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_40);
    
		} else if (javaType == JavaTypesManager.LONG) {
			
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(length);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_45);
    
		} else if (javaType == JavaTypesManager.INTEGER) {
			
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_49);
    
		} else if (javaType == JavaTypesManager.BOOLEAN) {
			if (boolType.equals("1_0")) {
				
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_53);
    
			} else if (boolType.equals("T_F")) {
				
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_57);
    
			} else if (boolType.equals("Y_N")) {
				
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_61);
    
			} else if (boolType.equals("TRUE_FALSE")) {
				
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_65);
    
			} else if (boolType.equals("YES_NO")) {
				
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_69);
    
			} else {
				
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_73);
    
			}
		} else {
			if (csv == false) {
				
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_77);
    
			} else { // csv == true
				
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(escapeChar);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(quoteChar);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
     if (!quoteAllFields) { 
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(quoteChar);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(quoteChar);
    stringBuffer.append(TEXT_98);
     } else { 
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
     } 
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    
			}
		}

		if (csv && quoteAllFields){
			
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(quoteChar);
    stringBuffer.append(TEXT_106);
    
		}

		if (!isPrimitive) {
			
    stringBuffer.append(TEXT_107);
    
		}
			
		if (i == (columnList.size() -1)) {
			
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_110);
    
		} else {
			
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(fieldSeparator);
    stringBuffer.append(TEXT_113);
    
		}
	}

	// ------------------------------------------
	// write the output to the outputstream
	// -------------------------------------------
	
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    
	
	//----------------------------------------------
	// assign output row to input row
	//----------------------------------------------
	if (hasOutputRow) {
		for (IMetadataColumn column : columnList) {
			
    stringBuffer.append(TEXT_120);
    stringBuffer.append(outputRowName);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_122);
    stringBuffer.append(inputRowName);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_124);
    
		}
	}
	
} // end of hasInputRow

    stringBuffer.append(TEXT_125);
    return stringBuffer.toString();
  }
}
