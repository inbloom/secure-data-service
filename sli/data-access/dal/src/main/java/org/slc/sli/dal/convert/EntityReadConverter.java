package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * Spring converter registered in the Mongo configuration to convert DBObjects into MongoEntity.
 *
 */
public class EntityReadConverter implements Converter<DBObject, Entity> {

    @Autowired(required = false)
    @Qualifier("entityEncryption")
    EntityEncryption encrypt;

    @Override
    public Entity convert(DBObject dbObj) {
        MongoEntity me = MongoEntity.fromDBObject(dbObj);
        if (encrypt != null) {
            me.decrypt(encrypt);
        }
        return me;
    }

}
