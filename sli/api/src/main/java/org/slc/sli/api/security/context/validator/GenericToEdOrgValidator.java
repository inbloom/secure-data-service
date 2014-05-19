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

package org.slc.sli.api.security.context.validator;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Validates the context of a user to see the requested set of education organizations. Returns
 * true if the user can see ALL of the entities, and false otherwise.
 */
@Component
public class GenericToEdOrgValidator extends AbstractContextValidator {
    private static final Logger LOG = LoggerFactory.getLogger(GenericToEdOrgValidator.class);

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        if (EntityNames.SCHOOL.equals(entityType) || EntityNames.EDUCATION_ORGANIZATION.equals(entityType)) {
            if (isStudentOrParent()) {
                // only validates non-transitive url for students and parents
                return !isTransitive;
            }
            return true;
        }
        return false;
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        LOG.trace(">>>GenericToEdOrgValidator.validate()");
        LOG.trace("  entityType: " + entityType);
        LOG.trace("  ids: " + ids.toString());

        if (!areParametersValid(Arrays.asList(EntityNames.SCHOOL, EntityNames.EDUCATION_ORGANIZATION), entityType, ids)) {
            LOG.trace("  ...return empty set - areParametersValid");
            return Collections.emptySet();
        }

        Set<String> edOrgs = getDirectEdorgs();
        LOG.trace("  ...count after adding DirectEdorgs: " + edOrgs.size());
        edOrgs.addAll(getEdorgDescendents(edOrgs));
        LOG.trace("  ...count after adding getEdorgDescendents: " + edOrgs.size());

        /* Retains only the elements in this set that are contained in the specified collection (optional operation).  In other words, removes
        * from this set all of its elements that are not contained in the specified collection. (An Intersection.) */

        edOrgs.retainAll(ids);
        LOG.trace("  ...count after adding providedIds: " + edOrgs.size());

        LOG.trace("  edOrgs: " + edOrgs.toString());

        return edOrgs;
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.DUAL_CONTEXT;
    }
}
