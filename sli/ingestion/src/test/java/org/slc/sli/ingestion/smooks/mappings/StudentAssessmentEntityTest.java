package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Test the smooks mappings for StudentAssessment entity
 * 
 * @author bsuzuki
 * 
 */
public class StudentAssessmentEntityTest {

    @Ignore
    @Test
    public void csvStudentAssessmentTest() throws Exception {

        String smooksConfig = "smooks_conf/smooks-studentAssessment-csv.xml";

        String targetSelector = "csv-record";
        
        String studentAssessmentCsv = null;
        
        try {
            studentAssessmentCsv = EntityTestUtils.readResourceAsString("smooks/unitTestData/StudentAssessmentEntity.csv");
            
        }   catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                studentAssessmentCsv);

        checkValidStudentAssessmentNeutralRecord(neutralRecord);
    }

    @Ignore
    @Test
    public void edfiXmlTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentAssessment/StudentAssessment";

        String edfiStudentAssessmentXml = null;
        
        try {
            edfiStudentAssessmentXml = EntityTestUtils
                    .readResourceAsString("smooks/unitTestData/StudentAssessmentEntity.csv");
            
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiStudentAssessmentXml);

        checkValidStudentAssessmentNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidStudentAssessmentNeutralRecord(
            NeutralRecord studentAssessmentNeutralRecord) {

        assertEquals("1999-07-12", studentAssessmentNeutralRecord
                .getAttributes().get("AdministrationDate"));
        assertEquals("2010-06-14", studentAssessmentNeutralRecord
                .getAttributes().get("AdministrationEndDate"));
        assertEquals("231101422", studentAssessmentNeutralRecord
                .getAttributes().get("SerialNumber"));
        assertEquals("English", studentAssessmentNeutralRecord.getAttributes()
                .get("AdministrationLanguage"));
        assertEquals("School", studentAssessmentNeutralRecord.getAttributes()
                .get("AdministrationEnvironment"));

        List specialAccommodationsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("SpecialAccommodations");
        assertEquals("Presentation", specialAccommodationsList.get(0));

        List linguisticAccommodationsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("LinguisticAccommodations");
        assertEquals("Bilingual Dictionary",
                linguisticAccommodationsList.get(0));

        assertEquals("Primary Administration", studentAssessmentNeutralRecord
                .getAttributes().get("RetestIndicator"));
        assertEquals("Absent", studentAssessmentNeutralRecord.getAttributes()
                .get("ReasonNotTested"));

        List scoreResultsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("ScoreResults");
        Map scoreResultMap = (Map) scoreResultsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap,
                "AssessmentReportingMethod", "Pass-fail");
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "Result",
                "Pass");

        assertEquals("Fourth grade", studentAssessmentNeutralRecord
                .getAttributes().get("GradeLevelWhenAssessed"));

        List performanceLevelsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("PerformanceLevels");
        Map performanceLevelMap = (Map) performanceLevelsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(performanceLevelMap,
                "CodeValue", "12");
        EntityTestUtils.assertObjectInMapEquals(performanceLevelMap,
                "Description", "performancelvldescription");

        Map studentReferenceMap = (Map) studentAssessmentNeutralRecord
                .getAttributes().get("StudentReference");
        EntityTestUtils.assertObjectInMapEquals(studentReferenceMap,
                "StudentUniqueStateId", "108000601");

        Map assessmentReferenceMap = (Map) studentAssessmentNeutralRecord
                .getAttributes().get("AssessmentReference");
        List assessmentIdentificationCodeList = (List) assessmentReferenceMap
                .get("AssessmentIdentificationCode");
        Map assessmentIdentificationCodeMap = (Map) assessmentIdentificationCodeList
                .get(0);
        EntityTestUtils.assertObjectInMapEquals(
                assessmentIdentificationCodeMap, "IdentificationSystem",
                "School");
        EntityTestUtils.assertObjectInMapEquals(
                assessmentIdentificationCodeMap, "AssigningOrganizationCode",
                "orgcode");
        EntityTestUtils.assertObjectInMapEquals(
                assessmentIdentificationCodeMap, "ID", "234000601");
    }

}
