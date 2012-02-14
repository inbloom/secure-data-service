package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.springframework.core.convert.converter.Converter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;


/**
 * Spring converter registered in the Mongo configuration to convert MongoEntity objects into DBObjects.
 *
 */
public class EntityWriteConverter implements Converter<Entity, DBObject> {

    @Override
    public DBObject convert(Entity e) {
        MongoEntity me;

        if (e instanceof MongoEntity) {
            me = (MongoEntity) e;
        } else {
            me = new MongoEntity(e.getType(), e.getEntityId(), e.getBody(), e.getMetaData());
        }

        return me.toDBObject();
    }

}
