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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.model.TestModelProvider;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.xml.namespace.QName;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ModelProviderTest {

    private ModelProvider provider; // class under test

    @Test
    public void testReadModel() {
        provider = new TestModelProvider();
        assertNotNull(provider);
    }

    @Test
    public void testDelegation() {
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final Identifier mockIdentifier = Identifier.fromString("testIdentifier");

        provider = new ModelProvider(mockIndex);
        provider.getAssociationEnds(mockIdentifier);
        verify(mockIndex).getAssociationEnds(mockIdentifier);

        final QName qName = QName.valueOf("Student");
        provider.lookupByName(qName);
        verify(mockIndex).lookupByName(qName);

        final Identifier testId = Identifier.fromString("test-id");

        provider.getTagDefinition(testId);
        verify(mockIndex).getTagDefinition(testId);

        provider.getType(testId);
        verify(mockIndex).getType(testId);

        @SuppressWarnings("unchecked")
        final Map<String, ClassType> mockClassTypes = mock(Map.class);
        when(mockIndex.getClassTypes()).thenReturn(mockClassTypes);
        provider.getClassType("test-name");
        verify(mockIndex).getClassTypes();
        verify(mockClassTypes).get("test-name");
    }

    @Test
    public void testIsAttribute() {
        provider = new TestModelProvider();
        final ClassType student = provider.getClassType("Student");
        assertTrue(provider.isAttribute(student, "name"));
        assertTrue(!provider.isAttribute(student, "studentSectionAssociations"));
    }

    @Test
    public void testIsAssociation() {
        provider = new TestModelProvider();
        final ClassType student = provider.getClassType("Student");
        assertTrue(provider.isAssociation(student, "studentSectionAssociations"));
        assertTrue(!provider.isAssociation(student, "name"));
    }

    @Test
    public void testGetType() {
        provider = new TestModelProvider();
        final ClassType student = provider.getClassType("Student");

        final ClassType name = provider.getClassType(student, "name");
        assertNotNull(name);
        assertTrue(name.isClassType());
        assertTrue(name.getAttributes().size() > 0);

        final ClassType sectionAssociations = provider.getClassType(student, "studentSectionAssociations");
        assertNotNull(sectionAssociations);
        assertTrue(sectionAssociations.isClassType());
        assertTrue(sectionAssociations.getAttributes().size() > 0);
    }

    @Test
    public void testGetAttribute() {
        provider = new TestModelProvider();
        final ClassType student = provider.getClassType("Student");

        final Attribute studentStateId = provider.getAttributeType(student, "studentUniqueStateId");
        assertNotNull(studentStateId);
        assertEquals("studentUniqueStateId", studentStateId.getName());
    }

    @Test
    public void testGetConnectionPath() {
        provider = new TestModelProvider();

        ClassType student = provider.getClassType("Student");
        ClassType section = provider.getClassType("Section");
        ClassType edOrg = provider.getClassType("EducationOrganization");
        ClassType staff = provider.getClassType("Staff");
        ClassType studentSchoolAssociation = provider.getClassType("StudentSchoolAssociation");
        ClassType school = provider.getClassType("School");

        String path = provider.getConnectionPath(section, student);
        assertEquals("Should match", "studentId", path);

        path = provider.getConnectionPath(staff, edOrg);
        assertEquals("Should match", "educationOrganizationReference", path);

        path = provider.getConnectionPath(edOrg, studentSchoolAssociation);
        assertEquals("Should match", "schoolId", path);

        path = provider.getConnectionPath(staff, school);
        assertEquals("Should match", "educationOrganizationReference", path);
    }
}
