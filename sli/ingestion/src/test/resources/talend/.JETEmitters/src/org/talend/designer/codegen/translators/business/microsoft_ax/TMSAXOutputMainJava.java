package org.talend.designer.codegen.translators.business.microsoft_ax;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class TMSAXOutputMainJava
{
  protected static String nl;
  public static synchronized TMSAXOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSAXOutputMainJava result = new TMSAXOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "        ";
  protected final String TEXT_2 = " = null;            ";
  protected final String TEXT_3 = NL + "\trecord_";
  protected final String TEXT_4 = ".callMethod(\"ExecuteStmt\", new Object[] { ";
  protected final String TEXT_5 = "});";
  protected final String TEXT_6 = NL + "\trecord_";
  protected final String TEXT_7 = ".callMethod(\"InitValue\");";
  protected final String TEXT_8 = NL + "    whetherReject_";
  protected final String TEXT_9 = " = false;";
  protected final String TEXT_10 = NL + "\trecord_";
  protected final String TEXT_11 = ".put(\"field\",new Object[]{";
  protected final String TEXT_12 = ",";
  protected final String TEXT_13 = "});";
  protected final String TEXT_14 = NL + "    try{" + NL + "        record_";
  protected final String TEXT_15 = ".callMethod(\"Insert\");" + NL + "        insertedCount_";
  protected final String TEXT_16 = "++;" + NL + "        nb_line_";
  protected final String TEXT_17 = "++;" + NL + "    }catch(Exception e){" + NL + "        whetherReject_";
  protected final String TEXT_18 = " = true;";
  protected final String TEXT_19 = NL + "            throw(e);";
  protected final String TEXT_20 = NL + "            ";
  protected final String TEXT_21 = " = new ";
  protected final String TEXT_22 = "Struct();";
  protected final String TEXT_23 = NL + "            ";
  protected final String TEXT_24 = ".";
  protected final String TEXT_25 = " = ";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = ";";
  protected final String TEXT_28 = NL + "            ";
  protected final String TEXT_29 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_30 = ";";
  protected final String TEXT_31 = NL + "            System.err.print(e.getMessage());";
  protected final String TEXT_32 = NL + "    }";
  protected final String TEXT_33 = NL + "        try{" + NL + "            while(record_";
  protected final String TEXT_34 = ".get(\"Found\").getObjectAsBoolean()){";
  protected final String TEXT_35 = NL + "\t\t\t\trecord_";
  protected final String TEXT_36 = ".put(\"field\",new Object[]{";
  protected final String TEXT_37 = ",";
  protected final String TEXT_38 = "});";
  protected final String TEXT_39 = "\t" + NL + "\t\t\t\trecord_";
  protected final String TEXT_40 = ".callMethod(\"Update\");" + NL + "                record_";
  protected final String TEXT_41 = ".callMethod(\"Next\");" + NL + "                updatedCount_";
  protected final String TEXT_42 = "++;" + NL + "            }" + NL + "            nb_line_";
  protected final String TEXT_43 = "++;" + NL + "        }catch(Exception e){" + NL + "            whetherReject_";
  protected final String TEXT_44 = " = true;";
  protected final String TEXT_45 = NL + "                throw(e);";
  protected final String TEXT_46 = NL + "                    ";
  protected final String TEXT_47 = " = new ";
  protected final String TEXT_48 = "Struct();";
  protected final String TEXT_49 = NL + "                    ";
  protected final String TEXT_50 = ".";
  protected final String TEXT_51 = " = ";
  protected final String TEXT_52 = ".";
  protected final String TEXT_53 = ";";
  protected final String TEXT_54 = NL + "                    ";
  protected final String TEXT_55 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_56 = ";";
  protected final String TEXT_57 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_58 = NL + "        }";
  protected final String TEXT_59 = NL + "\t\tif(record_";
  protected final String TEXT_60 = ".get(\"Found\").getObjectAsBoolean()){" + NL + "            try{" + NL + "\t\t\t\twhile(record_";
  protected final String TEXT_61 = ".get(\"Found\").getObjectAsBoolean()){";
  protected final String TEXT_62 = NL + "\t\t\t\t\trecord_";
  protected final String TEXT_63 = ".put(\"field\",new Object[]{";
  protected final String TEXT_64 = ",";
  protected final String TEXT_65 = "});";
  protected final String TEXT_66 = "\t" + NL + "\t\t\t\t\trecord_";
  protected final String TEXT_67 = ".callMethod(\"Update\");" + NL + "                \trecord_";
  protected final String TEXT_68 = ".callMethod(\"Next\");" + NL + "                \tupdatedCount_";
  protected final String TEXT_69 = "++;" + NL + "            \t}" + NL + "            }catch(Exception e){" + NL + "                whetherReject_";
  protected final String TEXT_70 = " = true;";
  protected final String TEXT_71 = NL + "                    throw(e);";
  protected final String TEXT_72 = NL + "                    ";
  protected final String TEXT_73 = " = new ";
  protected final String TEXT_74 = "Struct();";
  protected final String TEXT_75 = NL + "                            ";
  protected final String TEXT_76 = ".";
  protected final String TEXT_77 = " = ";
  protected final String TEXT_78 = ".";
  protected final String TEXT_79 = ";";
  protected final String TEXT_80 = NL + "                        ";
  protected final String TEXT_81 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_82 = ";";
  protected final String TEXT_83 = NL + "                        System.err.print(e.getMessage());";
  protected final String TEXT_84 = NL + "            }" + NL + "\t\t}else{ //insert" + NL + "            try{";
  protected final String TEXT_85 = NL + "\t\t\t\trecord_";
  protected final String TEXT_86 = ".put(\"field\",new Object[]{";
  protected final String TEXT_87 = ",";
  protected final String TEXT_88 = "});";
  protected final String TEXT_89 = NL + "                record_";
  protected final String TEXT_90 = ".callMethod(\"Insert\");" + NL + "        \t\tinsertedCount_";
  protected final String TEXT_91 = "++;" + NL + "            }catch(Exception e)" + NL + "            {" + NL + "                whetherReject_";
  protected final String TEXT_92 = " = true;";
  protected final String TEXT_93 = NL + "                    throw(e);";
  protected final String TEXT_94 = NL + "                    ";
  protected final String TEXT_95 = " = new ";
  protected final String TEXT_96 = "Struct();";
  protected final String TEXT_97 = NL + "                    ";
  protected final String TEXT_98 = ".";
  protected final String TEXT_99 = " = ";
  protected final String TEXT_100 = ".";
  protected final String TEXT_101 = ";";
  protected final String TEXT_102 = NL + "                    ";
  protected final String TEXT_103 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_104 = ";";
  protected final String TEXT_105 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_106 = NL + "            }" + NL + "\t\t}" + NL + "\t\tnb_line_";
  protected final String TEXT_107 = "++;";
  protected final String TEXT_108 = NL + "\t\tint updateFlag_";
  protected final String TEXT_109 = "=0;" + NL + "        try{" + NL + "       \t\twhile(record_";
  protected final String TEXT_110 = ".get(\"Found\").getObjectAsBoolean()){";
  protected final String TEXT_111 = NL + "\t\t\t\trecord_";
  protected final String TEXT_112 = ".put(\"field\",new Object[]{";
  protected final String TEXT_113 = ",";
  protected final String TEXT_114 = "});";
  protected final String TEXT_115 = "\t" + NL + "\t\t\t\trecord_";
  protected final String TEXT_116 = ".callMethod(\"Update\");" + NL + "                record_";
  protected final String TEXT_117 = ".callMethod(\"Next\");" + NL + "                updateFlag_";
  protected final String TEXT_118 = "++;" + NL + "                updatedCount_";
  protected final String TEXT_119 = "++;" + NL + "            }" + NL + "            nb_line_";
  protected final String TEXT_120 = "++;" + NL + "        }catch(Exception e)" + NL + "        {" + NL + "            whetherReject_";
  protected final String TEXT_121 = " = true;";
  protected final String TEXT_122 = NL + "                throw(e);";
  protected final String TEXT_123 = NL + "                ";
  protected final String TEXT_124 = " = new ";
  protected final String TEXT_125 = "Struct();";
  protected final String TEXT_126 = NL + "                ";
  protected final String TEXT_127 = ".";
  protected final String TEXT_128 = " = ";
  protected final String TEXT_129 = ".";
  protected final String TEXT_130 = ";";
  protected final String TEXT_131 = NL + "                ";
  protected final String TEXT_132 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_133 = ";";
  protected final String TEXT_134 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_135 = NL + "        }" + NL + "        if(updateFlag_";
  protected final String TEXT_136 = " == 0) {" + NL + "            try{";
  protected final String TEXT_137 = NL + "\t\t\t\trecord_";
  protected final String TEXT_138 = ".put(\"field\",new Object[]{";
  protected final String TEXT_139 = ",";
  protected final String TEXT_140 = "});";
  protected final String TEXT_141 = NL + "                record_";
  protected final String TEXT_142 = ".callMethod(\"Insert\");" + NL + "        \t\tinsertedCount_";
  protected final String TEXT_143 = "++;" + NL + "            }catch(Exception e){" + NL + "                whetherReject_";
  protected final String TEXT_144 = " = true;";
  protected final String TEXT_145 = NL + "                    throw(e);";
  protected final String TEXT_146 = NL + "                    ";
  protected final String TEXT_147 = ".";
  protected final String TEXT_148 = " = ";
  protected final String TEXT_149 = ".";
  protected final String TEXT_150 = ";";
  protected final String TEXT_151 = NL + "                    ";
  protected final String TEXT_152 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_153 = ";";
  protected final String TEXT_154 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_155 = NL + "            }" + NL + "        }" + NL + "        nb_line_";
  protected final String TEXT_156 = "++;";
  protected final String TEXT_157 = NL + "    try{" + NL + "\t    while(record_";
  protected final String TEXT_158 = ".get(\"Found\").getObjectAsBoolean()){" + NL + "\t\t\trecord_";
  protected final String TEXT_159 = ".callMethod(\"Delete\");" + NL + "            record_";
  protected final String TEXT_160 = ".callMethod(\"Next\");" + NL + "            updatedCount_";
  protected final String TEXT_161 = "++;" + NL + "        }" + NL + "    }catch(Exception e){" + NL + "        whetherReject_";
  protected final String TEXT_162 = " = true;";
  protected final String TEXT_163 = NL + "            throw(e);";
  protected final String TEXT_164 = NL + "            ";
  protected final String TEXT_165 = " = new ";
  protected final String TEXT_166 = "Struct();";
  protected final String TEXT_167 = NL + "            ";
  protected final String TEXT_168 = ".";
  protected final String TEXT_169 = " = ";
  protected final String TEXT_170 = ".";
  protected final String TEXT_171 = ";";
  protected final String TEXT_172 = NL + "            ";
  protected final String TEXT_173 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_174 = ";";
  protected final String TEXT_175 = NL + "                System.err.print(e.getMessage());";
  protected final String TEXT_176 = NL + "    }" + NL + "    nb_line_";
  protected final String TEXT_177 = "++;";
  protected final String TEXT_178 = NL + "        if(!whetherReject_";
  protected final String TEXT_179 = ") {";
  protected final String TEXT_180 = NL + "        \t";
  protected final String TEXT_181 = " = new ";
  protected final String TEXT_182 = "Struct();";
  protected final String TEXT_183 = NL + "       \t\t";
  protected final String TEXT_184 = ".";
  protected final String TEXT_185 = " = ";
  protected final String TEXT_186 = ".";
  protected final String TEXT_187 = ";";
  protected final String TEXT_188 = NL + "        }";
  protected final String TEXT_189 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
    String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
	String axTable = ElementParameterParser.getValue(node,"__TABLE__");
	
	List<Map<String, String>> addCols =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__ADD_COLS__");

	String incomingConnName = null;
	List<IMetadataColumn> columnList = null;
	
	List< ? extends IConnection> conns = node.getIncomingConnections();
	if(conns!=null && conns.size()>0){
		IConnection conn = conns.get(0);
		incomingConnName = conn.getName();
	}
	
	List<IMetadataTable> metadatas = node.getMetadataList();
	if(metadatas != null && metadatas.size()>0){
		IMetadataTable metadata = metadatas.get(0);
		if(metadata != null){
			columnList = metadata.getListColumns();
		}
	}
	
	String rejectConnName = null;
	List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
	if(rejectConns != null && rejectConns.size() > 0) {
	    IConnection rejectConn = rejectConns.get(0);
	    rejectConnName = rejectConn.getName();
	}
	List<IMetadataColumn> rejectColumnList = null;
	IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
	if(metadataTable != null) {
	    rejectColumnList = metadataTable.getListColumns();
	}
	
	List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
    for(IConnection conn : outgoingConns) {
        if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_2);
          }
    }

class Column{
	IMetadataColumn column;
	String name;
	String dataType;
	String operator;
	String DBname;
	String value;
	boolean addCol;
	List<Column> replacement = new ArrayList<Column>();
	
	public Column(String colName,String value,boolean addCol, String dataType){
		this.column = null;
		this.name=colName;
		this.DBname = colName;
		this.value = value;
		this.addCol =addCol;
		this.dataType = dataType;
	}
	
	public Column(IMetadataColumn column, String rowName){
		this.column = column;
		this.name = column.getLabel();
		this.value =rowName+"."+name;
		this.addCol =false;
		this.DBname=column.getOriginalDbColumnName();
		this.dataType=JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
	}

	public boolean isReplaced(){
		return replacement.size()>0;
	}
	
	public void replace(Column column){
		this.replacement.add(column);
	}
	
	public List<Column> getReplacement(){
		return this.replacement;
	}
}

class VariantTool{
	public String vStr(String value){
		return "new org.jinterop.dcom.core.JIVariant(new org.jinterop.dcom.core.JIString("+value+"))";
	}

	public String vCulomnVale(Column column,String rowName){
		if(("String").equals(column.dataType)){
			return vStr(column.value);
		}else if(("Int").equals(column.dataType) || ("Float").equals(column.dataType) || ("Double").equals(column.dataType)
			|| ("Short").equals(column.dataType) || ("Char").equals(column.dataType) || ("Object").equals(column.dataType)
			|| ("Date").equals(column.dataType) || ("Boolean").equals(column.dataType)){
			return "new org.jinterop.dcom.core.JIVariant("+column.value+")";
		}
		return "new org.jinterop.dcom.core.JIVariant.EMPTY";
	}
}
VariantTool vTool = new VariantTool();

List<Column> stmtStructure =  new LinkedList<Column>();
for(IMetadataColumn column:columnList){
    Column myColumn = new Column(column,incomingConnName);
    myColumn.operator = "==";
	stmtStructure.add(myColumn);
}

for(IMetadataColumn column:columnList){
	if(addCols != null && addCols.size()>0){
		for(Map<String, String> addCol:addCols){
			if(addCol.get("REFCOL").equals(column.getLabel())){
				int stmtIndex = 0;
				for(Column stmtStr:stmtStructure){					
					if(stmtStr.name.equals(addCol.get("REFCOL"))){
						break;
					}
					stmtIndex++;
				}
				
				if(("AFTER").equals(addCol.get("POS"))){
					Column insertAfter = new Column(addCol.get("NAME").replaceAll("\"",""),addCol.get("SQL"),true,addCol.get("DATATYPE"));
					insertAfter.dataType = addCol.get("DATATYPE");
					insertAfter.operator = addCol.get("OPERATOR");
					stmtStructure.add(stmtIndex+1,insertAfter);
				}else if(("BEFORE").equals(addCol.get("POS"))){
					Column insertBefore = new Column(addCol.get("NAME").replaceAll("\"",""),addCol.get("SQL"),true,addCol.get("DATATYPE"));
					insertBefore.dataType = addCol.get("DATATYPE");
					insertBefore.operator = addCol.get("OPERATOR");
					stmtStructure.add(stmtIndex,insertBefore);
				}else if(("REPLACE").equals(addCol.get("POS"))){
					Column replacementCol = new Column(addCol.get("NAME").replaceAll("\"",""),addCol.get("SQL"),true,addCol.get("DATATYPE"));
					replacementCol.dataType = addCol.get("DATATYPE");
					replacementCol.operator = addCol.get("OPERATOR");
					Column replacedCol = (Column) stmtStructure.get(stmtIndex);
					replacedCol.replace(replacementCol);
				}
			}
		}
	}
}

List<Column> insertValueList = new LinkedList<Column>();
List<Column> updateValueList = new LinkedList<Column>();
StringBuilder whereStmt = new StringBuilder();
for(Column column : stmtStructure) {
    if(column.isReplaced()) {
        List<Column> replacedColumns = column.getReplacement();          
        for(Column replacedColumn : replacedColumns) {
			insertValueList.add(replacedColumn);
            if(column.column.isKey()) {
            	if(whereStmt.length()>0){
            		whereStmt.append(" && ");
            		whereStmt.append("%1." + replacedColumn.name + " " + replacedColumn.operator + " \\\"\"+" + replacedColumn.value + "+\"\\\"" );
            	}else{
            		whereStmt.append(" %1." + replacedColumn.name + " " + replacedColumn.operator + " \\\"\"+" + replacedColumn.value+ "+\"\\\"");
            	}
            } else {
                updateValueList.add(replacedColumn);
            }
        }
    } else {
        if(column.addCol) {
            insertValueList.add(column);
			updateValueList.add(column);
        } else {
            insertValueList.add(column);
            if(column.column.isKey()) {
            	if(whereStmt.length()>0){
            		whereStmt.append(" && ");
            		whereStmt.append("%1." + column.name + " " + column.operator + " \\\"\"+" + column.value+"+\"\\\"");
            	}else{
            		whereStmt.append(" %1." + column.name + " " + column.operator + " \\\"\"+" + column.value+"+\"\\\"");
            	}
            } else {
                updateValueList.add(column);
            }
        }
    }
}

//select recode
if(!("INSERT").equals(dataAction)){
	String executeStmt = "\"select forupdate %1 where"+whereStmt.toString()+"\"";

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(vTool.vStr(executeStmt));
    stringBuffer.append(TEXT_5);
    
}else{

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
}



if(incomingConnName != null && columnList != null){

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
    if(("INSERT").equals(dataAction)){
		for(Column column : insertValueList){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(vTool.vStr("\""+column.name+"\""));
    stringBuffer.append(TEXT_12);
    stringBuffer.append(vTool.vCulomnVale(column,incomingConnName));
    stringBuffer.append(TEXT_13);
    
		}

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    
        if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_19);
    
        } else {
            if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_20);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_22);
    
                for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_23);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_25);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_27);
    
                }

    stringBuffer.append(TEXT_28);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_30);
    
            } else {

    stringBuffer.append(TEXT_31);
    
            }
        } 

    stringBuffer.append(TEXT_32);
    
	}else if(("UPDATE").equals(dataAction)){

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    
		for(Column column : updateValueList){

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(vTool.vStr("\""+column.name+"\""));
    stringBuffer.append(TEXT_37);
    stringBuffer.append(vTool.vCulomnVale(column,incomingConnName));
    stringBuffer.append(TEXT_38);
    
		}

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    
            if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_45);
    
            } else {
                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_46);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_48);
    
                    for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_49);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_51);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_53);
    
                    }

    stringBuffer.append(TEXT_54);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_56);
    
                } else {

    stringBuffer.append(TEXT_57);
    
                }
            } 

    stringBuffer.append(TEXT_58);
    
	}else if (("INSERT_OR_UPDATE").equals(dataAction)){

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    
		for(Column column : updateValueList){

    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(vTool.vStr("\""+column.name+"\""));
    stringBuffer.append(TEXT_64);
    stringBuffer.append(vTool.vCulomnVale(column,incomingConnName));
    stringBuffer.append(TEXT_65);
    
		}

    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    
                if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_71);
    
                } else {
                    if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_72);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_74);
    
                        for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_75);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_77);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_79);
    
                        }

    stringBuffer.append(TEXT_80);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_82);
    
                    } else {

    stringBuffer.append(TEXT_83);
    
                    }
                }

    stringBuffer.append(TEXT_84);
    
			for(Column column : insertValueList){

    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(vTool.vStr("\""+column.name+"\""));
    stringBuffer.append(TEXT_87);
    stringBuffer.append(vTool.vCulomnVale(column,incomingConnName));
    stringBuffer.append(TEXT_88);
    
			}

    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    
                if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_93);
    
                } else {
                    if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_94);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_96);
    
                        for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_97);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_99);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_101);
    
                        }

    stringBuffer.append(TEXT_102);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_104);
    
                    } else {

    stringBuffer.append(TEXT_105);
    
                    }
                }

    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    
	}else if (("UPDATE_OR_INSERT").equals(dataAction)){

    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    
		for(Column column : updateValueList){

    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(vTool.vStr("\""+column.name+"\""));
    stringBuffer.append(TEXT_113);
    stringBuffer.append(vTool.vCulomnVale(column,incomingConnName));
    stringBuffer.append(TEXT_114);
    
		}

    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_121);
    
            if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_122);
    
            } else {
                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_123);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_125);
    
                    for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_126);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_128);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_130);
    
                    }

    stringBuffer.append(TEXT_131);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_133);
    
                } else {

    stringBuffer.append(TEXT_134);
    
                }
            }

    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    
			for(Column column : insertValueList){

    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(vTool.vStr("\""+column.name+"\""));
    stringBuffer.append(TEXT_139);
    stringBuffer.append(vTool.vCulomnVale(column,incomingConnName));
    stringBuffer.append(TEXT_140);
    
			}

    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    
                if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_145);
    
                } else {
                    if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                        for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_146);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_148);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_150);
    
                        }

    stringBuffer.append(TEXT_151);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_153);
    
                    } else {

    stringBuffer.append(TEXT_154);
    
                    }
                }

    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_156);
    
	}else if (("DELETE").equals(dataAction)){

    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_162);
    
        if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_163);
    
        } else {
            if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_164);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_166);
    
                for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_167);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_168);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_169);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_171);
    
            	}

    stringBuffer.append(TEXT_172);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_174);
    
            } else {

    stringBuffer.append(TEXT_175);
    
            }
        }

    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    
	}
	
    if(outgoingConns != null && outgoingConns.size() > 0) {
        
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    
            for(IConnection outgoingConn : outgoingConns) {
                if(rejectConnName == null || (rejectConnName != null && !outgoingConn.getName().equals(rejectConnName))) {
                    if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_180);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_181);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_182);
    
                        for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_183);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_184);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_185);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_186);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_187);
    
                        }
                    }
                }
            }

    stringBuffer.append(TEXT_188);
    
    }
}

    stringBuffer.append(TEXT_189);
    return stringBuffer.toString();
  }
}
