package org.slc.sli.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import org.slc.sli.entity.Assessment;

/**
 * Unit tests for the StudentManager class.
 * 
 */
// Note that the implementation of TimedLogicTest is temporary, so we will throw all of this
// out when the Assessment entity is defined by the API team.
public class TimedLogicTest {

    // test parameters: Some fake assessment result objects
    List<Assessment> assessments;

    @Before
    public void setup() {
        // Populate the test object
        // Create 3 assesssments: one in 2008, with highest score, one in 2009 with lowest score, 
        //                        and one in 2007 
        Assessment a1 = createAssessment(100, 2008, "HighestEvah");
        Assessment a2 = createAssessment(1, 2009, "MostRecent");
        Assessment a3 = createAssessment(50, 2007, "Dummy");
        assessments = Arrays.asList(a1, a2, a3);
    }
    
    @Test
    public void testGetMostRecentAssessment() {
        Assessment a = TimedLogic.getMostRecentAssessment(assessments);
        assertEquals("MostRecent", a.getStudentId());
    }
    
    @Test
    public void testGetHighestEverAssessment() {
        Assessment a = TimedLogic.getHighestEverAssessment(assessments);
        assertEquals("HighestEvah", a.getStudentId());
    }
    
    @Test
    public void testGetMostRecentAssessmentWindow() {
        Assessment a = TimedLogic.getMostRecentAssessmentWindow(assessments);
        assertNull(a);
    }


    private Assessment createAssessment(int score, int year, String studentID) {
        Assessment retVal = new Assessment();
        retVal.setStudentId(studentID);
        retVal.setYear(year);
        retVal.setScaleScore(score);
        return retVal;
    }
}
