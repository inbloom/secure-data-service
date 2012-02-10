package org.talend.designer.codegen.translators.common;

import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.codegen.config.CloseBlocksCodeArgument;
import java.util.List;
import org.talend.core.model.process.BlockCode;

public class Close_blocks_codeJava
{
  protected static String nl;
  public static synchronized Close_blocks_codeJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Close_blocks_codeJava result = new Close_blocks_codeJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL + "\t\t} // ";
  protected final String TEXT_2 = NL + "\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
CloseBlocksCodeArgument closeBlocksCodeArgument = (CloseBlocksCodeArgument)codeGenArgument.getArgument();

List<BlockCode> blocksCodeList = closeBlocksCodeArgument.getBlocksCodeToClose();
if(blocksCodeList != null) {
	int count = blocksCodeList.size();
	for(int i = 0; i < count; i++) {
	
    stringBuffer.append(TEXT_1);
    stringBuffer.append( blocksCodeList.get(i).getLabel());
    stringBuffer.append(TEXT_2);
    
	}
}

    return stringBuffer.toString();
  }
}
