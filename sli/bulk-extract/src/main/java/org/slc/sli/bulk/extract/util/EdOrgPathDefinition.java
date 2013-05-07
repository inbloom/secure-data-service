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

/**
 * A definition for each entity and the path to a field that references an EdOrg entity.
 * @author ablum
 */
public enum EdOrgPathDefinition {

    STATE_EDUCATION_AGENCY("educationOrganization", "_id"),
    LOCAL_EDUCATION_AGENCY("educationOrganization", "body.parentEducationAgencyReference"),
    SCHOOL("school", "body.parentEducationAgencyReference"),
    COURSE("course", "body.schoolId"),
    COURSE_OFFERING("courseOffering", "body.schoolId"),
    SESSION("session", "body.schoolId"),
    GRADUATION_PLAN("graduationPlan", "body.educationOrganizationId");

    private final String entityName;
    private final String edOrgRefField;

    EdOrgPathDefinition(String entityName, String edOrgRefField) {
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

}
