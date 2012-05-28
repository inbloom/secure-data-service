package org.slc.sli.ingestion.smooks.mappings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.assessment.ObjectiveAssessmentBuilder;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ObjectiveAssessmentEntityTest {

    private String validXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<ObjectiveAssessment id=\"TAKSReading3-4\">"
            + "  <IdentificationCode>TAKSReading3-4</IdentificationCode>"
            + "  <MaxRawScore>8</MaxRawScore>"
            + "  <PercentOfAssessment>50</PercentOfAssessment>"
            + "  <Nomenclature>nomenclature</Nomenclature>"
            + "  <AssessmentPerformanceLevel>"
            + "    <AssessmentReportingMethod>ACT score</AssessmentReportingMethod>"
            + "    <MinimumScore>1</MinimumScore>"
            + "    <MaximumScore>20</MaximumScore>"
            + "    <PerformanceLevel>"
            + "      <Description>description</Description>"
            + "      <CodeValue>codevalue</CodeValue>"
            + "    </PerformanceLevel>"
            + "  </AssessmentPerformanceLevel>"
            + "  <AssessmentItemReference id=\"why is this here\" ref=\"EOA12\">"
            + "  </AssessmentItemReference>"
            + "  <LearningObjectiveReference id=\"Reading3-4\" ref=\"Reading3-4\">"
            + "    <LearningObjectiveIdentity>"
            + "      <LearningStandardId ContentStandardName=\"Reading3-4\">"
            + "        <IdentificationCode>Reading3-4</IdentificationCode>"
            + "      </LearningStandardId>"
            + "      <Objective>objective</Objective>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "  <LearningStandardReference id=\"Reading3-4\" ref=\"Reading3-4\">"
            + "    <LearningStandardIdentity>"
            + "      <LearningStandardId ContentStandardName=\"Reading3-4\">"
            + "        <IdentificationCode>Reading3-4</IdentificationCode>"
            + "      </LearningStandardId>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "  <ObjectiveAssessmentReference id=\"EOA13\" ref=\"sub\">"
            + "  </ObjectiveAssessmentReference>"
            + "</ObjectiveAssessment>"
            + "<ObjectiveAssessment id=\"sub\">"
            + "  <IdentificationCode>TAKSReading3-4</IdentificationCode>"
            + "</ObjectiveAssessment>"
            + "</InterchangeAssessmentMetadata>";

    @Test
    public void testValidObjectiveAssessmentXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/ObjectiveAssessment";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData);

        checkValidObjectiveAssessmentNeutralRecord(neutralRecord);
    }

    @Test
    public void testInvalidObjectiveAssessmentXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/ObjectiveAssessment";

        String invalidXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
                + "<ObjectiveAssessment id=\"TAKSReading3-4\">"
                + "<MaxRawScore>8</MaxRawScore>"
                + "<PercentOfAssessment>50</PercentOfAssessment>"
                + "<Nomenclature>nomenclature</Nomenclature>"
                + "</ObjectiveAssessment>"
                + "</InterchangeAssessmentMetadata>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlTestData);

        checkInvalidObjectiveAssessmentNeutralRecord(neutralRecord);

    }

    private void checkValidObjectiveAssessmentNeutralRecord(NeutralRecord neutralRecord) {
        Map<String, Object> entity = neutralRecord.getAttributes();

        Assert.assertEquals("TAKSReading3-4", entity.get("id"));
        Assert.assertEquals("TAKSReading3-4", entity.get("identificationCode"));
        Assert.assertEquals("8", entity.get("maxRawScore").toString());
        Assert.assertEquals("50", entity.get("percentOfAssessment").toString());
        Assert.assertEquals("nomenclature", entity.get("nomenclature"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> assessmentPerformanceLevelList = (List<Map<String, Object>>) entity.get("assessmentPerformanceLevel");
        Assert.assertTrue(assessmentPerformanceLevelList != null);
        Map<String, Object> assessmentPerformanceLevel = assessmentPerformanceLevelList.get(0);
        Assert.assertTrue(assessmentPerformanceLevel != null);
        Assert.assertEquals("ACT score", assessmentPerformanceLevel.get("assessmentReportingMethod"));
        Assert.assertEquals("1", assessmentPerformanceLevel.get("minimumScore").toString());
        Assert.assertEquals("20", assessmentPerformanceLevel.get("maximumScore").toString());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> performanceLevelList = (List<Map<String, Object>>) assessmentPerformanceLevel.get("performanceLevelDescriptor");
        Assert.assertTrue(performanceLevelList != null);
        Map<String, Object> performanceLevel = performanceLevelList.get(0);
        Assert.assertTrue(performanceLevel != null);
        Assert.assertEquals("description", performanceLevel.get("description"));
        performanceLevel = performanceLevelList.get(1);
        Assert.assertTrue(performanceLevel != null);
        Assert.assertEquals("codevalue", performanceLevel.get("codeValue"));
        
        List<?> subObjectiveAssessments = (List<?>) entity.get(ObjectiveAssessmentBuilder.SUB_OBJECTIVE_REFS);
        String subObjectiveAssessment = (String) subObjectiveAssessments.get(0);
        Assert.assertEquals("sub", subObjectiveAssessment);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> assessmentItems = (List<Map<String, Object>>) entity.get("assessmentItemRefs");
        Assert.assertNotNull(assessmentItems);
        Assert.assertEquals(1, assessmentItems.size());
        Assert.assertEquals("EOA12", assessmentItems.get(0).get("ref"));
    }

    private void checkInvalidObjectiveAssessmentNeutralRecord(NeutralRecord neutralRecord) {
        Map<String, Object> entity = neutralRecord.getAttributes();

        Assert.assertEquals("TAKSReading3-4", entity.get("id"));

        Assert.assertEquals(null, entity.get("identificationCode"));
        Assert.assertEquals("8", entity.get("maxRawScore").toString());
        Assert.assertEquals("50", entity.get("percentOfAssessment").toString());
        Assert.assertEquals("nomenclature", entity.get("nomenclature"));
    }

}
