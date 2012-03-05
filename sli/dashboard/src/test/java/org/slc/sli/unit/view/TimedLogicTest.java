package org.slc.sli.unit.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slc.sli.view.AssessmentMetaDataResolver;
import org.slc.sli.view.TimedLogic;

/**
 * Unit tests for the StudentManager class.
 * 
 */
// Note that the implementation of TimedLogicTest is temporary, so we will throw all of this
// out when the Assessment entity is defined by the API team.
public class TimedLogicTest {

    // test parameters: Some fake assessment result objects
    List<GenericEntity> assessments;
    AssessmentMetaDataResolver assessmentMetaDataResolver;

    @Before
    public void setup() {
        // Populate the test object
        
        // Create 3 assesssments: one in 2008, with highest score, one in 2009 with lowest score,
        // and one in 2007
        GenericEntity a1 = createStudentAssessment("100", "2008-01-01", "HighestEvah");
        GenericEntity a2 = createStudentAssessment("1", "2009-01-01", "MostRecent");
        GenericEntity a3 = createStudentAssessment("50", "2007-01-01", "Dummy1");
        GenericEntity a4 = createStudentAssessment("51", "2007-01-02", "Dummy2");
        GenericEntity a5 = createStudentAssessment("52", "2007-01-03", "Dummy3");

        assessments = Arrays.asList(a1, a2, a3, a4, a5);
        
        assessmentMetaDataResolver = new AssessmentMetaDataResolver(Arrays.asList(createAssessmentMetaData(
                "Mock Assessment", "01/01")));
    }

    @Test
    public void testGetMostRecentAssessment() {
        GenericEntity a = TimedLogic.getMostRecentAssessment(assessments);
        assertEquals("MostRecent", a.get("studentId"));
    }

    @Test
    public void testGetHighestEverAssessment() {
        GenericEntity a = TimedLogic.getHighestEverAssessment(assessments);
        assertEquals("HighestEvah", a.get("studentId"));
    }

    @Ignore
    @Test
    public void testGetMostRecentAssessmentWindow() {
        GenericEntity a = TimedLogic.getMostRecentAssessmentWindow(assessments, assessmentMetaDataResolver,
                "Mock Assessment");
        assertNull(a);
    }

    private GenericEntity createStudentAssessment(String scaleScore, String date, String studentID) {
        GenericEntity studentAssessmentAssoc = new GenericEntity();
        studentAssessmentAssoc.put("studentId", studentID);
        studentAssessmentAssoc.put("administrationDate", date);
        List<Map<String, String>> scoreResults = new ArrayList<Map<String, String>>();
        Map<String, String> scoreResult = new HashMap<String, String>();
        scoreResult.put(Constants.ATTR_ASSESSMENT_REPORTING_METHOD, Constants.ATTR_SCALE_SCORE);
        scoreResult.put(Constants.ATTR_RESULT, scaleScore);
        scoreResults.add(scoreResult);
        studentAssessmentAssoc.put(Constants.ATTR_SCORE_RESULTS, scoreResults);
        return studentAssessmentAssoc;
    }

    private GenericEntity createAssessmentMetaData(String name, String windowEndDate) {
        
        GenericEntity retVal = new GenericEntity();
        /*
         * retVal.setName(name);
         * Period p = new Period();
         * p.setWindowEnd(windowEndDate);
         * p.setName("Annual");
         * retVal.setPeriod("Annual");
         * Period[] ps = new Period[1];
         * ps[0] = p;
         * retVal.setPeriods(ps);
         */
        return retVal;
        
    }
}
