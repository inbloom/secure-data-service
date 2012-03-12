package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AssessmentCombinerTest {

    @Autowired
    AssessmentCombiner combiner;

    @Mock
    Criteria jobIdCriteria;

    @Autowired
    NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Mock
    NeutralRecordRepository repository = new NeutralRecordRepository();

    String batchJobId = "10001";

    @Before
    public void setup() {

        jobIdCriteria = Criteria.where("batchJobId").is(batchJobId);
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

        Map<String, String> path1 = new HashMap<String, String>();
        path1.put("body.AssessmentFamilyIdentificationCode.ID", "606L1");
        Mockito.when(repository.findByPaths(Mockito.eq("assessmentFamily"), Mockito.eq(path1))).thenReturn(assessmentFamily1);

        Map<String, String> path2 = new HashMap<String, String>();
        path2.put("body.AssessmentFamilyIdentificationCode.ID", "606L2");
        Mockito.when(repository.findByPaths(Mockito.eq("assessmentFamily"), Mockito.eq(path2))).thenReturn(assessmentFamily2);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadData() {

        //Performing the transformation
        combiner.perform(batchJobId);

        Map<String, Map<Object, NeutralRecord>> transformedCollections = null;
        try {
            //Get the result of the transformed data
            transformedCollections = (Map<String, Map<Object, NeutralRecord>>) PrivateAccessor.getField(combiner, "transformedCollections");
        } catch (Exception e) {
            Assert.fail();
        }

        //Compare the result
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {

            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {

                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                Assert.assertEquals("606L2.606L1", neutralRecord.getAttributes().get("assessmentFamilyHierarchyName"));
            }
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

        return assessment;
    }

    /**
     * Build a test assessmentFamily Neutral record
     *
     * @param id                : the id of the record
     * @param parantFamilyId    : true if you want this record to contain parent assessment
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
