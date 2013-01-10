package org.slc.sli.datastore.mongo;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

/**
 * Spring converter registered in the Mongo configuration to convert MongoEntity objects into DBObjects.
 * 
 */

public class MongoEntityWriteConverter implements Converter<MongoEntity, DBObject> {
    
    @Override
    public DBObject convert(MongoEntity e) {
        return e.toDBObject();
    }
    
}
