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
package org.slc.sli.api.resources.generic.service;

import org.slc.sli.common.constants.EntityNames;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Temporary class to list the entities supported by contextual roles. Should be removed after F262.
 * @author: npandey
 */
public final class ContextSupportedEntities {

    public static Set<String> getSupportedEntities() {
        return supportedEntities;
    }

    private static Set<String> supportedEntities;

    static {
        supportedEntities = new HashSet<String>();
        supportedEntities.add(EntityNames.STUDENT);
        supportedEntities.addAll(Arrays.asList(EntityNames.COHORT, EntityNames.DISCIPLINE_INCIDENT,
                EntityNames.DISCIPLINE_ACTION, EntityNames.GRADEBOOK_ENTRY,
                EntityNames.ATTENDANCE, EntityNames.COURSE_TRANSCRIPT));
        supportedEntities.add(EntityNames.STAFF_ED_ORG_ASSOCIATION);
        supportedEntities.add(EntityNames.TEACHER_SCHOOL_ASSOCIATION);
        supportedEntities.add(EntityNames.STUDENT_PROGRAM_ASSOCIATION);
        supportedEntities.add(EntityNames.STUDENT_SCHOOL_ASSOCIATION);
        supportedEntities.add(EntityNames.STUDENT_ACADEMIC_RECORD);
        supportedEntities.add(EntityNames.STUDENT_ASSESSMENT);
        supportedEntities.add(EntityNames.STUDENT_COHORT_ASSOCIATION);
        supportedEntities.add(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION);
        supportedEntities.add(EntityNames.STUDENT_PARENT_ASSOCIATION);
        supportedEntities.add(EntityNames.STUDENT_SECTION_ASSOCIATION);
        supportedEntities.add(EntityNames.REPORT_CARD);

        supportedEntities.addAll(EntityNames.PUBLIC_ENTITIES);
    }
}
