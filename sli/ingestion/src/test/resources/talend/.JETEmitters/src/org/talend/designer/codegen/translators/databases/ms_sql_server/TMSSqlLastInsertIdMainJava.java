package org.talend.designer.codegen.translators.databases.ms_sql_server;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class TMSSqlLastInsertIdMainJava
{
  protected static String nl;
  public static synchronized TMSSqlLastInsertIdMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSSqlLastInsertIdMainJava result = new TMSSqlLastInsertIdMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "                        ";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL + "                java.sql.ResultSet rs_";
  protected final String TEXT_8 = " = pstmt_";
  protected final String TEXT_9 = ".executeQuery();" + NL + "                if(rs_";
  protected final String TEXT_10 = ".next()) {";
  protected final String TEXT_11 = NL + "                    ";
  protected final String TEXT_12 = ".last_insert_id = rs_";
  protected final String TEXT_13 = ".getInt(1);" + NL + "                }" + NL + "                nb_line_";
  protected final String TEXT_14 = "++;";
  protected final String TEXT_15 = NL + NL + NL + NL;
  protected final String TEXT_16 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String incomingConnName = null;
Set<String> incomingColumns = new HashSet<String>();
List<? extends IConnection> incomingConns = node.getIncomingConnections();
if(incomingConns != null && incomingConns.size() > 0) {
    for(IConnection incomingConn : incomingConns) {
        if(incomingConn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
            incomingConnName = incomingConn.getName();
            IMetadataTable inputMetadataTable = incomingConn.getMetadataTable();
            List<IMetadataColumn> inputSchema = inputMetadataTable.getListColumns();
            if(inputSchema != null && inputSchema.size() > 0) {
                for(IMetadataColumn column : inputSchema) {
                    incomingColumns.add(column.getLabel());
                }
            }
            break;
        }
    }
}
List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
if(outgoingConns != null && outgoingConns.size() > 0) {
    for(IConnection outgoingConn : outgoingConns) {
        if(outgoingConn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
            IMetadataTable outputMetadataTable = outgoingConn.getMetadataTable();
            List<IMetadataColumn> outputSchema = outputMetadataTable.getListColumns();
            if(outputSchema != null && outputSchema.size() > 0) {
                for(IMetadataColumn column : outputSchema) {
                    if(incomingColumns.contains(column.getLabel())) {
                        
    stringBuffer.append(TEXT_2);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_6);
    
                    }
                }
                
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(outgoingConn.getName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    
            }
        }
    }
}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(TEXT_16);
    return stringBuffer.toString();
  }
}
