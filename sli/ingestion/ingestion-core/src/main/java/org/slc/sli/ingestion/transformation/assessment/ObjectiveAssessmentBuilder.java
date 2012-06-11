package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.cache.CacheProvider;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Class for building objective assessments
 * 
 * @author nbrown
 * @author shalka
 */
@Scope("prototype")
@Component
public class ObjectiveAssessmentBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectiveAssessmentBuilder.class);
    
    public static final String SUB_OBJECTIVE_REFS = "subObjectiveRefs";
    public static final String BY_IDENTIFICATION_CDOE = "identificationCode";
    public static final String BY_ID = "id";
    public static final String ASSESSMENT_ITEM_REFS = "assessmentItemRefs";
    public static final String OBJECTIVE_ASSESSMENT = "objectiveAssessment";
    
    @Autowired
    private CacheProvider cacheProvider;
    
    /**
     * Gets the specified objective assessment by first performing a look up on its '_id'
     * field, and if not found, checking the 'identificationCode' field.
     * 
     * @param objectiveAssessmentId
     *            xml id or identification code for the objective assessment.
     * @param objectiveAssessments
     *            set of objective assessments to search for single objective assessment in.
     * @return Map representing the objective assessment.
     */
    public Map<String, Object> getObjectiveAssessment(NeutralRecordMongoAccess access, Job job,
            String objectiveAssessmentId) {
        
        String tenantId = job.getProperty("tenantId");
        String batchJobId = job.getId();
        
        Map<String, Object> cached = getFromCache(OBJECTIVE_ASSESSMENT, tenantId, objectiveAssessmentId);
        if (cached != null) {
            return cached;
        }
        
        LOG.debug("Objective assessment: {} is not cached.. going to data store to find it.", objectiveAssessmentId);
        
        Map<String, Object> assessment = getObjectiveAssessment(access, batchJobId, objectiveAssessmentId, BY_ID);
        if (assessment == null || assessment.isEmpty()) {
            LOG.debug("Couldn't find objective assessment: {} using its id --> Using identification code.",
                    objectiveAssessmentId);
            assessment = getObjectiveAssessment(access, batchJobId, objectiveAssessmentId, BY_IDENTIFICATION_CDOE);
            
            if (assessment == null || assessment.isEmpty()) {
                LOG.warn(
                        "Failed to find objective assessment: {} using both id and identification code --> Returning null.",
                        objectiveAssessmentId);
                assessment = null;
            } else {
                LOG.debug("Found objective assessment: {} using its identification code.", objectiveAssessmentId);
            }
        } else {
            LOG.debug("Found objective assessment: {} using its id.", objectiveAssessmentId);
        }

        if (assessment != null) {
            LOG.debug("Caching objective assessment: {}", objectiveAssessmentId);
            cache(OBJECTIVE_ASSESSMENT, tenantId, objectiveAssessmentId, assessment);
        }
        
        return assessment;
    }
    
    /**
     * Begins the recursion process for nesting all sub-objective assessments.
     * 
     * @param objectiveAssessmentRef
     *            current objective assessment.
     * @param by
     *            how to search for objective assessments (default: 'BY_ID').
     * @param objectiveAssessments
     *            set of objective assessments to search for single objective assessment in.
     * @return Map representing the current objective assessment (containing all children as well).
     */
    public Map<String, Object> getObjectiveAssessment(NeutralRecordMongoAccess access, String batchJobId,
            String objectiveAssessmentRef, String by) {
        Set<String> parentObjs = Collections.emptySet();
        Map<Object, NeutralRecord> objectiveAssessments = loadAllObjectiveAssessments(access, batchJobId);
        return getObjectiveAssessment(objectiveAssessmentRef, parentObjs, by, objectiveAssessments);
    }
    
    /**
     * Performs head recursion to nest sub-objective assessments onto the current objective
     * assessment. Also checks for circular references.
     * 
     * @param objectiveAssessmentRef
     *            current objective assessment.
     * @param parentObjs
     *            ever-growing list of parent references (to prevent circular references).
     * @param by
     *            how to search for objective assessments (currently 'BY_ID').
     * @param objectiveAssessments
     *            set of objective assessments to search for single objective assessment in.
     * @return Map representing the current objective assessment (containing all children as well).
     */
    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, Set<String> parentObjs,
            String by, Map<Object, NeutralRecord> objectiveAssessments) {
        LOG.debug("Looking up objective assessment: {} by: {}", objectiveAssessmentRef, by);
        for (Map.Entry<Object, NeutralRecord> objectiveAssessment : objectiveAssessments.entrySet()) {
            Map<String, Object> record = objectiveAssessment.getValue().getAttributes();
            Map<String, Object> objectiveAssessmentToReturn = new HashMap<String, Object>();
            
            if (record.get(by).equals(objectiveAssessmentRef)) {
                List<?> subObjectiveRefs = (List<?>) record.get(SUB_OBJECTIVE_REFS);
                if (subObjectiveRefs != null && !subObjectiveRefs.isEmpty()) {
                    Set<String> newParents = new HashSet<String>(parentObjs);
                    newParents.add(objectiveAssessmentRef);
                    List<Map<String, Object>> subObjectives = new ArrayList<Map<String, Object>>();
                    for (Object subObjectiveRef : subObjectiveRefs) {
                        if (!newParents.contains(subObjectiveRef)) {
                            Map<String, Object> subAssessment = getObjectiveAssessment((String) subObjectiveRef,
                                    newParents, BY_ID, objectiveAssessments);
                            if (subAssessment != null) {
                                subObjectives.add(subAssessment);
                            } else {
                                LOG.warn("Could not find objective assessment ref: {}", subObjectiveRef);
                            }
                        } else {
                            LOG.warn("Ignoring sub objective assessment {} since it is already in the hierarchy",
                                    subObjectiveRef);
                        }
                    }
                    objectiveAssessmentToReturn.put("objectiveAssessments", subObjectives);
                    LOG.info("Found {} sub-objective assessments for objective assessment: {}", subObjectives.size(),
                            objectiveAssessmentRef);
                } else {
                    LOG.debug("Objective assessment: {} has no sub-objectives (field is absent).",
                            objectiveAssessmentRef);
                }
                
                for (Map.Entry<String, Object> entry : record.entrySet()) {
                    if (!(entry.getKey().equals(SUB_OBJECTIVE_REFS) || entry.getKey().equals(ASSESSMENT_ITEM_REFS) || entry
                            .getKey().equals("id"))) {
                        objectiveAssessmentToReturn.put(entry.getKey(), entry.getValue());
                    }
                }
                return objectiveAssessmentToReturn;
            }
        }
        return null;
    }
    
    /**
     * Returns collection entities found in staging ingestion database. If a work note was not
     * provided for
     * the job, then all entities in the collection will be returned.
     * 
     * @param collectionName
     *            name of collection to be queried for.
     */
    public Map<Object, NeutralRecord> loadAllObjectiveAssessments(NeutralRecordMongoAccess access, String batchJobId) {
        Map<Object, NeutralRecord> all = new HashMap<Object, NeutralRecord>();
        Iterable<NeutralRecord> data = access.getRecordRepository().findAllForJob(OBJECTIVE_ASSESSMENT, batchJobId,
                new NeutralQuery(0));
        Iterator<NeutralRecord> itr = data.iterator();
        NeutralRecord record = null;
        while (itr.hasNext()) {
            record = itr.next();
            all.put(record.getRecordId(), record);
        }
        return all;
    }
    
    private Map<String, Object> getFromCache(String collection, String tenantId, String criteria) {
        String key = composeKey(collection, tenantId, criteria);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> found = (Map<String, Object>) cacheProvider.get(key);
        
        return found;
    }
    
    private void cache(String collection, String tenantId, String criteria, Map<String, Object> value) {
        String key = composeKey(collection, tenantId, criteria);
        cacheProvider.add(key, value);
    }
    
    private String composeKey(String collection, String tenantId, String criteria) {
        return String.format("%s_%s_%s", tenantId, collection, criteria);
    }
}
