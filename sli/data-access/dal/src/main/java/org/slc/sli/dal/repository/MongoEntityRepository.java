package org.slc.sli.dal.repository;

import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.WriteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.util.datetime.DateTimeUtil;
import org.slc.sli.validation.EntityValidator;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 * @author Dong Liu dliu@wgen.net
 */

public class MongoEntityRepository extends MongoRepository<Entity> {
    protected static final Logger LOG = LoggerFactory.getLogger(MongoEntityRepository.class);

    @Autowired
    private EntityValidator validator;

    MongoEntityRepository() {
        super.setClass(Entity.class);
    }

    @Override
    public boolean update(String collection, Entity entity) {
        Assert.notNull(entity, "The given entity must not be null!");
        String id = entity.getEntityId();
        if (id.equals(""))
            return false;
        validator.validate(entity);

        updateTimestamp(entity);

        Entity found = template.findOne(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                Entity.class, collection);
        if (found != null) {
            template.save(entity, collection);
        }
        WriteResult result = template.updateFirst(new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id))),
                new Update().set("body", entity.getBody()), collection);
        LOG.info("update a entity in collection {} with id {}", new Object[] { collection, id });
        return result.getN() == 1;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        Entity entity = new MongoEntity(type, null, body, metaData);
        validator.validate(entity);

        addTimestamps(entity);

        template.save(entity, collectionName);
        LOG.info(" create a entity in collection {} with id {}", new Object[] { collectionName, getRecordId(entity) });
        return entity;
    }

    /** Add the created and updated timestamp to the document metadata. */
    private void addTimestamps(Entity entity) {
        // String now = DateTimeUtil.getNowInUTC();
        Date now = DateTimeUtil.getNowInUTC();

        Map<String, Object> metaData = entity.getMetaData();
        metaData.put(EntityMetadataKey.CREATED.getKey(), now);
        metaData.put(EntityMetadataKey.UPDATED.getKey(), now);
    }

    @Override
    protected String getRecordId(Entity entity) {
        return entity.getEntityId();
    }

    @Override
    protected String getRecordIdName() {
        return "_id";
    }

}
