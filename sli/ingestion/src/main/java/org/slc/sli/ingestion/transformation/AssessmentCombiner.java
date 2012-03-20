package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Transformer for Assessment Entities
 * 
 * @author ifaybyshev
 * 
 */
public class AssessmentCombiner extends AbstractTransformationStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(AssessmentCombiner.class);
    
    private Map<String, Map<Object, NeutralRecord>> collections;
    
    private Map<String, Map<Object, NeutralRecord>> transformedCollections;
    
    public AssessmentCombiner() {
        this.collections = new HashMap<String, Map<Object, NeutralRecord>>();
        this.transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
    }
    
    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     * 
     */
    public void performTransformation() {
        loadData();
        transform();
        persist();
    }
    
    public void loadData() {
        LOG.info("Loading data for transformation.");
        
        loadCollectionFromDb("assessment");
        LOG.info("Assessment is loaded into local storage.  Total Count = " + collections.get("assessment").size());
        
        loadCollectionFromDb("assessmentFamily");
        LOG.info("AssessmentFamily is loaded into local storage.  Total Count = "
                + collections.get("assessmentFamily").size());
    }
    
    public void transform() {
        LOG.debug("Transforming data: Injecting assessmentFamilies into assessment");
        
        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        String key;
        
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("assessment").entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            
            // get the key of parent
            Map<String, Object> attrs = neutralRecord.getAttributes();
            key = (String) attrs.get("parentAssessmentFamilyId");
            String familyHierarchyName = "";
            familyHierarchyName = getAssocationFamilyMap(key, new HashMap<String, Map<String, Object>>(),
                    familyHierarchyName);
            
            attrs.put("assessmentFamilyHierarchyName", familyHierarchyName);
            
            @SuppressWarnings("unchecked")
            List<String> objectiveAssessmentRefs = (List<String>) attrs.get("objectiveAssessmentRefs");
            List<Map<String, Object>> objectiveAssessments = new ArrayList<Map<String, Object>>();
            if (objectiveAssessmentRefs != null && !(objectiveAssessmentRefs.isEmpty())) {
                
                for (String objectiveAssessmentRef : objectiveAssessmentRefs) {
                    
                    objectiveAssessments.add(getObjectiveAssessment(objectiveAssessmentRef));
                    attrs.put("objectiveAssessment", objectiveAssessments);
                }
            }
            
            String assessmentPeriodDescriptorRef = (String) attrs.get("periodDescriptorRef");
            if (assessmentPeriodDescriptorRef != null) {
                
                attrs.put("assessmentPeriodDescriptor", getAssessmentPeriodDescriptor(assessmentPeriodDescriptorRef));
                
            }
            neutralRecord.setAttributes(attrs);
            newCollection.put(neutralRecord.getRecordId(), neutralRecord);
        }
        
        transformedCollections.put("assessment", newCollection);
    }
    
    private Map<String, Object> getAssessmentPeriodDescriptor(String assessmentPeriodDescriptorRef) {
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.codeValue", assessmentPeriodDescriptorRef);
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findAllByPaths(
                "assessmentPeriodDescriptor", paths, new NeutralQuery());
        
        if (data.iterator().hasNext()) {
            return data.iterator().next().getAttributes();
        }
        
        return null;
        
    }
    
    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef) {
        Map<String, String> paths = new HashMap<String, String>();
        
        paths.put("body.id", objectiveAssessmentRef);
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findAllByPaths(
                "objectiveAssessment", paths, new NeutralQuery());
        
        Map<String, Object> objectiveAssessment = data.iterator().next().getAttributes();
        objectiveAssessment.remove("id");
        
        return objectiveAssessment;
        
        // return null;
    }
    
    @SuppressWarnings("unchecked")
    private String getAssocationFamilyMap(String key, HashMap<String, Map<String, Object>> deepFamilyMap,
            String familyHierarchyName) {
        
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.AssessmentFamilyIdentificationCode.ID", key);
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findAllByPaths(
                "assessmentFamily", paths, new NeutralQuery());
        
        Map<String, Object> associationAttrs;
        
        ArrayList<Map<String, Object>> tempIdentificationCodes;
        Map<String, Object> tempMap;
        
        for (NeutralRecord tempNr : data) {
            associationAttrs = tempNr.getAttributes();
            
            if (associationAttrs.get("AssessmentFamilyIdentificationCode") instanceof ArrayList<?>) {
                tempIdentificationCodes = (ArrayList<Map<String, Object>>) associationAttrs
                        .get("AssessmentFamilyIdentificationCode");
                
                tempMap = tempIdentificationCodes.get(0);
                if (familyHierarchyName.equals("")) {
                    
                    familyHierarchyName = (String) associationAttrs.get("AssessmentFamilyTitle");
                    
                } else {
                    
                    familyHierarchyName = associationAttrs.get("AssessmentFamilyTitle") + "." + familyHierarchyName;
                    
                }
                deepFamilyMap.put((String) tempMap.get("ID"), associationAttrs);
            }
            
            // check if there are parent nodes
            if (associationAttrs.containsKey("parentAssessmentFamilyId")
                    && !deepFamilyMap.containsKey(associationAttrs.get("parentAssessmentFamilyId"))) {
                familyHierarchyName = getAssocationFamilyMap((String) associationAttrs.get("parentAssessmentFamilyId"),
                        deepFamilyMap, familyHierarchyName);
            }
            
        }
        
        return familyHierarchyName;
    }
    
    public void persist() {
        LOG.info("Persisting transformed data to storage.");
        
        // transformedCollections should have been populated in the transform() step.
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
            
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
                
                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
                
                getNeutralRecordMongoAccess().getRecordRepository().create(neutralRecord);
            }
        }
    }
    
    /**
     * Stores all items in collection found in database to local storage (HashMap)
     * 
     * @param collectionName
     */
    private void loadCollectionFromDb(String collectionName) {
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria(BATCH_JOB_ID_KEY, "=", getBatchJobId()));
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findAll(collectionName, neutralQuery);
        
        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;
        
        Iterator<NeutralRecord> iter = data.iterator();
        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        
        collections.put(collectionName, collection);
    }
    
}
