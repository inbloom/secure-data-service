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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Generic superdoc converter for most basic operations
 */
@Component
public class GenericSuperdocConverter {
	
    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    UUIDGeneratorStrategy uuidGeneratorStrategy;
    
    @Autowired
    INaturalKeyExtractor naturalKeyExtractor; 
    
    /*
     *  Move subdocs from embedded data to superdoc's body
     */
    protected Entity subdocsToBody(Entity parent, String subentityType, List<String> removeFields) {
        if (parent.getEmbeddedData() == null || parent.getEmbeddedData().isEmpty()) {
            return parent;
        }

        List<Entity> subdocs = parent.getEmbeddedData().remove(subentityType);
        if (subdocs == null || subdocs.isEmpty()) {
            return parent;
        }

        List<Map<String, Object>> subdocBody = new ArrayList<Map<String, Object>>();
        for (Entity e : subdocs) {
            for (String field : removeFields) {
                e.getBody().remove(field);
            }
            subdocBody.add(e.getBody());
        }
        parent.getBody().put(subentityType, subdocBody);

        return parent;
    } 

    /*
     * Move subdocs from superdoc's body to their own inside embedded data
     */
    protected Entity bodyToSubdocs(Entity parent, String subentityType, String parentKey) {
        if (parent.getBody().get(subentityType) != null) {
            List<Entity> subdocs = new ArrayList<Entity>();
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> subdocInBody = (List<Map<String, Object>>) parent.getBody().get(subentityType);
            for (Map<String, Object> inbodyDoc : subdocInBody) {
                inbodyDoc.put(parentKey, generateSuperdocDid(parent));
                MongoEntity subdoc = new MongoEntity(subentityType, null, inbodyDoc, null);
                subdocs.add(subdoc);
            }

            if (!subdocs.isEmpty()) {
                parent.getEmbeddedData().put(subentityType, subdocs);
                parent.getBody().remove(subentityType);
            }
        }

        return parent;
    }

    /**
     * calculates the Determinsitic ID of this entity
     * @param entity
     * @return
     */
    protected String generateSuperdocDid(Entity entity) {
        if (entity instanceof MongoEntity) {
            return ((MongoEntity) entity).generateDid(uuidGeneratorStrategy, naturalKeyExtractor);
        } else {
            MongoEntity wrapper = new MongoEntity(entity.getType(), null, entity.getBody(), entity.getMetaData());
            return wrapper.generateDid(uuidGeneratorStrategy, naturalKeyExtractor);
        }
    }
}
