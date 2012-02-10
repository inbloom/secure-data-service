package org.talend.designer.codegen.translators.internet.scp;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TSCPDeleteMainJava
{
  protected static String nl;
  public static synchronized TSCPDeleteMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSCPDeleteMainJava result = new TSCPDeleteMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t" + NL + "\t\t//initial the command" + NL + "\t\tStringBuilder command_";
  protected final String TEXT_3 = " = new StringBuilder();" + NL + "\t\tString dir_";
  protected final String TEXT_4 = " = null;";
  protected final String TEXT_5 = NL + "\t\t\t\tdir_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + "\t\t\t\tdir_";
  protected final String TEXT_8 = " = \"\\\"\"+dir_";
  protected final String TEXT_9 = "+\"\\\"\";";
  protected final String TEXT_10 = NL + "         \tcommand_";
  protected final String TEXT_11 = ".append(\"rm \" + dir_";
  protected final String TEXT_12 = ");" + NL + "         \tnb_file_";
  protected final String TEXT_13 = " ++;";
  protected final String TEXT_14 = NL + "         \tif(command_";
  protected final String TEXT_15 = ".length() > 0){" + NL + "         \t\tcommand_";
  protected final String TEXT_16 = ".append(\" \");" + NL + "         \t} " + NL + "            command_";
  protected final String TEXT_17 = ".append(dir_";
  protected final String TEXT_18 = ");" + NL + "            nb_file_";
  protected final String TEXT_19 = " ++;";
  protected final String TEXT_20 = NL + "       " + NL + "\t\t/* Create a session */" + NL + "\t\tch.ethz.ssh2.Session sess_";
  protected final String TEXT_21 = " = conn_";
  protected final String TEXT_22 = ".openSession();" + NL + "\t\t" + NL + "\t\t//execute the command" + NL + "\t\tsess_";
  protected final String TEXT_23 = ".execCommand((command_";
  protected final String TEXT_24 = ").toString());\t\t" + NL + "\t\t" + NL + "\t\t//get the return info" + NL + "\t\t" + NL + "\t\tjava.io.InputStream stderr_";
  protected final String TEXT_25 = " = sess_";
  protected final String TEXT_26 = ".getStderr();" + NL + "        java.io.BufferedReader breer_";
  protected final String TEXT_27 = "= new java.io.BufferedReader(new java.io.InputStreamReader(stderr_";
  protected final String TEXT_28 = "));" + NL + "        String line_err_";
  protected final String TEXT_29 = " = \"\";" + NL + "        StringBuilder stringStderr_";
  protected final String TEXT_30 = " =new StringBuilder();" + NL + "\t\twhile((line_err_";
  protected final String TEXT_31 = " = breer_";
  protected final String TEXT_32 = ".readLine()) != null) {" + NL + "\t\t\tstringStderr_";
  protected final String TEXT_33 = ".append(line_err_";
  protected final String TEXT_34 = " + \"\\n\");" + NL + "\t\t}" + NL + "\t\tString stderrString_";
  protected final String TEXT_35 = " = stringStderr_";
  protected final String TEXT_36 = ".toString();" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_37 = NL + "\t\t" + NL + "\t\t    if(stderrString_";
  protected final String TEXT_38 = ".contains(";
  protected final String TEXT_39 = ")){" + NL + "\t\t    \tnb_file_";
  protected final String TEXT_40 = " --;" + NL + "\t\t    }" + NL + "\t\t";
  protected final String TEXT_41 = NL + "\t\t" + NL + "\t\tbreer_";
  protected final String TEXT_42 = ".close();" + NL + "\t\tstderr_";
  protected final String TEXT_43 = ".close();" + NL + "\t\t" + NL + "\t\t /* Close this session */\t\t\t" + NL + "\t\tsess_";
  protected final String TEXT_44 = ".close();" + NL + "\t\t" + NL + "\t\tif((\"\").equals(stringStderr_";
  protected final String TEXT_45 = ".toString()) || (stringStderr_";
  protected final String TEXT_46 = ".toString() == null)){" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_47 = "_STATUS\",\"File(s) deleted OK\");" + NL + "\t\t}else{" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_48 = "_STATUS\",stringStderr_";
  protected final String TEXT_49 = ".toString());" + NL + "\t\t}";
  protected final String TEXT_50 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
        CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
        INode node = (INode)codeGenArgument.getArgument();
        String cid = node.getUniqueName();
        
        List<Map<String, String>> filelist = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILELIST__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
			for (int i = 0;i<filelist.size();i++) {

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(filelist.get(i).get("SOURCE"));
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
			  if(i==0){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    }else{
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    }
            }
         
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    for (int i = 0;i<filelist.size();i++) {
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(filelist.get(i).get("SOURCE"));
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    }
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(TEXT_50);
    return stringBuffer.toString();
  }
}
