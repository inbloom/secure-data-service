package org.slc.sli.validation.schema;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.DBObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
        
        boolean recordMatchingKeyFields = false;
        
        // TODO - a lot of this can be removed with deterministicIds given that we will no
        // longer support updates of key fields
        
        Map<String, Boolean> naturalKeys = new HashMap<String, Boolean>();
        try {
            naturalKeys = naturalKeyExtractor.getNaturalKeyFields(entity);
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
        
        if (naturalKeys != null && naturalKeys.size() != 0) {
            
            Map<String, Object> newEntityBody = entity.getBody();
            boolean possibleMatch = true;
            String entityId = entity.getEntityId();
            
            // if we have an existing entityId, then we're doing an update. Check to
            // make sure that there is no existing entity with the new key fields of the entity
            if (entityId != null && !entityId.isEmpty()) {
                possibleMatch = false;
                NeutralQuery neutralQuery = new NeutralQuery();
                neutralQuery.addCriteria(new NeutralCriteria("_id", "=", entity.getEntityId()));
                
                Entity existingEntity = validationRepo.findOne(collectionName, neutralQuery);
                if (existingEntity != null) {
                    for (Entry<String, Boolean> keyField : naturalKeys.entrySet()) {
                        
                        Map<String, Object> existingBody = existingEntity.getBody();
                        try {
                            Object value = PropertyUtils.getProperty(existingBody, keyField.getKey());
                            Object existingValue = PropertyUtils.getProperty(newEntityBody, keyField.getKey());
                            if (existingValue == null) {
                                PropertyUtils.setProperty(newEntityBody, keyField.getKey(), value);
                            } else {
                                Object newEntityValue = newEntityBody.get(keyField.getKey());
                                
                                // if all values are equal, then we're ok - as we're just trying to
                                // catch
                                // the case where a key field has been changed, and check the new
                                // target
                                if (!compare(value, newEntityValue)) {
                                    possibleMatch = true;
                                }
                            }
                        } catch (IllegalAccessException e) {
                            LOG.error(e.getMessage(), e);
                        } catch (InvocationTargetException e) {
                            LOG.error(e.getMessage(), e);
                        } catch (NoSuchMethodException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
            }
            
            // At this point we either have a possible match on the entity to create or the new key
            // fields of the entity we're updating
            if (possibleMatch) {
                NeutralQuery neutralQuery = new NeutralQuery();
                for (Entry<String, Boolean> keyField : naturalKeys.entrySet()) {
                    neutralQuery.addCriteria(new NeutralCriteria(keyField.getKey(), NeutralCriteria.OPERATOR_EQUAL,
                            newEntityBody.get(keyField.getKey())));
                }
                
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
                throw new NaturalKeyValidationException(entity.getType(), new ArrayList<String>(naturalKeys.keySet()));
            }
        }
        
    }
    
    private boolean compare(Object existingObject, Object newObject) {
        if (existingObject == null && newObject == null) {
            return true;
        } else if (existingObject instanceof DBObject) {
            return ((DBObject) existingObject).toMap().equals(newObject);
        } else {
            return existingObject.equals(newObject);
        }
    }
}
