package org.slc.sli.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.ExitWithdrawalType;
import org.slc.sli.domain.enums.GradeLevelType;
import org.slc.sli.domain.enums.LimitedEnglishProficiencyType;
import org.slc.sli.domain.enums.OldEthnicityType;
import org.slc.sli.domain.enums.SchoolFoodServicesEligibilityType;
import org.slc.sli.domain.enums.SexType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/db.xml" })
public class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentSchoolAssociationRepository assocRepo;

    @Test
    public void testAdd() {

        assertNotNull(studentRepository);

        Student student = buildTestStudent();

        student = studentRepository.save(student);

        Iterable<Student> students = studentRepository.findAll();
        Iterator<Student> iter = students.iterator();
        boolean found = false;
        while (iter.hasNext()) {
            Student next = iter.next();
            if (next.equals(student)) {
                assertEquals(student.toString(), next.toString());
                found = true;

            }
            System.out.println("Persisted student: " + next);
        }

        assertTrue(found);

    }

    @Test
    public void testFindByStudentSchoolId() {
        studentRepository.deleteAll();
        Student student = buildTestStudent();
        student = studentRepository.save(student);
        Student student2 = buildTestStudent();
        student2.setStudentSchoolId("changed");
        studentRepository.save(student2);

        Iterable<Student> students = studentRepository
                .findByStudentSchoolId(student.getStudentSchoolId());
        assertNotNull(students);
        int count = 0;
        for (Student s : students) {
            assertEquals(student.getStudentSchoolId(), s.getStudentSchoolId());
            count++;
        }
        assertTrue(count == 1);
    }

    @Test
    public void testDeleteWithAssoc() {
        Student student = buildTestStudent();
        student = studentRepository.save(student);
        assertNotNull(student);
        int studentId = student.getStudentId();
        StudentSchoolAssociation ssa = createTestAssociation(0, studentId);
        ssa = assocRepo.save(ssa);
        assertNotNull(ssa);
        int assocId = ssa.getSchoolId();
        studentRepository.deleteWithAssoc(studentId);
        student = studentRepository.findOne(studentId);
        assertNull(student);
        ssa = assocRepo.findOne(assocId);
        assertNull(ssa);

    }

    protected static StudentSchoolAssociation createTestAssociation(
            int schoolId, int studentId) {
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

    @Test
    public void testMinimal() {
        Student s = new Student();
        s.setBirthDate(new Timestamp((new Date().getTime() / 1000) * 1000));
        s.setSex(SexType.Male);
        s.setFirstName("John");
        s.setLastSurname("Doe");
        s.setStudentSchoolId("minTestID");
        int id = studentRepository.save(s).getStudentId();
        s.setStudentId(id);
        assertEquals(s.toString(), studentRepository.findOne(id).toString());
    }

    @Test
    public void testFull() {
        Student s = new Student();
        s.setBirthDate(new Timestamp((new Date().getTime() / 1000) * 1000));
        s.setCityOfBirth("Springfield");
        s.setCountryOfBirth("United States");
        s.setDateEnteredUs(new Timestamp(
                (new Date().getTime() / 1000) * 1000 + 5000));
        s.setDisplacementStatus("Tornado");
        s.setEconomicDisadvantaged(true);
        s.setFirstName("Mandy");
        s.setGenerationCodeSuffix("III");
        s.setHispanicLatinoEthnicity(true);
        s.setLastSurname("Mustard");
        s.setLimitedEnglishProficiency(LimitedEnglishProficiencyType.NO);
        s.setMaidenName("Sanders");
        s.setMiddleName("Merideth");
        s.setMultipleBirthStatus(true);
        s.setOldEthnicity(OldEthnicityType.WHITE_NOT_OF_HISPANIC_ORIGIN);
        s.setPersonalInformationVerification("Birth certificate");
        s.setPersonalTitlePrefix("Colonel");
        s.setProfileThumbnail("somePicture.jpg");
        s.setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType.REDUCED_PRICE);
        s.setSex(SexType.Female);
        s.setStateOfBirthAbbreviation("KY");
        s.setStudentSchoolId("FullTestID");
        int id = studentRepository.save(s).getStudentId();
        s.setStudentId(id);
        assertEquals(s.toString(), studentRepository.findOne(id).toString());

    }

}
