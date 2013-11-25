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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Resolves which courseTranscripts any given teacher can access.
 *
 */
@Component
public class TeacherToCourseTranscriptValidator extends AbstractContextValidator {

    private static final Logger LOG = LoggerFactory.getLogger(TeacherToCourseTranscriptValidator.class);

    @Autowired
    private TeacherToSubStudentEntityValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.COURSE_TRANSCRIPT.equals(entityType);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {

        if (!areParametersValid(EntityNames.COURSE_TRANSCRIPT, entityType, ids)) {
            return Collections.emptySet();
        }

        Map<String, Set<String>> studentAcademicRecordToCT = new HashMap<String, Set<String>>();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids)));
        Iterable<Entity> entities = getRepo().findAll(EntityNames.COURSE_TRANSCRIPT, query);

        for (Entity entity : entities) {
            Map<String, Object> body = entity.getBody();
            if (body.get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID) instanceof String) {
                String id = (String) body.get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID);
                if (!studentAcademicRecordToCT.containsKey(id)) {
                    studentAcademicRecordToCT.put(id, new HashSet<String>());
                }
                studentAcademicRecordToCT.get(id).add(entity.getEntityId());
            } else {
                //studentacademicrecord ID was not a string, this is unexpected
                LOG.warn("Possible Corrupt Data detected at "+entityType+"/"+entity.getEntityId());
            }
        }

        if (studentAcademicRecordToCT.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> sarIds = validator.validate(EntityNames.STUDENT_ACADEMIC_RECORD, studentAcademicRecordToCT.keySet());
        return getValidIds(sarIds, studentAcademicRecordToCT);
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.TEACHER_CONTEXT;
    }

}
