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


package org.slc.sli.domain;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


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
        assertEquals(neutralQuery.getIncludeFieldString(), null);
        assertEquals(neutralQuery.getExcludeFieldString(), null);
        assertEquals(neutralQuery.getSortBy(), null);
        assertEquals(neutralQuery.getSortOrder(), null);
    }

    @Test
    public void testEquals() {
        String includeFields = "field1,field2";
        String excludeFields = "field3,field4";
        String sortBy = "field5";
        int offset = 4;
        int limit = 5;
        NeutralQuery.SortOrder sortOrderAscending = NeutralQuery.SortOrder.ascending;
        NeutralQuery.SortOrder sortOrderDescending = NeutralQuery.SortOrder.descending;

        NeutralQuery neutralQuery1 = new NeutralQuery();
        neutralQuery1.setIncludeFieldString(includeFields);
        neutralQuery1.setExcludeFieldString(excludeFields);
        neutralQuery1.setLimit(limit);
        neutralQuery1.setOffset(offset);
        neutralQuery1.setSortBy(sortBy);
        neutralQuery1.setSortOrder(sortOrderAscending);

        NeutralQuery neutralQuery2 = new NeutralQuery(neutralQuery1);
        assertTrue(neutralQuery1.equals(neutralQuery2));
        assertTrue(neutralQuery1 != neutralQuery2);

        // change value on query 1, assert two are no longer equal,
        // then change value on query 2 to match, confirm equal again
        neutralQuery1.setIncludeFieldString("");
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery2.setIncludeFieldString("");
        assertTrue(neutralQuery1.equals(neutralQuery2));

        // change value on query 1, assert two are no longer equal,
        // then change value on query 2 to match, confirm equal again
        neutralQuery1.setExcludeFieldString("");
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery2.setExcludeFieldString("");
        assertTrue(neutralQuery1.equals(neutralQuery2));

        // change value on query 1, assert two are no longer equal,
        // then change value on query 2 to match, confirm equal again
        neutralQuery1.setLimit(7);
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery2.setLimit(7);
        assertTrue(neutralQuery1.equals(neutralQuery2));

        // change value on query 1, assert two are no longer equal,
        // then change value on query 2 to match, confirm equal again
        neutralQuery1.setOffset(9);
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery2.setOffset(9);
        assertTrue(neutralQuery1.equals(neutralQuery2));

        // change value on query 1, assert two are no longer equal,
        // then change value on query 2 to match, confirm equal again
        neutralQuery1.setSortBy("");
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery2.setSortBy("");
        assertTrue(neutralQuery1.equals(neutralQuery2));

        // change value on query 1, assert two are no longer equal,
        // then change value on query 2 to match, confirm equal again
        neutralQuery1.setSortOrder(sortOrderDescending);
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery2.setSortOrder(sortOrderDescending);
        assertTrue(neutralQuery1.equals(neutralQuery2));

        // change value on query 1, assert two are no longer equal,
        // then change value on query 2 to match, confirm equal again
        neutralQuery1.addCriteria(new NeutralCriteria("x=1"));
        neutralQuery2.addCriteria(new NeutralCriteria("x=2"));
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery1.addCriteria(new NeutralCriteria("x=2"));
        assertFalse(neutralQuery1.equals(neutralQuery2));
        neutralQuery2.prependCriteria(new NeutralCriteria("x=1"));
        assertTrue(neutralQuery1.equals(neutralQuery2));

    }

    @Test
    public void testCloneConstructor() {
        String includeFields = "field1,field2";
        String excludeFields = "field3,field4";
        String sortBy = "field5";
        int offset = 4;
        int limit = 5;
        NeutralQuery.SortOrder sortOrderAscending = NeutralQuery.SortOrder.ascending;
        NeutralQuery.SortOrder sortOrderDescending = NeutralQuery.SortOrder.descending;


        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setIncludeFieldString(includeFields);
        neutralQuery.setExcludeFieldString(excludeFields);
        neutralQuery.setLimit(limit);
        neutralQuery.setOffset(offset);
        neutralQuery.setSortBy(sortBy);
        neutralQuery.setSortOrder(sortOrderAscending);

        //confirm neutralquery was created right
        assertEquals(neutralQuery.getCriteria().size(), 0);
        assertEquals(neutralQuery.getLimit(), limit);
        assertEquals(neutralQuery.getOffset(), offset);
        assertEquals(neutralQuery.getIncludeFieldString(), includeFields);
        assertEquals(neutralQuery.getExcludeFieldString(), excludeFields);
        assertEquals(neutralQuery.getSortBy(), sortBy);
        assertEquals(neutralQuery.getSortOrder(), sortOrderAscending);

        //clone neutral query into neutral query 2
        NeutralQuery neutralQuery2 = new NeutralQuery(neutralQuery);

        //confirm all properties were copied
        assertEquals(neutralQuery.getCriteria().size(), neutralQuery2.getCriteria().size());
        assertEquals(neutralQuery.getLimit(), neutralQuery2.getLimit());
        assertEquals(neutralQuery.getOffset(), neutralQuery2.getOffset());
        assertEquals(neutralQuery.getIncludeFieldString(), neutralQuery2.getIncludeFieldString());
        assertEquals(neutralQuery.getExcludeFieldString(), neutralQuery2.getExcludeFieldString());
        assertEquals(neutralQuery.getSortBy(), neutralQuery2.getSortBy());
        assertEquals(neutralQuery.getSortOrder(), neutralQuery2.getSortOrder());

        //change all properties for neutral query 1
        neutralQuery.setIncludeFieldString("");
        neutralQuery.setExcludeFieldString("");
        neutralQuery.setLimit(0);
        neutralQuery.setOffset(0);
        neutralQuery.setSortBy("");
        neutralQuery.setSortOrder(sortOrderDescending);
        neutralQuery.addCriteria(new NeutralCriteria("x=1"));

        //confirm no "entanglement" between query 1 and query 2
        assertFalse(neutralQuery.getCriteria().size() == neutralQuery2.getCriteria().size());
        assertFalse(neutralQuery.getLimit() == neutralQuery2.getLimit());
        assertFalse(neutralQuery.getOffset() == neutralQuery2.getOffset());
        assertFalse(neutralQuery.getIncludeFieldString().equals(neutralQuery2.getIncludeFieldString()));
        assertFalse(neutralQuery.getExcludeFieldString().equals(neutralQuery2.getExcludeFieldString()));
        assertFalse(neutralQuery.getSortBy().equals(neutralQuery2.getSortBy()));
        assertFalse(neutralQuery.getSortOrder().equals(neutralQuery2.getSortOrder()));
    }

    @Test
    public void testSingleCriteriaConstructor() {
        NeutralQuery neutralQuery = new NeutralQuery(new NeutralCriteria("x=1"));
        assertEquals(neutralQuery.getCriteria().size(), 1);
        assertEquals(neutralQuery.getLimit(), 0);
        assertEquals(neutralQuery.getOffset(), 0);
        assertEquals(neutralQuery.getIncludeFieldString(), null);
        assertEquals(neutralQuery.getExcludeFieldString(), null);
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
        neutralQuery.setIncludeFieldString(includeFields);
        neutralQuery.setExcludeFieldString(excludeFields);
        neutralQuery.setLimit(limit);
        neutralQuery.setOffset(offset);
        neutralQuery.setSortBy(sortBy);
        neutralQuery.setSortOrder(sortOrderAscending);

        assertEquals(neutralQuery.getCriteria().size(), 0);
        assertEquals(neutralQuery.getLimit(), limit);
        assertEquals(neutralQuery.getOffset(), offset);
        assertEquals(neutralQuery.getIncludeFieldString(), includeFields);
        assertEquals(neutralQuery.getExcludeFieldString(), excludeFields);
        assertEquals(neutralQuery.getSortBy(), sortBy);
        assertEquals(neutralQuery.getSortOrder(), sortOrderAscending);

        neutralQuery.setSortOrder(sortOrderDescending);
        assertEquals(neutralQuery.getSortOrder(), sortOrderDescending);
    }

    @Test
    public void testToString() {
        assertNotNull(new NeutralQuery().toString());
    }

}
