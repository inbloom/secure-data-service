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

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public class ContainerDocumentHelper {

    public static NaturalKeyDescriptor extractNaturalKeyDescriptor(final Entity entity,
                                                                   final List<String> parentKeys) {
        final Map<String, String> naturalKeyMap = new HashMap<String, String>();
        for (final String key : parentKeys) {
            String value = (String) entity.getBody().get(key);
            naturalKeyMap.put(key, value);
        }
        return new NaturalKeyDescriptor(naturalKeyMap);
    }

    public static DBObject buildDocumentToPersist(final ContainerDocumentHolder containerDocumentHolder, final Entity entity, final UUIDGeneratorStrategy generatorStrategy, final INaturalKeyExtractor naturalKeyExtractor ) {
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        DBObject entityDetails = new BasicDBObject();
        final Map<String, Object> entityBody = entity.getBody();
        for (final String key : containerDocument.getParentNaturalKeys()) {
            entityDetails.put("body." + key, entityBody.get(key));
        }

        DBObject docToPersist = null;
        if(containerDocument.isContainerSubdoc()) {
            final Map<String, Object> containerSubDoc = new HashMap<String, Object>();
            String key = createParentKey(entity, containerDocumentHolder, generatorStrategy) + getContainerDocId(entity, generatorStrategy, naturalKeyExtractor);
            containerSubDoc.put("_id", key);
            containerSubDoc.put("type", entity.getType());
            containerSubDoc.put("body", entityBody);
            containerSubDoc.put("metaData", entity.getMetaData());

            final List<Map<String, Object>> containerSubDocList = new ArrayList<Map<String, Object>>();
            containerSubDocList.add(containerSubDoc);
            docToPersist =  BasicDBObjectBuilder.start().get();

        } else {
            if (entity.getMetaData() != null && !entity.getMetaData().isEmpty()) {
                entityDetails.put("metaData", entity.getMetaData());
            }
            entityDetails.put("type", entity.getType());
            String fieldToPersist = containerDocument.getFieldToPersist();
            if (entityBody.containsKey(fieldToPersist)) {
                docToPersist = BasicDBObjectBuilder.start().
                        add("body." + fieldToPersist, entityBody.get(fieldToPersist)).get();
            }

        }
        DBObject set = new BasicDBObject("$set", entityDetails);

        docToPersist.putAll(set);

        return docToPersist;
    }

    public static String createParentKey(final Entity entity, final ContainerDocumentHolder containerDocumentHolder,final UUIDGeneratorStrategy generatorStrategy ) {
        final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());

        if (entity.getEntityId() == null || entity.getEntityId().isEmpty() || containerDocument.isContainerSubdoc()) {
            final List<String> parentKeys = containerDocument.getParentNaturalKeys();
            final NaturalKeyDescriptor naturalKeyDescriptor = extractNaturalKeyDescriptor(entity, parentKeys);
            return generatorStrategy.generateId(naturalKeyDescriptor);
        } else {
            return entity.getEntityId();
        }
    }

    public static String getContainerDocId(final Entity entity, final UUIDGeneratorStrategy generatorStrategy, final INaturalKeyExtractor naturalKeyExtractor ) {
        if (entity.getEntityId() == null || entity.getEntityId().isEmpty()) {
            NaturalKeyDescriptor naturalKeyDescriptor = null;
            try {
                naturalKeyDescriptor = naturalKeyExtractor.getNaturalKeyDescriptor(entity);
            } catch (NoNaturalKeysDefinedException e) {
                throw new RuntimeException(e);
            }
            return generatorStrategy.generateId(naturalKeyDescriptor);
        } else {
            return entity.getEntityId();
        }

    }

    public static String extractParentId(String containerSubdocId) {
        return containerSubdocId.substring(0, containerSubdocId.indexOf("_id") + 3);
    }
}