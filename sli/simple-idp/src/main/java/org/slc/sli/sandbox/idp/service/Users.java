package org.slc.sli.sandbox.idp.service;

import java.util.ArrayList;
import java.util.List;

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
public class Users {
    
    @Autowired
    LdapTemplate ldapTemplate;
    
    /**
     * Holds user information
     */
    public static class User {
        String userId;
        List<String> roles;
        String name;
        
        User() {
            roles = new ArrayList<String>();
        }
        
        private User(String userId) {
            this.userId = userId;
            roles = new ArrayList<String>();
        }
        
        public String getName() {
            return name;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public List<String> getRoles() {
            return roles;
        }
    }
    
    public User authenticate(String tenant, String userId, String password) throws AuthenticationException {
        CollectingAuthenticationErrorCallback errorCallback = new CollectingAuthenticationErrorCallback();
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("uid", userId));
        boolean result = ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password,
                errorCallback);
        if (!result) {
            Exception error = errorCallback.getError();
            throw new AuthenticationException(error);
        }
        
        User user = (User) ldapTemplate.searchForObject(DistinguishedName.EMPTY_PATH, filter.toString(),
                new PersonContextMapper());
        user.userId = userId;
        
        // group retrieval
        filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "posixGroup")).and(new EqualsFilter("memberuid", userId));
        @SuppressWarnings("unchecked")
        List<String> groups = (List<String>) ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.toString(),
                new GroupContextMapper());
        user.roles = groups;
        return user;
    }
    
    public User getUser(String userId) {
        return new User(userId);
    }
    
    static class PersonContextMapper implements ContextMapper {
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            User user = new User();
            user.name = context.getStringAttribute("cn");
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
