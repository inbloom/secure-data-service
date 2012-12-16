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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validates access to sections that you are both directly associated to and that you have
 * access THROUGH your students to.
 * 
 */
@Component
public class TransitiveTeacherToSectionValidator extends AbstractContextValidator {
    @Autowired
    private TeacherToSectionValidator sectionValidator;

    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private StudentValidatorHelper studentHelper;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isStaff() && EntityNames.SECTION.equals(entityType) && isTransitive;
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.SECTION, entityType, ids)) {
            return false;
        }
        Set<String> validSections = new HashSet<String>();
        Set<String> myStudentIds = new HashSet<String>(studentHelper.getStudentIds());
        Set<String> studentIds = new HashSet<String>();
        for (String id : ids) {
            NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.SECTION_ID, NeutralCriteria.OPERATOR_EQUAL, id));
            Iterable<Entity> ssas = repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, basicQuery);
            for(Entity ssa : ssas) {
                if (isFieldExpired(ssa.getBody(), ParameterConstants.END_DATE, true)) {
                    continue;
                } else {
                    studentIds.add((String) ssa.getBody().get(ParameterConstants.STUDENT_ID));
                }
            }
            boolean isStudentOk = false;
            for (String studentId : studentIds) {
                if (myStudentIds.contains(studentId)) {
                    isStudentOk = true;
                    break;
                }
            }
            Set<String> sectionId = new HashSet<String>();
            sectionId.add(id);
            if (isStudentOk || sectionValidator.validate(EntityNames.SECTION, sectionId)) {
                validSections.add(id);
            } else {
                return false;
            }
            studentIds.clear();
        }
        return validSections.size() == ids.size();
    }
    
    /**
     * @param sectionValidator
     *            the sectionValidator to set
     */
    public void setSectionValidator(TeacherToSectionValidator sectionValidator) {
        this.sectionValidator = sectionValidator;
    }
    
    /**
     * @param studentHelper
     *            the studentHelper to set
     */
    public void setStudentHelper(StudentValidatorHelper studentHelper) {
        this.studentHelper = studentHelper;
    }
    

}
