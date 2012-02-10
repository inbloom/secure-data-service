package org.talend.designer.codegen.translators.system;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import java.util.List;

public class TSSHEndJava
{
  protected static String nl;
  public static synchronized TSSHEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSSHEndJava result = new TSSHEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t\tSystem.out.println(stringStdout_";
  protected final String TEXT_3 = ".toString());";
  protected final String TEXT_4 = NL + "\t\t\t\tSystem.out.println(stringStdout_";
  protected final String TEXT_5 = ".toString());";
  protected final String TEXT_6 = NL + "\t\t\t\tSystem.out.println(stringStderr_";
  protected final String TEXT_7 = ".toString());";
  protected final String TEXT_8 = NL + "\t\t\t\tSystem.out.println(stringStderr_";
  protected final String TEXT_9 = ".toString());";
  protected final String TEXT_10 = NL + "\t/* Close the connection */" + NL + "\tconn_";
  protected final String TEXT_11 = ".close();" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_12 = "_EXIT_CODE\",sess_";
  protected final String TEXT_13 = ".getExitStatus());" + NL + "\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
	boolean stats = codeGenArgument.isStatistics();
	String standardOutput = ElementParameterParser.getValue(node, "__STANDARDOUTPUT__");
	String errorOutput = ElementParameterParser.getValue(node, "__ERROROUTPUT__");
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    	if (metadata!=null) {	
			if(("TO_CONSOLE").equals(standardOutput)){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
			}else if(("TO_CONSOLE_AND_GLOBAL_VARIABLE").equals(standardOutput)){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    
			}
			if(("TO_CONSOLE").equals(errorOutput)){

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    		
			}else if(("TO_CONSOLE_AND_GLOBAL_VARIABLE").equals(errorOutput)){

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    
			}
		}
	}

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
