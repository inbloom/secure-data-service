package org.talend.designer.codegen.translators.system;

import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.process.IConnectionCategory;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class TRunJobMainJava
{
  protected static String nl;
  public static synchronized TRunJobMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRunJobMainJava result = new TRunJobMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "java.util.List<String> paraList_";
  protected final String TEXT_2 = " = new java.util.ArrayList<String>();";
  protected final String TEXT_3 = NL + "\tif(childJob_commandLine_Mapper_";
  protected final String TEXT_4 = ".get(";
  protected final String TEXT_5 = ")==null){" + NL + "\t\tthrow new RuntimeException(\"The child job named \"+";
  protected final String TEXT_6 = "+\" is not in the job list.\");" + NL + "\t}" + NL + "\tparaList_";
  protected final String TEXT_7 = ".addAll(childJob_commandLine_Mapper_";
  protected final String TEXT_8 = ".get(";
  protected final String TEXT_9 = "));" + NL + "\t";
  protected final String TEXT_10 = NL + "\t        paraList_";
  protected final String TEXT_11 = ".add(";
  protected final String TEXT_12 = ");" + NL + "\t      ";
  protected final String TEXT_13 = NL + "\t        paraList_";
  protected final String TEXT_14 = ".add(\"";
  protected final String TEXT_15 = "\");" + NL + "\t      ";
  protected final String TEXT_16 = NL + "\t  String osName_";
  protected final String TEXT_17 = " = System.getProperty(\"os.name\");" + NL + "\t  if (osName_";
  protected final String TEXT_18 = " != null && osName_";
  protected final String TEXT_19 = ".toLowerCase().startsWith(\"win\")){" + NL + "\t      ";
  protected final String TEXT_20 = NL + "\t\t      paraList_";
  protected final String TEXT_21 = ".add(\"";
  protected final String TEXT_22 = "\");" + NL + "\t\t      ";
  protected final String TEXT_23 = NL + "\t\t        paraList_";
  protected final String TEXT_24 = ".add(";
  protected final String TEXT_25 = ");" + NL + "\t\t      ";
  protected final String TEXT_26 = NL + "\t\t        paraList_";
  protected final String TEXT_27 = ".add(\"";
  protected final String TEXT_28 = "\");" + NL + "\t\t      ";
  protected final String TEXT_29 = NL + "\t  } else {" + NL + "\t      ";
  protected final String TEXT_30 = NL + "\t\t      paraList_";
  protected final String TEXT_31 = ".add(\"";
  protected final String TEXT_32 = "\");" + NL + "\t\t      ";
  protected final String TEXT_33 = NL + "\t\t        paraList_";
  protected final String TEXT_34 = ".add(";
  protected final String TEXT_35 = ".replace(\"$ROOT_PATH\",System.getProperty(\"user.dir\")));" + NL + "\t\t      ";
  protected final String TEXT_36 = NL + "\t\t        paraList_";
  protected final String TEXT_37 = ".add(";
  protected final String TEXT_38 = ");" + NL + "\t\t      ";
  protected final String TEXT_39 = NL + "\t  }    " + NL + "\t  ";
  protected final String TEXT_40 = NL + "//for feature:10589";
  protected final String TEXT_41 = NL + "paraList_";
  protected final String TEXT_42 = ".add(\"--stat_port=\" + null);";
  protected final String TEXT_43 = NL + "paraList_";
  protected final String TEXT_44 = ".add(\"--stat_port=\" + portStats);";
  protected final String TEXT_45 = NL + NL + "if(resuming_logs_dir_path != null){" + NL + "  paraList_";
  protected final String TEXT_46 = ".add(\"--resuming_logs_dir_path=\" + resuming_logs_dir_path);" + NL + "}" + NL + "String childResumePath_";
  protected final String TEXT_47 = " = ResumeUtil.getChildJobCheckPointPath(resuming_checkpoint_path);" + NL + "String tRunJobName_";
  protected final String TEXT_48 = " = ResumeUtil.getRighttRunJob(resuming_checkpoint_path);" + NL + "if(\"";
  protected final String TEXT_49 = "\".equals(tRunJobName_";
  protected final String TEXT_50 = ") && childResumePath_";
  protected final String TEXT_51 = " != null){" + NL + "  paraList_";
  protected final String TEXT_52 = ".add(\"--resuming_checkpoint_path=\" + ResumeUtil.getChildJobCheckPointPath(resuming_checkpoint_path));" + NL + "}" + NL + "paraList_";
  protected final String TEXT_53 = ".add(\"--parent_part_launcher=JOB:\" + jobName + \"/NODE:";
  protected final String TEXT_54 = "\");" + NL + "" + NL + "java.util.Map<String, Object> parentContextMap_";
  protected final String TEXT_55 = " = new java.util.HashMap<String, Object>();" + NL;
  protected final String TEXT_56 = " ";
  protected final String TEXT_57 = NL + "  ";
  protected final String TEXT_58 = ".synchronizeContext();" + NL + "  java.util.Enumeration<?> propertyNames_";
  protected final String TEXT_59 = " = ";
  protected final String TEXT_60 = ".propertyNames();" + NL + "  while (propertyNames_";
  protected final String TEXT_61 = ".hasMoreElements()) {" + NL + "    String key_";
  protected final String TEXT_62 = " = (String) propertyNames_";
  protected final String TEXT_63 = ".nextElement();" + NL + "    Object value_";
  protected final String TEXT_64 = " = (Object) ";
  protected final String TEXT_65 = ".get(key_";
  protected final String TEXT_66 = ");       " + NL + "    paraList_";
  protected final String TEXT_67 = ".add(\"--context_param \" + key_";
  protected final String TEXT_68 = " + \"=\" + value_";
  protected final String TEXT_69 = ");" + NL + "  }" + NL + "  ";
  protected final String TEXT_70 = "  " + NL + "\t\t    parentContextMap_";
  protected final String TEXT_71 = ".put(\"";
  protected final String TEXT_72 = "\", ";
  protected final String TEXT_73 = ".";
  protected final String TEXT_74 = ");";
  protected final String TEXT_75 = NL + NL + "Object obj_";
  protected final String TEXT_76 = " = null;" + NL;
  protected final String TEXT_77 = NL + "  obj_";
  protected final String TEXT_78 = " = ";
  protected final String TEXT_79 = ";" + NL + "  paraList_";
  protected final String TEXT_80 = ".add(\"--context_param ";
  protected final String TEXT_81 = "=\" + RuntimeUtils.tRunJobConvertContext(obj_";
  protected final String TEXT_82 = "));" + NL + "  parentContextMap_";
  protected final String TEXT_83 = ".put(\"";
  protected final String TEXT_84 = "\", obj_";
  protected final String TEXT_85 = ");";
  protected final String TEXT_86 = NL + "  System.out.println(\"";
  protected final String TEXT_87 = " in ";
  protected final String TEXT_88 = " call ";
  protected final String TEXT_89 = "\"+";
  protected final String TEXT_90 = "+\"";
  protected final String TEXT_91 = " with:\\n\\n\" + paraList_";
  protected final String TEXT_92 = " + \"\\n\");";
  protected final String TEXT_93 = NL + "  ";
  protected final String TEXT_94 = " childJob_";
  protected final String TEXT_95 = " = new ";
  protected final String TEXT_96 = "();";
  protected final String TEXT_97 = "  " + NL + "  childJob_";
  protected final String TEXT_98 = ".parentContextMap = parentContextMap_";
  protected final String TEXT_99 = ";";
  protected final String TEXT_100 = "  " + NL + "  String[][] childReturn_";
  protected final String TEXT_101 = " = childJob_";
  protected final String TEXT_102 = ".runJob((String[]) paraList_";
  protected final String TEXT_103 = ".toArray(new String[paraList_";
  protected final String TEXT_104 = ".size()]));";
  protected final String TEXT_105 = NL + "      ((java.util.Map)threadLocal.get()).put(\"errorCode\", childJob_";
  protected final String TEXT_106 = ".getErrorCode());";
  protected final String TEXT_107 = NL + "      errorCode = childJob_";
  protected final String TEXT_108 = ".getErrorCode();";
  protected final String TEXT_109 = NL + "            " + NL + "    if(childJob_";
  protected final String TEXT_110 = ".getErrorCode() == null){" + NL + "      globalMap.put(\"";
  protected final String TEXT_111 = "_CHILD_RETURN_CODE\", childJob_";
  protected final String TEXT_112 = ".getStatus() != null && (\"failure\").equals(childJob_";
  protected final String TEXT_113 = ".getStatus()) ? 1 : 0);" + NL + "    }else{" + NL + "      globalMap.put(\"";
  protected final String TEXT_114 = "_CHILD_RETURN_CODE\", childJob_";
  protected final String TEXT_115 = ".getErrorCode());" + NL + "    }  " + NL + "    globalMap.put(\"";
  protected final String TEXT_116 = "_CHILD_EXCEPTION_STACKTRACE\", childJob_";
  protected final String TEXT_117 = ".getExceptionStackTrace());" + NL + "  ";
  protected final String TEXT_118 = " " + NL + "      if (childJob_";
  protected final String TEXT_119 = ".getErrorCode() != null || (\"failure\").equals(childJob_";
  protected final String TEXT_120 = ".getStatus())) {" + NL + "        throw new RuntimeException(\"Child job running failed\");" + NL + "      }";
  protected final String TEXT_121 = NL + "      for (String[] item_";
  protected final String TEXT_122 = " : childReturn_";
  protected final String TEXT_123 = ") { ";
  protected final String TEXT_124 = "\t\t" + NL + "        if(";
  protected final String TEXT_125 = " < item_";
  protected final String TEXT_126 = ".length){\t\t\t\t";
  protected final String TEXT_127 = NL + "             ";
  protected final String TEXT_128 = ".";
  protected final String TEXT_129 = " = item_";
  protected final String TEXT_130 = "[";
  protected final String TEXT_131 = "];";
  protected final String TEXT_132 = NL + "             ";
  protected final String TEXT_133 = ".";
  protected final String TEXT_134 = " = ParserUtils.parseTo_Date(item_";
  protected final String TEXT_135 = "[";
  protected final String TEXT_136 = "], ";
  protected final String TEXT_137 = ");";
  protected final String TEXT_138 = "\t\t\t\t\t\t\t";
  protected final String TEXT_139 = NL + "             ";
  protected final String TEXT_140 = ".";
  protected final String TEXT_141 = " = item_";
  protected final String TEXT_142 = "[";
  protected final String TEXT_143 = "].getBytes();";
  protected final String TEXT_144 = NL + "             ";
  protected final String TEXT_145 = ".";
  protected final String TEXT_146 = " = ParserUtils.parseTo_";
  protected final String TEXT_147 = "(item_";
  protected final String TEXT_148 = "[";
  protected final String TEXT_149 = "]);";
  protected final String TEXT_150 = NL + "        }else{" + NL + "  \t      ";
  protected final String TEXT_151 = ".";
  protected final String TEXT_152 = " = ";
  protected final String TEXT_153 = ";" + NL + "        }";
  protected final String TEXT_154 = NL + "      {";
  protected final String TEXT_155 = NL + "  Runtime runtime_";
  protected final String TEXT_156 = " = Runtime.getRuntime();" + NL + "  final Process ps_";
  protected final String TEXT_157 = ";" + NL + "  ps_";
  protected final String TEXT_158 = " = runtime_";
  protected final String TEXT_159 = ".exec((String[])paraList_";
  protected final String TEXT_160 = ".toArray(new String[paraList_";
  protected final String TEXT_161 = ".size()]));" + NL + "" + NL + "  Thread normal_";
  protected final String TEXT_162 = " = new Thread() {" + NL + "    public void run() {" + NL + "      try {" + NL + "        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(ps_";
  protected final String TEXT_163 = ".getInputStream()));" + NL + "        String line = \"\";" + NL + "        try {" + NL + "          while((line = reader.readLine()) != null) {" + NL + "            System.out.println(line);" + NL + "          }" + NL + "        } finally {" + NL + "          reader.close();" + NL + "        }" + NL + "      } catch(java.io.IOException ioe) {" + NL + "        ioe.printStackTrace();" + NL + "      }" + NL + "    }" + NL + "  };" + NL + "  normal_";
  protected final String TEXT_164 = ".start();" + NL + "" + NL + "  final StringBuffer errorMsg_";
  protected final String TEXT_165 = " = new StringBuffer();" + NL + "  Thread error_";
  protected final String TEXT_166 = " = new Thread() {" + NL + "    public void run() {" + NL + "      try {" + NL + "        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(ps_";
  protected final String TEXT_167 = ".getErrorStream()));" + NL + "        String line = \"\";" + NL + "        try {" + NL + "          while((line = reader.readLine()) != null) {" + NL + "            errorMsg_";
  protected final String TEXT_168 = ".append(line).append(\"\\n\");" + NL + "          }" + NL + "        } finally {" + NL + "          reader.close();" + NL + "        }" + NL + "      } catch(java.io.IOException ioe) {" + NL + "        ioe.printStackTrace();" + NL + "      }" + NL + "    }" + NL + "  };" + NL + "  error_";
  protected final String TEXT_169 = ".start();" + NL + "" + NL + "  //0 indicates normal termination\t" + NL + "  int result_";
  protected final String TEXT_170 = " = ps_";
  protected final String TEXT_171 = ".waitFor();" + NL + "  normal_";
  protected final String TEXT_172 = ".join(10000);" + NL + "  error_";
  protected final String TEXT_173 = ".join(10000);" + NL + "  " + NL + "  globalMap.put(\"";
  protected final String TEXT_174 = "_CHILD_RETURN_CODE\",result_";
  protected final String TEXT_175 = ");" + NL + "  if(result_";
  protected final String TEXT_176 = " != 0){" + NL + "   \t\tglobalMap.put(\"";
  protected final String TEXT_177 = "_CHILD_EXCEPTION_STACKTRACE\",errorMsg_";
  protected final String TEXT_178 = ".toString());" + NL + "\t";
  protected final String TEXT_179 = "  " + NL + "\t    throw new RuntimeException(\"Child job return \" + result_";
  protected final String TEXT_180 = " + \". It doesn't terminate normally.\\n\" + errorMsg_";
  protected final String TEXT_181 = ".toString());" + NL + "\t";
  protected final String TEXT_182 = NL + "\t\tSystem.err.println(\"Child job return \" + result_";
  protected final String TEXT_183 = " + \". It doesn't terminate normally.\\n\" + errorMsg_";
  protected final String TEXT_184 = ".toString());" + NL + "\t";
  protected final String TEXT_185 = NL + "  }" + NL + " " + NL + NL;
  protected final String TEXT_186 = NL + "      {" + NL + "      \tSystem.err.println(\"when tRunJob runs in an independent process, it can't extract datas from tBufferOutput of child job.\"); ";
  protected final String TEXT_187 = NL + "      {";
  protected final String TEXT_188 = NL + "  ";
  protected final String TEXT_189 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataColumn> columns = null;
List<IMetadataTable> metadatas = node.getMetadataList();
if (metadatas != null && metadatas.size() > 0) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata != null) {
        columns = metadata.getListColumns();
    }
}
List< ? extends IConnection> outConns = node.getOutgoingSortedConnections();
  
//if tRunJob is the first node, it can get the return values of the child job.
//if tRunJob is middle node of the FLOW link, it will be data_auto_propagate=true 
List< ? extends IConnection> inConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
boolean useIndependentProcess = "true".equals(ElementParameterParser.getValue(node, "__USE_INDEPENDENT_PROCESS__"));
boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_CHILD_ERROR__"));  
boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();  
boolean transmitWholeContext = ("true").equals(ElementParameterParser.getValue(node, "__TRANSMIT_WHOLE_CONTEXT__"));  
boolean printParameter = ("true").equals(ElementParameterParser.getValue(node, "__PRINT_PARAMETER__")); 

boolean originalContext = ("true").equals(ElementParameterParser.getValue(node, "__TRANSMIT_ORIGINAL_CONTEXT__")); 

IProcess currentProcess = node.getProcess();
List<Map<String, String>> contextParams = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__CONTEXTPARAMS__");
String process = ElementParameterParser.getValue(node,"__PROCESS_TYPE_PROCESS__");
String context = ElementParameterParser.getValue(node,"__PROCESS_TYPE_CONTEXT__");
String version = ElementParameterParser.getValue(node,"__PROCESS_TYPE_VERSION__");
String[] codeOptions = new String[] {"\"--father_pid=\"+pid", "\"--root_pid=\"+rootPid", "\"--father_node="+ cid + "\""};
String[] commandLineWindows = new String[] {"<command>"};
String[] commandLineUnix = new String[] {"<command>"};
String[] commandLine = new String[] {"<command>"};
String childJob = ElementParameterParser.getValue(node,"__PROCESS__");

boolean useDynamicJob = ("true").equals(ElementParameterParser.getValue(node, "__USE_DYNAMIC_JOB__"));
String dynamicJobName = ElementParameterParser.getValue(node,"__CONTEXT_JOB__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    
if(useDynamicJob){
	useIndependentProcess = true;
	
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(dynamicJobName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(dynamicJobName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dynamicJobName);
    stringBuffer.append(TEXT_9);
    
}else{
	try {
	  if(useIndependentProcess){
	    commandLineWindows = ProcessorUtilities.getCommandLine("win32",true, process, context,org.talend.designer.runprocess.IProcessor.NO_STATISTICS,org.talend.designer.runprocess.IProcessor.NO_TRACES, codeOptions);
	    // remove the 2 fist lines
	    if (commandLineWindows.length > 0 && ProcessorUtilities.isExportConfig()){
	      int tmpSize = commandLineWindows.length - 2;
	      String[] tmp = new String[tmpSize];
	      System.arraycopy(commandLineWindows, 2, tmp, 0, tmpSize);
	      commandLineWindows = tmp;
	    }
	    commandLineUnix = ProcessorUtilities.getCommandLine("linux",true, process, context,org.talend.designer.runprocess.IProcessor.NO_STATISTICS,org.talend.designer.runprocess.IProcessor.NO_TRACES, codeOptions);
	    // remove the 2 first lines
	    if (commandLineUnix.length > 0 && ProcessorUtilities.isExportConfig()){
	      int tmpSize = commandLineUnix.length - 2;
	      String[] tmp = new String[tmpSize];
	      System.arraycopy(commandLineUnix, 2, tmp, 0, tmpSize);
	      commandLineUnix = tmp;
	    }
	  }else{
	    commandLine = ProcessorUtilities.getMainCommand(process,version, context,org.talend.designer.runprocess.IProcessor.NO_STATISTICS,org.talend.designer.runprocess.IProcessor.NO_TRACES, codeOptions);  
	  }
	} catch (ProcessorException e) {
	}


	if (!useIndependentProcess) {
	  for (int i = 0; i < commandLine.length; i++) {
		if (i == 0){
	      childJob = commandLine[0];
	    } else if (i > 0){
	      if (commandLine[i].indexOf("\"") >= 0){
	      
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(commandLine[i] );
    stringBuffer.append(TEXT_12);
    }else{
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(commandLine[i] );
    stringBuffer.append(TEXT_15);
    }
	    }
	  }
	} else {
	  
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    
		  for (int i = 0; i < commandLineWindows.length; i++) {
		    if (i == 0){
		      
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(commandLineWindows[i]);
    stringBuffer.append(TEXT_22);
    
		    } else if (i > 0){
		      if (commandLineWindows[i].indexOf("\"") >= 0){
		      
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(commandLineWindows[i] );
    stringBuffer.append(TEXT_25);
    }else{
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(commandLineWindows[i] );
    stringBuffer.append(TEXT_28);
    }
		    }
		  }
		  
    stringBuffer.append(TEXT_29);
    
		  for (int i = 0; i < commandLineUnix.length; i++) {
		    if (i == 0){
		      
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(commandLineUnix[i]);
    stringBuffer.append(TEXT_32);
    
		    } else if (i > 0){
		      String param;
		      if (commandLineUnix[i].indexOf("\"") >= 0){
		        param = commandLineUnix[i];
		      } else {
		      	param = "\""+commandLineUnix[i]+"\"";
		      }
		      if (param.contains("$ROOT_PATH")) {
		      
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(param );
    stringBuffer.append(TEXT_35);
    }else{
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(param );
    stringBuffer.append(TEXT_38);
    }
		    }
		  }
		  
    stringBuffer.append(TEXT_39);
    
	}
}

    stringBuffer.append(TEXT_40);
    if(useIndependentProcess){
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    }else{
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    }
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    
Set<IConnection> iterateConnSet =  new HashSet<IConnection>();
List<? extends IConnection> iterateConns = node.getIncomingConnections(EConnectionType.ITERATE);
if(iterateConns != null)  { 
	iterateConnSet.addAll(iterateConns);
}
boolean parallelIterate = false;
for (IConnection iterateConn : iterateConnSet) {
	parallelIterate = "true".equals(ElementParameterParser.getValue(iterateConn, "__ENABLE_PARALLEL__"));
}
if(transmitWholeContext){//111111

     
	//bug21906
	String localContext = "context";
	if(parallelIterate) {
		localContext = "localContext";
	}

    stringBuffer.append(TEXT_56);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(localContext);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(localContext);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(localContext);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    
 List<IContextParameter> params = currentProcess.getContextManager().getDefaultContext().getContextParameterList();
    for (IContextParameter ctxParam :params){
    	String ctxParamName = ctxParam.getName();
 
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(ctxParamName );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(localContext);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(ctxParamName );
    stringBuffer.append(TEXT_74);
    }

}//111111

    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    
for (int i=0; i<contextParams.size(); i++) {
  Map<String, String> contextParam = contextParams.get(i);
  String name = contextParam.get("PARAM_NAME_COLUMN");
  String value = contextParam.get("PARAM_VALUE_COLUMN");
  //bug22181
  if(parallelIterate && value!=null && value.contains("context.")) {
  	value = value.replace("context.","localContext.");
  }

    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(value );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(name );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(name );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    }
    if(printParameter){
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(currentProcess.getName() );
    stringBuffer.append(TEXT_88);
    if(!useDynamicJob){
    stringBuffer.append(childJob );
    }else{
    stringBuffer.append(TEXT_89);
    stringBuffer.append(dynamicJobName);
    stringBuffer.append(TEXT_90);
    }
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
if (!useIndependentProcess){//AAAAAAAAAAAA

    stringBuffer.append(TEXT_93);
    stringBuffer.append(childJob );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(childJob );
    stringBuffer.append(TEXT_96);
    if(originalContext){
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    }
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    
  if (childJob != null) {
    if (isRunInMultiThread ){
    
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    }else {
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    }
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    if (dieOnError) { 
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_120);
    }
  }
    
  String firstConnName = null; 
  if(outConns != null && outConns.size() > 0) {
    for (IConnection conn : outConns) {
      if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
      	firstConnName = conn.getName(); //get the first available flow link
      	break;
      }
    }
  }
  if(firstConnName != null) {//b
    if(inConns == null || inConns.size() == 0){//c
    
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    
      int columnSize = columns.size();
      for (int i = 0; i < columnSize; i++) {//a
        IMetadataColumn column = columns.get(i);
        String label = column.getLabel();
        String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
        JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
        String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
        
    stringBuffer.append(TEXT_124);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
    stringBuffer.append(TEXT_127);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_131);
    } else if(javaType == JavaTypesManager.DATE) {
    stringBuffer.append(TEXT_132);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_136);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_137);
    } else if(javaType == JavaTypesManager.BYTE_ARRAY){
    stringBuffer.append(TEXT_138);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_143);
    } else {
    stringBuffer.append(TEXT_144);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_146);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_149);
    }
    stringBuffer.append(TEXT_150);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate));
    stringBuffer.append(TEXT_153);
    
      }//a
    }else{//c
    //in order to match the "}" about IS_MULTIPLYING_OUTPUTS="true"
    
    stringBuffer.append(TEXT_154);
    
    }
    
  }//b  
  
} else { //AAAAAAAAAAAA

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
  // use independent process to run subjob

    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    if (dieOnError) { 
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    }else{
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_184);
    }
    stringBuffer.append(TEXT_185);
    
  String firstConnName = null; 
  if(outConns != null && outConns.size() > 0) {
    for (IConnection conn : outConns) {
      if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
      	firstConnName = conn.getName(); //get the first available flow link
      	break;
      }
    }
  }
  if(firstConnName != null) {//b
    if(inConns == null || inConns.size() == 0){//c
    
    stringBuffer.append(TEXT_186);
    }else{//c
    //in order to match the "}" about IS_MULTIPLYING_OUTPUTS="true"
    
    stringBuffer.append(TEXT_187);
    
    }//c
  }//b  

    stringBuffer.append(TEXT_188);
    
}//AAAAAAAAAAAA

    stringBuffer.append(TEXT_189);
    return stringBuffer.toString();
  }
}
