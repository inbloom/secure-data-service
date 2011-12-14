package org.slc.sli.repository;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.EntryType;
import org.slc.sli.domain.enums.ExitWithdrawalType;
import org.slc.sli.domain.enums.GradeLevelType;

/**
 * Test for old repo
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
// includes all contexts
public class MockStudentSchoolAssociationRepositoryTest {
    
    @Autowired(required = true)
    private StudentSchoolAssociationRepository schoolRepo;
    
    @Test
    public void testRepository() throws Exception {
        schoolRepo.deleteAll();
        schoolRepo.save(createTestAssociation());
        schoolRepo.save(createTestAssociation());
        schoolRepo.save(createTestAssociation());
        StudentSchoolAssociation different = createTestAssociation();
        different.setStudentId(1);
        different.setSchoolId(1);
        schoolRepo.save(different);
        StudentSchoolAssociation modified = createTestAssociation();
        modified.setStudentId(999);
        schoolRepo.save(modified);
        assertEquals(5, iterableSize(schoolRepo.findAll()));
        Iterable<StudentSchoolAssociation> associations = schoolRepo.findByStudentIdAndSchoolId(0, 0);
        assertEquals(3, iterableSize(associations));
        
        assertEquals(1, iterableSize(schoolRepo.findBySchoolId(1)));
        assertEquals(1, iterableSize(schoolRepo.findBySchoolId(1)));
        assertEquals(1, iterableSize(schoolRepo.findByStudentId(999)));
        
        schoolRepo.delete(modified);
        assertEquals(4, iterableSize(schoolRepo.findAll()));
        assertEquals(0, iterableSize(schoolRepo.findByStudentId(999)));
    }
    
    @SuppressWarnings("unused")
    private static <T> int iterableSize(Iterable<T> iterable) {
        int count = 0;
        for (T i : iterable) {
            count++;
        }
        return count;
    }
    
    protected static StudentSchoolAssociation createTestAssociation() {
        StudentSchoolAssociation association = new StudentSchoolAssociation();
        association.setSchoolId(0);
        association.setStudentId(0);
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
}
