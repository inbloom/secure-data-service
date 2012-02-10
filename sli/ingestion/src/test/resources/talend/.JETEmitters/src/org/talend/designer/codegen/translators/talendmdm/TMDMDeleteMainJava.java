package org.talend.designer.codegen.translators.talendmdm;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TMDMDeleteMainJava
{
  protected static String nl;
  public static synchronized TMDMDeleteMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMDMDeleteMainJava result = new TMDMDeleteMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "String[] wsIds_";
  protected final String TEXT_2 = " = {";
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = ".";
  protected final String TEXT_5 = "};" + NL + "org.talend.mdm.webservice.WSItemPK wsPK_";
  protected final String TEXT_6 = " = new org.talend.mdm.webservice.WSItemPK(dataCluster_";
  protected final String TEXT_7 = ",";
  protected final String TEXT_8 = ",wsIds_";
  protected final String TEXT_9 = ");" + NL + "///////////////////////\t" + NL + "" + NL + "try{";
  protected final String TEXT_10 = NL + "\torg.talend.mdm.webservice.WSDropItem item_";
  protected final String TEXT_11 = " = new org.talend.mdm.webservice.WSDropItem(wsPK_";
  protected final String TEXT_12 = ",";
  protected final String TEXT_13 = ",false);" + NL + "\txtentisWS_";
  protected final String TEXT_14 = ".dropItem(item_";
  protected final String TEXT_15 = ");";
  protected final String TEXT_16 = NL + "\torg.talend.mdm.webservice.WSDeleteItem item_";
  protected final String TEXT_17 = " = new org.talend.mdm.webservice.WSDeleteItem(wsPK_";
  protected final String TEXT_18 = ",false);" + NL + "\txtentisWS_";
  protected final String TEXT_19 = ".deleteItem(item_";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL + "}catch(Exception e){";
  protected final String TEXT_22 = NL + "\tthrow(e);";
  protected final String TEXT_23 = NL + "\tSystem.err.println(e.getMessage());";
  protected final String TEXT_24 = NL + "}" + NL + "nb_line_";
  protected final String TEXT_25 = "++;    \t\t\t" + NL + "" + NL + "///////////////////////    \t\t\t";
  protected final String TEXT_26 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

boolean isDropItem = ("true").equals(ElementParameterParser.getValue(node,"__USE_DROP_ITEM__"));
String partPath = ElementParameterParser.getValue(node,"__PART_PATH__");

boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node,"__DIE_ON_ERROR__"));

String dataModule = ElementParameterParser.getValue(node, "__CONCEPT__");
List<Map<String,String>> keyValues = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__KEYS__");


List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {//2

    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {//3
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//4

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    
			    for(int i=0;i<keyValues.size();i++){
			    	String columnName=keyValues.get(i).get("KEY");

    stringBuffer.append(TEXT_3);
    stringBuffer.append(i==0?"":",");
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(columnName );
    
			    }

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dataModule );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    
if(isDropItem){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(partPath );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    }else{
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    }
    stringBuffer.append(TEXT_21);
    
if(dieOnError){

    stringBuffer.append(TEXT_22);
    
}else{

    stringBuffer.append(TEXT_23);
    
}

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
    		}//4
    	}//3
    }//2
}//1

    stringBuffer.append(TEXT_26);
    return stringBuffer.toString();
  }
}
