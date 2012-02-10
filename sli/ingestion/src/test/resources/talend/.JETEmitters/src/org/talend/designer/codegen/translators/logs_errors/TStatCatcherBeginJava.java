package org.talend.designer.codegen.translators.logs_errors;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;

public class TStatCatcherBeginJava
{
  protected static String nl;
  public static synchronized TStatCatcherBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TStatCatcherBeginJava result = new TStatCatcherBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tfor (StatCatcherUtils.StatCatcherMessage scm : ";
  protected final String TEXT_3 = ".getMessages()) {";
  protected final String TEXT_4 = NL + "\t\t";
  protected final String TEXT_5 = ".pid = pid;" + NL + "\t\t";
  protected final String TEXT_6 = ".root_pid = rootPid;" + NL + "\t\t";
  protected final String TEXT_7 = ".father_pid = fatherPid;\t" + NL + "    \t";
  protected final String TEXT_8 = ".project = projectName;" + NL + "    \t";
  protected final String TEXT_9 = ".job = jobName;" + NL + "    \t";
  protected final String TEXT_10 = ".context = contextStr;" + NL + "\t\t";
  protected final String TEXT_11 = ".origin = (scm.getOrigin()==null || scm.getOrigin().length()<1 ? null : scm.getOrigin());" + NL + "\t\t";
  protected final String TEXT_12 = ".message = scm.getMessage();" + NL + "\t\t";
  protected final String TEXT_13 = ".duration = scm.getDuration();" + NL + "\t\t";
  protected final String TEXT_14 = ".moment = scm.getMoment();" + NL + "\t\t";
  protected final String TEXT_15 = ".message_type = scm.getMessageType();" + NL + "\t\t";
  protected final String TEXT_16 = ".job_version = scm.getJobVersion();" + NL + "\t\t";
  protected final String TEXT_17 = ".job_repository_id = scm.getJobId();" + NL + "\t\t";
  protected final String TEXT_18 = ".system_pid = scm.getSystemPid();";
  protected final String TEXT_19 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	for (IConnection conn : node.getOutgoingConnections()) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_18);
    
		}
	}

    stringBuffer.append(TEXT_19);
    return stringBuffer.toString();
  }
}
