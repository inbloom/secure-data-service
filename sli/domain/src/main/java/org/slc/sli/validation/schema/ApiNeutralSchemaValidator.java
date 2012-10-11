package org.slc.sli.validation.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
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

        // TODO - a lot of this can be removed with deterministicIds given that we will no
        // longer support updates of key fields

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
                neutralQuery.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL,
                        entity.getMetaData().get("tenantId"), false));
                Entity existingEntity = validationRepo.findOne(collectionName, neutralQuery);
                if (existingEntity != null) {
                    try {
                        DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy = new DeterministicUUIDGeneratorStrategy();

                        NaturalKeyDescriptor originalEntityNaturalKeyDescriptor = new NaturalKeyDescriptor();
                        originalEntityNaturalKeyDescriptor.setNaturalKeys(naturalKeyExtractor.getNaturalKeys(entity));
                        originalEntityNaturalKeyDescriptor.setEntityType(entity.getType());
                        originalEntityNaturalKeyDescriptor.setTenantId((String) entity.getMetaData().get("tenantId"));
                        String originalUUID = deterministicUUIDGeneratorStrategy.generateId(originalEntityNaturalKeyDescriptor);

                        NaturalKeyDescriptor newEntityNaturalKeyDescriptor = new NaturalKeyDescriptor();
                        newEntityNaturalKeyDescriptor.setNaturalKeys(naturalKeyExtractor.getNaturalKeys(existingEntity));
                        newEntityNaturalKeyDescriptor.setEntityType(existingEntity.getType());
                        newEntityNaturalKeyDescriptor.setTenantId((String) existingEntity.getMetaData().get("tenantId"));
                        String newUUID = deterministicUUIDGeneratorStrategy.generateId(newEntityNaturalKeyDescriptor);

                        if (!originalUUID.equals(newUUID)) {
                            throw new NaturalKeyValidationException(entity.getType(), new ArrayList<String>(naturalKeyFields.keySet()));
                        }
                    } catch (NoNaturalKeysDefinedException e) {
                        throw new NaturalKeyValidationException(entity.getType(), new ArrayList<String>(naturalKeyFields.keySet()));
                    }
                }
            }
        }
    }
}
