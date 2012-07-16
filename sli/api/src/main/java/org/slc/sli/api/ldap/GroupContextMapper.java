package org.slc.sli.api.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String[] memberUids = context.getStringAttributes("memberUid");
        if (memberUids != null && memberUids.length > 0) {
            List<String> uids = new ArrayList<String>();
            uids.addAll(Arrays.asList(memberUids));
            group.setMemberUids(uids);
        }
        return group;
    }
    
}
