package org.slc.sli.ingestion.smooks.mappings;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;

/**
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SessionEntityTest {

    @Autowired
    private EntityValidator validator;

    String validXmlTestData = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
        + "<Session> "
            + "<SessionName>2012 Spring</SessionName>"
            + "<SchoolYear>2011-2012</SchoolYear>"
            + "<Term>Spring Semester</Term>"
            + "<BeginDate>2012-01-02</BeginDate>"
            + "<EndDate>2012-06-22</EndDate>"
            + "<TotalInstructionalDays>118</TotalInstructionalDays>"
        + "</Session>"
    + "</InterchangeMasterSchedule>";

    @Ignore
    @Test
    public void testValidSession() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        Assert.assertTrue(validator.validate(e));
    }

    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingSessionName() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        String invalidXmlMissingSessionName = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
            + "</Session>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSessionName);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        validator.validate(e);

    }

    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingSchoolYear() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        String invalidXmlMissingSchoolYear = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
            + "</Session>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSchoolYear);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        validator.validate(e);

    }

    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingTerm() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        String invalidXmlMissingTerm = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
            + "</Session>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingTerm);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        validator.validate(e);

    }

    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingBeginDate() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        String invalidXmlMissingBeginDate = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
            + "</Session>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingBeginDate);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        validator.validate(e);

    }

    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingEndDate() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        String invalidXmlMissingEndDate = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
            + "</Session>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingEndDate);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        validator.validate(e);

    }

    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionMissingTotalInstructionalDays() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        String invalidXmlMissingTotalInstructionalDays = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Spring Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
            + "</Session>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingTotalInstructionalDays);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        validator.validate(e);

    }

    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidSessionIncorrectEnum() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        String invalidXmlIncorrectEnum = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
                + "<SessionName>2012 Spring</SessionName>"
                + "<SchoolYear>2011-2012</SchoolYear>"
                + "<Term>Winter Semester</Term>"
                + "<BeginDate>2012-01-02</BeginDate>"
                + "<EndDate>2012-06-22</EndDate>"
                + "<TotalInstructionalDays>118</TotalInstructionalDays>"
            + "</Session>"
        + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlIncorrectEnum);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("session");

        validator.validate(e);

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
    public void testValidSessionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Session";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData);

        checkValidSessionNeutralRecord(neutralRecord);

    }

    private void checkValidSessionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        Assert.assertEquals("2012 Spring", entity.get("sessionName"));
        Assert.assertEquals("2011-2012", entity.get("schoolYear"));
        Assert.assertEquals("Spring Semester", entity.get("term"));
        Assert.assertEquals("2012-01-02", entity.get("beginDate"));
        Assert.assertEquals("2012-06-22", entity.get("endDate"));
        Assert.assertEquals("118", entity.get("totalInstructionalDays").toString());

    }
}
