package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.ingestion.validation.DummyErrorReport;
import org.slc.sli.validation.EntityValidator;

/**
 * Unit Test for SmooksEdFi2SLITransformer
 *
 * @author okrook
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/transformation2-context.xml" })
public class SmooksEdFi2SLITransformerTest {

    @Autowired
    SmooksEdFi2SLITransformer transformer;

    @Autowired
    private EntityValidator validator;

    @Test
    public void testDirectMapping() {
        NeutralRecord directlyMapped = new NeutralRecord();
        directlyMapped.setRecordType("directEntity");
        directlyMapped.setAttributeField("field2", "Test String");

        List<? extends Entity> result = transformer.transform(directlyMapped, new DummyErrorReport());

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Test String", result.get(0).getBody().get("field1"));
    }

    @Test
    public void testAssessmentMapping() {
        NeutralRecord assessment = new NeutralRecord();
        assessment.setRecordType("assessment");
        assessment.setAttributeField("assessmentTitle", "assessmentTitle");
        assessment.setAttributeField("assessmentFamilyHierarchyName", "assessmentFamilyHierarchyName");

        List<Map> assessmentIdentificationCodeList = new ArrayList<Map>();
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

        List<Map> assessmentPerformanceLevelList = new ArrayList<Map>();
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

        List<? extends Entity> result = transformer.transform(assessment, new DummyErrorReport());

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        Assert.assertEquals("assessmentTitle", result.get(0).getBody().get("assessmentTitle"));
        Assert.assertEquals("assessmentFamilyHierarchyName", result.get(0).getBody().get("assessmentFamilyHierarchyName"));

        List<Map> assessmentIDCodeList = (List<Map>) result.get(0).getBody().get("assessmentIdentificationCode");
        Assert.assertNotNull(assessmentIDCodeList);
        Assert.assertEquals(2, assessmentIDCodeList.size());
        Map assessmentIDCode1 = assessmentIDCodeList.get(0);
        Assert.assertNotNull(assessmentIDCode1);
        Assert.assertEquals("202A1", assessmentIDCode1.get("ID"));
        Assert.assertEquals("School", assessmentIDCode1.get("identificationSystem"));
        Assert.assertEquals("assigningOrganizationCode", assessmentIDCode1.get("assigningOrganizationCode"));

        Map assessmentIDCode2 = assessmentIDCodeList.get(1);
        Assert.assertNotNull(assessmentIDCode2);
        Assert.assertEquals("303A1", assessmentIDCode2.get("ID"));
        Assert.assertEquals("State", assessmentIDCode2.get("identificationSystem"));
        Assert.assertEquals("assigningOrganizationCode2", assessmentIDCode2.get("assigningOrganizationCode"));

        List<Map> assessmentPerfLevelList = (List<Map>) result.get(0).getBody().get("assessmentPerformanceLevel");
        Assert.assertNotNull(assessmentPerfLevelList);
        Assert.assertEquals(2, assessmentPerfLevelList.size());
        Map assessmentPerfLevel1 = assessmentPerfLevelList.get(0);
        Assert.assertNotNull(assessmentPerfLevel1);
        Assert.assertEquals(1600, assessmentPerfLevel1.get("maximumScore"));
        Assert.assertEquals(2400, assessmentPerfLevel1.get("minimumScore"));
        Assert.assertEquals("C-scaled scores", assessmentPerfLevel1.get("assessmentReportingMethod"));
        Map perfLevelDescriptor1 = (Map) assessmentPerfLevel1.get("performanceLevelDescriptor");
        Assert.assertNotNull(perfLevelDescriptor1);
        Assert.assertEquals("description1", perfLevelDescriptor1.get("description"));

        Map assessmentPerfLevel2 = assessmentPerfLevelList.get(1);
        Assert.assertNotNull(assessmentPerfLevel2);
        Assert.assertEquals(1800, assessmentPerfLevel2.get("maximumScore"));
        Assert.assertEquals(2600, assessmentPerfLevel2.get("minimumScore"));
        Assert.assertEquals("ACT score", assessmentPerfLevel2.get("assessmentReportingMethod"));
        Map perfLevelDescriptor2 = (Map) assessmentPerfLevel2.get("performanceLevelDescriptor");
        Assert.assertNotNull(perfLevelDescriptor2);
        Assert.assertEquals("description2", perfLevelDescriptor2.get("description"));

        Assert.assertEquals("Achievement test", result.get(0).getBody().get("assessmentCategory"));
        Assert.assertEquals("English", result.get(0).getBody().get("academicSubject"));
        Assert.assertEquals("Adult Education", result.get(0).getBody().get("gradeLevelAssessed"));
        Assert.assertEquals("Early Education", result.get(0).getBody().get("lowestGradeLevelAssessed"));
        Assert.assertEquals("SAT", result.get(0).getBody().get("contentStandard"));
        Assert.assertEquals("assessmentForm", result.get(0).getBody().get("assessmentForm"));
        Assert.assertEquals(1, result.get(0).getBody().get("version"));
        Assert.assertEquals("1999-01-01", result.get(0).getBody().get("revisionDate"));
        Assert.assertEquals(2400, result.get(0).getBody().get("maxRawScore"));
        Assert.assertEquals("nomenclature", result.get(0).getBody().get("nomenclature"));
   }

    @Test
    public void testAssessmentValidation() {
        NeutralRecord assessment = new NeutralRecord();
        assessment.setRecordType("assessment");
        assessment.setAttributeField("assessmentTitle", "assessmentTitle");
        assessment.setAttributeField("assessmentFamilyHierarchyName", "assessmentFamilyHierarchyName");

        List<Map> assessmentIdentificationCodeList = new ArrayList<Map>();
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

        List<Map> assessmentPerformanceLevelList = new ArrayList<Map>();
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
        assessment.setAttributeField("version", 1);
        assessment.setAttributeField("revisionDate", "1999-01-01");
        assessment.setAttributeField("maxRawScore", "2400");
        assessment.setAttributeField("nomenclature", "nomenclature");

        List<? extends Entity> result = transformer.transform(assessment, new DummyErrorReport());

        Entity entity = result.get(0);

        EntityTestUtils.mapValidation(entity.getBody(), "assessment", validator);
    }
}
