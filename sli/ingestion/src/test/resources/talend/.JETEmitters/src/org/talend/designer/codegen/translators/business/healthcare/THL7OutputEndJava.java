package org.talend.designer.codegen.translators.business.healthcare;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class THL7OutputEndJava
{
  protected static String nl;
  public static synchronized THL7OutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    THL7OutputEndJava result = new THL7OutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.io.OutputStream os_";
  protected final String TEXT_3 = " = null;" + NL + "Object file_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = "; " + NL + "if(file_";
  protected final String TEXT_6 = " instanceof java.io.OutputStream){" + NL + "\tos_";
  protected final String TEXT_7 = " = (java.io.OutputStream)file_";
  protected final String TEXT_8 = ";" + NL + "}else{" + NL + "    String fileName_";
  protected final String TEXT_9 = " = (String)file_";
  protected final String TEXT_10 = ";";
  protected final String TEXT_11 = "         " + NL + "    String directory_";
  protected final String TEXT_12 = " = null;" + NL + "    if((fileName_";
  protected final String TEXT_13 = ".indexOf(\"/\") != -1)) {  " + NL + "        directory_";
  protected final String TEXT_14 = " = fileName_";
  protected final String TEXT_15 = ".substring(0, fileName_";
  protected final String TEXT_16 = ".lastIndexOf(\"/\"));            " + NL + "    } else {" + NL + "        directory_";
  protected final String TEXT_17 = " = \"\";" + NL + "    }" + NL + "    //create directory only if not exists" + NL + "    if(directory_";
  protected final String TEXT_18 = " != null && directory_";
  protected final String TEXT_19 = ".trim().length() != 0) {" + NL + "        java.io.File dir_";
  protected final String TEXT_20 = " = new java.io.File(directory_";
  protected final String TEXT_21 = ");" + NL + "        if(!dir_";
  protected final String TEXT_22 = ".exists()) {" + NL + "            dir_";
  protected final String TEXT_23 = ".mkdirs();" + NL + "        }" + NL + "    }";
  protected final String TEXT_24 = NL + "\tos_";
  protected final String TEXT_25 = " = new java.io.FileOutputStream(fileName_";
  protected final String TEXT_26 = ");" + NL + "}" + NL + "String resultMsg_";
  protected final String TEXT_27 = " = msg_";
  protected final String TEXT_28 = ".encode();" + NL + "java.io.Writer out";
  protected final String TEXT_29 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(os_";
  protected final String TEXT_30 = ",";
  protected final String TEXT_31 = "));" + NL + "out";
  protected final String TEXT_32 = ".write(resultMsg_";
  protected final String TEXT_33 = ");" + NL + "if(!(file_";
  protected final String TEXT_34 = " instanceof java.io.OutputStream)){" + NL + "\tout";
  protected final String TEXT_35 = ".close();" + NL + "}else{" + NL + "\tout";
  protected final String TEXT_36 = ".flush();" + NL + "\tos_";
  protected final String TEXT_37 = ".flush();" + NL + "}" + NL;
  protected final String TEXT_38 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String fileName = ElementParameterParser.getValue(node,"__FILENAME__");
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(fileName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
if(("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))){

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
    
}

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(TEXT_38);
    return stringBuffer.toString();
  }
}
