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

package org.slc.sli.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.BasicBSONObject;
import org.slc.sli.common.domain.EmbedDocumentRelations;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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
    private final Map<String, List<Map<String, Object>>> embeddedData;

    private static final List<String> BASE_ATTRIBUTES = Arrays.asList("_id", "body", "type", "metaData",
            "calculatedValues", "aggregations");

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
        this.embeddedData = new HashMap<String, List<Map<String, Object>>>();
    }

    public MongoEntity(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            CalculatedData<String> calculatedData, CalculatedData<Map<String, Integer>> aggregates,
            Map<String, List<Map<String, Object>>> embeddedData) {
        this.type = type;
        this.entityId = id;
        this.body = body == null ? new BasicBSONObject() : body;
        this.metaData = metaData == null ? new BasicBSONObject() : metaData;
        this.calculatedData = calculatedData == null ? new CalculatedData<String>() : calculatedData;
        this.aggregates = aggregates == null ? new CalculatedData<Map<String, Integer>>() : aggregates;
        this.embeddedData = embeddedData == null ? new HashMap<String, List<Map<String, Object>>>() : embeddedData;
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

    public DBObject toDBObject(UUIDGeneratorStrategy uuidGeneratorStrategy, INaturalKeyExtractor naturalKeyExtractor) {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", type);

        final String uid;

        if (entityId == null) {
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
                if (NaturalKeyExtractor.useDeterministicIds()) {
                    if (naturalKeyDescriptor.isNaturalKeysNotNeeded()) {
                        // generate a truly random id
                        uid = uuidGeneratorStrategy.generateId();
                    } else {
                        uid = uuidGeneratorStrategy.generateId(naturalKeyDescriptor);
                    }
                } else {
                    // generate a truly random id
                    uid = uuidGeneratorStrategy.generateId();
                }
            }

            entityId = uid.toString();
        } else {
            uid = entityId;
        }

        dbObj.put("_id", uid);
        dbObj.put("body", body);
        dbObj.put("metaData", metaData);

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

        Map<String, List<Map<String, Object>>> embeddedData = extractEmbeddedData(dbObj);

        return new MongoEntity(type, id, body, metaData, new CalculatedData<String>(cvals),
                new CalculatedData<Map<String, Integer>>(aggs, "aggregate"), embeddedData);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<Map<String, Object>>> extractEmbeddedData(DBObject dbObj) {
        Map<String, List<Map<String, Object>>> embeddedData = new HashMap<String, List<Map<String, Object>>>();
        for (String key : dbObj.keySet()) {
            // if (!BASE_ATTRIBUTES.contains(key)) {
            if (EmbedDocumentRelations.getSubDocuments().contains(key)) {
                List<Map<String, Object>> values = (List<Map<String, Object>>) dbObj.get(key);
                embeddedData.put(key, Collections.unmodifiableList(values));
            }
        }

        return embeddedData;
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
    public Map<String, List<Map<String, Object>>> getEmbeddedData() {
        return embeddedData;
    }
}
