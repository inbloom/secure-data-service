package org.slc.sli.api.service;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.EntityValidator;

/**
 * Core operations of the Basic Service.
 */
public class CoreBasicService implements CoreEntityService {

    private String collectionName;
    private EntityRepository repo;
    private EntityValidator validator;

    public CoreBasicService(String collectionName, EntityRepository repo, EntityValidator validator) {
        super();
        this.collectionName = collectionName;
        this.repo = repo;
        this.validator = validator;
    }

    @Override
    public String create(EntityBody body, String type) {
        MongoEntity entity = MongoEntity.create(type, body);
        validator.validate(entity);
        return repo.create(type, body, collectionName).getEntityId();
    }

    @Override
    public Entity get(String id) {
        Entity entity = repo.find(collectionName, id);
        return entity;
    }

    @Override
    public void update(Entity entity, EntityBody updates) {
        Entity repoEntity = repo.find(collectionName, entity.getEntityId());
        repoEntity.getBody().putAll(updates);
        validator.validate(repoEntity);
        repo.update(collectionName, repoEntity);
    }

    @Override
    public void delete(Entity entity) {
        repo.delete(collectionName, entity.getEntityId());
    }
}
