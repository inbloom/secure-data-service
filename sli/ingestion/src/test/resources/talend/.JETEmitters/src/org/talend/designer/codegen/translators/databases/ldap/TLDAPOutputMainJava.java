package org.talend.designer.codegen.translators.databases.ldap;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TLDAPOutputMainJava
{
  protected static String nl;
  public static synchronized TLDAPOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLDAPOutputMainJava result = new TLDAPOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "        ";
  protected final String TEXT_3 = " = null;            ";
  protected final String TEXT_4 = NL + "\t\t\t\t\tjavax.naming.directory.Attributes entry_";
  protected final String TEXT_5 = " = new javax.naming.directory.BasicAttributes(true);" + NL + "\t\t\t\t\twhetherReject_";
  protected final String TEXT_6 = " = false;" + NL + "\t\t\t\t\tString dn_";
  protected final String TEXT_7 = " = String.valueOf(";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = ").trim();";
  protected final String TEXT_10 = NL + "\t\t\t\t\t\t\tif(";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = " != null){";
  protected final String TEXT_13 = NL + "\t\t\t\t\t\t\t\t\tbase64_";
  protected final String TEXT_14 = ".checkByteArray(";
  protected final String TEXT_15 = ",\"";
  protected final String TEXT_16 = "\",entry_";
  protected final String TEXT_17 = ",";
  protected final String TEXT_18 = ".";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + "\t\t\t\t\t\t\t\t\t\tString incomingColumn_";
  protected final String TEXT_21 = " = FormatterUtils.format_Date(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ", ";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = NL + "        \t\t\t\t\t\t\t\t\tString incomingColumn_";
  protected final String TEXT_26 = " = FormatterUtils.format_Number(";
  protected final String TEXT_27 = ".toPlainString(), ";
  protected final String TEXT_28 = ", ";
  protected final String TEXT_29 = ");\t\t\t\t\t";
  protected final String TEXT_30 = NL + "        \t\t\t\t\t\t\t\t\tString incomingColumn_";
  protected final String TEXT_31 = " = FormatterUtils.format_Number(new java.math.BigDecimal(String.valueOf(";
  protected final String TEXT_32 = ".";
  protected final String TEXT_33 = ")).toPlainString(), ";
  protected final String TEXT_34 = ", ";
  protected final String TEXT_35 = ");\t\t\t\t\t";
  protected final String TEXT_36 = NL + "\t\t\t\t\t\t\t\t\t\tString incomingColumn_";
  protected final String TEXT_37 = " = ";
  protected final String TEXT_38 = ".toPlainString();";
  protected final String TEXT_39 = NL + "\t\t\t\t\t\t\t\t\t\tString incomingColumn_";
  protected final String TEXT_40 = " = String.valueOf(";
  protected final String TEXT_41 = ".";
  protected final String TEXT_42 = ");";
  protected final String TEXT_43 = NL + "\t\t\t\t\t\t\t    \tif(incomingColumn_";
  protected final String TEXT_44 = ".trim().indexOf(";
  protected final String TEXT_45 = ")>0){" + NL + "\t\t\t\t\t\t\t    \t\tjavax.naming.directory.Attribute attr_";
  protected final String TEXT_46 = " = new javax.naming.directory.BasicAttribute(\"";
  protected final String TEXT_47 = "\");" + NL + "\t\t\t\t\t\t\t    \t\tfor(String value_";
  protected final String TEXT_48 = " : incomingColumn_";
  protected final String TEXT_49 = ".trim().split(";
  protected final String TEXT_50 = ")){" + NL + "\t\t\t\t\t\t\t\t\t\t\t\tbase64_";
  protected final String TEXT_51 = ".addToAttribute(";
  protected final String TEXT_52 = ",attr_";
  protected final String TEXT_53 = ",value_";
  protected final String TEXT_54 = ");" + NL + "\t\t\t\t\t\t\t    \t\t}" + NL + "\t\t\t\t\t\t\t    \t\tentry_";
  protected final String TEXT_55 = ".put(attr_";
  protected final String TEXT_56 = ");" + NL + "\t\t\t\t\t\t\t    \t}else{" + NL + "\t\t\t\t\t\t\t\t\t    \tbase64_";
  protected final String TEXT_57 = ".checkString(";
  protected final String TEXT_58 = ",\"";
  protected final String TEXT_59 = "\",entry_";
  protected final String TEXT_60 = ",incomingColumn_";
  protected final String TEXT_61 = ");" + NL + "\t\t\t\t\t\t\t    \t}";
  protected final String TEXT_62 = NL + "\t\t\t\t\t\t    }";
  protected final String TEXT_63 = NL + "\ttry{";
  protected final String TEXT_64 = NL + "\t\t\t\t\t\tctx_";
  protected final String TEXT_65 = ".modifyAttributes(dn_";
  protected final String TEXT_66 = ", javax.naming.directory.DirContext.ADD_ATTRIBUTE, entry_";
  protected final String TEXT_67 = ");" + NL + "\t\t\t\t\t\tnb_line_";
  protected final String TEXT_68 = " ++;";
  protected final String TEXT_69 = " " + NL + "\t\t\t\t\t\tctx_";
  protected final String TEXT_70 = ".createSubcontext(dn_";
  protected final String TEXT_71 = ", entry_";
  protected final String TEXT_72 = ");" + NL + "\t\t\t\t\t\tnb_line_";
  protected final String TEXT_73 = " ++;";
  protected final String TEXT_74 = NL + "\t\t\t\t\t\t\tList<javax.naming.directory.ModificationItem> mfItem_";
  protected final String TEXT_75 = " = new java.util.ArrayList();" + NL + "\t\t\t\t\t\t\tList<javax.naming.directory.Attribute> forUpate";
  protected final String TEXT_76 = " = new java.util.ArrayList();" + NL + "\t\t\t\t\t\t\tString incomingColumn_";
  protected final String TEXT_77 = "  = null;";
  protected final String TEXT_78 = NL + "\t\t\t\t\t\t\t\t\t\t\tbase64_";
  protected final String TEXT_79 = ".checkByteArrayUpdate(";
  protected final String TEXT_80 = ",\"";
  protected final String TEXT_81 = "\",forUpate";
  protected final String TEXT_82 = ",";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = ");";
  protected final String TEXT_85 = "\t\t\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t    \t\tfor(String value_";
  protected final String TEXT_86 = " : ";
  protected final String TEXT_87 = ".";
  protected final String TEXT_88 = ".trim().split(";
  protected final String TEXT_89 = ")){" + NL + "\t\t\t\t\t\t\t\t    \t\t\tbase64_";
  protected final String TEXT_90 = ".checkStringUpdate(";
  protected final String TEXT_91 = ",\"";
  protected final String TEXT_92 = "\",forUpate";
  protected final String TEXT_93 = ",value_";
  protected final String TEXT_94 = ");" + NL + "\t\t\t\t\t\t\t\t    \t\t}";
  protected final String TEXT_95 = NL + "        \t\t\t\t\t\t\t\t\t\t\tincomingColumn_";
  protected final String TEXT_96 = " = FormatterUtils.format_Number(";
  protected final String TEXT_97 = ".toPlainString(), ";
  protected final String TEXT_98 = ", ";
  protected final String TEXT_99 = ");";
  protected final String TEXT_100 = NL + "        \t\t\t\t\t\t\t\t\t\t\tincomingColumn_";
  protected final String TEXT_101 = " = FormatterUtils.format_Number(new java.math.BigDecimal(String.valueOf(";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = ")).toPlainString(), ";
  protected final String TEXT_104 = ", ";
  protected final String TEXT_105 = ");";
  protected final String TEXT_106 = NL + "\t\t\t\t\t\t\t\t\t\t\t\tincomingColumn_";
  protected final String TEXT_107 = " = ";
  protected final String TEXT_108 = ".toPlainString();";
  protected final String TEXT_109 = NL + "\t\t\t\t\t\t\t\t\t\t\t\tincomingColumn_";
  protected final String TEXT_110 = " = FormatterUtils.format_Date(";
  protected final String TEXT_111 = ".";
  protected final String TEXT_112 = ", ";
  protected final String TEXT_113 = ");";
  protected final String TEXT_114 = NL + "\t\t\t\t\t\t\t\t\t\t\t\tincomingColumn_";
  protected final String TEXT_115 = " = String.valueOf(";
  protected final String TEXT_116 = ".";
  protected final String TEXT_117 = ");";
  protected final String TEXT_118 = NL + "\t\t\t\t\t\t\t\t\t\t\t\tbase64_";
  protected final String TEXT_119 = ".checkStringUpdate(";
  protected final String TEXT_120 = ",\"";
  protected final String TEXT_121 = "\",forUpate";
  protected final String TEXT_122 = ",incomingColumn_";
  protected final String TEXT_123 = ");";
  protected final String TEXT_124 = NL + "\t\t\t\t\t\t\t  \t\tforUpate";
  protected final String TEXT_125 = ".add(new javax.naming.directory.BasicAttribute(\"";
  protected final String TEXT_126 = "\"));";
  protected final String TEXT_127 = NL + "\t\t\t\t\t\t\t  \t\tfor (javax.naming.directory.Attribute attrForUpate_";
  protected final String TEXT_128 = " : forUpate";
  protected final String TEXT_129 = ") {" + NL + "\t\t\t\t\t\t\t  \t\t\tmfItem_";
  protected final String TEXT_130 = ".add(new javax.naming.directory.ModificationItem(javax.naming.directory.DirContext.ADD_ATTRIBUTE, attrForUpate_";
  protected final String TEXT_131 = "));" + NL + "\t\t\t\t\t\t\t  \t\t}";
  protected final String TEXT_132 = NL + "\t\t\t\t\t\t\t    \tfor (javax.naming.directory.Attribute attrForUpate_";
  protected final String TEXT_133 = " : forUpate";
  protected final String TEXT_134 = ") {" + NL + "\t\t\t\t\t\t\t  \t\t\tmfItem_";
  protected final String TEXT_135 = ".add(new javax.naming.directory.ModificationItem(javax.naming.directory.DirContext.REPLACE_ATTRIBUTE, attrForUpate_";
  protected final String TEXT_136 = "));" + NL + "\t\t\t\t\t\t\t  \t\t}";
  protected final String TEXT_137 = NL + "\t\t\t\t\t\t\t\t\tfor (javax.naming.directory.Attribute attrForUpate_";
  protected final String TEXT_138 = " : forUpate";
  protected final String TEXT_139 = ") {" + NL + "\t\t\t\t\t\t\t\t\t\tmfItem_";
  protected final String TEXT_140 = ".add(new javax.naming.directory.ModificationItem(javax.naming.directory.DirContext.REMOVE_ATTRIBUTE, attrForUpate_";
  protected final String TEXT_141 = "));" + NL + "\t\t\t\t\t\t\t\t\t}";
  protected final String TEXT_142 = NL + "\t\t\t\t\t\t\t  \t  for (javax.naming.directory.Attribute attrDelete_";
  protected final String TEXT_143 = " : forUpate";
  protected final String TEXT_144 = ") {" + NL + "\t\t\t\t\t\t\t  \t\t\tmfItem_";
  protected final String TEXT_145 = ".add(new javax.naming.directory.ModificationItem(javax.naming.directory.DirContext.REMOVE_ATTRIBUTE, attrDelete_";
  protected final String TEXT_146 = "));" + NL + "\t\t\t\t\t\t\t  \t\t}";
  protected final String TEXT_147 = NL + "\t\t\t\t\t\t\t\tforUpate";
  protected final String TEXT_148 = ".clear();";
  protected final String TEXT_149 = NL + "  \t\t\t\t\t\t\tctx_";
  protected final String TEXT_150 = ".modifyAttributes(dn_";
  protected final String TEXT_151 = ",mfItem_";
  protected final String TEXT_152 = ".toArray(new javax.naming.directory.ModificationItem[0]));" + NL + "  \t\t\t\t\t\t\tnb_line_";
  protected final String TEXT_153 = " ++;";
  protected final String TEXT_154 = NL + "\t\t\t\t\t\t\tctx_";
  protected final String TEXT_155 = ".modifyAttributes(dn_";
  protected final String TEXT_156 = ", javax.naming.directory.DirContext.REPLACE_ATTRIBUTE, entry_";
  protected final String TEXT_157 = ");" + NL + "\t\t\t\t\t\t\tnb_line_";
  protected final String TEXT_158 = " ++;";
  protected final String TEXT_159 = NL + "\t\t\t\t\t\t//ctx_";
  protected final String TEXT_160 = ".destroySubcontext(dn_";
  protected final String TEXT_161 = ");" + NL + "\t\t\t\t\t\tint nb_line = 0;" + NL + "\t\t\t\t\t\tnb_line = ldap_";
  protected final String TEXT_162 = ".delete(dn_";
  protected final String TEXT_163 = ",\"(objectclass=*)\",ctx_";
  protected final String TEXT_164 = ",";
  protected final String TEXT_165 = ");" + NL + "\t\t\t\t\t\tnb_line_";
  protected final String TEXT_166 = " = nb_line_";
  protected final String TEXT_167 = " + nb_line;";
  protected final String TEXT_168 = NL + "\t\t\t\t\t\tboolean found = false;" + NL + "\t\t\t\t\t\ttry{" + NL + "\t\t\t\t\t\t\tctx_";
  protected final String TEXT_169 = ".getAttributes(dn_";
  protected final String TEXT_170 = ");" + NL + "\t\t\t\t\t\t\tfound = true;" + NL + "\t\t\t\t\t\t}catch(javax.naming.NameNotFoundException nfEx_";
  protected final String TEXT_171 = ")" + NL + "\t\t\t\t\t\t{" + NL + "\t\t\t\t\t\t\tctx_";
  protected final String TEXT_172 = ".createSubcontext(dn_";
  protected final String TEXT_173 = ", entry_";
  protected final String TEXT_174 = ");" + NL + "\t\t\t\t\t\t\tnb_line_";
  protected final String TEXT_175 = " ++;" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tif(found)" + NL + "\t\t\t\t\t\t{" + NL + "\t\t\t\t\t\t\tctx_";
  protected final String TEXT_176 = ".modifyAttributes(dn_";
  protected final String TEXT_177 = ", javax.naming.directory.DirContext.REPLACE_ATTRIBUTE, entry_";
  protected final String TEXT_178 = ");" + NL + "\t\t\t\t\t\t\tnb_line_";
  protected final String TEXT_179 = " ++;" + NL + "\t\t\t\t\t\t}";
  protected final String TEXT_180 = NL + "\t}catch(Exception e){" + NL + "        whetherReject_";
  protected final String TEXT_181 = " = true;";
  protected final String TEXT_182 = NL + "        throw(e);";
  protected final String TEXT_183 = NL + "        ";
  protected final String TEXT_184 = " = new ";
  protected final String TEXT_185 = "Struct();";
  protected final String TEXT_186 = NL + "        ";
  protected final String TEXT_187 = ".";
  protected final String TEXT_188 = " = ";
  protected final String TEXT_189 = ".";
  protected final String TEXT_190 = ";";
  protected final String TEXT_191 = NL + "                ";
  protected final String TEXT_192 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_193 = ";";
  protected final String TEXT_194 = NL + "                System.err.print(e.getMessage());";
  protected final String TEXT_195 = NL + "    }";
  protected final String TEXT_196 = NL + "        if(!whetherReject_";
  protected final String TEXT_197 = ") {";
  protected final String TEXT_198 = NL + "\t\t\t";
  protected final String TEXT_199 = " = new ";
  protected final String TEXT_200 = "Struct();";
  protected final String TEXT_201 = NL + "            ";
  protected final String TEXT_202 = ".";
  protected final String TEXT_203 = " = ";
  protected final String TEXT_204 = ".";
  protected final String TEXT_205 = ";";
  protected final String TEXT_206 = NL + "        }";
  protected final String TEXT_207 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	String insertMode =ElementParameterParser.getValue(node, "__INSERT_MODE__");
	String separatorText=ElementParameterParser.getValue(node, "__MULTI_VALUE_SEPARATOR__");
	String dieOnError = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
	String dnColumnName = ElementParameterParser.getValue(node, "__DN_COLUMN_NAME__");
	boolean useAttributeOptions = "true".equals(ElementParameterParser.getValue(node, "__USE_ATTRIBUTE_OPTIONS__"));
	List<Map<String, String>> attributeOptions = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ATTRIBUTE_OPTIONS__");
	List<Map<String, String>> fieldOptions = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FIELD_OPTIONS__");
	boolean useFieldOptions = ("true").equals(ElementParameterParser.getValue(node, "__USE_FIELD_OPTIONS__"));
	boolean isBase64 = false;
	String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
	boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
	String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
	String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
	String baseDN=ElementParameterParser.getValue(node, "__BASEDN__");
	String separator = separatorText;
	if(("\"|\"").equals(separatorText)){
		separator="\"\\\\|\"";
	}
	
	String rejectConnName = null;
	List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
	if(rejectConns != null && rejectConns.size() > 0) {
	    IConnection rejectConn = rejectConns.get(0);
	    rejectConnName = rejectConn.getName();
	}
	List<IMetadataColumn> rejectColumnList = null;
	IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
	if(metadataTable != null) {
	    rejectColumnList = metadataTable.getListColumns();	    
	}
	
	List<? extends IConnection> outgoingConns = node.getOutgoingSortedConnections();
    for(IConnection conn : outgoingConns) {
        if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
          }
    }
    
	String cid = node.getUniqueName();
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		List<IMetadataColumn> columnList = metadata.getListColumns();
		JavaType javaType = null;
		String pattern = null;
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for(IConnection conn:conns){
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.MAIN)) {
     			String firstConnName = conn.getName();
    			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {	

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dnColumnName);
    stringBuffer.append(TEXT_9);
    
					for(IMetadataColumn column:columnList){
						javaType= JavaTypesManager.getJavaTypeFromId(column.getTalendType());
						pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
						if (dnColumnName.equals(column.getLabel().trim())) {
							continue;
						}
						if(!("DELETE").equals(insertMode) && !("UPDATA").equals(insertMode)){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_12);
    
								if(useFieldOptions){
									for(Map<String, String> line:fieldOptions){// search in the configuration table
										String columnName = line.get("SCHEMA_COLUMN");				
										if(column.getLabel().equals(columnName)){
											isBase64 = "true".equals(line.get("BASE64"));
											break;
										}
									}
								}
								if(javaType ==  JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(isBase64);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_19);
    
								}else{
									if(javaType ==  JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0){

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_24);
    
									} else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 
										if(javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_27);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_29);
    
										} else {

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_34);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_35);
    
										}									
									} else if(javaType == JavaTypesManager.BIGDECIMAL) { 

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_38);
    
									}else {

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_41);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_42);
    
									}

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(separatorText);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(isBase64);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(isBase64);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    
								}

    stringBuffer.append(TEXT_62);
    
						}
					}

    stringBuffer.append(TEXT_63);
    
					if(("ADD").equals(insertMode)){

    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    
					} else if(("INSERT").equals(insertMode)){

    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    
					}else if(("UPDATA").equals(insertMode)){
						if(useAttributeOptions && attributeOptions.size() > 0){
						//7631: tLDAPOutput: Add a attributes options parameter 

    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    
						  	for (int i=0; i<attributeOptions.size(); i++) {
						  		Map<String, String> row  = attributeOptions.get(i);
						  		String option = row.get("ATTRIBUTE_OPTION").trim();
						  		String name = row.get("ATTRIBUTE_NAME").trim();
						  		isBase64 = "true".equals(row.get("UPDATEBASE64"));
						  		Integer precision = null;
						  		boolean isNullable = false;
						  		for(IMetadataColumn column : columnList){
						  			if(column.getLabel().equals(name)){
										javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
										pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
										precision = column.getPrecision();
										isNullable = column.isNullable();
						  			}
								}
					  			if (!"REMOVE_ATTRIBUTE".equals(option)) {
					  					if(javaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(isBase64);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_84);
    																  					
										}else if(javaType == JavaTypesManager.STRING){

    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(isBase64);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    
										}else{
											if(advancedSeparator && JavaTypesManager.isNumberType(javaType, isNullable)) { 
												if(javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(precision == null? conn.getName() + "." + name : conn.getName() + "." + name + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_97);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_98);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_99);
    
												} else {

    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(name );
    stringBuffer.append(TEXT_103);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_104);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_105);
    
												}
											}else if(javaType == JavaTypesManager.BIGDECIMAL){

    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(precision == null? conn.getName() + "." + name : conn.getName() + "." + name + ".setScale(" + precision + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_108);
    
											}else if(javaType ==  JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0){

    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(name );
    stringBuffer.append(TEXT_112);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_113);
    
											}else{

    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_116);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_117);
    
											}

    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(isBase64);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    
										}
  								} else {

    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(name);
    stringBuffer.append(TEXT_126);
    
  								}
  								if ("ADD".equals(option)) {

    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_131);
    
  								} else if ("REPLACE".equals(option)) {

    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    
  								} else if ("REMOVE_VALUE".equals(option)){

    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_141);
    
  								} else {

    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    
  								}

    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_148);
    
  							}

    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_153);
    
  	//7631
						}else{

    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    
						}
					}else if(("DELETE").equals(insertMode)){

    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(baseDN);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_167);
    
					}else if(("INSERT_UPDATA").equals(insertMode)){

    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_179);
    
					}

    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_181);
    
        if (("true").equals(dieOnError)) {

    stringBuffer.append(TEXT_182);
    
        } else {
            if(rejectConnName != null && rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_183);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_184);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_185);
    
                for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_186);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_188);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_189);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_190);
    
                }

    stringBuffer.append(TEXT_191);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_192);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_193);
    
            } else {

    stringBuffer.append(TEXT_194);
    
            }
        }

    stringBuffer.append(TEXT_195);
    
    	if(outgoingConns != null && outgoingConns.size() > 0) {

    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_197);
    
            for(IConnection outgoingConn : outgoingConns) {
                if(rejectConnName == null || (rejectConnName != null && !outgoingConn.getName().equals(rejectConnName))) {
                    if(outgoingConn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_198);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_199);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_200);
    
                        for(IMetadataColumn column : columnList) {

    stringBuffer.append(TEXT_201);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_202);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_203);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_205);
     
                        }
                    }
                }
            }

    stringBuffer.append(TEXT_206);
    
    }
				}
			}
		}
	}

    stringBuffer.append(TEXT_207);
    return stringBuffer.toString();
  }
}
