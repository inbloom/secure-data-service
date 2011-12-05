package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.slc.sli.domain.MongoEntity;
import org.springframework.core.convert.converter.Converter;

public class MongoEntityWriteConverter implements Converter<MongoEntity, DBObject> {
    
    public DBObject convert(MongoEntity e) {
        return e.toDBObject();
    }
    
}
