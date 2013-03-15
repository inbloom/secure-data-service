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

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates teacher context to a global section. This logic is applied to transitive UPDATEs and non-transitive GETs.
 */
@Component
public class TeacherToSectionValidator extends AbstractContextValidator {

    @Resource
    private PagingRepositoryDelegate<Entity> repo;

    @Resource
    private DateHelper dateHelper;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.SECTION.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.SECTION, entityType, ids)) {
            return false;
        }

        Set<String> sectionIds = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.SECTION_ID, NeutralCriteria.CRITERIA_IN, ids));
        query.addCriteria(new NeutralCriteria(ParameterConstants.TEACHER_ID, NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        Iterable<Entity> associations = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, query);

        if (associations != null) {
            for (Entity association : associations) {
                if (!dateHelper.isFieldExpired(association.getBody(), ParameterConstants.END_DATE)) {
                    sectionIds.add((String) association.getBody().get(ParameterConstants.SECTION_ID));
                }
            }
        }
        return ids.size() == sectionIds.size();
    }

}
