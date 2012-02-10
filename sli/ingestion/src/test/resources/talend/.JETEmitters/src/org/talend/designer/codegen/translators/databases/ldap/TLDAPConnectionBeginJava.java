package org.talend.designer.codegen.translators.databases.ldap;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.EConnectionType;
import java.util.List;

public class TLDAPConnectionBeginJava
{
  protected static String nl;
  public static synchronized TLDAPConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLDAPConnectionBeginJava result = new TLDAPConnectionBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.util.Hashtable env_";
  protected final String TEXT_3 = " = new java.util.Hashtable();" + NL + "env_";
  protected final String TEXT_4 = ".put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, \"com.sun.jndi.ldap.LdapCtxFactory\");" + NL + "env_";
  protected final String TEXT_5 = ".put(javax.naming.Context.SECURITY_AUTHENTICATION, \"simple\");// \"none\",\"simple\",\"strong\"";
  protected final String TEXT_6 = NL + "env_";
  protected final String TEXT_7 = ".put(javax.naming.Context.SECURITY_PRINCIPAL, ";
  protected final String TEXT_8 = ");" + NL + "env_";
  protected final String TEXT_9 = ".put(javax.naming.Context.SECURITY_CREDENTIALS, ";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "env_";
  protected final String TEXT_12 = ".put(javax.naming.Context.REFERRAL, \"";
  protected final String TEXT_13 = "\");" + NL + "env_";
  protected final String TEXT_14 = ".put(\"java.naming.ldap.derefAliases\",\"";
  protected final String TEXT_15 = "\");" + NL + "env_";
  protected final String TEXT_16 = ".put(javax.naming.Context.PROVIDER_URL, \"ldap://\"+";
  protected final String TEXT_17 = "+\":\"+";
  protected final String TEXT_18 = "+\"/\"+";
  protected final String TEXT_19 = ");" + NL + "env_";
  protected final String TEXT_20 = ".put(\"com.sun.jndi.ldap.connect.pool\", \"true\");";
  protected final String TEXT_21 = NL + "\tenv_";
  protected final String TEXT_22 = ".put(javax.naming.Context.SECURITY_PROTOCOL, \"ssl\");" + NL + "\tenv_";
  protected final String TEXT_23 = ".put(\"java.naming.ldap.factory.socket\", \"talend.ssl.AdvancedSocketFactory\");";
  protected final String TEXT_24 = NL + "\t\t\ttalend.ssl.AdvancedSocketFactory.alwaysTrust();";
  protected final String TEXT_25 = NL + "\t\t\ttalend.ssl.AdvancedSocketFactory.setCertStorePath(";
  protected final String TEXT_26 = ");";
  protected final String TEXT_27 = NL + "\t\t\t\ttalend.ssl.AdvancedSocketFactory.setCertStorePassword(";
  protected final String TEXT_28 = ");";
  protected final String TEXT_29 = NL + "javax.naming.ldap.InitialLdapContext ctx_";
  protected final String TEXT_30 = " = new javax.naming.ldap.InitialLdapContext(env_";
  protected final String TEXT_31 = ", null);" + NL + "globalMap.put(\"conn_";
  protected final String TEXT_32 = "\",ctx_";
  protected final String TEXT_33 = ");" + NL + "globalMap.put(\"connBaseDN_";
  protected final String TEXT_34 = "\",";
  protected final String TEXT_35 = ");";
  protected final String TEXT_36 = NL + "\tjavax.naming.ldap.StartTlsRequest tldsReq_";
  protected final String TEXT_37 = " = new javax.naming.ldap.StartTlsRequest();" + NL + "    javax.naming.ldap.StartTlsResponse tls_";
  protected final String TEXT_38 = " =(javax.naming.ldap.StartTlsResponse)ctx_";
  protected final String TEXT_39 = ".extendedOperation(tldsReq_";
  protected final String TEXT_40 = ");" + NL + "    javax.net.ssl.SSLSession session_";
  protected final String TEXT_41 = " = tls_";
  protected final String TEXT_42 = ".negotiate((javax.net.ssl.SSLSocketFactory)talend.ssl.AdvancedSocketFactory.getDefault());";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String host=ElementParameterParser.getValue(node, "__HOST__");
String port=ElementParameterParser.getValue(node, "__PORT__");
String protocol=ElementParameterParser.getValue(node, "__PROTOCOL__");
String advanced=ElementParameterParser.getValue(node, "__ADVANCEDCA__");
String storepath=ElementParameterParser.getValue(node, "__STORECA__");
String storepwd = ElementParameterParser.getValue(node, "__STORECAPWD__");
boolean useAuth = ("true").equals(ElementParameterParser.getValue(node, "__AUTHENTIFICATION__"));
boolean alwaysTrust = ("true").equals(ElementParameterParser.getValue(node, "__ALWAYS_TRUST__"));
String user =ElementParameterParser.getValue(node, "__USER__");
String passwd =ElementParameterParser.getValue(node, "__PASS__");
String referrals=ElementParameterParser.getValue(node, "__REFERRALS__");
String aliases=ElementParameterParser.getValue(node, "__ALIASES__");
String baseDN=ElementParameterParser.getValue(node, "__BASEDN__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    if(useAuth){
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(user);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(passwd);
    stringBuffer.append(TEXT_10);
    }
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(referrals);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(aliases);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(baseDN);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    
if(("LDAPS").equals(protocol)){

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
}
if(("LDAPS").equals(protocol) || ("TLS").equals(protocol)){
	if(("true").equals(advanced)){
		if(alwaysTrust) {

    stringBuffer.append(TEXT_24);
    
		} else {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(storepath);
    stringBuffer.append(TEXT_26);
    
			if(storepwd !=null && storepwd.length() !=0 ){
    stringBuffer.append(TEXT_27);
    stringBuffer.append(storepwd );
    stringBuffer.append(TEXT_28);
    
			}
		}
	}
}

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
    stringBuffer.append(baseDN);
    stringBuffer.append(TEXT_35);
    
if(("TLS").equals(protocol)){

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
    
}

    return stringBuffer.toString();
  }
}
