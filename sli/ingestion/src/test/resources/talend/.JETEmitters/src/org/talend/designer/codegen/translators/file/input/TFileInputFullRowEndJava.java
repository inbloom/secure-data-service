package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TFileInputFullRowEndJava
{
  protected static String nl;
  public static synchronized TFileInputFullRowEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputFullRowEndJava result = new TFileInputFullRowEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "            }" + NL + "           \t}finally{" + NL + "           \t\tif(fid_";
  protected final String TEXT_3 = "!=null){" + NL + "            \t\tfid_";
  protected final String TEXT_4 = ".close();" + NL + "            \t}" + NL + "            }" + NL + "            globalMap.put(\"";
  protected final String TEXT_5 = "_NB_LINE\", fid_";
  protected final String TEXT_6 = ".getRowNumber());";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
		}
	}

    return stringBuffer.toString();
  }
}
