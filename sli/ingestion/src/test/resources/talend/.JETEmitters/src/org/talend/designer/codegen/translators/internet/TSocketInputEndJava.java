package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TSocketInputEndJava
{
  protected static String nl;
  public static synchronized TSocketInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSocketInputEndJava result = new TSocketInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\t\t        }//end of while(readRecord)" + NL + "            //in";
  protected final String TEXT_4 = ".close(); " + NL + "            } finally {" + NL + "            \tif(socket";
  protected final String TEXT_5 = " != null && !socket";
  protected final String TEXT_6 = ".isClosed()) {" + NL + "            \t\tsocket";
  protected final String TEXT_7 = ".close();" + NL + "            \t}" + NL + "            \tdone";
  protected final String TEXT_8 = " = false;" + NL + "\t\t\t}" + NL + "        }" + NL + "        if(ss";
  protected final String TEXT_9 = " !=null && !ss";
  protected final String TEXT_10 = ".isClosed()) {" + NL + "        \tss";
  protected final String TEXT_11 = ".close();" + NL + "        }" + NL + "        globalMap.put(\"";
  protected final String TEXT_12 = "_NB_LINE\", nb_line_";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = "  ";

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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
		}
	}

    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}
