package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.INode;

import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.CorePlugin;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.utils.JavaResourcesHelper;
import org.talend.core.model.process.ElementParameterParser;

public class FooterJava
{
  protected static String nl;
  public static synchronized FooterJava create(String lineSeparator)
  {
    nl = lineSeparator;
    FooterJava result = new FooterJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tpublic String resuming_logs_dir_path = null;" + NL + "\tpublic String resuming_checkpoint_path = null;" + NL + "\tpublic String parent_part_launcher = null;" + NL + "\tprivate String resumeEntryMethodName = null;" + NL + "\tprivate boolean globalResumeTicket = false;\t " + NL + "\t    " + NL + "    public boolean watch = false;\t  " + NL + "\t// portStats is null, it means don't execute the statistics" + NL + "\tpublic Integer portStats = null;" + NL + "    public int portTraces = 4334;" + NL + "    public String clientHost;" + NL + "    public String defaultClientHost = \"localhost\";" + NL + "    public String contextStr = \"";
  protected final String TEXT_2 = "\";" + NL + "    public boolean isDefaultContext = true;" + NL + "    public String pid = \"0\";" + NL + "    public String rootPid = null;" + NL + "    public String fatherPid = null;" + NL + "    public String fatherNode = null;" + NL + "    public long startTime = 0;" + NL + "    public boolean isChildJob = false;" + NL + "    " + NL + "    private boolean execStat = true;" + NL + "    " + NL + "    private ThreadLocal threadLocal = new ThreadLocal();" + NL + "\t{" + NL + "\t\tjava.util.Map threadRunResultMap = new java.util.HashMap();" + NL + "\t\tthreadRunResultMap.put(\"errorCode\", null);" + NL + "\t\tthreadRunResultMap.put(\"status\", \"\");" + NL + "\t\tthreadLocal.set(threadRunResultMap);" + NL + "\t}    ";
  protected final String TEXT_3 = "  " + NL + "\tprivate SyncInt runningThreadCount =new SyncInt();" + NL + "\t" + NL + "\tprivate class SyncInt" + NL + "\t{" + NL + "\t\tprivate int count = 0;" + NL + "\t    public synchronized void add(int i)" + NL + "\t    {" + NL + "\t        count +=i;" + NL + "\t    }" + NL + "\t    " + NL + "\t    public synchronized int getCount()" + NL + "\t    {" + NL + "\t        return count;" + NL + "\t    }" + NL + "\t}";
  protected final String TEXT_4 = NL + "  " + NL + "    private java.util.Properties context_param = new java.util.Properties();" + NL + "    public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();" + NL + "    " + NL + "    public String status= \"\";" + NL + "    " + NL + "    public static void main(String[] args){" + NL + "    \tfinal ";
  protected final String TEXT_5 = " ";
  protected final String TEXT_6 = "Class = new ";
  protected final String TEXT_7 = "();" + NL + "    " + NL + "        int exitCode = ";
  protected final String TEXT_8 = "Class.runJobInTOS(args);" + NL + "         " + NL + "        System.exit(exitCode);" + NL + "    }    " + NL + "   " + NL + " " + NL + "    public String[][] runJob(String[] args) {  " + NL + "    " + NL + "\t\tint exitCode = runJobInTOS(args);";
  protected final String TEXT_9 = "  \t\t\t\t" + NL + "\t\tString[][] bufferValue = (String[][])globalBuffer.toArray(new String[globalBuffer.size()][]);\t\t" + NL + "\t";
  protected final String TEXT_10 = NL + "\t\tString[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };" + NL + "\t";
  protected final String TEXT_11 = NL + "\t\t\t" + NL + "\t\treturn bufferValue;" + NL + "    }" + NL + "    " + NL + "    " + NL + "    public int runJobInTOS(String[] args) {" + NL + "    \t" + NL + "        String lastStr = \"\";" + NL + "        for (String arg : args) {" + NL + "        \tif (arg.equalsIgnoreCase(\"--context_param\")) {" + NL + "                lastStr = arg;" + NL + "            } else if (lastStr.equals(\"\")) {" + NL + "                evalParam(arg);" + NL + "            } else {" + NL + "                evalParam(lastStr + \" \" + arg);" + NL + "                lastStr = \"\";" + NL + "            }" + NL + "        }" + NL + "    \t" + NL + "    \tif(clientHost == null) {" + NL + "    \t\tclientHost = defaultClientHost;" + NL + "    \t}" + NL + "    \t" + NL + "    \tif(pid == null || \"0\".equals(pid)) {" + NL + "\t    \tpid = TalendString.getAsciiRandomString(6);" + NL + "\t    }" + NL + "" + NL + "    \tif (rootPid==null) {" + NL + "    \t\trootPid = pid;" + NL + "    \t}" + NL + "    \tif (fatherPid==null) {    \t \t" + NL + "    \t\tfatherPid = pid;    \t\t" + NL + "    \t}else{" + NL + "    \t\tisChildJob = true;" + NL + "    \t}" + NL;
  protected final String TEXT_12 = "    \t" + NL + "        if (portStats != null) {" + NL + "            // portStats = -1; //for testing" + NL + "            if (portStats < 0 || portStats > 65535) {" + NL + "                // issue:10869, the portStats is invalid, so this client socket can't open" + NL + "                System.err.println(\"The statistics socket port \" + portStats + \" is invalid.\");" + NL + "                execStat = false;" + NL + "            }" + NL + "\t\t}else{" + NL + "\t\t\texecStat = false;" + NL + "\t\t}            \t";
  protected final String TEXT_13 = "    \t" + NL + "    \t" + NL + "    \ttry {" + NL + "    \t\t//call job/subjob with an existing context, like: --context=production. if without this parameter, there will use the default context instead.  " + NL + "    \t\tjava.io.InputStream inContext = ";
  protected final String TEXT_14 = ".class.getClassLoader().getResourceAsStream(\"";
  protected final String TEXT_15 = "/";
  protected final String TEXT_16 = "/contexts/\"+contextStr+\".properties\");" + NL + "    \t\tif(isDefaultContext && inContext ==null) {" + NL + "                " + NL + "            } else {    \t\t" + NL + "    \t\tif (inContext!=null) {" + NL + "\t    \t\t//defaultProps is in order to keep the original context value" + NL + "\t    \t\tdefaultProps.load(inContext);" + NL + "    \t\t\tinContext.close();" + NL + "\t    \t\tcontext = new ContextProperties(defaultProps);" + NL + "\t    \t}else{" + NL + "\t    \t\t//print info and job continue to run, for case: context_param is not empty." + NL + "\t    \t\tSystem.err.println(\"Could not find the context \" + contextStr);" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\tif(!context_param.isEmpty()) {\t\t\t\t\t\t" + NL + "\t\t\t    context.putAll(context_param);\t\t" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_17 = NL + "\t\t\ttry{" + NL + "\t\t\t\tString context_";
  protected final String TEXT_18 = "_value = context.getProperty(\"";
  protected final String TEXT_19 = "\");" + NL + "\t\t\t\tif (context_";
  protected final String TEXT_20 = "_value == null){" + NL + "\t\t\t\t\tcontext_";
  protected final String TEXT_21 = "_value = \"\";" + NL + "\t\t\t\t}" + NL + "\t\t\t\tint context_";
  protected final String TEXT_22 = "_pos = context_";
  protected final String TEXT_23 = "_value.indexOf(\";\");" + NL + "\t\t\t\tString context_";
  protected final String TEXT_24 = "_pattern =  \"yyyy-MM-dd HH:mm:ss\";" + NL + "\t\t\t\tif(context_";
  protected final String TEXT_25 = "_pos > -1){" + NL + "\t\t\t\t\tcontext_";
  protected final String TEXT_26 = "_pattern = context_";
  protected final String TEXT_27 = "_value.substring(0, context_";
  protected final String TEXT_28 = "_pos);" + NL + "\t\t\t\t\tcontext_";
  protected final String TEXT_29 = "_value = context_";
  protected final String TEXT_30 = "_value.substring(context_";
  protected final String TEXT_31 = "_pos + 1);" + NL + "\t\t\t\t}" + NL + "\t\t\t\t" + NL + "\t\t\t    context.";
  protected final String TEXT_32 = "=(java.util.Date)(new java.text.SimpleDateFormat(context_";
  protected final String TEXT_33 = "_pattern).parse(context_";
  protected final String TEXT_34 = "_value));" + NL + "\t\t\t   " + NL + "\t\t\t}catch(ParseException e)" + NL + "\t\t\t{" + NL + "\t\t\t    context.";
  protected final String TEXT_35 = "=null;" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_36 = " " + NL + "\t\t\t  try{" + NL + "\t\t\t      context.";
  protected final String TEXT_37 = "=Integer.parseInt(context.getProperty(\"";
  protected final String TEXT_38 = "\"));" + NL + "\t\t\t  }catch(NumberFormatException e){" + NL + "\t\t\t      context.";
  protected final String TEXT_39 = "=null;" + NL + "\t\t\t  }" + NL + "\t\t\t  ";
  protected final String TEXT_40 = NL + "\t\t\t    context.";
  protected final String TEXT_41 = "=(";
  protected final String TEXT_42 = ") context.getProperty(\"";
  protected final String TEXT_43 = "\");" + NL + "\t\t\t    ";
  protected final String TEXT_44 = NL + "\t\t\t context.";
  protected final String TEXT_45 = "= new java.text.StringCharacterIterator(context.getProperty(\"";
  protected final String TEXT_46 = "\")).first();\t\t\t " + NL + "\t\t\t ";
  protected final String TEXT_47 = NL + "         try{" + NL + "         \tcontext.";
  protected final String TEXT_48 = "= new ";
  protected final String TEXT_49 = "(context.getProperty(\"";
  protected final String TEXT_50 = "\"));" + NL + "\t\t }catch(NumberFormatException e){" + NL + "\t\t    context.";
  protected final String TEXT_51 = "=null;" + NL + "\t\t }";
  protected final String TEXT_52 = " " + NL + "\t\t\t try{" + NL + "\t\t\t     context.";
  protected final String TEXT_53 = "=";
  protected final String TEXT_54 = ".parse";
  protected final String TEXT_55 = " (context.getProperty(\"";
  protected final String TEXT_56 = "\"));" + NL + "\t\t\t }catch(NumberFormatException e){" + NL + "\t\t\t     context.";
  protected final String TEXT_57 = "=null;" + NL + "\t\t\t  }" + NL + "\t\t\t ";
  protected final String TEXT_58 = NL + "\t\t\t}" + NL + "    \t} catch (java.io.IOException ie) {" + NL + "    \t\tSystem.err.println(\"Could not load context \"+contextStr);" + NL + "    \t\tie.printStackTrace();" + NL + "    \t}" + NL + "    \t" + NL + "    \t" + NL + "\t\t// get context value from parent directly" + NL + "\t\tif (parentContextMap != null && !parentContextMap.isEmpty()) {" + NL + "\t\t";
  protected final String TEXT_59 = "if (parentContextMap.containsKey(\"";
  protected final String TEXT_60 = "\")) {" + NL + "\t\t\t\tcontext.";
  protected final String TEXT_61 = " = (";
  protected final String TEXT_62 = ") parentContextMap.get(\"";
  protected final String TEXT_63 = "\");" + NL + "\t\t\t}";
  protected final String TEXT_64 = NL + "\t\t}    \t" + NL + "" + NL + "    \t//Resume: init the resumeUtil" + NL + "    \tresumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);" + NL + "    \tresumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);" + NL + "\t\tresumeUtil.initCommonInfo(pid, rootPid, fatherPid, projectName, jobName, contextStr, jobVersion);" + NL + "    \t" + NL + "    \t//Resume: jobStart" + NL + "    \tresumeUtil.addLog(\"JOB_STARTED\", \"JOB:\" + jobName, parent_part_launcher, Thread.currentThread().getId() + \"\", \"\",\"\",\"\",\"\",resumeUtil.convertToJsonText(context));     \t" + NL;
  protected final String TEXT_65 = NL + "if(execStat){\t" + NL + "\ttry {" + NL + "\t\trunStat.openSocket(!isChildJob);" + NL + "\t\trunStat.setAllPID(rootPid, fatherPid, pid, jobName);" + NL + "\t\trunStat.startThreadStat(clientHost, portStats);\t\t" + NL + "\t\trunStat.updateStatOnJob(RunStat.JOBSTART, fatherNode);\t" + NL + "\t} catch (java.io.IOException ioException) {" + NL + "\t\tioException.printStackTrace();" + NL + "\t}" + NL + "}\t";
  protected final String TEXT_66 = NL;
  protected final String TEXT_67 = NL + "\t\ttry {" + NL + "\t\trunTrace.openSocket(!isChildJob);" + NL + "\t\trunTrace.startThreadTrace(clientHost, portTraces);" + NL + "\t} catch (java.io.IOException ioException) {" + NL + "\t\tioException.printStackTrace();" + NL + "\t}";
  protected final String TEXT_68 = NL;
  protected final String TEXT_69 = NL;
  protected final String TEXT_70 = NL + NL + NL + NL + "\tlong startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();" + NL + "\tlong endUsedMemory = 0;" + NL + "\tlong end = 0;" + NL + "\t" + NL + "\tstartTime = System.currentTimeMillis();";
  protected final String TEXT_71 = NL + "\t\t";
  protected final String TEXT_72 = ".addMessage(\"begin\");\t\t\t\t\t";
  protected final String TEXT_73 = NL;
  protected final String TEXT_74 = NL;
  protected final String TEXT_75 = NL + NL + "this.globalResumeTicket = true;//to run tPreJob" + NL;
  protected final String TEXT_76 = NL;
  protected final String TEXT_77 = NL + NL + "    \t";
  protected final String TEXT_78 = NL + "\t\ttry {" + NL + "\t\t\t";
  protected final String TEXT_79 = "Process(globalMap);" + NL + "\t\t} catch (Exception e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t}\t\t";
  protected final String TEXT_80 = NL + NL + "this.globalResumeTicket = false;//to run others jobs" + NL;
  protected final String TEXT_81 = NL + "\t\trunningThreadCount.add(1);" + NL + "   \t\tnew Thread(){" + NL + "\t    \tpublic void run()" + NL + "\t   \t\t{" + NL + "                java.util.Map threadRunResultMap = new java.util.HashMap();" + NL + "                threadRunResultMap.put(\"errorCode\", null);" + NL + "                threadRunResultMap.put(\"status\", \"\");" + NL + "                threadLocal.set(threadRunResultMap);" + NL + "\t    \t    " + NL + "\t\t\t\t";
  protected final String TEXT_82 = NL + "\t        \t" + NL + "                Integer localErrorCode = (Integer)(((java.util.Map)threadLocal.get()).get(\"errorCode\"));" + NL + "                String localStatus = (String)(((java.util.Map)threadLocal.get()).get(\"status\"));" + NL + "                if (localErrorCode != null) {" + NL + "                    if (errorCode == null || localErrorCode.compareTo(errorCode) > 0) {" + NL + "                       errorCode = localErrorCode;" + NL + "                    }" + NL + "                }" + NL + "                if(!status.equals(\"failure\")){" + NL + "                        status = localStatus;" + NL + "                }\t" + NL + "         \t    " + NL + "                runningThreadCount.add(-1);\t        \t" + NL + "\t    \t}" + NL + "\t\t}.start();" + NL + "\t\t";
  protected final String TEXT_83 = NL + "\twhile(runningThreadCount.getCount()>0)" + NL + "    {" + NL + "        try {" + NL + "            Thread.sleep(10);" + NL + "        } catch (Exception e) {" + NL + "            e.printStackTrace();" + NL + "        }            " + NL + "    }";
  protected final String TEXT_84 = NL;
  protected final String TEXT_85 = NL + NL + "this.globalResumeTicket = true;//to run tPostJob" + NL;
  protected final String TEXT_86 = NL;
  protected final String TEXT_87 = NL;
  protected final String TEXT_88 = NL;
  protected final String TEXT_89 = NL + NL + "\t" + NL + "\t\tend = System.currentTimeMillis();" + NL + "\t\t" + NL + "\t\tif (watch) {" + NL + "    \t\tSystem.out.println((end-startTime)+\" milliseconds\");" + NL + "\t    }" + NL + "\t" + NL + "\t\tendUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();" + NL + "\t\tif (false) {" + NL + "    \t\tSystem.out.println((endUsedMemory - startUsedMemory) + \" bytes memory increase when running : ";
  protected final String TEXT_90 = "\");" + NL + "\t    }\t\t  ";
  protected final String TEXT_91 = NL + "\t\t";
  protected final String TEXT_92 = ".addMessage(status==\"\"?\"end\":status, (end-startTime));" + NL + "\t\ttry {" + NL + "\t\t\t";
  protected final String TEXT_93 = "Process(globalMap);" + NL + "\t\t} catch (Exception e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t}";
  protected final String TEXT_94 = NL;
  protected final String TEXT_95 = NL + "if (execStat) {" + NL + "\trunStat.updateStatOnJob(RunStat.JOBEND, fatherNode);" + NL + "\trunStat.stopThreadStat();" + NL + "}\t";
  protected final String TEXT_96 = NL + "\t\trunTrace.stopThreadTrace();";
  protected final String TEXT_97 = NL + "\tint returnCode = 0;\t" + NL + "    if(errorCode == null) {" + NL + "         returnCode = status != null && status.equals(\"failure\") ? 1 : 0;\t" + NL + "    } else {   " + NL + "         returnCode = errorCode.intValue();" + NL + "    }" + NL + "\tresumeUtil.addLog(\"JOB_ENDED\", \"JOB:\" + jobName, parent_part_launcher, Thread.currentThread().getId() + \"\", \"\",\"\" + returnCode,\"\",\"\",\"\");" + NL + "  " + NL + "    return returnCode;" + NL + "" + NL + "  }" + NL + "\t" + NL + "\tprivate void evalParam(String arg) {" + NL + "\t\tif (arg.startsWith(\"--resuming_logs_dir_path\")) {" + NL + "\t\t\tresuming_logs_dir_path = arg.substring(25);" + NL + "\t\t} else if (arg.startsWith(\"--resuming_checkpoint_path\")) {" + NL + "\t\t\tresuming_checkpoint_path = arg.substring(27);" + NL + "\t\t} else if (arg.startsWith(\"--parent_part_launcher\")) {" + NL + "\t\t\tparent_part_launcher = arg.substring(23);" + NL + "\t\t} else if (arg.startsWith(\"--watch\")) {" + NL + "    \t\twatch = true;" + NL + "    \t} else if (arg.startsWith(\"--stat_port=\")) {" + NL + "\t\t\tString portStatsStr = arg.substring(12);" + NL + "\t\t\tif (portStatsStr != null && !portStatsStr.equals(\"null\")) {" + NL + "\t\t\t\tportStats = Integer.parseInt(portStatsStr);" + NL + "\t\t\t}    \t\t" + NL + "    \t} else if (arg.startsWith(\"--trace_port=\")) {" + NL + "    \t\tportTraces = Integer.parseInt(arg.substring(13));" + NL + "    \t} else if (arg.startsWith(\"--client_host=\")) {" + NL + "    \t\tclientHost = arg.substring(14);" + NL + "    \t} else if (arg.startsWith(\"--context=\")) {" + NL + "    \t\tcontextStr = arg.substring(10);" + NL + "    \t\tisDefaultContext = false;" + NL + "    \t} else if (arg.startsWith(\"--father_pid=\")) {" + NL + "    \t\tfatherPid = arg.substring(13);" + NL + "    \t} else if (arg.startsWith(\"--root_pid=\")) {" + NL + "    \t\trootPid = arg.substring(11);" + NL + "    \t} else if (arg.startsWith(\"--father_node=\")) {" + NL + "    \t\tfatherNode = arg.substring(14);" + NL + "    \t} else if (arg.startsWith(\"--pid=\")) {" + NL + "    \t\tpid = arg.substring(6);" + NL + "    \t} else if (arg.startsWith(\"--context_param\")) {   \t\t " + NL + "            String keyValue = arg.substring(16);           " + NL + "            int index = -1;" + NL + "            if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {" + NL + "                context_param.put(keyValue.substring(0, index), replaceEscapeChars(keyValue.substring(index + 1)));" + NL + "            }" + NL + "    \t}" + NL + "    \t" + NL + "\t}\t" + NL + "" + NL + "\tprivate final String[][] escapeChars = {" + NL + "\t\t{\"\\\\n\",\"\\n\"},{\"\\\\'\",\"\\'\"},{\"\\\\r\",\"\\r\"}," + NL + "\t\t{\"\\\\f\",\"\\f\"},{\"\\\\b\",\"\\b\"},{\"\\\\t\",\"\\t\"}," + NL + "\t\t{\"\\\\\\\\\",\"\\\\\"}" + NL + "\t\t}; " + NL + "\tprivate String replaceEscapeChars (String keyValue) {" + NL + "\t\tif(keyValue==null || (\"\").equals(keyValue.trim())) {" + NL + "\t\t\treturn keyValue;" + NL + "\t\t}" + NL + "\t\tfor(String[] strArray: escapeChars) {" + NL + "\t\t\tkeyValue = keyValue.replace(strArray[0], strArray[1]);" + NL + "\t\t}" + NL + "\t\treturn keyValue;" + NL + "\t}" + NL + "" + NL + "\tpublic Integer getErrorCode() {" + NL + "\t\treturn errorCode;" + NL + "\t}" + NL + "" + NL + "" + NL + "\tpublic String getStatus() {" + NL + "\t\treturn status;" + NL + "\t}" + NL + "\t" + NL + "\tResumeUtil resumeUtil = null;\t\t\t" + NL + "}";

    private static String end_multiThread = "((java.util.Map) threadLocal.get()).put(\"status\", \"end\");";
    private static String end_singleThread = "if(!\"failure\".equals(status)) { status = \"end\"; }";
    private static String failure_multiThread = "((java.util.Map) threadLocal.get()).put(\"status\", \"failure\");";
    private static String failure_singleThread = "status = \"failure\";";
    private static String errorCode_multiThread = "((java.util.Map) threadLocal.get()).put(\"errorCode\", null);";
    private static String errorCode_singleThread = "errorCode = null;";
    
    // add the list of the connection names to avoid to declare two times the same name.
    public String createCallProcess(INode rootNode, String className, boolean isMultiThread) {
        String toReturn = "";
        toReturn =  "try {\n";
        if(isMultiThread) {
            toReturn +=  errorCode_multiThread;
        }else{
            toReturn +=  errorCode_singleThread;
        }        
        
        toReturn += rootNode.getUniqueName() + "Process(globalMap);\n";
        
        if(isMultiThread) {
            toReturn +=  end_multiThread;
        }else{
            toReturn +=  end_singleThread;
        }
        
        toReturn += "\n}catch (TalendException e_" + rootNode.getUniqueName() + ") {\n";
        
        if(isMultiThread) {
            toReturn +=  failure_multiThread;
        }else{
            toReturn +=  failure_singleThread;
        }
        
        toReturn += "\ne_" + rootNode.getUniqueName() + ".printStackTrace();\n";
        
        toReturn += "globalMap.put(\""+rootNode.getUniqueName()+ "_SUBPROCESS_STATE\", -1);\n";
      
       //List< ? extends IConnection> onSubJobErrorConns = rootNode.getOutgoingConnections(EConnectionType.ON_SUBJOB_ERROR);
       //if(onSubJobErrorConns!=null){
       //    for(IConnection conn : onSubJobErrorConns) {               
       //        toReturn += createCallProcess(conn.getTarget(),  className, isMultiThread);
       //    }
       //}            
       toReturn += "\n}finally {\n}"; 
       return toReturn;
    }
		 
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    Vector v = (Vector) codeGenArgument.getArgument();
    IProcess process = (IProcess)v.get(0);
    List<INode> rootNodes = (List<INode>)v.get(1);

	boolean stats = codeGenArgument.isStatistics();
	boolean trace = codeGenArgument.isTrace();
	boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();
	List<IContextParameter> params = new ArrayList<IContextParameter>();
    params=process.getContextManager().getDefaultContext().getContextParameterList();
    String jobFolderName = JavaResourcesHelper.getJobFolderName(process.getName(), process.getVersion());
    
    boolean exist_tParallelize = false;
    List<? extends INode> tParallelizeList = process.getNodesOfType("tParallelize");
    if(tParallelizeList != null && tParallelizeList.size() > 0){
    	exist_tParallelize = true;
    }

    stringBuffer.append(TEXT_1);
    stringBuffer.append(codeGenArgument.getContextName() );
    stringBuffer.append(TEXT_2);
    
	if(isRunInMultiThread || exist_tParallelize){

    stringBuffer.append(TEXT_3);
    
	}

    stringBuffer.append(TEXT_4);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_8);
    	
        List<INode> tBufferNodes = (List<INode>)process.getNodesOfType("tBufferOutput");
        if(tBufferNodes != null && tBufferNodes.size() > 0) {
    
    stringBuffer.append(TEXT_9);
    
		} else {
	
    stringBuffer.append(TEXT_10);
    
		}
	
    stringBuffer.append(TEXT_11);
    
		if (stats) {

    stringBuffer.append(TEXT_12);
    
		}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(process.getName());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(codeGenArgument.getCurrentProjectName().toLowerCase() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(jobFolderName );
    stringBuffer.append(TEXT_16);
     for (IContextParameter ctxParam :params)
			{
			    String typeToGenerate ="String";
			    if( !(ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory") ||ctxParam.getType().equals("id_List Of Value")))
			    {
			       typeToGenerate=JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true);
			    }
			    if(typeToGenerate.equals("java.util.Date"))
			    {
			        
			
    stringBuffer.append(TEXT_17);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_18);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_19);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_20);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_21);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_23);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_24);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_25);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_27);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_28);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_29);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_30);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_31);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_32);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_34);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_35);
    
			      
			    }else if(typeToGenerate.equals("Integer"))
			    {
			  
    stringBuffer.append(TEXT_36);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_39);
    
			    }else if(typeToGenerate.equals("Object")||typeToGenerate.equals("String")||typeToGenerate.equals("java.lang.String") )
			    {
			    
    stringBuffer.append(TEXT_40);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_41);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_43);
    
			    }else if(typeToGenerate.equals("Character")&&ctxParam.getName()!=null)
			    {
			 
    stringBuffer.append(TEXT_44);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_45);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_46);
    
			    } else if(typeToGenerate.equals("BigDecimal"))
            {
         
    stringBuffer.append(TEXT_47);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_48);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_50);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_51);
    
            }
			     else
			    {
			 
    stringBuffer.append(TEXT_52);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_57);
    
			    }
			}
			
    stringBuffer.append(TEXT_58);
     for (IContextParameter ctxParam :params){
			//about the type, they are same as header.javajet
			String typeToGenerate = "String";
			if(ctxParam.getType().equals("id_List Of Value") || ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory")){
				typeToGenerate = "String";
			}else{			 
				typeToGenerate = JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true);
			}
		
    stringBuffer.append(TEXT_59);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(typeToGenerate );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_63);
    }
    stringBuffer.append(TEXT_64);
    
		if (stats) {

    stringBuffer.append(TEXT_65);
    
		}

    stringBuffer.append(TEXT_66);
    
		if (trace) {

    stringBuffer.append(TEXT_67);
    
		}

    stringBuffer.append(TEXT_68);
    stringBuffer.append(TEXT_69);
    stringBuffer.append( ElementParameterParser.getValue(process, "__HEADER_CODE__"));
    stringBuffer.append(TEXT_70);
    
		//1. send the begin msg

		for (INode statCatcherNode : process.getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_71);
    stringBuffer.append(statCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_72);
    
    	}
	
        List<INode> prejobNodes = new ArrayList<INode>();
        
        List<INode> postjobNodes = new ArrayList<INode>();
        
        INode implicit_Context = null;

		for (INode rootNode : rootNodes) {
			String componentName = rootNode.getComponent().getName();
			String uniqueName = rootNode.getUniqueName();
			
            if (componentName.equals("tPrejob")) {
            	prejobNodes.add(rootNode);
                continue;
            }        

            if (componentName.equals("tPostjob")) {
                postjobNodes.add(rootNode);
                continue;
            }            

            if (uniqueName.startsWith("Implicit_Context_")) {        	
                implicit_Context = rootNode;
                continue;
            }
        }

    stringBuffer.append(TEXT_73);
    
//2. load implicit contextload
 if(implicit_Context != null){

    stringBuffer.append(TEXT_74);
    stringBuffer.append(createCallProcess(implicit_Context, process.getName(), false) );
     
 }

    stringBuffer.append(TEXT_75);
    
//3. run pre-job
 if(!prejobNodes.isEmpty()){
 	for(INode preNode:prejobNodes){

    stringBuffer.append(TEXT_76);
    stringBuffer.append(createCallProcess(preNode, process.getName(), false) );
     
	}
 }

    stringBuffer.append(TEXT_77);
    
		//4. flush the begin msg
		for (INode statCatcherNode : process.getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_78);
    stringBuffer.append(statCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_79);
    
    	}	

    stringBuffer.append(TEXT_80);
    

	//5. all others sub-job (MultiThread mode)
	if(isRunInMultiThread){
		for (INode rootNode : rootNodes) {
			String componentName = rootNode.getComponent().getName();
			String uniqueName = rootNode.getUniqueName();
			
			//filter the special subjob
			if("tPrejob".equals(componentName)) continue;
			if("tPostjob".equals(componentName)) continue;
			
			//bug16808 when use parallel option at Output components, that will multi thread
			if(componentName.startsWith("tAsyncIn")) continue;
			//end bug16808
			
			if(implicit_Context!=null && implicit_Context.getUniqueName().equals(uniqueName)) continue;
			
            if (!componentName.equals("tLogCatcher") && !componentName.equals("tFlowMeterCatcher") && !componentName.equals("tAssertCatcher") && !componentName.equals("tStatCatcher")) {

    stringBuffer.append(TEXT_81);
    stringBuffer.append(createCallProcess(rootNode, process.getName(), true) );
    stringBuffer.append(TEXT_82);
    
			}
		}

    stringBuffer.append(TEXT_83);
    
	}else{ // isRunInMultiThread  //5. all others sub-job (SingleThread  mode)
		for (INode rootNode : rootNodes) {
			String componentName = rootNode.getComponent().getName();
			String uniqueName = rootNode.getUniqueName();
			
			//filter the special subjob
			if("tPrejob".equals(componentName)) continue;
			if("tPostjob".equals(componentName)) continue;
			if(implicit_Context!=null && implicit_Context.getUniqueName().equals(uniqueName)) continue;
						
            if (!componentName.equals("tLogCatcher") && !componentName.equals("tFlowMeterCatcher") && !componentName.equals("tAssertCatcher") && !componentName.equals("tStatCatcher") && !componentName.equals("tAsyncIn")) {

    stringBuffer.append(TEXT_84);
    stringBuffer.append(createCallProcess(rootNode, process.getName(), false) );
    	  }
	}
}// end if(isRunInMultiThread)

    stringBuffer.append(TEXT_85);
    
//6. run the post-job
 if(!postjobNodes.isEmpty()){
 	for(INode postNode:postjobNodes){

    stringBuffer.append(TEXT_86);
    stringBuffer.append(createCallProcess(postNode, process.getName(), false) );
     
	}
 }

    stringBuffer.append(TEXT_87);
    stringBuffer.append(TEXT_88);
    stringBuffer.append( ElementParameterParser.getValue(process, "__FOOTER_CODE__"));
    stringBuffer.append(TEXT_89);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_90);
    
	//7. send & flush the end msg to statcatcher
	for (INode statCatcherNode : process.getNodesOfType("tStatCatcher")) {

    stringBuffer.append(TEXT_91);
    stringBuffer.append(statCatcherNode.getUniqueName() );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(statCatcherNode.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_93);
    
    }	

    stringBuffer.append(TEXT_94);
    
	if (stats) {

    stringBuffer.append(TEXT_95);
    	
	}

	if (trace) {

    stringBuffer.append(TEXT_96);
    
	}	

    stringBuffer.append(TEXT_97);
    return stringBuffer.toString();
  }
}