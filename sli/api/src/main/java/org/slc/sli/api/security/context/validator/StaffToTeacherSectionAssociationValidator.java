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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates the context of a staff member to see the requested set of teacher school associations.
 * Returns true if the staff member can see ALL of the associations, and false otherwise.
 *
 * @author shalka
 */
@Component
public class StaffToTeacherSectionAssociationValidator extends AbstractContextValidator {

    @Autowired
    private StaffToSectionValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStaff() && EntityNames.TEACHER_SECTION_ASSOCIATION.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.TEACHER_SECTION_ASSOCIATION, entityType, ids)) {
            return false;
        }

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids)));
        Iterable<Entity> teacherSectionAssociations = getRepo().findAll(entityType, query);
        Set<String> sections = new HashSet<String>();
        if (teacherSectionAssociations != null) {
            for (Entity teacherSectionAssociation : teacherSectionAssociations) {
                Map<String, Object> body = teacherSectionAssociation.getBody();
                String section = (String) body.get("sectionId");
                sections.add(section);
            }
        }

        if (sections.isEmpty()) {
            return false;
        }

        return validator.validate(EntityNames.SECTION, sections);
    }

    /**
     * Sets the staff to section validator (for unit testing).
     *
     * @param validator
     *            Staff to section validator to be used.
     */
    protected void setStaffToSectionValidator(StaffToSectionValidator validator) {
        this.validator = validator;
    }
}
