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

package org.slc.sli.validation.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;

/**
 * This validator is used to get the naturalKeyFields for an entity.
 * This validator applies specifically to the API CRUD operations,
 * as the natural keys are not currently applicable for ingestion.
 *
 * @author John Cole <jcole@wgen.net>
 *
 */
public class ApiNeutralSchemaValidator extends NeutralSchemaValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ApiNeutralSchemaValidator.class);
    
    private static final DeterministicUUIDGeneratorStrategy DETERMINISTIC_UUID_GENERATOR = new DeterministicUUIDGeneratorStrategy();

    private static final boolean INSERT_OR_UPDATE = true;
    private static final boolean PATCH = false;
    
    @Autowired
    INaturalKeyExtractor naturalKeyExtractor;

    @Override
    public boolean validate(Entity entity) throws EntityValidationException {
        this.validateNaturalKeys(entity, INSERT_OR_UPDATE);
        return super.validate(entity);
    }
    

    @Override
    public boolean validatePresent(Entity entity) throws EntityValidationException {
        this.validateNaturalKeys(entity, PATCH);
        return super.validatePresent(entity);
    }

    /**
     * Validates natural keys against a given entity
     *
     * @param entity
     * @return
     */
    protected void validateNaturalKeys(final Entity entity, boolean clearOriginal) {
        String collectionName = entity.getType();
        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());

        if (schema != null && schema.getAppInfo() != null && schema.getAppInfo().getCollectionType() != null) {
            collectionName = schema.getAppInfo().getCollectionType();
        }

        Map<String, Boolean> naturalKeyFields = new HashMap<String, Boolean>();
        try {
            naturalKeyFields = naturalKeyExtractor.getNaturalKeyFields(entity);
        } catch (NaturalKeyValidationException e) {
            // swallow exception. if there are missing keys fields,
            // they will be validated in the validate method
            LOG.error(e.getMessage(), e);
            return;
        } catch (NoNaturalKeysDefinedException e) {
            // swallow exception. if there are missing keys fields,
            // they will be validated in the validate method
            LOG.error(e.getMessage(), e);
            return;
        }

        if (naturalKeyFields != null && naturalKeyFields.size() != 0) {
            String entityId = entity.getEntityId();

            // if we have an existing entityId, then we're doing an update. Check to
            // make sure that there is no existing entity with the new key fields of the entity
            if (entityId != null && !entityId.isEmpty()) {
                NeutralQuery neutralQuery = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL,
                        entityId, false));

                Entity existingEntity = validationRepo.findOne(collectionName, neutralQuery);
                if (existingEntity != null) {
                    this.validateNaturalKeysUnchanged(existingEntity, entity, naturalKeyFields, clearOriginal);
                }
            } else {
                this.validateNaturalKeyDoesNotConflictWithExistingDocument(entity, collectionName, naturalKeyFields);
            }
        }
    }
    
    /**
     * Given an entity, returns its generated UUID.
     * 
     * 
     * @param entity
     * @return
     * @throws NoNaturalKeysDefinedException
     */
    private String calculateUUID(Entity entity) throws NoNaturalKeysDefinedException {

        NaturalKeyDescriptor existingEntityNaturalKeyDescriptor = new NaturalKeyDescriptor();
        existingEntityNaturalKeyDescriptor.setNaturalKeys(naturalKeyExtractor.getNaturalKeys(entity));
        existingEntityNaturalKeyDescriptor.setEntityType(entity.getType());
        existingEntityNaturalKeyDescriptor.setTenantId(TenantContext.getTenantId());
        
        return DETERMINISTIC_UUID_GENERATOR.generateId(existingEntityNaturalKeyDescriptor);
    }
    
    /**
     * Calculates the existing document's UUID and the updated version's UUID and compares them, throwing a
     * NaturalKeyValidationException if the two UUIDs do not match.
     * 
     * 
     * @param originalEntity
     * @param newEntity
     * @param naturalKeyFields
     * @param clearOriginal
     */
    private void validateNaturalKeysUnchanged(Entity originalEntity, Entity newEntity, Map<String, Boolean> naturalKeyFields, boolean clearOriginal) {
        try {
            // calculate current UUID before any changes applied
            String existingUUID = this.calculateUUID(originalEntity);
            
            // true when doing a PUT, false when doing a PATCH
            if (clearOriginal) {
                originalEntity.getBody().clear();
            }
            
            // apply the patch. 
            originalEntity.getBody().putAll(newEntity.getBody());

            // recalculate the natural key
            String newUUID = this.calculateUUID(originalEntity);

            // error if generated UUID does not match existing UUID. This implies a forbidden natural key change
            if (!newUUID.equals(existingUUID)) {
                throw new NaturalKeyValidationException(originalEntity.getType(), new ArrayList<String>(naturalKeyFields.keySet()));
            }
        } catch (NoNaturalKeysDefinedException e) {
            throw new NaturalKeyValidationException(e, originalEntity.getType(), new ArrayList<String>(naturalKeyFields.keySet()));
        }
    }
    
    /**
     * Calculates the new entity's UUID and confirms that no document with that UUID already exists. If one exists,
     * a NaturalKeyValidationException is thrown.
     * 
     * 
     * @param entity
     * @param collectionName
     * @param naturalKeyFields
     */
    private void validateNaturalKeyDoesNotConflictWithExistingDocument(Entity entity, String collectionName, Map<String, Boolean> naturalKeyFields) {
        NeutralQuery neutralQuery = new NeutralQuery();
        Map<String, Object> newEntityBody = entity.getBody();
        for (Entry<String, Boolean> keyField : naturalKeyFields.entrySet()) {
            neutralQuery.addCriteria(new NeutralCriteria(keyField.getKey(), NeutralCriteria.OPERATOR_EQUAL,
                    newEntityBody.get(keyField.getKey())));
        }
        neutralQuery.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL,
                entity.getMetaData().get("tenantId"), false));

        Entity existingEntity = validationRepo.findOne(collectionName, neutralQuery);
        if (existingEntity != null) {
            throw new NaturalKeyValidationException(entity.getType(), new ArrayList<String>(naturalKeyFields.keySet()));
        }
    }

}
