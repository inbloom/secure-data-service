package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.FileTypeValidator;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;

/**
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SessionEntityTest {

    @Autowired
    private EntityValidator validator;

    private TestErrorReport errorReport = new TestErrorReport();

    private LocalFileSystemLandingZone landingZone = new LocalFileSystemLandingZone();

    private FileTypeValidator fileTypeValidator = new FileTypeValidator();

    private String validXmlTestData = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
            + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
            + "<SessionName>2012 Spring</SessionName>"
            + "<SchoolYear>2011-2012</SchoolYear>"
            + "<Term>Spring Semester</Term>"
            + "<BeginDate>2012-01-02</BeginDate>"
            + "<EndDate>2012-06-22</EndDate>"
            + "<TotalInstructionalDays>118</TotalInstructionalDays>" + "</Session>" + "</InterchangeEducationOrgCalendar>";

    @Test
    public void testValidSession() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertTrue(validator.validate(e));
    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingSessionName() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        String invalidXmlMissingSessionName = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
                + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Session> "
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSessionName);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertFalse(validator.validate(e));

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingSchoolYear() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        String invalidXmlMissingSchoolYear = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
                + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSchoolYear);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertFalse(validator.validate(e));

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingTerm() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        String invalidXmlMissingTerm = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
                + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingTerm);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertFalse(validator.validate(e));

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingBeginDate() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        String invalidXmlMissingBeginDate = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
                + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingBeginDate);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertFalse(validator.validate(e));

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingEndDate() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        String invalidXmlMissingEndDate = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
                + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingEndDate);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertFalse(validator.validate(e));

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingTotalInstructionalDays() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        String invalidXmlMissingTotalInstructionalDays = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
                + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>" + "</Session>" + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingTotalInstructionalDays);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertFalse(validator.validate(e));

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionIncorrectEnum() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        String invalidXmlIncorrectEnum = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
                + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Winter Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
                + "</Session>" + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlIncorrectEnum);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        assertFalse(validator.validate(e));

    }

    @Test
    public void testValidSessionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-session-csv.xml";
        String targetSelector = "csv-record";

        String csvTestData = "2012 Spring,2011-2012,Spring Semester,2012-01-02,2012-06-22,118";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData);

        checkValidSessionNeutralRecord(neutralRecord);

    }

    @Test
    public void testInvalidSessionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-session-csv.xml";
        String targetSelector = "csv-record";

        String csvTestData = "2012 Spring,2011-2012,Winter Semester,2012-01-02,2012-06-22,118";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        try {
        validator.validate(e);
        } catch (EntityValidationException ex) {
            assertEquals(ex.getEntityType(), "session");
            assertFalse(ex.getValidationErrors().isEmpty());
        }

    }

    @Test
    public void testValidSessionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData);

        checkValidSessionNeutralRecord(neutralRecord);

    }

    private void checkValidSessionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        assertEquals("2012 Spring", entity.get("sessionName"));
        assertEquals("2011-2012", entity.get("schoolYear"));
        assertEquals("Spring Semester", entity.get("term"));
        assertEquals("2012-01-02", entity.get("beginDate"));
        assertEquals("2012-06-22", entity.get("endDate"));
        assertEquals("118", entity.get("totalInstructionalDays").toString());

    }

    @Test
    public void testValidSessionCSVFileType() throws Exception {
        // Create a valid CSV file of type Session.
        FileEntryDescriptor entry = new FileEntryDescriptor(new IngestionFileEntry(FileFormat.CSV,
                FileType.CSV_SESSION, "Dummy.csv", ""), landingZone);

        // Now validate that the file is indeed of type Session.
        assertTrue(fileTypeValidator.isValid(entry, errorReport));
    }

}
