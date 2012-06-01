package org.slc.sli.ingestion.dal;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    @Qualifier("type4UUIDGeneratorStrategy")
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

        UUID uid = null;
        if (neutralRecord.getRecordId() == null) {
            uid = uuidGeneratorStrategy.randomUUID();
            neutralRecord.setRecordId(uid.toString());
        } else {
            uid = UUID.fromString(neutralRecord.getRecordId());
        }

        Map<String, Object> localParentIds = neutralRecord.getLocalParentIds();
        if (localParentIds != null) {
            // The old ingestion id resolver code used fields with "." in the name. This will cause the
            // mongo driver to throw an exception. If one of those fields exist in an entity being
            // saved to here, it is likely a legacy smooks config nobody bothered to update.
            cleanMap(localParentIds);
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
        dbObj.put("association", neutralRecord.isAssociation());
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
            key = key.replaceAll("\\.", "%DELIM%");
            map.put(key, value);
        }
    }
}
