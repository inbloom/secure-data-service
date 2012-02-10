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

public class TSPSSInputBeginJava
{
  protected static String nl;
  public static synchronized TSPSSInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSPSSInputBeginJava result = new TSPSSInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\torg.talend.jspss.spss ";
  protected final String TEXT_4 = "_sp = new org.talend.jspss.spss();" + NL + "\torg.talend.jspss.spssfile ";
  protected final String TEXT_5 = "_sf = ";
  protected final String TEXT_6 = "_sp.openFile(";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = "\t" + NL + "\t";
  protected final String TEXT_9 = "_sf.setWithLabelTranslation(true);";
  protected final String TEXT_10 = NL + "\torg.talend.jspss.spssrecord ";
  protected final String TEXT_11 = "_sr;" + NL + "\twhile(!";
  protected final String TEXT_12 = "_sf.isEOF()){";
  protected final String TEXT_13 = NL;
  protected final String TEXT_14 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String filename = ElementParameterParser.getValue(node, "__FILENAME__");
String withtranslation = ElementParameterParser.getValue(node, "__TRANSLATELABELS__");

String outputConnName = null;
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
	}
}

if (outputConnName != null){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_7);
    
	if(("true").equals(withtranslation)){

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
	}

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
