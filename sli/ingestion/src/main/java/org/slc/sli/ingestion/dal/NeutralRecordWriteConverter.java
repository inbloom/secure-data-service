package org.slc.sli.ingestion.dal;

import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Spring converter registered in the Mongo configuration to convert MongoEntity objects into
 * DBObjects.
 *
 */
public class NeutralRecordWriteConverter implements Converter<NeutralRecord, DBObject> {

    @Autowired(required = false)
    private EntityEncryption encryptor;

    public void setEncryptor(EntityEncryption encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public DBObject convert(NeutralRecord neutralRecord) {

        Map<String, Object> body = neutralRecord.getAttributes();
        body.put("localId", neutralRecord.getLocalId());

        // Encrypt the neutral record for datastore persistence. TODO: Create a generic encryptor!
        Map<String, Object> encryptedBody = body;
        if (encryptor != null) {
            encryptedBody = encryptor.encrypt(neutralRecord.getRecordType(), neutralRecord.getAttributes());
        }

        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", neutralRecord.getRecordType());
        dbObj.put("_id", UUID.randomUUID());
        dbObj.put("body", encryptedBody);
        dbObj.put("batchJobId", neutralRecord.getBatchJobId());
        return dbObj;
    }

}
