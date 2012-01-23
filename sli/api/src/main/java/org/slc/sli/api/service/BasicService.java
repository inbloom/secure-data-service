package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;

/**
 * Implementation of EntityService that can be used for most entities.
 * 
 * It is very important this bean prototype scope, since one service is needed per
 * entity/association.
 */
@Scope("prototype")
@Component("basicService")
public class BasicService implements EntityService {
    private static final Logger LOG = LoggerFactory.getLogger(BasicService.class);
    
    private String collectionName;
    private List<Treatment> treatments;
    private EntityDefinition defn;
    private CoreEntityService coreService;
    
    @Autowired
    private EntityRepository repo;
    
    public BasicService(String collectionName, List<Treatment> treatments, CoreBasicService coreService) {
        this.collectionName = collectionName;
        this.treatments = treatments;
        this.coreService = coreService;
    }
    
    /**
     * Set the entity definition for this service.
     * There is a circular dependency between BasicService and EntityDefinition, so they both can't
     * have it be a constructor arg.
     */
    public void setDefn(EntityDefinition defn) {
        this.defn = defn;
    }
    
    public EntityDefinition getEntityDefinition() {
        return defn;
    }
    
    protected String getCollectionName() {
        return collectionName;
    }
    
    protected List<Treatment> getTreatments() {
        return treatments;
    }
    
    protected EntityRepository getRepo() {
        return repo;
    }
    
    @Override
    public String create(EntityBody content) {
        LOG.debug("Creating a new entity in collection {} with content {}", new Object[] { collectionName, content });
        String type = defn.getType();
        EntityBody body = sanitizeEntityBody(content);
        return coreService.create(body, type);
    }
    
    @Override
    public void delete(String id) {
        LOG.debug("Deleting {} in {}", new String[] { id, collectionName });
        if (!coreService.delete(id)) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        
        if (!(defn instanceof AssociationDefinition))
            removeEntityWithAssoc(id);
    }
    
    @Override
    public boolean update(String id, EntityBody content) {
        LOG.debug("Updating {} in {}", new String[] { id, collectionName });
        Entity entity = repo.find(collectionName, id);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        EntityBody sanitized = sanitizeEntityBody(content);
        if (entity.getBody().equals(sanitized)) {
            LOG.info("No change detected to {}", id);
            return false;
        }
        LOG.info("new body is {}", sanitized);
        coreService.update(entity, sanitized);
        return true;
    }
    
    @Override
    public EntityBody get(String id) {
        Entity entity = coreService.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        
        return makeEntityBody(entity);
    }
    
    @Override
    public Iterable<EntityBody> get(Iterable<String> ids) {
        List<EntityBody> results = new ArrayList<EntityBody>();
        for (String id : ids) {
            Entity entity = coreService.get(id);
            if (entity != null) {
                results.add(makeEntityBody(entity));
            }
        }
        return results;
    }
    
    @Override
    public Iterable<String> list(int start, int numResults) {
        List<String> results = new ArrayList<String>();
        for (Entity entity : repo.findAll(collectionName, start, numResults)) {
            results.add(entity.getEntityId());
        }
        return results;
    }
    
    @Override
    public boolean exists(String id) {
        return getRepo().find(collectionName, id) != null;
    }
    
    /**
     * given an entity, make the entity body to expose
     * 
     * @param entity
     * @return
     */
    private EntityBody makeEntityBody(Entity entity) {
        EntityBody toReturn = new EntityBody(entity.getBody());
        for (Treatment treatment : treatments) {
            toReturn = treatment.toExposed(toReturn, defn, entity.getEntityId());
        }
        return toReturn;
    }
    
    /**
     * given an entity body that was exposed, return the version with the treatments reversed
     * 
     * @param content
     * @return
     */
    private EntityBody sanitizeEntityBody(EntityBody content) {
        EntityBody sanitized = new EntityBody(content);
        for (Treatment treatment : treatments) {
            sanitized = treatment.toStored(sanitized, defn);
        }
        return sanitized;
    }
    
    private void removeEntityWithAssoc(String sourceId) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put(defn.getType() + "Id", sourceId);
        
        for (AssociationDefinition assocDef : defn.getLinkedAssoc()) {
            String assocCollection = assocDef.getStoredCollectionName();
            Iterable<Entity> iterable = repo.findByFields(assocCollection, fields);
            Iterator<Entity> foundEntities;
            if (iterable != null) {
                foundEntities = iterable.iterator();
                while (foundEntities.hasNext()) {
                    Entity assocEntity = foundEntities.next();
                    repo.delete(assocCollection, assocEntity.getEntityId());
                }
            }
        }
    }
}
