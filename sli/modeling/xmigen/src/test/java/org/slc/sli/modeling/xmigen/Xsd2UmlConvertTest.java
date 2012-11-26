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

package org.slc.sli.modeling.xmigen;


import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContentModel;
import org.apache.ws.commons.schema.XmlSchemaDocumentation;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFractionDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        
        XmlSchemaSimpleType xmlSchemaSimpleType = mock(XmlSchemaSimpleType.class);
        when(xmlSchemaSimpleType.getQName()).thenReturn(nameQName);
        
        XmlSchemaElement xmlSchemaElement = mock(XmlSchemaElement.class);
        when(xmlSchemaElement.getQName()).thenReturn(nameQName);
        when(xmlSchemaElement.getSchemaType()).thenReturn(xmlSchemaSimpleType);
        
        XmlSchemaComplexContentExtension xmlSchemaComplexContentExtension = mock(XmlSchemaComplexContentExtension.class);
        when(xmlSchemaComplexContentExtension.getParticle()).thenReturn(xmlSchemaElement);
        when(xmlSchemaComplexContentExtension.getBaseTypeName()).thenReturn(nameQName);
        
        NamespacePrefixList namespacePrefixList = mock(NamespacePrefixList.class);
        when(namespacePrefixList.getPrefix(any(String.class))).thenReturn("PREFIX");
        when(namespacePrefixList.getNamespaceURI(any(String.class))).thenReturn("NAMESPACE_URI");
        
        XmlSchema xmlSchema = new XmlSchema();
        xmlSchema.setNamespaceContext(namespacePrefixList);
        
        XmlSchemaContentModel xmlSchemaContentModel = mock(XmlSchemaContentModel.class);
        when(xmlSchemaContentModel.getContent()).thenReturn(xmlSchemaComplexContentExtension);
        
        Node node = mock(Node.class);
        when(node.getTextContent()).thenReturn("textContent");
        
        NodeList nodeList = mock(NodeList.class);
        when(nodeList.getLength()).thenReturn(1);
        when(nodeList.item(anyInt())).thenReturn(node);
        
        XmlSchemaDocumentation xmlSchemaDocumentation = new XmlSchemaDocumentation();
        xmlSchemaDocumentation.setMarkup(nodeList);
        
        XmlSchemaObjectCollection children = new XmlSchemaObjectCollection();
        children.add(xmlSchemaDocumentation);
        
        XmlSchemaAnnotation xmlSchemaAnnotation = mock(XmlSchemaAnnotation.class);
        when(xmlSchemaAnnotation.getItems()).thenReturn(children);
        
        XmlSchemaSimpleTypeRestriction xmlSchemaSimpleTypeRestriction = mock(XmlSchemaSimpleTypeRestriction.class);
        when(xmlSchemaSimpleTypeRestriction.getBaseTypeName()).thenReturn(nameQName);
        
        XmlSchemaSimpleType xmlSchemaSimpleType2 = mock(XmlSchemaSimpleType.class);
        when(xmlSchemaSimpleType2.getContent()).thenReturn(xmlSchemaSimpleTypeRestriction);
        
        XmlSchemaAttribute xmlSchemaAttribute = mock(XmlSchemaAttribute.class);
        when(xmlSchemaAttribute.getQName()).thenReturn(nameQName);
        when(xmlSchemaAttribute.getSchemaType()).thenReturn(xmlSchemaSimpleType2);
        
        XmlSchemaObjectCollection xmlSchemaAttributes = new XmlSchemaObjectCollection();
        xmlSchemaAttributes.add(xmlSchemaAttribute);
        
        XmlSchemaComplexType xmlso1 = mock(XmlSchemaComplexType.class);
        when(xmlso1.getQName()).thenReturn(nameQName);
        when(xmlso1.getContentModel()).thenReturn(xmlSchemaContentModel);
        when(xmlso1.getAttributes()).thenReturn(xmlSchemaAttributes);
        when(xmlso1.getAnnotation()).thenReturn(xmlSchemaAnnotation);
        
        XmlSchemaEnumerationFacet xmlSchemaEnumerationFacet = mock(XmlSchemaEnumerationFacet.class);
        XmlSchemaMinLengthFacet xmlSchemaMinLengthFacet = mock(XmlSchemaMinLengthFacet.class);
        XmlSchemaMaxLengthFacet xmlSchemaMaxLengthFacet = mock(XmlSchemaMaxLengthFacet.class);
        XmlSchemaPatternFacet xmlSchemaPatternFacet = mock(XmlSchemaPatternFacet.class);
        XmlSchemaMinInclusiveFacet xmlSchemaMinInclusiveFacet = mock(XmlSchemaMinInclusiveFacet.class);
        XmlSchemaMaxInclusiveFacet xmlSchemaMaxInclusiveFacet = mock(XmlSchemaMaxInclusiveFacet.class);
        XmlSchemaFractionDigitsFacet xmlSchemaFractionDigitsFacet = mock(XmlSchemaFractionDigitsFacet.class);
        
        when(xmlSchemaEnumerationFacet.getValue()).thenReturn("xmlSchemaEnumerationFacet");
        when(xmlSchemaMinLengthFacet.getValue()).thenReturn("xmlSchemaMinLengthFacet");
        when(xmlSchemaMaxLengthFacet.getValue()).thenReturn("xmlSchemaMaxLengthFacet");
        when(xmlSchemaPatternFacet.getValue()).thenReturn("xmlSchemaPatternFacet");
        when(xmlSchemaMinInclusiveFacet.getValue()).thenReturn("xmlSchemaMinInclusiveFacet");
        when(xmlSchemaMaxInclusiveFacet.getValue()).thenReturn("xmlSchemaMaxInclusiveFacet");
        when(xmlSchemaFractionDigitsFacet.getValue()).thenReturn("xmlSchemaFractionDigitsFacet");
        
        XmlSchemaObjectCollection facets = new XmlSchemaObjectCollection();
        facets.add(xmlSchemaEnumerationFacet);
        facets.add(xmlSchemaMinLengthFacet);
        facets.add(xmlSchemaMaxLengthFacet);
        facets.add(xmlSchemaPatternFacet);
        facets.add(xmlSchemaMinInclusiveFacet);
        facets.add(xmlSchemaMaxInclusiveFacet);
        facets.add(xmlSchemaFractionDigitsFacet);
        
        XmlSchemaSimpleTypeRestriction xmlSchemaSimpleTypeRestriction2 = mock(XmlSchemaSimpleTypeRestriction.class);
        when(xmlSchemaSimpleTypeRestriction2.getBaseTypeName()).thenReturn(nameQName);
        when(xmlSchemaSimpleTypeRestriction2.getFacets()).thenReturn(facets);
        
        XmlSchemaSimpleType xmlso2 = mock(XmlSchemaSimpleType.class);
        when(xmlso2.getQName()).thenReturn(nameQName);
        when(xmlso2.getContent()).thenReturn(xmlSchemaSimpleTypeRestriction2);
        
        final XmlSchemaObjectCollection xmlSchemaObjectCollection = new XmlSchemaObjectCollection();
        xmlSchemaObjectCollection.add(xmlso1);
        xmlSchemaObjectCollection.add(xmlso2);
        
        final String name = "foo";
        final XmlSchema schema = mock(XmlSchema.class);
        final Xsd2UmlPlugin plugin = mock(Xsd2UmlPlugin.class);
        
        when(schema.getItems()).thenReturn(xmlSchemaObjectCollection);
        when(plugin.nameFromSchemaTypeName(any(QName.class))).thenReturn("name");
        when(plugin.nameFromSchemaElementName(any(QName.class))).thenReturn("name");
        when(plugin.nameFromComplexTypeExtension(any(QName.class), any(QName.class))).thenReturn("name");
        when(plugin.nameFromSchemaAttributeName(any(QName.class))).thenReturn("name");
        when(plugin.nameFromSimpleTypeRestriction(any(QName.class), any(QName.class))).thenReturn("name");
        
        assertNotNull(Xsd2UmlConvert.extract(name, schema, plugin));
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new Xsd2UmlConvert();
    }
}
