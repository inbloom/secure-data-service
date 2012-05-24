package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Modifies the LearningObjective to match the SLI datamodel.
 *
 * Transform:
 * Instead of a LO pointing to its children, it points to its parent.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@Scope("prototype")
@Component("learningObjectiveTransformationStrategy")
public class LearningObjectiveTransform extends AbstractTransformationStrategy {

    public static final String ID_CODE = "identificationCode";
    public static final String CONTENT_STANDARD_NAME = "contentStandardName";
    public static final String LO_ID_CODE_PATH = "learningObjectiveId." + ID_CODE;
    public static final String LO_CONTENT_STANDARD_NAME_PATH = "learningObjectiveId." + CONTENT_STANDARD_NAME;
    public static final String LS_ID_CODE_PATH = "learningStandardId." + ID_CODE;
    public static final String LS_CONTENT_STANDARD_NAME_PATH = "learningStandardId." + CONTENT_STANDARD_NAME;
    public static final String LEARNING_OBJ_REFS = "learningObjectiveRefs";
    public static final String LEARNING_STD_REFS = "learningStandardRefs";
    public static final String LOCAL_ID_OBJECTIVE_ID = "parentObjectiveId";
    public static final String LOCAL_ID_CONTENT_STANDARD = "parentContentStandardName";
    public static final String LOCAL_ID_OBJECTIVE = "parentObjective";
    public static final String LOCAL_ID_LEARNING_STANDARDS = "childLearningStandards";

    private static final Logger LOG = LoggerFactory.getLogger(LearningObjectiveTransform.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void performTransformation() {
        
        Map<LearningObjectiveId, NeutralRecord> learningObjectiveIdMap = new HashMap<LearningObjectiveId, NeutralRecord>();

        List<NeutralRecord> allLearningObjectives = new ArrayList<NeutralRecord>();
        
        LOG.info("Loading data for learning objective transformation.");
        Map<Object, NeutralRecord> learningObjectives = getCollectionFromDb(EntityNames.LEARNINGOBJECTIVE);
        LOG.info("{} is loaded into local storage.  Total Count = {}", EntityNames.LEARNINGOBJECTIVE, learningObjectives.size());
        
        for (Map.Entry<Object, NeutralRecord> entry : learningObjectives.entrySet()) {
            NeutralRecord lo = entry.getValue();
            Map<String, Object> attributes = lo.getAttributes();
            String objectiveId = getByPath(LO_ID_CODE_PATH, attributes);
            String contentStandard = getByPath(LO_CONTENT_STANDARD_NAME_PATH, attributes);
            if (objectiveId != null) {
                if (learningObjectiveIdMap.containsKey(new LearningObjectiveId(objectiveId, contentStandard))) {
                    super.getErrorReport(lo.getSourceFile()).error(
                            "Two or more LearningObjectives have duplicate IdentificationCode, ContentStandardName combination. IdentificationCode: "
                                    + objectiveId + ", ContentStandardName" + contentStandard, this);
                    continue;
                }
                learningObjectiveIdMap.put(new LearningObjectiveId(objectiveId, contentStandard), lo);
            }
            allLearningObjectives.add(lo);
        }

        for (NeutralRecord parentLO : allLearningObjectives) {

            String parentObjectiveId = getByPath(LO_ID_CODE_PATH, parentLO.getAttributes());
            String parentContentStandard = getByPath(LO_CONTENT_STANDARD_NAME_PATH, parentLO.getAttributes());
            String parentObjective = getByPath("objective", parentLO.getAttributes());
            List<Map<String, Object>> childLearningObjRefs = (List<Map<String, Object>>) parentLO.getAttributes().get(
                    LEARNING_OBJ_REFS);

            for (Map<String, Object> loRef : childLearningObjRefs) {
                String objectiveId = getByPath(LO_ID_CODE_PATH, loRef);
                String contentStandard = getByPath(LO_CONTENT_STANDARD_NAME_PATH, loRef);
                LearningObjectiveId loId = new LearningObjectiveId(objectiveId, contentStandard);
                NeutralRecord childLo = learningObjectiveIdMap.get(loId);
                if (childLo != null) {
                    if (childLo.getLocalParentIds() == null) {
                        childLo.setLocalParentIds(new HashMap<String, Object>());
                    }
                    if (!childLo.getLocalParentIds().containsKey(LOCAL_ID_OBJECTIVE_ID)) {
                        childLo.getLocalParentIds().put(LOCAL_ID_OBJECTIVE_ID, parentObjectiveId);
                        childLo.getLocalParentIds().put(LOCAL_ID_CONTENT_STANDARD, parentContentStandard);
                        childLo.getLocalParentIds().put(LOCAL_ID_OBJECTIVE, parentObjective);
                    } else {
                        super.getErrorReport(childLo.getSourceFile())
                                .error("LearningObjective cannot have multiple parents. IdentificationCode: "
                                        + loId.objectiveId, this);
                    }
                } else {
                    super.getErrorReport(parentLO.getSourceFile()).error(
                            "Could not resolve LearningObjectiveReference with IdentificationCode " + loId
                                    + ", ContentStandardName " + contentStandard, this);
                }
            }

            List<Map<String, Object>> childLearningStdRefs = (List<Map<String, Object>>) parentLO.getAttributes().get(
                    LEARNING_STD_REFS);
            List<Map<String, Object>> lsRefList = new ArrayList<Map<String, Object>>();
            parentLO.getLocalParentIds().put(LOCAL_ID_LEARNING_STANDARDS, lsRefList);

            ArrayList<String> uuidRefs = new ArrayList<String>();
            if (childLearningStdRefs != null) {
                for (Map<String, Object> learnStdRef : childLearningStdRefs) {
                    String idCode = getByPath(LS_ID_CODE_PATH, learnStdRef);
                    String csn = getByPath(LS_CONTENT_STANDARD_NAME_PATH, learnStdRef);
                    if (idCode != null) {
                        Map<String, Object> ref = new HashMap<String, Object>();
                        ref.put(ID_CODE, idCode);
                        ref.put(CONTENT_STANDARD_NAME, csn);
                        lsRefList.add(ref);
                        uuidRefs.add(null); // this is slightly hacky, but the IdNormalizer will
                                            // throw ArrayIndexOutOfBounds unless we pre-populate
                                            // the list the UUIDs will be added to.
                    }
                }
            }

            parentLO.getAttributes().remove(LEARNING_OBJ_REFS);
            parentLO.getAttributes().remove(LEARNING_STD_REFS);
            parentLO.getAttributes().put("learningStandards", uuidRefs);
        }

        for (NeutralRecord nr : allLearningObjectives) {
            nr.setRecordType(nr.getRecordType() + "_transformed");
            getNeutralRecordMongoAccess().getRecordRepository().createForJob(nr, getBatchJobId());
        }
    }

    @SuppressWarnings("unchecked")
    private static String getByPath(String name, Map<String, Object> map) {
        // how many times have I written this code? Not enough, I say!
        String[] path = name.split("\\.");
        for (int i = 0; i < path.length; i++) {
            Object obj = map.get(path[i]);
            if (obj == null) {
                return null;
            } else if (i == path.length - 1 && obj instanceof String) {
                return (String) obj;
            } else if (obj instanceof Map) {
                map = (Map<String, Object>) obj;
            } else {
                return null;
            }
        }
        return null;
    }

    private static class LearningObjectiveId {
        final String objectiveId;
        final String contentStandardName;

        public LearningObjectiveId(String objectiveId, String contentStandardName) {
            this.objectiveId = objectiveId;
            this.contentStandardName = contentStandardName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((contentStandardName == null) ? 0 : contentStandardName.hashCode());
            result = prime * result + ((objectiveId == null) ? 0 : objectiveId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            LearningObjectiveId other = (LearningObjectiveId) obj;
            if (contentStandardName == null) {
                if (other.contentStandardName != null) {
                    return false;
                }
            } else if (!contentStandardName.equals(other.contentStandardName)) {
                return false;
            }
            if (objectiveId == null) {
                if (other.objectiveId != null) {
                    return false;
                }
            } else if (!objectiveId.equals(other.objectiveId)) {
                return false;
            }
            return true;
        }
    }
}
