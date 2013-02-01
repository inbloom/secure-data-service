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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.xsd.WxsNamespace;
import org.slc.sli.modeling.xsd.XsdAttributeName;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;


/**
 * @author jstokes
 */
public class Uml2XsdPluginWriterAdapterTest {

    private Uml2XsdPluginWriterAdapter uml2XsdPluginWriterAdapter;
    private XMLStreamWriter mockWriter;
    private String prefix;

    @Before
    public void setup() {
        mockWriter = Mockito.mock(XMLStreamWriter.class);
        prefix = "sli";
        uml2XsdPluginWriterAdapter = new Uml2XsdPluginWriterAdapter(mockWriter, prefix);
    }

    @Test
    public void testAnnotation() throws Exception {
        uml2XsdPluginWriterAdapter.annotation();
        Mockito.verify(mockWriter).writeStartElement(prefix, "annotation", WxsNamespace.URI);
    }

    @Test
    public void testAppinfo() throws Exception {
        uml2XsdPluginWriterAdapter.appinfo();
        Mockito.verify(mockWriter).writeStartElement(prefix, "appinfo", WxsNamespace.URI);
    }

    @Test
    public void testBegin() throws Exception {
        uml2XsdPluginWriterAdapter.begin(prefix, "test", "testns");
        Mockito.verify(mockWriter).writeStartElement(prefix, "test", "testns");
    }

    @Test
    public void testCharacters() throws Exception {
        uml2XsdPluginWriterAdapter.characters("test");
        Mockito.verify(mockWriter).writeCharacters("test");
    }

    @Test
    public void testChoice() throws Exception {
        uml2XsdPluginWriterAdapter.choice();
        Mockito.verify(mockWriter).writeStartElement(prefix, "choice", WxsNamespace.URI);
    }

    @Test
    public void testComment() throws Exception {
        uml2XsdPluginWriterAdapter.comment("test");
        Mockito.verify(mockWriter).writeComment("test");
    }

    @Test
    public void testComplexType() throws Exception {
        uml2XsdPluginWriterAdapter.complexType();
        Mockito.verify(mockWriter).writeStartElement(prefix, "complexType", WxsNamespace.URI);
    }

    @Test
    public void testDocumentation() throws Exception {
        uml2XsdPluginWriterAdapter.documentation();
        Mockito.verify(mockWriter).writeStartElement(prefix, "documentation", WxsNamespace.URI);
    }

    @Test
    public void testElement() throws Exception {
        uml2XsdPluginWriterAdapter.element();
        Mockito.verify(mockWriter).writeStartElement(prefix, "element", WxsNamespace.URI);
    }

    @Test
    public void testElementName() throws Exception {
        final QName qName = new QName("test");
        uml2XsdPluginWriterAdapter.elementName(qName);
        Mockito.verify(mockWriter).writeAttribute("name", "test");
    }

    @Test
    public void testEnd() throws Exception {
        uml2XsdPluginWriterAdapter.end();
        Mockito.verify(mockWriter).writeEndElement();
    }

    @Test
    public void testMaxOccurs() throws Exception {
        uml2XsdPluginWriterAdapter.maxOccurs(Occurs.ONE);
        Mockito.verifyZeroInteractions(mockWriter);

        uml2XsdPluginWriterAdapter.maxOccurs(Occurs.UNBOUNDED);
        Mockito.verify(mockWriter).writeAttribute(XsdAttributeName.MAX_OCCURS.getLocalName(), "unbounded");

        uml2XsdPluginWriterAdapter.maxOccurs(Occurs.ZERO);
        Mockito.verify(mockWriter).writeAttribute(XsdAttributeName.MAX_OCCURS.getLocalName(), "0");
    }

    @Test
    public void testMinOccurs() throws Exception {
        uml2XsdPluginWriterAdapter.minOccurs(Occurs.ONE);
        Mockito.verifyZeroInteractions(mockWriter);

        uml2XsdPluginWriterAdapter.minOccurs(Occurs.UNBOUNDED);
        Mockito.verify(mockWriter).writeAttribute(XsdAttributeName.MIN_OCCURS.getLocalName(), "unbounded");

        uml2XsdPluginWriterAdapter.minOccurs(Occurs.ZERO);
        Mockito.verify(mockWriter).writeAttribute(XsdAttributeName.MIN_OCCURS.getLocalName(), "0");
    }

    @Test
    public void testRef() throws Exception {
        final NamespaceContext mockNamespaceContext = Mockito.mock(NamespaceContext.class);
        Mockito.when(mockWriter.getNamespaceContext()).thenReturn(mockNamespaceContext);
        Mockito.when(mockNamespaceContext.getPrefix(Matchers.anyString())).thenReturn("sli");

        final QName qName = new QName(WxsNamespace.URI, "test");
        uml2XsdPluginWriterAdapter.ref(qName);
        Mockito.verify(mockWriter).writeAttribute("ref", "sli:test");

        final QName qName1 = new QName("ns", "test2");
        uml2XsdPluginWriterAdapter.ref(qName1);
        Mockito.verify(mockWriter).writeAttribute("ref", "sli:test2");
    }

    @Test
    public void testSequence() throws Exception {
        uml2XsdPluginWriterAdapter.sequence();
        Mockito.verify(mockWriter).writeStartElement(prefix, "sequence", WxsNamespace.URI);
    }

    @Test
    public void testType() throws Exception {
        final NamespaceContext mockNamespaceContext = Mockito.mock(NamespaceContext.class);
        Mockito.when(mockWriter.getNamespaceContext()).thenReturn(mockNamespaceContext);
        Mockito.when(mockNamespaceContext.getPrefix(Matchers.anyString())).thenReturn("sli");

        final QName qName = new QName(WxsNamespace.URI, "test");
        uml2XsdPluginWriterAdapter.type(qName);
        Mockito.verify(mockWriter).writeAttribute("type", "sli:test");

        final QName qName1 = new QName("ns", "test2");
        uml2XsdPluginWriterAdapter.type(qName1);
        Mockito.verify(mockWriter).writeAttribute("type", "sli:test2");
    }
}
