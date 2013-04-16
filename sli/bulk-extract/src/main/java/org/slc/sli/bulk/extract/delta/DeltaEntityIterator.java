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
import org.slc.sli.bulk.extract.context.resolver.EdOrgContextResolverFactory;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.DeltaRecord;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.DeltaJournal;
import org.slc.sli.domain.Entity;
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

    public enum Operation {
        UPDATE,
        DELETE
    }

    public void init(String tenant, DateTime deltaUptoTime) {
        LOG.info(String.format("Generating deltas for tenant: %s", tenant));
        TenantContext.setTenantId(tenant);
        
        long lastDeltaTime = getLastDeltaRun(tenant);
        LOG.info(String.format("creating delta between %d and %d", lastDeltaTime, deltaUptoTime.getMillis()));
        
        deltaCursor = deltaJournal.findDeltaRecordBetween(lastDeltaTime, deltaUptoTime.getMillis());
        nextDelta = setupNext();
    }

    private long getLastDeltaRun(String tenant) {
        long lastRun = 0; // assume if we can't find last time it ran, we need to get all the deltas
        
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, tenant));
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, "true"));
        query.setSortBy("body.date");
        query.setSortOrder(NeutralQuery.SortOrder.descending);
        query.setIncludeFields(Arrays.asList("body.date"));
        
        Iterable<Entity> entities = repo.findAll(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION, query);
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
            long deletedTime = 0;
            long updatedTime = 0;
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

            Entity entity = repo.findById((String) delta.get("c"), (String) delta.get("_id"));
            Set<String> topLevelGoverningLEA = null; 
            if (entity != null) {
                topLevelGoverningLEA = resolverFactory.getResolver((String) delta.get("c")).findGoverningLEA(entity);
            }
            
            if (topLevelGoverningLEA != null && !topLevelGoverningLEA.isEmpty()) {
                return new DeltaRecord(entity, topLevelGoverningLEA, deletedTime > updatedTime ? Operation.DELETE : Operation.UPDATE);
            }
        }
        
        return null;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public static class DeltaRecord {

        private Entity entity;
        private Set<String> belongsToLEA;
        private Operation op;
        
        DeltaRecord(Entity entity, Set<String> belongsToLEA, Operation op) {
            this.entity = entity;
            this.belongsToLEA = belongsToLEA;
            this.op = op;
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
    }
}
