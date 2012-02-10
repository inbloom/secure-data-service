package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TSCPFileListEndJava
{
  protected static String nl;
  public static synchronized TSCPFileListEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPFileListEndJava result = new TSCPFileListEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "    \t\t}" + NL + "    \t\t" + NL + "    \t    brout_";
  protected final String TEXT_3 = ".close();" + NL + "    \t\tstdout_";
  protected final String TEXT_4 = ".close();" + NL + "    \t\t" + NL + "    \t\tjava.io.InputStream stderr_";
  protected final String TEXT_5 = " = sess_";
  protected final String TEXT_6 = ".getStderr();" + NL + "            java.io.BufferedReader breer_";
  protected final String TEXT_7 = "= new java.io.BufferedReader(new java.io.InputStreamReader(stderr_";
  protected final String TEXT_8 = "));" + NL + "            String line_err_";
  protected final String TEXT_9 = " = \"\";" + NL + "            StringBuilder stringStderr_";
  protected final String TEXT_10 = " =new StringBuilder();" + NL + "    \t\twhile((line_err_";
  protected final String TEXT_11 = " = breer_";
  protected final String TEXT_12 = ".readLine()) != null) {" + NL + "    \t\t\tstringStderr_";
  protected final String TEXT_13 = ".append(line_err_";
  protected final String TEXT_14 = " + \"\\n\");" + NL + "    \t\t}" + NL + "    \t\tbreer_";
  protected final String TEXT_15 = ".close();" + NL + "    \t\tstderr_";
  protected final String TEXT_16 = ".close();" + NL + "" + NL + "    \t\t /* Close this session */\t\t\t" + NL + "    \t\tsess_";
  protected final String TEXT_17 = ".close();\t\t" + NL;
  protected final String TEXT_18 = "  " + NL + "            /* Close the connection */" + NL + "            conn_";
  protected final String TEXT_19 = ".close();";
  protected final String TEXT_20 = NL + "            if((\"\").equals(stringStderr_";
  protected final String TEXT_21 = ")||stringStderr_";
  protected final String TEXT_22 = " == null){" + NL + "            \tglobalMap.put(\"";
  protected final String TEXT_23 = "_STATUS\",stringStdout_";
  protected final String TEXT_24 = ".toString());" + NL + "            }else{" + NL + "            \tglobalMap.put(\"";
  protected final String TEXT_25 = "_STATUS\",stringStderr_";
  protected final String TEXT_26 = ".toString());" + NL + "            }" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_27 = "_NB_LINE\", nb_line_";
  protected final String TEXT_28 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
            CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
            INode node = (INode)codeGenArgument.getArgument();
            String cid = node.getUniqueName();
            String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
	if(!("true").equals(useExistingConn)){

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
      }
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    return stringBuffer.toString();
  }
}
