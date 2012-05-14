package org.slc.sli.ingestion.smooks.mappings;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Smooks test for StudentAssessmentItem
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentAssessmentItemTests {

    private String validXmlTestData = "<InterchangeStudentAssessment xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-InterchangeStudentAssessment.xsd\">"
            + "<StudentAssessmentItem>"
            + "  <AssessmentResponse>response-1</AssessmentResponse>"
            + "  <ResponseIndicator>Effective response</ResponseIndicator>"
            + "  <AssessmentItemResult>Correct</AssessmentItemResult>"
            + "  <RawScoreResult>100</RawScoreResult>"
            + "  <StudentTestAssessmentReference ref='sass-ref' />"
            + "  <StudentObjectiveAssessmentReference ref='soa-ref' />"
            + "  <AssessmentItemReference>"
            + "    <AssessmentItemIdentity>"
            + "      <AssessmentItemIdentificationCode>aii-code</AssessmentItemIdentificationCode>"
            + "    </AssessmentItemIdentity>"
            + "  </AssessmentItemReference>"
            + "</StudentAssessmentItem>" + "</InterchangeStudentAssessment>";


    @SuppressWarnings("unchecked")
    @Test
    public void testLearningObjectiveXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentAssessment/StudentAssessmentItem";

        NeutralRecord nr = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validXmlTestData);
        Map<String, Object> m = nr.getAttributes();
        Assert.assertEquals("response-1", m.get("assessmentResponse"));
        Assert.assertEquals("Effective response", m.get("responseIndicator"));
        Assert.assertEquals("Correct", m.get("assessmentItemResult"));
        Assert.assertEquals(100, m.get("rawScoreResult"));

        Map<String, Object> pIds = nr.getLocalParentIds();
        Assert.assertEquals("sass-ref", pIds.get("studentTestResultRef"));
        Assert.assertEquals("soa-ref", pIds.get("studentObjectiveAssessmentRef"));
        Assert.assertEquals("aii-code", pIds.get("assessmentItemIdentificatonCode"));
    }

}
