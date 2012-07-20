/**
 *
 */
package org.slc.sli.api.resources.aggregation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.domain.AggregateData;
import org.slc.sli.domain.AggregateDatum;
import org.slc.sli.domain.Entity;

/**
 * @author nbrown
 *
 */
public class AggregateListingResourceTest {

    private Entity entityToAggregate = mock(Entity.class);
    private AggregateListingResource resource;

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
        AggregateData data = new AggregateData(aggregateMap);
        when(entityToAggregate.getAggregates()).thenReturn(data);
        resource = new AggregateListingResource(entityToAggregate);

    }

    /**
     * Test method for
     * {@link org.slc.sli.api.resources.aggregation.AggregateListingResource#getAvailableAggregates()}
     * .
     */
    @Test
    public void testGetAvailableAggregates() {
        assertEquals(Arrays.asList(new AggregateDatum("assessments", "HighestEver", "ACT", "ScaleScore", "28.0"),
                new AggregateDatum("attendance", "LastSemester", "MathClass", "PercentInClass", "90%")), resource
                .getAggregates(null, null, null, null).getEntity());
    }

}
