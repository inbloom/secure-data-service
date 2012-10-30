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

package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates the context of a staff member to see the requested set of sections. Returns true if the
 * staff member can see ALL of the sections, and false otherwise.
 */
@Component
public class StaffToSectionValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return through == false && entityType.equals(EntityNames.SECTION) && isStaff();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        boolean match = false;
        // Get my staffEdorg to get my edorg hierarchy
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        Iterable<Entity> staffEdorgs = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
        Set<String> edorgLineage = new HashSet<String>();
        for (Entity staffEdOrg : staffEdorgs) {
            if (!isFieldExpired(staffEdOrg.getBody(), ParameterConstants.END_DATE)) {
                edorgLineage
                        .add((String) staffEdOrg.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
            }
        }
        edorgLineage.addAll(getChildEdOrgs(edorgLineage));

        for (String id : ids) {
            basicQuery = new NeutralQuery(
                    new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, id));
            Entity section = repo.findOne(EntityNames.SECTION, basicQuery);
            String schoolId = (String) section.getBody().get(ParameterConstants.SCHOOL_ID);
            if (!edorgLineage.contains(schoolId)) {
                return false;
            } else {
                match = true;
            }
        }
        return match;
    }

}
