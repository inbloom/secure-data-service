package org.talend.designer.codegen.translators.logs_errors;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;

public class TFlowMeterCatcherBeginJava
{
  protected static String nl;
  public static synchronized TFlowMeterCatcherBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFlowMeterCatcherBeginJava result = new TFlowMeterCatcherBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tfor (MetterCatcherUtils.MetterCatcherMessage mcm : ";
  protected final String TEXT_3 = ".getMessages()) {";
  protected final String TEXT_4 = NL + "\t\t";
  protected final String TEXT_5 = ".pid = pid;" + NL + "\t\t";
  protected final String TEXT_6 = ".root_pid = rootPid;" + NL + "\t\t";
  protected final String TEXT_7 = ".father_pid = fatherPid;\t";
  protected final String TEXT_8 = NL + "        ";
  protected final String TEXT_9 = ".project = projectName;";
  protected final String TEXT_10 = NL + "        ";
  protected final String TEXT_11 = ".job = jobName;";
  protected final String TEXT_12 = NL + "        ";
  protected final String TEXT_13 = ".context = contextStr;" + NL + "\t\t";
  protected final String TEXT_14 = ".origin = (mcm.getOrigin()==null || mcm.getOrigin().length()<1 ? null : mcm.getOrigin());" + NL + "\t\t";
  protected final String TEXT_15 = ".moment = mcm.getMoment();" + NL + "\t\t";
  protected final String TEXT_16 = ".job_version = mcm.getJobVersion();" + NL + "\t\t";
  protected final String TEXT_17 = ".job_repository_id = mcm.getJobId();" + NL + "\t\t";
  protected final String TEXT_18 = ".system_pid = mcm.getSystemPid();" + NL + "\t\t";
  protected final String TEXT_19 = ".label = mcm.getLabel();" + NL + "\t\t";
  protected final String TEXT_20 = ".count = mcm.getCount();" + NL + "\t\t";
  protected final String TEXT_21 = ".reference = ";
  protected final String TEXT_22 = ".getConnLinesCount(mcm.getReferense()+\"_count\");" + NL + "\t\t";
  protected final String TEXT_23 = ".thresholds = mcm.getThresholds();" + NL + "\t\t";
  protected final String TEXT_24 = NL;

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
    
	for (IConnection conn : node.getOutgoingSortedConnections()) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.MAIN)) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
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
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_23);
    
		}
	}

    stringBuffer.append(TEXT_24);
    return stringBuffer.toString();
  }
}
