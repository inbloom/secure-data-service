package org.slc.sli.ingestion.smooks.mappings;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

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
  + "<IdentificationCode>TAKSReading3-4</IdentificationCode>"
  + "<MaxRawScore>8</MaxRawScore>"
  + "<PercentOfAssessment>50</PercentofAssessment>"
  + "<Nomenclature>nomenclature</Nomenclature>"
  + "<AssessmentPerformanceLevel>"
    + "<AssessmentReportingMethod>ACT score</AssessmentReportingMethod>"
    + "<MinimumScore>1</MinimumScore>"
    + "<MaximumScore>20</MaximumScore>"
    + "<PerformanceLevel>"
      + "<Description>description</Description>"
      + "<CodeValue>codevalue</CodeValue>"
    + "</PerformanceLevel>"
  + "</AssessmentPerformanceLevel>"
  + "<AssessmentItemReference>"
    + "<AssessmentItemIdentity>"
      + "<AssessmentItemIdentificationCode>EOA12</AssessmentItemIdentificationCode>"
    + "</AssessmentItemIdentity>"
  + "</AssessmentItemReference>"
  + "<LearningObjectiveReference ref=\"Reading3-4\">"
  +   "<LearningObjectiveIdentity>"
  +     "<LearningObjectiveId>"
  +       "<IdentificationCode>Reading3-4</IdentificationCode>"
  +     "</LearningObjectiveId>"
  +   "</LearningObjectiveIdentity>"
  + "</LearningObjectiveReference>"
  + "<LearningStandardReference ref=\"Reading3-4\">"
  +   "<LearningStandardIdentity>"
  +     "<LearningStandardId>"
  +       "<IdentificationCode>Reading3-4</IdentificationCode>"
  +     "</LearningStandardId>"
  +   "</LearningStandardIdentity>"
  + "</LearningStandardReference>"
  + "<ObjectiveAssessmentReference>"
    + "<ObjectiveAssessmentIdentity>"
      + "<ObjectiveAssessmentIdentificationCode>EOA12</ObjectiveAssessmentIdentificationCode>"
    + "</ObjectiveAssessmentIdentity>"
  + "</ObjectiveAssessmentReference>"
  + "</ObjectiveAssessment>"
  + "</InterchangeAssessmentMetadata>";

    @Test
    public void testValidObjectiveAssessmentXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/ObjectiveAssessment";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig,
                targetSelector, validXmlTestData);

        checkValidObjectiveAssessmentNeutralRecord(neutralRecord);

    }

    private void checkValidObjectiveAssessmentNeutralRecord(NeutralRecord neutralRecord) {
        Map<String, Object> entity = neutralRecord.getAttributes();

        Assert.assertEquals("TAKSReading3-4", entity.get("identificationCode"));
        Assert.assertEquals("8", entity.get("maxRawScore"));
        Assert.assertEquals("50", entity.get("percentofAssessment"));
        Assert.assertEquals("nomenclature", entity.get("nomenclature"));

        List assessmentPerformanceLevelList = (List) entity.get("assessmentPerformanceLevel");
        Assert.assertTrue(assessmentPerformanceLevelList != null);
        Map assessmentPerformanceLevel = (Map) entity.get(0);
        Assert.assertTrue(assessmentPerformanceLevel != null);
        Assert.assertEquals("ACT score", assessmentPerformanceLevel.get("assessmentReportingMethod"));
        Assert.assertEquals("1", assessmentPerformanceLevel.get("minimumScore").toString());
        Assert.assertEquals("20", assessmentPerformanceLevel.get("maximumScore").toString());

        Map performanceLevel = (Map) entity.get("performanceLevel");
        Assert.assertTrue(performanceLevel != null);
        Assert.assertEquals("description", performanceLevel.get("description"));
        Assert.assertEquals("codevalue", performanceLevel.get("codeValue"));

        Assert.assertEquals("EOA12", entity.get("AssessmentItemReference"));
        Assert.assertEquals("Reading3-4", entity.get("LearningObjectiveReference"));
        Assert.assertEquals("Reading3-4", entity.get("LearningStandardReference"));
        Assert.assertEquals("EOA12", entity.get("ObjectiveAssessmentReference"));
    }

}
