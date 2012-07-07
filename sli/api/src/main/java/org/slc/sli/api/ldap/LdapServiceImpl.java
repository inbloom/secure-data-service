package org.slc.sli.api.ldap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Component;

/**
 * implementation of LdapService interface for basic CRUD and search operations on LDAP directory
 * 
 * @author dliu
 * 
 */

@Component
public class LdapServiceImpl implements LdapService {
    
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
    
    public LdapServiceImpl() {
    }

    @Override
    public User getUser(String realm, String uid) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", userObjectClass)).and(new EqualsFilter(userSearchAttribute, uid));
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        User user = (User) ldapTemplate.searchForObject(dn, filter.toString(), new PersonContextMapper());
        user.setUid(uid);
        return user;
    }
    
    @Override
    public List<String> getUserGroups(String realm, String uid) {
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", groupObjectClass)).and(new EqualsFilter(groupSearchAttribute, uid));
        @SuppressWarnings("unchecked")
        List<String> groups = ldapTemplate.search(dn, filter.toString(), new GroupContextMapper());
        return groups;
    }
    
}
