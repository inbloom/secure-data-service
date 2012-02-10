package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TPOPEndJava
{
  protected static String nl;
  public static synchronized TPOPEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPOPEndJava result = new TPOPEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "      }";
  protected final String TEXT_2 = NL + "  } catch (javax.mail.MessageRemovedException mre) {" + NL + "    System.out.println(\"one mail fails to retrieve since it was removed\");";
  protected final String TEXT_3 = NL + "  } catch (javax.mail.MessagingException me) {" + NL + "    if (!\"Cannot load header\".equals(me.getMessage()))" + NL + "      throw me;" + NL + "    else " + NL + "      System.out.println(\"one mail fails to retrieve since it was removed\");";
  protected final String TEXT_4 = NL + "  }" + NL + "}" + NL + "" + NL + "if (folder_";
  protected final String TEXT_5 = " != null) {";
  protected final String TEXT_6 = NL + "    folder_";
  protected final String TEXT_7 = ".close(true); ";
  protected final String TEXT_8 = " " + NL + "    folder_";
  protected final String TEXT_9 = ".close(false);";
  protected final String TEXT_10 = NL + "}" + NL + "" + NL + "if (store_";
  protected final String TEXT_11 = " != null) {" + NL + "  store_";
  protected final String TEXT_12 = ".close();" + NL + "}" + NL + "globalMap.put(\"";
  protected final String TEXT_13 = "_NB_EMAIL\", nb_email_";
  protected final String TEXT_14 = ");  ";
  protected final String TEXT_15 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    boolean bDeleteFromServer = "true".equals(ElementParameterParser.getValue(node, "__DELETE_FROM_SERVER__"));
    List<Map<String, String>> filterList = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ADVANCED_FILTER__");
    String protocol = ElementParameterParser.getValue(node, "__PROTOCOL__");
    
    if (filterList.size() > 0) {
    
    stringBuffer.append(TEXT_1);
    
    }
  
  if ("pop3".equals(protocol)) {
  
    stringBuffer.append(TEXT_2);
    } else {
    stringBuffer.append(TEXT_3);
    }
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    if (bDeleteFromServer) {
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    } else {
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    }
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
