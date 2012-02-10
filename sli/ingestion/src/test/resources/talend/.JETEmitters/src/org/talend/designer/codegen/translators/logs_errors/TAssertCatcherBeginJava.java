package org.talend.designer.codegen.translators.logs_errors;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;

public class TAssertCatcherBeginJava
{
  protected static String nl;
  public static synchronized TAssertCatcherBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAssertCatcherBeginJava result = new TAssertCatcherBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tfor (AssertCatcherUtils.AssertCatcherMessage acm : ";
  protected final String TEXT_3 = ".getMessages()) {";
  protected final String TEXT_4 = NL + "\t\t";
  protected final String TEXT_5 = ".moment = acm.getMoment();" + NL + "\t\t";
  protected final String TEXT_6 = ".pid = acm.getPid();" + NL + "\t\t";
  protected final String TEXT_7 = ".project = acm.getProject();" + NL + "\t\t";
  protected final String TEXT_8 = ".job = acm.getJob();" + NL + "\t\t";
  protected final String TEXT_9 = ".language = acm.getLanguage();" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_10 = ".origin = (acm.getOrigin()==null || acm.getOrigin().length()<1 ? null : acm.getOrigin());" + NL + "\t" + NL + "    \t";
  protected final String TEXT_11 = ".status = acm.getStatus();" + NL + "\t\t";
  protected final String TEXT_12 = ".substatus = acm.getSubstatus();" + NL + "\t\t";
  protected final String TEXT_13 = ".description = acm.getDescription();";

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
    
		}
	}

    return stringBuffer.toString();
  }
}
