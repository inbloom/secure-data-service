package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of AssociationService. 
 * 
 */
public class BasicAssocService extends BasicService implements AssociationService {
    
    private final EntityDefinition source;
    private final String sourceKey;
    
    public BasicAssocService(String collectionName, List<Treatment> treatments, List<Validator> validators,
            EntityRepository repo, EntityDefinition source, String sourceKey) {
        super(collectionName, treatments, validators, repo);
        this.source = source;
        this.sourceKey = sourceKey;
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(BasicAssocService.class);
    
    @Override
    public Iterable<String> getAssociatedWith(String id, int start, int numResults) {
        LOG.debug("Getting assocations with {} from {} through {}", new Object[] { id, start, numResults });
        EntityBody existingEntity = source.getService().get(id);
        if (existingEntity == null) {
            throw new EntityNotFoundException();
        }
        Map<String, String> fields = new HashMap<String, String>();
        fields.put(sourceKey, id);
        List<String> results = new ArrayList<String>();
        for (Entity entity : getRepo().findByFields(getCollectionName(), fields, start, numResults)) {
            results.add(entity.getEntityId());
        }
        return results;
    }
}
