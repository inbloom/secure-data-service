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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates a teacher accessing a set of entities that are directly associated to a student.
 * Currently supported entities are:
 * attendance,
 * course transcript,
 * discipline action,
 * student academic record,
 * student assessment association,
 * student discipline incident association,
 * student grade book entry,
 * student section association,
 * student school association.
 *
 * @author shalka
 */
@Component
public class TeacherToSubStudentEntityValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private TeacherToStudentValidator validator;

    /**
     * Determines if the entity type is a sub-entity of student.
     */
    @Override
    public boolean canValidate(String entityType, boolean through) {
        return isTeacher() && isSubEntityOfStudent(entityType);
    }

    /**
     * Determines if the teacher can see the set of entities specified by 'ids'.
     */
    @SuppressWarnings("unchecked")
	@Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(SUB_ENTITIES_OF_STUDENT, entityType, ids)) {
            return false;
        }
        
        Set<String> students = new HashSet<String>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids)));
        Iterable<Entity> entities = repo.findAll(entityType, query);

        for (Entity entity : entities) {
            Map<String, Object> body = entity.getBody();
            if (body.get(ParameterConstants.STUDENT_ID) instanceof String) {
                students.add((String) body.get(ParameterConstants.STUDENT_ID));
            } else if (body.get(ParameterConstants.STUDENT_ID) instanceof List) {
                students.addAll((List<String>) body.get(ParameterConstants.STUDENT_ID));
            } else {
                //Student ID was not a string or a list of strings, this is unexpected
                warn("Possible Corrupt Data detected at "+entityType+"/"+entity.getEntityId());
                return false;
            }
        }

        if (students.isEmpty()) {
            return false;
        }

        return validator.validate(EntityNames.STUDENT, students);
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
        this.validator = teacherToStudentValidator;
    }
}
