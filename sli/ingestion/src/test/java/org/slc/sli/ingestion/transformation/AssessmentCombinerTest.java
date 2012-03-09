package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;


/**
 * Unit Test for AssessmentCombiner
 * 
 * @author tke
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/transformation-context.xml" })
public class AssessmentCombinerTest {
    
    @InjectMocks
    AssessmentCombiner combiner;
    
    @Mock
    Criteria jobIdCriteria;
    
    @InjectMocks
    NeutralRecordMongoAccess neutralRecordMongoAccess = new NeutralRecordMongoAccess();
    
    @Mock
    NeutralRecordRepository repository = new NeutralRecordRepository();
    
    String batchJobId = "10001";
    
    @Before
    public void setup() {
        
        jobIdCriteria = Criteria.where("batchJobId").is(batchJobId);
        combiner = new AssessmentCombiner(neutralRecordMongoAccess);
        MockitoAnnotations.initMocks(this);
        
        NeutralRecord assessment = buildTestAssessmentNeutralRecord();
        List<NeutralRecord> data = new ArrayList<NeutralRecord>();
        data.add(assessment);
        
        NeutralRecord assessmentF1 = buildTestAssessmentFamilyNeutralRecord("606L1", true);
        List<NeutralRecord> assessmentFamily1 = new ArrayList<NeutralRecord>();
        assessmentFamily1.add(assessmentF1);
        List<NeutralRecord> assessmentFamily2 = new ArrayList<NeutralRecord>();
        NeutralRecord assessmentF2 = buildTestAssessmentFamilyNeutralRecord("606L2", false);
        assessmentFamily2.add(assessmentF2);
        
        Mockito.when(repository.findByQuery(Mockito.eq("assessment"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0))).thenReturn(data);
        Mockito.when(repository.findByQuery(Mockito.eq("assessmentFamily"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0))).thenReturn(assessmentFamily1);
        //Mockito.when(repository.findByPaths(Mockito.eq("assessmentFamily"),Mockito.eq(path1) )).thenReturn(assessmentFamily1);
        
        Map<String, String> path1 = new HashMap<String, String>();
        path1.put("body.AssessmentFamilyIdentificationCode.ID", "606L1");
        Mockito.when(repository.findByPaths(Mockito.eq("assessmentFamily"),Mockito.eq(path1) )).thenReturn(assessmentFamily1);
        
        Map<String, String> path2 = new HashMap<String, String>();
        path2.put("body.AssessmentFamilyIdentificationCode.ID", "606L2");
        Mockito.when(repository.findByPaths(Mockito.eq("assessmentFamily"), Mockito.eq(path2) )).thenReturn(assessmentFamily2);
        
        /**
         * AssessmentFamilyMatcher
         * 
         * @author tke
         *
         */
        
        class AssessmentFamilyMatcher extends ArgumentMatcher<Map<String, String>> {

            @Override
            public boolean matches(Object argument) {
                Map<String, String> paths = (HashMap<String, String>) argument;
                return "606L1".equals(paths.get("body.AssessmentFamilyIdentificationCode.ID"));
            }
            
        }
        /*
        Mockito.when(repository.findByPaths(Mockito.eq("assessmentFamily"), Mockito.argThat(new AssessmentFamilyMatcher()))).thenReturn(assessmentFamily);
        Mockito.verify(repository).findByPaths(Mockito.eq("assessmentFamily"), Mockito.argThat(new AssessmentFamilyMatcher()));
        */
        /**
         * @author tke
         *
         */
        
        class AssessmentFamilyNotMatcher extends ArgumentMatcher<Map<String, String>> {

            @Override
            public boolean matches(Object argument) {
                Map<String, String> paths = (HashMap<String, String>) argument;
                return !"606L1".equals(paths.get("body.AssessmentFamilyIdentificationCode.ID"));
            }
            
        }
        
        //Mockito.when(repository.findByPaths(Mockito.eq("assessmentFamily"), Mockito.argThat(new AssessmentFamilyNotMatcher()))).thenReturn(Collections.<NeutralRecord>emptyList());
       
    }
    
    @Test
    public void testLoadData() {
        //fail("Not yet implemented");
 
        combiner.perform(batchJobId);
        //combiner.loadData();
        
    }
    /*
    @Test
    public void testTransform() {
        combiner.transform();
    }
    
    @Test
    public void testPersist() {
        combiner.persist();
    }
    */
    
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
        
        return assessment;
    }
    
    private NeutralRecord buildTestAssessmentFamilyNeutralRecord(String id, boolean parantFamilyId) {
        NeutralRecord assessmentFamily = new NeutralRecord();
        assessmentFamily.setAttributeField("id", id);
        assessmentFamily.setRecordType("assessmentFamily");
        assessmentFamily.setAttributeField("AssessmentFamilyTitle", "assessmentFamilyTitle");
        
        List<Map<String, Object>> assessmentFamilyIdentificationCodeList = new ArrayList<Map<String, Object>>();
        Map<String, Object> assessmentFamilyIdentificationCode = new HashMap<String, Object>();
        assessmentFamilyIdentificationCode.put("ID", id);
        assessmentFamilyIdentificationCode.put("IdentificationSystem", "identificationSystem");
        assessmentFamilyIdentificationCode.put("AssigningOrganizationCode", "assigningOrganizationCode");
        assessmentFamilyIdentificationCodeList.add(assessmentFamilyIdentificationCode);
        assessmentFamily.setAttributeField("AssessmentFamilyIdentificationCode", assessmentFamilyIdentificationCodeList);
        
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
    
}
