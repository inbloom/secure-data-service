package org.slc.sli.ingestion.dal;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.model.Error;

/**
 * Spring converter registered in the Mongo configuration to convert Error into DBObject.
 *
 */

public class ErrorWriteConverter implements Converter<Error, DBObject> {

    private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordWriteConverter.class);

    // private EntityEncryption encryptor;
    // @Autowired
    EntityEncryption encryptor;

    @Override
    public DBObject convert(Error error) {

        String errorDetail;

        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("_class", error.getClass().getName());
        dbObj.put("batchJobId", error.getBatchJobId());
        dbObj.put("stageName", error.getStageName());
        dbObj.put("resourceId", error.getResourceId());
        dbObj.put("sourceIp", error.getSourceIp());
        dbObj.put("hostname", error.getHostname());
        dbObj.put("timestamp", error.getTimestamp());
        dbObj.put("severity", error.getSeverity());
        dbObj.put("errorDetail", encryptor.encryptSingleValue(error.getErrorDetail()));
        return dbObj;

    }

    public EntityEncryption getEncryptor() {
        return encryptor;
    }

    public void setEncryptor(EntityEncryption encryptor) {
        this.encryptor = encryptor;
    }

}
