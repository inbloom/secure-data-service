package org.talend.designer.codegen.translators.databases.mysql;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import java.util.Set;
import java.util.HashSet;

public class TMysqlLastInsertIdMainJava
{
  protected static String nl;
  public static synchronized TMysqlLastInsertIdMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMysqlLastInsertIdMainJava result = new TMysqlLastInsertIdMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL + "java.sql.ResultSet rs_";
  protected final String TEXT_8 = " = pstmt_";
  protected final String TEXT_9 = ".executeQuery();" + NL + "if(rs_";
  protected final String TEXT_10 = ".next()){";
  protected final String TEXT_11 = NL + "\t\t\t\t";
  protected final String TEXT_12 = ".last_insert_id = rs_";
  protected final String TEXT_13 = ".getLong(1);";
  protected final String TEXT_14 = NL + "\t\t\t\t";
  protected final String TEXT_15 = ".last_insert_id = rs_";
  protected final String TEXT_16 = ".getInt(1);";
  protected final String TEXT_17 = NL + "\t\t\t\tthrow new RuntimeException(\"Data type of \\\"last_insert_id\\\": ";
  protected final String TEXT_18 = " is not supported,please choose Integer or Long!\");";
  protected final String TEXT_19 = NL + "}" + NL + "nb_line_";
  protected final String TEXT_20 = "++;";
  protected final String TEXT_21 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String inputConnName = null;
	Set<String> inputCols = new HashSet<String>();
	if (node.getIncomingConnections()!=null) {
		for (IConnection incomingConn : node.getIncomingConnections()) {
			if (incomingConn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
				inputConnName = incomingConn.getName();
				IMetadataTable inputMetadataTable = incomingConn.getMetadataTable();
				for (IMetadataColumn inputCol : inputMetadataTable.getListColumns()) {
					inputCols.add(inputCol.getLabel());
				}
				break;
			}
		}
	}
	

	for (IConnection conn : node.getOutgoingConnections()) {
		if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
			IMetadataTable outputMetadataTable = conn.getMetadataTable();
			if (outputMetadataTable!=null) {
				for (IMetadataColumn outputCol : outputMetadataTable.getListColumns()) { 
					if (inputCols.contains(outputCol.getLabel())) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(outputCol.getLabel() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inputConnName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(outputCol.getLabel() );
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
    
	for (IMetadataColumn outputCol : outputMetadataTable.getListColumns()) {
		if(outputCol.getLabel().equals("last_insert_id")){
			String type = outputCol.getTalendType();
			if(type.equals("id_Long")){

    stringBuffer.append(TEXT_11);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    
			}else if(type.equals("id_Integer")){

    stringBuffer.append(TEXT_14);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
			}else{

    stringBuffer.append(TEXT_17);
    stringBuffer.append(type.substring(3));
    stringBuffer.append(TEXT_18);
    
			}
			break;
		}
	}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    
			}
		}
	}

    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}
