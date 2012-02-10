package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TSocketOutputEndJava
{
  protected static String nl;
  public static synchronized TSocketOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSocketOutputEndJava result = new TSocketOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "    " + NL + "    socket";
  protected final String TEXT_4 = ".close();" + NL + "    CsvWriter";
  protected final String TEXT_5 = ".close();" + NL + "    " + NL + "    globalMap.put(\"";
  protected final String TEXT_6 = "_NB_LINE\", nb_line_";
  protected final String TEXT_7 = ");" + NL + "" + NL + "    ";
  protected final String TEXT_8 = "\t\t\t  ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	

    stringBuffer.append(TEXT_2);
    
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    
		}
	}

    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
