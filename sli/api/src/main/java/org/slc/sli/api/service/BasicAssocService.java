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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.FullSuperDoc;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Implementation of AssociationService.
 * 
 */
@Scope("prototype")
@Component("basicAssociationService")
public class BasicAssocService extends BasicService implements AssociationService {

    private final EntityDefinition sourceDefn;
    private final EntityDefinition targetDefn;
    private final String sourceKey;
    private final String targetKey;

    public BasicAssocService(final String collectionName, final List<Treatment> treatments,
            final EntityDefinition sourceDefn, final String sourceKey, final EntityDefinition targetDefn,
            final String targetKey, Repository<Entity> repo) {
        super(collectionName, treatments, repo);
        this.sourceDefn = sourceDefn;
        this.targetDefn = targetDefn;
        this.sourceKey = sourceKey;
        this.targetKey = targetKey;
    }

    @Override
    public Iterable<String> getAssociationsFor(final String id, final NeutralQuery neutralQuery) {
        List<String> results = new ArrayList<String>();
        results.addAll(getAssociationsList(sourceDefn, id, sourceKey, neutralQuery));
        results.addAll(getAssociationsList(targetDefn, id, targetKey, neutralQuery));
        return results;
    }

    @Override
    public Iterable<String> getAssociationsWith(final String id, final NeutralQuery neutralQuery) {
        return getAssociations(sourceDefn, id, sourceKey, neutralQuery);
    }

    @Override
    public Iterable<String> getAssociationsTo(final String id, final NeutralQuery neutralQuery) {
        return getAssociations(targetDefn, id, targetKey, neutralQuery);
    }

    @Override
    public EntityIdList getAssociatedEntitiesWith(final String id, final NeutralQuery neutralQuery) {
        return getAssociatedEntities(sourceDefn, id, sourceKey, targetKey, neutralQuery);
    }

    @Override
    public EntityIdList getAssociatedEntitiesTo(final String id, final NeutralQuery neutralQuery) {
        return getAssociatedEntities(targetDefn, id, targetKey, sourceKey, neutralQuery);
    }

    @Override
    public String create(final EntityBody content) {

        // Create the association
        String id = super.create(content);

        String sourceCollection = sourceDefn.getStoredCollectionName();
        String targetCollection = targetDefn.getStoredCollectionName();

        List<String> srcId = getIds(content, sourceKey);
        List<String> targetId = getIds(content, targetKey);

        NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, srcId, false));
        Iterable<Entity> sourceEntities = getRepo().findAll(sourceCollection, query);
        query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, targetId, false));
        Iterable<Entity> targetEntities = getRepo().findAll(sourceCollection, query);

        for (Entity sourceEntity : sourceEntities) {
            for (Entity targetEntity : targetEntities) {
                // If both entities are orphaned, don't allow linking
                if ("true".equals(sourceEntity.getMetaData().get("isOrphaned"))
                        && "true".equals(targetEntity.getMetaData().get("isOrphaned"))) {
                    warn("Link two orphaned entities, ids {} & {}", sourceEntity.getEntityId(),
                            targetEntity.getEntityId());
                }

                // Unorphan
                targetEntity.getMetaData().remove("isOrphaned");
                sourceEntity.getMetaData().remove("isOrphaned");

                try {
                    getRepo().update(sourceCollection, sourceEntity, FullSuperDoc.isFullSuperdoc(sourceEntity));
                    getRepo().update(targetCollection, targetEntity, FullSuperDoc.isFullSuperdoc(targetEntity));
                } catch (EntityValidationException e) {
                    error("Invariant violation.  Read entity couldn't be updated", e);
                }
            }
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    private List<String> getIds(EntityBody content, String key) {
        List<String> foundIds = new ArrayList<String>();

        Object ids = content.get(key);
        if (ids instanceof List) {
            foundIds.addAll((Collection<? extends String>) ids);
        } else if (ids instanceof String) {
            foundIds.add((String) ids);
        }

        return foundIds;
    }

    @Override
    public long countAssociationsWith(final String id, NeutralQuery neutralQuery) {
        return countAssociationsTo(id, neutralQuery) + countAssociationsFor(id, neutralQuery);
    }

    @Override
    public long countAssociationsTo(final String id, NeutralQuery neutralQuery) {
        return getAssociationCount(targetDefn, id, targetKey, neutralQuery);
    }

    @Override
    public long countAssociationsFor(final String id, NeutralQuery neutralQuery) {
        return getAssociationCount(sourceDefn, id, sourceKey, neutralQuery);
    }

    /**
     * Get associations to the entity of the given type and id, where id is keyed off of key
     * 
     * @param type
     *            the type of the entity being queried
     * @param id
     *            the id of the entity being queried
     * @param key
     *            the key the id maps to
     * @param start
     *            the number of entities in the list to skip
     * @param numResults
     *            the number of entities to return
     * @param queryString
     *            the query string to filter returned collection results
     * @return
     */
    private Iterable<String> getAssociations(final EntityDefinition type, final String id, final String key,
            final NeutralQuery neutralQuery) {
        // LOG.debug("Getting assocations with {} from {} through {}", new Object[] { id, start,
        // numResults });
        return getAssociationsList(type, id, key, neutralQuery);
    }

    private List<String> getAssociationsList(final EntityDefinition type, final String id, final String key,
            final NeutralQuery neutralQuery) {
        List<String> results = new ArrayList<String>();
        Iterable<Entity> entityObjects = getAssociationObjects(type, id, key, neutralQuery);
        for (Entity entity : entityObjects) {
            results.add(entity.getEntityId());
        }
        return results;
    }

    /**
     * Get associations to the entity of the given type and id, where id is keyed off of key
     * 
     * @param type
     *            the type of the entity being queried
     * @param id
     *            the id of the entity being queried
     * @param key
     *            the key the id maps to
     * @param start
     *            the number of entities in the list to skip
     * @param numResults
     *            the number of entities to return
     * @param queryString
     *            the query string to filter returned collection results
     * @return
     */
    private EntityIdList getAssociatedEntities(final EntityDefinition type, final String id, final String key,
            final String otherEntityKey, final NeutralQuery neutralQuery) {
        // LOG.debug("Getting assocated entities with {} from {} through {}", new Object[] { id,
        // start, numResults });
        EntityDefinition otherEntityDefn = type == sourceDefn ? targetDefn : sourceDefn;

        Iterable<Entity> entityObjects = getAssociationObjects(type, id, key, new NeutralQuery());
        // there can be multiple association objects pointing to the same associated entity, and we
        // need the number of unique ones for the totalCount
        Set<String> associatedEntityIdSet = new HashSet<String>();
        List<String> ids = new ArrayList<String>();
        for (Entity entity : entityObjects) {
            Object other = entity.getBody().get(otherEntityKey);
            if (other != null && other instanceof String) {
                if (associatedEntityIdSet.add((String) other)) {
                    ids.add((String) other);
                }
            } else {
                error("Association had bad value of key {}: {}", new Object[] { otherEntityKey, other });
            }
        }

        NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);
        localNeutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));

        final Iterable<String> results = getRepo().findAllIds(otherEntityDefn.getStoredCollectionName(),
                localNeutralQuery);
        final long totalCount = associatedEntityIdSet.size();
        return new EntityIdList() {
            @Override
            public Iterator<String> iterator() {
                return results.iterator();
            }

            @Override
            public long getTotalCount() {
                return totalCount;
            }
        };
    }

    /**
     * Gets the actual association objects (and not just the ids
     * 
     * @param type
     *            the type of the entity being queried
     * @param id
     *            the id of the entity being queried
     * @param key
     *            the key the id maps to
     * @param start
     *            the number of entities in the list to skip
     * @param numResults
     *            the number of entities to return
     * @param queryString
     *            the query string to filter returned collection results
     * @return
     */
    private Iterable<Entity> getAssociationObjects(final EntityDefinition type, final String id, final String key,
            final NeutralQuery neutralQuery) {
        EntityBody existingEntity = type.getService().get(id);
        if (existingEntity == null) {
            throw new EntityNotFoundException(id);
        }

        NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);
        localNeutralQuery.addCriteria(new NeutralCriteria(key, NeutralCriteria.OPERATOR_EQUAL, id));

        return getRepo().findAll(getCollectionName(), localNeutralQuery);
    }

    private long getAssociationCount(final EntityDefinition type, final String id, final String key,
            final NeutralQuery neutralQuery) {
        NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);
        localNeutralQuery.addCriteria(new NeutralCriteria(key, NeutralCriteria.OPERATOR_EQUAL, id));

        return getRepo().count(getCollectionName(), localNeutralQuery);
    }
    
}
