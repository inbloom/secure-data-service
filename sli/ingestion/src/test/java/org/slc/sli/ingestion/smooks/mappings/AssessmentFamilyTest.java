package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Tests for smooks mappings of AssessmentFamily
 * 
 * @author jtully
 *
 */
public class AssessmentFamilyTest {
    @Test
    public void edfiXmlCourseTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeAssessmentMetadata/AssessmentFamily";

        String edfiAssessmentFamilyXml = "<InterchangeAssessmentMetadata xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<AssessmentFamily id=\"familyid\">"
                + "<AssessmentFamilyTitle>familyTitle</AssessmentFamilyTitle>"
                + "<AssessmentFamilyIdentificationCode IdentificationSystem=\"firstIdentificationSystem\" AssigningOrganizationCode=\"firstAssigningOrganizationCode\" >"
                + "  <ID>firstId</ID>"
                + "</AssessmentFamilyIdentificationCode>"
                + "<AssessmentCategory>State summative assessment 3-8 general</AssessmentCategory>"
                + "<AcademicSubject>Reading</AcademicSubject>"
                + "<GradeLevelAssessed>Third grade</GradeLevelAssessed>"
                + "<LowestGradeLevelAssessed>Fourth grade</LowestGradeLevelAssessed>"
                + "<ContentStandard>State Standard</ContentStandard>"
                + "<Version>2002</Version>"
                + "<RevisionDate>2002-09-01</RevisionDate>"
                + "<Nomenclature>the nomenclature</Nomenclature>"
                + "<AssessmentPeriods id=\"theid\" ref=\"theref\">"
                + "  <CodeValue>code value</CodeValue>"
                + "  <ShortDescription>short desc</ShortDescription>"
                + "  <Description>descript</Description>"
                + "</AssessmentPeriods>"
                + "<AssessmentPeriods id=\"theid2\" ref=\"theref2\">"
                + "  <CodeValue>code value2</CodeValue>"
                + "  <ShortDescription>short desc2</ShortDescription>"
                + "  <Description>descript2</Description>"
                + "</AssessmentPeriods>"
                + "<AssessmentFamilyIdentificationCode IdentificationSystem=\"secondIdentificationSystem\" AssigningOrganizationCode=\"secondAssigningOrganizationCode\" >"
                + "  <ID>secondId</ID>"
                + "</AssessmentFamilyIdentificationCode>"
                + "<AssessmentFamilyReference id=\"tk31\" ref=\"TAKSReading3-1\">"
                + "  <AssessmentFamilyIdentity>"
                + "    <AssessmentFamilyIdentificationCode IdentificationSystem=\"firstRefIdentificationSystem\" AssigningOrganizationCode=\"firstRefAssigningOrganizationCode\" >"
                + "      <ID>firstRefId</ID>"
                + "    </AssessmentFamilyIdentificationCode>"
                + "    <AssessmentFamilyIdentificationCode IdentificationSystem=\"secondRefIdentificationSystem\" AssigningOrganizationCode=\"secondRefAssigningOrganizationCode\" >"
                + "      <ID>secondRefId</ID>"
                + "    </AssessmentFamilyIdentificationCode>"
                + "    <AssessmentFamilyTitle>refFamilyTitle</AssessmentFamilyTitle>"
                + "    <Version>1</Version>"
                + "  </AssessmentFamilyIdentity>"
                + "</AssessmentFamilyReference>"
                + "</AssessmentFamily>"
                + "</InterchangeAssessmentMetadata>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiAssessmentFamilyXml);

        checkValidAssessmentFamilyNeutralRecord(neutralRecord);
    }
    
    @Test
    public void csvAssessmentFamilyTest() throws Exception {

        String smooksConfig = "smooks_conf/smooks-assessmentFamily-csv.xml";
        String targetSelector = "csv-record";

        String assessmentFamilyCsv = "familyid,familyTitle,firstIdentificationSystem,firstAssigningOrganizationCode,firstId,State summative assessment 3-8 general,"
                + "Reading,Third grade,Fourth grade,State Standard,2002,2002-09-01,the nomenclature,theid,theref,code value,short desc,descript,"
                + "tk31,TAKSReading3-1,firstRefIdentificationSystem,firstRefAssigningOrganizationCode,firstRefId,refFamilyTitle,1";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, assessmentFamilyCsv);

        checkValidAssessmentFamilyNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidAssessmentFamilyNeutralRecord(NeutralRecord neutralRecord) {

        assertEquals("record type was not AssessmentFamily", "AssessmentFamily", neutralRecord.getRecordType());
        
        assertEquals("record localId does not match", "familyTitle", neutralRecord.getLocalId());
        
        assertEquals("id does not match", "familyid", neutralRecord.getAttributes().get("id"));
        
        assertEquals("AssessmentFamilyTitle does not match", "familyTitle", neutralRecord.getAttributes().get("AssessmentFamilyTitle"));
        
        List identificationCodeList = (List) neutralRecord.getAttributes().get("AssessmentFamilyIdentificationCode");
        assertNotNull("AssessmentFamilyIdentificationCode list is null", identificationCodeList);
        assertFalse("empty AssessmentFamilyIdentificationCode list", identificationCodeList.isEmpty());
        
        Map firstIdCodeMap = (Map) identificationCodeList.get(0);
        assertNotNull("first AssessmentFamilyIdentificationCode map is null", firstIdCodeMap);
        EntityTestUtils.assertObjectInMapEquals(firstIdCodeMap, "ID", "firstId");
        EntityTestUtils.assertObjectInMapEquals(firstIdCodeMap, "IdentificationSystem", "firstIdentificationSystem");
        EntityTestUtils.assertObjectInMapEquals(firstIdCodeMap, "AssigningOrganizationCode", "firstAssigningOrganizationCode");
        
        if (identificationCodeList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            Map secondIdCodeMap = (Map) identificationCodeList.get(1);
            assertNotNull("second AssessmentFamilyIdentificationCode map is null", secondIdCodeMap);
            EntityTestUtils.assertObjectInMapEquals(secondIdCodeMap, "ID", "secondId");
            EntityTestUtils.assertObjectInMapEquals(secondIdCodeMap, "IdentificationSystem", "secondIdentificationSystem");
            EntityTestUtils.assertObjectInMapEquals(secondIdCodeMap, "AssigningOrganizationCode", "secondAssigningOrganizationCode");
        }
        
        assertEquals("AssessmentCategory does not match", "State summative assessment 3-8 general", neutralRecord.getAttributes().get("AssessmentCategory"));
        assertEquals("AcademicSubject does not match", "Reading", neutralRecord.getAttributes().get("AcademicSubject"));
        assertEquals("GradeLevelAssessed does not match", "Third grade", neutralRecord.getAttributes().get("GradeLevelAssessed"));

        assertEquals("LowestGradeLevelAssessed does not match", "Fourth grade", neutralRecord.getAttributes().get("LowestGradeLevelAssessed"));

        assertEquals("ContentStandard does not match", "State Standard", neutralRecord.getAttributes().get("ContentStandard"));
        assertEquals("Version does not match", "2002", neutralRecord.getAttributes().get("Version"));
        assertEquals("Nomenclature does not match", "the nomenclature", neutralRecord.getAttributes().get("Nomenclature"));
        
        List assessmentPeriodsList = (List) neutralRecord.getAttributes().get("AssessmentPeriods");
        assertNotNull("AssessmentPeriods list is null", assessmentPeriodsList);
        assertFalse("empty AssessmentPeriods list", assessmentPeriodsList.isEmpty());
        Map firstAssesmentPeriod = (Map) assessmentPeriodsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(firstAssesmentPeriod, "id", "theid");
        EntityTestUtils.assertObjectInMapEquals(firstAssesmentPeriod, "ref", "theref");
        EntityTestUtils.assertObjectInMapEquals(firstAssesmentPeriod, "CodeValue", "code value");
        EntityTestUtils.assertObjectInMapEquals(firstAssesmentPeriod, "ShortDescription", "short desc");
        EntityTestUtils.assertObjectInMapEquals(firstAssesmentPeriod, "Description", "descript");
        
        if (assessmentPeriodsList.size() > 1) {
            Map secondAssesmentPeriod = (Map) assessmentPeriodsList.get(1);
            EntityTestUtils.assertObjectInMapEquals(secondAssesmentPeriod, "id", "theid2");
            EntityTestUtils.assertObjectInMapEquals(secondAssesmentPeriod, "ref", "theref2");
            EntityTestUtils.assertObjectInMapEquals(secondAssesmentPeriod, "CodeValue", "code value2");
            EntityTestUtils.assertObjectInMapEquals(secondAssesmentPeriod, "ShortDescription", "short desc2");
            EntityTestUtils.assertObjectInMapEquals(secondAssesmentPeriod, "Description", "descript2");
        }
                
        Map referenceMap = (Map) neutralRecord.getAttributes().get("AssessmentFamilyReference");
        EntityTestUtils.assertObjectInMapEquals(referenceMap, "id", "tk31");
        EntityTestUtils.assertObjectInMapEquals(referenceMap, "ref", "TAKSReading3-1");
        assertNotNull("AssessmentFamilyReference map is null", referenceMap);
        Map referenceIdentityMap = (Map) referenceMap.get("AssessmentFamilyIdentity");
        assertNotNull("AssessmentFamilyIdentity map is null", referenceIdentityMap);
        
        List referenceIdCodeList = (List) referenceIdentityMap.get("AssessmentFamilyIdentificationCode");
        assertNotNull("AssessmentFamilyIdentificationCode list is null", referenceIdCodeList);
        assertFalse("empty AssessmentFamilyIdentificationCode list", referenceIdCodeList.isEmpty());
        
        Map firstRefIdCodeMap = (Map) referenceIdCodeList.get(0);
        assertNotNull("first AssessmentFamilyIdentificationCode map is null", firstRefIdCodeMap);
        EntityTestUtils.assertObjectInMapEquals(firstRefIdCodeMap, "ID", "firstRefId");
        EntityTestUtils.assertObjectInMapEquals(firstRefIdCodeMap, "IdentificationSystem", "firstRefIdentificationSystem");
        EntityTestUtils.assertObjectInMapEquals(firstRefIdCodeMap, "AssigningOrganizationCode", "firstRefAssigningOrganizationCode");
        
        if (referenceIdCodeList.size() > 1) {
            // TODO: remove if block when we support lists in CSV
            Map secondRefIdCodeMap = (Map) referenceIdCodeList.get(1);
            assertNotNull("first AssessmentFamilyIdentificationCode map is null", secondRefIdCodeMap);
            EntityTestUtils.assertObjectInMapEquals(secondRefIdCodeMap, "ID", "secondRefId");
            EntityTestUtils.assertObjectInMapEquals(secondRefIdCodeMap, "IdentificationSystem", "secondRefIdentificationSystem");
            EntityTestUtils.assertObjectInMapEquals(secondRefIdCodeMap, "AssigningOrganizationCode", "secondRefAssigningOrganizationCode");
        }
    }
}
