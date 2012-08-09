/**
 *
 */
package org.slc.sli.api.resources.aggregation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
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

    private CalculatedValueListingResource resource;

    @Before
    public void setup() {
        Map<String, Map<String, Map<String, Map<String, Object>>>> aggregateMap = new HashMap<String, Map<String, Map<String, Map<String, Object>>>>();
        Map<String, Map<String, Map<String, Object>>> assessments = new HashMap<String, Map<String, Map<String, Object>>>();
        Map<String, Map<String, Object>> act = new HashMap<String, Map<String, Object>>();
        Map<String, Object> highestEver = new HashMap<String, Object>();
        Map<String, Map<String, Map<String, Object>>> attendance = new HashMap<String, Map<String, Map<String, Object>>>();
        Map<String, Map<String, Object>> mathClass = new HashMap<String, Map<String, Object>>();
        Map<String, Object> lastSemester = new HashMap<String, Object>();
        highestEver.put("ScaleScore", "28.0");
        act.put("HighestEver", highestEver);
        assessments.put("ACT", act);
        aggregateMap.put("assessments", assessments);
        lastSemester.put("PercentInClass", "90%");
        mathClass.put("LastSemester", lastSemester);
        attendance.put("MathClass", mathClass);
        aggregateMap.put("attendance", attendance);
        CalculatedData data = new CalculatedData(aggregateMap);
        resource = new CalculatedValueListingResource(data);

    }

    /**
     * Test method for
     * {@link org.slc.sli.api.resources.aggregation.CalculatedValueListingResource#getAvailableAggregates()}
     * .
     */
    @Test
    public void testGetAvailableAggregates() {
        assertEquals(Arrays.asList(new CalculatedDatum("assessments", "HighestEver", "ACT", "ScaleScore", "28.0"),
                new CalculatedDatum("attendance", "LastSemester", "MathClass", "PercentInClass", "90%")), resource
                .getAggregates(null, null, null, null).getEntity());
    }

}
