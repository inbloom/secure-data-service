package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileOutputEBCDICBeginJava
{
  protected static String nl;
  public static synchronized TFileOutputEBCDICBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputEBCDICBeginJava result = new TFileOutputEBCDICBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "    // write file" + NL + "    javax.xml.bind.JAXBContext jaxbContextW_";
  protected final String TEXT_3 = " = javax.xml.bind.JAXBContext.newInstance(\"net.sf.cobol2j\");" + NL + "    javax.xml.bind.Unmarshaller unmarshallerW_";
  protected final String TEXT_4 = " = jaxbContextW_";
  protected final String TEXT_5 = ".createUnmarshaller();" + NL + "    Object oW_";
  protected final String TEXT_6 = " = unmarshallerW_";
  protected final String TEXT_7 = ".unmarshal(new java.io.FileInputStream(";
  protected final String TEXT_8 = "));" + NL + "    net.sf.cobol2j.FileFormat fFW_";
  protected final String TEXT_9 = " = (net.sf.cobol2j.FileFormat) oW_";
  protected final String TEXT_10 = ";" + NL + "    net.sf.cobol2j.RecordWriter rwriter_";
  protected final String TEXT_11 = " = new net.sf.cobol2j.RecordWriter( new java.io.FileOutputStream(";
  protected final String TEXT_12 = "), fFW_";
  protected final String TEXT_13 = " );" + NL + "    " + NL + "    int nb_line_";
  protected final String TEXT_14 = " = 0;";
  protected final String TEXT_15 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	

    stringBuffer.append(TEXT_1);
    
	String filename = ElementParameterParser.getValue(node,"__FILENAME__");
	String copybook = ElementParameterParser.getValue(node,"__COPYBOOK__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( copybook );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( filename );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
