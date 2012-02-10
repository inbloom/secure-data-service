package org.talend.designer.codegen.translators.databases.db_jdbc;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TJDBCRowEndJava
{
  protected static String nl;
  public static synchronized TJDBCRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJDBCRowEndJava result = new TJDBCRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tpstmt_";
  protected final String TEXT_3 = ".close();\t";
  protected final String TEXT_4 = NL + "stmt_";
  protected final String TEXT_5 = ".close();";
  protected final String TEXT_6 = NL + "    \tif (commitEvery_";
  protected final String TEXT_7 = " > commitCounter_";
  protected final String TEXT_8 = ") {" + NL + "    \t\tconnection_";
  protected final String TEXT_9 = ".commit();" + NL + "    \t\tcommitCounter_";
  protected final String TEXT_10 = " = 0;" + NL + "    \t}" + NL + "    \t";
  protected final String TEXT_11 = NL + "\tconnection_";
  protected final String TEXT_12 = ".close();" + NL + "\t";
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode) codeGenArgument.getArgument();
String cid = node.getUniqueName();
	String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    		
	if (usePrepareStatement) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	} else {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
	}
if(("false").equals(useExistingConn)){
	if (!("").equals(commitEvery) && !("0").equals(commitEvery)) {
	    
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    
	}
	
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
