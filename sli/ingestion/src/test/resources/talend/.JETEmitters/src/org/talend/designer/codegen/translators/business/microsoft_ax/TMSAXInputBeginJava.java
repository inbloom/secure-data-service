package org.talend.designer.codegen.translators.business.microsoft_ax;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;

public class TMSAXInputBeginJava
{
  protected static String nl;
  public static synchronized TMSAXInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMSAXInputBeginJava result = new TMSAXInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "" + NL + "//connect to com server" + NL + "org.jinterop.dcom.common.JISystem.setAutoRegisteration(true);" + NL + "org.jinterop.dcom.core.JISession session_";
  protected final String TEXT_3 = " = org.jinterop.dcom.core.JISession.createSession(";
  protected final String TEXT_4 = ", ";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ");" + NL + "org.jinterop.dcom.core.JIClsid clsid_";
  protected final String TEXT_7 = " = org.jinterop.dcom.core.JIClsid.valueOf(\"71421B8A-81A8-4373-BD8D-E0D83B0B3DAB\");" + NL + "org.jinterop.dcom.core.JIComServer comServer_";
  protected final String TEXT_8 = " = new org.jinterop.dcom.core.JIComServer(clsid_";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = ", session_";
  protected final String TEXT_11 = ");" + NL + "" + NL + "//get IAxapta3 interface" + NL + "org.jinterop.dcom.core.IJIComObject comObject_";
  protected final String TEXT_12 = " = comServer_";
  protected final String TEXT_13 = ".createInstance();" + NL + "org.jinterop.dcom.win32.IJIDispatch  axapta3_";
  protected final String TEXT_14 = " = (org.jinterop.dcom.win32.IJIDispatch) org.jinterop.dcom.win32.ComFactory.createCOMInstance(" + NL + "                    org.jinterop.dcom.win32.ComFactory.IID_IDispatch, comObject_";
  protected final String TEXT_15 = ");" + NL + "" + NL + "//logon ax server" + NL + "axapta3_";
  protected final String TEXT_16 = ".callMethod(\"Logon\", " + NL + "\tnew Object[] { ";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ", ";
  protected final String TEXT_19 = ", ";
  protected final String TEXT_20 = " });" + NL + "" + NL + "//init record" + NL + "org.jinterop.dcom.core.JIVariant[] results_";
  protected final String TEXT_21 = " = axapta3_";
  protected final String TEXT_22 = ".callMethodA(\"CreateRecord\", new Object[]{";
  protected final String TEXT_23 = " });" + NL + "org.jinterop.dcom.win32.IJIDispatch record_";
  protected final String TEXT_24 = " = (org.jinterop.dcom.win32.IJIDispatch) results_";
  protected final String TEXT_25 = "[0].getObjectAsComObject(comObject_";
  protected final String TEXT_26 = ");" + NL + "" + NL + "//exe the SQL query" + NL + "String sqlstmt_";
  protected final String TEXT_27 = " = ";
  protected final String TEXT_28 = ";" + NL + "org.jinterop.dcom.core.JIVariant sqlStmtVar_";
  protected final String TEXT_29 = " = new org.jinterop.dcom.core.JIVariant(new org.jinterop.dcom.core.JIString(sqlstmt_";
  protected final String TEXT_30 = "));" + NL + "record_";
  protected final String TEXT_31 = ".callMethod(\"ExecuteStmt\", new Object[] { sqlStmtVar_";
  protected final String TEXT_32 = "});" + NL + "" + NL + "java.util.Calendar calendar_";
  protected final String TEXT_33 = " = java.util.Calendar.getInstance();" + NL + "calendar_";
  protected final String TEXT_34 = ".set(0, 0, 0, 0, 0, 0);" + NL + "java.util.Date year0_";
  protected final String TEXT_35 = " = calendar_";
  protected final String TEXT_36 = ".getTime();" + NL + "globalMap.put(\"";
  protected final String TEXT_37 = "_QUERY\",";
  protected final String TEXT_38 = ");" + NL;
  protected final String TEXT_39 = NL + "//loop record" + NL + "while(record_";
  protected final String TEXT_40 = ".get(\"Found\").getObjectAsBoolean()){" + NL + "\tnb_line_";
  protected final String TEXT_41 = "++;";
  protected final String TEXT_42 = " \t" + NL + "\t{" + NL + "\t\torg.jinterop.dcom.core.JIVariant[] field_";
  protected final String TEXT_43 = " =  record_";
  protected final String TEXT_44 = ".get(\"field\", new Object[] { ";
  protected final String TEXT_45 = " });";
  protected final String TEXT_46 = NL + "\t \tif(field_";
  protected final String TEXT_47 = "!=null && field_";
  protected final String TEXT_48 = ".length>0 && field_";
  protected final String TEXT_49 = "[0].getObject() != null)" + NL + "\t \t{";
  protected final String TEXT_50 = NL + "\t\t\t";
  protected final String TEXT_51 = ".";
  protected final String TEXT_52 = " = (List)field_";
  protected final String TEXT_53 = "[0].getObjectAsArray().getArrayInstance();";
  protected final String TEXT_54 = NL + "\t\t\t";
  protected final String TEXT_55 = ".";
  protected final String TEXT_56 = " = field_";
  protected final String TEXT_57 = "[0].getObject();";
  protected final String TEXT_58 = NL + "\t\t\t";
  protected final String TEXT_59 = ".";
  protected final String TEXT_60 = " = field_";
  protected final String TEXT_61 = "[0].getObjectAsString().getString();";
  protected final String TEXT_62 = NL + "\t \t\t";
  protected final String TEXT_63 = ".";
  protected final String TEXT_64 = " = field_";
  protected final String TEXT_65 = "[0].getObjectAs";
  protected final String TEXT_66 = "();";
  protected final String TEXT_67 = NL + "\t \t}else{";
  protected final String TEXT_68 = NL + " \t\t\t";
  protected final String TEXT_69 = ".";
  protected final String TEXT_70 = " = null;";
  protected final String TEXT_71 = "    " + NL + "\t\t \tthrow new RuntimeException(\"Null value in non-Nullable column\");";
  protected final String TEXT_72 = NL + " \t\t}" + NL + " \t}\t";
  protected final String TEXT_73 = NL + " \t\t   \t";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = "=";
  protected final String TEXT_76 = ".";
  protected final String TEXT_77 = ";";
  protected final String TEXT_78 = NL;
  protected final String TEXT_79 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
        
String axHost = ElementParameterParser.getValue(node, "__HOST__");
String axDomain = ElementParameterParser.getValue(node, "__DOMAIN__");
String axUser = ElementParameterParser.getValue(node, "__USER__");
String axPwd = ElementParameterParser.getValue(node, "__PASS__");
String axTable = ElementParameterParser.getValue(node, "__TABLE__");
String dbquery = ElementParameterParser.getValue(node, "__QUERY__");
	   dbquery = dbquery.replaceAll("\n"," ");
	   dbquery = dbquery.replaceAll("\r"," ");

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {

    	class VariantTool{
    		public String vStr(String value){
    			return "new org.jinterop.dcom.core.JIVariant(new org.jinterop.dcom.core.JIString("+value+"))";
    		}
    		public String vInt(int value){
    			return "new org.jinterop.dcom.core.JIVariant("+value+")";
    		}
    	}
    	VariantTool vTool = new VariantTool();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(axDomain);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(axUser);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(axPwd);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(axHost);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_17);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_18);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_19);
    stringBuffer.append(vTool.vStr("\"\""));
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(vTool.vStr(axTable));
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_38);
    
        List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
        List<IMetadataColumn> columnList = metadata.getListColumns();

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    
        if(conns != null && conns.size()>0){
         	IConnection conn = conns.get(0);
         	String firstConnName = conn.getName();
         	for(IMetadataColumn column:columnList){
             	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
             	String defVal = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate);
             	if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(vTool.vStr("\""+column.getLabel()+"\""));
    stringBuffer.append(TEXT_45);
    
            		if(("byte[]").equals(typeToGenerate)){
            			typeToGenerate = "Bytes";
            		}else if(("java.util.Date").equals(typeToGenerate)){
            			typeToGenerate = "Date";
                    }else if(("Integer").equals(typeToGenerate)){
                      	typeToGenerate = "Int";
                    }else{
                    	typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
                    }

    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    
	 				if (("List").equals(typeToGenerate)) {

    stringBuffer.append(TEXT_50);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    
					}else if(("Object").equals(typeToGenerate)){

    stringBuffer.append(TEXT_54);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    
					}else if(("String").equals(typeToGenerate)){

    stringBuffer.append(TEXT_58);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    
					}else{

    stringBuffer.append(TEXT_62);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_66);
    
					}

    stringBuffer.append(TEXT_67);
    
    				if(column.isNullable()){

    stringBuffer.append(TEXT_68);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_70);
    
		 			}else{

    stringBuffer.append(TEXT_71);
    
            		}

    stringBuffer.append(TEXT_72);
    
    			}
 			}
         	if(conns.size()>1){
         		for(int connNO = 1; connNO < conns.size(); connNO++){
        			IConnection conn2 = conns.get(connNO);
        			if((conn2.getName().compareTo(firstConnName)!=0)&&(conn2.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))){
        				for(IMetadataColumn column:columnList){

    stringBuffer.append(TEXT_73);
    stringBuffer.append(conn2.getName());
    stringBuffer.append(TEXT_74);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_75);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_77);
     
        				}
        			}
         		}
         	}
		}

	}
}

    stringBuffer.append(TEXT_78);
    stringBuffer.append(TEXT_79);
    return stringBuffer.toString();
  }
}
