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

public class Camel_speciallinksJava
{
  protected static String nl;
  public static synchronized Camel_speciallinksJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Camel_speciallinksJava result = new Camel_speciallinksJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t.when().";
  protected final String TEXT_3 = "(";
  protected final String TEXT_4 = ")";
  protected final String TEXT_5 = NL + "\t\t.otherwise()";
  protected final String TEXT_6 = NL + "\t\t.doTry()";
  protected final String TEXT_7 = NL + "\t\t.doCatch(";
  protected final String TEXT_8 = ")";
  protected final String TEXT_9 = NL + "\t\t.doFinally()";
  protected final String TEXT_10 = NL + "\t\t.end()";
  protected final String TEXT_11 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    

CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;

INode node = (INode) codeGenArgument.getArgument();

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
    
	} else if(connsIf.get(0).getLineStyle()==EConnectionType.ROUTE_TRY) {

    stringBuffer.append(TEXT_6);
    
	} else if(connsIf.get(0).getLineStyle()==EConnectionType.ROUTE_CATCH) {

    stringBuffer.append(TEXT_7);
    stringBuffer.append(connsIf.get(0).getExceptionList());
    stringBuffer.append(TEXT_8);
    
	} else if(connsIf.get(0).getLineStyle()==EConnectionType.ROUTE_FINALLY) {

    stringBuffer.append(TEXT_9);
    
	} else if(connsIf.get(0).getLineStyle()==EConnectionType.ROUTE_ENDBLOCK) {

    stringBuffer.append(TEXT_10);
    
	}
}	

    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
