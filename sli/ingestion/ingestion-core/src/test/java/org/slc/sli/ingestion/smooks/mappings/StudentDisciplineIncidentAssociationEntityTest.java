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

        @SuppressWarnings("unchecked")
        Map<String, String> disciplineIncidentReference = (Map<String, String>) attributes.get("DisciplineIncidentReference");
        assertNotNull("Expected non-null DisciplineIncidentReference", disciplineIncidentReference);
        assertEquals("Expected different ref", "waterboard", disciplineIncidentReference.get("ref"));
        //assertEquals("Expected different id", "", disciplineIncidentReference("id"));

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
        @SuppressWarnings("unchecked")
        Map<String, Object> studentReference = (Map<String, Object>) attributes.get("StudentReference");
        assertNotNull("Expected non-null student reference", studentReference);

        @SuppressWarnings("unchecked")
        Map<String, Object> studentIdentity = (Map<String, Object>) studentReference.get("StudentIdentity");
        assertNotNull("Expected non-null student identity", studentIdentity);

        assertEquals("Expected different student unique state id", "Student Unique State Id", studentIdentity.get("StudentUniqueStateId"));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> studentIdentificationCodes = (List<Map<String, String>>) studentIdentity.get("StudentIdentificationCode");
        assertNotNull("Expected non-null student identification codes", studentIdentificationCodes);
        assertEquals("Expected two StudentIdentificationCodes", 2, studentIdentificationCodes.size());

        Map<String, String> studentIdentificationCode = studentIdentificationCodes.get(0);
        assertNotNull("Expected non-null student identification code", studentIdentificationCode);
        assertEquals("Expected different identification system", "Canadian SIN", studentIdentificationCode.get("IdentificationSystem"));
        assertEquals("Expected different AssigningOrganizationCode", "Assigning Organization Code 1", studentIdentificationCode.get("AssigningOrganizationCode"));
        assertEquals("Expected different ID", "Student Identification Code 1", studentIdentificationCode.get("ID"));

        studentIdentificationCode = studentIdentificationCodes.get(1);
        assertNotNull("Expected non-null student identification code", studentIdentificationCode);
        assertEquals("Expected different identification system", "Medicaid", studentIdentificationCode.get("IdentificationSystem"));
        assertEquals("Expected different AssigningOrganizationCode", "Assigning Organization Code 2", studentIdentificationCode.get("AssigningOrganizationCode"));
        assertEquals("Expected different ID", "Student Identification Code 2", studentIdentificationCode.get("ID"));

        @SuppressWarnings("unchecked")
        Map<String, String> name = (Map<String, String>) studentIdentity.get("Name");
        assertNotNull("Expected non-null name", name);
        assertEquals("Expected different Verification", "Baptismal or church certificate", name.get("Verification"));
        assertEquals("Expected different PersonalTitlePrefix", "Colonel", name.get("PersonalTitlePrefix"));
        assertEquals("Expected different FirstName", "FN1", name.get("FirstName"));
        assertEquals("Expected different MiddleName", "MN1", name.get("MiddleName"));
        assertEquals("Expected different LastSurname", "LN1", name.get("LastSurname"));
        assertEquals("Expected different GenerationCodeSuffix", "Jr", name.get("GenerationCodeSuffix"));
        assertEquals("Expected different MaidenName", "Nee", name.get("MaidenName"));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> otherNames = (List<Map<String, String>>) studentIdentity.get("OtherName");
        assertNotNull("Expected non-null other names", otherNames);
        assertEquals("Expected two OtherNames", 2, otherNames.size());

        Map<String, String> otherName = otherNames.get(0);
        assertNotNull("Expected non-null OtherName", otherName);
        assertEquals("Expected different OtherNameType", "Previous Legal Name", otherName.get("OtherNameType"));
        assertEquals("Expected different PersonalTitlePrefix", "Sister", otherName.get("PersonalTitlePrefix"));
        assertEquals("Expected different FirstName", "FN2", otherName.get("FirstName"));
        assertEquals("Expected different MiddleName", "MN2", otherName.get("MiddleName"));
        assertEquals("Expected different LastSurname", "LN2", otherName.get("LastSurname"));
        assertEquals("Expected different GenerationCodeSuffix", "Sr", otherName.get("GenerationCodeSuffix"));

        otherName = otherNames.get(1);
        assertNotNull("Expected non-null OtherName", otherName);
        assertEquals("Expected different OtherNameType", "Alias", otherName.get("OtherNameType"));
        assertEquals("Expected different PersonalTitlePrefix", "Sr", otherName.get("PersonalTitlePrefix"));
        assertEquals("Expected different FirstName", "FN3", otherName.get("FirstName"));
        assertEquals("Expected different MiddleName", "MN3", otherName.get("MiddleName"));
        assertEquals("Expected different LastSurname", "LN3", otherName.get("LastSurname"));
        assertEquals("Expected different GenerationCodeSuffix", "VIII", otherName.get("GenerationCodeSuffix"));

        assertEquals("Expected different sex", "Female", studentIdentity.get("Sex"));
        assertEquals("Expected different birth date", "05-27-1980", studentIdentity.get("BirthDate"));
        assertEquals("Expected different hispanic latino ethnicity", false, studentIdentity.get("HispanicLatinoEthnicity"));

        @SuppressWarnings("unchecked")
        Map<String, List<String>> race = (Map<String, List<String>>) studentIdentity.get("Race");
        assertNotNull("Expected non-null race", race);

        List<String> racialCategories = race.get("RacialCategory");
        assertNotNull("Expected non-null RacialCategories", racialCategories);
        assertEquals("Expected two RacialCategories", 2, racialCategories.size());
        assertEquals("Expected different RacialCategory", "American Indian - Alaskan Native", racialCategories.get(0));
        assertEquals("Expected different RacialCategory", "Native Hawaiian - Pacific Islander", racialCategories.get(1));
    }
}