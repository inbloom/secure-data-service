package org.slc.sli.domain;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Unit test for Student
 */
public class StudentTest {
    
    /**
     * Rigourous Test :-)
     */
    @Test
    public void testStudent() {
        Student student = new Student();
        assertNotNull(student);
    }
}
