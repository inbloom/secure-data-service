package org.slc.sli.validation.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.ValidationError;

/**
 * This validator is used to get the naturalKeyFields for an entity.
 * This validator applies specifically to the API CRUD operations,
 * as the natural keys are not currently applicable for ingestion.
 *
 * @author John Cole <jcole@wgen.net>
 *
 */
public class ApiNeutralSchemaValidator extends NeutralSchemaValidator {

    @Override
    public boolean validate(Entity entity) throws EntityValidationException {
        validateNaturalKeys(entity);
        return super.validate(entity);
    }

    @Override
    public List<String> getNaturalKeyFields(Entity entity) {

        List<String> naturalKeyFields = new ArrayList<String>();

        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema != null) {

            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                if (appInfo.applyNaturalKeys()) {
                    Map<String, NeutralSchema> fields = schema.getFields();
                    for (Iterator<Map.Entry<String, NeutralSchema>> i = fields.entrySet().iterator(); i.hasNext();) {
                        Map.Entry entry = i.next();
                        String field = (String) entry.getKey();
                        NeutralSchema fieldSchema = (NeutralSchema) entry.getValue();

                        AppInfo fieldsAppInfo = fieldSchema.getAppInfo();
                        if (fieldsAppInfo != null) {
                            if (fieldsAppInfo.isNaturalKey()) {
                                naturalKeyFields.add(field);
                            }
                        }
                    }
                }
            }
        }

        return naturalKeyFields;
    }

    /**
     * Validates natural keys
     * @param entity
     * @return
     */
    private void validateNaturalKeys(final Entity entity) {
        String collectionName = entity.getType();
        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());

        if (schema != null && schema.getAppInfo() != null && schema.getAppInfo().getCollectionType() != null) {
            collectionName = schema.getAppInfo().getCollectionType();
        }

        boolean recordMatchingKeyFields = false;
        List<String> naturalKeyList = getNaturalKeyFields(entity);

        if (naturalKeyList != null && naturalKeyList.size() != 0) {

            Map<String, Object> newEntityBody = entity.getBody();
            boolean possibleMatch = true;
            String entityId = entity.getEntityId();

            // if we have an existing entityId, then we're doing an update. Check to
            // make sure that there is no existing entity with the new key fields of the entity
            if (entityId != null && !entityId.isEmpty()) {
                possibleMatch = false;
                NeutralQuery neutralQuery = new NeutralQuery();
                neutralQuery.addCriteria(new NeutralCriteria("_id", "=", entity.getEntityId()));
                neutralQuery.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL, entity.getMetaData().get("tenantId"), false));

                Entity existingEntity = validationRepo.findOne(collectionName, neutralQuery);
                if (existingEntity != null) {
                    for (String key : naturalKeyList) {

                        Map<String, Object> existingBody = existingEntity.getBody();
                        Object value = existingBody.get(key);
                        if (!newEntityBody.containsKey(key)) {
                            newEntityBody.put(key, value);
                        } else {
                            String existingValueString = value.toString();
                            String newValueString = newEntityBody.get(key).toString();

                            // if all values are equal, then we're ok - as we're just trying to catch
                            // the case where a key field has been changed, and check the new target
                            if (!existingValueString.equals(newValueString)) {
                                possibleMatch = true;
                            }
                        }
                    }
                }
            }

            // At this point we either have a possible match on the entity to create or the new key fields of the entity we're updating
            if (possibleMatch) {
                NeutralQuery neutralQuery = new NeutralQuery();
                for (String key : naturalKeyList) {
                    neutralQuery.addCriteria(new NeutralCriteria(key, NeutralCriteria.OPERATOR_EQUAL, newEntityBody.get(key)));
                }
                neutralQuery.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL, entity.getMetaData().get("tenantId"), false));

                Entity existingEntity = validationRepo.findOne(collectionName, neutralQuery);
                if (existingEntity != null) {
                    recordMatchingKeyFields = true;
                }
            }
        }

        if (recordMatchingKeyFields) {
            if (entity.getEntityId() != null && !entity.getEntityId().isEmpty()) {
                List<ValidationError> errors = new ArrayList<ValidationError>();
                throw new EntityValidationException(entity.getEntityId(), entity.getType(), errors);
            } else {
                throw new NaturalKeyValidationException(entity.getType());
            }
        }

    }

}
