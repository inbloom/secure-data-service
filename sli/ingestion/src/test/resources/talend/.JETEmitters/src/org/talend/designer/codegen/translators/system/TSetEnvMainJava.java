package org.talend.designer.codegen.translators.system;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.runprocess.ProcessorException;
import java.util.Map;
import java.util.List;

public class TSetEnvMainJava
{
  protected static String nl;
  public static synchronized TSetEnvMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSetEnvMainJava result = new TSetEnvMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "                        if(System.getProperty(";
  protected final String TEXT_3 = ")!=null)" + NL + "                        {" + NL + "                            System.setProperty(";
  protected final String TEXT_4 = ", System.getProperty(";
  protected final String TEXT_5 = ")+System.getProperty(\"path.separator\")+";
  protected final String TEXT_6 = ");" + NL + "                        }else" + NL + "                        {" + NL + "                           System.setProperty(";
  protected final String TEXT_7 = ",";
  protected final String TEXT_8 = ");                         " + NL + "                        }";
  protected final String TEXT_9 = NL + "                        System.setProperty(";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    List<IMetadataTable> metadatas = node.getMetadataList();
    if ((metadatas!=null)&&(metadatas.size()>0)) {
        IMetadataTable metadata = metadatas.get(0);
        if (metadata!=null) {
            // component id
            String cid = node.getUniqueName();
            List<Map<String, String>> params = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__PARAMS__");
            for (int i = 0; i < params.size(); i++) {
                Map<String, String> line = params.get(i);
                if(("true").equals(line.get("APPEND"))==true)
                    {

    stringBuffer.append(TEXT_2);
    stringBuffer.append( line.get("NAME") );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( line.get("NAME") );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( line.get("NAME") );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( line.get("VALUE") );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( line.get("NAME") );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( line.get("VALUE") );
    stringBuffer.append(TEXT_8);
    
                    }else
                    {
                        
 
    stringBuffer.append(TEXT_9);
    stringBuffer.append( line.get("NAME") );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( line.get("VALUE") );
    stringBuffer.append(TEXT_11);
    
                    }
                }
            }
        }
   
    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
