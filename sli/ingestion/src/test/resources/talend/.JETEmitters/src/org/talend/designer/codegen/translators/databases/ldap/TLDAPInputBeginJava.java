package org.talend.designer.codegen.translators.databases.ldap;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.EConnectionType;
import java.util.List;
import java.util.Map;

public class TLDAPInputBeginJava
{
  protected static String nl;
  public static synchronized TLDAPInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLDAPInputBeginJava result = new TLDAPInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tString baseDN_";
  protected final String TEXT_3 = " = \"\";" + NL + "\tint ";
  protected final String TEXT_4 = "_NB_LINE = 0;";
  protected final String TEXT_5 = NL + "\tjava.util.Hashtable env_";
  protected final String TEXT_6 = " = new java.util.Hashtable();" + NL + "\tenv_";
  protected final String TEXT_7 = ".put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, \"com.sun.jndi.ldap.LdapCtxFactory\");" + NL + "    env_";
  protected final String TEXT_8 = ".put(javax.naming.Context.SECURITY_AUTHENTICATION, \"simple\");// \"none\",\"simple\",\"strong\"" + NL + "\tenv_";
  protected final String TEXT_9 = ".put(javax.naming.Context.REFERRAL, \"";
  protected final String TEXT_10 = "\");" + NL + "\tenv_";
  protected final String TEXT_11 = ".put(\"java.naming.ldap.derefAliases\",\"";
  protected final String TEXT_12 = "\");";
  protected final String TEXT_13 = NL + "\t\t\t\tenv_";
  protected final String TEXT_14 = ".put(\"java.naming.ldap.attributes.binary\",\"";
  protected final String TEXT_15 = "\");";
  protected final String TEXT_16 = NL + "\tenv_";
  protected final String TEXT_17 = ".put(javax.naming.Context.PROVIDER_URL, \"ldap://\"+";
  protected final String TEXT_18 = "+\":\"+";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + "\tenv_";
  protected final String TEXT_21 = ".put(javax.naming.Context.PROVIDER_URL, \"ldap://\"+";
  protected final String TEXT_22 = "+\":\"+";
  protected final String TEXT_23 = ");";
  protected final String TEXT_24 = NL + "    env_";
  protected final String TEXT_25 = ".put(javax.naming.Context.SECURITY_PRINCIPAL, ";
  protected final String TEXT_26 = ");" + NL + "    env_";
  protected final String TEXT_27 = ".put(javax.naming.Context.SECURITY_CREDENTIALS, ";
  protected final String TEXT_28 = ");";
  protected final String TEXT_29 = NL + "\tenv_";
  protected final String TEXT_30 = ".put(javax.naming.Context.SECURITY_PROTOCOL, \"ssl\");" + NL + "\tenv_";
  protected final String TEXT_31 = ".put(\"java.naming.ldap.factory.socket\", \"talend.ssl.AdvancedSocketFactory\");";
  protected final String TEXT_32 = NL + "\ttalend.ssl.AdvancedSocketFactory.alwaysTrust();";
  protected final String TEXT_33 = NL + "\ttalend.ssl.AdvancedSocketFactory.setCertStorePath(";
  protected final String TEXT_34 = ");";
  protected final String TEXT_35 = NL + "\ttalend.ssl.AdvancedSocketFactory.setCertStorePassword(";
  protected final String TEXT_36 = ");";
  protected final String TEXT_37 = NL + "   " + NL + "   \tjavax.naming.ldap.InitialLdapContext ctx_";
  protected final String TEXT_38 = " = null;" + NL + "\ttry{" + NL + "\tctx_";
  protected final String TEXT_39 = " = new javax.naming.ldap.InitialLdapContext(env_";
  protected final String TEXT_40 = ", null);";
  protected final String TEXT_41 = NL + "\tjavax.naming.ldap.StartTlsRequest tldsReq_";
  protected final String TEXT_42 = " = new javax.naming.ldap.StartTlsRequest();" + NL + "    javax.naming.ldap.StartTlsResponse tls_";
  protected final String TEXT_43 = " =(javax.naming.ldap.StartTlsResponse)ctx_";
  protected final String TEXT_44 = ".extendedOperation(tldsReq_";
  protected final String TEXT_45 = ");" + NL + "    javax.net.ssl.SSLSession session_";
  protected final String TEXT_46 = " = tls_";
  protected final String TEXT_47 = ".negotiate((javax.net.ssl.SSLSocketFactory)talend.ssl.AdvancedSocketFactory.getDefault());";
  protected final String TEXT_48 = NL + "\ttry{" + NL + "\tjavax.naming.ldap.InitialLdapContext ctx_";
  protected final String TEXT_49 = " = (javax.naming.ldap.InitialLdapContext)globalMap.get(\"";
  protected final String TEXT_50 = "\");" + NL + "\tbaseDN_";
  protected final String TEXT_51 = " = (String)globalMap.get(\"";
  protected final String TEXT_52 = "\");";
  protected final String TEXT_53 = NL + "\t" + NL + "\tjavax.naming.directory.SearchControls searchCtls_";
  protected final String TEXT_54 = " = new javax.naming.directory.SearchControls();" + NL + "    searchCtls_";
  protected final String TEXT_55 = ".setSearchScope(javax.naming.directory.SearchControls.SUBTREE_SCOPE);" + NL + "    searchCtls_";
  protected final String TEXT_56 = ".setReturningAttributes(new String[]{";
  protected final String TEXT_57 = NL + "\t\t\"";
  protected final String TEXT_58 = "\",";
  protected final String TEXT_59 = "    " + NL + "    });" + NL + "    searchCtls_";
  protected final String TEXT_60 = ".setTimeLimit(";
  protected final String TEXT_61 = "*1000);" + NL + "    searchCtls_";
  protected final String TEXT_62 = ".setCountLimit(";
  protected final String TEXT_63 = ");";
  protected final String TEXT_64 = NL + "\t//get basce dn" + NL + "\tjavax.naming.directory.Attributes attrsNaming_";
  protected final String TEXT_65 = " = ctx_";
  protected final String TEXT_66 = ".getAttributes(\"\",new String[] {\"namingContexts\"});" + NL + "    javax.naming.directory.Attribute aNaming_";
  protected final String TEXT_67 = " = attrsNaming_";
  protected final String TEXT_68 = ".get(\"namingContexts\");" + NL + "    if(aNaming_";
  protected final String TEXT_69 = " == null)" + NL + "    {" + NL + "        aNaming_";
  protected final String TEXT_70 = " =attrsNaming_";
  protected final String TEXT_71 = ".get(\"namingcontexts\");" + NL + "    }" + NL + "    // String searchBase_";
  protected final String TEXT_72 = " = (aNaming_";
  protected final String TEXT_73 = "==null)|())?\"\":aNaming_";
  protected final String TEXT_74 = ".get().toString();         " + NL + "\tString searchBase_";
  protected final String TEXT_75 = " = ((aNaming_";
  protected final String TEXT_76 = " == null) || ((aNaming_";
  protected final String TEXT_77 = " != null)" + NL + "                    && (aNaming_";
  protected final String TEXT_78 = ".get().toString() != null)" + NL + "                    && (aNaming_";
  protected final String TEXT_79 = ".get().toString().length() > 0) && (Character" + NL + "                    .isIdentifierIgnorable(aNaming_";
  protected final String TEXT_80 = ".get().toString().charAt(0))))) ? \"\"" + NL + "                    : aNaming_";
  protected final String TEXT_81 = ".get().toString();";
  protected final String TEXT_82 = NL + "\t\tString searchBase_";
  protected final String TEXT_83 = " = ";
  protected final String TEXT_84 = ";";
  protected final String TEXT_85 = NL + "\t\t\t\t\tString searchBase_";
  protected final String TEXT_86 = " = \"\";";
  protected final String TEXT_87 = NL + "\t\tbaseDN_";
  protected final String TEXT_88 = " = searchBase_";
  protected final String TEXT_89 = ";";
  protected final String TEXT_90 = NL + "    // Set the page size and initialize the cookie that we pass back in subsequent pages" + NL + "    int pageSize_";
  protected final String TEXT_91 = " = ";
  protected final String TEXT_92 = ";" + NL + "    byte[] cookie_";
  protected final String TEXT_93 = " = null;" + NL + "    // Request the paged results control" + NL + "    javax.naming.ldap.Control[] ctls_";
  protected final String TEXT_94 = " = new javax.naming.ldap.Control[] { new javax.naming.ldap.PagedResultsControl(pageSize_";
  protected final String TEXT_95 = ", true) };" + NL + "    ctx_";
  protected final String TEXT_96 = ".setRequestControls(ctls_";
  protected final String TEXT_97 = ");" + NL + "    " + NL + "do {";
  protected final String TEXT_98 = NL + NL + "    javax.naming.NamingEnumeration answer_";
  protected final String TEXT_99 = " = ctx_";
  protected final String TEXT_100 = ".search(searchBase_";
  protected final String TEXT_101 = ", ";
  protected final String TEXT_102 = ", searchCtls_";
  protected final String TEXT_103 = ");" + NL + "    while (answer_";
  protected final String TEXT_104 = " .hasMoreElements()) {//a";
  protected final String TEXT_105 = NL + "    \t\t";
  protected final String TEXT_106 = " = null;\t\t\t";
  protected final String TEXT_107 = NL + "    \t";
  protected final String TEXT_108 = "_NB_LINE++;" + NL + "    \tjavax.naming.directory.Attributes attrs_";
  protected final String TEXT_109 = " = null;";
  protected final String TEXT_110 = NL + "\t\t";
  protected final String TEXT_111 = " = new ";
  protected final String TEXT_112 = "Struct(); " + NL + "\ttry{";
  protected final String TEXT_113 = NL + NL + "            javax.naming.directory.SearchResult sr_";
  protected final String TEXT_114 = "  = (javax.naming.directory.SearchResult) answer_";
  protected final String TEXT_115 = ".next();" + NL + "    \t\tglobalMap.put(\"";
  protected final String TEXT_116 = "_RESULT_NAME\",sr_";
  protected final String TEXT_117 = ".getName());" + NL + "            attrs_";
  protected final String TEXT_118 = " = sr_";
  protected final String TEXT_119 = ".getAttributes();" + NL + "     \t\t// for output";
  protected final String TEXT_120 = "          " + NL + "        if (attrs_";
  protected final String TEXT_121 = " != null) {//b";
  protected final String TEXT_122 = NL + "\t\t\t\t\t\t\tif(attrs_";
  protected final String TEXT_123 = ".get(\"";
  protected final String TEXT_124 = "\") !=null){" + NL + "                            \t";
  protected final String TEXT_125 = ".";
  protected final String TEXT_126 = " = (byte[])attrs_";
  protected final String TEXT_127 = ".get(\"";
  protected final String TEXT_128 = "\").get();" + NL + "    \t\t\t\t\t\t} else {" + NL + "                           \t\t";
  protected final String TEXT_129 = ".";
  protected final String TEXT_130 = " = null;" + NL + "    \t\t\t\t\t\t}";
  protected final String TEXT_131 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_132 = ".";
  protected final String TEXT_133 = " = (\"\".equals(sr_";
  protected final String TEXT_134 = ".getName())?sr_";
  protected final String TEXT_135 = ".getName():(sr_";
  protected final String TEXT_136 = ".getName()+ \",\")) + baseDN_";
  protected final String TEXT_137 = ";";
  protected final String TEXT_138 = NL + "\t\t\tjavax.naming.directory.Attribute attr_";
  protected final String TEXT_139 = "_";
  protected final String TEXT_140 = " = attrs_";
  protected final String TEXT_141 = ".get(\"";
  protected final String TEXT_142 = "\");" + NL + "\t\t\tif(attr_";
  protected final String TEXT_143 = "_";
  protected final String TEXT_144 = " != null){";
  protected final String TEXT_145 = "\t\t\t" + NL + "\t\t\t\t\tList list_";
  protected final String TEXT_146 = " = new java.util.ArrayList();" + NL + "\t\t\t\t\tfor (javax.naming.NamingEnumeration e_";
  protected final String TEXT_147 = " = attr_";
  protected final String TEXT_148 = "_";
  protected final String TEXT_149 = ".getAll(); e_";
  protected final String TEXT_150 = ".hasMore();){" + NL + "    \t\t\t\t \tlist_";
  protected final String TEXT_151 = ".add(e_";
  protected final String TEXT_152 = ".next());" + NL + "    \t\t\t\t}" + NL + "    \t\t\t\t";
  protected final String TEXT_153 = ".";
  protected final String TEXT_154 = " = list_";
  protected final String TEXT_155 = ";";
  protected final String TEXT_156 = NL + "    \t\t\tStringBuilder attrStr_";
  protected final String TEXT_157 = " = new StringBuilder();" + NL + "    \t\t\tfor (javax.naming.NamingEnumeration e_";
  protected final String TEXT_158 = " = attr_";
  protected final String TEXT_159 = "_";
  protected final String TEXT_160 = ".getAll(); e_";
  protected final String TEXT_161 = ".hasMore();){" + NL + "    \t\t\t\tif(attrStr_";
  protected final String TEXT_162 = ".length()>0){" + NL + "    \t\t\t\t\tattrStr_";
  protected final String TEXT_163 = ".append(";
  protected final String TEXT_164 = ");" + NL + "    \t\t\t\t}" + NL + "    \t\t\t\tattrStr_";
  protected final String TEXT_165 = ".append(e_";
  protected final String TEXT_166 = ".next().toString());" + NL + "    \t\t\t}" + NL + "    \t\t\t";
  protected final String TEXT_167 = ".";
  protected final String TEXT_168 = " = attrStr_";
  protected final String TEXT_169 = ".toString();";
  protected final String TEXT_170 = "\t\t\t" + NL + "\t\t\t} else {" + NL + "\t\t\t\t";
  protected final String TEXT_171 = ".";
  protected final String TEXT_172 = " = null;" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_173 = NL + "\t\t}//b" + NL + "\t} catch (Exception e) {" + NL;
  protected final String TEXT_174 = NL + "        throw(e);";
  protected final String TEXT_175 = "                    ";
  protected final String TEXT_176 = NL + "        ";
  protected final String TEXT_177 = " = new ";
  protected final String TEXT_178 = "Struct();";
  protected final String TEXT_179 = NL + "        ";
  protected final String TEXT_180 = ".";
  protected final String TEXT_181 = " = ";
  protected final String TEXT_182 = ".";
  protected final String TEXT_183 = ";";
  protected final String TEXT_184 = NL + "                ";
  protected final String TEXT_185 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_186 = ";";
  protected final String TEXT_187 = NL + "                ";
  protected final String TEXT_188 = " = null;";
  protected final String TEXT_189 = NL + "                System.err.println(e.getMessage());";
  protected final String TEXT_190 = NL + "                ";
  protected final String TEXT_191 = " = null;";
  protected final String TEXT_192 = NL + "            \t";
  protected final String TEXT_193 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_194 = ";";
  protected final String TEXT_195 = NL + "    }";
  protected final String TEXT_196 = NL + "///////////////////////////////////        ";
  protected final String TEXT_197 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	
	List<Map<String, String>> fieldOptions = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FIELD_OPTIONS__");
	boolean useFieldOptions = ("true").equals(ElementParameterParser.getValue(node, "__USE_FIELD_OPTIONS__"));
	List<IMetadataTable> metadatas = node.getMetadataList();
	    if ((metadatas!=null)&&(metadatas.size()>0)) {
	        IMetadataTable metadata = metadatas.get(0);	        
	        if(metadata!=null) {

	List<IMetadataColumn> columnList = metadata.getListColumns();
	String cid = node.getUniqueName();
	String filter=ElementParameterParser.getValue(node, "__FILTER__");
	String separator=ElementParameterParser.getValue(node, "__MULTI_VALUE_SEPARATOR__");
	boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));	
	boolean paging =("true").equals(ElementParameterParser.getValue(node, "__ISPAGING__"));	
	String pageSize =ElementParameterParser.getValue(node, "__PAGESIZE__");
	String limit =ElementParameterParser.getValue(node, "__LIMIT__");
	String timeout =ElementParameterParser.getValue(node, "__TIMEOUT__");
	String baseDN=ElementParameterParser.getValue(node, "__BASEDN__");
	
	String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
	String exConn= "conn_" + connection;
	String exConnBaseDN = "connBaseDN_" + connection;
	String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
if(("false").equals(useExistingConn)){
	String host=ElementParameterParser.getValue(node, "__HOST__");
	String port=ElementParameterParser.getValue(node, "__PORT__");
	String protocol=ElementParameterParser.getValue(node, "__PROTOCOL__");
	String advanced=ElementParameterParser.getValue(node, "__ADVANCEDCA__");
	String storepath=ElementParameterParser.getValue(node, "__STORECA__");
	String storepwd = ElementParameterParser.getValue(node, "__STORECAPWD__");
	boolean alwaysTrust = ("true").equals(ElementParameterParser.getValue(node, "__ALWAYS_TRUST__"));
	String referrals=ElementParameterParser.getValue(node, "__REFERRALS__");
	String aliases=ElementParameterParser.getValue(node, "__ALIASES__");
	String authen =ElementParameterParser.getValue(node, "__AUTHENTIFICATION__");
	String user =ElementParameterParser.getValue(node, "__USER__");
	String passwd =ElementParameterParser.getValue(node, "__PASS__");

    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(referrals);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(aliases);
    stringBuffer.append(TEXT_12);
    	
	// Types binaires
	if(useFieldOptions){
		boolean isBinary = false;
		for(IMetadataColumn column:columnList){
			for(Map<String, String> line:fieldOptions){// search in the configuration table
				String columnName = line.get("SCHEMA_COLUMN");				
				if(column.getLabel().equals(columnName)){
					isBinary = "true".equals(line.get("BINARY"));
					break;
				}
			}
			if(isBinary){

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_15);
    
			}
		}
	}
	if(baseDN == null || baseDN.length()<=2){

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_19);
    
	}else{

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_23);
    
	}
    if(("true").equals(authen)){

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(passwd);
    stringBuffer.append(TEXT_28);
    
    }
    if(("LDAPS").equals(protocol)){

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    
	}
	if(("LDAPS").equals(protocol) || ("TLS").equals(protocol)){
		if(("true").equals(advanced)){
			if(alwaysTrust) {

    stringBuffer.append(TEXT_32);
    
			} else {

    stringBuffer.append(TEXT_33);
    stringBuffer.append(storepath);
    stringBuffer.append(TEXT_34);
    
				if(storepwd !=null && storepwd.length() !=0 ){
    stringBuffer.append(TEXT_35);
    stringBuffer.append(storepwd );
    stringBuffer.append(TEXT_36);
    
				}
			}
		}
    }

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    
	if(("TLS").equals(protocol)){

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    
	}
}else{

    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(exConn);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(exConnBaseDN);
    stringBuffer.append(TEXT_52);
    
}

    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    
					for(IMetadataColumn column:columnList){		

    stringBuffer.append(TEXT_57);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_58);
    
                	}

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(timeout);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(limit );
    stringBuffer.append(TEXT_63);
    
				if(("false").equals(useExistingConn)){
					if(baseDN == null || baseDN.length()<=2){

    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    
					}else{

    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(baseDN);
    stringBuffer.append(TEXT_84);
    
					}
				}else{

    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    
				}

    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    if(paging){
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(pageSize );
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
    }
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(filter);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    
	List< ? extends IConnection> conns = node.getOutgoingConnections(EConnectionType.FLOW_MAIN);
	if(conns.size() == 0){
		conns = node.getOutgoingConnections(EConnectionType.FLOW_MERGE);
	}
    String rejectConnName = "";
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
    
    String firstConnName = "";
	if (conns!=null) {
        if (conns.size()>0) {
        	for (int i=0;i<conns.size();i++) {
        		IConnection connTemp = conns.get(i);
        		if(i==0){
        			firstConnName = connTemp.getName();
        		}
        		if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_105);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_106);
    
				}
			}
		}
	}

    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    
	if(firstConnName.length()>0){

    stringBuffer.append(TEXT_110);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_112);
    
	}

    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    
            if (conns!=null) {//1
                if (conns.size()>0) {//2
                    IConnection conn = conns.get(0); //the first connection
                    firstConnName = conn.getName();         
                    if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//3
                        
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_121);
    
					for(IMetadataColumn column:columnList){//5		
						JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
						if(javaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_124);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_125);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_128);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_130);
    
						}else{
							if("dn".equals(column.getLabel().toLowerCase())){

    stringBuffer.append(TEXT_131);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_132);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_137);
    
							}else{

    stringBuffer.append(TEXT_138);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(column.getOriginalDbColumnName());
    stringBuffer.append(TEXT_142);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    
								if(javaType == JavaTypesManager.LIST){

    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_148);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    				
								} else {

    stringBuffer.append(TEXT_156);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    
								}

    stringBuffer.append(TEXT_170);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_172);
    
							}
						}
                	}//5

    stringBuffer.append(TEXT_173);
    
        if (dieOnError) {

    stringBuffer.append(TEXT_174);
    
        } else {
            if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_175);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_177);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_178);
    
                for(IMetadataColumn column : metadata.getListColumns()) {

    stringBuffer.append(TEXT_179);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_180);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_181);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_182);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_183);
    
                }

    stringBuffer.append(TEXT_184);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_185);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_188);
    
            } else if(("").equals(rejectConnName)){

    stringBuffer.append(TEXT_189);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_191);
    
            } else if(rejectConnName.equals(firstConnName)){

    stringBuffer.append(TEXT_192);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_193);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_194);
    
			}
        }

    stringBuffer.append(TEXT_195);
    
        }//3
    }//2
    
}//1


    stringBuffer.append(TEXT_196);
    
  }
}  
 
    stringBuffer.append(TEXT_197);
    return stringBuffer.toString();
  }
}
