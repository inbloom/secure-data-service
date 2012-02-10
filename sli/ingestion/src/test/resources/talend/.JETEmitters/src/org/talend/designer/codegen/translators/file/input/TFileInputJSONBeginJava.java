package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.utils.NodeUtil;

public class TFileInputJSONBeginJava
{
  protected static String nl;
  public static synchronized TFileInputJSONBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputJSONBeginJava result = new TFileInputJSONBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "  class JSONUtil_";
  protected final String TEXT_2 = "{" + NL + "      public int getData(String query,javax.script.Invocable invocableEngine,java.util.List<org.json.simple.JSONArray> jsonResultList,int recordMaxSize){" + NL + "          try{" + NL + "              //only 2 types: String/Boolean" + NL + "              String resultObj = invocableEngine.invokeFunction(\"jsonPath\",query).toString();" + NL + "              if(!\"false\".equals(resultObj)){" + NL + "\t              org.json.simple.JSONArray resultArray= (org.json.simple.JSONArray)org.json.simple.JSONValue.parse(resultObj);" + NL + "\t              jsonResultList.add(resultArray);" + NL + "\t              if(recordMaxSize != -1 && recordMaxSize != resultArray.size()){" + NL + "\t\t             //just give an error, don't stop" + NL + "\t\t              System.err.println(\"The Json resource datas maybe have some problems, please make sure the data structure with the same fields.\");" + NL + "\t               }" + NL + "\t               recordMaxSize = Math.max(recordMaxSize, resultArray.size());" + NL + "               }else{" + NL + "\t               System.err.println(\"Can't find any data with JSONPath \" + query);" + NL + "\t               //add null to take a place in List(buffer)" + NL + "\t               jsonResultList.add(null); " + NL + "               }" + NL + "          }catch(Exception e){" + NL + "              e.printStackTrace();" + NL + "          }" + NL + "          return recordMaxSize;" + NL + "      }" + NL;
  protected final String TEXT_3 = NL + "\tvoid setRowValue_";
  protected final String TEXT_4 = "(";
  protected final String TEXT_5 = "Struct ";
  protected final String TEXT_6 = ", java.util.List<org.json.simple.JSONArray> JSONResultList_";
  protected final String TEXT_7 = ", int nbResultArray_";
  protected final String TEXT_8 = ") throws java.io.UnsupportedEncodingException{";
  protected final String TEXT_9 = NL + "\t\t\t\t\t\t\t\tif(JSONResultList_";
  protected final String TEXT_10 = ".get(";
  protected final String TEXT_11 = ") != null && nbResultArray_";
  protected final String TEXT_12 = "<JSONResultList_";
  protected final String TEXT_13 = ".get(";
  protected final String TEXT_14 = ").size() && JSONResultList_";
  protected final String TEXT_15 = ".get(";
  protected final String TEXT_16 = ").get(nbResultArray_";
  protected final String TEXT_17 = ")!=null){" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_18 = "\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_19 = ".";
  protected final String TEXT_20 = " = JSONResultList_";
  protected final String TEXT_21 = ".get(";
  protected final String TEXT_22 = ").get(nbResultArray_";
  protected final String TEXT_23 = ").toString();";
  protected final String TEXT_24 = "\t\t\t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_25 = ".";
  protected final String TEXT_26 = " = ParserUtils.parseTo_Date(JSONResultList_";
  protected final String TEXT_27 = ".get(";
  protected final String TEXT_28 = ").get(nbResultArray_";
  protected final String TEXT_29 = ").toString(), ";
  protected final String TEXT_30 = ");\t\t\t\t";
  protected final String TEXT_31 = NL + "\t\t\t\t";
  protected final String TEXT_32 = ".";
  protected final String TEXT_33 = " = ParserUtils.parseTo_";
  protected final String TEXT_34 = "(ParserUtils.parseTo_Number(JSONResultList_";
  protected final String TEXT_35 = ".get(";
  protected final String TEXT_36 = ").get(nbResultArray_";
  protected final String TEXT_37 = ").toString(), ";
  protected final String TEXT_38 = ", ";
  protected final String TEXT_39 = "));";
  protected final String TEXT_40 = "\t\t\t\t\t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_41 = ".";
  protected final String TEXT_42 = " = JSONResultList_";
  protected final String TEXT_43 = ".get(";
  protected final String TEXT_44 = ").get(nbResultArray_";
  protected final String TEXT_45 = ").toString().getBytes(";
  protected final String TEXT_46 = ");";
  protected final String TEXT_47 = NL + "\t\t\t\t";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = " = ParserUtils.parseTo_";
  protected final String TEXT_50 = "(JSONResultList_";
  protected final String TEXT_51 = ".get(";
  protected final String TEXT_52 = ").get(nbResultArray_";
  protected final String TEXT_53 = ").toString());";
  protected final String TEXT_54 = NL + "\t\t\t\t\t\t\t\t}else{" + NL;
  protected final String TEXT_55 = NL + "\t\t\t\t\tthrow new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_56 = "' in '";
  protected final String TEXT_57 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_58 = NL + "\t\t\t\t";
  protected final String TEXT_59 = ".";
  protected final String TEXT_60 = " = ";
  protected final String TEXT_61 = ";" + NL + "\t\t\t";
  protected final String TEXT_62 = NL + "\t\t\t}";
  protected final String TEXT_63 = NL + "\t\t\t}";
  protected final String TEXT_64 = NL + "\t\t\t}";
  protected final String TEXT_65 = NL + "  }" + NL + "int nb_line_";
  protected final String TEXT_66 = " = 0;" + NL + "" + NL + "javax.script.ScriptEngineManager scriptEngineMgr_";
  protected final String TEXT_67 = " = new javax.script.ScriptEngineManager();" + NL + "javax.script.ScriptEngine jsEngine_";
  protected final String TEXT_68 = " = scriptEngineMgr_";
  protected final String TEXT_69 = ".getEngineByName(\"JavaScript\");" + NL + "if (jsEngine_";
  protected final String TEXT_70 = " == null) {" + NL + "    System.err.println(\"No script engine found for JavaScript\");" + NL + "}" + NL + "java.io.InputStream jsonis_";
  protected final String TEXT_71 = " = com.jsonpath.test.ReadJar.class.getResource(\"json.js\").openStream();" + NL + "jsEngine_";
  protected final String TEXT_72 = ".eval( new java.io.BufferedReader(new java.io.InputStreamReader(jsonis_";
  protected final String TEXT_73 = ")));" + NL + "" + NL + "java.io.InputStream jsonpathis_";
  protected final String TEXT_74 = " = com.jsonpath.test.ReadJar.class.getResource(\"jsonpath.js\").openStream();" + NL + "jsEngine_";
  protected final String TEXT_75 = ".eval(new java.io.BufferedReader(new java.io.InputStreamReader(jsonpathis_";
  protected final String TEXT_76 = ")));" + NL;
  protected final String TEXT_77 = NL + "java.io.BufferedReader fr_";
  protected final String TEXT_78 = " = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(";
  protected final String TEXT_79 = "),";
  protected final String TEXT_80 = "));";
  protected final String TEXT_81 = NL + "java.net.URL url_";
  protected final String TEXT_82 = " = new java.net.URL(";
  protected final String TEXT_83 = ");" + NL + "java.net.URLConnection urlConn_";
  protected final String TEXT_84 = " = url_";
  protected final String TEXT_85 = ".openConnection();" + NL + "java.io.InputStreamReader fr_";
  protected final String TEXT_86 = " = new java.io.InputStreamReader(urlConn_";
  protected final String TEXT_87 = ".getInputStream(),";
  protected final String TEXT_88 = ");";
  protected final String TEXT_89 = NL + "java.lang.Object jsonText_";
  protected final String TEXT_90 = " = org.json.simple.JSONValue.parse(fr_";
  protected final String TEXT_91 = ");" + NL + "jsEngine_";
  protected final String TEXT_92 = ".eval(\"var obj=\"+jsonText_";
  protected final String TEXT_93 = ".toString());" + NL + "" + NL + "java.util.List<org.json.simple.JSONArray> JSONResultList_";
  protected final String TEXT_94 = " = new java.util.ArrayList<org.json.simple.JSONArray>();" + NL + "" + NL + "int recordMaxSize_";
  protected final String TEXT_95 = " = -1;" + NL + "javax.script.Invocable invocableEngine_";
  protected final String TEXT_96 = " = (javax.script.Invocable)jsEngine_";
  protected final String TEXT_97 = ";" + NL + "" + NL + "JSONUtil_";
  protected final String TEXT_98 = " jsonUtil_";
  protected final String TEXT_99 = "=new JSONUtil_";
  protected final String TEXT_100 = "();";
  protected final String TEXT_101 = NL + NL + "recordMaxSize_";
  protected final String TEXT_102 = "=jsonUtil_";
  protected final String TEXT_103 = ".getData(";
  protected final String TEXT_104 = ",invocableEngine_";
  protected final String TEXT_105 = ",JSONResultList_";
  protected final String TEXT_106 = ",recordMaxSize_";
  protected final String TEXT_107 = ");" + NL;
  protected final String TEXT_108 = NL + NL + NL + "\tfor(int nbResultArray_";
  protected final String TEXT_109 = " = 0; nbResultArray_";
  protected final String TEXT_110 = " < recordMaxSize_";
  protected final String TEXT_111 = "; nbResultArray_";
  protected final String TEXT_112 = "++){" + NL + "\t" + NL + "\t\tnb_line_";
  protected final String TEXT_113 = "++;";
  protected final String TEXT_114 = NL + "\tjsonUtil_";
  protected final String TEXT_115 = ".setRowValue_";
  protected final String TEXT_116 = "(";
  protected final String TEXT_117 = ",JSONResultList_";
  protected final String TEXT_118 = ",nbResultArray_";
  protected final String TEXT_119 = ");";
  protected final String TEXT_120 = NL + "\t";
  protected final String TEXT_121 = ".";
  protected final String TEXT_122 = " = ";
  protected final String TEXT_123 = ".";
  protected final String TEXT_124 = ";";
  protected final String TEXT_125 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();

List<Map<String, String>> mapping = (List<Map<String,String>>)ElementParameterParser.getObjectValueXML(node, "__MAPPING__");
String encoding = ElementParameterParser.getValue(node, "__ENCODING__");

String useUrl = ElementParameterParser.getValue(node, "__USEURL__");
boolean isUseUrl = "true".equals(useUrl);
String urlpath = ElementParameterParser.getValue(node, "__URLPATH__");

String filename = ElementParameterParser.getValue(node, "__FILENAME__");

String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);

List< ? extends IConnection> conns = NodeUtil.getOutgoingConnections(node, IConnectionCategory.DATA);
String firstConnName = "";
List<IMetadataTable> metadatas = node.getMetadataList();

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {
		List<IMetadataColumn> columns=metadata.getListColumns();
		if (conns!=null) {
			if (conns.size()>0) {
				IConnection conn = conns.get(0);
				firstConnName = conn.getName();
				for (int i=0;i<mapping.size();i++) {
    				if(i % 100 == 0){

    stringBuffer.append(TEXT_3);
    stringBuffer.append((i/100) );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    
    				}

    
					for(IMetadataColumn column:columns) {
						if (mapping.get(i).get("SCHEMA_COLUMN")!=null) {
							if (column.getLabel().compareTo(mapping.get(i).get("SCHEMA_COLUMN"))==0) {
								String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
								JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
								String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    		
						if (javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_18);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    		
						} else if(javaType == JavaTypesManager.DATE) {						

    stringBuffer.append(TEXT_24);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_30);
    		
						}else if(advancedSeparator && JavaTypesManager.isNumberType(javaType)) { 

    stringBuffer.append(TEXT_31);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_38);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_39);
    
						} else if(javaType == JavaTypesManager.BYTE_ARRAY) { 

    stringBuffer.append(TEXT_40);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_46);
    
						} else {

    stringBuffer.append(TEXT_47);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_49);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    
						}

    stringBuffer.append(TEXT_54);
    
						String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
						if(defaultValue == null) {

    stringBuffer.append(TEXT_55);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_57);
    
						} else {

    stringBuffer.append(TEXT_58);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(defaultValue);
    stringBuffer.append(TEXT_61);
    
						}

    stringBuffer.append(TEXT_62);
    

}
						}
					}
    				if((i + 1) % 100 == 0){

    stringBuffer.append(TEXT_63);
    
    				}
				} // for (int i=0)
  				if(mapping.size() > 0 && mapping.size() % 100 > 0){

    stringBuffer.append(TEXT_64);
    
 				 }
			}

		}

	}
}

    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    if(!isUseUrl){//read from a file
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_80);
    }else{ //read from internet
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(urlpath );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_88);
    }
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    
	for(Map<String,String> path:mapping){
		String column = path.get("SCHEMA_COLUMN");
		String query = path.get("QUERY");

    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(query);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    
	}

    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_113);
    
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {
		List<IMetadataColumn> columns=metadata.getListColumns();
		if (conns!=null) {
			if (conns.size()>0) {
				IConnection conn = conns.get(0);
				firstConnName = conn.getName();
				for (int i=0;i<mapping.size();i++) {
					if(i % 100 == 0){

    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append( (i/100) );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    
					}
				}// for
			}// conns.size()>0
			/**
			have no idea what the following code is used for
			*/
			if (conns.size()>1) {
				for (int i=1;i<conns.size();i++) {
					IConnection conn = conns.get(i);
					if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {
						for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_120);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_124);
    
						}
					}
				}
			}
		} // if(conns!=null)
	} // if (metadata!=null)
} // if ((metadatas!=null)&&(metadatas.size()>0))

    stringBuffer.append(TEXT_125);
    return stringBuffer.toString();
  }
}
