package org.talend.designer.codegen.translators.misc;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.IContextParameter;
import java.util.List;
import java.util.Enumeration;

public class TContextDumpBeginJava
{
  protected static String nl;
  public static synchronized TContextDumpBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TContextDumpBeginJava result = new TContextDumpBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "        int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "        java.util.List<String> assignList_";
  protected final String TEXT_3 = " = new java.util.ArrayList<String>();" + NL + "        for( java.util.Enumeration<?> en_";
  protected final String TEXT_4 = " = context.propertyNames() ; en_";
  protected final String TEXT_5 = ".hasMoreElements() ; ) {        " + NL + "            nb_line_";
  protected final String TEXT_6 = "++;" + NL + "            Object key_";
  protected final String TEXT_7 = " = en_";
  protected final String TEXT_8 = ".nextElement();" + NL + "            Object value_";
  protected final String TEXT_9 = " = context.getProperty(key_";
  protected final String TEXT_10 = ".toString());";
  protected final String TEXT_11 = NL + "                    ";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = " = key_";
  protected final String TEXT_14 = ".toString();";
  protected final String TEXT_15 = NL + "                    ";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = " = value_";
  protected final String TEXT_18 = ".toString();";
  protected final String TEXT_19 = NL + "\t\t\t\t\t\t\tif((\"";
  protected final String TEXT_20 = "\").equals(key_";
  protected final String TEXT_21 = ".toString())){" + NL + "\t\t\t                    ";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = " = value_";
  protected final String TEXT_24 = ".toString().replaceAll(\".\",\"*\");" + NL + "\t\t\t                }" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_25 = NL + "\t\t\t\t\t\t\tif((\"";
  protected final String TEXT_26 = "\").equals(key_";
  protected final String TEXT_27 = ".toString())){" + NL + "\t\t\t\t\t\t\t\tif(value_";
  protected final String TEXT_28 = ".toString().indexOf(\";\")>-1){" + NL + "\t\t\t                    \t";
  protected final String TEXT_29 = ".";
  protected final String TEXT_30 = " = value_";
  protected final String TEXT_31 = ".toString().substring(value_";
  protected final String TEXT_32 = ".toString().indexOf(\";\")+1);" + NL + "\t\t\t                    }" + NL + "\t\t\t                }" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_33 = NL + "\t\t\t\t\t";
  protected final String TEXT_34 = NL + "                    ";
  protected final String TEXT_35 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	List<IContextParameter> params = node.getProcess().getContextManager().getDefaultContext().getContextParameterList();
	boolean hidePassword = ("true").equals(ElementParameterParser.getValue(node,"__HIDE_PASSWORD__"));
	String cid = node.getUniqueName();
    List<IMetadataColumn> columnList = null;
    List<IMetadataTable> metadatas = node.getMetadataList();
    if(metadatas != null && metadatas.size() > 0) {
        IMetadataTable metadata = metadatas.get(0);
        if(metadata != null) {
            columnList = metadata.getListColumns();
        }
    }
    List< ? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
    if(columnList != null && columnList.size() > 0 && outgoingConns != null && outgoingConns.size() > 0) {
        
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
            for(IConnection outgoingConn : outgoingConns) {
                if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                    
    stringBuffer.append(TEXT_11);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(columnList.get(0).getLabel());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(columnList.get(1).getLabel());
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    for (IContextParameter param :params){
	                  	if(("id_Password").equals(param.getType()) && hidePassword){
	                  		String str = param.getName(); 
	                
    stringBuffer.append(TEXT_19);
    stringBuffer.append(str);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(columnList.get(1).getLabel());
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    
						}else if(("id_Date").equals(param.getType())){
							String str = param.getName();
						
    stringBuffer.append(TEXT_25);
    stringBuffer.append(str);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_29);
    stringBuffer.append(columnList.get(1).getLabel());
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    
						}
						
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(TEXT_34);
    
                }
            }
    }

    stringBuffer.append(TEXT_35);
    return stringBuffer.toString();
  }
}
