package org.slc.sli.validation;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;


/**
 * Various utility functions for test
 * 
 * @author kmyers
 *
 */
public class NeutralQueryTest {

    @Test
    public void testDefaultConstructor() {
        NeutralQuery neutralQuery = new NeutralQuery();
        assertEquals(neutralQuery.getCriteria().size(), 0);
        assertEquals(neutralQuery.getLimit(), 0);
        assertEquals(neutralQuery.getOffset(), 0);
        assertEquals(neutralQuery.getIncludeFields(), null);
        assertEquals(neutralQuery.getExcludeFields(), null);
        assertEquals(neutralQuery.getSortBy(), null);
        assertEquals(neutralQuery.getSortOrder(), null);
    }

    @Test
    public void testSingleCriteriaConstructor() {
        NeutralQuery neutralQuery = new NeutralQuery(new NeutralCriteria("x=1"));
        assertEquals(neutralQuery.getCriteria().size(), 1);
        assertEquals(neutralQuery.getLimit(), 0);
        assertEquals(neutralQuery.getOffset(), 0);
        assertEquals(neutralQuery.getIncludeFields(), null);
        assertEquals(neutralQuery.getExcludeFields(), null);
        assertEquals(neutralQuery.getSortBy(), null);
        assertEquals(neutralQuery.getSortOrder(), null);
    }

    @Test
    public void testGetterAndSetterMethods() {
        String includeFields = "field1,field2";
        String excludeFields = "field3,field4";
        String sortBy = "field5";
        int offset = 4;
        int limit = 5;
        NeutralQuery.SortOrder sortOrderAscending = NeutralQuery.SortOrder.ascending;
        NeutralQuery.SortOrder sortOrderDescending = NeutralQuery.SortOrder.descending;
        

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setIncludeFields(includeFields);
        neutralQuery.setExcludeFields(excludeFields);
        neutralQuery.setLimit(limit);
        neutralQuery.setOffset(offset);
        neutralQuery.setSortBy(sortBy);
        neutralQuery.setSortOrder(sortOrderAscending);

        assertEquals(neutralQuery.getCriteria().size(), 0);
        assertEquals(neutralQuery.getLimit(), limit);
        assertEquals(neutralQuery.getOffset(), offset);
        assertEquals(neutralQuery.getIncludeFields(), includeFields);
        assertEquals(neutralQuery.getExcludeFields(), excludeFields);
        assertEquals(neutralQuery.getSortBy(), sortBy);
        assertEquals(neutralQuery.getSortOrder(), sortOrderAscending);
        
        neutralQuery.setSortOrder(sortOrderDescending);
        assertEquals(neutralQuery.getSortOrder(), sortOrderDescending);
    }
}
