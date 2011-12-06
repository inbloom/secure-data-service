package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.domain.MongoEntity;

public class MongoEntityReadConverter implements Converter<DBObject, MongoEntity> {
    
    @Override
    public MongoEntity convert(DBObject dbObj) {
        return MongoEntity.fromDBObject(dbObj);
    }
    
}
