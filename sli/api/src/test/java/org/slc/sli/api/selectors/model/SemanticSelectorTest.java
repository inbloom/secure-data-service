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
package org.slc.sli.api.selectors.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.selectors.doc.SelectorQueryVisitor;
import org.slc.sli.api.selectors.model.elem.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.modeling.uml.Type;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SemanticSelectorTest {
    private SemanticSelector selector;

    @Before
    public void setup() {
        selector = new SemanticSelector();
    }

    @Test
    public void testAddSelector() {
        final Type testType = mock(Type.class);
        final SelectorElement se = mock(SelectorElement.class);
        selector.addSelector(testType, se);
        assertNotNull(selector.get(testType));
        assertEquals(1, selector.get(testType).size());

        selector.addSelector(testType, mock(BooleanSelectorElement.class));
        assertEquals(2, selector.get(testType).size());
    }

    @Test
    public void testToString() {
        assertTrue(selector.toString().isEmpty());
    }

    @Test
    public void testVisitor() {
        final SelectorQueryVisitor visitor = mock(SelectorQueryVisitor.class);
        selector.accept(visitor);
        verify(visitor).visit(selector);
    }
}
