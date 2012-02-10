package org.talend.designer.codegen.translators.business_intelligence.spss;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TSPSSOutputEndJava
{
  protected static String nl;
  public static synchronized TSPSSOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSPSSOutputEndJava result = new TSPSSOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t//}" + NL + "\t";
  protected final String TEXT_3 = "_sf.close();" + NL + "\tglobalMap.put(\"";
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

String inputConnName = null;
List< ? extends IConnection> conns = node.getIncomingConnections();
if (conns!=null) {
	if (conns.size()>0) {
		for (int i=0;i<conns.size();i++) {
			IConnection connTemp = conns.get(i);
			if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				inputConnName = connTemp.getName();
				break;
			}
		}
	}
}

if (inputConnName != null){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
}

    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
