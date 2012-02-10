package org.talend.designer.codegen.translators.data_quality;

import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import java.util.List;
import java.util.Map;

public class TReplaceListMainJava
{
  protected static String nl;
  public static synchronized TReplaceListMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TReplaceListMainJava result = new TReplaceListMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\tString inputFlow_";
  protected final String TEXT_3 = "_";
  protected final String TEXT_4 = " = String.valueOf(";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ");" + NL + "" + NL + "            for(Object o:replace_";
  protected final String TEXT_7 = ".keySet()){" + NL + "            \tif(o.getClass().getSimpleName() .equals(\"";
  protected final String TEXT_8 = "\")){" + NL + "\t                String search";
  protected final String TEXT_9 = " = String.valueOf(o);" + NL + "            \t\tif(replace_";
  protected final String TEXT_10 = ".get(o).getClass().getSimpleName() .equals(\"";
  protected final String TEXT_11 = "\")){" + NL + "\t\t                String replace";
  protected final String TEXT_12 = " = String.valueOf(replace_";
  protected final String TEXT_13 = ".get(o));" + NL + "\t\t                inputFlow_";
  protected final String TEXT_14 = "_";
  protected final String TEXT_15 = " = inputFlow_";
  protected final String TEXT_16 = "_";
  protected final String TEXT_17 = ".replaceAll(";
  protected final String TEXT_18 = "\"(?i)\" + ";
  protected final String TEXT_19 = "search";
  protected final String TEXT_20 = ",replace";
  protected final String TEXT_21 = ");" + NL + "            \t\t}" + NL + "            \t}" + NL + "            }";
  protected final String TEXT_22 = NL + "                \t";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = " = new java.util.Date(inputFlow_";
  protected final String TEXT_25 = "_";
  protected final String TEXT_26 = ");";
  protected final String TEXT_27 = NL + "                \t";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = " = new ";
  protected final String TEXT_30 = "(inputFlow_";
  protected final String TEXT_31 = "_";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = NL + "                \t";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = " = new ";
  protected final String TEXT_36 = "(inputFlow_";
  protected final String TEXT_37 = "_";
  protected final String TEXT_38 = ".toCharArray()[0]);";
  protected final String TEXT_39 = NL + "                \t";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = " = ";
  protected final String TEXT_42 = ".valueOf(inputFlow_";
  protected final String TEXT_43 = "_";
  protected final String TEXT_44 = ");";
  protected final String TEXT_45 = NL;
  protected final String TEXT_46 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<Map<String, String>> columnOptions =
    (List<Map<String,String>>)ElementParameterParser.getObjectValue(
        node,
        "__COLUMN_OPTIONS__"
    );


List<IMetadataTable> metadatas = node.getMetadataList();
if (metadatas != null && metadatas.size() > 0) {
    List<IConnection> inputConnections;
    String main = null;
    inputConnections = (List<IConnection>) node.getIncomingConnections();
    for (IConnection connection : inputConnections) {
        if (connection == null) {
            continue;
        }

        if (connection.getLineStyle() == EConnectionType.FLOW_MAIN) {
            main = connection.getName();
            continue;
        }
    }

    IMetadataTable metadata = metadatas.get(0);

    int column_num = 0;
    for (Map<String, String> columnOption: columnOptions) {
        if (("true").equals(columnOption.get("REPLACE"))) {
        	IMetadataColumn column = metadata.getListColumns().get(column_num);
        	String testType = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
        	// Don't know why javaType.isGenerateWithCanonicalName() returns "true" when it's java.util.Date, so need to handle the name
        	if("java.util.Date".equals(testType)){
        		testType = "Date";
        	}
            String columnName = metadata.getListColumns().get(column_num).getLabel();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(main);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(testType);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(testType);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(columnName);
    stringBuffer.append(TEXT_17);
    if(("true").equals(columnOption.get("CASE_INSENSITIVE"))) {
    stringBuffer.append(TEXT_18);
    }
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
        }
        column_num++;
    }
    
    //output
        for(Map<String,String> columnOption: columnOptions){
            if (("true").equals(columnOption.get("REPLACE"))){
            	for (IMetadataColumn column : metadata.getListColumns()) { 
	            	if(column.getLabel().equals(columnOption.get("SCHEMA_COLUMN"))) {
	        			String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
	                	String columnname = column.getLabel();
	               		if("java.util.Date".equals(typeToGenerate)){

    stringBuffer.append(TEXT_22);
    stringBuffer.append(main);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_26);
    
						}else if("BigDecimal".equals(typeToGenerate)){

    stringBuffer.append(TEXT_27);
    stringBuffer.append(main);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_32);
    
						}else if("Character".equals(typeToGenerate)){

    stringBuffer.append(TEXT_33);
    stringBuffer.append(main);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_38);
    
						}else{

    stringBuffer.append(TEXT_39);
    stringBuffer.append(main);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(columnname);
    stringBuffer.append(TEXT_44);
    
						}
						break;
					} // if
				} // for
            }
       }
    
    

    stringBuffer.append(TEXT_45);
    
}

    stringBuffer.append(TEXT_46);
    return stringBuffer.toString();
  }
}
