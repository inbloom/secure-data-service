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
import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xsd.WxsNamespace;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
public class PluginForRESTTest {

    private PluginForREST pluginForREST;
    private static final String TARGET_NAMESPACE = "http://www.slcedu.org/api/v1";
    private static final String TARGET_NAMESPACE_PREFIX = "";

    @Before
    public void setup() {
        pluginForREST = new PluginForREST();
    }

    @Test
    public void testDeclarePrefixMappings() throws Exception {
        final Map<String, String> mappings = pluginForREST.declarePrefixMappings();
        Assert.assertTrue(mappings.containsKey(TARGET_NAMESPACE_PREFIX));
    }

    @Test
    public void testGetElementNameQName() throws Exception {
        final QName expected = new QName(TARGET_NAMESPACE, "myTest", TARGET_NAMESPACE_PREFIX);
        Assert.assertEquals(expected, pluginForREST.getElementName("MyTest", false));
    }

    @Test
    public void testGetElementType() throws Exception {
        final QName expected = new QName(TARGET_NAMESPACE, "MyTest", TARGET_NAMESPACE_PREFIX);
        Assert.assertEquals(expected, pluginForREST.getElementType("MyTest", false));
    }

    @Test
    public void testGetGraphAssociationEndName() throws Exception {
        final PsmDocument<Type> doc = new PsmDocument<Type>(Mockito.mock(Type.class),
                new PsmResource("resource"), new PsmCollection("collection"));

        final QName expected = new QName(TARGET_NAMESPACE, "resource", TARGET_NAMESPACE_PREFIX);
        Assert.assertEquals(expected, pluginForREST.getGraphAssociationEndName(doc));
    }

    @Test
    public void testGetElementName() throws Exception {
        final PsmDocument<Type> doc = new PsmDocument<Type>(Mockito.mock(Type.class),
                new PsmResource("resource"), new PsmCollection("collection"));

        final QName expected = new QName(TARGET_NAMESPACE, "collection", TARGET_NAMESPACE_PREFIX);
        Assert.assertEquals(expected, pluginForREST.getElementName(doc));
    }

    @Test
    public void testGetTargetNamespace() throws Exception {
        Assert.assertEquals(TARGET_NAMESPACE, pluginForREST.getTargetNamespace());
    }

    @Test
    public void testGetTypeName() throws Exception {
        final QName expected = new QName(TARGET_NAMESPACE, "test", TARGET_NAMESPACE_PREFIX);
        Assert.assertEquals(expected, pluginForREST.getTypeName("test"));
    }

    @Test
    public void testIsAttributeFormDefaultQualified() throws Exception {
        Assert.assertFalse(pluginForREST.isAttributeFormDefaultQualified());
    }

    @Test
    public void testIsElementFormDefaultQualified() throws Exception {
        Assert.assertTrue(pluginForREST.isElementFormDefaultQualified());
    }

    @Test
    public void testIsEnabled() throws Exception {
        final QName test = new QName("");
        Assert.assertFalse(pluginForREST.isEnabled(test));
    }

    @Test
    public void testWriteAppInfo() throws Exception {
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);
        pluginForREST.writeAppInfo(Mockito.mock(TaggedValue.class), Mockito.mock(ModelIndex.class), mockWriter);
        Mockito.verify(mockWriter, Mockito.never()).end();
    }

    @Test
    public void testWriteAssociation() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final Identifier id = Identifier.fromString("1234");
        final AssociationEnd mockEnd = new AssociationEnd(multiplicity, "test", true, id, "test");
        final ClassType mockClassType = Mockito.mock(ClassType.class);
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);
        final Type mockType = Mockito.mock(Type.class);

        Mockito.when(mockIndex.getType(id)).thenReturn(mockType);
        Mockito.when(mockType.getName()).thenReturn("TypeName");

        pluginForREST.writeAssociation(mockClassType, mockEnd, mockIndex, mockWriter);

        Mockito.verify(mockWriter).element();
        Mockito.verify(mockWriter).minOccurs(Occurs.ZERO);
        Mockito.verify(mockWriter).maxOccurs(Occurs.ONE);
        Mockito.verify(mockWriter, Mockito.atLeastOnce()).end();
    }

    @Test
    public void testWriteReference() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final Identifier id = Identifier.fromString("1234");
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final TaggedValue mockTaggedValue = new TaggedValue("MockValue", id);
        taggedValueList.add(mockTaggedValue);
        final AssociationEnd mockEnd = new AssociationEnd(multiplicity, "test", true, taggedValueList, id, "test");
        final ClassType mockClassType = Mockito.mock(ClassType.class);
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);
        final TagDefinition mockTagDef = new TagDefinition(id, TagName.MONGO_NAME, multiplicity);

        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(mockTagDef);

        pluginForREST.writeReference(mockClassType, mockEnd, mockIndex, mockWriter);

        Mockito.verify(mockWriter).element();
        Mockito.verify(mockWriter).type(WxsNamespace.STRING);
        Mockito.verify(mockWriter).minOccurs(Occurs.ZERO);
        Mockito.verify(mockWriter).maxOccurs(Occurs.ONE);
        Mockito.verify(mockWriter, Mockito.atLeastOnce()).end();
    }

    @Test
    public void testWriteTopLevelElement() throws Exception {
        final Type mockType = Mockito.mock(Type.class);
        final PsmDocument<Type> mockClassType = new PsmDocument<Type>(mockType,
                new PsmResource("resource"), new PsmCollection("collection"));
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);

        Mockito.when(mockType.getName()).thenReturn("MockType");

        final QName elementName = pluginForREST.getElementName(mockClassType);
        final QName expectedQName =
                new QName(elementName.getNamespaceURI(), elementName.getLocalPart().concat("List"));

        pluginForREST.writeTopLevelElement(mockClassType, mockIndex, mockWriter);

        Mockito.verify(mockWriter, Mockito.atLeastOnce()).element();
        Mockito.verify(mockWriter).elementName(expectedQName);
        Mockito.verify(mockWriter).complexType();
        Mockito.verify(mockWriter).sequence();
        Mockito.verify(mockWriter, Mockito.atLeastOnce()).end();
    }
}
