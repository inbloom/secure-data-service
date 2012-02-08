package org.slc.sli.dal.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.EntityValidationRepository;
import org.slc.sli.validation.EntityValidator;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 *
 * @author Dong Liu dliu@wgen.net
 *
 */

public class MongoEntityRepository implements EntityRepository, EntityValidationRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MongoEntityRepository.class);

    @Autowired
    private MongoTemplate       template;

    @Autowired
    private IdConverter         idConverter;

    @Autowired
    private EntityValidator validator;

    @Override
    public Entity find(String collectionName, String id) {
        Object databaseId = idConverter.toDatabaseId(id);
        LOG.debug("find a entity in collection {} with id {}", new Object[] { collectionName, id });
        return template.findById(databaseId, MongoEntity.class, collectionName);
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, int skip, int max) {

        List<Entity> entities = new LinkedList<Entity>();
        List<MongoEntity> results = template.find(new Query().skip(skip).limit(max), MongoEntity.class, collectionName);
        logResults(collectionName, results);
        entities.addAll(results);
        return entities;
    }

    @Override
    public boolean update(String collection, Entity entity) {
        Assert.notNull(entity, "The given entity must not be null!");
        String id = entity.getEntityId();
        if (id.equals(""))
            return false;
        validator.validate(entity);

        Entity found = template.findOne(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))), MongoEntity.class, collection);
        if (found != null)
            template.save(entity, collection);
        WriteResult result = template.updateFirst(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))), new Update().set("body", entity.getBody()), collection);
        LOG.info("update a entity in collection {} with id {}", new Object[] { collection, id });
        return result.getN() == 1;
    }

    @Override
    public Entity create(String type, Map<String, Object> body) {
        return create(type, body, type);
    }

    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        Entity entity = new MongoEntity(type, null, body, new HashMap<String, Object>());
        validator.validate(entity);
        template.save(entity, collectionName);
        LOG.info(" create a entity in collection {} with id {}", new Object[] { collectionName, entity.getEntityId() });
        return entity;
    }

    @Override
    public boolean delete(String collectionName, String id) {
        if (id.equals(""))
            return false;
        Entity deleted = template.findAndRemove(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))), MongoEntity.class, collectionName);
        LOG.info("delete a entity in collection {} with id {}", new Object[] { collectionName, id });
        return deleted != null;
    }

    @Override
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields, int skip, int max) {
        Query query = new Query();
        query.skip(skip).limit(max);
        List<MongoEntity> results = template.find(addSearchFieldsToQuery(query, fields), MongoEntity.class, collectionName);
        logResults(collectionName, results);
        return new LinkedList<Entity>(results);
    }

    @Override
    public void deleteAll(String collectionName) {
        template.remove(new Query(), collectionName);
        LOG.info("delete all entities in collection {}", collectionName);
    }

    @Override
    public Iterable<Entity> findAll(String collectionName) {
        List<Entity> entities = new LinkedList<Entity>();
        List<MongoEntity> results = template.find(new Query(), MongoEntity.class, collectionName);
        logResults(collectionName, results);
        entities.addAll(results);
        return entities;
    }

    @Override
    public Iterable<Entity> findByFields(String collectionName, Map<String, String> fields) {
        Query query = new Query();
        List<MongoEntity> results = template.find(addSearchFieldsToQuery(query, fields), MongoEntity.class, collectionName);
        logResults(collectionName, results);
        return new LinkedList<Entity>(results);
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        if (query == null)
            query = new Query();
        query.skip(skip).limit(max);
        List<MongoEntity> results = template.find(query, MongoEntity.class, collectionName);
        logResults(collectionName, results);
        return new LinkedList<Entity>(results);
    }

    @Override
    public boolean matchQuery(String collectionName, String id, Query query) {
        boolean match = false;
        if (query != null) {
            List<MongoEntity> results = template.find(query, MongoEntity.class, collectionName);
            for (MongoEntity entity : results) {
                if (entity.getEntityId().equals(id))
                    match = true;
            }
        }
        return match;
    }

    private Query addSearchFieldsToQuery(Query query, Map<String, String> searchFields) {
        for (Map.Entry<String, String> field : searchFields.entrySet()) {
            Criteria criteria = Criteria.where("body." + field.getKey()).is(field.getValue());
            query.addCriteria(criteria);
        }
        return query;
    }

    private void logResults(String collectioName, List<MongoEntity> results) {
        if (results == null) {
            LOG.debug("find entities in collection {} with total numbers is {}", new Object[] { collectioName, 0 });
        } else {
            LOG.debug("find entities in collection {} with total numbers is {}", new Object[] { collectioName, results.size() });
        }

    }
}
