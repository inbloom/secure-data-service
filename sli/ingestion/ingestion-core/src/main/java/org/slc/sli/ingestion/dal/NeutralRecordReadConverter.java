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

        String uuid = (String) dbObj.get("_id");
        String id = uuid.toString();

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

        NeutralRecord neutralRecord = new NeutralRecord();
        if (map.get("localId") != null) {
            neutralRecord.setLocalId(map.get("localId").toString());
        }
        neutralRecord.setRecordId(id);
        neutralRecord.setRecordType(type);
        neutralRecord.setAttributes(body);
        neutralRecord.setBatchJobId(batchJobId);
        neutralRecord.setSourceFile((String) map.get("sourceFile"));
        neutralRecord.setLocationInSourceFile(((Integer) map.get("locationInSourceFile")).intValue());
        neutralRecord.setLocalParentIds(localParentIds);
        neutralRecord.setAssociation(isAssociation);
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
            key = key.replaceAll("%DELIM%", ".");
            map.put(key, value);
        }
    }

}
