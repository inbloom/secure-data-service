package org.slc.sli.datastore.mongo;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

/**
 * Spring converter registered in the Mongo configuration to convert DBObjects into MongoEntity.
 * 
 */

public class MongoEntityReadConverter implements Converter<DBObject, MongoEntity> {
    
    @Override
    public MongoEntity convert(DBObject dbObj) {
        return MongoEntity.fromDBObject(dbObj);
    }
    
}
