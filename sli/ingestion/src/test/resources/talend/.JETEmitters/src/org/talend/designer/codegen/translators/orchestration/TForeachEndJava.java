package org.talend.designer.codegen.translators.orchestration;

public class TForeachEndJava
{
  protected static String nl;
  public static synchronized TForeachEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TForeachEndJava result = new TForeachEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "}";
  protected final String TEXT_2 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
