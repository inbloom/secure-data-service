package org.talend.designer.codegen.translators.system;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.designer.runprocess.ProcessorException;

public class TRunJobBeginJava
{
  protected static String nl;
  public static synchronized TRunJobBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRunJobBeginJava result = new TRunJobBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t//For different jobs, job name must be different, but classpath and JVM arguments are possbilely different" + NL + "\t\tjava.util.Map<String,List> childJob_commandLine_Mapper_";
  protected final String TEXT_2 = " = new java.util.HashMap<String,List>();" + NL + "\t\tjava.util.List<String> childJob_commandLine_";
  protected final String TEXT_3 = " = null;" + NL + "\t\tString classpathSeparator_";
  protected final String TEXT_4 = " = System.getProperty(\"path.separator\");" + NL + "\t\t";
  protected final String TEXT_5 = NL + "\t\t\t\tchildJob_commandLine_";
  protected final String TEXT_6 = " = new java.util.ArrayList<String>();" + NL + "\t\t\t\t";
  protected final String TEXT_7 = NL + "\t\t\t\t    childJob_commandLine_";
  protected final String TEXT_8 = ".add(\"";
  protected final String TEXT_9 = "\");" + NL + "\t\t\t\t    ";
  protected final String TEXT_10 = NL + "\t\t\t\t      childJob_commandLine_";
  protected final String TEXT_11 = ".add(";
  protected final String TEXT_12 = ".replaceAll(\";\",classpathSeparator_";
  protected final String TEXT_13 = "));" + NL + "\t\t\t\t    ";
  protected final String TEXT_14 = NL + "\t\t\t\t      childJob_commandLine_";
  protected final String TEXT_15 = ".add(\"";
  protected final String TEXT_16 = "\".replaceAll(\";\",classpathSeparator_";
  protected final String TEXT_17 = "));" + NL + "\t\t\t\t    ";
  protected final String TEXT_18 = NL + "\t\t\t\tchildJob_commandLine_Mapper_";
  protected final String TEXT_19 = ".put(\"";
  protected final String TEXT_20 = "\",childJob_commandLine_";
  protected final String TEXT_21 = ");\t" + NL + "\t\t\t";
  protected final String TEXT_22 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String process = ElementParameterParser.getValue(node,"__PROCESS_TYPE_PROCESS__");
String context = ElementParameterParser.getValue(node,"__PROCESS_TYPE_CONTEXT__");
String[] codeOptions = new String[] {"\"--father_pid=\"+pid", "\"--root_pid=\"+rootPid", "\"--father_node="+ cid + "\""};

String[] commandLine = new String[] {"<command>"};

boolean useDynamicJob = ("true").equals(ElementParameterParser.getValue(node, "__USE_DYNAMIC_JOB__"));

String childJobName;
int jobNamePosition;

    
try {
	if(useDynamicJob){
	
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
		if(process!=null && !process.equals("")){
			String[] childJobIds = process.split(";");
			for (int i=0;i<childJobIds.length;i++) {
			
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
			    commandLine = ProcessorUtilities.getCommandLine("win32",true, childJobIds[i], context,org.talend.designer.runprocess.IProcessor.NO_STATISTICS,org.talend.designer.runprocess.IProcessor.NO_TRACES, codeOptions);
			    // remove the frontal 2 lines
			    if (commandLine.length > 0 && ProcessorUtilities.isExportConfig()){
					int tmpSize = commandLine.length - 2;
					String[] tmp = new String[tmpSize];
					System.arraycopy(commandLine, 2, tmp, 0, tmpSize);
					commandLine = tmp;
			    }
			    for (int j = 0; j < commandLine.length; j++) {
				  // commandLine[j] = commandLine[j].replace("\n", "");
				  if (j == 0){
				    
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(commandLine[j]);
    stringBuffer.append(TEXT_9);
    
				  } else if (j > 0){
				    if (commandLine[j].indexOf("\"") >= 0){
				      
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(commandLine[j] );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    }else{
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(commandLine[j] );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    }
				  }
				}
				
				//find the job name and classpath, the job name in front of codeOptions
			    int position = 0;
			    for(String tempCommandLine : commandLine){
			   		if(codeOptions[0].equals(tempCommandLine)){
			   			break;
			   		}
			   		position++;
			    }
			    jobNamePosition = position-1;
			    
			    childJobName = commandLine[jobNamePosition];
			    childJobName = childJobName.substring(childJobName.lastIndexOf(".")+1);
				
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(childJobName);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
			}
		}
	}
} catch (ProcessorException e) {
}	

    stringBuffer.append(TEXT_22);
    return stringBuffer.toString();
  }
}
