package org.talend.designer.codegen.translators.dotnet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TDotNETInstantiateMainJava
{
  protected static String nl;
  public static synchronized TDotNETInstantiateMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TDotNETInstantiateMainJava result = new TDotNETInstantiateMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "   Object[] parameterList_";
  protected final String TEXT_2 = " = new Object[] {";
  protected final String TEXT_3 = NL + "     ";
  protected final String TEXT_4 = NL + "        ,";
  protected final String TEXT_5 = "   " + NL + "   };";
  protected final String TEXT_6 = NL + "   org.talend.net.Object netObject_";
  protected final String TEXT_7 = " =  ";
  protected final String TEXT_8 = ";   \t" + NL + "   globalMap.put(\"";
  protected final String TEXT_9 = "_INSTANCE\",netObject_";
  protected final String TEXT_10 = ");";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    String dllLoad = ElementParameterParser.getValue(node,"__LIBRARY__");    
    String clazz = ElementParameterParser.getValue(node,"__CLASS_NAME__");
    List<Map<String,String>> values = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__PARAMETERS__");
    String call = "org.talend.net.Object.createInstance("+dllLoad+","+clazz+")";
    if (values.size() > 0) {
        call = "org.talend.net.Object.createInstance("+dllLoad+","+clazz+",parameterList_"+cid+")";
    

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
     for (int i = 0; i < values.size(); i++) {
         Map<String,String> line = values.get(i);
     
    stringBuffer.append(TEXT_3);
    stringBuffer.append(line.get("PARAMETER") );
    
        if (i < values.size() - 1) {
        
    stringBuffer.append(TEXT_4);
    
        }
     }
   
    stringBuffer.append(TEXT_5);
    
   }
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(call);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
