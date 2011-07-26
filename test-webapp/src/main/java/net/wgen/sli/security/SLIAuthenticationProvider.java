package net.wgen.sli.security;

import java.util.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationServiceException;

import javax.xml.ws.soap.SOAPFaultException;

import com.sforce.soap.authentication.Authenticate;
import com.sforce.soap.authentication.AuthenticateResult;
import _29._113._72._184.sftest.SimpleAdAuth;
import _29._113._72._184.sftest.SimpleAdAuthSoap;

public class SLIAuthenticationProvider implements AuthenticationProvider {

    private static SimpleAdAuth slitestAuth = new SimpleAdAuth();
    private static SimpleAdAuthSoap slitestAuthSOAP = slitestAuth.getSimpleAdAuthSoap();

    //@Resource DataSource dataSource;
    private static Map<String, String> users = new HashMap<String, String>();
     static {
       users.put("admin", "admin");
       users.put("dave","coleman");
       users.put("abdul","itani");
       users.put("bill","hazard");
       users.put("jorge","montoya");
    }
    
    private Authentication authenticateLocal(Authentication authentication) throws AuthenticationException {
        String username = String.valueOf(authentication.getPrincipal());
        String password = String.valueOf(authentication.getCredentials());
        if(users.keySet().contains(username) && users.get(username).equals(password)) {
            // authentication.setAuthenticated(true);
        } else {
            throw new BadCredentialsException("UserName and Password Combination not found.");
        }
        return authentication;    
    }
    
    // Please note that this is insecure and should not be used for anything except a demonstration
    private Authentication authenticateSLITestDomain(Authentication authentication) throws AuthenticationException {
        Authenticate auth = new Authenticate();
        auth.setUsername(String.valueOf(authentication.getPrincipal()));
        auth.setPassword(String.valueOf(authentication.getCredentials()));
        auth.setSourceIp("1.1.1.1");
        AuthenticateResult result = null;
        
        try {
            result = slitestAuthSOAP.authenticate(auth);
        } catch (SOAPFaultException e) {
            throw new AuthenticationServiceException("Error connecting to SLITest domain (1).");
        } catch (Exception ee) {
            throw new AuthenticationServiceException("Error connecting to SLITest domain (2).");
        }
        
        if(result.isAuthenticated()) {
            // authentication.setAuthenticated(true);
        } else {
            throw new BadCredentialsException("UserName and Password Combination not found in slitest domain.");
        }
        return authentication;    
    } 

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        boolean useWebService = true;
        if(useWebService) {
            return authenticateSLITestDomain(authentication);
        } else {
            return authenticateLocal(authentication);
        }
    }
    
    public boolean supports(java.lang.Class<? extends java.lang.Object> authentication) {
        return true;
    }
} 