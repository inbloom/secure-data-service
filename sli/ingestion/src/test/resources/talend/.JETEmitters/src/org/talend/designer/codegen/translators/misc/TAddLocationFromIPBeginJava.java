package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TAddLocationFromIPBeginJava
{
  protected static String nl;
  public static synchronized TAddLocationFromIPBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAddLocationFromIPBeginJava result = new TAddLocationFromIPBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "com.maxmind.geoip.LookupService lookupService_";
  protected final String TEXT_3 = " = new com.maxmind.geoip.LookupService(";
  protected final String TEXT_4 = ", com.maxmind.geoip.LookupService.GEOIP_MEMORY_CACHE);";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String databaseFile = ElementParameterParser.getValue(node, "__DATABASE_FILEPATH__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(databaseFile);
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
