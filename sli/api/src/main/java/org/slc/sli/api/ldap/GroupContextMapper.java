package org.slc.sli.api.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * LDAPTemplate mapper for getting group names.
 * 
 * @author scole
 * 
 */
public class GroupContextMapper implements ContextMapper {
    @Override
    public Object mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        String group = context.getStringAttribute("cn");
        return group;
    }
    
}
