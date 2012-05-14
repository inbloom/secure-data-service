package org.slc.sli.ingestion.transformation.assessment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.FileUtils;

/**
 * Test for combining student assessments
 *
 * @author nbrown
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentAssessmentCombinerTest {

    private StudentAssessmentCombiner saCombiner = spy(new StudentAssessmentCombiner());

    @Autowired
    private FileUtils fileUtils;

    @Mock
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Mock
    private NeutralRecordRepository repository = Mockito.mock(NeutralRecordRepository.class);

    private String batchJobId = "10001";
    private Job job = mock(Job.class);
    private IngestionFileEntry fe = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_ASSESSMENT, "", "");

    @SuppressWarnings("deprecation")
    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        saCombiner.setNeutralRecordMongoAccess(neutralRecordMongoAccess);
        when(neutralRecordMongoAccess.getRecordRepository()).thenReturn(repository);

        File nrFile = fileUtils.createTempFile();
        NeutralRecordFileWriter writer = new NeutralRecordFileWriter(nrFile);
        for (NeutralRecord r : buildSANeutralRecords()) {
            writer.writeRecord(r);
        }
        for (NeutralRecord r : buildSOANeutralRecords()) {
            writer.writeRecord(r);
        }
        writer.close();
        fe.setNeutralRecordFile(nrFile);
        when(
                repository.findByQueryForJob(eq("studentObjectiveAssessment"), any(Query.class), eq(batchJobId), eq(0),
                        eq(0))).thenReturn(buildSOANeutralRecords());
        when(repository.findByQueryForJob(eq("objectiveAssessment"), any(Query.class), eq(batchJobId), eq(0), eq(0)))
                .thenReturn(
                        Arrays.asList(AssessmentCombinerTest.buildTestObjAssmt(AssessmentCombinerTest.OBJ1_ID),
                                AssessmentCombinerTest.buildTestObjAssmt(AssessmentCombinerTest.OBJ2_ID)));
        DBCollection oaCollection = mock(DBCollection.class);
        when(repository.getCollectionForJob("studentObjectiveAssessment", batchJobId)).thenReturn(oaCollection);
        when(
                oaCollection.distinct(eq("body." + StudentAssessmentCombiner.OBJECTIVE_ASSESSMENT_REFERENCE),
                        any(BasicDBObject.class))).thenReturn(
                Arrays.asList(AssessmentCombinerTest.OBJ1_ID, AssessmentCombinerTest.OBJ2_ID));
        Iterable<DBObject> sasCursor = Arrays.asList(buildSAObject("sa1"), buildSAObject("sa2"));
        doReturn(sasCursor).when(saCombiner).getMatching(eq("studentAssessmentAssociation"), any(DBObject.class));
        when(
                repository.findAllForJob("studentObjectiveAssessment", batchJobId, new NeutralQuery(
                        new NeutralCriteria(StudentAssessmentCombiner.STUDENT_ASSESSMENT_REFERENCE, "=", "sa1"))))
                .thenReturn(
                        Arrays.asList(buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa1"),
                                buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa1")));
        when(
                repository.findAllForJob("studentObjectiveAssessment", batchJobId, new NeutralQuery(
                        new NeutralCriteria(StudentAssessmentCombiner.STUDENT_ASSESSMENT_REFERENCE, "=", "sa2"))))
                .thenReturn(
                        Arrays.asList(buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa2"),
                                buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa2")));
        when(job.getId()).thenReturn(batchJobId);
        when(job.getFiles()).thenReturn(Arrays.asList(fe));


        NeutralRecord assessmentItem = buildAssessmentItem("item-id");
        NeutralRecord sai = buildStudentAssessmentItem("item-id", "sa1");

        Map<String, String> paths = new HashMap<String, String>();
        paths.put("localParentIds.studentTestResultRef", "sa1");
        when(repository.findByPathsForJob("studentAssessmentItem", paths, batchJobId)).thenReturn(Arrays.asList(sai));

        Map<String, String> paths2 = new HashMap<String, String>();
        paths2.put("body.identificationCode", "item-id");
        when(repository.findByPathsForJob("assessmentItem", paths2, batchJobId)).thenReturn(
                Arrays.asList(assessmentItem));
    }

    @Test
    public void testStudentObjectiveAssessment() throws IOException {
        Collection<NeutralRecord> sas = AssessmentCombinerTest.getTransformedEntities(saCombiner, job, fe);
        boolean foundSai = false;
        for (NeutralRecord record : sas) {
            assertEquals(2, ((Collection<?>) record.getAttributes().get("studentObjectiveAssessments")).size());
            if (record.getAttributes().containsKey(StudentAssessmentCombiner.STUDENT_ASSESSMENT_ITEMS_FIELD)) {
                foundSai = true;
                List<Map<String, Object>> saItems = (List<Map<String, Object>>) record.getAttributes().get(StudentAssessmentCombiner.STUDENT_ASSESSMENT_ITEMS_FIELD);
                assertEquals(1, saItems.size());

            }
        }
        assertEquals(2, sas.size());
        assertTrue(foundSai);
    }

    @SuppressWarnings("unchecked")
    public List<NeutralRecord> buildSANeutralRecords() {
        NeutralRecord sa1 = new NeutralRecord();
        sa1.setRecordType("studentAssessmentAssociation");
        sa1.setAttributeField("administrationDate", "2011-05-01");
        Map<String, Object> scoreResult11 = new HashMap<String, Object>();
        scoreResult11.put("assessmentReportingMethod", "Raw Score");
        scoreResult11.put("result", 2400);
        Map<String, Object> scoreResult12 = new HashMap<String, Object>();
        scoreResult12.put("assessmentReportingMethod", "Percentile");
        scoreResult12.put("result", 99);
        sa1.setAttributeField("ScoreResults", Arrays.asList(scoreResult11, scoreResult12));
        sa1.setAttributeField("xmlId", "sa1");
        NeutralRecord sa2 = new NeutralRecord();
        sa2.setRecordType("studentAssessmentAssociation");
        sa2.setAttributeField("administrationDate", "2011-05-01");
        Map<String, Object> scoreResult21 = new HashMap<String, Object>();
        scoreResult21.put("assessmentReportingMethod", "Raw Score");
        scoreResult21.put("result", 2400);
        Map<String, Object> scoreResult22 = new HashMap<String, Object>();
        scoreResult22.put("assessmentReportingMethod", "Percentile");
        scoreResult22.put("result", 99);
        sa2.setAttributeField("ScoreResults", Arrays.asList(scoreResult21, scoreResult22));
        sa2.setAttributeField("xmlId", "sa2");
        return Arrays.asList(sa1, sa2);
    }

    @SuppressWarnings("unchecked")
    private DBObject buildSAObject(String id) {
        DBObject body = new BasicDBObject();
        body.put("administrationDate", "2011-05-01");
        Map<String, Object> scoreResult11 = new HashMap<String, Object>();
        scoreResult11.put("assessmentReportingMethod", "Raw Score");
        scoreResult11.put("result", 2400);
        Map<String, Object> scoreResult12 = new HashMap<String, Object>();
        scoreResult12.put("assessmentReportingMethod", "Percentile");
        scoreResult12.put("result", 99);
        body.put("ScoreResults", Arrays.asList(scoreResult11, scoreResult12));
        body.put("xmlId", id);
        DBObject sa = new BasicDBObject();
        sa.put("body", body);
        sa.put("localId", new BasicDBObject("studentId", "sudent1"));
        return sa;
    }

    public List<NeutralRecord> buildSOANeutralRecords() {
        return Arrays.asList(buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa1"),
                buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa2"),
                buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa1"),
                buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa2"));
    }

    @SuppressWarnings("unchecked")
    private NeutralRecord buildSOANeutralRecord(String oaRef, String saRef) {
        NeutralRecord soa = new NeutralRecord();
        soa.setRecordType("studentObjectiveAssessment");
        Map<String, Object> scoreResult11 = new HashMap<String, Object>();
        scoreResult11.put("assessmentReportingMethod", "Raw Score");
        scoreResult11.put("result", 2400);
        Map<String, Object> scoreResult12 = new HashMap<String, Object>();
        scoreResult12.put("assessmentReportingMethod", "Percentile");
        scoreResult12.put("result", 99);
        soa.setAttributeField("ScoreResults", Arrays.asList(scoreResult11, scoreResult12));
        soa.setAttributeField("sTAReference", saRef);
        soa.setAttributeField("oAReference", oaRef);
        return soa;
    }

    private NeutralRecord buildAssessmentItem(String id) {
        NeutralRecord rec = new NeutralRecord();
        rec.setRecordType("assessmentItem");
        rec.setAttributeField("identificationCode", id);
        return rec;
    }

    private NeutralRecord buildStudentAssessmentItem(String assessmentItemId, String studentAssessmentId) {
        NeutralRecord rec = new NeutralRecord();
        rec.setRecordType("studentAssessmentItem");
        rec.setAttributeField("assessmentResponse", "response");
        rec.getLocalParentIds().put("assessmentItemIdentificatonCode", assessmentItemId);
        rec.getLocalParentIds().put("studentTestResultRef", studentAssessmentId);
        return rec;
    }

}
