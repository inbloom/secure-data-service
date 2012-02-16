package org.slc.sli.ingestion.smooks.mappings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

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
  + "<PercentOfAssessment>50</PercentOfAssessment>"
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
  + "<AssessmentItemReference id=\"EOA12\" ref=\"EOA12\">"
  + "</AssessmentItemReference>"
  + "<LearningObjectiveReference id=\"Reading3-4\" ref=\"Reading3-4\">"
  +   "<LearningObjectiveIdentity>"
  +     "<LearningStandardId ContentStandardName=\"Reading3-4\">"
  +       "<IdentificationCode>Reading3-4</IdentificationCode>"
  +     "</LearningStandardId>"
  +     "<Objective>objective</Objective>"
  +   "</LearningObjectiveIdentity>"
  + "</LearningObjectiveReference>"
  + "<LearningStandardReference id=\"Reading3-4\" ref=\"Reading3-4\">"
  +   "<LearningStandardIdentity>"
  +     "<LearningStandardId ContentStandardName=\"Reading3-4\">"
  +       "<IdentificationCode>Reading3-4</IdentificationCode>"
  +     "</LearningStandardId>"
  +   "</LearningStandardIdentity>"
  + "</LearningStandardReference>"
  + "<ObjectiveAssessmentReference id=\"EOA12\" ref=\"EOA12\">"
  + "</ObjectiveAssessmentReference>"
  + "</ObjectiveAssessment>"
  + "</InterchangeAssessmentMetadata>";

    @Test
    public void testValidObjectiveAssessmentXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/ObjectiveAssessment";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validXmlTestData);

        checkValidObjectiveAssessmentNeutralRecord(neutralRecord);

    }

    @Test
    public void testValidObjectiveAssessmentCSV() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-objectiveAssessment-csv.xml";
        String targetSelector = "csv-record";

        String validCsvTestData = "TAKSReading3-4,TAKSReading3-4,8,codevalue,description,ACT score,1,20,50,nomenclature,EOA12,EOA12,Reading3-4,Reading3-4,objective,Reading3-4,Reading3-4,Reading3-4,Reading3-4,Reading3-4,Reading3-4,EOA12,EOA12";
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validCsvTestData);

        checkValidObjectiveAssessmentNeutralRecord(neutralRecord);

    }

    private void checkValidObjectiveAssessmentNeutralRecord(NeutralRecord neutralRecord) {
        Map<String, Object> entity = neutralRecord.getAttributes();

        Assert.assertEquals("TAKSReading3-4", entity.get("id"));

        Assert.assertEquals("TAKSReading3-4", entity.get("identificationCode"));
        Assert.assertEquals("8", entity.get("maxRawScore").toString());
        Assert.assertEquals("50", entity.get("percentOfAssessment").toString());
        Assert.assertEquals("nomenclature", entity.get("nomenclature"));

        List assessmentPerformanceLevelList = (List) entity.get("assessmentPerformanceLevel");
        Assert.assertTrue(assessmentPerformanceLevelList != null);
        Map assessmentPerformanceLevel = (Map) assessmentPerformanceLevelList.get(0);
        Assert.assertTrue(assessmentPerformanceLevel != null);
        Assert.assertEquals("ACT score", assessmentPerformanceLevel.get("assessmentReportingMethod"));
        Assert.assertEquals("1", assessmentPerformanceLevel.get("minimumScore").toString());
        Assert.assertEquals("20", assessmentPerformanceLevel.get("maximumScore").toString());

        Map performanceLevel = (Map) assessmentPerformanceLevel.get("performanceLevel");
        Assert.assertTrue(performanceLevel != null);
        Assert.assertEquals("description", performanceLevel.get("description"));
        Assert.assertEquals("codevalue", performanceLevel.get("codeValue"));

        List assessmentItemReferenceList = (List) entity.get("assessmentItemReference");
        Assert.assertTrue(assessmentItemReferenceList != null);
        Map assessmentItemReference = (Map) assessmentItemReferenceList.get(0);
        Assert.assertTrue(assessmentItemReference != null);
        Assert.assertEquals("EOA12", assessmentItemReference.get("id"));
        Assert.assertEquals("EOA12", assessmentItemReference.get("ref"));

        List learningObjectiveReferenceList = (List) entity.get("learningObjectiveReference");
        Assert.assertTrue(learningObjectiveReferenceList != null);
        Map learningObjectiveReference = (Map) learningObjectiveReferenceList.get(0);
        Assert.assertTrue(learningObjectiveReference != null);
        Assert.assertEquals("Reading3-4", learningObjectiveReference.get("id"));
        Assert.assertEquals("Reading3-4", learningObjectiveReference.get("ref"));
        Map learningObjectiveIdentity = (Map) learningObjectiveReference.get("learningObjectiveIdentity");
        Assert.assertTrue(learningObjectiveIdentity != null);
        List objective = (List) learningObjectiveIdentity.get("objective");
        Assert.assertTrue(objective != null);
        Assert.assertEquals("objective", objective.get(0));
        List learningStandardIdArray = (List) learningObjectiveIdentity.get("learningStandardId");
        Assert.assertTrue(learningStandardIdArray != null);
        Map learningStandardIdObjective = (Map) learningStandardIdArray.get(0);
        Assert.assertTrue(learningStandardIdObjective != null);
        Assert.assertEquals("Reading3-4", learningStandardIdObjective.get("contentStandardName"));
        Assert.assertEquals("Reading3-4", learningStandardIdObjective.get("identificationCode"));

        List learningStandardReferenceList = (List) entity.get("learningStandardReference");
        Assert.assertTrue(learningStandardReferenceList != null);
        Map learningStandardReference = (Map) learningStandardReferenceList.get(0);
        Assert.assertTrue(learningStandardReference != null);
        Assert.assertEquals("Reading3-4", learningStandardReference.get("id"));
        Assert.assertEquals("Reading3-4", learningStandardReference.get("ref"));
        Map learningStandardIdentity = (Map) learningStandardReference.get("learningStandardIdentity");
        Assert.assertTrue(learningStandardIdentity != null);
        Map learningStandardId = (Map) learningStandardIdentity.get("learningStandardId");
        Assert.assertTrue(learningStandardId != null);
        Assert.assertEquals("Reading3-4", learningStandardId.get("contentStandardName"));
        Assert.assertEquals("Reading3-4", learningStandardId.get("identificationCode"));

        List objectiveAssessmentReferenceList = (List) entity.get("objectiveAssessmentReference");
        Assert.assertTrue(objectiveAssessmentReferenceList != null);
        Map objectiveAssessmentReference = (Map) objectiveAssessmentReferenceList.get(0);
        Assert.assertTrue(objectiveAssessmentReference != null);
        Assert.assertEquals("EOA12", objectiveAssessmentReference.get("id"));
        Assert.assertEquals("EOA12", objectiveAssessmentReference.get("ref"));
    }

}
