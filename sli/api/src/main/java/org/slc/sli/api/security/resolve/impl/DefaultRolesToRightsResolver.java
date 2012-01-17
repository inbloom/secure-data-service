package org.slc.sli.api.security.resolve.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.SliAdminValidator;
import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * 
 * @author dkornishev
 * 
 */
@Component
public class DefaultRolesToRightsResolver implements RolesToRightsResolver {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRolesToRightsResolver.class);
    
    @Autowired
    private ClientRoleResolver roleMapper;
    
    @Autowired
    private RoleRightAccess roleRightAccess;
    
    @Autowired
    private SliAdminValidator sliAdminValidator;
    
    @Override
    public Set<GrantedAuthority> resolveRoles(String realmId, List<String> roleNames) {
        Set<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        if (roleNames != null) {
            List<String> sliRoleNames = roleMapper.resolveRoles(roleNames);
            
            for (String sliRoleName : sliRoleNames) {
                Role role = findRole(sliRoleName);
                if (role != null) {
                    if (role.getName().equals(RoleInitializer.SLI_ADMINISTRATOR) && !sliAdminValidator.isSliAdminRealm(realmId)) {
                        LOG.trace("Ignoring SLI Admin because {} is not admin realm.", realmId);
                        continue;
                    }
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
    
    public void setSliAdminValidator(SliAdminValidator sliAdminValidator) {
        this.sliAdminValidator = sliAdminValidator;
    }
    
}
