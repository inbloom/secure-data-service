package org.talend.designer.codegen.translators.databases.teradata;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.MappingTypeRetriever;
import org.talend.core.model.metadata.MetadataTalendType;
import java.util.List;
import java.lang.StringBuilder;
import java.util.Map;

public class TTeradataFastExportMainJava
{
  protected static String nl;
  public static synchronized TTeradataFastExportMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataFastExportMainJava result = new TTeradataFastExportMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tString tableFullName_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = " + \".\" + ";
  protected final String TEXT_4 = ";";
  protected final String TEXT_5 = NL + "\tjava.io.FileWriter fw_";
  protected final String TEXT_6 = " = new java.io.FileWriter(";
  protected final String TEXT_7 = "+";
  protected final String TEXT_8 = "+\".script\");";
  protected final String TEXT_9 = NL + "\tjava.io.FileWriter fw_";
  protected final String TEXT_10 = " = new java.io.FileWriter(";
  protected final String TEXT_11 = "+";
  protected final String TEXT_12 = "+\".scr\");";
  protected final String TEXT_13 = NL + "\t" + NL + "\t";
  protected final String TEXT_14 = NL + "\t" + NL + "\tStringBuilder script_";
  protected final String TEXT_15 = " = new StringBuilder();" + NL + "\tfw_";
  protected final String TEXT_16 = ".write(\".LOGTABLE \"+";
  protected final String TEXT_17 = "+\".\"+";
  protected final String TEXT_18 = "+\";\" + \"";
  protected final String TEXT_19 = "\");" + NL + "\tfw_";
  protected final String TEXT_20 = ".write(\".LOGON \"+";
  protected final String TEXT_21 = "+\"/\"";
  protected final String TEXT_22 = " \"\" ";
  protected final String TEXT_23 = "+";
  protected final String TEXT_24 = "+\",\"+";
  protected final String TEXT_25 = "+\";\"+ \"";
  protected final String TEXT_26 = "\");" + NL + "\t" + NL + "\t";
  protected final String TEXT_27 = NL + "\tfw_";
  protected final String TEXT_28 = ".write(\".BEGIN EXPORT SESSIONS 8;\"+\"";
  protected final String TEXT_29 = "\");" + NL + "\t";
  protected final String TEXT_30 = NL + "\t\tfw_";
  protected final String TEXT_31 = ".write(\"SELECT ";
  protected final String TEXT_32 = "CAST(('' ||";
  protected final String TEXT_33 = "\"+\"";
  protected final String TEXT_34 = "\");" + NL + "\t\t";
  protected final String TEXT_35 = NL + "\t\t\t\t\tfw_";
  protected final String TEXT_36 = ".write(\"TRIM(COALESCE(CAST(";
  protected final String TEXT_37 = " AS CHAR(";
  protected final String TEXT_38 = ")),'')) || '\"+";
  protected final String TEXT_39 = "+\"' ||\"+\"";
  protected final String TEXT_40 = "\");" + NL + "\t\t\t\t\t";
  protected final String TEXT_41 = NL + "\t\t\t\t\tfw_";
  protected final String TEXT_42 = ".write(\"cast( ";
  protected final String TEXT_43 = " as CHAR(";
  protected final String TEXT_44 = ")),\"+\"";
  protected final String TEXT_45 = "\");" + NL + "\t\t\t\t\t";
  protected final String TEXT_46 = NL + "\t\t\t";
  protected final String TEXT_47 = NL + "\t\t\t\t\tfw_";
  protected final String TEXT_48 = ".write(\"TRIM(COALESCE(CAST(";
  protected final String TEXT_49 = " AS VARCHAR(";
  protected final String TEXT_50 = ")),''))) AS CHAR(";
  protected final String TEXT_51 = "))\"+\"";
  protected final String TEXT_52 = "\");" + NL + "\t\t\t\t\t";
  protected final String TEXT_53 = NL + "\t\t\t\t\tfw_";
  protected final String TEXT_54 = ".write(\"cast( ";
  protected final String TEXT_55 = " as CHAR(";
  protected final String TEXT_56 = "))\"+\"";
  protected final String TEXT_57 = "\");" + NL + "\t\t\t\t\t";
  protected final String TEXT_58 = NL + "\t\t\t";
  protected final String TEXT_59 = NL + "\t" + NL + "\t\tfw_";
  protected final String TEXT_60 = ".write(\"FROM \"+ ";
  protected final String TEXT_61 = " + \".\" + ";
  protected final String TEXT_62 = "+\";\"+\"";
  protected final String TEXT_63 = "\");" + NL + "\t";
  protected final String TEXT_64 = NL + "\t\tfw_";
  protected final String TEXT_65 = ".write(";
  protected final String TEXT_66 = " +\";\"+\"";
  protected final String TEXT_67 = "\");" + NL + "\t";
  protected final String TEXT_68 = NL + "\t" + NL + "\t";
  protected final String TEXT_69 = NL + "\tfw_";
  protected final String TEXT_70 = ".write(\".EXPORT OUTFILE \\\"\"+";
  protected final String TEXT_71 = "+\".data\\\" FORMAT TEXT mode record;\"+\"";
  protected final String TEXT_72 = "\");" + NL + "\tfw_";
  protected final String TEXT_73 = ".write(\".END EXPORT;\"+\"";
  protected final String TEXT_74 = "\");" + NL + "\tfw_";
  protected final String TEXT_75 = ".write(\".LOGOFF;\"+\"";
  protected final String TEXT_76 = "\");" + NL + "\t" + NL + "\t";
  protected final String TEXT_77 = NL + "\tfw_";
  protected final String TEXT_78 = ".close();" + NL + "\t" + NL + "\t";
  protected final String TEXT_79 = NL + "\t";
  protected final String TEXT_80 = NL + "\tString[] sb_";
  protected final String TEXT_81 = " = {\"cmd\",\"/c\",\"fexp < \\\"\"+";
  protected final String TEXT_82 = "+";
  protected final String TEXT_83 = "+\".script\\\" > \\\"\"+";
  protected final String TEXT_84 = "+\"\\\" 2>&1\"};" + NL + "\t";
  protected final String TEXT_85 = NL + "\tString[] sb_";
  protected final String TEXT_86 = " = {\"sh\",\"-c\",\"fexp < \"+";
  protected final String TEXT_87 = "+";
  protected final String TEXT_88 = "+\".scr\"+\" > \\\"\"+";
  protected final String TEXT_89 = "+\"\\\" 2>&1\"};" + NL + "\t";
  protected final String TEXT_90 = NL + "\tfinal Process process_";
  protected final String TEXT_91 = " = Runtime.getRuntime().exec(sb_";
  protected final String TEXT_92 = "); " + NL + "\t" + NL + "\tThread normal_";
  protected final String TEXT_93 = " = new Thread() {" + NL + "\t    public void run() {" + NL + "\t    \ttry {" + NL + "\t    \t\tjava.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process_";
  protected final String TEXT_94 = ".getInputStream()));" + NL + "\t    \t\tString line = \"\";" + NL + "\t    \t\t" + NL + "\t    \t\ttry {" + NL + "\t    \t\t\twhile((line = reader.readLine()) != null) {" + NL + "\t    \t\t\t\tSystem.out.println(line);" + NL + "\t    \t        }" + NL + "\t    \t    } finally {" + NL + "\t    \t         reader.close();" + NL + "\t    \t    }" + NL + "\t        }catch(java.io.IOException ioe) {" + NL + "\t    \t\tioe.printStackTrace();" + NL + "\t    \t}" + NL + "\t    }" + NL + "\t};" + NL + "\tnormal_";
  protected final String TEXT_95 = ".start();" + NL + "\t" + NL + "\tThread error_";
  protected final String TEXT_96 = " = new Thread() {" + NL + "\t\tpublic void run() {" + NL + "\t\t\ttry {" + NL + "\t\t\t\tjava.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process_";
  protected final String TEXT_97 = ".getErrorStream()));" + NL + "\t\t\t\tString line = \"\";" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\twhile((line = reader.readLine()) != null) {" + NL + "\t\t\t\t\t\tSystem.err.println(line); " + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t} finally {" + NL + "\t\t\t\t\treader.close();" + NL + "\t\t\t\t}" + NL + "\t\t\t} catch(java.io.IOException ioe) {" + NL + "\t\t\t   ioe.printStackTrace();" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t};" + NL + "\terror_";
  protected final String TEXT_98 = ".start();" + NL + "\t" + NL + "\tprocess_";
  protected final String TEXT_99 = ".waitFor();" + NL + "\t" + NL + "\tnormal_";
  protected final String TEXT_100 = ".interrupt();" + NL + "\t" + NL + "\terror_";
  protected final String TEXT_101 = ".interrupt();";
  protected final String TEXT_102 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String host = ElementParameterParser.getValue(node, "__HOST__");
String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
String dbuser= ElementParameterParser.getValue(node, "__USER__");
String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
String table= ElementParameterParser.getValue(node, "__TABLE__");
String logDB = ElementParameterParser.getValue(node, "__LOG_DB__");
String logTable = ElementParameterParser.getValue(node, "__LOG_TABLE__");

boolean needHost = ("".equals(host) || "\"\"".equals(host)) ? false : true;
logDB = ("".equals(logDB) || "\"\"".equals(logDB)) ? dbname : logDB;
logTable = ("".equals(logTable) || "\"\"".equals(logTable)) ? table + "+\"_lt\"" : logTable;

boolean useQuery = "true".equals(ElementParameterParser.getValue(node, "__USE_QUERY__"));
String query = ElementParameterParser.getValue(node, "__QUERY__"); 
query = query.replaceAll("\n"," ").replaceAll("\r"," ");

String execution= ElementParameterParser.getValue(node, "__EXECUTION__");
String scriptPath= ElementParameterParser.getValue(node, "__SCRIPT_PATH__");
String exportedFile= ElementParameterParser.getValue(node, "__EXPORTED_FILE__");
String separator= ElementParameterParser.getValue(node, "__FIELD_SEPARATOR__");
String errorFile= ElementParameterParser.getValue(node, "__ERROR_FILE__");

//windows line separator as default
String lineSeparator = "\\r\\n";

if(!scriptPath.endsWith("/\"")){
	scriptPath = scriptPath+	"+\"/\"";
}
if(exportedFile.indexOf("/") !=0 && ("Windows").equals(execution)){
	exportedFile = exportedFile.replaceAll("/", "\\\\\\\\");
} else {
	//Unix line separator
	lineSeparator = "\\n";
}

String dbmsId = "teradata_id";

List<IMetadataColumn> columnList = null;
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {
		columnList = metadata.getListColumns();
	}
}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_4);
    
	if(("Windows").equals(execution)){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(scriptPath);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_8);
    
	}else{

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(scriptPath);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_12);
    
	}

    stringBuffer.append(TEXT_13);
    //build script---------------------------------------------------------
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(logDB);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(logTable);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    if(needHost) {
    stringBuffer.append(host);
    stringBuffer.append(TEXT_21);
    } else {
    stringBuffer.append(TEXT_22);
    }
    stringBuffer.append(TEXT_23);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_26);
    //Layout---------------------------------------------------------------
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_29);
    if(!useQuery) {
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    if(separator.length()>0 && !"\"\"".equals(separator)){
    stringBuffer.append(TEXT_32);
    }
    stringBuffer.append(TEXT_33);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_34);
    
		if(columnList!=null){
			int counter = 1;
			int columnsLength = 0;
			for(IMetadataColumn column:columnList){	
				if( columnList.size() != counter){	
					if(separator.length()>0 && !"\"\"".equals(separator)){
						columnsLength+=column.getLength()+separator.length();
				
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_40);
    }else{
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_44);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_45);
    }
    stringBuffer.append(TEXT_46);
    
				} else {
					if(separator.length()>0 && !"\"\"".equals(separator)){
						columnsLength+=column.getLength()+1;
			
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_49);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_50);
    stringBuffer.append(columnsLength);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_52);
    }else{
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column.getLength());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_57);
    }
    stringBuffer.append(TEXT_58);
     } 
				counter++;
			}
		}
		
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_63);
    } else {
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(query);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_67);
    }
    stringBuffer.append(TEXT_68);
    //Export file----------------------------------------------------------
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(exportedFile);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(lineSeparator);
    stringBuffer.append(TEXT_76);
    //write script to file-------------------------------------------------
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    //run fexp command----------------------------------------------------
    stringBuffer.append(TEXT_79);
    
	if("Windows".equals(execution)){
	
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(scriptPath);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(errorFile);
    stringBuffer.append(TEXT_84);
    }else{
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(scriptPath);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(errorFile);
    stringBuffer.append(TEXT_89);
    }
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(TEXT_102);
    return stringBuffer.toString();
  }
}
