package org.talend.designer.codegen.translators.databases.teradata;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class TTeradataFastLoadBeginJava
{
  protected static String nl;
  public static synchronized TTeradataFastLoadBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataFastLoadBeginJava result = new TTeradataFastLoadBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "int nb_line_update_";
  protected final String TEXT_4 = " = 0;" + NL + "int nb_line_inserted_";
  protected final String TEXT_5 = " = 0;" + NL + "int nb_line_deleted_";
  protected final String TEXT_6 = " = 0;" + NL + "" + NL + "int deletedCount_";
  protected final String TEXT_7 = "=0;" + NL + "int updatedCount_";
  protected final String TEXT_8 = "=0;" + NL + "int insertedCount_";
  protected final String TEXT_9 = "=0;" + NL + "" + NL + "" + NL + "java.lang.Class.forName(\"com.teradata.jdbc.TeraDriver\");" + NL + "String url_";
  protected final String TEXT_10 = " = \"jdbc:teradata://\" + ";
  protected final String TEXT_11 = "+\"/TYPE=FASTLOAD\";" + NL;
  protected final String TEXT_12 = NL;
  protected final String TEXT_13 = NL + "    int keyCount_";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ";" + NL + "    if(keyCount_";
  protected final String TEXT_16 = " < 1)" + NL + "    {" + NL + "    \tthrow new RuntimeException(\"For update or delete, Schema must have a key\");" + NL + "    }";
  protected final String TEXT_17 = " " + NL + "" + NL + "String dbUser_";
  protected final String TEXT_18 = " = ";
  protected final String TEXT_19 = ";" + NL + "String dbPwd_";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = ";" + NL + "" + NL + "java.sql.Connection conn_";
  protected final String TEXT_22 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_23 = ",dbUser_";
  protected final String TEXT_24 = ",dbPwd_";
  protected final String TEXT_25 = ");" + NL;
  protected final String TEXT_26 = NL + "java.sql.Statement stmtClear_";
  protected final String TEXT_27 = " = conn_";
  protected final String TEXT_28 = ".createStatement();" + NL + "deletedCount_";
  protected final String TEXT_29 = " = deletedCount_";
  protected final String TEXT_30 = " + stmtClear_";
  protected final String TEXT_31 = ".executeUpdate(\"delete from \" + ";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = NL;
  protected final String TEXT_34 = NL + NL + "java.sql.PreparedStatement pstmtInsert_";
  protected final String TEXT_35 = " =null;" + NL + "" + NL + "java.sql.PreparedStatement pstmtUpdate_";
  protected final String TEXT_36 = " =null;" + NL + "" + NL + "java.sql.PreparedStatement pstmt_";
  protected final String TEXT_37 = " =null;" + NL;
  protected final String TEXT_38 = NL + NL + "java.sql.PreparedStatement pstmt_";
  protected final String TEXT_39 = " =null;" + NL + "\t";
  protected final String TEXT_40 = NL + "\t String tableName_";
  protected final String TEXT_41 = " = ";
  protected final String TEXT_42 = ";" + NL + "\t String dbname_";
  protected final String TEXT_43 = " = ";
  protected final String TEXT_44 = ";" + NL + "\t " + NL + "\tif(dbname_";
  protected final String TEXT_45 = " == null || dbname_";
  protected final String TEXT_46 = ".trim().length() == 0) {" + NL + "    \ttableName_";
  protected final String TEXT_47 = " = ";
  protected final String TEXT_48 = ";" + NL + "\t} else {" + NL + "    \ttableName_";
  protected final String TEXT_49 = " = dbname_";
  protected final String TEXT_50 = " + \".\" + ";
  protected final String TEXT_51 = ";" + NL + "\t}";
  protected final String TEXT_52 = "\t" + NL + "\t" + NL + "pstmt_";
  protected final String TEXT_53 = " = conn_";
  protected final String TEXT_54 = ".prepareStatement(\"INSERT INTO \"+tableName_";
  protected final String TEXT_55 = "+\" (";
  protected final String TEXT_56 = ") VALUES (";
  protected final String TEXT_57 = ")\");" + NL + "" + NL + "\t";
  protected final String TEXT_58 = NL + "\t" + NL + "pstmt_";
  protected final String TEXT_59 = " = conn_";
  protected final String TEXT_60 = ".prepareStatement(\"UPDATE \"+tableName_";
  protected final String TEXT_61 = "+\" SET ";
  protected final String TEXT_62 = " WHERE ";
  protected final String TEXT_63 = "\");" + NL + "" + NL + "\t";
  protected final String TEXT_64 = NL + "pstmt_";
  protected final String TEXT_65 = " = conn_";
  protected final String TEXT_66 = ".prepareStatement(\"SELECT COUNT(1) FROM \" + tableName_";
  protected final String TEXT_67 = " + \" WHERE ";
  protected final String TEXT_68 = "\");\t" + NL + "" + NL + "pstmtInsert_";
  protected final String TEXT_69 = " = conn_";
  protected final String TEXT_70 = ".prepareStatement(\"INSERT INTO \"+tableName_";
  protected final String TEXT_71 = "+\" (";
  protected final String TEXT_72 = ") VALUES (";
  protected final String TEXT_73 = ")\");" + NL + "" + NL + "pstmtUpdate_";
  protected final String TEXT_74 = " = conn_";
  protected final String TEXT_75 = ".prepareStatement(\"UPDATE \"+tableName_";
  protected final String TEXT_76 = "+\" SET ";
  protected final String TEXT_77 = " WHERE ";
  protected final String TEXT_78 = "\");" + NL + "\t" + NL + "\t";
  protected final String TEXT_79 = NL + NL + "pstmtUpdate_";
  protected final String TEXT_80 = " = conn_";
  protected final String TEXT_81 = ".prepareStatement(\"UPDATE \"+tableName_";
  protected final String TEXT_82 = "+\" SET ";
  protected final String TEXT_83 = " WHERE ";
  protected final String TEXT_84 = "\");" + NL + "" + NL + "pstmtInsert_";
  protected final String TEXT_85 = " = conn_";
  protected final String TEXT_86 = ".prepareStatement(\"INSERT INTO \"+tableName_";
  protected final String TEXT_87 = "+\" (";
  protected final String TEXT_88 = ") VALUES (";
  protected final String TEXT_89 = ")\");" + NL + "\t\t" + NL + "\t";
  protected final String TEXT_90 = NL + "\t" + NL + "pstmt_";
  protected final String TEXT_91 = " = conn_";
  protected final String TEXT_92 = ".prepareStatement(\"DELETE  FROM \"+tableName_";
  protected final String TEXT_93 = "+\" WHERE ";
  protected final String TEXT_94 = "\");" + NL + "" + NL + "\t";
  protected final String TEXT_95 = NL + "\tint commitCounter_";
  protected final String TEXT_96 = " = 0;";
  protected final String TEXT_97 = NL + NL + "int commitEvery_";
  protected final String TEXT_98 = " = ";
  protected final String TEXT_99 = ";" + NL + "" + NL + "conn_";
  protected final String TEXT_100 = ".setAutoCommit(false);";
  protected final String TEXT_101 = NL;
  protected final String TEXT_102 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
    
    String dbServer = ElementParameterParser.getValue(node, "__SERVER__");
    
    String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
    
    String dbhost = ElementParameterParser.getValue(node, "__HOST__");
    	
	String dbuser= ElementParameterParser.getValue(node, "__USER__");
	
	String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
	
	String tableName = ElementParameterParser.getValue(node,"__TABLE__");
	
	String dataAction = "INSERT";
	
	String tableAction = "false";
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_11);
    
List<IMetadataColumn> columnList = null;

List<IMetadataTable> metadatas = node.getMetadataList();
if(metadatas !=null && metadatas.size()>0){
	IMetadataTable metadata = metadatas.get(0);
	if(metadata != null){
		columnList = metadata.getListColumns();
	}
}

    stringBuffer.append(TEXT_12);
    
if(!("INSERT").equals(dataAction))
{
    int keyCount = 0;
    for(IMetadataColumn column:columnList)
    {
    	if(column.isKey())
    	{
    		keyCount++;
    	}
    }
    
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(keyCount);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    
}

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
if(("true").equals(tableAction))
{

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_32);
    	
}

    stringBuffer.append(TEXT_33);
    if(("INSERT_OR_UPDATE").equals(dataAction)||("UPDATE_OR_INSERT").equals(dataAction)){
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    }else{
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    
}

if(columnList != null && columnList.size()>0){

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

int counterOuter =0;
boolean firstKey = true;
boolean firstNoneKey = true;

for(Column colStmt:stmtStructure){
	String suffix = ",";
	
	if(colStmt.isReplaced()){		
		List<Column> replacedColumns = colStmt.getReplacement();
		int counterReplace = 0;
		if(counterOuter==(stmtStructure.size()-1) && counterReplace==(replacedColumns.size()-1) ){
			suffix = "";
		}
		for(Column replacement:replacedColumns){
			insertColName.append(replacement.getName()+suffix);
			insertValueStmt.append(replacement.getSqlStmt()+suffix);
			if(!colStmt.getColumn().isKey()){
				if(!firstNoneKey){
					updateSetStmt.append(",");
				}else{
					firstNoneKey = false;
				}
				updateSetStmt.append(replacement.getName());
				updateSetStmt.append("=" + replacement.getSqlStmt());
			}else{
				if(!firstKey){
					updateWhereStmt.append(" AND ");
				}else{
					firstKey = false;
				}
				updateWhereStmt.append(replacement.getName());
				updateWhereStmt.append("=" + replacement.getSqlStmt());
			}
			counterReplace++;
		}
	}else{
		if(counterOuter==(stmtStructure.size()-1)){
			suffix = "";
		}
		if(colStmt.isAddCol()){
			insertColName.append(colStmt.getName()+suffix);
			insertValueStmt.append(colStmt.getSqlStmt()+suffix);
			updateSetStmt.append(colStmt.getName());
			updateSetStmt.append("=" + colStmt.getSqlStmt()+suffix);
		}else{
			insertColName.append(colStmt.getName()+suffix);
			insertValueStmt.append(colStmt.getValue()+suffix);
			if(!colStmt.getColumn().isKey()){
				if(!firstNoneKey){
					updateSetStmt.append(",");
				}else{
					firstNoneKey = false;
				}
				updateSetStmt.append(colStmt.getName());
				updateSetStmt.append(colStmt.getSqlStmt());
			}else{
				if(!firstKey){
					updateWhereStmt.append(" AND ");
				}else{
					firstKey = false;
				}
				updateWhereStmt.append(colStmt.getName());
				updateWhereStmt.append(colStmt.getSqlStmt());
			}
		}
	}
	counterOuter ++;
}

    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_51);
    

	if(("INSERT").equals(dataAction)){
	
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(insertColName.toString());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(insertValueStmt.toString());
    stringBuffer.append(TEXT_57);
    
	}else if (("UPDATE").equals(dataAction)){
	
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(updateSetStmt.toString());
    stringBuffer.append(TEXT_62);
    stringBuffer.append(updateWhereStmt.toString());
    stringBuffer.append(TEXT_63);
    		
	}else if (("INSERT_OR_UPDATE").equals(dataAction)){
	
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(updateWhereStmt.toString());
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(insertColName.toString());
    stringBuffer.append(TEXT_72);
    stringBuffer.append(insertValueStmt.toString());
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(updateSetStmt.toString());
    stringBuffer.append(TEXT_77);
    stringBuffer.append(updateWhereStmt.toString());
    stringBuffer.append(TEXT_78);
    
	}else if (("UPDATE_OR_INSERT").equals(dataAction)){
	
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(updateSetStmt.toString());
    stringBuffer.append(TEXT_83);
    stringBuffer.append(updateWhereStmt.toString());
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(insertColName.toString());
    stringBuffer.append(TEXT_88);
    stringBuffer.append(insertValueStmt.toString());
    stringBuffer.append(TEXT_89);
    
			
	}else if (("DELETE").equals(dataAction)){
	
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(updateWhereStmt.toString());
    stringBuffer.append(TEXT_94);
    		
	}

    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    
	if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){

    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    
	}
}

    stringBuffer.append(TEXT_101);
    stringBuffer.append(TEXT_102);
    return stringBuffer.toString();
  }
}
