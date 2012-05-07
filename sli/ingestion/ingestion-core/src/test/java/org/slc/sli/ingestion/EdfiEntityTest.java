package org.slc.sli.ingestion;

import java.util.EnumSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author dkornishev
 * 
 */
public class EdfiEntityTest {
    
    @Test
    public void testSmallSubset() {
        Set<EdfiEntity> expected = EnumSet.of(EdfiEntity.STUDENT, EdfiEntity.PARENT);
        Set<EdfiEntity> actual = EdfiEntity.cleanse(EnumSet.of(EdfiEntity.STUDENT, EdfiEntity.STUDENT_ACADEMIC_RECORD, EdfiEntity.PARENT, EdfiEntity.STUDENT_PARENT_ASSOCIATION));
        
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testAll() {
        Set<EdfiEntity> expected = EnumSet.of(EdfiEntity.SELF, EdfiEntity.CALENDAR_DATE, EdfiEntity.CLASS_PERIOD, EdfiEntity.GRADUATION_PLAN, EdfiEntity.LEARNING_STANDARD, EdfiEntity.LOCATION, EdfiEntity.PARENT, EdfiEntity.PROGRAM,
                EdfiEntity.STAFF, EdfiEntity.STUDENT, EdfiEntity.TEACHER, EdfiEntity.BELL_SCHEDULE, EdfiEntity.COMPETENCY_LEVEL_DESCRIPTOR, EdfiEntity.CREDENTIAL_FIELD_DESCRIPTOR, EdfiEntity.PERFORMANCE_LEVEL_DESCRIPTOR,
                EdfiEntity.SERVICE_DESCRIPTOR);
        
        Set<EdfiEntity> actual = EdfiEntity.cleanse(EnumSet.allOf(EdfiEntity.class));
        
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testDeps() {
        EnumSet<EdfiEntity> expected = EnumSet.of(EdfiEntity.SECTION, EdfiEntity.STUDENT_ACADEMIC_RECORD, EdfiEntity.STUDENT_PARENT_ASSOCIATION);
        Set<EdfiEntity> actual = EdfiEntity.cleanse(expected);

        Assert.assertEquals(expected, actual);
    }    
}
