package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TExtractXMLFieldMainJava
{
  protected static String nl;
  public static synchronized TExtractXMLFieldMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TExtractXMLFieldMainJava result = new TExtractXMLFieldMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tString xmlStr_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";";
  protected final String TEXT_5 = NL + "\tString xmlStr_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = "_xml;";
  protected final String TEXT_8 = NL + "\tString xmlStr_";
  protected final String TEXT_9 = " = ";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ";";
  protected final String TEXT_12 = NL;
  protected final String TEXT_13 = " = null;";
  protected final String TEXT_14 = NL + "\tNameSpaceTool_";
  protected final String TEXT_15 = " nsTool_";
  protected final String TEXT_16 = " = new NameSpaceTool_";
  protected final String TEXT_17 = "();" + NL + "    org.dom4j.io.SAXReader reader_";
  protected final String TEXT_18 = " = new org.dom4j.io.SAXReader();" + NL + "    org.dom4j.Document doc_";
  protected final String TEXT_19 = " = null;" + NL + "    java.util.HashMap xmlNameSpaceMap_";
  protected final String TEXT_20 = " = null;" + NL + "    org.dom4j.XPath x_";
  protected final String TEXT_21 = " = null;" + NL + "    java.util.List<org.dom4j.tree.AbstractNode> nodeList_";
  protected final String TEXT_22 = " = null;";
  protected final String TEXT_23 = NL + "\tString loopQuery_";
  protected final String TEXT_24 = " = ";
  protected final String TEXT_25 = "+";
  protected final String TEXT_26 = ";";
  protected final String TEXT_27 = NL + "    \tString loopQuery_";
  protected final String TEXT_28 = " = ";
  protected final String TEXT_29 = ";" + NL + "\t";
  protected final String TEXT_30 = NL + "\t\tString loopQuery_";
  protected final String TEXT_31 = " = ";
  protected final String TEXT_32 = "; " + NL + "\t";
  protected final String TEXT_33 = NL + "    boolean isStructError_";
  protected final String TEXT_34 = "= true;" + NL + "        " + NL + "    try{" + NL + "    " + NL + "\t    doc_";
  protected final String TEXT_35 = "= reader_";
  protected final String TEXT_36 = ".read(new java.io.StringReader(xmlStr_";
  protected final String TEXT_37 = "));" + NL + "\t    nsTool_";
  protected final String TEXT_38 = ".countNSMap(doc_";
  protected final String TEXT_39 = ".getRootElement());" + NL + "\t    xmlNameSpaceMap_";
  protected final String TEXT_40 = " = nsTool_";
  protected final String TEXT_41 = ".xmlNameSpaceMap;" + NL + "" + NL + "    \tx_";
  protected final String TEXT_42 = " = doc_";
  protected final String TEXT_43 = ".createXPath(nsTool_";
  protected final String TEXT_44 = ".addDefaultNSPrefix(loopQuery_";
  protected final String TEXT_45 = ",loopQuery_";
  protected final String TEXT_46 = "));" + NL + "      " + NL + "    \tx_";
  protected final String TEXT_47 = ".setNamespaceURIs(xmlNameSpaceMap_";
  protected final String TEXT_48 = "); " + NL + "    " + NL + "    \tnodeList_";
  protected final String TEXT_49 = " = (java.util.List<org.dom4j.tree.AbstractNode>)x_";
  protected final String TEXT_50 = ".selectNodes(doc_";
  protected final String TEXT_51 = ");" + NL + "    \t" + NL + "    \tisStructError_";
  protected final String TEXT_52 = " = false;" + NL + "    \t" + NL + "    }catch(Exception ex_";
  protected final String TEXT_53 = "){";
  protected final String TEXT_54 = NL + "\tthrow(ex_";
  protected final String TEXT_55 = ");";
  protected final String TEXT_56 = NL + "\t";
  protected final String TEXT_57 = " = new ";
  protected final String TEXT_58 = "Struct();";
  protected final String TEXT_59 = NL + "    ";
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = " = ";
  protected final String TEXT_62 = ".";
  protected final String TEXT_63 = ";";
  protected final String TEXT_64 = NL + "\t";
  protected final String TEXT_65 = ".errorXMLField = xmlStr_";
  protected final String TEXT_66 = ";" + NL + "\t";
  protected final String TEXT_67 = ".errorMessage = ex_";
  protected final String TEXT_68 = ".getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_69 = ";";
  protected final String TEXT_70 = NL + "    System.err.println(ex_";
  protected final String TEXT_71 = ".getMessage());";
  protected final String TEXT_72 = NL + "    }" + NL + "    " + NL + "    org.dom4j.Node node_";
  protected final String TEXT_73 = " = null;" + NL + "    String str_";
  protected final String TEXT_74 = " = \"\";" + NL + "    for(int i_";
  protected final String TEXT_75 = "=0; isStructError_";
  protected final String TEXT_76 = " || (nodeList_";
  protected final String TEXT_77 = "!=null && i_";
  protected final String TEXT_78 = " < nodeList_";
  protected final String TEXT_79 = ".size());i_";
  protected final String TEXT_80 = "++){" + NL + "    \t" + NL + "    \tif(!isStructError_";
  protected final String TEXT_81 = "){";
  protected final String TEXT_82 = NL + "\t\t\t";
  protected final String TEXT_83 = " = null;";
  protected final String TEXT_84 = NL + "    \t\t";
  protected final String TEXT_85 = " = new ";
  protected final String TEXT_86 = "Struct();" + NL + "    \t" + NL + "    \t\torg.dom4j.tree.AbstractNode temp_";
  protected final String TEXT_87 = " = nodeList_";
  protected final String TEXT_88 = ".get(i_";
  protected final String TEXT_89 = ");" + NL + "\t" + NL + "\t    \tnb_line_";
  protected final String TEXT_90 = "++;\t";
  protected final String TEXT_91 = NL + "\t    \tif (nb_line_";
  protected final String TEXT_92 = ">";
  protected final String TEXT_93 = ") {" + NL + "\t    \t\tbreak;" + NL + "\t    \t}";
  protected final String TEXT_94 = NL + "\t\t\ttry{";
  protected final String TEXT_95 = NL + "\t\t\t\torg.dom4j.XPath xTmp";
  protected final String TEXT_96 = "_";
  protected final String TEXT_97 = " = temp_";
  protected final String TEXT_98 = ".createXPath(nsTool_";
  protected final String TEXT_99 = ".addDefaultNSPrefix(";
  protected final String TEXT_100 = ",loopQuery_";
  protected final String TEXT_101 = "));" + NL + "\t\t\t    xTmp";
  protected final String TEXT_102 = "_";
  protected final String TEXT_103 = ".setNamespaceURIs(xmlNameSpaceMap_";
  protected final String TEXT_104 = ");" + NL + "\t\t\t    Object obj";
  protected final String TEXT_105 = "_";
  protected final String TEXT_106 = " = xTmp";
  protected final String TEXT_107 = "_";
  protected final String TEXT_108 = ".evaluate(temp_";
  protected final String TEXT_109 = ");" + NL + "\t\t\t    if(obj";
  protected final String TEXT_110 = "_";
  protected final String TEXT_111 = " instanceof String || obj";
  protected final String TEXT_112 = "_";
  protected final String TEXT_113 = " instanceof Number){" + NL + "    \t\t\t\tnode_";
  protected final String TEXT_114 = " = temp_";
  protected final String TEXT_115 = ";" + NL + "    \t\t\t\tstr_";
  protected final String TEXT_116 = " = String.valueOf(obj";
  protected final String TEXT_117 = "_";
  protected final String TEXT_118 = ");" + NL + "   \t\t\t\t}else{" + NL + "\t\t\t\t    node_";
  protected final String TEXT_119 = " = xTmp";
  protected final String TEXT_120 = "_";
  protected final String TEXT_121 = ".selectSingleNode(temp_";
  protected final String TEXT_122 = ");";
  protected final String TEXT_123 = NL + "\t\t\t\t\tstr_";
  protected final String TEXT_124 = " = node_";
  protected final String TEXT_125 = "==null?null:node_";
  protected final String TEXT_126 = ".asXML();";
  protected final String TEXT_127 = NL + "\t\t\t\t    str_";
  protected final String TEXT_128 = " = xTmp";
  protected final String TEXT_129 = "_";
  protected final String TEXT_130 = ".valueOf(temp_";
  protected final String TEXT_131 = ");";
  protected final String TEXT_132 = NL + "\t\t\t\t}";
  protected final String TEXT_133 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_134 = ".";
  protected final String TEXT_135 = " = str_";
  protected final String TEXT_136 = ";";
  protected final String TEXT_137 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_138 = ".";
  protected final String TEXT_139 = " = ParserUtils.parseTo_Document(str_";
  protected final String TEXT_140 = ");";
  protected final String TEXT_141 = NL + "\t\t\t\t\t\t\t\t\tif(xml_api_";
  protected final String TEXT_142 = ".isDefNull(node_";
  protected final String TEXT_143 = ")){" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_144 = ".";
  protected final String TEXT_145 = " =null;" + NL + "\t\t\t\t\t\t\t\t\t}else if(xml_api_";
  protected final String TEXT_146 = ".isEmpty(node_";
  protected final String TEXT_147 = ")){" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_148 = ".";
  protected final String TEXT_149 = " =\"\";" + NL + "\t\t\t\t\t\t\t\t\t}else if(xml_api_";
  protected final String TEXT_150 = ".isMissing(node_";
  protected final String TEXT_151 = " )){ " + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_152 = ".";
  protected final String TEXT_153 = " =";
  protected final String TEXT_154 = ";" + NL + "\t\t\t\t\t\t\t\t\t}else{";
  protected final String TEXT_155 = NL + "\t\t\t\t\t\t\t\t\tif(xml_api_";
  protected final String TEXT_156 = ".isEmpty(node_";
  protected final String TEXT_157 = ")){" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_158 = ".";
  protected final String TEXT_159 = " =\"\";" + NL + "\t\t\t\t\t\t\t\t\t}else if(xml_api_";
  protected final String TEXT_160 = ".isMissing(node_";
  protected final String TEXT_161 = " )){ " + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_162 = ".";
  protected final String TEXT_163 = " =";
  protected final String TEXT_164 = ";" + NL + "\t\t\t\t\t\t\t\t\t}else{";
  protected final String TEXT_165 = "\t" + NL + "\t\t\t\t\t\t\t\t\t\tif(xml_api_";
  protected final String TEXT_166 = ".isDefNull(node_";
  protected final String TEXT_167 = ")){" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_168 = ".";
  protected final String TEXT_169 = " =null;" + NL + "\t\t\t\t\t\t\t\t\t\t}else if(xml_api_";
  protected final String TEXT_170 = ".isEmpty(node_";
  protected final String TEXT_171 = ") || xml_api_";
  protected final String TEXT_172 = ".isMissing(node_";
  protected final String TEXT_173 = ")){" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_174 = ".";
  protected final String TEXT_175 = "=";
  protected final String TEXT_176 = ";" + NL + "\t\t\t\t\t\t\t\t\t\t}else{";
  protected final String TEXT_177 = NL + "\t\t\t\t\t\t\t\t\t\tif(xml_api_";
  protected final String TEXT_178 = ".isMissing(node_";
  protected final String TEXT_179 = ") || xml_api_";
  protected final String TEXT_180 = ".isEmpty(node_";
  protected final String TEXT_181 = ")){" + NL + "\t\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_182 = ".";
  protected final String TEXT_183 = " =";
  protected final String TEXT_184 = ";" + NL + "\t\t\t\t\t\t\t\t\t\t}else{";
  protected final String TEXT_185 = NL + "\t\t\t\t";
  protected final String TEXT_186 = ".";
  protected final String TEXT_187 = " = str_";
  protected final String TEXT_188 = ";";
  protected final String TEXT_189 = NL + "\t\t\t\t";
  protected final String TEXT_190 = ".";
  protected final String TEXT_191 = " = ParserUtils.parseTo_Date(str_";
  protected final String TEXT_192 = ", ";
  protected final String TEXT_193 = ");";
  protected final String TEXT_194 = NL + "\t\t\t\t";
  protected final String TEXT_195 = ".";
  protected final String TEXT_196 = " = ParserUtils.parseTo_";
  protected final String TEXT_197 = "(str_";
  protected final String TEXT_198 = ");";
  protected final String TEXT_199 = NL + "\t\t\t\t\t\t\t\t\t}";
  protected final String TEXT_200 = NL + "\t\t";
  protected final String TEXT_201 = ".";
  protected final String TEXT_202 = " = ";
  protected final String TEXT_203 = ".";
  protected final String TEXT_204 = ";";
  protected final String TEXT_205 = NL + "\t";
  protected final String TEXT_206 = " = null;";
  protected final String TEXT_207 = NL + "}catch(Exception ex_";
  protected final String TEXT_208 = "){";
  protected final String TEXT_209 = NL + "\tthrow(ex_";
  protected final String TEXT_210 = ");";
  protected final String TEXT_211 = NL + "\t";
  protected final String TEXT_212 = " = new ";
  protected final String TEXT_213 = "Struct();";
  protected final String TEXT_214 = NL + "     ";
  protected final String TEXT_215 = ".";
  protected final String TEXT_216 = " = ";
  protected final String TEXT_217 = ".";
  protected final String TEXT_218 = ";";
  protected final String TEXT_219 = NL + "\t";
  protected final String TEXT_220 = ".errorXMLField = xmlStr_";
  protected final String TEXT_221 = ";" + NL + "\t";
  protected final String TEXT_222 = ".errorMessage = ex_";
  protected final String TEXT_223 = ".getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_224 = ";" + NL + "\t";
  protected final String TEXT_225 = " = null;";
  protected final String TEXT_226 = NL + "    System.err.println(ex_";
  protected final String TEXT_227 = ".getMessage());";
  protected final String TEXT_228 = NL + "    ";
  protected final String TEXT_229 = " = null;";
  protected final String TEXT_230 = NL + "\t";
  protected final String TEXT_231 = ".errorXMLField = xmlStr_";
  protected final String TEXT_232 = ";" + NL + "\t";
  protected final String TEXT_233 = ".errorMessage = ex_";
  protected final String TEXT_234 = ".getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_235 = ";";
  protected final String TEXT_236 = NL + "    \t}" + NL + "    }" + NL + "    " + NL + "    isStructError_";
  protected final String TEXT_237 = " = false;" + NL;
  protected final String TEXT_238 = NL + NL + "   globalMap.put(\"";
  protected final String TEXT_239 = "_NB_LINE\", nb_line_";
  protected final String TEXT_240 = ");";
  protected final String TEXT_241 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();

String cid = node.getUniqueName();
String xmlField = ElementParameterParser.getValue(node, "__XMLFIELD__");

String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;

String strXMLField = ElementParameterParser.getValue(node, "__USE_XML_FIELD__");
boolean useXMLField = (strXMLField!=null&&!("").equals(strXMLField))?("true").equals(strXMLField):false;
String strXMLText = ElementParameterParser.getValue(node, "__XML_TEXT__");
String strXMLPrefix = ElementParameterParser.getValue(node, "__XML_PREFIX__");
if(("TRIGGER_ITEM").equals(strXMLPrefix)){
	strXMLPrefix = "\"/exchange/item\"";
}else if(("PROCESS_ITEM").equals(strXMLPrefix)){
	strXMLPrefix = "\"/item\"";
}else if(("NONE_ITEM").equals(strXMLPrefix)){
	strXMLPrefix = "\"\"";
}

List<Map<String, String>> mapping = (List<Map<String,String>>)ElementParameterParser.getObjectValueXML(node, "__MAPPING__");
String loopQuery = ElementParameterParser.getValue(node, "__LOOP_QUERY__"); 
String limit = ElementParameterParser.getValue(node, "__LIMIT__");
if (("").equals(limit)) {
	limit = "-1";
}

//get XML field content
IConnection inConn = null;
List< ? extends IConnection> inConns = node.getIncomingConnections();
List<IMetadataColumn> inColumns = null;
if(useXMLField){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(strXMLText );
    stringBuffer.append(TEXT_4);
    
}else if (node.getIncomingConnections()!=null) {
	for (IConnection incomingConn : node.getIncomingConnections()) {
		if (incomingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			inConn = incomingConn;
			inConn.getMetadataTable();
			inColumns = inConn.getMetadataTable().getListColumns();
			if(("tMDMReadConf").equals(inConn.getSource().getComponent().getName())){

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_7);
    
			}else{
    			for (IMetadataColumn inputCol : inColumns) {
    				if(inputCol.getLabel().equals(xmlField))
    				{

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(xmlField);
    stringBuffer.append("id_Document".equals(inputCol.getTalendType())?".toString()":"");
    stringBuffer.append(TEXT_11);
    
    					break;
    				}
    			}
    		}
			break;
		}
	}
}

String rejectConnName = "";
List<IMetadataColumn> rejectColumnList = null;
List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
if(rejectConns != null && rejectConns.size() > 0) {
    IConnection rejectConn = rejectConns.get(0);
    rejectColumnList = rejectConn.getMetadataTable().getListColumns();
    rejectConnName = rejectConn.getName(); 
}

IConnection outConn = null;
String firstConnName = "";
List< ? extends IConnection> outConns = node.getOutgoingConnections();
if(outConns!=null){
    for (IConnection conn : outConns) {
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    		outConn = conn;
    		firstConnName = outConn.getName();
    		break;
    	}
    }
}

if(outConns!=null){
    for (IConnection conn : outConns) {
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_13);
    
    	}
    }
}

if (outConn!=null) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    if(useXMLField){
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(strXMLPrefix );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(loopQuery );
    stringBuffer.append(TEXT_26);
    }else{
	//useItems & loopQueryBase are only for tMDMInput
	String strUseItems = ElementParameterParser.getValue(node, "__USE_ITEMS__");
	boolean useItems = (strUseItems!=null&&!("").equals(strUseItems))?("true").equals(strUseItems):false;
	if(useItems){
		String loopQueryBase = ElementParameterParser.getValue(node, "__LOOP_QUERY_BASE__"); 
		
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append("\"\"".equals(loopQueryBase)?"":loopQueryBase+"+");
    stringBuffer.append(loopQuery );
    stringBuffer.append(TEXT_29);
    
	}else{
	
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(loopQuery );
    stringBuffer.append(TEXT_32);
    
	}
}
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    
	if(dieOnError){

    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    
	}else{
		if(!("").equals(rejectConnName) && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_56);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_58);
    
			if(inConn!=null){
		 		for(IMetadataColumn column : outConn.getMetadataTable().getListColumns()) {
	    	  		if(!xmlField.equals(column.getLabel())){
	    	  			for(IMetadataColumn inColumn : inConn.getMetadataTable().getListColumns()){
	    	  				if(inColumn.getLabel().equals(column.getLabel())){

    stringBuffer.append(TEXT_59);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_61);
    stringBuffer.append(inConn.getName() );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_63);
    
								break;
	    					}
	    				}
	    			}
	    	    }
	    	}

    stringBuffer.append(TEXT_64);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_69);
    
		} else if(("").equals(rejectConnName)){

    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    
		}
	}

    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
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
    
if(outConns!=null){
    for (IConnection conn : outConns) {
    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_82);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_83);
    
    	}
    }
}

    stringBuffer.append(TEXT_84);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    
		if (limit.compareTo("-1")!=0) {

    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(Integer.parseInt(limit));
    stringBuffer.append(TEXT_93);
    
		}

    stringBuffer.append(TEXT_94);
    
		//get the mapping column 
		for (int i=0;i<mapping.size();i++) {  //for S_0
			String query = mapping.get(i).get("QUERY");
			String nodeCheck = mapping.get(i).get("NODECHECK");
			if(query!=null && query.trim().length()>0){  // if S_0_0

    stringBuffer.append(TEXT_95);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(query);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    
					if(("true").equals(nodeCheck)){

    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_126);
    
					}else{

    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    
					}

    stringBuffer.append(TEXT_132);
    
				for(IMetadataColumn column:outConn.getMetadataTable().getListColumns()) { // for S_0_0_0
					if (mapping.get(i).get("SCHEMA_COLUMN")!=null) { // if S_0_0_0_0
						if (column.getLabel().compareTo(mapping.get(i).get("SCHEMA_COLUMN"))==0) { //if S_0_0_0_0_0
	        				
							String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
							JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
							String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
							
							boolean isNotSetDefault = false;
							String defaultValue=column.getDefault();
							if(defaultValue!=null){
								isNotSetDefault = defaultValue.length()==0;
							}else{
								isNotSetDefault=true;
							}
							
							if(("true").equals(nodeCheck)){
								if(!("id_Document".equals(column.getTalendType()))) { 

    stringBuffer.append(TEXT_133);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    
								} else {

    stringBuffer.append(TEXT_137);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_140);
    
								}
	            				continue;
	            			}
							if(javaType == JavaTypesManager.STRING){
								if(column.isNullable()){

    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(isNotSetDefault?null:column.getDefault());
    stringBuffer.append(TEXT_154);
    
								}else{ // column.isNullable()

    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_157);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(isNotSetDefault?JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate):column.getDefault());
    stringBuffer.append(TEXT_164);
    
								}
							}else{ // other type
								if(column.isNullable()){

    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(isNotSetDefault?null:column.getDefault());
    stringBuffer.append(TEXT_176);
    
								}else{ // column.isNullable()

    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_181);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(isNotSetDefault?JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate):column.getDefault());
    stringBuffer.append(TEXT_184);
    
								}
							}
									
							if (javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {

    stringBuffer.append(TEXT_185);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_186);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_187);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_188);
    
							} else {
								if (javaType == JavaTypesManager.DATE) {

    stringBuffer.append(TEXT_189);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_191);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_192);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_193);
    
								} else {

    stringBuffer.append(TEXT_194);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_196);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_197);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_198);
    
								}
							}

    stringBuffer.append(TEXT_199);
    
							break;
	        			} // if S_0_0_0_0_1
					} // if S_0_0_0_1
				} // for S_0_0_1
			}else if(inConn!=null){
				for(IMetadataColumn inColumn:inColumns) {
					if (mapping.get(i).get("SCHEMA_COLUMN")!=null) {
						if (inColumn.getLabel().compareTo(mapping.get(i).get("SCHEMA_COLUMN"))==0) {

    stringBuffer.append(TEXT_200);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(inColumn.getLabel());
    stringBuffer.append(TEXT_202);
    stringBuffer.append(inConn.getName() );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(inColumn.getLabel());
    stringBuffer.append(TEXT_204);
    
							break;
						}
					}
				}
			}// if S_0_1
		} // for S_1
		if(!("").equals(rejectConnName) && rejectConnName.equals(firstConnName)){

    stringBuffer.append(TEXT_205);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_206);
    
		}

    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_208);
    
	if(dieOnError){

    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_210);
    
	}else{
		if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_211);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_213);
    
    	 	for(IMetadataColumn column : outConn.getMetadataTable().getListColumns()) {

    stringBuffer.append(TEXT_214);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_215);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_216);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_217);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_218);
    
    	    }

    stringBuffer.append(TEXT_219);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_220);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_221);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_225);
    
		} else if(("").equals(rejectConnName)){

    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_227);
    stringBuffer.append(TEXT_228);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_229);
    
		} else if(rejectConnName.equals(firstConnName)){

    stringBuffer.append(TEXT_230);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_231);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_232);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_234);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_235);
    
		}
	}

    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_237);
    
}

    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_239);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_240);
    stringBuffer.append(TEXT_241);
    return stringBuffer.toString();
  }
}
