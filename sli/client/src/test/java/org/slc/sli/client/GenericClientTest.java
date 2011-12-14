package org.slc.sli.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.AdministrativeFundingControlType;
import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.ExitWithdrawalType;
import org.slc.sli.domain.enums.GradeLevelType;
import org.slc.sli.domain.enums.MagnetSpecialProgramEmphasisSchoolType;
import org.slc.sli.domain.enums.SchoolType;
import org.slc.sli.domain.enums.SexType;

/**
 * JUnit for Generic Client
 * 
 * @author nbrown
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@Ignore("This needs to be updated to work with new apis before tests will pass")
public class GenericClientTest {
    private static final Random RANDOM = new Random();
    
    private static final Logger LOG = LoggerFactory.getLogger(GenericClientTest.class);
    
    @Autowired
    private SliClient client;
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testList() {
        Student river = makeRiver();
        river = client.addNewResource(river);
        Iterator<Student> students = client.list(Student.class).iterator();
        assertNotNull(students);
        Student firstStudent = students.next();
        assertNotNull(firstStudent);
        client.deleteResource(Student.class, Integer.toString(river.getStudentId()));
        
    }
    
    @Test
    public void testCreateUpdateDeleteStudent() {
        // create
        Student river = makeRiver();
        String schoolID = river.getStudentSchoolId();
        LOG.debug("Student school id is {}", schoolID);
        river = client.addNewResource(river);
        assertNotNull(river);
        Student student = findStudent(schoolID);
        assertNotNull(student);
        assertEquals(river.getFirstName(), student.getFirstName());
        assertEquals(river.getLastSurname(), student.getLastSurname());
        assertEquals(river.getSex(), student.getSex());
        // looks like we lose ms somewhere, but we don't need to be that accurate
        assertEquals(river.getBirthDate().getTime() / 1000, student.getBirthDate().getTime() / 1000);
        // get
        assertEquals(student, client.getResource(Student.class, Integer.toString(student.getStudentId())));
        // update
        student.setCityOfBirth("San Antonio");
        student.setStateOfBirthAbbreviation("TX");
        assertTrue(client.updateResource(student));
        Student updatedStudent = findStudent(schoolID);
        assertEquals(river.getFirstName(), updatedStudent.getFirstName());
        assertEquals(river.getLastSurname(), updatedStudent.getLastSurname());
        assertEquals(river.getSex(), updatedStudent.getSex());
        assertEquals("San Antonio", updatedStudent.getCityOfBirth());
        assertEquals("TX", updatedStudent.getStateOfBirthAbbreviation());
        // delete
        assertTrue(client.deleteResource(Student.class, Integer.toString(student.getStudentId())));
        assertNull(findStudent(schoolID));
    }
    
    private Student findStudent(String schoolID) {
        Iterable<Student> students = client.list(Student.class);
        for (Student student : students) {
            if (schoolID.equals(student.getStudentSchoolId())) {
                return student;
            }
        }
        return null;
    }
    
    @Test
    public void testCreateUpdateDeleteSchool() {
        // create
        School midvale = makeMidvale();
        String stateOrganizationId = midvale.getStateOrganizationId();
        midvale = client.addNewResource(midvale);
        assertNotNull(midvale);
        School school = findSchool(stateOrganizationId);
        assertEquals(midvale.getFullName(), school.getFullName());
        assertEquals(midvale.getShortName(), school.getShortName());
        assertEquals(midvale.getMagnetSpecialProgramEmphasisSchool(), school.getMagnetSpecialProgramEmphasisSchool());
        // get
        assertEquals(school, client.getResource(School.class, Integer.toString(school.getSchoolId())));
        // update
        String webSite = "www.midvaleschoolforthegifted.com";
        school.setWebSite(webSite);
        assertTrue(client.updateResource(school));
        School updatedSchool = findSchool(stateOrganizationId);
        assertEquals(midvale.getFullName(), updatedSchool.getFullName());
        assertEquals(midvale.getShortName(), updatedSchool.getShortName());
        assertEquals(midvale.getMagnetSpecialProgramEmphasisSchool(),
                updatedSchool.getMagnetSpecialProgramEmphasisSchool());
        assertEquals(webSite, updatedSchool.getWebSite());
        assertTrue(client.deleteResource(School.class, Integer.toString(school.getSchoolId())));
    }
    
    @Test
    public void testAssociation() {
        School midvale = makeMidvale();
        midvale = client.addNewResource(midvale);
        Student river = makeRiver();
        river = client.addNewResource(river);
        try {
            StudentSchoolAssociation attends = new StudentSchoolAssociation();
            attends.setEntryGradeLevel(GradeLevelType.FIRST_GRADE);
            attends = client.associate(river, midvale, attends);
            assertNotNull(attends);
            Iterator<School> riversSchools = client.getAssociated(river, School.class).iterator();
            assertTrue(riversSchools.hasNext());
            School riversSchool = riversSchools.next();
            assertEquals(midvale.getStateOrganizationId(), riversSchool.getStateOrganizationId());
            assertTrue(!riversSchools.hasNext());
            Iterator<StudentSchoolAssociation> riverMidvaleAssocs = client.getAssociations(river, midvale,
                    StudentSchoolAssociation.class).iterator();
            assertTrue(riverMidvaleAssocs.hasNext());
            StudentSchoolAssociation assoc = riverMidvaleAssocs.next();
            assertEquals(GradeLevelType.FIRST_GRADE, assoc.getEntryGradeLevel());
            assertTrue(!riverMidvaleAssocs.hasNext());
            Iterator<Student> midvaleStudents = client.getAssociated(midvale, Student.class).iterator();
            assertTrue(midvaleStudents.hasNext());
            Student midValeStudent = midvaleStudents.next();
            assertEquals(river.getStudentSchoolId(), midValeStudent.getStudentSchoolId());
            assertTrue(!midvaleStudents.hasNext());
            Iterable<StudentSchoolAssociation> midvaleStudentAssociations = client.getAssociated(midvale,
                    StudentSchoolAssociation.class);
            boolean found = false;
            for (StudentSchoolAssociation association : midvaleStudentAssociations) {
                assertEquals(midvale.getSchoolId(), association.getSchoolId());
                if (association.getStudentId().equals(river.getStudentId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
            // Test update
            assoc.setEntryGradeLevel(GradeLevelType.SECOND_GRADE);
            assertTrue(client.reassociate(river, midvale, assoc));
            Iterator<StudentSchoolAssociation> riverMidvaleAssocs2 = client.getAssociations(river, midvale,
                    StudentSchoolAssociation.class).iterator();
            assertTrue(riverMidvaleAssocs2.hasNext());
            StudentSchoolAssociation assoc2 = riverMidvaleAssocs2.next();
            assertEquals(GradeLevelType.SECOND_GRADE, assoc2.getEntryGradeLevel());
            // Test disassociate
            assertTrue(client.disassociate(river, midvale, attends));
            assertTrue(!client.getAssociated(river, School.class).iterator().hasNext());
            assertTrue(!client.getAssociated(midvale, Student.class).iterator().hasNext());
        } finally {
            client.deleteResource(Student.class, Integer.toString(river.getStudentId()));
            client.deleteResource(School.class, Integer.toString(midvale.getSchoolId()));
        }
        
    }
    
    @Test
    public void testMultiples() {
        Student simon = client.addNewResource(makeSimon());
        Student river = client.addNewResource(makeRiver());
        School midVale = client.addNewResource(makeMidvale());
        School academy = client.addNewResource(makeAcademy());
        StudentSchoolAssociation a1 = new StudentSchoolAssociation();
        a1.setEntryType(EntryType.ORIGINAL);
        a1.setExitWithdrawType(ExitWithdrawalType.GRADUATED);
        a1 = client.associate(simon, midVale, a1);
        StudentSchoolAssociation a2 = new StudentSchoolAssociation();
        a2.setEntryType(EntryType.ORIGINAL);
        a2.setExitWithdrawType(ExitWithdrawalType.TRANSFER_INSTITUTION);
        a2 = client.associate(river, midVale, a2);
        StudentSchoolAssociation a3 = new StudentSchoolAssociation();
        a3.setEntryType(EntryType.TRANSFER_CHARTER);
        a3.setExitWithdrawType(ExitWithdrawalType.OTHER);
        a3 = client.associate(river, academy, a3);
        StudentSchoolAssociation a4 = new StudentSchoolAssociation();
        a4.setEntryType(EntryType.OTHER);
        a4.setExitWithdrawType(ExitWithdrawalType.GRADUATED);
        a4 = client.associate(river, midVale, a4); // made up happy ending
        try {
            assertTrue(contains(client.list(Student.class), river, simon));
            assertTrue(contains(client.list(School.class), midVale, academy));
            assertTrue(containsOnly(client.getAssociated(river, School.class), midVale, academy));
            assertTrue(containsOnly(client.getAssociated(midVale, Student.class), river, simon));
            assertTrue(containsOnly(client.getAssociated(river, StudentSchoolAssociation.class), a2, a3, a4));
            assertTrue(containsOnly(client.getAssociated(midVale, StudentSchoolAssociation.class), a1, a2, a4));
        } finally {
            client.deleteResource(river);
            client.deleteResource(simon);
            client.deleteResource(midVale);
            client.deleteResource(academy);
        }
    }
    
    @Test
    public void testConcurrentDelete() {
        Student simon = client.addNewResource(makeSimon());
        Student simonClone = client.addNewResource(makeSimon()); // from season 2
        Student river = client.addNewResource(makeRiver());
        Student riverClone = client.addNewResource(makeRiver());
        School midVale = client.addNewResource(makeMidvale());
        client.associate(simon, midVale, makeDummyAssoc());
        client.associate(simonClone, midVale, makeDummyAssoc());
        client.associate(river, midVale, makeDummyAssoc());
        client.associate(riverClone, midVale, makeDummyAssoc());
        assertTrue(contains(client.getAssociated(midVale, Student.class), simon, simonClone, river, riverClone));
        Iterable<Student> students = client.getAssociated(midVale, Student.class);
        client.deleteResource(simonClone);
        client.deleteResource(riverClone);
        assertTrue(containsOnly(students, simon, river));
        client.deleteResource(simon);
        client.deleteResource(river);
        client.deleteResource(midVale);
        
    }
    
    private StudentSchoolAssociation makeDummyAssoc() {
        StudentSchoolAssociation assoc = new StudentSchoolAssociation();
        assoc.setEntryType(EntryType.ORIGINAL);
        assoc.setExitWithdrawType(ExitWithdrawalType.GRADUATED);
        return assoc;
    }
    
    private <T> List<T> makeList(Iterable<T> coll) {
        List<T> list = new ArrayList<T>();
        for (T item : coll) {
            list.add(item);
        }
        return list;
    }
    
    private <T> boolean contains(Iterable<T> coll, T... toFind) {
        List<T> list = makeList(coll);
        return list.containsAll(Arrays.asList(toFind));
    }
    
    private <T> boolean containsOnly(Iterable<T> coll, T... toFind) {
        List<T> list = makeList(coll);
        return list.containsAll(Arrays.asList(toFind)) && list.size() == toFind.length;
    }
    
    private Student makeRiver() {
        Student river = new Student();
        river.setFirstName("River");
        river.setLastSurname("Tam");
        river.setSex(SexType.Female);
        river.setStudentSchoolId(Long.toString(RANDOM.nextLong()));
        Date birthDate = new Date(); // happy birthday River!
        river.setBirthDate(birthDate);
        return river;
    }
    
    private Student makeSimon() {
        Student simon = new Student();
        simon.setFirstName("Simon");
        simon.setLastSurname("Tam");
        simon.setSex(SexType.Male);
        simon.setStudentSchoolId(Long.toString(RANDOM.nextLong()));
        Date birthDate = new Date(0);
        simon.setBirthDate(birthDate);
        return simon;
    }
    
    private School makeMidvale() {
        School midvale = new School();
        midvale.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.ALL_STUDENTS_PARTICIPATE);
        midvale.setFullName("Midvale School for the Gifted");
        midvale.setShortName("Midvale");
        midvale.setStateOrganizationId(Long.toString(RANDOM.nextLong()));
        return midvale;
    }
    
    private School makeAcademy() {
        School academy = new School();
        academy.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.ALL_STUDENTS_PARTICIPATE);
        academy.setFullName("The Academy");
        academy.setShortName("The Academy");
        academy.setStateOrganizationId(Long.toString(RANDOM.nextLong()));
        academy.setAdministrativeFundingControl(AdministrativeFundingControlType.OTHER);
        academy.setSchoolType(SchoolType.ALTERNATIVE);
        return academy;
    }
    
    private School findSchool(String stateOrganizationId) {
        Iterable<School> schools = client.list(School.class);
        for (School school : schools) {
            if (stateOrganizationId.equals(school.getStateOrganizationId())) {
                return school;
                
            }
        }
        return null;
    }
}
