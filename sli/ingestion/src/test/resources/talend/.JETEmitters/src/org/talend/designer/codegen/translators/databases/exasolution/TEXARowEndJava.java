package org.talend.designer.codegen.translators.databases.exasolution;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TEXARowEndJava
{
  protected static String nl;
  public static synchronized TEXARowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TEXARowEndJava result = new TEXARowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    stmt_";
  protected final String TEXT_3 = ".close();\t" + NL + "\t";
  protected final String TEXT_4 = NL + "            if(commitEvery_";
  protected final String TEXT_5 = " > commitCounter_";
  protected final String TEXT_6 = "){            " + NL + "            \tconn_";
  protected final String TEXT_7 = ".commit();            \t" + NL + "            \tcommitCounter_";
  protected final String TEXT_8 = "=0;            \t" + NL + "            }";
  protected final String TEXT_9 = NL + "        conn_";
  protected final String TEXT_10 = ".close();\t    " + NL + "\t    ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	if(!useExistingConn.equals("true")) {	
	    if(!commitEvery.equals("") && !commitEvery.equals("0")) {
	        
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
	    }
	    
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
	}

    return stringBuffer.toString();
  }
}
