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

package org.slc.sli.modeling.xsdgen;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.psm.helpers.SliMongoConstants;
import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;

/**
 * @author jstokes
 */
public class PluginForMongoTest {

    private PluginForMongo pluginForMongo;

    @Before
    public void setup() {
        pluginForMongo = new PluginForMongo();
    }

    @Test
    public void testDeclarePrefixMappings() throws Exception {
        final Map<String, String> prefixMappings = pluginForMongo.declarePrefixMappings();
        assertTrue(prefixMappings.containsKey("sli"));
    }

    @Test
    public void testGetElementType() throws Exception {
        final QName qName = pluginForMongo.getElementType("test", false);
        assertEquals("test", qName.getLocalPart());
    }

    @Test
    public void testGetGraphAssociationEndName() throws Exception {
        final PsmDocument<Type> doc =
                new PsmDocument<Type>(mock(Type.class), new PsmResource("testResource"), new PsmCollection("testCollection"));
        assertEquals("testResource", pluginForMongo.getGraphAssociationEndName(doc).getLocalPart());
    }

    @Test
    public void testGetElementName() throws Exception {
        final PsmDocument<Type> doc =
                new PsmDocument<Type>(mock(Type.class), new PsmResource("testResource"), new PsmCollection("testCollection"));

        assertEquals("testCollection", pluginForMongo.getElementName(doc).getLocalPart());
    }

    @Test
    public void testGetTargetNamespace() throws Exception {
        assertEquals(pluginForMongo.getTargetNamespace(), SliMongoConstants.NAMESPACE_SLI);
    }

    @Test
    public void testGetTypeName() throws Exception {
        final QName test = new QName("test");
        assertEquals(test, pluginForMongo.getTypeName("test"));
    }

    @Test
    public void testIsAttributeFormDefaultQualified() throws Exception {
        assertTrue(pluginForMongo.isAttributeFormDefaultQualified());
    }

    @Test
    public void testIsElementFormDefaultQualified() throws Exception {
        assertTrue(pluginForMongo.isElementFormDefaultQualified());
    }

    @Test
    public void testIsEnabled() throws Exception {
        assertFalse(pluginForMongo.isEnabled(new QName("test")));
    }

    @Test
    public void testWriteAppInfo() throws Exception {
        final Identifier mockID = Identifier.fromString("1234");
        final TaggedValue mockTaggedValue = new TaggedValue("test", mockID);
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = mock(Uml2XsdPluginWriter.class);
        final TagDefinition mockTagDef =
                new TagDefinition(mockID, SliUmlConstants.TAGDEF_NATURAL_KEY, new Multiplicity(new Range(Occurs.ONE, Occurs.UNBOUNDED)));

        when(mockIndex.getTagDefinition(mockID)).thenReturn(mockTagDef);

        pluginForMongo.writeAppInfo(mockTaggedValue, mockIndex, mockWriter);

        verify(mockWriter).begin("sli", SliMongoConstants.SLI_NATURAL_KEY.getLocalPart(),
                SliMongoConstants.SLI_NATURAL_KEY.getNamespaceURI());
        verify(mockWriter, times(2)).end();
    }

    @Test
    public void testWriteAssociation() throws Exception {
        final ClassType mockClass = mock(ClassType.class);
        final List<TaggedValue> mockTaggedValues = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.random();
        final Identifier testId = Identifier.fromString("1234");
        mockTaggedValues.add(new TaggedValue("true", id));
        final Multiplicity mult = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final AssociationEnd mockEnd = new AssociationEnd(mult, "test", false, mockTaggedValues, testId, "test");
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = mock(Uml2XsdPluginWriter.class);
        final TagDefinition mockTagDef = new TagDefinition(Identifier.random(), "mongo.navigable", mult);
        final Type mockType = mock(Type.class);

        when(mockIndex.getTagDefinition(id)).thenReturn(mockTagDef);
        when(mockIndex.getType(testId)).thenReturn(mockType);
        when(mockType.getName()).thenReturn("test name");

        pluginForMongo.writeAssociation(mockClass, mockEnd, mockIndex, mockWriter);

        verify(mockWriter).element();
        verify(mockWriter).annotation();
        verify(mockWriter).begin("sli", SliMongoConstants.SLI_REFERENCE_TYPE.getLocalPart(),
                SliMongoConstants.SLI_REFERENCE_TYPE.getNamespaceURI());
    }

    @Test
    public void testWriteTopLevelElement() throws Exception {
        @SuppressWarnings("unchecked")
        final PsmDocument<Type> classType = mock(PsmDocument.class);
        final Type mockType = mock(Type.class);
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = mock(Uml2XsdPluginWriter.class);

        when(mockType.getName()).thenReturn("Type Name");
        when(classType.getType()).thenReturn(mockType);
        when(classType.getSingularResourceName()).thenReturn(new PsmCollection("test collection"));

        pluginForMongo.writeTopLevelElement(classType, mockIndex, mockWriter);

        final QName elementQName = pluginForMongo.getElementName(classType);

        verify(mockWriter).elementName(elementQName);
        verify(mockWriter).annotation();
        verify(mockWriter, atLeastOnce()).end();
    }

    @Test
    public void testGetElementNameByRef() {
        final QName expected = new QName(Uml2XsdTools.camelCase("MyTest"));
        assertEquals(expected, pluginForMongo.getElementName("MyTest", false));
    }

    @Test
    public void testIsUnbounded() {
        final Occurs truthy = Occurs.UNBOUNDED;
        assertTrue(PluginForMongo.isUnbounded(truthy));

        final Occurs falsey = Occurs.ONE;
        assertFalse(PluginForMongo.isUnbounded(falsey));
    }
}
