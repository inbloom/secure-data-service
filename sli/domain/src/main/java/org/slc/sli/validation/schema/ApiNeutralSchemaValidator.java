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

    @Autowired
    INaturalKeyExtractor naturalKeyExtractor;

    @Override
    public boolean validate(Entity entity) throws EntityValidationException {
        validateNaturalKeys(entity);
        return super.validate(entity);
    }

    /**
     * Validates natural keys against a given entity
     *
     * @param entity
     * @return
     */
    protected void validateNaturalKeys(final Entity entity) {
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
                NeutralQuery neutralQuery = new NeutralQuery();

                neutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL,
                        entityId, false));

                Entity existingEntity = validationRepo.findOne(collectionName, neutralQuery);
                if (existingEntity != null) {
                    try {
                        DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy = new DeterministicUUIDGeneratorStrategy();

                        NaturalKeyDescriptor originalEntityNaturalKeyDescriptor = new NaturalKeyDescriptor();
                        originalEntityNaturalKeyDescriptor.setNaturalKeys(naturalKeyExtractor.getNaturalKeys(entity));
                        originalEntityNaturalKeyDescriptor.setEntityType(entity.getType());
                        originalEntityNaturalKeyDescriptor.setTenantId(TenantContext.getTenantId());
                        String originalUUID = deterministicUUIDGeneratorStrategy.generateId(originalEntityNaturalKeyDescriptor);

                        NaturalKeyDescriptor newEntityNaturalKeyDescriptor = new NaturalKeyDescriptor();
                        newEntityNaturalKeyDescriptor.setNaturalKeys(naturalKeyExtractor.getNaturalKeys(existingEntity));
                        newEntityNaturalKeyDescriptor.setEntityType(existingEntity.getType());
                        newEntityNaturalKeyDescriptor.setTenantId(TenantContext.getTenantId());
                        String newUUID = deterministicUUIDGeneratorStrategy.generateId(newEntityNaturalKeyDescriptor);

                        if (!originalUUID.equals(newUUID)) {
                            throw new NaturalKeyValidationException(entity.getType(), new ArrayList<String>(naturalKeyFields.keySet()));
                        }
                    } catch (NoNaturalKeysDefinedException e) {
                        throw new NaturalKeyValidationException(e, entity.getType(), new ArrayList<String>(naturalKeyFields.keySet()));
                    }
                }
            } else {
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
    }
}
