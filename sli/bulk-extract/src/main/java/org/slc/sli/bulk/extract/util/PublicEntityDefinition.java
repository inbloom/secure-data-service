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

    STATE_EDUCATION_AGENCY("educationOrganization", "_id"),
    COURSE("course", "body.schoolId"),
    COURSE_OFFERING("courseOffering", "body.schoolId"),
    SESSION("session", "body.schoolId"),
    GRADUATION_PLAN("graduationPlan", "body.educationOrganizationId"),
    GRADING_PERIOD("gradingPeriod", "body.gradingPeriodIdentity.schoolId"),

    ASSESSMENT("assessment", null),
    LEARNING_OBJECTIVE("learningObjective", null),
    LEARNING_STANDARD("learningStandard",null),
    COMPETENCY_LEVEL_DESCRIPTOR("competencyLevelDescriptor", null),
    STUDENT_COMPETENCY_OBJECTIVE("studentCompetencyObjective", null),
    PROGRAM("program", null);

    private final String entityName;
    private final String edOrgRefField;

    PublicEntityDefinition(String entityName, String edOrgRefField) {
        this.entityName = entityName;
        this.edOrgRefField = edOrgRefField;
    }

    /**
     * Gets the name of the entity.
     * @return the name of the entity
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Gets the fieldname path for the EdOrg Reference in the entity.
     * @return edOrgRefField
     */
    public String getEdOrgRefField() {
        return edOrgRefField;
    }

    /**
     * returns the entities which directly reference an SEA
     * @return
     */
    public static List<PublicEntityDefinition> directReferencedEntities() {
        return Arrays.asList(STATE_EDUCATION_AGENCY, COURSE, COURSE_OFFERING, SESSION, GRADING_PERIOD);
    }

    /**
     * returns the entities which do not reference an SEA
     * @return
     */
    public static List<PublicEntityDefinition> unFilteredEntities() {
        return Arrays.asList(ASSESSMENT, LEARNING_OBJECTIVE, LEARNING_STANDARD, COMPETENCY_LEVEL_DESCRIPTOR, STUDENT_COMPETENCY_OBJECTIVE, PROGRAM);
    }

    /**
     * returns the entities which doesn't have reference to SEA
     * @return
     */
    public static List<PublicEntityDefinition> independentEntities() {
        return Arrays.asList(GRADUATION_PLAN);
    }
}
