package net.wgen.sli.security;

import java.util.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

public class SLIAuthenticationProvider implements AuthenticationProvider {

    //@Resource DataSource dataSource;
    private static Map<String, String> users = new HashMap<String, String>();
     static {
       users.put("admin", "admin");
       users.put("dave","coleman");
       users.put("abdul","itani");
       users.put("bill","hazard");
       users.put("jorge","montoya");
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
         String username = String.valueOf(authentication.getPrincipal());
         String password = String.valueOf(authentication.getCredentials());
         if(users.keySet().contains(username) && users.get(username).equals(password)) {
             // authentication.setAuthenticated(true);
         } else {
             throw new BadCredentialsException("UserName and Password Combination not found.");
         }
         return authentication;
    }
    
    public boolean supports(java.lang.Class<? extends java.lang.Object> authentication) {
        return true;
    }
} 