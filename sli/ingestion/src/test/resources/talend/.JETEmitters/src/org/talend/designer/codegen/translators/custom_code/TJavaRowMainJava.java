package org.talend.designer.codegen.translators.custom_code;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TJavaRowMainJava
{
  protected static String nl;
  public static synchronized TJavaRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJavaRowMainJava result = new TJavaRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    ";
  protected final String TEXT_3 = NL + "    nb_line_";
  protected final String TEXT_4 = "++;   ";
  protected final String TEXT_5 = NL;

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
        String code = ElementParameterParser.getValue(node, "__CODE__");
        
        // we give a default value to prevComponentName so that no error
        // occur when the user tries to generated Java code while no input
        // component was linked to our tJavaRow.
        String inputRowName = new String("input_row");
        String outputRowName = new String("output_row");

        List< ? extends IConnection> inConns = node.getIncomingConnections();
        if (inConns != null && !inConns.isEmpty()) {
            IConnection inConn = inConns.get(0);
            inputRowName = inConn.getName();
        }
        
        List< ? extends IConnection> outConns = node.getOutgoingConnections();
        if (outConns != null && !outConns.isEmpty()) {
            IConnection outConn = outConns.get(0);
            outputRowName = outConn.getName();
        }

        // In case the user would make some tricky Java things, he can use
        // the arrays as a whole.
        code = code.replaceAll(
            "input_row",
            inputRowName
        );

        code = code.replaceAll(
            "output_row",
            outputRowName
        );


    stringBuffer.append(TEXT_2);
    stringBuffer.append(code);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
    }
}

    stringBuffer.append(TEXT_5);
    return stringBuffer.toString();
  }
}
