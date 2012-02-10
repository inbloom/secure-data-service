package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IElementParameter;
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
import java.util.Map;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.utils.JavaResourcesHelper;

public class Camel_headerJava
{
  protected static String nl;
  public static synchronized Camel_headerJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Camel_headerJava result = new Camel_headerJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "class CamelImpl extends org.apache.camel.impl.MainSupport {" + NL + "\tprotected CamelImpl instance;" + NL + "" + NL + "\tpublic CamelImpl() {" + NL + "\t\ttry {" + NL + "\t\t\tpostProcessContext();" + NL + "\t\t\tinitRoute();" + NL + "\t\t} catch (Exception e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Returns the currently executing main" + NL + "\t * " + NL + "\t * @return the current running instance" + NL + "\t */" + NL + "\tpublic CamelImpl getInstance() {" + NL + "\t\treturn instance;" + NL + "\t}" + NL + "" + NL + "\tprotected void doStart() throws Exception {" + NL + "\t\tsuper.doStart();" + NL + "\t\t" + NL + "\t\torg.apache.camel.management.JmxNotificationEventNotifier notifier = new org.apache.camel.management.JmxNotificationEventNotifier();" + NL + "\t\tnotifier.setSource(\"MyCamel\");" + NL + "\t\tnotifier.setIgnoreCamelContextEvents(true);" + NL + "\t\tnotifier.setIgnoreRouteEvents(true);" + NL + "\t\tnotifier.setIgnoreServiceEvents(true);" + NL + "\t\t" + NL + "\t\tgetCamelContexts().get(0).getManagementStrategy().addEventNotifier(notifier);" + NL + "" + NL + "\t\tjava.net.URLClassLoader sysloader = (java.net.URLClassLoader) ClassLoader.getSystemClassLoader();" + NL + "\t\tjava.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod(\"addURL\", new Class[] { java.net.URL.class });" + NL + "\t\tmethod.setAccessible(true);" + NL + "\t\tString[] libPaths = new String[] { " + NL + NL;
  protected final String TEXT_3 = NL + "\t\t\t\t\t\"";
  protected final String TEXT_4 = "\",";
  protected final String TEXT_5 = NL + "\t};" + NL + "\tfor(String lib:libPaths){" + NL + "\t\tString separator = System.getProperty(\"path.separator\");" + NL + "\t\tString[] jarFiles = lib.split(separator);\t" + NL + "\t\tfor(String jarFile:jarFiles){\t\t" + NL + "\t\t\tmethod.invoke(sysloader, new Object[] { new java.io.File(jarFile).toURL() });" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\t\t\tgetCamelContexts().get(0).start();" + NL;
  protected final String TEXT_6 = "\t\t\t" + NL + "" + NL + "\t\t\tCamelStat runStat = new CamelStat(getCamelContexts().get(0));" + NL + "\t\t\t\t\t\t" + NL + "\t\t\trunStat.setParams();" + NL + "\t\t\t" + NL + "\t\t\trunStat.openSocket(true);" + NL + "\t\t\trunStat.setAllPID(rootPid, fatherPid, pid, jobName);" + NL + "\t\t\trunStat.startThreadStat(clientHost, portStats);" + NL + "\t\t\trunStat.updateStatOnJob(RunStat.JOBSTART, \"\");" + NL + "\t\t\t" + NL + "\t\t\tMyStatThread statsThread = new MyStatThread(runStat);" + NL + "\t\t\tstatsThread.run();";
  protected final String TEXT_7 = NL + "\t\t\t" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void initRoute() throws Exception {" + NL + "\t\t\trouteBuilder = new org.apache.camel.builder.RouteBuilder() {" + NL + "\t\t\t\tpublic void configure() throws Exception {";
  protected final String TEXT_8 = NL + "\t\t\t\t\t//CXF endpoint for ";
  protected final String TEXT_9 = NL + "   \t\t\t\t\torg.apache.camel.Endpoint ";
  protected final String TEXT_10 = " = endpoint(";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = "\t\t\t\t\t" + NL + "\t\t\t\t\t//Add Service Locator Service to ";
  protected final String TEXT_13 = NL + "\t\t\t\t\tif(locatorFeature != null){" + NL + "\t\t\t\t\t\t\t((org.apache.camel.component.cxf.CxfEndpoint)";
  protected final String TEXT_14 = ").getFeatures().add(locatorFeature);" + NL + "\t\t\t\t\t}";
  protected final String TEXT_15 = NL + "\t\t\t\t\t//Add Service Activity Monitor Service to ";
  protected final String TEXT_16 = NL + "\t\t\t\t\tif(eventFeature != null){" + NL + "\t\t\t\t\t\t\t((org.apache.camel.component.cxf.CxfEndpoint)";
  protected final String TEXT_17 = ").getFeatures().add(eventFeature);" + NL + "\t\t\t\t\t}";
  protected final String TEXT_18 = NL + "\t\t\t\t\t\torg.apache.camel.spi.AggregationRepository repo_";
  protected final String TEXT_19 = " = new ";
  protected final String TEXT_20 = ";";
  protected final String TEXT_21 = NL + "\t\t\t\t\t\torg.apache.camel.spi.RecoverableAggregationRepository repo_";
  protected final String TEXT_22 = " = new ";
  protected final String TEXT_23 = ";";
  protected final String TEXT_24 = NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_25 = ".setUseRecovery(true);" + NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_26 = ".setMaximumRedeliveries(";
  protected final String TEXT_27 = ");" + NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_28 = ".setDeadLetterUri(";
  protected final String TEXT_29 = ");" + NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_30 = ".setRecoveryInterval(";
  protected final String TEXT_31 = ");";
  protected final String TEXT_32 = NL + "\t\t\t\t\t\t\torg.apache.camel.component.hawtdb.HawtDBAggregationRepository repo_";
  protected final String TEXT_33 = " = new org.apache.camel.component.hawtdb.HawtDBAggregationRepository(\"";
  protected final String TEXT_34 = "\", ";
  protected final String TEXT_35 = ");";
  protected final String TEXT_36 = NL + "\t\t\t\t\t\t\torg.apache.camel.component.hawtdb.HawtDBAggregationRepository repo_";
  protected final String TEXT_37 = " = new org.apache.camel.component.hawtdb.HawtDBAggregationRepository(\"";
  protected final String TEXT_38 = "\");";
  protected final String TEXT_39 = NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_40 = ".setUseRecovery(true);" + NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_41 = ".setMaximumRedeliveries(";
  protected final String TEXT_42 = ");" + NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_43 = ".setDeadLetterUri(";
  protected final String TEXT_44 = ");" + NL + "\t\t\t\t\t\t\trepo_";
  protected final String TEXT_45 = ".setRecoveryInterval(";
  protected final String TEXT_46 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    Vector v = (Vector) codeGenArgument.getArgument();
	IProcess process = (IProcess)v.get(0);
    String version = (String)v.get(1);
	
	boolean startable = false;
	for (INode node : (List< ? extends INode>)process.getGraphicalNodes()) {
		Object value = node.getPropertyValue("STARTABLE");
		startable = value == null? false:(Boolean)value;
		if(startable){
			break;
		}
	}
	
	boolean stats = codeGenArgument.isStatistics();
	
	List< ? extends INode> processNodes = (List< ? extends INode>)process.getGeneratingNodes();

    stringBuffer.append(TEXT_2);
    
	List<Map<String, String>> dependencies = null;
	for (INode node : processNodes) {
		if (node.isActivate()) {
			if (node.getComponent().getName().equals("cMessagingEndpoint")) {
				dependencies = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__HOTLIBS__");
				for(Map<String, String> dependencie : dependencies){
					String librarieJar = dependencie.get("LIBPATH");

    stringBuffer.append(TEXT_3);
    stringBuffer.append(librarieJar );
    stringBuffer.append(TEXT_4);
    
				}
			}
		}
	}

    stringBuffer.append(TEXT_5);
    

			if(stats) {

    stringBuffer.append(TEXT_6);
    
			} //if stats

    stringBuffer.append(TEXT_7);
    
for (INode node : processNodes) {
			if (node.getComponent().getName().equals("cCXF")) {
				IElementParameter param = node.getElementParameter("LABEL");
				String cid = "";
   				if(param != null && !"__UNIQUE_NAME__".equals(param.getValue())){
   					cid = (String)param.getValue();	
   				}else{
   					cid = node.getUniqueName();
   				}
   				String endpointVar = "endpoint" + cid;
   				String uriRef = "uriMap.get(\"" + cid + "\")";

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(endpointVar);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(uriRef);
    stringBuffer.append(TEXT_11);
    
				String useSL = ElementParameterParser.getValue(node, "__ENABLE_SL__");
				String useSAM = ElementParameterParser.getValue(node, "__ENABLE_SAM__");
				if("true".equals(useSL)){

    stringBuffer.append(TEXT_12);
    stringBuffer.append(endpointVar);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(endpointVar);
    stringBuffer.append(TEXT_14);
    				
				}
				//http://jira.talendforge.org/browse/TESB-3850
				String formatType = ElementParameterParser.getValue(node, "__DATAFORMAT__");
				if("true".equals(useSAM) && !"MESSAGE".equals(formatType)){	

    stringBuffer.append(TEXT_15);
    stringBuffer.append(endpointVar);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(endpointVar);
    stringBuffer.append(TEXT_17);
    				
				}	
			}
		
}			

    	
	for (INode node : processNodes) {
		if (node.isActivate()) {
			if (node.getComponent().getName().equals("cAggregate")) {
				boolean usePersistence = "true".equals(ElementParameterParser.getValue(node, "__USE_PERSISTENCE__"));
				String repository = ElementParameterParser.getValue(node, "__REPOSITORY__");
				if(usePersistence) {
					boolean useRecovery = "true".equals(ElementParameterParser.getValue(node, "__USE_RECOVERY__"));
					String recoveryInterval = ElementParameterParser.getValue(node, "__RECOVERY_INTERVAL__");
					String deadLetterUri = ElementParameterParser.getValue(node, "__DEAD_LETTER_URI__");
					String maximumRedeliveries = ElementParameterParser.getValue(node, "__MAXIMUM_REDELIVERIES__");
					
					if("AGGREGATION".equals(repository)) {

    stringBuffer.append(TEXT_18);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(ElementParameterParser.getValue(node, "__CUSTOM_REPOSITORY__") );
    stringBuffer.append(TEXT_20);
    
					} else if("RECOVERABLE".equals(repository)) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(ElementParameterParser.getValue(node, "__CUSTOM_REPOSITORY__") );
    stringBuffer.append(TEXT_23);
    
						if(useRecovery) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(maximumRedeliveries);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(deadLetterUri);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(recoveryInterval);
    stringBuffer.append(TEXT_31);
     
						}
					} else if("HAWTDB".equals(repository)) {
						boolean usePersistentFile = "true".equals(ElementParameterParser.getValue(node, "__USE_PERSISTENT_FILE__"));
						String persistentFile = ElementParameterParser.getValue(node, "__PERSISTENT_FILENAME__");
						if(usePersistentFile) {

    stringBuffer.append(TEXT_32);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(persistentFile);
    stringBuffer.append(TEXT_35);
    
						} else {

    stringBuffer.append(TEXT_36);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_38);
    
						}
						if(useRecovery) {

    stringBuffer.append(TEXT_39);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(maximumRedeliveries);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(deadLetterUri);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(recoveryInterval);
    stringBuffer.append(TEXT_46);
    
						}
					}
				}
			}
		}
	}

    return stringBuffer.toString();
  }
}
