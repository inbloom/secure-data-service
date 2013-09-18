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

import java.util.*;

import org.slc.sli.api.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates the context of a staff member to see the requested set of teacher school associations.
 * Returns true if the staff member can see ALL of the associations, and false otherwise.
 *
 * @author shalka
 */
@Component
public class StaffToTeacherSchoolAssociationValidator extends AbstractContextValidator {

    @Autowired
    private GenericToEdOrgValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStaff() && EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(entityType);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.TEACHER_SCHOOL_ASSOCIATION, entityType, ids)) {
            return Collections.EMPTY_SET;
        }

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids)));
        Iterable<Entity> teacherSchoolAssociations = getRepo().findAll(entityType, query);
        Map<String, Set<String>> schools = new HashMap<String, Set<String>>();
        if (teacherSchoolAssociations != null) {
            for (Entity teacherSchoolAssociation : teacherSchoolAssociations) {
                Map<String, Object> body = teacherSchoolAssociation.getBody();
                String school = (String) body.get("schoolId");
                if (!schools.containsKey(school)) {
                    schools.put(school, new HashSet<String>());
                }
                schools.get(school).add(teacherSchoolAssociation.getEntityId());
            }
        }

        if (schools.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> validSchools = validator.validate(EntityNames.EDUCATION_ORGANIZATION, schools.keySet());
        return getValidIds(validSchools, schools);
    }

    /**
     * Sets the staff to education organization validator (for unit testing).
     *
     * @param validator
     *            Staff to education organization validator to be used.
     */
    protected void setStaffToEdOrgValidator(GenericToEdOrgValidator validator) {
        this.validator = validator;
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.STAFF_CONTEXT;
    }
}
