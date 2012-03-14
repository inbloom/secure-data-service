package org.slc.sli.unit.view;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.GradebookEntryResolver;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test class for GradebookEntryResolver
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class GradebookEntryResolverTest {
    private Logger log = LoggerFactory.getLogger(GradebookEntryResolverTest.class);

    private static final String STUDENT_1_ID = "1234";
    private static final String STUDENT_2_ID = "4567";
    private static final String GRADE_KEY = "numericGradeEarned";
    private static final String DATE_1 = "10-09-2011";
    private static final String DATE_2 = "10-12-2011";
    private static final String AVERAGE_KEY = "Average";

    private GradebookEntryResolver gradebookEntryResolver; // class under test
    @Before
    public void setup() {
        Map<String, Map<String, GenericEntity>> gradebookData;
        Map<String, GenericEntity> gradebookEntries1;
        Map<String, GenericEntity> gradebookEntries2;

        gradebookData = new HashMap<String, Map<String, GenericEntity>>();
        gradebookEntries1 = new HashMap<String, GenericEntity>();
        gradebookEntries2 = new HashMap<String, GenericEntity>();

        // Student 1 entries
        GenericEntity ge1 = new GenericEntity();
        ge1.put(GRADE_KEY, "97");
        GenericEntity ge2 = new GenericEntity();
        ge2.put(GRADE_KEY, "75");
        GenericEntity avg1 = new GenericEntity();
        avg1.put(GRADE_KEY, "86");

        //Student 2 entries
        GenericEntity ge3 = new GenericEntity();
        ge3.put(GRADE_KEY, "89");
        GenericEntity avg2 = new GenericEntity();
        avg2.put(GRADE_KEY, "89");

        gradebookEntries1.put(DATE_1, ge1);
        gradebookEntries1.put(DATE_2, ge2);
        gradebookEntries1.put(AVERAGE_KEY, avg1);
        gradebookEntries2.put(DATE_2, ge3);
        gradebookEntries2.put(AVERAGE_KEY, avg2);

        gradebookData.put(STUDENT_1_ID, gradebookEntries1);
        gradebookData.put(STUDENT_2_ID, gradebookEntries2);

        gradebookEntryResolver = new GradebookEntryResolver(gradebookData);
    }

    @Test
    public void testGetGrade() {
        assertEquals("97", gradebookEntryResolver.getGrade(STUDENT_1_ID, DATE_1));
        assertEquals("89", gradebookEntryResolver.getGrade(STUDENT_2_ID, DATE_2));
        assertEquals("75", gradebookEntryResolver.getGrade(STUDENT_1_ID, DATE_2));
        assertEquals("Missing information should return a dash", "-",
                gradebookEntryResolver.getGrade("DOES_NOT_EXIST","DOES_NOT_EXIST"));
    }

    @Test
    public void testGetAverage() {
        assertEquals("86", gradebookEntryResolver.getAverage(STUDENT_1_ID));
        assertEquals("89", gradebookEntryResolver.getAverage(STUDENT_2_ID));
        assertEquals("Missing information should return a dash", "-",
                gradebookEntryResolver.getAverage("DOES_NOT_EXIST"));
    }
}
