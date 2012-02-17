package org.slc.sli.api.service.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.data.mongodb.core.query.Order;

/**
 * A test for SortOrder class
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class SortOrderTest {
    
    @Test
    public void testToOrder() {
        assertEquals(Order.ASCENDING, SortOrder.ascending.toOrder());
        assertEquals(Order.DESCENDING, SortOrder.decending.toOrder());
    }
}
