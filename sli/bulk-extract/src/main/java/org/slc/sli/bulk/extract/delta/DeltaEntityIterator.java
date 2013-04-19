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
package org.slc.sli.bulk.extract.delta;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.bulk.extract.context.resolver.EdOrgContextResolverFactory;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.DeltaRecord;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.DeltaJournal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

@Component
public class DeltaEntityIterator implements Iterator<DeltaRecord> {
    
    private static final Logger LOG = LoggerFactory.getLogger(DeltaEntityIterator.class);
    
    @Autowired
    EdOrgContextResolverFactory resolverFactory;
    
    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;
    
    @Autowired
    DeltaJournal deltaJournal;
    
    private DeltaRecord nextDelta;

    private Iterator<Map<String, Object>> deltaCursor;
    
    private long lastDeltaTime;

    public enum Operation {
        UPDATE,
        DELETE
    }

    public void init(String tenant, DateTime deltaUptoTime) {
        LOG.info(String.format("Generating deltas for tenant: %s", tenant));
        TenantContext.setTenantId(tenant);
        
        lastDeltaTime = getLastDeltaRun(tenant);
        LOG.info(String.format("creating delta between %d and %d", lastDeltaTime, deltaUptoTime.getMillis()));
        
        deltaCursor = deltaJournal.findDeltaRecordBetween(lastDeltaTime, deltaUptoTime.getMillis());
        nextDelta = setupNext();
    }

    private long getLastDeltaRun(String tenant) {
        long lastRun = 0; // assume if we can't find last time it ran, we need to get all the deltas
        
        TenantContext.setIsSystemCall(true);
        Iterable<Entity> entities = repo.findAll(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION, queryForLastDeltaTime(tenant));
        TenantContext.setIsSystemCall(false);
        if (entities == null) {
            return lastRun;
        }
        
        if (entities.iterator().hasNext()) {
            Map<String, Object> body = entities.iterator().next().getBody();
            if (body != null) {
                Date date = (Date) body.get("date");
                lastRun = (date != null) ? date.getTime() : 0;
            }
        }
        
        return lastRun;
        
    }
    
    private NeutralQuery queryForLastDeltaTime(String tenant) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, tenant));
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, "true"));
        query.setSortBy("body.date");
        query.setSortOrder(NeutralQuery.SortOrder.descending);
        query.setIncludeFields(Arrays.asList("date"));
        query.setLimit(1);
        return query;
    }

    @Override
    public boolean hasNext() {
        return nextDelta != null;
    }
    
    @Override
    public DeltaRecord next() {
        DeltaRecord res = nextDelta;
        nextDelta = setupNext();
        return res;
    }
    
    private DeltaRecord setupNext() {
        if (deltaCursor == null) {
            return null;
        }
       
        while (deltaCursor.hasNext()) {
            Map<String, Object> delta = deltaCursor.next();
            long deletedTime = -1;
            long updatedTime = -1;

            if (delta.containsKey("d")) {
                deletedTime = (Long) delta.get("d");
            }
            if (delta.containsKey("u")) {
                updatedTime = (Long) delta.get("u");
            }
            
            String id = (String) delta.get("_id");
            if ("null".equals(id) || id == null) {
                continue;
            }
            
            String collection = (String) delta.get("c");
            ContextResolver resolver = resolverFactory.getResolver((String) delta.get("c"));
            if (resolver == null) {
                // we have no resolver defined for this type, i.e. this type should not be
                // extracted, do not waste resource to retrieve the mongo entity
                continue;
            }
            
            boolean spamDelete = false;
            Operation op = deletedTime > updatedTime ? Operation.DELETE : Operation.UPDATE;
            if (op == Operation.DELETE) {
                Entity deleted = new MongoEntity(collection, id, null, null);
                return new DeltaRecord(deleted, null, op, false, collection);
            }

            if (op == Operation.UPDATE && deletedTime >= lastDeltaTime) {
                // this entity's last operation is update, but has a delete that occured within the
                // delta window which means this entity has been removed from one LEA and possibly
                // moved to another, need to spam this delete to all
                spamDelete = true;
            }

            
            Entity entity = repo.findById(collection, id);
            Set<String> topLevelGoverningLEA = null; 
            if (entity != null) {
                topLevelGoverningLEA = resolver.findGoverningLEA(entity);
            }
            
            if (topLevelGoverningLEA != null && !topLevelGoverningLEA.isEmpty()) {
                return new DeltaRecord(entity, topLevelGoverningLEA, op, spamDelete, collection);
            }
        }
        
        return null;
    }
    
    @Override
    public void remove() {
        // assume bulk extract is read from secondary only, there is no remove support
        throw new UnsupportedOperationException();
    }

    public static class DeltaRecord {

        private Entity entity;
        private Set<String> belongsToLEA;
        private Operation op;
        private boolean spamDelete;
        private String type;
        
        DeltaRecord(Entity entity, Set<String> belongsToLEA, Operation op, boolean spamDelete, String type) {
            this.entity = entity;
            this.belongsToLEA = belongsToLEA;
            this.op = op;
            this.spamDelete = spamDelete;
            this.type = type;
        }
        
        public Entity getEntity() {
            return entity;
        }
        
        public Set<String> getBelongsToLEA() {
            return belongsToLEA;
        }
        
        public Operation getOp() {
            return op;
        }
        
        public void setEntity(Entity e) {
            this.entity = e;
        }
        
        public void setBelongsToLEA(Set<String> belongsToLEA) {
            this.belongsToLEA = belongsToLEA;
        }
        
        public void setOp(Operation op) {
            this.op = op;
        }

        public boolean isSpamDelete() {
            return spamDelete;
        }

        public void setSpamDelete(boolean spamDelete) {
            this.spamDelete = spamDelete;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
