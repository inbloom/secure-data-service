package org.slc.sli.api.security.roles;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple builder class to map their role to ours if it exists.
 *
 * TODO: Make it work against custom roles.
 */
public final class DefaultRoleMapperImpl implements IRoleMapper {
    List<GrantedAuthorityImpl> mappedRoles;
    List<String> theirRoles;

    public List<GrantedAuthorityImpl> buildMappedRoles() {
        for (String role : theirRoles) {
            if (DefaultRoles.ADMINISTRATOR.getRoleName().equals(role)
                    || DefaultRoles.LEADER.getRoleName().equals(role)
                    || DefaultRoles.EDUCATOR.getRoleName().equals(role)
                    || DefaultRoles.AGGREGATOR.getRoleName().equals(role)) {
                mappedRoles.add(new GrantedAuthorityImpl(createDefaultRole(role)));
            }
        }
        return mappedRoles;
    }
    
    private String createDefaultRole(String role) {
        return "ROLE_" + role.toUpperCase().replace(' ', '_');
    }
    
    public DefaultRoleMapperImpl(List<String> roles) {
        theirRoles = roles;
        mappedRoles = new ArrayList<GrantedAuthorityImpl>();
    }
}
