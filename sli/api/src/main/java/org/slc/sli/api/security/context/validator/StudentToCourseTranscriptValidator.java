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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;

/**
 * validate courseTranscripts for students both transitively and
 * non-transitively
 *
 * @author ycao
 *
 */
@Component
public class StudentToCourseTranscriptValidator extends BasicValidator {

    @Autowired
    StudentToSubStudentValidator studentToSubValidator;

    public StudentToCourseTranscriptValidator() {
        super(EntityNames.STUDENT, EntityNames.COURSE_TRANSCRIPT);
    }

    @Override
    protected boolean doValidate(Set<String> ids, Entity myself) {

        // Get the Student IDs on the things we want to see, compare with the IDs of yourself
        Set<String> studentAcademicRecordIds = new HashSet<String>(
                getIdsContainedInFieldOnEntities(EntityNames.COURSE_TRANSCRIPT, new ArrayList<String>(ids), ParameterConstants.STUDENT_ACADEMIC_RECORD_ID));

        return studentToSubValidator.validate(EntityNames.STUDENT_ACADEMIC_RECORD, studentAcademicRecordIds);
    }

}
