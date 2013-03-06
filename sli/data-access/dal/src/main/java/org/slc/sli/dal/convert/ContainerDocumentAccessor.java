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
        final String parentKey = ContainerDocumentHelper.createParentKey(entity, containerDocumentHolder, generatorStrategy );

        final Query query = Query.query(Criteria.where("_id").is(parentKey));

        return query.getQueryObject();
    }

    // TODO: private


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
        String parentKey = entity.getEntityId();
        if(containerDocument.isContainerSubdoc()) {
            parentKey = ContainerDocumentHelper.extractParentId(parentKey);
        }

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

        boolean persisted = true;

        final DBObject docToPersist = ContainerDocumentHelper.buildDocumentToPersist(containerDocumentHolder, entity, generatorStrategy, naturalKeyExtractor);
        ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        persisted &= mongoTemplate.getCollection(containerDocument.getCollectionToPersist()).update(query,
                docToPersist, true, false, WriteConcern.SAFE)
                .getLastError().ok();

        String key = (String) query.get("_id");

        if(containerDocument.isContainerSubdoc()) {
            getLocation(entity.getType()).create(entity);
            key = key + ContainerDocumentHelper.getContainerDocId(entity, containerDocumentHolder, generatorStrategy, naturalKeyExtractor);
        }

        if (persisted) {
            return key;
        } else {
            return "";
        }
    }

    private SubDocAccessor.Location getLocation(String type) {
        SubDocAccessor.Location location = null;

        if(locationMap.containsKey(type)) {
           location = locationMap.get(type);
        } else {
            ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(type);
            Map<String, String> parentToSubDocField = new HashMap<String, String>();
            for(String parentKey :containerDocument.getParentNaturalKeys()) {
                parentToSubDocField.put(parentKey,parentKey);
            }
            location = subDocAccessor.createLocation(containerDocument.getCollectionName(), containerDocument.getCollectionToPersist(), parentToSubDocField, containerDocument.getFieldToPersist());
            locationMap.put(type,location);
        }
        return location;
    }


}
