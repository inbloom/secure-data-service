package org.slc.sli.api.security.roles;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple builder class to map their role to ours if it exists.
 *
 * TODO: Extract it and make it work against custom roles.
 */
public final class RoleMapper {
    List<GrantedAuthorityImpl> mappedRoles;
    List<String> theirRoles;

    public List<GrantedAuthorityImpl> buildMappedRoles() {
        for (String role : theirRoles) {
            if (DefaultRoles.ADMINISTRATOR.getRoleName().equals(role)
                    || DefaultRoles.LEADER.getRoleName().equals(role)
                    || DefaultRoles.EDUCATOR.getRoleName().equals(role)
                    || DefaultRoles.AGGREGATOR.getRoleName().equals(role)) {
                mappedRoles.add(new GrantedAuthorityImpl(role));
            }
        }
        //TODO When we have the IDP generating real roles we won't use this.
        mappedRoles.add(new GrantedAuthorityImpl("ROLE_USER"));
        return mappedRoles;
    }
    public RoleMapper(List<String> roles) {
        theirRoles = roles;
        mappedRoles = new ArrayList<GrantedAuthorityImpl>();
    }
}
