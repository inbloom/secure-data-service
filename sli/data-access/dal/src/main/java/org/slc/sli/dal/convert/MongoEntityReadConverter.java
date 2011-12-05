package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.slc.sli.domain.MongoEntity;
import org.springframework.core.convert.converter.Converter;

public class MongoEntityReadConverter implements Converter<DBObject, MongoEntity> {
    
    public MongoEntity convert(DBObject dbObj) {
        return MongoEntity.fromDBObject(dbObj);
    }
    
}
