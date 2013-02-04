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
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates teacher access to sessions are associated by school/edOrg or that are accessible THROUGH students.
 */
@Component
public class TransitiveTeacherToSessionValidator extends AbstractContextValidator {

    @Autowired
    private TeacherToSessionValidator sessionValidator;

    @Autowired
    PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private StudentValidatorHelper studentHelper;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.SESSION.equals(entityType) && isTransitive;
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.SESSION, entityType, ids)) {
            return false;
        }
        return getValid(entityType, ids).containsAll(ids);
    }

    @Override
    public Set<String> getValid(String entityType, Set<String> ids) {
        Set<String> validSessions = sessionValidator.getValid(entityType, ids);

        Set<String> sessionsToValidate = new HashSet<String>(ids);
        sessionsToValidate.removeAll(validSessions);

        if (!sessionsToValidate.isEmpty()) {

            Iterable<Entity> ssas = repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, new NeutralQuery(
                    new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.CRITERIA_IN, studentHelper.getStudentIds())));

            Set<String> sectionIds = new HashSet<String>();
            for (Entity ssa : ssas) {
                sectionIds.add((String) ssa.getBody().get(ParameterConstants.SECTION_ID));
            }

            Iterable<Entity> sections = repo.findAll(EntityNames.SECTION, new NeutralQuery(
                    new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, sectionIds)));
            Set<String> studentsSessions = new HashSet<String>();
            for (Entity section : sections) {
                String sessionId = (String) section.getBody().get(ParameterConstants.SESSION_ID);
                if (sessionId != null) {
                    studentsSessions.add(sessionId);
                }
            }



            // any sessions to validate that are student sessions will be added to valid sessions
            sessionsToValidate.retainAll(studentsSessions);
            validSessions.addAll(sessionsToValidate);
        }

        return validSessions;
    }

    /**
     * @param sessionValidator the sessionValidator to set
     */
    public void setSectionValidator(TeacherToSessionValidator sessionValidator) {
        this.sessionValidator = sessionValidator;
    }

    /**
     * @param studentHelper the studentHelper to set
     */
    public void setStudentHelper(StudentValidatorHelper studentHelper) {
        this.studentHelper = studentHelper;
    }


}
