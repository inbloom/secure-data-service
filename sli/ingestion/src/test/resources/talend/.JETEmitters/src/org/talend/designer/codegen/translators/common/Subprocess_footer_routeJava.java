package org.talend.designer.codegen.translators.common;

import org.talend.designer.codegen.config.NodesSubTree;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Iterator;

public class Subprocess_footer_routeJava
{
  protected static String nl;
  public static synchronized Subprocess_footer_routeJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Subprocess_footer_routeJava result = new Subprocess_footer_routeJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	boolean stat = codeGenArgument.isStatistics();
	NodesSubTree subTree = (NodesSubTree) codeGenArgument.getArgument();

    return stringBuffer.toString();
  }
}
