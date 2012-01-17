package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.query.QueryConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Implementation of AssociationService.
 * 
 */
@Scope("prototype")
@Component("basicAssociationService")
public class BasicAssocService extends BasicService implements AssociationService {
    private static final Logger LOG = LoggerFactory.getLogger(BasicAssocService.class);
    
    @Autowired
    QueryConverter queryConverter;
    
    private EntityDefinition sourceDefn;
    private EntityDefinition targetDefn;
    private String sourceKey;
    private String targetKey;
    
    public BasicAssocService(String collectionName, List<Treatment> treatments, CoreBasicService coreService,
            EntityDefinition sourceDefn, String sourceKey, EntityDefinition targetDefn, String targetKey) {
        super(collectionName, treatments, coreService);
        this.sourceDefn = sourceDefn;
        this.targetDefn = targetDefn;
        this.sourceKey = sourceKey;
        this.targetKey = targetKey;
    }
    
    @Override
    public Iterable<String> getAssociationsWith(String id, int start, int numResults, String queryString) {
        return getAssociations(sourceDefn, id, sourceKey, start, numResults, queryString);
    }
    
    @Override
    public Iterable<String> getAssociationsTo(String id, int start, int numResults, String queryString) {
        return getAssociations(targetDefn, id, targetKey, start, numResults, queryString);
    }
    
    @Override
    public Iterable<String> getAssociatedEntitiesWith(String id, int start, int numResults, String queryString) {
        return getAssociatedEntities(sourceDefn, id, sourceKey, targetDefn, targetKey, start, numResults, queryString);
    }
    
    @Override
    public Iterable<String> getAssociatedEntitiesTo(String id, int start, int numResults, String queryString) {
        return getAssociatedEntities(targetDefn, id, targetKey, sourceDefn, sourceKey, start, numResults, queryString);
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
     * @param queryString
     *            the query string to filter returned collection results
     * @return
     */
    private Iterable<String> getAssociations(EntityDefinition type, String id, String key, int start, int numResults,
            String queryString) {
        LOG.debug("Getting assocations with {} from {} through {}", new Object[] { id, start, numResults });
        List<String> results = new ArrayList<String>();
        Iterable<Entity> entityObjects = getAssociationObjects(type, id, key, start, numResults, queryString);
        for (Entity entity : entityObjects) {
            results.add(entity.getEntityId());
        }
        return results;
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
     * @param queryString
     *            the query string to filter returned collection results
     * @return
     */
    private Iterable<String> getAssociatedEntities(EntityDefinition type, String id, String key,
            EntityDefinition otherEntityDefn, String otherEntityKey, int start, int numResults, String queryString) {
        LOG.debug("Getting assocated entities with {} from {} through {}", new Object[] { id, start, numResults });
        List<String> results = new ArrayList<String>();
        Iterable<Entity> entityObjects = getAssociationObjects(type, id, key, start, numResults, null);
        for (Entity entity : entityObjects) {
            Object other = entity.getBody().get(otherEntityKey);
            if (other != null
                    && other instanceof String
                    && getRepo().matchQuery(otherEntityDefn.getStoredCollectionName(), (String) other,
                            queryConverter.stringToQuery(otherEntityDefn.getType(), queryString))) {
                results.add((String) other);
            } else {
                LOG.error("Association had bad value of key {}: {}", new Object[] { otherEntityKey, other });
            }
        }
        return results;
    }
    
    /**
     * Gets the actual association objects (and not just the ids
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
     * @param queryString
     *            the query string to filter returned collection results
     * @return
     */
    private Iterable<Entity> getAssociationObjects(EntityDefinition type, String id, String key, int start,
            int numResults, String queryString) {
        EntityBody existingEntity = type.getService().get(id);
        if (existingEntity == null) {
            throw new EntityNotFoundException(id);
        }
        Query query = queryConverter.stringToQuery(this.getEntityDefinition().getType(), queryString);
        if (query == null)
            query = new Query(Criteria.where("body." + key).is(id));
        else
            query.addCriteria(Criteria.where("body." + key).is(id));
        
        Iterable<Entity> entityObjects = getRepo().findByFields(getCollectionName(), query, start, numResults);
        return entityObjects;
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
