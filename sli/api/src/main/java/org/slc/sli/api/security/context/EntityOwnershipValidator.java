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
package org.slc.sli.api.security.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

@Component
public class EntityOwnershipValidator {

    private Set<String> globalEntities;
    private Map<String, Reference> typeToReference = new HashMap<String, EntityOwnershipValidator.Reference>();

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    private static class Reference {
        // R2L = Right hand side entity contains reference to entity on left hand side
        // L2R = Left hand side entity contains reference to entity on right hand side
        public enum RefType {
            LEFT_TO_RIGHT, RIGHT_TO_LEFT
        };

        String fromType = null;
        String toType = null;
        RefType type = null;
        String refField = null;

        public Reference(String fromType, String toType, String refField, RefType refType) {
            this.fromType = fromType;
            this.toType = toType;
            this.type = refType;
            this.refField = refField;
        }

    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {

        typeToReference
                .put(EntityNames.STUDENT, new Reference(EntityNames.STUDENT, EntityNames.STUDENT_SCHOOL_ASSOCIATION,
                        ParameterConstants.STUDENT_ID, Reference.RefType.RIGHT_TO_LEFT));
        typeToReference.put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, new Reference(
                EntityNames.STUDENT_SCHOOL_ASSOCIATION, EntityNames.EDUCATION_ORGANIZATION,
                ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADE, new Reference(EntityNames.GRADE,
                EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_SECTION_ASSOCIATION, new Reference(
                EntityNames.STUDENT_SECTION_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.ATTENDANCE, new Reference(EntityNames.ATTENDANCE, EntityNames.SCHOOL,
                ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COHORT, new Reference(EntityNames.COHORT, EntityNames.EDUCATION_ORGANIZATION,
                "educationOrgId", Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.COURSE_TRANSCRIPT, new Reference(EntityNames.COURSE_TRANSCRIPT,
                EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.DISCIPLINE_INCIDENT, new Reference(EntityNames.DISCIPLINE_INCIDENT,
                EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.DISCIPLINE_ACTION, new Reference(EntityNames.DISCIPLINE_ACTION,
                EntityNames.SCHOOL, "responsibilitySchoolId", Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADEBOOK_ENTRY, new Reference(EntityNames.GRADEBOOK_ENTRY,
                EntityNames.SECTION, ParameterConstants.SECTION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.GRADUATION_PLAN, new Reference(EntityNames.GRADUATION_PLAN,
                EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.PARENT, new Reference(EntityNames.PARENT,
                EntityNames.STUDENT_PARENT_ASSOCIATION, ParameterConstants.PARENT_ID, Reference.RefType.RIGHT_TO_LEFT));
        typeToReference.put(EntityNames.REPORT_CARD, new Reference(EntityNames.REPORT_CARD, EntityNames.STUDENT,
                ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.SECTION, new Reference(EntityNames.SECTION, EntityNames.SCHOOL,
                ParameterConstants.SCHOOL_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF, new Reference(EntityNames.STAFF, EntityNames.STAFF_ED_ORG_ASSOCIATION,
                ParameterConstants.STAFF_REFERENCE, Reference.RefType.RIGHT_TO_LEFT));
        typeToReference.put(EntityNames.STAFF_ED_ORG_ASSOCIATION, new Reference(EntityNames.STAFF_ED_ORG_ASSOCIATION,
                EntityNames.EDUCATION_ORGANIZATION, ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF_COHORT_ASSOCIATION, new Reference(EntityNames.STAFF_COHORT_ASSOCIATION,
                EntityNames.STAFF, ParameterConstants.STAFF_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STAFF_PROGRAM_ASSOCIATION, new Reference(EntityNames.STAFF_PROGRAM_ASSOCIATION,
                EntityNames.STAFF, ParameterConstants.STAFF_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ACADEMIC_RECORD, new Reference(EntityNames.STUDENT_ACADEMIC_RECORD,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_ASSESSMENT, new Reference(EntityNames.STUDENT_ASSESSMENT,
                EntityNames.STUDENT, ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COHORT_ASSOCIATION, new Reference(
                EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COMPETENCY, new Reference(EntityNames.STUDENT_COMPETENCY,
                EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_COMPETENCY_OBJECTIVE, new Reference(
                EntityNames.STUDENT_COMPETENCY_OBJECTIVE, EntityNames.EDUCATION_ORGANIZATION,
                ParameterConstants.EDUCATION_ORGANIZATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new Reference(
                EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, EntityNames.STUDENT,
                ParameterConstants.STUDENT_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_PARENT_ASSOCIATION, new Reference(
                EntityNames.STUDENT_PARENT_ASSOCIATION, EntityNames.STUDENT, ParameterConstants.STUDENT_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_GRADEBOOK_ENTRY, new Reference(EntityNames.STUDENT_GRADEBOOK_ENTRY,
                EntityNames.GRADEBOOK_ENTRY, ParameterConstants.GRADEBOOK_ENTRY_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, new Reference(
                EntityNames.STUDENT_PROGRAM_ASSOCIATION, EntityNames.EDUCATION_ORGANIZATION,
                ParameterConstants.EDUCATION_ORGANIZATION_ID, Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER_SECTION_ASSOCIATION, new Reference(
                EntityNames.TEACHER_SECTION_ASSOCIATION, EntityNames.STAFF, ParameterConstants.TEACHER_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new Reference(
                EntityNames.TEACHER_SCHOOL_ASSOCIATION, EntityNames.SCHOOL, ParameterConstants.SCHOOL_ID,
                Reference.RefType.LEFT_TO_RIGHT));
        typeToReference.put(EntityNames.TEACHER, new Reference(EntityNames.TEACHER,
                EntityNames.STAFF_ED_ORG_ASSOCIATION, ParameterConstants.STAFF_REFERENCE,
                Reference.RefType.RIGHT_TO_LEFT));

        globalEntities = new HashSet<String>(Arrays.asList(EntityNames.ASSESSMENT,
                EntityNames.COMPETENCY_LEVEL_DESCRIPTOR, EntityNames.EDUCATION_ORGANIZATION, EntityNames.SCHOOL,
                EntityNames.LEARNING_OBJECTIVE, EntityNames.LEARNING_STANDARD, EntityNames.PROGRAM,
                EntityNames.GRADING_PERIOD, EntityNames.SESSION, EntityNames.COURSE, EntityNames.COURSE_OFFERING,
                "stateEducationAgency", "localEducationAgency"));

    }

    /**
     * Determines if the requested entity can be accessed. The implicit assumption in using this
     * function is that access to the entity requested is transitive (only to the entity requested,
     * and not through the entity). If this is not the case, use the canAccess method that takes the
     * isTransitive flag as an argument.
     *
     * @param entity
     *            Requested entity.
     * @return True if the requested entity can be accessed, false otherwise.
     */
    public boolean canAccess(Entity entity) {
        return canAccess(entity, true);
    }

    /**
     * Determines if the requested entity can be accessed in the specified transitive or
     * non-transitive way.
     *
     * @param entity
     *            Requested entity.
     * @param isTransitive
     *            Flag used for specifying transitive vs. non-transitive access to the entity.
     * @return True if the requested entity can be accessed, false otherwise.
     */
    public boolean canAccess(Entity entity, boolean isTransitive) {
        if (SecurityUtil.getSLIPrincipal().getAuthorizingEdOrgs() == null) {
            // explicitly set null if the app is marked as authorized_for_all_edorgs
            return true;
        }

        if (Arrays.asList(EntityNames.PROGRAM, EntityNames.SESSION).contains(entity.getType())) {
            //  Some entities are just cannot be pnwed
            return true;
        }

        if (isTransitive && globalEntities.contains(entity.getType())) {
            debug("skipping ownership validation --> transitive access to global entity: {}", entity.getType());
            return true;
        }

        Set<String> owningEdorgs = lookupEdorgs(Arrays.asList(entity), entity.getType());
        if (owningEdorgs.size() == 0) {
            warn("Potentially bad data found.");
            return true;
        }

        for (String edOrgId : owningEdorgs) {
            if (SecurityUtil.getSLIPrincipal().getAuthorizingEdOrgs().contains(edOrgId)) {
                debug("discovered owning education organization: {}", edOrgId);
                return true;
            }
        }
        return false;
    }

    /**
     * Look up the edorgs that can access the given entities.
     *
     * @param entities
     * @param entityType
     * @return a Set of edorg IDs
     */
    private Set<String> lookupEdorgs(Iterable<Entity> entities, String entityType) {
        Set<String> edorgs = new HashSet<String>();
        debug("checking ownership for entities of type: {}", entityType);

        if (isEducationOrganization(entityType)) {
            // No need to do an actual mongo lookup since we have the IDs we need
            for (Entity entity : entities) {
                edorgs.add(entity.getEntityId());
            }
        } else {
            Reference ref = typeToReference.get(entityType);
            if (ref == null) {
                warn("Cannot handle ownership for entity type {}.", entityType);
                throw new RuntimeException("No ownership for " + entityType);
            }

            for (Entity entity : entities) {
                EntityDefinition definition = store.lookupByEntityType(ref.toType);
                String collectionName = definition.getStoredCollectionName();
                String critField = null;
                Object critValue = null;
                if (ref.type == Reference.RefType.LEFT_TO_RIGHT) {
                    critField = ParameterConstants.ID;
                    critValue = entity.getBody().get(ref.refField);
                } else { // RIGHT_TO_LEFT
                    critField = ref.refField;
                    critValue = entity.getEntityId();
                }

                Iterable<Entity> ents = repo.findAll(collectionName, new NeutralQuery(new NeutralCriteria(critField,
                        NeutralCriteria.OPERATOR_EQUAL, critValue)));
                if (ents.iterator().hasNext()) {
                    Set<String> toAdd = lookupEdorgs(ents, collectionName);
                    edorgs.addAll(toAdd);
                } else {
                    throw new AccessDeniedException("Could not find a matching " + collectionName + " where "
                            + critField + " is " + critValue + ".");
                }
            }
        }

        return edorgs;
    }

    /**
     * Determines if the specified entity type represents an education organization.
     *
     * @param type
     *            Type of Entity to be checked.
     * @return True if the specified entity type is an education organization, false otherwise.
     */
    private boolean isEducationOrganization(String type) {
        return type.equals(EntityNames.EDUCATION_ORGANIZATION) || type.equals(EntityNames.SCHOOL)
                || type.equals("localEducationAgency") || type.equals("stateEducationAgency");
    }
}
