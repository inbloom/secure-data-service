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

public class Footer_routeJava
{
  protected static String nl;
  public static synchronized Footer_routeJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Footer_routeJava result = new Footer_routeJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tpublic String resuming_logs_dir_path = null;" + NL + "\tpublic String resuming_checkpoint_path = null;" + NL + "\tpublic String parent_part_launcher = null;" + NL + "\tprivate String resumeEntryMethodName = null;" + NL + "\tprivate boolean globalResumeTicket = false;" + NL + "" + NL + "\t" + NL + "\tpublic boolean watch = false;" + NL + "\t//portStats is null, it means don't execute the statistics" + NL + "\tpublic Integer portStats = null;" + NL + "\tpublic int portTraces = 4334;" + NL + "\tpublic String clientHost;" + NL + "\tpublic String defaultClientHost = \"localhost\";" + NL + "\tpublic String contextStr = \"Default\";" + NL + "\tpublic String pid = \"0\";" + NL + "\tpublic String rootPid = null;" + NL + "\tpublic String fatherPid = null;" + NL + "\tpublic String fatherNode = null;" + NL + "\tpublic long startTime = 0;" + NL + "\tpublic boolean isChildJob = false;" + NL + "\t" + NL + "\tprivate boolean execStat = true;" + NL + "\t" + NL + "\tprivate java.util.Properties context_param = new java.util.Properties();" + NL + "    public java.util.Map<String, Object> parentContextMap = new java.util.HashMap<String, Object>();" + NL + "    " + NL + "    public static void main(String[] args){" + NL + "    \tfinal ";
  protected final String TEXT_2 = " ";
  protected final String TEXT_3 = "Class = new ";
  protected final String TEXT_4 = "();" + NL + "    " + NL + "        int exitCode = ";
  protected final String TEXT_5 = "Class.runJobInTOS(args);" + NL + "         " + NL + "         if(exitCode==1)" + NL + "       System.exit(exitCode);" + NL + "    }    " + NL + "   " + NL + " " + NL + "    public String[][] runJob(String[] args) {  " + NL + "    " + NL + "\t\tint exitCode = runJobInTOS(args);" + NL + "\t\tString[][] bufferValue = new String[][] { { Integer.toString(exitCode) } };" + NL + "\t\treturn bufferValue;" + NL + "    }" + NL + "    " + NL + "    public int runJobInTOS(String[] args) {\t" + NL + "\t" + NL + "\t\tString lastStr = \"\";" + NL + "        for (String arg : args) {" + NL + "        \tif (arg.equalsIgnoreCase(\"--context_param\")) {" + NL + "                lastStr = arg;" + NL + "            } else if (lastStr.equals(\"\")) {" + NL + "                evalParam(arg);" + NL + "            } else {" + NL + "                evalParam(lastStr + \" \" + arg);" + NL + "                lastStr = \"\";" + NL + "            }" + NL + "        }" + NL + "\t\t" + NL + "\t\tif(clientHost == null) {" + NL + "    \t\tclientHost = defaultClientHost;" + NL + "    \t}" + NL + "    \t" + NL + "    \tif(pid == null || \"0\".equals(pid)) {" + NL + "\t    \tpid = TalendString.getAsciiRandomString(6);" + NL + "\t    }" + NL + "" + NL + "    \tif (rootPid==null) {" + NL + "    \t\trootPid = pid;" + NL + "    \t}" + NL + "    \tif (fatherPid==null) {    \t \t" + NL + "    \t\tfatherPid = pid;    \t\t" + NL + "    \t}else{" + NL + "    \t\tisChildJob = true;" + NL + "    \t}" + NL;
  protected final String TEXT_6 = "    \t" + NL + "\t\t\tif (portStats != null) {" + NL + "\t\t\t\t// portStats = -1; //for testing" + NL + "\t\t\t\tif (portStats < 0 || portStats > 65535) {" + NL + "\t\t\t\t\t// issue:10869, the portStats is invalid, so this client socket can't open" + NL + "\t\t\t\t\tSystem.err.println(\"The statistics socket port \" + portStats + \" is invalid.\");" + NL + "\t\t\t\t\texecStat = false;" + NL + "\t\t\t\t}" + NL + "\t\t\t}else{" + NL + "\t\t\t\texecStat = false;" + NL + "\t\t\t}            \t";
  protected final String TEXT_7 = " " + NL + "\t\t" + NL + "\t\t" + NL + "\t\ttry {" + NL + "\t\t\tjava.io.InputStream inContext = ";
  protected final String TEXT_8 = ".class.getClassLoader().getResourceAsStream(\"";
  protected final String TEXT_9 = "/";
  protected final String TEXT_10 = "/contexts/\"+contextStr+\".properties\");" + NL + "\t\t\t" + NL + "\t\t\tif (inContext!=null) {" + NL + "\t    \t\t//defaultProps is in order to keep the original context value" + NL + "\t    \t\tdefaultProps.load(inContext);" + NL + "    \t\t\tinContext.close();" + NL + "\t    \t\tcontext = new ContextProperties(defaultProps);" + NL + "\t    \t}else{" + NL + "\t    \t\t//print info and job continue to run, for case: context_param is not empty." + NL + "\t    \t\tSystem.err.println(\"Could not find the context \" + contextStr);" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\tif(!context_param.isEmpty()) {\t\t\t\t\t\t" + NL + "\t\t\t    context.putAll(context_param);\t\t" + NL + "\t\t\t}";
  protected final String TEXT_11 = NL + "\t\t\ttry{" + NL + "\t\t\t\tString context_";
  protected final String TEXT_12 = "_value = context.getProperty(\"";
  protected final String TEXT_13 = "\");" + NL + "\t\t\t\tif (context_";
  protected final String TEXT_14 = "_value == null){" + NL + "\t\t\t\t\tcontext_";
  protected final String TEXT_15 = "_value = \"\";" + NL + "\t\t\t\t}" + NL + "\t\t\t\tint context_";
  protected final String TEXT_16 = "_pos = context_";
  protected final String TEXT_17 = "_value.indexOf(\";\");" + NL + "\t\t\t\tString context_";
  protected final String TEXT_18 = "_pattern =  \"yyyy-MM-dd HH:mm:ss\";" + NL + "\t\t\t\tif(context_";
  protected final String TEXT_19 = "_pos > -1){" + NL + "\t\t\t\t\tcontext_";
  protected final String TEXT_20 = "_pattern = context_";
  protected final String TEXT_21 = "_value.substring(0, context_";
  protected final String TEXT_22 = "_pos);" + NL + "\t\t\t\t\tcontext_";
  protected final String TEXT_23 = "_value = context_";
  protected final String TEXT_24 = "_value.substring(context_";
  protected final String TEXT_25 = "_pos + 1);" + NL + "\t\t\t\t}" + NL + "\t\t\t\t" + NL + "\t\t\t    context.";
  protected final String TEXT_26 = "=(java.util.Date)(new java.text.SimpleDateFormat(context_";
  protected final String TEXT_27 = "_pattern).parse(context_";
  protected final String TEXT_28 = "_value));" + NL + "\t\t\t   " + NL + "\t\t\t}catch(ParseException e)" + NL + "\t\t\t{" + NL + "\t\t\t    context.";
  protected final String TEXT_29 = "=null;" + NL + "\t\t\t}" + NL + "%>" + NL + "\t\t\t}else if(typeToGenerate.equals(\"Integer\"))" + NL + "\t\t\t    {" + NL + "%> " + NL + "\t\t\t  try{" + NL + "\t\t\t      context.";
  protected final String TEXT_30 = "=Integer.parseInt(context.getProperty(\"";
  protected final String TEXT_31 = "\"));" + NL + "\t\t\t  }catch(NumberFormatException e){" + NL + "\t\t\t      context.";
  protected final String TEXT_32 = "=null;" + NL + "\t\t\t  }";
  protected final String TEXT_33 = NL + "\t\t\t    context.";
  protected final String TEXT_34 = "=(";
  protected final String TEXT_35 = ") context.getProperty(\"";
  protected final String TEXT_36 = "\");";
  protected final String TEXT_37 = NL + "\t\t\t context.";
  protected final String TEXT_38 = "= new java.text.StringCharacterIterator(context.getProperty(\"";
  protected final String TEXT_39 = "\")).first();\t\t\t ";
  protected final String TEXT_40 = NL + "         try{" + NL + "         \tcontext.";
  protected final String TEXT_41 = "= new ";
  protected final String TEXT_42 = "(context.getProperty(\"";
  protected final String TEXT_43 = "\"));" + NL + "\t\t }catch(NumberFormatException e){" + NL + "\t\t    context.";
  protected final String TEXT_44 = "=null;" + NL + "\t\t }";
  protected final String TEXT_45 = " " + NL + "\t\t\t try{" + NL + "\t\t\t     context.";
  protected final String TEXT_46 = "=";
  protected final String TEXT_47 = ".parse";
  protected final String TEXT_48 = " (context.getProperty(\"";
  protected final String TEXT_49 = "\"));" + NL + "\t\t\t }catch(NumberFormatException e){" + NL + "\t\t\t     context.";
  protected final String TEXT_50 = "=null;" + NL + "\t\t\t  }";
  protected final String TEXT_51 = NL + "    \t} catch (java.io.IOException ie) {" + NL + "    \t\tSystem.err.println(\"Could not load context \"+contextStr);" + NL + "    \t\tie.printStackTrace();" + NL + "    \t}" + NL + "    \t" + NL + "    \t" + NL + "\t\t// get context value from parent directly" + NL + "\t\tif (parentContextMap != null && !parentContextMap.isEmpty()) {";
  protected final String TEXT_52 = "\t\t\t\tif (parentContextMap.containsKey(\"";
  protected final String TEXT_53 = "\")) {" + NL + "\t\t\t\tcontext.";
  protected final String TEXT_54 = " = (";
  protected final String TEXT_55 = ") parentContextMap.get(\"";
  protected final String TEXT_56 = "\");" + NL + "\t\t\t}";
  protected final String TEXT_57 = NL + "\t\t}    " + NL + "\t\ttry {" + NL + "\t\t\tinitUriMap();" + NL + "\t\t\tRoute(true);" + NL + "\t\t} catch (Exception e) {" + NL + "\t\t\tSystem.err.println(e.getMessage());" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t\treturn 1;" + NL + "\t\t}" + NL + "\t\treturn 0;" + NL + "\t}" + NL + "" + NL + "\tprivate void evalParam(String arg) {" + NL + "\t\tif (arg.startsWith(\"--resuming_logs_dir_path\")) {" + NL + "\t\t\tresuming_logs_dir_path = arg.substring(25);" + NL + "\t\t} else if (arg.startsWith(\"--resuming_checkpoint_path\")) {" + NL + "\t\t\tresuming_checkpoint_path = arg.substring(27);" + NL + "\t\t} else if (arg.startsWith(\"--parent_part_launcher\")) {" + NL + "\t\t\tparent_part_launcher = arg.substring(23);" + NL + "\t\t} else if (arg.startsWith(\"--watch\")) {" + NL + "    \t\twatch = true;" + NL + "    \t} else if (arg.startsWith(\"--stat_port=\")) {" + NL + "\t\t\tString portStatsStr = arg.substring(12);" + NL + "\t\t\tif (portStatsStr != null && !portStatsStr.equals(\"null\")) {" + NL + "\t\t\t\tportStats = Integer.parseInt(portStatsStr);" + NL + "\t\t\t}    \t\t" + NL + "    \t} else if (arg.startsWith(\"--trace_port=\")) {" + NL + "    \t\tportTraces = Integer.parseInt(arg.substring(13));" + NL + "    \t} else if (arg.startsWith(\"--client_host=\")) {" + NL + "    \t\tclientHost = arg.substring(14);" + NL + "    \t} else if (arg.startsWith(\"--context=\")) {" + NL + "    \t\tcontextStr = arg.substring(10);" + NL + "    \t} else if (arg.startsWith(\"--father_pid=\")) {" + NL + "    \t\tfatherPid = arg.substring(13);" + NL + "    \t} else if (arg.startsWith(\"--root_pid=\")) {" + NL + "    \t\trootPid = arg.substring(11);" + NL + "    \t} else if (arg.startsWith(\"--father_node=\")) {" + NL + "    \t\tfatherNode = arg.substring(14);" + NL + "    \t} else if (arg.startsWith(\"--pid=\")) {" + NL + "    \t\tpid = arg.substring(6);" + NL + "    \t} else if (arg.startsWith(\"--context_param\")) {   \t\t " + NL + "            String keyValue = arg.substring(16);           " + NL + "            int index = -1;" + NL + "            if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {" + NL + "                context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));" + NL + "            }" + NL + "    \t}\t" + NL + "\t}" + NL + "}";

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
	
	List<IContextParameter> params = new ArrayList<IContextParameter>();
    params=process.getContextManager().getDefaultContext().getContextParameterList();
    String jobFolderName = JavaResourcesHelper.getJobFolderName(process.getName(), process.getVersion());
	
	boolean stats = codeGenArgument.isStatistics();
	

    stringBuffer.append(TEXT_1);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_5);
    
		if (stats) {

    stringBuffer.append(TEXT_6);
    
		}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(process.getName());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(codeGenArgument.getCurrentProjectName().toLowerCase() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(jobFolderName );
    stringBuffer.append(TEXT_10);
     
			for (IContextParameter ctxParam :params)
			{
			    String typeToGenerate ="String";
			    if( !(ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory") ||ctxParam.getType().equals("id_List Of Value")))
			    {
			       typeToGenerate=JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true);
			    }
			    if(typeToGenerate.equals("java.util.Date"))
			    {
			        

    stringBuffer.append(TEXT_11);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(ctxParam.getName());
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
    
			    }else if(typeToGenerate.equals("Object")||typeToGenerate.equals("String")||typeToGenerate.equals("java.lang.String") )
			    {

    stringBuffer.append(TEXT_33);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_34);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_36);
    
			    }else if(typeToGenerate.equals("Character")&&ctxParam.getName()!=null)
			    {

    stringBuffer.append(TEXT_37);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_39);
    
			    } else if(typeToGenerate.equals("BigDecimal"))
            {

    stringBuffer.append(TEXT_40);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_41);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_44);
    
            }
			     else
			    {

    stringBuffer.append(TEXT_45);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_46);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_49);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_50);
    
			    }
			}

    stringBuffer.append(TEXT_51);
     
			for (IContextParameter ctxParam :params){
				//about the type, they are same as header.javajet
				String typeToGenerate = "String";
				if(ctxParam.getType().equals("id_List Of Value") || ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory")){
					typeToGenerate = "String";
				}else{			 
					typeToGenerate = JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true);
				}

    stringBuffer.append(TEXT_52);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_54);
    stringBuffer.append(typeToGenerate );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_56);
    
		}

    stringBuffer.append(TEXT_57);
    return stringBuffer.toString();
  }
}