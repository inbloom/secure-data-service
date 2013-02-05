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
import org.slc.sli.api.selectors.model.elem.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author jstokes
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SelectorElementTest {
    private ClassType element;
    private SemanticSelector selector;
    private Attribute attribute;

    @Before
    public void setup() {
        selector = mock(SemanticSelector.class);

        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final AssociationEnd end1 = new AssociationEnd(multiplicity, "end1", true, Identifier.random(), "end1Id");
        final AssociationEnd end2 = new AssociationEnd(multiplicity, "end2", true, Identifier.random(), "end2Id");

        element = new ClassType("complexTypeName", end1, end2);
        attribute = new Attribute(Identifier.random(), "attributeName", Identifier.random(),
                multiplicity, new ArrayList<TaggedValue>());
    }

    @Test
    public void testIsTyped() {
        doTestIsTyped(new ComplexSelectorElement(element, selector));
        doTestIsTyped(new BooleanSelectorElement(element, true));
        doTestIsTyped(new IncludeAllSelectorElement(element));
    }

    private void doTestIsTyped(final SelectorElement element) {
        assertTrue(element.isTyped());
    }

    @Test
    public void testIsAttribute() {
        doTestIsAttribute(new ComplexSelectorElement(attribute, selector));
        doTestIsAttribute(new BooleanSelectorElement(attribute, true));
        doTestIsAttribute(new IncludeAllSelectorElement(attribute));
    }

    private void doTestIsAttribute(final SelectorElement element) {
        assertTrue(element.isAttribute());
    }

    @Test
    public void testGetLHS() {
        doTestGetLHS(new ComplexSelectorElement(attribute, selector));
        doTestGetLHS(new BooleanSelectorElement(attribute, true));
        doTestGetLHS(new IncludeAllSelectorElement(attribute));
    }

    private void doTestGetLHS(final SelectorElement element) {
        assertTrue(attribute == element.getLHS());
    }

    @Test
    public void testGetRHS() {
        final SelectorElement complexElem = new ComplexSelectorElement(attribute, selector);
        final SelectorElement booleanSelectorElem = new BooleanSelectorElement(attribute, true);
        final SelectorElement includeAllSelectorElem = new IncludeAllSelectorElement(attribute);

        assertEquals(selector, complexElem.getRHS());
        assertEquals(true, booleanSelectorElem.getRHS());
        assertEquals(SelectorElement.INCLUDE_ALL, includeAllSelectorElem.getRHS());
    }

    @Test
    public void testGetElementName() {
        SelectorElement complexElem;
        SelectorElement booleanSelectorElem;
        SelectorElement includeAllSelectorElem;

        complexElem = new ComplexSelectorElement(attribute, selector);
        booleanSelectorElem = new BooleanSelectorElement(attribute, true);
        includeAllSelectorElem = new IncludeAllSelectorElement(attribute);

        assertEquals("attributeName", complexElem.getElementName());
        assertEquals("attributeName", booleanSelectorElem.getElementName());
        assertEquals("attributeName", includeAllSelectorElem.getElementName());

        complexElem = new ComplexSelectorElement(element, selector);
        booleanSelectorElem = new BooleanSelectorElement(element, true);
        includeAllSelectorElem = new IncludeAllSelectorElement(element);

        assertEquals("complexTypeName", complexElem.getElementName());
        assertEquals("complexTypeName", booleanSelectorElem.getElementName());
        assertEquals("complexTypeName", includeAllSelectorElem.getElementName());
    }

    @Test
    public void testAccept() {
        final SelectorQueryVisitor queryVisitor = mock(SelectorQueryVisitor.class);

        final ComplexSelectorElement complexSelectorElement =
                new ComplexSelectorElement(attribute, selector);
        final BooleanSelectorElement booleanSelectorElement =
                new BooleanSelectorElement(attribute, true);
        final IncludeAllSelectorElement includeAllSelectorElement =
                new IncludeAllSelectorElement(attribute);

        complexSelectorElement.accept(queryVisitor);
        booleanSelectorElement.accept(queryVisitor);
        includeAllSelectorElement.accept(queryVisitor);

        verify(queryVisitor).visit(complexSelectorElement);
        verify(queryVisitor).visit(booleanSelectorElement);
        verify(queryVisitor).visit(includeAllSelectorElement);
    }

    @Test
    public void testBooleanQualifier() {
        final BooleanSelectorElement booleanSelectorElement =
                new BooleanSelectorElement(attribute, true);

        assertTrue(booleanSelectorElement.getQualifier());
    }

    @Test
    public void testGetSelector() {
        final ComplexSelectorElement complexSelectorElement =
                new ComplexSelectorElement(attribute, selector);

        assertEquals(selector, complexSelectorElement.getSelector());
    }
}
