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

package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.BasicDefinitionStore;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.resolver.AllowAllEntityContextResolver;
import org.slc.sli.api.security.context.resolver.DenyAllContextResolver;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.security.service.SecurityCriteria;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Implementation of EntityService that can be used for most entities.
 * <p/>
 * <p/>
 * It is very important this bean prototype scope, since one service is needed per
 * entity/association.
 */
@Scope("prototype")
@Component("basicService")
public class BasicService implements EntityService {

    private static final String ADMIN_SPHERE = "Admin";
    private static final String PUBLIC_SPHERE = "Public";

    private static final int MAX_RESULT_SIZE = 0;

    private static final String CUSTOM_ENTITY_COLLECTION = "custom_entities";
    private static final String CUSTOM_ENTITY_CLIENT_ID = "clientId";
    private static final String CUSTOM_ENTITY_ENTITY_ID = "entityId";

    private String collectionName;
    private List<Treatment> treatments;
    private EntityDefinition defn;

    private Right readRight;
    private Right writeRight; // this is possibly the worst named variable ever

    private static final boolean ENABLE_CONTEXT_RESOLVING = false;
    public static final Set<String> VALIDATOR_ENTITIES = new HashSet<String>(
//            Arrays.asList(
//                    EntityNames.STUDENT,
//                    EntityNames.STUDENT_SCHOOL_ASSOCIATION
//            )
    );

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private ContextResolverStore contextResolverStore;

    @Autowired
    private ContextValidator contextValidator;

    @Autowired
    private SchemaDataProvider provider;

    @Autowired
    private CallingApplicationInfoProvider clientInfo;

    @Autowired
    private BasicDefinitionStore definitionStore;

    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    @Value("${sli.security.in_clause_size}")
    private String securityInClauseSize;

    public BasicService(String collectionName, List<Treatment> treatments, Right readRight, Right writeRight) {
        this.collectionName = collectionName;
        this.treatments = treatments;
        this.readRight = readRight;
        this.writeRight = writeRight;
    }

    public BasicService(String collectionName, List<Treatment> treatments) {
        this(collectionName, treatments, Right.READ_GENERAL, Right.WRITE_GENERAL);
    }

    @Override
    public long count(NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        if (useContextResolver()) {
            NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);
            SecurityCriteria securityCriteria = findAccessible(defn.getType());
            localNeutralQuery = securityCriteria.applySecurityCriteria(localNeutralQuery);
            return repo.count(collectionName, localNeutralQuery);
        } else {
            return repo.count(collectionName, neutralQuery);
        }
    }

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     *
     * @param neutralQuery all parameters to be included in query
     * @return the body of the entity
     */
    @Override
    public Iterable<String> listIds(NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        if (useContextResolver()) {
            SecurityCriteria securityCriteria = findAccessible(defn.getType());
            neutralQuery = securityCriteria.applySecurityCriteria(neutralQuery);
        }
        Iterable<Entity> entities = repo.findAll(collectionName, neutralQuery);

        List<String> results = new ArrayList<String>();
        for (Entity entity : entities) {
            results.add(entity.getEntityId());
        }
        return results;
    }

    @Override
    public String create(EntityBody content) {
        // DE260 - Logging of possibly sensitive data
        // LOG.debug("Creating a new entity in collection {} with content {}", new Object[] {
        // collectionName, content });

        // if service does not allow anonymous write access, check user rights
        if (writeRight != Right.ANONYMOUS_ACCESS) {
            checkRights(determineWriteAccess(content, ""));
        }

        checkReferences(content);

        String entityId = "";
        Entity entity = repo.create(defn.getType(), sanitizeEntityBody(content), createMetadata(), collectionName);
        if (entity != null) {
            entityId = entity.getEntityId();
        }

        return entityId;
    }

    @Override
    public void delete(String id) {
        // DE260 - Logging of possibly sensitive data
        // LOG.debug("Deleting {} in {}", new String[] { id, collectionName });

        checkAccess(writeRight, id);

        try {
            cascadeDelete(id);
        } catch (RuntimeException re) {
            debug(re.toString());
        }

        if (!repo.delete(collectionName, id)) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        deleteAttachedCustomEntities(id);
    }

    @Override
    public boolean update(String id, EntityBody content) {
        debug("Updating {} in {}", id, collectionName);

        if (writeRight != Right.ANONYMOUS_ACCESS) {
            checkAccess(determineWriteAccess(content, ""), id);
        }

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));
        Entity entity = repo.findOne(collectionName, query);
        // Entity entity = repo.findById(collectionName, id);
        if (entity == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        EntityBody sanitized = sanitizeEntityBody(content);
        if (entity.getBody().equals(sanitized)) {
            info("No change detected to {}", id);
            return false;
        }

        checkReferences(content);

        info("new body is {}", sanitized);
        entity.getBody().clear();
        entity.getBody().putAll(sanitized);

        boolean success = repo.update(collectionName, entity);
        return success;
    }

    @Override
    public boolean patch(String id, EntityBody content) {
        debug("Patching {} in {}", id, collectionName);

        if (writeRight != Right.ANONYMOUS_ACCESS) {
            checkAccess(determineWriteAccess(content, ""), id);
        }

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        if (repo.findOne(collectionName, query) == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        EntityBody sanitized = sanitizeEntityBody(content);

        info("patch value(s): ", sanitized);

        // don't check references until things are combined
        checkReferences(sanitized);

        repo.patch(defn.getType(), collectionName, id, sanitized);

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

    private Entity getEntity(String id, NeutralQuery neutralQuery) {
        checkAccess(readRight, id);
        checkFieldAccess(neutralQuery);

        if (neutralQuery == null) {
            neutralQuery = new NeutralQuery();
        }
        neutralQuery.addCriteria(new NeutralCriteria("_id", "=", id));

        Entity entity = repo.findOne(collectionName, neutralQuery);
        return entity;
    }

    private Iterable<EntityBody> noEntitiesFound(NeutralQuery neutralQuery) {
        // this.addDefaultQueryParams(neutralQuery, collectionName);
        if (!repo.findAll(collectionName, neutralQuery).iterator().hasNext()) {
            return new ArrayList<EntityBody>();
        } else {
            throw new AccessDeniedException("Access to resource denied.");
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
    public Iterable<EntityBody> get(Iterable<String> ids, NeutralQuery neutralQuery) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        List<String> idList = new ArrayList<String>();

        for (String id : ids) {
            idList.add(id);
        }

        if (!idList.isEmpty()) {
            if (neutralQuery == null) {
                neutralQuery = new NeutralQuery();
                neutralQuery.setOffset(0);
                neutralQuery.setLimit(MAX_RESULT_SIZE);
            }

            if (useContextResolver()) {
                SecurityCriteria securityCriteria = findAccessible(defn.getType());
                neutralQuery = securityCriteria.applySecurityCriteria(neutralQuery);
            }

            // add the ids requested
            neutralQuery.addCriteria(new NeutralCriteria("_id", "in", idList));

            Iterable<Entity> entities = repo.findAll(collectionName, neutralQuery);

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
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        Collection<Entity> entities;
        if (useContextResolver()) {
            SecurityCriteria securityCriteria = findAccessible(defn.getType());
            NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);
            localNeutralQuery = securityCriteria.applySecurityCriteria(localNeutralQuery);
            entities = (Collection<Entity>) repo.findAll(collectionName, localNeutralQuery);
        } else {
            entities = (Collection<Entity>) repo.findAll(collectionName, neutralQuery);
        }


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
    public boolean exists(String id) {
        checkRights(readRight);

        boolean exists = false;
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        Iterable<Entity> entities = repo.findAll(collectionName, query);

        if (entities != null && entities.iterator().hasNext()) {
            exists = true;
        }

        return exists;
    }

    /**
     * TODO: refactor clientId, entityId out of body into root of mongo document
     * TODO: entity collection should be per application
     */
    @Override
    public EntityBody getCustom(String id) {
        checkAccess(readRight, id);

        String clientId = getClientId();

        debug("Reading custom entity: entity={}, entityId={}, clientId={}", new Object[]{
                getEntityDefinition().getType(), id, clientId});

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));

        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);
        if (entity != null) {
            EntityBody clonedBody = new EntityBody(entity.getBody());
            return clonedBody;
        } else {
            return null;
        }
    }

    /**
     * TODO: refactor clientId, entityId out of body into root of mongo document
     * TODO: entity collection should be per application
     */
    @Override
    public void deleteCustom(String id) {
        checkAccess(writeRight, id);

        String clientId = getClientId();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));

        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);

        if (entity == null) {
            throw new EntityNotFoundException(id);
        }

        boolean deleted = getRepo().delete(CUSTOM_ENTITY_COLLECTION, entity.getEntityId());
        debug("Deleting custom entity: entity={}, entityId={}, clientId={}, deleted?={}", new Object[]{
                getEntityDefinition().getType(), id, clientId, String.valueOf(deleted)});
    }

    /**
     * TODO: refactor clientId, entityId out of body into root of mongo document
     * TODO: entity collection should be per application
     */
    @Override
    public void createOrUpdateCustom(String id, EntityBody customEntity) {
        checkAccess(writeRight, id);

        String clientId = getClientId();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));

        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);

        if (entity != null && entity.getBody().equals(customEntity)) {
            debug("No change detected to custom entity, ignoring update: entity={}, entityId={}, clientId={}",
                    new Object[]{getEntityDefinition().getType(), id, clientId});

            return;
        }

        EntityBody clonedEntity = new EntityBody(customEntity);

        if (entity != null) {
            debug("Overwriting existing custom entity: entity={}, entityId={}, clientId={}", new Object[]{

                    getEntityDefinition().getType(), id, clientId});
            entity.getBody().clear();
            entity.getBody().putAll(clonedEntity);
            getRepo().update(CUSTOM_ENTITY_COLLECTION, entity);
        } else {
            debug("Creating new custom entity: entity={}, entityId={}, clientId={}", new Object[]{
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

    private void checkReferences(EntityBody eb) {
        for (Map.Entry<String, Object> entry : eb.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            String entityType = provider.getReferencingEntity(defn.getType(), fieldName);

            if (value == null || entityType == null) {
                continue;
            }

            debug("Field {} is referencing {}", fieldName, entityType);
            @SuppressWarnings("unchecked")
            List<String> ids = value instanceof List ? (List<String>) value : Arrays.asList((String) value);
            EntityDefinition def = definitionStore.lookupByEntityType(entityType);

            NeutralQuery neutralQuery = new NeutralQuery();
            neutralQuery.setOffset(0);
            neutralQuery.setLimit(MAX_RESULT_SIZE);
            if (useContextResolver()) {
                SecurityCriteria securityCriteria = findAccessible(entityType);
                neutralQuery = securityCriteria.applySecurityCriteria(neutralQuery);
                neutralQuery.addCriteria(new NeutralCriteria("_id", "in", ids));

                Iterable<Entity> entities = repo.findAll(def.getStoredCollectionName(), neutralQuery);
                int found = 0;
                if (entities != null) {
                    for (Iterator<?> it = entities.iterator(); it.hasNext(); it.next()) {
                        found++;
                    }
                }

                if (found != ids.size()) {

                    // Here's the deal - we want to avoid having to index based on createdBy/isOrphan
                    // So found won't include any orphaned entities that the user created.
                    // We do an additional query of the referenced fields without any additional
                    // security criteria
                    // and check the isOrphaned and createdBy on each of those
                    neutralQuery = new NeutralQuery();
                    neutralQuery.setOffset(0);
                    neutralQuery.setLimit(MAX_RESULT_SIZE);
                    neutralQuery.addCriteria(new NeutralCriteria("_id", "in", ids));

                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    SLIPrincipal user = (SLIPrincipal) auth.getPrincipal();
                    String userId = user.getEntity().getEntityId();
                    for (Entity ent : repo.findAll(def.getStoredCollectionName(), neutralQuery)) {

                        if (userId.equals(ent.getMetaData().get("createdBy"))
                                && "true".equals(ent.getMetaData().get("isOrphaned"))) {
                            found++;
                        }
                    }
                    if (found != ids.size()) {
                        debug("{} in {} is not accessible", value, def.getStoredCollectionName());
                        throw new AccessDeniedException("Invalid reference. No association to referenced entity.");
                    }
                }
            } else {
                try {
                    boolean useTransitiveResolver = true;
                    if (PUBLIC_SPHERE.equals(provider.getDataSphere(def.getType()))) {
                        //Transitive resolver for public resources would be too relaxed,
                        //e.g. letting anyone create any association to any edorg
                        useTransitiveResolver = false;
                    }
                    contextValidator.validateContextToEntities(def, ids, useTransitiveResolver);
                } catch (AccessDeniedException e) {
                    debug("Invalid Reference: {} in {} is not accessible by user", value, def.getStoredCollectionName());
                    throw new AccessDeniedException("Invalid reference. No association to referenced entity.");
                } catch (EntityNotFoundException e) {
                    debug("Invalid Reference: {} in {} does not exist", value, def.getStoredCollectionName());
                    throw new AccessDeniedException("Invalid reference. No association to referenced entity.");
                }
            }
        }
    }

    private String getClientId() {
        String clientId = clientInfo.getClientId();
        if (clientId == null) {
            throw new AccessDeniedException("No Application Id");
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
        EntityBody toReturn = createBody(entity);

        if ((entity.getEmbeddedData() != null) && !entity.getEmbeddedData().isEmpty()) {
            for (Map.Entry<String, List<Entity>> enbDocList : entity.getEmbeddedData().entrySet()) {
                List<EntityBody> subDocbody = new ArrayList<EntityBody>();
                for (Entity subEntity : enbDocList.getValue()) {
                    subDocbody.add(createBody(subEntity));
                }
                toReturn.put(enbDocList.getKey(), subDocbody);
            }
        }
        return toReturn;
    }

    private EntityBody createBody(Entity entity) {
        EntityBody toReturn = new EntityBody(entity.getBody());

        for (Treatment treatment : treatments) {
            toReturn = treatment.toExposed(toReturn, defn, entity);
        }

        if (readRight != Right.ANONYMOUS_ACCESS) {
            // Blank out fields inaccessible to the user
            filterFields(toReturn);
        }

        return toReturn;
    }

    /**
     * given an entity body that was exposed, return the version with the treatments reversed
     *
     * @param content
     * @return
     */
    private EntityBody sanitizeEntityBody(EntityBody content) {
        EntityBody sanitized = new EntityBody(content);
        for (Treatment treatment : treatments) {
            sanitized = treatment.toStored(sanitized, defn);
        }
        return sanitized;
    }

    /**
     * Deletes any object with a reference to the given sourceId. Assumes that the sourceId
     * still exists so that authorization/context can be checked.
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
                        for (EntityBody entityBody : referencingEntityService.list(neutralQuery)) {
                            String idToBePatched = (String) entityBody.get("id");
                            List<?> basicDBList = (List<?>) entityBody.get(referenceField);
                            basicDBList.remove(sourceId);
                            EntityBody patchEntityBody = new EntityBody();
                            patchEntityBody.put(referenceField, basicDBList);
                            referencingEntityService.patch(idToBePatched, patchEntityBody);
                        }
                    } else {
                        // list all entities that have the deleted entity's ID in their reference
                        // field (for deletion)
                        for (EntityBody entityBody : referencingEntityService.list(neutralQuery)) {
                            String idToBeDeleted = (String) entityBody.get("id");
                            // delete that entity as well
                            referencingEntityService.delete(idToBeDeleted);
                            // delete custom entities attached to this entity
                            deleteAttachedCustomEntities(idToBeDeleted);
                        }
                    }
                } catch (AccessDeniedException ade) {
                    debug("No {} have {}={}", new Object[]{referencingEntity.getResourceName(), referenceField,
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

    /**
     * Checks that Actor has the appropriate Rights and linkage to access given entity
     * Also checks for existence of the given entity
     *
     * @param right    needed Right for action
     * @param entityId id of the entity to access
     * @throws EntityNotFoundException if requested entity doesn't exist
     * @throws AccessDeniedException   if actor doesn't have association path to given entity
     */
    private void checkAccess(Right right, String entityId) {

        // Check that user has the needed right
        checkRights(right);

        // Check that target entity actually exists
        if (repo.findById(collectionName, entityId) == null) {
            warn("Could not find {}", entityId);
            throw new EntityNotFoundException(entityId);
        }

        // TODO Validate that this is needed?
        if (right != Right.ANONYMOUS_ACCESS) {
            // Check that target entity is accessible to the actor
            if (entityId != null && !isEntityAllowed(entityId, collectionName, defn.getType())) {
                throw new AccessDeniedException("No association between the user and target entity");
            }
        }
    }

    /**
     * Checks to see if the entity id is allowed by security
     *
     * @param entityId The id to check
     * @return
     */
    private boolean isEntityAllowed(String entityId, String collectionName, String toType) {
        SecurityCriteria securityCriteria = findAccessible(toType);
        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);
        query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(entityId)));
        Entity found = repo.findOne(collectionName, query);
        return found != null;
    }

    private void checkRights(Right neededRight) {

        // anonymous access is always granted
        if (neededRight == Right.ANONYMOUS_ACCESS) {
            return;
        }

        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            neededRight = Right.ADMIN_ACCESS;
        }

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            if (Right.READ_GENERAL.equals(neededRight)) {
                neededRight = Right.READ_PUBLIC;
            }
        }

        Collection<GrantedAuthority> auths = getAuths();

        if (auths.contains(Right.FULL_ACCESS)) {
            debug("User has full access");
        } else if (auths.contains(neededRight)) {
            debug("User has needed right: {}", neededRight);
        } else {
            throw new AccessDeniedException("Insufficient Privileges");
        }
    }

    private Collection<GrantedAuthority> getAuths() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (readRight != Right.ANONYMOUS_ACCESS) {
            SecurityUtil.ensureAuthenticated();
        }
        return auth.getAuthorities();
    }

    private SecurityCriteria findAccessible(String toType) {
        SecurityCriteria securityCriteria = new SecurityCriteria();

        if (useContextResolver()) {
            try {
                securityCriteria.setInClauseSize(Long.parseLong(securityInClauseSize));
            } catch (NumberFormatException e) {
                // It defaulted to 100000
            }
            String securityField = "_id";

            SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            securityCriteria.setCollectionName(toType);

            if (principal == null) {
                throw new AccessDeniedException("Principal cannot be found");
            }

            Entity entity = principal.getEntity();
            String type = entity.getType();
            // null for super admins because they don't contain mongo entries

            if (isPublic()) {
                securityCriteria.setSecurityCriteria(null);
                return securityCriteria;
            }

            List<String> allowed = null;
            EntityContextResolver resolver = new DenyAllContextResolver();
            if (!securityCachingStrategy.contains(toType)) {
                resolver = contextResolverStore.findResolver(type, toType);
                allowed = resolver.findAccessible(principal.getEntity());
            } else {
                allowed = new ArrayList<String>(securityCachingStrategy.retrieve(toType));
            }

            if (resolver instanceof AllowAllEntityContextResolver) {
                securityCriteria.setSecurityCriteria(null);
            } else {
                securityCriteria.setSecurityCriteria(new NeutralCriteria(securityField, NeutralCriteria.CRITERIA_IN,
                        allowed, false));
            }
        }

        return securityCriteria;
    }

    private boolean isPublic() {
        return getAuths().contains(Right.FULL_ACCESS) || defn.getType().equals(EntityNames.LEARNING_OBJECTIVE)
                || defn.getType().equals(EntityNames.LEARNING_STANDARD)
                || defn.getType().equals(EntityNames.ASSESSMENT) || defn.getType().equals(EntityNames.SCHOOL)
                || defn.getType().equals(EntityNames.EDUCATION_ORGANIZATION)
                || defn.getType().equals(EntityNames.GRADUATION_PLAN);

    }

    /**
     * Removes fields user isn't entitled to see
     *
     * @param eb
     */
    private void filterFields(Map<String, Object> eb) {
        filterFields(eb, "");
        complexFilter(eb);
    }

    private void complexFilter(Map<String, Object> eb) {
        Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (!auths.contains(Right.READ_RESTRICTED) && defn.getType().equals(EntityNames.STAFF)) {
            final String work = "Work";
            final String telephoneNumberType = "telephoneNumberType";
            final String emailAddressType = "emailAddressType";
            final String telephone = "telephone";
            final String electronicMail = "electronicMail";

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> telephones = (List<Map<String, Object>>) eb.get(telephone);
            if (telephones != null) {

                for (Iterator<Map<String, Object>> it = telephones.iterator(); it.hasNext(); ) {
                    if (!work.equals(it.next().get(telephoneNumberType))) {
                        it.remove();
                    }
                }

            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> emails = (List<Map<String, Object>>) eb.get(electronicMail);
            if (emails != null) {

                for (Iterator<Map<String, Object>> it = emails.iterator(); it.hasNext(); ) {
                    if (!work.equals(it.next().get(emailAddressType))) {
                        it.remove();
                    }
                }

            }

        }
    }

    /**
     * Removes fields user isn't entitled to see
     *
     * @param eb
     */
    @SuppressWarnings("unchecked")
    private void filterFields(Map<String, Object> eb, String prefix) {

        Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (!auths.contains(Right.FULL_ACCESS)) {

            List<String> toRemove = new LinkedList<String>();
            for (Map.Entry<String, Object> entry : eb.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                String fieldPath = prefix + fieldName;
                Right neededRight = getNeededRight(fieldPath);


                SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                if (!auths.contains(neededRight) && !principal.getEntity().getEntityId().equals(eb.get("id"))) {
                    toRemove.add(fieldName);
                } else if (value instanceof Map) {
                    filterFields((Map<String, Object>) value, prefix + "." + fieldName + ".");
                }
            }

            for (String fieldName : toRemove) {
                eb.remove(fieldName);
            }
        }
    }

    /**
     * Returns the needed right for a field by examining the schema
     *
     * @param fieldPath The field name
     * @return
     */
    protected Right getNeededRight(String fieldPath) {
        Right neededRight = provider.getRequiredReadLevel(defn.getType(), fieldPath);

        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            neededRight = Right.ADMIN_ACCESS;
        }

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            if (Right.READ_GENERAL.equals(neededRight)) {
                neededRight = Right.READ_PUBLIC;
            }
        }

        return neededRight;
    }

    /**
     * Checks query params for access restrictions
     *
     * @param query The query to check
     */
    protected void checkFieldAccess(NeutralQuery query) {

        if (query != null) {
            // get the authorities
            Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities();

            if (!auths.contains(Right.FULL_ACCESS) && !auths.contains(Right.ANONYMOUS_ACCESS)) {
                for (NeutralCriteria criteria : query.getCriteria()) {
                    // get the needed right for the field
                    Right neededRight = getNeededRight(criteria.getKey());

                    if (!auths.contains(neededRight)) {
                        throw new QueryParseException("Cannot search on restricted field", criteria.getKey());
                    }
                }
            }
        }
    }

    /**
     * Figures out if writing to restricted fields
     *
     * @param eb data currently being passed in
     * @return WRITE_RESTRICTED if restricted fields are being written, WRITE_GENERAL otherwise
     */
    @SuppressWarnings("unchecked")
    private Right determineWriteAccess(Map<String, Object> eb, String prefix) {
        Right toReturn = Right.WRITE_GENERAL;
        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            toReturn = Right.ADMIN_ACCESS;
        } else {

            for (Map.Entry<String, Object> entry : eb.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Map) {
                    filterFields((Map<String, Object>) value, prefix + "." + fieldName + ".");
                } else {
                    String fieldPath = prefix + fieldName;
                    Right neededRight = provider.getRequiredReadLevel(defn.getType(), fieldPath);
                    debug("Field {} requires {}", fieldPath, neededRight);

                    if (neededRight == Right.WRITE_RESTRICTED) {
                        toReturn = Right.WRITE_RESTRICTED;
                        break;
                    }
                }
            }
        }

        return toReturn;
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

    private boolean useContextResolver() {

        boolean useResolvers = true;

        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (principal.getEntity().getType().equals(EntityNames.STAFF)) {
            if (VALIDATOR_ENTITIES.contains(defn.getType())) {
                useResolvers = false;
            } else {
                useResolvers = ENABLE_CONTEXT_RESOLVING;
            }
        }

        return useResolvers;
    }

    /**
     * Set the entity definition for this service.
     * There is a circular dependency between BasicService and EntityDefinition, so they both can't
     * have it be a constructor arg.
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

}
