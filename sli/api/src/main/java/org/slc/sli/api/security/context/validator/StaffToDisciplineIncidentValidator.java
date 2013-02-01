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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates staff have the necessary context to view a given discipline incident.
 * 
 * @author kmyers
 *
 */
@Component
public class StaffToDisciplineIncidentValidator extends AbstractContextValidator {
    
    @Autowired
    private StaffToSubStudentEntityValidator subStudentValidator;
    
    @Autowired
    private StaffToEdOrgValidator schoolValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStaff() && EntityNames.DISCIPLINE_INCIDENT.equals(entityType);
    }
    
    /**
     * Can see the Discipline incidents of the students beneath you with student discipline incident
     * associations
     * and you can see the ones that are in the edorg heirarchy beneath you
     */
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.DISCIPLINE_INCIDENT, entityType, ids)) {
            return false;
        }
        
        boolean match = false;
        //Set<String> diIds = new HashSet<String>();
        
        for (String id : ids) {
            match = false;
            // The union of studentDisciplineIncidentAssociations and DI.schoolId
            NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.DISCIPLINE_INCIDENT_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            Iterable<Entity> associations = getRepo().findAll(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,
                    basicQuery);
            for (Entity association : associations) {
                if (subStudentValidator.validate(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,
                        new HashSet<String>(Arrays.asList(association.getEntityId())))) {
                    match = true;
                }
            }
            basicQuery = new NeutralQuery(
                    new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, id));
            Entity di = getRepo().findOne(EntityNames.DISCIPLINE_INCIDENT, basicQuery);
            // If the disciplineIncident doesn't exist, bail.
            if (di == null) {
                return false;
            }
            basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL,
                    (String) di.getBody().get(ParameterConstants.SCHOOL_ID)));
            Set<String> schoolId = new HashSet<String>(Arrays.asList(getRepo().findOne(
                    EntityNames.EDUCATION_ORGANIZATION, basicQuery).getEntityId()));
            if (schoolValidator.validate(EntityNames.SCHOOL, schoolId)) {
                match = true;
            }
            if (!match) {
                return false;
            }

        }
        
        return match;
    }
    
    /**
     * @param subStudentValidator
     *            the subStudentValidator to set
     */
    public void setSubStudentValidator(StaffToSubStudentEntityValidator subStudentValidator) {
        this.subStudentValidator = subStudentValidator;
    }
    
    /**
     * @param schoolValidator
     *            the schoolValidator to set
     */
    public void setSchoolValidator(StaffToEdOrgValidator schoolValidator) {
        this.schoolValidator = schoolValidator;
    }
}
