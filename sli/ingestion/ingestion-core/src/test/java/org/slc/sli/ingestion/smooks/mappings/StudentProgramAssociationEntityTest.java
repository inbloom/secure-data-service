package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
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
    @Ignore
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

    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "studentProgramAssociation", neutralRecord.getRecordType());

        assertEquals("Expected different local id", "", neutralRecord.getLocalId());
        assertEquals("Expected # local parent ids", 0, neutralRecord.getLocalParentIds().size());

        //TODO check parent ids

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different entity id", "", attributes.get("staffProgramAssociationId"));
        assertEquals("Expected different student id", "100000000", attributes.get("studentId"));
        assertEquals("Expected different program id", "ACC-TEST-PROG-1", attributes.get("programId"));
        assertEquals("Expected different education organization id", "IL", attributes.get("educationOrganizationId"));

        assertEquals("Expected different begin date", "2011-01-01", attributes.get("beginDate"));
        assertEquals("Expected different end date", "2011-12-31", attributes.get("endDate"));
        assertEquals("Expected different reason exited", "Program completion", attributes.get("reasonExited"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> services = (List<Map<String, Object>>) attributes.get("services");

        assertNotNull("Expected non-null list of services", services);

        assertEquals("Expected two services", 2, services.size());

        Map<String, Object> service1 = services.get(0);
        assertNotNull("Expected non-null service", service1);
        assertEquals("Expected different short description of service", "Short description for acceptance test staffProgramAssociation service 1", service1.get("shortDescription"));
        assertEquals("Expected different description of service", "This is a longer description of the services provided by acceptance test staffProgramAssociation service 1. More detail could be provided here.", service1.get("description"));
        assertEquals("Expected different service code value", "Test service 1", service1.get("codeValue"));

        Map<String, Object> service2 = services.get(1);
        assertNotNull("Expected non-null service", service2);
        assertEquals("Expected different short description of service", "Short description for acceptance test staffProgramAssociation service 2", service2.get("shortDescription"));
        assertEquals("Expected different description of service", "This is a longer description of the services provided by acceptance test staffProgramAssociation service 2. More detail could be provided here.", service2.get("description"));
        assertEquals("Expected different service code value", "Test service 2", service2.get("codeValue"));
    }

}
