package org.talend.designer.codegen.translators.processing;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TFilterRowBeginJava
{
  protected static String nl;
  public static synchronized TFilterRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFilterRowBeginJava result = new TFilterRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "    int nb_line_ok_";
  protected final String TEXT_3 = " = 0;" + NL + "    int nb_line_reject_";
  protected final String TEXT_4 = " = 0;" + NL + "" + NL + "    class Operator_";
  protected final String TEXT_5 = " {" + NL + "      private String sErrorMsg = \"\";" + NL + "      private boolean bMatchFlag = true;" + NL + "      private String sUnionFlag = \"&&\";" + NL + "" + NL + "      public Operator_";
  protected final String TEXT_6 = "(String unionFlag){" + NL + "        sUnionFlag = unionFlag;" + NL + "        bMatchFlag =  \"||\".equals(unionFlag) ? false : true;" + NL + "      }" + NL + "" + NL + "      public String getErrorMsg() {" + NL + "        if (sErrorMsg != null && sErrorMsg.length() > 1)" + NL + "          return sErrorMsg.substring(1);" + NL + "        else " + NL + "          return null;" + NL + "      }" + NL + "" + NL + "      public boolean getMatchFlag() {" + NL + "        return bMatchFlag;" + NL + "      }" + NL + "" + NL + "      public void matches(boolean partMatched, String reason) {" + NL + "        // no need to care about the next judgement" + NL + "        if (\"||\".equals(sUnionFlag) && bMatchFlag){" + NL + "          return;" + NL + "        }" + NL + "" + NL + "        if (!partMatched) {" + NL + "          sErrorMsg += \"|\" + reason;" + NL + "        }" + NL + "" + NL + "        if (\"||\".equals(sUnionFlag))" + NL + "          bMatchFlag = bMatchFlag || partMatched;" + NL + "        else" + NL + "          bMatchFlag = bMatchFlag && partMatched;" + NL + "      }" + NL + "    }";
  protected final String TEXT_7 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataTable> metadatas = node.getMetadataList();

if (metadatas != null && metadatas.size() > 0) {
  IMetadataTable metadata = metadatas.get(0);

  if (metadata != null) {
  
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
  }
}

    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
