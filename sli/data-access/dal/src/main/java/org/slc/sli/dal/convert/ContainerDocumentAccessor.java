/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.dal.convert;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pghosh
 */
public class ContainerDocumentAccessor {

    private ContainerDocumentHolder containerDocumentHolder;

    private UUIDGeneratorStrategy generatorStrategy;

    private INaturalKeyExtractor naturalKeyExtractor;

    private MongoTemplate mongoTemplate;

    private SubDocAccessor subDocAccessor;

    private final Map<String, SubDocAccessor.Location> locationMap = new HashMap<String, SubDocAccessor.Location>();

    private static final Logger LOG = LoggerFactory.getLogger(ContainerDocumentAccessor.class);

    public ContainerDocumentAccessor(final UUIDGeneratorStrategy strategy, final INaturalKeyExtractor extractor,
                                     final MongoTemplate mongoTemplate) {
        this.generatorStrategy = strategy;
        this.naturalKeyExtractor = extractor;
        this.mongoTemplate = mongoTemplate;
        //TODO: Fix (springify)
        this.containerDocumentHolder = new ContainerDocumentHolder();
        this.subDocAccessor = new SubDocAccessor(mongoTemplate, strategy, extractor);
    }

    public boolean isContainerDocument(final String entity) {
        return containerDocumentHolder.isContainerDocument(entity);
    }

    public boolean isContainerSubdoc(final String entity) {
        boolean isContainerSubdoc = false;
        if (containerDocumentHolder.isContainerDocument(entity)) {
            isContainerSubdoc = containerDocumentHolder.getContainerDocument(entity).isContainerSubdoc();
        }
        return isContainerSubdoc;
    }

    public boolean insert(final List<Entity> entityList) {
        boolean result = true;

        for (Entity entity : entityList) {
            result &= !insert(entity).isEmpty();
        }

        return result;
    }

    public String insert(final Entity entity) {
        final DBObject query = getContainerDocQuery(entity);
        return insertContainerDoc(query, entity);
    }

    public boolean update(final String type, final String id, Map<String, Object> newValues, String collectionName) {
        final Query query = Query.query(Criteria.where("_id").is(id));
        return updateContainerDoc(query, newValues, collectionName, type);
    }

    public String update(final Entity entity) {
        ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        if (containerDocument.isContainerSubdoc()) {
            boolean persisted = getLocation(entity.getType()).create(entity);
            if (persisted) {
                return entity.getEntityId();
            } else {
                return "";
            }
        }
        return updateContainerDoc(entity);
    }

    public Entity findById(String collectionName, String id) {
        return getLocation(collectionName).findById(id);
    }

    public List<Entity> findAll(String collectionName, Query query) {
        return getLocation(collectionName).findAll(query);
    }

    public boolean delete(final Entity entity) {
        return deleteContainerDoc(entity);
    }

    public long count(String collectionName, Query query) {
        return getLocation(collectionName).count(query);
    }

    public boolean exists(String collectionName, String id) {
        return getLocation(collectionName).exists(id);
    }

    private DBObject getContainerDocQuery(final Entity entity) {
        final String parentKey = ContainerDocumentHelper.createParentKey(entity, containerDocumentHolder, generatorStrategy);

        final Query query = Query.query(Criteria.where("_id").is(parentKey));

        return query.getQueryObject();
    }

    // TODO: private


    protected boolean updateContainerDoc(final Query query, Map<String, Object> newValues, String collectionName, String type) {
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(type);
        if (containerDocument.isContainerSubdoc()) {
            Update update = new Update();
            for (Map.Entry<String, Object> patch : newValues.entrySet()) {
                update.set("body." + patch.getKey(), patch.getValue());
            }
            return getLocation(type).doUpdate(query, update);
        }

        TenantContext.setIsSystemCall(false);
        final String fieldToPersist = containerDocument.getFieldToPersist();
        DBObject entityDetails = new BasicDBObject();
        for (Map.Entry<String, Object> newValue : newValues.entrySet()) {
            if (!newValue.getKey().equals(containerDocument.getFieldToPersist())) {
                entityDetails.put("body." + newValue.getKey(), newValue.getValue());
            }
        }
        DBObject set = new BasicDBObject("$set", entityDetails);
        DBObject docToPersist = null;
        if (newValues.containsKey(containerDocument.getFieldToPersist())) {
            docToPersist = BasicDBObjectBuilder.start().push("$pushAll")
                    .add("body." + fieldToPersist, newValues.get(fieldToPersist))
                    .get();
        } else {
            docToPersist = new BasicDBObject();
        }

        docToPersist.putAll(set);

        return mongoTemplate.getCollection(collectionName).update(query.getQueryObject(),
                docToPersist, true, false, WriteConcern.SAFE)
                .getLastError().ok();
    }

    protected String updateContainerDoc(final Entity entity) {
        TenantContext.setIsSystemCall(false);
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        final String fieldToPersist = containerDocument.getFieldToPersist();

        String parentKey = entity.getEntityId();

        final Query query = Query.query(Criteria.where("_id").is(parentKey));
        final Map<String, Object> entityBody = entity.getBody();

        DBObject pullObject = BasicDBObjectBuilder.start().push("$pullAll").
                add("body." + fieldToPersist, entityBody.get(fieldToPersist)).get();
        boolean persisted = true;
        persisted &= mongoTemplate.getCollection(entity.getType()).update(query.getQueryObject(),
                pullObject, false, false, WriteConcern.SAFE)
                .getLastError().ok();


        //persisted &= updateContainerDoc(query, entityBody, entity.getType(), entity.getType());
        if (persisted) {
            return insertContainerDoc(query.getQueryObject(), entity);
        } else {
            return "";
        }
    }

    protected String insertContainerDoc(final DBObject query, final Entity entity) {
        TenantContext.setIsSystemCall(false);

        boolean persisted = true;

        final DBObject docToPersist = ContainerDocumentHelper.buildDocumentToPersist(containerDocumentHolder, entity, generatorStrategy, naturalKeyExtractor);
        ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        persisted &= mongoTemplate.getCollection(containerDocument.getCollectionToPersist()).update(query,
                docToPersist, true, false, WriteConcern.SAFE)
                .getLastError().ok();

        String key = (String) query.get("_id");

        if (containerDocument.isContainerSubdoc()) {
            key = ContainerDocumentHelper.getContainerDocId(entity, generatorStrategy, naturalKeyExtractor);
            getLocation(entity.getType()).create(entity);

        }

        if (persisted) {
            return key;
        } else {
            return "";
        }
    }

    protected boolean deleteContainerDoc(final Entity entity) {
        return getLocation(entity.getType()).delete(entity);
    }

    private SubDocAccessor.Location getLocation(String type) {
        SubDocAccessor.Location location = null;

        if (locationMap.containsKey(type)) {
            location = locationMap.get(type);
        } else {
            ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(type);
            Map<String, String> parentToSubDocField = new HashMap<String, String>();
            for (String parentKey : containerDocument.getParentNaturalKeys()) {
                parentToSubDocField.put(parentKey, "body." + parentKey);
            }
            location = subDocAccessor.createLocation(containerDocument.getCollectionName(), containerDocument.getCollectionToPersist(), parentToSubDocField, containerDocument.getFieldToPersist());
            locationMap.put(type, location);
        }
        return location;
    }


}
