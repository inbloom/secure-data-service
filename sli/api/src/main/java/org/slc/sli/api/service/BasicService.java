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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.BasicDefinitionStore;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.roles.EntityRightsFilter;
import org.slc.sli.api.security.roles.RightAccessValidator;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.UserContext;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.AccessibilityCheck;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.FullSuperDoc;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;

/**
 * Implementation of EntityService that can be used for most entities.
 * <p/>
 * <p/>
 * It is very important this bean prototype scope, since one service is needed per
 * entity/association.
 */
@Scope("prototype")
@Component("basicService")
public class BasicService implements EntityService, AccessibilityCheck {

    private static final Logger LOG = LoggerFactory.getLogger(BasicService.class);

    private static final int MAX_RESULT_SIZE = 0;
    private static final String CUSTOM_ENTITY_COLLECTION = "custom_entities";
    private static final String CUSTOM_ENTITY_CLIENT_ID = "clientId";
    private static final String CUSTOM_ENTITY_ENTITY_ID = "entityId";
    private static final List<String> STUDENT_SELF = Arrays.asList(EntityNames.STUDENT, EntityNames.STUDENT_PROGRAM_ASSOCIATION, EntityNames.STUDENT_COHORT_ASSOCIATION,
            EntityNames.STUDENT_SECTION_ASSOCIATION, EntityNames.PARENT, EntityNames.STUDENT_PARENT_ASSOCIATION);
    private String collectionName;
    private List<Treatment> treatments;
    private EntityDefinition defn;
    private Repository<Entity> repo;

    @Autowired
    private ContextValidator contextValidator;

    @Autowired
    private SchemaDataProvider provider;

    @Autowired
    private CallingApplicationInfoProvider clientInfo;

    @Autowired
    private BasicDefinitionStore definitionStore;

    @Autowired
    private CustomEntityValidator customEntityValidator;

    @Autowired
    private RightAccessValidator rightAccessValidator;

    @Autowired
    private EntityRightsFilter entityRightsFilter;

    // The size limit of GET returns for dual contexts/differing user rights across edOrgs.
    @Value("${sli.api.service.countLimit:10000}")
    private long countLimit;

    // The size limit of GET returns for dual contexts/differing user rights across edOrgs.
    @Value("${sli.api.service.batchSize:10000}")
    private int batchSize;

    private static ThreadLocal<ContextualRolesListingCache> rolesListingCacheTL = new ThreadLocal<ContextualRolesListingCache>() {
        @Override
        protected ContextualRolesListingCache initialValue()
        {
            return new ContextualRolesListingCache();
        }
    };

    public BasicService(String collectionName, List<Treatment> treatments, Repository<Entity> repo) {
        this.collectionName = collectionName;
        this.treatments = treatments;
        this.repo = repo;
        if (repo == null) {
            throw new IllegalArgumentException("Please provide repo");
        }
    }

    @Override
    public long count(NeutralQuery neutralQuery) {
        boolean isSelf = isSelf(neutralQuery);
        checkAccess(true, isSelf, null);
        checkFieldAccess(neutralQuery, isSelf);

        return getRepo().count(collectionName, neutralQuery);
    }

    @Override
    public long countBasedOnContextualRoles(NeutralQuery neutralQuery) {
        boolean isSelf = isSelf(neutralQuery);
        Collection<GrantedAuthority> auths = SecurityUtil.getSLIPrincipal().getAllContextRights(isSelf);
        rightAccessValidator.checkAccess(true, null, defn.getType(), auths);
        rightAccessValidator.checkFieldAccess(neutralQuery, defn.getType(), auths);

        long count = 0;
        if (userHasMultipleContextsOrDifferingRights() && (!EntityNames.isPublic(defn.getType()))) {
            count = getAccessibleEntitiesCount(collectionName);
        } else {
            count = getRepo().count(collectionName, neutralQuery);
        }
        return count;
    }

    @Override
    public List<String> create(List<EntityBody> content) {
        List<String> entityIds = new ArrayList<String>();
        for (EntityBody entityBody : content) {
            entityIds.add(create(entityBody));
        }
        if (entityIds.size() != content.size()) {
            for (String id : entityIds) {
                delete(id);
            }
        }
        return entityIds;
    }

    @Override
    public List<String> createBasedOnContextualRoles(List<EntityBody> content) {
        List<String> entityIds = new ArrayList<String>();
        for (EntityBody entityBody : content) {
            entityIds.add(createBasedOnContextualRoles(entityBody));
        }
        if (entityIds.size() != content.size()) {
            for (String id : entityIds) {
                delete(id);
            }
        }
        return entityIds;
    }

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     *
     * @param neutralQuery all parameters to be included in query
     * @return the body of the entity
     */
    @Override
    public Iterable<String> listIds(final NeutralQuery neutralQuery) {
        checkAccess(true, false, null);
        checkFieldAccess(neutralQuery, false);

        NeutralQuery nq = neutralQuery;
        injectSecurity(nq);
        Iterable<Entity> entities = repo.findAll(collectionName, nq);

        List<String> results = new ArrayList<String>();
        for (Entity entity : entities) {
            results.add(entity.getEntityId());
        }
        return results;
    }

    @Override
    public String create(EntityBody content) {
        checkAccess(false, false, content);

        checkReferences(null, content);

        List<String> entityIds = new ArrayList<String>();
        sanitizeEntityBody(content);
        // ideally we should validate everything first before actually persisting
        Entity entity = getRepo().create(defn.getType(), content, createMetadata(), collectionName);
        if (entity != null) {
            entityIds.add(entity.getEntityId());
        }

        return entity.getEntityId();
    }

    @Override
    public String createBasedOnContextualRoles(EntityBody content) {

        Entity entity = new MongoEntity(defn.getType(), null, content, createMetadata());

        Collection<GrantedAuthority> auths = rightAccessValidator.getContextualAuthorities(false, entity, SecurityUtil.getUserContext(), false);
        rightAccessValidator.checkAccess(false, false, entity, entity.getType(), auths);

        checkReferences(null, content);

        List<String> entityIds = new ArrayList<String>();
        sanitizeEntityBody(content);

        // Ideally, we should validate everything first before actually persisting!
        Entity created = getRepo().create(defn.getDbType(), content, createMetadata(), collectionName);
        if (created != null) {
            entityIds.add(created.getEntityId());
        }

        return created.getEntityId();
    }

    /**
     * Determines if the user has multiple contexts, or differing sets of rights per role.
     *
     * @return Whether or not the user has multiple contexts, or differing sets of rights per role
     */
    private boolean userHasMultipleContextsOrDifferingRights() {
        if (SecurityUtil.getUserContext() == UserContext.DUAL_CONTEXT) {
            return true;
        }

        SLIPrincipal principal = SecurityUtil.getSLIPrincipal();
        if (principal.getEdOrgRights().size() > 1) {
            Iterator<Collection<GrantedAuthority>> edOrgRightsIter = principal.getEdOrgRights().values().iterator();
            Collection<GrantedAuthority> firstRightsSet = edOrgRightsIter.next();
            while (edOrgRightsIter.hasNext()) {
                Collection<GrantedAuthority> nextRightsSet = edOrgRightsIter.next();
                if ((!firstRightsSet.containsAll(nextRightsSet)) || (!nextRightsSet.containsAll(firstRightsSet))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Validates that user roles allow access to fields
     *
     * @param isRead  whether operation is "read" or "write"
     * @param isSelf  whether operation is being done in "self" context
     * @param content item under inspection
     */
    private void checkAccess(boolean isRead, boolean isSelf, EntityBody content) {

        Collection<GrantedAuthority> auths = getAuths(isSelf);

        rightAccessValidator.checkAccess(isRead, content, defn.getType(), auths);
    }

    private void checkAccess(boolean isRead, String entityId, EntityBody content) {
        rightAccessValidator.checkSecurity(isRead, entityId, defn.getType(), collectionName, getRepo());

        try {
            checkAccess(isRead, isSelf(entityId), content);
        } catch (APIAccessDeniedException e) {
            // we only know the target entity here so rethrow with that info so it can be used in the security event
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(entityId);
            e.setEntityType(defn.getType());
            e.setEntityIds(entityIds);
            throw e;
        }
    }

    /*
     * Check routine for interface
     * @see org.slc.sli.domain.AccessibilityCheck#accessibilityCheck(java.lang.String)
     */
    @Override
    public boolean accessibilityCheck(String id) {
        try {
            checkAccess(false, id, null);
        } catch (APIAccessDeniedException e) {
            return false;
        }
        return true;
    }

    // This will be replaced by a call to:
    //     getRepo().safeDelete(collectionName, id, true, false, 0, this);
    // I.e., cascade=true, dryrun=false, max=0=unlimited, access check = this service
    //
    // Future "enhanced" versions of delete exposed to the API can call safeDelete()
    // with different combinations of parameters

    @Override
     public void delete(String id) {

        checkAccess(false, id, null);

        try {
            cascadeDelete(id);
        } catch (RuntimeException re) {
            LOG.debug(re.toString());
        }

        if (!getRepo().delete(collectionName, id)) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        deleteAttachedCustomEntities(id);
    }

    @Override
    public void deleteBasedOnContextualRoles(String id) {

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        boolean isSelf = isSelf(query);

        Entity entity = repo.findOne(collectionName, query);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        Collection<GrantedAuthority> auths = getEntityContextAuthorities(entity, isSelf, false);

        rightAccessValidator.checkAccess(false, id, null, defn.getType(), collectionName, getRepo(), auths);

        try {
            cascadeDelete(id);
        } catch (RuntimeException re) {
            LOG.debug(re.toString());
        }

        if (!getRepo().delete(collectionName, id)) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        deleteAttachedCustomEntities(id);
    }

    @Override
    public boolean update(String id, EntityBody content) {
        LOG.debug("Updating {} in {} with {}", new Object[] {id, collectionName, SecurityUtil.getSLIPrincipal()});
        checkAccess(false, id, content);

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));
        Entity entity = getRepo().findOne(collectionName, query);
        // Entity entity = repo.findById(collectionName, id);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        sanitizeEntityBody(content);

        boolean success = false;
        if (entity.getBody().equals(content)) {
            LOG.info("No change detected to {}", id);
            return false;
        }

        checkReferences(id, content);

        LOG.info("new body is {}", content);
        entity.getBody().clear();
        entity.getBody().putAll(content);

        success = repo.update(collectionName, entity, FullSuperDoc.isFullSuperdoc(entity));
        return success;
    }

    @Override
    public boolean updateBasedOnContextualRoles(String id, EntityBody content) {
        LOG.debug("Updating {} in {} with {}", new Object[] {id, collectionName, SecurityUtil.getSLIPrincipal()});

        boolean isSelf = isSelf(id);
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));
        Entity entity = getRepo().findOne(collectionName, query);
        // Entity entity = repo.findById(collectionName, id);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        Collection<GrantedAuthority> auths = getEntityContextAuthorities(entity, isSelf, false);

        rightAccessValidator.checkAccess(false, id, content, defn.getType(), collectionName, getRepo(), auths);

        sanitizeEntityBody(content);

        boolean success = false;
        if (entity.getBody().equals(content)) {
            LOG.info("No change detected to {}", id);
            return false;
        }

        checkReferences(id, content);

        LOG.info("new body is {}", content);
        entity.getBody().clear();
        entity.getBody().putAll(content);

        success = repo.update(collectionName, entity, FullSuperDoc.isFullSuperdoc(entity));
        return success;
    }

    @Override
    public boolean patch(String id, EntityBody content) {
        LOG.debug("Patching {} in {}", id, collectionName);
        checkAccess(false, id, content);

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        if (repo.findOne(collectionName, query) == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        sanitizeEntityBody(content);

        LOG.info("patch value(s): ", content);

        // don't check references until things are combined
        checkReferences(id, content);

        repo.patch(defn.getType(), collectionName, id, content);

        return true;
    }

    @Override
    public boolean patchBasedOnContextualRoles(String id, EntityBody content) {
        LOG.debug("Patching {} in {}", id, collectionName);

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        boolean isSelf = isSelf(query);

        Entity entity = repo.findOne(collectionName, query);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        Collection<GrantedAuthority> auths = getEntityContextAuthorities(entity, isSelf, false);

        rightAccessValidator.checkAccess(false, id, content, defn.getType(), collectionName, getRepo(), auths);

        sanitizeEntityBody(content);

        LOG.info("patch value(s): ", content);

        // don't check references until things are combined
        checkReferences(id, content);

        repo.patch(defn.getType(), collectionName, id, content);

        return true;
    }

    @Override
    public EntityBody get(String id) {
        return get(id, new NeutralQuery());

    }

    @Override
    public EntityBody get(String id, NeutralQuery neutralQuery) {
        Entity entity = getEntity(id, neutralQuery);

        if (entity == null) {
            throw new EntityNotFoundException(id);
        }

        return makeEntityBody(entity);
    }

    private Entity getEntity(String id, final NeutralQuery neutralQuery) {
        checkAccess(true, id, null);
        checkFieldAccess(neutralQuery, isSelf(id));

        NeutralQuery nq = neutralQuery;
        if (nq == null) {
            nq = new NeutralQuery();
        }
        nq.addCriteria(new NeutralCriteria("_id", "=", id));

        Entity entity = repo.findOne(collectionName, nq);
        return entity;
    }

    private Iterable<EntityBody> noEntitiesFound(NeutralQuery neutralQuery) {
        // this.addDefaultQueryParams(neutralQuery, collectionName);
        if (!repo.findAll(collectionName, neutralQuery).iterator().hasNext()) {
            return new ArrayList<EntityBody>();
        } else {
            throw new APIAccessDeniedException("Access to resource denied.");
        }
    }

    private Iterable<EntityBody> noEntitiesFound(Boolean noDataInDB) {
        if (noDataInDB) {
            return new ArrayList<EntityBody>();
        } else {
            throw new APIAccessDeniedException("Access to resource denied.");
        }
    }

    @Override
    public Iterable<EntityBody> get(Iterable<String> ids) {

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(MAX_RESULT_SIZE);

        return get(ids, neutralQuery);
    }

    @Override
    public Iterable<EntityBody> get(Iterable<String> ids, final NeutralQuery neutralQuery) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        checkAccess(true, false, null);
        checkFieldAccess(neutralQuery, false);

        List<String> idList = new ArrayList<String>();

        for (String id : ids) {
            idList.add(id);
        }

        if (!idList.isEmpty()) {
            NeutralQuery nq = neutralQuery;
            if (nq == null) {
                nq = new NeutralQuery();
                nq.setOffset(0);
                nq.setLimit(MAX_RESULT_SIZE);
            }

            // add the ids requested
            nq.addCriteria(new NeutralCriteria("_id", "in", idList));

            injectSecurity(nq);
            Iterable<Entity> entities = repo.findAll(collectionName, nq);

            List<EntityBody> results = new ArrayList<EntityBody>();
            for (Entity e : entities) {
                results.add(makeEntityBody(e));
            }

            return results;
        }

        return Collections.emptyList();
    }

    @Override
    public Iterable<EntityBody> list(NeutralQuery neutralQuery) {
        boolean isSelf = isSelf(neutralQuery);
        checkAccess(true, isSelf, null);
        checkFieldAccess(neutralQuery, isSelf);

        injectSecurity(neutralQuery);
        Collection<Entity> entities = (Collection<Entity>) repo.findAll(collectionName, neutralQuery);
        setAccessibleEntitiesCount(collectionName, entities.size());

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
    public Iterable<EntityBody> listBasedOnContextualRoles(NeutralQuery neutralQuery) {
        boolean isSelf = isSelf(neutralQuery);
        boolean noDataInDB = true;
        Map<String, UserContext> entityContexts = null;

        injectSecurity(neutralQuery);

        boolean findSpecial = userHasMultipleContextsOrDifferingRights() && (!EntityNames.isPublic(defn.getType()));
        Collection<Entity> entities = new HashSet<Entity>();
        if (findSpecial) {
            entities = getResponseEntities(neutralQuery, isSelf);
            entityContexts = getEntityContexts();
        } else {
            entities = (Collection<Entity>) repo.findAll(collectionName, neutralQuery);

            if (SecurityUtil.getUserContext() == UserContext.DUAL_CONTEXT) {
                entityContexts = getEntityContextMap(entities, true);
            }
        }

        noDataInDB = entities.isEmpty();

        List<EntityBody> results = new ArrayList<EntityBody>();

        for (Entity entity : entities) {
            UserContext context = getEntityContext(entity.getEntityId(), entityContexts);

            try {
                Collection<GrantedAuthority> auths = null;
                if (!findSpecial) {
                    auths = rightAccessValidator.getContextualAuthorities(isSelf, entity, context, true);
                    rightAccessValidator.checkAccess(true, isSelf, entity, defn.getType(), auths);
                    rightAccessValidator.checkFieldAccess(neutralQuery, entity, defn.getType(), auths);
                } else {
                    auths = getEntityAuthorities(entity.getEntityId());
                }

                results.add(entityRightsFilter.makeEntityBody(entity, treatments, defn, isSelf, auths, context));
            } catch (AccessDeniedException aex) {
                if (entities.size() == 1) {
                    throw aex;
                } else {
                    LOG.error(aex.getMessage());
                }
            }

        }

        if (results.isEmpty()) {
            validateQuery(neutralQuery, isSelf);
            return noEntitiesFound(noDataInDB);
        }

        return results;
    }

    private Collection<Entity> getResponseEntities(NeutralQuery neutralQuery, boolean isSelf) throws AccessDeniedException {
        Collection<Entity> responseEntities = new ArrayList<Entity>();
        Map<String, UserContext> entityContexts = new HashMap<String, UserContext>();
        final int limit = neutralQuery.getLimit();
        final int offset = neutralQuery.getOffset();

        // Iterate through all queried entities.  For each one that passes access checks, increment the count.
        // Additionally, collect the accessible entities requested for this call.  Stop at the hard count limit.
        final int responseLimit = ((0 < limit) && (limit < getCountLimit())) ? limit : (int) getCountLimit();
        long accessibleCount = 0;
        int currentOffset = 0;
        int totalCount = 0;
        Collection<Entity> batchedEntities;
        do {
            batchedEntities = getBatchedEntities(neutralQuery, getBatchSize(), currentOffset);
            totalCount += batchedEntities.size();

            Map<String, UserContext> batchedEntityContexts = new HashMap<String, UserContext>();
            if (SecurityUtil.getUserContext() == UserContext.DUAL_CONTEXT) {
                batchedEntityContexts = getEntityContextMap(batchedEntities, true);
            }
            Iterator<Entity> batchedEntitiesIt = batchedEntities.iterator();
            while ((accessibleCount < getCountLimit()) && batchedEntitiesIt.hasNext()) {
                Entity entity = batchedEntitiesIt.next();
                try {
                    validateEntity(entity, isSelf, neutralQuery, batchedEntityContexts);
                    accessibleCount++;
                    if ((accessibleCount > offset) && (responseEntities.size() < responseLimit)) {
                        responseEntities.add(entity);
                        entityContexts.put(entity.getEntityId(), getEntityContext(entity.getEntityId(), batchedEntityContexts));
                    }
                } catch (AccessDeniedException aex) {
                    if (totalCount == 1) {
                        throw aex;
                    } else if ((accessibleCount > offset) && (responseEntities.size() < responseLimit)) {
                        LOG.error(aex.getMessage());
                    }
                }
            }

            currentOffset += getBatchSize();
        }  while ((accessibleCount < getCountLimit()) && (batchedEntities.size() == getBatchSize()));

        neutralQuery.setLimit(limit);
        neutralQuery.setOffset(offset);

        if ((totalCount > 0) && (accessibleCount == 0)) {
            validateQuery(neutralQuery, isSelf);
            throw new APIAccessDeniedException("Access to resource denied.");
        }

        setEntityContexts(entityContexts);

        //  Store the total accessible entity count, for later retrieval by countBasedOnContextualRoles.
        setAccessibleEntitiesCount(collectionName, accessibleCount);

        return responseEntities;
    }

    private Collection<Entity> getBatchedEntities(NeutralQuery neutralQuery, int limit, int offset) {
        neutralQuery.setOffset(offset);
        neutralQuery.setLimit(limit);
        Collection<Entity> batchedEntities = (Collection<Entity>) getRepo().findAll(collectionName, neutralQuery);

        return batchedEntities;
    }

    private void validateEntity(Entity entity, boolean isSelf, NeutralQuery neutralQuery, Map<String, UserContext> entityContexts) {
        UserContext context = getEntityContext(entity.getEntityId(), entityContexts);
        Collection<GrantedAuthority> auths = rightAccessValidator.getContextualAuthorities(isSelf, entity, context, true);
        rightAccessValidator.checkAccess(true, isSelf, entity, defn.getType(), auths);
        rightAccessValidator.checkFieldAccess(neutralQuery, entity, defn.getType(), auths);
        setEntityAuthorities(entity.getEntityId(), auths);
    }

    private UserContext getEntityContext(String entityId, Map<String, UserContext> entityContext) {
        UserContext context = SecurityUtil.getUserContext();
        if ((SecurityUtil.getUserContext() == UserContext.DUAL_CONTEXT) && (entityContext != null)) {
            if (entityContext.containsKey(entityId)) {
                context = entityContext.get(entityId);
            } else {
                context = UserContext.NO_CONTEXT;
            }
        }
        return context;
    }

    private void validateQuery(NeutralQuery neutralQuery, boolean self) {
        NeutralQuery newQuery = new NeutralQuery(neutralQuery);
        boolean removableCriteriaExists = false;
        for (NeutralCriteria cr : neutralQuery.getCriteria()) {
            if(cr.isRemovable()) {
                newQuery.removeCriteria(cr);
                removableCriteriaExists = true;
            }
        }
        if(removableCriteriaExists) {
            Collection<Entity> noSearchEntities = (Collection<Entity>) repo.findAll(collectionName, newQuery);
            for(Entity en : noSearchEntities) {
                Collection<GrantedAuthority> auths = getEntityContextAuthorities(en, self, true);
                rightAccessValidator.checkAccess(true, self, en, defn.getType(), auths);
                rightAccessValidator.checkFieldAccess(neutralQuery, en, defn.getType(), auths);
            }
        }
    }

    @Override
    public boolean exists(String id) {
        checkAccess(true, isSelf(id), null);

        boolean exists = false;
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        injectSecurity(query);
        Iterable<Entity> entities = repo.findAll(collectionName, query);

        if (entities != null && entities.iterator().hasNext()) {
            exists = true;
        }

        return exists;
    }

    @Override
    public EntityBody getCustom(String id) {
        if (SecurityUtil.isStaffUser()) {
            Entity entity = getEntity(id);

            Collection<GrantedAuthority> auths = getEntityContextAuthorities(entity, isSelf(id), true);

            rightAccessValidator.checkAccess(true, id, null, defn.getType(), collectionName, getRepo(), auths);
        } else {
            checkAccess(true, id, null);
        }

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
        if (SecurityUtil.isStaffUser()) {
            Entity entity = getEntity(id);
            Collection<GrantedAuthority> auths = getEntityContextAuthorities(entity, isSelf(id), false);

            rightAccessValidator.checkAccess(false, id, null, defn.getType(), collectionName, getRepo(), auths);
        } else {
            checkAccess(false, id, null);
        }

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
        String clientId = null;
        if(SecurityUtil.isStaffUser()) {
            Entity parentEntity = getEntity(id);
            Collection<GrantedAuthority> auths = getEntityContextAuthorities(parentEntity, isSelf(id), false);

            rightAccessValidator.checkAccess(false, id, customEntity, defn.getType(), collectionName, getRepo(), auths);
        } else {
            checkAccess(false, id, customEntity);
        }

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

    private void checkReferences(String entityId, EntityBody eb) {
        /* TODO: MAKE BETTER
         * Note that this is a workaround to allow students to validate
         * only their own student ID when checking references, else they'd never
         * be able to POST or PUT anything
         */
        if (SecurityUtil.isStudent()) {
            String entityType = defn.getType();

            if (entityType.equals(EntityNames.STUDENT)) {
                // Validate id is yourself
                if (!SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(entityId)) {
                    throw new APIAccessDeniedException("Cannot update student not yourself", entityType, entityId);
                }
            } else if (entityType.equals(EntityNames.STUDENT_ASSESSMENT)) {
                String studentId = (String) eb.get(ParameterConstants.STUDENT_ID);

                // Validate student ID is yourself
                if (studentId != null && !SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(studentId)) {
                    throw new APIAccessDeniedException("Cannot update student assessments that are not your own", EntityNames.STUDENT, studentId);
                }
            } else if (entityType.equals(EntityNames.STUDENT_GRADEBOOK_ENTRY) || entityType.equals(EntityNames.GRADE)) {
                String studentId = (String) eb.get(ParameterConstants.STUDENT_ID);
                String ssaId = (String) eb.get(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);

                // Validate student ID is yourself
                if (studentId != null && !SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(studentId)) {
                    throw new APIAccessDeniedException("Cannot update " + entityType + " that are not your own", EntityNames.STUDENT, studentId);
                }
                // Validate SSA ids are accessible via non-transitive SSA validator
                if (ssaId != null) {
                    EntityDefinition def = definitionStore.lookupByEntityType(EntityNames.STUDENT_SECTION_ASSOCIATION);
                    contextValidator.validateContextToEntities(def, Arrays.asList(ssaId), false);
                }
            } else {
                // At the time of this comment, students can only write to student, studentAssessment, studentGradebookEntry, or grade
                throw new IllegalArgumentException("Students cannot write entities of type " + entityType);
            }

            // If you get this far, its all good
            return;
        } else if (SecurityUtil.isParent()) {
            String entityType = defn.getType();

            if (entityType.equals(EntityNames.PARENT)) {
                // Validate id is yourself
                if (!SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(entityId)) {
                    throw new APIAccessDeniedException("Cannot update parent not yourself", entityType, entityId);
                }
            } else if (entityType.equals(EntityNames.STUDENT)) {
                Set<String> ownStudents = SecurityUtil.getSLIPrincipal().getOwnedStudentIds();
                if (!ownStudents.contains(entityId)) {
                    throw new APIAccessDeniedException("Cannot update student that are not your own", EntityNames.STUDENT, entityId);
                }
            } else {
                // At the time of this comment, parents can only write to student and parent
                throw new IllegalArgumentException("Parents cannot write entities of type " + entityType);
            }

            // If you get this far, its all good
            return;
        }
        // else if staff/teacher, do legacy
        for (Map.Entry<String, Object> entry : eb.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            String entityType = provider.getReferencingEntity(defn.getType(), fieldName);

            if (value == null || entityType == null) {
                continue;
            }

            LOG.debug("Field {} is referencing {}", fieldName, entityType);
            @SuppressWarnings("unchecked")
            List<String> ids = value instanceof List ? (List<String>) value : Arrays.asList((String) value);

            EntityDefinition def = definitionStore.lookupByEntityType(entityType);
            if (def == null) {
                LOG.debug("Invalid reference field: {} does not have an entity definition registered", fieldName);
                ValidationError error = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, fieldName, value, null);
                throw new EntityValidationException(null, null, Arrays.asList(error));
            }

            try {
                contextValidator.validateContextToEntities(def, ids, true);
            } catch (APIAccessDeniedException e) {
                LOG.debug("Invalid Reference: {} in {} is not accessible by user", value, def.getStoredCollectionName());
                throw new APIAccessDeniedException(
                        "Invalid reference. No association to referenced entity.", e);
            } catch (EntityNotFoundException e) {
                LOG.debug("Invalid Reference: {} in {} does not exist", value, def.getStoredCollectionName());
                throw (APIAccessDeniedException) new APIAccessDeniedException(
                        "Invalid reference. No association to referenced entity.",
                        defn.getType(), entityId).initCause(e);
            }

        }
    }

    private String getClientId(String id) {
        String clientId = null;
        try {
            clientId = clientInfo.getClientId();
        } catch (APIAccessDeniedException e) {
            // set custom entity data for security event targetEdOrgList
            APIAccessDeniedException wrapperE = new APIAccessDeniedException("Custom entity get denied.", e);
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(id);
            wrapperE.setEntityType(defn.getType());
            wrapperE.setEntityIds(entityIds);
        }

        if (clientId == null) {
            Set<String> entityIds = new HashSet<String>();
            entityIds.add(id);
            throw new APIAccessDeniedException("No Application Id", defn.getType(), entityIds);
        }
        return clientId;
    }

    /**
     * given an entity, make the entity body to expose
     *
     * @param entity
     * @return
     */
    private EntityBody makeEntityBody(Entity entity) {

        Collection<GrantedAuthority> selfAuths = getAuths(isSelf(entity.getEntityId()));
        Collection<GrantedAuthority> nonSelfAuths = getAuths(false);

        EntityBody toReturn = entityRightsFilter.makeEntityBody(entity, treatments, defn, nonSelfAuths, selfAuths);
        return toReturn;
    }

    /**
     * given an entity body that was exposed, return the version with the treatments reversed
     *
     * @param content
     * @return
     */
    private EntityBody sanitizeEntityBody(EntityBody content) {

        for (Treatment treatment : treatments) {
            treatment.toStored(content, defn);
        }
        return content;
    }

    /**
     * Deletes any object with a reference to the given sourceId. Assumes that the sourceId still
     * exists so that
     * authorization/context can be checked.
     *
     * @param sourceId ID that was deleted, where anything else with that ID should also be deleted
     */
    private void cascadeDelete(String sourceId) {
        // loop for every EntityDefinition that references the deleted entity's type
        for (EntityDefinition referencingEntity : defn.getReferencingEntities()) {
            // loop for every reference field that COULD reference the deleted ID

            for (String referenceField : referencingEntity.getReferenceFieldNames(defn.getStoredCollectionName())) {
                EntityService referencingEntityService = referencingEntity.getService();

                List<String> includeFields = new ArrayList<String>();
                includeFields.add(referenceField);
                NeutralQuery neutralQuery = new NeutralQuery();
                neutralQuery.addCriteria(new NeutralCriteria(referenceField + "=" + sourceId));
                neutralQuery.setIncludeFields(includeFields);

                try {
                    // entities that have arrays of references only cascade delete the array entry,
                    // not the whole entity
                    if (referencingEntity.hasArrayField(referenceField)) {
                        // list all entities that have the deleted entity's ID in one of their
                        // arrays

                        Iterable<EntityBody> entityList;
                        if (SecurityUtil.isStaffUser()) {
                            entityList = referencingEntityService.listBasedOnContextualRoles(neutralQuery);
                        } else {
                            entityList = referencingEntityService.list(neutralQuery);
                        }

                        for (EntityBody entityBody : entityList) {
                            String idToBePatched = (String) entityBody.get("id");
                            List<?> basicDBList = (List<?>) entityBody.get(referenceField);
                            basicDBList.remove(sourceId);
                            EntityBody patchEntityBody = new EntityBody();
                            patchEntityBody.put(referenceField, basicDBList);
                            if (SecurityUtil.isStaffUser()) {
                                referencingEntityService.patchBasedOnContextualRoles(idToBePatched, patchEntityBody);
                            } else {
                                referencingEntityService.patch(idToBePatched, patchEntityBody);
                            }
                        }
                    } else {
                        // list all entities that have the deleted entity's ID in their reference
                        // field (for deletion)

                        Iterable<EntityBody> entityList;
                        if (SecurityUtil.isStaffUser()) {
                            entityList = referencingEntityService.listBasedOnContextualRoles(neutralQuery);
                        } else {
                            entityList = referencingEntityService.list(neutralQuery);
                        }
                        for (EntityBody entityBody : entityList) {
                            String idToBeDeleted = (String) entityBody.get("id");
                            // delete that entity as well
                            if (SecurityUtil.isStaffUser()) {
                                referencingEntityService.deleteBasedOnContextualRoles(idToBeDeleted);
                            } else {
                                referencingEntityService.delete(idToBeDeleted);
                            }
                            // delete custom entities attached to this entity
                            deleteAttachedCustomEntities(idToBeDeleted);
                        }
                    }
                } catch (AccessDeniedException ade) {
                    LOG.debug("No {} have {}={}", new Object[]{referencingEntity.getResourceName(), referenceField,
                            sourceId});
                }
            }
        }
    }

    private void deleteAttachedCustomEntities(String sourceId) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", sourceId, false));
        Iterable<String> ids = getRepo().findAllIds(CUSTOM_ENTITY_COLLECTION, query);
        for (String id : ids) {
            getRepo().delete(CUSTOM_ENTITY_COLLECTION, id);
        }
    }

    protected boolean isSelf(NeutralQuery query) {

        //This checks if they're querying for a self entity.  It's overly convoluted because going to
        //resourcename/<ID> calls this method instead of calling get(String id)
        List<NeutralCriteria> allTheCriteria = query.getCriteria();
        for (NeutralQuery orQuery: query.getOrQueries()) {
            if(!isSelf(orQuery)) {
                return false;
            }
        }
        for(NeutralCriteria criteria: allTheCriteria) {
            if (criteria.getOperator().equals(NeutralCriteria.CRITERIA_IN) && criteria.getValue() instanceof List) {
                // key IN [{self id}]
                List<?> value = (List<?>) criteria.getValue();
                if (value.size() == 1 && isSelf(value.get(0).toString())) {
                    return true;
                }
            } else if (criteria.getOperator().equals(NeutralCriteria.OPERATOR_EQUAL) && criteria.getValue() instanceof String) {
                if (isSelf((String) criteria.getValue())){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSelf(String entityId) {
        SLIPrincipal principal = SecurityUtil.getSLIPrincipal();
        String selfId = principal.getEntity().getEntityId();
        Collection<String> studentIds = principal.getOwnedStudentIds();
        String type = defn.getType();
        if (selfId != null) {
            if (selfId.equals(entityId) || studentIds.contains(entityId)) {
                return true;
            } else if (EntityNames.STAFF_ED_ORG_ASSOCIATION.equals(type)) {
                Entity entity = repo.findById(defn.getStoredCollectionName(), entityId);
                if (entity != null) {
                    Map<String, Object> body = entity.getBody();
                    return selfId.equals(body.get(ParameterConstants.STAFF_REFERENCE));
                }
            } else if (EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(type)) {
                Entity entity = repo.findById(defn.getStoredCollectionName(), entityId);
                if (entity != null) {
                    Map<String, Object> body = entity.getBody();
                    return selfId.equals(body.get(ParameterConstants.TEACHER_ID));
                }
            } else if (SecurityUtil.isStudentOrParent() && STUDENT_SELF.contains(type)) {
                Entity entity = repo.findById(defn.getStoredCollectionName(), entityId);
                if (entity != null) {
                    Set<String> owned = principal.getOwnedStudentIds();
                    return owned.contains(entity.getBody().get(ParameterConstants.STUDENT_ID)) || owned.contains(entityId);
                }
            }
        }
        return false;
    }

    private Collection<GrantedAuthority> getAuths(boolean isSelf) {
        Collection<GrantedAuthority> result = new HashSet<GrantedAuthority>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        result.addAll(auth.getAuthorities());

        if (isSelf) {
            SLIPrincipal principal = SecurityUtil.getSLIPrincipal();
            result.addAll(principal.getSelfRights());
        }
        return result;
    }

    /**
     * Checks query params for access restrictions
     *
     * @param query The query to check
     */
    protected void checkFieldAccess(NeutralQuery query, boolean isSelf) {

        if (query != null) {
            // get the authorities
            Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
            auths.addAll(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            if (isSelf) {
                auths.addAll(SecurityUtil.getSLIPrincipal().getSelfRights());
            }

            rightAccessValidator.checkFieldAccess(query, defn.getType(), auths);
        }
    }

    /**
     * Determines if there is a union of all needed rights with the user's collection of granted
     * authorities.
     *
     * @param authorities  User's collection of granted authorities.
     * @param neededRights Set of rights needed for accessing a given field.
     * @return True if the user can access the field, false otherwise.
     */
    protected boolean union(Collection<GrantedAuthority> authorities, Set<Right> neededRights) {
        boolean union = true;
        for (Right neededRight : neededRights) {
            if (!authorities.contains(neededRight)) {
                union = false;
                break;
            }
        }
        return union;
    }

    /**
     * Creates the metaData HashMap to be added to the entity created in mongo.
     *
     * @return Map containing important metadata for the created entity.
     */
    private Map<String, Object> createMetadata() {
        Map<String, Object> metadata = new HashMap<String, Object>();
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String createdBy = principal.getEntity().getEntityId();
        if (createdBy != null && createdBy.equals("-133")) {
            createdBy = principal.getExternalId();
        }
        metadata.put("createdBy", createdBy);
        metadata.put("isOrphaned", "true");
        metadata.put("tenantId", principal.getTenantId());

        return metadata;
    }

    private Entity getEntity(String id) {
        NeutralQuery entityQuery = new NeutralQuery();
        entityQuery.addCriteria(new NeutralCriteria("_id", "=", id));

        Entity entity = repo.findOne(collectionName, entityQuery);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    private Entity getCustomEntity(String id, String clientId) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));

        return getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);
    }

    /**
     * Set the entity definition for this service. There is a circular dependency between
     * BasicService and
     * EntityDefinition, so they both can't have it be a constructor arg.
     */
    public void setDefn(EntityDefinition defn) {
        this.defn = defn;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        return defn;
    }

    protected String getCollectionName() {
        return collectionName;
    }

    protected List<Treatment> getTreatments() {
        return treatments;
    }

    protected Repository<Entity> getRepo() {
        return repo;
    }

    public long getCountLimit() {
        return countLimit;
    }

    public int getBatchSize() {
        return batchSize;
    }

    protected void setClientInfo(CallingApplicationInfoProvider clientInfo) {
        this.clientInfo = clientInfo;
    }

    @Override
    public CalculatedData<String> getCalculatedValues(String id) {
        Entity entity = getEntity(id, new NeutralQuery());
        return entity.getCalculatedValues();
    }

    @Override
    public CalculatedData<Map<String, Integer>> getAggregates(String id) {
        Entity entity = getEntity(id, new NeutralQuery());
        return entity.getAggregates();
    }

    @Override
    public boolean collectionExists(String collection) {
        return getRepo().collectionExists(collection);
    }

    private void injectSecurity(NeutralQuery nq) {
        SLIPrincipal prince = SecurityUtil.getSLIPrincipal();
        List<NeutralQuery> obligations = prince.getObligation(this.collectionName);

        for (NeutralQuery obligation : obligations) {
            nq.addOrQuery(obligation);
        }
    }

    protected Map<String, UserContext> getEntityContextMap(Collection<Entity> entities, boolean isRead) {
        return contextValidator.getValidatedEntityContexts(defn, entities, SecurityUtil.isTransitive(), isRead);
    }

    protected Collection<GrantedAuthority> getEntityContextAuthorities(Entity entity, boolean isSelf, boolean isRead) {
        UserContext context = SecurityUtil.getUserContext();

        if (context == UserContext.DUAL_CONTEXT) {
            Map<String, UserContext> entityContext = getEntityContextMap(Arrays.asList(entity), isRead);

            if (entityContext != null) {
                context = entityContext.get(entity.getEntityId());
            }
        }

        return rightAccessValidator.getContextualAuthorities(isSelf, entity, context, isRead);
    }

    protected long getAccessibleEntitiesCount(String collectionName) {
        return rolesListingCacheTL.get().getAccessibleEntitiesCount(collectionName);
    }

    protected void setAccessibleEntitiesCount(String collectionName, long count) {
        rolesListingCacheTL.get().setAccessibleEntitiesCount(collectionName, count);
    }

    protected Map<String, UserContext> getEntityContexts() {
        return rolesListingCacheTL.get().getEntityContexts();
    }

    protected void setEntityContexts(Map<String, UserContext> entityContexts) {
        rolesListingCacheTL.get().setEntityContexts(entityContexts);
    }

    protected Collection<GrantedAuthority> getEntityAuthorities(String entityId) {
        return rolesListingCacheTL.get().getEntityAuthorities(entityId);
    }

    protected void setEntityAuthorities(String entityId, Collection<GrantedAuthority> authorities) {
        rolesListingCacheTL.get().setEntityAuthorities(entityId, authorities);
    }

}
