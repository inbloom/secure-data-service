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
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
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

    private static final Logger LOG = LoggerFactory.getLogger(ContainerDocumentAccessor.class);

    public ContainerDocumentAccessor(final UUIDGeneratorStrategy strategy, final INaturalKeyExtractor extractor,
                                     final MongoTemplate mongoTemplate) {
        this.generatorStrategy = strategy;
        this.naturalKeyExtractor = extractor;
        this.mongoTemplate = mongoTemplate;
        //TODO: Fix (springify)
        this.containerDocumentHolder = new ContainerDocumentHolder();
    }

    public boolean isContainerDocument(final String entity) {
        return containerDocumentHolder.isContainerDocument(entity);
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
        return updateContainerDoc(query.getQueryObject(), newValues, collectionName, type);
    }

    public String update(final Entity entity) {
        return updateContainerDoc(entity);
    }

    private DBObject getContainerDocQuery(final Entity entity) {
        final String parentKey = createParentKey(entity);

        final Query query = Query.query(Criteria.where("_id").is(parentKey));

        return query.getQueryObject();
    }

    // TODO: private
    protected String createParentKey(final Entity entity) {
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());

        if (entity.getEntityId() == null || entity.getEntityId().isEmpty() || containerDocument.isContainerSubdoc()) {
            final List<String> parentKeys = containerDocument.getParentNaturalKeys();
            final NaturalKeyDescriptor naturalKeyDescriptor = ContainerDocumentHelper.extractNaturalKeyDescriptor(entity, parentKeys);
            return generatorStrategy.generateId(naturalKeyDescriptor);
        } else {
            return entity.getEntityId();
        }
    }
    protected String getContainerDocId(final Entity entity) {
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        if (entity.getEntityId() == null || entity.getEntityId().isEmpty()) {
            NaturalKeyDescriptor naturalKeyDescriptor = null;
            try {
                naturalKeyDescriptor = naturalKeyExtractor.getNaturalKeyDescriptor(entity);
            } catch (NoNaturalKeysDefinedException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            return generatorStrategy.generateId(naturalKeyDescriptor);
        } else {
            return entity.getEntityId();
        }

    }

    protected boolean updateContainerDoc(final DBObject query, Map<String, Object> newValues, String collectionName, String type) {
        TenantContext.setIsSystemCall(false);
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(type);
        final String fieldToPersist = containerDocument.getFieldToPersist();

        DBObject entityDetails = new BasicDBObject();


        for (Map.Entry<String, Object> newValue : newValues.entrySet()) {
            if (newValue.getKey().equals(containerDocument.getFieldToPersist())) {
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

        boolean persisted = mongoTemplate.getCollection(collectionName).update(query,
                docToPersist, true, false, WriteConcern.SAFE)
                .getLastError().ok();
        return persisted;
    }

    protected String updateContainerDoc(final Entity entity) {
        TenantContext.setIsSystemCall(false);
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        final String fieldToPersist = containerDocument.getFieldToPersist();

        DBObject entityDetails = new BasicDBObject();
        final String parentKey = createParentKey(entity);

        final Query query = Query.query(Criteria.where("_id").is(parentKey));


        final Map<String, Object> entityBody = entity.getBody();
        for (final String key : containerDocument.getParentNaturalKeys()) {
            entityDetails.put("body." + key, entityBody.get(key));
        }
        Object arrayFieldToPersist = entity.getBody().get(fieldToPersist);
        boolean persisted = true;

        Entity mongoEntity = mongoTemplate.findOne(query, Entity.class, entity.getType());
        Set<Object> persistedArrayField = new HashSet<Object>();
        List<Object> mongoList = (List<Object>) mongoEntity.getBody().get(fieldToPersist);
        for (Object listObject : mongoList) {
            persistedArrayField.add(listObject);
        }
        if (arrayFieldToPersist instanceof Collection) {
            persistedArrayField.addAll((Collection<?>) arrayFieldToPersist);
        } else {
            persistedArrayField.add(arrayFieldToPersist);
        }

        for (final String key : containerDocument.getParentNaturalKeys()) {
            entityDetails.put("body." + key, entityBody.get(key));
        }
        entityDetails.put("body." + fieldToPersist, persistedArrayField);
        DBObject set = new BasicDBObject("$set", entityDetails);

        persisted &= mongoTemplate.getCollection(entity.getType()).update(query.getQueryObject(),
                set, false, false, WriteConcern.SAFE)
                .getLastError().ok();


        //persisted &= updateContainerDoc(query, entityBody, entity.getType(), entity.getType());
        if (persisted) {
            return (String) query.getQueryObject().get("_id");
        } else {
            return "";
        }
    }

    protected String insertContainerDoc(final DBObject query, final Entity entity) {
        TenantContext.setIsSystemCall(false);
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        final String fieldToPersist = containerDocument.getFieldToPersist();

        boolean persisted = true;
        if(containerDocument.isContainerSubdoc()) {
            persisted = persistAsSubdoc(entity, query, containerDocument);
        }

        DBObject entityDetails = new BasicDBObject();

        if (entity.getMetaData() != null && !entity.getMetaData().isEmpty()) {
            entityDetails.put("metaData", entity.getMetaData());
        }
        entityDetails.put("type", entity.getType());
        final Map<String, Object> entityBody = entity.getBody();
        for (final String key : containerDocument.getParentNaturalKeys()) {
            entityDetails.put("body." + key, entityBody.get(key));
        }
        BasicDBObjectBuilder dbObjectBuilder = BasicDBObjectBuilder.start();
        if (entityBody.containsKey(fieldToPersist)) {
            dbObjectBuilder.push("$pushAll")
                    .add("body." + fieldToPersist, entityBody.get(fieldToPersist));
        }
        final DBObject docToPersist = dbObjectBuilder.get();

        LOG.debug(entity.getEntityId());
        DBObject set = new BasicDBObject("$set", entityDetails);

        docToPersist.putAll(set);
        persisted &= mongoTemplate.getCollection(entity.getType()).update(query,
                docToPersist, true, false, WriteConcern.SAFE)
                .getLastError().ok();

        if (persisted) {
            return (String) query.get("_id");
        } else {
            return "";
        }
    }
    private String extractParentId(String containerSubdocId) {
        return containerSubdocId.substring(0, containerSubdocId.indexOf("_id") + 3);
    }

    private boolean persistAsSubdoc(Entity entity, DBObject query, ContainerDocument containerDocument) {

        DBObject entityDetails = new BasicDBObject();
        final Map<String, Object> entityBody = entity.getBody();
        for (final String key : containerDocument.getParentNaturalKeys()) {
            entityDetails.put("body." + key, entityBody.get(key));
        }

        final Map<String, Object> containerSubDoc = new HashMap<String, Object>();
        containerSubDoc.put("_id",createParentKey(entity) + getContainerDocId(entity));
        containerSubDoc.put("type", entity.getType());
        containerSubDoc.put("body", entityBody);
        containerSubDoc.put("metaData", entity.getMetaData());

        final List<Map<String, Object>> containerSubDocList = new ArrayList<Map<String, Object>>();
        containerSubDocList.add(containerSubDoc);

        BasicDBObjectBuilder dbObjectBuilder = BasicDBObjectBuilder.start().push("$pushAll")
                .add(containerDocument.getFieldToPersist(), containerSubDocList.toArray());
        DBObject set = new BasicDBObject("$set", entityDetails);
        DBObject docToPersist = dbObjectBuilder.get();
        docToPersist.putAll(set);

        boolean persisted = mongoTemplate.getCollection(containerDocument.getCollectionToPersist()).update(query,
                docToPersist, true, false, WriteConcern.SAFE )
                .getLastError().ok();
        return persisted;
    }
}
