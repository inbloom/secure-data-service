package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;

/**
 * Spring converter registered in the Mongo configuration to convert DBObjects into MongoEntity.
 * 
 */
public class EntityReadConverter implements Converter<DBObject, Entity> {
    
    @Autowired(required = false)
    @Qualifier("entityEncryption")
    EntityEncryption encrypt;
    
    @Autowired
    ApplicationContext ctx;
    
    @Override
    public Entity convert(DBObject dbObj) {
        MongoEntity me = MongoEntity.fromDBObject(dbObj);
        EntityEncryption encrypt = ctx.getBean("entityEncryption", EntityEncryption.class);
        if (encrypt != null) {
            me.decrypt(encrypt);
        }
        return me;
    }
    
}
