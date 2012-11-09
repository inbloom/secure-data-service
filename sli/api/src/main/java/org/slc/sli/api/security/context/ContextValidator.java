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
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.validator.GenericContextValidator;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.security.context.validator.TeacherToStudentValidator;
import org.slc.sli.api.security.context.validator.TeacherToSubStudentEntityValidator;
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        validators = new ArrayList<IContextValidator>();
        validators.addAll(applicationContext.getBeansOfType(IContextValidator.class).values());

        IContextValidator genVal = null;
        IContextValidator studentVal = null;
        IContextValidator subEntityVal = null;
        // Make GenericContextValidator last, since we want to use that as a last resort
        for (IContextValidator validator : validators) {
            if (validator instanceof GenericContextValidator) {
                genVal = validator;
            } else if (validator instanceof TeacherToStudentValidator) {
                studentVal = validator;
            } else if (validator instanceof TeacherToSubStudentEntityValidator) {
                subEntityVal = validator;
            }
        }

        // move generic validator to end
        validators.remove(genVal);
        validators.add(genVal);

        // temporarily disable teacher-student validator
        // temporarily disable teacher-sub-student entity validator
        validators.remove(studentVal);
        validators.remove(subEntityVal);
    }

    public void validateContextToUri(ContainerRequest request, SLIPrincipal principal) {
        validateUserHasAccessToEndpoint(request, principal);
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
        /**
         * If we are v1/entity/id and the entity is "public" don't validate
         */
        if (segs.size() == 3 || (segs.size() == 4 && segs.get(3).getPath().equals("custom"))) {
            if (def.getStoredCollectionName().equals(EntityNames.EDUCATION_ORGANIZATION)) {
                info("Not validating access to public entity and it's custom data");
                return;
            }
        }

        /*
         * e.g.
         * !isTransitive - /v1/staff/<ID>/disciplineActions
         * isTransitive - /v1/staff/<ID>
         */
        boolean isTransitive = segs.size() < 4;
        String idsString = segs.get(2).getPath();
        Set<String> ids = new HashSet<String>(Arrays.asList(idsString.split(",")));
        validateContextToEntities(def, ids, isTransitive);
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
                } else {
                    idsToValidate.add(ent.getEntityId());
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

    private void validateUserHasAccessToEndpoint(ContainerRequest request, SLIPrincipal principal) {
        // TODO replace stub
        // make data driven from v1_resource
        // each resource will have an accessibleBy key with an array value, listing each of the user
        // types that can accesses the resource
        // example accessibleBy: ['teacher', 'staff']
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
