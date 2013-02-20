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

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public class ContainerDocumentAccessor {

    private ContainerDocumentHolder containerDocumentHolder;

    private UUIDGeneratorStrategy generatorStrategy;

    private MongoTemplate mongoTemplate;

    public ContainerDocumentAccessor(final UUIDGeneratorStrategy strategy, final MongoTemplate mongoTemplate) {
        this.generatorStrategy = strategy;
        this.mongoTemplate = mongoTemplate;
        //TODO: Fix (springify)
        this.containerDocumentHolder = new ContainerDocumentHolder();
    }

    public boolean isContainerDocument(final String entity) {
        return containerDocumentHolder.isContainerDocument(entity);
    }

    public boolean insert(final List<Entity> entityList) {
        boolean result = true;

        for (Entity entity : entityList) {
            result &= insert(entity);
        }
        return result;
    }

    public boolean insert(final Entity entity) {

        DBObject query = getContainerDocQuery(entity);
        return insertContainerDoc(query, entity);
    }

    private DBObject getContainerDocQuery(Entity entity) {
        String parentKey = createParentKey(entity);
        //remove parent keys
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(parentKey));
        return query.getQueryObject();

    }

    // TODO: private
    protected String createParentKey(final Entity entity) {
        if(entity.getEntityId().isEmpty()) {
            final ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
            final Map<String, String> parentKeyMap = containerDocument.getParentNaturalKeyMap();
            final NaturalKeyDescriptor naturalKeyDescriptor = ContainerDocumentHelper.extractNaturalKeyDescriptor(entity, parentKeyMap);

            return generatorStrategy.generateId(naturalKeyDescriptor);
        } else {
            return entity.getEntityId();
        }
    }

    protected boolean insertContainerDoc(DBObject query, Entity entity) {
        TenantContext.setIsSystemCall(false);
        ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        boolean persisted = mongoTemplate.getCollection(entity.getType()).update(query,
                BasicDBObjectBuilder.start().push("$pushAll").add(containerDocument.getFieldToPersist(), entity.getBody()).get(), true, false)
                .getLastError().ok();
        return persisted;
    }
}
