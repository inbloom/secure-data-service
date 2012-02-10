package org.talend.designer.codegen.translators.databases.ldap;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import java.util.List;

public class TLDAPRenameEntryBeginJava
{
  protected static String nl;
  public static synchronized TLDAPRenameEntryBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLDAPRenameEntryBeginJava result = new TLDAPRenameEntryBeginJava();
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
  protected final String TEXT_26 = NL + "\ttalend.ssl.AdvancedSocketFactory.setCertStorePath(";
  protected final String TEXT_27 = ");" + NL + "\t\t  ";
  protected final String TEXT_28 = NL + "\ttalend.ssl.AdvancedSocketFactory.setCertStorePassword(";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL + "\ttalend.ssl.AdvancedSocketFactory.setCertStorePath(";
  protected final String TEXT_31 = ");";
  protected final String TEXT_32 = NL + "    javax.naming.ldap.InitialLdapContext ctx_";
  protected final String TEXT_33 = " = new javax.naming.ldap.InitialLdapContext(env_";
  protected final String TEXT_34 = ", null);";
  protected final String TEXT_35 = NL + "\tjavax.naming.ldap.StartTlsRequest tldsReq_";
  protected final String TEXT_36 = " = new javax.naming.ldap.StartTlsRequest();" + NL + "    javax.naming.ldap.StartTlsResponse tls_";
  protected final String TEXT_37 = " =(javax.naming.ldap.StartTlsResponse)ctx_";
  protected final String TEXT_38 = ".extendedOperation(tldsReq_";
  protected final String TEXT_39 = ");" + NL + "    javax.net.ssl.SSLSession session_";
  protected final String TEXT_40 = " = tls_";
  protected final String TEXT_41 = ".negotiate((javax.net.ssl.SSLSocketFactory)talend.ssl.AdvancedSocketFactory.getDefault());";
  protected final String TEXT_42 = NL + "javax.naming.ldap.InitialLdapContext ctx_";
  protected final String TEXT_43 = " = (javax.naming.ldap.InitialLdapContext)globalMap.get(\"";
  protected final String TEXT_44 = "\");";
  protected final String TEXT_45 = NL + "boolean whetherReject_";
  protected final String TEXT_46 = " = false;" + NL + "class LDAPRenameEntryUtil_";
  protected final String TEXT_47 = " {" + NL + "\t" + NL + "\tprivate java.util.Map<String, javax.naming.directory.Attributes> childs = new java.util.HashMap();" + NL + "\tprivate java.util.List<String> paths = new java.util.ArrayList();" + NL + "\t" + NL + "\tpublic void renameWithChilds(String oldName, String newName, javax.naming.ldap.InitialLdapContext ldapCtx) throws Exception {" + NL + "\t\tthis.list(oldName, ldapCtx);" + NL + "\t\t" + NL + "\t\tthis.destroySubcontext(paths, ldapCtx);" + NL + "\t\t" + NL + "\t\ttry {" + NL + "\t\t\tldapCtx.rename(oldName, newName);" + NL + "\t\t} catch (Exception e) {" + NL + "\t\t\tthis.createSubcontext(paths, paths, childs, ldapCtx);" + NL + "\t\t\t" + NL + "\t\t\tthrow e;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tjava.util.List<String> newPaths = new java.util.ArrayList();" + NL + "\t\tfor (String childName : paths) {" + NL + "\t\t\tnewPaths.add(childName.replaceAll(oldName, newName));" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tthis.createSubcontext(newPaths, paths, childs, ldapCtx);" + NL + "\t}" + NL + "\tpublic void list(String rootName, javax.naming.ldap.InitialLdapContext ldapCtx) throws Exception {" + NL + "\t\tjavax.naming.NamingEnumeration nern =ldapCtx.list(rootName);" + NL + "\t\twhile(nern.hasMore()){" + NL + "\t\t\tjavax.naming.NameClassPair nameCP= (javax.naming.NameClassPair)nern.next();" + NL + "\t\t\tchilds.put(nameCP.getName()+\",\"+rootName, ldapCtx.getAttributes(nameCP.getName()+\",\"+rootName));" + NL + "\t\t\tlist(nameCP.getName()+\",\"+rootName, ldapCtx);" + NL + "\t\t\tpaths.add(nameCP.getName()+\",\"+rootName);" + NL + "\t\t} " + NL + "\t}" + NL + "\t" + NL + "\tpublic void destroySubcontext(java.util.List<String> paths, javax.naming.ldap.InitialLdapContext ldapCtx)throws Exception {" + NL + "\t\tfor (String childName : paths) {" + NL + "\t\t\tldapCtx.destroySubcontext(childName);" + NL + "\t\t}" + NL + "\t}" + NL + "\t\t" + NL + "\tpublic void createSubcontext(java.util.List<String> newPaths, java.util.List<String> oldPaths," + NL + "\t\t\t\t\t\t\t java.util.Map<String, javax.naming.directory.Attributes> childs, " + NL + "\t\t\t\t\t\t\tjavax.naming.ldap.InitialLdapContext ldapCtx)throws Exception {" + NL + "\t\tfor (int i=newPaths.size(); i>0; i-- ) {" + NL + "\t\t\tldapCtx.createSubcontext(newPaths.get(i-1), childs.get(oldPaths.get(i-1)));" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "}" + NL + NL + NL + NL;
  protected final String TEXT_48 = NL;

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
	
if(("false").equals(useExistingConn)){
	String host=ElementParameterParser.getValue(node, "__HOST__");
	String port=ElementParameterParser.getValue(node, "__PORT__");
	String protocol=ElementParameterParser.getValue(node, "__PROTOCOL__");
	String advanced=ElementParameterParser.getValue(node, "__ADVANCEDCA__");
	String storepath=ElementParameterParser.getValue(node, "__STORECA__");
	String storepwd = ElementParameterParser.getValue(node, "__STORECAPWD__");
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
    
		if(("true").equals(advanced)){

    stringBuffer.append(TEXT_26);
    stringBuffer.append(storepath);
    stringBuffer.append(TEXT_27);
    if(storepwd !=null && storepwd.length() !=0 ){
    stringBuffer.append(TEXT_28);
    stringBuffer.append(storepwd );
    stringBuffer.append(TEXT_29);
    
			}
		}
    }else if(("TLS").equals(protocol)){
    	if(("true").equals(advanced)){

    stringBuffer.append(TEXT_30);
    stringBuffer.append(storepath);
    stringBuffer.append(TEXT_31);
    
		}
    }

    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    
	if(("TLS").equals(protocol)){

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
    
	}
}else{

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(exConn);
    stringBuffer.append(TEXT_44);
    
}

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(TEXT_48);
    return stringBuffer.toString();
  }
}
