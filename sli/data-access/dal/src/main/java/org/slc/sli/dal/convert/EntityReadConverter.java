package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;


/**
 * Spring converter registered in the Mongo configuration to convert DBObjects into MongoEntity.
 *
 */
public class EntityReadConverter implements Converter<DBObject, Entity> {

    @Override
    public Entity convert(DBObject dbObj) {
        return MongoEntity.fromDBObject(dbObj);
    }

}
