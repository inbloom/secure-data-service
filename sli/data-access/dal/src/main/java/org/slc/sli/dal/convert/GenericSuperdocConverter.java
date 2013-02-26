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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

/**
 * Generic superdoc converter for most basic operations
 *
 * @author Yang Cao ycao@wgen.net
 * @author Dong Liu dliu@wgen.net
 */
@Component
public class GenericSuperdocConverter {

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    UUIDGeneratorStrategy uuidGeneratorStrategy;

    @Autowired
    INaturalKeyExtractor naturalKeyExtractor;

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    /*
     * Move subdocs from embedded data to superdoc's body
     */
    protected void subdocsToBody(Entity parent, String subdocFieldName, String inBodyFieldName,
            List<String> removeFields) {
        if (parent.getEmbeddedData() != null && parent.getEmbeddedData().size() > 0) {

            List<Entity> subdocs = parent.getEmbeddedData().remove(subdocFieldName);
            if (subdocs != null && subdocs.size() > 0) {

                List<Map<String, Object>> subdocBody = new ArrayList<Map<String, Object>>();

                for (Entity e : subdocs) {
                    if (removeFields != null && removeFields.size() > 0) {
                        for (String field : removeFields) {
                            e.getBody().remove(field);
                        }
                    }
                    subdocBody.add(e.getBody());
                }
                parent.getBody().put(inBodyFieldName, subdocBody);
            }
        }

    }

    /*
     * Move subdocs from superdoc's body to their own inside embedded data
     */
    protected void bodyToSubdocs(Entity parent, String subdocFieldName, String inBodyFieldName, String parentKey) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> subdocInBody = (List<Map<String, Object>>) parent.getBody().remove(inBodyFieldName);
        if (subdocInBody != null) {
            makeSubDocs(parent, subdocFieldName, parentKey, subdocInBody);
        }
    }

    protected void makeSubDocs(Entity parent, String subdocFieldName, String parentKey,
            List<Map<String, Object>> subdocFromBody) {
        List<Entity> subdocs = new ArrayList<Entity>();

        for (Map<String, Object> inbodyDoc : subdocFromBody) {
            String parentId = generateDid(parent);
            // if the parentKey is removed on subdoc to body transformation, put it back
            if (parentKey != null && !parentKey.isEmpty() && inbodyDoc.get(parentKey) == null) {
                inbodyDoc.put(parentKey, parentId);
            }

            // assume subdocFieldName is the subdoc entity type
            MongoEntity subdoc = new MongoEntity(subdocFieldName, generateSubdocDid(inbodyDoc, subdocFieldName),
                    inbodyDoc, null);

            subdocs.add(subdoc);
        }

        if (!subdocs.isEmpty()) {
            parent.getEmbeddedData().put(subdocFieldName, subdocs);
        }
    }

    /**
     * calculates the Determinsitic ID of this entity
     *
     * @param entity
     * @return
     */
    protected String generateDid(Entity entity) {
        if (entity instanceof MongoEntity) {
            return ((MongoEntity) entity).generateDid(uuidGeneratorStrategy, naturalKeyExtractor);
        } else {
            MongoEntity wrapper = new MongoEntity(entity.getType(), null, entity.getBody(), entity.getMetaData());
            return wrapper.generateDid(uuidGeneratorStrategy, naturalKeyExtractor);
        }
    }

    /**
     * calculates the Determinsitic ID of subdoc entity
     *
     * @param subEntityBody
     * @param subEntityType
     * @return
     */
    protected String generateSubdocDid(Map<String, Object> subEntityBody, String subEntityType) {
        MongoEntity subEntity = new MongoEntity(subEntityType, null, subEntityBody, null);
        return generateDid(subEntity);
    }

}
