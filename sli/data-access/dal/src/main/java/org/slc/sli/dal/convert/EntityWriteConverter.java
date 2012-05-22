package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * Spring converter registered in the Mongo configuration to convert MongoEntity objects into
 * DBObjects.
 *
 */
public class EntityWriteConverter implements Converter<Entity, DBObject> {

    @Autowired(required = false)
    @Qualifier("entityEncryption")
    EntityEncryption encrypt;

    @Autowired
    UUIDGeneratorStrategy uuidGeneratorStrategy;

    @Override
    public DBObject convert(Entity e) {
        MongoEntity me;

        if (e instanceof MongoEntity) {
            me = (MongoEntity) e;
        } else {
            me = new MongoEntity(e.getType(), e.getEntityId(), e.getBody(), e.getMetaData());
        }
        if (encrypt != null) {
            me.encrypt(encrypt);
        }
        return me.toDBObject(uuidGeneratorStrategy);
    }

}
