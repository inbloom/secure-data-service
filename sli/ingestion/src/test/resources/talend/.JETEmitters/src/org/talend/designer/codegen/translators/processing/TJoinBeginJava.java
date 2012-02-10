package org.talend.designer.codegen.translators.processing;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.EConnectionType;
import java.util.List;
import java.util.Map;
import org.talend.core.model.utils.NodeUtil;

public class TJoinBeginJava
{
  protected static String nl;
  public static synchronized TJoinBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJoinBeginJava result = new TJoinBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    final java.util.Map<";
  protected final String TEXT_2 = "Struct, ";
  protected final String TEXT_3 = "Struct> tHash_";
  protected final String TEXT_4 = " = (java.util.Map<";
  protected final String TEXT_5 = "Struct, ";
  protected final String TEXT_6 = "Struct>) globalMap.get(\"tHash_";
  protected final String TEXT_7 = "\");" + NL + "    " + NL + "    class Util_";
  protected final String TEXT_8 = NL + "    {";
  protected final String TEXT_9 = NL + "        ";
  protected final String TEXT_10 = "Struct lookupValue = null;";
  protected final String TEXT_11 = NL + "        ";
  protected final String TEXT_12 = "Struct ";
  protected final String TEXT_13 = "HashKey = new ";
  protected final String TEXT_14 = "Struct(); " + NL + "        " + NL + "        public boolean isJoined(";
  protected final String TEXT_15 = "Struct mainRow) {            ";
  protected final String TEXT_16 = "            ";
  protected final String TEXT_17 = NL + "           ";
  protected final String TEXT_18 = "HashKey.";
  protected final String TEXT_19 = " = mainRow.";
  protected final String TEXT_20 = ";";
  protected final String TEXT_21 = NL + "\t\t\t";
  protected final String TEXT_22 = "HashKey.hashCodeDirty = true;";
  protected final String TEXT_23 = "\t\t\t             " + NL + "            lookupValue = tHash_";
  protected final String TEXT_24 = ".get(";
  protected final String TEXT_25 = "HashKey);            " + NL + "            if(lookupValue != null){" + NL + "            \treturn true;" + NL + "            }  " + NL + "            return false;" + NL + "        }" + NL + "    }" + NL + "        " + NL + "\tUtil_";
  protected final String TEXT_26 = " util_";
  protected final String TEXT_27 = " = new Util_";
  protected final String TEXT_28 = "();" + NL + "        " + NL + "    int nb_line_";
  protected final String TEXT_29 = " = 0; ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);
    String lookupConName = "";
    if (metadata!=null) {//2
    
        String cid = node.getUniqueName();
        String hashName = "";

        //get the input Main and Lookup connection
        IConnection inMainCon = null;
        IConnection inRefCon = null;   
        List< ? extends IConnection> connsIn = node.getIncomingConnections();     
        for (IConnection conn : connsIn) {//3
        	if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN)) {
        		inMainCon = NodeUtil.getRealConnectionTypeBased(conn);
        	}
        	else if(conn.getLineStyle().equals(EConnectionType.FLOW_REF))
        	{
        		inRefCon = conn;
        		lookupConName = inRefCon.getName();
        		hashName = lookupConName;
        		if (conn.getSource().getComponent().isHashComponent()) {
        			hashName = ElementParameterParser.getValue(conn.getSource(), "__LIST__");
        			lookupConName = "row2";
        		}
        	}
        }//3 
        
        if(inMainCon == null || inRefCon == null){
        	return "";
        }
        
        String incomingName = inMainCon.getName();
        
        List<Map<String, String>> joinKeys = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__JOIN_KEY__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(hashName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_15);
                
		boolean hasHashableKey = false;
		for(Map<String, String> joinKeyLine : joinKeys){
			String inputKey = joinKeyLine.get("INPUT_COLUMN");
			String lookupKey = joinKeyLine.get("LOOKUP_COLUMN");
			hasHashableKey = true;            

    stringBuffer.append(TEXT_16);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(lookupKey );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(inputKey );
    stringBuffer.append(TEXT_20);
    
		}
		
		if(hasHashableKey) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_22);
    
		}

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(lookupConName );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_29);
    
    }//2
}//1

    return stringBuffer.toString();
  }
}
