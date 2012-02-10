package org.talend.designer.codegen.translators.databases.teradata;

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

public class TTeradataFastLoadMainJava
{
  protected static String nl;
  public static synchronized TTeradataFastLoadMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataFastLoadMainJava result = new TTeradataFastLoadMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "\t";
  protected final String TEXT_3 = NL + "if(";
  protected final String TEXT_4 = ".";
  protected final String TEXT_5 = "==null){" + NL + "\t";
  protected final String TEXT_6 = ".setNull(";
  protected final String TEXT_7 = ",java.sql.Types.CHAR);" + NL + "\t";
  protected final String TEXT_8 = NL + "if(";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = "==null){" + NL + "\t";
  protected final String TEXT_11 = ".setNull(";
  protected final String TEXT_12 = ",java.sql.Types.DATE);" + NL + "\t";
  protected final String TEXT_13 = NL + "if(";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = "==null){";
  protected final String TEXT_16 = NL + "    ";
  protected final String TEXT_17 = ".setNull(";
  protected final String TEXT_18 = ",java.sql.Types.TIMESTAMP);";
  protected final String TEXT_19 = NL + "if(";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = "==null){" + NL + "\t";
  protected final String TEXT_22 = ".setNull(";
  protected final String TEXT_23 = ",java.sql.Types.ARRAY);\t\t\t" + NL + "\t";
  protected final String TEXT_24 = NL + "if(";
  protected final String TEXT_25 = ".";
  protected final String TEXT_26 = "==null){" + NL + "\t";
  protected final String TEXT_27 = ".setNull(";
  protected final String TEXT_28 = ",java.sql.Types.INTEGER);" + NL + "\t";
  protected final String TEXT_29 = NL + "if(";
  protected final String TEXT_30 = ".";
  protected final String TEXT_31 = "==null){" + NL + "\t";
  protected final String TEXT_32 = ".setNull(";
  protected final String TEXT_33 = ",java.sql.Types.VARCHAR);" + NL + "\t";
  protected final String TEXT_34 = NL + "if(";
  protected final String TEXT_35 = ".";
  protected final String TEXT_36 = "==null){" + NL + "\t";
  protected final String TEXT_37 = ".setNull(";
  protected final String TEXT_38 = ",java.sql.Types.OTHER);" + NL + "\t";
  protected final String TEXT_39 = NL + "if(";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = "==null){" + NL + "\t";
  protected final String TEXT_42 = ".setNull(";
  protected final String TEXT_43 = ",java.sql.Types.BOOLEAN);\t" + NL + "\t";
  protected final String TEXT_44 = NL + "if(";
  protected final String TEXT_45 = ".";
  protected final String TEXT_46 = "==null){" + NL + "\t";
  protected final String TEXT_47 = ".setNull(";
  protected final String TEXT_48 = ",java.sql.Types.DOUBLE);\t" + NL + "\t";
  protected final String TEXT_49 = NL + "if(";
  protected final String TEXT_50 = ".";
  protected final String TEXT_51 = "==null){" + NL + "\t";
  protected final String TEXT_52 = ".setNull(";
  protected final String TEXT_53 = ",java.sql.Types.FLOAT);\t" + NL + "\t";
  protected final String TEXT_54 = NL + NL + "}else{" + NL + "" + NL + "\t";
  protected final String TEXT_55 = NL + "\t";
  protected final String TEXT_56 = NL + "\tif(";
  protected final String TEXT_57 = ".";
  protected final String TEXT_58 = "==null){" + NL + "\t";
  protected final String TEXT_59 = NL + "\tif((\"null\").equals(String.valueOf(";
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = ").toLowerCase())){" + NL + "\t";
  protected final String TEXT_62 = NL + "\t\t";
  protected final String TEXT_63 = ".setNull(";
  protected final String TEXT_64 = ",java.sql.Types.CHAR);" + NL + "\t\t" + NL + "\t}else if(";
  protected final String TEXT_65 = ".";
  protected final String TEXT_66 = " == '\\0'){" + NL + "\t" + NL + "\t\t";
  protected final String TEXT_67 = ".setString(";
  protected final String TEXT_68 = ",\"\");" + NL + "\t\t" + NL + "\t}else{" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_69 = ".setString(";
  protected final String TEXT_70 = ",String.valueOf(";
  protected final String TEXT_71 = ".";
  protected final String TEXT_72 = "));" + NL + "\t}" + NL + "\t";
  protected final String TEXT_73 = NL + "\tif(";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = "!=null){" + NL + "\t" + NL + "\t\t";
  protected final String TEXT_76 = ".setDate(";
  protected final String TEXT_77 = ",new java.sql.Date(";
  protected final String TEXT_78 = ".";
  protected final String TEXT_79 = ".getTime()));" + NL + "\t\t" + NL + "\t}else{" + NL + "\t" + NL + "\t\t";
  protected final String TEXT_80 = ".setNull(";
  protected final String TEXT_81 = ",java.sql.Types.DATE);" + NL + "\t\t" + NL + "\t}" + NL + "\t";
  protected final String TEXT_82 = NL + "\tif(";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = "!=null){" + NL + "\t    " + NL + "\t   ";
  protected final String TEXT_85 = ".setTimestamp(";
  protected final String TEXT_86 = ",new java.sql.Timestamp(";
  protected final String TEXT_87 = ".";
  protected final String TEXT_88 = ".getTime()));" + NL + "\t        " + NL + "\t}else{" + NL + "\t    " + NL + "\t   ";
  protected final String TEXT_89 = ".setNull(";
  protected final String TEXT_90 = ",java.sql.Types.TIMESTAMP);" + NL + "\t        " + NL + "    }" + NL + "\t";
  protected final String TEXT_91 = NL + "\t";
  protected final String TEXT_92 = ".set";
  protected final String TEXT_93 = "(";
  protected final String TEXT_94 = ",";
  protected final String TEXT_95 = ".";
  protected final String TEXT_96 = ");" + NL + "\t";
  protected final String TEXT_97 = NL + "\t\t" + NL + "}" + NL + "\t";
  protected final String TEXT_98 = NL + "        try{" + NL + "\t\tpstmt_";
  protected final String TEXT_99 = ".addBatch();" + NL + "        }catch(Exception e)" + NL + "        {";
  protected final String TEXT_100 = NL + "                throw(e);";
  protected final String TEXT_101 = NL + "                        ";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = " = ";
  protected final String TEXT_104 = ".";
  protected final String TEXT_105 = ";";
  protected final String TEXT_106 = NL + "                    ";
  protected final String TEXT_107 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_108 = ";";
  protected final String TEXT_109 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_110 = NL + "        }" + NL + "\t";
  protected final String TEXT_111 = NL + "        try{" + NL + "\t\tpstmt_";
  protected final String TEXT_112 = ".addBatch();" + NL + "        }catch(Exception e)" + NL + "        {";
  protected final String TEXT_113 = NL + "                throw(e);";
  protected final String TEXT_114 = NL + "                        ";
  protected final String TEXT_115 = ".";
  protected final String TEXT_116 = " = ";
  protected final String TEXT_117 = ".";
  protected final String TEXT_118 = ";";
  protected final String TEXT_119 = NL + "                    ";
  protected final String TEXT_120 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_121 = ";";
  protected final String TEXT_122 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_123 = NL + "        }";
  protected final String TEXT_124 = NL;
  protected final String TEXT_125 = NL + NL + "\t\tjava.sql.ResultSet rs_";
  protected final String TEXT_126 = " = pstmt_";
  protected final String TEXT_127 = ".executeQuery();" + NL + "\t\tint checkCount_";
  protected final String TEXT_128 = " = -1;" + NL + "\t\twhile(rs_";
  protected final String TEXT_129 = ".next())" + NL + "\t\t{" + NL + "\t\t\tcheckCount_";
  protected final String TEXT_130 = " = rs_";
  protected final String TEXT_131 = ".getInt(1);" + NL + "\t\t}" + NL + "\t\tif(checkCount_";
  protected final String TEXT_132 = " > 0)" + NL + "\t\t{" + NL + "\t\t\t";
  protected final String TEXT_133 = NL + "            try{" + NL + "\t\tpstmt_";
  protected final String TEXT_134 = ".addBatch();" + NL + "\t\t\t}catch(Exception e)" + NL + "\t\t\t{" + NL + "\t            ";
  protected final String TEXT_135 = NL + "\t                throw(e);" + NL + "\t                ";
  protected final String TEXT_136 = NL + "\t                        ";
  protected final String TEXT_137 = ".";
  protected final String TEXT_138 = " = ";
  protected final String TEXT_139 = ".";
  protected final String TEXT_140 = ";" + NL + "\t                        ";
  protected final String TEXT_141 = NL + "\t                    ";
  protected final String TEXT_142 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_143 = ";" + NL + "\t                    ";
  protected final String TEXT_144 = NL + "\t                    System.err.print(e.getMessage());" + NL + "\t                    ";
  protected final String TEXT_145 = NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\telse" + NL + "\t\t{" + NL + "\t\t\t";
  protected final String TEXT_146 = NL + "            try{" + NL + "\t\tpstmt_";
  protected final String TEXT_147 = ".addBatch();" + NL + "\t\t\t}catch(Exception e)" + NL + "\t\t\t{" + NL + "\t            ";
  protected final String TEXT_148 = NL + "\t                throw(e);" + NL + "\t                ";
  protected final String TEXT_149 = NL + "\t                        ";
  protected final String TEXT_150 = ".";
  protected final String TEXT_151 = " = ";
  protected final String TEXT_152 = ".";
  protected final String TEXT_153 = ";" + NL + "\t                        ";
  protected final String TEXT_154 = NL + "\t                    ";
  protected final String TEXT_155 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_156 = ";" + NL + "\t                    ";
  protected final String TEXT_157 = NL + "\t                    System.err.print(e.getMessage());" + NL + "\t                    ";
  protected final String TEXT_158 = NL + "\t\t\t}" + NL + "\t\t}" + NL;
  protected final String TEXT_159 = NL;
  protected final String TEXT_160 = NL + NL + "    try{" + NL + "\t\tpstmt_";
  protected final String TEXT_161 = ".addBatch();" + NL + "\t}catch(Exception e)" + NL + "\t{";
  protected final String TEXT_162 = NL + "            throw(e);";
  protected final String TEXT_163 = NL + "                    ";
  protected final String TEXT_164 = ".";
  protected final String TEXT_165 = " = ";
  protected final String TEXT_166 = ".";
  protected final String TEXT_167 = ";";
  protected final String TEXT_168 = NL + "                ";
  protected final String TEXT_169 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_170 = ";";
  protected final String TEXT_171 = NL + "                System.err.print(e.getMessage());";
  protected final String TEXT_172 = NL + "\t}" + NL + "\tif(updatedCount_";
  protected final String TEXT_173 = " == 0) {" + NL + "\t\t";
  protected final String TEXT_174 = NL + "        try{" + NL + "\t\tpstmt_";
  protected final String TEXT_175 = ".addBatch();" + NL + "            nb_line_inserted_";
  protected final String TEXT_176 = "++;" + NL + "\t\t}catch(Exception e)" + NL + "\t\t{";
  protected final String TEXT_177 = NL + "                throw(e);";
  protected final String TEXT_178 = NL + "                        ";
  protected final String TEXT_179 = ".";
  protected final String TEXT_180 = " = ";
  protected final String TEXT_181 = ".";
  protected final String TEXT_182 = ";";
  protected final String TEXT_183 = NL + "                    ";
  protected final String TEXT_184 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_185 = ";";
  protected final String TEXT_186 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_187 = NL + "\t\t}" + NL + "\t} else {" + NL + "\t    nb_line_update_";
  protected final String TEXT_188 = " ++;" + NL + "\t}" + NL;
  protected final String TEXT_189 = NL + "    try{" + NL + "\t\tpstmt_";
  protected final String TEXT_190 = ".addBatch();" + NL + "\t\t}catch(Exception e)" + NL + "\t\t{";
  protected final String TEXT_191 = NL + "                throw(e);";
  protected final String TEXT_192 = NL + "                        ";
  protected final String TEXT_193 = ".";
  protected final String TEXT_194 = " = ";
  protected final String TEXT_195 = ".";
  protected final String TEXT_196 = ";";
  protected final String TEXT_197 = NL + "                    ";
  protected final String TEXT_198 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_199 = ";";
  protected final String TEXT_200 = NL + "                    System.err.print(e.getMessage());";
  protected final String TEXT_201 = NL + "\t\t}" + NL + "\t";
  protected final String TEXT_202 = NL + "\t                ";
  protected final String TEXT_203 = ".";
  protected final String TEXT_204 = " = ";
  protected final String TEXT_205 = ".";
  protected final String TEXT_206 = ";" + NL + "\t                ";
  protected final String TEXT_207 = NL + "\t    commitCounter_";
  protected final String TEXT_208 = "++;";
  protected final String TEXT_209 = NL + "        " + NL + "        if(commitEvery_";
  protected final String TEXT_210 = " <= commitCounter_";
  protected final String TEXT_211 = "){" + NL + "        " + NL + "\t\t\tpstmt_";
  protected final String TEXT_212 = ".executeBatch();" + NL + "\t\t\tpstmt_";
  protected final String TEXT_213 = ".clearBatch();" + NL + "        \tcommitCounter_";
  protected final String TEXT_214 = "=0;" + NL + "        \t" + NL + "        }\t" + NL + "\t";
  protected final String TEXT_215 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String dataAction = "INSERT";
    String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
	String tableName = ElementParameterParser.getValue(node,"__TABLE__");
            
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
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

///////////////Inner Class Column///////////////////////////
class Column{

	IMetadataColumn column;
	
	String name;
	
	String sqlStmt;
	
	String value;
	
	boolean addCol;
	
	List<Column> replacement = new ArrayList<Column>();
	
	public Column(String colName,String sqlStmt,boolean addCol){
		this.column = null;
		this.name = colName;
		this.sqlStmt = sqlStmt;
		this.value = "?";
		this.addCol =addCol;
	}
	
	public Column(IMetadataColumn column){
		this.column = column;
		this.name = column.getLabel();
		this.sqlStmt = "=?";
		this.value = "?";
		this.addCol =false;
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
	
	public void setColumn(IMetadataColumn column){
		this.column = column;
	}
	
	public IMetadataColumn getColumn(){
		return this.column;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
	
	public void setIsAddCol(boolean isadd){
		this.addCol = isadd;
	}
	
	public boolean isAddCol(){
		return this.addCol;
	}
	
	public void setSqlStmt(String sql){
		this.sqlStmt = sql;
	}
	
	public String getSqlStmt(){
		return this.sqlStmt;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
}
	StringBuilder insertColName = new StringBuilder();
	
	StringBuilder insertValueStmt = new StringBuilder();
	
	StringBuilder updateSetStmt = new StringBuilder();
	
	StringBuilder updateWhereStmt = new StringBuilder();
	
	List<Column> stmtStructure =  new LinkedList<Column>();
	
for(IMetadataColumn column:columnList){
	stmtStructure.add(new Column(column));
}

////////////////////////////////////////////////////////////
List<Column> colStruct =  new ArrayList();
for(Column colStmt:stmtStructure){
	if(!colStmt.isReplaced()&&!colStmt.isAddCol()){
		colStruct.add(colStmt);
	}
}

/////////////////Inner Class Operation///////////////////////
class Operation{
	public static final int NORMAL_TYPE = 0;
	public static final int INSERT_TYPE = 1;
	public static final int UPDATE_TYPE = 2;
	
	public String generateType(String typeToGenerate){
		if(("byte[]").equals(typeToGenerate)){
 	  		typeToGenerate = "Bytes";
 	   	}else if(("java.util.Date").equals(typeToGenerate)){
 	   		typeToGenerate = "Date";
 	  	}else if(("java.util.Timestamp").equals(typeToGenerate)){
            typeToGenerate = "Timestamp";
        }else if(("Integer").equals(typeToGenerate)){
 	   		typeToGenerate = "Int";
 	   	}else{
			typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
		}
		return typeToGenerate;
	}
	
	public void generateSetStmt(String typeToGenerate,Column column,
								int counter,String incomingConnName,String cid,int stmtType){
		boolean isObject = false;								
		String prefix = null;								
		if(stmtType == Operation.NORMAL_TYPE){
			prefix = "pstmt_";
		}else if(stmtType == Operation.INSERT_TYPE){
			prefix = "pstmtInsert_";
		}else if(stmtType == Operation.UPDATE_TYPE){
			prefix = "pstmtUpdate_";
		}
		
    stringBuffer.append(TEXT_2);
    if(("Character").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_3);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_7);
    
	}else if(("Date").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_8);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_12);
    
	}else if(("Timestamp").equals(typeToGenerate)){
    isObject = true;
    
    stringBuffer.append(TEXT_13);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_18);
    
    }else if(("byte[]").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_19);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_21);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_23);
    
	}else if(("Long").equals(typeToGenerate)||("Byte").equals(typeToGenerate)||("Integer").equals(typeToGenerate)||("Short").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_24);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_28);
    
	}else if(("String").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_29);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_31);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_33);
    
	}else if(("Object").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_34);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_36);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_38);
    
	}else if(("Boolean").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_39);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_41);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_43);
    
	}else if(("Double").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_44);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_46);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_48);
    
	}else if(("Float").equals(typeToGenerate)){
	isObject = true;
	
    stringBuffer.append(TEXT_49);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_51);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_53);
    
	}
if(isObject){
	
    stringBuffer.append(TEXT_54);
    
}
	typeToGenerate = generateType(typeToGenerate);
	
	if(("Char").equals(typeToGenerate)||("Character").equals(typeToGenerate)){
	
    stringBuffer.append(TEXT_55);
    if(isObject){
    stringBuffer.append(TEXT_56);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_58);
    }else{
    stringBuffer.append(TEXT_59);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_61);
    }
    stringBuffer.append(TEXT_62);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_72);
    
	}else if(("Date").equals(typeToGenerate)){
	
    stringBuffer.append(TEXT_73);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_75);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_79);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_81);
    
	}else if(("Timestamp").equals(typeToGenerate)){
	
    stringBuffer.append(TEXT_82);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_84);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_88);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_90);
    
	}else{
	
    stringBuffer.append(TEXT_91);
    stringBuffer.append(prefix+cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(counter);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(column.getName());
    stringBuffer.append(TEXT_96);
    
	}
if(isObject){
	
    stringBuffer.append(TEXT_97);
    
}
	}
}


Operation operation = new Operation();

if(incomingConnName != null && columnList != null){
	if(("INSERT").equals(dataAction)){
		int counter = 1;
		
		for(Column column:colStruct){
			String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
			
			//typeToGenerate = operation.generateType(typeToGenerate);
			
			operation.generateSetStmt(typeToGenerate,column,counter,incomingConnName,cid,0);
			
        	counter++;
		}
	
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    
            if (("true").equals(dieOnError)) {
                
    stringBuffer.append(TEXT_100);
    
            } else {
                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                    for(IMetadataColumn column : columnList) {
                        
    stringBuffer.append(TEXT_101);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_103);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_105);
    
                    }
                    
    stringBuffer.append(TEXT_106);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_108);
    
                } else {
                    
    stringBuffer.append(TEXT_109);
    
                }
            } 
            
    stringBuffer.append(TEXT_110);
    
	}else if(("UPDATE").equals(dataAction)){
		int counterCol = 1;
		
		for(Column column:colStruct){
			String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
			
			//typeToGenerate = operation.generateType(typeToGenerate);
			
			if(!column.getColumn().isKey()){
				
				operation.generateSetStmt(typeToGenerate,column,counterCol,incomingConnName,cid,0);
				
        		counterCol++;
			}
		}

		for(Column column:colStruct){
			String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
			
			//typeToGenerate = operation.generateType(typeToGenerate);
			
			if(column.getColumn().isKey()){
			
				operation.generateSetStmt(typeToGenerate,column,counterCol,incomingConnName,cid,0);
				
        		counterCol++;
			}
		}

	
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    
            if (("true").equals(dieOnError)) {
                
    stringBuffer.append(TEXT_113);
    
            } else {
                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                    for(IMetadataColumn column : columnList) {
                        
    stringBuffer.append(TEXT_114);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_116);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_118);
    
                    }
                    
    stringBuffer.append(TEXT_119);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_121);
    
                } else {
                    
    stringBuffer.append(TEXT_122);
    
                }
            } 
            
    stringBuffer.append(TEXT_123);
    
	}else if (("INSERT_OR_UPDATE").equals(dataAction)){

    stringBuffer.append(TEXT_124);
    
		int columnIndex = 1;
		for(Column column:colStruct)
		{
			if(column.getColumn().isKey())
			{
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
				operation.generateSetStmt(typeToGenerate,column,columnIndex,incomingConnName,cid,0);
				columnIndex++;				
			}
		}

    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_132);
    
			int counterCol = 1;
			for(Column column:colStruct)
			{
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
				if(!column.getColumn().isKey())
				{
					operation.generateSetStmt(typeToGenerate,column,counterCol,incomingConnName,cid,2);
					counterCol++;
				}
			}
			for(Column column:colStruct)
			{
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
				if(column.getColumn().isKey())
				{
					operation.generateSetStmt(typeToGenerate,column,counterCol,incomingConnName,cid,2);					
	        		counterCol++;
				}
			}
			
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    
	            if (("true").equals(dieOnError)) {
	                
    stringBuffer.append(TEXT_135);
    
	            } else {
	                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
	                    for(IMetadataColumn column : columnList) {
	                        
    stringBuffer.append(TEXT_136);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_138);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_140);
    
	                    }
	                    
    stringBuffer.append(TEXT_141);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_143);
    
	                } else {
	                    
    stringBuffer.append(TEXT_144);
    
	                }
	            } 
	            
    stringBuffer.append(TEXT_145);
    
			int counterInsert = 1;
			for(Column columnInsert:colStruct)
			{
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(columnInsert.getColumn().getTalendType(), columnInsert.getColumn().isNullable());
				operation.generateSetStmt(typeToGenerate,columnInsert,counterInsert,incomingConnName,cid,1);
				counterInsert++;
			}
			
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_147);
    
	            if (("true").equals(dieOnError)) {
	                
    stringBuffer.append(TEXT_148);
    
	            } else {
	                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
	                    for(IMetadataColumn column : columnList) {
	                        
    stringBuffer.append(TEXT_149);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_151);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_153);
    
	                    }
	                    
    stringBuffer.append(TEXT_154);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_156);
    
	                } else {
	                    
    stringBuffer.append(TEXT_157);
    
	                }
	            } 
	            
    stringBuffer.append(TEXT_158);
    
	}else if (("UPDATE_OR_INSERT").equals(dataAction)){

    stringBuffer.append(TEXT_159);
    
		int counterColUpdate = 1;
		for(Column columnUpdate:colStruct){
			String typeToGenerate = JavaTypesManager.getTypeToGenerate(columnUpdate.getColumn().getTalendType(), columnUpdate.getColumn().isNullable());
			
			//typeToGenerate = operation.generateType(typeToGenerate);
			
			if(!columnUpdate.getColumn().isKey()){
			
				operation.generateSetStmt(typeToGenerate,columnUpdate,counterColUpdate,incomingConnName,cid,2);
				
        		counterColUpdate++;
			}
		}

		for(Column columnUpdate:colStruct){
			String typeToGenerate = JavaTypesManager.getTypeToGenerate(columnUpdate.getColumn().getTalendType(), columnUpdate.getColumn().isNullable());
			
			//typeToGenerate = operation.generateType(typeToGenerate);
			
			if(columnUpdate.getColumn().isKey()){
			
				operation.generateSetStmt(typeToGenerate,columnUpdate,counterColUpdate,incomingConnName,cid,2);
				
        		counterColUpdate++;
			}
		}
	
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    
        if (("true").equals(dieOnError)) {
            
    stringBuffer.append(TEXT_162);
    
        } else {
            if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                for(IMetadataColumn column : columnList) {
                    
    stringBuffer.append(TEXT_163);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_165);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_167);
    
                }
                
    stringBuffer.append(TEXT_168);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_170);
    
            } else {
                
    stringBuffer.append(TEXT_171);
    
            }
        } 
        
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_173);
    
		int counter = 1;
		
		for(Column column:colStruct){
			String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
			
			//typeToGenerate = operation.generateType(typeToGenerate);
			
			operation.generateSetStmt(typeToGenerate,column,counter,incomingConnName,cid,1);
			
        	counter++;
		}
		
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    
            if (("true").equals(dieOnError)) {
                
    stringBuffer.append(TEXT_177);
    
            } else {
                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                    for(IMetadataColumn column : columnList) {
                        
    stringBuffer.append(TEXT_178);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_179);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_180);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_181);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_182);
    
                    }
                    
    stringBuffer.append(TEXT_183);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_185);
    
                } else {
                    
    stringBuffer.append(TEXT_186);
    
                }
            } 
            
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_188);
    
	
	}else if (("DELETE").equals(dataAction)){
		int keyCounter = 1;
		for(Column column:colStruct){
			String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getColumn().getTalendType(), column.getColumn().isNullable());
			
			//typeToGenerate = operation.generateType(typeToGenerate);
			
			if(column.getColumn().isKey()){
				
				operation.generateSetStmt(typeToGenerate,column,keyCounter,incomingConnName,cid,0);
				
        		keyCounter++;
			}
		}
	
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_190);
    
            if (("true").equals(dieOnError)) {
                
    stringBuffer.append(TEXT_191);
    
            } else {
                if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {
                    for(IMetadataColumn column : columnList) {
                        
    stringBuffer.append(TEXT_192);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_193);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_194);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_196);
    
                    }
                    
    stringBuffer.append(TEXT_197);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_199);
    
                } else {
                    
    stringBuffer.append(TEXT_200);
    
                }
            } 
            
    stringBuffer.append(TEXT_201);
    
	}
	
	if(outgoingConns != null && outgoingConns.size() > 0) {
	    for(IConnection outgoingConn : outgoingConns) {
	        if(!outgoingConn.getName().equals(rejectConnName) &&
	        	outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA) ) {
	            for(IMetadataColumn column : columnList) {
	                
    stringBuffer.append(TEXT_202);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_203);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_204);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_205);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_206);
    	                
	            }
	        }
	    }
	}

    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_208);
    
	////////////commit every////////////
	if(!("").equals(commitEvery) && !("0").equals(commitEvery)){
	
    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_210);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_214);
    
	}
}

    stringBuffer.append(TEXT_215);
    return stringBuffer.toString();
  }
}
