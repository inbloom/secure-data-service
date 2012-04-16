package org.slc.sli.ingestion.transformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Transformer for Assessment Entities
 * 
 * @author ifaybyshev
 * 
 */
public class AssessmentCombiner extends AbstractTransformationStrategy {
    
    public static final String SUB_OBJECTIVE_REFS = "subObjectiveRefs";
    
    private static final Logger LOG = LoggerFactory.getLogger(AssessmentCombiner.class);
    
    private Map<String, Map<Object, NeutralRecord>> collections;
    
    private List<NeutralRecord> transformedAssessments = new ArrayList<NeutralRecord>();
    
    private final FileUtils fileUtils;
    
    @Autowired
    public AssessmentCombiner(FileUtils fileUtils) {
        this.collections = new HashMap<String, Map<Object, NeutralRecord>>();
        this.fileUtils = fileUtils;
    }
    
    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     * 
     */
    @Override
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
        
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("assessment").entrySet()) {
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
            List<Map<String, Object>> objectiveAssessments = new ArrayList<Map<String, Object>>();
            if (objectiveAssessmentRefs != null && !(objectiveAssessmentRefs.isEmpty())) {
                
                for (String objectiveAssessmentRef : objectiveAssessmentRefs) {
                    
                    objectiveAssessments.add(getObjectiveAssessment(objectiveAssessmentRef));
                }
                attrs.put("objectiveAssessment", objectiveAssessments);
            }
            
            String assessmentPeriodDescriptorRef = (String) attrs.get("periodDescriptorRef");
            attrs.remove("periodDescriptorRef");
            if (assessmentPeriodDescriptorRef != null) {
                
                attrs.put("assessmentPeriodDescriptor", getAssessmentPeriodDescriptor(assessmentPeriodDescriptorRef));
                
            }
            neutralRecord.setAttributes(attrs);
            transformedAssessments.add(neutralRecord);
            neutralRecord.setLocalId(attrs.get("assessmentTitle"));
        }
        
    }
    
    private Map<String, Object> getAssessmentPeriodDescriptor(String assessmentPeriodDescriptorRef) {
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.codeValue", assessmentPeriodDescriptorRef);
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPaths(
                "assessmentPeriodDescriptor", paths);
        
        if (data.iterator().hasNext()) {
            return data.iterator().next().getAttributes();
        }
        
        return null;
        
    }
    
    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef) {
        Set<String> parentObjs = Collections.emptySet();
        return getObjectiveAssessment(objectiveAssessmentRef, parentObjs);
    }
    
    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, Set<String> parentObjs) {
        
        Map<String, Object> objectiveAssessment = getNeutralRecordMongoAccess()
                .getRecordRepository()
                .findOne("objectiveAssessment",
                        new NeutralQuery(new NeutralCriteria("id", "=", objectiveAssessmentRef))).getAttributes();
        
        objectiveAssessment.remove("id");
        
        List<?> subObjectiveRefs = (List<?>) objectiveAssessment.get(SUB_OBJECTIVE_REFS);
        if (subObjectiveRefs != null && !subObjectiveRefs.isEmpty()) {
            Set<String> newParents = new HashSet<String>(parentObjs);
            newParents.add(objectiveAssessmentRef);
            List<Map<String, Object>> subObjectives = new ArrayList<Map<String, Object>>();
            for (Object subObjectiveRef : subObjectiveRefs) {
                if (!newParents.contains(subObjectiveRef)) {
                    Map<String, Object> subAssessment = getObjectiveAssessment((String) subObjectiveRef, newParents);
                    subObjectives.add(subAssessment);
                } else {
                    // sorry Mr. Hofstadter, no infinitely recursive assessments allowed due to
                    // finite memory limitations
                    LOG.warn("Ignoring sub objective assessment {} since it is already in the hierarchy",
                            subObjectiveRef);
                }
            }
            objectiveAssessment.put("objectiveAssessments", subObjectives);
        }
        objectiveAssessment.remove(SUB_OBJECTIVE_REFS);
        
        return objectiveAssessment;
        
    }
    
    @SuppressWarnings("unchecked")
    private String getAssocationFamilyMap(String key, HashMap<String, Map<String, Object>> deepFamilyMap,
            String familyHierarchyName) {
        
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.AssessmentFamilyIdentificationCode.ID", key);
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPaths(
                "assessmentFamily", paths);
        
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
        try {
            cleanMetaDataFiles();
            IngestionFileEntry fileEntry = getMetaDataFiles().get(0);
            NeutralRecordFileWriter writer = new NeutralRecordFileWriter(fileEntry.getNeutralRecordFile());
            
            // transformedCollections should have been populated in the transform() step.
            for (NeutralRecord neutralRecord : transformedAssessments) {
                writer.writeRecord(neutralRecord);
            }
            writer.close();
            
        } catch (IOException e) {
            LOG.error("Exception occurred", e);
        }
    }
    
    private void cleanMetaDataFiles() throws IOException {
        for (IngestionFileEntry fe : getMetaDataFiles()) {
            File neutralRecordFile = fe.getNeutralRecordFile();
            if (neutralRecordFile != null) {
                neutralRecordFile.delete();
            }
            fe.setNeutralRecordFile(fileUtils.createTempFile());
        }
    }
    
    /**
     * Will return the assessment metadata files we are processing.
     * Since we are transforming assessments, one should exist.
     * I know this is hacky, replace when we get a better way to bypass additional ingestion work
     * that breaks this.
     * 
     * @return
     */
    private List<IngestionFileEntry> getMetaDataFiles() {
        List<IngestionFileEntry> allFiles = getJob().getFiles();
        List<IngestionFileEntry> metaDataFiles = new ArrayList<IngestionFileEntry>();
        for (IngestionFileEntry fe : allFiles) {
            if (fe.getFileType().equals(FileType.XML_ASSESSMENT_METADATA)) {
                metaDataFiles.add(fe);
            }
        }
        return metaDataFiles;
    }
    
    /**
     * Stores all items in collection found in database to local storage (HashMap)
     * 
     * @param collectionName
     */
    private void loadCollectionFromDb(String collectionName) {
        
        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQuery(collectionName,
                new Query(jobIdCriteria), 0, 0);
        
        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;
        
        Iterator<NeutralRecord> iter = data.iterator();
        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        
        collections.put(collectionName, collection);
    }
    
    protected List<NeutralRecord> getTransformedAssessments() {
        return transformedAssessments;
    }
    
}
