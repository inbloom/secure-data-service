package org.slc.sli.ingestion.dal;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.ingestion.NeutralRecord;


/**
 * Spring converter registered in the Mongo configuration to convert DBObjects into MongoEntity.
 *
 */
public class NeutralRecordReadConverter implements Converter<DBObject, NeutralRecord> {

    @Override
    public NeutralRecord convert(DBObject dbObj) {

        String type = dbObj.get("type").toString();
        String id = dbObj.get("body.localId").toString();
        Map<?, ?> map = dbObj.toMap();
        Map<String, Object> body = new HashMap<String, Object>();
        if (map.containsKey("body")) {
            body.putAll((Map<String, ?>) map.get("body"));
        }
        body.remove("localId");

        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(id);
        neutralRecord.setRecordType(type);
        neutralRecord.setAttributes(body);
        return neutralRecord;
    }

}
