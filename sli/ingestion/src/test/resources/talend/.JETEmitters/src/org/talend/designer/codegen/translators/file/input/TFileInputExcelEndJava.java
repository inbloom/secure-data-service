package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;

public class TFileInputExcelEndJava
{
  protected static String nl;
  public static synchronized TFileInputExcelEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputExcelEndJava result = new TFileInputExcelEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\t\tnb_line_";
  protected final String TEXT_2 = "++;" + NL + "\t\t\t}" + NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_3 = "_NB_LINE\",nb_line_";
  protected final String TEXT_4 = ");" + NL + "\t\t} finally { " + NL + "\t\t\t\t";
  protected final String TEXT_5 = NL + "\t\t\t\t\tif(!(source_";
  protected final String TEXT_6 = " instanceof java.io.InputStream)){" + NL + "\t\t\t\t\t\tworkbook_";
  protected final String TEXT_7 = ".close();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_8 = NL + "  \t\t\t\tif(!(source_";
  protected final String TEXT_9 = " instanceof java.io.InputStream)){" + NL + "  \t\t\t\t\tworkbook_";
  protected final String TEXT_10 = ".getPackage().revert();" + NL + "  \t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_11 = NL + "\t\t}\t" + NL + "\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	boolean version07 = ("true").equals(ElementParameterParser.getValue(node,"__VERSION_2007__"));

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    if(!version07){
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    }else{
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    }
    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
