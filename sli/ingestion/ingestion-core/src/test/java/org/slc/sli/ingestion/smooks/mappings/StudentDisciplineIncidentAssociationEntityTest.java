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
 * Test the smooks mappings for Program entity.
 * 
 * @author vmcglaughlin
 *
 */
public class StudentDisciplineIncidentAssociationEntityTest {

    /**
     * Test that Ed-Fi program is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentDiscipline/StudentDisciplineIncidentAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StudentDisciplineIncidentAssociationEntity.xml");
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
        assertEquals("Expecting different record type", "studentDisciplineIncidentAssociation", neutralRecord.getRecordType());

        Map<String, Object> attributes = neutralRecord.getAttributes();

        assertEquals("Expected different number of attributes", 5, attributes.size());

        assertEquals("Expected different StudentParticipationCode", "Perpetrator", attributes.get("StudentParticipationCode"));

        String disciplineIncidentReference = (String) attributes.get("disciplineIncidentIdentifier");
        assertNotNull("Expected non-null DisciplineIncidentReference", disciplineIncidentReference);
        assertEquals("Expected different ref", "waterboard", disciplineIncidentReference);
        
        //behaviors
        @SuppressWarnings("unchecked")
        List<List<Map<String, Object>>> behaviors = (List<List<Map<String, Object>>>) attributes.get("Behaviors");
        assertNotNull("Expected non-null list of behaviors", behaviors);
        assertEquals("Expected two behaviors", 2, behaviors.size());

        List<Map<String, Object>> behavior = behaviors.get(0);
        assertNotNull("Expected non-null behavior", behavior);
        assertEquals("Expected three choices", 3, behavior.size());

        Map<String, Object> choice = behavior.get(0);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different behavior code value", "Code Value 1", choice.get("CodeValue"));

        choice = behavior.get(1);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different short description of behavior", "Short Description 1", choice.get("ShortDescription"));

        choice = behavior.get(2);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different description of behavior", "Description 1", choice.get("Description"));

        behavior = behaviors.get(1);
        assertNotNull("Expected non-null behavior", behavior);
        assertEquals("Expected three choices", 3, behavior.size());

        choice = behavior.get(0);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different behavior code value", "Code Value 2", choice.get("CodeValue"));

        choice = behavior.get(1);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different short description of behavior", "Short Description 2", choice.get("ShortDescription"));

        choice = behavior.get(2);
        assertNotNull("Expected non-null behavior choice", choice);
        assertEquals("Expected different description of behavior", "Description 2", choice.get("Description"));

        //secondary behaviors
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> secondaryBehaviors = (List<Map<String, Object>>) attributes.get("SecondaryBehaviors");
        assertNotNull("Expected non-null list of secondaryBehaviors", secondaryBehaviors);
        assertEquals("Expected two secondaryBehaviors", 2, secondaryBehaviors.size());

        Map<String, Object> secondaryBehavior = secondaryBehaviors.get(0);
        assertNotNull("Expected non-null secondaryBehavior", secondaryBehavior);
        assertEquals("Expected different secondaryBehavior category", "School Code of Conduct", secondaryBehavior.get("BehaviorCategory"));
        assertEquals("Expected different secondaryBehavior", "Mischief", secondaryBehavior.get("SecondaryBehavior"));

        secondaryBehavior = secondaryBehaviors.get(1);
        assertNotNull("Expected non-null secondaryBehavior", secondaryBehavior);
        assertEquals("Expected different secondaryBehavior category", "State Law Crime", secondaryBehavior.get("BehaviorCategory"));
        assertEquals("Expected different secondaryBehavior", "Hair Pulling", secondaryBehavior.get("SecondaryBehavior"));

        //student
        String studentIdentity = (String) attributes.get("studentUniqueStateId");
        assertNotNull("Expected non-null student reference", studentIdentity);
        assertEquals("Expected different student unique state id", "Student Unique State Id", studentIdentity);
    }
}