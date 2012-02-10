package org.talend.designer.codegen.translators.databases.ldap;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TLDAPOutputBeginJava
{
  protected static String nl;
  public static synchronized TLDAPOutputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLDAPOutputBeginJava result = new TLDAPOutputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tjava.util.Hashtable env_";
  protected final String TEXT_3 = " = new java.util.Hashtable();" + NL + "\tenv_";
  protected final String TEXT_4 = ".put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, \"com.sun.jndi.ldap.LdapCtxFactory\");" + NL + "    env_";
  protected final String TEXT_5 = ".put(javax.naming.Context.SECURITY_AUTHENTICATION, \"simple\");// \"none\",\"simple\",\"strong\"" + NL + "    env_";
  protected final String TEXT_6 = ".put(javax.naming.Context.SECURITY_PRINCIPAL, ";
  protected final String TEXT_7 = ");" + NL + "    env_";
  protected final String TEXT_8 = ".put(javax.naming.Context.SECURITY_CREDENTIALS, ";
  protected final String TEXT_9 = ");" + NL + "\tenv_";
  protected final String TEXT_10 = ".put(javax.naming.Context.REFERRAL, \"";
  protected final String TEXT_11 = "\");" + NL + "\tenv_";
  protected final String TEXT_12 = ".put(\"java.naming.ldap.derefAliases\",\"";
  protected final String TEXT_13 = "\");";
  protected final String TEXT_14 = NL + "\tenv_";
  protected final String TEXT_15 = ".put(javax.naming.Context.PROVIDER_URL, \"ldap://\"+";
  protected final String TEXT_16 = "+\":\"+";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL + "\tenv_";
  protected final String TEXT_19 = ".put(javax.naming.Context.PROVIDER_URL, \"ldap://\"+";
  protected final String TEXT_20 = "+\":\"+";
  protected final String TEXT_21 = "+\"/\"+";
  protected final String TEXT_22 = ");";
  protected final String TEXT_23 = NL + "\tenv_";
  protected final String TEXT_24 = ".put(javax.naming.Context.SECURITY_PROTOCOL, \"ssl\");" + NL + "\tenv_";
  protected final String TEXT_25 = ".put(\"java.naming.ldap.factory.socket\", \"talend.ssl.AdvancedSocketFactory\");";
  protected final String TEXT_26 = NL + "\ttalend.ssl.AdvancedSocketFactory.alwaysTrust();";
  protected final String TEXT_27 = NL + "\ttalend.ssl.AdvancedSocketFactory.setCertStorePath(";
  protected final String TEXT_28 = ");";
  protected final String TEXT_29 = NL + "\ttalend.ssl.AdvancedSocketFactory.setCertStorePassword(";
  protected final String TEXT_30 = ");";
  protected final String TEXT_31 = NL + "    javax.naming.ldap.InitialLdapContext ctx_";
  protected final String TEXT_32 = " = new javax.naming.ldap.InitialLdapContext(env_";
  protected final String TEXT_33 = ", null);";
  protected final String TEXT_34 = NL + "\tjavax.naming.ldap.StartTlsRequest tldsReq_";
  protected final String TEXT_35 = " = new javax.naming.ldap.StartTlsRequest();" + NL + "    javax.naming.ldap.StartTlsResponse tls_";
  protected final String TEXT_36 = " =(javax.naming.ldap.StartTlsResponse)ctx_";
  protected final String TEXT_37 = ".extendedOperation(tldsReq_";
  protected final String TEXT_38 = ");" + NL + "    javax.net.ssl.SSLSession session_";
  protected final String TEXT_39 = " = tls_";
  protected final String TEXT_40 = ".negotiate((javax.net.ssl.SSLSocketFactory)talend.ssl.AdvancedSocketFactory.getDefault());";
  protected final String TEXT_41 = NL + "\tjavax.naming.ldap.InitialLdapContext ctx_";
  protected final String TEXT_42 = " = (javax.naming.ldap.InitialLdapContext)globalMap.get(\"";
  protected final String TEXT_43 = "\");";
  protected final String TEXT_44 = NL + "boolean whetherReject_";
  protected final String TEXT_45 = " = false;" + NL + "int nb_line_";
  protected final String TEXT_46 = " = 0;";
  protected final String TEXT_47 = NL + "\t\ttalend.ssl.ldap.LdapAction ldap_";
  protected final String TEXT_48 = " = new talend.ssl.ldap.LdapAction();";
  protected final String TEXT_49 = NL + "\t//check is base64 begin\t" + NL + "\tclass CheckBase64_";
  protected final String TEXT_50 = "{" + NL + "\t\tpublic void checkByteArray(boolean isBase64,String columnName,javax.naming.directory.Attributes entry,byte[] bytearray) throws com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException{" + NL + "\t\t\tif(isBase64){" + NL + "\t\t\t\tentry.put(columnName,com.sun.org.apache.xml.internal.security.utils.Base64.decode(bytearray));" + NL + "\t\t\t}" + NL + "\t\t\telse{" + NL + "\t\t\t\tentry.put(columnName,bytearray);" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void checkString(boolean isBase64,String columnName,javax.naming.directory.Attributes entry,String inputValue) throws com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException{" + NL + "\t\t\tif(isBase64){" + NL + "\t\t\t\tentry.put(columnName,com.sun.org.apache.xml.internal.security.utils.Base64.decode(inputValue));" + NL + "\t\t\t}" + NL + "\t\t\telse{" + NL + "\t\t\t\tentry.put(columnName,inputValue);" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void addToAttribute(boolean isBase64,javax.naming.directory.Attribute attr,String inputValue) throws com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException{" + NL + "\t\t\tif(isBase64){" + NL + "  \t\t\t\tattr.add(com.sun.org.apache.xml.internal.security.utils.Base64.decode(inputValue));" + NL + "\t\t\t}else{" + NL + "\t\t\t\tattr.add(inputValue);" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void checkByteArrayUpdate(boolean isBase64,String columnName,List<javax.naming.directory.Attribute> forUpate,byte[] bytearray) throws com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException{" + NL + "\t\t\tif(isBase64){" + NL + "\t\t\t\tforUpate.add(new javax.naming.directory.BasicAttribute(columnName, com.sun.org.apache.xml.internal.security.utils.Base64.decode(bytearray)));" + NL + "\t\t\t}else{" + NL + "\t\t\t\tforUpate.add(new javax.naming.directory.BasicAttribute(columnName, bytearray));" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\tpublic void checkStringUpdate(boolean isBase64,String columnName,List<javax.naming.directory.Attribute> forUpate,String inputValue) throws com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException{" + NL + "\t\t\tif(isBase64){" + NL + "\t\t\t\tforUpate.add(new javax.naming.directory.BasicAttribute(columnName, com.sun.org.apache.xml.internal.security.utils.Base64.decode(inputValue)));" + NL + "\t\t\t}else{" + NL + "\t\t\t\tforUpate.add(new javax.naming.directory.BasicAttribute(columnName, inputValue));" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t}" + NL + "\tCheckBase64_";
  protected final String TEXT_51 = " base64_";
  protected final String TEXT_52 = " = new CheckBase64_";
  protected final String TEXT_53 = "();" + NL + "\t//check is base64 end";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String baseDN=ElementParameterParser.getValue(node, "__BASEDN__");

	String connection = ElementParameterParser.getValue(node, "__CONNECTION__");
	String exConn= "conn_" + connection;
	String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
	String insertMode =ElementParameterParser.getValue(node, "__INSERT_MODE__");

if(("false").equals(useExistingConn)){
	String host=ElementParameterParser.getValue(node, "__HOST__");
	String port=ElementParameterParser.getValue(node, "__PORT__");
	String protocol=ElementParameterParser.getValue(node, "__PROTOCOL__");
	String advanced=ElementParameterParser.getValue(node, "__ADVANCEDCA__");
	String storepath=ElementParameterParser.getValue(node, "__STORECA__");
	String storepwd = ElementParameterParser.getValue(node, "__STORECAPWD__");
	boolean alwaysTrust = ("true").equals(ElementParameterParser.getValue(node, "__ALWAYS_TRUST__"));
	String user =ElementParameterParser.getValue(node, "__USER__");
	String passwd =ElementParameterParser.getValue(node, "__PASS__");
	String referrals=ElementParameterParser.getValue(node, "__REFERRALS__");
	String aliases=ElementParameterParser.getValue(node, "__ALIASES__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(passwd);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(referrals);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(aliases);
    stringBuffer.append(TEXT_13);
    
	if(baseDN == null || baseDN.length()<=2){

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_17);
    
	}else{

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(baseDN);
    stringBuffer.append(TEXT_22);
    
	}
    if(("LDAPS").equals(protocol)){

    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
	}
	if(("LDAPS").equals(protocol) || ("TLS").equals(protocol)){
		if(("true").equals(advanced)){
			if(alwaysTrust) {

    stringBuffer.append(TEXT_26);
    
			} else {

    stringBuffer.append(TEXT_27);
    stringBuffer.append(storepath);
    stringBuffer.append(TEXT_28);
    
				if(storepwd !=null && storepwd.length() !=0 ){
    stringBuffer.append(TEXT_29);
    stringBuffer.append(storepwd );
    stringBuffer.append(TEXT_30);
    
				}
			}
		}
    }

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    
	if(("TLS").equals(protocol)){

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
    
	}
}else{

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(exConn);
    stringBuffer.append(TEXT_43);
    
}

    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    	if(("DELETE").equals(insertMode)){ 
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    	}
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    return stringBuffer.toString();
  }
}
