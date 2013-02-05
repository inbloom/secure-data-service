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

package org.slc.sli.modeling.tools.xmigen;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.modeling.psm.helpers.SliMongoConstants;
import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.uml.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/** 
* Xsd2UmlPluginForSLI Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 12, 2012</pre> 
* @version 1.0 
*/ 
public class Xsd2UmlPluginForSLITest { 

    private Xsd2UmlPluginForSLI pluginForSLI;
    //private TagDefinition tagDefinition;

@Before
public void before() throws Exception {
    pluginForSLI = new Xsd2UmlPluginForSLI();
}

/** 
* 
* Method: declareTagDefinitions(final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testDeclareTagDefinitions() throws Exception { 
    Xsd2UmlPluginHost host = Mockito.mock(Xsd2UmlPluginHost.class);
    Mockito.when(host.ensureTagDefinitionId(Matchers.anyString())).thenReturn(Identifier.random());
    List<TagDefinition> tagDefinitionList = pluginForSLI.declareTagDefinitions(host);
    Assert.assertNotNull(tagDefinitionList);
    Assert.assertEquals(18, tagDefinitionList.size());
} 

/** 
* 
* Method: getAssociationEndTypeName(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testGetAssociationEndTypeName() throws Exception { 
    ClassType classType = Mockito.mock(ClassType.class);
    Attribute attribute = Mockito.mock(Attribute.class);
    Xsd2UmlPluginHost host = Mockito.mock(Xsd2UmlPluginHost.class);
    TaggedValue taggedValue = Mockito.mock(TaggedValue.class);
    TagDefinition tagDefinition = Mockito.mock(TagDefinition.class);
    Identifier id = Identifier.random();
    List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
    taggedValueList.add(taggedValue);

    Mockito.when(attribute.getTaggedValues()).thenReturn(taggedValueList);
    Mockito.when(taggedValue.getTagDefinition()).thenReturn(id);
    Mockito.when(host.getTagDefinition(Matchers.any(Identifier.class))).thenReturn(tagDefinition);
    Mockito.when(tagDefinition.getName()).thenReturn(SliUmlConstants.TAGDEF_REFERENCE);
    Mockito.when(taggedValue.getValue()).thenReturn("test");

    String name = pluginForSLI.getAssociationEndTypeName(classType, attribute, host);
    Assert.assertNotNull(name);
} 

/** 
* 
* Method: isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testIsAssociationEnd() throws Exception { 
    ClassType classType = Mockito.mock(ClassType.class);
    Attribute attribute = Mockito.mock(Attribute.class);
    Xsd2UmlPluginHost host = Mockito.mock(Xsd2UmlPluginHost.class);
    TaggedValue taggedValue = Mockito.mock(TaggedValue.class);
    TagDefinition tagDefinition = Mockito.mock(TagDefinition.class);
    Identifier id = Identifier.random();
    List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
    taggedValueList.add(taggedValue);

    Mockito.when(attribute.getTaggedValues()).thenReturn(taggedValueList);
    Mockito.when(taggedValue.getTagDefinition()).thenReturn(id);
    Mockito.when(host.getTagDefinition(Matchers.any(Identifier.class))).thenReturn(tagDefinition);
    Mockito.when(tagDefinition.getName()).thenReturn(SliUmlConstants.TAGDEF_REFERENCE);
    Mockito.when(taggedValue.getValue()).thenReturn("test");
    Assert.assertTrue(pluginForSLI.isAssociationEnd(classType, attribute, host));
}
    @Test
    public void testIsAssociationEndFalse() throws Exception {
        ClassType classType = Mockito.mock(ClassType.class);
        Attribute attribute = Mockito.mock(Attribute.class);
        Xsd2UmlPluginHost host = Mockito.mock(Xsd2UmlPluginHost.class);
        TaggedValue taggedValue = Mockito.mock(TaggedValue.class);
        TagDefinition tagDefinition = Mockito.mock(TagDefinition.class);
        Identifier id = Identifier.random();
        List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        taggedValueList.add(taggedValue);

        Mockito.when(attribute.getTaggedValues()).thenReturn(taggedValueList);
        Mockito.when(taggedValue.getTagDefinition()).thenReturn(id);
        Mockito.when(host.getTagDefinition(Matchers.any(Identifier.class))).thenReturn(tagDefinition);
        Mockito.when(tagDefinition.getName()).thenReturn(SliUmlConstants.TAGDEF_REST_RESOURCE);
        Mockito.when(taggedValue.getValue()).thenReturn("test");
        Assert.assertFalse(pluginForSLI.isAssociationEnd(classType, attribute, host));
    }

    /**
* 
* Method: nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testNameAssociation() throws Exception { 
    AssociationEnd associationEnd = Mockito.mock(AssociationEnd.class);
    Xsd2UmlPluginHost host = Mockito.mock(Xsd2UmlPluginHost.class);
    Assert.assertTrue(pluginForSLI.nameAssociation(associationEnd, associationEnd, host.getPlugin()).isEmpty());
} 

/** 
* 
* Method: tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testTagsFromAppInfo() throws Exception {
    XmlSchemaAppInfo xmlSchemaAppInfo = Mockito.mock(XmlSchemaAppInfo.class);
    Xsd2UmlPluginHost host = Mockito.mock(Xsd2UmlPluginHost.class);
    NodeList nodeList = Mockito.mock(NodeList.class);
    Element node = Mockito.mock(Element.class);

    Mockito.when(host.ensureTagDefinitionId(Mockito.anyString())).thenReturn(Identifier.random());
    Mockito.when(xmlSchemaAppInfo.getMarkup()).thenReturn(nodeList);
    Mockito.when(nodeList.item(Matchers.anyInt())).thenReturn(node);
    Mockito.when(nodeList.getLength()).thenReturn(1);
    Mockito.when(node.getNodeType()).thenReturn(Node.ELEMENT_NODE);
    Mockito.when(node.getNamespaceURI()).thenReturn(SliMongoConstants.NAMESPACE_SLI);
    Mockito.when(node.getLocalName()).thenReturn("CollectionType");
    Mockito.when(node.getChildNodes()).thenReturn(nodeList);


    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("naturalKey");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("applyNaturalKeys");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("PersonallyIdentifiableInfo");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("ReferenceType");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("ReadEnforcement");
    Mockito.when(node.getTextContent()).thenReturn("READ_RESTRICTED");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("SecuritySphere");
    Mockito.when(node.getTextContent()).thenReturn("Public");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("RelaxedBlacklist");
    Mockito.when(node.getTextContent()).thenReturn("true");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("RestrictedForLogging");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("WriteEnforcement");
    Mockito.when(node.getTextContent()).thenReturn("WRITE_RESTRICTED");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));
    Mockito.when(node.getLocalName()).thenReturn("schemaVersion");
    Assert.assertNotNull(pluginForSLI.tagsFromAppInfo(xmlSchemaAppInfo, host));

}





} 
