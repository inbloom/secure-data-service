package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Transformer for StudentAssessment entities. Current implementation has REMOVED checking for
 * circular
 * references in objective assessments.
 * 
 * @author nbrown
 * @author shalka
 */
@Scope("prototype")
@Component("studentAssessmentAssociationTransformationStrategy")
public class StudentAssessmentCombiner extends AbstractTransformationStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(StudentAssessmentCombiner.class);
    
    private static final String STUDENT_ASSESSMENT_ASSOCIATION = "studentAssessmentAssociation";
    private static final String STUDENT_OBJECTIVE_ASSESSMENT = "studentObjectiveAssessment";
    private static final String STUDENT_ASSESSMENT_REFERENCE = "studentTestAssessmentRef";
    private static final String OBJECTIVE_ASSESSMENT_REFERENCE = "objectiveAssessmentRef";
    
    private Map<String, Map<Object, NeutralRecord>> collections;
    private Map<String, Map<Object, NeutralRecord>> transformedCollections;
    private Map<String, Map<String, Object>> objectiveAssessments;
    
    /**
     * Default constructor.
     */
    public StudentAssessmentCombiner() {
        collections = new HashMap<String, Map<Object, NeutralRecord>>();
        transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
        buildObjectiveAssessmentMap();
    }
    
    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go."
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        persist();
    }
    
    /**
     * Pre-requisite interchanges for student assessment data to be successfully transformed:
     * student, assessment metadata
     */
    public void loadData() {
        LOG.info("Loading data for studentAssessmentAssociation transformation.");
        List<String> collectionsToLoad = Arrays.asList(STUDENT_ASSESSMENT_ASSOCIATION, STUDENT_OBJECTIVE_ASSESSMENT);
        for (String collectionName : collectionsToLoad) {
            Map<Object, NeutralRecord> collection = getCollectionFromDb(collectionName);
            collections.put(collectionName, collection);
            LOG.info("{} is loaded into local storage.  Total Count = {}", collectionName, collection.size());
        }
        LOG.info("Finished loading data for studentAssessmentAssociation transformation.");
    }
    
    /**
     * Transforms student assessments from Ed-Fi model into SLI model.
     */
    public void transform() {
        LOG.info("Transforming student assessment data");
        Map<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get(STUDENT_ASSESSMENT_ASSOCIATION)
                .entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String studentAssessmentAssociationId = (String) attributes.remove("xmlId");
            
            if (studentAssessmentAssociationId != null) {
                List<Map<String, Object>> studentObjectiveAssessments = getStudentObjectiveAssessments(studentAssessmentAssociationId);
                LOG.warn("found {} student objective assessments for student assessment id: {}.",
                        studentObjectiveAssessments.size(), studentAssessmentAssociationId);
                attributes.put("studentObjectiveAssessments", studentObjectiveAssessments);
            } else {
                LOG.warn(
                        "no local id for student assessment association: {}. cannot embed student objective assessment objects.",
                        studentAssessmentAssociationId);
            }
            
            neutralRecord.setAttributes(attributes);
            newCollection.put(neutralRecord.getRecordId(), neutralRecord);
        }
        transformedCollections.put(STUDENT_ASSESSMENT_ASSOCIATION, newCollection);
        LOG.info("Finished transforming student assessment data for {} student assessment associations.", newCollection
                .entrySet().size());
    }
    
    /**
     * Persists the transformed data into mongo.
     */
    public void persist() {
        LOG.info("Persisting transformed data into studentAssessmentAssociation_transformed staging collection.");
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
                getNeutralRecordMongoAccess().getRecordRepository().createForJob(neutralRecord, getJob().getId());
            }
        }
        LOG.info("Finished persisting transformed data into studentAssessmentAssociation_transformed staging collection.");
    }
    
    /**
     * Returns all collection entities found in ingestion staging database.
     * 
     * @param collectionName
     *            collection to be loaded from staging database.
     */
    private Map<Object, NeutralRecord> getCollectionFromDb(String collectionName) {
        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());
        
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQueryForJob(
                collectionName, new Query(jobIdCriteria), getJob().getId(), 0, 0);
        
        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;
        
        Iterator<NeutralRecord> neutralRecordIterator = data.iterator();
        while (neutralRecordIterator.hasNext()) {
            tempNr = neutralRecordIterator.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        return collection;
    }
    
    /**
     * Gets all student objective assessments that reference the student assessment's local (xml)
     * id.
     * 
     * @param studentAssessmentAssociationId
     *            volatile identifier.
     * @return list of student objective assessments (represented by neutral records).
     */
    private List<Map<String, Object>> getStudentObjectiveAssessments(String studentAssessmentAssociationId) {
        List<Map<String, Object>> assessments = new ArrayList<Map<String, Object>>();
        for (Map.Entry<Object, NeutralRecord> studentObjectiveAssessment : collections
                .get(STUDENT_OBJECTIVE_ASSESSMENT).entrySet()) {
            NeutralRecord record = studentObjectiveAssessment.getValue();
            Map<String, Object> assessmentAttributes = record.getAttributes();
            String studentTestAssessmentRef = (String) assessmentAttributes.get(STUDENT_ASSESSMENT_REFERENCE);
            if (studentTestAssessmentRef.equals(studentAssessmentAssociationId)) {
                String objectiveAssessmentRef = (String) assessmentAttributes.remove(OBJECTIVE_ASSESSMENT_REFERENCE);
                Map<String, Object> objectiveAssessment = objectiveAssessments.get(objectiveAssessmentRef);
                if (objectiveAssessment != null) {
                    assessmentAttributes.put("objectiveAssessment", objectiveAssessment);
                }
                Map<String, Object> newAssessmentAttributes = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : assessmentAttributes.entrySet()) {
                    if (!entry.getKey().equals(STUDENT_ASSESSMENT_REFERENCE)) {
                        newAssessmentAttributes.put(entry.getKey(), entry.getValue());
                    }
                }
                assessments.add(newAssessmentAttributes);
            }
        }
        return assessments;
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
