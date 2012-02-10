package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;

public class TFileTouchMainJava
{
  protected static String nl;
  public static synchronized TFileTouchMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileTouchMainJava result = new TFileTouchMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = "     " + NL + "\t\tjava.io.File file_";
  protected final String TEXT_3 = " = new java.io.File((";
  protected final String TEXT_4 = "));";
  protected final String TEXT_5 = NL + "\t\tjava.io.File dir_";
  protected final String TEXT_6 = " = file_";
  protected final String TEXT_7 = ".getParentFile();" + NL + "\t\tif(dir_";
  protected final String TEXT_8 = "!= null){" + NL + "\t\tdir_";
  protected final String TEXT_9 = ".mkdirs();" + NL + "\t\t}";
  protected final String TEXT_10 = "        " + NL + "        " + NL + "        //create new file" + NL + "        boolean result";
  protected final String TEXT_11 = " = file_";
  protected final String TEXT_12 = ".createNewFile();" + NL + "        //if file already exists, modify the last-modified-time of the file" + NL + "        if (!result";
  protected final String TEXT_13 = ") {" + NL + "        \tfile_";
  protected final String TEXT_14 = ".setLastModified((new Date()).getTime());" + NL + "        }";
  protected final String TEXT_15 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String fileName = ElementParameterParser.getValue(node, "__FILENAME__");	
	boolean replaceFile=("true").equals(ElementParameterParser.getValue(node,"__REPLACE_FILE__"));
	boolean createDir=("true").equals(ElementParameterParser.getValue(node, "__CREATEDIR__"));
   

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_4);
    if(createDir){
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    }
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
