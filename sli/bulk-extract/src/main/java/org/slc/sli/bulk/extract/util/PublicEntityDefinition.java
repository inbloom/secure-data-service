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

package org.slc.sli.bulk.extract.util;

import java.util.Arrays;
import java.util.List;

/**
 * A definition for each entity and the path to a field that references an EdOrg entity.
 * @author ablum
 */
public enum PublicEntityDefinition {

    EDUCATION_ORGANIZATION("educationOrganization"),
    COURSE("course"),
    COURSE_OFFERING("courseOffering"),
    SESSION("session"),
    GRADUATION_PLAN("graduationPlan"),
    GRADING_PERIOD("gradingPeriod"),
    CALENDAR_DATE("calendarDate"),
    ASSESSMENT("assessment"),
    LEARNING_OBJECTIVE("learningObjective"),
    LEARNING_STANDARD("learningStandard"),
    COMPETENCY_LEVEL_DESCRIPTOR("competencyLevelDescriptor"),
    STUDENT_COMPETENCY_OBJECTIVE("studentCompetencyObjective"),
    PROGRAM("program"),
    SECTION("section"),
    COHORT("cohort");

    private final String entityName;

    PublicEntityDefinition(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Gets the name of the entity.
     * @return the name of the entity
     */
    public String getEntityName() {
        return entityName;
    }

}
