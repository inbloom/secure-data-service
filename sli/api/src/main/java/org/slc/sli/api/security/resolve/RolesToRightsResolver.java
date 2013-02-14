/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.security.resolve;

import java.util.List;
import java.util.Set;

import org.slc.sli.api.security.roles.Role;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author dkornishev
 *
 */
public interface RolesToRightsResolver {
    
    /**
     * Map roles to sli roles
     * @param tenantId
     * @param realmId
     * @param roleNames
     * @return
     */
    public Set<Role> mapRoles(String tenantId, String realmId, List<String> roleNames, boolean isAdminRealm);
    
    /**
     * Resolve roles to rights
     * @param tenantId
     * @param realmId
     * @param roleNames
     * @return
     */
    public Set<GrantedAuthority> resolveRoles(String tenantId, String realmId, List<String> roleNames, boolean isAdminRealm, boolean getSelfRights);
}
