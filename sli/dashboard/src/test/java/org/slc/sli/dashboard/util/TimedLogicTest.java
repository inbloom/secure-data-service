/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import org.slc.sli.dashboard.entity.GenericEntity;

/**
 * Unit tests for the StudentManager class.
 *
 */
// Note that the implementation of TimedLogicTest is temporary, so we will throw all of this
// out when the Assessment entity is defined by the API team.
public class TimedLogicTest {

    // test parameters: Some fake assessment result objects
    List<GenericEntity> assessments;

    @Before
    public void setup() {
        // Populate the test object

        // Create 3 assesssments: one in 2008, with highest score, one in 2009 with lowest score,
        // and one in 2007
        GenericEntity a1 = createResult("100", "2008-01-01", "HighestEvah");
        GenericEntity a2 = createResult("1", "2009-01-01", "MostRecent");
        GenericEntity a3 = createResult("50", "2007-01-01", "Dummy1");
        GenericEntity a4 = createResult("51", "2007-01-02", "Dummy2");
        GenericEntity a5 = createResult("52", "2007-01-03", "Dummy3");

        assessments = Arrays.asList(a1, a2, a3, a4, a5);

    }

    /*

    @Test
    public void testGetMostRecentAssessment() {
        GenericEntity a = TimedLogic2.getMostRecentAssessment(assessments);
        assertEquals("MostRecent", a.get("studentId"));
    }

    @Test
    public void testGetHighestEverAssessment() {
        GenericEntity a = TimedLogic2.getHighestEverAssessment(assessments);
        assertEquals("HighestEvah", a.get("studentId"));
    }

    @Test
    public void testGetMostRecentAssessmentWindow() {
        List<GenericEntity> results = new LinkedList<GenericEntity>();
        results.add(createResult("100", "2010-01-05", "s1"));

        List<GenericEntity> metaData = new LinkedList<GenericEntity>();
        metaData.add(createMetaData("a1", "2010-01-01", "2010-01-10"));

        GenericEntity r1 = TimedLogic2.getMostRecentAssessmentWindow(results, metaData);
        assertEquals(results.get(0), r1);

        metaData.add(createMetaData("a2", "2010-02-01", "2010-02-10"));
        GenericEntity r2 = TimedLogic2.getMostRecentAssessmentWindow(results, metaData);
        assertNull(r2);

        results.add(createResult("100", "2010-02-01", "s2"));
        GenericEntity r3 = TimedLogic2.getMostRecentAssessmentWindow(results, metaData);
        assertEquals(results.get(1), r3);

        metaData.add(createMetaData("a3", "2010-02-05", "2010-02-20"));
        GenericEntity r4 = TimedLogic2.getMostRecentAssessmentWindow(results, metaData);
        assertNull(r4);

        results.add(createResult("100", "2010-02-06", "s3"));
        GenericEntity r5 = TimedLogic2.getMostRecentAssessmentWindow(results, metaData);
        assertEquals(results.get(2), r5);

        metaData.add(createMetaData("a4", "2010-02-15", "2010-02-16"));
        GenericEntity r6 = TimedLogic2.getMostRecentAssessmentWindow(results, metaData);
        assertNull(r6);

        GenericEntity r7 = TimedLogic2.getMostRecentAssessmentWindow(results, new LinkedList<GenericEntity>());
        assertEquals(results.get(2), r7);
    }

    */
    private GenericEntity createResult(String scaleScore, String date, String studentID) {
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

    private GenericEntity createMetaData(String name, String windowBeginDate, String windowEndDate) {
        GenericEntity retVal = new GenericEntity();
        retVal.put(Constants.ATTR_ASSESSMENT_ID, name);
        Map<String, Object> periodDescriptor = new HashMap<String, Object>();
        periodDescriptor.put(Constants.ATTR_ASSESSMENT_PERIOD_BEGIN_DATE, windowBeginDate);
        periodDescriptor.put(Constants.ATTR_ASSESSMENT_PERIOD_END_DATE, windowEndDate);
        retVal.put(Constants.ATTR_ASSESSMENT_PERIOD_DESCRIPTOR, periodDescriptor);
        return retVal;

    }

}
