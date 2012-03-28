package org.slc.sli.ingestion.dal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Spring converter registered in the Mongo configuration to convert DBObjects into MongoEntity.
 *
 */
public class NeutralRecordReadConverter implements Converter<DBObject, NeutralRecord> {

    @Autowired(required = false)
    private EntityEncryption encryptor;

    public void setEncryptor(EntityEncryption encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NeutralRecord convert(DBObject dbObj) {

        UUID uuid = (UUID) dbObj.get("_id");
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

        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordId(id);
        neutralRecord.setRecordType(type);
        neutralRecord.setAttributes(body);
        neutralRecord.setBatchJobId(batchJobId);
        return neutralRecord;
    }

}
