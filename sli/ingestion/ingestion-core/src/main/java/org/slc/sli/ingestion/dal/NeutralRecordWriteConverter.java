package org.slc.sli.ingestion.dal;

import java.util.Map;
import java.util.UUID;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.NeutralRecord;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Spring converter registered in the Mongo configuration to convert MongoEntity objects into
 * DBObjects.
 * 
 */
public class NeutralRecordWriteConverter implements Converter<NeutralRecord, DBObject> {
    
    private EntityEncryption encryptor;
    
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
            uid = UUID.randomUUID();
            neutralRecord.setRecordId(uid.toString());
        } else {
            uid = UUID.fromString(neutralRecord.getRecordId());
        }
        
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", neutralRecord.getRecordType());
        dbObj.put("_id", uid);
        dbObj.put("body", encryptedBody);
        dbObj.put("batchJobId", neutralRecord.getBatchJobId());
        dbObj.put("localId", neutralRecord.getLocalId());
        dbObj.put("sourceFile", neutralRecord.getSourceFile());
        return dbObj;
    }
}
