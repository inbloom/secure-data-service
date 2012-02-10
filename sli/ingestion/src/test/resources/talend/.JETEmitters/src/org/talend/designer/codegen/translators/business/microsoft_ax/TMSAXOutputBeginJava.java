package org.talend.designer.codegen.translators.business.microsoft_ax;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.MappingTypeRetriever;
import org.talend.core.model.metadata.MetadataTalendType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class TMSAXOutputBeginJava
{
  protected static String nl;
  public static synchronized TMSAXOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSAXOutputBeginJava result = new TMSAXOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "    int keyCount_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "    if(keyCount_";
  protected final String TEXT_6 = " < 1){" + NL + "    \tthrow new RuntimeException(\"For update or delete, Schema must have a key\");" + NL + "    }";
  protected final String TEXT_7 = " " + NL + "" + NL + "int nb_line_";
  protected final String TEXT_8 = " = 0;" + NL + "int nb_line_update_";
  protected final String TEXT_9 = " = 0;" + NL + "int nb_line_inserted_";
  protected final String TEXT_10 = " = 0;" + NL + "int nb_line_deleted_";
  protected final String TEXT_11 = " = 0;" + NL + "" + NL + "int deletedCount_";
  protected final String TEXT_12 = "=0;" + NL + "int updatedCount_";
  protected final String TEXT_13 = "=0;" + NL + "int insertedCount_";
  protected final String TEXT_14 = "=0;" + NL + "" + NL + "boolean whetherReject_";
  protected final String TEXT_15 = " = false;" + NL + "" + NL + "java.util.Calendar calendar_";
  protected final String TEXT_16 = " = java.util.Calendar.getInstance();" + NL + "calendar_";
  protected final String TEXT_17 = ".set(1, 0, 1, 0, 0, 0);" + NL + "long year1_";
  protected final String TEXT_18 = " = calendar_";
  protected final String TEXT_19 = ".getTime().getTime();" + NL + "calendar_";
  protected final String TEXT_20 = ".set(10000, 0, 1, 0, 0, 0);" + NL + "long year10000_";
  protected final String TEXT_21 = " = calendar_";
  protected final String TEXT_22 = ".getTime().getTime();" + NL + "long date_";
  protected final String TEXT_23 = ";" + NL + "" + NL + "//connect to com server" + NL + "org.jinterop.dcom.common.JISystem.setAutoRegisteration(true);" + NL + "org.jinterop.dcom.core.JISession session_";
  protected final String TEXT_24 = " = org.jinterop.dcom.core.JISession.createSession(";
  protected final String TEXT_25 = ", ";
  protected final String TEXT_26 = ", ";
  protected final String TEXT_27 = ");" + NL + "org.jinterop.dcom.core.JIClsid clsid_";
  protected final String TEXT_28 = " = org.jinterop.dcom.core.JIClsid.valueOf(\"71421B8A-81A8-4373-BD8D-E0D83B0B3DAB\");" + NL + "org.jinterop.dcom.core.JIComServer comServer_";
  protected final String TEXT_29 = " = new org.jinterop.dcom.core.JIComServer(clsid_";
  protected final String TEXT_30 = ", ";
  protected final String TEXT_31 = ", session_";
  protected final String TEXT_32 = ");" + NL + "" + NL + "//get IAxapta3 interface" + NL + "org.jinterop.dcom.core.IJIComObject comObject_";
  protected final String TEXT_33 = " = comServer_";
  protected final String TEXT_34 = ".createInstance();" + NL + "org.jinterop.dcom.win32.IJIDispatch  axapta3_";
  protected final String TEXT_35 = " = (org.jinterop.dcom.win32.IJIDispatch) org.jinterop.dcom.win32.ComFactory.createCOMInstance(" + NL + "                    org.jinterop.dcom.win32.ComFactory.IID_IDispatch, comObject_";
  protected final String TEXT_36 = ");" + NL + "" + NL + "//logon ax server" + NL + "axapta3_";
  protected final String TEXT_37 = ".callMethod(\"Logon\", " + NL + "\tnew Object[] { ";
  protected final String TEXT_38 = ", ";
  protected final String TEXT_39 = ", ";
  protected final String TEXT_40 = ", ";
  protected final String TEXT_41 = " });" + NL + "" + NL + "//init record" + NL + "org.jinterop.dcom.core.JIVariant[] results_";
  protected final String TEXT_42 = " = axapta3_";
  protected final String TEXT_43 = ".callMethodA(\"CreateRecord\", new Object[]{";
  protected final String TEXT_44 = " });" + NL + "org.jinterop.dcom.win32.IJIDispatch record_";
  protected final String TEXT_45 = " = (org.jinterop.dcom.win32.IJIDispatch) results_";
  protected final String TEXT_46 = "[0].getObjectAsComObject(comObject_";
  protected final String TEXT_47 = ");" + NL + "" + NL + "//begin modify" + NL + "axapta3_";
  protected final String TEXT_48 = ".callMethod(\"TTSBegin\");";
  protected final String TEXT_49 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
    List<Map<String, String>> addCols =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,"__ADD_COLS__" );
    String axHost = ElementParameterParser.getValue(node, "__HOST__");
	String axDomain = ElementParameterParser.getValue(node, "__DOMAIN__");
	String axUser= ElementParameterParser.getValue(node, "__USER__");
	String axPwd= ElementParameterParser.getValue(node, "__PASS__");
	String axTable = ElementParameterParser.getValue(node,"__TABLE__");
	String dataAction = ElementParameterParser.getValue(node,"__DATA_ACTION__");
	
    List<IMetadataColumn> columnList = null;
    List<IMetadataTable> metadatas = node.getMetadataList();
    if(metadatas !=null && metadatas.size()>0){
    	IMetadataTable metadata = metadatas.get(0);
    	if(metadata != null){
    		columnList = metadata.getListColumns();
    	}
    }

    stringBuffer.append(TEXT_2);
    
if(!("INSERT").equals(dataAction)){
    int keyCount = 0;
    for(IMetadataColumn column:columnList){
    	if(column.isKey()){
    		keyCount++;
    	}
    }

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(keyCount);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
}

class VariantTool{
	public String vStr(String value){
		return "new org.jinterop.dcom.core.JIVariant(new org.jinterop.dcom.core.JIString("+value+"))";
	}
	public String vInt(int value){
		return "new org.jinterop.dcom.core.JIVariant("+value+")";
	}
}
VariantTool vTool = new VariantTool();

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(axDomain);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(axUser);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(axPwd);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(axHost);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_38);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_39);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_40);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(vTool.vStr(axTable));
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(TEXT_49);
    return stringBuffer.toString();
  }
}
