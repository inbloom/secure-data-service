package org.talend.designer.codegen.translators.custom_code;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TJavaFlexMainJava
{
  protected static String nl;
  public static synchronized TJavaFlexMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJavaFlexMainJava result = new TJavaFlexMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "        \t\t\t\t";
  protected final String TEXT_4 = ".";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ";";
  protected final String TEXT_8 = NL;
  protected final String TEXT_9 = NL;
  protected final String TEXT_10 = NL;
  protected final String TEXT_11 = NL + "        \t\t\t\t";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = " = ";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = ";";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	boolean autoPropagate = "true".equals(ElementParameterParser.getValue(node, "__DATA_AUTO_PROPAGATE__"));
	
	//this 3 flag must be only one as true, the new component is default isVersion_V4.0=true	
	boolean isVersion_V2_0 = "true".equals(ElementParameterParser.getValue(node, "__Version_V2_0__"));
	boolean isVersion_V3_2 = "true".equals(ElementParameterParser.getValue(node, "__Version_V3_2__"));
	boolean isVersion_V4_0 = "true".equals(ElementParameterParser.getValue(node, "__Version_V4.0__"));

    stringBuffer.append(TEXT_2);
    
//after TOS4.0, copy action BEFORE code

if(autoPropagate&&isVersion_V4_0){//1
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {//A
    	IMetadataTable metadata = metadatas.get(0);
    	if (metadata!=null) {//2
			IConnection inConn = null;
			IConnection outConn = null;
	        List< ? extends IConnection> inConns = node.getIncomingConnections();
	        
	        //in order to support the "Iterate/Main" at the same time.
	        List< ? extends IConnection> outConns = node.getOutgoingSortedConnections();
	        if (inConns != null && !inConns.isEmpty()) {
	            inConn = inConns.get(0);
	        }
	        if (outConns != null && !outConns.isEmpty()) {
	            outConn = outConns.get(0);
	        }
	        if(inConn != null && outConn != null){//3
	        	if(inConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA) && outConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {	        			
    	    		List<IMetadataColumn> columns = metadata.getListColumns();
    	    		IMetadataTable ref_metadata = inConn.getMetadataTable();
        	    	for(IMetadataColumn column : columns){
        	    		//add a name mapping for issue:11712
        	    		if (ref_metadata.getColumn(column.getLabel()) != null) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(outConn.getName());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_7);
    
        				}
        			}
				}
			} //3
		} //2
	} //A
} //1

    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(ElementParameterParser.getValue(node, "__CODE_MAIN__") );
    stringBuffer.append(TEXT_10);
    
//between TOS3.2 and TOS4.0, copy action AFTER code
 
if(autoPropagate&&isVersion_V3_2){//1
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {//A
    	IMetadataTable metadata = metadatas.get(0);
    	if (metadata!=null) {//2
			IConnection inConn = null;
			IConnection outConn = null;
	        List< ? extends IConnection> inConns = node.getIncomingConnections();
	        
	        //in order to support the "Iterate/Main" at the same time.
	        List< ? extends IConnection> outConns = node.getOutgoingSortedConnections();
	        if (inConns != null && !inConns.isEmpty()) {
	            inConn = inConns.get(0);
	        }
	        if (outConns != null && !outConns.isEmpty()) {
	            outConn = outConns.get(0);
	        }
	        if(inConn != null && outConn != null){//3
	        	if(inConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA) && outConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {	        			
    	    		List<IMetadataColumn> columns = metadata.getListColumns();
    	    		IMetadataTable ref_metadata = inConn.getMetadataTable();
        	    	for(IMetadataColumn column : columns){
        	    		//add a name mapping for issue:11712, 
        	    		if (ref_metadata.getColumn(column.getLabel()) != null) {        	    	

    stringBuffer.append(TEXT_11);
    stringBuffer.append(outConn.getName());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_15);
    
        				}
        			}
				}
			} //3
		} //2
	} //A
} //1

    return stringBuffer.toString();
  }
}
