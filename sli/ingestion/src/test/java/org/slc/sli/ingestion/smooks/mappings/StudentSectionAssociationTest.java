package org.slc.sli.ingestion.smooks.mappings;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidator;

/**
 *
 * @author mpatel
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentSectionAssociationTest {

    @Autowired
    private EntityValidator validator;

    String xmlTestData = "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentSectionAssociation><SectionReference><SectionIdentity>"
            + "<UniqueSectionCode>MT100</UniqueSectionCode>"
            + "</SectionIdentity></SectionReference><StudentReference>"
            + "<StudentIdentity>"
            + "<StudentUniqueStateId>111220001</StudentUniqueStateId>"
            + "</StudentIdentity>"
            + "</StudentReference>"
            + " <SectionReference>"
            + " </SectionReference>"
            + " <BeginDate>2009-09-15</BeginDate>"
            + " <EndDate>2010-06-02</EndDate>"
            + " <HomeroomIndicator>false</HomeroomIndicator>"
            + " <RepeatIdentifier>Not Repeated</RepeatIdentifier>"
            + "</StudentSectionAssociation></InterchangeStudentEnrollment>";

    @Test
    public void testValidSectionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-studentSectionAssociation-csv.xml";
        String targetSelector = "csv-record";

        String testData = "111220001,MT100,2009-09-15,2010-06-02,false,Not Repeated";

        ByteArrayInputStream testInput = new ByteArrayInputStream(testData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            // Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            checkValidSectionNeutralRecord(record);
        } finally {
            nrfr.close();
        }
    }

    @Test
    public void testValidatorSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSectionAssociation";

        ByteArrayInputStream testInput = new ByteArrayInputStream(xmlTestData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);

            // Tests that the NeutralRecord was created
            Assert.assertTrue(nrfr.hasNext());

            NeutralRecord record = nrfr.next();
            EntityTestUtils.mapValidation(record.getAttributes(), "studentSectionAssociation", validator);

        } finally {
            nrfr.close();
        }

    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSectionAssociation";

        ByteArrayInputStream testInput = new ByteArrayInputStream(xmlTestData.getBytes());
        NeutralRecordFileReader nrfr = null;
        try {
            nrfr = EntityTestUtils.getNeutralRecords(testInput, smooksConfig, targetSelector);
            // Tests that the NeutralRecords were created
            Assert.assertTrue(nrfr.hasNext());
            NeutralRecord record = nrfr.next();
            checkValidSectionNeutralRecord(record);
        } finally {
            nrfr.close();
        }
    }

    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        Assert.assertEquals("111220001", entity.get("studentId"));
        Assert.assertEquals("MT100", entity.get("sectionId"));
        Assert.assertEquals("2009-09-15", entity.get("beginDate"));
        Assert.assertEquals("2010-06-02", entity.get("endDate"));
        Assert.assertEquals("false", entity.get("homeroomIndicator").toString());
        Assert.assertEquals("Not Repeated", entity.get("repeatIdentifier"));
    }
}
