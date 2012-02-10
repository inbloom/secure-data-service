package org.talend.designer.codegen.translators.databases.access;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TAccessRowEndJava
{
  protected static String nl;
  public static synchronized TAccessRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAccessRowEndJava result = new TAccessRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = NL + "\t\tpstmt_";
  protected final String TEXT_4 = ".close();\t";
  protected final String TEXT_5 = NL + "\t\tstmt_";
  protected final String TEXT_6 = ".close();";
  protected final String TEXT_7 = NL + "        conn_";
  protected final String TEXT_8 = ".commit();";
  protected final String TEXT_9 = NL + "    conn_";
  protected final String TEXT_10 = " .close();" + NL + "\t";
  protected final String TEXT_11 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
	boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
	
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    stringBuffer.append(TEXT_2);
    		
	if(usePrepareStatement){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
	}else{

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
	}

    
	if(!useExistingConnection)
	{
        if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){
        
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
    	}
    	
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
	}
    
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
