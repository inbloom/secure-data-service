package org.slc.sli.ingestion.landingzone.validation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Tests good and bad sample Avro schemas for Students.
 *
 * @author Tom Shewchuk <tshewchuk@wgen.net>
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AvroRecordValidatorTest {

    @Autowired
    private AvroRecordValidator validator;

    @SuppressWarnings("unchecked")
    @Test
    public void testValidStudent() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/student_fixture.json"));

        // Insure each record is valid.
        String student;
        int recordNumber = 0;
        while ((student = reader.readLine()) != null) {
            ObjectMapper oRead = new ObjectMapper();
            Map<String, Object> obj = oRead.readValue(student, Map.class);
            Assert.assertTrue(mapValidation((Map<String, Object>) obj.get("body"), "student", ++recordNumber));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidStudent() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/bad_student_fixture.json"));

        // Insure each record is invalid.
        String student;
        int recordNumber = 0;
        while ((student = reader.readLine()) != null) {
            ObjectMapper oRead = new ObjectMapper();
            Map<String, Object> obj = oRead.readValue(student, Map.class);
            Assert.assertFalse(mapValidation((Map<String, Object>) obj.get("body"), "student", ++recordNumber));
        }
    }

    private boolean mapValidation(Map<String, Object> obj, String schemaName, int recordNumber) {

        boolean returnVal;

        ErrorReport errorReport = new TestErrorReport();

        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setAttributes(obj);
        neutralRecord.setRecordType(schemaName);
        Entity entity = new NeutralRecordEntity(neutralRecord, recordNumber);

        returnVal = validator.isValid(entity, errorReport);

        // Insure each error message is as expected.
        if (returnVal) {
            // Record is valid; insure there are no messages.
            Assert.assertFalse(((TestErrorReport) errorReport).hasErrors());
        } else {
            // Record is invalid; insure messages are as expected.
            Assert.assertTrue(((TestErrorReport) errorReport).hasErrors());
            switch (recordNumber) {
                case 1:
                    Assert.assertEquals(2, ((TestErrorReport) errorReport).getMessages().size());
                    Assert.assertEquals("Record 1: Missing or empty field <name.lastSurname>.",
                            ((TestErrorReport) errorReport).getMessages().toArray()[0]);
                    Assert.assertEquals("Record 1: Unknown field <name.lastSurename>.", ((TestErrorReport) errorReport)
                            .getMessages().toArray()[1]);
                    break;
                case 2:
                    Assert.assertEquals(1, ((TestErrorReport) errorReport).getMessages().size());
                    Assert.assertEquals(
                            "Record 2: Enumeration mismatch for field <sex> (legal values are [Female, Male]).",
                            ((TestErrorReport) errorReport).getMessages().toArray()[0]);
                    break;
                case 3:
                    Assert.assertEquals(1, ((TestErrorReport) errorReport).getMessages().size());
                    Assert.assertEquals("Record 3: Invalid data type for field <name.firstName> (expected String).",
                            ((TestErrorReport) errorReport).getMessages().toArray()[0]);
                    break;
                default:
                    Assert.fail();
            }
        }

        return returnVal;
    }

}
