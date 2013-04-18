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

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Provides journaling of entity updates for delta processing
 *
 * @author nbrown
 *
 */
@Component
public class DeltaJournal {

    private static final Logger LOG = LoggerFactory.getLogger(DeltaJournal.class);

    @Value("${sli.bulk.extract.deltasEnabled:false}")
    private boolean isEnabled;

    public static final String DELTA_COLLECTION = "deltas";
    
    // in paged query, get upto 1000 items each time
    public static final int DEFAULT_LIMIT = 1000;
    public int limit = DEFAULT_LIMIT;

    @Autowired
    @Qualifier("journalTemplate")
    private MongoTemplate template;

    public void journal(List<String> ids, String collection, boolean isDelete) {
        if (isEnabled) {
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
                template.upsert(Query.query(where("_id").is(id)), update, DELTA_COLLECTION);
            }
        }
    }

    public void journal(String id, String collection, boolean isDelete) {
        journal(Arrays.asList(id), collection, isDelete);
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
                return currMark != -1 && currMark < deltas.size();
            }
            
            @Override
            public Map<String, Object> next() {
                Map<String, Object> nextDelta = deltas.get(currMark++);
                if (currMark >= deltas.size()) {
                    deltas = findNextBatchOfDeltas((Long) nextDelta.get("t"), (String) nextDelta.get("_id"));
                    currMark = 0;
                }
                return nextDelta;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private List<Map<String, Object>> findNextBatchOfDeltas(long lastBatchEndTime, String lastBatchId) {
                TenantContext.setIsSystemCall(false);
                return find(buildDeltaQuery(lastBatchEndTime, end, lastBatchId, limit));
            }
            
            @SuppressWarnings("unchecked")
            private List<Map<String, Object>> find(Query query) {
                @SuppressWarnings("rawtypes")
                List res = template.find(query, Map.class, DELTA_COLLECTION);
                return (List<Map<String, Object>>) res;
            }
            
            private Query buildDeltaQuery(long lastDeltaTime, long uptoTime, String lastBatchId, int limit) {
                Criteria timeElasped;
                if (lastBatchId == null) {
                    // first time, include everything from the lastDeltaTime
                    timeElasped = where("t").gte(lastDeltaTime).lt(uptoTime);
                } else {
                    // get everything that's in next time slot plus everything that are equal to
                    // lastDeltaTime, but after the batch id
                    timeElasped = new Criteria().orOperator(where("t").is(lastDeltaTime).andOperator(where("_id").gt(lastBatchId)),
                            where("t").gt(lastDeltaTime).lt(uptoTime));
                }
                Query q = Query.query(timeElasped).limit(limit);
                q.sort().on("t", Order.ASCENDING).on("_id", Order.ASCENDING);
                return q;
            }
        };
    }
    


}
