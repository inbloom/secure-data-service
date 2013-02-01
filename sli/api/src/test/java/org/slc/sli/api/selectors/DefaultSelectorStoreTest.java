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

package org.slc.sli.api.selectors;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.selectors.model.elem.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeXSDSelectorElement;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.modeling.uml.ClassType;

/**
 * Tests the expected loading and analysis of XMI into selectors.
 *
 *
 *
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultSelectorStoreTest {

    @Autowired
    @Qualifier("defaultSelectorRepository")
    private SelectorRepository defaultSelectorRepository;

    private ModelProvider provider;

    static final String TEST_XMI_LOC = "/sliModel/test_SLI.xmi";

    @Before
    public void setup() {
        provider = new ModelProvider(TEST_XMI_LOC);
    }

    @Test
    public void testSuccessfulLoadOfData() {
        final ClassType studentType = provider.getClassType("Student");
        final ClassType studentSectionAssociationType = provider.getClassType("StudentSectionAssociation");
        final ClassType sectionType = provider.getClassType("Section");

        SemanticSelector sectionSelector = defaultSelectorRepository.getSelector("Section");
        assertNotNull("Should not be null", sectionSelector);
        List<SelectorElement> elements = sectionSelector.get(sectionType);
        SelectorElement element = elements.get(0);
        assertTrue("Should be true", element instanceof BooleanSelectorElement);
        assertEquals("Should match", "sequenceOfCourse", element.getElementName());

        SemanticSelector studentSelector = defaultSelectorRepository.getSelector("Student");
        assertNotNull("Should not be null", studentSelector);
        elements = studentSelector.get(studentType);

        for (SelectorElement e : elements) {
            if (e instanceof IncludeXSDSelectorElement) {
                assertEquals("Should match", studentType, e.getLHS());
            } else if (e instanceof BooleanSelectorElement) {
                assertEquals("Should match", studentSectionAssociationType, e.getLHS());
            } else {
                fail("unknown type");
            }
        }

        SemanticSelector studentSectionAssociationSelector = defaultSelectorRepository.getSelector("StudentSectionAssociation");
        assertNotNull("Should not be null", studentSectionAssociationSelector);
        elements = studentSectionAssociationSelector.get(studentSectionAssociationType);
        element = elements.get(0);
        assertTrue("Should be true", element instanceof IncludeXSDSelectorElement);
    }

    @Test
    public void assertGracefulHandlingOfInvalidTypeDefaultSelector() {
        assertNull("Should be null", defaultSelectorRepository.getSelector("type1"));
    }

    @Test
    public void assertGracefulHandlingOfMissingDefaultSelector() {
        final ClassType schoolType = provider.getClassType("School");

        SemanticSelector schoolSelector = defaultSelectorRepository.getSelector("School");
        assertNotNull("Should not be null", schoolSelector);

        List<SelectorElement> elements = schoolSelector.get(schoolType);
        assertEquals("Should match", 1, elements.size());

        SelectorElement element = elements.get(0);
        assertTrue("Should be true", element instanceof IncludeXSDSelectorElement);
    }
}
