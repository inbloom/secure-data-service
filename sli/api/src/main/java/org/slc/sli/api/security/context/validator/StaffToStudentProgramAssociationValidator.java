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
 * Validates the context of a staff member to see the requested set of student program associations.
 * Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class StaffToStudentProgramAssociationValidator extends AbstractContextValidator {

    @Autowired
    private StaffToProgramValidator staffProgramValidator;
    
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStaff() && EntityNames.STUDENT_PROGRAM_ASSOCIATION.equals(entityType);
    }
    
    /**
     * Can see all of the studentProgramAssociations of all of the edorgs and programs
     * that I can see that aren't expired.
     */
    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_PROGRAM_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        boolean match = false;
        // See the program && see the edorg
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, ids));
        Iterable<Entity> scas = getRepo().findAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, basicQuery);
        Set<String> lineage = getStaffEdOrgLineage();
        for (Entity sca : scas) {
            String edOrgId = (String) sca.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_ID);
            String programId = (String) sca.getBody().get(ParameterConstants.PROGRAM_ID);
            boolean validByEdOrg = lineage.contains(edOrgId);
            boolean validByProgram = staffProgramValidator.validate(EntityNames.PROGRAM,
                    new HashSet<String>(Arrays.asList(programId)));
            if (!(validByEdOrg || validByProgram) || isFieldExpired(sca.getBody(), ParameterConstants.END_DATE, true)) {
                return false;
            } else {
                match = true;
            }
        }
        return match;
    }
    
    /**
     * @param staffProgramValidator
     *            the staffProgramValidator to set
     */
    public void setStaffProgramValidator(StaffToProgramValidator staffProgramValidator) {
        this.staffProgramValidator = staffProgramValidator;
    }
    
}
