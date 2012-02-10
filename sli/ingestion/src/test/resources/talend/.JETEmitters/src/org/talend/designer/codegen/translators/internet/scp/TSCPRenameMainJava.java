package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TSCPRenameMainJava
{
  protected static String nl;
  public static synchronized TSCPRenameMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPRenameMainJava result = new TSCPRenameMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t" + NL + "\t\t//initial the command" + NL + "\t\tStringBuilder command_";
  protected final String TEXT_3 = " = new StringBuilder();" + NL + "        command_";
  protected final String TEXT_4 = ".append(\"mv \" + (";
  protected final String TEXT_5 = ") + \" \" + (";
  protected final String TEXT_6 = "));" + NL + "         " + NL + "       " + NL + "\t\t/* Create a session */" + NL + "\t\tch.ethz.ssh2.Session sess_";
  protected final String TEXT_7 = " = conn_";
  protected final String TEXT_8 = ".openSession();" + NL + "\t\t" + NL + "\t\t//execute the command" + NL + "\t\tsess_";
  protected final String TEXT_9 = ".execCommand((command_";
  protected final String TEXT_10 = ").toString());\t\t" + NL + "\t\t" + NL + "\t\t//get the return info" + NL + "\t\t" + NL + "\t\tjava.io.InputStream stderr_";
  protected final String TEXT_11 = " = sess_";
  protected final String TEXT_12 = ".getStderr();" + NL + "        java.io.BufferedReader breer_";
  protected final String TEXT_13 = "= new java.io.BufferedReader(new java.io.InputStreamReader(stderr_";
  protected final String TEXT_14 = "));" + NL + "        String line_err_";
  protected final String TEXT_15 = " = \"\";" + NL + "        StringBuilder stringStderr_";
  protected final String TEXT_16 = " =new StringBuilder();" + NL + "\t\twhile((line_err_";
  protected final String TEXT_17 = " = breer_";
  protected final String TEXT_18 = ".readLine()) != null) {" + NL + "\t\t\tstringStderr_";
  protected final String TEXT_19 = ".append(line_err_";
  protected final String TEXT_20 = " + \"\\n\");" + NL + "\t\t}" + NL + "\t\tbreer_";
  protected final String TEXT_21 = ".close();" + NL + "\t\tstderr_";
  protected final String TEXT_22 = ".close();" + NL + "\t\t" + NL + "\t\t /* Close this session */\t\t\t" + NL + "\t\tsess_";
  protected final String TEXT_23 = ".close();" + NL + "\t\t" + NL + "\t\tif((\"\").equals(stringStderr_";
  protected final String TEXT_24 = ".toString()) || (stringStderr_";
  protected final String TEXT_25 = ".toString() == null)){" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_26 = "_STATUS\",\"File rename OK\");" + NL + "\t\t}else{" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_27 = "_STATUS\",stringStderr_";
  protected final String TEXT_28 = ".toString());" + NL + "\t\t}";
  protected final String TEXT_29 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
        CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
        INode node = (INode)codeGenArgument.getArgument();
        String cid = node.getUniqueName();
       	String fromName = ElementParameterParser.getValue(node, "__FROMNAME__");
       	String toName = ElementParameterParser.getValue(node,"__TONAME__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(fromName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(toName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(TEXT_29);
    return stringBuffer.toString();
  }
}
