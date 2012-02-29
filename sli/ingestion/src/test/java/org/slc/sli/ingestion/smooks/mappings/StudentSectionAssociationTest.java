package org.slc.sli.ingestion.smooks.mappings;

import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.ingestion.NeutralRecord;
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

    @InjectMocks
    @Autowired
    private EntityValidator validator;

    @Mock
    private EntityRepository mockRepository;

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
            + " <RepeatIdentifier>Not repeated</RepeatIdentifier>"
            + "</StudentSectionAssociation></InterchangeStudentEnrollment>";

    String invalidXMLTestData = "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentSectionAssociation>"
            + "<SectionReference>"
            + "<SectionIdentity>"
            + "<UniqueSectionCode>MT100</UniqueSectionCode>"
            + "</SectionIdentity>"
            + "</SectionReference>"
            + " <BeginDate>2009-09-15</BeginDate>"
            + " <HomeroomIndicator>false</HomeroomIndicator>"
            + "</StudentSectionAssociation></InterchangeStudentEnrollment>";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidSectionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-studentSectionAssociation-csv.xml";
        String targetSelector = "csv-record";

        String testData = "111220001,MT100,2009-09-15,2010-06-02,false,Not repeated";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, testData);
        checkValidSectionNeutralRecord(record);
    }

    @Test
    public void testValidatorSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSectionAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);

        // mock repository will simulate "finding" the referenced educationOrganization
        Entity returnEntity = mock(Entity.class);
        Mockito.when(mockRepository.find("section", "MT100")).thenReturn(returnEntity);
        Mockito.when(mockRepository.find("student", "111220001")).thenReturn(returnEntity);

        EntityTestUtils.mapValidation(record.getAttributes(), "studentSectionAssociation", validator);
    }

    @Test
    public void testInValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSectionAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXMLTestData);
        checkInValidSectionNeutralRecord(record);
    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSectionAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData);
        checkValidSectionNeutralRecord(record);
    }

    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        Assert.assertEquals("111220001", entity.get("studentId"));
        Assert.assertEquals("MT100", entity.get("sectionId"));
        Assert.assertEquals("2009-09-15", entity.get("beginDate"));
        Assert.assertEquals("2010-06-02", entity.get("endDate"));
        Assert.assertEquals("false", entity.get("homeroomIndicator").toString());
        Assert.assertEquals("Not repeated", entity.get("repeatIdentifier"));
    }

    private void checkInValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        Assert.assertEquals(null, entity.get("studentId"));
        Assert.assertEquals("MT100", entity.get("sectionId"));
        Assert.assertEquals("2009-09-15", entity.get("beginDate"));
        Assert.assertEquals(null, entity.get("endDate"));
        Assert.assertEquals("false", entity.get("homeroomIndicator").toString());
    }
}
