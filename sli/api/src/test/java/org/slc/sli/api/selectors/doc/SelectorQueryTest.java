/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import org.slc.sli.modeling.uml.Type;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SelectorQueryTest {

    private SelectorQuery query; // class under test

    @Before
    public void setup() {
        query = new SelectorQuery();
    }

    @Test
    public void testGetSetIncludeFields() {
        final List<String> includeFields = new ArrayList<String>(Arrays.asList("1", "2", "3"));
//        query.setIncludeFields(includeFields);
//        assertEquals(includeFields, query.getIncludeFields());
    }

    @Test
    public void testGetSetExcludeFields() {
        final List<String> excludeFields = new ArrayList<String>(Arrays.asList("1", "2", "3"));
//        query.setExcludeFields(excludeFields);
//        assertEquals(excludeFields, query.getExcludeFields());
    }

    @Test
    public void testGetSetQueries() {
        @SuppressWarnings("unchecked")
        final List<Map<Type, SelectorQueryPlan>> mockList = mock(List.class);

//        query.setQueries(mockList);
//        assertEquals(mockList, query.getQueries());
    }
}
