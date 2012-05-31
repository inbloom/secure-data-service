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
            uid = uuidGeneratorStrategy.randomUUID();
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

        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", neutralRecord.getRecordType());
        dbObj.put("_id", uid);
        dbObj.put("body", encryptedBody);
        dbObj.put("batchJobId", neutralRecord.getBatchJobId());
        dbObj.put("localId", neutralRecord.getLocalId());
        dbObj.put("localParentIds", localParentIds);
        dbObj.put("sourceFile", neutralRecord.getSourceFile());
        return dbObj;
    }

    @SuppressWarnings("unchecked")
    private static void cleanMap(Map<String, Object> map) {
        List<String> toRemove = new LinkedList<String>();
        for (String key : map.keySet()) {
            if (key.contains(".")) {
                toRemove.add(key);
                LOG.debug("Field being saved to mongo has a . in it.  Removing.  key: {}", key);
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
            map.remove(key);
        }
    }
}
