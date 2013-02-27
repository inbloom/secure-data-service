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

package org.slc.sli.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

/**
 * Mongodb specific implementation of Entity Interface with basic conversion method
 * for convert from and to DBObject
 *
 * @author Dong Liu dliu@wgen.net
 *
 */
public class MongoEntity implements Entity, Serializable {

    private static final long serialVersionUID = -3661562228274704762L;

    private static final Logger LOG = LoggerFactory.getLogger(MongoEntity.class);

    private final String type;

    /** Called entity id to avoid Spring Data using this as the ID field. */
    private String entityId;
    private String stagedEntityId;
    private Map<String, Object> body;
    private final Map<String, Object> metaData;
    private final CalculatedData<String> calculatedData;
    private final CalculatedData<Map<String, Integer>> aggregates;
    private final Map<String, List<Entity>> embeddedData;
    private final Map<String, List<Map<String, Object>>> denormalizedData;

    /**
     * Default constructor for the MongoEntity class.
     *
     * @param type
     *            Mongo Entity type.
     * @param body
     *            Body of Mongo Entity.
     */
    public MongoEntity(String type, Map<String, Object> body) {
        this(type, null, body, null, null, null);
    }

    /**
     * Specify the type, id, body, and metadata for the Mongo Entity using this constructor.
     *
     * @param type
     *            Mongo Entity type.
     * @param id
     *            Mongo Entity id.
     * @param body
     *            Body of Mongo Entity.
     * @param metaData
     *            Metadata of Mongo Entity.
     */
    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData) {
        this(type, id, body, metaData, new CalculatedData<String>(), null);
    }

    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            CalculatedData<String> calculatedData) {
        this(type, id, body, metaData, calculatedData, null);
    }

    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            CalculatedData<String> calculatedData, CalculatedData<Map<String, Integer>> aggregates) {
        this.type = type;
        this.entityId = id;
        this.body = body == null ? new BasicBSONObject() : body;
        this.metaData = metaData == null ? new BasicBSONObject() : metaData;
        this.calculatedData = calculatedData == null ? new CalculatedData<String>() : calculatedData;
        this.aggregates = aggregates == null ? new CalculatedData<Map<String, Integer>>() : aggregates;
        this.embeddedData = new HashMap<String, List<Entity>>();
        this.denormalizedData = new HashMap<String, List<Map<String, Object>>>();
    }

    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            CalculatedData<String> calculatedData, CalculatedData<Map<String, Integer>> aggregates,
            Map<String, List<Entity>> embeddedData, Map<String, List<Map<String, Object>>> denormalizedData) {
        this.type = type;
        this.entityId = id;
        this.body = body == null ? new BasicBSONObject() : body;
        this.metaData = metaData == null ? new BasicBSONObject() : metaData;
        this.calculatedData = calculatedData == null ? new CalculatedData<String>() : calculatedData;
        this.aggregates = aggregates == null ? new CalculatedData<Map<String, Integer>>() : aggregates;
        this.embeddedData = embeddedData == null ? new HashMap<String, List<Entity>>() : embeddedData;
        this.denormalizedData = denormalizedData == null ? new HashMap<String, List<Map<String, Object>>>()
                : denormalizedData;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Map<String, Object> getBody() {
        return body;
    }

    @Override
    public String getStagedEntityId() {
        return stagedEntityId;
    }

    /**
     * This method enables encryption of the entity without exposing the internals to mutation via a
     * setBody() method.
     *
     * @param crypt
     *            The EntityEncryptor to sue
     */
    public void encrypt(EntityEncryption crypt) {
        this.body = crypt.encrypt(getType(), body);
    }

    /**
     * This method enables decryption of the entity without exposing the internals to mutation via a
     * setBody() method.
     *
     * @param crypt
     *            The EntityEncryptor to sue
     */
    public void decrypt(EntityEncryption crypt) {
        this.body = crypt.decrypt(getType(), body);
    }

    public String generateDid(UUIDGeneratorStrategy uuidGeneratorStrategy, INaturalKeyExtractor naturalKeyExtractor) {

    	final String uid;
        NaturalKeyDescriptor naturalKeyDescriptor;
        try {
            naturalKeyDescriptor = naturalKeyExtractor.getNaturalKeyDescriptor(this);
        } catch (NoNaturalKeysDefinedException e) {
            // Nothing can be done with the entity at this point,
            // it is supposed to have natural keys, but none were defined.
            // Picking a random UUID would be undesired behavior
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        if (uuidGeneratorStrategy == null) {
            LOG.warn("Generating Type 4 UUID by default because the UUID generator strategy is null.  This will cause issues if this value is being used in a Mongo indexed field (like _id)");
            uid = UUID.randomUUID().toString();
        } else {
            if (naturalKeyDescriptor.isNaturalKeysNotNeeded()) {
                // generate a truly random id
                uid = uuidGeneratorStrategy.generateId();
            } else {
                uid = uuidGeneratorStrategy.generateId(naturalKeyDescriptor);
            }
        }

        return uid.toString();
    }

    public DBObject toDBObject(UUIDGeneratorStrategy uuidGeneratorStrategy, INaturalKeyExtractor naturalKeyExtractor) {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", type);

        final String uid;

        if (entityId == null) {
            uid = generateDid(uuidGeneratorStrategy, naturalKeyExtractor);
            entityId = uid;
        } else {
            uid = entityId;
        }

        dbObj.put("_id", uid);
        dbObj.put("body", body);
        dbObj.put("metaData", metaData);
        if (embeddedData != null && embeddedData.size() > 0) {
            for (Map.Entry<String, List<Entity>> subdocList : embeddedData.entrySet()) {
                List<DBObject> dbObjs = new ArrayList<DBObject>();
                for (Entity subdocEntity : subdocList.getValue()) {
                    if (subdocEntity instanceof MongoEntity) {
                        dbObjs.add(((MongoEntity) subdocEntity).toDBObject(uuidGeneratorStrategy, naturalKeyExtractor));
                    }
                }
                dbObj.put(subdocList.getKey(), dbObjs);
            }
        }

        return dbObj;
    }

    /**
     * Convert the specified db object to a Mongo Entity.
     *
     * @param dbObj
     *            DBObject that need to be converted to MongoEntity
     * @return converted MongoEntity from DBObject
     */
    @SuppressWarnings("unchecked")
    public static MongoEntity fromDBObject(DBObject dbObj) {
        String type = (String) dbObj.get("type");

        String id = null;
        Object mongoId = dbObj.get("_id");
        if (mongoId instanceof UUID) {
            UUID mongoUuidId = (UUID) mongoId;
            id = mongoUuidId.toString();
        } else {
            id = (String) mongoId;
        }

        Map<String, Object> metaData = (Map<String, Object>) dbObj.get("metaData");
        Map<String, Object> body = (Map<String, Object>) dbObj.get("body");
        Map<String, Map<String, Map<String, Map<String, String>>>> cvals = (Map<String, Map<String, Map<String, Map<String, String>>>>) dbObj
                .get("calculatedValues");
        Map<String, Map<String, Map<String, Map<String, Integer>>>> aggs = (Map<String, Map<String, Map<String, Map<String, Integer>>>>) dbObj
                .get("aggregations");

        Map<String, List<Entity>> embeddedData = extractEmbeddedData(dbObj);
        Map<String, List<Map<String, Object>>> denormalizedData = extractDenormalizedData(dbObj);

        return new MongoEntity(type, id, body, metaData, new CalculatedData<String>(cvals),
                new CalculatedData<Map<String, Integer>>(aggs, "aggregate"), embeddedData, denormalizedData);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<Entity>> extractEmbeddedData(DBObject dbObj) {
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();

        for (String key : dbObj.keySet()) {
            if (EmbeddedDocumentRelations.getSubDocuments().contains(key)) {
                List<DBObject> values = (List<DBObject>) dbObj.get(key);
                List<Entity> subEntityList = new ArrayList<Entity>();
                for (DBObject subEntity : values) {
                    subEntityList.add(fromDBObject(subEntity));
                }
                embeddedData.put(key, subEntityList);
            }
        }

        return embeddedData;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<Map<String, Object>>> extractDenormalizedData(DBObject dbObj) {
        String type = (String) dbObj.get("type");
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();

        for (String key : dbObj.keySet()) {
            if (EmbeddedDocumentRelations.isDenormalization(type, key)) {
                List<Map<String, Object>> values = (List<Map<String, Object>>) dbObj.get(key);
                denormalized.put(key, values);
            }
        }

        return denormalized;
    }

    /**
     * Create and return a Mongo Entity.
     *
     * @param type
     *            Mongo Entity type.
     * @param body
     *            Body of Mongo Entity.
     * @return Newly created Mongo Entity.
     */
    public static MongoEntity create(String type, Map<String, Object> body) {
        return new MongoEntity(type, body);
    }

    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    @Override
    public CalculatedData<String> getCalculatedValues() {
        return calculatedData;
    }

    @Override
    public CalculatedData<Map<String, Integer>> getAggregates() {
        return aggregates;
    }

    @Override
    public Map<String, List<Entity>> getEmbeddedData() {
        return embeddedData;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getDenormalizedData() {
        return denormalizedData;
    }

    @Override
    public String toString() {
        return "MongoEntity " + entityId;
    }
}
