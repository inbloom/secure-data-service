package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.runprocess.CodeGeneratorRoutine;
import org.talend.designer.codegen.i18n.Messages;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.core.ui.branding.AbstractBrandingService;
import org.talend.core.GlobalServiceRegister;
import org.talend.designer.codegen.ITalendSynchronizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.utils.JavaResourcesHelper;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Header_routeJava
{
  protected static String nl;
  public static synchronized Header_routeJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Header_routeJava result = new Header_routeJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " ";
  protected final String TEXT_2 = " " + NL + "package ";
  protected final String TEXT_3 = ";" + NL;
  protected final String TEXT_4 = NL + "import routines.";
  protected final String TEXT_5 = ";";
  protected final String TEXT_6 = NL + "import routines.system.*;" + NL + "import routines.system.api.*; " + NL + "import java.text.ParseException;" + NL + "import java.text.SimpleDateFormat;" + NL + "import java.util.Date;" + NL + "import java.util.List;" + NL + "import java.math.BigDecimal;" + NL + "import java.io.ByteArrayOutputStream;" + NL + "import java.io.ByteArrayInputStream;" + NL + "import java.io.DataInputStream;" + NL + "import java.io.DataOutputStream;" + NL + "import java.io.ObjectOutputStream;" + NL + "import java.io.ObjectInputStream;" + NL + "import java.io.IOException;" + NL + "import java.util.Comparator;" + NL;
  protected final String TEXT_7 = NL;
  protected final String TEXT_8 = NL;
  protected final String TEXT_9 = NL + NL;
  protected final String TEXT_10 = NL + "\t//the import part of ";
  protected final String TEXT_11 = NL + "\t";
  protected final String TEXT_12 = NL + "\t";
  protected final String TEXT_13 = NL + NL + "/**" + NL + " * Job: ";
  protected final String TEXT_14 = " Purpose: ";
  protected final String TEXT_15 = "<br>" + NL + " * Description: ";
  protected final String TEXT_16 = " <br>" + NL + " * @author ";
  protected final String TEXT_17 = NL + " * @version ";
  protected final String TEXT_18 = NL + " * @status ";
  protected final String TEXT_19 = " " + NL + " */                                " + NL + " public class ";
  protected final String TEXT_20 = " implements TalendESBRoute { " + NL + " \t\t" + NL + "\tprivate final String jobVersion = \"";
  protected final String TEXT_21 = "\";" + NL + "\tprivate final String jobName = \"";
  protected final String TEXT_22 = "\";" + NL + "\tprivate final String projectName = \"";
  protected final String TEXT_23 = "\";" + NL + "\tpublic Integer errorCode = null;" + NL + "\tprivate String currentComponent = \"\";" + NL + "\tpublic TalendESBRoute stopDelegate = null;" + NL + "\t" + NL + "\tpublic ";
  protected final String TEXT_24 = "(){" + NL + "\t\tinitUriMap();" + NL + "\t}" + NL + "\t" + NL + "\tpublic void stop() throws Exception {" + NL + "\t\tif (stopDelegate != null) stopDelegate.stop();" + NL + "\t}" + NL + "\t" + NL + "\tpublic void shutdown() throws Exception {" + NL + "\t\tif (stopDelegate != null) stopDelegate.shutdown();" + NL + "\t}" + NL + "\t";
  protected final String TEXT_25 = NL + "\tprivate org.osgi.framework.BundleContext bundleContext;" + NL + "" + NL + "\tpublic void setBundleContext(org.osgi.framework.BundleContext bundleContext) {" + NL + "\t\tthis.bundleContext = bundleContext;" + NL + "\t}" + NL + "\t";
  protected final String TEXT_26 = NL + NL + "\t//ESB Service Locator Feature" + NL + "\tprivate org.apache.cxf.feature.AbstractFeature locatorFeature;" + NL + "\t" + NL + "\t//ESB Service Activity Monitor Feature" + NL + "\tprivate org.apache.cxf.feature.AbstractFeature eventFeature;" + NL + "" + NL + "\tpublic void setLocatorFeature(org.apache.cxf.feature.AbstractFeature locatorFeature) {" + NL + "\t\tthis.locatorFeature = locatorFeature;" + NL + "\t}" + NL + "\t" + NL + "\tpublic void setEventFeature(org.apache.cxf.feature.AbstractFeature eventFeature) {" + NL + "\t\tthis.eventFeature = eventFeature;" + NL + "\t}" + NL;
  protected final String TEXT_27 = NL + "\tprivate org.apache.camel.builder.RouteBuilder routeBuilder;" + NL + "" + NL + "\tprivate java.util.Map<String, String> uriMap; " + NL + "\t" + NL + "\tpublic java.util.Map<String,String> getUriMap(){" + NL + "\t\treturn this.uriMap;" + NL + "\t}" + NL + "\t" + NL + "\tpublic void loadCustomUriMap(java.util.Map<String,String> newMap){" + NL + "\t\tif(newMap == null){" + NL + "\t\t\treturn;" + NL + "\t\t}" + NL + "\t\tfor(java.util.Map.Entry<String, String> entry: newMap.entrySet()){" + NL + "\t\t\turiMap.put(entry.getKey(), entry.getValue());" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\tprotected void initUriMap(){" + NL + "\t\turiMap = new java.util.HashMap<String, String>();";
  protected final String TEXT_28 = "uriMap.put(\"";
  protected final String TEXT_29 = "\", ";
  protected final String TEXT_30 = ");";
  protected final String TEXT_31 = NL + "\t\t}" + NL + "" + NL + "\tprivate java.util.Properties defaultProps = new java.util.Properties();" + NL + "\t" + NL + "\tpublic class ContextProperties extends java.util.Properties {" + NL + "" + NL + "\t\tpublic ContextProperties(java.util.Properties properties){" + NL + "\t\t\tsuper(properties);" + NL + "\t\t}" + NL + "\t\tpublic ContextProperties(){" + NL + "\t\t\tsuper();" + NL + "\t\t}" + NL + "" + NL + "\t\tpublic void synchronizeContext(){" + NL + "\t\t\t";
  protected final String TEXT_32 = NL + "\t\t\tif(";
  protected final String TEXT_33 = " != null){" + NL + "\t\t\t\t";
  protected final String TEXT_34 = NL + "\t\t\t\t\tString pattern_";
  protected final String TEXT_35 = " = \"yyyy-MM-dd HH:mm:ss\";" + NL + "\t\t\t\t\tString value_";
  protected final String TEXT_36 = " = \"";
  protected final String TEXT_37 = "\";" + NL + "\t\t\t\t\tString[] parts_";
  protected final String TEXT_38 = " = value_";
  protected final String TEXT_39 = ".split(\";\");" + NL + "\t\t\t\t\tif(parts_";
  protected final String TEXT_40 = ".length > 1){" + NL + "\t\t\t\t\t\tpattern_";
  protected final String TEXT_41 = " = parts_";
  protected final String TEXT_42 = "[0];" + NL + "\t\t\t\t\t\tthis.setProperty(\"";
  protected final String TEXT_43 = "\", pattern_";
  protected final String TEXT_44 = " + \";\" + FormatterUtils.format_Date(";
  protected final String TEXT_45 = ", pattern_";
  protected final String TEXT_46 = "));" + NL + "\t\t\t\t\t}else{" + NL + "\t\t\t\t\t\tthis.setProperty(\"";
  protected final String TEXT_47 = "\", FormatterUtils.format_Date(";
  protected final String TEXT_48 = ", pattern_";
  protected final String TEXT_49 = "));" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_50 = NL + "\t\t\t\t\tthis.setProperty(\"";
  protected final String TEXT_51 = "\", ";
  protected final String TEXT_52 = ".toString());" + NL + "\t\t\t\t";
  protected final String TEXT_53 = NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_54 = NL + "\t\t}" + NL;
  protected final String TEXT_55 = NL + "\t\tpublic String ";
  protected final String TEXT_56 = ";" + NL + "\t\tpublic String get";
  protected final String TEXT_57 = "(){" + NL + "\t\t\treturn this.";
  protected final String TEXT_58 = ";" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_59 = NL + "public ";
  protected final String TEXT_60 = " ";
  protected final String TEXT_61 = ";" + NL + "public ";
  protected final String TEXT_62 = " get";
  protected final String TEXT_63 = "(){" + NL + "\treturn this.";
  protected final String TEXT_64 = ";" + NL + "}";
  protected final String TEXT_65 = NL + "\t}" + NL + "\tprivate ContextProperties context = new ContextProperties();" + NL + "\tpublic ContextProperties getContext() {" + NL + "\t\treturn this.context;" + NL + "\t}" + NL + "\t" + NL + "\t" + NL;
  protected final String TEXT_66 = "\t" + NL + "" + NL + "\tclass MyStatThread implements Runnable {" + NL + "\t\t\t" + NL + "\t\tCamelStat stats;" + NL + "\t\tboolean isFinished;" + NL + "\t\t" + NL + "\t\tpublic MyStatThread(CamelStat stats) {" + NL + "\t\t\tthis.stats = stats;" + NL + "\t\t}" + NL + "" + NL + "\t\tpublic void run() {" + NL + "\t\t\twhile(true) {" + NL + "\t\t\t\ttry {";
  protected final String TEXT_67 = NL + "\t\t\t\t\t\t\t\tstats.updateStatOnConnection(\"";
  protected final String TEXT_68 = "\", 1, \"";
  protected final String TEXT_69 = "\");";
  protected final String TEXT_70 = NL + "\t\t\t\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\tThread.sleep(1000);" + NL + "\t\t\t\t} catch (Exception e) {" + NL + "\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    Vector v = (Vector) codeGenArgument.getArgument();
	IProcess process = (IProcess)v.get(0);
    String version = (String)v.get(1);  
	
	List< ? extends INode> processNodes = (List< ? extends INode>)process.getGeneratingNodes();
	boolean stats = codeGenArgument.isStatistics();
	boolean trace = codeGenArgument.isTrace();
	boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();
	List<IContextParameter> params = new ArrayList<IContextParameter>();
	params=process.getContextManager().getDefaultContext().getContextParameterList();

    
IBrandingService service=(IBrandingService)GlobalServiceRegister.getDefault().getService(IBrandingService.class);
if(service instanceof AbstractBrandingService){
 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(((AbstractBrandingService) service).getJobLicenseHeader(version));
    
 }
  String jobFolderName = JavaResourcesHelper.getJobFolderName(process.getName(), process.getVersion());
  String packageName = codeGenArgument.getCurrentProjectName().toLowerCase() + "." + jobFolderName;

    stringBuffer.append(TEXT_2);
    stringBuffer.append( packageName );
    stringBuffer.append(TEXT_3);
    for (String routine : CodeGeneratorRoutine.getRequiredRoutineName(process)) {
	if(!routine.equals(ITalendSynchronizer.TEMPLATE)){
    stringBuffer.append(TEXT_4);
    stringBuffer.append(routine);
    stringBuffer.append(TEXT_5);
    	}
}
    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(ElementParameterParser.getValue(process, "__HEADER_IMPORT__") );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(ElementParameterParser.getValue(process, "__FOOTER_IMPORT__") );
    stringBuffer.append(TEXT_9);
    
	List<INode> nodesWithImport = process.getNodesWithImport();
	if(nodesWithImport != null) {
		for(INode node:nodesWithImport){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(ElementParameterParser.getValue(node, "__IMPORT__") );
    stringBuffer.append(TEXT_12);
     		}
    } 

    stringBuffer.append(TEXT_13);
    stringBuffer.append(process.getName() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(ElementParameterParser.getValue(process, "__PURPOSE__") );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(ElementParameterParser.getValue(process, "__DESCRIPTION__") );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(ElementParameterParser.getValue(process, "__AUTHOR__") );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(version );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(ElementParameterParser.getValue(process, "__STATUS__") );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(process.getName());
    stringBuffer.append(TEXT_20);
    stringBuffer.append(process.getVersion() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(codeGenArgument.getJobName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(codeGenArgument.getCurrentProjectName() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(process.getName());
    stringBuffer.append(TEXT_24);
    
	boolean startable = false;	
	for (INode node : (List< ? extends INode>)process.getGraphicalNodes()) {
		Object value = node.getPropertyValue("STARTABLE");
		startable = value == null? false:(Boolean)value;
		if(startable){
			break;
		}
	}
	if (startable) { 

    stringBuffer.append(TEXT_25);
    
	}

    
	boolean hasCXFCom = false;
	for (INode node : (List< ? extends INode>)process.getGraphicalNodes()) {
		String componentName = node.getComponent().getName();
		if("cCXF".equals(componentName)){
			hasCXFCom = true;
			break;
		}
	}
	if(hasCXFCom){

    stringBuffer.append(TEXT_26);
    
	}

    stringBuffer.append(TEXT_27);
    
String cid = "";
String componentName = "";
String uri = "";
for(INode node:(List< ? extends INode>)process.getGraphicalNodes())
{
   	IElementParameter param = node.getElementParameter("LABEL");
   	if(param != null && !"__UNIQUE_NAME__".equals(param.getValue())){
   		cid = (String)param.getValue();	
   	}else{
   		cid = node.getUniqueName();
   	}
	componentName = node.getComponent().getName();
	uri = "";
	if("cMessagingEndpoint".equals(componentName)){
		uri = ElementParameterParser.getValue(node, "__URI__");
	}else if("cFile".equals(componentName)){
		String filePath = ElementParameterParser.getValue(node, "__URI__");
    	String noop = ElementParameterParser.getValue(node, "__NOOP__");
     	String flatten = ElementParameterParser.getValue(node, "__FLATTEN__");
    	String bufferSize = ElementParameterParser.getValue(node, "__BUFFER_SIZE__");
    	String autoCreate = ElementParameterParser.getValue(node, "__AUTOCREATE__");
    	String fileName = ElementParameterParser.getValue(node, "__FILENAME__");
    	String charset = ElementParameterParser.getValue(node, "__ENCODING__");
 
     	StringBuffer arguments = new StringBuffer();
 
     	uri = "\"file:///\"+" + filePath;
 
     	if ("true".equals(noop)) {
    	     arguments.append("\"noop=true\"+");
    	 } else {
	         arguments.append("\"noop=false\"+");
	     }
	 
	     if ("false".equals(autoCreate)) {// default true, ignore.
	         arguments.append("\"&autoCreate=false\"+");
	     }
	     
	     if ("true".equals(flatten)) {// default false, ignore.
	         arguments.append("\"&flatten=true\"+");
	     }
	 
	     if (fileName != null && !fileName.equals("\"\"") && fileName.length() > 0 ) {
	      arguments.append("\"&fileName=\"+");
	      arguments.append(fileName);
	      arguments.append("+");
	     }
	     
	     if (charset != null  && !charset.equals("\"\"") && charset.length() > 0) {
	      arguments.append("\"&charset=\"+");
	      arguments.append(charset);
	      arguments.append("+");
	     }
	     
	     if (bufferSize != null && !bufferSize.equals("\"\"") && bufferSize.length() > 0) {
	      arguments.append("\"&bufferSize=\"+");
	      arguments.append(bufferSize);
	      arguments.append("+");
	     }
	 
	     List<Map<String, String>> tableValues = (List<Map<String, String>>) ElementParameterParser.getObjectValue(node,
	             "__ADVARGUMENTS__");
	     for (Map<String, String> map : tableValues) {
	         String argName = map.get("NAME");
	         String argValue = map.get("VALUE");
	         arguments.append("\"&\"+");
	         arguments.append(argName);
	         arguments.append("+\"=\"+");
	         arguments.append(argValue);
	         arguments.append("+");
	     }
	 
	     if (arguments.length() > 0) {
	      arguments.deleteCharAt(arguments.length()-1);
	         uri = uri + "+\"?\"+" + arguments.toString();
	     } else {
	     }
	     System.out.println(uri);
	}else if("cCXF".equals(componentName)){
		String url = ElementParameterParser.getValue(node,"__ADDRESS__").trim();
		String serviceType = ElementParameterParser.getValue(node, "__SERVICE_TYPE__");
		
		StringBuilder sb = new StringBuilder();
		sb.append("\"cxf://\"+");
		sb.append(url);
		sb.append("+\"?" + serviceType + "=\"");
		
		if("wsdlURL".equals(serviceType)){
			sb.append("+");
			String filePath = ElementParameterParser.getValue(node, "__WSDL_FILE__");
			filePath.replaceAll("\\\\","/");
			sb.append(filePath);
		}else {
			sb.append("+" + ElementParameterParser.getValue(node, "__SERVICE_CLASS__"));
		}
		
	
		String dataformat = ElementParameterParser.getValue(node, "__DATAFORMAT__");
			sb.append("+\"&dataFormat=" + dataformat + "\"");
		
		String specifyService = ElementParameterParser.getValue(node, "__SPECIFY_SERVICE__");
		if("true".equals(specifyService)){
			sb.append("+\"&serviceName=\"+");
			sb.append(ElementParameterParser.getValue(node, "__SERVICE_NAME__").trim());
			sb.append("+\"&portName=\"+");
			sb.append(ElementParameterParser.getValue(node, "__PORT_NAME__").trim());
		}
		
		List<Map<String, String>> tableValues = (List<Map<String, String>>) ElementParameterParser.getObjectValue(node,
	            "__ADVARGUMENTS__");
	    for (Map<String, String> map : tableValues) {
	        String argName = map.get("NAME").trim();
	        String argValue = map.get("VALUE").trim();
			sb.append("+\"&\"+" + argName + "+\"=\"+" + argValue);
	    }
		uri = sb.toString();
	}else if("cFtp".equals(componentName)){
		String type = ElementParameterParser.getValue(node, "__TYPE__");
	    String username = ElementParameterParser.getValue(node, "__USERNAME__");
	    String server = ElementParameterParser.getValue(node, "__SERVER__");
	    String port = ElementParameterParser.getValue(node, "__PORT__");
	    String password = ElementParameterParser.getValue(node, "__PASSWORD__");
	    String directory = ElementParameterParser.getValue(node, "__DIRECTORY__");
	
	    StringBuffer fragments = new StringBuffer();
	    fragments.append("\"" + type + "://\"");
	
	    if (username.trim().length() > 0) {
	        fragments.append("+" + username.trim());
	        fragments.append("+\"@\"");
	    }
	
	    fragments.append("+" + server.trim());
	
	    if (port.trim().length() > 0) {
	        fragments.append("+\":\"");
	        fragments.append("+" + port.trim());
	    }
	
	    if (directory.trim().length() > 0) {
	        fragments.append("+\"/\"");
	        fragments.append("+" + directory.trim());
	    }
	
	    boolean hasArg = false;
	    if (password.trim().length() > 0) {
	        hasArg = true;
	        fragments.append("+\"?password=\"+" + password);
	    }
	
	    List<Map<String, String>> tableValues = (List<Map<String, String>>) ElementParameterParser.getObjectValue(node,
	            "__ADVARGUMENTS__");
	    for (Map<String, String> map : tableValues) {
	        String argName = map.get("NAME").trim();
	        String argValue = map.get("VALUE").trim();
			 if (!hasArg) {
	            fragments.append("+\"?\"+" + argName + "+\"=\"+" + argValue);
	            hasArg = true;
	        } else {
	            fragments.append("+\"&\"+" + argName + "+\"=\"+" + argValue);
	        }
	
	    }
	    uri = fragments.toString();
	}else if("cJMS".equals(componentName)){
		String name = node.getUniqueName().replace("_", "");
		name = "\"" + name + "\"";
		//String name = ElementParameterParser.getValue(node, "__NAME__").trim();
		//Remove unnecessary subfix
		//name += "+";
	    //name += ElementParameterParser.getValue(node, "__SUBFIX__").trim();
	    String type = ElementParameterParser.getValue(node, "__TYPE__");
	    String destination = ElementParameterParser.getValue(node, "__DESTINATION__").trim();
	
	    boolean hasOptions = false;
	
	    StringBuffer sb = new StringBuffer();
	    sb.append(name);
	    sb.append("+\":" + type + ":\"");
	    sb.append("+" + destination);
	   
	
	    List<Map<String, String>> tableValues = (List<Map<String, String>>) ElementParameterParser.getObjectValue(node,
	            "__URI_OPTIONS__");
	    for (Map<String, String> map : tableValues) {
	        String argName = map.get("NAME").trim();
	        String argValue = map.get("VALUE").trim();
	
	         if (!hasOptions) {
	            sb.append("+\"?\"+" + argName + "+\"=\"+" + argValue);
	            hasOptions = true;
	        } else {
	            sb.append("+\"&\"+" + argName + "+\"=\"+" + argValue);
	        }
	    }
	    
	    uri = sb.toString();
	}else{
		continue;
	}
		
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(uri);
    stringBuffer.append(TEXT_30);
    
}
	
    stringBuffer.append(TEXT_31);
     for (IContextParameter ctxParam :params){
				String cParaName = ctxParam.getName();
			
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_33);
    if(ctxParam.getType().equals("id_Date")){
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(ctxParam.getValue() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_49);
    }else{
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cParaName );
    stringBuffer.append(TEXT_52);
    }
    stringBuffer.append(TEXT_53);
     } 
    stringBuffer.append(TEXT_54);
    
		for (IContextParameter ctxParam :params)
		{
				if(ctxParam.getType().equals("id_List Of Value") || ctxParam.getType().equals("id_File") || ctxParam.getType().equals("id_Directory"))
				{
		
    stringBuffer.append(TEXT_55);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(Character.toUpperCase(ctxParam.getName().charAt(0)) + ctxParam.getName().substring(1));
    stringBuffer.append(TEXT_57);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_58);
    
				}else
				{

    stringBuffer.append(TEXT_59);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true));
    stringBuffer.append(TEXT_60);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(JavaTypesManager.getTypeToGenerate(ctxParam.getType(),true));
    stringBuffer.append(TEXT_62);
    stringBuffer.append(Character.toUpperCase(ctxParam.getName().charAt(0)) + ctxParam.getName().substring(1));
    stringBuffer.append(TEXT_63);
    stringBuffer.append(ctxParam.getName());
    stringBuffer.append(TEXT_64);
    				}
		}

    stringBuffer.append(TEXT_65);
    
	if(stats) {

    stringBuffer.append(TEXT_66);
    
					for (INode node : processNodes) {
						if (node.isActivate()) {
							for(int i=0; i<node.getIncomingConnections().size(); i++) {

    stringBuffer.append(TEXT_67);
    stringBuffer.append(node.getIncomingConnections().get(i).getUniqueName());
    stringBuffer.append(TEXT_68);
    stringBuffer.append(node.getUniqueName());
    stringBuffer.append(TEXT_69);
    								
							}
						}
					}

    stringBuffer.append(TEXT_70);
    
	} //if stats

    return stringBuffer.toString();
  }
}
