package org.talend.designer.codegen.translators.logs_errors;

public class TAssertCatcherEndJava
{
  protected static String nl;
  public static synchronized TAssertCatcherEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAssertCatcherEndJava result = new TAssertCatcherEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    return stringBuffer.toString();
  }
}
