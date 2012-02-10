package org.talend.designer.codegen.translators.databases.sybase;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TSybaseRowEndJava
{
  protected static String nl;
  public static synchronized TSybaseRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSybaseRowEndJava result = new TSybaseRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "        stmt_";
  protected final String TEXT_3 = ".execute(\"SET IDENTITY_INSERT \"+ ";
  protected final String TEXT_4 = " +\" OFF\");";
  protected final String TEXT_5 = NL;
  protected final String TEXT_6 = NL + "\tpstmt_";
  protected final String TEXT_7 = ".close();\t";
  protected final String TEXT_8 = NL + "\tstmt_";
  protected final String TEXT_9 = ".close();\t";
  protected final String TEXT_10 = NL + "    \t    if(commitEvery_";
  protected final String TEXT_11 = ">commitCounter_";
  protected final String TEXT_12 = "){" + NL + "" + NL + "    \t        conn_";
  protected final String TEXT_13 = ".commit();" + NL + "\t" + NL + "    \t        commitCounter_";
  protected final String TEXT_14 = "=0;" + NL + "\t" + NL + "    \t    }" + NL + "    \t    ";
  protected final String TEXT_15 = NL + "    \tconn_";
  protected final String TEXT_16 = " .close();" + NL + "    \t";
  protected final String TEXT_17 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	
	boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));

    String tableName = ElementParameterParser.getValue(node,"__TABLE__");
    String identityInsert = ElementParameterParser.getValue(node, "__IDENTITY_INSERT__");
    boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
    if(("true").equals(identityInsert)) {
        
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(tableName );
    stringBuffer.append(TEXT_4);
    
    }
    
    stringBuffer.append(TEXT_5);
    		
	if (usePrepareStatement) {

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
	} else {

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
	}
    if(!useExistingConn) {
    	if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
    	    
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
		}
    	
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
	}

    stringBuffer.append(TEXT_17);
    return stringBuffer.toString();
  }
}
