package org.talend.designer.codegen.translators.logs_errors;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TWarnMainJava
{
  protected static String nl;
  public static synchronized TWarnMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWarnMainJava result = new TWarnMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = "\t" + NL + "\tresumeUtil.addLog(\"USER_DEF_LOG\", \"NODE:";
  protected final String TEXT_4 = "\", \"\", Thread.currentThread().getId() + \"\", \"";
  protected final String TEXT_5 = "\",\"\",";
  protected final String TEXT_6 = ",\"\", \"\");";
  protected final String TEXT_7 = NL + "\t";
  protected final String TEXT_8 = ".addMessage(\"tWarn\", \"";
  protected final String TEXT_9 = "\", ";
  protected final String TEXT_10 = ", ";
  protected final String TEXT_11 = ", ";
  protected final String TEXT_12 = ");" + NL + "\t";
  protected final String TEXT_13 = "Process(globalMap);";
  protected final String TEXT_14 = NL + "globalMap.put(\"";
  protected final String TEXT_15 = "_WARN_MESSAGES\", ";
  protected final String TEXT_16 = "); " + NL + "globalMap.put(\"";
  protected final String TEXT_17 = "_WARN_PRIORITY\", ";
  protected final String TEXT_18 = ");" + NL + "globalMap.put(\"";
  protected final String TEXT_19 = "_WARN_CODE\", ";
  protected final String TEXT_20 = ");" + NL;
  protected final String TEXT_21 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    stringBuffer.append(TEXT_2);
    
String priority = ElementParameterParser.getValue(node, "__PRIORITY__");
String label = "WARN";
if(priority.equals("1")){
	label = "TRACE";
}else if(priority.equals("2")){
	label = "DEBUG";
}else if(priority.equals("3")){
	label = "INFO";
}else if(priority.equals("4")){
	label = "WARN";
}else if(priority.equals("5")){
	label = "ERROR";
}else if(priority.equals("6")){
	label = "FATAL";
}

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(label );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(ElementParameterParser.getValue(node, "__MESSAGE__") );
    stringBuffer.append(TEXT_6);
    
			if (node.getProcess().getNodesOfType("tLogCatcher").size() > 0) {
				List<INode> logCatchers = (List<INode>)node.getProcess().getNodesOfType("tLogCatcher");
				for (INode logCatcher : logCatchers) {
					if (("true").equals(ElementParameterParser.getValue(logCatcher, "__CATCH_TWARN__"))) {

    stringBuffer.append(TEXT_7);
    stringBuffer.append(logCatcher.getUniqueName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(ElementParameterParser.getValue(node, "__PRIORITY__") );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(ElementParameterParser.getValue(node, "__MESSAGE__") );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(ElementParameterParser.getValue(node, "__CODE__") );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(logCatcher.getDesignSubjobStartNode().getUniqueName() );
    stringBuffer.append(TEXT_13);
    
					}
				}
			}

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(ElementParameterParser.getValue(node, "__MESSAGE__"));
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(ElementParameterParser.getValue(node, "__PRIORITY__") );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(ElementParameterParser.getValue(node, "__CODE__") );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
