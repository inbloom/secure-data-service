package org.slc.sli.ingestion.transformation;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * Transformer for StudentAssessment entities
 */
@Scope("prototype")
@Component(StudentAssessmentCombiner.SOA_EDFI_COLLECTION_NAME + "TransformationStrategy")
public class StudentAssessmentCombiner extends AbstractTransformationStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(StudentAssessmentCombiner.class);
    
    public static final String SA_EDFI_COLLECTION_NAME = "studentAssessmentAssociation";
    public static final String SOA_EDFI_COLLECTION_NAME = "studentObjectiveAssessment";
    private static final String STUDENT_ASSESSMENT_REFERENCE = "sTAReference";
    private static final String OBJECTIVE_ASSESSMENT_REFERENCE = "oAReference";
    
    @Override
    protected void performTransformation() {
        NeutralRecordMongoAccess db = getNeutralRecordMongoAccess();
        Repository<?> repo = db.getRecordRepository();
        DBCollection coll = repo.getCollection(SOA_EDFI_COLLECTION_NAME);
        DBCursor cursor = coll.find(new BasicDBObject(BATCH_JOB_ID_KEY, getBatchJobId()));
        while (cursor.hasNext()) {
            DBObject n = cursor.next();
            LOG.debug("Transforming student objective assessment {}", n);
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) n.get("body");
            Object studentAssessmentRef = body.get(STUDENT_ASSESSMENT_REFERENCE);
            Object objectiveAssessmentRef = body.get(OBJECTIVE_ASSESSMENT_REFERENCE);
            body.remove(STUDENT_ASSESSMENT_REFERENCE);
            body.remove(OBJECTIVE_ASSESSMENT_REFERENCE);
            LOG.debug("body is {}", body);
            LOG.debug("references student-assessment {} and objective assessment {}", new Object[] {
                    studentAssessmentRef, objectiveAssessmentRef });
            // look up related student objective assessments
        }
    }
    
}
