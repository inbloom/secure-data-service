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
package org.slc.sli.api.security.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

@Component
public class EntityOwnershipValidator {

    private static final Logger LOG = LoggerFactory.getLogger(EntityOwnershipValidator.class);

    private Set<String> globalEntities;

    @Autowired
    private EdOrgOwnershipArbiter arbiter;

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {

        globalEntities = new HashSet<String>(Arrays.asList(EntityNames.ASSESSMENT,
    EntityNames.COMPETENCY_LEVEL_DESCRIPTOR, EntityNames.EDUCATION_ORGANIZATION, EntityNames.SCHOOL,
    EntityNames.LEARNING_OBJECTIVE, EntityNames.LEARNING_STANDARD, EntityNames.PROGRAM,
    EntityNames.GRADING_PERIOD, EntityNames.SESSION, EntityNames.COURSE, EntityNames.COURSE_OFFERING,
            "stateEducationAgency", "localEducationAgency"));
    }
    /**
     * Determines if the requested entity can be accessed. The implicit assumption in using this
     * function is that access to the entity requested is transitive (only to the entity requested,
     * and not through the entity). If this is not the case, use the canAccess method that takes the
     * isTransitive flag as an argument.
     *
     * @param entity
     *            Requested entity.
     * @return True if the requested entity can be accessed, false otherwise.
     */
    public boolean canAccess(Entity entity) {
        return canAccess(entity, true);
    }

    /**
     * Determines if the requested entity can be accessed in the specified transitive or
     * non-transitive way.
     *
     * @param entity
     *            Requested entity.
     * @param isTransitive
     *            Flag used for specifying transitive vs. non-transitive access to the entity.
     * @return True if the requested entity can be accessed, false otherwise.
     */
    public boolean canAccess(Entity entity, boolean isTransitive) {
        if (SecurityUtil.getSLIPrincipal().getAuthorizingEdOrgs() == null) {
            // explicitly set null if the app is marked as authorized_for_all_edorgs
            return true;
        }

        if (Arrays.asList(EntityNames.PROGRAM, EntityNames.SESSION).contains(entity.getType())) {
            //  Some entities are just cannot be pnwed
            return true;
        }

        if (isTransitive && globalEntities.contains(entity.getType())) {
            LOG.debug("skipping ownership validation --> transitive access to global entity: {}", entity.getType());
            return true;
        }

        Set<String> owningEdorgs = arbiter.determineEdorgs(Arrays.asList(entity), entity.getType());
        if (owningEdorgs.size() == 0) {
            LOG.warn("Potentially bad data found.");
            return true;
        }

        for (String edOrgId : owningEdorgs) {
            if (SecurityUtil.getSLIPrincipal().getAuthorizingEdOrgs().contains(edOrgId)) {
                LOG.debug("discovered owning education organization: {}", edOrgId);
                return true;
            }
        }
        return false;
    }

}
