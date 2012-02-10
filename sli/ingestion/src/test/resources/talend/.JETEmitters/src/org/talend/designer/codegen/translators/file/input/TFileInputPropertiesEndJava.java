package org.talend.designer.codegen.translators.file.input;

import java.util.List;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileInputPropertiesEndJava
{
  protected static String nl;
  public static synchronized TFileInputPropertiesEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputPropertiesEndJava result = new TFileInputPropertiesEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\ttos_count_";
  protected final String TEXT_2 = "++;";
  protected final String TEXT_3 = NL + "\t\t}";
  protected final String TEXT_4 = NL + "\t}" + NL + "}";
  protected final String TEXT_5 = NL + "}";
  protected final String TEXT_6 = NL + "globalMap.put(\"";
  protected final String TEXT_7 = "_NB_LINE\", tos_count_";
  protected final String TEXT_8 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    String fileFormat = ElementParameterParser.getValue(node,"__FILE_FORMAT__");
    String outConnName = null;
    List< ? extends IConnection> outConns = node.getOutgoingConnections();
    for(IConnection conn : outConns){
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    		outConnName = conn.getName();
    	}
    }
    if(outConnName == null){
        return ""; //return immediately, generate nothing
    }   	

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    
	if(("PROPERTIES_FORMAT").equals(ElementParameterParser.getValue(node,"__FILE_FORMAT__"))){

    stringBuffer.append(TEXT_3);
    
	}else{
		if(("RETRIVE_ALL").equals(ElementParameterParser.getValue(node,"__RETRIVE_MODE__"))){

    stringBuffer.append(TEXT_4);
    
		}else{

    stringBuffer.append(TEXT_5);
    
		}
	}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
