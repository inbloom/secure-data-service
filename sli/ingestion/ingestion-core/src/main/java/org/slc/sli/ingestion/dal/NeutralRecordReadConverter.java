package org.slc.sli.ingestion.dal;

import java.util.HashMap;
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
        }

        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(map.get("localId"));
        neutralRecord.setRecordId(id);
        neutralRecord.setRecordType(type);
        neutralRecord.setAttributes(body);
        neutralRecord.setBatchJobId(batchJobId);
        neutralRecord.setSourceFile((String) map.get("sourceFile"));
        neutralRecord.setLocalParentIds(localParentIds);
        return neutralRecord;
    }

}
