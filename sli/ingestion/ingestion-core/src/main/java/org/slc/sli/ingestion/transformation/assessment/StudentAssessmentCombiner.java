package org.slc.sli.ingestion.transformation.assessment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.MongoIterable;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Transformer for StudentAssessment entities
 */
@Scope("prototype")
@Component(StudentAssessmentCombiner.SA_EDFI_COLLECTION_NAME + "TransformationStrategy")
public class StudentAssessmentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentAssessmentCombiner.class);

    public static final String SA_EDFI_COLLECTION_NAME = "studentAssessmentAssociation";
    public static final String SOA_EDFI_COLLECTION_NAME = "studentObjectiveAssessment";
    public static final String STUDENT_ASSESSMENT_REFERENCE = "sTAReference";
    public static final String OBJECTIVE_ASSESSMENT_REFERENCE = "oAReference";
    public static final String XML_ID_FIELD = "xmlId";
    public static final String STUDENT_ASSESSMENT_ITEMS_FIELD = "studentAssessmentItems";

    @SuppressWarnings("unchecked")
    @Override
    protected void performTransformation() {
        NeutralRecordMongoAccess db = getNeutralRecordMongoAccess();
        NeutralRecordRepository repo = db.getRecordRepository();
        Map<String, Map<String, Object>> oas = getOAs(repo.getCollectionForJob(SOA_EDFI_COLLECTION_NAME,
                getBatchJobId()));
        for (IngestionFileEntry fe : getFEs()) {
            BasicDBObject query = getJobQuery();
            query.append("sourceFile", fe.getFileName());
            Iterable<DBObject> cursor = getMatching(SA_EDFI_COLLECTION_NAME, query);
            try {
                NeutralRecordFileWriter writer = new NeutralRecordFileWriter(fe.getNeutralRecordFile());
                try {
                    for (DBObject studentAssessment : cursor) {
                        LOG.debug("Transforming student assessment");
                        Map<String, Object> body = ((DBObject) studentAssessment.get("body")).toMap();
                        String id = (String) body.get(XML_ID_FIELD);
                        body.remove(XML_ID_FIELD);

                        Iterable<NeutralRecord> studentObjectAssessmentsRefs = repo.findAllForJob(
                                SOA_EDFI_COLLECTION_NAME, getBatchJobId(), new NeutralQuery(new NeutralCriteria(
                                        STUDENT_ASSESSMENT_REFERENCE, "=", id)));

//                        LOG.debug("related SOAs are {}", studentObjectAssessmentsRefs);
                        List<Map<String, Object>> soas = new ArrayList<Map<String, Object>>();
                        for (NeutralRecord ref : studentObjectAssessmentsRefs) {
                            soas.add(resolveSoa(ref, oas));
                        }
                        body.put("studentObjectiveAssessments", soas);

                        List<Map<String, Object>> studentAssessmentItems = new ArrayList<Map<String, Object>>();
                        addStudentAssessmentItems(id, studentAssessmentItems);
                        if (studentAssessmentItems.size() > 0) {
                            body.put(STUDENT_ASSESSMENT_ITEMS_FIELD, studentAssessmentItems);
                        }

                        Map<String, Object> localParentIds = (Map<String, Object>) body.get("parents");
                        body.remove("parents");
                        Map<String, Object> localId = ((DBObject) studentAssessment.get("localId")).toMap();
                        NeutralRecord transformed = new NeutralRecord();
                        transformed.setRecordType(SA_EDFI_COLLECTION_NAME);
                        transformed.setLocalParentIds(localParentIds);
                        transformed.setLocalId(localId);
                        transformed.setAttributes(body);
                        writer.writeRecord(transformed);
                    }
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                LogUtil.error(LOG, "Exception occurred while writing transformed student assessments", e);
            }
        }
    }

    private void addStudentAssessmentItems(String studentAssessmentId, List<Map<String, Object>> list) {
        Map<String, String> studentAssessmentItemSearchPaths = new HashMap<String, String>();
        studentAssessmentItemSearchPaths.put("localParentIds.studentTestResultRef", studentAssessmentId);

        Iterable<NeutralRecord> sassItems = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                "studentAssessmentItem", studentAssessmentItemSearchPaths, getJob().getId());

        if (sassItems != null) {
            for (NeutralRecord sai : sassItems) {
                String assessmentId = (String) sai.getLocalParentIds().get("assessmentItemIdentificatonCode");
                if (assessmentId != null) {
                    Map<String, String> assessmentSearchPath = new HashMap<String, String>();
                    assessmentSearchPath.put("body.identificationCode", assessmentId);

                    Iterable<NeutralRecord> assessmentItems = getNeutralRecordMongoAccess().getRecordRepository()
                            .findByPathsForJob("assessmentItem", assessmentSearchPath, getJob().getId());

                    if (assessmentItems.iterator().hasNext()) {
                        NeutralRecord assessmentItem = assessmentItems.iterator().next();
                        sai.getAttributes().put("assessmentItem", assessmentItem.getAttributes());
                    } else {
                        super.getErrorReport(sai.getSourceFile()).error(
                                "Cannot find AssessmentItem referenced by StudentAssessmentItem.  AssessmentItemIdentificationCode: "
                                        + assessmentId, this);
                    }
                } else {
                    super.getErrorReport(sai.getSourceFile())
                            .error("StudentAsessmentItem does not contain an AssessmentItemIdentificationCode referencing an AssessmentItem",
                                    this);
                }

                list.add(sai.getAttributes());
            }
        }
    }

    protected Iterable<DBObject> getMatching(String collectionName, DBObject query) {
        DBCollection studentAssessments = getNeutralRecordMongoAccess().getRecordRepository().getCollectionForJob(
                collectionName, getJob().getId());
        return new MongoIterable(studentAssessments, query, 1000);
    }

    private BasicDBObject getJobQuery() {
        return new BasicDBObject(BATCH_JOB_ID_KEY, getBatchJobId());
    }

    private Map<String, Map<String, Object>> getOAs(DBCollection soas) {
        @SuppressWarnings("unchecked")
        List<String> oaRefs = soas.distinct("body." + OBJECTIVE_ASSESSMENT_REFERENCE, getJobQuery());
        ObjectiveAssessmentBuilder builder = new ObjectiveAssessmentBuilder(getNeutralRecordMongoAccess(), getJob()
                .getId());
        Map<String, Map<String, Object>> oas = new HashMap<String, Map<String, Object>>(oaRefs.size());
        for (String ref : oaRefs) {
            oas.put(ref, builder.getObjectiveAssessment(ref, ObjectiveAssessmentBuilder.BY_IDENTIFICATION_CDOE));
        }
        return oas;
    }

    private Map<String, Object> resolveSoa(NeutralRecord ref, Map<String, Map<String, Object>> oas) {
        Map<String, Object> soaObject = ref.getAttributes();
        soaObject.remove(STUDENT_ASSESSMENT_REFERENCE);
        String objAssmtRef = (String) soaObject.get(OBJECTIVE_ASSESSMENT_REFERENCE);
        LOG.debug("trying to resolve soa for {}", objAssmtRef);
        soaObject.remove(OBJECTIVE_ASSESSMENT_REFERENCE);
        Map<String, Object> objAssmt = oas.get(objAssmtRef);
        if (objAssmt != null) {
            soaObject.put("objectiveAssessment", objAssmt);
        }
        return soaObject;
    }

    private List<IngestionFileEntry> getFEs() {
        List<IngestionFileEntry> all = getJob().getFiles();
        List<IngestionFileEntry> studentAssessmentFiles = new ArrayList<IngestionFileEntry>();
        for (IngestionFileEntry fe : all) {
            if (FileType.XML_STUDENT_ASSESSMENT.equals(fe.getFileType())) {
                studentAssessmentFiles.add(fe);
            }
        }
        return studentAssessmentFiles;
    }
}
