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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.security.context.APIAccessDeniedException;
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
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.roles.EntityRightsFilter;
import org.slc.sli.api.security.roles.RightAccessValidator;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.security.service.SecurityCriteria;
import org.slc.sli.api.util.SecurityUtil;
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

    @Autowired
    private RightAccessValidator rightAccessValidator;

    @Autowired
    private EntityRightsFilter entityRightsFilter;

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

        Collection<GrantedAuthority> auths = rightAccessValidator.getContextualAuthorities(false, entity);
        rightAccessValidator.checkAccess(false, false, entity, entity.getType(), auths);

        checkReferences(null, content);

        List<String> entityIds = new ArrayList<String>();
        sanitizeEntityBody(content);

        // Ideally, we should validate everything first before actually persisting!
        Entity created = getRepo().create(defn.getType(), content, createMetadata(), collectionName);
        if (created != null) {
            entityIds.add(created.getEntityId());
        }

        return created.getEntityId();
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
        // Check that target entity actually exists
        if (securityRepo.findById(collectionName, entityId) == null) {
            warn("Could not find {}", entityId);
            throw new EntityNotFoundException(entityId);
        }
        Set<Right> rights = provider.getAllFieldRights(defn.getType(), isRead);
        if (rights.equals(new HashSet<Right>(Arrays.asList(Right.ANONYMOUS_ACCESS)))) {
            // Check that target entity is accessible to the actor
            if (entityId != null && !isEntityAllowed(entityId, collectionName, defn.getType())) {
                throw new AccessDeniedException("No association between the user and target entity");
            }
        }

        checkAccess(isRead, isSelf(entityId), content);
    }

    /*
     * Check routine for interface
     * @see org.slc.sli.domain.AccessibilityCheck#accessibilityCheck(java.lang.String)
     */
    @Override
    public boolean accessibilityCheck(String id) {
        try {
            checkAccess(false, id, null);
        } catch (AccessDeniedException e) {
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
        debug("Updating {} in {} with {}", id, collectionName, SecurityUtil.getSLIPrincipal());
        checkAccess(false, id, content);

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));
        Entity entity = getRepo().findOne(collectionName, query);
        // Entity entity = repo.findById(collectionName, id);
        if (entity == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        sanitizeEntityBody(content);

        boolean success = false;
        if (entity.getBody().equals(content)) {
            info("No change detected to {}", id);
            return false;
        }

        checkReferences(id, content);

        info("new body is {}", content);
        entity.getBody().clear();
        entity.getBody().putAll(content);

        success = repo.update(collectionName, entity, FullSuperDoc.isFullSuperdoc(entity));
        return success;
    }

    @Override
    public boolean updateBasedOnContextualRoles(String id, EntityBody content) {
        debug("Updating {} in {} with {}", id, collectionName, SecurityUtil.getSLIPrincipal());

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));
        Entity entity = getRepo().findOne(collectionName, query);
        // Entity entity = repo.findById(collectionName, id);
        if (entity == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        Collection<GrantedAuthority> auths = rightAccessValidator.getContextualAuthorities(false, entity);
        rightAccessValidator.checkAccess(false, content, defn.getType(), auths);

        sanitizeEntityBody(content);

        boolean success = false;
        if (entity.getBody().equals(content)) {
            info("No change detected to {}", id);
            return false;
        }

        checkReferences(id, content);

        info("new body is {}", content);
        entity.getBody().clear();
        entity.getBody().putAll(content);

        success = repo.update(collectionName, entity, FullSuperDoc.isFullSuperdoc(entity));
        return success;
    }

    @Override
    public boolean patch(String id, EntityBody content) {
        debug("Patching {} in {}", id, collectionName);
        checkAccess(false, id, content);

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        if (repo.findOne(collectionName, query) == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        sanitizeEntityBody(content);

        info("patch value(s): ", content);

        // don't check references until things are combined
        checkReferences(id, content);

        repo.patch(defn.getType(), collectionName, id, content);

        return true;
    }

    @Override
    public boolean patchBasedOnContextualRoles(String id, EntityBody content) {
        debug("Patching {} in {}", id, collectionName);

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));

        Entity entity = new MongoEntity(defn.getType(), null, content, createMetadata());

        boolean isSelf = isSelf(query);
        Collection<GrantedAuthority> auths = rightAccessValidator.getContextualAuthorities(isSelf, entity);
        rightAccessValidator.checkAccess(true, isSelf, entity, defn.getType(), auths);

        if (repo.findOne(collectionName, query) == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        sanitizeEntityBody(content);

        info("patch value(s): ", content);

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
        checkFieldAccess(neutralQuery, isSelf);

        injectSecurity(neutralQuery);
        Collection<Entity> entities = (Collection<Entity>) repo.findAll(collectionName, neutralQuery);

        List<EntityBody> results = new ArrayList<EntityBody>();

        for (Entity entity : entities) {
            try {
            Collection<GrantedAuthority> auths = rightAccessValidator.getContextualAuthorities(isSelf, entity);
            rightAccessValidator.checkAccess(true, isSelf, entity, defn.getType(), auths);
            rightAccessValidator.checkFieldAccess(neutralQuery, isSelf, entity, defn.getType(), auths);

            results.add(entityRightsFilter.makeEntityBody(entity, treatments, defn, isSelf, auths));
            } catch (AccessDeniedException aex) {
                if(entities.size() == 1) {
                    throw aex;
                } else {
                    error(aex.getMessage());
                }
            }
        }

        if (results.isEmpty()) {
            return noEntitiesFound(neutralQuery);
        }

        return results;
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
        checkAccess(true, id, null);

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

    @Override
    public void deleteCustom(String id) {
        checkAccess(false, id, null);

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

    @Override
    public void createOrUpdateCustom(String id, EntityBody customEntity) throws EntityValidationException {
        checkAccess(false, id, customEntity);

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

        // Verify field names contain no blacklisted components.
        List<ValidationError> errorList = customEntityValidator.validate(customEntity);
        if (!errorList.isEmpty()) {
            debug("Blacklist validation failed for custom entity {}", id);
            throw new EntityValidationException(id, PathConstants.CUSTOM_ENTITIES, errorList);
        }

        EntityBody clonedEntity = new EntityBody(customEntity);

        if (entity != null) {
            debug("Overwriting existing custom entity: entity={}, entityId={}, clientId={}", new Object[]{
                    getEntityDefinition().getType(), id, clientId});
            entity.getBody().clear();
            entity.getBody().putAll(clonedEntity);
            // custom entity is not superdoc
            getRepo().update(CUSTOM_ENTITY_COLLECTION, entity, false);
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
                    throw new AccessDeniedException("Cannot update student not yourself");
                }
            } else if (entityType.equals(EntityNames.STUDENT_ASSESSMENT)) {
                String studentId = (String) eb.get(ParameterConstants.STUDENT_ID);

                // Validate student ID is yourself
                if (studentId != null && !SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(studentId)) {
                    throw new AccessDeniedException("Cannot update student assessments that are not your own");
                }
            } else if (entityType.equals(EntityNames.STUDENT_GRADEBOOK_ENTRY) || entityType.equals(EntityNames.GRADE)) {
                String studentId = (String) eb.get(ParameterConstants.STUDENT_ID);
                String ssaId = (String) eb.get(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);

                // Validate student ID is yourself
                if (studentId != null && !SecurityUtil.getSLIPrincipal().getEntity().getEntityId().equals(studentId)) {
                    throw new AccessDeniedException("Cannot update " + entityType + " that are not your own");
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
                    throw new AccessDeniedException("Cannot update parent not yourself");
                }
            } else if (entityType.equals(EntityNames.STUDENT)) {
                Set<String> ownStudents = SecurityUtil.getSLIPrincipal().getOwnedStudentIds();
                if (!ownStudents.contains(entityId)) {
                    throw new AccessDeniedException("Cannot update student that are not your own");
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

            debug("Field {} is referencing {}", fieldName, entityType);
            @SuppressWarnings("unchecked")
            List<String> ids = value instanceof List ? (List<String>) value : Arrays.asList((String) value);

            EntityDefinition def = definitionStore.lookupByEntityType(entityType);
            if (def == null) {
                debug("Invalid reference field: {} does not have an entity definition registered", fieldName);
                ValidationError error = new ValidationError(ValidationError.ErrorType.INVALID_FIELD_NAME, fieldName, value, null);
                throw new EntityValidationException(null, null, Arrays.asList(error));
            }

            try {
                contextValidator.validateContextToEntities(def, ids, true);
            } catch (APIAccessDeniedException e) {
                debug("Invalid Reference: {} in {} is not accessible by user", value, def.getStoredCollectionName());
                throw (APIAccessDeniedException) new APIAccessDeniedException(
                        "Invalid reference. No association to referenced entity.", e);
            } catch (AccessDeniedException e) {
                debug("Invalid Reference: {} in {} is not accessible by user", value, def.getStoredCollectionName());
                throw (AccessDeniedException) new AccessDeniedException(
                        "Invalid reference. No association to referenced entity.").initCause(e);
            } catch (EntityNotFoundException e) {
                debug("Invalid Reference: {} in {} does not exist", value, def.getStoredCollectionName());
                throw (APIAccessDeniedException) new APIAccessDeniedException(
                        "Invalid reference. No association to referenced entity.",
                        defn.getType(), entityId).initCause(e);
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

    /**
     * Checks to see if the entity id is allowed by security
     *
     * @param entityId The id to check
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

    private void injectSecurity(NeutralQuery nq) {
        SLIPrincipal prince = SecurityUtil.getSLIPrincipal();
        List<NeutralQuery> obligations = prince.getObligation(this.collectionName);

        for (NeutralQuery obligation : obligations) {
            nq.addOrQuery(obligation);
        }
    }

}
