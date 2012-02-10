package org.talend.designer.codegen.translators.technical;

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

public class TTeradataTPTExecMainJava
{
  protected static String nl;
  public static synchronized TTeradataTPTExecMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataTPTExecMainJava result = new TTeradataTPTExecMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "String tableFullName_";
  protected final String TEXT_2 = " = ";
  protected final String TEXT_3 = " + \".\" + ";
  protected final String TEXT_4 = ";" + NL + "java.io.FileWriter fw_";
  protected final String TEXT_5 = " = new java.io.FileWriter(";
  protected final String TEXT_6 = "+";
  protected final String TEXT_7 = "+\".script\");" + NL;
  protected final String TEXT_8 = NL + NL + NL + "fw_";
  protected final String TEXT_9 = ".write(\"/* 1 */  \\r\\n\");" + NL + "fw_";
  protected final String TEXT_10 = ".write(\"/* 2 */  \\r\\n\");" + NL + "fw_";
  protected final String TEXT_11 = ".write(\"/* 3 */  DEFINE JOB \" + ";
  protected final String TEXT_12 = " + \"\\r\\n\");" + NL + "fw_";
  protected final String TEXT_13 = ".write(\"/* 4 */  (\\r\\n\");" + NL + "fw_";
  protected final String TEXT_14 = ".write(\"/* 5 */  \tDEFINE OPERATOR \" + ";
  protected final String TEXT_15 = " + \"\\r\\n\");" + NL + "fw_";
  protected final String TEXT_16 = ".write(\"/* 6 */  \tTYPE LOAD\\r\\n\");" + NL + "fw_";
  protected final String TEXT_17 = ".write(\"/* 7 */  \tSCHEMA *\\r\\n\");" + NL + "fw_";
  protected final String TEXT_18 = ".write(\"/* 8 */  \tATTRIBUTES\\r\\n\");" + NL + "fw_";
  protected final String TEXT_19 = ".write(\"/* 9 */  \t(\\r\\n\");" + NL + "fw_";
  protected final String TEXT_20 = ".write(\"/* 10 */  \t\tVARCHAR UserName, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_21 = ".write(\"/* 11 */  \t\tVARCHAR UserPassword, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_22 = ".write(\"/* 12 */  \t\tVARCHAR LogTable, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_23 = ".write(\"/* 13 */  \t\tVARCHAR TargetTable, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_24 = ".write(\"/* 14 */  \t\tINTEGER BufferSize, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_25 = ".write(\"/* 15 */  \t\tINTEGER ErrorLimit, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_26 = ".write(\"/* 16 */  \t\tINTEGER MaxSessions, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_27 = ".write(\"/* 17 */  \t\tINTEGER MinSessions, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_28 = ".write(\"/* 18 */  \t\tINTEGER TenacityHours, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_29 = ".write(\"/* 19 */  \t\tINTEGER TenacitySleep, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_30 = ".write(\"/* 20 */  \t\tVARCHAR AccountID, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_31 = ".write(\"/* 21 */  \t\tVARCHAR DateForm, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_32 = ".write(\"/* 22 */  \t\tVARCHAR ErrorTable1, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_33 = ".write(\"/* 23 */  \t\tVARCHAR ErrorTable2, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_34 = ".write(\"/* 24 */  \t\tVARCHAR NotifyExit, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_35 = ".write(\"/* 25 */  \t\tVARCHAR NotifyExitIsDLL, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_36 = ".write(\"/* 26 */  \t\tVARCHAR NotifyLevel, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_37 = ".write(\"/* 27 */  \t\tVARCHAR NotifyMethod, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_38 = ".write(\"/* 28 */  \t\tVARCHAR NotifyString, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_39 = ".write(\"/* 29 */  \t\tVARCHAR PauseAcq, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_40 = ".write(\"/* 30 */  \t\tVARCHAR PrivateLogName,\\r\\n\"); " + NL + "fw_";
  protected final String TEXT_41 = ".write(\"/* 31 */  \t\tVARCHAR TdpId, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_42 = ".write(\"/* 32 */  \t\tVARCHAR TraceLevel, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_43 = ".write(\"/* 33 */  \t\tVARCHAR WorkingDatabase\\r\\n\");" + NL + "fw_";
  protected final String TEXT_44 = ".write(\"/* 34 */  \t);\\r\\n\");" + NL + "fw_";
  protected final String TEXT_45 = ".write(\"/* 35 */  \\r\\n\");" + NL + "fw_";
  protected final String TEXT_46 = ".write(\"/* 36 */  \tDEFINE SCHEMA \" + ";
  protected final String TEXT_47 = " + \"\\r\\n\");" + NL + "fw_";
  protected final String TEXT_48 = ".write(\"/* 37 */  \t(\\r\\n\");" + NL;
  protected final String TEXT_49 = NL + "\t\tfw_";
  protected final String TEXT_50 = ".write(\"               ";
  protected final String TEXT_51 = "\"+\" VARCHAR(";
  protected final String TEXT_52 = "),\\r\\n\");" + NL + "\t";
  protected final String TEXT_53 = NL + "\t\t\tfw_";
  protected final String TEXT_54 = ".write(\"               ";
  protected final String TEXT_55 = "\"+\" VARCHAR(";
  protected final String TEXT_56 = ")\\r\\n\");" + NL + "\t";
  protected final String TEXT_57 = NL + NL + "fw_";
  protected final String TEXT_58 = ".write(\"/* 45 */  \t);\\r\\n\");" + NL + "fw_";
  protected final String TEXT_59 = ".write(\"/* 46 */  \\r\\n\");" + NL + "fw_";
  protected final String TEXT_60 = ".write(\"/* 47 */  \tDEFINE OPERATOR \" + ";
  protected final String TEXT_61 = " + \"\\r\\n\");" + NL + "fw_";
  protected final String TEXT_62 = ".write(\"/* 48 */  \tTYPE DATACONNECTOR PRODUCER\\r\\n\");" + NL + "fw_";
  protected final String TEXT_63 = ".write(\"/* 49 */  \tSCHEMA \" + ";
  protected final String TEXT_64 = " + \"\\r\\n\");" + NL + "fw_";
  protected final String TEXT_65 = ".write(\"/* 50 */  \tATTRIBUTES\\r\\n\");" + NL + "fw_";
  protected final String TEXT_66 = ".write(\"/* 51 */  \t(\\r\\n\");" + NL + "fw_";
  protected final String TEXT_67 = ".write(\"/* 52 */  \t\tVARCHAR FileName, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_68 = ".write(\"/* 53 */  \t\tVARCHAR Format, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_69 = ".write(\"/* 54 */  \t\tVARCHAR OpenMode, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_70 = ".write(\"/* 55 */  \t\tINTEGER BlockSize, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_71 = ".write(\"/* 56 */  \t\tINTEGER BufferSize, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_72 = ".write(\"/* 57 */  \t\tINTEGER RetentionPeriod, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_73 = ".write(\"/* 58 */  \t\tINTEGER RowsPerInstance, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_74 = ".write(\"/* 59 */  \t\tINTEGER SecondarySpace, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_75 = ".write(\"/* 60 */  \t\tINTEGER UnitCount, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_76 = ".write(\"/* 61 */  \t\tINTEGER VigilElapsedTime, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_77 = ".write(\"/* 62 */  \t\tINTEGER VigilWaitTime, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_78 = ".write(\"/* 63 */  \t\tINTEGER VolumeCount, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_79 = ".write(\"/* 64 */  \t\tVARCHAR AccessModuleName, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_80 = ".write(\"/* 65 */  \t\tVARCHAR AccessModuleInitStr, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_81 = ".write(\"/* 66 */  \t\tVARCHAR DirectoryPath, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_82 = ".write(\"/* 67 */  \t\tVARCHAR ExpirationDate, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_83 = ".write(\"/* 68 */  \t\tVARCHAR IndicatorMode, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_84 = ".write(\"/* 69 */  \t\tVARCHAR PrimarySpace, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_85 = ".write(\"/* 70 */  \t\tVARCHAR PrivateLogName, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_86 = ".write(\"/* 71 */  \t\tVARCHAR RecordFormat, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_87 = ".write(\"/* 72 */  \t\tVARCHAR RecordLength, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_88 = ".write(\"/* 73 */  \t\tVARCHAR SpaceUnit, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_89 = ".write(\"/* 74 */  \t\tVARCHAR TextDelimiter, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_90 = ".write(\"/* 75 */  \t\tVARCHAR VigilNoticeFileName, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_91 = ".write(\"/* 76 */  \t\tVARCHAR VigilStartTime, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_92 = ".write(\"/* 77 */  \t\tVARCHAR VigilStopTime, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_93 = ".write(\"/* 78 */  \t\tVARCHAR VolSerNumber, \\r\\n\");" + NL + "fw_";
  protected final String TEXT_94 = ".write(\"/* 79 */  \t\tVARCHAR UnitType\\r\\n\");" + NL + "fw_";
  protected final String TEXT_95 = ".write(\"/* 80 */  \t);\\r\\n\");" + NL + "fw_";
  protected final String TEXT_96 = ".write(\"/* 81 */  \\r\\n\");" + NL + "fw_";
  protected final String TEXT_97 = ".write(\"/* 82 */  \tAPPLY\\r\\n\");" + NL + "fw_";
  protected final String TEXT_98 = ".write(\"/* 83 */  \t\t(\\r\\n\");" + NL;
  protected final String TEXT_99 = NL + "fw_";
  protected final String TEXT_100 = ".write(\"'INSERT INTO \"+ ";
  protected final String TEXT_101 = " + \".\" + ";
  protected final String TEXT_102 = "+\" (";
  protected final String TEXT_103 = ") VALUES (";
  protected final String TEXT_104 = ");'\\r\\n\");" + NL + "" + NL + "" + NL + "" + NL + "" + NL + "fw_";
  protected final String TEXT_105 = ".write(\"/* 85 */  \t\t)\\r\\n\");" + NL + "fw_";
  protected final String TEXT_106 = ".write(\"/* 86 */  \tTO OPERATOR\\r\\n\");" + NL + "fw_";
  protected final String TEXT_107 = ".write(\"/* 87 */  \t(\\r\\n\");" + NL + "fw_";
  protected final String TEXT_108 = ".write(\"/* 88 */  \t\t\" + ";
  protected final String TEXT_109 = " + \"[1]\\r\\n\");" + NL + "fw_";
  protected final String TEXT_110 = ".write(\"/* 89 */  \\r\\n\");" + NL + "fw_";
  protected final String TEXT_111 = ".write(\"/* 90 */  \t\tATTRIBUTES\\r\\n\");" + NL + "fw_";
  protected final String TEXT_112 = ".write(\"/* 91 */  \t\t(\\r\\n\");" + NL + "fw_";
  protected final String TEXT_113 = ".write(\"/* 92 */  \t\t\tUserName = '\" + ";
  protected final String TEXT_114 = " + \"', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_115 = ".write(\"/* 93 */  \t\t\tUserPassword = '\" + ";
  protected final String TEXT_116 = " + \"', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_117 = ".write(\"/* 94 */  \t\t\tLogTable = '\" + ";
  protected final String TEXT_118 = " + \"_log',\\r\\n\"); " + NL + "fw_";
  protected final String TEXT_119 = ".write(\"/* 95 */  \t\t\tTargetTable = '\" + ";
  protected final String TEXT_120 = " + \"', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_121 = ".write(\"/* 96 */  \t\t\tTdpId = '\" + ";
  protected final String TEXT_122 = " + \"'\\r\\n\");" + NL + "fw_";
  protected final String TEXT_123 = ".write(\"/* 97 */  \t\t)\\r\\n\");" + NL + "fw_";
  protected final String TEXT_124 = ".write(\"/* 98 */  \t)\\r\\n\");" + NL + "fw_";
  protected final String TEXT_125 = ".write(\"/* 99 */  \tSELECT * FROM OPERATOR\\r\\n\");" + NL + "fw_";
  protected final String TEXT_126 = ".write(\"/* 100 */  \t(\\r\\n\");" + NL + "fw_";
  protected final String TEXT_127 = ".write(\"/* 101 */  \t\t\" + ";
  protected final String TEXT_128 = " + \"[1]\\r\\n\");" + NL + "fw_";
  protected final String TEXT_129 = ".write(\"/* 102 */  \\r\\n\");" + NL + "fw_";
  protected final String TEXT_130 = ".write(\"/* 103 */  \t\tATTRIBUTES\\r\\n\");" + NL + "fw_";
  protected final String TEXT_131 = ".write(\"/* 104 */  \t\t(\\r\\n\");" + NL + "fw_";
  protected final String TEXT_132 = ".write(\"/* 105 */  \t\t\tFileName = '\" + ";
  protected final String TEXT_133 = " + \"', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_134 = ".write(\"/* 106 */  \t\t\tFormat = 'DELIMITED', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_135 = ".write(\"/* 107 */  \t\t\tOpenMode = 'Read', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_136 = ".write(\"/* 108 */  \t\t\tDirectoryPath = '', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_137 = ".write(\"/* 109 */  \t\t\tIndicatorMode = 'N', \\r\\n\");" + NL + "fw_";
  protected final String TEXT_138 = ".write(\"/* 110 */  \t\t\tTextDelimiter = '\" + ";
  protected final String TEXT_139 = " + \"'\\r\\n\");" + NL + "fw_";
  protected final String TEXT_140 = ".write(\"/* 111 */  \t\t)\\r\\n\");" + NL + "fw_";
  protected final String TEXT_141 = ".write(\"/* 112 */  \t);\\r\\n\");" + NL + "fw_";
  protected final String TEXT_142 = ".write(\"/* 113 */  );\\r\\n\");" + NL;
  protected final String TEXT_143 = NL + "fw_";
  protected final String TEXT_144 = ".close();" + NL;
  protected final String TEXT_145 = NL + "String sb_";
  protected final String TEXT_146 = "= new String(\"";
  protected final String TEXT_147 = "tbuild -f \"+";
  protected final String TEXT_148 = "+\" \\\"\"+";
  protected final String TEXT_149 = "+";
  protected final String TEXT_150 = "+\".script\\\" > \\\"\"+";
  protected final String TEXT_151 = "+\"\\\" 2>&1\");";
  protected final String TEXT_152 = "\t" + NL + "String sb_";
  protected final String TEXT_153 = "= new String(\"";
  protected final String TEXT_154 = "tbuild -f \\\"\"+";
  protected final String TEXT_155 = "+";
  protected final String TEXT_156 = "+\".script\\\" > \\\"\"+";
  protected final String TEXT_157 = "+\"\\\" 2>&1\");";
  protected final String TEXT_158 = NL + "final Process process_";
  protected final String TEXT_159 = " = Runtime.getRuntime().exec(sb_";
  protected final String TEXT_160 = "); " + NL + "Thread normal_";
  protected final String TEXT_161 = " = new Thread() {" + NL + "    public void run() {" + NL + "    \ttry {" + NL + "    \t\tjava.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process_";
  protected final String TEXT_162 = ".getInputStream()));" + NL + "    \t\tString line = \"\";" + NL + "    \t\ttry {" + NL + "    \t\t\twhile((line = reader.readLine()) != null) {" + NL + "    \t\t\t   System.out.println(line);" + NL + "    \t        }" + NL + "    \t    } finally {" + NL + "    \t         reader.close();" + NL + "    \t    }" + NL + "        }catch(java.io.IOException ioe) {" + NL + "    \t\tioe.printStackTrace();" + NL + "    \t}" + NL + "    }" + NL + "};" + NL + "normal_";
  protected final String TEXT_163 = ".start();" + NL + "" + NL + "Thread error_";
  protected final String TEXT_164 = " = new Thread() {" + NL + "\tpublic void run() {" + NL + "\t\ttry {" + NL + "\t\t\tjava.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process_";
  protected final String TEXT_165 = ".getErrorStream()));" + NL + "\t\t\tString line = \"\";" + NL + "\t\t\ttry {" + NL + "\t\t\t\twhile((line = reader.readLine()) != null) {" + NL + "\t\t\t\t\tSystem.err.println(line);" + NL + "\t\t\t\t}" + NL + "\t\t\t} finally {" + NL + "\t\t\t\treader.close();" + NL + "\t\t\t}" + NL + "\t\t} catch(java.io.IOException ioe) {" + NL + "\t\t   ioe.printStackTrace();" + NL + "\t\t}" + NL + "\t}" + NL + "};" + NL + "error_";
  protected final String TEXT_166 = ".start();" + NL + "" + NL + "process_";
  protected final String TEXT_167 = ".waitFor();" + NL + "" + NL + "normal_";
  protected final String TEXT_168 = ".interrupt();" + NL + "" + NL + "error_";
  protected final String TEXT_169 = ".interrupt();" + NL + "" + NL + "globalMap.put(\"";
  protected final String TEXT_170 = "_EXIT_VALUE\", process_";
  protected final String TEXT_171 = ".exitValue());" + NL;
  protected final String TEXT_172 = NL + "\tif(process_";
  protected final String TEXT_173 = ".exitValue()>=";
  protected final String TEXT_174 = ") {" + NL + "\t\tthrow new RuntimeException(\"TPT returned exit code \"+process_";
  protected final String TEXT_175 = ".exitValue());" + NL + "\t}";
  protected final String TEXT_176 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String tdpid= ElementParameterParser.getValue(node, "__TDPID__");
String jobName= ElementParameterParser.getValue(node, "__JOB_NAME__");
String operatorLoad= ElementParameterParser.getValue(node, "__OPERATOR_LOAD__");
String operatorDataConnector= ElementParameterParser.getValue(node, "__OPERATOR_DATA_CONNECTOR__");
String layoutName= ElementParameterParser.getValue(node, "__LAYOUT_NAME__");
String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
String dbuser= ElementParameterParser.getValue(node, "__USER__");
String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
String table= ElementParameterParser.getValue(node, "__TABLE__");

String scriptPath= ElementParameterParser.getValue(node, "__SCRIPT_PATH__");
String execution= ElementParameterParser.getValue(node, "__EXECUTION__");
String action= ElementParameterParser.getValue(node, "__ACTION__");
String where= ElementParameterParser.getValue(node, "__WHERE__");
String loadFile= ElementParameterParser.getValue(node, "__LOAD_FILE__");
String separator= ElementParameterParser.getValue(node, "__FIELD_SEPARATOR__");
String errorFile= ElementParameterParser.getValue(node, "__ERROR_FILE__");
String beginLoad= ElementParameterParser.getValue(node, "__BEGINLOAD_ADVANCEDPARAM__");
boolean returnCodeDie= ElementParameterParser.getValue(node, "__RETURN_CODE_DIE__").equals("true");
String returnCode= ElementParameterParser.getValue(node, "__RETURN_CODE__");
boolean specifyLogTable= ElementParameterParser.getValue(node, "__SPECIFY_LOG_TABLE__").equals("true");
String logTable= ElementParameterParser.getValue(node, "__LOG_TABLE_TABLE__");

String defineCharset = ElementParameterParser.getValue(node, "__DEFINE_CHARSET__");
String charset = ElementParameterParser.getValue(node, "__CHARSET__");

if(!scriptPath.endsWith("/\"")){
	scriptPath = scriptPath+	"+\"/\"";
}
if(loadFile.indexOf("/") !=0 && ("Windows").equals(execution)){
	loadFile = loadFile.replaceAll("/", "\\\\\\\\");
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(scriptPath);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_7);
    //build script---------------------------------------------------------
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(jobName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(operatorLoad);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(layoutName);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    
if(columnList!=null){
	int counter = 1;
	for(IMetadataColumn column:columnList){	
		if( columnList.size() != counter){	
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_51);
    stringBuffer.append(column.getLength() == null ? 0 : column.getLength());
    stringBuffer.append(TEXT_52);
    
		} else {
	
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column.getLength() == null ? 0 : column.getLength());
    stringBuffer.append(TEXT_56);
     	}
		counter++;
	}
}

    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(operatorDataConnector);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(layoutName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
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
    
StringBuilder insertColSQL = new StringBuilder();
StringBuilder insertValueSQL = new StringBuilder();
if(columnList!=null){
	String columnName = "";
 	for(IMetadataColumn column:columnList){	
 		columnName= column.getOriginalDbColumnName();
 		if(insertColSQL.length()>0){
 			insertColSQL.append(",");
 		}
 		insertColSQL.append(columnName);
 		
 		if(insertValueSQL.length()>0){
 			insertValueSQL.append(",");
 		}
 		insertValueSQL.append(":").append(columnName);
	}
}

    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(insertColSQL.toString());
    stringBuffer.append(TEXT_103);
    stringBuffer.append(insertValueSQL.toString());
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(operatorLoad);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(tdpid);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(operatorDataConnector);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(loadFile);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    //write script to file-------------------------------------------------
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_144);
    //run tbuild (TPT) command----------------------------------------------------
    if("true".equals(defineCharset)) {
	if(!("".equals(charset))) {
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append((("Windows").equals(execution))?"cmd /c ":"");
    stringBuffer.append(TEXT_147);
    stringBuffer.append(charset);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(scriptPath);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(errorFile);
    stringBuffer.append(TEXT_151);
    }} else {
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_153);
    stringBuffer.append((("Windows").equals(execution))?"cmd /c ":"");
    stringBuffer.append(TEXT_154);
    stringBuffer.append(scriptPath);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_156);
    stringBuffer.append(errorFile);
    stringBuffer.append(TEXT_157);
    }
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_171);
    
if(returnCodeDie) {

    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(returnCode);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    	
}

    stringBuffer.append(TEXT_176);
    return stringBuffer.toString();
  }
}
