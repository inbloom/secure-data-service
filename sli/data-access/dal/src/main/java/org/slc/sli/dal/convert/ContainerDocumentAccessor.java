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
import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public class ContainerDocumentAccessor {

    private ContainerDocumentHolder containerDocumentHolder;

    private UUIDGeneratorStrategy generatorStrategy;

    private MongoTemplate mongoTemplate;

    public ContainerDocumentAccessor(final UUIDGeneratorStrategy strategy, final MongoTemplate mongoTemplate) {
        this.generatorStrategy = strategy;
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
            result &= insert(entity);
        }
        return result;
    }

    public boolean insert(final Entity entity) {
        final DBObject query = getContainerDocQuery(entity);
        return insertContainerDoc(query, entity);
    }

    public boolean update(final String type, final String id, Map<String, Object> newValues, String collectionName) {
        final Query query = Query.query(Criteria.where("_id").is(id));
        return updateContainerDoc(query.getQueryObject(), newValues, collectionName, type);
    }

    private DBObject getContainerDocQuery(final Entity entity) {
        final String parentKey = createParentKey(entity);

        final Query query = Query.query(Criteria.where("_id").is(parentKey));

        return query.getQueryObject();
    }

    // TODO: private
    protected String createParentKey(final Entity entity) {
        if (entity.getEntityId() == null || entity.getEntityId().isEmpty()) {
            final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
            final List<String> parentKeys = containerDocument.getParentNaturalKeys();
            final NaturalKeyDescriptor naturalKeyDescriptor = ContainerDocumentHelper.extractNaturalKeyDescriptor(entity, parentKeys);

            return generatorStrategy.generateId(naturalKeyDescriptor);
        } else {
            return entity.getEntityId();
        }
    }

    protected boolean updateContainerDoc(final DBObject query,  Map<String, Object> newValues, String collectionName, String type) {
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
        if(newValues.containsKey(containerDocument.getFieldToPersist()) ) {
        docToPersist = BasicDBObjectBuilder.start().push("$pushAll")
                .add("body." + fieldToPersist, newValues.get(fieldToPersist))
                .get();

        } else {
            docToPersist = new BasicDBObject();
        }

        docToPersist.putAll(set);

        boolean persisted = mongoTemplate.getCollection(collectionName).update(query,
                docToPersist, true, false)
                .getLastError().ok();
        return persisted;
    }

    protected boolean insertContainerDoc(final DBObject query, final Entity entity) {
        TenantContext.setIsSystemCall(false);
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        final String fieldToPersist = containerDocument.getFieldToPersist();

        DBObject entityDetails = new BasicDBObject();

        if (entity.getMetaData() != null || !entity.getMetaData().isEmpty()) {
            entityDetails.put("metaData", entity.getMetaData());
        }
        final Map<String, Object> entityBody = entity.getBody();
        for (final String key : containerDocument.getParentNaturalKeys()) {
            entityDetails.put("body." + key, entityBody.get(key));
        }
        final DBObject docToPersist = BasicDBObjectBuilder.start().push("$pushAll")
                .add("body." + fieldToPersist, entity.getBody().get(fieldToPersist))
                .get();
        DBObject set = new BasicDBObject("$set", entityDetails);

        docToPersist.putAll(set);
        boolean persisted = mongoTemplate.getCollection(entity.getType()).update(query,
                docToPersist, true, false)
                .getLastError().ok();
        return persisted;
    }
}
