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
package org.slc.sli.dal.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;

/**
 * Provides journaling of entity updates for delta processing
 *
 * @author nbrown
 *
 */
@Component
public class DeltaJournal implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(DeltaJournal.class);

    @Value("${sli.bulk.extract.deltasEnabled:true}")
    private boolean deltasEnabled;

    @Value("${sli.sandbox.enabled}")
    private boolean isSandbox;

    public static final String DELTA_COLLECTION = "deltas";

    public static final String PURGE = "purge";

    // in paged query, get upto 50000 items each time
    @Value("${sli.bulk.extract.delta.iterationSize:50000}")
    private int limit;

    @Autowired
    @Qualifier("journalTemplate")
    private MongoTemplate template;

    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    private final Map<String, String> subdocCollectionsToCollapse = new HashMap<String, String>();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (isSandbox) {
            deltasEnabled = false;
        }
        subdocCollectionsToCollapse.put("assessmentItem", "assessment");
        subdocCollectionsToCollapse.put("objectiveAssessment", "assessment");
        subdocCollectionsToCollapse.put("studentAssessmentItem", "studentAssessment");
        subdocCollectionsToCollapse.put("studentObjectiveAssessment", "studentAssessment");
    }

    public void journal(Collection<String> ids, String collection, boolean isDelete) {
        if (deltasEnabled && !TenantContext.isSystemCall()) {
            if (subdocCollectionsToCollapse.containsKey(collection)) {
                journalCollapsedSubDocs(ids, collection);
            }
            long now = new Date().getTime();
            Update update = new Update();
            update.set("t", now);
            update.set("c", collection);
            if (isDelete) {
                update.set("d", now);
            } else {
                update.set("u", now);
            }
            for (String id : ids) {
                byte[] idbytes = getByteId(id);
                update.set("i", id);
                template.upsert(Query.query(where("_id").is(idbytes)), update, DELTA_COLLECTION);
            }
        }
    }

    public static byte[] getByteId(String id) {
        try {
            int idLength = id.length();
            if(idLength < 43) {
                LOG.error("Short ID encountered: {}", id);
                return id.getBytes();
            }
            return Hex.decodeHex(id.substring(idLength - 43, idLength - 3).toCharArray());
        } catch (DecoderException e) {
            LOG.error("Decoder exception while decoding {}", id, e);
            return id.getBytes();
        }
    }

    public void journal(String id, String collection, boolean isDelete) {
        journal(Arrays.asList(id), collection, isDelete);
    }

    public void journalCollapsedSubDocs(Collection<String> ids, String collection) {
        LOG.debug("journaling {} to {}", ids, subdocCollectionsToCollapse.get(collection));
        Set<String> newIds = new HashSet<String>();
        for (String id : ids) {
            newIds.add(id.split("_id")[0] + "_id");
        }
        journal(newIds, subdocCollectionsToCollapse.get(collection), false);
    }

    /**
     * Record a purge event in the delta journal
     * @param timeOfPurge
     *          start time of purge
     */
    public void journalPurge(long timeOfPurge) {
        String id = uuidGeneratorStrategy.generateId();

        Update update = new Update();
        update.set("t", timeOfPurge);
        update.set("c", PURGE);

        TenantContext.setIsSystemCall(false);
        template.upsert(Query.query(where("_id").is(id)), update, DELTA_COLLECTION);
    }

    /*
     * a range querying paging iterator on the delta collections.
     */
    public Iterator<Map<String, Object>> findDeltaRecordBetween(final long start, final long end) {

        return new Iterator<Map<String, Object>>() {
            List<Map<String, Object>> deltas = findNextBatchOfDeltas(start, null);
            int currMark = 0;

            @Override
            public boolean hasNext() {
                return currMark < deltas.size();
            }

            @Override
            public Map<String, Object> next() {
                Map<String, Object> nextDelta = deltas.get(currMark++);
                if (currMark >= deltas.size()) {
                    deltas = findNextBatchOfDeltas((Long) nextDelta.get("t"), nextDelta.get("_id"));
                    currMark = 0;
                }
                return nextDelta;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private List<Map<String, Object>> findNextBatchOfDeltas(long lastBatchEndTime, Object lastBatchId) {
                TenantContext.setIsSystemCall(false);
                return find(buildDeltaQuery(lastBatchEndTime, end, lastBatchId, limit));
            }

            @SuppressWarnings("unchecked")
            private List<Map<String, Object>> find(Query query) {
                @SuppressWarnings("rawtypes")
                List res = template.find(query, Map.class, DELTA_COLLECTION);
                return res;
            }

            private Query buildDeltaQuery(long lastDeltaTime, long uptoTime, Object lastBatchId, int limit) {
                Criteria timeElasped;
                if (lastBatchId == null) {
                    // first time, include everything from the lastDeltaTime
                    timeElasped = where("t").gte(lastDeltaTime).lt(uptoTime);
                } else {
                    // get everything that's in next time slot plus everything that are equal to
                    // lastDeltaTime, but after the batch id
                    timeElasped = new Criteria().orOperator(
                            where("t").is(lastDeltaTime).andOperator(where("_id").gt(lastBatchId)),
                            where("t").gt(lastDeltaTime).lt(uptoTime));
                }
                Query q = Query.query(timeElasped).limit(limit);
                q.sort().on("t", Order.ASCENDING).on("_id", Order.ASCENDING);
                return q;
            }
        };
    }

    /**
     * Remove all delta journal entries with a "t" value less than the specified
     * time for the tenant
     *
     * @param cleanUptoTime
     *            epoch
     * @param tenant
     */
    public void removeDeltaJournals(String tenant, long cleanUptoTime) {
        if (tenant != null) {
            TenantContext.setTenantId(tenant);
        }
        TenantContext.setIsSystemCall(false);

        // remove in batches
        Query beforeWithLimit = new Query(where("t").lt(cleanUptoTime)).limit(limit);
        beforeWithLimit.fields().include("_id");

        @SuppressWarnings("rawtypes")
        List<Map> deltas;
        do {
            deltas = template.find(beforeWithLimit, Map.class, DELTA_COLLECTION);
            List<byte[]> idsToRemove = new ArrayList<byte[]>(deltas.size());
            for (Map<String, Object> delta : deltas) {
                idsToRemove.add((byte[]) delta.get("_id"));
            }

            if (!idsToRemove.isEmpty()) {
                Query removeIds = new Query(where("_id").in(idsToRemove));
                template.remove(removeIds, DELTA_COLLECTION);
                LOG.debug("Removing delta records in batch size: {}", idsToRemove.size());
            }
        } while (!deltas.isEmpty());
    }

    protected boolean isDeltasEnabled() {
        return deltasEnabled;
    }

    protected void setDeltasEnabled(boolean deltasEnabled) {
        this.deltasEnabled = deltasEnabled;
    }

    protected int getLimit() {
        return limit;
    }

    protected void setLimit(int limit) {
        this.limit = limit;
    }

}
