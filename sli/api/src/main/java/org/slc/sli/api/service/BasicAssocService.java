package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.config.AssociationDefinition.EntityInfo;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;

/**
 * Implementation of AssociationService.
 * 
 */
public class BasicAssocService extends BasicService implements AssociationService {
    
    private final EntityDefinition sourceDefn;
    private final EntityDefinition targetDefn;
    private final String sourceKey;
    private final String targetKey;
    
    public BasicAssocService(String collectionName, List<Treatment> treatments, List<Validator> validators,
            EntityRepository repo, EntityInfo source, EntityInfo target) {
        super(collectionName, treatments, validators, repo);
        this.sourceDefn = source.getDefn();
        this.targetDefn = target.getDefn();
        this.sourceKey = source.getKey();
        this.targetKey = target.getKey();
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(BasicAssocService.class);
    
    @Override
    public Iterable<String> getAssociatedWith(String id, int start, int numResults) {
        return getAssociations(sourceDefn, id, sourceKey, start, numResults);
    }
    
    @Override
    public Iterable<String> getAssociatedTo(String id, int start, int numResults) {
        return getAssociations(targetDefn, id, targetKey, start, numResults);
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
    private Iterable<String> getAssociations(EntityDefinition type, String id, String key, int start, int numResults) {
        LOG.debug("Getting assocations with {} from {} through {}", new Object[] { id, start, numResults });
        EntityBody existingEntity = type.getService().get(id);
        if (existingEntity == null) {
            throw new EntityNotFoundException(id);
        }
        Map<String, String> fields = new HashMap<String, String>();
        fields.put(key, id);
        List<String> results = new ArrayList<String>();
        for (Entity entity : getRepo().findByFields(getCollectionName(), fields, start, numResults)) {
            results.add(entity.getEntityId());
        }
        return results;
    }
    
}
