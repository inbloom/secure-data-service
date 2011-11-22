package org.slc.sli.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.AdministrativeFundingControlType;
import org.slc.sli.domain.enums.CharterStatusType;
import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.ExitWithdrawalType;
import org.slc.sli.domain.enums.GradeLevelType;
import org.slc.sli.domain.enums.LimitedEnglishProficiencyType;
import org.slc.sli.domain.enums.MagnetSpecialProgramEmphasisSchoolType;
import org.slc.sli.domain.enums.OldEthnicityType;
import org.slc.sli.domain.enums.OperationalStatusType;
import org.slc.sli.domain.enums.SchoolFoodServicesEligibilityType;
import org.slc.sli.domain.enums.SchoolType;
import org.slc.sli.domain.enums.SexType;
import org.slc.sli.domain.enums.TitleIPartASchoolDesignationType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/db.xml"})
public class StudentSchoolAssociationRepositoryTest {
    
    @Autowired
    StudentSchoolAssociationRepository studentSchoolRepo;
    
    @Autowired
    StudentRepository studentRepo;
    
    @Autowired
    SchoolRepository schoolRepo;
    
    @Test
    public void testAddAssociation() {
        StudentSchoolAssociation test = createTestAssociation();
        studentSchoolRepo.deleteAll();
        test = studentSchoolRepo.save(test);
        StudentSchoolAssociation found = studentSchoolRepo.findOne(test.getAssociationId());
        assertEquals(test, found);
    }
    
    @Test
    public void testUpdateAssociation() {
        StudentSchoolAssociation test = createTestAssociation();
        studentSchoolRepo.deleteAll();
        test = studentSchoolRepo.save(test);
        Integer origId = test.getAssociationId();
        test.setEntryType(EntryType.TRANSFER_PUBLIC_DIFFERENT_STATE);
        test = studentSchoolRepo.save(test);
        
        StudentSchoolAssociation found = studentSchoolRepo.findOne(origId);
        assertNotNull(found);
        assertEquals(EntryType.TRANSFER_PUBLIC_DIFFERENT_STATE, found.getEntryType());
    }
    
    @Test
    public void testFindByStudentIdAndSchoolId() {
        studentSchoolRepo.deleteAll();
        studentSchoolRepo.save(createTestAssociation());
        studentSchoolRepo.save(createTestAssociation());
        studentSchoolRepo.save(createTestAssociation());
        StudentSchoolAssociation modified = createTestAssociation();
        modified.setStudentId(999);
        studentSchoolRepo.save(modified);
        assertEquals(4, iterableSize(studentSchoolRepo.findAll()));
        Iterable<StudentSchoolAssociation> associations = studentSchoolRepo.findByStudentIdAndSchoolId(20, 10);
        assertEquals(3, iterableSize(associations));
    }
    
    @Test
    public void testFindByStudentId() {
        studentSchoolRepo.deleteAll();
        studentSchoolRepo.save(createTestAssociation());
        studentSchoolRepo.save(createTestAssociation());
        studentSchoolRepo.save(createTestAssociation());
        StudentSchoolAssociation modified = createTestAssociation();
        modified.setStudentId(999);
        studentSchoolRepo.save(modified);
        
        assertEquals(3, iterableSize(studentSchoolRepo.findByStudentId(20)));
        assertEquals(1, iterableSize(studentSchoolRepo.findByStudentId(999)));
    }
    
    @Test
    public void testFindBySchoolId() {
        studentSchoolRepo.deleteAll();
        studentSchoolRepo.save(createTestAssociation());
        studentSchoolRepo.save(createTestAssociation());
        studentSchoolRepo.save(createTestAssociation());
        StudentSchoolAssociation modified = createTestAssociation();
        modified.setSchoolId(999);
        studentSchoolRepo.save(modified);
        
        assertEquals(3, iterableSize(studentSchoolRepo.findBySchoolId(10)));
        assertEquals(1, iterableSize(studentSchoolRepo.findBySchoolId(999)));
    }
    
    @Test
    public void testMinimal() {
        StudentSchoolAssociation s = new StudentSchoolAssociation();
        int id = studentSchoolRepo.save(s).getAssociationId();
        s.setAssociationId(id);
        StudentSchoolAssociation returned = studentSchoolRepo.findOne(id);
        assertEquals(s.toString(), returned.toString());
        
    }
    
    @Test
    public void testFull() {
        StudentSchoolAssociation s = new StudentSchoolAssociation();
        Calendar classOf = Calendar.getInstance();
        classOf.set(Calendar.YEAR, 2000);
        s.setClassOf(classOf); // TODO, edfi only lists this as a year, so why are we storing a full
                               // calendar?
        s.setEntryDate(Calendar.getInstance());
        s.setEntryGradeLevel(GradeLevelType.UNGRADED);
        s.setEntryType(EntryType.REENTRY_INVOLUNTARY_WITHDRAWAL);
        s.setExitWithdrawDate(Calendar.getInstance());
        s.setExitWithdrawType(ExitWithdrawalType.ILLNESS);
        s.setRepeatedGrade(true);
        s.setSchoolChoiceTransfer(true);
        s.setSchoolId(10);
        s.setStudentId(100);
        int id = studentSchoolRepo.save(s).getAssociationId();
        s.setAssociationId(id);
        StudentSchoolAssociation returned = studentSchoolRepo.findOne(id);
        assertEquals(s.toString(), returned.toString());
    }
    
 // test DataAccessException thrown by saveWithAssoc if no student or school exist   
    @Test(expected = DataAccessException.class)
    public void testSaveWithAssocException() {
        StudentSchoolAssociation ssa = createTestAssociation();
        ssa.setSchoolId(-1);
        studentSchoolRepo.saveWithAssoc(ssa);
    }
    
    @Test
    public void testSaveWithAssoc() {
        
        // test save
        Student student = buildTestStudent();
        student = studentRepo.save(student);
        School school = createTestSchool();
        school = schoolRepo.save(school);
        StudentSchoolAssociation ssa = createTestAssociation();
        ssa.setSchoolId(school.getSchoolId());
        ssa.setStudentId(student.getStudentId()); 
        ssa = studentSchoolRepo.saveWithAssoc(ssa);
        assertNotNull(ssa);
        
        // test update
        ssa.setEntryType(EntryType.TRANSFER_PUBLIC_DIFFERENT_STATE);
        studentSchoolRepo.saveWithAssoc(ssa);
        Integer assocId = ssa.getAssociationId();
        StudentSchoolAssociation found = studentSchoolRepo.findOne(assocId);
        assertNotNull(found);
        assertEquals(EntryType.TRANSFER_PUBLIC_DIFFERENT_STATE, found.getEntryType());
        
    }
    
    @SuppressWarnings("unused")
    private static <T> int iterableSize(Iterable<T> it) {
        int count = 0;
        for (T i : it) {
            count++;
        }
        return count;
    }
    
    protected static StudentSchoolAssociation createTestAssociation() {
        StudentSchoolAssociation association = new StudentSchoolAssociation();
        association.setSchoolId(10);
        association.setStudentId(20);
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
    
    private Student buildTestStudent() {
        Student student = new Student();
        student.setFirstName("Jane");
        student.setLastSurname("Doe");
        Date birthDate = new Timestamp(23234000);
        student.setBirthDate(birthDate);
        student.setCityOfBirth("Chicago");
        student.setCountryOfBirth("US");
        student.setDateEnteredUs(birthDate);
        student.setDisplacementStatus("some");
        student.setEconomicDisadvantaged(true);
        student.setGenerationCodeSuffix("Z");
        student.setHispanicLatinoEthnicity(true);
        student.setLimitedEnglishProficiency(LimitedEnglishProficiencyType.YES);
        student.setMaidenName("Smith");
        student.setMiddleName("Patricia");
        student.setMultipleBirthStatus(true);
        student.setOldEthnicity(OldEthnicityType.AMERICAN_INDIAN_OR_ALASKAN_NATIVE);
        student.setPersonalInformationVerification("verified");
        student.setPersonalTitlePrefix("Miss");
        student.setProfileThumbnail("doej23.png");
        student.setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType.REDUCED_PRICE);
        student.setSex(SexType.Female);
        student.setStateOfBirthAbbreviation("IL");
        student.setStudentSchoolId("DOE-JANE-222");
        
        return student;
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
    
}
