package org.slc.sli.api.security.resolve.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.security.roles.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;

/**
 * 
 * @author dkornishev
 * 
 */
@Component
public class DefaultRolesToRightsResolver implements RolesToRightsResolver {
    
    @Autowired
    private ClientRoleResolver roleMapper;
    @Autowired
    private RoleRightAccess roleRightAccess;

    @Override
    public Set<GrantedAuthority> resolveRoles(List<String> roleNames) {
        Set<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        
        if (roleNames != null) {
            List<String> sliRoleNames = roleMapper.resolveRoles(roleNames);
            
            for (String sliRoleName : sliRoleNames) {
                Role role = findRole(sliRoleName);
                if (role != null) {
                    auths.addAll(role.getRights());
                }
            }
        }
        return auths;
    }
    
    private Role findRole(String roleName) {
        return roleRightAccess.getDefaultRole(roleName);
    }

    public void setRoleRightAccess(RoleRightAccess roleRightAccess) {
        this.roleRightAccess = roleRightAccess;
    }

    public void setRoleMapper(ClientRoleResolver roleMapper) {
        this.roleMapper = roleMapper;
    }
    
}
