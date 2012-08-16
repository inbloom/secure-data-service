/**
 *
 */
package org.slc.sli.api.resources.v1.aggregation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.CalculatedDatum;

/**
 * @author nbrown
 *
 */
public class AggregateListingResourceTest {

    private CalculatedDataListingResource cVResource;

    @Before
    public void setup() {
        Map<String, Map<String, Map<String, Map<String, String>>>> aggregateMap = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
        Map<String, Map<String, Map<String, String>>> assessments = new HashMap<String, Map<String, Map<String, String>>>();
        Map<String, Map<String, String>> act = new HashMap<String, Map<String, String>>();
        Map<String, String> highestEver = new HashMap<String, String>();
        Map<String, Map<String, Map<String, String>>> attendance = new HashMap<String, Map<String, Map<String, String>>>();
        Map<String, Map<String, String>> mathClass = new HashMap<String, Map<String, String>>();
        Map<String, String> lastSemester = new HashMap<String, String>();
        highestEver.put("ScaleScore", "28.0");
        act.put("HighestEver", highestEver);
        assessments.put("ACT", act);
        aggregateMap.put("assessments", assessments);
        lastSemester.put("PercentInClass", "90%");
        mathClass.put("LastSemester", lastSemester);
        attendance.put("MathClass", mathClass);
        aggregateMap.put("attendance", attendance);
        CalculatedData<String> cvData = new CalculatedData<String>(aggregateMap);
        cVResource = new CalculatedDataListingResource(cvData);

    }

    /**
     * Test method for
     * {@link org.slc.sli.api.resources.v1.aggregation.CalculatedDataListingResource#getAvailableAggregates()}
     * .
     */
    @Test
    public void testGetAvailableCalculatedValues() {
        CalculatedDatum<String> actScore = new CalculatedDatum<String>("assessments", "HighestEver", "ACT",
                "ScaleScore", "28.0");
        CalculatedDatum<String> mathScore = new CalculatedDatum<String>("attendance", "LastSemester", "MathClass",
                "PercentInClass", "90%");
        @SuppressWarnings("unchecked")
        List<CalculatedDatum<String>> expected = Arrays.asList(actScore, mathScore);
        assertEquals(expected, cVResource.getCalculatedValues(null, null, null, null)
                .getEntity());
    }

    @Test
    public void testGetAvailableAggregates() {

    }

}
