package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.runprocess.CodeGeneratorRoutine;
import org.talend.designer.codegen.i18n.Messages;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.core.ui.branding.AbstractBrandingService;
import org.talend.core.GlobalServiceRegister;
import org.talend.designer.codegen.ITalendSynchronizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import org.talend.designer.codegen.config.NodesSubTree;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.utils.JavaResourcesHelper;

public class Camel_run_ifJava
{
  protected static String nl;
  public static synchronized Camel_run_ifJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Camel_run_ifJava result = new Camel_run_ifJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t.when(";
  protected final String TEXT_3 = "(";
  protected final String TEXT_4 = "))";
  protected final String TEXT_5 = NL + "\t\t.otherwise()";
  protected final String TEXT_6 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    

CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
NodesSubTree subTree = (NodesSubTree) codeGenArgument.getArgument();


INode node = subTree.getRootNode();
List< ? extends IConnection> connsIf = node.getIncomingConnections();
if(connsIf.size()>0) {
	if(connsIf.get(0).getLineStyle()==EConnectionType.ROUTE_WHEN) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(connsIf.get(0).getRouteConnectionType());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(connsIf.get(0).getCondition());
    stringBuffer.append(TEXT_4);
    
	} else if(connsIf.get(0).getLineStyle()==EConnectionType.ROUTE_OTHER) {

    stringBuffer.append(TEXT_5);
    
	}
}	

    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
