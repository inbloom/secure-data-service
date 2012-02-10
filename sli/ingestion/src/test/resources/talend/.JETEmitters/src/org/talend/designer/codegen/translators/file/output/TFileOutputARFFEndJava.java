package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TFileOutputARFFEndJava
{
  protected static String nl;
  public static synchronized TFileOutputARFFEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputARFFEndJava result = new TFileOutputARFFEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.io.FileOutputStream out";
  protected final String TEXT_3 = " = null;" + NL + "try {" + NL + "    out";
  protected final String TEXT_4 = " = new java.io.FileOutputStream(fileNewName_";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ");";
  protected final String TEXT_7 = NL + "    try{" + NL + "        //to check if the file-to-write is an existing-valid ARFF file" + NL + "       new weka.core.Instances(new java.io.BufferedReader(new java.io.FileReader(fileNewName_";
  protected final String TEXT_8 = ")));" + NL + "       StringBuilder sb";
  protected final String TEXT_9 = " = new StringBuilder();" + NL + "       for (int i = oldInsNum";
  protected final String TEXT_10 = "; i < m_Data";
  protected final String TEXT_11 = ".numInstances(); i++) {" + NL + "           sb";
  protected final String TEXT_12 = ".append(\"\\n\");" + NL + "           sb";
  protected final String TEXT_13 = ".append(m_Data";
  protected final String TEXT_14 = ".instance(i).toString());" + NL + "       }" + NL + "       out";
  protected final String TEXT_15 = ".write(sb";
  protected final String TEXT_16 = ".toString().getBytes(), 0, sb";
  protected final String TEXT_17 = ".toString().getBytes().length);" + NL + "    }catch(Exception e){" + NL + "       out";
  protected final String TEXT_18 = ".write(m_Data";
  protected final String TEXT_19 = ".toString().getBytes(),0,m_Data";
  protected final String TEXT_20 = ".toString().getBytes().length);" + NL + "    }   ";
  protected final String TEXT_21 = "   " + NL + "    out";
  protected final String TEXT_22 = ".write(m_Data";
  protected final String TEXT_23 = ".toString().getBytes(),0,m_Data";
  protected final String TEXT_24 = ".toString().getBytes().length);";
  protected final String TEXT_25 = NL + "} catch (java.io.IOException e) {" + NL + "    // TODO Auto-generated catch block" + NL + "    e.printStackTrace();" + NL + "}finally{" + NL + "\tout";
  protected final String TEXT_26 = ".close();" + NL + "\tout";
  protected final String TEXT_27 = " = null;" + NL + "}" + NL + "globalMap.put(\"";
  protected final String TEXT_28 = "_NB_LINE\",nb_line_";
  protected final String TEXT_29 = ");" + NL;
  protected final String TEXT_30 = NL + "\tif(nb_line_";
  protected final String TEXT_31 = " == 0){" + NL + "\t\tnew java.io.File(";
  protected final String TEXT_32 = ").delete();" + NL + "\t}\t\t";
  protected final String TEXT_33 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String filename = ElementParameterParser.getValue(node,"__FILENAME__");
boolean isDeleteEmptyFile = ("true").equals(ElementParameterParser.getValue(node, "__DELETE_EMPTYFILE__"));

boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(isAppend);
    stringBuffer.append(TEXT_6);
     if(isAppend){
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    }else{
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    }
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    if(!isAppend && isDeleteEmptyFile){
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_32);
    }
    stringBuffer.append(TEXT_33);
    return stringBuffer.toString();
  }
}
