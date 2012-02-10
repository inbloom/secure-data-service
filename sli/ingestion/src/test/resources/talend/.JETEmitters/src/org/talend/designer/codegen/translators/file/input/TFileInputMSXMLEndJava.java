package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TFileInputMSXMLEndJava
{
  protected static String nl;
  public static synchronized TFileInputMSXMLEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputMSXMLEndJava result = new TFileInputMSXMLEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t}//while";
  protected final String TEXT_3 = NL + "}" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_4 = "_NB_LINE\",nb_line_";
  protected final String TEXT_5 = ");";
  protected final String TEXT_6 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();
List< ? extends IConnection> connections = node.getOutgoingSortedConnections();
String mode = ElementParameterParser.getValue(node, "__GENERATION_MODE__");

if(connections!=null && connections.size()>0){

	boolean hasConn = false;
	
	//get all the children collections of the loop node.
	for(IConnection conn : connections){
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
			hasConn = true;
		}
	}
	if(hasConn == true && ("Dom4j").equals(mode)){

    stringBuffer.append(TEXT_2);
    
	}
}

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
