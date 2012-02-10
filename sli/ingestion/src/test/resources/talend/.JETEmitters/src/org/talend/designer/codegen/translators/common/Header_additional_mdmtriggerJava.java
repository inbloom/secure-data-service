package org.talend.designer.codegen.translators.common;

import java.util.List;
import java.util.Vector;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IProcess;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class Header_additional_mdmtriggerJava
{
  protected static String nl;
  public static synchronized Header_additional_mdmtriggerJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Header_additional_mdmtriggerJava result = new Header_additional_mdmtriggerJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = "\t" + NL + "\t" + NL + "\t" + NL + "\t// These fields are defined for \"MDM triggers\"  " + NL + "\tprivate routines.system.Document MDMOutputMessage = null;" + NL + "" + NL + "\tpublic org.dom4j.Document getMDMOutputMessage() {" + NL + "\t\tif(this.MDMOutputMessage != null)" + NL + "\t\t\treturn this.MDMOutputMessage.getDocument();" + NL + "\t\telse" + NL + "\t\t\treturn null;" + NL + "\t}" + NL + "" + NL + "\tprivate routines.system.Document MDMInputMessage = null;" + NL + "" + NL + "\tpublic void setMDMInputMessage(org.dom4j.Document message) {" + NL + "\t\tif(message != null)" + NL + "\t\t\tthis.MDMInputMessage.setDocument(message);" + NL + "\t}" + NL + "" + NL + "\tpublic void setMDMInputMessage(String message) throws org.dom4j.DocumentException {" + NL + "\t\ttry {" + NL + "\t\t\tthis.MDMInputMessage = ParserUtils.parseTo_Document(message);" + NL + "\t\t} catch (org.dom4j.DocumentException e) {" + NL + "\t\t\tthrow new org.dom4j.DocumentException(e);" + NL + "\t\t}" + NL + "\t}" + NL + "" + NL + "\t// End of MDM trigger fields" + NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
boolean withMDMTrigger = false;

CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
Vector v = (Vector) codeGenArgument.getArgument();
IProcess process = (IProcess)v.get(0);

List<? extends INode> tMDMTriggerInputList = process.getNodesOfType("tMDMTriggerInput");
List<? extends INode> tMDMTriggerOutputList = process.getNodesOfType("tMDMTriggerOutput");

if((tMDMTriggerInputList != null && tMDMTriggerInputList.size() > 0) || (tMDMTriggerOutputList != null && tMDMTriggerOutputList.size() > 0)){
	withMDMTrigger = true;
}

if(withMDMTrigger){

    stringBuffer.append(TEXT_2);
    
}

    return stringBuffer.toString();
  }
}
