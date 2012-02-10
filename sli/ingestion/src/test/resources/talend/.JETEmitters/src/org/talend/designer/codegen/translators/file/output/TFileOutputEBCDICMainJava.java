package org.talend.designer.codegen.translators.file.output;

import java.util.List;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileOutputEBCDICMainJava
{
  protected static String nl;
  public static synchronized TFileOutputEBCDICMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputEBCDICMainJava result = new TFileOutputEBCDICMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "\t" + NL + "\t\t\tjava.util.List record_";
  protected final String TEXT_3 = " = new java.util.ArrayList();";
  protected final String TEXT_4 = NL + "\t\t\trecord_";
  protected final String TEXT_5 = ".add(";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ") ;";
  protected final String TEXT_8 = NL + "        \trwriter_";
  protected final String TEXT_9 = ".writeRecord(record_";
  protected final String TEXT_10 = ");\t" + NL + "        \tnb_line_";
  protected final String TEXT_11 = "++;\t\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();	
    String incomingName = (String)codeGenArgument.getIncomingName();

    stringBuffer.append(TEXT_1);
    		List< ? extends IConnection> conns = node.getIncomingConnections();
    	List<IMetadataTable> preMetadatas = null;
		
		for (int i=0;i<conns.size();i++) {
			IConnection conn = conns.get(i);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {    			
				if( conn.getName() == incomingName ) {				
					preMetadatas = conn.getSource().getMetadataList();

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
			for (IMetadataColumn column: preMetadatas.get(0).getListColumns()) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_7);
    
			}

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    
				}
			}
		}

    return stringBuffer.toString();
  }
}
