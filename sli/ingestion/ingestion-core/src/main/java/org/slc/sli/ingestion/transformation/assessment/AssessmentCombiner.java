package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Transformer for Assessment Entities
 * 
 * @author ifaybyshev
 * @author shalka
 */
@Scope("prototype")
@Component("assessmentTransformationStrategy")
public class AssessmentCombiner extends AbstractTransformationStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(AssessmentCombiner.class);
    
    private static final String ASSESSMENT = "assessment";
    private static final String ASSESSMENT_FAMILY = "assessmentFamily";
    private static final String ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    
    private Map<String, Map<Object, NeutralRecord>> collections;
    private Map<String, Map<Object, NeutralRecord>> transformedCollections;
    private Map<String, Map<String, Object>> objectiveAssessments;
    
    public AssessmentCombiner() {
        collections = new HashMap<String, Map<Object, NeutralRecord>>();
        transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
    }
    
    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     * 
     */
    @Override
    public void performTransformation() {
        loadData();
        buildObjectiveAssessmentMap();
        transform();
        persist();
    }
    
    /**
     * Pre-requisite interchanges for assessment data to be successfully transformed:
     * none (as of 5/8/2012)
     */
    public void loadData() {
        LOG.info("Loading data for assessment transformation.");
        List<String> collectionsToLoad = Arrays.asList(ASSESSMENT, ASSESSMENT_FAMILY);
        for (String collectionName : collectionsToLoad) {
            Map<Object, NeutralRecord> collection = getCollectionFromDb(collectionName);
            collections.put(collectionName, collection);
            LOG.info("{} is loaded into local storage.  Total Count = {}", collectionName, collection.size());
        }
        LOG.info("Finished loading data for assessment transformation.");
    }
    
    /**
     * Transforms assessments from Ed-Fi model into SLI model.
     */
    public void transform() {
        LOG.info("Transforming assessment data");
        
        Map<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get(ASSESSMENT).entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            
            // get the key of parent
            Map<String, Object> attrs = neutralRecord.getAttributes();
            String parentFamilyId = (String) attrs.get("parentAssessmentFamilyId");
            attrs.remove("parentAssessmentFamilyId");
            String familyHierarchyName = "";
            familyHierarchyName = getAssocationFamilyMap(parentFamilyId, new HashMap<String, Map<String, Object>>(),
                    familyHierarchyName);
            
            attrs.put("assessmentFamilyHierarchyName", familyHierarchyName);
            
            @SuppressWarnings("unchecked")
            List<String> objectiveAssessmentRefs = (List<String>) attrs.get("objectiveAssessmentRefs");
            attrs.remove("objectiveAssessmentRefs");
            List<Map<String, Object>> familyObjectiveAssessments = new ArrayList<Map<String, Object>>();
            if (objectiveAssessmentRefs != null && !(objectiveAssessmentRefs.isEmpty())) {
                for (String objectiveAssessmentRef : objectiveAssessmentRefs) {
                    if (objectiveAssessments.containsKey(objectiveAssessmentRef)) {
                        familyObjectiveAssessments.add(objectiveAssessments.get(objectiveAssessmentRef));
                    } else {
                        LOG.warn("Failed to match objective assessment: {} for family: {}", objectiveAssessmentRef,
                                familyHierarchyName);
                    }
                }
                attrs.put("objectiveAssessment", familyObjectiveAssessments);
            }
            
            String assessmentPeriodDescriptorRef = (String) attrs.get("periodDescriptorRef");
            attrs.remove("periodDescriptorRef");
            if (assessmentPeriodDescriptorRef != null) {
                attrs.put(ASSESSMENT_PERIOD_DESCRIPTOR, getAssessmentPeriodDescriptor(assessmentPeriodDescriptorRef));
            }
            neutralRecord.setAttributes(attrs);
            newCollection.put(neutralRecord.getLocalId(), neutralRecord);
        }
        transformedCollections.put(ASSESSMENT, newCollection);
    }
    
    private Map<String, Object> getAssessmentPeriodDescriptor(String assessmentPeriodDescriptorRef) {
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.codeValue", assessmentPeriodDescriptorRef);
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                ASSESSMENT_PERIOD_DESCRIPTOR, paths, getJob().getId());
        
        if (data.iterator().hasNext()) {
            return data.iterator().next().getAttributes();
        }
        
        return null;
        
    }
    
    @SuppressWarnings("unchecked")
    private String getAssocationFamilyMap(String key, HashMap<String, Map<String, Object>> deepFamilyMap,
            String familyHierarchyName) {
        
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.AssessmentFamilyIdentificationCode.ID", key);
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                "assessmentFamily", paths, getJob().getId());
        
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
    
    /**
     * Persists the transformed data into mongo.
     */
    public void persist() {
        LOG.info("Persisting transformed data into assessment_transformed staging collection.");
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
                getNeutralRecordMongoAccess().getRecordRepository().createForJob(neutralRecord, getJob().getId());
            }
        }
        LOG.info("Finished persisting transformed data into assessment_transformed staging collection.");
    }
    
    /**
     * Builds a map of objective assessments, preserving their nesting structure.
     */
    private void buildObjectiveAssessmentMap() {
        ObjectiveAssessmentBuilder builder = new ObjectiveAssessmentBuilder(getNeutralRecordMongoAccess(),
                getBatchJobId());
        objectiveAssessments = builder.buildObjectiveAssessmentMap();
    }
}
