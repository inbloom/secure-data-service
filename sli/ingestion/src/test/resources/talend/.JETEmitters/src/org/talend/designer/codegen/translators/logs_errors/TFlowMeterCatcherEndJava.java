package org.talend.designer.codegen.translators.logs_errors;

public class TFlowMeterCatcherEndJava
{
  protected static String nl;
  public static synchronized TFlowMeterCatcherEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFlowMeterCatcherEndJava result = new TFlowMeterCatcherEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t}" + NL;
  protected final String TEXT_2 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
