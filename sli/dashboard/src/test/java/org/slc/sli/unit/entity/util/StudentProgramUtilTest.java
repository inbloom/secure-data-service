package org.slc.sli.unit.entity.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.StudentProgramUtil;
import org.slc.sli.util.Constants;

/**
 * 
 * @author dwu
 */
public class StudentProgramUtilTest {

    GenericEntity student;
    
    @Before
    public void setupAll() {
        student = new GenericEntity();
        student.put(Constants.PROGRAM_ELL, Constants.SHOW_ELL_LOZENGE);
        student.put(Constants.PROGRAM_FRE, "No");
    }
    
    @Test
    public void testHasProgramParticipation() {
        Assert.assertTrue(StudentProgramUtil.hasProgramParticipation(student, Constants.PROGRAM_ELL));
        Assert.assertFalse(StudentProgramUtil.hasProgramParticipation(student, Constants.PROGRAM_FRE));
        Assert.assertFalse(StudentProgramUtil.hasProgramParticipation(student, "Fake program"));
    }

}
