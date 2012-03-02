package org.slc.sli.ingestion.dal;

import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * Spring converter registered in the Mongo configuration to convert MongoEntity objects into
 * DBObjects.
 *
 */
public class NeutralRecordWriteConverter implements Converter<NeutralRecord, DBObject> {

    @Override
    public DBObject convert(NeutralRecord neutralRecord) {

        Map<String, Object> body = neutralRecord.getAttributes();
        body.put("localId", neutralRecord.getLocalId());

        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("type", neutralRecord.getRecordType());
        dbObj.put("_id", UUID.randomUUID());
        dbObj.put("body", body);
        return dbObj;
    }

}
