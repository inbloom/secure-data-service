package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for building objective assessments
 * 
 * @author nbrown
 * @author shalka
 */
public class ObjectiveAssessmentBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectiveAssessmentBuilder.class);
    
    public static final String SUB_OBJECTIVE_REFS = "subObjectiveRefs";
    public static final String BY_IDENTIFICATION_CDOE = "identificationCode";
    public static final String BY_ID = "id";
    public static final String ASSESSMENT_ITEM_REFS = "assessmentItemRefs";
    
    private final NeutralRecordMongoAccess mongoAccess;
    private final String jobId;
    
    /**
     * Default constructor.
     * 
     * @param mongoAccess
     *            used for making calls to mongo to assemble objective assessments.
     * @param jobId
     *            current batch job id.
     */
    public ObjectiveAssessmentBuilder(NeutralRecordMongoAccess mongoAccess, String jobId) {
        super();
        this.mongoAccess = mongoAccess;
        this.jobId = jobId;
    }
    
    /**
     * Gets the specified objective assessment by first performing a look up on its '_id'
     * field, and if not found, checking the 'identificationCode' field.
     * 
     * @param objectiveAssessmentId
     *            xml id or identification code for the objective assessment.
     * @return Map representing the objective assessment.
     */
    public Map<String, Object> getObjectiveAssessment(String objectiveAssessmentId) {
        Map<String, Object> assessment = getObjectiveAssessment(objectiveAssessmentId, BY_ID);
        
        if (assessment == null || assessment.isEmpty()) {
            LOG.info("Couldn't find objective assessment: {} using its id --> Using identification code.",
                    objectiveAssessmentId);
            assessment = getObjectiveAssessment(objectiveAssessmentId, BY_IDENTIFICATION_CDOE);
            
            if (assessment == null || assessment.isEmpty()) {
                LOG.warn(
                        "Failed to find objective assessment: {} using both id and identification code --> Returning null.",
                        objectiveAssessmentId);
                assessment = null;
            } else {
                LOG.info("Found objective assessment: {} using its identification code.", objectiveAssessmentId);
            }
        } else {
            LOG.info("Found objective assessment: {} using its id.", objectiveAssessmentId);
        }
        return assessment;
    }
    
    /**
     * Begins the recursion process for nesting all sub-objective assessments.
     * 
     * @param objectiveAssessmentRef
     *            objectiveAssessmentRef current objective assessment.
     * @param by
     *            how to search for objective assessments (currently 'BY_ID').
     * @return Map representing the current objective assessment (containing all children as well).
     */
    public Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, String by) {
        Set<String> parentObjs = Collections.emptySet();
        return getObjectiveAssessment(objectiveAssessmentRef, parentObjs, by);
    }
    
    /**
     * Performs head recursion to nest sub-objective assessments onto the current objective
     * assessment.
     * Also checks for circular references.
     * 
     * @param objectiveAssessmentRef
     *            current objective assessment.
     * @param parentObjs
     *            ever-growing list of parent references (to prevent circular references).
     * @param by
     *            how to search for objective assessments (currently 'BY_ID').
     * @return Map representing the current objective assessment (containing all children as well).
     */
    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, Set<String> parentObjs, String by) {
        LOG.debug("Looking up objective assessment: {} by: {}", objectiveAssessmentRef, by);
        NeutralRecord objectiveAssessmentRecord = mongoAccess.getRecordRepository().findOneForJob(
                EntityNames.OBJECTIVE_ASSESSMENT,
                new NeutralQuery(new NeutralCriteria(by, "=", objectiveAssessmentRef)), jobId);
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
                            BY_ID);
                    if (subAssessment != null) {
                        subObjectives.add(subAssessment);
                    } else {
                        LOG.warn("Could not find objective assessment ref: {}", subObjectiveRef);
                    }
                } else {
                    // sorry Mr. Hofstadter, no infinitely recursive assessments allowed due to
                    // finite memory limitations
                    LOG.warn("Ignoring sub objective assessment {} since it is already in the hierarchy",
                            subObjectiveRef);
                }
            }
            objectiveAssessment.put("objectiveAssessments", subObjectives);
            LOG.info("Found {} sub-objective assessments for objective assessment: {}", subObjectives.size(),
                    objectiveAssessmentRef);
        } else {
            LOG.debug("Objective assessment: {} has no sub-objectives (field is absent).", objectiveAssessmentRef);
        }
        objectiveAssessment.remove(SUB_OBJECTIVE_REFS);
        objectiveAssessment.remove(ASSESSMENT_ITEM_REFS);
        return objectiveAssessment;
    }
}
