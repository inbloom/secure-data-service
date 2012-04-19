package org.slc.sli.ingestion.transformation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit Test for AssessmentCombiner
 * 
 * @author tke
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AssessmentCombinerTest {
    
    private static final String OBJ2_ID = "Obj2";
    
    private static final String OBJ1_ID = "Obj1";
    
    @Autowired
    private AssessmentCombiner combiner;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Mock
    private NeutralRecordMongoAccess neutralRecordMongoAccess;
    
    @Mock
    private NeutralRecordRepository repository = Mockito.mock(NeutralRecordRepository.class);
    
    private String batchJobId = "10001";
    private Job job = mock(Job.class);
    private IngestionFileEntry fe = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_ASSESSMENT_METADATA, "",
            "");
    
    private static final String PERIOD_DESCRIPTOR_CODE_VALUE = "Spring2012";
    
    @SuppressWarnings("deprecation")
    @Before
    public void setup() throws IOException {
        
        MockitoAnnotations.initMocks(this);
        
        combiner.setNeutralRecordMongoAccess(neutralRecordMongoAccess);
        when(neutralRecordMongoAccess.getRecordRepository()).thenReturn(repository);
        
        NeutralRecord assessment = buildTestAssessmentNeutralRecord();
        List<NeutralRecord> assessments = new ArrayList<NeutralRecord>();
        assessments.add(assessment);
        
        NeutralRecord assessmentF1 = buildTestAssessmentFamilyNeutralRecord("606L1", true);
        List<NeutralRecord> assessmentFamily1 = new ArrayList<NeutralRecord>();
        assessmentFamily1.add(assessmentF1);
        List<NeutralRecord> assessmentFamily2 = new ArrayList<NeutralRecord>();
        NeutralRecord assessmentF2 = buildTestAssessmentFamilyNeutralRecord("606L2", false);
        assessmentFamily2.add(assessmentF2);
        List<NeutralRecord> families = Arrays.asList(assessmentF1, assessmentF2);
        File nrFile = fileUtils.createTempFile();
        NeutralRecordFileWriter writer = new NeutralRecordFileWriter(nrFile);
        writer.writeRecord(assessment);
        writer.writeRecord(assessmentF1);
        writer.writeRecord(assessmentF2);
        writer.close();
        fe.setNeutralRecordFile(nrFile);
        
        when(repository.findByQuery(Mockito.eq("assessment"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(assessments);
        when(
                repository.findByQuery(Mockito.eq("assessmentFamily"), Mockito.any(Query.class), Mockito.eq(0),
                        Mockito.eq(0))).thenReturn(families);
        
        Map<String, String> path1 = new HashMap<String, String>();
        path1.put("body.AssessmentFamilyIdentificationCode.ID", "606L1");
        when(repository.findByPaths(Mockito.eq("assessmentFamily"), Mockito.eq(path1))).thenReturn(assessmentFamily1);
        
        Map<String, String> path2 = new HashMap<String, String>();
        path2.put("body.AssessmentFamilyIdentificationCode.ID", "606L2");
        when(repository.findByPaths(Mockito.eq("assessmentFamily"), Mockito.eq(path2))).thenReturn(assessmentFamily2);
        
        Map<String, String> pdPath = new HashMap<String, String>();
        pdPath.put("body.codeValue", PERIOD_DESCRIPTOR_CODE_VALUE);
        when(repository.findByPaths("assessmentPeriodDescriptor", pdPath)).thenReturn(
                Arrays.asList(buildTestPeriodDescriptor()));
        
        when(repository.findOne("objectiveAssessment", new NeutralQuery(new NeutralCriteria("id", "=", OBJ1_ID))))
                .thenReturn(buildTestObjAssmt(OBJ1_ID));
        
        when(repository.findOne("objectiveAssessment", new NeutralQuery(new NeutralCriteria("id", "=", OBJ2_ID))))
                .thenReturn(buildTestObjAssmt(OBJ2_ID));
        
        when(job.getId()).thenReturn(batchJobId);
        when(job.getFiles()).thenReturn(Arrays.asList(fe));
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testAssessments() throws IOException {
        
        Collection<NeutralRecord> transformedCollections = getTransformedAssessments();
        
        // Compare the result
        for (NeutralRecord neutralRecord : transformedCollections) {
            
            assertEquals("606L2.606L1", neutralRecord.getAttributes().get("assessmentFamilyHierarchyName"));
            assertEquals(buildTestPeriodDescriptor().getAttributes(),
                    neutralRecord.getAttributes().get("assessmentPeriodDescriptor"));
            assertEquals(Arrays.asList(buildTestObjAssmt(OBJ1_ID).getAttributes(), buildTestObjAssmt(OBJ2_ID)
                    .getAttributes()), neutralRecord.getAttributes().get("objectiveAssessment"));
        }
    }
    
    private Collection<NeutralRecord> getTransformedAssessments() throws IOException {
        // Performing the transformation
        combiner.perform(job);
        NeutralRecordFileReader reader = new NeutralRecordFileReader(fe.getNeutralRecordFile());
        List<NeutralRecord> records = new ArrayList<NeutralRecord>();
        while (reader.hasNext()) {
            records.add(reader.next());
        }
        fe.getNeutralRecordFile().delete();
        return records;
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    @Test
    public void testHierarchicalAssessments() throws IOException {
        String superOA = "SuperObjAssessment";
        String subOA = "SubObjAssessment";
        NeutralRecord superObjAssessmentRef = buildTestObjAssmt(superOA);
        superObjAssessmentRef.setAttributeField("subObjectiveRefs", Arrays.asList(subOA));
        NeutralRecord superObjAssessmentActual = buildTestObjAssmt(superOA);
        superObjAssessmentActual.setAttributeField("objectiveAssessments",
                Arrays.asList(buildTestObjAssmt(subOA).getAttributes()));
        
        when(repository.findOne("objectiveAssessment", new NeutralQuery(new NeutralCriteria("id", "=", superOA))))
                .thenReturn(superObjAssessmentRef);
        
        when(repository.findOne("objectiveAssessment", new NeutralQuery(new NeutralCriteria("id", "=", subOA))))
                .thenReturn(buildTestObjAssmt(subOA));
        
        NeutralRecord assessment = buildTestAssessmentNeutralRecord();
        assessment.setAttributeField("objectiveAssessmentRefs", Arrays.asList(superOA));
        when(repository.findByQuery(Mockito.eq("assessment"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(assessment));
        for (NeutralRecord neutralRecord : getTransformedAssessments()) {
            assertEquals(Arrays.asList(superObjAssessmentActual.getAttributes()),
                    neutralRecord.getAttributes().get("objectiveAssessment"));
            
        }
    }
    
    private NeutralRecord buildTestAssessmentNeutralRecord() {
        
        NeutralRecord assessment = new NeutralRecord();
        assessment.setRecordType("assessment");
        assessment.setAttributeField("assessmentTitle", "assessmentTitle");
        assessment.setAttributeField("parentAssessmentFamilyId", "606L1");
        
        List<Map<String, Object>> assessmentIdentificationCodeList = new ArrayList<Map<String, Object>>();
        Map<String, Object> assessmentIdentificationCode1 = new HashMap<String, Object>();
        assessmentIdentificationCode1.put("ID", "202A1");
        assessmentIdentificationCode1.put("identificationSystem", "School");
        assessmentIdentificationCode1.put("assigningOrganizationCode", "assigningOrganizationCode");
        Map<String, Object> assessmentIdentificationCode2 = new HashMap<String, Object>();
        assessmentIdentificationCode2.put("ID", "303A1");
        assessmentIdentificationCode2.put("identificationSystem", "State");
        assessmentIdentificationCode2.put("assigningOrganizationCode", "assigningOrganizationCode2");
        assessmentIdentificationCodeList.add(assessmentIdentificationCode1);
        assessmentIdentificationCodeList.add(assessmentIdentificationCode2);
        assessment.setAttributeField("assessmentIdentificationCode", assessmentIdentificationCodeList);
        
        assessment.setAttributeField("assessmentCategory", "Achievement test");
        assessment.setAttributeField("academicSubject", "English");
        assessment.setAttributeField("gradeLevelAssessed", "Adult Education");
        assessment.setAttributeField("lowestGradeLevelAssessed", "Early Education");
        
        List<Map<String, Object>> assessmentPerformanceLevelList = new ArrayList<Map<String, Object>>();
        Map<String, Object> assessmentPerformanceLevel1 = new HashMap<String, Object>();
        assessmentPerformanceLevel1.put("maximumScore", "1600");
        assessmentPerformanceLevel1.put("minimumScore", "2400");
        assessmentPerformanceLevel1.put("assessmentReportingMethod", "C-scaled scores");
        Map<String, Object> performanceLevelDescriptor1 = new HashMap<String, Object>();
        performanceLevelDescriptor1.put("description", "description1");
        assessmentPerformanceLevel1.put("performanceLevelDescriptor", performanceLevelDescriptor1);
        
        Map<String, Object> assessmentPerformanceLevel2 = new HashMap<String, Object>();
        assessmentPerformanceLevel2.put("maximumScore", "1800");
        assessmentPerformanceLevel2.put("minimumScore", "2600");
        assessmentPerformanceLevel2.put("assessmentReportingMethod", "ACT score");
        Map<String, Object> performanceLevelDescriptor2 = new HashMap<String, Object>();
        performanceLevelDescriptor2.put("description", "description2");
        assessmentPerformanceLevel2.put("performanceLevelDescriptor", performanceLevelDescriptor2);
        
        assessmentPerformanceLevelList.add(assessmentPerformanceLevel1);
        assessmentPerformanceLevelList.add(assessmentPerformanceLevel2);
        assessment.setAttributeField("assessmentPerformanceLevel", assessmentPerformanceLevelList);
        
        assessment.setAttributeField("contentStandard", "SAT");
        assessment.setAttributeField("assessmentForm", "assessmentForm");
        assessment.setAttributeField("version", "1");
        assessment.setAttributeField("revisionDate", "1999-01-01");
        assessment.setAttributeField("maxRawScore", "2400");
        assessment.setAttributeField("nomenclature", "nomenclature");
        
        assessment.setAttributeField("periodDescriptorRef", PERIOD_DESCRIPTOR_CODE_VALUE);
        assessment.setAttributeField("objectiveAssessmentRefs", Arrays.asList(OBJ1_ID, OBJ2_ID));
        
        return assessment;
    }
    
    /**
     * Build a test assessmentFamily Neutral record
     * 
     * @param id
     *            : the id of the record
     * @param parantFamilyId
     *            : true if you want this record to contain parent assessment
     * @return the created neutral record
     */
    private NeutralRecord buildTestAssessmentFamilyNeutralRecord(String id, boolean parantFamilyId) {
        NeutralRecord assessmentFamily = new NeutralRecord();
        assessmentFamily.setAttributeField("id", id);
        assessmentFamily.setRecordType("assessmentFamily");
        assessmentFamily.setAttributeField("AssessmentFamilyTitle", id);
        
        List<Map<String, Object>> assessmentFamilyIdentificationCodeList = new ArrayList<Map<String, Object>>();
        Map<String, Object> assessmentFamilyIdentificationCode = new HashMap<String, Object>();
        assessmentFamilyIdentificationCode.put("ID", id);
        assessmentFamilyIdentificationCode.put("IdentificationSystem", "identificationSystem");
        assessmentFamilyIdentificationCode.put("AssigningOrganizationCode", "assigningOrganizationCode");
        assessmentFamilyIdentificationCodeList.add(assessmentFamilyIdentificationCode);
        assessmentFamily
                .setAttributeField("AssessmentFamilyIdentificationCode", assessmentFamilyIdentificationCodeList);
        
        assessmentFamily.setAttributeField("AssessmentCategory", "assessmentCategory");
        assessmentFamily.setAttributeField("AcademicSubject", "academicSubject");
        assessmentFamily.setAttributeField("GradeLevelAssessed", "gradeLevelAssessed");
        assessmentFamily.setAttributeField("LowestGradeLevelAssessed", "lowestGradeLevelAssessed");
        assessmentFamily.setAttributeField("ContentStandard", "contentStandard");
        assessmentFamily.setAttributeField("Version", "1");
        assessmentFamily.setAttributeField("RevisionDate", "1990-01-01");
        assessmentFamily.setAttributeField("NomenClature", "nomenClature");
        
        List<Map<String, Object>> assessmentPeriodsList = new ArrayList<Map<String, Object>>();
        Map<String, Object> assessmentPeriod = new HashMap<String, Object>();
        assessmentPeriod.put("id", "p101");
        assessmentPeriod.put("ref", "r101");
        List<String> codeValues = new ArrayList<String>();
        codeValues.add("2000-01-01");
        assessmentPeriod.put("CodeValues", codeValues);
        List<String> shortDescriptions = new ArrayList<String>();
        shortDescriptions.add("short description");
        assessmentPeriod.put("ShortDescriptions", shortDescriptions);
        List<String> descriptions = new ArrayList<String>();
        descriptions.add("description");
        assessmentPeriod.put("Description", descriptions);
        assessmentPeriodsList.add(assessmentPeriod);
        assessmentFamily.setAttributeField("AssessmentPeriods", assessmentPeriodsList);
        
        if (parantFamilyId)
            assessmentFamily.setAttributeField("parentAssessmentFamilyId", "606L2");
        
        return assessmentFamily;
    }
    
    private NeutralRecord buildTestPeriodDescriptor() {
        NeutralRecord rec = new NeutralRecord();
        rec.setRecordType("assessmentPeriodDescriptor");
        rec.setAttributeField("codeValue", PERIOD_DESCRIPTOR_CODE_VALUE);
        rec.setAttributeField("description", "Spring 2012");
        return rec;
    }
    
    private NeutralRecord buildTestObjAssmt(String idCode) {
        NeutralRecord rec = new NeutralRecord();
        rec.setRecordType("objectiveAssessment");
        rec.setAttributeField("identificationCode", idCode);
        
        return rec;
    }
    
}
