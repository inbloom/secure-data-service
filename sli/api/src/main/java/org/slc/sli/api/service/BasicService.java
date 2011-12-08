package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicService implements EntityService {
    private static final Logger LOG = LoggerFactory.getLogger(BasicService.class);
    private final String collectionName;
    
    private final List<Treatment> treatments;
    private final List<Validator> validators;
    private final EntityRepository repo;
    
    public BasicService(String collectionName, List<Treatment> treatments, List<Validator> validators,
            EntityRepository repo) {
        super();
        this.collectionName = collectionName;
        this.treatments = treatments;
        this.validators = validators;
        this.repo = repo;
    }
    
    protected String getCollectionName() {
        return collectionName;
    }
    
    protected List<Treatment> getTreatments() {
        return treatments;
    }
    
    protected List<Validator> getValidators() {
        return validators;
    }
    
    protected EntityRepository getRepo() {
        return repo;
    }
    
    @Override
    public String create(EntityBody content) {
        LOG.debug("Creating a new entity in collection {} with content {}", new Object[] { collectionName, content });
        if (!validate(content)) {
            LOG.info("validation failed for {}", content);
            throw new ValidationException();
        }
        return getRepo().create(content, collectionName).getEntityId();
    }
    
    @Override
    public boolean delete(String id) {
        LOG.debug("Deleting {} in {}", new String[] { id, collectionName });
        Entity entity = getRepo().find(collectionName, id);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            return false;
        }
        getRepo().delete(entity);
        return true;
    }
    
    @Override
    public boolean update(String id, EntityBody content) {
        LOG.debug("Updating {} in {}", new String[] { id, collectionName });
        if (!validate(content)) {
            LOG.info("Validation failed for {}", content);
            throw new ValidationException();
        }
        Entity entity = getRepo().find(collectionName, id);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException();
        }
        if (makeEntityBody(entity).equals(content)) {
            LOG.info("No change detected to {}", id);
            return false;
        }
        entity.getBody().putAll(content);
        getRepo().update(entity);
        return true;
    }
    
    @Override
    public EntityBody get(String id) {
        Entity entity = getRepo().find(collectionName, id);
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        return makeEntityBody(entity);
    }
    
    @Override
    public Iterable<EntityBody> get(Iterable<String> ids) {
        List<EntityBody> results = new ArrayList<EntityBody>();
        for (String id : ids) {
            Entity entity = getRepo().find(collectionName, id);
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
    
    private EntityBody makeEntityBody(Entity entity) {
        EntityBody toReturn = new EntityBody(entity.getBody());
        for (Treatment treatment : treatments) {
            toReturn = treatment.toExposed(toReturn);
        }
        return toReturn;
    }
    
    private boolean validate(EntityBody body) {
        for (Validator v : validators) {
            if (!v.validate(body)) {
                LOG.info("Validator {} reported failure", v);
                return false;
            }
        }
        return true;
    }
    
}
