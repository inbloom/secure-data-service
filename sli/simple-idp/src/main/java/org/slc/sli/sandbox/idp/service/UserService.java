package org.slc.sli.sandbox.idp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.CollectingAuthenticationErrorCallback;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Component;

/**
 * Returns users that can be logged in as
 * 
 */
@Component
public class UserService {
    
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    LdapTemplate ldapTemplate;
    
    // @Value("${sli.simple-idp.sandboxImpersonationEnabled}")
    private boolean isSandboxImpersonationEnabled = false;
    
    /**
     * Holds user information
     */
    public static class User {
        String userId;
        List<String> roles;
        Map<String, String> attributes;
        
        public User() {
        }
        
        public User(String userId, List<String> roles, Map<String, String> attributes) {
            this.userId = userId;
            this.roles = roles;
            this.attributes = attributes;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public List<String> getRoles() {
            return roles;
        }
        
        public Map<String, String> getAttributes() {
            return attributes;
        }
    }
    
    public User authenticate(String realm, String userId, String password) throws AuthenticationException {
        CollectingAuthenticationErrorCallback errorCallback = new CollectingAuthenticationErrorCallback();
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("uid", userId));
        DistinguishedName dn = new DistinguishedName("ou="+realm);
        boolean result = ldapTemplate.authenticate(dn, filter.toString(), password,
                errorCallback);
        if (!result) {
            Exception error = errorCallback.getError();
            if(error==null){
                LOG.error("SimpleIDP Authentication Eception");
            }else{
                LOG.error("SimpleIDP Authentication Exception", error);
            }
            throw new AuthenticationException(error);
        }
        
        User user = (User) ldapTemplate.searchForObject(dn, filter.toString(), new PersonContextMapper());
        user.userId = userId;
        
        String usersRealm = user.attributes.get("sandbox.realm");
        if (isSandboxImpersonationEnabled && !realm.equals("SLI") && !realm.equals(usersRealm)) {
            throw new AuthenticationException(
                    "Requested authentication realm does not match authenticated users realm.");
        }
        filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "posixGroup")).and(new EqualsFilter("memberuid", userId));
        @SuppressWarnings("unchecked")
        List<String> groups = (List<String>) ldapTemplate.search(dn, filter.toString(),
                new GroupContextMapper());
        user.roles = groups;
        return user;
    }
    
    static class PersonContextMapper implements ContextMapper {
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            User user = new User();
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("userName", context.getStringAttribute("cn"));
            // String raw = context.getStringAttribute("description");
            // if(raw==null || raw.length()==0){
            // throw new IllegalArgumentException("Invalid attribute definition for user");
            // }
            // String[] rows = raw.split("\n");
            // for(String row : rows){
            // String[] pair = row.split("=");
            // attributes.put(pair[0], pair[1]);
            // }
            user.attributes = attributes;
            return user;
        }
    }
    
    static class GroupContextMapper implements ContextMapper {
        @Override
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            String group = context.getStringAttribute("cn");
            return group;
        }
        
    }
}
