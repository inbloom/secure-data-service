package org.slc.sli.ingestion.smooks.mappings;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author mpatel
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentObjectiveAssessmentTest {
    
    @Autowired
    private EntityValidator validator;
    
    String xmlTestData = "<InterchangeStudentAssessment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentAssessment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentObjectiveAssessment>"
            + "<ScoreResults AssessmentReportingMethod=\"Raw score\">"
            + "<Result>12</Result>"
            + "</ScoreResults>"
            + "<StudentTestAssessmentReference ref=\"STA-TAKS-Reading-8-2011-604844\"></StudentTestAssessmentReference>"
            + "<ObjectiveAssessmentReference>"
            + "<ObjectiveAssessmentIdentity>"
            + "<ObjectiveAssessmentIdentificationCode>TAKSReading8-1</ObjectiveAssessmentIdentificationCode>"
            + "</ObjectiveAssessmentIdentity>"
            + "</ObjectiveAssessmentReference>"
            + "</StudentObjectiveAssessment>"
            + "</InterchangeStudentAssessment>";
    
    /*
     * TODO: write CSV unit test
     * public void testValidSectionCSV() throws Exception {
     * 
     * String smooksConfig = "smooks_conf/smooks-studentObjectiveAssessment-csv.xml";
     * String targetSelector = "csv-record";
     * 
     * String testData = "";
     * 
     * ByteArrayInputStream testInput = new ByteArrayInputStream(testData.getBytes());
     * NeutralRecordFileReader nrfr = null;
     * try {
     * nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);
     * 
     * // Tests that the NeutralRecord was created
     * Assert.assertTrue(nrfr.hasNext());
     * 
     * NeutralRecord record = nrfr.next();
     * checkValidSectionNeutralRecord(record);
     * } finally {
     * nrfr.close();
     * }
     * }
     */
    
    @Test
    public void testValidatorSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentAssessment/StudentObjectiveAssessment";
        
        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);
        EntityTestUtils.mapValidation(record.getAttributes(), "student.objective.assessment", validator);
    }
    
    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentAssessment/StudentObjectiveAssessment";
        
        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);
        checkValidSectionNeutralRecord(record);
    }
    
    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        
        List scoreResultList = (List) entity.get("scoreResults");
        Assert.assertTrue(scoreResultList != null);
        Map scoreResult = (Map) scoreResultList.get(0);
        Assert.assertTrue(scoreResult != null);
        Assert.assertEquals("12", scoreResult.get("result").toString());
        Assert.assertEquals("Raw score", scoreResult.get("assessmentReportingMethod"));
        Assert.assertEquals("STA-TAKS-Reading-8-2011-604844", entity.get("studentTestAssessmentRef"));
        Assert.assertEquals("TAKSReading8-1", entity.get("objectiveAssessmentRef"));
    }
}
