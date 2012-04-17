package org.slc.sli.ingestion.transformation.assessment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
    private static final String XML_ID_FIELD = "xmlId";
    
    @SuppressWarnings("unchecked")
    @Override
    protected void performTransformation() {
        NeutralRecordMongoAccess db = getNeutralRecordMongoAccess();
        Repository<NeutralRecord> repo = db.getRecordRepository();
        DBCollection studentAssessments = repo.getCollection(SA_EDFI_COLLECTION_NAME);
        for (IngestionFileEntry fe : getFEs()) {
            BasicDBObject query = new BasicDBObject(BATCH_JOB_ID_KEY, getBatchJobId());
            query.append("sourceFile", fe.getFileName());
            DBCursor cursor = studentAssessments.find(query);
            try {
                NeutralRecordFileWriter writer = new NeutralRecordFileWriter(fe.getNeutralRecordFile());
                try {
                    while (cursor.hasNext()) {
                        DBObject studentAssessment = cursor.next();
                        LOG.debug("Transforming student assessment {}", studentAssessment);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> body = ((DBObject) studentAssessment.get("body")).toMap();
                        String id = (String) body.get(XML_ID_FIELD);
                        body.remove(XML_ID_FIELD);
                        Iterable<NeutralRecord> studentObjectAssessmentsRefs = repo.findAll(SOA_EDFI_COLLECTION_NAME,
                                new NeutralQuery(new NeutralCriteria(STUDENT_ASSESSMENT_REFERENCE, "=", id)));
                        LOG.debug("related SOAs are {}", studentObjectAssessmentsRefs);
                        List<Map<String, Object>> soas = new ArrayList<Map<String, Object>>();
                        for (NeutralRecord ref : studentObjectAssessmentsRefs) {
                            soas.add(resolveSoa(ref));
                        }
                        body.put("studentObjectiveAssessments", soas);
                        NeutralRecord transformed = new NeutralRecord();
                        transformed.setRecordType(SA_EDFI_COLLECTION_NAME);
                        transformed.setAttributes((Map<String, Object>) body);
                        transformed.setLocalParentIds((Map<String, Object>) studentAssessment.get("localParentIds"));
                        writer.writeRecord(transformed);
                    }
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                LOG.error("Exception occurred while writing transformed student assessments", e);
            }
        }
    }
    
    private Map<String, Object> resolveSoa(NeutralRecord ref) {
        // TODO this needs to be implemented to get the actual SOA
        return ref.getAttributes();
    }
    
    private List<IngestionFileEntry> getFEs() {
        List<IngestionFileEntry> all = getJob().getFiles();
        List<IngestionFileEntry> studentAssessmentFiles = new ArrayList<IngestionFileEntry>();
        for (IngestionFileEntry fe : all) {
            if (fe.getFileType().equals(FileType.XML_STUDENT_ASSESSMENT)) {
                studentAssessmentFiles.add(fe);
            }
        }
        return studentAssessmentFiles;
    }
}
