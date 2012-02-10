package org.talend.designer.codegen.translators.technical;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.xmlmap.XmlMapComponent;

public class TXMLMapInBeginJava
{
  protected static String nl;
  public static synchronized TXMLMapInBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TXMLMapInBeginJava result = new TXMLMapInBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "for(Object  row_out_";
  protected final String TEXT_2 = " : allOutsForAggregate_";
  protected final String TEXT_3 = ") {//TD512";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	XmlMapComponent node = (XmlMapComponent) codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String uniqueNameComponent = cid.replaceAll("_TXMLMAP_IN", "");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(uniqueNameComponent );
    stringBuffer.append(TEXT_3);
    return stringBuffer.toString();
  }
}
