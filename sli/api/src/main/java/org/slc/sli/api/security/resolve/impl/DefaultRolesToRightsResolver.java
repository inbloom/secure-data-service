/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security.resolve.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
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

    @Autowired
    private ClientRoleResolver roleMapper;

    @Autowired
    private RoleRightAccess roleRightAccess;
    
    @Autowired
    private Repository<Entity> repo;
    
    @Override
    public Set<GrantedAuthority> resolveRoles(String realmId, List<String> roleNames) {
        Set<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        if (roleNames != null) {
            List<String> sliRoleNames = roleMapper.resolveRoles(realmId, roleNames);

            for (String sliRoleName : sliRoleNames) {
                Role role = findRole(sliRoleName);
                if (role != null) {
                    if (role.isAdmin() && !isAdminRealm(realmId)) {
                        debug("Ignoring {} because {} is not admin realm.", role.getName(), realmId);
                        continue;
                    }
                    auths.addAll(role.getRights());
                }
            }
        }
        return auths;
    }
    
    private boolean isAdminRealm(String realmId) {
        Entity entity = repo.findById("realm", realmId);
        Boolean admin = (Boolean) entity.getBody().get("admin");
        return admin != null ? admin : false;
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
