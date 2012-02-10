package org.talend.designer.codegen.translators.business_intelligence.olap_cube.palo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TPaloDimensionListEndJava
{
  protected static String nl;
  public static synchronized TPaloDimensionListEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloDimensionListEndJava result = new TPaloDimensionListEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\t}" + NL + "   }" + NL + "\tbreak;" + NL + "  }" + NL + "\t";
  protected final String TEXT_4 = NL + "\telse {" + NL + "\t\t    throw new RuntimeException (\"Cube '\" + ";
  protected final String TEXT_5 = " + " + NL + "\t\t                              \"' not found in database '\" + ";
  protected final String TEXT_6 = " + \"'. exiting...\" );" + NL + "\t\t}" + NL + "\t}" + NL + "\t";
  protected final String TEXT_7 = NL + "//globalMap.put(\"";
  protected final String TEXT_8 = "_NB_RULES\", NB_RULES";
  protected final String TEXT_9 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String sDatabaseName = ElementParameterParser.getValue(node,"__DATABASE__");
    String sCubeName = ElementParameterParser.getValue(node,"__CUBE__");

    boolean useRetriveFromCube = "true".equals(ElementParameterParser.getValue(node,"__RETRIEVE_DIMENSIONS_FROM_CUBE__"));


    
String outputConnName = null;
boolean bIterate=false;
List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
if (conns!=null) {
	if (conns.size()>0) {
		for (int i=0;i<conns.size();i++) {
			IConnection connTemp = conns.get(i);
			if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				outputConnName = connTemp.getName();
				break;
			}
		}
		for (int i=0;i<conns.size();i++) {
			IConnection connTemp = conns.get(i);
			if(connTemp.getLineStyle().toString().equals("ITERATE")) {
				bIterate=true;
				//break;
			}
		}
	}
}

    stringBuffer.append(TEXT_2);
    
if (outputConnName != null || bIterate){

    stringBuffer.append(TEXT_3);
    
	if(useRetriveFromCube){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_6);
    }
    
}

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
