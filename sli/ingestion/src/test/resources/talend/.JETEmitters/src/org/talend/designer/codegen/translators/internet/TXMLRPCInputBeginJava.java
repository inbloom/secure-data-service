package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.commons.utils.StringUtils;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.IConnectionCategory;
import java.util.Map;
import java.util.List;

public class TXMLRPCInputBeginJava
{
  protected static String nl;
  public static synchronized TXMLRPCInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TXMLRPCInputBeginJava result = new TXMLRPCInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "org.apache.xmlrpc.client.XmlRpcClientConfigImpl config";
  protected final String TEXT_2 = "= new org.apache.xmlrpc.client.XmlRpcClientConfigImpl();" + NL + "config";
  protected final String TEXT_3 = ".setServerURL(new java.net.URL(";
  protected final String TEXT_4 = "));" + NL + "config";
  protected final String TEXT_5 = ".setEnabledForExtensions(true);  " + NL + "config";
  protected final String TEXT_6 = ".setConnectionTimeout(60 * 1000);" + NL + "config";
  protected final String TEXT_7 = ".setReplyTimeout(60 * 1000);";
  protected final String TEXT_8 = NL + "\tconfig";
  protected final String TEXT_9 = ".setBasicUserName(";
  protected final String TEXT_10 = ");" + NL + "\tconfig";
  protected final String TEXT_11 = ".setBasicPassword(";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL + "org.apache.xmlrpc.client.XmlRpcClient client";
  protected final String TEXT_14 = " = new org.apache.xmlrpc.client.XmlRpcClient();" + NL + "// use Commons HttpClient as transport" + NL + " client";
  protected final String TEXT_15 = ".setTransportFactory(" + NL + "              new org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory(client";
  protected final String TEXT_16 = "));" + NL + "        // set configuration" + NL + "\t\tclient";
  protected final String TEXT_17 = ".setConfig(config";
  protected final String TEXT_18 = ");" + NL + "        // make the a regular call" + NL + "        Object[] params_";
  protected final String TEXT_19 = " = new Object[] {         " + NL + "\t  ";
  protected final String TEXT_20 = NL + "\t        \t";
  protected final String TEXT_21 = "," + NL + "\t\t    ";
  protected final String TEXT_22 = "      " + NL + "        };  " + NL + "        Object[] results_";
  protected final String TEXT_23 = " = null;" + NL + "\t    int nb_line_";
  protected final String TEXT_24 = " = 0;\t " + NL + "        Object reValue";
  protected final String TEXT_25 = " = client";
  protected final String TEXT_26 = ".execute(";
  protected final String TEXT_27 = ", params_";
  protected final String TEXT_28 = ");" + NL + "        " + NL + "        Object[] tempArray_";
  protected final String TEXT_29 = " = null;" + NL + "\t\tif (reValue";
  protected final String TEXT_30 = " instanceof Object[]) {\t\t\t" + NL + "\t\t\ttempArray_";
  protected final String TEXT_31 = " = (Object[]) reValue";
  protected final String TEXT_32 = ";" + NL + "\t\t} else {" + NL + "\t\t\ttempArray_";
  protected final String TEXT_33 = " = new Object[1];" + NL + "\t\t\ttempArray_";
  protected final String TEXT_34 = "[0] = reValue";
  protected final String TEXT_35 = ";" + NL + "\t\t}" + NL + "        for (int i_";
  protected final String TEXT_36 = " = 0; i_";
  protected final String TEXT_37 = " < tempArray_";
  protected final String TEXT_38 = ".length; i_";
  protected final String TEXT_39 = "++) {" + NL + "\t\t    results_";
  protected final String TEXT_40 = " = new Object[";
  protected final String TEXT_41 = "];\t\t    " + NL + "\t\t\t" + NL + "\t\t\tif (tempArray_";
  protected final String TEXT_42 = "[i_";
  protected final String TEXT_43 = "] instanceof java.util.Map) {" + NL + "\t\t\t\tjava.util.Map map_";
  protected final String TEXT_44 = " = (java.util.Map)tempArray_";
  protected final String TEXT_45 = "[i_";
  protected final String TEXT_46 = "];" + NL + "                java.util.Collection values_";
  protected final String TEXT_47 = " = map_";
  protected final String TEXT_48 = ".values();" + NL + "                int len_";
  protected final String TEXT_49 = " = Math.min(values_";
  protected final String TEXT_50 = ".size(), results_";
  protected final String TEXT_51 = ".length);" + NL + "                int k_";
  protected final String TEXT_52 = " = 0;" + NL + "                for (java.util.Iterator iter_";
  protected final String TEXT_53 = " = values_";
  protected final String TEXT_54 = ".iterator(); iter_";
  protected final String TEXT_55 = ".hasNext() && k_";
  protected final String TEXT_56 = " < len_";
  protected final String TEXT_57 = "; k_";
  protected final String TEXT_58 = "++) {" + NL + "                    results_";
  protected final String TEXT_59 = "[k_";
  protected final String TEXT_60 = "] =iter_";
  protected final String TEXT_61 = ".next();" + NL + "                    if(results_";
  protected final String TEXT_62 = "[k_";
  protected final String TEXT_63 = "] instanceof Object[] ){" + NL + "                    \tjava.util.List<Object> val_";
  protected final String TEXT_64 = " = new java.util.ArrayList<Object>();" + NL + "                    \t//results_";
  protected final String TEXT_65 = " = org.talend.rpc.util.ArrayUtilities.dumpMapInArray((Object[])results_";
  protected final String TEXT_66 = "[k_";
  protected final String TEXT_67 = "], val_";
  protected final String TEXT_68 = ").toArray();" + NL + "                    \tresults_";
  protected final String TEXT_69 = "[k_";
  protected final String TEXT_70 = "] = org.talend.rpc.util.ArrayUtilities.dumpMapInArray((Object[])results_";
  protected final String TEXT_71 = "[k_";
  protected final String TEXT_72 = "], val_";
  protected final String TEXT_73 = ").toArray();" + NL + "" + NL + "                    }" + NL + "                    else " + NL + "                     results_";
  protected final String TEXT_74 = "[k_";
  protected final String TEXT_75 = "] =String.valueOf(results_";
  protected final String TEXT_76 = "[k_";
  protected final String TEXT_77 = "]);" + NL + "                    \t" + NL + "                }\t\t\t\t" + NL + "\t\t\t} else {" + NL + "\t\t\t\tresults_";
  protected final String TEXT_78 = "[0] = String.valueOf(tempArray_";
  protected final String TEXT_79 = "[i_";
  protected final String TEXT_80 = "]);" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "        nb_line_";
  protected final String TEXT_81 = "++;" + NL + "\t       " + NL + "// for output";
  protected final String TEXT_82 = NL + "\t\t\t" + NL + "\t\t\tif(";
  protected final String TEXT_83 = " < results_";
  protected final String TEXT_84 = ".length && results_";
  protected final String TEXT_85 = "[";
  protected final String TEXT_86 = "]!=null){\t\t\t\t";
  protected final String TEXT_87 = NL + "\t\t\t\t\t";
  protected final String TEXT_88 = ".";
  protected final String TEXT_89 = " = results_";
  protected final String TEXT_90 = "[";
  protected final String TEXT_91 = "].toString();";
  protected final String TEXT_92 = NL + "\t\t\t\t\t";
  protected final String TEXT_93 = ".";
  protected final String TEXT_94 = " = ParserUtils.parseTo_Date(results_";
  protected final String TEXT_95 = "[";
  protected final String TEXT_96 = "], ";
  protected final String TEXT_97 = ");";
  protected final String TEXT_98 = NL + "\t\t\t\t\t";
  protected final String TEXT_99 = ".";
  protected final String TEXT_100 = " = results_";
  protected final String TEXT_101 = "[";
  protected final String TEXT_102 = "].getBytes();" + NL + "\t";
  protected final String TEXT_103 = "\t\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_104 = ".";
  protected final String TEXT_105 = " = ParserUtils.parseTo_";
  protected final String TEXT_106 = "(results_";
  protected final String TEXT_107 = "[";
  protected final String TEXT_108 = "]);";
  protected final String TEXT_109 = NL + "\t\t\t} else { " + NL + "\t\t\t\t\t";
  protected final String TEXT_110 = ".";
  protected final String TEXT_111 = " = ";
  protected final String TEXT_112 = ";" + NL + "\t\t\t}";
  protected final String TEXT_113 = NL + "   ";
  protected final String TEXT_114 = "  ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        String cid = node.getUniqueName();
        List<IMetadataColumn> listColumns = metadata.getListColumns(); 
        String needAuth = ElementParameterParser.getValue(node, "__NEED_AUTH__");
        String authUsername = ElementParameterParser.getValue(node, "__AUTH_USERNAME__");
		String authPassword = ElementParameterParser.getValue(node, "__AUTH_PASSWORD__");
        List<Map<String, String>> params = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__PARAMS__");
        String  url=ElementParameterParser.getValue(node,"__SERVER-URL__");
        String  method=ElementParameterParser.getValue(node,"__METHOD__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(url);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    
 if ("true".equals(needAuth)) {
 
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(authUsername);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(authPassword);
    stringBuffer.append(TEXT_12);
    
}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    
	        for (int i = 0; i < params.size(); i++) {
	            Map<String, String> line = params.get(i);
	  
    stringBuffer.append(TEXT_20);
    stringBuffer.append( line.get("VALUE") );
    stringBuffer.append(TEXT_21);
    
	        }
		    
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(listColumns.size() );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	String firstConnName = "";
	if (conns!=null) {//1
		if (conns.size()>0) {//2
			IConnection conn = conns.get(0); //the first connection
			firstConnName = conn.getName();			
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//3
    			List<IMetadataColumn> columns=metadata.getListColumns();
    			int columnSize = columns.size();
    			for (int i=0;i<columnSize;i++) {//4
    				IMetadataColumn column=columns.get(i);
    				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
    				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
    				String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
			
    stringBuffer.append(TEXT_82);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_86);
    
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) { //String or Object

    stringBuffer.append(TEXT_87);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_91);
    
					} else if(javaType == JavaTypesManager.DATE) { //Date

    stringBuffer.append(TEXT_92);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_96);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_97);
    
					} else if(javaType == JavaTypesManager.BYTE_ARRAY) { //byte[]

    stringBuffer.append(TEXT_98);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_102);
    
					} else  { //other

    stringBuffer.append(TEXT_103);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_108);
    
					}

    stringBuffer.append(TEXT_109);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate));
    stringBuffer.append(TEXT_112);
    			
				} //4
			}//3
		}//2
	}//1


    stringBuffer.append(TEXT_113);
    }
 }
 
    stringBuffer.append(TEXT_114);
    return stringBuffer.toString();
  }
}
