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


package org.slc.sli.ingestion.dal;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Spring converter registered in the Mongo configuration to convert MongoEntity objects into
 * DBObjects.
 *
 */
public class NeutralRecordWriteConverter implements Converter<NeutralRecord, DBObject> {
    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordWriteConverter.class);


    private EntityEncryption encryptor;

    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    public EntityEncryption getStagingEncryptor() {
        return encryptor;
    }

    public void setStagingEncryptor(EntityEncryption stagingEncryptor) {
        this.encryptor = stagingEncryptor;
    }

    @Override
    public DBObject convert(NeutralRecord neutralRecord) {

        Map<String, Object> body = neutralRecord.getAttributes();

        // Encrypt the neutral record for datastore persistence. TODO: Create a generic encryptor!
        Map<String, Object> encryptedBody = body;
        if (encryptor != null) {
            encryptedBody = encryptor.encrypt(neutralRecord.getRecordType(), neutralRecord.getAttributes());
        }

        String uid = null;
        if (neutralRecord.getRecordId() == null) {
            uid = uuidGeneratorStrategy.generateId();
            neutralRecord.setRecordId(uid);
        } else {
            uid = neutralRecord.getRecordId();
        }

        Map<String, Object> localParentIds = neutralRecord.getLocalParentIds();
        if (localParentIds != null) {
            // The old ingestion id resolver code used fields with "." in the name. This will cause the
            // mongo driver to throw an exception. If one of those fields exist in an entity being
            // saved to here, it is likely a legacy smooks config nobody bothered to update.
            cleanMap(localParentIds);
        }

        Map<String, Object> metaData = neutralRecord.getMetaData();
        if (metaData != null) {
            // The old ingestion id resolver code used fields with "." in the name. This will cause the
            // mongo driver to throw an exception. If one of those fields exist in an entity being
            // saved to here, it is likely a legacy smooks config nobody bothered to update.
            cleanMap(metaData);
        }

        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", neutralRecord.getRecordType());
        dbObj.put("_id", uid);
        dbObj.put("body", encryptedBody);
        dbObj.put("batchJobId", neutralRecord.getBatchJobId());
        if (neutralRecord.getLocalId() != null) {
            dbObj.put("localId", neutralRecord.getLocalId().toString());
        }
        dbObj.put("localParentIds", localParentIds);
        dbObj.put("sourceFile", neutralRecord.getSourceFile());
        dbObj.put("locationInSourceFile", neutralRecord.getLocationInSourceFile());
        dbObj.put("visitBeforeLineNumber", neutralRecord.getVisitBeforeLineNumber());
        dbObj.put("visitBeforeColumnNumber", neutralRecord.getVisitBeforeColumnNumber());
        dbObj.put("visitAfterLineNumber", neutralRecord.getVisitAfterLineNumber());
        dbObj.put("visitAfterColumnNumber", neutralRecord.getVisitAfterColumnNumber());
        dbObj.put("association", neutralRecord.isAssociation());

        if (neutralRecord.getCreationTime() != 0) {
            dbObj.put("creationTime", neutralRecord.getCreationTime());
        } else {
            dbObj.put("creationTime", System.currentTimeMillis());
        }

        dbObj.put("metaData", metaData);

        return dbObj;
    }

    @SuppressWarnings("unchecked")
    private static void cleanMap(Map<String, Object> map) {
        List<String> toRemove = new LinkedList<String>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            String key = entry.getKey();
            if (key.contains(".")) {
                LOG.debug("Field being saved to mongo has a . in it.  Wrapping in quotes  key: {}", key);
                toRemove.add(key);
            } else {
                Object val = map.get(key);
                if (val instanceof Map) {
                    cleanMap((Map<String, Object>) val);
                } else if (val instanceof List) {
                    for (Object item : ((List<Object>) val)) {
                        if (item instanceof Map) {
                            cleanMap((Map<String, Object>) item);
                        }
                    }
                }
            }
        }
        for (String key : toRemove) {
            Object value = map.remove(key);
            String newKey = key.replace("%DELIM%", ".");
            map.put(newKey, value);
        }
    }
}
