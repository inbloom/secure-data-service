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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.PathSegment;

import com.sun.jersey.spi.container.ContainerRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * ContextValidator
 * Determines if the principal has context to a resource.
 * Verifies the requested endpoint is accessible by the principal
 */
@Component
public class ContextValidator implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ContextValidator.class);

    protected static final Set<String> GLOBAL_RESOURCES = new HashSet<String>(
            Arrays.asList(ResourceNames.ASSESSMENTS,
            ResourceNames.CALENDAR_DATES,
            ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS,
            ResourceNames.COURSES,
            ResourceNames.COURSE_OFFERINGS,
            ResourceNames.EDUCATION_ORGANIZATIONS,
            ResourceNames.GRADUATION_PLANS,
            ResourceNames.GRADING_PERIODS,
            ResourceNames.LEARNINGOBJECTIVES,
            ResourceNames.LEARNINGSTANDARDS,
            ResourceNames.PROGRAMS,
            ResourceNames.SCHOOLS,
            ResourceNames.SECTIONS,
            ResourceNames.SESSIONS,
            ResourceNames.STUDENT_COMPETENCY_OBJECTIVES,
            ResourceNames.CUSTOM,
            "parentLearningObjectives",
            "childLearningObjectives",
            ResourceNames.CLASS_PERIODS,
            ResourceNames.BELL_SCHEDULES));

    private List<IContextValidator> validators;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private EntityOwnershipValidator ownership;

    @Autowired
    private StudentAccessValidator studentAccessValidator;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private ParentAccessValidator parentAccessValidator;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        validators = new ArrayList<IContextValidator>();
        validators.addAll(applicationContext.getBeansOfType(IContextValidator.class).values());
    }

    public void validateContextToUri(ContainerRequest request, SLIPrincipal principal) {
        validateUserHasContextToRequestedEntities(request, principal);
    }

    /**
     * white list student accessible URL. Can't do it in validateUserHasContextToRequestedEntity
     * because we must also block some url that only has 2 segment, i.e.
     * disciplineActions/disciplineIncidents
     *
     * @param request
     * @return if url is accessible to students principals
     */
    public boolean isUrlBlocked(ContainerRequest request) {
        List<PathSegment> segs = cleanEmptySegments(request.getPathSegments());

        if (isSystemCall(segs)) {
            // do not block system calls
            return false;
        }

        if (SecurityUtil.isStudent()) {
            return !studentAccessValidator.isAllowed(request);
        } else if (SecurityUtil.isParent()) {
            return !parentAccessValidator.isAllowed(request);
        }

        return false;
    }

    private boolean isSystemCall(List<PathSegment> pathSegments) {
        /**
         * assuming all resource endpoints are versioned
         */
        if (pathSegments == null || pathSegments.size() == 0) {
            // /api/rest/ root access?
            return false;
        }

        // all data model resources are versioned
        if (isVersionString(pathSegments.get(0).getPath())) {
            return false;
        }

        return true;
    }

    private boolean isVersionString(String path) {
        if (path != null && path.startsWith("v")) {
            return NumberUtils.isNumber(path.substring(1));
        }
        return false;
    }

    public List<PathSegment> cleanEmptySegments(List<PathSegment> pathSegments) {
        for (Iterator<PathSegment> i = pathSegments.iterator(); i.hasNext();) {
            if (i.next().getPath().isEmpty()) {
                i.remove();
            }
        }

        return pathSegments;
    }

    private void validateUserHasContextToRequestedEntities(ContainerRequest request, SLIPrincipal principal) {

        List<PathSegment> segs = request.getPathSegments();
        segs = cleanEmptySegments(segs);

        if (segs.size() < 3) {
            return;
        }

        /*
         * If the URI being requested is a GET full of global entities, we do
         * not need to attempt validation Global entities include: ASSESSMENT,
         * LEARNING_OBJECTIVE, LEARNING_STANDARD, COMPETENCY_LEVEL_DESCRIPTOR,
         * SESSION, COURSE_OFFERING, GRADING_PERIOD, COURSE,
         * EDUCATION_ORGANIZATION, SCHOOL, SECITON, PROGRAM, GRADUATION_PLAN,
         * STUDENT_COMPETENCY_OBJECTIVE, and CUSTOM (custom entity exists under
         * another entity, they should not prevent classification of a call
         * being global)
         */
        boolean isGlobal = true;
        for (PathSegment seg : segs) {
            // First segment is always API version, skip it
            // Third segment is always the ID, skip it
            if (seg.equals(segs.get(0)) || seg.equals(segs.get(2))) {
                continue;
            }
            // Check if the segment is not global, if so break
            if (!GLOBAL_RESOURCES.contains(seg.getPath())) {
                isGlobal = false;
                break;
            }
        }
        // Only skip validation if method is a get, updates may still require
        // validation
        if (isGlobal && request.getMethod().equals("GET")) {
            // The entity has global context, just return and don't call the
            // validators
            LOG.debug("Call to {} is of global context, skipping validation", request.getAbsolutePath().toString());
            return;
        }

        String rootEntity = segs.get(1).getPath();

        EntityDefinition def = resourceHelper.getEntityDefinition(rootEntity);
        if (def == null || def.skipContextValidation()) {
            return;
        }

        /*
         * e.g.
         * !isTransitive - /v1/staff/<ID>/disciplineActions
         * isTransitive - /v1/staff/<ID> OR /v1/staff/<ID>/custom
         */
        boolean isTransitive = segs.size() == 3
                || (segs.size() == 4 && segs.get(3).getPath().equals(ResourceNames.CUSTOM));

        validateContextToCallUri(segs);
        String idsString = segs.get(2).getPath();
        Set<String> ids = new HashSet<String>(Arrays.asList(idsString.split(",")));
        validateContextToEntities(def, ids, isTransitive);
    }

    /**
     * Validates the principal's context to call the given URI.
     *
     * @param segments
     *            List of Path Segments representing API call.
     */
    public void validateContextToCallUri(List<PathSegment> segments) {
        if (SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.TEACHER)
                && checkAccessOfStudentsThroughDisciplineIncidents(segments)) {
            throw new APIAccessDeniedException("Cannot access endpoint.");
        }
    }

    /**
     * Check for the path segments to match the following patterns:
     * /disciplineIncidents/{ids}/studentDisciplineIncidentAssociations,
     * /disciplineIncidents/{ids}/studentDisciplineIncidentAssociations/students
     *
     * @param segments
     *            List of Path Segments representing API call.
     * @return true if the URI exactly matches one of the enumerated patterns,
     *         false otherwise.
     */
    public boolean checkAccessOfStudentsThroughDisciplineIncidents(List<PathSegment> segments) {
        /*
         * Both /disciplineIncidents/{ids}/studentDisciplineIncidentAssociations and
         * /disciplineIncidents/{ids}/studentDisciplineIncidentAssociations/students
         * have the same pattern (first segment of discipline incident, third of
         * the association, so just check for those two conditions
         */
        if (segments.size() > 3) {
            return segments.get(1).getPath().equals(ResourceNames.DISCIPLINE_INCIDENTS)
                    && segments.get(3).getPath().equals(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS);
        }
        return false; // Num segments is 3, just return
    }

    /**
    * Validates entities, based upon entity definition and entity ids to validate.
    *
    * @param def - Definition of entities to validate
    * @param ids - Collection of ids of entities to validate
    * @param isTransitive - Determines whether validation is through another entity type
    *
    * @throws APIAccessDeniedException - When entities cannot be accessed
    */
    public void validateContextToEntities(EntityDefinition def, Collection<String> ids, boolean isTransitive) throws APIAccessDeniedException {
        IContextValidator validator = findValidator(def.getType(), isTransitive);
        if (validator != null) {
            NeutralQuery getIdsQuery = new NeutralQuery(new NeutralCriteria("_id", "in", new ArrayList<String>(ids)));
            Collection<Entity> entities = (Collection<Entity>) repo.findAll(def.getStoredCollectionName(), getIdsQuery);
            Set<String> idsToValidate = getEntityIdsToValidate(def, entities, isTransitive, ids);
            Set<String> validatedIds = getValidatedIds(def, idsToValidate, validator);
            if (!validatedIds.containsAll(idsToValidate)) {
                throw new APIAccessDeniedException("Cannot access entities", def.getType(), idsToValidate);
            }
        } else {
            throw new APIAccessDeniedException("No validator for " + def.getType() + ", transitive=" + isTransitive, def.getType(), ids);
        }
    }

    /**
     * Validates entities, based upon entity definition and entity ids to validate.
     *
     * @param def - Definition of entities to validate
     * @param ids - Collection of ids of entities to validate
     * @param isTransitive - Determines whether validation is through another entity type
     *
     * @throws APIAccessDeniedException - When entities cannot be accessed
     */
    public Set<String> getValidIdsIncludeOrphans(EntityDefinition def, Set<String> ids, boolean isTransitive) throws APIAccessDeniedException {
        IContextValidator validator = findValidator(def.getType(), isTransitive);
        if (validator != null) {
            NeutralQuery getIdsQuery = new NeutralQuery(new NeutralCriteria("_id", "in", new ArrayList<String>(ids)));
            Collection<Entity> entities = (Collection<Entity>) repo.findAll(def.getStoredCollectionName(), getIdsQuery);
            Set<String> orphans = new HashSet<String>();
            for (Entity entity: entities) {
                if (isOrphanCreatedByUser(entity)) {
                    orphans.add(entity.getEntityId());
                }
            }
            Set<String> nonOrphanIds = new HashSet<String>();
            nonOrphanIds.addAll(ids);
            nonOrphanIds.removeAll(orphans);
            Set<String> validatedIds = validator.getValid(def.getType(), nonOrphanIds);

            validatedIds.addAll(orphans);
            return validatedIds;
        } else {
            throw new APIAccessDeniedException("No validator for " + def.getType() + ", transitive=" + isTransitive, def.getType(), ids);
        }
    }

    /**
    * Returns a map of validated entity ids and their contexts, based upon entity definition and entities to validate.
    *
    * @param def - Definition of entities to validate
    * @param entities - Collection of entities to validate
    * @param isTransitive - Determines whether validation is through another entity type
    *
    * @return - Map of validated entity ids and their contexts, null if the validation is skipped
    *
    * @throws APIAccessDeniedException - When no entity validators can be found
    */
    public Map<String, SecurityUtil.UserContext> getValidatedEntityContexts(EntityDefinition def, Collection<Entity> entities, boolean isTransitive, boolean isRead) throws APIAccessDeniedException {

        if (skipValidation(def, isRead)) {
            return null;
        }

        Map<String, SecurityUtil.UserContext> entityContexts = new HashMap<String, SecurityUtil.UserContext>();

        List<IContextValidator> contextValidators = findContextualValidators(def.getType(), isTransitive);
        Collection<String> ids = getEntityIds(entities);
        if (!contextValidators.isEmpty()) {
            Set<String> idsToValidate = getEntityIdsToValidateForgiving(entities, isTransitive);
            for (IContextValidator validator : contextValidators) {
                // Add validated entity ids to the map.
                Set<String> validatedIds = getValidatedIds(def, idsToValidate, validator);
                for (String id : validatedIds) {
                    if (!entityContexts.containsKey(id)) {
                        entityContexts.put(id, validator.getContext());
                    } else if ((entityContexts.get(id).equals(SecurityUtil.UserContext.STAFF_CONTEXT)) && (validator.getContext().equals(SecurityUtil.UserContext.TEACHER_CONTEXT)) ||
                               (entityContexts.get(id).equals(SecurityUtil.UserContext.TEACHER_CONTEXT)) && (validator.getContext().equals(SecurityUtil.UserContext.STAFF_CONTEXT))) {
                        entityContexts.put(id, SecurityUtil.UserContext.DUAL_CONTEXT);
                    }
                }
            }

            // Add accessible, non-validated entity ids (orphaned and owned/self) to the map.
            Set<String> accessibleNonValidatedIds = new HashSet<String>(ids);
            accessibleNonValidatedIds.removeAll(idsToValidate);
            for (String id : accessibleNonValidatedIds) {
                entityContexts.put(id, SecurityUtil.getUserContext());
            }
        } else {
            throw new APIAccessDeniedException("No validator for " + def.getType() + ", transitive=" + isTransitive, def.getType(), ids);
        }

        return entityContexts;
    }

    /**
    * Returns a set of entity ids to validate, based upon entity type and list of entities to filter for validation.
    *
    * @param def - Definition of entities to filter
    * @param entities - Collection of entities to filter for validation
    * @param isTransitive - Determines whether validation is through another entity type
    * @param ids - Original set of entity ids to validate
    *
    * @return - Set of entity ids to validate
    *
    * @throws APIAccessDeniedException - When an entity cannot be accessed
    * @throws EntityNotFoundException - When an entity cannot be located
    */
    protected Set<String> getEntityIdsToValidate(EntityDefinition def, Collection<Entity> entities, boolean isTransitive, Collection<String> ids)
                          throws APIAccessDeniedException, EntityNotFoundException {
        Set<String> entityIdsToValidate = new HashSet<String>();
        for (Entity ent : entities) {
            Collection<String> userEdOrgs = edOrgHelper.getDirectEdorgs(ent);
            if (isOrphanCreatedByUser(ent)) {
                LOG.debug("Entity is orphaned: id {} of type {}", ent.getEntityId(), ent.getType());
            } else if (SecurityUtil.getSLIPrincipal().getEntity() != null
                        && SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(ent.getEntityId())) {
                LOG.debug("Entity is themselves: id {} of type {}", ent.getEntityId(), ent.getType());
            } else {
                if (ownership.canAccess(ent, isTransitive)) {
                    entityIdsToValidate.add(ent.getEntityId());
                } else {
                    throw new APIAccessDeniedException("Access to " + ent.getEntityId() + " is not authorized", userEdOrgs);
                }
            }
        }

        // report an EntityNotFoundException on the id we find without a corresponding entity
        // so that we don't use the constructor for EntityNotFoundException incorrectly
        if (entities.size() != ids.size()) {
            for (String id : ids ) {
                boolean foundentity = false;
                for (Entity ent : entities) {
                    if (ent.getEntityId().contains(id)){
                    	foundentity = true;
                    	break;
                    }
                }
                if (!foundentity) {
                    LOG.debug("Invalid reference, an entity does not exist. collection: {} entities: {}",
                            def.getStoredCollectionName(), entities);
                    throw new EntityNotFoundException(id);
                }
            }
        }

        return entityIdsToValidate;
    }

    /**
     * Returns true if the entity is an orphan that is created by the user, false otherwise
     *
     * @param entity - Collection of entities to filter for validation
     *
     * @return
     */
    private boolean isOrphanCreatedByUser(Entity entity) {
         return SecurityUtil.principalId().equals(entity.getMetaData().get("createdBy"))
                    && "true".equals(entity.getMetaData().get("isOrphaned"));
        }

    /**
     * This method forgivingly iterate through the input entities, returns a set of entity ids to validate,
     * based upon entity type and list of entities to filter for validation.
     *
     *
     * @param entities - Collection of entities to filter for validation
     * @param isTransitive - Determines whether validation is through another entity type
     *
     * @return - Set of entity ids to validate
     */
    protected Set<String> getEntityIdsToValidateForgiving(Collection<Entity> entities, boolean isTransitive){
        Set<String> entityIdsToValidate = new HashSet<String>();
        for (Entity ent : entities) {
            if (isOrphanCreatedByUser(ent)) {
                LOG.debug("Entity is orphaned: id {} of type {}", ent.getEntityId(), ent.getType());
            } else if (SecurityUtil.getSLIPrincipal().getEntity() != null
                    && SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(ent.getEntityId())) {
                LOG.debug("Entity is themselves: id {} of type {}", ent.getEntityId(), ent.getType());
            } else {
                try{
                    if (ownership.canAccess(ent, isTransitive)) {
                        entityIdsToValidate.add(ent.getEntityId());
                    }
                } catch (AccessDeniedException aex) {
                    LOG.error(aex.getMessage());
                }
            }
        }

        return entityIdsToValidate;
    }

    /**
    * Returns a set of validated entity ids based upon entity type and list of entity ids to validate.
    *
    * @param def - Definition of entities to validate
    * @param idsToValidate - Collection of entity ids to validate
    * @param validator - Validator to use
    *
    * @return - Set of validated entities
    */
    protected Set<String> getValidatedIds(EntityDefinition def, Set<String> idsToValidate, IContextValidator validator) {
        Set<String> validatedIds = new HashSet<String>();
        if (!idsToValidate.isEmpty()) {
            validatedIds = validator.validate(def.getType(), idsToValidate);
        }

        return validatedIds;
    }

    /**
    * Returns a set of entity ids from a set of entities.
    *
    * @param entities - Collection of entities to validate
    *
    * @return - Set of entity ids
    */
    protected Set<String> getEntityIds(Collection<Entity> entities) {
        Set<String> entityIds = new HashSet<String>();
        for (Entity ent : entities) {
            entityIds.add(ent.getEntityId());
        }

        return entityIds;
    }

    /**
     * Returns a contextual validator based upon entity type.
     *
    * @param toType - Type of entity to validate
    * @param isTransitive - Determines whether validation is through another entity type
     *
     * @return - Validator applicable to given type
     *
     * @throws IllegalStateException - ???
     */
    public IContextValidator findValidator(String toType, boolean isTransitive) throws IllegalStateException {

        IContextValidator found = null;
        for (IContextValidator validator : this.validators) {
            if (validator.canValidate(toType, isTransitive)) {
                LOG.info("Using {} to validate {}", new Object[] { validator.getClass().toString(), toType });
                found = validator;
                break;
            }
        }

        if (found == null) {
            LOG.warn("No {} validator to {}.", isTransitive ? "TRANSITIVE" : "NOT TRANSITIVE", toType);
        }

        return found;
    }

    public static boolean isTransitive(List<PathSegment> segs) {
                /*
         * e.g.
         * !isTransitive - /v1/staff/<ID>/disciplineActions
         * isTransitive - /v1/staff/<ID> OR /v1/staff/<ID>/custom
         */
        return segs.size() == 3
                || (segs.size() == 4 && segs.get(3).getPath().equals(ResourceNames.CUSTOM));
    }

    /**
    * Returns a list of contextual validators based upon entity type.
    *
    * @param toType - Type of entity to validate
    * @param isTransitive - Determines whether validation is through another entity type
    *
    * @return - List of validators to use
    */
    public List<IContextValidator> findContextualValidators(String toType, boolean isTransitive) {

        List<IContextValidator> validators = new ArrayList<IContextValidator>();
        for (IContextValidator validator : this.validators) {
            if (validator.canValidate(toType, isTransitive)) {
                LOG.info("Using {} to validate {}", new Object[] { validator.getClass().toString(), toType });
                validators.add(validator);
            }
        }

        if (validators.isEmpty()) {
            LOG.warn("No {} validator to {}.", isTransitive ? "TRANSITIVE" : "NOT TRANSITIVE", toType);
        }

        return validators;
    }

    /**
     * Determines if the entity is global.
     *
     * @param type
     *            Type of entity to checked.
     * @return True if the entity is global, false otherwise.
     */
    public boolean isGlobalEntity(String type) {
        return EntityNames.isPublic(type);
    }

    private boolean skipValidation(EntityDefinition def, boolean isRead) {
        return def == null || def.skipContextValidation() || (GLOBAL_RESOURCES.contains(def.getResourceName()) && isRead);
    }

}
