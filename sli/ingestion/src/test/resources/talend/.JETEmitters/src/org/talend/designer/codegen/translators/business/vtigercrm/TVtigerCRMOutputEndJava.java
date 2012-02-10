package org.talend.designer.codegen.translators.business.vtigercrm;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TVtigerCRMOutputEndJava
{
  protected static String nl;
  public static synchronized TVtigerCRMOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TVtigerCRMOutputEndJava result = new TVtigerCRMOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "        vtigerManager_";
  protected final String TEXT_2 = ".";
  protected final String TEXT_3 = "(clndrDetais_";
  protected final String TEXT_4 = ".toArray(new org.talend.vtiger.module.outlook.Clndrdetail[clndrDetais_";
  protected final String TEXT_5 = ".size()]));";
  protected final String TEXT_6 = NL + "        vtigerManager_";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = "(contactdetails_";
  protected final String TEXT_9 = ".toArray(new org.talend.vtiger.module.outlook.Contactdetail[contactdetails_";
  protected final String TEXT_10 = ".size()]));";
  protected final String TEXT_11 = NL + "        vtigerManager_";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = "(taskdetails_";
  protected final String TEXT_14 = ".toArray(new org.talend.vtiger.module.outlook.Taskdetail[taskdetails_";
  protected final String TEXT_15 = ".size()]));";
  protected final String TEXT_16 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String version_selection = ElementParameterParser.getValue(node, "__VERSION_SELECTION__");
if(version_selection.equals("VTIGER_50")){
    String method = ElementParameterParser.getValue(node, "__METHODNAME__");
    if(("addClndr").equals(method) || ("updateClndr").equals(method)) {
        
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
    } else if(("addContacts").equals(method) || ("updateContacts").equals(method)) {
        
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
    } else if(("addTasks").equals(method) || ("updateTasks").equals(method)) {
        
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    
    }
}

    stringBuffer.append(TEXT_16);
    return stringBuffer.toString();
  }
}
