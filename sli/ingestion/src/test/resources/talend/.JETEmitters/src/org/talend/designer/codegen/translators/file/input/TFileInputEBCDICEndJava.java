package org.talend.designer.codegen.translators.file.input;

public class TFileInputEBCDICEndJava
{
  protected static String nl;
  public static synchronized TFileInputEBCDICEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputEBCDICEndJava result = new TFileInputEBCDICEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "   }" + NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    return stringBuffer.toString();
  }
}
