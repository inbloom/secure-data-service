package org.talend.designer.codegen.translators.databases.firebird;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFirebirdRowEndJava
{
  protected static String nl;
  public static synchronized TFirebirdRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFirebirdRowEndJava result = new TFirebirdRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tpstmt_";
  protected final String TEXT_3 = ".close();\t";
  protected final String TEXT_4 = NL + "\t\tstmt_";
  protected final String TEXT_5 = ".close();";
  protected final String TEXT_6 = NL + "    if(conn_";
  protected final String TEXT_7 = " != null && !conn_";
  protected final String TEXT_8 = ".isClosed()) {";
  protected final String TEXT_9 = NL + "            if(commitEvery_";
  protected final String TEXT_10 = " > commitCounter_";
  protected final String TEXT_11 = ") {" + NL + "        " + NL + "                conn_";
  protected final String TEXT_12 = ".commit();" + NL + "        " + NL + "                commitCounter_";
  protected final String TEXT_13 = "=0;" + NL + "        " + NL + "            }";
  protected final String TEXT_14 = NL + "        conn_";
  protected final String TEXT_15 = " .close();" + NL + "    }";
  protected final String TEXT_16 = NL;

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

    		
	if (usePrepareStatement) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	} else {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
	}
if(!("true").equals(useExistingConn)) {
    
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
        if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
            
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    
        }
        
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    
}

    stringBuffer.append(TEXT_16);
    return stringBuffer.toString();
  }
}
