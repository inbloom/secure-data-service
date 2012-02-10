package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;

public class TFileInputPositionalEndJava
{
  protected static String nl;
  public static synchronized TFileInputPositionalEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputPositionalEndJava result = new TFileInputPositionalEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "\tnb_line_";
  protected final String TEXT_3 = "++;" + NL + "\tif ( nb_limit_";
  protected final String TEXT_4 = " > 0 && nb_line_";
  protected final String TEXT_5 = " >= nb_limit_";
  protected final String TEXT_6 = " ){" + NL + "\t\tbreak;" + NL + "\t}" + NL + "}";
  protected final String TEXT_7 = "}";
  protected final String TEXT_8 = NL + "if(!(filename_";
  protected final String TEXT_9 = " instanceof java.io.InputStream)){" + NL + "\tif(reader_";
  protected final String TEXT_10 = "!=null) {" + NL + "\t\treader_";
  protected final String TEXT_11 = ".close();" + NL + "\t}" + NL + "}" + NL + "globalMap.put(\"";
  protected final String TEXT_12 = "_NB_LINE\",nb_line_";
  protected final String TEXT_13 = ");";
  protected final String TEXT_14 = "      ";
  protected final String TEXT_15 = NL;

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
			boolean uncompress = ("true").equals(ElementParameterParser.getValue(node,"__UNCOMPRESS__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    if(uncompress){//compress
    stringBuffer.append(TEXT_7);
    }
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
		}
	}

    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
