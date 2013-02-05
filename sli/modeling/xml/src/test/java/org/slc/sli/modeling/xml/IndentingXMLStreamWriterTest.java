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

package org.slc.sli.modeling.xml;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author chung
 */
public class IndentingXMLStreamWriterTest {

    ByteArrayOutputStream outputStream;
    XMLStreamWriter streamWriter;
    IndentingXMLStreamWriter out;

    private static final String NAMESPACE = "http://www.namespace.com/ns/1";
    private static final String PREFIX = "prefix";
    private static final String DEFAULT_NAMESPACE = "http://www.namespace.com/ns/default";
    private static final String DEFAULT_PREFIX = "dprefix";

    @Before
    public void setup() throws XMLStreamException {
        outputStream = new ByteArrayOutputStream();
        streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
        out = new IndentingXMLStreamWriter(streamWriter);
        out.setDefaultNamespace(DEFAULT_NAMESPACE);
        out.setPrefix(DEFAULT_PREFIX, DEFAULT_NAMESPACE);
        out.setPrefix(PREFIX, NAMESPACE);

        NamespaceContext context = mock(NamespaceContext.class);
        when(context.getNamespaceURI(anyString())).thenReturn(NAMESPACE);
        out.setNamespaceContext(context);
    }

    @Test
    public void testWriteDocumentAndElement() throws IOException, XMLStreamException {
        out.writeStartDocument();
        out.writeStartElement("testElement1");
        out.writeEndElement();
        out.writeEmptyElement("emptyElement1");
        out.writeEndDocument();
        assertOutput("<?xml version=\"1.0\" ?><testElement1></testElement1><emptyElement1/>");

        out.writeStartDocument("1.0");
        out.writeStartElement(DEFAULT_NAMESPACE, "testElement2");
        out.writeEndElement();
        out.writeEmptyElement(DEFAULT_NAMESPACE, "emptyElement2");
        out.writeEndDocument();
        assertOutput("<?xml version=\"1.0\"?>\n<dprefix:testElement2></dprefix:testElement2><dprefix:emptyElement2/>");

        out.writeStartDocument("UTF-8", "1.0");
        out.writeStartElement(PREFIX, "testElement3", NAMESPACE);
        out.writeEndElement();
        out.writeEmptyElement(PREFIX, "emptyElement3", NAMESPACE);
        out.writeEndDocument();
        assertOutput("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<prefix:testElement3></prefix:testElement3><prefix:emptyElement3/>");
    }

    @Test
    public void testWriteAttribute() throws IOException, XMLStreamException {
        out.writeStartElement("testElement1");
        out.writeAttribute("attr1", "val1");
        out.writeEndElement();
        assertOutput("<testElement1 attr1=\"val1\"></testElement1>");

        out.writeStartElement("testElement2");
        out.writeAttribute(DEFAULT_NAMESPACE, "attr2", "val2");
        out.writeEndElement();
        assertOutput("<testElement2 dprefix:attr2=\"val2\"></testElement2>");

        out.writeStartElement("testElement3");
        out.writeAttribute(PREFIX, NAMESPACE, "attr3", "val3");
        out.writeEndElement();
        assertOutput("<testElement3 prefix:attr3=\"val3\"></testElement3>");
    }

    @Test
    public void testWriteCDATA() throws IOException, XMLStreamException {
        out.writeCData("TestCData");
        assertOutput("<![CDATA[TestCData]]>");
    }

    @Test
    public void testWriteComment() throws IOException, XMLStreamException {
        out.writeComment("This is a comment");
        assertOutput("<!--This is a comment-->");
    }

    @Test
    public void testWriteCharacters() throws IOException, XMLStreamException {
        out.writeCharacters("This is string 1.");
        out.writeCharacters("This is string 2.".toCharArray(), 0, 7);
        assertOutput("This is string 1.This is");
    }

    @Test
    public void testWriteDTD() throws IOException, XMLStreamException {
        out.writeDTD("This is DTD");
        assertOutput("This is DTD");
    }

    @Test
    public void testWriteNamespace() throws IOException, XMLStreamException {
        out.writeStartElement("element1");
        out.writeDefaultNamespace(DEFAULT_NAMESPACE);
        out.writeEndElement();
        assertOutput("<element1 xmlns=\"" + DEFAULT_NAMESPACE + "\"></element1>");

        out.writeStartElement("element2");
        out.writeNamespace(PREFIX, NAMESPACE);
        out.writeEndElement();
        assertOutput("<element2 xmlns:" + PREFIX + "=\"" + NAMESPACE + "\"></element2>");
    }

    @Test
    public void testGetNamespaceContext() {
        NamespaceContext context = out.getNamespaceContext();
        assertTrue(context.getNamespaceURI(NAMESPACE).equals(NAMESPACE));
    }

    @Test
    public void testGetPrefix() throws XMLStreamException {
        String prefix = out.getPrefix(DEFAULT_NAMESPACE);
        assertTrue(prefix.equals(DEFAULT_PREFIX));

        prefix = out.getPrefix(NAMESPACE);
        assertTrue(prefix.equals(PREFIX));
    }

    @Test(expected = NullPointerException.class)
    public void testClose() throws XMLStreamException {
        out.close();
        out.writeEmptyElement("test");
    }

    private void assertOutput(String inString) throws IOException, XMLStreamException {
        out.flush();
        InputStream in = new ByteArrayInputStream(outputStream.toByteArray());
        String outString = IOUtils.toString(in);
        assertTrue(outString.equals(inString));
        outputStream.reset();
    }

}
