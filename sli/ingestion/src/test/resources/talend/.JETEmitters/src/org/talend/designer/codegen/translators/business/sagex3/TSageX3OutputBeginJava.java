package org.talend.designer.codegen.translators.business.sagex3;

import java.util.List;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSageX3OutputBeginJava
{
  protected static String nl;
  public static synchronized TSageX3OutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSageX3OutputBeginJava result = new TSageX3OutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "         \t\torg.talend.sage.Client client_";
  protected final String TEXT_2 = " = new org.talend.sage.Client();" + NL + "                client_";
  protected final String TEXT_3 = ".ConnectService(";
  protected final String TEXT_4 = ");" + NL + "                client_";
  protected final String TEXT_5 = ".setContext(";
  protected final String TEXT_6 = ",";
  protected final String TEXT_7 = ",";
  protected final String TEXT_8 = ",";
  protected final String TEXT_9 = ",";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "  \t\t" + NL + "        \t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
        CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
        INode node = (INode)codeGenArgument.getArgument();
        String cid = node.getUniqueName();

        List<IMetadataTable> metadatas = node.getMetadataList();
        if ((metadatas!=null)&&(metadatas.size()>0)) {//1
            IMetadataTable metadata = metadatas.get(0);
            if (metadata!=null) {//2
        
            	List< ? extends IConnection> conns = node.getIncomingConnections();
            	for (IConnection conn : conns) {//3
            		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4
            		
                		String endpoint = ElementParameterParser.getValue(node, "__ENDPOINT__");
                        String user = ElementParameterParser.getValue(node, "__USER__");
                        String password = ElementParameterParser.getValue(node, "__PASSWORD__");
                        String language = ElementParameterParser.getValue(node, "__LANGUAGE__");
                        String poolAlias = ElementParameterParser.getValue(node, "__POOL_ALIAS__");
                        String requestConfig = ElementParameterParser.getValue(node, "__REQUEST_CONFIG__");
                        

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(language);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(poolAlias);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(requestConfig);
    stringBuffer.append(TEXT_10);
    
            		}//4
            	}//3
            }//2
        }//1

    stringBuffer.append(TEXT_11);
    return stringBuffer.toString();
  }
}
