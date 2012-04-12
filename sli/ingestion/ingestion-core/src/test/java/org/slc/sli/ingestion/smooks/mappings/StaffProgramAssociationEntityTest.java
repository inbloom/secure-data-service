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
 * Test the smooks mappings for StaffProgramAssociation entity.
 *
 * @author vmcglaughlin
 *
 */
public class StaffProgramAssociationEntityTest {

    /**
     * Test that Ed-Fi staffProgramAssociation is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Ignore
    @Test
    public final void mapEdfiXmlProgramToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentProgram/StaffProgramAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StaffProgramAssociationEntity.xml");
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
        assertEquals("Expecting different record type", "staffProgramAssociation", neutralRecord.getRecordType());

        assertEquals("Expected different local id", "", neutralRecord.getLocalId());
        assertEquals("Expected # local parent ids", 0, neutralRecord.getLocalParentIds().size());

        //TODO check parent ids

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different entity id", "", attributes.get("staffProgramAssociationId"));

        List<Map<String, Object>> staffReferences = (List<Map<String, Object>>) attributes.get("staffReference");
        assertNotNull("Expected non-null list of staff references", staffReferences);
        assertEquals("Expected 2 staff references", 2, staffReferences.size());

        Map<String, Object> staff1 = staffReferences.get(0);
        assertEquals("Expected different staff id", "linda.kim", staff1.get("staffId"));

        Map<String, Object> staff2 = staffReferences.get(1);
        assertEquals("Expected different staff id", "rbraverman", staff2.get("staffId"));

        List<Map<String, Object>> programReferences = (List<Map<String, Object>>) attributes.get("programReference");
        assertNotNull("Expected non-null list of program references", programReferences);
        assertEquals("Expected 2 program references", 2, programReferences.size());

        Map<String, Object> program1 = programReferences.get(0);
        assertEquals("Expected different program id", "ACC-TEST-PROG-1", program1.get("programId"));

        Map<String, Object> program2 = programReferences.get(1);
        assertEquals("Expected different program id", "ACC-TEST-PROG-2", program2.get("programId"));

        assertEquals("Expected different begin date", "2011-01-01", attributes.get("beginDate"));
        assertEquals("Expected different end date", "2012-02-15", attributes.get("endDate"));
        assertEquals("Expected different student record access", "true", attributes.get("studentRecordAccess"));
    }

}
