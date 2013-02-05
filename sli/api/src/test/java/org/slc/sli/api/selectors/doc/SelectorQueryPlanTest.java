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


package org.slc.sli.api.selectors.doc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.Type;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SelectorQueryPlanTest {

    private SelectorQueryPlan queryPlan;

    @Before
    public void setup() {
        queryPlan = new SelectorQueryPlan();
    }

    @Test
    public void testGetSetQuery() {
        final NeutralQuery mockQuery = mock(NeutralQuery.class);
        queryPlan.setQuery(mockQuery);
        assertEquals(mockQuery, queryPlan.getQuery());
    }

    @Test
    public void testGetSetType() {
        final Type mockType = mock(Type.class);
        queryPlan.setType(null);
        assertEquals(null, queryPlan.getType());
    }

    @Test
    public void testGetSetChildQueryPlans() {
        @SuppressWarnings("unchecked")
        final List<Object> mockList = mock(List.class);
        queryPlan.setChildQueryPlans(mockList);
        assertEquals(mockList, queryPlan.getChildQueryPlans());
    }

    @Test
    public void testIncludeExcludeFields() {
        @SuppressWarnings("unchecked")
        final List<String> mockInclude = mock(List.class);
        queryPlan.setIncludeFields(mockInclude);
        assertEquals(mockInclude, queryPlan.getIncludeFields());

        @SuppressWarnings("unchecked")
        final List<String> mockExclude = mock(List.class);
        queryPlan.setExcludeFields(mockExclude);
        assertEquals(mockExclude, queryPlan.getExcludeFields());
    }

    @Test
    public void testParseFields() {
        @SuppressWarnings("unchecked")
        final List<String> mockParse = mock(List.class);
        queryPlan.setParseFields(mockParse);
        assertEquals(mockParse, queryPlan.getParseFields());
    }
}
