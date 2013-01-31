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
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.BasicDefinitionStore;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
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

    private Repository<Entity> repo;
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> securityRepo;

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

    public BasicService(String collectionName, List<Treatment> treatments, Right readRight, Right writeRight,
            Repository<Entity> repo) {
        this.collectionName = collectionName;
        this.treatments = treatments;
        this.readRight = readRight;
        this.writeRight = writeRight;
        this.repo = repo;
        if (repo == null) {
            throw new IllegalArgumentException("Please provide repo");
        }
    }

    public BasicService(String collectionName, List<Treatment> treatments, Repository<Entity> repo) {
        this(collectionName, treatments, Right.READ_GENERAL, Right.WRITE_GENERAL, repo);
    }

    @Override
    public long count(NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        return getRepo().count(collectionName, neutralQuery);
    }

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     *
     * @param neutralQuery
     *            all parameters to be included in query
     * @return the body of the entity
     */
    @Override
    public Iterable<String> listIds(final NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        NeutralQuery nq = neutralQuery;
        Iterable<Entity> entities = repo.findAll(collectionName, nq);

        List<String> results = new ArrayList<String>();
        for (Entity entity : entities) {
            results.add(entity.getEntityId());
        }
        return results;
    }

    @Override
    public String create(EntityBody content) {

        // if service does not allow anonymous write access, check user rights
        if (writeRight != Right.ANONYMOUS_ACCESS) {
            checkAllRights(determineWriteAccess(content, ""), true);
        }

        checkReferences(content);

        String entityId = "";
        Entity entity = getRepo().create(defn.getType(), sanitizeEntityBody(content), createMetadata(), collectionName);
        if (entity != null) {
            entityId = entity.getEntityId();
        }

        return entityId;
    }

    private void checkAllRights(Set<Right> neededRights, boolean isWrite) {
        Collection<GrantedAuthority> auths = getAuths();

        if (auths.contains(Right.FULL_ACCESS)) {
            debug("User has full access");
        } else if (neededRights.isEmpty()) {
            debug("User is granted access because there are no needed rights.");
        } else if (!isWrite && intersection(auths, neededRights)) {
            debug("User is performing READ and has at least one of the needed rights: {}", neededRights);
        } else if (isWrite && union(auths, neededRights)) {
            debug("User is performing WRITE and has all of the needed rights: {}", neededRights);
        } else {
            throw new AccessDeniedException("Insufficient Privileges");
        }
    }

    @Override
    public void delete(String id) {

        checkAccess(writeRight, id);

        try {
            cascadeDelete(id);
        } catch (RuntimeException re) {
            debug(re.toString());
        }

        if (!getRepo().delete(collectionName, id)) {
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
        Entity entity = getRepo().findOne(collectionName, query);
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

    private Entity getEntity(String id, final NeutralQuery neutralQuery) {
        checkAccess(readRight, id);
        checkFieldAccess(neutralQuery);

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
    public Iterable<EntityBody> get(Iterable<String> ids, final NeutralQuery neutralQuery) {
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
            NeutralQuery nq = neutralQuery;
            if (nq == null) {
                nq = new NeutralQuery();
                nq.setOffset(0);
                nq.setLimit(MAX_RESULT_SIZE);
            }

            // add the ids requested
            nq.addCriteria(new NeutralCriteria("_id", "in", idList));

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
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

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

    @Override
    public EntityBody getCustom(String id) {
        checkAccess(readRight, id);

        String clientId = getClientId();

        debug("Reading custom entity: entity={}, entityId={}, clientId={}", new Object[] {
                getEntityDefinition().getType(), id, clientId });

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
        debug("Deleting custom entity: entity={}, entityId={}, clientId={}, deleted?={}", new Object[] {
                getEntityDefinition().getType(), id, clientId, String.valueOf(deleted) });
    }

    @Override
    public void createOrUpdateCustom(String id, EntityBody customEntity) throws EntityValidationException {
        checkAccess(writeRight, id);

        String clientId = getClientId();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));

        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);

        if (entity != null && entity.getBody().equals(customEntity)) {
            debug("No change detected to custom entity, ignoring update: entity={}, entityId={}, clientId={}",
                    new Object[] { getEntityDefinition().getType(), id, clientId });

            return;
        }

        // Verify field names contain no blacklisted components.
        List<ValidationError> errorList = customEntityValidator.validate(customEntity);
        if (!errorList.isEmpty()) {
            debug("Blacklist validation failed for custom entity {}", id);
            throw new EntityValidationException(id, PathConstants.CUSTOM_ENTITIES, errorList);
        }

        EntityBody clonedEntity = new EntityBody(customEntity);

        if (entity != null) {
            debug("Overwriting existing custom entity: entity={}, entityId={}, clientId={}", new Object[] {
                    getEntityDefinition().getType(), id, clientId });
            entity.getBody().clear();
            entity.getBody().putAll(clonedEntity);
            getRepo().update(CUSTOM_ENTITY_COLLECTION, entity);
        } else {
            debug("Creating new custom entity: entity={}, entityId={}, clientId={}", new Object[] {
                    getEntityDefinition().getType(), id, clientId });
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
            try {
                contextValidator.validateContextToEntities(def, ids, true);
            } catch (AccessDeniedException e) {
                debug("Invalid Reference: {} in {} is not accessible by user", value, def.getStoredCollectionName());
                throw (AccessDeniedException) new AccessDeniedException(
                        "Invalid reference. No association to referenced entity.").initCause(e);
            } catch (EntityNotFoundException e) {
                debug("Invalid Reference: {} in {} does not exist", value, def.getStoredCollectionName());
                throw (AccessDeniedException) new AccessDeniedException(
                        "Invalid reference. No association to referenced entity.").initCause(e);
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
     * Deletes any object with a reference to the given sourceId. Assumes that the sourceId still
     * exists so that
     * authorization/context can be checked.
     *
     * @param sourceId
     *            ID that was deleted, where anything else with that ID should also be deleted
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
                    debug("No {} have {}={}", new Object[] { referencingEntity.getResourceName(), referenceField,
                            sourceId });
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
     * Checks that Actor has the appropriate Rights and linkage to access given entity Also checks
     * for existence of the
     * given entity
     *
     * @param right
     *            needed Right for action
     * @param entityId
     *            id of the entity to access
     * @throws EntityNotFoundException
     *             if requested entity doesn't exist
     * @throws AccessDeniedException
     *             if actor doesn't have association path to given entity
     */
    @SuppressWarnings("unchecked")
    private void checkAccess(Object right, String entityId) {
    	if (isSelf(entityId)) {
    		
    	}
    	
        // Check that user has the needed right(s)
        if (right instanceof Right) {
            checkRights((Right) right);
        } else if (Set.class.isAssignableFrom(right.getClass())) {
            checkAllRights((Set<Right>) right, true);
        } else {
            throw new IllegalArgumentException("Passed right wasn't a right or a list of rights");
        }

        // Check that target entity actually exists
        if (securityRepo.findById(collectionName, entityId) == null) {
            warn("Could not find {}", entityId);
            throw new EntityNotFoundException(entityId);
        }

        if (right != Right.ANONYMOUS_ACCESS) {
            // Check that target entity is accessible to the actor
            if (entityId != null && !isEntityAllowed(entityId, collectionName, defn.getType())) {
                throw new AccessDeniedException("No association between the user and target entity");
            }
        }
    }
    
    private boolean isSelf(String entityId) {
    	SLIPrincipal principal = SecurityUtil.getSLIPrincipal();
    	String curId = principal.getEntity().getEntityId();
    	return curId.equals(entityId);
    }

    /**
     * Checks to see if the entity id is allowed by security
     *
     * @param entityId
     *            The id to check
     * @return
     */
    private boolean isEntityAllowed(String entityId, String collectionName, String toType) {
        SecurityCriteria securityCriteria = new SecurityCriteria();
        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);
        query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(entityId)));
        Entity found = getRepo().findOne(collectionName, query);
        return found != null;
    }

    private void checkRights(final Right neededRight) {
        Right nRight = neededRight;

        // anonymous access is always granted
        if (nRight == Right.ANONYMOUS_ACCESS) {
            return;
        }

        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            nRight = Right.ADMIN_ACCESS;
        }

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            if (Right.READ_GENERAL.equals(nRight)) {
                nRight = Right.READ_PUBLIC;
            }
        }

        Collection<GrantedAuthority> auths = getAuths();

        if (auths.contains(Right.FULL_ACCESS)) {
            debug("User has full access");
        } else if (auths.contains(nRight)) {
            debug("User has needed right: {}", nRight);
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

                for (Iterator<Map<String, Object>> it = telephones.iterator(); it.hasNext();) {
                    if (!work.equals(it.next().get(telephoneNumberType))) {
                        it.remove();
                    }
                }

            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> emails = (List<Map<String, Object>>) eb.get(electronicMail);
            if (emails != null) {

                for (Iterator<Map<String, Object>> it = emails.iterator(); it.hasNext();) {
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

                String fieldPath = prefix.replaceAll("^.", "") + fieldName;
                Set<Right> neededRights = getNeededRights(fieldPath);

                SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                if (!intersection(auths, neededRights) && !principal.getEntity().getEntityId().equals(eb.get("id"))) {
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
     * @param fieldPath
     *            The field name
     * @return
     */
    protected Set<Right> getNeededRights(String fieldPath) {
        Set<Right> neededRights = provider.getRequiredReadLevels(defn.getType(), fieldPath);

        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            neededRights.add(Right.ADMIN_ACCESS);
        }

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            if (neededRights.contains(Right.READ_GENERAL)) {
                neededRights.add(Right.READ_PUBLIC);
            }
        }
        
        return neededRights;
    }

    /**
     * Checks query params for access restrictions
     *
     * @param query
     *            The query to check
     */
    protected void checkFieldAccess(NeutralQuery query) {

        if (query != null) {
            // get the authorities
            Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities();

            if (!auths.contains(Right.FULL_ACCESS) && !auths.contains(Right.ANONYMOUS_ACCESS)) {
                for (NeutralCriteria criteria : query.getCriteria()) {
                    // get the needed rights for the field
                    Set<Right> neededRights = getNeededRights(criteria.getKey());

                    if (!intersection(auths, neededRights)) {
                        throw new QueryParseException("Cannot search on restricted field", criteria.getKey());
                    }
                }
            }
        }
    }

    /**
     * Determines if there is an intersection of a single needed right within the user's collection
     * of granted authorities.
     *
     * @param authorities
     *            User's collection of granted authorities.
     * @param neededRights
     *            Set of rights needed for accessing a given field.
     * @return True if the user can access the field, false otherwise.
     */
    protected boolean intersection(Collection<GrantedAuthority> authorities, Set<Right> neededRights) {
        boolean intersects = false;
        for (GrantedAuthority authority : authorities) {
            if (neededRights.contains(authority)) {
                intersects = true;
                break;
            }
        }
        return intersects;
    }

    /**
     * Determines if there is a union of all needed rights with the user's collection of granted
     * authorities.
     *
     * @param authorities
     *            User's collection of granted authorities.
     * @param neededRights
     *            Set of rights needed for accessing a given field.
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
     * Figures out if writing to restricted fields
     *
     * @param eb
     *            data currently being passed in
     * @return WRITE_RESTRICTED if restricted fields are being written, WRITE_GENERAL otherwise
     */
    @SuppressWarnings("unchecked")
    private Set<Right> determineWriteAccess(Map<String, Object> eb, String prefix) {
        Set<Right> toReturn = new HashSet<Right>();
        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            toReturn.add(Right.ADMIN_ACCESS);
        } else {
            for (Map.Entry<String, Object> entry : eb.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Map) {
                    filterFields((Map<String, Object>) value, prefix + "." + fieldName + ".");
                } else {
                    String fieldPath = prefix + fieldName;
                    Set<Right> neededRights = provider.getRequiredWriteLevels(defn.getType(), fieldPath);
                    debug("Field {} requires {}", fieldPath, neededRights);
                    toReturn.addAll(neededRights);
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
}
