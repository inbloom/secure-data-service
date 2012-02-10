package org.talend.designer.codegen.translators.cloud.amazonrds.mysql;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TAmazonMysqlRowEndJava
{
  protected static String nl;
  public static synchronized TAmazonMysqlRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAmazonMysqlRowEndJava result = new TAmazonMysqlRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = NL + "\tpstmt_";
  protected final String TEXT_4 = ".close();\t";
  protected final String TEXT_5 = NL + "\tstmt_";
  protected final String TEXT_6 = ".close();\t";
  protected final String TEXT_7 = NL + "            if(commitEvery_";
  protected final String TEXT_8 = " > commitCounter_";
  protected final String TEXT_9 = ") {" + NL + "            " + NL + "            \tconn_";
  protected final String TEXT_10 = ".commit();" + NL + "            \t" + NL + "            \tcommitCounter_";
  protected final String TEXT_11 = " = 0;" + NL + "            \t" + NL + "            }";
  protected final String TEXT_12 = NL + "    \tconn_";
  protected final String TEXT_13 = " .close();" + NL + "    \t";
  protected final String TEXT_14 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
	String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    stringBuffer.append(TEXT_2);
    		
	if (usePrepareStatement) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
	} else {

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
	}
	if(!("true").equals(useExistingConn)) {
    	if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
            
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
        }
    	
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
    }

    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
