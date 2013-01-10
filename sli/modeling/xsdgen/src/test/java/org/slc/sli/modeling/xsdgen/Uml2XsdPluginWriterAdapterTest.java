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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.xsd.WxsNamespace;
import org.slc.sli.modeling.xsd.XsdAttributeName;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verifyZeroInteractions;


/**
 * @author jstokes
 */
public class Uml2XsdPluginWriterAdapterTest {

    private Uml2XsdPluginWriterAdapter uml2XsdPluginWriterAdapter;
    private XMLStreamWriter mockWriter;
    private String prefix;

    @Before
    public void setup() {
        mockWriter = mock(XMLStreamWriter.class);
        prefix = "sli";
        uml2XsdPluginWriterAdapter = new Uml2XsdPluginWriterAdapter(mockWriter, prefix);
    }

    @Test
    public void testAnnotation() throws Exception {
        uml2XsdPluginWriterAdapter.annotation();
        verify(mockWriter).writeStartElement(prefix, "annotation", WxsNamespace.URI);
    }

    @Test
    public void testAppinfo() throws Exception {
        uml2XsdPluginWriterAdapter.appinfo();
        verify(mockWriter).writeStartElement(prefix, "appinfo", WxsNamespace.URI);
    }

    @Test
    public void testBegin() throws Exception {
        uml2XsdPluginWriterAdapter.begin(prefix, "test", "testns");
        verify(mockWriter).writeStartElement(prefix, "test", "testns");
    }

    @Test
    public void testCharacters() throws Exception {
        uml2XsdPluginWriterAdapter.characters("test");
        verify(mockWriter).writeCharacters("test");
    }

    @Test
    public void testChoice() throws Exception {
        uml2XsdPluginWriterAdapter.choice();
        verify(mockWriter).writeStartElement(prefix, "choice", WxsNamespace.URI);
    }

    @Test
    public void testComment() throws Exception {
        uml2XsdPluginWriterAdapter.comment("test");
        verify(mockWriter).writeComment("test");
    }

    @Test
    public void testComplexType() throws Exception {
        uml2XsdPluginWriterAdapter.complexType();
        verify(mockWriter).writeStartElement(prefix, "complexType", WxsNamespace.URI);
    }

    @Test
    public void testDocumentation() throws Exception {
        uml2XsdPluginWriterAdapter.documentation();
        verify(mockWriter).writeStartElement(prefix, "documentation", WxsNamespace.URI);
    }

    @Test
    public void testElement() throws Exception {
        uml2XsdPluginWriterAdapter.element();
        verify(mockWriter).writeStartElement(prefix, "element", WxsNamespace.URI);
    }

    @Test
    public void testElementName() throws Exception {
        final QName qName = new QName("test");
        uml2XsdPluginWriterAdapter.elementName(qName);
        verify(mockWriter).writeAttribute("name", "test");
    }

    @Test
    public void testEnd() throws Exception {
        uml2XsdPluginWriterAdapter.end();
        verify(mockWriter).writeEndElement();
    }

    @Test
    public void testMaxOccurs() throws Exception {
        uml2XsdPluginWriterAdapter.maxOccurs(Occurs.ONE);
        verifyZeroInteractions(mockWriter);

        uml2XsdPluginWriterAdapter.maxOccurs(Occurs.UNBOUNDED);
        verify(mockWriter).writeAttribute(XsdAttributeName.MAX_OCCURS.getLocalName(), "unbounded");

        uml2XsdPluginWriterAdapter.maxOccurs(Occurs.ZERO);
        verify(mockWriter).writeAttribute(XsdAttributeName.MAX_OCCURS.getLocalName(), "0");
    }

    @Test
    public void testMinOccurs() throws Exception {
        uml2XsdPluginWriterAdapter.minOccurs(Occurs.ONE);
        verifyZeroInteractions(mockWriter);

        uml2XsdPluginWriterAdapter.minOccurs(Occurs.UNBOUNDED);
        verify(mockWriter).writeAttribute(XsdAttributeName.MIN_OCCURS.getLocalName(), "unbounded");

        uml2XsdPluginWriterAdapter.minOccurs(Occurs.ZERO);
        verify(mockWriter).writeAttribute(XsdAttributeName.MIN_OCCURS.getLocalName(), "0");
    }

    @Test
    public void testRef() throws Exception {
        final NamespaceContext mockNamespaceContext = mock(NamespaceContext.class);
        when(mockWriter.getNamespaceContext()).thenReturn(mockNamespaceContext);
        when(mockNamespaceContext.getPrefix(anyString())).thenReturn("sli");

        final QName qName = new QName(WxsNamespace.URI, "test");
        uml2XsdPluginWriterAdapter.ref(qName);
        verify(mockWriter).writeAttribute("ref", "sli:test");

        final QName qName1 = new QName("ns", "test2");
        uml2XsdPluginWriterAdapter.ref(qName1);
        verify(mockWriter).writeAttribute("ref", "sli:test2");
    }

    @Test
    public void testSequence() throws Exception {
        uml2XsdPluginWriterAdapter.sequence();
        verify(mockWriter).writeStartElement(prefix, "sequence", WxsNamespace.URI);
    }

    @Test
    public void testType() throws Exception {
        final NamespaceContext mockNamespaceContext = mock(NamespaceContext.class);
        when(mockWriter.getNamespaceContext()).thenReturn(mockNamespaceContext);
        when(mockNamespaceContext.getPrefix(anyString())).thenReturn("sli");

        final QName qName = new QName(WxsNamespace.URI, "test");
        uml2XsdPluginWriterAdapter.type(qName);
        verify(mockWriter).writeAttribute("type", "sli:test");

        final QName qName1 = new QName("ns", "test2");
        uml2XsdPluginWriterAdapter.type(qName1);
        verify(mockWriter).writeAttribute("type", "sli:test2");
    }
}
