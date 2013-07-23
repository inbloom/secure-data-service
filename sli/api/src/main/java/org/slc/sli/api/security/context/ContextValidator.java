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

import java.util.*;

import javax.ws.rs.core.PathSegment;

import com.sun.jersey.spi.container.ContainerRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.common.inject.matcher.Matcher;
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
            "childLearningObjectives"));

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

    private List<PathSegment> cleanEmptySegments(List<PathSegment> pathSegments) {
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
            debug("Call to {} is of global context, skipping validation", request.getAbsolutePath().toString());
            return;
        }

        String rootEntity = segs.get(1).getPath();

        EntityDefinition def = resourceHelper.getEntityDefinition(rootEntity);
        if (def == null || def.skipContextValidation()) {
            return;
        }


        boolean isTransitive =  isTransitive(segs);

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

    public void validateContextToEntities(EntityDefinition def, Collection<String> ids, boolean isTransitive) {

        IContextValidator validator = findValidator(def.getType(), isTransitive);
        if (validator != null) {
            NeutralQuery getIdsQuery = new NeutralQuery(new NeutralCriteria("_id", "in", new ArrayList<String>(ids)));
            Collection<Entity> entities = (Collection<Entity>) repo.findAll(def.getStoredCollectionName(), getIdsQuery);
            validateContextToEntities(def, entities, isTransitive, validator);
        }    else {
            throw new APIAccessDeniedException("No validator for " + def.getType() + ", transitive=" + isTransitive, def.getType(), ids);
        }
    }

    public Map<Entity, String> validateContextToEntitiesNew(EntityDefinition def, Collection<Entity> entities, boolean isTransitive) {

        List<IContextValidator> contextValidators = findContextualValidator(def.getType(), isTransitive);

        Map<Entity, String> entityContexts = new HashMap<Entity, String>();

        for (IContextValidator validator : contextValidators) {
           try {
               Set<Entity> entitiesValidated = validateContextToEntities(def, entities, isTransitive, validator);
               contexts.add(validator.getContext());
               for(Entity ent : entitiesValidated) {
                   if (entityContexts.containsKey(id)) {
                       entityContexts.put(ent, SecurityUtil.NULL_CONTEXT);
                   } else {
                       entityContexts.put(ent, validator.getContext());
                   }
               }
           } catch (Exception e) {
               error(e.getMessage());
           }
        }

        if (entityContexts.size() == 0) {
            throw new APIAccessDeniedException("No validator for " + def.getType() + ", transitive=" + isTransitive, def.getType(), entity.toString());
        }

        return entityContexts;
    }

    protected Set<Entity> validateContextToEntities(EntityDefinition def, Collection<Entity> entities, boolean isTransitive, IContextValidator validator) {
         int found = 0;
         Map<String, Entity> idsToValidate = new HashMap<String, Entity>();
            for (Entity ent : entities) {
                found++;
                Collection<String> userEdOrgs = edOrgHelper.getDirectEdorgs( ent );
                if (SecurityUtil.principalId().equals(ent.getMetaData().get("createdBy"))
                        && "true".equals(ent.getMetaData().get("isOrphaned"))) {
                    debug("Entity is orphaned: id {} of type {}", ent.getEntityId(), ent.getType());
                } else if (SecurityUtil.getSLIPrincipal().getEntity() != null
                        && SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(ent.getEntityId())) {
                    debug("Entity is themselves: id {} of type {}", ent.getEntityId(), ent.getType());
                } else {
                    if (ownership.canAccess(ent, isTransitive)) {
                        idsToValidate.put(ent.getEntityId(), ent);
                    } else {
                        throw new APIAccessDeniedException("Access to " + ent.getEntityId() + " is not authorized",
                            userEdOrgs);
                    }
                }
            }

            if (found != entities.size()) {
                debug("Invalid reference, an entity does not exist. collection: {} entities: {}",
                        def.getStoredCollectionName(), entities);
                throw new EntityNotFoundException("Could not locate " + def.getType() + " with ids " + entities);
            }

            Set<Entity> validatedEntities = new HashSet<Entity>();
            if (!idsToValidate.isEmpty()) {
                Set<String> validatedIds = validator.validate(def.getType(), idsToValidate.keySet());
                for(String id : validatedIds) {
                    validatedEntities.add(idsToValidate.get(id));
                }
            }

         return validatedEntities;
       }

    /**
     *
     * @param toType
     * @param isTransitive
     * @return
     * @throws IllegalStateException
     */
    public IContextValidator findValidator(String toType, boolean isTransitive) throws IllegalStateException {

        IContextValidator found = null;
        for (IContextValidator validator : this.validators) {
            if (validator.canValidate(toType, isTransitive)) {
                info("Using {} to validate {}", new Object[] { validator.getClass().toString(), toType });
                found = validator;
                break;
            }
        }

        if (found == null) {
            warn("No {} validator to {}.", isTransitive ? "TRANSITIVE" : "NOT TRANSITIVE", toType);
        }

        return found;
    }

    public List<IContextValidator> findContextualValidator(String toType, boolean isTransitive) throws IllegalStateException {

        List<IContextValidator> validators = new ArrayList<IContextValidator>();
        for (IContextValidator validator : this.validators) {
            if (validator.canValidate(toType, isTransitive)) {
                info("Using {} to validate {}", new Object[] { validator.getClass().toString(), toType });
                validators.add(validator);
            }
        }

        if (validators.isEmpty()) {
            warn("No {} validator to {}.", isTransitive ? "TRANSITIVE" : "NOT TRANSITIVE", toType);
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

    public static boolean isTransitive(List<PathSegment> segs) {
                /*
         * e.g.
         * !isTransitive - /v1/staff/<ID>/disciplineActions
         * isTransitive - /v1/staff/<ID> OR /v1/staff/<ID>/custom
         */
        return segs.size() == 3
                || (segs.size() == 4 && segs.get(3).getPath().equals(ResourceNames.CUSTOM));
    }

    public List<IContextValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<IContextValidator> validators) {
        this.validators = validators;
    }
    
}
