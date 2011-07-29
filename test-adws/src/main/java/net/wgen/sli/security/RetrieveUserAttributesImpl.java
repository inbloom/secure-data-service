package net.wgen.sli.security;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("RetrieveUserAttributes")
public class RetrieveUserAttributesImpl implements RetrieveUserAttributes {

    // ToDo: set these in a property file
    private String m_ldapURL;
    private String m_ldapUser;
    private String m_ldapPassword;
    
    public RetrieveUserAttributesImpl(String ldapURL, String ldapUser, String ldapPassword) {
        m_ldapUser = ldapUser;
        m_ldapURL = ldapURL;
        m_ldapPassword = ldapPassword;
    }

    public Map<String, String> getAttributes(String username) {
        return getUserBasicAttributes(username, getLdapContext());
    }
    
    public String getUserCN(String username) {
        String cn = null;
        Logger log = Logger.getLogger(RetrieveUserAttributes.class.getName());
        try {
          Attribute attr = getUserCNAttribute(username, getLdapContext());
          if (attr != null) { 
            cn = attr.get(0).toString(); //ToDo - assming there is only one attribute - make sure this is true
          } 
        } catch (Exception e) {
              log.info(e.toString());
        }
        return cn;
    }

	private LdapContext getLdapContext(){
		LdapContext ctx = null;
		Logger log = Logger.getLogger(RetrieveUserAttributes.class.getName());
		try{
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "Simple");
			env.put(Context.SECURITY_PRINCIPAL, m_ldapUser);
			env.put(Context.SECURITY_CREDENTIALS, m_ldapPassword);
			env.put(Context.PROVIDER_URL, m_ldapURL);
			ctx = new InitialLdapContext(env, null);
			// log.info("Connection Successful.");
		}catch(NamingException nex){
			System.out.println("LDAP Connection: FAILED");
			log.info(nex.toString());
		}
		return ctx;
	}
	
    private Attribute getUserCNAttribute(String username, LdapContext ctx) {
        Logger log = Logger.getLogger(RetrieveUserAttributes.class.getName());
    	    Map<String, String> attributes = new HashMap<String, String>();
    		try {
    			SearchControls constraints = new SearchControls();
    			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
                String[] attrIDs = { "samAccountName", "employeeID", "givenname", "sn", "cn"}; 
    			constraints.setReturningAttributes(attrIDs);
    			
    			//First input parameter is search bas, it can be "CN=Users,DC=YourDomain,DC=com"
    			//Second Attribute can be uid=username
    			NamingEnumeration answer = ctx.search("DC=wgenhq,DC=net", "sAMAccountName=" + username, constraints);
    			if (answer.hasMore()) {
    				Attributes attrs = ((SearchResult) answer.next()).getAttributes();
    				return attrs.get("cn");
    			}else{
    				throw new Exception("Invalid User");
    			}
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    		return null;
    	}	
	
	private Map getUserBasicAttributes(String username, LdapContext ctx) {
        Logger log = Logger.getLogger(RetrieveUserAttributes.class.getName());
	    Map<String, String> attributes = new HashMap<String, String>();
		try {
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			//String[] attrIDs = { "distinguishedName", "sn", "givenname", "mail", "telephonenumber"};
            String[] attrIDs = { "samAccountName", "employeeID", "givenname", "sn", "cn"}; // "memberOf"
			constraints.setReturningAttributes(attrIDs);
			//First input parameter is search bas, it can be "CN=Users,DC=YourDomain,DC=com"
			//Second Attribute can be uid=username
			NamingEnumeration answer = ctx.search("DC=wgenhq,DC=net", "sAMAccountName=" + username, constraints);
			if (answer.hasMore()) {
				Attributes attrs = ((SearchResult) answer.next()).getAttributes();
//				attributes.put("samAccountName",attrs.get("samAccountName").toString());
//				attributes.put("employeeID",attrs.get("employeeID").toString());
//				attributes.put("givenname",attrs.get("givenname"));
//				attributes.put("sn",attrs.get("sn"));
				attributes.put("cn",attrs.get("cn").toString());
			} else {
				throw new Exception("Invalid User");
			}
		} catch (Exception ex) {
			log.info(ex.toString());
		}
		return attributes;
	}
}