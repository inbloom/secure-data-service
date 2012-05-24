package org.slc.sli.sandbox.idp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${sli.simple-idp.userSearchAttribute}")
    private String userSearchAttribute;
    
    @Value("${sli.simple-idp.userObjectClass}")
    private String userObjectClass;
    
    @Value("${sli.simple-idp.groupSearchAttribute}")
    private String groupSearchAttribute;
    
    @Value("${sli.simple-idp.groupObjectClass}")
    private String groupObjectClass;
    
    public UserService() {
    }
    
    UserService(String userSearchAttribute, String userObjectClass, String groupSearchAttribute, String groupObjectClass) {
        this.userSearchAttribute = userSearchAttribute;
        this.userObjectClass = userObjectClass;
        this.groupSearchAttribute = groupSearchAttribute;
        this.groupObjectClass = groupObjectClass;
    }
    
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
        
        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
    
    /**
     * Authenticate the user for the given realm and return back a User object with
     * all user attributes and roles loaded. If sandbox mode then it ensures the realm provided
     * matches with the realm stored in the users LDAP description attribute (Realm=xxx)
     * 
     * @param realm
     * @param userId
     * @param password
     * @return
     * @throws AuthenticationException
     */
    public User authenticate(String realm, String userId, String password) throws AuthenticationException {
        CollectingAuthenticationErrorCallback errorCallback = new CollectingAuthenticationErrorCallback();
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", userObjectClass)).and(new EqualsFilter(userSearchAttribute, userId));
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        boolean result = ldapTemplate.authenticate(dn, filter.toString(), password, errorCallback);
        if (!result) {
            Exception error = errorCallback.getError();
            if (error == null) {
                LOG.error("SimpleIDP Authentication Eception");
            } else {
                LOG.error("SimpleIDP Authentication Exception", error);
            }
            throw new AuthenticationException(error);
        }
        
        User user = (User) ldapTemplate.searchForObject(dn, filter.toString(), new PersonContextMapper());
        user.userId = userId;
        
        filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", groupObjectClass)).and(
                new EqualsFilter(groupSearchAttribute, userId));
        @SuppressWarnings("unchecked")
        List<String> groups = ldapTemplate.search(dn, filter.toString(), new GroupContextMapper());
        user.roles = groups;
        return user;
    }
    
    /**
     * LDAPTemplate mapper for getting attributes from the person context. Retrieves cn and
     * description,
     * parsing the value of description by line and then by key=value.
     * 
     * @author scole
     * 
     */
    static class PersonContextMapper implements ContextMapper {
        @Override
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            User user = new User();
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("userName", context.getStringAttribute("cn"));
            String description = context.getStringAttribute("description");
            if (description != null && description.length() > 0) {
                String[] pairs;
                if (description.indexOf("\n") > 0) {
                    pairs = description.split("\n");
                } else if (description.indexOf(",") > 0) {
                    pairs = description.split(",");
                } else {
                    pairs = description.split(" ");
                }
                for (String pair : pairs) {
                    pair = pair.trim();
                    String[] pairArray = pair.split("=", 2);
                    if (pairArray.length == 2) {
                        String value = pairArray[1].trim();
                        if(value.length()>0){
                            attributes.put(pairArray[0].trim(), value);
                        }
                    }
                }
            }
            user.attributes = attributes;
            return user;
        }
    }
    
    /**
     * LDAPTemplate mapper for getting group names.
     * 
     * @author scole
     * 
     */
    static class GroupContextMapper implements ContextMapper {
        @Override
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            String group = context.getStringAttribute("cn");
            return group;
        }
        
    }
}
