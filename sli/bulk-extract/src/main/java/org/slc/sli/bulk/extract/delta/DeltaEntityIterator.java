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
    
    @Value("${sli.bulk.extract.deltasBatchSize:100}")
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
    
    // the Map<String, Boolean> is a map of entityId:isSpamDelete, must record the
    // spamDelete information before it's lost
    private Map<String, Map<String, Boolean>> idsForCollection = new HashMap<String, Map<String, Boolean>>();
    
    // a queue of stuff that we need to delete
    private Queue<DeltaRecord> deleteQueue = new LinkedList<DeltaRecord>();
    
    // the list that we are currently serving
    private Queue<DeltaRecord> workingQueue = new LinkedList<DeltaRecord>();

    static {
        Map<String, List<String>> requiredDenormalizedFields = new HashMap<String, List<String>>();
        requiredDenormalizedFields.put("student", Arrays.asList("schools"));
        requiredDenormalizedFields.put("studentAssessment", Arrays.asList("studentAssessmentItem", "studentObjectiveAssessment"));
        REQUIRED_EMBEDDED_FIELDS = Collections.unmodifiableMap(requiredDenormalizedFields);
    }

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

    private DeltaRecord setupNext() {
        if (deltaCursor == null) {
            return null;
        }

        // we have existing stuff to serve the call, use it
        if (!workingQueue.isEmpty()) {
            return workingQueue.poll();
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
                deleteQueue.add(new DeltaRecord(deleted, null, op, false, collection));
                if (deleteQueue.size() == batchSize) {
                    workingQueue = deleteQueue;
                    deleteQueue = new LinkedList<DeltaRecord>();
                    return workingQueue.poll();
                }
            } else { // (op == Operation.UPDATE )
                if (deletedTime >= lastDeltaTime) {
                    // this entity's last operation is update, but has a delete that occured within the
                    // delta window which means this entity has been removed from one LEA and possibly
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
        while (workingQueue.isEmpty()) {
            // populate the work queue
            if (batchedCollection == null) {
                List<String> keys = new ArrayList<String>(idsForCollection.keySet());
                if (keys.size() > 0) {
                    batchedCollection = keys.get(0);
                } else {
                    // there is nothing to retrieve anymore,
                    // just set the working queue to delete queue
                    workingQueue = deleteQueue;
                    break;
                }
            }

            workingQueue = new LinkedList<DeltaRecord>();
            Map<String, Boolean> ids = idsForCollection.remove(batchedCollection);
            ContextResolver resolver = resolverFactory.getResolver(batchedCollection);
            Iterator<Entity> it = repo.findAll(batchedCollection, buildBatchQuery(batchedCollection, ids.keySet())).iterator();
            
            while (it.hasNext()) {
                Entity entity = it.next();
                Set<String> topLevelGoverningLEA = resolver.findGoverningLEA(entity);
                if (topLevelGoverningLEA != null && !topLevelGoverningLEA.isEmpty()) {
                    workingQueue.add(new DeltaRecord(entity, topLevelGoverningLEA,
                            Operation.UPDATE, ids.get(entity.getEntityId()), batchedCollection));
                } else {
                    LOG.debug(String.format("Can not resolve the governing lea for entity: %s", entity.getEntityId()));
                }
            }
            
            // done with this batch
            batchedCollection = null;
        }
        
        return workingQueue.poll();
    }

    NeutralQuery buildBatchQuery(String collection, Set<String> ids) {
        NeutralQuery q = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));
        LOG.info(String.format("Retrieving %d entities from %s", ids.size(), collection));
        if (REQUIRED_EMBEDDED_FIELDS.containsKey(collection)) {
            q.setEmbeddedFields(REQUIRED_EMBEDDED_FIELDS.get(collection));
        }
        return q;
    }

    NeutralQuery buildQuery(String collection, String id) {
        NeutralQuery q = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, id));
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
        private Set<String> belongsToLEA;
        private Operation op;
        private boolean spamDelete;
        private String type;

        public DeltaRecord(Entity entity, Set<String> belongsToLEA, Operation op, boolean spamDelete, String type) {
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

        public boolean isSpamDelete() {
            return spamDelete;
        }

        public String getType() {
            return type;
        }
    }
}
