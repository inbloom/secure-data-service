package org.talend.designer.codegen.translators.business.healthcare;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class THL7InputEndJava
{
  protected static String nl;
  public static synchronized THL7InputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    THL7InputEndJava result = new THL7InputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t}" + NL + "\t}" + NL + "\tif(!(file_";
  protected final String TEXT_3 = " instanceof java.io.InputStream)){" + NL + "\t\tif(reader_";
  protected final String TEXT_4 = "!=null){" + NL + "\t\t\treader_";
  protected final String TEXT_5 = ".close();" + NL + "\t\t}" + NL + "\t}" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_6 = "_NB_LINE\", nb_line_";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = "  ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	
	
	String fileName = ElementParameterParser.getValue(node,"__FILENAME__");
    List< ? extends IConnection> connections = node.getOutgoingSortedConnections();
	
	if(connections!=null && connections.size()>0){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
	}

    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
