package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.security.roles.RolesAndPermissionsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.enums.DefaultRoles;
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
    private RolesAndPermissionsResource roleResource;
    
    @Override
    public List<GrantedAuthority> resolveRoles(List<String> roleNames) {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        
        if (roleNames != null) {
            List<String> sliRoleNames = roleMapper.resolveRoles(roleNames);
            
            for (String sliRoleName : sliRoleNames) {
                auths.addAll(findRole(sliRoleName).getRights());
            }
            
        }
        
        return auths;
    }
    
    private DefaultRoles findRole(String roleName) {
        return DefaultRoles.getDefaultRoleByName(roleName);
    }
    
    public void setRoleMapper(ClientRoleResolver roleMapper) {
        this.roleMapper = roleMapper;
    }
    
}
