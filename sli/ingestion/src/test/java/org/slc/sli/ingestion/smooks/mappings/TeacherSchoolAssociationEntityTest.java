package org.slc.sli.ingestion.smooks.mappings;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;

/**
 * 
 * @author ablum
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TeacherSchoolAssociationEntityTest {
    @Autowired
    EntityValidator validator;
    
    String xmlTestData = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
            + "<TeacherSchoolAssociation>"
            + "<TeacherReference>"
            + "<StaffIdentity>"
            + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
            + "</StaffIdentity>"
            + "</TeacherReference>"
            + "<SchoolReference>"
            + "<EducationalOrgIdentity>"
            + "<StateOrganizationId>123456111</StateOrganizationId>"
            + "</EducationalOrgIdentity>"
            + "</SchoolReference>"
            + "<ProgramAssignment>Title I-Academic</ProgramAssignment>"
            + "<InstructionalGradeLevels>"
            + "<GradeLevel>Ungraded</GradeLevel>"
            + "<GradeLevel>Graded</GradeLevel>"
            + "</InstructionalGradeLevels>"
            + "<AcademicSubjects>"
            + "<AcademicSubject>English</AcademicSubject>"
            + "<AcademicSubject>Spanish</AcademicSubject>"
            + "</AcademicSubjects>"
            + "</TeacherSchoolAssociation>"
            + "</InterchangeStaffAssociation>";
    
    @Ignore
    @Test
    public void testValidTeacherSectionAssociation() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                xmlTestData);
        
        Entity e = mock(Entity.class);
        
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacherSchoolAssociation");
        
        Assert.assertTrue(validator.validate(e));
    }
    
    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSchoolAssociationMissingTeacherReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        String invalidXmlMissingTeacherReference = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
                + "<TeacherSchoolAssociation>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>123456111</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<ProgramAssignment>Title I-Academic</ProgramAssignment>"
                + "<InstructionalGradeLevels>"
                + "<GradeLevel>Ungraded</GradeLevel>"
                + "</InstructionalGradeLevels>"
                + "<AcademicSubjects>"
                + "<AcademicSubject>English</AcademicSubject>"
                + "</AcademicSubjects>"
                + "</TeacherSchoolAssociation>" + "</InterchangeStaffAssociation>";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingTeacherReference);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacherSchoolAssociation");
        
        validator.validate(e);
        
    }
    
    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSchoolAssociationMissingSchoolReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        String invalidXmlMissingSchoolReference = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
                + "<TeacherSchoolAssociation>"
                + "<TeacherReference>"
                + "<StaffIdentity>"
                + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<ProgramAssignment>Title I-Academic</ProgramAssignment>"
                + "<InstructionalGradeLevels>"
                + "<GradeLevel>Ungraded</GradeLevel>"
                + "</InstructionalGradeLevels>"
                + "<AcademicSubjects>"
                + "<AcademicSubject>English</AcademicSubject>"
                + "</AcademicSubjects>"
                + "</TeacherSchoolAssociation>"
                + "</InterchangeStaffAssociation>";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSchoolReference);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacherSchoolAssociation");
        
        validator.validate(e);
        
    }
    
    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSchoolAssociationMissingProgramAssignment() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        String invalidXmlMissingProgramAssignment = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
                + "<TeacherSchoolAssociation>"
                + "<TeacherReference>"
                + "<StaffIdentity>"
                + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>123456111</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<InstructionalGradeLevels>"
                + "<GradeLevel>Ungraded</GradeLevel>"
                + "</InstructionalGradeLevels>"
                + "<AcademicSubjects>"
                + "<AcademicSubject>English</AcademicSubject>"
                + "</AcademicSubjects>" + "</TeacherSchoolAssociation>" + "</InterchangeStaffAssociation>";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingProgramAssignment);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacherSchoolAssociation");
        
        validator.validate(e);
        
    }
    
    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSchoolAssociationMissingInstructionalGradeLevels() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        String invalidXmlMissingInstructionalGradeLevels = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
                + "<TeacherSchoolAssociation>"
                + "<TeacherReference>"
                + "<StaffIdentity>"
                + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>123456111</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<ProgramAssignment>Title I-Academic</ProgramAssignment>"
                + "<AcademicSubjects>"
                + "<AcademicSubject>English</AcademicSubject>"
                + "</AcademicSubjects>"
                + "</TeacherSchoolAssociation>" + "</InterchangeStaffAssociation>";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingInstructionalGradeLevels);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacherSchoolAssociation");
        
        validator.validate(e);
        
    }
    
    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSchoolAssociationMissingAcademicSubjects() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        String invalidXmlMissingAcademicSubjects = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
                + "<TeacherSchoolAssociation>"
                + "<TeacherReference>"
                + "<StaffIdentity>"
                + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>123456111</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<ProgramAssignment>Title I-Academic</ProgramAssignment>"
                + "<InstructionalGradeLevels>"
                + "<GradeLevel>Ungraded</GradeLevel>"
                + "</InstructionalGradeLevels>"
                + "</TeacherSchoolAssociation>" + "</InterchangeStaffAssociation>";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingAcademicSubjects);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacherSchoolAssociation");
        
        validator.validate(e);
        
    }
    
    @Ignore
    @Test(expected = EntityValidationException.class)
    public void testInvalidTeacherSchoolAssociationIncorrectEnum() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        String invalidXmIncorrectEnum = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
                + "<TeacherSchoolAssociation>"
                + "<TeacherReference>"
                + "<StaffIdentity>"
                + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
                + "</StaffIdentity>"
                + "</TeacherReference>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>123456111</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<ProgramAssignment>Title I-Academics</ProgramAssignment>"
                + "<InstructionalGradeLevels>"
                + "<GradeLevel>Ungraded</GradeLevel>"
                + "</InstructionalGradeLevels>"
                + "<AcademicSubjects>"
                + "<AcademicSubject>English</AcademicSubject>"
                + "</AcademicSubjects>"
                + "</TeacherSchoolAssociation>" + "</InterchangeStaffAssociation>";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmIncorrectEnum);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("teacherSchoolAssociation");
        
        validator.validate(e);
        
    }
    
    @Test
    public void testValidTeacherSchoolAssociaitionCSV() throws Exception {
        
        String smooksConfig = "smooks_conf/smooks-teacherSchoolAssociation-csv.xml";
        
        String targetSelector = "csv-record";
        
        String csvTestData = "333333332,123456111,Title I-Academic,Ungraded,English";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData);
        
        checkValidTeacherSchoolAssociationNeutralRecord(neutralRecord);
        
    }
    
    @Test
    public void testValidTeacherSchoolAssociationXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        
        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";
        
        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                xmlTestData);
        
        checkValidTeacherSchoolAssociationNeutralRecord(neutralRecord);
    }
    
    private void checkValidTeacherSchoolAssociationNeutralRecord(NeutralRecord neutralRecord) {
        
        Assert.assertEquals("RecordType not teacherSchoolAssociation", "teacherSchoolAssociation",
                neutralRecord.getRecordType());
        Map<?, ?> localParentIds = neutralRecord.getLocalParentIds();
        Assert.assertNotNull("null localParentIds map", localParentIds);
        
        Assert.assertEquals("333333332", neutralRecord.getAttributes().get("teacherId"));
        
        Assert.assertEquals("123456111", neutralRecord.getAttributes().get("schoolId"));
        
        Assert.assertEquals("Title I-Academic", neutralRecord.getAttributes().get("programAssignment"));
        
        List<?> gradeLevels = (List<?>) neutralRecord.getAttributes().get("instructionalGradeLevels");
        Assert.assertNotNull("Null gradelevel list", gradeLevels);
        // Assert.assertEquals("gradelevel list does not contain one element", 1,
        // gradelevels.size());
        Assert.assertEquals("Ungraded", gradeLevels.get(0));
        if (gradeLevels.size() > 1) {
            // TODO: remove if block when we support csv lists
            Assert.assertEquals("Graded", gradeLevels.get(1));
        }
        
        List<?> academicSubjectList = (List<?>) neutralRecord.getAttributes().get("academicSubjects");
        Assert.assertNotNull("Null academiSubject list", academicSubjectList);
        Assert.assertEquals("English", academicSubjectList.get(0));
        if (academicSubjectList.size() > 1) {
            // TODO: remove if block when we support csv lists
            Assert.assertEquals("Spanish", academicSubjectList.get(1));
        }
    }
}
