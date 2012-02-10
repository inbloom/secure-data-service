package org.talend.designer.codegen.translators.business_intelligence.jasper;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import java.util.List;

public class TJasperOutputExecEndJava
{
  protected static String nl;
  public static synchronized TJasperOutputExecEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJasperOutputExecEndJava result = new TJasperOutputExecEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    return stringBuffer.toString();
  }
}
