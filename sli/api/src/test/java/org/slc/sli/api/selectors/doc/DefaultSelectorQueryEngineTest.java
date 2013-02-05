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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.selectors.model.elem.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.elem.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.elem.EmptySelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeDefaultSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeXSDSelectorElement;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Tests
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultSelectorQueryEngineTest {

    @Autowired
    private DefaultSelectorQueryEngine defaultSelectorQueryEngine;

    @Autowired
    private ModelProvider provider;

    @Test
    public void testComplexSelector() {
        SemanticSelector selectorsWithType =  generateSelectorObjectMap();
        ClassType studentType = provider.getClassType("Student");

        Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selectorsWithType);

        assertNotNull("Should not be null", queryPlan);

        SelectorQueryPlan plan = queryPlan.get(studentType);
        assertNotNull("Should not be null", plan);
        assertNotNull("Should not be null", plan.getQuery());
        assertEquals("Should match", 2, plan.getChildQueryPlans().size());

        //NeutralQuery query = plan.getQuery();
        assertEquals("Should match", 2, plan.getIncludeFields().size());
    }

    @Test
    public void testIncludeAllSelector() {
        SemanticSelector selectorsWithType =  generateIncludeAllSelectorObjectMap();
        ClassType studentType = provider.getClassType("Student");

        Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selectorsWithType);

        assertNotNull("Should not be null", queryPlan);

        SelectorQueryPlan plan = queryPlan.get(studentType);
        assertNotNull("Should not be null", plan);
        assertNotNull("Should not be null", plan.getQuery());
        assertFalse("Should be false", plan.getIncludeFields().isEmpty());
        assertFalse("Should be false", plan.getChildQueryPlans().isEmpty());
        assertTrue("Should be true", plan.getExcludeFields().isEmpty());
    }

    @Test
    public void testExcludeSelector() {
        SemanticSelector selectorsWithType =  generateExcludeSelectorObjectMap();
        ClassType studentType = provider.getClassType("Student");

        Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selectorsWithType);

        assertNotNull("Should not be null", queryPlan);

        SelectorQueryPlan plan = queryPlan.get(studentType);
        assertNotNull("Should not be null", plan);
        assertNotNull("Should not be null", plan.getQuery());
        assertFalse("Should be false", plan.getIncludeFields().isEmpty());
        assertFalse("Should be false", plan.getExcludeFields().isEmpty());
        assertFalse("Should be false", plan.getChildQueryPlans().isEmpty());
    }

    @Test
    public void testAssociationSelector() {
        SemanticSelector selectorsWithType =  generateAssociationSelectorMap();
        ClassType studentType = provider.getClassType("Student");

        Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selectorsWithType);

        assertNotNull("Should not be null", queryPlan);

        SelectorQueryPlan plan = queryPlan.get(studentType);
        assertNotNull("Should not be null", plan);
        assertNotNull("Should not be null", plan.getQuery());
        assertEquals("Should match", 1, plan.getChildQueryPlans().size());
    }

    @Test
    public void testSkipAssociation() {
        final SemanticSelector selector = generateSkipAssociationSelectorMap();
        final ClassType studentType = provider.getClassType("Student");
        final ClassType sectionType = provider.getClassType("Section");

        final Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selector);
        assertNotNull(queryPlan);

        final SelectorQueryPlan plan = queryPlan.get(studentType);

        final List<Object> childPlans = plan.getChildQueryPlans();
        // One query to studentSectionAssociations
        assertEquals(1, childPlans.size());

        @SuppressWarnings("unchecked")
        final Map<Type, SelectorQueryPlan> studentSectionPlanMap = (Map<Type, SelectorQueryPlan>) childPlans.get(0);
        assertNotNull(studentSectionPlanMap);
        final SelectorQueryPlan studentSectionPlan = studentSectionPlanMap.get(sectionType);
        assertNotNull(studentSectionPlan);
    }

    @Test
    public void testDefaultSelector() {
        final ClassType studentType = provider.getClassType("Student");
        final ClassType studentSectionAssociationType = provider.getClassType("StudentSectionAssociation");
        final SemanticSelector selector = generateDefaultSelectorMap();

        final Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selector);
        assertNotNull(queryPlan);

        SelectorQueryPlan plan = queryPlan.get(studentType);
        assertFalse("Should be false", plan.getIncludeFields().isEmpty());
        assertTrue("Should be true", plan.getExcludeFields().isEmpty());
        assertFalse("Should be false", plan.getChildQueryPlans().isEmpty());

        SelectorQuery childQuery = (SelectorQuery) plan.getChildQueryPlans().get(0);
        SelectorQueryPlan childPlan = childQuery.get(studentSectionAssociationType);
        assertNotNull("Should not be null", childPlan.getQuery());
    }

    @Test
    public void testXSDSelector() {
        final ClassType studentType = provider.getClassType("Student");
        final SemanticSelector selector = generateXSDSelectorMap();

        final Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selector);
        assertNotNull(queryPlan);

        SelectorQueryPlan plan = queryPlan.get(studentType);
        assertFalse("should be false", plan.getIncludeFields().isEmpty());
        assertTrue("should be true", plan.getExcludeFields().isEmpty());
        assertTrue("should be true", plan.getChildQueryPlans().isEmpty());
    }

    @Test
    public void testEmptySelector() {
        final ClassType studentType = provider.getClassType("Student");
        final SemanticSelector selector = generateEmptySelectorMap();

        final Map<Type, SelectorQueryPlan> queryPlan = defaultSelectorQueryEngine.assembleQueryPlan(selector);
        assertNotNull(queryPlan);

        SelectorQueryPlan plan = queryPlan.get(studentType);
        assertTrue("should be true", plan.getIncludeFields().isEmpty());
        assertTrue("should be true", plan.getExcludeFields().isEmpty());
        assertTrue("should be true", plan.getChildQueryPlans().isEmpty());
        assertNotNull("Should not be null", plan.getQuery());
    }

    private SemanticSelector generateXSDSelectorMap() {
        final ClassType studentType = provider.getClassType("Student");

        final SemanticSelector studentAttrs = new SemanticSelector();
        final List<SelectorElement> attrs = new ArrayList<SelectorElement>();
        attrs.add(new IncludeXSDSelectorElement(studentType));
        studentAttrs.put(studentType, attrs);

        return studentAttrs;
    }

    private SemanticSelector generateEmptySelectorMap() {
        final ClassType studentType = provider.getClassType("Student");

        final SemanticSelector studentAttrs = new SemanticSelector();
        final List<SelectorElement> attrs = new ArrayList<SelectorElement>();
        attrs.add(new EmptySelectorElement(studentType));
        studentAttrs.put(studentType, attrs);

        return studentAttrs;
    }

    private SemanticSelector generateDefaultSelectorMap() {
        final ClassType studentType = provider.getClassType("Student");

        final SemanticSelector studentAttrs = new SemanticSelector();
        final List<SelectorElement> attrs = new ArrayList<SelectorElement>();
        attrs.add(new IncludeDefaultSelectorElement(studentType));
        studentAttrs.put(studentType, attrs);

        return studentAttrs;
    }

    private SemanticSelector generateSkipAssociationSelectorMap() {
        final ClassType studentType = provider.getClassType("Student");
        final Attribute name = getMockAttribute("name");
        final ClassType sectionType = provider.getClassType("Section");

        final SemanticSelector studentAttrs = new SemanticSelector();
        final List<SelectorElement> attrs = new ArrayList<SelectorElement>();
        attrs.add(new BooleanSelectorElement(name, true));
        attrs.add(new BooleanSelectorElement(sectionType, true));
        studentAttrs.put(studentType, attrs);

        return studentAttrs;
    }

    public SemanticSelector generateAssociationSelectorMap() {
        ClassType studentType = provider.getClassType("Student");

        Attribute name = getMockAttribute("name");
        ClassType sectionAssociations = getMockClassType("StudentSectionAssociation");

        SemanticSelector studentsAttrs = new SemanticSelector();
        List<SelectorElement> attributes1 = new ArrayList<SelectorElement>();
        attributes1.add(new BooleanSelectorElement(name, true));
        attributes1.add(new BooleanSelectorElement(sectionAssociations, true));
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }

    public SemanticSelector generateIncludeAllSelectorObjectMap() {
        ClassType studentType = provider.getClassType("Student");

        SemanticSelector studentsAttrs = new SemanticSelector();
        List<SelectorElement> attributes1 = new ArrayList<SelectorElement>();
        attributes1.add(new IncludeAllSelectorElement(studentType));
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }

    public SemanticSelector generateExcludeSelectorObjectMap() {
        ClassType studentType = provider.getClassType("Student");

        Attribute name = getMockAttribute("name");

        SemanticSelector studentsAttrs = new SemanticSelector();
        List<SelectorElement> attributes1 = new ArrayList<SelectorElement>();
        attributes1.add(new BooleanSelectorElement(name, false));
        attributes1.add(new IncludeAllSelectorElement(studentType));
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }

    public SemanticSelector generateSelectorObjectMap() {
        ClassType sectionType = provider.getClassType("Section");
        ClassType studentType = provider.getClassType("Student");
        ClassType studentSchoolAssocicationType = provider.getClassType("schoolAssociations<=>student");
        ClassType studentSectionAssocicationType = provider.getClassType("sectionAssociations<=>student");

        Attribute entryGradeLevel = getMockAttribute("entryGradeLevel");
        Attribute entryDate = getMockAttribute("entryDate");
        Attribute someField = getMockAttribute("someField");
        Attribute sessionId = getMockAttribute("sessionId");
        Attribute name = getMockAttribute("name");
        Attribute economicDisadvantaged = getMockAttribute("economicDisadvantaged");

        SemanticSelector studentSchoolAttrs = new SemanticSelector();
        List<SelectorElement> attributes = new ArrayList<SelectorElement>();
        attributes.add(new BooleanSelectorElement(entryGradeLevel, true));
        attributes.add(new BooleanSelectorElement(entryDate, true));
        studentSchoolAttrs.put(studentSchoolAssocicationType, attributes);

        SemanticSelector sectionAttrs = new SemanticSelector();
        List<SelectorElement> attributes3 = new ArrayList<SelectorElement>();
        attributes3.add(new BooleanSelectorElement(sessionId, true));
        sectionAttrs.put(sectionType, attributes3);

        SemanticSelector studentSectionAttrs = new SemanticSelector();
        List<SelectorElement> attributes2 = new ArrayList<SelectorElement>();
        attributes2.add(new BooleanSelectorElement(someField, true));
        attributes2.add(new ComplexSelectorElement(sectionType, sectionAttrs));
        studentSectionAttrs.put(studentSectionAssocicationType, attributes2);

        SemanticSelector studentsAttrs = new SemanticSelector();
        List<SelectorElement> attributes1 = new ArrayList<SelectorElement>();
        attributes1.add(new BooleanSelectorElement(name, true));
        attributes1.add(new BooleanSelectorElement(economicDisadvantaged, true));
        attributes1.add(new ComplexSelectorElement(studentSchoolAssocicationType, studentSchoolAttrs));
        attributes1.add(new ComplexSelectorElement(studentSectionAssocicationType, studentSectionAttrs));
        studentsAttrs.put(studentType, attributes1);

        return studentsAttrs;
    }

    private ClassType getMockClassType(String typeName) {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final AssociationEnd end1 = new AssociationEnd(multiplicity, "end1", true, Identifier.random(), "end1Id");
        final AssociationEnd end2 = new AssociationEnd(multiplicity, "end2", true, Identifier.random(), "end2Id");

        return new ClassType(typeName, end1, end2);
    }

    private Attribute getMockAttribute(String attributeName) {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));

        return new Attribute(Identifier.random(), attributeName, Identifier.random(),
                multiplicity, new ArrayList<TaggedValue>());
    }
}
