package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Test the smooks mappings for StudentProgramAssociation entity.
 *
 * @author vmcglaughlin
 *
 */
public class StudentProgramAssociationEntityTest {

    /**
     * Test that Ed-Fi studentProgramAssociation is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlProgramToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentProgram/StudentProgramAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StudentProgramAssociationEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils
                .smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                        targetSelector, edfiXml);

        checkValidNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "studentProgramAssociation", neutralRecord.getRecordType());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 7, attributes.size());

        Map<String, Object> studentReference = (Map<String, Object>) attributes.get("StudentReference");
        assertNotNull("Expected non-null student reference", studentReference);
        Map<String, Object> studentIdentity = (Map<String, Object>) studentReference.get("StudentIdentity");
        assertNotNull("Expected non-null student identity", studentIdentity);
        assertEquals("Expected different student id", "100000000", studentIdentity.get("StudentUniqueStateId"));

        Map<String, Object> programReference = (Map<String, Object>) attributes.get("ProgramReference");
        assertNotNull("Expected non-null student reference", studentReference);
        Map<String, Object> programIdentity = (Map<String, Object>) programReference.get("ProgramIdentity");
        assertNotNull("Expected non-null program identity", programIdentity);
        assertEquals("Expected different program id", "ACC-TEST-PROG-1", programIdentity.get("ProgramId"));

        Map<String, Object> educationOrganizationReference =
                (Map<String, Object>) attributes.get("EducationOrganizationReference");
        assertNotNull("Expected non-null education organization reference", educationOrganizationReference);
        Map<String, Object> educationalOrgIdentity =
                (Map<String, Object>) educationOrganizationReference.get("EducationalOrgIdentity");
        assertNotNull("Expected non-null educational org identity", educationalOrgIdentity);
        List<String> stateOrganizationIds = (List<String>) educationalOrgIdentity.get("StateOrganizationId");
        assertNotNull("Expected non-null list of state organization ids", stateOrganizationIds);
        assertEquals("Expected different education organization id", "IL", stateOrganizationIds.get(0));

        assertEquals("Expected different begin date", "2011-01-01", attributes.get("BeginDate"));
        assertEquals("Expected different end date", "2011-12-31", attributes.get("EndDate"));
        assertEquals("Expected different reason exited", "Program completion", attributes.get("ReasonExited"));

        List<Map<String, Object>> services = (List<Map<String, Object>>) attributes.get("Services");

        assertNotNull("Expected non-null list of services", services);

        assertEquals("Expected two services", 2, services.size());

        Map<String, Object> service1 = services.get(0);
        assertNotNull("Expected non-null service", service1);
        assertEquals("Expected different short description of service",
                "Short description for acceptance test studentProgramAssociation service 1",
                service1.get("ShortDescription"));
        assertEquals("Expected different description of service",
                "This is a longer description of the services provided by acceptance test studentProgramAssociation service 1. More detail could be provided here.",
                service1.get("Description"));
        assertEquals("Expected different service code value", "Test service 1", service1.get("CodeValue"));

        Map<String, Object> service2 = services.get(1);
        assertNotNull("Expected non-null service", service2);
        assertEquals("Expected different short description of service",
                "Short description for acceptance test studentProgramAssociation service 2",
                service2.get("ShortDescription"));
        assertEquals("Expected different description of service",
                "This is a longer description of the services provided by acceptance test studentProgramAssociation service 2. More detail could be provided here.",
                service2.get("Description"));
        assertEquals("Expected different service code value", "Test service 2", service2.get("CodeValue"));
    }

}
