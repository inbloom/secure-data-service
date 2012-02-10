package org.talend.designer.codegen.translators.business.bonita;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TBonitaDeployMainJava
{
  protected static String nl;
  public static synchronized TBonitaDeployMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TBonitaDeployMainJava result = new TBonitaDeployMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tSystem.setProperty(\"org.ow2.bonita.environment\", ";
  protected final String TEXT_3 = ");" + NL + "\tSystem.setProperty(\"java.security.auth.login.config\", ";
  protected final String TEXT_4 = " );" + NL + "\tSystem.setProperty(\"java.util.logging.config.file\", new java.io.File(";
  protected final String TEXT_5 = ").toURI().toURL().toString());" + NL + "\t" + NL + "\t" + NL + "\torg.ow2.bonita.facade.ManagementAPI managementAPI_";
  protected final String TEXT_6 = " = org.ow2.bonita.util.AccessorUtil.getAPIAccessor().getManagementAPI();" + NL + "\tjavax.security.auth.login.LoginContext loginContext_";
  protected final String TEXT_7 = " = null;" + NL + "\torg.ow2.bonita.facade.def.majorElement.ProcessDefinition process_";
  protected final String TEXT_8 = " = null;" + NL + "\t" + NL + "\tString processDefinitionUUID_";
  protected final String TEXT_9 = " = null;" + NL + "\ttry {" + NL + "\t\t" + NL + "\t\tloginContext_";
  protected final String TEXT_10 = " = new javax.security.auth.login.LoginContext(";
  protected final String TEXT_11 = ", new org.ow2.novabpm.util.SimpleCallbackHandler(";
  protected final String TEXT_12 = ", ";
  protected final String TEXT_13 = "));" + NL + "\t\tloginContext_";
  protected final String TEXT_14 = ".login();" + NL + "\t\tprocess_";
  protected final String TEXT_15 = " = managementAPI_";
  protected final String TEXT_16 = ".deploy(org.ow2.bonita.util.BusinessArchiveFactory.getBusinessArchive(new java.io.File(";
  protected final String TEXT_17 = ").toURI().toURL()));" + NL + "\t\tprocessDefinitionUUID_";
  protected final String TEXT_18 = " = process_";
  protected final String TEXT_19 = ".getUUID().getValue();" + NL + "\t\t" + NL + "\t\tSystem.out.println(\"**** Process \"+ processDefinitionUUID_";
  protected final String TEXT_20 = " + \" deploy successful ****\");" + NL + "" + NL + "\t} catch (javax.security.auth.login.LoginException le_";
  protected final String TEXT_21 = ") {//just login exception";
  protected final String TEXT_22 = NL + "\t\tthrow le_";
  protected final String TEXT_23 = ";\t\t";
  protected final String TEXT_24 = NL + "\t\tSystem.err.println(le_";
  protected final String TEXT_25 = ".getCause().getMessage());";
  protected final String TEXT_26 = "\t\t" + NL + "\t} catch (Exception e_";
  protected final String TEXT_27 = ") {";
  protected final String TEXT_28 = NL + "\t\tthrow e_";
  protected final String TEXT_29 = ";\t\t";
  protected final String TEXT_30 = NL + "\t\tSystem.err.println(e_";
  protected final String TEXT_31 = ".getMessage());";
  protected final String TEXT_32 = NL + "\t} finally {" + NL + "\t\tloginContext_";
  protected final String TEXT_33 = ".logout();" + NL + "\t}" + NL + "" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_34 = "_ProcessDefinitionUUID\", processDefinitionUUID_";
  protected final String TEXT_35 = "); ";
  protected final String TEXT_36 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String bonitaRuntimePath = ElementParameterParser.getValue(node, "__BONITA_RUNTIME_PATH__");
	String businessArchive = ElementParameterParser.getValue(node, "__BUSINESS_ARCHIVE__");
	String userName = ElementParameterParser.getValue(node, "__USERNAME__");
	String password = ElementParameterParser.getValue(node, "__PASSWORD__");
	
	boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));
	
	boolean useBonitaEnvironmentFile = ("true").equals(ElementParameterParser.getValue(node, "__USE_BONITA_ENVIRONMENT_FILE__"));
	String bonitaEnvironmentFile = ElementParameterParser.getValue(node, "__BONITA_ENVIRONMENT_FILE__");
	boolean useJassFile = ("true").equals(ElementParameterParser.getValue(node, "__USE_JASS_STANDARD_FILE__"));
	String jassFile = ElementParameterParser.getValue(node, "__JASS_STANDARD_FILE__");
	boolean useLoggingFile = ("true").equals(ElementParameterParser.getValue(node, "__USE_LOGGING_FILE__"));
	String loggingFile = ElementParameterParser.getValue(node, "__LOGGING_FILE__");
	String loginModule = ElementParameterParser.getValue(node, "__LOGIN_MODULE__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(bonitaEnvironmentFile);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(jassFile);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(loggingFile);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(loginModule);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(userName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(businessArchive);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
	if (dieOnError) {

    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
	} else {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    	
	}

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    
	if (dieOnError) {

    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    
	} else {

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    	
	}

    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(TEXT_36);
    return stringBuffer.toString();
  }
}
