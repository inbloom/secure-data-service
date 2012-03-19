package org.slc.sli.ingestion.transformation;

import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBList;
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

@Scope("prototype")
@Component(StudentAssessmentCombiner.SA_EDFI_COLLECTION_NAME+"TransformationStrategy")
public class StudentAssessmentCombiner extends AbstractTransformationStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(StudentAssessmentCombiner.class);
    
    public final static String SA_EDFI_COLLECTION_NAME = "studentObjectiveAssessment";
    private static final String STUDENT_ASSESSMENT_REFERENCE = "StudentTestAssessmentReference";
    private final static List<String> KEYS_NEEDED = Arrays.asList(STUDENT_ASSESSMENT_REFERENCE);
    
    @Override
    protected void performTransformation() {
        NeutralRecordMongoAccess db = getNeutralRecordMongoAccess();
        Repository<?> repo = db.getRecordRepository();
        DBCollection coll = repo.getCollection(SA_EDFI_COLLECTION_NAME);
        BasicDBList keys = new BasicDBList();
        keys.addAll(KEYS_NEEDED);
        DBCursor cursor = coll.find(new BasicDBObject(BATCH_JOB_ID_KEY, getBatchJobId()), keys);
        while (cursor.hasNext()) {
            DBObject n = cursor.next();
            LOG.debug("Transforming student assessment {}", n);
        }
    }
    
}
