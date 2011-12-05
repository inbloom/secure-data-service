package org.slc.sli.dal.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */

@Repository
public class MongoEntityRepository implements EntityRepository {

    @Autowired
    private MongoTemplate template;

    @Override
    public Entity find(String entityType, String id) {
        return template.findById(new ObjectId(id), MongoEntity.class,
                entityType);
    }

    @Override
    public Iterable<Entity> findAll(String entityType, int skip, int max) {

        LinkedList<Entity> entities = new LinkedList<Entity>();
        List<MongoEntity> results = template.find(
                new Query().skip(skip).limit(max), MongoEntity.class,
                entityType);
        entities.addAll(results);
        return entities;
    }

    @Override
    public void update(Entity entity) {
        Assert.notNull(entity, "The given entity must not be null!");
        String id = entity.getId();
        String collection = entity.getType();
        if (id.equals(""))
            return;
        Entity found = template.findOne(
                new Query(Criteria.where("_id").is(new ObjectId(id))),
                MongoEntity.class, collection);
        if (found != null)
            template.save(entity, collection);
    }

    @Override
    public Entity create(Entity entity) {
        Assert.notNull(entity, "The given entity must not be null!");
        template.save(entity, entity.getType());
        return entity;
    }

    @Override
    public void delete(Entity entity) {
        Assert.notNull(entity, "The given entity must not be null!");
        String id = entity.getId();
        if (id.equals(""))
            return;
        template.remove(new Query(Criteria.where("_id").is(new ObjectId(id))),
                entity.getType());
    }

    @Override
    public void delete(String entityType, String id) {
        if (id.equals(""))
            return;
        template.remove(new Query(Criteria.where("_id").is(new ObjectId(id))),
                entityType);
    }

    @Override
    public Iterable<Entity> findByFields(String entityType,
            Map<String, String> fields, int skip, int max) {
        Query query = new Query();
        query.skip(skip).limit(max);
        for (String key : fields.keySet()) {
            Criteria criteria = Criteria.where("body." + key).is(
                    fields.get(key));
            query.addCriteria(criteria);
        }
        List<MongoEntity> results = template.find(query, MongoEntity.class,
                entityType);
        return new LinkedList<Entity>(results);
    }

    @Override
    public void deleteAll(String entityType) {
        template.remove(new Query(), entityType);

    }

}
