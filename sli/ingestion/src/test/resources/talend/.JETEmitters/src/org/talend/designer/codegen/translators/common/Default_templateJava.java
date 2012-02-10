package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.temp.ECodePart;

public class Default_templateJava
{
  protected static String nl;
  public static synchronized Default_templateJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Default_templateJava result = new Default_templateJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "The ";
  protected final String TEXT_2 = " part of ";
  protected final String TEXT_3 = " compile with error, it can't generate any code, please check and correct it." + NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	Object object = codeGenArgument.getArgument();
	
	//This is only for the component, when it compile error, there will call this template file to generate.
	//If the files in /resources compile error, ignore them.
	if(object instanceof INode)	{
	
    	INode node = (INode)codeGenArgument.getArgument();
    	String cid = node.getUniqueName();
    	ECodePart codePart = codeGenArgument.getCodePart();
    	String part = codePart.getName();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(part );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	}

    return stringBuffer.toString();
  }
}
