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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.dal.RetryMongoCommand;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.EntityValidator;

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

    @Value("${sli.default.mongotemplate.writeConcern}")
    private String writeConcern;

    @Override
    public void afterPropertiesSet() throws Exception {
        setWriteConcern(writeConcern);
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

    @Override
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
                return create(type, id, body, metaData, collectionName);
            }
        };
        return (Entity) rc.executeOperation(noOfRetries);
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        Entity entity = new MongoEntity(type, null, newValues, null);
        validator.validatePresent(entity);
        return super.patch(type, collectionName, id, newValues);
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }

        String tenantId = TenantContext.getTenantId();
        if (tenantId != null && !NOT_BY_TENANT.contains(collectionName)) {
            if (metaData.get("tenantId") == null) {
                metaData.put("tenantId", tenantId);
            }
        }

        Entity entity = new MongoEntity(type, null, body, metaData);
        validator.validate(entity);

        this.addTimestamps(entity);
        return super.create(entity, collectionName);
    }

    public Entity create(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }

        String tenantId = TenantContext.getTenantId();
        if (tenantId != null && !NOT_BY_TENANT.contains(collectionName)) {
            if (metaData.get("tenantId") == null) {
                metaData.put("tenantId", tenantId);
            }
        }

        if (collectionName.equals("educationOrganization")) {
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

        Entity entity = new MongoEntity(type, id, body, metaData);
        validator.validate(entity);

        this.addTimestamps(entity);
        return super.create(entity, collectionName);
    }

    @Override
    public List<Entity> insert(List<Entity> records, String collectionName) {
        List<Entity> persist = new ArrayList<Entity>();

        for (Entity record : records) {
            Entity entity = new MongoEntity(record.getType(), record.getStagedEntityId(), record.getBody(),
                    record.getMetaData());
            persist.add(entity);
        }

        return super.insert(persist, collectionName);
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

}
