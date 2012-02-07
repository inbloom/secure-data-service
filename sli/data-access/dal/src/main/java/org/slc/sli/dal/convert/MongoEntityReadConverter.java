package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.domain.MongoEntity;


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
