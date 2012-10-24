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
import java.util.Collection;
import java.util.List;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * ContextValidator
 * Determines if the principal has context to a resource.
 * Verifies the requested endpoint is accessible by the principal
 */
@Component
public class ContextValidator implements ApplicationContextAware {
    
    private Collection<IContextValidator> validators;
    
    @Autowired
    private ResourceHelper resourceHelper;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        validators = applicationContext.getBeansOfType(IContextValidator.class).values();  
    }

    public void validateContextToUri(ContainerRequest request, SLIPrincipal principal) {
        validateUserHasAccessToEndpoint(request, principal);
        validateUserHasContextToRequestedEntities(request, principal);
    }

    private void validateUserHasContextToRequestedEntities(ContainerRequest request, SLIPrincipal principal) {
   
        if (request.getPathSegments().size() < 3) {
            return;
        }

        String rootEntity = request.getPathSegments().get(1).getPath();
        EntityDefinition def = resourceHelper.getEntityDefinition(rootEntity);
        if (def == null) //e.g. home resource
            return;
        String entityName = def.getType();
        
        /*
         * e.g. 
         * through - /v1/staff/<ID>/disciplineActions
         * !through - /v1/staff/<ID>
         */
        boolean through = request.getPathSegments().size() > 3;
        IContextValidator validator = findValidator(entityName, through);
        if (validator != null) {
            String idsString = request.getPathSegments().get(2).getPath();
            List<String> ids = Arrays.asList(idsString.split(","));
            if (!validator.validate(ids)) {
                if (!exists(ids, def.getStoredCollectionName())) {
                    throw new EntityNotFoundException("Could not locate " + entityName + "with ids " + idsString);
                }
                throw new AccessDeniedException("Cannot access entities " + idsString);
            }
        }
    }

    private boolean exists(List<String> ids, String collectionName) {
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));
        return repo.count(collectionName, query) == ids.size();
    }

    private void validateUserHasAccessToEndpoint(ContainerRequest request, SLIPrincipal principal) {
        //TODO replace stub
        // make data driven from v1_resource
        // each resource will have an accessibleBy key with an array value, listing each of the user types that can accesses the resource
        // example accessibleBy: ['teacher', 'staff']

    }

    /**
     * 
     * @param toType
     * @param through
     * @return
     * @throws IllegalStateException
     */
    private IContextValidator findValidator(String toType, boolean through) throws IllegalStateException {
        
        IContextValidator found = null;
        for (IContextValidator validator : this.validators) {
            if (validator.canValidate(toType, through)) {
                found = validator;
                break;
            }
        }
        
        if (found == null) {
            warn("No {} validator from to {}.", through ? "through": "non-through", toType);
        }

        return found;
    }

}