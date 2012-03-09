package org.slc.sli.unit.view;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.HistoricalDataResolver;
import org.slf4j.Logger;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Test class for HistoricalDataResolver
 * @author jstokes
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class HistoricalDataResolverTest {
    private Logger log = LoggerFactory.getLogger(HistoricalDataResolverTest.class);

    private HistoricalDataResolver historicalDataResolver; // class under test

    @Before
    public void setup() {
        SortedSet<String> schoolYears = new TreeSet<String>();
        schoolYears.add("Tenth Grade");
        schoolYears.add("Ninth Grade");
        schoolYears.add("Eleventh Grade");
        
        String subjectArea = "English";

        Map<String, List<GenericEntity>> historicalData = new HashMap<String, List<GenericEntity>>();
        List<GenericEntity> genericEntities = new ArrayList<GenericEntity>();
        GenericEntity ge1 = new GenericEntity();
        ge1.put("courseTitle", "English 101");
        ge1.put("finalLetterGradeEarned", "A");
        ge1.put("gradeLevelWhenTaken", "Tenth Grade");
        
        GenericEntity ge2 = new GenericEntity();
        ge2.put("courseTitle", "English 102");
        ge2.put("finalLetterGradeEarned", "B");
        ge2.put("gradeLevelWhenTaken", "Ninth Grade");
        
        GenericEntity ge3 = new GenericEntity();
        ge3.put("courseTitle", "English 103");
        ge3.put("finalLetterGradeEarned", "B");
        ge3.put("gradeLevelWhenTaken", "Ninth Grade");

        genericEntities.add(ge1);
        genericEntities.add(ge2);
        genericEntities.add(ge3);
        
        historicalData.put("1234", genericEntities);

        historicalDataResolver = new HistoricalDataResolver(historicalData, schoolYears, subjectArea);
    }

    @Test
    public void testGetSubjectArea() {
        assertEquals("Subject area should be correct", "English", historicalDataResolver.getSubjectArea());
    }

    @Test
    public void testGetSchoolYears() {
        SortedSet<String> testYears = new TreeSet<String>();
        testYears.add("Tenth Grade");
        testYears.add("Ninth Grade");
        testYears.add("Eleventh Grade");
        assertEquals("School years should be correct", testYears, historicalDataResolver.getSchoolYears());
    }

    @Test
    public void testGetCourse() {
        Field testField = new Field();
        testField.setTimeSlot("Tenth Grade");
        
        Map<String, String> testStudent = new HashMap<String, String>();
        testStudent.put("id", "1234");

        assertEquals("Get course should return correct course", "English 101", historicalDataResolver.getCourse(testField, testStudent));
        
        Map<String, String> testStudent2 = new HashMap<String, String>();
        testStudent2.put("id", "4567");
        
        assertEquals("Course should return '-' when there is no information", "-", historicalDataResolver.getCourse(testField, testStudent2));
        
        Field testField2 = new Field();
        testField2.setTimeSlot("Ninth Grade");

        assertEquals("Course should return '...' when there is more than one record", "...", historicalDataResolver.getCourse(testField2, testStudent));
    }

    @Test
    public void testGetGrade() {
        Field testField = new Field();
        testField.setTimeSlot("Tenth Grade");
        
        Map<String, String> testStudent = new HashMap<String, String>();
        testStudent.put("id", "1234");
        
        assertEquals("Get grade should return correct grade", "A", historicalDataResolver.getGrade(testField, testStudent));


    }
}
