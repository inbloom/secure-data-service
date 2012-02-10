package org.talend.designer.codegen.translators.logs_errors;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.List;

public class TLogRowEndJava
{
  protected static String nl;
  public static synchronized TLogRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLogRowEndJava result = new TLogRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "//////";
  protected final String TEXT_2 = NL + NL + "                    " + NL + "                    java.io.PrintStream consoleOut_";
  protected final String TEXT_3 = " = null;" + NL + "                    if (globalMap.get(\"tLogRow_CONSOLE\")!=null)" + NL + "                    {" + NL + "                    \tconsoleOut_";
  protected final String TEXT_4 = " = (java.io.PrintStream) globalMap.get(\"tLogRow_CONSOLE\");" + NL + "                    }" + NL + "                    else" + NL + "                    {" + NL + "                    \tconsoleOut_";
  protected final String TEXT_5 = " = new java.io.PrintStream(new java.io.BufferedOutputStream(System.out));" + NL + "                    \tglobalMap.put(\"tLogRow_CONSOLE\",consoleOut_";
  protected final String TEXT_6 = ");" + NL + "                    }" + NL + "                    " + NL + "                    consoleOut_";
  protected final String TEXT_7 = ".println(util_";
  protected final String TEXT_8 = ".format().toString());" + NL + "                    consoleOut_";
  protected final String TEXT_9 = ".flush();";
  protected final String TEXT_10 = NL + "//////" + NL + "globalMap.put(\"";
  protected final String TEXT_11 = "_NB_LINE\",nb_line_";
  protected final String TEXT_12 = ");" + NL + "" + NL + "///////////////////////    \t\t\t";
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2 	
	
	String cid = node.getUniqueName();
	String tablePrint = ElementParameterParser.getValue(node,"__TABLE_PRINT__");

    stringBuffer.append(TEXT_1);
    
	if (("true").equals(tablePrint)) {//print all records one time

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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
  }

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
    }//2
}//1

    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
