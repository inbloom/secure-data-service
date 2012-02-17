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
        assertEquals("ID005", studentAssessmentNeutralRecord.getLocalId());
        
        assertEquals("2013-11-11", studentAssessmentNeutralRecord
                .getAttributes().get("AdministrationDate"));
        assertEquals("2013-08-07", studentAssessmentNeutralRecord
                .getAttributes().get("AdministrationEndDate"));
        assertEquals("231101422", studentAssessmentNeutralRecord
                .getAttributes().get("SerialNumber"));
        assertEquals("Malay", studentAssessmentNeutralRecord.getAttributes()
                .get("AdministrationLanguage"));
        assertEquals("School", studentAssessmentNeutralRecord.getAttributes()
                .get("AdministrationEnvironment"));

        List specialAccommodationsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("SpecialAccommodations");
        List specialAccommodationList = (List) specialAccommodationsList.get(0);
        assertEquals("Colored lenses", specialAccommodationList.get(0));

        List linguisticAccommodationsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("LinguisticAccommodations");
        List linguisticAccommodationList = (List) linguisticAccommodationsList.get(0);
        assertEquals("Oral Translation - Word or Phrase",
                linguisticAccommodationList.get(0));

        assertEquals("2nd Retest", studentAssessmentNeutralRecord.getAttributes()
                .get("RetestIndicator"));
        assertEquals("Medical waiver", studentAssessmentNeutralRecord.getAttributes()
                .get("ReasonNotTested"));

        List scoreResultsList = (List) studentAssessmentNeutralRecord.getAttributes().get(
                "ScoreResults");
        Map scoreResultMap = (Map) scoreResultsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "AssessmentReportingMethod", "Workplace readiness score");
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "Result", "uDcDPPMbzwXnlNsazojAEF6R8LIME6");

        assertEquals("Eleventh grade", studentAssessmentNeutralRecord.getAttributes()
                .get("GradeLevelWhenAssessed"));

        List performanceLevelsList = (List) studentAssessmentNeutralRecord.getAttributes().get(
                "PerformanceLevels");
        Map performanceLevelDescriptorTypeMap = (Map) performanceLevelsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(performanceLevelDescriptorTypeMap, "id", "ID007");
        EntityTestUtils.assertObjectInMapEquals(performanceLevelDescriptorTypeMap, "ref", "ID001");
        List codeValueArray = (List) performanceLevelDescriptorTypeMap.get("CodeValues");
        assertEquals("KYn6axx9pJEX", codeValueArray.get(0));
        List descriptionList = (List) performanceLevelDescriptorTypeMap.get("Descriptions");
        if (!descriptionList.isEmpty())
            assertEquals("bn", descriptionList.get(0));

        /* TODO: Decide whether to model unbounded choices as a map of arrays or array of a map
        List performanceLevelsList = (List) studentAssessmentNeutralRecord.getAttributes().get(
                "PerformanceLevels");
        Map performanceLevelDescriptorTypeMap = (Map) performanceLevelsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(performanceLevelDescriptorTypeMap, "id", "plvlid");
        EntityTestUtils.assertObjectInMapEquals(performanceLevelDescriptorTypeMap, "ref", "plvlref");
        List performanceLevelDescriptorChoiceArray = (List) performanceLevelDescriptorTypeMap.get("performanceLevelChoiceArray");
        EntityTestUtils.assertObjectInMapEquals(performanceLevelMap,
                "codeValue", "12");
        EntityTestUtils.assertObjectInMapEquals(performanceLevelMap,
                "description", "performancelvldescription");
         */

        Map studentReferenceMap = (Map) studentAssessmentNeutralRecord
                .getAttributes().get("StudentReference");
        EntityTestUtils.assertObjectInMapEquals(studentReferenceMap, "id", "ID011");
        EntityTestUtils.assertObjectInMapEquals(studentReferenceMap, "ref", "ID008");
        Map studentIdentityTypeMap = (Map) studentReferenceMap.get("StudentIdentity");
            EntityTestUtils.assertObjectInMapEquals(studentIdentityTypeMap, "StudentUniqueStateId", "Yjmyw");
            List studentIdentificationCodeArray = (List) studentIdentityTypeMap.get("StudentIdentificationCode");
                Map studentIdentificationCodeMap = (Map) studentIdentificationCodeArray.get(0);
                    EntityTestUtils.assertObjectInMapEquals(studentIdentificationCodeMap, "IdentificationSystem", "State Migrant");
                    EntityTestUtils.assertObjectInMapEquals(studentIdentificationCodeMap, "AssigningOrganizationCode", "XcjWvDlTR");
                    EntityTestUtils.assertObjectInMapEquals(studentIdentificationCodeMap, 
                            "IdentificationCode", "rm2eQj5XDoyb1_vtJ5JRfWX.y4DQEKL7bp8HmsOnbd");
            Map nameMap = (Map) studentIdentityTypeMap.get("Name");
                EntityTestUtils.assertObjectInMapEquals(nameMap, "Verification", "Immigration document/visa");
                EntityTestUtils.assertObjectInMapEquals(nameMap, "PersonalTitlePrefix", "Reverend");
                EntityTestUtils.assertObjectInMapEquals(nameMap, "FirstName", "fuQxfaI6tDdkdwIV837X23zwA5gu8UMjpXnmJXhk");
                EntityTestUtils.assertObjectInMapEquals(nameMap, "MiddleName", "C1llfwqfNCmKIc.1B2BgZob3Zj3BB5XpJLegzhm.Q9LfXkRvr7KP5F2BD5ikZoUB");
                EntityTestUtils.assertObjectInMapEquals(nameMap, "LastSurname", "OxHxe4Aupz.3jC");
                EntityTestUtils.assertObjectInMapEquals(nameMap, "GenerationCodeSuffix", "III");
                EntityTestUtils.assertObjectInMapEquals(nameMap, "MaidenName", "Ymdze9xSyTMRSEcf9XGLIpqv24jgVTyon");
            List otherNameList = (List) studentIdentityTypeMap.get("OtherName");
                Map otherNameMap = (Map) otherNameList.get(0);
                    EntityTestUtils.assertObjectInMapEquals(otherNameMap, "OtherNameType", "Alias");
                    EntityTestUtils.assertObjectInMapEquals(otherNameMap, "PersonalTitlePrefix", "Colonel");
                    EntityTestUtils.assertObjectInMapEquals(otherNameMap, "FirstName", "e_lSI9l.l1zL");
                    EntityTestUtils.assertObjectInMapEquals(otherNameMap, "MiddleName", "YO.h5EvS73OrUSF4Od4j");
                    EntityTestUtils.assertObjectInMapEquals(otherNameMap, "LastSurname", "hqFflX_H6_Sz3");
                    EntityTestUtils.assertObjectInMapEquals(otherNameMap, "GenerationCodeSuffix", "Jr");
            EntityTestUtils.assertObjectInMapEquals(studentIdentityTypeMap, "BirthDate", "2013-10-21");
            EntityTestUtils.assertObjectInMapEquals(studentIdentityTypeMap, "Sex", "Male");
            EntityTestUtils.assertObjectInMapEquals(studentIdentityTypeMap, "HispanicLatinoEthnicity", "0");
            List raceList = (List) studentIdentityTypeMap.get("Race");
            assertEquals("Black - African American", raceList.get(0));

            Map assessmentReferenceMap = (Map) studentAssessmentNeutralRecord
                    .getAttributes().get("AssessmentReference");
            EntityTestUtils.assertObjectInMapEquals(assessmentReferenceMap, "id", "ID013");
            EntityTestUtils.assertObjectInMapEquals(assessmentReferenceMap, "ref", "ID002");
            Map assessmentIdentityTypeMap = (Map) assessmentReferenceMap.get("AssessmentIdentity");
            List assessmentIdentificationCodeList = (List) assessmentIdentityTypeMap.get("AssessmentIdentificationCode");
            if (assessmentIdentificationCodeList.isEmpty()) {
                EntityTestUtils.assertObjectInMapEquals(assessmentIdentityTypeMap, "AssessmentFamilyTitle", "xxNq_TgKZ-y4Al0vVtxFo3D5.azgcH.I");
                EntityTestUtils.assertObjectInMapEquals(assessmentIdentityTypeMap, "AssessmentTitle", "c-aKzuT08");
                EntityTestUtils.assertObjectInMapEquals(assessmentIdentityTypeMap, "AssessmentCategory", "State high school subject assessment");
                EntityTestUtils.assertObjectInMapEquals(assessmentIdentityTypeMap, "AcademicSubject", "Communication and Audio/Visual Technology");
                EntityTestUtils.assertObjectInMapEquals(assessmentIdentityTypeMap, "GradeLevelAssessed", "Postsecondary");
                EntityTestUtils.assertObjectInMapEquals(assessmentIdentityTypeMap, "Version", "-133291797");
            } else {
                Map assessmentIdentificationCodeMap = (Map) assessmentIdentificationCodeList.get(0);
                    EntityTestUtils.assertObjectInMapEquals(assessmentIdentificationCodeMap, "IdentificationSystem", "State Migrant");
                    EntityTestUtils.assertObjectInMapEquals(assessmentIdentificationCodeMap, "AssigningOrganizationCode", "XcjWvDlTR");
                    EntityTestUtils.assertObjectInMapEquals(assessmentIdentificationCodeMap, 
                            "ID", "rm2eQj5XDoyb1_vtJ5JRfWX.y4DQEKL7bp8HmsOnbd");
            }
    }

}
