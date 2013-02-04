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


import org.apache.ws.commons.schema.*;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;

/**
 * Tests the XSD to UML conversion process.
 * 
 * 
 * @author kmyers
 *
 */
public class Xsd2UmlConvertTest {

    @Test
    public void test() {
        
        final QName nameQName = new QName("NAMESPACE_URI", "name", "PREFIX");
        
        XmlSchemaSimpleType xmlSchemaSimpleType = Mockito.mock(XmlSchemaSimpleType.class);
        Mockito.when(xmlSchemaSimpleType.getQName()).thenReturn(nameQName);
        
        XmlSchemaElement xmlSchemaElement = Mockito.mock(XmlSchemaElement.class);
        Mockito.when(xmlSchemaElement.getQName()).thenReturn(nameQName);
        Mockito.when(xmlSchemaElement.getSchemaType()).thenReturn(xmlSchemaSimpleType);
        
        XmlSchemaComplexContentExtension xmlSchemaComplexContentExtension = Mockito.mock(XmlSchemaComplexContentExtension.class);
        Mockito.when(xmlSchemaComplexContentExtension.getParticle()).thenReturn(xmlSchemaElement);
        Mockito.when(xmlSchemaComplexContentExtension.getBaseTypeName()).thenReturn(nameQName);
        
        NamespacePrefixList namespacePrefixList = Mockito.mock(NamespacePrefixList.class);
        Mockito.when(namespacePrefixList.getPrefix(Matchers.any(String.class))).thenReturn("PREFIX");
        Mockito.when(namespacePrefixList.getNamespaceURI(Matchers.any(String.class))).thenReturn("NAMESPACE_URI");
        
        XmlSchema xmlSchema = new XmlSchema();
        xmlSchema.setNamespaceContext(namespacePrefixList);
        
        XmlSchemaContentModel xmlSchemaContentModel = Mockito.mock(XmlSchemaContentModel.class);
        Mockito.when(xmlSchemaContentModel.getContent()).thenReturn(xmlSchemaComplexContentExtension);
        
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.getTextContent()).thenReturn("textContent");
        
        NodeList nodeList = Mockito.mock(NodeList.class);
        Mockito.when(nodeList.getLength()).thenReturn(1);
        Mockito.when(nodeList.item(Matchers.anyInt())).thenReturn(node);
        
        XmlSchemaDocumentation xmlSchemaDocumentation = new XmlSchemaDocumentation();
        xmlSchemaDocumentation.setMarkup(nodeList);
        
        XmlSchemaObjectCollection children = new XmlSchemaObjectCollection();
        children.add(xmlSchemaDocumentation);
        
        XmlSchemaAnnotation xmlSchemaAnnotation = Mockito.mock(XmlSchemaAnnotation.class);
        Mockito.when(xmlSchemaAnnotation.getItems()).thenReturn(children);
        
        XmlSchemaSimpleTypeRestriction xmlSchemaSimpleTypeRestriction = Mockito.mock(XmlSchemaSimpleTypeRestriction.class);
        Mockito.when(xmlSchemaSimpleTypeRestriction.getBaseTypeName()).thenReturn(nameQName);
        
        XmlSchemaSimpleType xmlSchemaSimpleType2 = Mockito.mock(XmlSchemaSimpleType.class);
        Mockito.when(xmlSchemaSimpleType2.getContent()).thenReturn(xmlSchemaSimpleTypeRestriction);
        
        XmlSchemaAttribute xmlSchemaAttribute = Mockito.mock(XmlSchemaAttribute.class);
        Mockito.when(xmlSchemaAttribute.getQName()).thenReturn(nameQName);
        Mockito.when(xmlSchemaAttribute.getSchemaType()).thenReturn(xmlSchemaSimpleType2);
        
        XmlSchemaObjectCollection xmlSchemaAttributes = new XmlSchemaObjectCollection();
        xmlSchemaAttributes.add(xmlSchemaAttribute);
        
        XmlSchemaComplexType xmlso1 = Mockito.mock(XmlSchemaComplexType.class);
        Mockito.when(xmlso1.getQName()).thenReturn(nameQName);
        Mockito.when(xmlso1.getContentModel()).thenReturn(xmlSchemaContentModel);
        Mockito.when(xmlso1.getAttributes()).thenReturn(xmlSchemaAttributes);
        Mockito.when(xmlso1.getAnnotation()).thenReturn(xmlSchemaAnnotation);
        
        XmlSchemaEnumerationFacet xmlSchemaEnumerationFacet = Mockito.mock(XmlSchemaEnumerationFacet.class);
        XmlSchemaMinLengthFacet xmlSchemaMinLengthFacet = Mockito.mock(XmlSchemaMinLengthFacet.class);
        XmlSchemaMaxLengthFacet xmlSchemaMaxLengthFacet = Mockito.mock(XmlSchemaMaxLengthFacet.class);
        XmlSchemaPatternFacet xmlSchemaPatternFacet = Mockito.mock(XmlSchemaPatternFacet.class);
        XmlSchemaMinInclusiveFacet xmlSchemaMinInclusiveFacet = Mockito.mock(XmlSchemaMinInclusiveFacet.class);
        XmlSchemaMaxInclusiveFacet xmlSchemaMaxInclusiveFacet = Mockito.mock(XmlSchemaMaxInclusiveFacet.class);
        XmlSchemaFractionDigitsFacet xmlSchemaFractionDigitsFacet = Mockito.mock(XmlSchemaFractionDigitsFacet.class);
        
        Mockito.when(xmlSchemaEnumerationFacet.getValue()).thenReturn("xmlSchemaEnumerationFacet");
        Mockito.when(xmlSchemaMinLengthFacet.getValue()).thenReturn("xmlSchemaMinLengthFacet");
        Mockito.when(xmlSchemaMaxLengthFacet.getValue()).thenReturn("xmlSchemaMaxLengthFacet");
        Mockito.when(xmlSchemaPatternFacet.getValue()).thenReturn("xmlSchemaPatternFacet");
        Mockito.when(xmlSchemaMinInclusiveFacet.getValue()).thenReturn("xmlSchemaMinInclusiveFacet");
        Mockito.when(xmlSchemaMaxInclusiveFacet.getValue()).thenReturn("xmlSchemaMaxInclusiveFacet");
        Mockito.when(xmlSchemaFractionDigitsFacet.getValue()).thenReturn("xmlSchemaFractionDigitsFacet");
        
        XmlSchemaObjectCollection facets = new XmlSchemaObjectCollection();
        facets.add(xmlSchemaEnumerationFacet);
        facets.add(xmlSchemaMinLengthFacet);
        facets.add(xmlSchemaMaxLengthFacet);
        facets.add(xmlSchemaPatternFacet);
        facets.add(xmlSchemaMinInclusiveFacet);
        facets.add(xmlSchemaMaxInclusiveFacet);
        facets.add(xmlSchemaFractionDigitsFacet);
        
        XmlSchemaSimpleTypeRestriction xmlSchemaSimpleTypeRestriction2 = Mockito.mock(XmlSchemaSimpleTypeRestriction.class);
        Mockito.when(xmlSchemaSimpleTypeRestriction2.getBaseTypeName()).thenReturn(nameQName);
        Mockito.when(xmlSchemaSimpleTypeRestriction2.getFacets()).thenReturn(facets);
        
        XmlSchemaSimpleType xmlso2 = Mockito.mock(XmlSchemaSimpleType.class);
        Mockito.when(xmlso2.getQName()).thenReturn(nameQName);
        Mockito.when(xmlso2.getContent()).thenReturn(xmlSchemaSimpleTypeRestriction2);
        
        final XmlSchemaObjectCollection xmlSchemaObjectCollection = new XmlSchemaObjectCollection();
        xmlSchemaObjectCollection.add(xmlso1);
        xmlSchemaObjectCollection.add(xmlso2);
        
        final String name = "foo";
        final XmlSchema schema = Mockito.mock(XmlSchema.class);
        final Xsd2UmlHostedPlugin plugin = Mockito.mock(Xsd2UmlHostedPlugin.class);
        
        Mockito.when(schema.getItems()).thenReturn(xmlSchemaObjectCollection);
        Mockito.when(plugin.nameFromSchemaTypeName(Matchers.any(QName.class))).thenReturn("name");
        Mockito.when(plugin.nameFromSchemaElementName(Matchers.any(QName.class))).thenReturn("name");
        Mockito.when(plugin.nameFromComplexTypeExtension(Matchers.any(QName.class), Matchers.any(QName.class))).thenReturn("name");
        Mockito.when(plugin.nameFromSchemaAttributeName(Matchers.any(QName.class))).thenReturn("name");
        Mockito.when(plugin.nameFromSimpleTypeRestriction(Matchers.any(QName.class), Matchers.any(QName.class))).thenReturn("name");
        
        Assert.assertNotNull(Xsd2UmlConvert.extract(name, schema, plugin));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new Xsd2UmlConvert();
    }
}
