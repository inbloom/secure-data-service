package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * Class for building objective assessments
 *
 * @author nbrown
 * @author shalka
 */
public class ObjectiveAssessmentBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectiveAssessmentBuilder.class);
    
    private static final String OBJECTIVE_ASSESSMENT = "objectiveAssessment";
    
    public static final String SUB_OBJECTIVE_REFS = "subObjectiveRefs";
    public static final String BY_IDENTIFICATION_CDOE = "identificationCode";
    public static final String BY_ID = "id";
    
    private final NeutralRecordMongoAccess mongoAccess;
    private final String jobId;

    /**
     * Default constructor.
     * 
     * @param mongoAccess used for making calls to mongo to assemble objective assessments.
     * @param jobId current batch job id.
     */
    public ObjectiveAssessmentBuilder(NeutralRecordMongoAccess mongoAccess, String jobId) {
        super();
        this.mongoAccess = mongoAccess;
        this.jobId = jobId;
    }

    /**
     * Builds a map containing entries of the form {identificationCode --> objective assessment}.
     * 
     * @return Map for looking up an objective assessment by its identification code.
     */
    public Map<String, Map<String, Object>> buildObjectiveAssessmentMap() {
        if (mongoAccess == null || jobId == null) {
            throw new RuntimeException("could not build objective assessment map due to bad initialization.");
        }
        
        Map<String, Map<String, Object>> objectiveAssessments = new HashMap<String, Map<String, Object>>();
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : getAllObjectiveAssessments().entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String identificationCode = (String) attributes.get("identificationCode");
            
            Map<String, Object> objectiveAssessment = getObjectiveAssessment(identificationCode, BY_ID);
            if (objectiveAssessment != null) {
                objectiveAssessments.put(identificationCode, objectiveAssessment);
            } else {
                LOG.warn("Could not find objective assessment: {}", identificationCode);
            }
        }
        return objectiveAssessments;
    }
    
    /**
     * Begins the recursion process for nesting all sub-objective assessments.
     * @param objectiveAssessmentRef objectiveAssessmentRef current objective assessment.
     * @param by how to search for objective assessments (currently 'BY_ID').
     * @return Map representing the current objective assessment (containing all children as well).
     */
    public Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, String by) {
        Set<String> parentObjs = Collections.emptySet();
        return getObjectiveAssessment(objectiveAssessmentRef, parentObjs, by);
    }

    /**
     * Performs head recursion to nest sub-objective assessments onto the current objective assessment.
     * Also checks for circular references.
     * 
     * @param objectiveAssessmentRef current objective assessment.
     * @param parentObjs ever-growing list of parent references (to prevent circular references).
     * @param by how to search for objective assessments (currently 'BY_ID').
     * @return Map representing the current objective assessment (containing all children as well).
     */
    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, Set<String> parentObjs, String by) {
        LOG.debug("Looking up objective assessment: {}", objectiveAssessmentRef);
        NeutralRecord objectiveAssessmentRecord = mongoAccess.getRecordRepository().findOneForJob(
                "objectiveAssessment", new NeutralQuery(new NeutralCriteria(by, "=", objectiveAssessmentRef)), jobId);
        if (objectiveAssessmentRecord == null) {
            return null;
        }
        Map<String, Object> objectiveAssessment = objectiveAssessmentRecord.getAttributes();

        objectiveAssessment.remove("id");

        List<?> subObjectiveRefs = (List<?>) objectiveAssessment.get(SUB_OBJECTIVE_REFS);
        if (subObjectiveRefs != null && !subObjectiveRefs.isEmpty()) {
            Set<String> newParents = new HashSet<String>(parentObjs);
            newParents.add(objectiveAssessmentRef);
            List<Map<String, Object>> subObjectives = new ArrayList<Map<String, Object>>();
            for (Object subObjectiveRef : subObjectiveRefs) {
                if (!newParents.contains(subObjectiveRef)) {
                    Map<String, Object> subAssessment = getObjectiveAssessment((String) subObjectiveRef, newParents,
                            ObjectiveAssessmentBuilder.BY_ID);
                    if (subAssessment != null) {
                        subObjectives.add(subAssessment);
                    } else {
                        LOG.warn("Could not find objective assessment ref {}", subObjectiveRef);
                    }
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
    
    /**
     * Gets all objective assessments currently stored in mongo, and returns in a Map with entries of the form:
     * {recordId --> NeutralRecord}.
     * 
     * @return Map for finding Neutral Records by their (staging) mongo UUID.
     */
    private Map<Object, NeutralRecord> getAllObjectiveAssessments() {
        Iterable<NeutralRecord> objectiveAssessmentRecords = mongoAccess.getRecordRepository().findAllForJob(
                OBJECTIVE_ASSESSMENT, jobId, new NeutralQuery(0));
        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;
        
        Iterator<NeutralRecord> neutralRecordIterator = objectiveAssessmentRecords.iterator();
        while (neutralRecordIterator.hasNext()) {
            tempNr = neutralRecordIterator.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        return collection;
    }
}
