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
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.selectors.model.elem.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.elem.EmptySelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeDefaultSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeXSDSelectorElement;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.ModelElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests
 *
 * @author jstokes
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultSelectorSemanticModelTest {

    @Autowired
    private DefaultSelectorSemanticModel defaultSelectorSemanticModel;
    private ModelProvider provider;

    private static final String TEST_XMI_LOC = "/sliModel/test_SLI.xmi";

    @Before
    public void setup() {
        provider = new ModelProvider(TEST_XMI_LOC);
        defaultSelectorSemanticModel.setModelProvider(provider);
    }

    @Test
    public void testSemanticParser() throws SelectorParseException {
        final ClassType student = provider.getClassType("Student");
        final SemanticSelector selector;
        final ClassType section = provider.getClassType("Section");
        final ClassType studentSectionAssociation = provider.getClassType("StudentSectionAssociation");
        final Attribute name = provider.getAttributeType(student, "name");
        final Attribute economicDisadvantaged = provider.getAttributeType(student, "economicDisadvantaged");
        final ClassType studentSchoolAssociation = provider.getClassType("StudentSchoolAssociation");
        final Attribute entryDate = provider.getAttributeType(studentSchoolAssociation, "entryDate");

        selector = defaultSelectorSemanticModel.parse(generateSelectorObjectMap(), student);

        assertTrue("Should contain base type", selector.containsKey(student));
        assertNotNull("Should have a list of attributes", selector.get(student));
        assertEquals("Base type should have 1 selector", 1, selector.size());

        final List<SelectorElement> studentList = selector.get(student);
        assertEquals(5, studentList.size());
        assertTrue(!studentList.contains(null));


        final Map<ModelElement, Object> selectorMap = mapify(studentList);
        assertTrue(selectorMap.containsKey(section));
        assertTrue(selectorMap.containsKey(studentSectionAssociation));
        assertTrue(selectorMap.containsKey(name));
        assertTrue(selectorMap.containsKey(economicDisadvantaged));
        assertTrue(selectorMap.containsKey(studentSchoolAssociation));
        @SuppressWarnings("unchecked")
        final SemanticSelector sectionSelector = (SemanticSelector) selectorMap.get(section);
        final List<SelectorElement> sectionSelectorElements = (List<SelectorElement>) sectionSelector.get(section);

        assertEquals(1, sectionSelectorElements.size());
        assertTrue(sectionSelectorElements.get(0) instanceof IncludeAllSelectorElement);

        assertEquals(true, selectorMap.get(studentSectionAssociation));
        assertTrue(selectorMap.get(name) instanceof SemanticSelector);
        assertTrue(selectorMap.get(studentSchoolAssociation) instanceof SemanticSelector);

        final SemanticSelector studentSchoolSelector = (SemanticSelector) selectorMap.get(studentSchoolAssociation);
        final Map<ModelElement, Object> studentSchoolSelectorMap = mapify(studentSchoolSelector.get(studentSchoolAssociation));
        assertEquals(true, studentSchoolSelectorMap.get(entryDate));
    }

    private Map<ModelElement, Object> mapify(final List<SelectorElement> studentList) {
        final Map<ModelElement, Object> rVal = new HashMap<ModelElement, Object>();

        for (final SelectorElement element : studentList) {
            rVal.put(element.getLHS(), element.getRHS());
        }

        return rVal;
    }

    @Test
    public void testIncludeAll() {
        final Map<String, Object> selector = new HashMap<String, Object>();

        final Map<String, Object> sectionAssocs = new HashMap<String, Object>();
        sectionAssocs.put("*", true);
        sectionAssocs.put("beginDate", false);
        selector.put("studentSectionAssociations", sectionAssocs);

        final ClassType student = provider.getClassType("Student");
        final SemanticSelector semanticSelector = defaultSelectorSemanticModel.parse(selector, student);
    }

    @Test(expected = SelectorParseException.class)
    public void testInvalidSelectors() throws SelectorParseException {
        final ClassType student = provider.getClassType("Student");
        final SemanticSelector selector =
                defaultSelectorSemanticModel.parse(generateFaultySelectorObjectMap(), student);
    }

    @Test
    public void testDefaultXSD() {
        final Map<String, Object> studentAttrs = new HashMap<String, Object>();
        studentAttrs.put("$", true);

        final ClassType student = provider.getClassType("Student");
        final SemanticSelector semanticSelector = defaultSelectorSemanticModel.parse(studentAttrs, student);

        final List<SelectorElement> elementList = semanticSelector.get(student);
        assertEquals(1, elementList.size());
        assertTrue(elementList.get(0) instanceof IncludeXSDSelectorElement);
    }


    @Test
    public void testDefault() {
        final Map<String, Object> studentAttrs = new HashMap<String, Object>();
        studentAttrs.put(".", true);

        final ClassType student = provider.getClassType("Student");
        final SemanticSelector semanticSelector = defaultSelectorSemanticModel.parse(studentAttrs, student);

        final List<SelectorElement> elementList = semanticSelector.get(student);
        assertEquals(1, elementList.size());
        assertTrue(elementList.get(0) instanceof IncludeDefaultSelectorElement);

        final Map<String, Object> embedded = new HashMap<String, Object>();
        final Map<String, Object> sectionAttrs = new HashMap<String, Object>();
        sectionAttrs.put(".", true);
        embedded.put("sections", sectionAttrs);

        final SemanticSelector embeddedSelector = defaultSelectorSemanticModel.parse(embedded, student);
        assertEquals(1, embeddedSelector.get(student).size());
        final SelectorElement embeddedElement = embeddedSelector.get(student).get(0);
        assertTrue(embeddedElement instanceof ComplexSelectorElement);
        assertEquals(1, ((ComplexSelectorElement) embeddedElement).getSelector().size());
    }

    @Test
    public void testEmptySelectors() {
        final Map<String, Object> studentAttrs = new HashMap<String, Object>();

        final ClassType student = provider.getClassType("Student");
        final SemanticSelector semanticSelector = defaultSelectorSemanticModel.parse(studentAttrs, student);
        assertEquals(1, semanticSelector.get(student).size());
        assertTrue(semanticSelector.get(student).get(0) instanceof EmptySelectorElement);

        final Map<String, Object> embeddedEmpty = new HashMap<String, Object>();
        embeddedEmpty.put("sections", new HashMap<String, Object>());
        final SemanticSelector emptySelector = defaultSelectorSemanticModel.parse(embeddedEmpty, student);
        assertEquals(1, semanticSelector.get(student).size());

        final ClassType section = provider.getClassType("Section");
        assertEquals(section, emptySelector.get(student).get(0).getLHS());
    }

    public Map<String, Object> generateFaultySelectorObjectMap() {
        final Map<String, Object> studentAttrs = generateSelectorObjectMap();
        studentAttrs.put("someNonExistentObject", true);
        return studentAttrs;
    }

    public Map<String, Object> generateSelectorObjectMap() {
        final Map<String, Object> schoolAttrs = new HashMap<String, Object>();
        schoolAttrs.put("schoolType", true);

        final Map<String, Object> ssaAttrs = new HashMap<String, Object>();
        ssaAttrs.put("entryGradeLevel", true);
        ssaAttrs.put("entryDate", true);
        ssaAttrs.put("school", schoolAttrs);

        final Map<String, Object> nameAttrs = new HashMap<String, Object>();
        nameAttrs.put("firstName", true);
        nameAttrs.put("lastSurname", false);

        final Map<String, Object> studentsAttrs = new HashMap<String, Object>();
        studentsAttrs.put("name", nameAttrs);
        studentsAttrs.put("economicDisadvantaged", true);
        studentsAttrs.put("studentSectionAssociations", true);
        studentsAttrs.put("studentSchoolAssociations", ssaAttrs);

        final Map<String, Object> sectionAttributes = new HashMap<String, Object>();
        sectionAttributes.put("*", true);
        studentsAttrs.put("sections", sectionAttributes);

        return studentsAttrs;
    }
}
