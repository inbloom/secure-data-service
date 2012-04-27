package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * 
 */
public class ObjectiveAssessmentBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectiveAssessmentBuilder.class);
    public static final String SUB_OBJECTIVE_REFS = "subObjectiveRefs";
    public static final String BY_IDENTIFICATION_CDOE = "identificationCode";
    public static final String BY_ID = "id";
    private final NeutralRecordMongoAccess mongoAccess;
    
    public ObjectiveAssessmentBuilder(NeutralRecordMongoAccess mongoAccess) {
        super();
        this.mongoAccess = mongoAccess;
    }
    
    public Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, String by) {
        Set<String> parentObjs = Collections.emptySet();
        return getObjectiveAssessment(objectiveAssessmentRef, parentObjs, by);
    }
    
    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef, Set<String> parentObjs, String by) {
        LOG.debug("Looking up objective assessment {}", objectiveAssessmentRef);
        NeutralRecord objectiveAssessmentRecord = mongoAccess.getRecordRepository().findOne("objectiveAssessment",
                new NeutralQuery(new NeutralCriteria(by, "=", objectiveAssessmentRef)));
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
    
}
