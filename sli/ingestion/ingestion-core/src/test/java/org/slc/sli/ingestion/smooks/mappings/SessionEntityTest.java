package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SessionEntityTest {

    private String validXmlTestData = "<InterchangeEducationOrgCalendar xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance"
            + "\" xsi:schemaLocation=\"Interchange-EducationOrgCalendar.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<Session> "
            + "<SessionName>2012 Spring</SessionName>"
            + "<SchoolYear>2011-2012</SchoolYear>"
            + "<Term>Spring Semester</Term>"
            + "<BeginDate>2012-01-02</BeginDate>"
            + "<EndDate>2012-06-22</EndDate>"
            + "<TotalInstructionalDays>118</TotalInstructionalDays>" 
            + "<EducationOrganizationReference>"
            + "<EducationalOrgIdentity>"
            + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
            + "</EducationalOrgIdentity>"
            + "</EducationOrganizationReference>"
            + "</Session>" 
            + "</InterchangeEducationOrgCalendar>";

    @Test
    public void testValidSession() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeEducationOrgCalendar/Session";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData);

        checkValidSessionNeutralRecord(neutralRecord);
    }

    @Test(expected = AssertionError.class)
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
                + "<EducationOrganizationReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</EducationOrganizationReference>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSessionName);

        checkValidSessionNeutralRecord(neutralRecord);
    }

    @Test(expected = AssertionError.class)
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
                + "<EducationOrganizationReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</EducationOrganizationReference>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSchoolYear);

        checkValidSessionNeutralRecord(neutralRecord);
    }

    @Test(expected = AssertionError.class)
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
                + "<EducationOrganizationReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</EducationOrganizationReference>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingTerm);

        checkValidSessionNeutralRecord(neutralRecord);
    }

    @Test(expected = AssertionError.class)
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
                + "<EducationOrganizationReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</EducationOrganizationReference>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingBeginDate);

        checkValidSessionNeutralRecord(neutralRecord);
    }

    @Test(expected = AssertionError.class)
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
                + "<EducationOrganizationReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</EducationOrganizationReference>"
                + "</Session>"
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingEndDate);

        checkValidSessionNeutralRecord(neutralRecord);
    }

    @Test(expected = NullPointerException.class)
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
                + "<EducationOrganizationReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</EducationOrganizationReference>"
                + "<EndDate>2012-06-22</EndDate>" + "</Session>" + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingTotalInstructionalDays);

        checkValidSessionNeutralRecord(neutralRecord);
    }

    @Test(expected = AssertionError.class)
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
                + "<EducationOrganizationReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>East Daybreak Junior High</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</EducationOrganizationReference>"
                + "</Session>" 
                + "</InterchangeEducationOrgCalendar>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlIncorrectEnum);

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
}
