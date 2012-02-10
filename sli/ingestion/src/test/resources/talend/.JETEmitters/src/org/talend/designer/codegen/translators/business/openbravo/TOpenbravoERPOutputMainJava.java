package org.talend.designer.codegen.translators.business.openbravo;

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

public class TOpenbravoERPOutputMainJava
{
  protected static String nl;
  public static synchronized TOpenbravoERPOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOpenbravoERPOutputMainJava result = new TOpenbravoERPOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "nb_line_";
  protected final String TEXT_2 = "++;";
  protected final String TEXT_3 = NL + "final java.net.URL url_";
  protected final String TEXT_4 = " = new java.net.URL(urlString_";
  protected final String TEXT_5 = " + ";
  protected final String TEXT_6 = ".id);" + NL + "final java.net.HttpURLConnection hc_";
  protected final String TEXT_7 = " = (java.net.HttpURLConnection) url_";
  protected final String TEXT_8 = ".openConnection();" + NL + "hc_";
  protected final String TEXT_9 = ".setRequestMethod(method_";
  protected final String TEXT_10 = ");" + NL + "hc_";
  protected final String TEXT_11 = ".setAllowUserInteraction(false);" + NL + "hc_";
  protected final String TEXT_12 = ".setDefaultUseCaches(false);" + NL + "hc_";
  protected final String TEXT_13 = ".setDoOutput(true);" + NL + "hc_";
  protected final String TEXT_14 = ".setDoInput(true);" + NL + "hc_";
  protected final String TEXT_15 = ".setInstanceFollowRedirects(true);" + NL + "hc_";
  protected final String TEXT_16 = ".setUseCaches(false);" + NL + "hc_";
  protected final String TEXT_17 = ".setRequestProperty(\"Content-Type\", \"text/xml\");" + NL + "hc_";
  protected final String TEXT_18 = ".connect();" + NL + "if(hc_";
  protected final String TEXT_19 = ".getResponseCode() == 200){" + NL + "\torg.dom4j.io.SAXReader sr_";
  protected final String TEXT_20 = " = new org.dom4j.io.SAXReader();" + NL + "\tjava.io.InputStream is_";
  protected final String TEXT_21 = " = hc_";
  protected final String TEXT_22 = ".getInputStream();" + NL + "\tif(is_";
  protected final String TEXT_23 = ".available()>0){" + NL + "\t\torg.dom4j.Document doc_";
  protected final String TEXT_24 = " = sr_";
  protected final String TEXT_25 = ".read(is_";
  protected final String TEXT_26 = ");" + NL + "\t\torg.dom4j.io.OutputFormat format_";
  protected final String TEXT_27 = " = org.dom4j.io.OutputFormat.createPrettyPrint();" + NL + "      \tformat_";
  protected final String TEXT_28 = ".setEncoding(\"UTF-8\");" + NL + "      \tformat_";
  protected final String TEXT_29 = ".setTrimText(false);" + NL + "      \tjava.io.StringWriter out_";
  protected final String TEXT_30 = " = new java.io.StringWriter();" + NL + "      \torg.dom4j.io.XMLWriter writer_";
  protected final String TEXT_31 = " = new org.dom4j.io.XMLWriter(out_";
  protected final String TEXT_32 = ", format_";
  protected final String TEXT_33 = ");" + NL + "      \twriter_";
  protected final String TEXT_34 = ".write(doc_";
  protected final String TEXT_35 = ");" + NL + "      \twriter_";
  protected final String TEXT_36 = ".close();" + NL + "       \tString content_";
  protected final String TEXT_37 = " = out_";
  protected final String TEXT_38 = ".toString();" + NL + "       \tif(content_";
  protected final String TEXT_39 = ".indexOf(\"<log>Removed business object\") > 0){" + NL + "       \t\tnb_line_removed_";
  protected final String TEXT_40 = "++;" + NL + "       \t}" + NL + "    \tis_";
  protected final String TEXT_41 = ".close();" + NL + "\t}" + NL + "}else if(hc_";
  protected final String TEXT_42 = ".getResponseCode() == 401){" + NL + "\tnb_line_unauthorized_";
  protected final String TEXT_43 = "++;" + NL + "}else{" + NL + "\tnb_line_failed_";
  protected final String TEXT_44 = "++;" + NL + "}" + NL + "hc_";
  protected final String TEXT_45 = ".disconnect();";
  protected final String TEXT_46 = NL + "//Generate xml content begin." + NL + "sb_";
  protected final String TEXT_47 = ".delete(xmlHeadLength_";
  protected final String TEXT_48 = ", sb_";
  protected final String TEXT_49 = ".length());" + NL + "sb_";
  protected final String TEXT_50 = ".append(\"<";
  protected final String TEXT_51 = "\");" + NL + "if(";
  protected final String TEXT_52 = ".id != null && !";
  protected final String TEXT_53 = ".id.equals(\"\")){" + NL + "\tsb_";
  protected final String TEXT_54 = ".append(\" id=\\\"\").append(";
  protected final String TEXT_55 = ".id).append(\"\\\"\");" + NL + "}" + NL + "if(";
  protected final String TEXT_56 = ".identifier != null && !";
  protected final String TEXT_57 = ".identifier.equals(\"\")){" + NL + "\tsb_";
  protected final String TEXT_58 = ".append(\" identifier=\\\"\").append(";
  protected final String TEXT_59 = ".identifier).append(\"\\\"\");" + NL + "}" + NL + "//attribut \"reference\" are always nullable." + NL + "if(";
  protected final String TEXT_60 = ".reference != null){" + NL + "\tsb_";
  protected final String TEXT_61 = ".append(\" reference=\\\"\").append(";
  protected final String TEXT_62 = ".reference).append(\"\\\"\");" + NL + "}" + NL + "sb_";
  protected final String TEXT_63 = ".append(\">\\n\");";
  protected final String TEXT_64 = NL + "if(";
  protected final String TEXT_65 = ".";
  protected final String TEXT_66 = " == null || ";
  protected final String TEXT_67 = ".";
  protected final String TEXT_68 = ".size()<1){" + NL + "\tsb_";
  protected final String TEXT_69 = ".append(\"<";
  protected final String TEXT_70 = " />\\n\");" + NL + "}else{" + NL + "\t//Generate the full xml contents of the list............" + NL + "\tsb_";
  protected final String TEXT_71 = ".append(\"<";
  protected final String TEXT_72 = ">\\n\");" + NL + "\tfor(Object item_";
  protected final String TEXT_73 = ":";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = "){" + NL + "\t\tString[] values_";
  protected final String TEXT_76 = "_";
  protected final String TEXT_77 = " = ((String)item_";
  protected final String TEXT_78 = ").split(\";\", 3);" + NL + "\t\tsb_";
  protected final String TEXT_79 = ".append(\"<\").append(values_";
  protected final String TEXT_80 = "_";
  protected final String TEXT_81 = "[1]).append(\" id=\\\"\").append(values_";
  protected final String TEXT_82 = "_";
  protected final String TEXT_83 = "[0]).append(\"\\\" identifier=\\\"\").append(values_";
  protected final String TEXT_84 = "_";
  protected final String TEXT_85 = "[2]).append(\"\\\">\\n\");" + NL + "\t\tsb_";
  protected final String TEXT_86 = ".append(\"<id>\").append(values_";
  protected final String TEXT_87 = "_";
  protected final String TEXT_88 = "[0]).append(\"</id>\\n\");" + NL + "\t\tsb_";
  protected final String TEXT_89 = ".append(\"</\").append(values_";
  protected final String TEXT_90 = "_";
  protected final String TEXT_91 = "[1]).append(\">\\n\"); " + NL + "\t}" + NL + "\tsb_";
  protected final String TEXT_92 = ".append(\"</";
  protected final String TEXT_93 = ">\\n\");" + NL + "}";
  protected final String TEXT_94 = NL + "if(";
  protected final String TEXT_95 = ".";
  protected final String TEXT_96 = " == null){" + NL + "\tsb_";
  protected final String TEXT_97 = ".append(\"<";
  protected final String TEXT_98 = " />\\n\");" + NL + "}else{";
  protected final String TEXT_99 = NL + "String[] valueArray_";
  protected final String TEXT_100 = "_";
  protected final String TEXT_101 = " = ((String)(";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = ")).split(\";\", 5);" + NL + "sb_";
  protected final String TEXT_104 = ".append(\"<";
  protected final String TEXT_105 = "\");" + NL + "for(int i=0; i<valueArray_";
  protected final String TEXT_106 = "_";
  protected final String TEXT_107 = ".length;i++){" + NL + "\tif(\"\".equals(valueArray_";
  protected final String TEXT_108 = "_";
  protected final String TEXT_109 = "[i])){" + NL + "\t\tcontinue;" + NL + "\t}" + NL + "\tsb_";
  protected final String TEXT_110 = ".append(\" \").append(referenceAttributes_";
  protected final String TEXT_111 = "[i]).append(\"=\\\"\").append(valueArray_";
  protected final String TEXT_112 = "_";
  protected final String TEXT_113 = "[i]).append(\"\\\"\");" + NL + "}" + NL + "sb_";
  protected final String TEXT_114 = ".append(\" />\\n\");";
  protected final String TEXT_115 = NL + "}";
  protected final String TEXT_116 = NL + "if(";
  protected final String TEXT_117 = ".";
  protected final String TEXT_118 = " == null){" + NL + "\tsb_";
  protected final String TEXT_119 = ".append(\"<";
  protected final String TEXT_120 = " xsi:nil=\\\"true\\\" />\\n\");" + NL + "}else{";
  protected final String TEXT_121 = NL + "String dateString_";
  protected final String TEXT_122 = "_";
  protected final String TEXT_123 = " = FormatterUtils.format_Date(";
  protected final String TEXT_124 = ".";
  protected final String TEXT_125 = ", \"yyyy-MM-dd hh:mm:ss.SSS\");" + NL + "dateString_";
  protected final String TEXT_126 = "_";
  protected final String TEXT_127 = " = dateString_";
  protected final String TEXT_128 = "_";
  protected final String TEXT_129 = ".replace(' ', 'T');" + NL + "//dateString_";
  protected final String TEXT_130 = "_";
  protected final String TEXT_131 = " = dateString_";
  protected final String TEXT_132 = "_";
  protected final String TEXT_133 = ".replace('#', 'Z');" + NL + "//(It seems)The attribute value of transient is always \"true\" for datetime." + NL + "sb_";
  protected final String TEXT_134 = ".append(\"<";
  protected final String TEXT_135 = " transient=\\\"true\\\">\");" + NL + "sb_";
  protected final String TEXT_136 = ".append(dateString_";
  protected final String TEXT_137 = "_";
  protected final String TEXT_138 = ");" + NL + "sb_";
  protected final String TEXT_139 = ".append(\"Z</";
  protected final String TEXT_140 = ">\\n\");";
  protected final String TEXT_141 = NL + "}";
  protected final String TEXT_142 = NL + "if(";
  protected final String TEXT_143 = ".";
  protected final String TEXT_144 = " == null){" + NL + "\tsb_";
  protected final String TEXT_145 = ".append(\"<";
  protected final String TEXT_146 = " xsi:nil=\\\"true\\\" />\\n\");" + NL + "}else{";
  protected final String TEXT_147 = NL + "sb_";
  protected final String TEXT_148 = ".append(\"<";
  protected final String TEXT_149 = ">\");" + NL + "sb_";
  protected final String TEXT_150 = ".append(";
  protected final String TEXT_151 = ".";
  protected final String TEXT_152 = ");" + NL + "sb_";
  protected final String TEXT_153 = ".append(\"</";
  protected final String TEXT_154 = ">\\n\");";
  protected final String TEXT_155 = NL + "}";
  protected final String TEXT_156 = NL + "sb_";
  protected final String TEXT_157 = ".append(\"</";
  protected final String TEXT_158 = ">\\n\");" + NL + "sb_";
  protected final String TEXT_159 = ".append(\"</ob:Openbravo>\\n\");" + NL + "//Generate xml content end." + NL + "final java.net.URL url_";
  protected final String TEXT_160 = " = new java.net.URL(urlString_";
  protected final String TEXT_161 = ");" + NL + "final java.net.HttpURLConnection hc_";
  protected final String TEXT_162 = " = (java.net.HttpURLConnection) url_";
  protected final String TEXT_163 = ".openConnection();" + NL + "hc_";
  protected final String TEXT_164 = ".setRequestMethod(method_";
  protected final String TEXT_165 = ");" + NL + "hc_";
  protected final String TEXT_166 = ".setAllowUserInteraction(false);" + NL + "hc_";
  protected final String TEXT_167 = ".setDefaultUseCaches(false);" + NL + "hc_";
  protected final String TEXT_168 = ".setDoOutput(true);" + NL + "hc_";
  protected final String TEXT_169 = ".setDoInput(true);" + NL + "hc_";
  protected final String TEXT_170 = ".setInstanceFollowRedirects(true);" + NL + "hc_";
  protected final String TEXT_171 = ".setUseCaches(false);" + NL + "hc_";
  protected final String TEXT_172 = ".setRequestProperty(\"Content-Type\", \"text/xml\");" + NL + "java.io.OutputStream os_";
  protected final String TEXT_173 = " = hc_";
  protected final String TEXT_174 = ".getOutputStream();" + NL + "os_";
  protected final String TEXT_175 = ".write(sb_";
  protected final String TEXT_176 = ".toString().getBytes(\"UTF-8\"));" + NL + "//System.out.println(sb_";
  protected final String TEXT_177 = ".toString());" + NL + "os_";
  protected final String TEXT_178 = ".flush();" + NL + "os_";
  protected final String TEXT_179 = ".close();" + NL + "hc_";
  protected final String TEXT_180 = ".connect();" + NL + "if(hc_";
  protected final String TEXT_181 = ".getResponseCode() == 200){" + NL + "\torg.dom4j.io.SAXReader sr_";
  protected final String TEXT_182 = " = new org.dom4j.io.SAXReader();" + NL + "\tjava.io.InputStream is_";
  protected final String TEXT_183 = " = hc_";
  protected final String TEXT_184 = ".getInputStream();" + NL + "\tif(is_";
  protected final String TEXT_185 = ".available()>0){" + NL + "\t\torg.dom4j.Document doc_";
  protected final String TEXT_186 = " = sr_";
  protected final String TEXT_187 = ".read(is_";
  protected final String TEXT_188 = ");" + NL + "\t\torg.dom4j.io.OutputFormat format_";
  protected final String TEXT_189 = " = org.dom4j.io.OutputFormat.createPrettyPrint();" + NL + "      \tformat_";
  protected final String TEXT_190 = ".setEncoding(\"UTF-8\");" + NL + "      \tformat_";
  protected final String TEXT_191 = ".setTrimText(false);" + NL + "      \tjava.io.StringWriter out_";
  protected final String TEXT_192 = " = new java.io.StringWriter();" + NL + "      \torg.dom4j.io.XMLWriter writer_";
  protected final String TEXT_193 = " = new org.dom4j.io.XMLWriter(out_";
  protected final String TEXT_194 = ", format_";
  protected final String TEXT_195 = ");" + NL + "      \twriter_";
  protected final String TEXT_196 = ".write(doc_";
  protected final String TEXT_197 = ");" + NL + "      \twriter_";
  protected final String TEXT_198 = ".close();" + NL + "       \tString content_";
  protected final String TEXT_199 = " = out_";
  protected final String TEXT_200 = ".toString();" + NL + "       \tif(content_";
  protected final String TEXT_201 = ".indexOf(\"Updated 1\") > 0){" + NL + "       \t\tnb_line_updated_";
  protected final String TEXT_202 = "++;" + NL + "       \t}" + NL + "       \tif(content_";
  protected final String TEXT_203 = ".indexOf(\"Inserted 1\") > 0){" + NL + "       \t\tnb_line_created_";
  protected final String TEXT_204 = "++;" + NL + "       \t}" + NL + "    \tis_";
  protected final String TEXT_205 = ".close();" + NL + "\t}" + NL + "}else if(hc_";
  protected final String TEXT_206 = ".getResponseCode() == 401){" + NL + "\tnb_line_unauthorized_";
  protected final String TEXT_207 = "++;" + NL + "}else{" + NL + "\tnb_line_failed_";
  protected final String TEXT_208 = "++;" + NL + "}" + NL + "hc_";
  protected final String TEXT_209 = ".disconnect();";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
    
	//String endpoint = ElementParameterParser.getValue(node, "__ENDPOINT__");
	String entityName = ElementParameterParser.getValue(node, "__ENTITYNAME__");
	//String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
	String usingExistingFile = ElementParameterParser.getValue(node, "__USE_EXISTING_FILE__");
	//String username = ElementParameterParser.getValue(node, "__USER__");
	//String password = ElementParameterParser.getValue(node, "__PASS__");

	String action = ElementParameterParser.getValue(node, "__ACTION__");
String conName = "";
List<IMetadataColumn> columns = null;
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
	IMetadataTable metadata = metadatas.get(0);
	if (metadata!=null) {	
		columns=metadata.getListColumns();
		List< ? extends IConnection> conns = node.getIncomingConnections();
		if(conns!=null && conns.size()>0){
			IConnection conn = conns.get(0);
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { // test for connection type
				conName = conn.getName();
			}
		}
	}
}

if(conName != null && !"".equals(conName) && columns != null){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
	if(action.equals("REMOVE")){

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    
	}else if(action.equals("UPDATE_CREATE")){//if(action.equals("REMOVE")){

    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(entityName );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    
		for (int i=0;i<columns.size();i++) { // loop for columns
			IMetadataColumn column = columns.get(i);
			String columnName = column.getLabel();
			if(columnName.equals("identifier") || columnName.equals("reference")){
				continue;
			}
			//String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			//String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
			///boolean isNotSetDefault = false;
			//String defaultValue=column.getDefault();
			//if(defaultValue!=null){
			//	isNotSetDefault = (defaultValue.length()==0);
			//}else{
			//	isNotSetDefault=true;
			//}
			String openbravoName = columnName;
			if(columnName.equals("Default")){
				openbravoName = "default";
			}
			if(columnName.equals("Transient")){
				openbravoName = "transient";
			}
			//boolean attribute = false;
			//if(openbravoName.equals("identifier") || openbravoName.equals("reference")){
			//	attribute = true;
			//}
			//list
			if(javaType == JavaTypesManager.LIST){

    stringBuffer.append(TEXT_64);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_93);
    
			//Reference Type
			}else if(javaType == JavaTypesManager.OBJECT){
				if(column.isNullable()){

    stringBuffer.append(TEXT_94);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_98);
    
				}

    stringBuffer.append(TEXT_99);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    
				if(column.isNullable()){

    stringBuffer.append(TEXT_115);
    
				}
			//Datetime Type: openbravo datatime pattern is: yyyy-MM-ddThh:mm:ss.SSSZ. But this id an invalid pattern in java.
			}else if(javaType == JavaTypesManager.DATE){
				if(column.isNullable()){

    stringBuffer.append(TEXT_116);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_120);
    
				}

    stringBuffer.append(TEXT_121);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_134);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_140);
    
				if(column.isNullable()){

    stringBuffer.append(TEXT_141);
    
				}
			//for other types....
			}else{
				if(column.isNullable()){

    stringBuffer.append(TEXT_142);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_146);
    
				}

    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(conName );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(columnName );
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(openbravoName );
    stringBuffer.append(TEXT_154);
    
				if(column.isNullable()){

    stringBuffer.append(TEXT_155);
    
				}
			}
		}//loop for columns end;

    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_157);
    stringBuffer.append(entityName );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_192);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_195);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_198);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_209);
    
	}//end if(action.equals("REMOVE")){
}	

    return stringBuffer.toString();
  }
}
