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
 * Transformer for StudentAssessmentAssociation entities.
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
    
    /**
     * Default constructor.
     */
    public StudentAssessmentCombiner() {
        collections = new HashMap<String, Map<Object, NeutralRecord>>();
        transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
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
                
                LOG.info("Checking for objective assessment: {}", objectiveAssessmentRef);
                
                Map<String, Object> objectiveAssessment = new ObjectiveAssessmentBuilder(getNeutralRecordMongoAccess(), getJob().getId()).
                        getObjectiveAssessment(objectiveAssessmentRef);
                
                if (objectiveAssessment != null) {
                    LOG.info("Found objective assessment: {}", objectiveAssessmentRef);
                    assessmentAttributes.put("objectiveAssessment", objectiveAssessment);
                } else {
                    LOG.warn("Failed to find objective assessment: {} for student assessment: {}", objectiveAssessmentRef, studentAssessmentAssociationId);
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
}
