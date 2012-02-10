package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.Map;

public class TFlowToIterateMainJava
{
  protected static String nl;
  public static synchronized TFlowToIterateMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFlowToIterateMainJava result = new TFlowToIterateMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "    \t";
  protected final String TEXT_4 = "            " + NL + "            globalMap.put(\"";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = "\", ";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = ");" + NL + "            nb_line_";
  protected final String TEXT_9 = "++;  ";
  protected final String TEXT_10 = NL;
  protected final String TEXT_11 = "\t" + NL + "    \t";
  protected final String TEXT_12 = NL + "              globalMap.put(";
  protected final String TEXT_13 = ", ";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = ");" + NL + "              nb_line_";
  protected final String TEXT_16 = "++;";
  protected final String TEXT_17 = " ";
  protected final String TEXT_18 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();

boolean useDefault = ("true").equals(ElementParameterParser.getValue(node, "__DEFAULT_MAP__"));
List<Map<String, String>> map = (List<Map<String, String>>)ElementParameterParser.getObjectValue(node, "__MAP__");

String inputRowName = null;
IConnection inMainConn = null;

List< ? extends IConnection> inMainConns = node.getIncomingConnections(EConnectionType.FLOW_MAIN);
if (inMainConns != null && !inMainConns.isEmpty()) {
    inMainConn = inMainConns.get(0);
    inputRowName = inMainConn.getName();
}else{
	return "";
 }       


    stringBuffer.append(TEXT_2);
     if(useDefault){  
    stringBuffer.append(TEXT_3);
          
        IMetadataTable metadata = inMainConn.getMetadataTable();
        List<IMetadataColumn> listColumns = metadata.getListColumns();
        
        for (int i = 0; i < listColumns.size(); i++) {
            IMetadataColumn column = listColumns.get(i);
            String columnLabel = column.getLabel();
        
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inputRowName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(columnLabel );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(inputRowName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(columnLabel );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
      
          }
        
    stringBuffer.append(TEXT_10);
      } else { 
    stringBuffer.append(TEXT_11);
    
        for (int i = 0; i < map.size(); i++) {
              Map<String, String> line = map.get(i);
        
    stringBuffer.append(TEXT_12);
    stringBuffer.append( line.get("KEY") );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(inputRowName );
    stringBuffer.append(TEXT_14);
    stringBuffer.append( line.get("VALUE") );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
            }
        
    
	}            

    stringBuffer.append(TEXT_17);
    stringBuffer.append(TEXT_18);
    return stringBuffer.toString();
  }
}
