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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.EdOrgContextRightsCache;
import org.slc.sli.api.security.context.EdOrgOwnershipArbiter;
import org.slc.sli.api.security.context.StudentOwnershipArbiter;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.enums.Right;

/**
 * Build user's access rights to an entity based on its associated edOrgs.
 * @author - tshewchuk
 */
@Component
public class EntityEdOrgRightBuilder {

    @Autowired
    private EdOrgOwnershipArbiter edOrgOwnershipArbiter;

    @Autowired
    private StudentOwnershipArbiter studentOwnershipArbiter;

    protected static final Set<String> STUDENT_RELATED_DATA = new HashSet<String>(Arrays.asList(
            EntityNames.ATTENDANCE, EntityNames.DISCIPLINE_ACTION, EntityNames.STUDENT_SCHOOL_ASSOCIATION,
            EntityNames.DISCIPLINE_INCIDENT, EntityNames.COURSE_TRANSCRIPT, EntityNames.STUDENT_GRADEBOOK_ENTRY));

    /**
     * Builds a set of access rights to an entity, based upon the specified EdOrg-Rights map, and the EdOrgs to which the entity belongs.
     *
     * @param edOrgRights - user EdOrg-Rights map
     * @param entity - entity to which access is sought
     *
     * @param isRead if operation is a read operation
     * @return - The set of all the user's access rights to the entity
     */
    public Collection<GrantedAuthority> buildEntityEdOrgRights(final Map<String, Collection<GrantedAuthority>> edOrgRights, final Entity entity, boolean isRead) {
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        Set<String> edorgs = edOrgOwnershipArbiter.determineHierarchicalEdorgs(Arrays.asList(entity), entity.getType());
        edorgs.retainAll(edOrgRights.keySet());

        if (edorgs.isEmpty()) {
            if(STUDENT_RELATED_DATA.contains(entity.getType()) && isRead) {
              List<Entity> studentEntities = studentOwnershipArbiter.findOwner(Arrays.asList(entity), entity.getType(), true);
              edorgs = edOrgOwnershipArbiter.determineHierarchicalEdorgs(studentEntities, EntityNames.STUDENT);
              edorgs.retainAll(edOrgRights.keySet());

              if(edorgs.isEmpty()) {
                    warn("Attempted access to an entity with no matching edorg association.");
                }
            } else {
                warn("Attempted access to an entity with no matching edorg association.");
            }
        }
            for (String edorg : edorgs) {
                authorities.addAll(edOrgRights.get(edorg));
            }

        return authorities;
    }

    /**
     * Builds a set of access rights to an entity, based upon the specified EdOrg-Context-Rights cache, the EdOrgs to which the entity belongs, and its associated contexts.
     *
     * @param edOrgContextRights - user EdOrg-Context-Rights cache
     * @param entity - entity to which access is sought
     * @param context - context(s) to which the entity belongs
     * @param isRead if operation is a read operation
     *
     * @return - The set of all the user's access rights to the entity
     */
    public Collection<GrantedAuthority> buildEntityEdOrgContextRights(final EdOrgContextRightsCache edOrgContextRights, final Entity entity,
                                                                      final SecurityUtil.UserContext context, boolean isRead) {
        Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        Set<String> edorgs = edOrgOwnershipArbiter.determineHierarchicalEdorgs(Arrays.asList(entity), entity.getType());
        edorgs.retainAll(edOrgContextRights.keySet());
        if (edorgs.isEmpty()) {
            if (STUDENT_RELATED_DATA.contains(entity.getType()) && isRead) {
                List<Entity> studentEntities = studentOwnershipArbiter.findOwner(Arrays.asList(entity), entity.getType(), true);
                edorgs = edOrgOwnershipArbiter.determineHierarchicalEdorgs(studentEntities, EntityNames.STUDENT);
                edorgs.retainAll(edOrgContextRights.keySet());

                if (edorgs.isEmpty()) {
                    warn("Attempted access to an entity with no matching edorg association.");
                }
            } else {
                warn("Attempted access to an entity with no matching edorg association.");
            }
        }

        for (String edorg : edorgs) {
            switch (context) {
                case DUAL_CONTEXT:
                    authorities.addAll(edOrgContextRights.get(edorg).get(Right.STAFF_CONTEXT.name()));
                    authorities.addAll(edOrgContextRights.get(edorg).get(Right.TEACHER_CONTEXT.name()));
                break;
                case STAFF_CONTEXT:
                    authorities.addAll(edOrgContextRights.get(edorg).get(Right.STAFF_CONTEXT.name()));
                break;
                case TEACHER_CONTEXT:
                    authorities.addAll(edOrgContextRights.get(edorg).get(Right.TEACHER_CONTEXT.name()));
                break;
                // Return empty set for no context.
            }
        }

        return authorities;
    }

    public void setEdOrgOwnershipArbiter(EdOrgOwnershipArbiter edOrgOwnershipArbiter) {
        this.edOrgOwnershipArbiter = edOrgOwnershipArbiter;
    }

}
