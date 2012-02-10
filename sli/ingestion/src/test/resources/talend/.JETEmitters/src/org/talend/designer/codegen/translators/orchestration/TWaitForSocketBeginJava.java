package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TWaitForSocketBeginJava
{
  protected static String nl;
  public static synchronized TWaitForSocketBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWaitForSocketBeginJava result = new TWaitForSocketBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "java.net.Socket socket";
  protected final String TEXT_3 = ";" + NL + "java.io.PrintWriter out";
  protected final String TEXT_4 = ";" + NL + "java.io.BufferedReader in";
  protected final String TEXT_5 = ";" + NL + "" + NL + "java.net.ServerSocket ss";
  protected final String TEXT_6 = " = new java.net.ServerSocket(";
  protected final String TEXT_7 = ");" + NL + "boolean ifContinue";
  protected final String TEXT_8 = " = true;" + NL + "while (ifContinue";
  protected final String TEXT_9 = ") {" + NL + "    socket";
  protected final String TEXT_10 = " = ss";
  protected final String TEXT_11 = ".accept();    " + NL + "   // new SocketDataProcess(socket);" + NL + "    try{       " + NL + "        //write data to client" + NL + "        out";
  protected final String TEXT_12 = " = new java.io.PrintWriter(socket";
  protected final String TEXT_13 = ".getOutputStream(), true);" + NL + "        out";
  protected final String TEXT_14 = ".println(";
  protected final String TEXT_15 = "); " + NL + "        " + NL + "        in";
  protected final String TEXT_16 = " = new java.io.BufferedReader(new java.io.InputStreamReader(socket";
  protected final String TEXT_17 = ".getInputStream(),";
  protected final String TEXT_18 = "));" + NL + "        StringBuilder sb";
  protected final String TEXT_19 = " = new StringBuilder();" + NL + "        char[] target_";
  protected final String TEXT_20 = " = new char[1024];" + NL + "        int length_";
  protected final String TEXT_21 = " = -1;" + NL + "\t\twhile((length_";
  protected final String TEXT_22 = " = in";
  protected final String TEXT_23 = ".read(target_";
  protected final String TEXT_24 = ")) != -1){" + NL + "\t\t\tsb";
  protected final String TEXT_25 = ".append(new String(target_";
  protected final String TEXT_26 = ",0,length_";
  protected final String TEXT_27 = "));" + NL + "\t\t}";
  protected final String TEXT_28 = NL + "        //get data from client" + NL + "            System.out.println(socket";
  protected final String TEXT_29 = ".getInetAddress()+\" input is : \");" + NL + "            System.out.println(sb";
  protected final String TEXT_30 = ".toString().trim());";
  protected final String TEXT_31 = NL + "        //put in globalMap " + NL + "        globalMap.put(\"";
  protected final String TEXT_32 = "_INPUT_DATA\",sb";
  protected final String TEXT_33 = ".toString().trim());" + NL + "        in";
  protected final String TEXT_34 = ".close();" + NL + "        out";
  protected final String TEXT_35 = ".close();" + NL + "        socket";
  protected final String TEXT_36 = ".close();" + NL + "    }catch (java.io.IOException e) {" + NL + "       e.printStackTrace();" + NL + "    }   " + NL + "    " + NL + "    ifContinue";
  protected final String TEXT_37 = " = ";
  protected final String TEXT_38 = ";" + NL + "    " + NL + "" + NL + "    ";
  protected final String TEXT_39 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String port = ElementParameterParser.getValue(node,"__PORT__");

String output = ElementParameterParser.getValue(node,"__OUTPUT__");

String print = ElementParameterParser.getValue(node,"__PRINT__");

boolean then = ("continue").equals(ElementParameterParser.getValue(node,"__THEN__"));

String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(port);
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
    stringBuffer.append(output);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(encoding);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
     if(("true").equals(print)){ 
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    }
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(then);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(TEXT_39);
    return stringBuffer.toString();
  }
}
