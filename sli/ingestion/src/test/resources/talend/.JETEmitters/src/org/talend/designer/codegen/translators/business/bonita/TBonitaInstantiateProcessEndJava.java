package org.talend.designer.codegen.translators.business.bonita;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;

public class TBonitaInstantiateProcessEndJava
{
  protected static String nl;
  public static synchronized TBonitaInstantiateProcessEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TBonitaInstantiateProcessEndJava result = new TBonitaInstantiateProcessEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "\t" + NL + "try {" + NL + "\tloginContext_";
  protected final String TEXT_3 = ".logout();" + NL + "} catch (javax.security.auth.login.LoginException lee_";
  protected final String TEXT_4 = ") {";
  protected final String TEXT_5 = NL + "\t\tthrow lee_";
  protected final String TEXT_6 = ";\t";
  protected final String TEXT_7 = NL + "\t\tSystem.err.println(lee_";
  protected final String TEXT_8 = ".getMessage());";
  protected final String TEXT_9 = NL + "}";
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
	if (dieOnError) {

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
	} else {

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    	
	}

    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
