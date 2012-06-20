package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.xml.sax.SAXException;

/**
 * Test the smooks mappings for StudentAssessment entity
 *
 * @author bsuzuki
 *
 */
public class StudentAssessmentEntityTest {

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

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiStudentAssessmentXml);

        checkValidStudentAssessmentNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("rawtypes")
    private void checkValidStudentAssessmentNeutralRecord(
            NeutralRecord studentAssessmentNeutralRecord) {

        assertEquals("studentAssessmentAssociation", studentAssessmentNeutralRecord.getRecordType());
        
        assertEquals("2013-11-11", studentAssessmentNeutralRecord
                .getAttributes().get("administrationDate"));
        assertEquals("2013-08-07", studentAssessmentNeutralRecord
                .getAttributes().get("administrationEndDate"));
        assertEquals("231101422", studentAssessmentNeutralRecord
                .getAttributes().get("serialNumber"));
        assertEquals("Malay", studentAssessmentNeutralRecord.getAttributes()
                .get("administrationLanguage"));
        assertEquals("School", studentAssessmentNeutralRecord.getAttributes()
                .get("administrationEnvironment"));

        List specialAccommodationsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("specialAccommodations");
        assertEquals("Colored lenses", specialAccommodationsList.get(0));

        List linguisticAccommodationsList = (List) studentAssessmentNeutralRecord
                .getAttributes().get("linguisticAccommodations");
        assertEquals("Oral Translation - Word or Phrase",
                linguisticAccommodationsList.get(0));

        assertEquals("2nd Retest", studentAssessmentNeutralRecord.getAttributes()
                .get("retestIndicator"));
        assertEquals("Medical waiver", studentAssessmentNeutralRecord.getAttributes()
                .get("reasonNotTested"));

        List scoreResultsList = (List) studentAssessmentNeutralRecord.getAttributes().get(
                "scoreResults");
        Map scoreResultMap = (Map) scoreResultsList.get(0);
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "assessmentReportingMethod", "Workplace readiness score");
        EntityTestUtils.assertObjectInMapEquals(scoreResultMap, "result", "uDcDPPMbzwXnlNsazojAEF6R8LIME6");

        assertEquals("Eleventh grade", studentAssessmentNeutralRecord.getAttributes()
                .get("gradeLevelWhenAssessed"));

        List performanceLevelsList = (List) studentAssessmentNeutralRecord.getAttributes().get(
                "performanceLevelDescriptors");
        List performanceLevelDescriptorTypeList = (List) performanceLevelsList.get(0);
        EntityTestUtils.assertObjectInMapEquals((Map) performanceLevelDescriptorTypeList.get(0), "codeValue", "KYn6axx9pJEX");
        EntityTestUtils.assertObjectInMapEquals((Map) performanceLevelDescriptorTypeList.get(1), "description", "bn");

        assertEquals("Yjmyw", studentAssessmentNeutralRecord
                .getAttributes().get("studentId"));

    }
}
