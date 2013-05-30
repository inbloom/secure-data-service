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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * This class provides ways to interact with the delta collection
 * 
 * @author ycao
 * 
 */
@Component
public class DeltaEntityIterator implements Iterator<DeltaRecord> {
    
    private static final Logger LOG = LoggerFactory.getLogger(DeltaEntityIterator.class);
    
    @Value("${sli.bulk.extract.deltasBatchSize:1000}")
    private int batchSize;
    
    @Autowired
    private EdOrgContextResolverFactory resolverFactory;
    
    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
    
    @Autowired
    private DeltaJournal deltaJournal;
    
    private DeltaRecord nextDelta;
    
    private Iterator<Map<String, Object>> deltaCursor;
    
    private long lastDeltaTime;
    
    private final static Map<String, List<String>> REQUIRED_EMBEDDED_FIELDS;
    
    private final static Set<String> KEEP_DENORMALIZED = Collections.unmodifiableSet(new HashSet<String>(Arrays
            .asList("assessment", "studentAssessment")));
    
    // the Map<String, Boolean> is a map of entityId:isSpamDelete, must record the
    // spamDelete information before it's lost
    private Map<String, Map<String, Boolean>> idsForCollection = new HashMap<String, Map<String, Boolean>>();
    
    // a queue of stuff that we need to delete
    private Queue<DeltaRecord> deleteQueue = new LinkedList<DeltaRecord>();
    
    // the list that we are currently serving
    private Queue<DeltaRecord> workQueue = new LinkedList<DeltaRecord>();
    
    static {
        Map<String, List<String>> requiredDenormalizedFields = new HashMap<String, List<String>>();
        requiredDenormalizedFields.put("student", Arrays.asList("schools"));
        requiredDenormalizedFields.put("section", Arrays.asList("studentSectionAssociation"));
        requiredDenormalizedFields.put("studentAssessment",
                Arrays.asList("studentAssessmentItem", "studentObjectiveAssessment"));
        REQUIRED_EMBEDDED_FIELDS = Collections.unmodifiableMap(requiredDenormalizedFields);
    }
    
    public enum Operation {
        UPDATE, DELETE, PURGE
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
        Iterable<Entity> entities = repo.findAll(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION,
                queryForLastDeltaTime(tenant));
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
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, true));
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
    
    @SuppressWarnings("rawtypes")
    private DeltaRecord setupNext() {
        if (deltaCursor == null) {
            return null;
        }
        
        // we have existing stuff to serve the call, use it
        if (!workQueue.isEmpty()) {
            return workQueue.poll();
        }
        
        String batchedCollection = null;
        
        // populate the id lists so we can do query in batch
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


            if (collection.equals(DeltaJournal.PURGE)) {
                Entity purge = new MongoEntity(collection, id, delta, null);
                return new DeltaRecord(purge, null, Operation.PURGE, false, collection);
            }
           
            ContextResolver resolver = resolverFactory.getResolver((String) delta.get("c"));
            if (resolver == null) {
                // we have no resolver defined for this type, i.e. this type should not be
                // extracted, do not waste resource to retrieve the mongo entity
                LOG.debug("unable to resolve entity type {}", delta.get("c"));
                continue;
            }
            
            boolean spamDelete = false;
            if (deletedTime > updatedTime) {
                Entity deleted = new MongoEntity(collection, id, null, null);
                deleteQueue.add(new DeltaRecord(deleted, null, Operation.DELETE, false, collection));
                if (deleteQueue.size() >= batchSize) {
                    workQueue = deleteQueue;
                    deleteQueue = new LinkedList<DeltaRecord>();
                    return workQueue.poll();
                }
            } else { // (op == Operation.UPDATE )
                if (deletedTime >= lastDeltaTime) {
                    // this entity's last operation is update, but has a delete that occured within
                    // the
                    // delta window which means this entity has been removed from one LEA and
                    // possibly
                    // moved to another, need to spam this delete to all
                    spamDelete = true;
                }
                
                if (!idsForCollection.containsKey(collection)) {
                    Map<String, Boolean> idList = new HashMap<String, Boolean>(batchSize);
                    idsForCollection.put(collection, idList);
                }
                
                Map<String, Boolean> ids = idsForCollection.get(collection);
                ids.put(id, spamDelete);
                
                if (ids.size() >= batchSize) {
                    batchedCollection = collection;
                    break;
                }
            }
        }
        
        // I am here because workingQueue is empty, and I either got enough ids to do next batch
        // query, or there is no more delta records that I should just start retrieve entities
        // collection by collection
        while (workQueue.isEmpty()) {
            // if batchedCollection is null, just pick anything from the ids collection to use to
            // populate the workqueue
            if (batchedCollection == null) {
                List<String> keys = new ArrayList<String>(idsForCollection.keySet());
                if (keys.size() > 0) {
                    batchedCollection = keys.get(0);
                } else {
                    // there is nothing to retrieve anymore,
                    // just set the working queue to delete queue
                    workQueue = deleteQueue;
                    break;
                }
            }
            
            // populate the work queue
            workQueue = new LinkedList<DeltaRecord>();
            Map<String, Boolean> ids = idsForCollection.remove(batchedCollection);
            ContextResolver resolver = resolverFactory.getResolver(batchedCollection);
            Iterable<Entity> entities = repo.findAll(batchedCollection,
                    buildBatchQuery(batchedCollection, ids.keySet()));
            if (entities instanceof List) {
                LOG.debug(String.format("Retrieved %d entities from %s", ((List) entities).size(), batchedCollection));
            }
            
            Iterator<Entity> it = entities.iterator();
            while (it.hasNext()) {
                Entity entity = it.next();
                
                Boolean spamDelete = ids.remove(entity.getEntityId());
                spamDelete = spamDelete != null ? spamDelete : false;

                // those stuff are deleted with empty bodies around...
                if (entity.getBody() == null || entity.getBody().isEmpty()) {
                    Entity deleted = new MongoEntity(batchedCollection, entity.getEntityId(), null, null);
                    deleteQueue.add(new DeltaRecord(deleted, null, Operation.DELETE, false, batchedCollection));
                    continue;
                }

                Set<String> governingEdOrgs = resolver.findGoverningEdOrgs(entity);
                if (!KEEP_DENORMALIZED.contains(entity.getType())) {
                    entity.getDenormalizedData().clear();
                    entity.getEmbeddedData().clear();
                }
                
                if (governingEdOrgs != null && !governingEdOrgs.isEmpty()) {
                    workQueue.add(new DeltaRecord(entity, governingEdOrgs, Operation.UPDATE, spamDelete,
                            batchedCollection));
                } else {
                    LOG.debug(String.format("Can not resolve the governing lea for entity: %s", entity.getEntityId()));
                }
            }
            
            if (!ids.isEmpty()) {
                LOG.warn("Entity IDs were in deltas collection, but was not in the result of findAll query: " + ids);
                // those ids are most likely been deleted as well...
                for (String id : ids.keySet()) {
                    Entity deleted = new MongoEntity(batchedCollection, id, null, null);
                    deleteQueue.add(new DeltaRecord(deleted, null, Operation.DELETE, false, batchedCollection));
                }
            }
            
            // done with this batch
            batchedCollection = null;
        }
        
        return workQueue.poll();
    }
    
    /**
     * build a batch query with embedded date if required
     * 
     * @param collection
     * @param ids
     * @return a query with embedded data added if required
     */
    public static NeutralQuery buildBatchQuery(String collection, Set<String> ids) {
        NeutralQuery q = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));
        return addEmbeddedQuery(collection, q);
    }
    
    /**
     * build a query with embedded date if required
     * 
     * @param collection
     * @param id
     * @return a query with embedded data added if required
     */
    public static NeutralQuery buildQuery(String collection, String id) {
        NeutralQuery q = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, id));
        return addEmbeddedQuery(collection, q);
    }
    
    private static NeutralQuery addEmbeddedQuery(String collection, NeutralQuery q) {
        if (REQUIRED_EMBEDDED_FIELDS.containsKey(collection)) {
            q.setEmbeddedFields(REQUIRED_EMBEDDED_FIELDS.get(collection));
        }
        return q;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Remove all deltas that is updated before the start of this run, those entities have no values
     * now since we created delta extract for them
     * 
     * @param tenant
     * @param uptoTime
     */
    public void removeAllDeltas(String tenant, DateTime uptoTime) {
        deltaJournal.removeDeltaJournals(tenant, uptoTime.getMillis());
    }
    
    public static class DeltaRecord {
        
        private Entity entity;
        private Set<String> belongsToEdOrgs;
        private Operation op;
        private boolean spamDelete;
        private String type;
        
        public DeltaRecord(Entity entity, Set<String> belongsToEdOrgs, Operation op, boolean spamDelete, String type) {
            this.entity = entity;
            this.belongsToEdOrgs = belongsToEdOrgs;
            this.op = op;
            this.spamDelete = spamDelete;
            this.type = type;
        }
        
        public Entity getEntity() {
            return entity;
        }
        
        public Set<String> getBelongsToEdOrgs() {
            return belongsToEdOrgs;
        }
        
        public Operation getOp() {
            return op;
        }
        
        public boolean isSpamDelete() {
            return spamDelete;
        }
        
        public String getType() {
            return type;
        }
    }
}
