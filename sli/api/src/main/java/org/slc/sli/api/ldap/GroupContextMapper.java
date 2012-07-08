package org.slc.sli.api.ldap;

import java.util.Arrays;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * LDAPTemplate mapper for converting Group object from the LDAP group context.
 * 
 * @author dliu
 * 
 */
public class GroupContextMapper implements ContextMapper {

    @Override
    public Object mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        Group group = new Group();
        group.setGroupName(context.getStringAttribute("cn"));
        group.setMemberUids(Arrays.asList(context.getStringAttributes("memberUid")));
        return group;
    }
    
}
