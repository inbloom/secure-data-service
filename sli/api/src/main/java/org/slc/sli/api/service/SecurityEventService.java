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

package org.slc.sli.api.service;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.SecurityEventContextResolver;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom service for accessing the security event entity from the database.
 *
 * @author Andrew D. Ball
 * @since 2013-11-22
 */
@Scope("prototype")
@Component
public class SecurityEventService extends BasicService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityEventService.class);

    @Autowired
    SecurityEventContextResolver securityEventContextResolver;

    public SecurityEventService(String collectionName, List<Treatment> treatments, Repository<Entity> repo) {
        super(collectionName, treatments, repo);
    }

    private void checkWhetherUserHasAccessToSecurityEvent(String securityEventId) {
        List<String> accessibleIds = securityEventContextResolver.findAccessible(null);
        if (!accessibleIds.contains(securityEventId)) {
            throw new APIAccessDeniedException("Access denied");
        }
    }

    /**
     *
     * @param neutralQuery
     * @return
     */
    @Override
    public Iterable<EntityBody> list(NeutralQuery neutralQuery) {

        neutralQuery.setSortBy("timeStamp");
        neutralQuery.setSortOrder(NeutralQuery.SortOrder.descending);

        Set<String> idsToFilter = new HashSet<String>();
        idsToFilter.addAll(securityEventContextResolver.findAccessible(null));

        // At present, multiple "in" queries do not get joined together properly by the Mongo Query converter.
        // Therefore, explicitly check for pre-existing "in" queries or "=" queries and combine them
        // here manually.

        boolean foundIdInQuery = false;
        for (NeutralCriteria criterion : neutralQuery.getCriteria()) {
            if ("_id".equals(criterion.getKey())) {
                foundIdInQuery = true;
                if (NeutralCriteria.CRITERIA_IN.equals(criterion.getOperator())) {
                    Set<String> values = new HashSet<String>();
                    for (String idValue : (Iterable<String>) criterion.getValue()) {
                        values.add(idValue);
                    }
                    values.retainAll(idsToFilter);
                } else if (NeutralCriteria.OPERATOR_EQUAL.equals(criterion.getOperator())) {
                    Set<String> values = new HashSet<String>();
                    values.add((String) criterion.getValue());
                    values.retainAll(idsToFilter);
                    criterion.setOperator(NeutralCriteria.CRITERIA_IN);
                    criterion.setValue(values);
                } else {
                    throw new UnsupportedOperationException(
                            "do not know how to handle additional _id criterion with operator " +
                            criterion.getOperator());
                }
            }
        }

        if (!foundIdInQuery) {
            neutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, idsToFilter));
        }

        Collection<Entity> entities = (Collection<Entity>) repo.findAll(collectionName, neutralQuery);

        List<EntityBody> results = new ArrayList<EntityBody>();

        for (Entity entity : entities) {
            results.add(makeEntityBody(entity));
        }

        if (results.isEmpty()) {
            return noEntitiesFound(neutralQuery);
        }

        return results;
    }

    @Override
    public EntityBody getCustom(String id) {
        checkWhetherUserHasAccessToSecurityEvent(id);

        String clientId = null;
        try {
            clientId = getClientId(id);
        } catch (APIAccessDeniedException e) {
            // set custom entity data for security event targetEdOrgList
            APIAccessDeniedException wrapperE = new APIAccessDeniedException("Custom entity get denied.", e);
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(id);
            wrapperE.setEntityType(defn.getType());
            wrapperE.setEntityIds(entityIds);
            throw wrapperE;
        }

        LOG.debug("Reading custom entity: entity={}, entityId={}, clientId={}", new Object[]{
                getEntityDefinition().getType(), id, clientId});

        Entity customEntity = getCustomEntity(id, clientId);

        if (customEntity != null) {
            EntityBody clonedBody = new EntityBody(customEntity.getBody());
            return clonedBody;
        } else {
            return null;
        }
    }


    @Override
    public void deleteCustom(String id) {
        checkWhetherUserHasAccessToSecurityEvent(id);

        String clientId = null;
        try {
            clientId = getClientId(id);
        } catch (APIAccessDeniedException e) {
            // set custom entity data for security event targetEdOrgList
            APIAccessDeniedException wrapperE = new APIAccessDeniedException("Custom entity delete denied.", e);
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(id);
            wrapperE.setEntityType(defn.getType());
            wrapperE.setEntityIds(entityIds);
            throw wrapperE;
        }

        Entity customEntity = getCustomEntity(id, clientId);

        if (customEntity == null) {
            throw new EntityNotFoundException(id);
        }

        boolean deleted = getRepo().delete(CUSTOM_ENTITY_COLLECTION, customEntity.getEntityId());

        LOG.debug("Deleting custom entity: entity={}, entityId={}, clientId={}, deleted?={}", new Object[]{
                getEntityDefinition().getType(), id, clientId, String.valueOf(deleted)});
    }

    @Override
    public void createOrUpdateCustom(String id, EntityBody customEntity) throws EntityValidationException {
        checkWhetherUserHasAccessToSecurityEvent(id);

        String clientId = null;
        Entity parentEntity = getEntity(id);

        try {
            clientId = getClientId(id);
        } catch (APIAccessDeniedException e) {
            // set custom entity data for security event targetEdOrgList
            APIAccessDeniedException wrapperE = new APIAccessDeniedException("Custom entity write denied.", e);
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(id);
            wrapperE.setEntityType(defn.getType());
            wrapperE.setEntityIds(entityIds);
            throw wrapperE;
        }

        Entity entity = getCustomEntity(id, clientId);

        if (entity != null && entity.getBody().equals(customEntity)) {
            LOG.debug("No change detected to custom entity, ignoring update: entity={}, entityId={}, clientId={}",
                    new Object[]{getEntityDefinition().getType(), id, clientId});

            return;
        }

        // Verify field names contain no blacklisted components.
        List<ValidationError> errorList = customEntityValidator.validate(customEntity);
        if (!errorList.isEmpty()) {
            LOG.debug("Blacklist validation failed for custom entity {}", id);
            throw new EntityValidationException(id, PathConstants.CUSTOM_ENTITIES, errorList);
        }

        EntityBody clonedEntity = new EntityBody(customEntity);

        if (entity != null) {
            LOG.debug("Overwriting existing custom entity: entity={}, entityId={}, clientId={}", new Object[]{
                    getEntityDefinition().getType(), id, clientId});
            entity.getBody().clear();
            entity.getBody().putAll(clonedEntity);
            // custom entity is not superdoc
            getRepo().update(CUSTOM_ENTITY_COLLECTION, entity, false);
        } else {
            LOG.debug("Creating new custom entity: entity={}, entityId={}, clientId={}", new Object[]{
                    getEntityDefinition().getType(), id, clientId});
            EntityBody metaData = new EntityBody();

            SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            metaData.put(CUSTOM_ENTITY_CLIENT_ID, clientId);
            metaData.put(CUSTOM_ENTITY_ENTITY_ID, id);
            metaData.put("tenantId", principal.getTenantId());
            getRepo().create(CUSTOM_ENTITY_COLLECTION, clonedEntity, metaData, CUSTOM_ENTITY_COLLECTION);
        }
    }

}
