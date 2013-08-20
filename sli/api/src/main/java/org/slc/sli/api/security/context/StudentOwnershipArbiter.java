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
package org.slc.sli.api.security.context;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Establish student ownership of an entity.
 *
 * @author: npandey ablum
 */

@Component
public class StudentOwnershipArbiter extends OwnershipArbiter {

    @SuppressWarnings("unused")
    @PostConstruct
    public void init() {

        typeToReference.put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, new Reference(EntityNames.STUDENT_SCHOOL_ASSOCIATION,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.ATTENDANCE, new Reference(EntityNames.ATTENDANCE, EntityNames.STUDENT,
                ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.COURSE_TRANSCRIPT, new Reference(EntityNames.COURSE_TRANSCRIPT,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.DISCIPLINE_INCIDENT, new Reference(EntityNames.DISCIPLINE_INCIDENT,
                EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, ParameterConstants.DISCIPLINE_INCIDENT_ID, Reference.RefType.RIGHT_TO_LEFT));

        typeToReference.put(EntityNames.DISCIPLINE_ACTION, new Reference(EntityNames.DISCIPLINE_ACTION,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new Reference(
                EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, EntityNames.STUDENT,
                ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_GRADEBOOK_ENTRY, new Reference(EntityNames.STUDENT_GRADEBOOK_ENTRY,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_ACADEMIC_RECORD, new Reference(EntityNames.STUDENT_ACADEMIC_RECORD,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.PARENT, new Reference(EntityNames.PARENT,
                EntityNames.STUDENT_PARENT_ASSOCIATION, ParameterConstants.PARENT_ID, Reference.RefType.RIGHT_TO_LEFT));

        typeToReference.put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new Reference(EntityNames.STUDENT_PROGRAM_ASSOCIATION,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_COHORT_ASSOCIATION, new Reference(EntityNames.STUDENT_COHORT_ASSOCIATION,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_SECTION_ASSOCIATION, new Reference(EntityNames.STUDENT_SECTION_ASSOCIATION,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_PARENT_ASSOCIATION, new Reference(EntityNames.STUDENT_PARENT_ASSOCIATION,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.GRADE, new Reference(EntityNames.GRADE, EntityNames.STUDENT,
                ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.REPORT_CARD, new Reference(EntityNames.REPORT_CARD,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_ASSESSMENT, new Reference(EntityNames.STUDENT_ASSESSMENT,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));

        typeToReference.put(EntityNames.STUDENT_COMPETENCY, new Reference(EntityNames.STUDENT_COMPETENCY,
                EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, Reference.RefType.LEFT_TO_RIGHT));
    }

    @Override
    protected boolean isBaseType(String type) {
        return type.equals(EntityNames.STUDENT);
    }
}
