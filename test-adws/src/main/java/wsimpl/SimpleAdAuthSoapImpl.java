package net.wgenhq.dcoleman_wks3._8443.sftest;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.*;
import javax.naming.directory.*;

import java.util.logging.Logger;
import java.util.Hashtable;

import com.sforce.soap.authentication.AuthenticateResult;
import net.wgen.sli.security.*;
import net.wgen.sli.util.SpringApplicationContext;

 
@WebService(targetNamespace = "http://dcoleman-wks3.wgenhq.net:8443/sftest/", name = "SimpleAdAuthSoap")
@XmlSeeAlso({com.sforce.soap.authentication.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE) 
public class SimpleAdAuthSoapImpl implements SimpleAdAuthSoap {

    private static String AD_URL = "ldap://wgdc07.wgenhq.net:389";
    private static String AD_CONTEXT_STRING = "OU=FullTime,OU=Users,OU=Corporate,DC=wgenhq,DC=net";
    private RetrieveUserAttributes m_rua;

    @WebResult(name = "AuthenticateResult", targetNamespace = "urn:authentication.soap.sforce.com", partName = "parameters")
    @WebMethod(operationName = "Authenticate")
    @Override
    public AuthenticateResult authenticate(
        @WebParam(partName = "parameters", name = "Authenticate", targetNamespace = "urn:authentication.soap.sforce.com")
        com.sforce.soap.authentication.Authenticate parameters
    ) {
        Logger log = Logger.getLogger(SimpleAdAuthSoapImpl.class.getName());
        log.info("starting SimpleAdAuthSoapImpl");
        AuthenticateResult result = new AuthenticateResult();
        result.setAuthenticated(false);
        if(authenticateLDAP(parameters.getUsername(), parameters.getPassword())) {
            result.setAuthenticated(true);
        }
        log.info("done SimpleAdAuthSoapImpl");
        return result;
    }
    
    // @Autowired ToDo - figure out how to get Spring to inject this or something
    public void setRetrieveUserAttributes(RetrieveUserAttributes rua) {
        m_rua = rua;
    }
    
    //ToDo - figure out how to get Spring to inject this or something
    private RetrieveUserAttributes getRetrieveUserAttributes() {
        if(m_rua == null) {
            m_rua = (RetrieveUserAttributes) SpringApplicationContext.getBean("retrieveUserAttributes");
        }
        return m_rua;
    }
    
    private boolean authenticateLDAP(String username, String password) {
            boolean authenticated = false;
            Logger log = Logger.getLogger(SimpleAdAuthSoapImpl.class.getName());
 
            String cn = getRetrieveUserAttributes().getUserCN(username.trim());
            log.info("cn = " + cn);
            if(cn != null) {
            
            // ToDo: security checks for username and password
        	Hashtable authEnv = new Hashtable(11);
        	String dn = "cn=" + cn.trim() + "," + AD_CONTEXT_STRING;
        	log.info(dn);
      
        	authEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
       		authEnv.put(Context.PROVIDER_URL, AD_URL);
       		authEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
       		authEnv.put(Context.SECURITY_PRINCIPAL, dn);
       		authEnv.put(Context.SECURITY_CREDENTIALS, password);

        	try {
        		DirContext authContext = new InitialDirContext(authEnv);
        		authenticated = true;
        	} catch (AuthenticationException authEx) {
        	    authenticated = false;
        		log.info("Authentication failed!");
        	} catch (Exception namEx) {
        	    authenticated = false;
        		log.info("Something went wrong! " + namEx.toString());
        	} 
    	}
        	return authenticated;
    }
}