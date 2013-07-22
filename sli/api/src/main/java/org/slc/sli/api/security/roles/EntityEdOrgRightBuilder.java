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

package org.slc.sli.api.security.roles;

import java.util.*;

import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.EdOrgOwnershipArbiter;
import org.slc.sli.domain.Entity;

/**
 * Build user's access rights to an entity based on its associated edOrgs.
 * @author - tshewchuk
 */
@Component
public class EntityEdOrgRightBuilder {

    @Autowired
    private EdOrgOwnershipArbiter edOrgOwnershipArbiter;

    @Autowired
    private ContextValidator conTextValidator;

    /**
     * Builds a set of access rights to an entity, based upon the specified EdOrg-Rights map, and the EdOrgs to which the entity belongs.
     * @param edOrgRights - user EdOrg-Rights map
     * @param entity - entity to which access is sought
     *
     * @return - The set of all the user's access rights to the entity
     */
    public Collection<GrantedAuthority> buildEntityEdOrgRights(final Map<String, Collection<GrantedAuthority>> edOrgRights, final Entity entity) {
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        Set<String> edorgs = edOrgOwnershipArbiter.determineHierarchicalEdorgs(Arrays.asList(entity), entity.getType());

        edorgs.retainAll(edOrgRights.keySet());

        if (edorgs.isEmpty()) {
            warn("Attempted access to an entity with no matching edorg association.");
        } else {
            for (String edorg : edorgs) {
                authorities.addAll(edOrgRights.get(edorg));
            }
        }

        return authorities;
    }

    /**
     * Builds a set of access rights to an entity, based upon the specified EdOrg-Rights map, and the EdOrgs to which the entity belongs.
     * @param edOrgContextRights - user EdOrg-Context-Rights map
     * @param entity - entity to which access is sought
     *
     * @return - The set of all the user's access rights to the entity
     */
    public Collection<GrantedAuthority> buildContextualEntityEdOrgRights(final Map<String, Map<String, Collection<GrantedAuthority>>> edOrgContextRights, final Entity entity, Set<String> contexts) {
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        Set<String> edorgs = edOrgOwnershipArbiter.determineHierarchicalEdorgs(Arrays.asList(entity), entity.getType());

        if (edorgs.isEmpty()) {
            warn("Attempted access to an entity with no matching edorg association.");
        } if (contexts.size() == 0) {
            warn("Attempted access to entity with no context.");
        } else {
            for (String edorg : edorgs) {
                Map<String, Collection<GrantedAuthority>> contextRights = edOrgContextRights.get(edorg);
                authorities.addAll(getContextRights(contextRights, contexts));
            }
        }

        return authorities;
    }

    protected Collection<GrantedAuthority> getContextRights(Map<String, Collection<GrantedAuthority>> contextRights, Set<String> contexts) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        if (contextRights != null) {

            if (validCotnext(contextRights, contexts, SecurityUtil.STAFF_CONTEXT)) {
                grantedAuthorities.addAll(contextRights.get(SecurityUtil.STAFF_CONTEXT));
            }

            if (validCotnext(contextRights, contexts, SecurityUtil.TEACHER_CONTEXT)) {
                grantedAuthorities.addAll(contextRights.get(SecurityUtil.TEACHER_CONTEXT));
            }
        }

        return grantedAuthorities;
    }

    private boolean validCotnext(Map<String, Collection<GrantedAuthority>> contextRights, Set<String> contexts, String curContext) {
        return contextRights.containsKey(curContext) && (contexts.contains(curContext) || contexts.contains(SecurityUtil.NULL_CONTEXT));
    }

    public void setEdOrgOwnershipArbiter(EdOrgOwnershipArbiter edOrgOwnershipArbiter) {
        this.edOrgOwnershipArbiter = edOrgOwnershipArbiter;
    }

}
