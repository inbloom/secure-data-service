package org.talend.designer.codegen.translators.data_quality;

import java.util.List;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TSchemaComplianceCheckMainJava
{
  protected static String nl;
  public static synchronized TSchemaComplianceCheckMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSchemaComplianceCheckMainJava result = new TSchemaComplianceCheckMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "      ";
  protected final String TEXT_2 = " = null;";
  protected final String TEXT_3 = NL + "\trsvUtil_";
  protected final String TEXT_4 = ".setRowValue_";
  protected final String TEXT_5 = "(";
  protected final String TEXT_6 = ");";
  protected final String TEXT_7 = NL + "    if (rsvUtil_";
  protected final String TEXT_8 = ".ifPassedThrough) {";
  protected final String TEXT_9 = NL + "          ";
  protected final String TEXT_10 = " = new ";
  protected final String TEXT_11 = "Struct();";
  protected final String TEXT_12 = NL + "            ";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = ";";
  protected final String TEXT_17 = NL + "    }";
  protected final String TEXT_18 = NL + "    if (!rsvUtil_";
  protected final String TEXT_19 = ".ifPassedThrough) {";
  protected final String TEXT_20 = NL + "      ";
  protected final String TEXT_21 = " = new ";
  protected final String TEXT_22 = "Struct();";
  protected final String TEXT_23 = NL + "        ";
  protected final String TEXT_24 = ".";
  protected final String TEXT_25 = " = ";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = ";";
  protected final String TEXT_28 = NL + "      ";
  protected final String TEXT_29 = ".errorCode = String.valueOf(rsvUtil_";
  protected final String TEXT_30 = ".resultErrorCodeThrough);";
  protected final String TEXT_31 = NL + "      ";
  protected final String TEXT_32 = ".errorMessage = rsvUtil_";
  protected final String TEXT_33 = ".resultErrorMessageThrough;" + NL + "    }";
  protected final String TEXT_34 = NL + "rsvUtil_";
  protected final String TEXT_35 = ".reset();";
  protected final String TEXT_36 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

/*in shema:*/
List<? extends IConnection> listInConns = node.getIncomingConnections();
String sInConnName = null;
List<IMetadataColumn> listInColumns = null;

if (listInConns != null && listInConns.size() > 0) {
  IConnection inConn = listInConns.get(0);
  sInConnName = inConn.getName();
  if (inConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
    listInColumns = inConn.getMetadataTable().getListColumns();
  }
}

/*out shema(include reject and main):*/
List<? extends IConnection> listOutConns = node.getOutgoingSortedConnections();
String sRejectConnName = null;

if (sInConnName != null && listInColumns != null && listInColumns.size() > 0) {
  
  for (IConnection outConn : listOutConns) {
    if (outConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
      if ("REJECT".equals(outConn.getConnectorName())){
        sRejectConnName = outConn.getName();
      }
      
    stringBuffer.append(TEXT_1);
    stringBuffer.append(outConn.getName());
    stringBuffer.append(TEXT_2);
    
    }
  }
  	int inputColumnSize = listInColumns.size();
	for (int i = 0; i < inputColumnSize; i++ ) {
		if(i % 100 == 0){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( (i/100) );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(sInConnName );
    stringBuffer.append(TEXT_6);
    
		}
	}
  if (listOutConns != null && listOutConns.size() > 0) {
  
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    
      for (IConnection conn : listOutConns) {
        if (sRejectConnName == null || (sRejectConnName != null && !sRejectConnName.equals(conn.getName()))){
        
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
    
          for (IMetadataColumn inColumn : listInColumns) {
          
    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(inColumn.getLabel());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(sInConnName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(inColumn.getLabel());
    stringBuffer.append(TEXT_16);
    
          }
        }
      }
      
    stringBuffer.append(TEXT_17);
    
  }
  
  if (sRejectConnName != null) {
  
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(sRejectConnName );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(sRejectConnName );
    stringBuffer.append(TEXT_22);
    
      for (IMetadataColumn inColumn : listInColumns) {
      
    stringBuffer.append(TEXT_23);
    stringBuffer.append(sRejectConnName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(inColumn.getLabel());
    stringBuffer.append(TEXT_25);
    stringBuffer.append(sInConnName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(inColumn.getLabel());
    stringBuffer.append(TEXT_27);
    
      }
      
    stringBuffer.append(TEXT_28);
    stringBuffer.append(sRejectConnName);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(sRejectConnName);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    
  }
}

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(TEXT_36);
    return stringBuffer.toString();
  }
}
