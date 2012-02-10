package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TFileInputMSDelimitedEndJava
{
  protected static String nl;
  public static synchronized TFileInputMSDelimitedEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputMSDelimitedEndJava result = new TFileInputMSDelimitedEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "}" + NL + "fid_";
  protected final String TEXT_3 = ".close();";
  protected final String TEXT_4 = NL + "}";
  protected final String TEXT_5 = NL;
  protected final String TEXT_6 = "csvReader";
  protected final String TEXT_7 = ".close();";
  protected final String TEXT_8 = "\t\t\t  ";
  protected final String TEXT_9 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
boolean useMulSeprators = "true".equals(ElementParameterParser.getValue(node,"__USE_MULTISEPARATORS__"));

boolean hasDataLink = false;
if(conns!=null){
	for(int i=0;i<conns.size();i++){
		IConnection connTemp = conns.get(i);
	    if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	   		hasDataLink = true;
	   		break;
	    }
	}
}
if(hasDataLink){
	if(useMulSeprators==false && ("false").equals(ElementParameterParser.getValue(node,"__CSV_OPTION__"))) {	

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	}else{//the following is the tFileInputCSV component

    stringBuffer.append(TEXT_4);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(useMulSeprators? "tmp":"" );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
	}
}

    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}
