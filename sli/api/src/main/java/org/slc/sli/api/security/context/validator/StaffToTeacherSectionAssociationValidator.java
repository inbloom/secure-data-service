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
public class StaffToTeacherSectionAssociationValidator extends AbstractContextValidator {

    @Autowired
    private StaffToGlobalSectionValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStaff() && EntityNames.TEACHER_SECTION_ASSOCIATION.equals(entityType);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.TEACHER_SECTION_ASSOCIATION, entityType, ids)) {
            return Collections.EMPTY_SET;
        }

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids)));
        Iterable<Entity> teacherSectionAssociations = getRepo().findAll(entityType, query);
        Map<String, Set<String>> sections = new HashMap<String, Set<String>>();
        if (teacherSectionAssociations != null) {
            for (Entity teacherSectionAssociation : teacherSectionAssociations) {
                Map<String, Object> body = teacherSectionAssociation.getBody();
                String section = (String) body.get(ParameterConstants.SECTION_ID);

                if (!sections.containsKey(section)) {
                    sections.put(section, new HashSet<String>());
                }
                sections.get(section).add(teacherSectionAssociation.getEntityId());
            }
        }

        if (sections.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> validSections = validator.validate(EntityNames.SECTION, sections.keySet());
        return getValidIds(validSections, sections);
    }

    /**
     * Sets the staff to section validator (for unit testing).
     *
     * @param validator
     *            Staff to section validator to be used.
     */
    protected void setStaffToSectionValidator(StaffToGlobalSectionValidator validator) {
        this.validator = validator;
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.STAFF_CONTEXT;
    }
}
