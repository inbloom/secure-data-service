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
    public void mapCsvStudentAssessmentToNeutralRecordTest() throws Exception {

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

    @Test
    public void mapEdfiXmlStudentAssessmentToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentAssessment/StudentAssessment";

        String edfiStudentAssessmentXml = null;
        
        try {
            edfiStudentAssessmentXml = EntityTestUtils
                    .readResourceAsString("smooks/unitTestData/StudentAssessmentEntity.xml");
            
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        System.out.println(edfiStudentAssessmentXml);

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiStudentAssessmentXml);

        checkValidStudentAssessmentNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidStudentAssessmentNeutralRecord(
            NeutralRecord studentAssessmentNeutralRecord) {
        
        assertEquals("studentAssessment", studentAssessmentNeutralRecord.getRecordType());
// TODO: check this works after master pull of Dani's changes
        //        assertEquals("SaId", studentAssessmentNeutralRecord.getLocalId());
        
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
        List specialAccommodationList = (List) specialAccommodationsList.get(0);
        assertEquals("Presentation", specialAccommodationList.get(0));

        List linguisticAccommodationsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("LinguisticAccommodations");
        List linguisticAccommodationList = (List) linguisticAccommodationsList.get(0);
        assertEquals("Bilingual Dictionary",
                linguisticAccommodationList.get(0));

        assertEquals("Primary Administration", studentAssessmentNeutralRecord.getAttributes()
                .get("RetestIndicator"));
        assertEquals("Absent", studentAssessmentNeutralRecord.getAttributes()
                .get("ReasonNotTested"));

        List scoreResultsList = (List) studentAssessmentNeutralRecord.getAttributes().get(
                "ScoreResults");
        Map scoreResultMap = (Map) scoreResultsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "AssessmentReportingMethod", "Pass-fail");
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "Result", "Pass");
        /*
        if (assessmentItemReferenceList.size() > 1) {
            // TODO: remove when we support csv lists
            Map assessmentItemReferenceMap2 = (Map) assessmentItemReferenceList.get(1);
            EntityTestUtils.assertObjectInMapEquals(assessmentItemReferenceMap2, "id", "tk32");
            EntityTestUtils.assertObjectInMapEquals(assessmentItemReferenceMap2, "ref", "TAKSReading3-2");
        }
        
        assertEquals("Primary Administration", studentAssessmentNeutralRecord
                .getAttributes().get("retestIndicator"));
        assertEquals("Absent", studentAssessmentNeutralRecord.getAttributes()
                .get("reasonNotTested"));

        List scoreResultsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("scoreResults");
        Map scoreResultMap = (Map) scoreResultsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap,
                "assessmentReportingMethod", "Pass-fail");
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "result",
                "Pass");

        assertEquals("Fourth grade", studentAssessmentNeutralRecord
                .getAttributes().get("gradeLevelWhenAssessed"));

        List performanceLevelsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("performanceLevels");
        Map performanceLevelMap = (Map) performanceLevelsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(performanceLevelMap,
                "codeValue", "12");
        EntityTestUtils.assertObjectInMapEquals(performanceLevelMap,
                "description", "performancelvldescription");

        Map studentReferenceMap = (Map) studentAssessmentNeutralRecord
                .getAttributes().get("studentReference");
        EntityTestUtils.assertObjectInMapEquals(studentReferenceMap,
                "studentUniqueStateId", "108000601");

        Map assessmentReferenceMap = (Map) studentAssessmentNeutralRecord
                .getAttributes().get("assessmentReference");
        List assessmentIdentificationCodeList = (List) assessmentReferenceMap
                .get("assessmentIdentificationCode");
        Map assessmentIdentificationCodeMap = (Map) assessmentIdentificationCodeList
                .get(0);
        EntityTestUtils.assertObjectInMapEquals(
                assessmentIdentificationCodeMap, "identificationSystem",
                "School");
        EntityTestUtils.assertObjectInMapEquals(
                assessmentIdentificationCodeMap, "assigningOrganizationCode",
                "orgcode");
        EntityTestUtils.assertObjectInMapEquals(
                assessmentIdentificationCodeMap, "ID", "234000601");
        */
    }

}
