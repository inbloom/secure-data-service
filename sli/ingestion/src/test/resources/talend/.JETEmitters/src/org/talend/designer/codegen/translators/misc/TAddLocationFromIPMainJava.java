package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TAddLocationFromIPMainJava
{
  protected static String nl;
  public static synchronized TAddLocationFromIPMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAddLocationFromIPMainJava result = new TAddLocationFromIPMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "                com.maxmind.geoip.Country country_";
  protected final String TEXT_3 = " = lookupService_";
  protected final String TEXT_4 = ".getCountry(java.net.InetAddress.getByName(";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ").getHostAddress());";
  protected final String TEXT_7 = NL + "                com.maxmind.geoip.Country country_";
  protected final String TEXT_8 = " = lookupService_";
  protected final String TEXT_9 = ".getCountry(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL + "                ";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = " = country_";
  protected final String TEXT_15 = ".getCode();";
  protected final String TEXT_16 = NL + "                ";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = " = country_";
  protected final String TEXT_19 = ".getName();";
  protected final String TEXT_20 = NL + "            ";
  protected final String TEXT_21 = ".";
  protected final String TEXT_22 = " = ";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = ";";
  protected final String TEXT_25 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List<IMetadataColumn> columnList = null;
List<IMetadataTable> metadatas = node.getMetadataList();
if(metadatas != null && metadatas.size() > 0) {
    IMetadataTable metadata = metadatas.get(0);
    if(metadata != null) {
        columnList = metadata.getListColumns();
    }
}
String incomingConnName = null;
List<? extends IConnection> inConns = node.getIncomingConnections();
if(inConns != null && inConns.size() > 0) {
    for(IConnection inConn : inConns) {
        if(inConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            incomingConnName = inConn.getName();
            break;
        }
    }
}
String outgoingConnName = null;
List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
if(outgoingConns != null && outgoingConns.size() > 0) {
    for(IConnection outgoingConn : outgoingConns) {
        if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            outgoingConnName = outgoingConn.getName();
            break;
        }
    }
}
if(incomingConnName != null && outgoingConnName != null && columnList != null && columnList.size() > 0) {
    String inputColumn = ElementParameterParser.getValue(node, "__INPUT_COLUMN__");
    boolean isHostName = ("true").equals(ElementParameterParser.getValue(node, "__INPUT_HOST_NAME__"));
    boolean isIPAddress = ("true").equals(ElementParameterParser.getValue(node, "__INPUT_IP__"));
    boolean isLocationTypeCode = ("true").equals(ElementParameterParser.getValue(node, "__LOCATION_TYPE_CODE__"));
    boolean isLocationTypeName = ("true").equals(ElementParameterParser.getValue(node, "__LOCATION_TYPE_NAME__"));
    for(int i = 0 ; i < columnList.size() ; i++) {
        if(i == columnList.size() -1) {
            if(isHostName) {
                
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inputColumn);
    stringBuffer.append(TEXT_6);
    
            } else if(isIPAddress) {
                
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(inputColumn);
    stringBuffer.append(TEXT_11);
    
            }
            if(isLocationTypeCode) {
                
    stringBuffer.append(TEXT_12);
    stringBuffer.append(outgoingConnName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    
            } else if(isLocationTypeName) {
                
    stringBuffer.append(TEXT_16);
    stringBuffer.append(outgoingConnName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
            }
        } else {
            
    stringBuffer.append(TEXT_20);
    stringBuffer.append(outgoingConnName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(columnList.get(i).getLabel());
    stringBuffer.append(TEXT_24);
                
        }
    }
}

    stringBuffer.append(TEXT_25);
    return stringBuffer.toString();
  }
}
