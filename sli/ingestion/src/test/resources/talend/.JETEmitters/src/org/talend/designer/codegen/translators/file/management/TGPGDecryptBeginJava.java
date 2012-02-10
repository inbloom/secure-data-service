package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import java.util.List;

public class TGPGDecryptBeginJava
{
  protected static String nl;
  public static synchronized TGPGDecryptBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TGPGDecryptBeginJava result = new TGPGDecryptBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\tString[] cmdArray_";
  protected final String TEXT_3 = " = new String[]{" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_4 = "," + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_5 = NL + "\t\t\t\t\t\t\t\t\t\t\t\t\"--no-tty\"," + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_6 = NL + "\t\t\t\t\t\t\t\t\t\t\t\"--yes\"," + NL + "\t\t\t\t\t\t\t\t\t\t\t\"-q\"," + NL + "\t\t\t\t\t\t\t\t\t\t\t\"-d\"," + NL + "\t\t\t\t\t\t\t\t\t\t\t\"--passphrase\"," + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_7 = "," + NL + "\t\t\t\t\t\t\t\t\t\t\t\"-o\"," + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_8 = "," + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_9 = NL + "\t\t\t\t\t\t\t \t\t\t\t};" + NL + "            Runtime runtime_";
  protected final String TEXT_10 = " = Runtime.getRuntime();" + NL + "            final Process ps_";
  protected final String TEXT_11 = " = runtime_";
  protected final String TEXT_12 = ".exec(cmdArray_";
  protected final String TEXT_13 = ");" + NL + "            Thread normal_";
  protected final String TEXT_14 = " = new Thread() {" + NL + "" + NL + "                public void run() {" + NL + "                    try {" + NL + "                        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(ps_";
  protected final String TEXT_15 = NL + "                                .getInputStream()));" + NL + "                        String line = \"\";" + NL + "                        try {" + NL + "                            while ((line = reader.readLine()) != null) {" + NL + "                                System.out.println(line);" + NL + "                            }" + NL + "                        } finally {" + NL + "                            reader.close();" + NL + "                        }" + NL + "                    } catch (java.io.IOException ioe) {" + NL + "                        ioe.printStackTrace();" + NL + "                    }" + NL + "                }" + NL + "            };" + NL + "            normal_";
  protected final String TEXT_16 = ".start();" + NL + "" + NL + "            Thread error_";
  protected final String TEXT_17 = " = new Thread() {" + NL + "" + NL + "                public void run() {" + NL + "                    try {" + NL + "                        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(ps_";
  protected final String TEXT_18 = NL + "                                .getErrorStream()));" + NL + "                        String line = \"\";" + NL + "                        try {" + NL + "                            while ((line = reader.readLine()) != null) {" + NL + "                                System.err.println(line);" + NL + "                            }" + NL + "                        } finally {" + NL + "                            reader.close();" + NL + "                        }" + NL + "                    } catch (java.io.IOException ioe) {" + NL + "                        ioe.printStackTrace();" + NL + "                    }" + NL + "                }" + NL + "            };" + NL + "            error_";
  protected final String TEXT_19 = ".start();" + NL + "" + NL + "            ps_";
  protected final String TEXT_20 = ".waitFor();" + NL + "            normal_";
  protected final String TEXT_21 = ".join(10000);" + NL + "            error_";
  protected final String TEXT_22 = ".join(10000);" + NL + "            " + NL + "            java.io.File file_";
  protected final String TEXT_23 = " = new java.io.File(";
  protected final String TEXT_24 = ");" + NL + "            globalMap.put(\"";
  protected final String TEXT_25 = "_FILE\", file_";
  protected final String TEXT_26 = ".getName());" + NL + "            globalMap.put(\"";
  protected final String TEXT_27 = "_FILEPATH\", file_";
  protected final String TEXT_28 = ".getPath());";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String gpgBin = ElementParameterParser.getValue(node, "__GPGBIN__");		
	String inputFile  = ElementParameterParser.getValue(node, "__INPUT_FILE__");
	String outputFile  = ElementParameterParser.getValue(node, "__OUTPUT_FILE__");
	String password  = ElementParameterParser.getValue(node, "__PASSPHRASE__");
	boolean noTTY  = "true".equals(ElementParameterParser.getValue(node, "__NOTTY__"));
	
	//"cmd /c " + gpgBin + " --yes -q  -d --passphrase " + password + " -o " + outputFile + " " + inputFile;
	
	// To launch an internal command to cmd.exe (XP), you have to call :
	// cmd.exe /c dir (for example)

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(gpgBin );
    stringBuffer.append(TEXT_4);
    if(noTTY){
    stringBuffer.append(TEXT_5);
    }
    stringBuffer.append(TEXT_6);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(outputFile );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(inputFile );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(outputFile );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    return stringBuffer.toString();
  }
}
