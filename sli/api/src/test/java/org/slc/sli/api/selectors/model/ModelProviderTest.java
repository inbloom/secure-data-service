package org.slc.sli.api.selectors.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ModelProvider provider; // class under test

    private static final String TEST_XMI_LOC = "/sliModel/test_SLI.xmi";

    @Before
    public void setup() {
    }

    @Test
    public void testReadModel() {
        provider = new ModelProvider(TEST_XMI_LOC);
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
        provider = new ModelProvider(TEST_XMI_LOC);
        final ClassType student = provider.getClassType("Student");
        assertTrue(provider.isAttribute(student, "name"));
        assertTrue(!provider.isAttribute(student, "sectionAssociations"));
    }

    @Test
    public void testIsAssociation() {
        provider = new ModelProvider(TEST_XMI_LOC);
        final ClassType student = provider.getClassType("Student");
        assertTrue(provider.isAssociation(student, "sectionAssociations"));
        assertTrue(!provider.isAssociation(student, "name"));
    }

    @Test
    public void testGetType() {
        provider = new ModelProvider(TEST_XMI_LOC);
        final ClassType student = provider.getClassType("Student");

        final ClassType name = provider.getClassType(student, "name");
        assertNotNull(name);
        assertTrue(name.isClassType());
        assertTrue(name.getAttributes().size() > 0);

        final ClassType sectionAssociations = provider.getClassType(student, "sectionAssociations");
        assertNotNull(sectionAssociations);
        assertTrue(sectionAssociations.isClassType());
        assertTrue(sectionAssociations.getAttributes().size() > 0);
    }

    @Test
    public void testGetAttribute() {
        provider = new ModelProvider(TEST_XMI_LOC);
        final ClassType student = provider.getClassType("Student");

        final Attribute studentStateId = provider.getAttributeType(student, "studentUniqueStateId");
        assertNotNull(studentStateId);
        assertEquals("studentUniqueStateId", studentStateId.getName());
    }
}
