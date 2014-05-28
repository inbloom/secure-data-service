package org.slc.sli.dal.repository;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
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

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

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

    /* in paged query, get upto 50000 items each time. */
    @Value("${sli.bulk.extract.delta.iterationSize:50000}")
    private int limit;

    @Autowired
    @Qualifier("journalTemplate")
    private MongoTemplate template;

    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    public static final String DELTA_COLLECTION = "deltas";
    public static final String PURGE = "purge";
    private final Map<String, String> subdocCollectionsToCollapse = new HashMap<String, String>();

    @Override
    public void afterPropertiesSet() throws Exception {
        subdocCollectionsToCollapse.put("assessmentItem", "assessment");
        subdocCollectionsToCollapse.put("objectiveAssessment", "assessment");
        subdocCollectionsToCollapse.put("studentAssessmentItem", "studentAssessment");
        subdocCollectionsToCollapse.put("studentObjectiveAssessment", "studentAssessment");
    }

    public void journal(Collection<String> ids, String collection, boolean isDelete) {
    	
    	// Don't journal certain collections that use the MongoEntity wrapper (id/type/body/metaData)
    	// but which are not inBloom educational entities and thus never part of the Bulk Extract
    	// delta
    	if ( collection.equals("applicationAuthorization") ) {
    		return;
    	}

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
                List<byte[]> idbytes = getByteId(id);
                if(idbytes.size() > 1) {
                    update.set("i", idbytes.subList(1, idbytes.size()));
                }
                template.upsert(Query.query(where("_id").is(idbytes.get(0))), update, DELTA_COLLECTION);
            }
        }
    }

    public static List<byte[]> getByteId(String id) {
        try {
            List<byte[]> result = new ArrayList<byte[]>();
            int idLength = id.length();
            if(idLength < 43) {
                LOG.error("Short ID encountered: {}", id);
                return Arrays.asList(id.getBytes());
            }
            for(String idPart: id.split("_id")){
                if(!idPart.isEmpty()){
                    idPart = idPart.replaceAll("[^\\p{XDigit}]", "");
                    result.add(0, Hex.decodeHex(idPart.toCharArray()));
                }
            }
            return result;
        } catch (DecoderException e) {
            LOG.error("Decoder exception while decoding {}", id, e);
            return Arrays.asList(id.getBytes());
        }
    }

    public static String getEntityId(Map<String, Object> deltaRecord) {
        Object deltaId = deltaRecord.get("_id");
        if(deltaId instanceof String){
            //legacy case, where the delta and entity used the same id
            return (String) deltaId;
        } else if (deltaId instanceof byte[]) {
            StringBuilder id = new StringBuilder("");
            @SuppressWarnings("unchecked")
            List<byte[]> parts = (List<byte[]>) deltaRecord.get("i");
            if (parts != null) {
                for (byte[] part : parts) {
                    id.insert(0, Hex.encodeHexString(part) + "_id");
                }
            }
            id.append(Hex.encodeHexString((byte[]) deltaId) + "_id");
            return id.toString();
        }
        else {
            throw new IllegalArgumentException("Illegal id: " + deltaId);
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
            List<Object> idsToRemove = new ArrayList<Object>(deltas.size());
            for (Map<String, Object> delta : deltas) {
                idsToRemove.add(delta.get("_id"));
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
