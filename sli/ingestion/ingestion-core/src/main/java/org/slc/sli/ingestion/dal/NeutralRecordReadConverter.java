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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Spring converter registered in the Mongo configuration to convert DBObjects into MongoEntity.
 *
 */
public class NeutralRecordReadConverter implements Converter<DBObject, NeutralRecord> {

    private EntityEncryption encryptor;

    public EntityEncryption getStagingEncryptor() {
        return encryptor;
    }

    public void setStagingEncryptor(EntityEncryption stagingEncryptor) {
        this.encryptor = stagingEncryptor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NeutralRecord convert(DBObject dbObj) {

        String id = (String) dbObj.get("_id");

        String type = null;
        Map<?, ?> map = dbObj.toMap();
        if (map.containsKey("type")) {
            type = dbObj.get("type").toString();
        }
        Map<String, Object> encryptedBody = new HashMap<String, Object>();
        if (map.containsKey("body")) {
            encryptedBody.putAll((Map<String, ?>) map.get("body"));
        }

        // Decrypt the neutral record from datastore persistence. TODO: Create a generic encryptor!
        Map<String, Object> body = encryptedBody;
        if (encryptor != null) {
            body = encryptor.decrypt(type, encryptedBody);
        }

        String batchJobId = null;
        if (dbObj.get("batchJobId") != null) {
            batchJobId = dbObj.get("batchJobId").toString();
        }

        Map<String, Object> localParentIds = null;
        if (map.containsKey("localParentIds")) {
            localParentIds = (Map<String, Object>) map.get("localParentIds");
            cleanMap(localParentIds);
        }
        boolean isAssociation = false;
        if (map.get("association") != null) {
            isAssociation = Boolean.parseBoolean(map.get("association").toString());
        }

        Map<String, Object> metaData = null;
        if (map.containsKey("metaData")) {
            metaData = (Map<String, Object>) map.get("metaData");
            cleanMap(metaData);
        }

        NeutralRecord neutralRecord = new NeutralRecord();
        if (map.get("localId") != null) {
            neutralRecord.setLocalId(map.get("localId").toString());
        }
        neutralRecord.setRecordId(id);
        neutralRecord.setRecordType(type);
        neutralRecord.setAttributes(body);
        neutralRecord.setBatchJobId(batchJobId);
        neutralRecord.setSourceFile((String) map.get("sourceFile"));
        neutralRecord.setVisitBeforeLineNumber(((Integer) map.get("visitBeforeLineNumber")).intValue());
        neutralRecord.setVisitBeforeColumnNumber(((Integer) map.get("visitBeforeColumnNumber")).intValue());
        neutralRecord.setVisitAfterLineNumber(((Integer) map.get("visitAfterLineNumber")).intValue());
        neutralRecord.setVisitAfterColumnNumber(((Integer) map.get("visitAfterColumnNumber")).intValue());
        neutralRecord.setCreationTime((Long) map.get("creationTime"));
        neutralRecord.setLocalParentIds(localParentIds);
        neutralRecord.setAssociation(isAssociation);
        neutralRecord.setMetaData(metaData);
        return neutralRecord;
    }

    @SuppressWarnings({ "unchecked" })
    private static void cleanMap(Map<String, Object> map) {
        List<String> toRemove = new LinkedList<String>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            String key = entry.getKey();
            if (key.contains("%DELIM%")) {
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
