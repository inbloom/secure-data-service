/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dal.convert;

import com.mongodb.DBObject;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

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
    @Qualifier("deterministicUUIDGeneratorStrategy")
    UUIDGeneratorStrategy uuidGeneratorStrategy;
    
    @Autowired
    INaturalKeyExtractor naturalKeyExtractor;
    
    @Override
    public DBObject convert(Entity e) {
        MongoEntity me;
        
        if (e instanceof MongoEntity) {
            me = (MongoEntity) e;
        } else {
            me = new MongoEntity(e.getType(), e.getEntityId(), e.getBody(), e.getMetaData(), e.getCalculatedValues(),
                    e.getAggregates());
        }
        if (encrypt != null) {
            me.encrypt(encrypt);
        }
        return me.toDBObject(uuidGeneratorStrategy, naturalKeyExtractor);
    }
    
}
