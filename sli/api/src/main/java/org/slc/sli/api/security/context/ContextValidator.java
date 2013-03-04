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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.PathSegment;

import com.sun.jersey.spi.container.ContainerRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.util.SecurityUtil;
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

    private List<IContextValidator> validators;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    @Autowired
    private EntityOwnershipValidator ownership;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        validators = new ArrayList<IContextValidator>();
        validators.addAll(applicationContext.getBeansOfType(IContextValidator.class).values());
    }

    public void validateContextToUri(ContainerRequest request, SLIPrincipal principal) {
        validateUserHasContextToRequestedEntities(request, principal);
    }

    private void validateUserHasContextToRequestedEntities(ContainerRequest request, SLIPrincipal principal) {

        List<PathSegment> segs = request.getPathSegments();
        for (Iterator<PathSegment> i = segs.iterator(); i.hasNext();) {
            if (i.next().getPath().isEmpty()) {
                i.remove();
            }
        }

        if (segs.size() < 3) {
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
         * isTransitive - /v1/staff/<ID>
         */
        boolean isTransitive = segs.size() < 4;

        /**
         * If we are v1/entity/id and the entity is "public" don't validate
         *
         * Unless of course you're posting/putting/deleting, blah blah blah.
         */
        if (segs.size() == 3 || (segs.size() == 4 && segs.get(3).getPath().equals("custom"))) {
            if (def.getStoredCollectionName().equals(EntityNames.EDUCATION_ORGANIZATION)) {
                if (!request.getMethod().equals("GET")) {
                    isTransitive = false;
                } else {
                    info("Not validating access to public entity and it's custom data");
                    return;
                }
            }
        }

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
            throw new AccessDeniedException("Cannot access endpoint.");
        }
    }

    /**
     * Check for the path segments to match the following patterns:
     * /disciplineIncidents/{ids}/studentDisciplineIncidentAssociations,
     * /disciplineIncidents/{ids}/studentDisciplineIncidentAssociations/students
     *
     * @param segments
     *            List of Path Segments representing API call.
     * @return true if the URI exactly matches one of the enumerated patterns, false otherwise.
     */
    public boolean checkAccessOfStudentsThroughDisciplineIncidents(List<PathSegment> segments) {
        if (segments.size() == 4) {
            return segments.get(1).getPath().equals(ResourceNames.DISCIPLINE_INCIDENTS)
                    && segments.get(3).getPath().equals(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS);
        } else if (segments.size() == 5) {
            return segments.get(1).getPath().equals(ResourceNames.DISCIPLINE_INCIDENTS)
                    && segments.get(3).getPath().equals(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS)
                    && segments.get(4).getPath().equals(ResourceNames.STUDENTS);
        }
        return false;
    }

    public void validateContextToEntities(EntityDefinition def, Collection<String> ids, boolean isTransitive) {

        IContextValidator validator = findValidator(def.getType(), isTransitive);
        if (validator != null) {
            Set<String> idsToValidate = new HashSet<String>();
            NeutralQuery getIdsQuery = new NeutralQuery(new NeutralCriteria("_id", "in", new ArrayList<String>(ids)));
            int found = 0;
            for (Entity ent : repo.findAll(def.getStoredCollectionName(), getIdsQuery)) {
                found++;
                if (SecurityUtil.principalId().equals(ent.getMetaData().get("createdBy"))
                        && "true".equals(ent.getMetaData().get("isOrphaned"))) {
                    debug("Entity is orphaned: id {} of type {}", ent.getEntityId(), ent.getType());
                } else if (SecurityUtil.getSLIPrincipal().getEntity() != null
                        && SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(ent.getEntityId())) {
                    debug("Entity is themselves: id {} of type {}", ent.getEntityId(), ent.getType());
                } else {
                    if (ownership.canAccess(ent)) {
                        idsToValidate.add(ent.getEntityId());
                    } else {
                        throw new AccessDeniedException("Access to " + ent.getEntityId() + " is not authorized");
                    }
                }
            }

            if (found != ids.size()) {
                debug("Invalid reference, an entity does not exist. collection: {} ids: {}",
                        def.getStoredCollectionName(), ids);
                throw new EntityNotFoundException("Could not locate " + def.getType() + " with ids " + ids);
            }

            if (!idsToValidate.isEmpty()) {
                if (!validator.validate(def.getType(), idsToValidate)) {
                    throw new AccessDeniedException("Cannot access entities " + ids);
                }
            }
        } else {
            throw new AccessDeniedException("No validator for " + def.getType() + ", transitive=" + isTransitive);
        }
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

}
