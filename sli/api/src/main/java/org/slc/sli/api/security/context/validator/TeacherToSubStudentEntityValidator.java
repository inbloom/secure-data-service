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

import java.util.ArrayList;
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
 * Validates a teacher accessing a set of entities that are directly associated to a student.
 * Currently supported entities are: attendance, course transcript, discipline action, student
 * academic record, student assessment association, student discipline incident association, and
 * student grade book entry.
 *
 * @author shalka
 */
@Component
public class TeacherToSubStudentEntityValidator implements SubStudentEntityValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private TeacherToStudentValidator teacherToStudentValidator;

    @Override
    public boolean canValidate(String entityType) {
        return SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.TEACHER)
                && isSubEntityOfStudent(entityType);
    }

    /**
     * Determines if the entity type is a sub-entity of student.
     *
     * @param type
     *            Entity type.
     * @return True if the entity is a sub-entity of student, false otherwise.
     */
    private boolean isSubEntityOfStudent(String type) {
        return EntityNames.ATTENDANCE.equals(type) || EntityNames.COURSE_TRANSCRIPT.equals(type)
                || EntityNames.DISCIPLINE_ACTION.equals(type) || EntityNames.STUDENT_ACADEMIC_RECORD.equals(type)
                || EntityNames.STUDENT_ASSESSMENT_ASSOCIATION.equals(type)
                || EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION.equals(type)
                || EntityNames.STUDENT_GRADEBOOK_ENTRY.equals(type);
    }

    @Override
    public boolean validate(Set<String> ids, String type) {
        Set<String> students = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.OPERATOR_EQUAL,
                new ArrayList<String>(ids)));
        Iterable<Entity> entities = repo.findAll(type, query);
        if (entities != null) {
            for (Entity entity : entities) {
                students.add((String) entity.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }
        return teacherToStudentValidator.validate(students);
    }

    /**
     * Sets the paging repository delegate (for unit testing).
     *
     * @param repo
     *            Paging Delete Repository to use.
     */
    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }

    /**
     * Sets the teacher to student validator (for unit testing).
     *
     * @param teacherToStudentValidator
     *            Teacher To Student Validator.
     */
    public void setTeacherToStudentValidator(TeacherToStudentValidator teacherToStudentValidator) {
        this.teacherToStudentValidator = teacherToStudentValidator;
    }
}
