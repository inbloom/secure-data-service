package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class Component_part_endmainJava
{
  protected static String nl;
  public static synchronized Component_part_endmainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Component_part_endmainJava result = new Component_part_endmainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "if(!isChildJob && (Boolean)globalMap.get(\"ENABLE_TRACES_CONNECTION_";
  protected final String TEXT_3 = "\")){" + NL + "\tif (globalMap.get(\"USE_CONDITION\") != null && (Boolean) globalMap.get(\"USE_CONDITION\")) {\t    " + NL + "\t\tif (globalMap.get(\"TRACE_CONDITION\") != null && (Boolean) globalMap.get(\"TRACE_CONDITION\")) {" + NL + "\t\t    // if next breakpoint has been clicked on UI or if start job, should wait action of user." + NL + "            if (runTrace.isNextBreakpoint()) {" + NL + "            \trunTrace.waitForUserAction();" + NL + "            } else if (runTrace.isNextRow()) {" + NL + "            \trunTrace.waitForUserAction();" + NL + "            }" + NL + "\t\t} else {" + NL + "\t\t    // if next row has been clicked on UI or if start job, should wait action of user." + NL + "            if (runTrace.isNextRow()) {" + NL + "            \trunTrace.waitForUserAction();" + NL + "            }" + NL + "\t\t}" + NL + "\t} else { // no condition set" + NL + "        if (runTrace.isNextRow()) {" + NL + "           runTrace.waitForUserAction();" + NL + "        } else {" + NL + "\t\t   Thread.sleep(";
  protected final String TEXT_4 = ");" + NL + "\t\t}" + NL + "\t}" + NL + "" + NL + "}" + NL + "globalMap.put(\"USE_CONDITION\",Boolean.FALSE);";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String startNodeCid=node.getDesignSubjobStartNode().getUniqueName();
	boolean trace =  codeGenArgument.isTrace();
	int pauseTime = codeGenArgument.getPauseTime();

    stringBuffer.append(TEXT_1);
    
if ((trace)&&(node!=null)&&(!node.isThereLinkWithHash())) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(startNodeCid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(pauseTime);
    stringBuffer.append(TEXT_4);
    
}

    return stringBuffer.toString();
  }
}
