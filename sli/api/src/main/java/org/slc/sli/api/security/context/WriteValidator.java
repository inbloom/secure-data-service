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

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Verify the user has access to write to an entity. We do this by determining the entities edOrg and the principals edOrgs
 * Verify the user has access to the entity once their changes are applied.
 */
@Component
public class WriteValidator {

    private HashMap<String, String> entitiesNeedingEdOrgWriteValidation;
    private List<ComplexValidation> complexValidationList;
    private Map<String, ComplexValidation> complexValidationMap;

    @Value("${sli.security.writeValidation}")
    private boolean isWriteValidationEnabled;

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    private class ComplexValidation {
        private String entityType;
        private String validationReferenceName;
        private String validationReferenceType;

        public ComplexValidation(String entityType, String validationReferenceName, String validationReferenceType) {
            this.entityType = entityType;
            this.validationReferenceName = validationReferenceName;
            this.validationReferenceType = validationReferenceType;
        }

        public String getEntityType() {
            return entityType;
        }

        public String getValidationReferenceName() {
            return validationReferenceName;
        }

        public String getValidationReferenceType() {
            return validationReferenceType;
        }
    }


    @SuppressWarnings("serial")
    @PostConstruct
    private void init() {
        entitiesNeedingEdOrgWriteValidation = new HashMap<String, String>() {
        {
            put(EntityNames.ATTENDANCE, "schoolId");
            put(EntityNames.COHORT, "educationOrgId");
            put(EntityNames.COURSE, "schoolId");
            put(EntityNames.COURSE_OFFERING, "schoolId");
            put(EntityNames.DISCIPLINE_ACTION, "responsibilitySchoolId");
            put(EntityNames.DISCIPLINE_INCIDENT, "schoolId");
            put(EntityNames.GRADUATION_PLAN, "educationOrganizationId");
            put(EntityNames.SECTION, ParameterConstants.SCHOOL_ID);
            put(EntityNames.SESSION, "schoolId");
            put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, "educationOrganizationId");
            put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "schoolId");
        }};

        complexValidationList = Arrays.asList(
                new ComplexValidation(EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.SECTION_ID, EntityNames.SECTION),
                new ComplexValidation(EntityNames.STUDENT_GRADEBOOK_ENTRY, ParameterConstants.GRADEBOOK_ENTRY_ID, EntityNames.GRADEBOOK_ENTRY),
                new ComplexValidation(EntityNames.GRADEBOOK_ENTRY, ParameterConstants.SECTION_ID, EntityNames.SECTION),
                new ComplexValidation(EntityNames.COURSE_TRANSCRIPT, ParameterConstants.COURSE_ID, EntityNames.COURSE)
        );

        complexValidationMap = new HashMap<String, ComplexValidation>();
        for (ComplexValidation validationRule: complexValidationList) {
            complexValidationMap.put(validationRule.getEntityType(), validationRule);
        }

    }


    public void validateWriteRequest(EntityBody entityBody, UriInfo uriInfo, SLIPrincipal principal) {

        if (isWriteValidationEnabled && !isValidForEdOrgWrite(entityBody, uriInfo, principal)) {
            throw new AccessDeniedException("Invalid reference. No association to referenced entity.");
        }

    }

    private boolean isValidForEdOrgWrite(EntityBody entityBody, UriInfo uriInfo, SLIPrincipal principal) {
        boolean isValid = true;

        int RESOURCE_SEGMENT_INDEX = 1;
        int VERSION_INDEX = 0;
        if (uriInfo.getPathSegments().size() > RESOURCE_SEGMENT_INDEX
                && uriInfo.getPathSegments().get(VERSION_INDEX).getPath().startsWith(PathConstants.V)) {

            String resourceName = uriInfo.getPathSegments().get(RESOURCE_SEGMENT_INDEX).getPath();
            EntityDefinition def = store.lookupByResourceName(resourceName);

            int IDS_SEGMENT_INDEX = 2;
            if (uriInfo.getPathSegments().size() > IDS_SEGMENT_INDEX) {
                // look if we have ed org write context to already existing entity
                String id = uriInfo.getPathSegments().get(IDS_SEGMENT_INDEX).getPath();
                Entity existingEntity = repo.findById(def.getStoredCollectionName(), id);
                if (existingEntity != null) {
                    isValid = isEntityValidForEdOrgWrite(existingEntity.getBody(), existingEntity.getType(), principal);
                }
            }

            if (isValid && entityBody != null && !entityBody.isEmpty()) {
                isValid = isEntityValidForEdOrgWrite(entityBody, def.getType(), principal);
            }
        }
        return isValid;
    }


    private boolean isEntityValidForEdOrgWrite(Map<String, Object> entityBody, String entityType, SLIPrincipal principal) {
        boolean isValid = true;
        if (entitiesNeedingEdOrgWriteValidation.get(entityType) != null) {
            String edOrgId = (String) entityBody.get(entitiesNeedingEdOrgWriteValidation.get(entityType));
            isValid = principal.getSubEdOrgHierarchy().contains(edOrgId);
        } else if (complexValidationMap.containsKey(entityType)) {
            ComplexValidation validation = complexValidationMap.get(entityType);
            EntityDefinition definition = store.lookupByEntityType(validation.getValidationReferenceType());
            String id = (String) entityBody.get(validation.getValidationReferenceName());
            if (id != null) {
                final Entity entity = repo.findById(definition.getStoredCollectionName(), id);
                if (entity != null) {
                	isValid = isEntityValidForEdOrgWrite(entity.getBody(), definition.getType(), principal);
                } else {
                	isValid = false;
                }
            }
        }
        return isValid;
    }

    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
