package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    @Override
    public List<GrantedAuthority> resolveRoles(List<String> roleNames) {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        
        if (roleNames != null) {
            List<String> sliRoleNames = roleMapper.resolveRoles(roleNames);
            
            for (String sliRoleName : sliRoleNames) {
                auths.addAll(Arrays.asList(findRole(sliRoleName).getRights()));
            }
            
        }
        
        return auths;
    }
    
    private DefaultRoles findRole(String roleName) {
        DefaultRoles role = DefaultRoles.NONE;
        
        for (DefaultRoles r : DefaultRoles.values()) {
            if (r.getRoleName().equals(roleName)) {
                role = r;
                break;
            }
        }
        
        return role;
    }
    
    public void setRoleMapper(ClientRoleResolver roleMapper) {
        this.roleMapper = roleMapper;
    }
    
}
