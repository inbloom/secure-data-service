package org.slc.sli.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.School;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.AdministrativeFundingControlType;
import org.slc.sli.domain.enums.CharterStatusType;
import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.ExitWithdrawalType;
import org.slc.sli.domain.enums.GradeLevelType;
import org.slc.sli.domain.enums.MagnetSpecialProgramEmphasisSchoolType;
import org.slc.sli.domain.enums.OperationalStatusType;
import org.slc.sli.domain.enums.SchoolType;
import org.slc.sli.domain.enums.TitleIPartASchoolDesignationType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SchoolRepositoryTest {
    
    @Autowired
    SchoolRepository schoolRepo;
    
    @Autowired
    StudentSchoolAssociationRepository assocRepo;
    
    @Test
    public void testAddSchool() {
        School school = createTestSchool();
        school = schoolRepo.save(school);
        
        School s = schoolRepo.findOne(school.getSchoolId());
        assertNotNull(s);
        assertEquals(school, s);
    }
    
    @Test
    public void testUpdateSchool() {
        schoolRepo.deleteAll();
        
        School school = createTestSchool();
        School newSchool = schoolRepo.save(school);
        
        newSchool.setFullName("Changed School Name");
        
        schoolRepo.save(newSchool);
        
        School s = schoolRepo.findOne(newSchool.getSchoolId());
        assertNotNull(s);
        assertEquals(newSchool, s);
    }
    
    @Test
    public void testFindByStateId() {
        schoolRepo.deleteAll();
        School school = createTestSchool();
        school = schoolRepo.save(school);
        School school2 = createTestSchool();
        school2.setStateOrganizationId("changed");
        school2 = schoolRepo.save(school2);
        Iterable<School> schools = schoolRepo.findByStateOrganizationId(school.getStateOrganizationId());
        assertNotNull(schools);
        int count = 0;
        for (School s : schools) {
            assertEquals(school.getStateOrganizationId(), s.getStateOrganizationId());
            count++;
        }
        assertTrue(count == 1);
        schoolRepo.delete(school);
    }
    
    @Test
    public void testDeleteWithAssoc() {
        School school = createTestSchool();
        school = schoolRepo.save(school);
        assertNotNull(school);
        int schoolId = school.getSchoolId();
        StudentSchoolAssociation assoc = createTestAssociation(schoolId, 0);
        assoc = assocRepo.save(assoc);
        assertNotNull(assoc);
        int assocId = assoc.getAssociationId();
        schoolRepo.deleteWithAssoc(schoolId);
        school = schoolRepo.findOne(schoolId);
        assertNull(school);
        assoc = assocRepo.findOne(assocId);
        assertNull(assoc);
        
    }
    
    protected static School createTestSchool() {
        School school = new School();
        school.setFullName("Plymounth-Canton High School");
        school.setShortName("Canton");
        school.setCharterStatus(CharterStatusType.COLLEGE_UNIVERSITY_CHARTER);
        school.setAdministrativeFundingControl(AdministrativeFundingControlType.PRIVATE_SCHOOL);
        school.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.SOME_BUT_NOT_ALL_STUDENTS_PARTICIPATE);
        school.setOperationalStatus(OperationalStatusType.ACTIVE);
        school.setSchoolType(SchoolType.SPECIAL_EDUCATION);
        school.setStateOrganizationId("MI-ID-PCEP-001");
        school.setTitleIPartASchoolDesignation(TitleIPartASchoolDesignationType.TITLE_I_PART_A_SCHOOLWIDE_ASSISTANCE_PROGRAM_SCHOOL);
        school.setWebSite("http://pcep.pccs.k12.mi.us/");
        return school;
    }
    
    protected static StudentSchoolAssociation createTestAssociation(int schoolId, int studentId) {
        StudentSchoolAssociation association = new StudentSchoolAssociation();
        association.setSchoolId(schoolId);
        association.setStudentId(studentId);
        association.setClassOf(Calendar.getInstance());
        association.setEntryDate(Calendar.getInstance());
        association.setEntryGradeLevel(GradeLevelType.FIRST_GRADE);
        association.setEntryType(EntryType.ORIGINAL);
        association.setExitWithdrawDate(Calendar.getInstance());
        association.setExitWithdrawType(ExitWithdrawalType.GRADUATED);
        association.setRepeatedGrade(false);
        association.setSchoolChoiceTransfer(false);
        return association;
    }
    
    @Test
    public void testFull() {
        School s = new School();
        s.setAdministrativeFundingControl(AdministrativeFundingControlType.PUBLIC_SCHOOL);
        s.setCharterStatus(CharterStatusType.NOT_A_CHARTER_SCHOOL);
        s.setFullName("Springfield Elementary");
        s.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.SOME_BUT_NOT_ALL_STUDENTS_PARTICIPATE);
        s.setOperationalStatus(OperationalStatusType.CLOSED);
        s.setSchoolType(SchoolType.ALTERNATIVE);
        s.setShortName("Springfield");
        s.setStateOrganizationId("Springfield's State Organization ID");
        s.setTitleIPartASchoolDesignation(TitleIPartASchoolDesignationType.TITLE_I_PART_A_TARGETED_ASSISTANCE_SCHOOL);
        s.setWebSite("www.springfield-elem.com");
        int id = schoolRepo.save(s).getSchoolId();
        s.setSchoolId(id);
        School returnedSchool = schoolRepo.findOne(id);
        assertEquals(s.toString(), returnedSchool.toString());
    }
    
    @Test
    public void testMinimal() {
        School s = new School();
        int id = schoolRepo.save(s).getSchoolId();
        s.setSchoolId(id);
        School returnedSchool = schoolRepo.findOne(id);
        assertEquals(s.toString(), returnedSchool.toString());
    }
}
