package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * Transformer for Assessment Entities
 *
 * @author ifaybyshev
 *
 */
public class AssessmentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentCombiner.class);

    private final Map<String, Map<Object, NeutralRecord>> collections;

    private final Map<String, Map<Object, NeutralRecord>> transformedCollections;

    // TODO can we make this a service instead of passing it through every layer?
    private final NeutralRecordMongoAccess neutralRecordMongoAccess;

    public AssessmentCombiner(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
        this.collections = new HashMap<String, Map<Object, NeutralRecord>>();
        this.transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
    }

    @Override
    public void loadData() {
        LOG.info("Loading data for transformation.");

        loadCollectionFromDb("assessment");
        LOG.info("Assessment is loaded into local storage.  Total Count = " + collections.get("assessment").size());

        loadCollectionFromDb("assessmentFamily");
        LOG.info("AssessmentFamily is loaded into local storage.  Total Count = "
                + collections.get("assessmentFamily").size());
    }

    @Override
    public void transform() {
        LOG.debug("Transforming data: Injecting assessmentFamilies into assessment");

        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        String key;

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("assessment").entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();

            // get the key of parent
            Map<String, Object> attrs = neutralRecord.getAttributes();
            key = (String) attrs.get("parentAssessmentFamilyId");

            ArrayList<Map<String, Object>> associationFamilyMap = getAssocationFamilyMap(key, new ArrayList<Map<String, Object>>());

            attrs.put("assessmentFamily", associationFamilyMap);

            neutralRecord.setAttributes(attrs);
            newCollection.put(neutralRecord.getLocalId(), neutralRecord);
        }

        transformedCollections.put("assessment", newCollection);
    }

    
    private ArrayList<Map<String, Object>> getAssocationFamilyMap(String key, ArrayList<Map<String, Object>> deepFamilyMap) {
        
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.AssessmentFamilyIdentificationCode.ID", key);

        Iterable<NeutralRecord> data = neutralRecordMongoAccess.getRecordRepository().findByPaths("assessmentFamily", paths);
        Iterator<NeutralRecord> iter = data.iterator();

        NeutralRecord tempNr;
        Map<String, Object> associationAttrs;

        while (iter.hasNext()) {
            tempNr = iter.next();
            associationAttrs = tempNr.getAttributes();
            deepFamilyMap.add(associationAttrs);
            
            //check if there are parent nodes
            if (associationAttrs.containsKey("parentAssessmentFamilyId")) {
                deepFamilyMap = getAssocationFamilyMap((String) associationAttrs.get("parentAssessmentFamilyId"), deepFamilyMap);
            }
            
        }
        
        return deepFamilyMap;
    }
    
    
    @Override
    public String persist() {
        LOG.info("Persisting transformed data to storage.");

        // transformedCollections should have been populated in the transform() step.
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {

            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {

                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");

                neutralRecordMongoAccess.getRecordRepository().create(neutralRecord);
            }
        }

        return "OK";
    }

    /**
     * Stores all items in collection found in database to local storage (HashMap)
     *
     * @param collectionName
     */
    private void loadCollectionFromDb(String collectionName) {

        Criteria jobIdCriteria = Criteria.where("batchJobId").is(batchJobId);

        Iterable<NeutralRecord> data = neutralRecordMongoAccess.getRecordRepository().findByQuery(collectionName,
                new Query(jobIdCriteria), 0, 0);

        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;

        Iterator<NeutralRecord> iter = data.iterator();
        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getLocalId(), tempNr);
        }

        collections.put(collectionName, collection);
    }

}
