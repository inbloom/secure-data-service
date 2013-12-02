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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.*;

import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.springframework.stereotype.Component;

/**
 * @author pghosh
 */
public class ContainerDocumentAccessor {

    private ContainerDocumentHolder containerDocumentHolder;

    private UUIDGeneratorStrategy generatorStrategy;

    private INaturalKeyExtractor naturalKeyExtractor;

    private MongoTemplate mongoTemplate;

    private SubDocAccessor subDocAccessor;

    private SchemaRepository schemaRepo;

    private final Map<String, SubDocAccessor.Location> locationMap = new HashMap<String, SubDocAccessor.Location>();

    private static final Logger LOG = LoggerFactory.getLogger(ContainerDocumentAccessor.class);

    public ContainerDocumentAccessor(final UUIDGeneratorStrategy strategy, final INaturalKeyExtractor extractor,
                                     final MongoTemplate mongoTemplate, final SchemaRepository schemaRepo) {
        this.generatorStrategy = strategy;
        this.naturalKeyExtractor = extractor;
        this.mongoTemplate = mongoTemplate;
        //TODO: Fix (springify)
        this.containerDocumentHolder = new ContainerDocumentHolder();
        this.subDocAccessor = new SubDocAccessor(mongoTemplate, strategy, extractor);
        this.schemaRepo = schemaRepo;
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

    private Map<String, Object> filterByNaturalKeys(String embeddedDocType, Map<String, Object> doc) {
        Map<String, Object> filteredDoc = new HashMap<String, Object>();
        List<Map<String, Object>> fieldCriteria = new ArrayList<Map<String, Object>>();

        // get natural key fields from schema
        NeutralSchema schema = schemaRepo.getSchema(embeddedDocType);

        // don't filter if the natural keys are unknown
        if (schema == null) {
            return doc;
        }
        
        // loop over natural key fields
        Map<String, NeutralSchema> fieldSchemas = schema.getFields();
        for(Map.Entry<String, NeutralSchema> fieldSchema: fieldSchemas.entrySet()) {
            AppInfo appInfo = (fieldSchema.getValue() == null) ? null : fieldSchema.getValue().getAppInfo();
            if (appInfo != null && appInfo.isNaturalKey()) {
                Map<String, Object> naturalKeyCriteria = new HashMap<String, Object>();
                if (doc.containsKey(fieldSchema.getKey())) {
                    // add it to the update criteria
                    naturalKeyCriteria.put(fieldSchema.getKey(), doc.get(fieldSchema.getKey()));
                    fieldCriteria.add(naturalKeyCriteria);
                } else {
                    Map<String, Object> nonExistCriteria = new HashMap<String, Object>();
                    nonExistCriteria.put(QueryOperators.EXISTS, false);
                    // explicitly exclude missing natural key fields
                    naturalKeyCriteria.put(fieldSchema.getKey(), nonExistCriteria);
                    fieldCriteria.add(naturalKeyCriteria);
                }
            }
        }

        filteredDoc.put(QueryOperators.AND, fieldCriteria);
        return filteredDoc;
    }

    /**
     * Delete embedded documents from container doc record.
     *
     * @param containerDoc
     *        Container document containing embedded entities to delete
     *
     * @return
     *         Whether or not delete was successful.
     */
    @SuppressWarnings("unchecked")
    public boolean deleteContainerNonSubDocs(final Entity containerDoc) {
        // Extract embedded non-subdocs from container doc.
        String collection = containerDoc.getType();
        String embeddedDocType = getEmbeddedDocType(collection);
        List<Map<String, Object>> embeddedDocs = (List<Map<String, Object>>) containerDoc.getBody().get(embeddedDocType);

        // Delete the specified embedded documents from the container doc in the database.
        String containerDocId = containerDoc.getEntityId();
        final BasicDBObject query = new BasicDBObject();
        query.put("_id", containerDocId);
        DBObject result = null;
        for (Map<String, Object> docToDelete : embeddedDocs) {
            // filter update to include natural key values and explicitly exclude missing natural keys
            Map<String, Object> filteredDocToDelete = filterByNaturalKeys(embeddedDocType, docToDelete);
            BasicDBObject dBDocToDelete = new BasicDBObject("body." + embeddedDocType, filteredDocToDelete);
            final BasicDBObject update = new BasicDBObject("$pull", dBDocToDelete);
            result = this.mongoTemplate.getCollection(collection).findAndModify(query, null, null, false, update,
                    true, false);
            if (result == null) {
                LOG.error("Could not delete " + embeddedDocType + " instance from " + collection
                        + " record with id " + containerDocId);
                return false;
            }
        }

        // If this was the last embedded document, delete the container doc as well.
        List<Map<String, Object>> remainingAttendanceEvents = (List<Map<String, Object>>) ((Map<String, Object>) result
                .get("body")).get(embeddedDocType);
        if (remainingAttendanceEvents == null || remainingAttendanceEvents.isEmpty()) {
            Query frQuery = new Query(Criteria.where("_id").is(containerDocId));
            Entity deleted = this.mongoTemplate.findAndRemove(frQuery, Entity.class, collection);
            if (deleted == null) {
                LOG.error("Could not delete empty " + collection + " record with id " + containerDocId);
                return false;
            }
        }

        return true;
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

    /**
     * Get embedded document type for a container doc.
     *
     * @param containerDocType
     *        Type of container document
     *
     * @return
     *         Embedded document type of this container doc
     */
    public String getEmbeddedDocType(final String containerDocType) {
        return containerDocumentHolder.getContainerDocument(containerDocType).getFieldToPersist();
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

        //empty attendanceEvent array
        DBObject emptyArray = new BasicDBObject();
        emptyArray.put("body." + containerDocument.getFieldToPersist(), new ArrayList());
        DBObject setEmptyArray = new BasicDBObject("$set", emptyArray);
        boolean makeEntyArray = mongoTemplate.getCollection(collectionName).update(query.getQueryObject(),
                setEmptyArray, false, false, WriteConcern.SAFE)
                    .getLastError().ok();

        if(!makeEntyArray) return false;

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
        String parentKey = entity.getEntityId();

        final Query query = Query.query(Criteria.where("_id").is(parentKey));

        // delete the embedded docs to update
        boolean removed = deleteContainerNonSubDocs(entity);

        // insert updated embedded docs
        if (removed) {
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
        if( entity == null ) {
            return false;
        }
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
