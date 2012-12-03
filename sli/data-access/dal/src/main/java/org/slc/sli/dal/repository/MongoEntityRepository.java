/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.RetryMongoCommand;
import org.slc.sli.dal.convert.Denormalizer;
import org.slc.sli.dal.convert.SubDocAccessor;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 *
 * @author Dong Liu dliu@wgen.net
 */

public class MongoEntityRepository extends MongoRepository<Entity> implements InitializingBean {

    @Autowired
    private EntityValidator validator;

    @Autowired(required = false)
    @Qualifier("entityEncryption")
    EntityEncryption encrypt;

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    UUIDGeneratorStrategy uuidGeneratorStrategy;

    @Autowired
    INaturalKeyExtractor naturalKeyExtractor;

    @Autowired
    @Qualifier("entityKeyEncoder")
    EntityKeyEncoder keyEncoder;

    @Value("${sli.default.mongotemplate.writeConcern}")
    private String writeConcern;

    private SubDocAccessor subDocs;

    private Denormalizer denormalizer;

    @Override
    public void afterPropertiesSet() throws Exception {
        setWriteConcern(writeConcern);
        subDocs = new SubDocAccessor(getTemplate(), uuidGeneratorStrategy, naturalKeyExtractor);
        denormalizer = new Denormalizer(getTemplate());
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        validator.setReferenceCheck(referenceCheck);

    }

    @Override
    protected String getRecordId(Entity entity) {
        return entity.getEntityId();
    }

    @Override
    protected Class<Entity> getRecordClass() {
        return Entity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Iterable<Entity> findAllAcrossTenants(String collectionName, Query mongoQuery) {
        List<Entity> crossTenantResults = Collections.emptyList();

        guideIfTenantAgnostic("realm");
        List<String> distinctTenantIds = template.getCollection("realm").distinct("body.tenantId");

        String originalTenantId = TenantContext.getTenantId();
        try {
            crossTenantResults = issueQueryToTenantDbs(collectionName, mongoQuery, distinctTenantIds);
        } finally {
            TenantContext.setTenantId(originalTenantId);
        }

        return crossTenantResults;
    }

    private List<Entity> issueQueryToTenantDbs(String collectionName, Query mongoQuery, List<String> distinctTenantIds) {
        List<Entity> crossTenantResults = new ArrayList<Entity>();

        guideIfTenantAgnostic(collectionName);
        for (String tenantId : distinctTenantIds) {
            // escape nasty characters

            String dbName = TenantIdToDbName.convertTenantIdToDbName(tenantId);

            if (isValidDbName(dbName)) {
                TenantContext.setTenantId(tenantId);

                List<Entity> resultsForThisTenant = template.find(mongoQuery, getRecordClass(), collectionName);
                crossTenantResults.addAll(resultsForThisTenant);
            }
        }
        return crossTenantResults;
    }

    private boolean isValidDbName(String tenantId) {
        return tenantId != null && !"sli".equalsIgnoreCase(tenantId) && tenantId.length() > 0 && tenantId.indexOf(" ") == -1;
    }


    public Entity createWithRetries(final String type, final Map<String, Object> body,
            final Map<String, Object> metaData, final String collectionName, int noOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                return create(type, body, metaData, collectionName);
            }
        };
        return (Entity) rc.executeOperation(noOfRetries);
    }


    @Override
    public Entity createWithRetries(final String type, final String id, final Map<String, Object> body,
            final Map<String, Object> metaData, final String collectionName, int noOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                return internalCreate(type, id, body, metaData, collectionName);
            }
        };
        return (Entity) rc.executeOperation(noOfRetries);
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        boolean result = false;
        Entity entity = new MongoEntity(type, id, newValues, null);
        validator.validatePresent(entity);
        keyEncoder.encodeEntityKey(entity);
        if (subDocs.isSubDoc(collectionName)) {

            // prepare to find desired record to be patched
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(idConverter.toDatabaseId(id)));
            query.addCriteria(createTenantCriteria(collectionName));

            // prepare update operation for record to be patched
            Update update = new Update();
            for (Entry<String, Object> patch : newValues.entrySet()) {
                update.set("body." + patch.getKey(), patch.getValue());
            }
            result = subDocs.subDoc(collectionName).doUpdate(query, update);
        } else {
            result = super.patch(type, collectionName, id, newValues);
        }

        if (result && denormalizer.isDenormalizedDoc(collectionName)) {
            Entity updateEntity;
            if (subDocs.isSubDoc(collectionName)) {
                updateEntity = subDocs.subDoc(collectionName).findById(id);
            } else {
                updateEntity = super.findById(collectionName, id);
            }

            Update update = new Update();
            for (Map.Entry<String, Object> patch : newValues.entrySet()) {
                update.set(patch.getKey(), patch.getValue());
            }

            denormalizer.denormalization(collectionName).doUpdate(updateEntity, update);
        }

        return result;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        return internalCreate(type, null, body, metaData, collectionName);
    }

    /*
     * This method should be private, but is used via mockito in the tests, thus
     * it's public. (S. Altmueller)
     */
    Entity internalCreate(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }

        String tenantId = TenantContext.getTenantId();

        if (id != null && collectionName.equals("educationOrganization")) {
            if (metaData.containsKey("edOrgs")) {
                @SuppressWarnings("unchecked")
                List<String> edOrgs = (List<String>) metaData.get("edOrgs");
                edOrgs.add(id);
                metaData.put("edOrgs", edOrgs);
            } else {
                List<String> edOrgs = new ArrayList<String>();
                edOrgs.add(id);
                metaData.put("edOrgs", edOrgs);
            }
        }

        MongoEntity entity = new MongoEntity(type, id, body, metaData);
        validator.validate(entity);
        keyEncoder.encodeEntityKey(entity);

        this.addTimestamps(entity);

        if (subDocs.isSubDoc(collectionName)) {
            subDocs.subDoc(collectionName).create(entity);

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).create(entity);
            }

            return entity;
        } else {
            Entity result = super.insert(entity, collectionName);

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).create(entity);
            }

            return result;
        }
    }

    @Override
    public List<Entity> insert(List<Entity> records, String collectionName) {

        if (subDocs.isSubDoc(collectionName)) {
            subDocs.subDoc(collectionName).insert(records);

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).insert(records);
            }

            return records;
        } else {
            List<Entity> persist = new ArrayList<Entity>();

            for (Entity record : records) {

                Entity entity = new MongoEntity(record.getType(), null, record.getBody(), record.getMetaData());
                keyEncoder.encodeEntityKey(entity);
                persist.add(entity);
            }

            List<Entity> results = super.insert(persist, collectionName);

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).insert(records);
            }
            if(denormalizer.isCached(collectionName)) {
                denormalizer.addToCache(results,collectionName);
            }

            return results;
        }
    }

    @Override
    public Entity findOne(String collectionName, Query query) {
        if (subDocs.isSubDoc(collectionName)) {
            List<Entity> entities = subDocs.subDoc(collectionName).findAll(query);
            if (entities != null && entities.size() > 0) {
                return entities.get(0);
            }
            return null;
        }
        return super.findOne(collectionName, query);
    }

    @Override
    public boolean delete(String collectionName, String id) {

        if (subDocs.isSubDoc(collectionName)) {
            Entity entity = subDocs.subDoc(collectionName).findById(id);

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).delete(entity, id);
            }

            return subDocs.subDoc(collectionName).delete(entity);
        }

        if (denormalizer.isDenormalizedDoc(collectionName)) {
            denormalizer.denormalization(collectionName).delete(null, id);
        }

        return super.delete(collectionName, id);
    }

    @Override
    protected Query getUpdateQuery(Entity entity) {
        String id = getRecordId(entity);
        return new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id)));
    }

    @Override
    protected Entity getEncryptedRecord(Entity entity) {
        MongoEntity encryptedEntity = new MongoEntity(entity.getType(), entity.getEntityId(), entity.getBody(),
                entity.getMetaData(), entity.getCalculatedValues(), entity.getAggregates());
        encryptedEntity.encrypt(encrypt);
        return encryptedEntity;
    }

    @Override
    protected Update getUpdateCommand(Entity entity) {
        // set up update query
        Map<String, Object> entityBody = entity.getBody();
        Map<String, Object> entityMetaData = entity.getMetaData();
        Update update = new Update().set("body", entityBody).set("metaData", entityMetaData);
        return update;
    }

    @Override
    public boolean updateWithRetries(final String collection, final Entity entity, int noOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                return update(collection, entity);
            }
        };
        Object result = rc.executeOperation(noOfRetries);
        if (result != null) {
            return (Boolean) result;
        } else {
            return false;
        }

    }

    @Override
    public boolean update(String collection, Entity entity) {
        validator.validate(entity);
        this.updateTimestamp(entity);

        if (denormalizer.isDenormalizedDoc(collection)) {
            denormalizer.denormalization(collection).create(entity);
        }
        if (subDocs.isSubDoc(collection)) {
            return subDocs.subDoc(collection).create(entity);
        }

        return update(collection, entity, null); // body);
    }

    /** Add the created and updated timestamp to the document metadata. */
    private void addTimestamps(Entity entity) {
        Date now = DateTimeUtil.getNowInUTC();

        Map<String, Object> metaData = entity.getMetaData();
        metaData.put(EntityMetadataKey.CREATED.getKey(), now);
        metaData.put(EntityMetadataKey.UPDATED.getKey(), now);
    }

    /** Update the updated timestamp on the document metadata. */
    public void updateTimestamp(Entity entity) {
        Date now = DateTimeUtil.getNowInUTC();
        entity.getMetaData().put(EntityMetadataKey.UPDATED.getKey(), now);
    }

    public void setValidator(EntityValidator validator) {
        this.validator = validator;
    }

    @Override
    public Entity findById(String collectionName, String id) {
        if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).findById(id);
            // return new MongoEntity(collectionName, id, subDocs.subDoc(collectionName).read(id),
            // null);
        }
        return super.findById(collectionName, id);
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        if (subDocs.isSubDoc(collectionName)) {
            if (neutralQuery == null) {
                neutralQuery = new NeutralQuery();
            }
            neutralQuery.setIncludeFieldString("_id");
            addDefaultQueryParams(neutralQuery, collectionName);
            Query q = getQueryConverter().convert(collectionName, neutralQuery);

            List<String> ids = new LinkedList<String>();
            for (Entity e : subDocs.subDoc(collectionName).findAll(q)) {
                ids.add(e.getEntityId());
            }
            return ids;
        }
        return super.findAllIds(collectionName, neutralQuery);
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        if (subDocs.isSubDoc(collectionName)) {
            this.addDefaultQueryParams(neutralQuery, collectionName);
            return subDocs.subDoc(collectionName).findAll(getQueryConverter().convert(collectionName, neutralQuery));
        }
        return super.findAll(collectionName, neutralQuery);
    }

    @Override
    public boolean exists(String collectionName, String id) {
        if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).exists(id);
        }
        return super.exists(collectionName, id);
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        if (subDocs.isSubDoc(collectionName)) {
            Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
            return count(collectionName, query);
        }
        return super.count(collectionName, neutralQuery);
    }

    @Override
    public long count(String collectionName, Query query) {
        if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).count(query);
        }
        return super.count(collectionName, query);
    }


    @Override
    public boolean doUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        if (subDocs.isSubDoc(collectionName)) {
            Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
            return subDocs.subDoc(collectionName).doUpdate(query, update);
        }
        return super.doUpdate(collectionName, neutralQuery, update);
    }


    @Override
    public void deleteAll(String collectionName, NeutralQuery neutralQuery) {
        if (subDocs.isSubDoc(collectionName)) {
            Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
            subDocs.subDoc(collectionName).deleteAll(query);
        } else {
            super.deleteAll(collectionName, neutralQuery);
        }
    }

    /**
     * @Deprecated
     *             "This is a deprecated method that should only be used by the ingestion ID
     *             Normalization code.
     *             It is not tenant-safe meaning clients of this method must include tenantId in the
     *             metaData block"
     */
    @Override
    @Deprecated
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {

        if (query == null) {
            query = new Query();
        }

        query.skip(skip).limit(max);

        if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).findAll(query);
        }

        return findByQuery(collectionName, query);
    }

    @Override
    public Entity findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
        FindAndModifyOptions options = new FindAndModifyOptions();
        return template.findAndModify(query, update, options, getRecordClass(), collectionName);
    }
}
