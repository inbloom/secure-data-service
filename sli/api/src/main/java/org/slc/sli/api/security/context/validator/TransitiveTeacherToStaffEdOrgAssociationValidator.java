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

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates teachers accessing 'staffEdOrgAssociations' transitively.
 * General rule is that you can see things through associations that have your id on them
 */
@Component
public class TransitiveTeacherToStaffEdOrgAssociationValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    DateHelper dateHelper;


    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STAFF_ED_ORG_ASSOCIATION.equals(entityType) && isTeacher() && isTransitive;
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.STAFF_ED_ORG_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        Set<String> requiredEdOrgs = new HashSet<String>();
        {
            Iterable<Entity> requestedAssociations = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION,
                    new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids)));

            for (Entity entity : requestedAssociations) {
                requiredEdOrgs.add((String) entity.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
            }
        }

        Set<String> teachersEdOrgs = new HashSet<String>();
        {
            NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                    NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.principalId()));
            Iterable<Entity> teachersAssociations = this.repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, query);
            for (Entity entity : teachersAssociations) {
                if (!dateHelper.isFieldExpired(entity.getBody(), ParameterConstants.END_DATE)) {
                    teachersEdOrgs.add((String) entity.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
                }
            }
        }

        return teachersEdOrgs.containsAll(requiredEdOrgs);
    }

}
