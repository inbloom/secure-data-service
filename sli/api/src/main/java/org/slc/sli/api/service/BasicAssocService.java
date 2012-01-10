package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.config.AssociationDefinition.EntityInfo;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of AssociationService.
 */
public class BasicAssocService extends BasicService implements AssociationService {
    
    private final EntityDefinition sourceDefn;
    private final EntityDefinition targetDefn;
    private final String sourceKey;
    private final String targetKey;
    
    public BasicAssocService(String collectionName, List<Treatment> treatments, EntityRepository repo,
            EntityInfo source, EntityInfo target, EntityValidator validator) {
        super(collectionName, treatments, repo, validator);
        this.sourceDefn = source.getDefn();
        this.targetDefn = target.getDefn();
        this.sourceKey = source.getKey();
        this.targetKey = target.getKey();
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(BasicAssocService.class);
    
    @Override
    public Iterable<String> getAssociatedWith(String id, int start, int numResults, Map<String, String> queryFields) {
        return getAssociations(sourceDefn, id, sourceKey, start, numResults, queryFields);
    }
    
    @Override
    public Iterable<String> getAssociatedTo(String id, int start, int numResults, Map<String, String> queryFields) {
        return getAssociations(targetDefn, id, targetKey, start, numResults, queryFields);
    }
    
    public String create(EntityBody content) {
        
        createAssocValidate(content);
        return super.create(content);
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
     * @return
     */
    private Iterable<String> getAssociations(EntityDefinition type, String id, String key, int start, int numResults,
            Map<String, String> queryFields) {
        LOG.debug("Getting assocations with {} from {} through {}", new Object[] { id, start, numResults });
        EntityBody existingEntity = type.getService().get(id);
        if (existingEntity == null) {
            throw new EntityNotFoundException(id);
        }
        Map<String, String> fields = new HashMap<String, String>();
        fields.put(key, id);
        if (queryFields != null)
            fields.putAll(queryFields);
        List<String> results = new ArrayList<String>();
        for (Entity entity : getRepo().findByFields(getCollectionName(), fields, start, numResults)) {
            results.add(entity.getEntityId());
        }
        return results;
    }
    
    private boolean createAssocValidate(EntityBody content) {
        String sourceType = sourceDefn.getType();
        String sourceId = (String) content.get(sourceType + "Id");
        String sourceCollectionName = sourceDefn.getStoredCollectionName();
        ValidationError sourceError;
        ValidationError targetError;
        List<ValidationError> errors = new ArrayList<ValidationError>();
        
        if (!checkEntityExist(sourceCollectionName, sourceId)) {
            sourceError = new ValidationError(ValidationError.ErrorType.REFERENTIAL_INFO_MISSING, sourceDefn.getType()
                    + "Id", sourceId, null);
            errors.add(sourceError);
        }
        String targetType = targetDefn.getType();
        String targetId = (String) content.get(targetType + "Id");
        String targetCollectionName = targetDefn.getStoredCollectionName();
        
        if (!checkEntityExist(targetCollectionName, targetId)) {
            targetError = new ValidationError(ValidationError.ErrorType.REFERENTIAL_INFO_MISSING, targetDefn.getType()
                    + "Id", targetId, null);
            errors.add(targetError);
        }
        
        if (!errors.isEmpty()) {
            throw new EntityValidationException("", getEntityDefinition().getType(), errors);
        }
        return true;
    }
    
    private boolean checkEntityExist(String collectionName, String id) {
        try {
            Entity entity = getRepo().find(collectionName, id);
            if (entity == null)
                return false;
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }
}
