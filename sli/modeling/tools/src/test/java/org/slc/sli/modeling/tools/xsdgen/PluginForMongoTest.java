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

package org.slc.sli.modeling.tools.xsdgen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.psm.helpers.SliMongoConstants;
import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.uml.index.ModelIndex;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Assert.assertTrue(prefixMappings.containsKey("sli"));
    }

    @Test
    public void testGetElementType() throws Exception {
        final QName qName = pluginForMongo.getElementType("test", false);
        Assert.assertEquals("test", qName.getLocalPart());
    }

    @Test
    public void testGetGraphAssociationEndName() throws Exception {
        final PsmDocument<Type> doc =
                new PsmDocument<Type>(Mockito.mock(Type.class), new PsmResource("testResource"), new PsmCollection("testCollection"));
        Assert.assertEquals("testResource", pluginForMongo.getGraphAssociationEndName(doc).getLocalPart());
    }

    @Test
    public void testGetElementName() throws Exception {
        final PsmDocument<Type> doc =
                new PsmDocument<Type>(Mockito.mock(Type.class), new PsmResource("testResource"), new PsmCollection("testCollection"));

        Assert.assertEquals("testCollection", pluginForMongo.getElementName(doc).getLocalPart());
    }

    @Test
    public void testGetTargetNamespace() throws Exception {
        Assert.assertEquals(pluginForMongo.getTargetNamespace(), SliMongoConstants.NAMESPACE_SLI);
    }

    @Test
    public void testGetTypeName() throws Exception {
        final QName test = new QName("test");
        Assert.assertEquals(test, pluginForMongo.getTypeName("test"));
    }

    @Test
    public void testIsAttributeFormDefaultQualified() throws Exception {
        Assert.assertTrue(pluginForMongo.isAttributeFormDefaultQualified());
    }

    @Test
    public void testIsElementFormDefaultQualified() throws Exception {
        Assert.assertTrue(pluginForMongo.isElementFormDefaultQualified());
    }

    @Test
    public void testIsEnabled() throws Exception {
        Assert.assertFalse(pluginForMongo.isEnabled(new QName("test")));
    }

    @Test
    public void testWriteAppInfo() throws Exception {
        final Identifier mockID = Identifier.fromString("1234");
        final TaggedValue mockTaggedValue = new TaggedValue("test", mockID);
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);
        final TagDefinition mockTagDef =
                new TagDefinition(mockID, SliUmlConstants.TAGDEF_NATURAL_KEY, new Multiplicity(new Range(Occurs.ONE, Occurs.UNBOUNDED)));

        Mockito.when(mockIndex.getTagDefinition(mockID)).thenReturn(mockTagDef);

        pluginForMongo.writeAppInfo(mockTaggedValue, mockIndex, mockWriter);

        Mockito.verify(mockWriter).begin("sli", SliMongoConstants.SLI_NATURAL_KEY.getLocalPart(),
                SliMongoConstants.SLI_NATURAL_KEY.getNamespaceURI());
        Mockito.verify(mockWriter, Mockito.times(2)).end();
    }

    @Test
    public void testWriteAssociation() throws Exception {
        final ClassType mockClass = Mockito.mock(ClassType.class);
        final List<TaggedValue> mockTaggedValues = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.random();
        final Identifier testId = Identifier.fromString("1234");
        mockTaggedValues.add(new TaggedValue("true", id));
        final Multiplicity mult = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final AssociationEnd mockEnd = new AssociationEnd(mult, "test", false, mockTaggedValues, testId, "test");
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);
        final TagDefinition mockTagDef = new TagDefinition(Identifier.random(), "mongo.navigable", mult);
        final Type mockType = Mockito.mock(Type.class);

        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(mockTagDef);
        Mockito.when(mockIndex.getType(testId)).thenReturn(mockType);
        Mockito.when(mockType.getName()).thenReturn("test name");

        pluginForMongo.writeAssociation(mockClass, mockEnd, mockIndex, mockWriter);

        Mockito.verify(mockWriter).element();
        Mockito.verify(mockWriter).annotation();
        Mockito.verify(mockWriter).begin("sli", SliMongoConstants.SLI_REFERENCE_TYPE.getLocalPart(),
                SliMongoConstants.SLI_REFERENCE_TYPE.getNamespaceURI());
    }

    @Test
    public void testWriteTopLevelElement() throws Exception {
        @SuppressWarnings("unchecked")
        final PsmDocument<Type> classType = Mockito.mock(PsmDocument.class);
        final Type mockType = Mockito.mock(Type.class);
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);

        Mockito.when(mockType.getName()).thenReturn("Type Name");
        Mockito.when(classType.getType()).thenReturn(mockType);
        Mockito.when(classType.getSingularResourceName()).thenReturn(new PsmCollection("test collection"));

        pluginForMongo.writeTopLevelElement(classType, mockIndex, mockWriter);

        final QName elementQName = pluginForMongo.getElementName(classType);

        Mockito.verify(mockWriter).elementName(elementQName);
        Mockito.verify(mockWriter).annotation();
        Mockito.verify(mockWriter, Mockito.atLeastOnce()).end();
    }

    @Test
    public void testGetElementNameByRef() {
        final QName expected = new QName(Uml2XsdTools.camelCase("MyTest"));
        Assert.assertEquals(expected, pluginForMongo.getElementName("MyTest", false));
    }

    @Test
    public void testIsUnbounded() {
        final Occurs truthy = Occurs.UNBOUNDED;
        Assert.assertTrue(PluginForMongo.isUnbounded(truthy));

        final Occurs falsey = Occurs.ONE;
        Assert.assertFalse(PluginForMongo.isUnbounded(falsey));
    }
}
